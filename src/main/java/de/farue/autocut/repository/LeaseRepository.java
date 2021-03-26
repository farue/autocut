package de.farue.autocut.repository;

import de.farue.autocut.domain.Lease;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Lease entity.
 */
@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long> {
    @Query(
        value = "select distinct lease from Lease lease left join fetch lease.transactionBooks",
        countQuery = "select count(distinct lease) from Lease lease"
    )
    Page<Lease> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct lease from Lease lease left join fetch lease.transactionBooks")
    List<Lease> findAllWithEagerRelationships();

    @Query("select lease from Lease lease left join fetch lease.transactionBooks where lease.id =:id")
    Optional<Lease> findOneWithEagerRelationships(@Param("id") Long id);
}
