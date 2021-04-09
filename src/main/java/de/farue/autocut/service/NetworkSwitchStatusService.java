package de.farue.autocut.service;

import com.google.common.collect.Table;
import de.farue.autocut.domain.InternetAccess;
import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.domain.NetworkSwitchStatus;
import de.farue.autocut.repository.NetworkSwitchStatusRepository;
import de.farue.autocut.service.internetaccess.SwitchStatusColumns;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
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

    private static final TemporalAmount OUTDATED_DURATION = Duration.ofDays(1);

    private final Logger log = LoggerFactory.getLogger(NetworkSwitchStatusService.class);

    private final NetworkSwitchStatusRepository networkSwitchStatusRepository;
    private final NetworkSwitchService networkSwitchService;

    public NetworkSwitchStatusService(
        NetworkSwitchStatusRepository networkSwitchStatusRepository,
        NetworkSwitchService networkSwitchService
    ) {
        this.networkSwitchStatusRepository = networkSwitchStatusRepository;
        this.networkSwitchService = networkSwitchService;
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
            .map(
                existingNetworkSwitchStatus -> {
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
                }
            )
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

    public NetworkSwitchStatus getSwitchInterfaceStatus(InternetAccess internetAccess) {
        return getSwitchStatus(internetAccess.getNetworkSwitch())
            .stream()
            .filter(status -> StringUtils.equals(status.getPort(), internetAccess.getSwitchPortName()))
            .findFirst()
            .orElseThrow();
    }

    public List<NetworkSwitchStatus> getSwitchStatus(NetworkSwitch networkSwitch) {
        List<NetworkSwitchStatus> switchStatus = networkSwitchStatusRepository.findAllByNetworkSwitch(networkSwitch);
        Instant timestamp = Instant.now();

        if (!switchStatus.isEmpty()) {
            boolean outdated = switchStatus.stream().anyMatch(status -> status.getTimestamp().plus(OUTDATED_DURATION).isBefore(timestamp));
            if (!outdated) {
                return switchStatus;
            }
        }

        Table<String, String, String> status = networkSwitchService.getStatus(networkSwitch);
        Map<String, NetworkSwitchStatus> switchStatusByInterface = switchStatus
            .stream()
            .collect(
                Collectors.toMap(
                    NetworkSwitchStatus::getPort,
                    Function.identity(),
                    (s1, s2) -> {
                        throw new RuntimeException("");
                    }
                )
            );
        for (Entry<String, Map<String, String>> entry : status.rowMap().entrySet()) {
            Map<String, String> row = entry.getValue();
            String interfaceName = row.get(SwitchStatusColumns.PORT);
            NetworkSwitchStatus interfaceStatus = switchStatusByInterface.getOrDefault(interfaceName, new NetworkSwitchStatus());

            interfaceStatus
                .timestamp(timestamp)
                .networkSwitch(networkSwitch)
                .port(row.get(SwitchStatusColumns.PORT))
                .name(row.get(SwitchStatusColumns.NAME))
                .status(row.get(SwitchStatusColumns.STATUS))
                .vlan(row.get(SwitchStatusColumns.VLAN))
                .speed(row.get(SwitchStatusColumns.SPEED))
                .type(row.get(SwitchStatusColumns.TYPE));
            networkSwitchStatusRepository.save(interfaceStatus);
        }
        return networkSwitchStatusRepository.findAllByNetworkSwitch(networkSwitch);
    }
}
