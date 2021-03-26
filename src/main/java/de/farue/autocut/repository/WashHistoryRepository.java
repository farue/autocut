package de.farue.autocut.repository;

import de.farue.autocut.domain.WashHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the WashHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WashHistoryRepository extends JpaRepository<WashHistory, Long> {}
