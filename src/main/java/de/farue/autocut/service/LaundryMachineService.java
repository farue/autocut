package de.farue.autocut.service;

import static de.farue.autocut.utils.BigDecimalUtil.modify;

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
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        LeaseService leaseService,
        WashHistoryService washHistoryService,
        InternalTransactionService transactionService,
        GlobalSettingService globalSettingService,
        UserRepository userRepository,
        TenantRepository tenantRepository,
        LaundryMachineRepository laundryMachineRepository,
        LaundryMachineProgramRepository laundryMachineProgramRepository
    ) {
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
     * Save a laundryMachine.
     *
     * @param laundryMachine the entity to save.
     * @return the persisted entity.
     */
    public LaundryMachine save(LaundryMachine laundryMachine) {
        log.debug("Request to save LaundryMachine : {}", laundryMachine);
        return laundryMachineRepository.save(laundryMachine);
    }

    /**
     * Partially update a laundryMachine.
     *
     * @param laundryMachine the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LaundryMachine> partialUpdate(LaundryMachine laundryMachine) {
        log.debug("Request to partially update LaundryMachine : {}", laundryMachine);

        return laundryMachineRepository
            .findById(laundryMachine.getId())
            .map(
                existingLaundryMachine -> {
                    if (laundryMachine.getIdentifier() != null) {
                        existingLaundryMachine.setIdentifier(laundryMachine.getIdentifier());
                    }
                    if (laundryMachine.getName() != null) {
                        existingLaundryMachine.setName(laundryMachine.getName());
                    }
                    if (laundryMachine.getType() != null) {
                        existingLaundryMachine.setType(laundryMachine.getType());
                    }
                    if (laundryMachine.getEnabled() != null) {
                        existingLaundryMachine.setEnabled(laundryMachine.getEnabled());
                    }

                    return existingLaundryMachine;
                }
            )
            .map(laundryMachineRepository::save);
    }

    /**
     * Get all the laundryMachines.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<LaundryMachine> findAll() {
        log.debug("Request to get all LaundryMachines");
        return laundryMachineRepository.findAll();
    }

    /**
     * Get one laundryMachine by id.
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
     * Delete the laundryMachine by id.
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
            throw new IllegalArgumentException(
                "Supplied LaundryMachineProgram does not belong to LaundryMachine. " + machine + ", " + program
            );
        }

        Instant timestamp = Instant.now();
        WashHistory washHistory = findOrCreateWashHistory(tenant, timestamp, machine, program);
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
