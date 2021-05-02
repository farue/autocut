package de.farue.autocut.service;

import de.farue.autocut.domain.Address;
import de.farue.autocut.repository.AddressRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Address}.
 */
@Service
@Transactional
public class AddressService {

    private final Logger log = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * Save a address.
     *
     * @param address the entity to save.
     * @return the persisted entity.
     */
    public Address save(Address address) {
        log.debug("Request to save Address : {}", address);
        return addressRepository.save(address);
    }

    /**
     * Partially update a address.
     *
     * @param address the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Address> partialUpdate(Address address) {
        log.debug("Request to partially update Address : {}", address);

        return addressRepository
            .findById(address.getId())
            .map(
                existingAddress -> {
                    if (address.getStreet() != null) {
                        existingAddress.setStreet(address.getStreet());
                    }
                    if (address.getStreetNumber() != null) {
                        existingAddress.setStreetNumber(address.getStreetNumber());
                    }
                    if (address.getZip() != null) {
                        existingAddress.setZip(address.getZip());
                    }
                    if (address.getCity() != null) {
                        existingAddress.setCity(address.getCity());
                    }
                    if (address.getCountry() != null) {
                        existingAddress.setCountry(address.getCountry());
                    }

                    return existingAddress;
                }
            )
            .map(addressRepository::save);
    }

    /**
     * Get all the addresses.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Address> findAll() {
        log.debug("Request to get all Addresses");
        return addressRepository.findAll();
    }

    /**
     * Get one address by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Address> findOne(Long id) {
        log.debug("Request to get Address : {}", id);
        return addressRepository.findById(id);
    }

    /**
     * Delete the address by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Address : {}", id);
        addressRepository.deleteById(id);
    }

    public Address findByStudierendenwerkStreetNrIdentifier(int streetNrIdentifier) {
        return switch (streetNrIdentifier) {
            case 3 -> addressRepository
                .findOneByStreetAndStreetNumberAndZipAndCityAndCountry("Rütscher Str.", "123", "52072", "Aachen", "Germany")
                .orElseThrow(() -> new RuntimeException("The address was not found in the database."));
            case 5 -> addressRepository
                .findOneByStreetAndStreetNumberAndZipAndCityAndCountry("Rütscher Str.", "125", "52072", "Aachen", "Germany")
                .orElseThrow(() -> new RuntimeException("The address was not found in the database."));
            default -> throw new IllegalArgumentException("Street no identifier needs to be 3 or 5, but was " + streetNrIdentifier);
        };
    }
}
