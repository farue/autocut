package de.farue.autocut.repository;

import de.farue.autocut.domain.LaundryMachine;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LaundryMachine entity.
 */
@Repository
public interface LaundryMachineRepository extends JpaRepository<LaundryMachine, Long> {
    Optional<LaundryMachine> findByIdentifier(String identifier);
}
