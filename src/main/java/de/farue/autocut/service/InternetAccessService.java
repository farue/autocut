package de.farue.autocut.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.domain.InternetAccess;
import de.farue.autocut.repository.InternetAccessRepository;

/**
 * Service Implementation for managing {@link InternetAccess}.
 */
@Service
@Transactional
public class InternetAccessService {

    private final Logger log = LoggerFactory.getLogger(InternetAccessService.class);

    private final InternetAccessRepository internetAccessRepository;

    public InternetAccessService(InternetAccessRepository internetAccessRepository) {
        this.internetAccessRepository = internetAccessRepository;
    }

    /**
     * Save a internetAccess.
     *
     * @param internetAccess the entity to save.
     * @return the persisted entity.
     */
    public InternetAccess save(InternetAccess internetAccess) {
        log.debug("Request to save InternetAccess : {}", internetAccess);
        return internetAccessRepository.save(internetAccess);
    }

    /**
     * Partially update a internetAccess.
     *
     * @param internetAccess the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InternetAccess> partialUpdate(InternetAccess internetAccess) {
        log.debug("Request to partially update InternetAccess : {}", internetAccess);

        return internetAccessRepository
            .findById(internetAccess.getId())
            .map(
                existingInternetAccess -> {
                    if (internetAccess.getBlocked() != null) {
                        existingInternetAccess.setBlocked(internetAccess.getBlocked());
                    }
                    if (internetAccess.getIp1() != null) {
                        existingInternetAccess.setIp1(internetAccess.getIp1());
                    }
                    if (internetAccess.getIp2() != null) {
                        existingInternetAccess.setIp2(internetAccess.getIp2());
                    }
                    if (internetAccess.getSwitchInterface() != null) {
                        existingInternetAccess.setSwitchInterface(internetAccess.getSwitchInterface());
                    }
                    if (internetAccess.getPort() != null) {
                        existingInternetAccess.setPort(internetAccess.getPort());
                    }

                    return existingInternetAccess;
                }
            )
            .map(internetAccessRepository::save);
    }

    /**
     * Get all the internetAccesses.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<InternetAccess> findAll() {
        log.debug("Request to get all InternetAccesses");
        return internetAccessRepository.findAll();
    }

    /**
     *  Get all the internetAccesses where Apartment is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<InternetAccess> findAllWhereApartmentIsNull() {
        log.debug("Request to get all internetAccesses where Apartment is null");
        return StreamSupport
            .stream(internetAccessRepository.findAll().spliterator(), false)
            .filter(internetAccess -> internetAccess.getApartment() == null)
            .collect(Collectors.toList());
    }

    /**
     * Get one internetAccess by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InternetAccess> findOne(Long id) {
        log.debug("Request to get InternetAccess : {}", id);
        return internetAccessRepository.findById(id);
    }

    /**
     * Delete the internetAccess by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete InternetAccess : {}", id);
        internetAccessRepository.deleteById(id);
    }
}
