package de.farue.autocut.repository;

import de.farue.autocut.domain.NetworkSwitchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the NetworkSwitchStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NetworkSwitchStatusRepository extends JpaRepository<NetworkSwitchStatus, Long> {}
