package de.farue.autocut.web.rest;

import de.farue.autocut.domain.Totp;
import de.farue.autocut.repository.TotpRepository;
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
 * REST controller for managing {@link de.farue.autocut.domain.Totp}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TotpResource {

    private final Logger log = LoggerFactory.getLogger(TotpResource.class);

    private static final String ENTITY_NAME = "totp";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TotpRepository totpRepository;

    public TotpResource(TotpRepository totpRepository) {
        this.totpRepository = totpRepository;
    }

    /**
     * {@code POST  /totps} : Create a new totp.
     *
     * @param totp the totp to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new totp, or with status {@code 400 (Bad Request)} if the totp has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/totps")
    public ResponseEntity<Totp> createTotp(@Valid @RequestBody Totp totp) throws URISyntaxException {
        log.debug("REST request to save Totp : {}", totp);
        if (totp.getId() != null) {
            throw new BadRequestAlertException("A new totp cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Totp result = totpRepository.save(totp);
        return ResponseEntity.created(new URI("/api/totps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /totps} : Updates an existing totp.
     *
     * @param totp the totp to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated totp,
     * or with status {@code 400 (Bad Request)} if the totp is not valid,
     * or with status {@code 500 (Internal Server Error)} if the totp couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/totps")
    public ResponseEntity<Totp> updateTotp(@Valid @RequestBody Totp totp) throws URISyntaxException {
        log.debug("REST request to update Totp : {}", totp);
        if (totp.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Totp result = totpRepository.save(totp);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, totp.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /totps} : get all the totps.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of totps in body.
     */
    @GetMapping("/totps")
    public List<Totp> getAllTotps() {
        log.debug("REST request to get all Totps");
        return totpRepository.findAll();
    }

    /**
     * {@code GET  /totps/:id} : get the "id" totp.
     *
     * @param id the id of the totp to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the totp, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/totps/{id}")
    public ResponseEntity<Totp> getTotp(@PathVariable Long id) {
        log.debug("REST request to get Totp : {}", id);
        Optional<Totp> totp = totpRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(totp);
    }

    /**
     * {@code DELETE  /totps/:id} : delete the "id" totp.
     *
     * @param id the id of the totp to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/totps/{id}")
    public ResponseEntity<Void> deleteTotp(@PathVariable Long id) {
        log.debug("REST request to delete Totp : {}", id);
        totpRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
