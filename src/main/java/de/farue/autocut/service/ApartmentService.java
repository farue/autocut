package de.farue.autocut.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.domain.Address;
import de.farue.autocut.domain.Apartment;
import de.farue.autocut.domain.enumeration.ApartmentTypes;
import de.farue.autocut.repository.ApartmentRepository;
import de.farue.autocut.service.StwApartmentParser.StwApartment;

/**
 * Service Implementation for managing {@link Apartment}.
 */
@Service
@Transactional
public class ApartmentService {

    private final Logger log = LoggerFactory.getLogger(ApartmentService.class);

    private final ApartmentRepository apartmentRepository;

    private final AddressService addressService;

    public ApartmentService(ApartmentRepository apartmentRepository, AddressService addressService) {
        this.apartmentRepository = apartmentRepository;
        this.addressService = addressService;
    }

    /**
     * Save a apartment.
     *
     * @param apartment the entity to save.
     * @return the persisted entity.
     */
    public Apartment save(Apartment apartment) {
        log.debug("Request to save Apartment : {}", apartment);
        return apartmentRepository.save(apartment);
    }

    /**
     * Get all the apartments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Apartment> findAll() {
        log.debug("Request to get all Apartments");
        return apartmentRepository.findAll();
    }


    /**
     * Get one apartment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Apartment> findOne(Long id) {
        log.debug("Request to get Apartment : {}", id);
        return apartmentRepository.findById(id);
    }

    /**
     * Delete the apartment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Apartment : {}", id);
        apartmentRepository.deleteById(id);
    }

    public Optional<Apartment> findByStudierendenwerkNumber(String apartmentString) {
        Objects.requireNonNull(apartmentString);

        StwApartment stwApartment = StwApartmentParser.parseApartmentString(apartmentString);
        Address address = addressService.findByStudierendenwerkStreetNrIdentifier(stwApartment.getStreetNoIdentifier()).orElseThrow();
        return apartmentRepository.findOneByNrAndAddress(String.valueOf(stwApartment.getApartmentNr()), address)
            .map(apartment -> {
                verifyApartmentType(apartment, stwApartment);
                return apartment;
            });
    }

    private void verifyApartmentType(Apartment apartment, StwApartment stwApartment) {
        boolean ok = (apartment.getType() == ApartmentTypes.SINGLE && stwApartment.getApartmentTypeIdentifier() == 0)
            || ((apartment.getType() == ApartmentTypes.SHARED || apartment.getType() == ApartmentTypes.SHORT_TERM) &&
            (stwApartment.getApartmentTypeIdentifier() == 1 || stwApartment.getApartmentTypeIdentifier() == 2));
        if (!ok) {
            throw new IllegalArgumentException(
                "Apartment type is not correct. Expected " + apartment.getType() + ", but was " + stwApartment.getApartmentTypeIdentifier());
        }
    }
}
