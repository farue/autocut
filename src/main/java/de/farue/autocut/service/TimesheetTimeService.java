package de.farue.autocut.service;

import static de.farue.autocut.utils.BigDecimalUtil.compare;

import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.domain.TimesheetProject;
import de.farue.autocut.domain.TimesheetTask;
import de.farue.autocut.domain.TimesheetTime;
import de.farue.autocut.repository.TimesheetTimeRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.timesheet.TimesheetTimeCalculator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.ValidationException;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TimesheetTime}.
 */
@Service
@Transactional
public class TimesheetTimeService {

    private final Logger log = LoggerFactory.getLogger(TimesheetTimeService.class);

    private final TimesheetTimeRepository timesheetTimeRepository;

    private final TimesheetProjectService timesheetProjectService;
    private final TimesheetService timesheetService;

    public TimesheetTimeService(
        TimesheetTimeRepository timesheetTimeRepository,
        TimesheetProjectService timesheetProjectService,
        TimesheetService timesheetService
    ) {
        this.timesheetTimeRepository = timesheetTimeRepository;
        this.timesheetProjectService = timesheetProjectService;
        this.timesheetService = timesheetService;
    }

    /**
     * Save a timesheetTime.
     *
     * @param timesheetTime the entity to save.
     * @return the persisted entity.
     */
    public TimesheetTime save(TimesheetTime timesheetTime) {
        log.debug("Request to save TimesheetTime : {}", timesheetTime);
        return timesheetTimeRepository.save(timesheetTime);
    }

    /**
     * Partially update a timesheetTime.
     *
     * @param timesheetTime the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TimesheetTime> partialUpdate(TimesheetTime timesheetTime) {
        log.debug("Request to partially update TimesheetTime : {}", timesheetTime);

        return timesheetTimeRepository
            .findById(timesheetTime.getId())
            .map(existingTimesheetTime -> {
                if (timesheetTime.getStart() != null) {
                    existingTimesheetTime.setStart(timesheetTime.getStart());
                }
                if (timesheetTime.getEnd() != null) {
                    existingTimesheetTime.setEnd(timesheetTime.getEnd());
                }
                if (timesheetTime.getEffectiveTime() != null) {
                    existingTimesheetTime.setEffectiveTime(timesheetTime.getEffectiveTime());
                }
                if (timesheetTime.getPause() != null) {
                    existingTimesheetTime.setPause(timesheetTime.getPause());
                }
                if (timesheetTime.getDescription() != null) {
                    existingTimesheetTime.setDescription(timesheetTime.getDescription());
                }
                if (timesheetTime.getEditedFactor() != null) {
                    existingTimesheetTime.setEditedFactor(timesheetTime.getEditedFactor());
                }
                if (timesheetTime.getEditedConstant() != null) {
                    existingTimesheetTime.setEditedConstant(timesheetTime.getEditedConstant());
                }

                return existingTimesheetTime;
            })
            .map(timesheetTimeRepository::save);
    }

    /**
     * Get all the timesheetTimes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TimesheetTime> findAll() {
        log.debug("Request to get all TimesheetTimes");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return timesheetTimeRepository.findAll();
        } else {
            return timesheetService
                .findOneForCurrentUser()
                .map(timesheet -> findAllByTimesheet(timesheet, Pageable.unpaged()))
                .map(Slice::getContent)
                .orElse(Collections.emptyList());
        }
    }

    /**
     * Get one timesheetTime by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TimesheetTime> findOne(Long id) {
        log.debug("Request to get TimesheetTime : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return timesheetTimeRepository.findById(id);
        } else {
            return timesheetService
                .findOneForCurrentUser()
                .flatMap(timesheet -> timesheetTimeRepository.findById(id).filter(time -> time.getTimesheet().equals(timesheet)));
        }
    }

    /**
     * Delete the timesheetTime by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TimesheetTime : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            timesheetTimeRepository.deleteById(id);
        } else {
            Optional<TimesheetTime> timesheetTimeOptional = timesheetService
                .findOneForCurrentUser()
                .flatMap(timesheet -> timesheetTimeRepository.findById(id).filter(time -> time.getTimesheet().equals(timesheet)));
            if (timesheetTimeOptional.isPresent()) {
                timesheetTimeRepository.deleteById(id);
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<TimesheetTime> findAllByTimesheet(Timesheet timesheet, Pageable pageable) {
        return timesheetTimeRepository.findAllByTimesheet(timesheet, pageable);
    }

    public TimesheetTime saveWithValidation(TimesheetTime time) {
        validate(time);
        time.setEffectiveTime(new TimesheetTimeCalculator(time).getEffectiveTime());
        return save(time);
    }

    public List<String> getDescriptions(Timesheet timesheet, TimesheetProject project) {
        return timesheetTimeRepository
            .findByTimesheetOrderByLastUsed(timesheet, project, Pageable.ofSize(10))
            .stream()
            .map(TimesheetTime::getDescription)
            .distinct()
            .toList();
    }

    private void validate(TimesheetTime time) {
        // TODO: Create DTO with correct validation annotations and create custom validators
        Timesheet timesheet = time.getTimesheet();
        if (timesheet == null) {
            throw new ValidationException("timesheet must not be null");
        }
        TimesheetProject project = time.getProject();
        if (project == null) {
            throw new ValidationException("project must not be null");
        }
        TimesheetTask task = time.getTask();
        if (task == null) {
            throw new ValidationException("task must not be null");
        }
        if (time.getStart() == null) {
            throw new ValidationException("start must not be null");
        }
        if (time.getPause() == null) {
            throw new ValidationException("pause must not be null");
        }

        TimesheetTimeCalculator calculator = new TimesheetTimeCalculator(time);

        if (!compare(calculator.getFactor()).isZero() && time.getEnd() == null) {
            throw new ValidationException("end must not be null");
        }
        if (time.getEnd() != null && time.getStart().isAfter(time.getEnd())) {
            throw new ValidationException("start must not be after end");
        }
        if (calculator.getWorkedTime() < 0) {
            throw new ValidationException("worked time must be positive integer");
        }

        if (!timesheetProjectService.findAllByTimesheet(timesheet).contains(project)) {
            throw new ValidationException("project does not exist");
        }
        if (!project.getTasks().contains(task)) {
            throw new ValidationException("task does not exist");
        }

        if (BooleanUtils.isNotTrue(task.getConstantEditable())) {
            if (time.getEditedConstant() != null) {
                throw new ValidationException("not allowed to edit constant");
            }
        }
        if (BooleanUtils.isNotTrue(task.getFactorEditable())) {
            if (time.getEditedFactor() != null) {
                throw new ValidationException("not allowed to edit constant");
            }
        }
    }
}
