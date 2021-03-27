package de.farue.autocut.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.farue.autocut.domain.LaundryMachine;


/**
 * Spring Data SQL repository for the LaundryMachine entity.
 */
@Repository
public interface LaundryMachineRepository extends JpaRepository<LaundryMachine, Long> {

    Optional<LaundryMachine> findByIdentifier(String identifier);

    @Query("select distinct m from LaundryMachine m left join fetch m.programs where m.enabled =:enabled")
    List<LaundryMachine> findAllWithEagerRelationshipsAndStatus(boolean enabled);
}
