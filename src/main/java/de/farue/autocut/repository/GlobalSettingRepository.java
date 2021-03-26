package de.farue.autocut.repository;

import de.farue.autocut.domain.GlobalSetting;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the GlobalSetting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GlobalSettingRepository extends JpaRepository<GlobalSetting, Long> {}
