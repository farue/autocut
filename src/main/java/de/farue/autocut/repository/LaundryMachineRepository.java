package de.farue.autocut.repository;

import de.farue.autocut.domain.LaundryMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the LaundryMachine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LaundryMachineRepository extends JpaRepository<LaundryMachine, Long> {

    Optional<LaundryMachine> findByIdentifier(String identifier);
}
