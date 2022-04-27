package de.farue.autocut.repository;

import de.farue.autocut.domain.InternetAccess;
import de.farue.autocut.domain.NetworkSwitch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the InternetAccess entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InternetAccessRepository extends JpaRepository<InternetAccess, Long> {
    List<InternetAccess> findAllByNetworkSwitch(NetworkSwitch networkSwitch);
}
