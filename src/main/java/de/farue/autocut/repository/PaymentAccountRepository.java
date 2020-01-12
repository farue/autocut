package de.farue.autocut.repository;

import de.farue.autocut.domain.PaymentAccount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the PaymentAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentAccountRepository extends JpaRepository<PaymentAccount, Long> {

}
