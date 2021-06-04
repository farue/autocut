package de.farue.autocut.service;

import de.farue.autocut.domain.*;
import de.farue.autocut.domain.enumeration.WashHistoryStatus;
import de.farue.autocut.repository.LaundryProgramRepository;
import de.farue.autocut.repository.WashHistoryRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WashHistoryService {

    private static final int RESERVATION_TIMESPAN = 15;

    private final Logger log = LoggerFactory.getLogger(WashHistoryService.class);

    private final WashHistoryRepository washHistoryRepository;
    private final LeaseService leaseService;

    private final LaundryProgramRepository laundryProgramRepository;

    @Autowired
    public WashHistoryService(
        WashHistoryRepository washHistoryRepository,
        LeaseService leaseService,
        LaundryProgramRepository laundryProgramRepository
    ) {
        this.washHistoryRepository = washHistoryRepository;
        this.leaseService = leaseService;
        this.laundryProgramRepository = laundryProgramRepository;
    }

    public WashHistory save(WashHistory washHistory) {
        return washHistoryRepository.save(washHistory);
    }

    public Optional<WashHistory> getFirstOpenReservation(Tenant tenant, Instant time) {
        return washHistoryRepository.findFirstByReservationTenantIsInAndStatusAndReservationDateIsBetweenOrderByReservationDateAsc(
            getAllTenantsInLease(tenant),
            WashHistoryStatus.OPEN,
            time.minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES),
            time
        );
    }

    public Optional<WashHistory> getFirstOpenReservationWithProgram(Tenant tenant, Instant time, LaundryMachineProgram program) {
        return washHistoryRepository.findFirstByReservationTenantIsInAndStatusAndReservationDateIsBetweenAndProgramIsOrderByReservationDateAsc(
            getAllTenantsInLease(tenant),
            WashHistoryStatus.OPEN,
            time.minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES),
            time,
            program
        );
    }

    public Optional<WashHistory> getFirstOpenReservationWithMachine(Tenant tenant, Instant time, LaundryMachine machine) {
        return washHistoryRepository.findFirstByReservationTenantIsInAndStatusAndReservationDateIsBetweenAndMachineIsOrderByReservationDateAsc(
            getAllTenantsInLease(tenant),
            WashHistoryStatus.OPEN,
            time.minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES),
            time,
            machine
        );
    }

    public Optional<WashHistory> getFirstOpenReservationWithMachineAndProgram(
        Tenant tenant,
        Instant time,
        LaundryMachine machine,
        LaundryMachineProgram program
    ) {
        return washHistoryRepository.findFirstByReservationTenantIsInAndStatusAndReservationDateIsBetweenAndMachineIsAndProgramIsOrderByReservationDateAsc(
            getAllTenantsInLease(tenant),
            WashHistoryStatus.OPEN,
            time.minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES),
            time,
            machine,
            program
        );
    }

    public List<WashHistory> getOpenReservationsThatExpired() {
        Instant timestamp = Instant.now();
        return washHistoryRepository.findAllByStatusAndReservationDateIsBefore(
            WashHistoryStatus.OPEN,
            timestamp.minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES)
        );
    }

    private Set<Tenant> getAllTenantsInLease(Tenant tenant) {
        // Load lease again since the supplied tenant might contain a lease without fetched tenants
        return leaseService.findOne(tenant.getLease().getId()).map(Lease::getTenants).orElse(new HashSet<>());
    }

    public void cancelReservationsForMachine(LaundryMachine machine) {
        Instant timestamp = Instant.now();
        washHistoryRepository
            .findAllByMachineAndStatusAndReservationDateIsNotNull(machine, WashHistoryStatus.OPEN)
            .forEach(
                reservation -> {
                    if (reservation.getReservationDate().isBefore(timestamp.minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES))) {
                        reservation.setStatus(WashHistoryStatus.EXPIRED);
                    } else {
                        reservation.setStatus(WashHistoryStatus.CANCELLED_BY_SYSTEM);
                    }
                    washHistoryRepository.save(reservation);
                }
            );
        washHistoryRepository.flush();
    }

    public Optional<WashHistory> getLastWashHistory(LaundryMachine machine) {
        return washHistoryRepository.findFirstByMachineAndStatusAndUsingDateIsNotNullOrderByUsingDateDesc(
            machine,
            WashHistoryStatus.COMPLETED
        );
    }

    public Page<WashHistory> getWashHistory(Collection<Tenant> tenants, Pageable pageable) {
        return washHistoryRepository.findWashHistory(tenants, pageable);
    }

    public Page<WashHistory> getWashHistory(Collection<Tenant> tenants, LaundryMachine machine, Pageable pageable) {
        return washHistoryRepository.findWashHistory(tenants, machine, pageable);
    }

    public List<LaundryProgram> findSuggestions(Collection<Tenant> tenants, LaundryMachine machine) {
        return laundryProgramRepository.findSuggestions(tenants, machine);
    }

    /**
     * Set {@link WashHistoryStatus#EXPIRED} to expired reservations.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void markExpiredReservations() {
        washHistoryRepository
            .findAllByStatusAndReservationDateIsBefore(
                WashHistoryStatus.OPEN,
                Instant.now().minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES)
            )
            .forEach(
                reservation -> {
                    log.debug("Marking reservation {} as expired", reservation.getId());
                    reservation.setStatus(WashHistoryStatus.EXPIRED);
                    washHistoryRepository.save(reservation);
                }
            );
        washHistoryRepository.flush();
    }
}
