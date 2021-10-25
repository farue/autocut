package de.farue.autocut.repository;

import de.farue.autocut.domain.InternalTransaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the InternalTransaction entity.
 */
@Repository
public interface InternalTransactionRepository extends JpaRepository<InternalTransaction, Long> {
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
}
