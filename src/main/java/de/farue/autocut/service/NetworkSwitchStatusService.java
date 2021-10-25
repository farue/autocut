package de.farue.autocut.service;

import de.farue.autocut.domain.NetworkSwitchStatus;
import de.farue.autocut.repository.NetworkSwitchStatusRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link NetworkSwitchStatus}.
 */
@Service
@Transactional
public class NetworkSwitchStatusService {

    private final Logger log = LoggerFactory.getLogger(NetworkSwitchStatusService.class);

    private final NetworkSwitchStatusRepository networkSwitchStatusRepository;

    public NetworkSwitchStatusService(NetworkSwitchStatusRepository networkSwitchStatusRepository) {
        this.networkSwitchStatusRepository = networkSwitchStatusRepository;
    }

    /**
     * Save a networkSwitchStatus.
     *
     * @param networkSwitchStatus the entity to save.
     * @return the persisted entity.
     */
    public NetworkSwitchStatus save(NetworkSwitchStatus networkSwitchStatus) {
        log.debug("Request to save NetworkSwitchStatus : {}", networkSwitchStatus);
        return networkSwitchStatusRepository.save(networkSwitchStatus);
    }

    /**
     * Partially update a networkSwitchStatus.
     *
     * @param networkSwitchStatus the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<NetworkSwitchStatus> partialUpdate(NetworkSwitchStatus networkSwitchStatus) {
        log.debug("Request to partially update NetworkSwitchStatus : {}", networkSwitchStatus);

        return networkSwitchStatusRepository
            .findById(networkSwitchStatus.getId())
            .map(existingNetworkSwitchStatus -> {
                if (networkSwitchStatus.getPort() != null) {
                    existingNetworkSwitchStatus.setPort(networkSwitchStatus.getPort());
                }
                if (networkSwitchStatus.getName() != null) {
                    existingNetworkSwitchStatus.setName(networkSwitchStatus.getName());
                }
                if (networkSwitchStatus.getStatus() != null) {
                    existingNetworkSwitchStatus.setStatus(networkSwitchStatus.getStatus());
                }
                if (networkSwitchStatus.getVlan() != null) {
                    existingNetworkSwitchStatus.setVlan(networkSwitchStatus.getVlan());
                }
                if (networkSwitchStatus.getSpeed() != null) {
                    existingNetworkSwitchStatus.setSpeed(networkSwitchStatus.getSpeed());
                }
                if (networkSwitchStatus.getType() != null) {
                    existingNetworkSwitchStatus.setType(networkSwitchStatus.getType());
                }
                if (networkSwitchStatus.getTimestamp() != null) {
                    existingNetworkSwitchStatus.setTimestamp(networkSwitchStatus.getTimestamp());
                }

                return existingNetworkSwitchStatus;
            })
            .map(networkSwitchStatusRepository::save);
    }

    /**
     * Get all the networkSwitchStatuses.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<NetworkSwitchStatus> findAll() {
        log.debug("Request to get all NetworkSwitchStatuses");
        return networkSwitchStatusRepository.findAll();
    }

    /**
     * Get one networkSwitchStatus by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NetworkSwitchStatus> findOne(Long id) {
        log.debug("Request to get NetworkSwitchStatus : {}", id);
        return networkSwitchStatusRepository.findById(id);
    }

    /**
     * Delete the networkSwitchStatus by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete NetworkSwitchStatus : {}", id);
        networkSwitchStatusRepository.deleteById(id);
    }
}
