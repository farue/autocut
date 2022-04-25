package de.farue.autocut.web.rest;

import de.farue.autocut.domain.TimesheetTime;
import de.farue.autocut.repository.TimesheetTimeRepository;
import de.farue.autocut.service.TimesheetTimeService;
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
 * REST controller for managing {@link de.farue.autocut.domain.TimesheetTime}.
 */
@RestController
@RequestMapping("/api")
public class TimesheetTimeResource {

    private final Logger log = LoggerFactory.getLogger(TimesheetTimeResource.class);

    private static final String ENTITY_NAME = "timesheetTime";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TimesheetTimeService timesheetTimeService;

    private final TimesheetTimeRepository timesheetTimeRepository;

    public TimesheetTimeResource(TimesheetTimeService timesheetTimeService, TimesheetTimeRepository timesheetTimeRepository) {
        this.timesheetTimeService = timesheetTimeService;
        this.timesheetTimeRepository = timesheetTimeRepository;
    }

    /**
     * {@code POST  /timesheet-times} : Create a new timesheetTime.
     *
     * @param timesheetTime the timesheetTime to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timesheetTime, or with status {@code 400 (Bad Request)} if the timesheetTime has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/timesheet-times")
    public ResponseEntity<TimesheetTime> createTimesheetTime(@Valid @RequestBody TimesheetTime timesheetTime) throws URISyntaxException {
        log.debug("REST request to save TimesheetTime : {}", timesheetTime);
        if (timesheetTime.getId() != null) {
            throw new BadRequestAlertException("A new timesheetTime cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TimesheetTime result = timesheetTimeService.save(timesheetTime);
        return ResponseEntity
            .created(new URI("/api/timesheet-times/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /timesheet-times/:id} : Updates an existing timesheetTime.
     *
     * @param id the id of the timesheetTime to save.
     * @param timesheetTime the timesheetTime to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timesheetTime,
     * or with status {@code 400 (Bad Request)} if the timesheetTime is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timesheetTime couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/timesheet-times/{id}")
    public ResponseEntity<TimesheetTime> updateTimesheetTime(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TimesheetTime timesheetTime
    ) throws URISyntaxException {
        log.debug("REST request to update TimesheetTime : {}, {}", id, timesheetTime);
        if (timesheetTime.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timesheetTime.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timesheetTimeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TimesheetTime result = timesheetTimeService.save(timesheetTime);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timesheetTime.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /timesheet-times/:id} : Partial updates given fields of an existing timesheetTime, field will ignore if it is null
     *
     * @param id the id of the timesheetTime to save.
     * @param timesheetTime the timesheetTime to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timesheetTime,
     * or with status {@code 400 (Bad Request)} if the timesheetTime is not valid,
     * or with status {@code 404 (Not Found)} if the timesheetTime is not found,
     * or with status {@code 500 (Internal Server Error)} if the timesheetTime couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/timesheet-times/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TimesheetTime> partialUpdateTimesheetTime(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TimesheetTime timesheetTime
    ) throws URISyntaxException {
        log.debug("REST request to partial update TimesheetTime partially : {}, {}", id, timesheetTime);
        if (timesheetTime.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timesheetTime.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timesheetTimeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TimesheetTime> result = timesheetTimeService.partialUpdate(timesheetTime);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timesheetTime.getId().toString())
        );
    }

    /**
     * {@code GET  /timesheet-times} : get all the timesheetTimes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of timesheetTimes in body.
     */
    @GetMapping("/timesheet-times")
    public List<TimesheetTime> getAllTimesheetTimes() {
        log.debug("REST request to get all TimesheetTimes");
        return timesheetTimeService.findAll();
    }

    /**
     * {@code GET  /timesheet-times/:id} : get the "id" timesheetTime.
     *
     * @param id the id of the timesheetTime to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timesheetTime, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/timesheet-times/{id}")
    public ResponseEntity<TimesheetTime> getTimesheetTime(@PathVariable Long id) {
        log.debug("REST request to get TimesheetTime : {}", id);
        Optional<TimesheetTime> timesheetTime = timesheetTimeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(timesheetTime);
    }

    /**
     * {@code DELETE  /timesheet-times/:id} : delete the "id" timesheetTime.
     *
     * @param id the id of the timesheetTime to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/timesheet-times/{id}")
    public ResponseEntity<Void> deleteTimesheetTime(@PathVariable Long id) {
        log.debug("REST request to delete TimesheetTime : {}", id);
        timesheetTimeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
