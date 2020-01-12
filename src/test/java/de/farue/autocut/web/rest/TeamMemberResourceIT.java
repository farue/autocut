package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.TeamMember;
import de.farue.autocut.domain.Team;
import de.farue.autocut.repository.TeamMemberRepository;
import de.farue.autocut.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static de.farue.autocut.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.domain.enumeration.TeamRole;
/**
 * Integration tests for the {@link TeamMemberResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
public class TeamMemberResourceIT {

    private static final TeamRole DEFAULT_ROLE = TeamRole.SPOKESPERSON;
    private static final TeamRole UPDATED_ROLE = TeamRole.DEPUTY;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restTeamMemberMockMvc;

    private TeamMember teamMember;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TeamMemberResource teamMemberResource = new TeamMemberResource(teamMemberRepository);
        this.restTeamMemberMockMvc = MockMvcBuilders.standaloneSetup(teamMemberResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamMember createEntity(EntityManager em) {
        TeamMember teamMember = new TeamMember()
            .role(DEFAULT_ROLE);
        // Add required entity
        Team team;
        if (TestUtil.findAll(em, Team.class).isEmpty()) {
            team = TeamResourceIT.createEntity(em);
            em.persist(team);
            em.flush();
        } else {
            team = TestUtil.findAll(em, Team.class).get(0);
        }
        teamMember.setTeam(team);
        return teamMember;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamMember createUpdatedEntity(EntityManager em) {
        TeamMember teamMember = new TeamMember()
            .role(UPDATED_ROLE);
        // Add required entity
        Team team;
        if (TestUtil.findAll(em, Team.class).isEmpty()) {
            team = TeamResourceIT.createUpdatedEntity(em);
            em.persist(team);
            em.flush();
        } else {
            team = TestUtil.findAll(em, Team.class).get(0);
        }
        teamMember.setTeam(team);
        return teamMember;
    }

    @BeforeEach
    public void initTest() {
        teamMember = createEntity(em);
    }

    @Test
    @Transactional
    public void createTeamMember() throws Exception {
        int databaseSizeBeforeCreate = teamMemberRepository.findAll().size();

        // Create the TeamMember
        restTeamMemberMockMvc.perform(post("/api/team-members")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(teamMember)))
            .andExpect(status().isCreated());

        // Validate the TeamMember in the database
        List<TeamMember> teamMemberList = teamMemberRepository.findAll();
        assertThat(teamMemberList).hasSize(databaseSizeBeforeCreate + 1);
        TeamMember testTeamMember = teamMemberList.get(teamMemberList.size() - 1);
        assertThat(testTeamMember.getRole()).isEqualTo(DEFAULT_ROLE);
    }

    @Test
    @Transactional
    public void createTeamMemberWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = teamMemberRepository.findAll().size();

        // Create the TeamMember with an existing ID
        teamMember.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamMemberMockMvc.perform(post("/api/team-members")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(teamMember)))
            .andExpect(status().isBadRequest());

        // Validate the TeamMember in the database
        List<TeamMember> teamMemberList = teamMemberRepository.findAll();
        assertThat(teamMemberList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllTeamMembers() throws Exception {
        // Initialize the database
        teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList
        restTeamMemberMockMvc.perform(get("/api/team-members?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teamMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));
    }
    
    @Test
    @Transactional
    public void getTeamMember() throws Exception {
        // Initialize the database
        teamMemberRepository.saveAndFlush(teamMember);

        // Get the teamMember
        restTeamMemberMockMvc.perform(get("/api/team-members/{id}", teamMember.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(teamMember.getId().intValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTeamMember() throws Exception {
        // Get the teamMember
        restTeamMemberMockMvc.perform(get("/api/team-members/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTeamMember() throws Exception {
        // Initialize the database
        teamMemberRepository.saveAndFlush(teamMember);

        int databaseSizeBeforeUpdate = teamMemberRepository.findAll().size();

        // Update the teamMember
        TeamMember updatedTeamMember = teamMemberRepository.findById(teamMember.getId()).get();
        // Disconnect from session so that the updates on updatedTeamMember are not directly saved in db
        em.detach(updatedTeamMember);
        updatedTeamMember
            .role(UPDATED_ROLE);

        restTeamMemberMockMvc.perform(put("/api/team-members")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTeamMember)))
            .andExpect(status().isOk());

        // Validate the TeamMember in the database
        List<TeamMember> teamMemberList = teamMemberRepository.findAll();
        assertThat(teamMemberList).hasSize(databaseSizeBeforeUpdate);
        TeamMember testTeamMember = teamMemberList.get(teamMemberList.size() - 1);
        assertThat(testTeamMember.getRole()).isEqualTo(UPDATED_ROLE);
    }

    @Test
    @Transactional
    public void updateNonExistingTeamMember() throws Exception {
        int databaseSizeBeforeUpdate = teamMemberRepository.findAll().size();

        // Create the TeamMember

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamMemberMockMvc.perform(put("/api/team-members")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(teamMember)))
            .andExpect(status().isBadRequest());

        // Validate the TeamMember in the database
        List<TeamMember> teamMemberList = teamMemberRepository.findAll();
        assertThat(teamMemberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTeamMember() throws Exception {
        // Initialize the database
        teamMemberRepository.saveAndFlush(teamMember);

        int databaseSizeBeforeDelete = teamMemberRepository.findAll().size();

        // Delete the teamMember
        restTeamMemberMockMvc.perform(delete("/api/team-members/{id}", teamMember.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TeamMember> teamMemberList = teamMemberRepository.findAll();
        assertThat(teamMemberList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
