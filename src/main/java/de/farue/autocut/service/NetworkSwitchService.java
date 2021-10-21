package de.farue.autocut.service;

import com.google.common.collect.Table;
import de.farue.autocut.domain.InternetAccess;
import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.repository.NetworkSwitchRepository;
import de.farue.autocut.service.internetaccess.SwitchCommandExecutor;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link NetworkSwitch}.
 */
@Service
@Transactional
public class NetworkSwitchService {

    private final Logger log = LoggerFactory.getLogger(NetworkSwitchService.class);

    private final NetworkSwitchRepository networkSwitchRepository;

    private final Map<String, SwitchCommandExecutor> switchExecutorsByHostName;

    public NetworkSwitchService(
        NetworkSwitchRepository networkSwitchRepository,
        Map<String, SwitchCommandExecutor> switchExecutorsByHostName
    ) {
        this.networkSwitchRepository = networkSwitchRepository;
        this.switchExecutorsByHostName = switchExecutorsByHostName;
    }

    @PreDestroy
    public void destroy() {
        switchExecutorsByHostName
            .values()
            .forEach(
                executor -> {
                    try {
                        executor.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            );
    }

    /**
     * Save a networkSwitch.
     *
     * @param networkSwitch the entity to save.
     * @return the persisted entity.
     */
    public NetworkSwitch save(NetworkSwitch networkSwitch) {
        log.debug("Request to save NetworkSwitch : {}", networkSwitch);
        return networkSwitchRepository.save(networkSwitch);
    }

    /**
     * Partially update a networkSwitch.
     *
     * @param networkSwitch the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<NetworkSwitch> partialUpdate(NetworkSwitch networkSwitch) {
        log.debug("Request to partially update NetworkSwitch : {}", networkSwitch);

        return networkSwitchRepository
            .findById(networkSwitch.getId())
            .map(existingNetworkSwitch -> {
                if (networkSwitch.getInterfaceName() != null) {
                    existingNetworkSwitch.setInterfaceName(networkSwitch.getInterfaceName());
                }
                if (networkSwitch.getSshHost() != null) {
                    existingNetworkSwitch.setSshHost(networkSwitch.getSshHost());
                }

                return existingNetworkSwitch;
            })
            .map(networkSwitchRepository::save);
    }

    /**
     * Get all the networkSwitches.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<NetworkSwitch> findAll() {
        log.debug("Request to get all NetworkSwitches");
        return networkSwitchRepository.findAll();
    }

    /**
     * Get one networkSwitch by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NetworkSwitch> findOne(Long id) {
        log.debug("Request to get NetworkSwitch : {}", id);
        return networkSwitchRepository.findById(id);
    }

    /**
     * Delete the networkSwitch by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete NetworkSwitch : {}", id);
        networkSwitchRepository.deleteById(id);
    }

    public void enable(InternetAccess internetAccess) {
        findSwitchCommandExecutor(internetAccess).enable(internetAccess);
    }

    public void disable(InternetAccess internetAccess) {
        findSwitchCommandExecutor(internetAccess).disable(internetAccess);
    }

    public Table<String, String, String> getStatus(NetworkSwitch networkSwitch) {
        return findSwitchCommandExecutor(networkSwitch).getStatus();
    }

    private SwitchCommandExecutor findSwitchCommandExecutor(InternetAccess internetAccess) {
        return findSwitchCommandExecutor(internetAccess.getNetworkSwitch());
    }

    private SwitchCommandExecutor findSwitchCommandExecutor(NetworkSwitch networkSwitch) {
        String hostname = networkSwitch.getSshHost();
        if (!switchExecutorsByHostName.containsKey(hostname)) {
            throw new RuntimeException("No switch command executor found for host: " + hostname);
        }
        return switchExecutorsByHostName.get(hostname);
    }
}
