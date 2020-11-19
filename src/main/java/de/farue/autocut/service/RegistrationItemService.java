package de.farue.autocut.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.domain.RegistrationItem;
import de.farue.autocut.repository.RegistrationItemRepository;

/**
 * Service Implementation for managing {@link RegistrationItem}.
 */
@Service
@Transactional
public class RegistrationItemService {

    private final Logger log = LoggerFactory.getLogger(RegistrationItemService.class);

    private final RegistrationItemRepository registrationItemRepository;

    public RegistrationItemService(RegistrationItemRepository registrationItemRepository) {
        this.registrationItemRepository = registrationItemRepository;
    }

    /**
     * Save a registrationItem.
     *
     * @param registrationItem the entity to save.
     * @return the persisted entity.
     */
    public RegistrationItem save(RegistrationItem registrationItem) {
        log.debug("Request to save RegistrationItem : {}", registrationItem);
        return registrationItemRepository.save(registrationItem);
    }

    /**
     * Get all the registrationItems.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RegistrationItem> findAll() {
        log.debug("Request to get all RegistrationItems");
        return registrationItemRepository.findAll();
    }


    /**
     * Get one registrationItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RegistrationItem> findOne(Long id) {
        log.debug("Request to get RegistrationItem : {}", id);
        return registrationItemRepository.findById(id);
    }

    /**
     * Delete the registrationItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RegistrationItem : {}", id);
        registrationItemRepository.deleteById(id);
    }
}
