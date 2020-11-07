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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.service.accounting.InternalTransactionService;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

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

    public InternalTransactionResource(InternalTransactionService internalTransactionService) {
        this.internalTransactionService = internalTransactionService;
    }

    /**
     * {@code POST  /internal-transactions} : Create a new internalTransaction.
     *
     * @param internalTransaction the internalTransaction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new internalTransaction, or with status {@code 400 (Bad Request)} if the internalTransaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/internal-transactions")
    public ResponseEntity<InternalTransaction> createInternalTransaction(@Valid @RequestBody InternalTransaction internalTransaction) throws URISyntaxException {
        log.debug("REST request to save InternalTransaction : {}", internalTransaction);
        if (internalTransaction.getId() != null) {
            throw new BadRequestAlertException("A new internalTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        InternalTransaction result = internalTransactionService.save(internalTransaction);
        return ResponseEntity.created(new URI("/api/internal-transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /internal-transactions} : Updates an existing internalTransaction.
     *
     * @param internalTransaction the internalTransaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated internalTransaction,
     * or with status {@code 400 (Bad Request)} if the internalTransaction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the internalTransaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/internal-transactions")
    public ResponseEntity<InternalTransaction> updateInternalTransaction(@Valid @RequestBody InternalTransaction internalTransaction) throws URISyntaxException {
        log.debug("REST request to update InternalTransaction : {}", internalTransaction);
        if (internalTransaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        InternalTransaction result = internalTransactionService.save(internalTransaction);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, internalTransaction.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /internal-transactions} : get all the internalTransactions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of internalTransactions in body.
     */
    @GetMapping("/internal-transactions")
    public List<InternalTransaction> getAllInternalTransactions(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all InternalTransactions");
        return internalTransactionService.findAll();
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
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
