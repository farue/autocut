package de.farue.autocut.repository;

import de.farue.autocut.domain.BankTransaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BankTransaction entity.
 */
@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    @Query(
        value = "select distinct bankTransaction from BankTransaction bankTransaction left join fetch bankTransaction.lefts",
        countQuery = "select count(distinct bankTransaction) from BankTransaction bankTransaction"
    )
    Page<BankTransaction> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct bankTransaction from BankTransaction bankTransaction left join fetch bankTransaction.lefts")
    List<BankTransaction> findAllWithEagerRelationships();

    @Query(
        "select bankTransaction from BankTransaction bankTransaction left join fetch bankTransaction.lefts where bankTransaction.id =:id"
    )
    Optional<BankTransaction> findOneWithEagerRelationships(@Param("id") Long id);
}
