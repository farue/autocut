package de.farue.autocut.service;

import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.domain.LaundryMachineProgram;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.WashHistory;
import de.farue.autocut.domain.enumeration.WashHistoryStatus;
import de.farue.autocut.repository.WashHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class WashHistoryService {

    private static final int RESERVATION_TIMESPAN = 15;

    private final Logger log = LoggerFactory.getLogger(WashHistoryService.class);

    private final WashHistoryRepository washHistoryRepository;

    @Autowired
    public WashHistoryService(WashHistoryRepository washHistoryRepository) {
        this.washHistoryRepository = washHistoryRepository;
    }

    public WashHistory saveAndFlush(WashHistory washHistory) {
        return washHistoryRepository.saveAndFlush(washHistory);
    }

    public Optional<WashHistory> getFirstOpenReservation(Tenant tenant, Instant time) {
        return washHistoryRepository
            .findFirstByReservationTenantIsInAndStatusAndReservationDateIsBetweenOrderByReservationDateAsc(
                getAllTenantsInLease(tenant), WashHistoryStatus.OPEN,
                time.minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES), time);
    }

    public Optional<WashHistory> getFirstOpenReservationWithProgram(Tenant tenant, Instant time,
        LaundryMachineProgram program) {
        return washHistoryRepository
            .findFirstByReservationTenantIsInAndStatusAndReservationDateIsBetweenAndProgramIsOrderByReservationDateAsc(
                getAllTenantsInLease(tenant), WashHistoryStatus.OPEN,
                time.minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES), time, program);
    }

    public Optional<WashHistory> getFirstOpenReservationWithMachine(Tenant tenant, Instant time,
        LaundryMachine machine) {
        return washHistoryRepository
            .findFirstByReservationTenantIsInAndStatusAndReservationDateIsBetweenAndMachineIsOrderByReservationDateAsc(
                getAllTenantsInLease(tenant), WashHistoryStatus.OPEN,
                time.minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES), time, machine);
    }

    public Optional<WashHistory> getFirstOpenReservationWithMachineAndProgram(Tenant tenant,
        Instant time, LaundryMachine machine, LaundryMachineProgram program) {
        return washHistoryRepository
            .findFirstByReservationTenantIsInAndStatusAndReservationDateIsBetweenAndMachineIsAndProgramIsOrderByReservationDateAsc(
                getAllTenantsInLease(tenant), WashHistoryStatus.OPEN,
                time.minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES), time, machine, program);
    }

    public List<WashHistory> getOpenReservationsThatExpired() {
        Instant timestamp = Instant.now();
        return washHistoryRepository
            .findAllByStatusAndReservationDateIsBefore(WashHistoryStatus.OPEN,
                timestamp.minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES));
    }

    private Set<Tenant> getAllTenantsInLease(Tenant tenant) {
        return tenant.getLease().getTenants();
    }

    public void cancelReservationsForMachine(LaundryMachine machine) {
        Instant timestamp = Instant.now();
        washHistoryRepository
            .findAllByMachineAndStatusAndReservationDateIsNotNull(machine, WashHistoryStatus.OPEN)
            .forEach(reservation -> {
                if (reservation.getReservationDate()
                    .isBefore(timestamp.minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES))) {
                    reservation.setStatus(WashHistoryStatus.EXPIRED);
                } else {
                    reservation.setStatus(WashHistoryStatus.CANCELLED_BY_SYSTEM);
                }
                washHistoryRepository.save(reservation);
            });
        washHistoryRepository.flush();
    }

    /**
     * Set {@link WashHistoryStatus#EXPIRED} to expired reservations.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void markExpiredReservations() {
        washHistoryRepository
            .findAllByStatusAndReservationDateIsBefore(WashHistoryStatus.OPEN,
                Instant.now().minus(RESERVATION_TIMESPAN, ChronoUnit.MINUTES))
            .forEach(reservation -> {
                log.debug("Marking reservation {} as expired", reservation.getId());
                reservation.setStatus(WashHistoryStatus.EXPIRED);
                washHistoryRepository.save(reservation);
            });
        washHistoryRepository.flush();
    }
}
