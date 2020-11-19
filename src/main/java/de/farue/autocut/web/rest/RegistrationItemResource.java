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

import de.farue.autocut.domain.RegistrationItem;
import de.farue.autocut.service.RegistrationItemService;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.RegistrationItem}.
 */
@RestController
@RequestMapping("/api")
public class RegistrationItemResource {

    private final Logger log = LoggerFactory.getLogger(RegistrationItemResource.class);

    private static final String ENTITY_NAME = "registrationItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RegistrationItemService registrationItemService;

    public RegistrationItemResource(RegistrationItemService registrationItemService) {
        this.registrationItemService = registrationItemService;
    }

    /**
     * {@code POST  /registration-items} : Create a new registrationItem.
     *
     * @param registrationItem the registrationItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new registrationItem, or with status {@code 400 (Bad Request)} if the registrationItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/registration-items")
    public ResponseEntity<RegistrationItem> createRegistrationItem(@Valid @RequestBody RegistrationItem registrationItem) throws URISyntaxException {
        log.debug("REST request to save RegistrationItem : {}", registrationItem);
        if (registrationItem.getId() != null) {
            throw new BadRequestAlertException("A new registrationItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RegistrationItem result = registrationItemService.save(registrationItem);
        return ResponseEntity.created(new URI("/api/registration-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /registration-items} : Updates an existing registrationItem.
     *
     * @param registrationItem the registrationItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated registrationItem,
     * or with status {@code 400 (Bad Request)} if the registrationItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the registrationItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/registration-items")
    public ResponseEntity<RegistrationItem> updateRegistrationItem(@Valid @RequestBody RegistrationItem registrationItem) throws URISyntaxException {
        log.debug("REST request to update RegistrationItem : {}", registrationItem);
        if (registrationItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RegistrationItem result = registrationItemService.save(registrationItem);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, registrationItem.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /registration-items} : get all the registrationItems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of registrationItems in body.
     */
    @GetMapping("/registration-items")
    public List<RegistrationItem> getAllRegistrationItems() {
        log.debug("REST request to get all RegistrationItems");
        return registrationItemService.findAll();
    }

    /**
     * {@code GET  /registration-items/:id} : get the "id" registrationItem.
     *
     * @param id the id of the registrationItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the registrationItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/registration-items/{id}")
    public ResponseEntity<RegistrationItem> getRegistrationItem(@PathVariable Long id) {
        log.debug("REST request to get RegistrationItem : {}", id);
        Optional<RegistrationItem> registrationItem = registrationItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(registrationItem);
    }

    /**
     * {@code DELETE  /registration-items/:id} : delete the "id" registrationItem.
     *
     * @param id the id of the registrationItem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/registration-items/{id}")
    public ResponseEntity<Void> deleteRegistrationItem(@PathVariable Long id) {
        log.debug("REST request to delete RegistrationItem : {}", id);
        registrationItemService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
