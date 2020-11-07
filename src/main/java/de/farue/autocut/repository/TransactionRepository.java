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
public interface TransactionRepository<T extends Transaction> extends JpaRepository<T, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select t from #{#entityName} t where t.transactionBook = :transactionBook and t.valueDate <= :date order by t.valueDate desc, t.id desc")
    Page<T> findFirstByTransactionBookBefore(TransactionBook transactionBook, Instant date, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from #{#entityName} t where t.transactionBook = :transactionBook and t.valueDate <= :date order by t.valueDate desc, t.id desc")
    Page<T> findFirstByTransactionBookBeforeWithLock(TransactionBook transactionBook, Instant date, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select t from #{#entityName} t where t.transactionBook = :transactionBook")
    Page<T> findAllByTransactionBook(TransactionBook transactionBook, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query(value = "select t from #{#entityName} t left join fetch t.lefts where t.transactionBook = :transactionBook",
        countQuery = "select count(distinct t) from #{#entityName} t where t.transactionBook = :transactionBook")
    List<T> findAllByTransactionBookWithEagerRelationships(TransactionBook transactionBook);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from #{#entityName} t where t.transactionBook = :transactionBook and (t.valueDate > :date or (t.valueDate = :date and t.id > :id))")
    List<T> findAllNewerThanWithLock(TransactionBook transactionBook, Instant date, long id, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from #{#entityName} t where t.transactionBook = :transactionBook and (t.valueDate < :date or (t.valueDate = :date and t.id < :id))")
    Page<T> findAllOlderThanWithLock(TransactionBook transactionBook, Instant date, long id, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query(value = "select transaction from #{#entityName} transaction left join fetch transaction.lefts",
        countQuery = "select count(distinct transaction) from #{#entityName} transaction")
    Page<T> findAllWithEagerRelationships(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select transaction from #{#entityName} transaction left join fetch transaction.lefts")
    List<T> findAllWithEagerRelationships();

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select transaction from #{#entityName} transaction left join fetch transaction.lefts where transaction.id =:id")
    Optional<T> findOneWithEagerRelationships(@Param("id") Long id);
}
