package de.farue.autocut.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.farue.autocut.domain.RegistrationItem;

/**
 * Spring Data  repository for the RegistrationItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RegistrationItemRepository extends JpaRepository<RegistrationItem, Long> {
}
