package de.farue.autocut.repository;

import de.farue.autocut.domain.LaundryMachine;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the LaundryMachine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LaundryMachineRepository extends JpaRepository<LaundryMachine, Long> {

}
