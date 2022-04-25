package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.domain.TimesheetProject;
import de.farue.autocut.domain.TimesheetProjectMember;
import de.farue.autocut.repository.TimesheetProjectMemberRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link TimesheetProjectMemberResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class TimesheetProjectMemberResourceIT {

    private static final Instant DEFAULT_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/timesheet-project-members";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TimesheetProjectMemberRepository timesheetProjectMemberRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTimesheetProjectMemberMockMvc;

    private TimesheetProjectMember timesheetProjectMember;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimesheetProjectMember createEntity(EntityManager em) {
        TimesheetProjectMember timesheetProjectMember = new TimesheetProjectMember().start(DEFAULT_START).end(DEFAULT_END);
        // Add required entity
        TimesheetProject timesheetProject;
        if (TestUtil.findAll(em, TimesheetProject.class).isEmpty()) {
            timesheetProject = TimesheetProjectResourceIT.createEntity(em);
            em.persist(timesheetProject);
            em.flush();
        } else {
            timesheetProject = TestUtil.findAll(em, TimesheetProject.class).get(0);
        }
        timesheetProjectMember.setProject(timesheetProject);
        // Add required entity
        Timesheet timesheet;
        if (TestUtil.findAll(em, Timesheet.class).isEmpty()) {
            timesheet = TimesheetResourceIT.createEntity(em);
            em.persist(timesheet);
            em.flush();
        } else {
            timesheet = TestUtil.findAll(em, Timesheet.class).get(0);
        }
        timesheetProjectMember.setTimesheet(timesheet);
        return timesheetProjectMember;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimesheetProjectMember createUpdatedEntity(EntityManager em) {
        TimesheetProjectMember timesheetProjectMember = new TimesheetProjectMember().start(UPDATED_START).end(UPDATED_END);
        // Add required entity
        TimesheetProject timesheetProject;
        if (TestUtil.findAll(em, TimesheetProject.class).isEmpty()) {
            timesheetProject = TimesheetProjectResourceIT.createUpdatedEntity(em);
            em.persist(timesheetProject);
            em.flush();
        } else {
            timesheetProject = TestUtil.findAll(em, TimesheetProject.class).get(0);
        }
        timesheetProjectMember.setProject(timesheetProject);
        // Add required entity
        Timesheet timesheet;
        if (TestUtil.findAll(em, Timesheet.class).isEmpty()) {
            timesheet = TimesheetResourceIT.createUpdatedEntity(em);
            em.persist(timesheet);
            em.flush();
        } else {
            timesheet = TestUtil.findAll(em, Timesheet.class).get(0);
        }
        timesheetProjectMember.setTimesheet(timesheet);
        return timesheetProjectMember;
    }

    @BeforeEach
    public void initTest() {
        timesheetProjectMember = createEntity(em);
    }

    @Test
    @Transactional
    void createTimesheetProjectMember() throws Exception {
        int databaseSizeBeforeCreate = timesheetProjectMemberRepository.findAll().size();
        // Create the TimesheetProjectMember
        restTimesheetProjectMemberMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timesheetProjectMember))
            )
            .andExpect(status().isCreated());

        // Validate the TimesheetProjectMember in the database
        List<TimesheetProjectMember> timesheetProjectMemberList = timesheetProjectMemberRepository.findAll();
        assertThat(timesheetProjectMemberList).hasSize(databaseSizeBeforeCreate + 1);
        TimesheetProjectMember testTimesheetProjectMember = timesheetProjectMemberList.get(timesheetProjectMemberList.size() - 1);
        assertThat(testTimesheetProjectMember.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testTimesheetProjectMember.getEnd()).isEqualTo(DEFAULT_END);
    }

    @Test
    @Transactional
    void createTimesheetProjectMemberWithExistingId() throws Exception {
        // Create the TimesheetProjectMember with an existing ID
        timesheetProjectMember.setId(1L);

        int databaseSizeBeforeCreate = timesheetProjectMemberRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimesheetProjectMemberMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timesheetProjectMember))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetProjectMember in the database
        List<TimesheetProjectMember> timesheetProjectMemberList = timesheetProjectMemberRepository.findAll();
        assertThat(timesheetProjectMemberList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTimesheetProjectMembers() throws Exception {
        // Initialize the database
        timesheetProjectMemberRepository.saveAndFlush(timesheetProjectMember);

        // Get all the timesheetProjectMemberList
        restTimesheetProjectMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timesheetProjectMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())));
    }

    @Test
    @Transactional
    void getTimesheetProjectMember() throws Exception {
        // Initialize the database
        timesheetProjectMemberRepository.saveAndFlush(timesheetProjectMember);

        // Get the timesheetProjectMember
        restTimesheetProjectMemberMockMvc
            .perform(get(ENTITY_API_URL_ID, timesheetProjectMember.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timesheetProjectMember.getId().intValue()))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.end").value(DEFAULT_END.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTimesheetProjectMember() throws Exception {
        // Get the timesheetProjectMember
        restTimesheetProjectMemberMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTimesheetProjectMember() throws Exception {
        // Initialize the database
        timesheetProjectMemberRepository.saveAndFlush(timesheetProjectMember);

        int databaseSizeBeforeUpdate = timesheetProjectMemberRepository.findAll().size();

        // Update the timesheetProjectMember
        TimesheetProjectMember updatedTimesheetProjectMember = timesheetProjectMemberRepository
            .findById(timesheetProjectMember.getId())
            .get();
        // Disconnect from session so that the updates on updatedTimesheetProjectMember are not directly saved in db
        em.detach(updatedTimesheetProjectMember);
        updatedTimesheetProjectMember.start(UPDATED_START).end(UPDATED_END);

        restTimesheetProjectMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTimesheetProjectMember.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTimesheetProjectMember))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetProjectMember in the database
        List<TimesheetProjectMember> timesheetProjectMemberList = timesheetProjectMemberRepository.findAll();
        assertThat(timesheetProjectMemberList).hasSize(databaseSizeBeforeUpdate);
        TimesheetProjectMember testTimesheetProjectMember = timesheetProjectMemberList.get(timesheetProjectMemberList.size() - 1);
        assertThat(testTimesheetProjectMember.getStart()).isEqualTo(UPDATED_START);
        assertThat(testTimesheetProjectMember.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    @Transactional
    void putNonExistingTimesheetProjectMember() throws Exception {
        int databaseSizeBeforeUpdate = timesheetProjectMemberRepository.findAll().size();
        timesheetProjectMember.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimesheetProjectMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timesheetProjectMember.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timesheetProjectMember))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetProjectMember in the database
        List<TimesheetProjectMember> timesheetProjectMemberList = timesheetProjectMemberRepository.findAll();
        assertThat(timesheetProjectMemberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimesheetProjectMember() throws Exception {
        int databaseSizeBeforeUpdate = timesheetProjectMemberRepository.findAll().size();
        timesheetProjectMember.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetProjectMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timesheetProjectMember))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetProjectMember in the database
        List<TimesheetProjectMember> timesheetProjectMemberList = timesheetProjectMemberRepository.findAll();
        assertThat(timesheetProjectMemberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimesheetProjectMember() throws Exception {
        int databaseSizeBeforeUpdate = timesheetProjectMemberRepository.findAll().size();
        timesheetProjectMember.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetProjectMemberMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timesheetProjectMember))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimesheetProjectMember in the database
        List<TimesheetProjectMember> timesheetProjectMemberList = timesheetProjectMemberRepository.findAll();
        assertThat(timesheetProjectMemberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTimesheetProjectMemberWithPatch() throws Exception {
        // Initialize the database
        timesheetProjectMemberRepository.saveAndFlush(timesheetProjectMember);

        int databaseSizeBeforeUpdate = timesheetProjectMemberRepository.findAll().size();

        // Update the timesheetProjectMember using partial update
        TimesheetProjectMember partialUpdatedTimesheetProjectMember = new TimesheetProjectMember();
        partialUpdatedTimesheetProjectMember.setId(timesheetProjectMember.getId());

        partialUpdatedTimesheetProjectMember.start(UPDATED_START).end(UPDATED_END);

        restTimesheetProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimesheetProjectMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimesheetProjectMember))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetProjectMember in the database
        List<TimesheetProjectMember> timesheetProjectMemberList = timesheetProjectMemberRepository.findAll();
        assertThat(timesheetProjectMemberList).hasSize(databaseSizeBeforeUpdate);
        TimesheetProjectMember testTimesheetProjectMember = timesheetProjectMemberList.get(timesheetProjectMemberList.size() - 1);
        assertThat(testTimesheetProjectMember.getStart()).isEqualTo(UPDATED_START);
        assertThat(testTimesheetProjectMember.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    @Transactional
    void fullUpdateTimesheetProjectMemberWithPatch() throws Exception {
        // Initialize the database
        timesheetProjectMemberRepository.saveAndFlush(timesheetProjectMember);

        int databaseSizeBeforeUpdate = timesheetProjectMemberRepository.findAll().size();

        // Update the timesheetProjectMember using partial update
        TimesheetProjectMember partialUpdatedTimesheetProjectMember = new TimesheetProjectMember();
        partialUpdatedTimesheetProjectMember.setId(timesheetProjectMember.getId());

        partialUpdatedTimesheetProjectMember.start(UPDATED_START).end(UPDATED_END);

        restTimesheetProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimesheetProjectMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimesheetProjectMember))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetProjectMember in the database
        List<TimesheetProjectMember> timesheetProjectMemberList = timesheetProjectMemberRepository.findAll();
        assertThat(timesheetProjectMemberList).hasSize(databaseSizeBeforeUpdate);
        TimesheetProjectMember testTimesheetProjectMember = timesheetProjectMemberList.get(timesheetProjectMemberList.size() - 1);
        assertThat(testTimesheetProjectMember.getStart()).isEqualTo(UPDATED_START);
        assertThat(testTimesheetProjectMember.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    @Transactional
    void patchNonExistingTimesheetProjectMember() throws Exception {
        int databaseSizeBeforeUpdate = timesheetProjectMemberRepository.findAll().size();
        timesheetProjectMember.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimesheetProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timesheetProjectMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timesheetProjectMember))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetProjectMember in the database
        List<TimesheetProjectMember> timesheetProjectMemberList = timesheetProjectMemberRepository.findAll();
        assertThat(timesheetProjectMemberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimesheetProjectMember() throws Exception {
        int databaseSizeBeforeUpdate = timesheetProjectMemberRepository.findAll().size();
        timesheetProjectMember.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timesheetProjectMember))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetProjectMember in the database
        List<TimesheetProjectMember> timesheetProjectMemberList = timesheetProjectMemberRepository.findAll();
        assertThat(timesheetProjectMemberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimesheetProjectMember() throws Exception {
        int databaseSizeBeforeUpdate = timesheetProjectMemberRepository.findAll().size();
        timesheetProjectMember.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timesheetProjectMember))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimesheetProjectMember in the database
        List<TimesheetProjectMember> timesheetProjectMemberList = timesheetProjectMemberRepository.findAll();
        assertThat(timesheetProjectMemberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTimesheetProjectMember() throws Exception {
        // Initialize the database
        timesheetProjectMemberRepository.saveAndFlush(timesheetProjectMember);

        int databaseSizeBeforeDelete = timesheetProjectMemberRepository.findAll().size();

        // Delete the timesheetProjectMember
        restTimesheetProjectMemberMockMvc
            .perform(delete(ENTITY_API_URL_ID, timesheetProjectMember.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TimesheetProjectMember> timesheetProjectMemberList = timesheetProjectMemberRepository.findAll();
        assertThat(timesheetProjectMemberList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
