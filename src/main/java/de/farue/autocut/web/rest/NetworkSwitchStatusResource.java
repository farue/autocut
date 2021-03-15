package de.farue.autocut.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.farue.autocut.domain.NetworkSwitchStatus;
import de.farue.autocut.service.NetworkSwitchStatusService;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.NetworkSwitchStatus}.
 */
@RestController
@RequestMapping("/api")
public class NetworkSwitchStatusResource {

    private final Logger log = LoggerFactory.getLogger(NetworkSwitchStatusResource.class);

    private static final String ENTITY_NAME = "networkSwitchStatus";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NetworkSwitchStatusService networkSwitchStatusService;

    public NetworkSwitchStatusResource(NetworkSwitchStatusService networkSwitchStatusService) {
        this.networkSwitchStatusService = networkSwitchStatusService;
    }

    /**
     * {@code POST  /network-switch-statuses} : Create a new networkSwitchStatus.
     *
     * @param networkSwitchStatus the networkSwitchStatus to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new networkSwitchStatus, or with status {@code 400 (Bad Request)} if the networkSwitchStatus has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/network-switch-statuses")
    public ResponseEntity<NetworkSwitchStatus> createNetworkSwitchStatus(@Valid @RequestBody NetworkSwitchStatus networkSwitchStatus) throws URISyntaxException {
        log.debug("REST request to save NetworkSwitchStatus : {}", networkSwitchStatus);
        if (networkSwitchStatus.getId() != null) {
            throw new BadRequestAlertException("A new networkSwitchStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NetworkSwitchStatus result = networkSwitchStatusService.save(networkSwitchStatus);
        return ResponseEntity.created(new URI("/api/network-switch-statuses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /network-switch-statuses} : Updates an existing networkSwitchStatus.
     *
     * @param networkSwitchStatus the networkSwitchStatus to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated networkSwitchStatus,
     * or with status {@code 400 (Bad Request)} if the networkSwitchStatus is not valid,
     * or with status {@code 500 (Internal Server Error)} if the networkSwitchStatus couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/network-switch-statuses")
    public ResponseEntity<NetworkSwitchStatus> updateNetworkSwitchStatus(@Valid @RequestBody NetworkSwitchStatus networkSwitchStatus) throws URISyntaxException {
        log.debug("REST request to update NetworkSwitchStatus : {}", networkSwitchStatus);
        if (networkSwitchStatus.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        NetworkSwitchStatus result = networkSwitchStatusService.save(networkSwitchStatus);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, networkSwitchStatus.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /network-switch-statuses} : get all the networkSwitchStatuses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of networkSwitchStatuses in body.
     */
    @GetMapping("/network-switch-statuses")
    public List<NetworkSwitchStatus> getAllNetworkSwitchStatuses() {
        log.debug("REST request to get all NetworkSwitchStatuses");
        return networkSwitchStatusService.findAll();
    }

    /**
     * {@code GET  /network-switch-statuses/:id} : get the "id" networkSwitchStatus.
     *
     * @param id the id of the networkSwitchStatus to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the networkSwitchStatus, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/network-switch-statuses/{id}")
    public ResponseEntity<NetworkSwitchStatus> getNetworkSwitchStatus(@PathVariable Long id) {
        log.debug("REST request to get NetworkSwitchStatus : {}", id);
        Optional<NetworkSwitchStatus> networkSwitchStatus = networkSwitchStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(networkSwitchStatus);
    }

    /**
     * {@code DELETE  /network-switch-statuses/:id} : delete the "id" networkSwitchStatus.
     *
     * @param id the id of the networkSwitchStatus to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/network-switch-statuses/{id}")
    public ResponseEntity<Void> deleteNetworkSwitchStatus(@PathVariable Long id) {
        log.debug("REST request to delete NetworkSwitchStatus : {}", id);
        networkSwitchStatusService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
