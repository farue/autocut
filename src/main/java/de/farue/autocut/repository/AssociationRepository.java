package de.farue.autocut.repository;

import de.farue.autocut.domain.Association;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Association entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssociationRepository extends JpaRepository<Association, Long> {
    String ACTIVE_ASSOCIATION_CACHE = "activeAssociation";

    @Cacheable(cacheNames = ACTIVE_ASSOCIATION_CACHE)
    Optional<Association> findByActiveTrue();
}
