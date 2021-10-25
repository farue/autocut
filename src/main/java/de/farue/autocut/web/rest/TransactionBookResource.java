package de.farue.autocut.web.rest;

import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.repository.TransactionBookRepository;
import de.farue.autocut.service.TransactionBookService;
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

    private final TransactionBookRepository transactionBookRepository;

    public TransactionBookResource(TransactionBookService transactionBookService, TransactionBookRepository transactionBookRepository) {
        this.transactionBookService = transactionBookService;
        this.transactionBookRepository = transactionBookRepository;
    }

    /**
     * {@code POST  /transaction-books} : Create a new transactionBook.
     *
     * @param transactionBook the transactionBook to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionBook, or with status {@code 400 (Bad Request)} if the transactionBook has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transaction-books")
    public ResponseEntity<TransactionBook> createTransactionBook(@Valid @RequestBody TransactionBook transactionBook)
        throws URISyntaxException {
        log.debug("REST request to save TransactionBook : {}", transactionBook);
        if (transactionBook.getId() != null) {
            throw new BadRequestAlertException("A new transactionBook cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransactionBook result = transactionBookService.save(transactionBook);
        return ResponseEntity
            .created(new URI("/api/transaction-books/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transaction-books/:id} : Updates an existing transactionBook.
     *
     * @param id the id of the transactionBook to save.
     * @param transactionBook the transactionBook to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionBook,
     * or with status {@code 400 (Bad Request)} if the transactionBook is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionBook couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transaction-books/{id}")
    public ResponseEntity<TransactionBook> updateTransactionBook(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransactionBook transactionBook
    ) throws URISyntaxException {
        log.debug("REST request to update TransactionBook : {}, {}", id, transactionBook);
        if (transactionBook.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionBook.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionBookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TransactionBook result = transactionBookService.save(transactionBook);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionBook.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /transaction-books/:id} : Partial updates given fields of an existing transactionBook, field will ignore if it is null
     *
     * @param id the id of the transactionBook to save.
     * @param transactionBook the transactionBook to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionBook,
     * or with status {@code 400 (Bad Request)} if the transactionBook is not valid,
     * or with status {@code 404 (Not Found)} if the transactionBook is not found,
     * or with status {@code 500 (Internal Server Error)} if the transactionBook couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/transaction-books/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransactionBook> partialUpdateTransactionBook(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransactionBook transactionBook
    ) throws URISyntaxException {
        log.debug("REST request to partial update TransactionBook partially : {}, {}", id, transactionBook);
        if (transactionBook.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionBook.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionBookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransactionBook> result = transactionBookService.partialUpdate(transactionBook);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionBook.getId().toString())
        );
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
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
