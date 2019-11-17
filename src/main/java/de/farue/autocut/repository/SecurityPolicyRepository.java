package de.farue.autocut.repository;

import de.farue.autocut.domain.SecurityPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the SecurityPolicy entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SecurityPolicyRepository extends JpaRepository<SecurityPolicy, Long> {

}
