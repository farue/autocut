package de.farue.autocut.web.rest;

import de.farue.autocut.domain.SecurityPolicy;
import de.farue.autocut.repository.SecurityPolicyRepository;
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
 * REST controller for managing {@link de.farue.autocut.domain.SecurityPolicy}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SecurityPolicyResource {

    private final Logger log = LoggerFactory.getLogger(SecurityPolicyResource.class);

    private static final String ENTITY_NAME = "securityPolicy";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SecurityPolicyRepository securityPolicyRepository;

    public SecurityPolicyResource(SecurityPolicyRepository securityPolicyRepository) {
        this.securityPolicyRepository = securityPolicyRepository;
    }

    /**
     * {@code POST  /security-policies} : Create a new securityPolicy.
     *
     * @param securityPolicy the securityPolicy to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new securityPolicy, or with status {@code 400 (Bad Request)} if the securityPolicy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/security-policies")
    public ResponseEntity<SecurityPolicy> createSecurityPolicy(@Valid @RequestBody SecurityPolicy securityPolicy)
        throws URISyntaxException {
        log.debug("REST request to save SecurityPolicy : {}", securityPolicy);
        if (securityPolicy.getId() != null) {
            throw new BadRequestAlertException("A new securityPolicy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SecurityPolicy result = securityPolicyRepository.save(securityPolicy);
        return ResponseEntity
            .created(new URI("/api/security-policies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /security-policies/:id} : Updates an existing securityPolicy.
     *
     * @param id the id of the securityPolicy to save.
     * @param securityPolicy the securityPolicy to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated securityPolicy,
     * or with status {@code 400 (Bad Request)} if the securityPolicy is not valid,
     * or with status {@code 500 (Internal Server Error)} if the securityPolicy couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/security-policies/{id}")
    public ResponseEntity<SecurityPolicy> updateSecurityPolicy(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SecurityPolicy securityPolicy
    ) throws URISyntaxException {
        log.debug("REST request to update SecurityPolicy : {}, {}", id, securityPolicy);
        if (securityPolicy.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, securityPolicy.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!securityPolicyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SecurityPolicy result = securityPolicyRepository.save(securityPolicy);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, securityPolicy.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /security-policies/:id} : Partial updates given fields of an existing securityPolicy, field will ignore if it is null
     *
     * @param id the id of the securityPolicy to save.
     * @param securityPolicy the securityPolicy to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated securityPolicy,
     * or with status {@code 400 (Bad Request)} if the securityPolicy is not valid,
     * or with status {@code 404 (Not Found)} if the securityPolicy is not found,
     * or with status {@code 500 (Internal Server Error)} if the securityPolicy couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/security-policies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SecurityPolicy> partialUpdateSecurityPolicy(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SecurityPolicy securityPolicy
    ) throws URISyntaxException {
        log.debug("REST request to partial update SecurityPolicy partially : {}, {}", id, securityPolicy);
        if (securityPolicy.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, securityPolicy.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!securityPolicyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SecurityPolicy> result = securityPolicyRepository
            .findById(securityPolicy.getId())
            .map(existingSecurityPolicy -> {
                if (securityPolicy.getProtectionUnit() != null) {
                    existingSecurityPolicy.setProtectionUnit(securityPolicy.getProtectionUnit());
                }
                if (securityPolicy.getAccess() != null) {
                    existingSecurityPolicy.setAccess(securityPolicy.getAccess());
                }

                return existingSecurityPolicy;
            })
            .map(securityPolicyRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, securityPolicy.getId().toString())
        );
    }

    /**
     * {@code GET  /security-policies} : get all the securityPolicies.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of securityPolicies in body.
     */
    @GetMapping("/security-policies")
    public List<SecurityPolicy> getAllSecurityPolicies() {
        log.debug("REST request to get all SecurityPolicies");
        return securityPolicyRepository.findAll();
    }

    /**
     * {@code GET  /security-policies/:id} : get the "id" securityPolicy.
     *
     * @param id the id of the securityPolicy to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the securityPolicy, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/security-policies/{id}")
    public ResponseEntity<SecurityPolicy> getSecurityPolicy(@PathVariable Long id) {
        log.debug("REST request to get SecurityPolicy : {}", id);
        Optional<SecurityPolicy> securityPolicy = securityPolicyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(securityPolicy);
    }

    /**
     * {@code DELETE  /security-policies/:id} : delete the "id" securityPolicy.
     *
     * @param id the id of the securityPolicy to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/security-policies/{id}")
    public ResponseEntity<Void> deleteSecurityPolicy(@PathVariable Long id) {
        log.debug("REST request to delete SecurityPolicy : {}", id);
        securityPolicyRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
