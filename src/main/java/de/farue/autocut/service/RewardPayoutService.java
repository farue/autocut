package de.farue.autocut.service;

import de.farue.autocut.domain.RewardPayout;
import de.farue.autocut.repository.RewardPayoutRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RewardPayout}.
 */
@Service
@Transactional
public class RewardPayoutService {

    private final Logger log = LoggerFactory.getLogger(RewardPayoutService.class);

    private final RewardPayoutRepository rewardPayoutRepository;

    public RewardPayoutService(RewardPayoutRepository rewardPayoutRepository) {
        this.rewardPayoutRepository = rewardPayoutRepository;
    }

    /**
     * Save a rewardPayout.
     *
     * @param rewardPayout the entity to save.
     * @return the persisted entity.
     */
    public RewardPayout save(RewardPayout rewardPayout) {
        log.debug("Request to save RewardPayout : {}", rewardPayout);
        return rewardPayoutRepository.save(rewardPayout);
    }

    /**
     * Partially update a rewardPayout.
     *
     * @param rewardPayout the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RewardPayout> partialUpdate(RewardPayout rewardPayout) {
        log.debug("Request to partially update RewardPayout : {}", rewardPayout);

        return rewardPayoutRepository
            .findById(rewardPayout.getId())
            .map(existingRewardPayout -> {
                if (rewardPayout.getTimestamp() != null) {
                    existingRewardPayout.setTimestamp(rewardPayout.getTimestamp());
                }
                if (rewardPayout.getAmount() != null) {
                    existingRewardPayout.setAmount(rewardPayout.getAmount());
                }
                if (rewardPayout.getTime() != null) {
                    existingRewardPayout.setTime(rewardPayout.getTime());
                }

                return existingRewardPayout;
            })
            .map(rewardPayoutRepository::save);
    }

    /**
     * Get all the rewardPayouts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RewardPayout> findAll() {
        log.debug("Request to get all RewardPayouts");
        return rewardPayoutRepository.findAll();
    }

    /**
     * Get one rewardPayout by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RewardPayout> findOne(Long id) {
        log.debug("Request to get RewardPayout : {}", id);
        return rewardPayoutRepository.findById(id);
    }

    /**
     * Delete the rewardPayout by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RewardPayout : {}", id);
        rewardPayoutRepository.deleteById(id);
    }
}
