package de.farue.autocut.repository;

import de.farue.autocut.domain.LaundryProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LaundryProgram entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LaundryProgramRepository extends JpaRepository<LaundryProgram, Long> {}
