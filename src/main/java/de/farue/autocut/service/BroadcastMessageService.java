package de.farue.autocut.service;

import de.farue.autocut.domain.BroadcastMessage;
import de.farue.autocut.repository.BroadcastMessageRepository;
import de.farue.autocut.security.SecurityUtils;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BroadcastMessage}.
 */
@Service
@Transactional
public class BroadcastMessageService {

    private final Logger log = LoggerFactory.getLogger(BroadcastMessageService.class);

    private final BroadcastMessageRepository broadcastMessageRepository;

    public BroadcastMessageService(BroadcastMessageRepository broadcastMessageRepository) {
        this.broadcastMessageRepository = broadcastMessageRepository;
    }

    /**
     * Save a broadcastMessage.
     *
     * @param broadcastMessage the entity to save.
     * @return the persisted entity.
     */
    public BroadcastMessage save(BroadcastMessage broadcastMessage) {
        log.debug("Request to save BroadcastMessage : {}", broadcastMessage);
        return broadcastMessageRepository.save(broadcastMessage);
    }

    /**
     * Partially update a broadcastMessage.
     *
     * @param broadcastMessage the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BroadcastMessage> partialUpdate(BroadcastMessage broadcastMessage) {
        log.debug("Request to partially update BroadcastMessage : {}", broadcastMessage);

        return broadcastMessageRepository
            .findById(broadcastMessage.getId())
            .map(
                existingBroadcastMessage -> {
                    if (broadcastMessage.getType() != null) {
                        existingBroadcastMessage.setType(broadcastMessage.getType());
                    }
                    if (broadcastMessage.getStart() != null) {
                        existingBroadcastMessage.setStart(broadcastMessage.getStart());
                    }
                    if (broadcastMessage.getEnd() != null) {
                        existingBroadcastMessage.setEnd(broadcastMessage.getEnd());
                    }
                    if (broadcastMessage.getUsersOnly() != null) {
                        existingBroadcastMessage.setUsersOnly(broadcastMessage.getUsersOnly());
                    }
                    if (broadcastMessage.getDismissible() != null) {
                        existingBroadcastMessage.setDismissible(broadcastMessage.getDismissible());
                    }

                    return existingBroadcastMessage;
                }
            )
            .map(broadcastMessageRepository::save);
    }

    /**
     * Get all the broadcastMessages.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BroadcastMessage> findAll() {
        log.debug("Request to get all BroadcastMessages");
        return broadcastMessageRepository.findAll();
    }

    /**
     * Get one broadcastMessage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BroadcastMessage> findOne(Long id) {
        log.debug("Request to get BroadcastMessage : {}", id);
        return broadcastMessageRepository.findById(id);
    }

    /**
     * Delete the broadcastMessage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BroadcastMessage : {}", id);
        broadcastMessageRepository.deleteById(id);
    }

    public List<BroadcastMessage> findAllActive() {
        return broadcastMessageRepository.findAllActiveAt(Instant.now(), SecurityUtils.isAuthenticated());
    }
}
