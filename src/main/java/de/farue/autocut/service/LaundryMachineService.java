package de.farue.autocut.service;

import static de.farue.autocut.utils.BigDecimalUtil.modify;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.domain.GlobalSetting;
import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.domain.LaundryMachineProgram;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.WashHistory;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.domain.enumeration.WashHistoryStatus;
import de.farue.autocut.repository.LaundryMachineProgramRepository;
import de.farue.autocut.repository.LaundryMachineRepository;
import de.farue.autocut.repository.TenantRepository;
import de.farue.autocut.repository.UserRepository;
import de.farue.autocut.service.accounting.BookingBuilder;
import de.farue.autocut.service.accounting.BookingTemplate;
import de.farue.autocut.service.accounting.InternalTransactionService;

@Service
@Transactional
public class LaundryMachineService {

    private final Logger log = LoggerFactory.getLogger(LaundryMachineService.class);

    private final WashItClient washItClient;

    private final LeaseService leaseService;
    private final WashHistoryService washHistoryService;
    private final InternalTransactionService transactionService;
    private final GlobalSettingService globalSettingService;

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final LaundryMachineRepository laundryMachineRepository;
    private final LaundryMachineProgramRepository laundryMachineProgramRepository;

    @Autowired
    public LaundryMachineService(
        WashItClient washItClient,
        LeaseService leaseService, WashHistoryService washHistoryService,
        InternalTransactionService transactionService,
        GlobalSettingService globalSettingService,
        UserRepository userRepository,
        TenantRepository tenantRepository,
        LaundryMachineRepository laundryMachineRepository,
        LaundryMachineProgramRepository laundryMachineProgramRepository) {
        this.washItClient = washItClient;
        this.leaseService = leaseService;
        this.washHistoryService = washHistoryService;
        this.transactionService = transactionService;
        this.globalSettingService = globalSettingService;
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.laundryMachineRepository = laundryMachineRepository;
        this.laundryMachineProgramRepository = laundryMachineProgramRepository;
    }

    /**
     * Save a laundry machine.
     *
     * @param laundryMachine the entity to save.
     * @return the persisted entity.
     */
    public LaundryMachine save(LaundryMachine laundryMachine) {
        log.debug("Request to save LaundryMachine : {}", laundryMachine);
        return laundryMachineRepository.save(laundryMachine);
    }

    /**
     * Get all the laundry machines.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<LaundryMachine> findAll() {
        log.debug("Request to get all LaundryMachines");
        return laundryMachineRepository.findAll();
    }


    /**
     * Get one laundry machine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LaundryMachine> findOne(Long id) {
        log.debug("Request to get LaundryMachine : {}", id);
        return laundryMachineRepository.findById(id);
    }

    /**
     * Delete the laundry machine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete LaundryMachine : {}", id);
        laundryMachineRepository.deleteById(id);
    }

    public List<LaundryMachine> getAllLaundryMachines(boolean enabled) {
        return laundryMachineRepository.findAllWithEagerRelationshipsAndStatus(enabled);
    }

    public void purchaseAndUnlock(Tenant tenant, LaundryMachine machine, LaundryMachineProgram program) {
        if (!machine.equals(program.getLaundryMachine())) {
            throw new IllegalArgumentException("Supplied LaundryMachineProgram does not belong to LaundryMachine. " + machine + ", " + program);
        }

        Instant timestamp = Instant.now();
        WashHistory washHistory = findOrCreateWashHistory(tenant, timestamp, machine, program);
        if (!machine.isEnabled()) {
            log.debug("{} is disabled. Adding history item {}", machine.getName(), washHistory);
            washHistory.setStatus(WashHistoryStatus.CANCELLED_BY_SYSTEM);
            washHistoryService.save(washHistory);
            throw new LaundryMachineUnavailableException();
        }
        BigDecimal cost = switch (machine.getType()) {
            case WASHING_MACHINE -> globalSettingService
                .getValue(GlobalSetting.WASHING_PRICE_WASHING_MACHINE);
            case DRYER -> globalSettingService
                .getValue(GlobalSetting.WASHING_PRICE_DRYER);
        };

        BigDecimal value = modify(cost).negative();
        BookingTemplate bookingTemplate = BookingBuilder.bookingTemplate()
            .bookingDate(timestamp)
            .valueDate(timestamp)
            .transactionTemplate(BookingBuilder.transactionTemplate()
                .type(TransactionType.PURCHASE)
                .value(value)
                .transactionBook(leaseService.getCashTransactionBook(tenant.getLease()))
                .issuer(LaundryMachineService.class.getSimpleName())
                .description(machine.getName())
                .build())
            .build();

        transactionService.saveWithContraTransaction(bookingTemplate);
        washHistoryService.save(washHistory);
        washItClient.activate(Integer.valueOf(machine.getIdentifier()));

        log.debug("{} unlocked by {}", machine.getName(), tenant);
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
