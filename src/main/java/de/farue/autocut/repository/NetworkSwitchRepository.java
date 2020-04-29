package de.farue.autocut.repository;

import de.farue.autocut.domain.NetworkSwitch;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the NetworkSwitch entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NetworkSwitchRepository extends JpaRepository<NetworkSwitch, Long> {
}
