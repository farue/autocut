package de.farue.autocut.repository;

import de.farue.autocut.domain.Communication;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Communication entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommunicationRepository extends JpaRepository<Communication, Long> {

    @Query("select communication from Communication communication where communication.tenant.login = ?#{principal.username}")
    List<Communication> findByTenantIsCurrentUser();

}
