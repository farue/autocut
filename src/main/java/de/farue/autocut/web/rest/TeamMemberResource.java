package de.farue.autocut.web.rest;

import de.farue.autocut.domain.TeamMember;
import de.farue.autocut.repository.TeamMemberRepository;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link de.farue.autocut.domain.TeamMember}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TeamMemberResource {

    private final Logger log = LoggerFactory.getLogger(TeamMemberResource.class);

    private static final String ENTITY_NAME = "teamMember";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeamMemberRepository teamMemberRepository;

    public TeamMemberResource(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    /**
     * {@code POST  /team-members} : Create a new teamMember.
     *
     * @param teamMember the teamMember to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teamMember, or with status {@code 400 (Bad Request)} if the teamMember has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/team-members")
    public ResponseEntity<TeamMember> createTeamMember(@Valid @RequestBody TeamMember teamMember) throws URISyntaxException {
        log.debug("REST request to save TeamMember : {}", teamMember);
        if (teamMember.getId() != null) {
            throw new BadRequestAlertException("A new teamMember cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TeamMember result = teamMemberRepository.save(teamMember);
        return ResponseEntity.created(new URI("/api/team-members/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /team-members} : Updates an existing teamMember.
     *
     * @param teamMember the teamMember to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamMember,
     * or with status {@code 400 (Bad Request)} if the teamMember is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teamMember couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/team-members")
    public ResponseEntity<TeamMember> updateTeamMember(@Valid @RequestBody TeamMember teamMember) throws URISyntaxException {
        log.debug("REST request to update TeamMember : {}", teamMember);
        if (teamMember.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TeamMember result = teamMemberRepository.save(teamMember);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teamMember.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /team-members} : get all the teamMembers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teamMembers in body.
     */
    @GetMapping("/team-members")
    public List<TeamMember> getAllTeamMembers() {
        log.debug("REST request to get all TeamMembers");
        return teamMemberRepository.findAll();
    }

    /**
     * {@code GET  /team-members/:id} : get the "id" teamMember.
     *
     * @param id the id of the teamMember to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teamMember, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/team-members/{id}")
    public ResponseEntity<TeamMember> getTeamMember(@PathVariable Long id) {
        log.debug("REST request to get TeamMember : {}", id);
        Optional<TeamMember> teamMember = teamMemberRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(teamMember);
    }

    /**
     * {@code DELETE  /team-members/:id} : delete the "id" teamMember.
     *
     * @param id the id of the teamMember to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/team-members/{id}")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable Long id) {
        log.debug("REST request to delete TeamMember : {}", id);
        teamMemberRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
