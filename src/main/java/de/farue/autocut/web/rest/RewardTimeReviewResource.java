package de.farue.autocut.web.rest;

import de.farue.autocut.domain.RewardTimeReview;
import de.farue.autocut.repository.RewardTimeReviewRepository;
import de.farue.autocut.service.RewardTimeReviewService;
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
 * REST controller for managing {@link de.farue.autocut.domain.RewardTimeReview}.
 */
@RestController
@RequestMapping("/api")
public class RewardTimeReviewResource {

    private final Logger log = LoggerFactory.getLogger(RewardTimeReviewResource.class);

    private static final String ENTITY_NAME = "rewardTimeReview";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RewardTimeReviewService rewardTimeReviewService;

    private final RewardTimeReviewRepository rewardTimeReviewRepository;

    public RewardTimeReviewResource(
        RewardTimeReviewService rewardTimeReviewService,
        RewardTimeReviewRepository rewardTimeReviewRepository
    ) {
        this.rewardTimeReviewService = rewardTimeReviewService;
        this.rewardTimeReviewRepository = rewardTimeReviewRepository;
    }

    /**
     * {@code POST  /reward-time-reviews} : Create a new rewardTimeReview.
     *
     * @param rewardTimeReview the rewardTimeReview to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rewardTimeReview, or with status {@code 400 (Bad Request)} if the rewardTimeReview has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reward-time-reviews")
    public ResponseEntity<RewardTimeReview> createRewardTimeReview(@Valid @RequestBody RewardTimeReview rewardTimeReview)
        throws URISyntaxException {
        log.debug("REST request to save RewardTimeReview : {}", rewardTimeReview);
        if (rewardTimeReview.getId() != null) {
            throw new BadRequestAlertException("A new rewardTimeReview cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RewardTimeReview result = rewardTimeReviewService.save(rewardTimeReview);
        return ResponseEntity
            .created(new URI("/api/reward-time-reviews/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /reward-time-reviews/:id} : Updates an existing rewardTimeReview.
     *
     * @param id the id of the rewardTimeReview to save.
     * @param rewardTimeReview the rewardTimeReview to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rewardTimeReview,
     * or with status {@code 400 (Bad Request)} if the rewardTimeReview is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rewardTimeReview couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reward-time-reviews/{id}")
    public ResponseEntity<RewardTimeReview> updateRewardTimeReview(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RewardTimeReview rewardTimeReview
    ) throws URISyntaxException {
        log.debug("REST request to update RewardTimeReview : {}, {}", id, rewardTimeReview);
        if (rewardTimeReview.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rewardTimeReview.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rewardTimeReviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RewardTimeReview result = rewardTimeReviewService.save(rewardTimeReview);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rewardTimeReview.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /reward-time-reviews/:id} : Partial updates given fields of an existing rewardTimeReview, field will ignore if it is null
     *
     * @param id the id of the rewardTimeReview to save.
     * @param rewardTimeReview the rewardTimeReview to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rewardTimeReview,
     * or with status {@code 400 (Bad Request)} if the rewardTimeReview is not valid,
     * or with status {@code 404 (Not Found)} if the rewardTimeReview is not found,
     * or with status {@code 500 (Internal Server Error)} if the rewardTimeReview couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/reward-time-reviews/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RewardTimeReview> partialUpdateRewardTimeReview(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RewardTimeReview rewardTimeReview
    ) throws URISyntaxException {
        log.debug("REST request to partial update RewardTimeReview partially : {}, {}", id, rewardTimeReview);
        if (rewardTimeReview.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rewardTimeReview.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rewardTimeReviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RewardTimeReview> result = rewardTimeReviewService.partialUpdate(rewardTimeReview);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rewardTimeReview.getId().toString())
        );
    }

    /**
     * {@code GET  /reward-time-reviews} : get all the rewardTimeReviews.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rewardTimeReviews in body.
     */
    @GetMapping("/reward-time-reviews")
    public List<RewardTimeReview> getAllRewardTimeReviews() {
        log.debug("REST request to get all RewardTimeReviews");
        return rewardTimeReviewService.findAll();
    }

    /**
     * {@code GET  /reward-time-reviews/:id} : get the "id" rewardTimeReview.
     *
     * @param id the id of the rewardTimeReview to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rewardTimeReview, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reward-time-reviews/{id}")
    public ResponseEntity<RewardTimeReview> getRewardTimeReview(@PathVariable Long id) {
        log.debug("REST request to get RewardTimeReview : {}", id);
        Optional<RewardTimeReview> rewardTimeReview = rewardTimeReviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rewardTimeReview);
    }

    /**
     * {@code DELETE  /reward-time-reviews/:id} : delete the "id" rewardTimeReview.
     *
     * @param id the id of the rewardTimeReview to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reward-time-reviews/{id}")
    public ResponseEntity<Void> deleteRewardTimeReview(@PathVariable Long id) {
        log.debug("REST request to delete RewardTimeReview : {}", id);
        rewardTimeReviewService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
