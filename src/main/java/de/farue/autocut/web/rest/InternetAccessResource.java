package de.farue.autocut.web.rest;

import de.farue.autocut.domain.InternetAccess;
import de.farue.autocut.repository.InternetAccessRepository;
import de.farue.autocut.service.InternetAccessService;
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
 * REST controller for managing {@link de.farue.autocut.domain.InternetAccess}.
 */
@RestController
@RequestMapping("/api")
public class InternetAccessResource {

    private final Logger log = LoggerFactory.getLogger(InternetAccessResource.class);

    private static final String ENTITY_NAME = "internetAccess";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InternetAccessService internetAccessService;

    private final InternetAccessRepository internetAccessRepository;

    public InternetAccessResource(InternetAccessService internetAccessService, InternetAccessRepository internetAccessRepository) {
        this.internetAccessService = internetAccessService;
        this.internetAccessRepository = internetAccessRepository;
    }

    /**
     * {@code POST  /internet-accesses} : Create a new internetAccess.
     *
     * @param internetAccess the internetAccess to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new internetAccess, or with status {@code 400 (Bad Request)} if the internetAccess has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/internet-accesses")
    public ResponseEntity<InternetAccess> createInternetAccess(@Valid @RequestBody InternetAccess internetAccess)
        throws URISyntaxException {
        log.debug("REST request to save InternetAccess : {}", internetAccess);
        if (internetAccess.getId() != null) {
            throw new BadRequestAlertException("A new internetAccess cannot already have an ID", ENTITY_NAME, "idexists");
        }
        InternetAccess result = internetAccessService.save(internetAccess);
        return ResponseEntity
            .created(new URI("/api/internet-accesses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /internet-accesses/:id} : Updates an existing internetAccess.
     *
     * @param id the id of the internetAccess to save.
     * @param internetAccess the internetAccess to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated internetAccess,
     * or with status {@code 400 (Bad Request)} if the internetAccess is not valid,
     * or with status {@code 500 (Internal Server Error)} if the internetAccess couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/internet-accesses/{id}")
    public ResponseEntity<InternetAccess> updateInternetAccess(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InternetAccess internetAccess
    ) throws URISyntaxException {
        log.debug("REST request to update InternetAccess : {}, {}", id, internetAccess);
        if (internetAccess.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, internetAccess.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!internetAccessRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        InternetAccess result = internetAccessService.save(internetAccess);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, internetAccess.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /internet-accesses/:id} : Partial updates given fields of an existing internetAccess, field will ignore if it is null
     *
     * @param id the id of the internetAccess to save.
     * @param internetAccess the internetAccess to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated internetAccess,
     * or with status {@code 400 (Bad Request)} if the internetAccess is not valid,
     * or with status {@code 404 (Not Found)} if the internetAccess is not found,
     * or with status {@code 500 (Internal Server Error)} if the internetAccess couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/internet-accesses/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InternetAccess> partialUpdateInternetAccess(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InternetAccess internetAccess
    ) throws URISyntaxException {
        log.debug("REST request to partial update InternetAccess partially : {}, {}", id, internetAccess);
        if (internetAccess.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, internetAccess.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!internetAccessRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InternetAccess> result = internetAccessService.partialUpdate(internetAccess);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, internetAccess.getId().toString())
        );
    }

    /**
     * {@code GET  /internet-accesses} : get all the internetAccesses.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of internetAccesses in body.
     */
    @GetMapping("/internet-accesses")
    public List<InternetAccess> getAllInternetAccesses(@RequestParam(required = false) String filter) {
        if ("apartment-is-null".equals(filter)) {
            log.debug("REST request to get all InternetAccesss where apartment is null");
            return internetAccessService.findAllWhereApartmentIsNull();
        }
        log.debug("REST request to get all InternetAccesses");
        return internetAccessService.findAll();
    }

    /**
     * {@code GET  /internet-accesses/:id} : get the "id" internetAccess.
     *
     * @param id the id of the internetAccess to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the internetAccess, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/internet-accesses/{id}")
    public ResponseEntity<InternetAccess> getInternetAccess(@PathVariable Long id) {
        log.debug("REST request to get InternetAccess : {}", id);
        Optional<InternetAccess> internetAccess = internetAccessService.findOne(id);
        return ResponseUtil.wrapOrNotFound(internetAccess);
    }

    /**
     * {@code DELETE  /internet-accesses/:id} : delete the "id" internetAccess.
     *
     * @param id the id of the internetAccess to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/internet-accesses/{id}")
    public ResponseEntity<Void> deleteInternetAccess(@PathVariable Long id) {
        log.debug("REST request to delete InternetAccess : {}", id);
        internetAccessService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
