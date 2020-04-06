package de.farue.autocut.repository;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Transaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds the last transaction.
     *
     * @return last transaction
     */
    Optional<Transaction> findFirstByLeaseOrderByIdDesc(Lease lease);

    Page<Transaction> findAllByLeaseOrderByIdDesc(Lease lease, Pageable pageable);

}
