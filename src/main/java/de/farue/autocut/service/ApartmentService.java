package de.farue.autocut.service;

import de.farue.autocut.domain.Apartment;
import de.farue.autocut.repository.ApartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Apartment}.
 */
@Service
@Transactional
public class ApartmentService {

    private final Logger log = LoggerFactory.getLogger(ApartmentService.class);

    private final ApartmentRepository apartmentRepository;

    public ApartmentService(ApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
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
}
