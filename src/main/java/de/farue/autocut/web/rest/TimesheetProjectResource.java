package de.farue.autocut.web.rest;

import de.farue.autocut.domain.TimesheetProject;
import de.farue.autocut.repository.TimesheetProjectRepository;
import de.farue.autocut.service.TimesheetProjectService;
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
 * REST controller for managing {@link de.farue.autocut.domain.TimesheetProject}.
 */
@RestController
@RequestMapping("/api")
public class TimesheetProjectResource {

    private final Logger log = LoggerFactory.getLogger(TimesheetProjectResource.class);

    private static final String ENTITY_NAME = "timesheetProject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TimesheetProjectService timesheetProjectService;

    private final TimesheetProjectRepository timesheetProjectRepository;

    public TimesheetProjectResource(
        TimesheetProjectService timesheetProjectService,
        TimesheetProjectRepository timesheetProjectRepository
    ) {
        this.timesheetProjectService = timesheetProjectService;
        this.timesheetProjectRepository = timesheetProjectRepository;
    }

    /**
     * {@code POST  /timesheet-projects} : Create a new timesheetProject.
     *
     * @param timesheetProject the timesheetProject to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timesheetProject, or with status {@code 400 (Bad Request)} if the timesheetProject has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/timesheet-projects")
    public ResponseEntity<TimesheetProject> createTimesheetProject(@Valid @RequestBody TimesheetProject timesheetProject)
        throws URISyntaxException {
        log.debug("REST request to save TimesheetProject : {}", timesheetProject);
        if (timesheetProject.getId() != null) {
            throw new BadRequestAlertException("A new timesheetProject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TimesheetProject result = timesheetProjectService.save(timesheetProject);
        return ResponseEntity
            .created(new URI("/api/timesheet-projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /timesheet-projects/:id} : Updates an existing timesheetProject.
     *
     * @param id the id of the timesheetProject to save.
     * @param timesheetProject the timesheetProject to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timesheetProject,
     * or with status {@code 400 (Bad Request)} if the timesheetProject is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timesheetProject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/timesheet-projects/{id}")
    public ResponseEntity<TimesheetProject> updateTimesheetProject(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TimesheetProject timesheetProject
    ) throws URISyntaxException {
        log.debug("REST request to update TimesheetProject : {}, {}", id, timesheetProject);
        if (timesheetProject.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timesheetProject.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timesheetProjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TimesheetProject result = timesheetProjectService.save(timesheetProject);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timesheetProject.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /timesheet-projects/:id} : Partial updates given fields of an existing timesheetProject, field will ignore if it is null
     *
     * @param id the id of the timesheetProject to save.
     * @param timesheetProject the timesheetProject to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timesheetProject,
     * or with status {@code 400 (Bad Request)} if the timesheetProject is not valid,
     * or with status {@code 404 (Not Found)} if the timesheetProject is not found,
     * or with status {@code 500 (Internal Server Error)} if the timesheetProject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/timesheet-projects/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TimesheetProject> partialUpdateTimesheetProject(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TimesheetProject timesheetProject
    ) throws URISyntaxException {
        log.debug("REST request to partial update TimesheetProject partially : {}, {}", id, timesheetProject);
        if (timesheetProject.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timesheetProject.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timesheetProjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TimesheetProject> result = timesheetProjectService.partialUpdate(timesheetProject);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timesheetProject.getId().toString())
        );
    }

    /**
     * {@code GET  /timesheet-projects} : get all the timesheetProjects.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of timesheetProjects in body.
     */
    @GetMapping("/timesheet-projects")
    public List<TimesheetProject> getAllTimesheetProjects(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all TimesheetProjects");
        return timesheetProjectService.findAll();
    }

    /**
     * {@code GET  /timesheet-projects/:id} : get the "id" timesheetProject.
     *
     * @param id the id of the timesheetProject to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timesheetProject, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/timesheet-projects/{id}")
    public ResponseEntity<TimesheetProject> getTimesheetProject(@PathVariable Long id) {
        log.debug("REST request to get TimesheetProject : {}", id);
        Optional<TimesheetProject> timesheetProject = timesheetProjectService.findOne(id);
        return ResponseUtil.wrapOrNotFound(timesheetProject);
    }

    /**
     * {@code DELETE  /timesheet-projects/:id} : delete the "id" timesheetProject.
     *
     * @param id the id of the timesheetProject to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/timesheet-projects/{id}")
    public ResponseEntity<Void> deleteTimesheetProject(@PathVariable Long id) {
        log.debug("REST request to delete TimesheetProject : {}", id);
        timesheetProjectService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
