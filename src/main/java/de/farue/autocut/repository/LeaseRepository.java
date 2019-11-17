package de.farue.autocut.repository;

import de.farue.autocut.domain.Lease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Lease entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long> {

}
