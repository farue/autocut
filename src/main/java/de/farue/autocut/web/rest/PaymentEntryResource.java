package de.farue.autocut.web.rest;

import de.farue.autocut.domain.PaymentEntry;
import de.farue.autocut.repository.PaymentEntryRepository;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link de.farue.autocut.domain.PaymentEntry}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PaymentEntryResource {

    private final Logger log = LoggerFactory.getLogger(PaymentEntryResource.class);

    private static final String ENTITY_NAME = "paymentEntry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaymentEntryRepository paymentEntryRepository;

    public PaymentEntryResource(PaymentEntryRepository paymentEntryRepository) {
        this.paymentEntryRepository = paymentEntryRepository;
    }

    /**
     * {@code POST  /payment-entries} : Create a new paymentEntry.
     *
     * @param paymentEntry the paymentEntry to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paymentEntry, or with status {@code 400 (Bad Request)} if the paymentEntry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/payment-entries")
    public ResponseEntity<PaymentEntry> createPaymentEntry(@Valid @RequestBody PaymentEntry paymentEntry) throws URISyntaxException {
        log.debug("REST request to save PaymentEntry : {}", paymentEntry);
        if (paymentEntry.getId() != null) {
            throw new BadRequestAlertException("A new paymentEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PaymentEntry result = paymentEntryRepository.save(paymentEntry);
        return ResponseEntity.created(new URI("/api/payment-entries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /payment-entries} : Updates an existing paymentEntry.
     *
     * @param paymentEntry the paymentEntry to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentEntry,
     * or with status {@code 400 (Bad Request)} if the paymentEntry is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paymentEntry couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/payment-entries")
    public ResponseEntity<PaymentEntry> updatePaymentEntry(@Valid @RequestBody PaymentEntry paymentEntry) throws URISyntaxException {
        log.debug("REST request to update PaymentEntry : {}", paymentEntry);
        if (paymentEntry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PaymentEntry result = paymentEntryRepository.save(paymentEntry);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentEntry.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /payment-entries} : get all the paymentEntries.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paymentEntries in body.
     */
    @GetMapping("/payment-entries")
    public List<PaymentEntry> getAllPaymentEntries() {
        log.debug("REST request to get all PaymentEntries");
        return paymentEntryRepository.findAll();
    }

    /**
     * {@code GET  /payment-entries/:id} : get the "id" paymentEntry.
     *
     * @param id the id of the paymentEntry to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paymentEntry, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/payment-entries/{id}")
    public ResponseEntity<PaymentEntry> getPaymentEntry(@PathVariable Long id) {
        log.debug("REST request to get PaymentEntry : {}", id);
        Optional<PaymentEntry> paymentEntry = paymentEntryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(paymentEntry);
    }

    /**
     * {@code DELETE  /payment-entries/:id} : delete the "id" paymentEntry.
     *
     * @param id the id of the paymentEntry to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/payment-entries/{id}")
    public ResponseEntity<Void> deletePaymentEntry(@PathVariable Long id) {
        log.debug("REST request to delete PaymentEntry : {}", id);
        paymentEntryRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
