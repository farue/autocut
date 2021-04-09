package de.farue.autocut.repository;

import de.farue.autocut.domain.LaundryMachineProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LaundryMachineProgram entity.
 */
@Repository
public interface LaundryMachineProgramRepository extends JpaRepository<LaundryMachineProgram, Long> {}
