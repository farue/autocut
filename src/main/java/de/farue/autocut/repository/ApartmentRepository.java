package de.farue.autocut.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.domain.Address;
import de.farue.autocut.domain.Apartment;

/**
 * Spring Data  repository for the Apartment entity.
 */
@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    @Transactional(readOnly = true)
    Optional<Apartment> findOneByNrAndAddress(String nr, Address address);

}
