package de.farue.autocut.web.rest;

import de.farue.autocut.domain.BroadcastMessageText;
import de.farue.autocut.repository.BroadcastMessageTextRepository;
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
 * REST controller for managing {@link de.farue.autocut.domain.BroadcastMessageText}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BroadcastMessageTextResource {

    private final Logger log = LoggerFactory.getLogger(BroadcastMessageTextResource.class);

    private static final String ENTITY_NAME = "broadcastMessageText";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BroadcastMessageTextRepository broadcastMessageTextRepository;

    public BroadcastMessageTextResource(BroadcastMessageTextRepository broadcastMessageTextRepository) {
        this.broadcastMessageTextRepository = broadcastMessageTextRepository;
    }

    /**
     * {@code POST  /broadcast-message-texts} : Create a new broadcastMessageText.
     *
     * @param broadcastMessageText the broadcastMessageText to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new broadcastMessageText, or with status {@code 400 (Bad Request)} if the broadcastMessageText has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/broadcast-message-texts")
    public ResponseEntity<BroadcastMessageText> createBroadcastMessageText(@Valid @RequestBody BroadcastMessageText broadcastMessageText)
        throws URISyntaxException {
        log.debug("REST request to save BroadcastMessageText : {}", broadcastMessageText);
        if (broadcastMessageText.getId() != null) {
            throw new BadRequestAlertException("A new broadcastMessageText cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BroadcastMessageText result = broadcastMessageTextRepository.save(broadcastMessageText);
        return ResponseEntity
            .created(new URI("/api/broadcast-message-texts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /broadcast-message-texts/:id} : Updates an existing broadcastMessageText.
     *
     * @param id the id of the broadcastMessageText to save.
     * @param broadcastMessageText the broadcastMessageText to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated broadcastMessageText,
     * or with status {@code 400 (Bad Request)} if the broadcastMessageText is not valid,
     * or with status {@code 500 (Internal Server Error)} if the broadcastMessageText couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/broadcast-message-texts/{id}")
    public ResponseEntity<BroadcastMessageText> updateBroadcastMessageText(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BroadcastMessageText broadcastMessageText
    ) throws URISyntaxException {
        log.debug("REST request to update BroadcastMessageText : {}, {}", id, broadcastMessageText);
        if (broadcastMessageText.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, broadcastMessageText.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!broadcastMessageTextRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BroadcastMessageText result = broadcastMessageTextRepository.save(broadcastMessageText);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, broadcastMessageText.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /broadcast-message-texts/:id} : Partial updates given fields of an existing broadcastMessageText, field will ignore if it is null
     *
     * @param id the id of the broadcastMessageText to save.
     * @param broadcastMessageText the broadcastMessageText to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated broadcastMessageText,
     * or with status {@code 400 (Bad Request)} if the broadcastMessageText is not valid,
     * or with status {@code 404 (Not Found)} if the broadcastMessageText is not found,
     * or with status {@code 500 (Internal Server Error)} if the broadcastMessageText couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/broadcast-message-texts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BroadcastMessageText> partialUpdateBroadcastMessageText(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BroadcastMessageText broadcastMessageText
    ) throws URISyntaxException {
        log.debug("REST request to partial update BroadcastMessageText partially : {}, {}", id, broadcastMessageText);
        if (broadcastMessageText.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, broadcastMessageText.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!broadcastMessageTextRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BroadcastMessageText> result = broadcastMessageTextRepository
            .findById(broadcastMessageText.getId())
            .map(existingBroadcastMessageText -> {
                if (broadcastMessageText.getLangKey() != null) {
                    existingBroadcastMessageText.setLangKey(broadcastMessageText.getLangKey());
                }
                if (broadcastMessageText.getText() != null) {
                    existingBroadcastMessageText.setText(broadcastMessageText.getText());
                }

                return existingBroadcastMessageText;
            })
            .map(broadcastMessageTextRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, broadcastMessageText.getId().toString())
        );
    }

    /**
     * {@code GET  /broadcast-message-texts} : get all the broadcastMessageTexts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of broadcastMessageTexts in body.
     */
    @GetMapping("/broadcast-message-texts")
    public List<BroadcastMessageText> getAllBroadcastMessageTexts() {
        log.debug("REST request to get all BroadcastMessageTexts");
        return broadcastMessageTextRepository.findAll();
    }

    /**
     * {@code GET  /broadcast-message-texts/:id} : get the "id" broadcastMessageText.
     *
     * @param id the id of the broadcastMessageText to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the broadcastMessageText, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/broadcast-message-texts/{id}")
    public ResponseEntity<BroadcastMessageText> getBroadcastMessageText(@PathVariable Long id) {
        log.debug("REST request to get BroadcastMessageText : {}", id);
        Optional<BroadcastMessageText> broadcastMessageText = broadcastMessageTextRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(broadcastMessageText);
    }

    /**
     * {@code DELETE  /broadcast-message-texts/:id} : delete the "id" broadcastMessageText.
     *
     * @param id the id of the broadcastMessageText to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/broadcast-message-texts/{id}")
    public ResponseEntity<Void> deleteBroadcastMessageText(@PathVariable Long id) {
        log.debug("REST request to delete BroadcastMessageText : {}", id);
        broadcastMessageTextRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
