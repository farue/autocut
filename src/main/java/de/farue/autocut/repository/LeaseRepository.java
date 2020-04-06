package de.farue.autocut.repository;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Lease entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long> {

    Optional<Lease> findOneByTenants(Tenant tenant);

}
