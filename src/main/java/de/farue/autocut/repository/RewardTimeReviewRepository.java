package de.farue.autocut.repository;

import de.farue.autocut.domain.RewardTimeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the RewardTimeReview entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RewardTimeReviewRepository extends JpaRepository<RewardTimeReview, Long> {}
