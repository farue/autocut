package de.farue.autocut.repository;

import de.farue.autocut.domain.RewardPayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the RewardPayout entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RewardPayoutRepository extends JpaRepository<RewardPayout, Long> {}
