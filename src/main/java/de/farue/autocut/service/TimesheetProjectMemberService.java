package de.farue.autocut.service;

import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.domain.TimesheetProjectMember;
import de.farue.autocut.repository.TimesheetProjectMemberRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TimesheetProjectMember}.
 */
@Service
@Transactional
public class TimesheetProjectMemberService {

    private final Logger log = LoggerFactory.getLogger(TimesheetProjectMemberService.class);

    private final TimesheetProjectMemberRepository timesheetProjectMemberRepository;

    public TimesheetProjectMemberService(TimesheetProjectMemberRepository timesheetProjectMemberRepository) {
        this.timesheetProjectMemberRepository = timesheetProjectMemberRepository;
    }

    /**
     * Save a timesheetProjectMember.
     *
     * @param timesheetProjectMember the entity to save.
     * @return the persisted entity.
     */
    public TimesheetProjectMember save(TimesheetProjectMember timesheetProjectMember) {
        log.debug("Request to save TimesheetProjectMember : {}", timesheetProjectMember);
        return timesheetProjectMemberRepository.save(timesheetProjectMember);
    }

    /**
     * Partially update a timesheetProjectMember.
     *
     * @param timesheetProjectMember the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TimesheetProjectMember> partialUpdate(TimesheetProjectMember timesheetProjectMember) {
        log.debug("Request to partially update TimesheetProjectMember : {}", timesheetProjectMember);

        return timesheetProjectMemberRepository
            .findById(timesheetProjectMember.getId())
            .map(existingTimesheetProjectMember -> {
                if (timesheetProjectMember.getStart() != null) {
                    existingTimesheetProjectMember.setStart(timesheetProjectMember.getStart());
                }
                if (timesheetProjectMember.getEnd() != null) {
                    existingTimesheetProjectMember.setEnd(timesheetProjectMember.getEnd());
                }

                return existingTimesheetProjectMember;
            })
            .map(timesheetProjectMemberRepository::save);
    }

    /**
     * Get all the timesheetProjectMembers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TimesheetProjectMember> findAll() {
        log.debug("Request to get all TimesheetProjectMembers");
        return timesheetProjectMemberRepository.findAll();
    }

    /**
     * Get one timesheetProjectMember by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TimesheetProjectMember> findOne(Long id) {
        log.debug("Request to get TimesheetProjectMember : {}", id);
        return timesheetProjectMemberRepository.findById(id);
    }

    /**
     * Delete the timesheetProjectMember by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TimesheetProjectMember : {}", id);
        timesheetProjectMemberRepository.deleteById(id);
    }

    public List<TimesheetProjectMember> findAllByTimesheet(Timesheet timesheet) {
        return timesheetProjectMemberRepository.findAllByTimesheet(timesheet);
    }
}
