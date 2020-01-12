package de.farue.autocut.repository;

import de.farue.autocut.domain.PaymentEntry;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the PaymentEntry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentEntryRepository extends JpaRepository<PaymentEntry, Long> {

}
