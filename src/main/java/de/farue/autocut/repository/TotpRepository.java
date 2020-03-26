package de.farue.autocut.repository;

import de.farue.autocut.domain.Totp;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Totp entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TotpRepository extends JpaRepository<Totp, Long> {

}
