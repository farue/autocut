package de.farue.autocut.repository;

import de.farue.autocut.domain.Coin;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Coin entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {

    Page<Coin> findAllByTenantOrderByIdDesc(Lease lease, Pageable pageable);

}
