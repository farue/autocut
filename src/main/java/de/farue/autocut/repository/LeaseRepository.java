package de.farue.autocut.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.farue.autocut.domain.Apartment;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;

/**
 * Spring Data  repository for the Lease entity.
 */
@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long> {

    Optional<Lease> findOneByTenants(Tenant tenant);

    // Not "findFirst" because there should only be one at most
    @Query("select l from Lease l where l.apartment = :apartment and l.start <= :date and (l.end is null or l.end > :date)")
    List<Lease> findAllByApartmentAndDate(Apartment apartment, Instant date);

}
