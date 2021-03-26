package de.farue.autocut.repository;

import de.farue.autocut.domain.LaundryMachineProgram;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LaundryMachineProgram entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LaundryMachineProgramRepository extends JpaRepository<LaundryMachineProgram, Long> {}
