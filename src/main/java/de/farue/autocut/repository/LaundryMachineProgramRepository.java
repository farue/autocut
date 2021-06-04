package de.farue.autocut.repository;

import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.domain.LaundryMachineProgram;
import de.farue.autocut.domain.LaundryProgram;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LaundryMachineProgram entity.
 */
@Repository
public interface LaundryMachineProgramRepository extends JpaRepository<LaundryMachineProgram, Long> {
    List<LaundryMachineProgram> findAllByMachine(LaundryMachine machine);

    Optional<LaundryMachineProgram> findFirstByMachineAndProgram(LaundryMachine machine, LaundryProgram program);
}
