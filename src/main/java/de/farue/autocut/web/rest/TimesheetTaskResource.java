package de.farue.autocut.web.rest;

import de.farue.autocut.domain.TimesheetTask;
import de.farue.autocut.repository.TimesheetTaskRepository;
import de.farue.autocut.service.TimesheetTaskService;
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
 * REST controller for managing {@link de.farue.autocut.domain.TimesheetTask}.
 */
@RestController
@RequestMapping("/api")
public class TimesheetTaskResource {

    private final Logger log = LoggerFactory.getLogger(TimesheetTaskResource.class);

    private static final String ENTITY_NAME = "timesheetTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TimesheetTaskService timesheetTaskService;

    private final TimesheetTaskRepository timesheetTaskRepository;

    public TimesheetTaskResource(TimesheetTaskService timesheetTaskService, TimesheetTaskRepository timesheetTaskRepository) {
        this.timesheetTaskService = timesheetTaskService;
        this.timesheetTaskRepository = timesheetTaskRepository;
    }

    /**
     * {@code POST  /timesheet-tasks} : Create a new timesheetTask.
     *
     * @param timesheetTask the timesheetTask to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timesheetTask, or with status {@code 400 (Bad Request)} if the timesheetTask has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/timesheet-tasks")
    public ResponseEntity<TimesheetTask> createTimesheetTask(@Valid @RequestBody TimesheetTask timesheetTask) throws URISyntaxException {
        log.debug("REST request to save TimesheetTask : {}", timesheetTask);
        if (timesheetTask.getId() != null) {
            throw new BadRequestAlertException("A new timesheetTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TimesheetTask result = timesheetTaskService.save(timesheetTask);
        return ResponseEntity
            .created(new URI("/api/timesheet-tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /timesheet-tasks/:id} : Updates an existing timesheetTask.
     *
     * @param id the id of the timesheetTask to save.
     * @param timesheetTask the timesheetTask to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timesheetTask,
     * or with status {@code 400 (Bad Request)} if the timesheetTask is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timesheetTask couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/timesheet-tasks/{id}")
    public ResponseEntity<TimesheetTask> updateTimesheetTask(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TimesheetTask timesheetTask
    ) throws URISyntaxException {
        log.debug("REST request to update TimesheetTask : {}, {}", id, timesheetTask);
        if (timesheetTask.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timesheetTask.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timesheetTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TimesheetTask result = timesheetTaskService.save(timesheetTask);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timesheetTask.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /timesheet-tasks/:id} : Partial updates given fields of an existing timesheetTask, field will ignore if it is null
     *
     * @param id the id of the timesheetTask to save.
     * @param timesheetTask the timesheetTask to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timesheetTask,
     * or with status {@code 400 (Bad Request)} if the timesheetTask is not valid,
     * or with status {@code 404 (Not Found)} if the timesheetTask is not found,
     * or with status {@code 500 (Internal Server Error)} if the timesheetTask couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/timesheet-tasks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TimesheetTask> partialUpdateTimesheetTask(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TimesheetTask timesheetTask
    ) throws URISyntaxException {
        log.debug("REST request to partial update TimesheetTask partially : {}, {}", id, timesheetTask);
        if (timesheetTask.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timesheetTask.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timesheetTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TimesheetTask> result = timesheetTaskService.partialUpdate(timesheetTask);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timesheetTask.getId().toString())
        );
    }

    /**
     * {@code GET  /timesheet-tasks} : get all the timesheetTasks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of timesheetTasks in body.
     */
    @GetMapping("/timesheet-tasks")
    public List<TimesheetTask> getAllTimesheetTasks() {
        log.debug("REST request to get all TimesheetTasks");
        return timesheetTaskService.findAll();
    }

    /**
     * {@code GET  /timesheet-tasks/:id} : get the "id" timesheetTask.
     *
     * @param id the id of the timesheetTask to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timesheetTask, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/timesheet-tasks/{id}")
    public ResponseEntity<TimesheetTask> getTimesheetTask(@PathVariable Long id) {
        log.debug("REST request to get TimesheetTask : {}", id);
        Optional<TimesheetTask> timesheetTask = timesheetTaskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(timesheetTask);
    }

    /**
     * {@code DELETE  /timesheet-tasks/:id} : delete the "id" timesheetTask.
     *
     * @param id the id of the timesheetTask to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/timesheet-tasks/{id}")
    public ResponseEntity<Void> deleteTimesheetTask(@PathVariable Long id) {
        log.debug("REST request to delete TimesheetTask : {}", id);
        timesheetTaskService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
