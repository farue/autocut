package de.farue.autocut.repository;

import de.farue.autocut.domain.Apartment;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Lease entity.
 */
@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long> {
    @Query(
        value = "select distinct lease from Lease lease left join fetch lease.transactionBooks left join fetch lease.tenants",
        countQuery = "select count(distinct lease) from Lease lease"
    )
    Page<Lease> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct lease from Lease lease left join fetch lease.transactionBooks left join fetch lease.tenants")
    List<Lease> findAllWithEagerRelationships();

    @Query("select lease from Lease lease left join fetch lease.transactionBooks left join fetch lease.tenants where lease.id =:id")
    Optional<Lease> findOneWithEagerRelationships(@Param("id") Long id);

    Optional<Lease> findOneByTenants(Tenant tenant);

    // Not "findFirst" because there should only be one at most
    @Query("select l from Lease l where l.apartment = :apartment and l.start <= :date and (l.end is null or l.end > :date)")
    List<Lease> findAllByApartmentAndDate(Apartment apartment, Instant date);
}
