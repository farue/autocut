package de.farue.autocut.web.rest;

import de.farue.autocut.domain.PaymentAccount;
import de.farue.autocut.service.PaymentAccountService;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link de.farue.autocut.domain.PaymentAccount}.
 */
@RestController
@RequestMapping("/api")
public class PaymentAccountResource {

    private final Logger log = LoggerFactory.getLogger(PaymentAccountResource.class);

    private static final String ENTITY_NAME = "paymentAccount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaymentAccountService paymentAccountService;

    public PaymentAccountResource(PaymentAccountService paymentAccountService) {
        this.paymentAccountService = paymentAccountService;
    }

    /**
     * {@code POST  /payment-accounts} : Create a new paymentAccount.
     *
     * @param paymentAccount the paymentAccount to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paymentAccount, or with status {@code 400 (Bad Request)} if the paymentAccount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/payment-accounts")
    public ResponseEntity<PaymentAccount> createPaymentAccount(@Valid @RequestBody PaymentAccount paymentAccount) throws URISyntaxException {
        log.debug("REST request to save PaymentAccount : {}", paymentAccount);
        if (paymentAccount.getId() != null) {
            throw new BadRequestAlertException("A new paymentAccount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PaymentAccount result = paymentAccountService.save(paymentAccount);
        return ResponseEntity.created(new URI("/api/payment-accounts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /payment-accounts} : Updates an existing paymentAccount.
     *
     * @param paymentAccount the paymentAccount to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentAccount,
     * or with status {@code 400 (Bad Request)} if the paymentAccount is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paymentAccount couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/payment-accounts")
    public ResponseEntity<PaymentAccount> updatePaymentAccount(@Valid @RequestBody PaymentAccount paymentAccount) throws URISyntaxException {
        log.debug("REST request to update PaymentAccount : {}", paymentAccount);
        if (paymentAccount.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PaymentAccount result = paymentAccountService.save(paymentAccount);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentAccount.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /payment-accounts} : get all the paymentAccounts.
     *

     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paymentAccounts in body.
     */
    @GetMapping("/payment-accounts")
    public List<PaymentAccount> getAllPaymentAccounts(@RequestParam(required = false) String filter) {
        if ("lease-is-null".equals(filter)) {
            log.debug("REST request to get all PaymentAccounts where lease is null");
            return paymentAccountService.findAllWhereLeaseIsNull();
        }
        log.debug("REST request to get all PaymentAccounts");
        return paymentAccountService.findAll();
    }

    /**
     * {@code GET  /payment-accounts/:id} : get the "id" paymentAccount.
     *
     * @param id the id of the paymentAccount to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paymentAccount, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/payment-accounts/{id}")
    public ResponseEntity<PaymentAccount> getPaymentAccount(@PathVariable Long id) {
        log.debug("REST request to get PaymentAccount : {}", id);
        Optional<PaymentAccount> paymentAccount = paymentAccountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paymentAccount);
    }

    /**
     * {@code DELETE  /payment-accounts/:id} : delete the "id" paymentAccount.
     *
     * @param id the id of the paymentAccount to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/payment-accounts/{id}")
    public ResponseEntity<Void> deletePaymentAccount(@PathVariable Long id) {
        log.debug("REST request to delete PaymentAccount : {}", id);
        paymentAccountService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
