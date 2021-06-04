package de.farue.autocut.repository;

import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.domain.LaundryMachineProgram;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.WashHistory;
import de.farue.autocut.domain.enumeration.WashHistoryStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the WashHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WashHistoryRepository extends JpaRepository<WashHistory, Long> {
    Optional<WashHistory> findFirstByReservationTenantIsInAndStatusAndReservationDateIsBetweenOrderByReservationDateAsc(
        Set<Tenant> tenants,
        WashHistoryStatus status,
        Instant from,
        Instant until
    );

    Optional<WashHistory> findFirstByReservationTenantIsInAndStatusAndReservationDateIsBetweenAndProgramIsOrderByReservationDateAsc(
        Set<Tenant> tenants,
        WashHistoryStatus status,
        Instant from,
        Instant until,
        LaundryMachineProgram program
    );

    Optional<WashHistory> findFirstByReservationTenantIsInAndStatusAndReservationDateIsBetweenAndMachineIsOrderByReservationDateAsc(
        Set<Tenant> tenants,
        WashHistoryStatus status,
        Instant from,
        Instant until,
        LaundryMachine machine
    );

    Optional<WashHistory> findFirstByReservationTenantIsInAndStatusAndReservationDateIsBetweenAndMachineIsAndProgramIsOrderByReservationDateAsc(
        Set<Tenant> tenants,
        WashHistoryStatus status,
        Instant from,
        Instant until,
        LaundryMachine machine,
        LaundryMachineProgram program
    );

    List<WashHistory> findAllByStatusAndReservationDateIsBefore(WashHistoryStatus status, Instant time);

    List<WashHistory> findAllByMachineAndStatusAndReservationDateIsNotNull(LaundryMachine machine, WashHistoryStatus status);

    Optional<WashHistory> findFirstByMachineAndStatusAndUsingDateIsNotNullOrderByUsingDateDesc(
        LaundryMachine machine,
        WashHistoryStatus status
    );

    @Query(
        "select h from WashHistory h where h.usingTenant in :tenants and h.status = de.farue.autocut.domain.enumeration.WashHistoryStatus.COMPLETED"
    )
    Page<WashHistory> findWashHistory(Collection<Tenant> tenants, Pageable pageable);

    @Query(
        "select h from WashHistory h where h.usingTenant in :tenants and h.machine = :machine and h.status = de.farue.autocut.domain.enumeration.WashHistoryStatus.COMPLETED"
    )
    Page<WashHistory> findWashHistory(Collection<Tenant> tenants, LaundryMachine machine, Pageable pageable);
}
