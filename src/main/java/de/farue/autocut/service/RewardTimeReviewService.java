package de.farue.autocut.service;

import de.farue.autocut.domain.RewardTimeReview;
import de.farue.autocut.repository.RewardTimeReviewRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RewardTimeReview}.
 */
@Service
@Transactional
public class RewardTimeReviewService {

    private final Logger log = LoggerFactory.getLogger(RewardTimeReviewService.class);

    private final RewardTimeReviewRepository rewardTimeReviewRepository;

    public RewardTimeReviewService(RewardTimeReviewRepository rewardTimeReviewRepository) {
        this.rewardTimeReviewRepository = rewardTimeReviewRepository;
    }

    /**
     * Save a rewardTimeReview.
     *
     * @param rewardTimeReview the entity to save.
     * @return the persisted entity.
     */
    public RewardTimeReview save(RewardTimeReview rewardTimeReview) {
        log.debug("Request to save RewardTimeReview : {}", rewardTimeReview);
        return rewardTimeReviewRepository.save(rewardTimeReview);
    }

    /**
     * Partially update a rewardTimeReview.
     *
     * @param rewardTimeReview the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RewardTimeReview> partialUpdate(RewardTimeReview rewardTimeReview) {
        log.debug("Request to partially update RewardTimeReview : {}", rewardTimeReview);

        return rewardTimeReviewRepository
            .findById(rewardTimeReview.getId())
            .map(existingRewardTimeReview -> {
                if (rewardTimeReview.getStatus() != null) {
                    existingRewardTimeReview.setStatus(rewardTimeReview.getStatus());
                }
                if (rewardTimeReview.getComment() != null) {
                    existingRewardTimeReview.setComment(rewardTimeReview.getComment());
                }

                return existingRewardTimeReview;
            })
            .map(rewardTimeReviewRepository::save);
    }

    /**
     * Get all the rewardTimeReviews.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RewardTimeReview> findAll() {
        log.debug("Request to get all RewardTimeReviews");
        return rewardTimeReviewRepository.findAll();
    }

    /**
     * Get one rewardTimeReview by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RewardTimeReview> findOne(Long id) {
        log.debug("Request to get RewardTimeReview : {}", id);
        return rewardTimeReviewRepository.findById(id);
    }

    /**
     * Delete the rewardTimeReview by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RewardTimeReview : {}", id);
        rewardTimeReviewRepository.deleteById(id);
    }
}
