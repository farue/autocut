package de.farue.autocut.repository;

import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Tenant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findOneByUser(User user);
}
