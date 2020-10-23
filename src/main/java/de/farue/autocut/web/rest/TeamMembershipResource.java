package de.farue.autocut.web.rest;

import de.farue.autocut.domain.TeamMembership;
import de.farue.autocut.repository.TeamMembershipRepository;
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
 * REST controller for managing {@link de.farue.autocut.domain.TeamMembership}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TeamMembershipResource {

    private final Logger log = LoggerFactory.getLogger(TeamMembershipResource.class);

    private static final String ENTITY_NAME = "teamMembership";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeamMembershipRepository teamMembershipRepository;

    public TeamMembershipResource(TeamMembershipRepository teamMembershipRepository) {
        this.teamMembershipRepository = teamMembershipRepository;
    }

    /**
     * {@code POST  /team-memberships} : Create a new teamMembership.
     *
     * @param teamMembership the teamMembership to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teamMembership, or with status {@code 400 (Bad Request)} if the teamMembership has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/team-memberships")
    public ResponseEntity<TeamMembership> createTeamMembership(@Valid @RequestBody TeamMembership teamMembership) throws URISyntaxException {
        log.debug("REST request to save TeamMembership : {}", teamMembership);
        if (teamMembership.getId() != null) {
            throw new BadRequestAlertException("A new teamMembership cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TeamMembership result = teamMembershipRepository.save(teamMembership);
        return ResponseEntity.created(new URI("/api/team-memberships/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /team-memberships} : Updates an existing teamMembership.
     *
     * @param teamMembership the teamMembership to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamMembership,
     * or with status {@code 400 (Bad Request)} if the teamMembership is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teamMembership couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/team-memberships")
    public ResponseEntity<TeamMembership> updateTeamMembership(@Valid @RequestBody TeamMembership teamMembership) throws URISyntaxException {
        log.debug("REST request to update TeamMembership : {}", teamMembership);
        if (teamMembership.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TeamMembership result = teamMembershipRepository.save(teamMembership);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teamMembership.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /team-memberships} : get all the teamMemberships.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teamMemberships in body.
     */
    @GetMapping("/team-memberships")
    public List<TeamMembership> getAllTeamMemberships() {
        log.debug("REST request to get all TeamMemberships");
        return teamMembershipRepository.findAll();
    }

    /**
     * {@code GET  /team-memberships/:id} : get the "id" teamMembership.
     *
     * @param id the id of the teamMembership to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teamMembership, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/team-memberships/{id}")
    public ResponseEntity<TeamMembership> getTeamMembership(@PathVariable Long id) {
        log.debug("REST request to get TeamMembership : {}", id);
        Optional<TeamMembership> teamMembership = teamMembershipRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(teamMembership);
    }

    /**
     * {@code DELETE  /team-memberships/:id} : delete the "id" teamMembership.
     *
     * @param id the id of the teamMembership to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/team-memberships/{id}")
    public ResponseEntity<Void> deleteTeamMembership(@PathVariable Long id) {
        log.debug("REST request to delete TeamMembership : {}", id);
        teamMembershipRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
