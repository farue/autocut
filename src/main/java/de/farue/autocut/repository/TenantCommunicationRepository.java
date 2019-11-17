package de.farue.autocut.repository;

import de.farue.autocut.domain.TenantCommunication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the TenantCommunication entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TenantCommunicationRepository extends JpaRepository<TenantCommunication, Long> {

}
