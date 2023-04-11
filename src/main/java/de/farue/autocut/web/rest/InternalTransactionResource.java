package de.farue.autocut.web.rest;

import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.repository.InternalTransactionRepository;
import de.farue.autocut.service.accounting.InternalTransactionService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.InternalTransaction}.
 */
@RestController
@RequestMapping("/api")
public class InternalTransactionResource {

    private final Logger log = LoggerFactory.getLogger(InternalTransactionResource.class);

    private static final String ENTITY_NAME = "internalTransaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InternalTransactionService internalTransactionService;

    private final InternalTransactionRepository internalTransactionRepository;

    public InternalTransactionResource(
        InternalTransactionService internalTransactionService,
        InternalTransactionRepository internalTransactionRepository
    ) {
        this.internalTransactionService = internalTransactionService;
        this.internalTransactionRepository = internalTransactionRepository;
    }

    /**
     * {@code POST  /internal-transactions} : Create a new internalTransaction.
     *
     * @param internalTransaction the internalTransaction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new internalTransaction, or with status {@code 400 (Bad Request)} if the internalTransaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/internal-transactions")
    public ResponseEntity<InternalTransaction> createInternalTransaction(@Valid @RequestBody InternalTransaction internalTransaction)
        throws URISyntaxException {
        log.debug("REST request to save InternalTransaction : {}", internalTransaction);
        if (internalTransaction.getId() != null) {
            throw new BadRequestAlertException("A new internalTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        InternalTransaction result = internalTransactionService.save(internalTransaction);
        return ResponseEntity
            .created(new URI("/api/internal-transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /internal-transactions/:id} : Updates an existing internalTransaction.
     *
     * @param id the id of the internalTransaction to save.
     * @param internalTransaction the internalTransaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated internalTransaction,
     * or with status {@code 400 (Bad Request)} if the internalTransaction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the internalTransaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/internal-transactions/{id}")
    public ResponseEntity<InternalTransaction> updateInternalTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InternalTransaction internalTransaction
    ) throws URISyntaxException {
        log.debug("REST request to update InternalTransaction : {}, {}", id, internalTransaction);
        if (internalTransaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, internalTransaction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!internalTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        InternalTransaction result = internalTransactionService.save(internalTransaction);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, internalTransaction.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /internal-transactions/:id} : Partial updates given fields of an existing internalTransaction, field will ignore if it is null
     *
     * @param id the id of the internalTransaction to save.
     * @param internalTransaction the internalTransaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated internalTransaction,
     * or with status {@code 400 (Bad Request)} if the internalTransaction is not valid,
     * or with status {@code 404 (Not Found)} if the internalTransaction is not found,
     * or with status {@code 500 (Internal Server Error)} if the internalTransaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/internal-transactions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InternalTransaction> partialUpdateInternalTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InternalTransaction internalTransaction
    ) throws URISyntaxException {
        log.debug("REST request to partial update InternalTransaction partially : {}, {}", id, internalTransaction);
        if (internalTransaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, internalTransaction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!internalTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InternalTransaction> result = internalTransactionService.partialUpdate(internalTransaction);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, internalTransaction.getId().toString())
        );
    }

    /**
     * {@code GET  /internal-transactions} : get all the internalTransactions.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of internalTransactions in body.
     */
    @GetMapping("/internal-transactions")
    public ResponseEntity<List<InternalTransaction>> getAllInternalTransactions(
        Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of InternalTransactions");
        Page<InternalTransaction> page;
        if (eagerload) {
            page = internalTransactionService.findAllWithEagerRelationships(pageable);
        } else {
            page = internalTransactionService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /internal-transactions/:id} : get the "id" internalTransaction.
     *
     * @param id the id of the internalTransaction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the internalTransaction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/internal-transactions/{id}")
    public ResponseEntity<InternalTransaction> getInternalTransaction(@PathVariable Long id) {
        log.debug("REST request to get InternalTransaction : {}", id);
        Optional<InternalTransaction> internalTransaction = internalTransactionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(internalTransaction);
    }

    /**
     * {@code DELETE  /internal-transactions/:id} : delete the "id" internalTransaction.
     *
     * @param id the id of the internalTransaction to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/internal-transactions/{id}")
    public ResponseEntity<Void> deleteInternalTransaction(@PathVariable Long id) {
        log.debug("REST request to delete InternalTransaction : {}", id);
        internalTransactionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/internal-transactions/{id}/reverse")
    public void reverse(@PathVariable Long id) {
        log.debug("REST request to reverse InternalTransaction : {}", id);
        internalTransactionService.reverse(id);
    }
}
