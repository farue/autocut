package de.farue.autocut.service;

import static de.farue.autocut.utils.BigDecimalUtil.modify;

import de.farue.autocut.domain.*;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.domain.enumeration.WashHistoryStatus;
import de.farue.autocut.repository.LaundryMachineProgramRepository;
import de.farue.autocut.repository.LaundryMachineRepository;
import de.farue.autocut.service.accounting.BookingBuilder;
import de.farue.autocut.service.accounting.BookingTemplate;
import de.farue.autocut.service.accounting.InternalTransactionService;
import de.farue.autocut.service.dto.WashitActivateDTO;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WashingService {

    private final Logger log = LoggerFactory.getLogger(WashingService.class);

    private final WashItClient washItClient;

    private final LeaseService leaseService;
    private final WashHistoryService washHistoryService;
    private final InternalTransactionService transactionService;
    private final GlobalSettingService globalSettingService;

    private final LaundryMachineRepository laundryMachineRepository;
    private final LaundryMachineProgramRepository laundryMachineProgramRepository;

    public WashingService(
        WashItClient washItClient,
        LeaseService leaseService,
        WashHistoryService washHistoryService,
        InternalTransactionService transactionService,
        GlobalSettingService globalSettingService,
        LaundryMachineRepository laundryMachineRepository,
        LaundryMachineProgramRepository laundryMachineProgramRepository
    ) {
        this.washItClient = washItClient;
        this.leaseService = leaseService;
        this.washHistoryService = washHistoryService;
        this.transactionService = transactionService;
        this.globalSettingService = globalSettingService;
        this.laundryMachineRepository = laundryMachineRepository;
        this.laundryMachineProgramRepository = laundryMachineProgramRepository;
    }

    public Optional<Instant> getInUseUntilDate(LaundryMachine machine) {
        return washHistoryService
            .getLastWashHistory(machine)
            .map(
                history -> {
                    Instant usingDate = history.getUsingDate();
                    Instant inUseUntil = usingDate;
                    if (history.getProgram() != null && history.getProgram().getTime() != null) {
                        inUseUntil = usingDate.plus(history.getProgram().getTime(), ChronoUnit.MINUTES);
                    }
                    return inUseUntil.isAfter(Instant.now()) ? inUseUntil : null;
                }
            );
    }

    public WashitActivateDTO purchaseAndUnlock(Tenant tenant, LaundryMachine machine, LaundryProgram program) {
        LaundryMachineProgram laundryMachineProgram = laundryMachineProgramRepository
            .findFirstByMachineAndProgram(machine, program)
            .orElseThrow(() -> new IllegalArgumentException("Selected program is not applicable to machine " + machine.getId() + "."));

        Instant timestamp = Instant.now();
        WashHistory washHistory = findOrCreateWashHistory(tenant, timestamp, machine, laundryMachineProgram);
        if (BooleanUtils.isFalse(machine.getEnabled())) {
            log.debug("{} is disabled. Adding history item {}", machine.getName(), washHistory);
            washHistory.setStatus(WashHistoryStatus.CANCELLED_BY_SYSTEM);
            washHistoryService.save(washHistory);
            throw new LaundryMachineUnavailableException();
        }
        BigDecimal cost =
            switch (machine.getType()) {
                case WASHING_MACHINE -> globalSettingService.getValue(GlobalSetting.WASHING_PRICE_WASHING_MACHINE);
                case DRYER -> globalSettingService.getValue(GlobalSetting.WASHING_PRICE_DRYER);
            };

        BigDecimal value = modify(cost).negative();
        BookingTemplate bookingTemplate = BookingBuilder
            .bookingTemplate()
            .bookingDate(timestamp)
            .valueDate(timestamp)
            .transactionTemplate(
                BookingBuilder
                    .transactionTemplate()
                    .type(TransactionType.PURCHASE)
                    .value(value)
                    .transactionBook(leaseService.getCashTransactionBook(tenant.getLease()))
                    .issuer(LaundryMachineService.class.getSimpleName())
                    .description(machine.getName())
                    .build()
            )
            .build();

        transactionService.saveWithContraTransaction(bookingTemplate);
        washHistoryService.save(washHistory);
        WashitActivateDTO response = washItClient.activate(Integer.valueOf(machine.getIdentifier()));

        log.debug("{} unlocked by {}: {}", machine.getName(), tenant, response);
        return response;
    }

    public void disableMachine(Long id) {
        laundryMachineRepository.findById(id).ifPresent(this::disableMachine);
    }

    public void disableMachine(LaundryMachine machine) {
        washHistoryService.cancelReservationsForMachine(machine);
        machine.setEnabled(false);
        laundryMachineRepository.save(machine);
    }

    public void enableMachine(Long id) {
        laundryMachineRepository.findById(id).ifPresent(this::enableMachine);
    }

    public void enableMachine(LaundryMachine machine) {
        machine.setEnabled(true);
        laundryMachineRepository.save(machine);
    }

    private WashHistory findOrCreateWashHistory(Tenant tenant, Instant time, LaundryMachine machine, LaundryMachineProgram program) {
        Optional<WashHistory> reservationOpt;
        if (machine == null && program == null) {
            reservationOpt = washHistoryService.getFirstOpenReservation(tenant, time);
        } else if (machine == null) {
            reservationOpt = washHistoryService.getFirstOpenReservationWithProgram(tenant, time, program);
        } else if (program == null) {
            reservationOpt = washHistoryService.getFirstOpenReservationWithMachine(tenant, time, machine);
        } else {
            reservationOpt = washHistoryService.getFirstOpenReservationWithMachineAndProgram(tenant, time, machine, program);
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
