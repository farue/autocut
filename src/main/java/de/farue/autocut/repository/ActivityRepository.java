package de.farue.autocut.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.farue.autocut.domain.Activity;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.enumeration.SemesterTerms;

/**
 * Spring Data  repository for the Activity entity.
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("select a from Activity a where a.year = :year and a.term = :term and a.tenant in :tenants")
    Page<Activity> findAllByTerm(int year, SemesterTerms term, Set<Tenant> tenants, Pageable pageable);
}
