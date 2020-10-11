package de.farue.autocut.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.farue.autocut.domain.GlobalSetting;
import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.domain.LaundryMachineProgram;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.WashHistory;
import de.farue.autocut.domain.enumeration.TransactionKind;
import de.farue.autocut.domain.enumeration.WashHistoryStatus;
import de.farue.autocut.repository.LaundryMachineRepository;
import de.farue.autocut.repository.TenantRepository;
import de.farue.autocut.repository.UserRepository;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.accounting.BookingBuilder;
import de.farue.autocut.service.accounting.BookingTemplate;
import de.farue.autocut.service.accounting.TransactionBookService;
import de.farue.autocut.utils.BigDecimalUtil;
import de.farue.autocut.web.rest.errors.LaundryMachineDoesNotExistException;

@Service
public class WashingService {

    private final Logger log = LoggerFactory.getLogger(WashingService.class);

    private final WashItClient washItClient;

    private final LeaseService leaseService;
    private final WashHistoryService washHistoryService;
    private final TransactionService transactionService;
    private final TransactionBookService transactionBookService;
    private final GlobalSettingService globalSettingService;

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final LaundryMachineRepository laundryMachineRepository;

    @Autowired
    public WashingService(
        WashItClient washItClient,
        LeaseService leaseService, WashHistoryService washHistoryService,
        TransactionService transactionService,
        TransactionBookService transactionBookService, GlobalSettingService globalSettingService,
        UserRepository userRepository,
        TenantRepository tenantRepository,
        LaundryMachineRepository laundryMachineRepository) {
        this.washItClient = washItClient;
        this.leaseService = leaseService;
        this.washHistoryService = washHistoryService;
        this.transactionService = transactionService;
        this.transactionBookService = transactionBookService;
        this.globalSettingService = globalSettingService;
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.laundryMachineRepository = laundryMachineRepository;
    }

    public List<LaundryMachine> getAllEnabledLaundryMachines() {
        return laundryMachineRepository.findAll().stream()
            .filter(LaundryMachine::isEnabled)
            .collect(Collectors.toList());
    }

    public void purchaseAndUnlock(LaundryMachine machine, LaundryMachineProgram program) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(tenantRepository::findOneByUser)
            .ifPresent(tenant -> doUnlock(tenant, machine, program));
    }

    public void disableMachine(LaundryMachine machine) {
        washHistoryService.cancelReservationsForMachine(machine);
        LaundryMachine loadedMachine = laundryMachineRepository
            .findByIdentifier(machine.getIdentifier())
            .orElseThrow(LaundryMachineDoesNotExistException::new);
        loadedMachine.setEnabled(false);
        laundryMachineRepository.saveAndFlush(loadedMachine);
    }

    private void doUnlock(Tenant tenant, LaundryMachine machine, LaundryMachineProgram program) {
        assert machine.getPrograms().contains(program);

        Instant timestamp = Instant.now();
        WashHistory washHistory = findOrCreateWashHistory(tenant, timestamp, machine, program);
        if (!machine.isEnabled()) {
            log.debug("{} is disabled. Adding history item {}", machine.getName(), washHistory);
            washHistory.setStatus(WashHistoryStatus.CANCELLED_BY_SYSTEM);
            washHistoryService.saveAndFlush(washHistory);
            throw new LaundryMachineUnavailableException();
        }
        BigDecimal cost = switch (machine.getType()) {
            case WASHING_MACHINE -> globalSettingService
                .getValue(GlobalSetting.WASHING_PRICE_WASHING_MACHINE);
            case DRYER -> globalSettingService
                .getValue(GlobalSetting.WASHING_PRICE_DRYER);
        };

        BigDecimal value = BigDecimalUtil.negative(cost);
        BookingTemplate bookingTemplate = BookingBuilder.bookingTemplate()
            .bookingDate(timestamp)
            .valueDate(timestamp)
            .transactionTemplate(BookingBuilder.transactionTemplate()
                .kind(TransactionKind.PURCHASE)
                .value(value)
                .transactionBook(leaseService.getCashTransactionBook(tenant.getLease()))
                .issuer(WashingService.class.getSimpleName())
                .description(machine.getName())
                .build())
            .build();

        transactionBookService.saveMemberBooking(bookingTemplate);
        washHistoryService.saveAndFlush(washHistory);
        washItClient.activate(Integer.valueOf(machine.getIdentifier()));

        log.debug("{} unlocked by {}", machine.getName(), tenant);
    }

    private WashHistory findOrCreateWashHistory(Tenant tenant, Instant time,
        @Nullable LaundryMachine machine, @Nullable LaundryMachineProgram program) {
        Optional<WashHistory> reservationOpt;
        if (machine == null && program == null) {
            reservationOpt = washHistoryService.getFirstOpenReservation(tenant, time);
        } else if (machine == null) {
            reservationOpt = washHistoryService
                .getFirstOpenReservationWithProgram(tenant, time, program);
        } else if (program == null) {
            reservationOpt = washHistoryService
                .getFirstOpenReservationWithMachine(tenant, time, machine);
        } else {
            reservationOpt = washHistoryService
                .getFirstOpenReservationWithMachineAndProgram(tenant, time, machine, program);
            if (reservationOpt.isEmpty()) {
                WashHistory washHistory = new WashHistory();
                washHistory.setUsingDate(time);
                washHistory.setStatus(WashHistoryStatus.COMPLETED);
                washHistory.setMachine(machine);
                washHistory.setProgram(program);
                washHistory.setUsingTenant(tenant);
                reservationOpt = Optional.of(washHistory);
            }
        }
        return reservationOpt.orElseThrow(IllegalArgumentException::new);
    }


}
