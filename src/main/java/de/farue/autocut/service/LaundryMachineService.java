package de.farue.autocut.service;

import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.repository.LaundryMachineRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link LaundryMachine}.
 */
@Service
@Transactional
public class LaundryMachineService {

    private final Logger log = LoggerFactory.getLogger(LaundryMachineService.class);

    private final LaundryMachineRepository laundryMachineRepository;

    public LaundryMachineService(LaundryMachineRepository laundryMachineRepository) {
        this.laundryMachineRepository = laundryMachineRepository;
    }

    /**
     * Save a laundryMachine.
     *
     * @param laundryMachine the entity to save.
     * @return the persisted entity.
     */
    public LaundryMachine save(LaundryMachine laundryMachine) {
        log.debug("Request to save LaundryMachine : {}", laundryMachine);
        return laundryMachineRepository.save(laundryMachine);
    }

    /**
     * Partially update a laundryMachine.
     *
     * @param laundryMachine the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LaundryMachine> partialUpdate(LaundryMachine laundryMachine) {
        log.debug("Request to partially update LaundryMachine : {}", laundryMachine);

        return laundryMachineRepository
            .findById(laundryMachine.getId())
            .map(existingLaundryMachine -> {
                if (laundryMachine.getIdentifier() != null) {
                    existingLaundryMachine.setIdentifier(laundryMachine.getIdentifier());
                }
                if (laundryMachine.getName() != null) {
                    existingLaundryMachine.setName(laundryMachine.getName());
                }
                if (laundryMachine.getType() != null) {
                    existingLaundryMachine.setType(laundryMachine.getType());
                }
                if (laundryMachine.getEnabled() != null) {
                    existingLaundryMachine.setEnabled(laundryMachine.getEnabled());
                }
                if (laundryMachine.getPositionX() != null) {
                    existingLaundryMachine.setPositionX(laundryMachine.getPositionX());
                }
                if (laundryMachine.getPositionY() != null) {
                    existingLaundryMachine.setPositionY(laundryMachine.getPositionY());
                }

                return existingLaundryMachine;
            })
            .map(laundryMachineRepository::save);
    }

    /**
     * Get all the laundryMachines.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<LaundryMachine> findAll() {
        log.debug("Request to get all LaundryMachines");
        return laundryMachineRepository.findAll();
    }

    /**
     * Get one laundryMachine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LaundryMachine> findOne(Long id) {
        log.debug("Request to get LaundryMachine : {}", id);
        return laundryMachineRepository.findById(id);
    }

    /**
     * Delete the laundryMachine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete LaundryMachine : {}", id);
        laundryMachineRepository.deleteById(id);
    }
}
