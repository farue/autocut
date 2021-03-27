package de.farue.autocut.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.farue.autocut.domain.LaundryMachineProgram;

/**
 * Spring Data SQL repository for the LaundryMachineProgram entity.
 */
@Repository
public interface LaundryMachineProgramRepository extends JpaRepository<LaundryMachineProgram, Long> {

}
