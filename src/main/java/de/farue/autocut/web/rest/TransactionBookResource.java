package de.farue.autocut.web.rest;

import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.service.TransactionBookService;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link de.farue.autocut.domain.TransactionBook}.
 */
@RestController
@RequestMapping("/api")
public class TransactionBookResource {

    private final Logger log = LoggerFactory.getLogger(TransactionBookResource.class);

    private static final String ENTITY_NAME = "transactionBook";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionBookService transactionBookService;

    public TransactionBookResource(TransactionBookService transactionBookService) {
        this.transactionBookService = transactionBookService;
    }

    /**
     * {@code POST  /transaction-books} : Create a new transactionBook.
     *
     * @param transactionBook the transactionBook to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionBook, or with status {@code 400 (Bad Request)} if the transactionBook has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transaction-books")
    public ResponseEntity<TransactionBook> createTransactionBook(@RequestBody TransactionBook transactionBook) throws URISyntaxException {
        log.debug("REST request to save TransactionBook : {}", transactionBook);
        if (transactionBook.getId() != null) {
            throw new BadRequestAlertException("A new transactionBook cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransactionBook result = transactionBookService.save(transactionBook);
        return ResponseEntity.created(new URI("/api/transaction-books/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transaction-books} : Updates an existing transactionBook.
     *
     * @param transactionBook the transactionBook to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionBook,
     * or with status {@code 400 (Bad Request)} if the transactionBook is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionBook couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transaction-books")
    public ResponseEntity<TransactionBook> updateTransactionBook(@RequestBody TransactionBook transactionBook) throws URISyntaxException {
        log.debug("REST request to update TransactionBook : {}", transactionBook);
        if (transactionBook.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TransactionBook result = transactionBookService.save(transactionBook);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionBook.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /transaction-books} : get all the transactionBooks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactionBooks in body.
     */
    @GetMapping("/transaction-books")
    public List<TransactionBook> getAllTransactionBooks() {
        log.debug("REST request to get all TransactionBooks");
        return transactionBookService.findAll();
    }

    /**
     * {@code GET  /transaction-books/:id} : get the "id" transactionBook.
     *
     * @param id the id of the transactionBook to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transactionBook, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transaction-books/{id}")
    public ResponseEntity<TransactionBook> getTransactionBook(@PathVariable Long id) {
        log.debug("REST request to get TransactionBook : {}", id);
        Optional<TransactionBook> transactionBook = transactionBookService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionBook);
    }

    /**
     * {@code DELETE  /transaction-books/:id} : delete the "id" transactionBook.
     *
     * @param id the id of the transactionBook to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transaction-books/{id}")
    public ResponseEntity<Void> deleteTransactionBook(@PathVariable Long id) {
        log.debug("REST request to delete TransactionBook : {}", id);
        transactionBookService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
