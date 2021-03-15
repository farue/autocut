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

import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.service.NetworkSwitchService;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.NetworkSwitch}.
 */
@RestController
@RequestMapping("/api")
public class NetworkSwitchResource {

    private final Logger log = LoggerFactory.getLogger(NetworkSwitchResource.class);

    private static final String ENTITY_NAME = "networkSwitch";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NetworkSwitchService networkSwitchService;

    public NetworkSwitchResource(NetworkSwitchService networkSwitchService) {
        this.networkSwitchService = networkSwitchService;
    }

    /**
     * {@code POST  /network-switches} : Create a new networkSwitch.
     *
     * @param networkSwitch the networkSwitch to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new networkSwitch, or with status {@code 400 (Bad Request)} if the networkSwitch has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/network-switches")
    public ResponseEntity<NetworkSwitch> createNetworkSwitch(@Valid @RequestBody NetworkSwitch networkSwitch) throws URISyntaxException {
        log.debug("REST request to save NetworkSwitch : {}", networkSwitch);
        if (networkSwitch.getId() != null) {
            throw new BadRequestAlertException("A new networkSwitch cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NetworkSwitch result = networkSwitchService.save(networkSwitch);
        return ResponseEntity.created(new URI("/api/network-switches/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /network-switches} : Updates an existing networkSwitch.
     *
     * @param networkSwitch the networkSwitch to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated networkSwitch,
     * or with status {@code 400 (Bad Request)} if the networkSwitch is not valid,
     * or with status {@code 500 (Internal Server Error)} if the networkSwitch couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/network-switches")
    public ResponseEntity<NetworkSwitch> updateNetworkSwitch(@Valid @RequestBody NetworkSwitch networkSwitch) throws URISyntaxException {
        log.debug("REST request to update NetworkSwitch : {}", networkSwitch);
        if (networkSwitch.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        NetworkSwitch result = networkSwitchService.save(networkSwitch);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, networkSwitch.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /network-switches} : get all the networkSwitches.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of networkSwitches in body.
     */
    @GetMapping("/network-switches")
    public List<NetworkSwitch> getAllNetworkSwitches() {
        log.debug("REST request to get all NetworkSwitches");
        return networkSwitchService.findAll();
    }

    /**
     * {@code GET  /network-switches/:id} : get the "id" networkSwitch.
     *
     * @param id the id of the networkSwitch to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the networkSwitch, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/network-switches/{id}")
    public ResponseEntity<NetworkSwitch> getNetworkSwitch(@PathVariable Long id) {
        log.debug("REST request to get NetworkSwitch : {}", id);
        Optional<NetworkSwitch> networkSwitch = networkSwitchService.findOne(id);
        return ResponseUtil.wrapOrNotFound(networkSwitch);
    }

    /**
     * {@code DELETE  /network-switches/:id} : delete the "id" networkSwitch.
     *
     * @param id the id of the networkSwitch to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/network-switches/{id}")
    public ResponseEntity<Void> deleteNetworkSwitch(@PathVariable Long id) {
        log.debug("REST request to delete NetworkSwitch : {}", id);
        networkSwitchService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
