package de.farue.autocut.repository;

import de.farue.autocut.domain.InternetAccess;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the InternetAccess entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InternetAccessRepository extends JpaRepository<InternetAccess, Long> {
}
