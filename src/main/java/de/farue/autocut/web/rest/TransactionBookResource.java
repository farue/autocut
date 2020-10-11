package de.farue.autocut.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.repository.UserRepository;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.TenantService;
import de.farue.autocut.service.TransactionService;
import de.farue.autocut.service.accounting.TransactionBookService;
import de.farue.autocut.service.dto.TransactionsOverviewDTO;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

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
    private final TransactionService transactionService;

    private final LeaseService leaseService;
    private final TenantService tenantService;
    private final UserRepository userRepository;


    public TransactionBookResource(TransactionBookService transactionBookService, TransactionService transactionService,
        LeaseService leaseService, TenantService tenantService, UserRepository userRepository) {
        this.transactionBookService = transactionBookService;
        this.transactionService = transactionService;
        this.leaseService = leaseService;
        this.tenantService = tenantService;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /transaction-books} : Create a new transactionBook.
     *
     * @param transactionBook the transactionBook to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionBook, or with status {@code 400 (Bad Request)} if the transactionBook has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transaction-books")
    public ResponseEntity<TransactionBook> createTransactionBook(@Valid @RequestBody TransactionBook transactionBook) throws URISyntaxException {
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
    public ResponseEntity<TransactionBook> updateTransactionBook(@Valid @RequestBody TransactionBook transactionBook) throws URISyntaxException {
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

    @GetMapping("/transaction-books/overview")
    @Transactional
    public ResponseEntity<TransactionsOverviewDTO> getTransactionsOverview(Pageable pageable) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(tenantService::findOneByUser)
            .map(Tenant::getLease)
            .map(lease -> {
                TransactionBook cashTransactionBook = leaseService.getCashTransactionBook(lease);
                TransactionBook depositTransactionBook = leaseService.getDepositTransactionBook(lease);
                Page<Transaction> page = transactionService.findAllForTransactionBook(cashTransactionBook, pageable);

                TransactionsOverviewDTO dto = new TransactionsOverviewDTO();
                dto.setTransactions(page.getContent());
                dto.setBalanceNow(transactionBookService.getCurrentBalance(cashTransactionBook));
                dto.setDeposit(transactionBookService.getCurrentBalance(depositTransactionBook));

                HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                return new ResponseEntity<>(dto, headers, HttpStatus.OK);
            })
            .orElse(new ResponseEntity<>(HttpStatus.OK));

    }
}
