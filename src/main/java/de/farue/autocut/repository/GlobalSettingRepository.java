package de.farue.autocut.repository;

import de.farue.autocut.domain.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the GlobalSetting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GlobalSettingRepository extends JpaRepository<GlobalSetting, Long> {

    Optional<GlobalSetting> getByKey(String key);
}
