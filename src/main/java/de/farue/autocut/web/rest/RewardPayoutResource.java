package de.farue.autocut.web.rest;

import de.farue.autocut.domain.RewardPayout;
import de.farue.autocut.repository.RewardPayoutRepository;
import de.farue.autocut.service.RewardPayoutService;
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
 * REST controller for managing {@link de.farue.autocut.domain.RewardPayout}.
 */
@RestController
@RequestMapping("/api")
public class RewardPayoutResource {

    private final Logger log = LoggerFactory.getLogger(RewardPayoutResource.class);

    private static final String ENTITY_NAME = "rewardPayout";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RewardPayoutService rewardPayoutService;

    private final RewardPayoutRepository rewardPayoutRepository;

    public RewardPayoutResource(RewardPayoutService rewardPayoutService, RewardPayoutRepository rewardPayoutRepository) {
        this.rewardPayoutService = rewardPayoutService;
        this.rewardPayoutRepository = rewardPayoutRepository;
    }

    /**
     * {@code POST  /reward-payouts} : Create a new rewardPayout.
     *
     * @param rewardPayout the rewardPayout to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rewardPayout, or with status {@code 400 (Bad Request)} if the rewardPayout has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reward-payouts")
    public ResponseEntity<RewardPayout> createRewardPayout(@Valid @RequestBody RewardPayout rewardPayout) throws URISyntaxException {
        log.debug("REST request to save RewardPayout : {}", rewardPayout);
        if (rewardPayout.getId() != null) {
            throw new BadRequestAlertException("A new rewardPayout cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RewardPayout result = rewardPayoutService.save(rewardPayout);
        return ResponseEntity
            .created(new URI("/api/reward-payouts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /reward-payouts/:id} : Updates an existing rewardPayout.
     *
     * @param id the id of the rewardPayout to save.
     * @param rewardPayout the rewardPayout to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rewardPayout,
     * or with status {@code 400 (Bad Request)} if the rewardPayout is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rewardPayout couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reward-payouts/{id}")
    public ResponseEntity<RewardPayout> updateRewardPayout(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RewardPayout rewardPayout
    ) throws URISyntaxException {
        log.debug("REST request to update RewardPayout : {}, {}", id, rewardPayout);
        if (rewardPayout.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rewardPayout.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rewardPayoutRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RewardPayout result = rewardPayoutService.save(rewardPayout);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rewardPayout.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /reward-payouts/:id} : Partial updates given fields of an existing rewardPayout, field will ignore if it is null
     *
     * @param id the id of the rewardPayout to save.
     * @param rewardPayout the rewardPayout to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rewardPayout,
     * or with status {@code 400 (Bad Request)} if the rewardPayout is not valid,
     * or with status {@code 404 (Not Found)} if the rewardPayout is not found,
     * or with status {@code 500 (Internal Server Error)} if the rewardPayout couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/reward-payouts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RewardPayout> partialUpdateRewardPayout(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RewardPayout rewardPayout
    ) throws URISyntaxException {
        log.debug("REST request to partial update RewardPayout partially : {}, {}", id, rewardPayout);
        if (rewardPayout.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rewardPayout.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rewardPayoutRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RewardPayout> result = rewardPayoutService.partialUpdate(rewardPayout);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rewardPayout.getId().toString())
        );
    }

    /**
     * {@code GET  /reward-payouts} : get all the rewardPayouts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rewardPayouts in body.
     */
    @GetMapping("/reward-payouts")
    public List<RewardPayout> getAllRewardPayouts() {
        log.debug("REST request to get all RewardPayouts");
        return rewardPayoutService.findAll();
    }

    /**
     * {@code GET  /reward-payouts/:id} : get the "id" rewardPayout.
     *
     * @param id the id of the rewardPayout to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rewardPayout, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reward-payouts/{id}")
    public ResponseEntity<RewardPayout> getRewardPayout(@PathVariable Long id) {
        log.debug("REST request to get RewardPayout : {}", id);
        Optional<RewardPayout> rewardPayout = rewardPayoutService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rewardPayout);
    }

    /**
     * {@code DELETE  /reward-payouts/:id} : delete the "id" rewardPayout.
     *
     * @param id the id of the rewardPayout to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reward-payouts/{id}")
    public ResponseEntity<Void> deleteRewardPayout(@PathVariable Long id) {
        log.debug("REST request to delete RewardPayout : {}", id);
        rewardPayoutService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
