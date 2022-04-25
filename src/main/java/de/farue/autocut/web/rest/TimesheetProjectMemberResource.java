package de.farue.autocut.web.rest;

import de.farue.autocut.domain.TimesheetProjectMember;
import de.farue.autocut.repository.TimesheetProjectMemberRepository;
import de.farue.autocut.service.TimesheetProjectMemberService;
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
 * REST controller for managing {@link de.farue.autocut.domain.TimesheetProjectMember}.
 */
@RestController
@RequestMapping("/api")
public class TimesheetProjectMemberResource {

    private final Logger log = LoggerFactory.getLogger(TimesheetProjectMemberResource.class);

    private static final String ENTITY_NAME = "timesheetProjectMember";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TimesheetProjectMemberService timesheetProjectMemberService;

    private final TimesheetProjectMemberRepository timesheetProjectMemberRepository;

    public TimesheetProjectMemberResource(
        TimesheetProjectMemberService timesheetProjectMemberService,
        TimesheetProjectMemberRepository timesheetProjectMemberRepository
    ) {
        this.timesheetProjectMemberService = timesheetProjectMemberService;
        this.timesheetProjectMemberRepository = timesheetProjectMemberRepository;
    }

    /**
     * {@code POST  /timesheet-project-members} : Create a new timesheetProjectMember.
     *
     * @param timesheetProjectMember the timesheetProjectMember to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timesheetProjectMember, or with status {@code 400 (Bad Request)} if the timesheetProjectMember has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/timesheet-project-members")
    public ResponseEntity<TimesheetProjectMember> createTimesheetProjectMember(
        @Valid @RequestBody TimesheetProjectMember timesheetProjectMember
    ) throws URISyntaxException {
        log.debug("REST request to save TimesheetProjectMember : {}", timesheetProjectMember);
        if (timesheetProjectMember.getId() != null) {
            throw new BadRequestAlertException("A new timesheetProjectMember cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TimesheetProjectMember result = timesheetProjectMemberService.save(timesheetProjectMember);
        return ResponseEntity
            .created(new URI("/api/timesheet-project-members/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /timesheet-project-members/:id} : Updates an existing timesheetProjectMember.
     *
     * @param id the id of the timesheetProjectMember to save.
     * @param timesheetProjectMember the timesheetProjectMember to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timesheetProjectMember,
     * or with status {@code 400 (Bad Request)} if the timesheetProjectMember is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timesheetProjectMember couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/timesheet-project-members/{id}")
    public ResponseEntity<TimesheetProjectMember> updateTimesheetProjectMember(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TimesheetProjectMember timesheetProjectMember
    ) throws URISyntaxException {
        log.debug("REST request to update TimesheetProjectMember : {}, {}", id, timesheetProjectMember);
        if (timesheetProjectMember.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timesheetProjectMember.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timesheetProjectMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TimesheetProjectMember result = timesheetProjectMemberService.save(timesheetProjectMember);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timesheetProjectMember.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /timesheet-project-members/:id} : Partial updates given fields of an existing timesheetProjectMember, field will ignore if it is null
     *
     * @param id the id of the timesheetProjectMember to save.
     * @param timesheetProjectMember the timesheetProjectMember to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timesheetProjectMember,
     * or with status {@code 400 (Bad Request)} if the timesheetProjectMember is not valid,
     * or with status {@code 404 (Not Found)} if the timesheetProjectMember is not found,
     * or with status {@code 500 (Internal Server Error)} if the timesheetProjectMember couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/timesheet-project-members/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TimesheetProjectMember> partialUpdateTimesheetProjectMember(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TimesheetProjectMember timesheetProjectMember
    ) throws URISyntaxException {
        log.debug("REST request to partial update TimesheetProjectMember partially : {}, {}", id, timesheetProjectMember);
        if (timesheetProjectMember.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timesheetProjectMember.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timesheetProjectMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TimesheetProjectMember> result = timesheetProjectMemberService.partialUpdate(timesheetProjectMember);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timesheetProjectMember.getId().toString())
        );
    }

    /**
     * {@code GET  /timesheet-project-members} : get all the timesheetProjectMembers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of timesheetProjectMembers in body.
     */
    @GetMapping("/timesheet-project-members")
    public List<TimesheetProjectMember> getAllTimesheetProjectMembers() {
        log.debug("REST request to get all TimesheetProjectMembers");
        return timesheetProjectMemberService.findAll();
    }

    /**
     * {@code GET  /timesheet-project-members/:id} : get the "id" timesheetProjectMember.
     *
     * @param id the id of the timesheetProjectMember to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timesheetProjectMember, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/timesheet-project-members/{id}")
    public ResponseEntity<TimesheetProjectMember> getTimesheetProjectMember(@PathVariable Long id) {
        log.debug("REST request to get TimesheetProjectMember : {}", id);
        Optional<TimesheetProjectMember> timesheetProjectMember = timesheetProjectMemberService.findOne(id);
        return ResponseUtil.wrapOrNotFound(timesheetProjectMember);
    }

    /**
     * {@code DELETE  /timesheet-project-members/:id} : delete the "id" timesheetProjectMember.
     *
     * @param id the id of the timesheetProjectMember to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/timesheet-project-members/{id}")
    public ResponseEntity<Void> deleteTimesheetProjectMember(@PathVariable Long id) {
        log.debug("REST request to delete TimesheetProjectMember : {}", id);
        timesheetProjectMemberService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
