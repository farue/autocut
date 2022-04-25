package de.farue.autocut.web.rest;

import de.farue.autocut.domain.TimesheetTimer;
import de.farue.autocut.repository.TimesheetTimerRepository;
import de.farue.autocut.service.TimesheetTimerService;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.farue.autocut.domain.TimesheetTimer}.
 */
@RestController
@RequestMapping("/api")
public class TimesheetTimerResource {

    private final Logger log = LoggerFactory.getLogger(TimesheetTimerResource.class);

    private static final String ENTITY_NAME = "timesheetTimer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TimesheetTimerService timesheetTimerService;

    private final TimesheetTimerRepository timesheetTimerRepository;

    public TimesheetTimerResource(TimesheetTimerService timesheetTimerService, TimesheetTimerRepository timesheetTimerRepository) {
        this.timesheetTimerService = timesheetTimerService;
        this.timesheetTimerRepository = timesheetTimerRepository;
    }

    /**
     * {@code POST  /timesheet-timers} : Create a new timesheetTimer.
     *
     * @param timesheetTimer the timesheetTimer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timesheetTimer, or with status {@code 400 (Bad Request)} if the timesheetTimer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/timesheet-timers")
    public ResponseEntity<TimesheetTimer> createTimesheetTimer(@Valid @RequestBody TimesheetTimer timesheetTimer)
        throws URISyntaxException {
        log.debug("REST request to save TimesheetTimer : {}", timesheetTimer);
        if (timesheetTimer.getId() != null) {
            throw new BadRequestAlertException("A new timesheetTimer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TimesheetTimer result = timesheetTimerService.save(timesheetTimer);
        return ResponseEntity
            .created(new URI("/api/timesheet-timers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /timesheet-timers/:id} : Updates an existing timesheetTimer.
     *
     * @param id the id of the timesheetTimer to save.
     * @param timesheetTimer the timesheetTimer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timesheetTimer,
     * or with status {@code 400 (Bad Request)} if the timesheetTimer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timesheetTimer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/timesheet-timers/{id}")
    public ResponseEntity<TimesheetTimer> updateTimesheetTimer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TimesheetTimer timesheetTimer
    ) throws URISyntaxException {
        log.debug("REST request to update TimesheetTimer : {}, {}", id, timesheetTimer);
        if (timesheetTimer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timesheetTimer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timesheetTimerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TimesheetTimer result = timesheetTimerService.save(timesheetTimer);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timesheetTimer.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /timesheet-timers/:id} : Partial updates given fields of an existing timesheetTimer, field will ignore if it is null
     *
     * @param id the id of the timesheetTimer to save.
     * @param timesheetTimer the timesheetTimer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timesheetTimer,
     * or with status {@code 400 (Bad Request)} if the timesheetTimer is not valid,
     * or with status {@code 404 (Not Found)} if the timesheetTimer is not found,
     * or with status {@code 500 (Internal Server Error)} if the timesheetTimer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/timesheet-timers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TimesheetTimer> partialUpdateTimesheetTimer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TimesheetTimer timesheetTimer
    ) throws URISyntaxException {
        log.debug("REST request to partial update TimesheetTimer partially : {}, {}", id, timesheetTimer);
        if (timesheetTimer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timesheetTimer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timesheetTimerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TimesheetTimer> result = timesheetTimerService.partialUpdate(timesheetTimer);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timesheetTimer.getId().toString())
        );
    }

    /**
     * {@code GET  /timesheet-timers} : get all the timesheetTimers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of timesheetTimers in body.
     */
    @GetMapping("/timesheet-timers")
    public List<TimesheetTimer> getAllTimesheetTimers() {
        log.debug("REST request to get all TimesheetTimers");
        return timesheetTimerService.findAll();
    }

    /**
     * {@code GET  /timesheet-timers/:id} : get the "id" timesheetTimer.
     *
     * @param id the id of the timesheetTimer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timesheetTimer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/timesheet-timers/{id}")
    public ResponseEntity<TimesheetTimer> getTimesheetTimer(@PathVariable Long id) {
        log.debug("REST request to get TimesheetTimer : {}", id);
        Optional<TimesheetTimer> timesheetTimer = timesheetTimerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(timesheetTimer);
    }

    /**
     * {@code DELETE  /timesheet-timers/:id} : delete the "id" timesheetTimer.
     *
     * @param id the id of the timesheetTimer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/timesheet-timers/{id}")
    public ResponseEntity<Void> deleteTimesheetTimer(@PathVariable Long id) {
        log.debug("REST request to delete TimesheetTimer : {}", id);
        timesheetTimerService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
