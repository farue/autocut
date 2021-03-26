package de.farue.autocut.repository;

import de.farue.autocut.domain.Transaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Transaction entity.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(
        value = "select distinct transaction from Transaction transaction left join fetch transaction.lefts",
        countQuery = "select count(distinct transaction) from Transaction transaction"
    )
    Page<Transaction> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct transaction from Transaction transaction left join fetch transaction.lefts")
    List<Transaction> findAllWithEagerRelationships();

    @Query("select transaction from Transaction transaction left join fetch transaction.lefts where transaction.id =:id")
    Optional<Transaction> findOneWithEagerRelationships(@Param("id") Long id);
}
