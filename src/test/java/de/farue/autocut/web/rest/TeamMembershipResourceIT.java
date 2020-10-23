package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.TeamMembership;
import de.farue.autocut.domain.Team;
import de.farue.autocut.repository.TeamMembershipRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.domain.enumeration.TeamRole;
/**
 * Integration tests for the {@link TeamMembershipResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class TeamMembershipResourceIT {

    private static final TeamRole DEFAULT_ROLE = TeamRole.SPOKESPERSON;
    private static final TeamRole UPDATED_ROLE = TeamRole.DEPUTY;

    private static final LocalDate DEFAULT_START = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private TeamMembershipRepository teamMembershipRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeamMembershipMockMvc;

    private TeamMembership teamMembership;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamMembership createEntity(EntityManager em) {
        TeamMembership teamMembership = new TeamMembership()
            .role(DEFAULT_ROLE)
            .start(DEFAULT_START)
            .end(DEFAULT_END);
        // Add required entity
        Team team;
        if (TestUtil.findAll(em, Team.class).isEmpty()) {
            team = TeamResourceIT.createEntity(em);
            em.persist(team);
            em.flush();
        } else {
            team = TestUtil.findAll(em, Team.class).get(0);
        }
        teamMembership.setTeam(team);
        return teamMembership;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamMembership createUpdatedEntity(EntityManager em) {
        TeamMembership teamMembership = new TeamMembership()
            .role(UPDATED_ROLE)
            .start(UPDATED_START)
            .end(UPDATED_END);
        // Add required entity
        Team team;
        if (TestUtil.findAll(em, Team.class).isEmpty()) {
            team = TeamResourceIT.createUpdatedEntity(em);
            em.persist(team);
            em.flush();
        } else {
            team = TestUtil.findAll(em, Team.class).get(0);
        }
        teamMembership.setTeam(team);
        return teamMembership;
    }

    @BeforeEach
    public void initTest() {
        teamMembership = createEntity(em);
    }

    @Test
    @Transactional
    public void createTeamMembership() throws Exception {
        int databaseSizeBeforeCreate = teamMembershipRepository.findAll().size();
        // Create the TeamMembership
        restTeamMembershipMockMvc.perform(post("/api/team-memberships")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(teamMembership)))
            .andExpect(status().isCreated());

        // Validate the TeamMembership in the database
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeCreate + 1);
        TeamMembership testTeamMembership = teamMembershipList.get(teamMembershipList.size() - 1);
        assertThat(testTeamMembership.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testTeamMembership.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testTeamMembership.getEnd()).isEqualTo(DEFAULT_END);
    }

    @Test
    @Transactional
    public void createTeamMembershipWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = teamMembershipRepository.findAll().size();

        // Create the TeamMembership with an existing ID
        teamMembership.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamMembershipMockMvc.perform(post("/api/team-memberships")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(teamMembership)))
            .andExpect(status().isBadRequest());

        // Validate the TeamMembership in the database
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllTeamMemberships() throws Exception {
        // Initialize the database
        teamMembershipRepository.saveAndFlush(teamMembership);

        // Get all the teamMembershipList
        restTeamMembershipMockMvc.perform(get("/api/team-memberships?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teamMembership.getId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())));
    }
    
    @Test
    @Transactional
    public void getTeamMembership() throws Exception {
        // Initialize the database
        teamMembershipRepository.saveAndFlush(teamMembership);

        // Get the teamMembership
        restTeamMembershipMockMvc.perform(get("/api/team-memberships/{id}", teamMembership.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teamMembership.getId().intValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.end").value(DEFAULT_END.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingTeamMembership() throws Exception {
        // Get the teamMembership
        restTeamMembershipMockMvc.perform(get("/api/team-memberships/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTeamMembership() throws Exception {
        // Initialize the database
        teamMembershipRepository.saveAndFlush(teamMembership);

        int databaseSizeBeforeUpdate = teamMembershipRepository.findAll().size();

        // Update the teamMembership
        TeamMembership updatedTeamMembership = teamMembershipRepository.findById(teamMembership.getId()).get();
        // Disconnect from session so that the updates on updatedTeamMembership are not directly saved in db
        em.detach(updatedTeamMembership);
        updatedTeamMembership
            .role(UPDATED_ROLE)
            .start(UPDATED_START)
            .end(UPDATED_END);

        restTeamMembershipMockMvc.perform(put("/api/team-memberships")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedTeamMembership)))
            .andExpect(status().isOk());

        // Validate the TeamMembership in the database
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeUpdate);
        TeamMembership testTeamMembership = teamMembershipList.get(teamMembershipList.size() - 1);
        assertThat(testTeamMembership.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testTeamMembership.getStart()).isEqualTo(UPDATED_START);
        assertThat(testTeamMembership.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    @Transactional
    public void updateNonExistingTeamMembership() throws Exception {
        int databaseSizeBeforeUpdate = teamMembershipRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamMembershipMockMvc.perform(put("/api/team-memberships")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(teamMembership)))
            .andExpect(status().isBadRequest());

        // Validate the TeamMembership in the database
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTeamMembership() throws Exception {
        // Initialize the database
        teamMembershipRepository.saveAndFlush(teamMembership);

        int databaseSizeBeforeDelete = teamMembershipRepository.findAll().size();

        // Delete the teamMembership
        restTeamMembershipMockMvc.perform(delete("/api/team-memberships/{id}", teamMembership.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
