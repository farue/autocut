package de.farue.autocut.repository;

import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.domain.NetworkSwitchStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the NetworkSwitchStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NetworkSwitchStatusRepository extends JpaRepository<NetworkSwitchStatus, Long> {
    List<NetworkSwitchStatus> findAllByNetworkSwitch(NetworkSwitch networkSwitch);
}
