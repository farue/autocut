package de.farue.autocut.repository;

import de.farue.autocut.domain.SecurityPolicy;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the SecurityPolicy entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SecurityPolicyRepository extends JpaRepository<SecurityPolicy, Long> {}
