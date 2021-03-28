package de.farue.autocut.web.rest;

import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.repository.NetworkSwitchRepository;
import de.farue.autocut.service.NetworkSwitchService;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

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

    private final NetworkSwitchRepository networkSwitchRepository;

    public NetworkSwitchResource(NetworkSwitchService networkSwitchService, NetworkSwitchRepository networkSwitchRepository) {
        this.networkSwitchService = networkSwitchService;
        this.networkSwitchRepository = networkSwitchRepository;
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
        return ResponseEntity
            .created(new URI("/api/network-switches/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /network-switches/:id} : Updates an existing networkSwitch.
     *
     * @param id the id of the networkSwitch to save.
     * @param networkSwitch the networkSwitch to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated networkSwitch,
     * or with status {@code 400 (Bad Request)} if the networkSwitch is not valid,
     * or with status {@code 500 (Internal Server Error)} if the networkSwitch couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/network-switches/{id}")
    public ResponseEntity<NetworkSwitch> updateNetworkSwitch(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NetworkSwitch networkSwitch
    ) throws URISyntaxException {
        log.debug("REST request to update NetworkSwitch : {}, {}", id, networkSwitch);
        if (networkSwitch.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, networkSwitch.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!networkSwitchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        NetworkSwitch result = networkSwitchService.save(networkSwitch);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, networkSwitch.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /network-switches/:id} : Partial updates given fields of an existing networkSwitch, field will ignore if it is null
     *
     * @param id the id of the networkSwitch to save.
     * @param networkSwitch the networkSwitch to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated networkSwitch,
     * or with status {@code 400 (Bad Request)} if the networkSwitch is not valid,
     * or with status {@code 404 (Not Found)} if the networkSwitch is not found,
     * or with status {@code 500 (Internal Server Error)} if the networkSwitch couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/network-switches/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<NetworkSwitch> partialUpdateNetworkSwitch(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NetworkSwitch networkSwitch
    ) throws URISyntaxException {
        log.debug("REST request to partial update NetworkSwitch partially : {}, {}", id, networkSwitch);
        if (networkSwitch.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, networkSwitch.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!networkSwitchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NetworkSwitch> result = networkSwitchService.partialUpdate(networkSwitch);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, networkSwitch.getId().toString())
        );
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
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
