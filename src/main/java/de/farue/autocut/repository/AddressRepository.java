package de.farue.autocut.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.farue.autocut.domain.Address;

/**
 * Spring Data SQL repository for the Address entity.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findOneByStreetAndStreetNumberAndZipAndCityAndCountry(String street, String streetNumber, String zip, String city, String country);
}
