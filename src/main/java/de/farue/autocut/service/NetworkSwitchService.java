package de.farue.autocut.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Table;

import de.farue.autocut.domain.InternetAccess;
import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.repository.NetworkSwitchRepository;
import de.farue.autocut.service.internetaccess.SwitchCommandExecutor;

/**
 * Service Implementation for managing {@link NetworkSwitch}.
 */
@Service
@Transactional
public class NetworkSwitchService {

    private final Logger log = LoggerFactory.getLogger(NetworkSwitchService.class);

    private final NetworkSwitchRepository NetworkSwitchRepository;

    private final Map<String, SwitchCommandExecutor> switchExecutorsByHostName;

    public NetworkSwitchService(NetworkSwitchRepository NetworkSwitchRepository,
        Map<String, SwitchCommandExecutor> switchExecutorsByHostName) {
        this.NetworkSwitchRepository = NetworkSwitchRepository;
        this.switchExecutorsByHostName = switchExecutorsByHostName;
    }

    /**
     * Save a NetworkSwitch.
     *
     * @param NetworkSwitch the entity to save.
     * @return the persisted entity.
     */
    public NetworkSwitch save(NetworkSwitch NetworkSwitch) {
        log.debug("Request to save NetworkSwitch : {}", NetworkSwitch);
        return NetworkSwitchRepository.save(NetworkSwitch);
    }

    /**
     * Get all the NetworkSwitches.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<NetworkSwitch> findAll() {
        log.debug("Request to get all NetworkSwitches");
        return NetworkSwitchRepository.findAll();
    }


    /**
     * Get one NetworkSwitch by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NetworkSwitch> findOne(Long id) {
        log.debug("Request to get NetworkSwitch : {}", id);
        return NetworkSwitchRepository.findById(id);
    }

    /**
     * Delete the NetworkSwitch by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete NetworkSwitch : {}", id);
        NetworkSwitchRepository.deleteById(id);
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
