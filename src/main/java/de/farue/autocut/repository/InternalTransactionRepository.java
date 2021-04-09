package de.farue.autocut.repository;

import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.TransactionBook;
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the InternalTransaction entity.
 */
@Repository
public interface InternalTransactionRepository extends TransactionRepository<InternalTransaction> {
    @Query(
        value = "select distinct internalTransaction from InternalTransaction internalTransaction left join fetch internalTransaction.lefts",
        countQuery = "select count(distinct internalTransaction) from InternalTransaction internalTransaction"
    )
    Page<InternalTransaction> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct internalTransaction from InternalTransaction internalTransaction left join fetch internalTransaction.lefts")
    List<InternalTransaction> findAllWithEagerRelationships();

    @Query(
        "select internalTransaction from InternalTransaction internalTransaction left join fetch internalTransaction.lefts where internalTransaction.id =:id"
    )
    Optional<InternalTransaction> findOneWithEagerRelationships(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select t from InternalTransaction t where t.transactionBook = :transactionBook and t.issuer = :issuer")
    Page<InternalTransaction> findAllByTransactionBookAndIssuer(TransactionBook transactionBook, String issuer, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query(
        "select t from InternalTransaction t where t.transactionBook = :transactionBook and t.issuer = :issuer and t.serviceQulifier = :serviceQualifier"
    )
    List<InternalTransaction> findAllByTransactionBookAndIssuerAndServiceQulifier(
        TransactionBook transactionBook,
        String issuer,
        String serviceQualifier
    );
}
