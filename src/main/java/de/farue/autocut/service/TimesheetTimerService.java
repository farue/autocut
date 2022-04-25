package de.farue.autocut.service;

import de.farue.autocut.domain.TimesheetTimer;
import de.farue.autocut.repository.TimesheetTimerRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TimesheetTimer}.
 */
@Service
@Transactional
public class TimesheetTimerService {

    private final Logger log = LoggerFactory.getLogger(TimesheetTimerService.class);

    private final TimesheetTimerRepository timesheetTimerRepository;

    public TimesheetTimerService(TimesheetTimerRepository timesheetTimerRepository) {
        this.timesheetTimerRepository = timesheetTimerRepository;
    }

    /**
     * Save a timesheetTimer.
     *
     * @param timesheetTimer the entity to save.
     * @return the persisted entity.
     */
    public TimesheetTimer save(TimesheetTimer timesheetTimer) {
        log.debug("Request to save TimesheetTimer : {}", timesheetTimer);
        return timesheetTimerRepository.save(timesheetTimer);
    }

    /**
     * Partially update a timesheetTimer.
     *
     * @param timesheetTimer the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TimesheetTimer> partialUpdate(TimesheetTimer timesheetTimer) {
        log.debug("Request to partially update TimesheetTimer : {}", timesheetTimer);

        return timesheetTimerRepository
            .findById(timesheetTimer.getId())
            .map(existingTimesheetTimer -> {
                if (timesheetTimer.getStart() != null) {
                    existingTimesheetTimer.setStart(timesheetTimer.getStart());
                }
                if (timesheetTimer.getPauseStart() != null) {
                    existingTimesheetTimer.setPauseStart(timesheetTimer.getPauseStart());
                }
                if (timesheetTimer.getPause() != null) {
                    existingTimesheetTimer.setPause(timesheetTimer.getPause());
                }

                return existingTimesheetTimer;
            })
            .map(timesheetTimerRepository::save);
    }

    /**
     * Get all the timesheetTimers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TimesheetTimer> findAll() {
        log.debug("Request to get all TimesheetTimers");
        return timesheetTimerRepository.findAll();
    }

    /**
     * Get one timesheetTimer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TimesheetTimer> findOne(Long id) {
        log.debug("Request to get TimesheetTimer : {}", id);
        return timesheetTimerRepository.findById(id);
    }

    /**
     * Delete the timesheetTimer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TimesheetTimer : {}", id);
        timesheetTimerRepository.deleteById(id);
    }
}
