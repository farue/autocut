package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.Team;
import de.farue.autocut.domain.TeamMembership;
import de.farue.autocut.domain.enumeration.TeamRole;
import de.farue.autocut.repository.TeamMembershipRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TeamMembershipResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class TeamMembershipResourceIT {

    private static final TeamRole DEFAULT_ROLE = TeamRole.LEAD;
    private static final TeamRole UPDATED_ROLE = TeamRole.DEPUTY;

    private static final LocalDate DEFAULT_START = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/team-memberships";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

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
        TeamMembership teamMembership = new TeamMembership().role(DEFAULT_ROLE).start(DEFAULT_START).end(DEFAULT_END);
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
        TeamMembership teamMembership = new TeamMembership().role(UPDATED_ROLE).start(UPDATED_START).end(UPDATED_END);
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
    void createTeamMembership() throws Exception {
        int databaseSizeBeforeCreate = teamMembershipRepository.findAll().size();
        // Create the TeamMembership
        restTeamMembershipMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamMembership))
            )
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
    void createTeamMembershipWithExistingId() throws Exception {
        // Create the TeamMembership with an existing ID
        teamMembership.setId(1L);

        int databaseSizeBeforeCreate = teamMembershipRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamMembershipMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamMembership))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamMembership in the database
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTeamMemberships() throws Exception {
        // Initialize the database
        teamMembershipRepository.saveAndFlush(teamMembership);

        // Get all the teamMembershipList
        restTeamMembershipMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teamMembership.getId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())));
    }

    @Test
    @Transactional
    void getTeamMembership() throws Exception {
        // Initialize the database
        teamMembershipRepository.saveAndFlush(teamMembership);

        // Get the teamMembership
        restTeamMembershipMockMvc
            .perform(get(ENTITY_API_URL_ID, teamMembership.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teamMembership.getId().intValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.end").value(DEFAULT_END.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTeamMembership() throws Exception {
        // Get the teamMembership
        restTeamMembershipMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTeamMembership() throws Exception {
        // Initialize the database
        teamMembershipRepository.saveAndFlush(teamMembership);

        int databaseSizeBeforeUpdate = teamMembershipRepository.findAll().size();

        // Update the teamMembership
        TeamMembership updatedTeamMembership = teamMembershipRepository.findById(teamMembership.getId()).get();
        // Disconnect from session so that the updates on updatedTeamMembership are not directly saved in db
        em.detach(updatedTeamMembership);
        updatedTeamMembership.role(UPDATED_ROLE).start(UPDATED_START).end(UPDATED_END);

        restTeamMembershipMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTeamMembership.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTeamMembership))
            )
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
    void putNonExistingTeamMembership() throws Exception {
        int databaseSizeBeforeUpdate = teamMembershipRepository.findAll().size();
        teamMembership.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamMembershipMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teamMembership.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamMembership))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamMembership in the database
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeamMembership() throws Exception {
        int databaseSizeBeforeUpdate = teamMembershipRepository.findAll().size();
        teamMembership.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMembershipMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamMembership))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamMembership in the database
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeamMembership() throws Exception {
        int databaseSizeBeforeUpdate = teamMembershipRepository.findAll().size();
        teamMembership.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMembershipMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamMembership)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamMembership in the database
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeamMembershipWithPatch() throws Exception {
        // Initialize the database
        teamMembershipRepository.saveAndFlush(teamMembership);

        int databaseSizeBeforeUpdate = teamMembershipRepository.findAll().size();

        // Update the teamMembership using partial update
        TeamMembership partialUpdatedTeamMembership = new TeamMembership();
        partialUpdatedTeamMembership.setId(teamMembership.getId());

        partialUpdatedTeamMembership.role(UPDATED_ROLE).end(UPDATED_END);

        restTeamMembershipMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamMembership.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeamMembership))
            )
            .andExpect(status().isOk());

        // Validate the TeamMembership in the database
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeUpdate);
        TeamMembership testTeamMembership = teamMembershipList.get(teamMembershipList.size() - 1);
        assertThat(testTeamMembership.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testTeamMembership.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testTeamMembership.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    @Transactional
    void fullUpdateTeamMembershipWithPatch() throws Exception {
        // Initialize the database
        teamMembershipRepository.saveAndFlush(teamMembership);

        int databaseSizeBeforeUpdate = teamMembershipRepository.findAll().size();

        // Update the teamMembership using partial update
        TeamMembership partialUpdatedTeamMembership = new TeamMembership();
        partialUpdatedTeamMembership.setId(teamMembership.getId());

        partialUpdatedTeamMembership.role(UPDATED_ROLE).start(UPDATED_START).end(UPDATED_END);

        restTeamMembershipMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamMembership.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeamMembership))
            )
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
    void patchNonExistingTeamMembership() throws Exception {
        int databaseSizeBeforeUpdate = teamMembershipRepository.findAll().size();
        teamMembership.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamMembershipMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teamMembership.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teamMembership))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamMembership in the database
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeamMembership() throws Exception {
        int databaseSizeBeforeUpdate = teamMembershipRepository.findAll().size();
        teamMembership.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMembershipMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teamMembership))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamMembership in the database
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeamMembership() throws Exception {
        int databaseSizeBeforeUpdate = teamMembershipRepository.findAll().size();
        teamMembership.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMembershipMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(teamMembership))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamMembership in the database
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeamMembership() throws Exception {
        // Initialize the database
        teamMembershipRepository.saveAndFlush(teamMembership);

        int databaseSizeBeforeDelete = teamMembershipRepository.findAll().size();

        // Delete the teamMembership
        restTeamMembershipMockMvc
            .perform(delete(ENTITY_API_URL_ID, teamMembership.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TeamMembership> teamMembershipList = teamMembershipRepository.findAll();
        assertThat(teamMembershipList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
