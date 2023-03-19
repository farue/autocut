package de.farue.autocut.service;

import static de.farue.autocut.utils.BigDecimalUtil.compare;

import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.domain.TimesheetProject;
import de.farue.autocut.domain.TimesheetTask;
import de.farue.autocut.domain.TimesheetTime;
import de.farue.autocut.repository.TimesheetTimeRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.dto.CreateTimesheetTimeDTO;
import de.farue.autocut.service.timesheet.TimesheetTimeCalculator;
import de.farue.autocut.utils.DateUtil;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link TimesheetTime}.
 */
@Service
@Transactional
public class TimesheetTimeService {

    public static final Duration BOOKING_PERIOD = Duration.ofHours(2 * 24);
    public static final Duration DELETION_PERIOD = Duration.ofHours(7 * 24);

    private final Logger log = LoggerFactory.getLogger(TimesheetTimeService.class);

    private final TimesheetTimeRepository timesheetTimeRepository;

    private final TimesheetProjectService timesheetProjectService;
    private final TimesheetService timesheetService;
    private final TimesheetTaskService timesheetTaskService;

    public TimesheetTimeService(
        TimesheetTimeRepository timesheetTimeRepository,
        TimesheetProjectService timesheetProjectService,
        TimesheetService timesheetService,
        TimesheetTaskService timesheetTaskService
    ) {
        this.timesheetTimeRepository = timesheetTimeRepository;
        this.timesheetProjectService = timesheetProjectService;
        this.timesheetService = timesheetService;
        this.timesheetTaskService = timesheetTaskService;
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
        Optional<TimesheetTime> timesheetTimeOptional = timesheetService
            .findOneForCurrentUser()
            .flatMap(timesheet -> timesheetTimeRepository.findById(id).filter(time -> time.getTimesheet().equals(timesheet)));
        if (timesheetTimeOptional.isPresent()) {
            if (timesheetTimeOptional.get().getStart().isBefore(Instant.now().minus(DELETION_PERIOD))) {
                throw new TimesheetTimeEntryTooOldException(ChangeType.DELETE, DELETION_PERIOD);
            }
            timesheetTimeRepository.deleteById(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Transactional(readOnly = true)
    public Page<TimesheetTime> findAllByTimesheet(Timesheet timesheet, Pageable pageable) {
        return timesheetTimeRepository.findAllByTimesheet(timesheet, pageable);
    }

    public TimesheetTime save(Long timesheetId, Long timeId, CreateTimesheetTimeDTO timeDTO) {
        Timesheet timesheet = timesheetService
            .findOneForCurrentUser()
            .stream()
            .filter(t -> t.getId().equals(timesheetId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TimesheetProject project = timesheetProjectService
            .findAllByTimesheet(timesheet)
            .stream()
            .filter(p -> p.getId().equals(timeDTO.getProjectId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Project " + timeDTO.getProjectId() + " does not exist."));
        TimesheetTask task = timesheetTaskService
            .findAllByTimesheetProject(project)
            .stream()
            .filter(t -> t.getId().equals(timeDTO.getTaskId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Task " + timeDTO.getTaskId() + " does not exist or has been disabled."));
        TimesheetTime time = new TimesheetTime()
            .id(timeId)
            .start(DateUtil.roundDown(timeDTO.getStart(), ChronoUnit.SECONDS))
            .end(DateUtil.roundUp(timeDTO.getEnd(), ChronoUnit.SECONDS))
            .pause(timeDTO.getPause())
            .timesheet(timesheet)
            .project(project)
            .task(task)
            .editedConstant(timeDTO.getEditedConstant())
            .editedFactor(timeDTO.getEditedFactor())
            .description(timeDTO.getDescription());

        TimesheetTimeCalculator calculator = new TimesheetTimeCalculator(time);
        time.setEffectiveTime(calculator.getEffectiveTime());
        validate(time, calculator);
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

    public long getSumTime(Timesheet timesheet) {
        Long sum = timesheetTimeRepository.getSumTimes(timesheet);
        return sum != null ? sum : 0;
    }

    public long getSumTime(Timesheet timesheet, Instant earliest, Instant latest) {
        Long sum = timesheetTimeRepository.getSumTimes(timesheet, earliest, latest);
        return sum != null ? sum : 0;
    }

    private void validate(TimesheetTime time, TimesheetTimeCalculator calculator) {
        if (!compare(calculator.getFactor()).isZero() && time.getEnd() == null) {
            throw new ValidationException("end must not be null");
        }
        if (time.getEnd() != null && time.getStart().isAfter(time.getEnd())) {
            throw new ValidationException("start must not be after end");
        }
        if (calculator.getWorkedTime() < 0) {
            throw new ValidationException("worked time must be positive integer");
        }

        if (BooleanUtils.isNotTrue(time.getTask().getConstantEditable())) {
            if (time.getEditedConstant() != null) {
                throw new ValidationException("not allowed to edit constant");
            }
        }
        if (BooleanUtils.isNotTrue(time.getTask().getFactorEditable())) {
            if (time.getEditedFactor() != null) {
                throw new ValidationException("not allowed to edit constant");
            }
        }

        if (time.getStart().isBefore(Instant.now().minus(BOOKING_PERIOD))) {
            throw new TimesheetTimeEntryTooOldException(time.getId() == null ? ChangeType.CREATE : ChangeType.UPDATE, BOOKING_PERIOD);
        }
    }
}
