package de.farue.autocut.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;

/**
 * Spring Data  repository for the Transaction entity.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select t from Transaction t where t.transactionBook = :transactionBook and t.valueDate <= :date order by t.valueDate desc, t.id desc")
    Page<Transaction> findFirstByTransactionBookBefore(TransactionBook transactionBook, Instant date, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Transaction t where t.transactionBook = :transactionBook and t.valueDate <= :date order by t.valueDate desc, t.id desc")
    Page<Transaction> findFirstByTransactionBookBeforeWithLock(TransactionBook transactionBook, Instant date, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select t from Transaction t where t.transactionBook = :transactionBook")
    Page<Transaction> findAllByTransactionBook(TransactionBook transactionBook, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Transaction t where t.transactionBook = :transactionBook and (t.valueDate > :date or (t.valueDate = :date and t.id > :id)) order by t.valueDate asc, t.id asc")
    List<Transaction> findAllNewerThanWithLock(TransactionBook transactionBook, Instant date, long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query(value = "select transaction from Transaction transaction left join fetch transaction.lefts",
        countQuery = "select count(distinct transaction) from Transaction transaction")
    Page<Transaction> findAllWithEagerRelationships(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select transaction from Transaction transaction left join fetch transaction.lefts")
    List<Transaction> findAllWithEagerRelationships();

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select transaction from Transaction transaction left join fetch transaction.lefts where transaction.id =:id")
    Optional<Transaction> findOneWithEagerRelationships(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select t from Transaction t where t.transactionBook = :transactionBook and t.issuer = :issuer")
    Page<Transaction> findAllByTransactionBookAndIssuer(TransactionBook transactionBook, String issuer, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select t from Transaction t where t.transactionBook = :transactionBook and t.issuer = :issuer and t.serviceQulifier = :serviceQualifier")
    List<Transaction> findAllByTransactionBookAndIssuerAndServiceQulifier(TransactionBook transactionBook, String issuer, String serviceQualifier);
}
