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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.repository.BankTransactionRepository;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.BankTransaction}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BankTransactionResource {

    private final Logger log = LoggerFactory.getLogger(BankTransactionResource.class);

    private static final String ENTITY_NAME = "bankTransaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BankTransactionRepository bankTransactionRepository;

    public BankTransactionResource(BankTransactionRepository bankTransactionRepository) {
        this.bankTransactionRepository = bankTransactionRepository;
    }

    /**
     * {@code POST  /bank-transactions} : Create a new bankTransaction.
     *
     * @param bankTransaction the bankTransaction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bankTransaction, or with status {@code 400 (Bad Request)} if the bankTransaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bank-transactions")
    public ResponseEntity<BankTransaction> createBankTransaction(@Valid @RequestBody BankTransaction bankTransaction) throws URISyntaxException {
        log.debug("REST request to save BankTransaction : {}", bankTransaction);
        if (bankTransaction.getId() != null) {
            throw new BadRequestAlertException("A new bankTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BankTransaction result = bankTransactionRepository.save(bankTransaction);
        return ResponseEntity.created(new URI("/api/bank-transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bank-transactions} : Updates an existing bankTransaction.
     *
     * @param bankTransaction the bankTransaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bankTransaction,
     * or with status {@code 400 (Bad Request)} if the bankTransaction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bankTransaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bank-transactions")
    public ResponseEntity<BankTransaction> updateBankTransaction(@Valid @RequestBody BankTransaction bankTransaction) throws URISyntaxException {
        log.debug("REST request to update BankTransaction : {}", bankTransaction);
        if (bankTransaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BankTransaction result = bankTransactionRepository.save(bankTransaction);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bankTransaction.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /bank-transactions} : get all the bankTransactions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bankTransactions in body.
     */
    @GetMapping("/bank-transactions")
    public List<BankTransaction> getAllBankTransactions(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all BankTransactions");
        return bankTransactionRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /bank-transactions/:id} : get the "id" bankTransaction.
     *
     * @param id the id of the bankTransaction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bankTransaction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bank-transactions/{id}")
    public ResponseEntity<BankTransaction> getBankTransaction(@PathVariable Long id) {
        log.debug("REST request to get BankTransaction : {}", id);
        Optional<BankTransaction> bankTransaction = bankTransactionRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(bankTransaction);
    }

    /**
     * {@code DELETE  /bank-transactions/:id} : delete the "id" bankTransaction.
     *
     * @param id the id of the bankTransaction to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bank-transactions/{id}")
    public ResponseEntity<Void> deleteBankTransaction(@PathVariable Long id) {
        log.debug("REST request to delete BankTransaction : {}", id);
        bankTransactionRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
