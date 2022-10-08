package de.farue.autocut.service;

import de.farue.autocut.domain.TimesheetProject;
import de.farue.autocut.domain.TimesheetTask;
import de.farue.autocut.repository.TimesheetTaskRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TimesheetTask}.
 */
@Service
@Transactional
public class TimesheetTaskService {

    private final Logger log = LoggerFactory.getLogger(TimesheetTaskService.class);

    private final TimesheetTaskRepository timesheetTaskRepository;

    public TimesheetTaskService(TimesheetTaskRepository timesheetTaskRepository) {
        this.timesheetTaskRepository = timesheetTaskRepository;
    }

    /**
     * Save a timesheetTask.
     *
     * @param timesheetTask the entity to save.
     * @return the persisted entity.
     */
    public TimesheetTask save(TimesheetTask timesheetTask) {
        log.debug("Request to save TimesheetTask : {}", timesheetTask);
        return timesheetTaskRepository.save(timesheetTask);
    }

    /**
     * Partially update a timesheetTask.
     *
     * @param timesheetTask the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TimesheetTask> partialUpdate(TimesheetTask timesheetTask) {
        log.debug("Request to partially update TimesheetTask : {}", timesheetTask);

        return timesheetTaskRepository
            .findById(timesheetTask.getId())
            .map(existingTimesheetTask -> {
                if (timesheetTask.getName() != null) {
                    existingTimesheetTask.setName(timesheetTask.getName());
                }
                if (timesheetTask.getEnabled() != null) {
                    existingTimesheetTask.setEnabled(timesheetTask.getEnabled());
                }
                if (timesheetTask.getConstant() != null) {
                    existingTimesheetTask.setConstant(timesheetTask.getConstant());
                }
                if (timesheetTask.getConstantEditable() != null) {
                    existingTimesheetTask.setConstantEditable(timesheetTask.getConstantEditable());
                }
                if (timesheetTask.getFactor() != null) {
                    existingTimesheetTask.setFactor(timesheetTask.getFactor());
                }
                if (timesheetTask.getFactorEditable() != null) {
                    existingTimesheetTask.setFactorEditable(timesheetTask.getFactorEditable());
                }
                if (timesheetTask.getDefaultTimespan() != null) {
                    existingTimesheetTask.setDefaultTimespan(timesheetTask.getDefaultTimespan());
                }

                return existingTimesheetTask;
            })
            .map(timesheetTaskRepository::save);
    }

    /**
     * Get all the timesheetTasks.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TimesheetTask> findAll() {
        log.debug("Request to get all TimesheetTasks");
        return timesheetTaskRepository.findAll();
    }

    /**
     * Get one timesheetTask by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TimesheetTask> findOne(Long id) {
        log.debug("Request to get TimesheetTask : {}", id);
        return timesheetTaskRepository.findById(id);
    }

    /**
     * Delete the timesheetTask by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TimesheetTask : {}", id);
        timesheetTaskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TimesheetTask> findAllByTimesheetProject(TimesheetProject project) {
        return timesheetTaskRepository.findAllByTimesheetProject(project);
    }

    @Transactional(readOnly = true)
    public List<TimesheetTask> findAllByTimesheetProjectOrderByLastUsed(TimesheetProject project) {
        return timesheetTaskRepository.findAllByTimesheetProjectOrderByLastUsed(project).stream().distinct().toList();
    }
}
