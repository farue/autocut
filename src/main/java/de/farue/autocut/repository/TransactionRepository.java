package de.farue.autocut.repository;

import java.time.Instant;
import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.Transaction;


/**
 * Spring Data  repository for the Transaction entity.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select t from Transaction t where t.lease = :lease and t.valueDate <= :date order by t.valueDate desc, t.id desc")
    Page<Transaction> findFirstByLeaseBefore(Lease lease, Instant date, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Transaction t where t.lease = :lease and t.valueDate <= :date order by t.valueDate desc, t.id desc")
    Page<Transaction> findFirstByLeaseBeforeWithLock(Lease lease, Instant date, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Page<Transaction> findAllByLeaseOrderByValueDateDesc(Lease lease, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Page<Transaction> findAllByTenantOrderByValueDateDesc(Tenant tenant, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Transaction t where t.lease = :lease and t.valueDate > :date order by t.valueDate asc, t.id asc")
    List<Transaction> findAllNewerThanWithLock(Lease lease, Instant date);
}
