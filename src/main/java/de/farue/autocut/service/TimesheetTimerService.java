package de.farue.autocut.service;

import de.farue.autocut.domain.TimesheetTimer;
import de.farue.autocut.repository.TimesheetTimerRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    private final LoggedInUserService loggedInUserService;
    private final TimesheetService timesheetService;

    public TimesheetTimerService(
        TimesheetTimerRepository timesheetTimerRepository,
        LoggedInUserService loggedInUserService,
        TimesheetService timesheetService
    ) {
        this.timesheetTimerRepository = timesheetTimerRepository;
        this.loggedInUserService = loggedInUserService;
        this.timesheetService = timesheetService;
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

    @Transactional(readOnly = true)
    public TimesheetTimer getTimer() {
        return timesheetService.findOneForCurrentUser().flatMap(timesheetTimerRepository::findOneByTimesheet).orElse(null);
    }

    public TimesheetTimer startTimer() {
        Instant start = Instant.now();
        return timesheetService
            .findOneForCurrentUser()
            .map(timesheet -> {
                timesheetTimerRepository
                    .findOneByTimesheet(timesheet)
                    .ifPresent(timer -> {
                        throw new IllegalStateException(
                            "Timer is already running for timesheet " + timesheet.getId() + " with start " + timer.getStart()
                        );
                    });

                TimesheetTimer timer = new TimesheetTimer().start(start).timesheet(timesheet);
                return this.timesheetTimerRepository.save(timer);
            })
            .orElse(null);
    }

    public void deleteTimer() {
        System.out.println(timesheetTimerRepository.findAll());

        timesheetService
            .findOneForCurrentUser()
            .flatMap(timesheetTimerRepository::findOneByTimesheet)
            .ifPresent(timesheetTimerRepository::delete);

        System.out.println(timesheetTimerRepository.findAll());
    }

    public TimesheetTimer pauseTimer() {
        return timesheetService
            .findOneForCurrentUser()
            .flatMap(timesheetTimerRepository::findOneByTimesheet)
            .map(timer -> {
                timer.setPauseStart(Instant.now());
                return timesheetTimerRepository.save(timer);
            })
            .orElse(null);
    }

    public TimesheetTimer unpauseTimer() {
        return timesheetService
            .findOneForCurrentUser()
            .flatMap(timesheetTimerRepository::findOneByTimesheet)
            .map(timer -> {
                Instant pauseStart = timer.getPauseStart();
                Instant pauseEnd = Instant.now();
                int pause = (int) pauseStart.until(pauseEnd, ChronoUnit.SECONDS);
                timer.setPauseStart(null);
                timer.setPause(timer.getPause() != null ? timer.getPause() + pause : pause);
                return timesheetTimerRepository.save(timer);
            })
            .orElse(null);
    }
}
