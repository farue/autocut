package de.farue.autocut.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.domain.NetworkSwitchStatus;

/**
 * Spring Data  repository for the NetworkSwitchStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NetworkSwitchStatusRepository extends JpaRepository<NetworkSwitchStatus, Long> {

    List<NetworkSwitchStatus> findAllByNetworkSwitch(NetworkSwitch networkSwitch);
}
