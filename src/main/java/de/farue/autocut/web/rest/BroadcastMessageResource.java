package de.farue.autocut.web.rest;

import de.farue.autocut.domain.BroadcastMessage;
import de.farue.autocut.repository.BroadcastMessageRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.BroadcastMessage}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BroadcastMessageResource {

    private final Logger log = LoggerFactory.getLogger(BroadcastMessageResource.class);

    private static final String ENTITY_NAME = "broadcastMessage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BroadcastMessageRepository broadcastMessageRepository;

    public BroadcastMessageResource(BroadcastMessageRepository broadcastMessageRepository) {
        this.broadcastMessageRepository = broadcastMessageRepository;
    }

    /**
     * {@code POST  /broadcast-messages} : Create a new broadcastMessage.
     *
     * @param broadcastMessage the broadcastMessage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new broadcastMessage, or with status {@code 400 (Bad Request)} if the broadcastMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/broadcast-messages")
    public ResponseEntity<BroadcastMessage> createBroadcastMessage(@Valid @RequestBody BroadcastMessage broadcastMessage)
        throws URISyntaxException {
        log.debug("REST request to save BroadcastMessage : {}", broadcastMessage);
        if (broadcastMessage.getId() != null) {
            throw new BadRequestAlertException("A new broadcastMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BroadcastMessage result = broadcastMessageRepository.save(broadcastMessage);
        return ResponseEntity
            .created(new URI("/api/broadcast-messages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /broadcast-messages/:id} : Updates an existing broadcastMessage.
     *
     * @param id the id of the broadcastMessage to save.
     * @param broadcastMessage the broadcastMessage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated broadcastMessage,
     * or with status {@code 400 (Bad Request)} if the broadcastMessage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the broadcastMessage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/broadcast-messages/{id}")
    public ResponseEntity<BroadcastMessage> updateBroadcastMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BroadcastMessage broadcastMessage
    ) throws URISyntaxException {
        log.debug("REST request to update BroadcastMessage : {}, {}", id, broadcastMessage);
        if (broadcastMessage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, broadcastMessage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!broadcastMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BroadcastMessage result = broadcastMessageRepository.save(broadcastMessage);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, broadcastMessage.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /broadcast-messages/:id} : Partial updates given fields of an existing broadcastMessage, field will ignore if it is null
     *
     * @param id the id of the broadcastMessage to save.
     * @param broadcastMessage the broadcastMessage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated broadcastMessage,
     * or with status {@code 400 (Bad Request)} if the broadcastMessage is not valid,
     * or with status {@code 404 (Not Found)} if the broadcastMessage is not found,
     * or with status {@code 500 (Internal Server Error)} if the broadcastMessage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/broadcast-messages/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<BroadcastMessage> partialUpdateBroadcastMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BroadcastMessage broadcastMessage
    ) throws URISyntaxException {
        log.debug("REST request to partial update BroadcastMessage partially : {}, {}", id, broadcastMessage);
        if (broadcastMessage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, broadcastMessage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!broadcastMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BroadcastMessage> result = broadcastMessageRepository
            .findById(broadcastMessage.getId())
            .map(
                existingBroadcastMessage -> {
                    if (broadcastMessage.getType() != null) {
                        existingBroadcastMessage.setType(broadcastMessage.getType());
                    }
                    if (broadcastMessage.getStart() != null) {
                        existingBroadcastMessage.setStart(broadcastMessage.getStart());
                    }
                    if (broadcastMessage.getEnd() != null) {
                        existingBroadcastMessage.setEnd(broadcastMessage.getEnd());
                    }
                    if (broadcastMessage.getUsersOnly() != null) {
                        existingBroadcastMessage.setUsersOnly(broadcastMessage.getUsersOnly());
                    }
                    if (broadcastMessage.getDismissible() != null) {
                        existingBroadcastMessage.setDismissible(broadcastMessage.getDismissible());
                    }

                    return existingBroadcastMessage;
                }
            )
            .map(broadcastMessageRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, broadcastMessage.getId().toString())
        );
    }

    /**
     * {@code GET  /broadcast-messages} : get all the broadcastMessages.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of broadcastMessages in body.
     */
    @GetMapping("/broadcast-messages")
    public List<BroadcastMessage> getAllBroadcastMessages() {
        log.debug("REST request to get all BroadcastMessages");
        return broadcastMessageRepository.findAll();
    }

    /**
     * {@code GET  /broadcast-messages/:id} : get the "id" broadcastMessage.
     *
     * @param id the id of the broadcastMessage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the broadcastMessage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/broadcast-messages/{id}")
    public ResponseEntity<BroadcastMessage> getBroadcastMessage(@PathVariable Long id) {
        log.debug("REST request to get BroadcastMessage : {}", id);
        Optional<BroadcastMessage> broadcastMessage = broadcastMessageRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(broadcastMessage);
    }

    /**
     * {@code DELETE  /broadcast-messages/:id} : delete the "id" broadcastMessage.
     *
     * @param id the id of the broadcastMessage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/broadcast-messages/{id}")
    public ResponseEntity<Void> deleteBroadcastMessage(@PathVariable Long id) {
        log.debug("REST request to delete BroadcastMessage : {}", id);
        broadcastMessageRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
