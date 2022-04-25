package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.TimesheetProject;
import de.farue.autocut.repository.TimesheetProjectRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import de.farue.autocut.service.TimesheetProjectService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TimesheetProjectResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class TimesheetProjectResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/timesheet-projects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TimesheetProjectRepository timesheetProjectRepository;

    @Mock
    private TimesheetProjectRepository timesheetProjectRepositoryMock;

    @Mock
    private TimesheetProjectService timesheetProjectServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTimesheetProjectMockMvc;

    private TimesheetProject timesheetProject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimesheetProject createEntity(EntityManager em) {
        TimesheetProject timesheetProject = new TimesheetProject().name(DEFAULT_NAME).start(DEFAULT_START).end(DEFAULT_END);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createEntity(em);
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        timesheetProject.setOwner(tenant);
        return timesheetProject;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimesheetProject createUpdatedEntity(EntityManager em) {
        TimesheetProject timesheetProject = new TimesheetProject().name(UPDATED_NAME).start(UPDATED_START).end(UPDATED_END);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createUpdatedEntity(em);
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        timesheetProject.setOwner(tenant);
        return timesheetProject;
    }

    @BeforeEach
    public void initTest() {
        timesheetProject = createEntity(em);
    }

    @Test
    @Transactional
    void createTimesheetProject() throws Exception {
        int databaseSizeBeforeCreate = timesheetProjectRepository.findAll().size();
        // Create the TimesheetProject
        restTimesheetProjectMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetProject))
            )
            .andExpect(status().isCreated());

        // Validate the TimesheetProject in the database
        List<TimesheetProject> timesheetProjectList = timesheetProjectRepository.findAll();
        assertThat(timesheetProjectList).hasSize(databaseSizeBeforeCreate + 1);
        TimesheetProject testTimesheetProject = timesheetProjectList.get(timesheetProjectList.size() - 1);
        assertThat(testTimesheetProject.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTimesheetProject.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testTimesheetProject.getEnd()).isEqualTo(DEFAULT_END);
    }

    @Test
    @Transactional
    void createTimesheetProjectWithExistingId() throws Exception {
        // Create the TimesheetProject with an existing ID
        timesheetProject.setId(1L);

        int databaseSizeBeforeCreate = timesheetProjectRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimesheetProjectMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetProject in the database
        List<TimesheetProject> timesheetProjectList = timesheetProjectRepository.findAll();
        assertThat(timesheetProjectList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = timesheetProjectRepository.findAll().size();
        // set the field null
        timesheetProject.setName(null);

        // Create the TimesheetProject, which fails.

        restTimesheetProjectMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetProject))
            )
            .andExpect(status().isBadRequest());

        List<TimesheetProject> timesheetProjectList = timesheetProjectRepository.findAll();
        assertThat(timesheetProjectList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTimesheetProjects() throws Exception {
        // Initialize the database
        timesheetProjectRepository.saveAndFlush(timesheetProject);

        // Get all the timesheetProjectList
        restTimesheetProjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timesheetProject.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTimesheetProjectsWithEagerRelationshipsIsEnabled() throws Exception {
        when(timesheetProjectServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTimesheetProjectMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(timesheetProjectServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTimesheetProjectsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(timesheetProjectServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTimesheetProjectMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(timesheetProjectServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getTimesheetProject() throws Exception {
        // Initialize the database
        timesheetProjectRepository.saveAndFlush(timesheetProject);

        // Get the timesheetProject
        restTimesheetProjectMockMvc
            .perform(get(ENTITY_API_URL_ID, timesheetProject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timesheetProject.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.end").value(DEFAULT_END.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTimesheetProject() throws Exception {
        // Get the timesheetProject
        restTimesheetProjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTimesheetProject() throws Exception {
        // Initialize the database
        timesheetProjectRepository.saveAndFlush(timesheetProject);

        int databaseSizeBeforeUpdate = timesheetProjectRepository.findAll().size();

        // Update the timesheetProject
        TimesheetProject updatedTimesheetProject = timesheetProjectRepository.findById(timesheetProject.getId()).get();
        // Disconnect from session so that the updates on updatedTimesheetProject are not directly saved in db
        em.detach(updatedTimesheetProject);
        updatedTimesheetProject.name(UPDATED_NAME).start(UPDATED_START).end(UPDATED_END);

        restTimesheetProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTimesheetProject.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTimesheetProject))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetProject in the database
        List<TimesheetProject> timesheetProjectList = timesheetProjectRepository.findAll();
        assertThat(timesheetProjectList).hasSize(databaseSizeBeforeUpdate);
        TimesheetProject testTimesheetProject = timesheetProjectList.get(timesheetProjectList.size() - 1);
        assertThat(testTimesheetProject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTimesheetProject.getStart()).isEqualTo(UPDATED_START);
        assertThat(testTimesheetProject.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    @Transactional
    void putNonExistingTimesheetProject() throws Exception {
        int databaseSizeBeforeUpdate = timesheetProjectRepository.findAll().size();
        timesheetProject.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimesheetProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timesheetProject.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timesheetProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetProject in the database
        List<TimesheetProject> timesheetProjectList = timesheetProjectRepository.findAll();
        assertThat(timesheetProjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimesheetProject() throws Exception {
        int databaseSizeBeforeUpdate = timesheetProjectRepository.findAll().size();
        timesheetProject.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timesheetProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetProject in the database
        List<TimesheetProject> timesheetProjectList = timesheetProjectRepository.findAll();
        assertThat(timesheetProjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimesheetProject() throws Exception {
        int databaseSizeBeforeUpdate = timesheetProjectRepository.findAll().size();
        timesheetProject.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetProjectMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetProject))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimesheetProject in the database
        List<TimesheetProject> timesheetProjectList = timesheetProjectRepository.findAll();
        assertThat(timesheetProjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTimesheetProjectWithPatch() throws Exception {
        // Initialize the database
        timesheetProjectRepository.saveAndFlush(timesheetProject);

        int databaseSizeBeforeUpdate = timesheetProjectRepository.findAll().size();

        // Update the timesheetProject using partial update
        TimesheetProject partialUpdatedTimesheetProject = new TimesheetProject();
        partialUpdatedTimesheetProject.setId(timesheetProject.getId());

        partialUpdatedTimesheetProject.end(UPDATED_END);

        restTimesheetProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimesheetProject.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimesheetProject))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetProject in the database
        List<TimesheetProject> timesheetProjectList = timesheetProjectRepository.findAll();
        assertThat(timesheetProjectList).hasSize(databaseSizeBeforeUpdate);
        TimesheetProject testTimesheetProject = timesheetProjectList.get(timesheetProjectList.size() - 1);
        assertThat(testTimesheetProject.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTimesheetProject.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testTimesheetProject.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    @Transactional
    void fullUpdateTimesheetProjectWithPatch() throws Exception {
        // Initialize the database
        timesheetProjectRepository.saveAndFlush(timesheetProject);

        int databaseSizeBeforeUpdate = timesheetProjectRepository.findAll().size();

        // Update the timesheetProject using partial update
        TimesheetProject partialUpdatedTimesheetProject = new TimesheetProject();
        partialUpdatedTimesheetProject.setId(timesheetProject.getId());

        partialUpdatedTimesheetProject.name(UPDATED_NAME).start(UPDATED_START).end(UPDATED_END);

        restTimesheetProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimesheetProject.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimesheetProject))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetProject in the database
        List<TimesheetProject> timesheetProjectList = timesheetProjectRepository.findAll();
        assertThat(timesheetProjectList).hasSize(databaseSizeBeforeUpdate);
        TimesheetProject testTimesheetProject = timesheetProjectList.get(timesheetProjectList.size() - 1);
        assertThat(testTimesheetProject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTimesheetProject.getStart()).isEqualTo(UPDATED_START);
        assertThat(testTimesheetProject.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    @Transactional
    void patchNonExistingTimesheetProject() throws Exception {
        int databaseSizeBeforeUpdate = timesheetProjectRepository.findAll().size();
        timesheetProject.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimesheetProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timesheetProject.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timesheetProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetProject in the database
        List<TimesheetProject> timesheetProjectList = timesheetProjectRepository.findAll();
        assertThat(timesheetProjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimesheetProject() throws Exception {
        int databaseSizeBeforeUpdate = timesheetProjectRepository.findAll().size();
        timesheetProject.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timesheetProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetProject in the database
        List<TimesheetProject> timesheetProjectList = timesheetProjectRepository.findAll();
        assertThat(timesheetProjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimesheetProject() throws Exception {
        int databaseSizeBeforeUpdate = timesheetProjectRepository.findAll().size();
        timesheetProject.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetProjectMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timesheetProject))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimesheetProject in the database
        List<TimesheetProject> timesheetProjectList = timesheetProjectRepository.findAll();
        assertThat(timesheetProjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTimesheetProject() throws Exception {
        // Initialize the database
        timesheetProjectRepository.saveAndFlush(timesheetProject);

        int databaseSizeBeforeDelete = timesheetProjectRepository.findAll().size();

        // Delete the timesheetProject
        restTimesheetProjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, timesheetProject.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TimesheetProject> timesheetProjectList = timesheetProjectRepository.findAll();
        assertThat(timesheetProjectList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
