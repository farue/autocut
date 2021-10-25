package de.farue.autocut.service;

import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.repository.NetworkSwitchRepository;
import java.util.List;
import java.util.Optional;
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

    public NetworkSwitchService(NetworkSwitchRepository networkSwitchRepository) {
        this.networkSwitchRepository = networkSwitchRepository;
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
}
