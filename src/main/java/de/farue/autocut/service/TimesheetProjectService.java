package de.farue.autocut.service;

import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.domain.TimesheetProject;
import de.farue.autocut.repository.TimesheetProjectRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TimesheetProject}.
 */
@Service
@Transactional
public class TimesheetProjectService {

    private final Logger log = LoggerFactory.getLogger(TimesheetProjectService.class);

    private final TimesheetProjectRepository timesheetProjectRepository;

    public TimesheetProjectService(TimesheetProjectRepository timesheetProjectRepository) {
        this.timesheetProjectRepository = timesheetProjectRepository;
    }

    /**
     * Save a timesheetProject.
     *
     * @param timesheetProject the entity to save.
     * @return the persisted entity.
     */
    public TimesheetProject save(TimesheetProject timesheetProject) {
        log.debug("Request to save TimesheetProject : {}", timesheetProject);
        return timesheetProjectRepository.save(timesheetProject);
    }

    /**
     * Partially update a timesheetProject.
     *
     * @param timesheetProject the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TimesheetProject> partialUpdate(TimesheetProject timesheetProject) {
        log.debug("Request to partially update TimesheetProject : {}", timesheetProject);

        return timesheetProjectRepository
            .findById(timesheetProject.getId())
            .map(existingTimesheetProject -> {
                if (timesheetProject.getName() != null) {
                    existingTimesheetProject.setName(timesheetProject.getName());
                }
                if (timesheetProject.getStart() != null) {
                    existingTimesheetProject.setStart(timesheetProject.getStart());
                }
                if (timesheetProject.getEnd() != null) {
                    existingTimesheetProject.setEnd(timesheetProject.getEnd());
                }

                return existingTimesheetProject;
            })
            .map(timesheetProjectRepository::save);
    }

    /**
     * Get all the timesheetProjects.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TimesheetProject> findAll() {
        log.debug("Request to get all TimesheetProjects");
        return timesheetProjectRepository.findAllWithEagerRelationships();
    }

    /**
     * Get all the timesheetProjects with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TimesheetProject> findAllWithEagerRelationships(Pageable pageable) {
        return timesheetProjectRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one timesheetProject by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TimesheetProject> findOne(Long id) {
        log.debug("Request to get TimesheetProject : {}", id);
        return timesheetProjectRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the timesheetProject by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TimesheetProject : {}", id);
        timesheetProjectRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TimesheetProject> findAllByTimesheet(Timesheet timesheet) {
        return timesheetProjectRepository.findAllByTimesheet(timesheet);
    }

    @Transactional(readOnly = true)
    public List<TimesheetProject> findAllByTimesheetOrderByLastUsed(Timesheet timesheet) {
        return timesheetProjectRepository.findAllByTimesheetOrderByLastUsed(timesheet).stream().distinct().toList();
    }
}
