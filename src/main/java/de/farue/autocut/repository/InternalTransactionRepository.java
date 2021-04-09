package de.farue.autocut.repository;

import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.TransactionBook;
import java.util.List;
import javax.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface InternalTransactionRepository extends TransactionRepository<InternalTransaction> {
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
