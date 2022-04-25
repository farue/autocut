package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.domain.TimesheetTimer;
import de.farue.autocut.repository.TimesheetTimerRepository;
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
 * Integration tests for the {@link TimesheetTimerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class TimesheetTimerResourceIT {

    private static final Instant DEFAULT_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_PAUSE_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PAUSE_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_PAUSE = 1;
    private static final Integer UPDATED_PAUSE = 2;

    private static final String ENTITY_API_URL = "/api/timesheet-timers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TimesheetTimerRepository timesheetTimerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTimesheetTimerMockMvc;

    private TimesheetTimer timesheetTimer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimesheetTimer createEntity(EntityManager em) {
        TimesheetTimer timesheetTimer = new TimesheetTimer().start(DEFAULT_START).pauseStart(DEFAULT_PAUSE_START).pause(DEFAULT_PAUSE);
        // Add required entity
        Timesheet timesheet;
        if (TestUtil.findAll(em, Timesheet.class).isEmpty()) {
            timesheet = TimesheetResourceIT.createEntity(em);
            em.persist(timesheet);
            em.flush();
        } else {
            timesheet = TestUtil.findAll(em, Timesheet.class).get(0);
        }
        timesheetTimer.setTimesheet(timesheet);
        return timesheetTimer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimesheetTimer createUpdatedEntity(EntityManager em) {
        TimesheetTimer timesheetTimer = new TimesheetTimer().start(UPDATED_START).pauseStart(UPDATED_PAUSE_START).pause(UPDATED_PAUSE);
        // Add required entity
        Timesheet timesheet;
        if (TestUtil.findAll(em, Timesheet.class).isEmpty()) {
            timesheet = TimesheetResourceIT.createUpdatedEntity(em);
            em.persist(timesheet);
            em.flush();
        } else {
            timesheet = TestUtil.findAll(em, Timesheet.class).get(0);
        }
        timesheetTimer.setTimesheet(timesheet);
        return timesheetTimer;
    }

    @BeforeEach
    public void initTest() {
        timesheetTimer = createEntity(em);
    }

    @Test
    @Transactional
    void createTimesheetTimer() throws Exception {
        int databaseSizeBeforeCreate = timesheetTimerRepository.findAll().size();
        // Create the TimesheetTimer
        restTimesheetTimerMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTimer))
            )
            .andExpect(status().isCreated());

        // Validate the TimesheetTimer in the database
        List<TimesheetTimer> timesheetTimerList = timesheetTimerRepository.findAll();
        assertThat(timesheetTimerList).hasSize(databaseSizeBeforeCreate + 1);
        TimesheetTimer testTimesheetTimer = timesheetTimerList.get(timesheetTimerList.size() - 1);
        assertThat(testTimesheetTimer.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testTimesheetTimer.getPauseStart()).isEqualTo(DEFAULT_PAUSE_START);
        assertThat(testTimesheetTimer.getPause()).isEqualTo(DEFAULT_PAUSE);
    }

    @Test
    @Transactional
    void createTimesheetTimerWithExistingId() throws Exception {
        // Create the TimesheetTimer with an existing ID
        timesheetTimer.setId(1L);

        int databaseSizeBeforeCreate = timesheetTimerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimesheetTimerMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTimer))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTimer in the database
        List<TimesheetTimer> timesheetTimerList = timesheetTimerRepository.findAll();
        assertThat(timesheetTimerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartIsRequired() throws Exception {
        int databaseSizeBeforeTest = timesheetTimerRepository.findAll().size();
        // set the field null
        timesheetTimer.setStart(null);

        // Create the TimesheetTimer, which fails.

        restTimesheetTimerMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTimer))
            )
            .andExpect(status().isBadRequest());

        List<TimesheetTimer> timesheetTimerList = timesheetTimerRepository.findAll();
        assertThat(timesheetTimerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTimesheetTimers() throws Exception {
        // Initialize the database
        timesheetTimerRepository.saveAndFlush(timesheetTimer);

        // Get all the timesheetTimerList
        restTimesheetTimerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timesheetTimer.getId().intValue())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].pauseStart").value(hasItem(DEFAULT_PAUSE_START.toString())))
            .andExpect(jsonPath("$.[*].pause").value(hasItem(DEFAULT_PAUSE)));
    }

    @Test
    @Transactional
    void getTimesheetTimer() throws Exception {
        // Initialize the database
        timesheetTimerRepository.saveAndFlush(timesheetTimer);

        // Get the timesheetTimer
        restTimesheetTimerMockMvc
            .perform(get(ENTITY_API_URL_ID, timesheetTimer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timesheetTimer.getId().intValue()))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.pauseStart").value(DEFAULT_PAUSE_START.toString()))
            .andExpect(jsonPath("$.pause").value(DEFAULT_PAUSE));
    }

    @Test
    @Transactional
    void getNonExistingTimesheetTimer() throws Exception {
        // Get the timesheetTimer
        restTimesheetTimerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTimesheetTimer() throws Exception {
        // Initialize the database
        timesheetTimerRepository.saveAndFlush(timesheetTimer);

        int databaseSizeBeforeUpdate = timesheetTimerRepository.findAll().size();

        // Update the timesheetTimer
        TimesheetTimer updatedTimesheetTimer = timesheetTimerRepository.findById(timesheetTimer.getId()).get();
        // Disconnect from session so that the updates on updatedTimesheetTimer are not directly saved in db
        em.detach(updatedTimesheetTimer);
        updatedTimesheetTimer.start(UPDATED_START).pauseStart(UPDATED_PAUSE_START).pause(UPDATED_PAUSE);

        restTimesheetTimerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTimesheetTimer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTimesheetTimer))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetTimer in the database
        List<TimesheetTimer> timesheetTimerList = timesheetTimerRepository.findAll();
        assertThat(timesheetTimerList).hasSize(databaseSizeBeforeUpdate);
        TimesheetTimer testTimesheetTimer = timesheetTimerList.get(timesheetTimerList.size() - 1);
        assertThat(testTimesheetTimer.getStart()).isEqualTo(UPDATED_START);
        assertThat(testTimesheetTimer.getPauseStart()).isEqualTo(UPDATED_PAUSE_START);
        assertThat(testTimesheetTimer.getPause()).isEqualTo(UPDATED_PAUSE);
    }

    @Test
    @Transactional
    void putNonExistingTimesheetTimer() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTimerRepository.findAll().size();
        timesheetTimer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimesheetTimerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timesheetTimer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timesheetTimer))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTimer in the database
        List<TimesheetTimer> timesheetTimerList = timesheetTimerRepository.findAll();
        assertThat(timesheetTimerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimesheetTimer() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTimerRepository.findAll().size();
        timesheetTimer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetTimerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timesheetTimer))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTimer in the database
        List<TimesheetTimer> timesheetTimerList = timesheetTimerRepository.findAll();
        assertThat(timesheetTimerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimesheetTimer() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTimerRepository.findAll().size();
        timesheetTimer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetTimerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTimer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimesheetTimer in the database
        List<TimesheetTimer> timesheetTimerList = timesheetTimerRepository.findAll();
        assertThat(timesheetTimerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTimesheetTimerWithPatch() throws Exception {
        // Initialize the database
        timesheetTimerRepository.saveAndFlush(timesheetTimer);

        int databaseSizeBeforeUpdate = timesheetTimerRepository.findAll().size();

        // Update the timesheetTimer using partial update
        TimesheetTimer partialUpdatedTimesheetTimer = new TimesheetTimer();
        partialUpdatedTimesheetTimer.setId(timesheetTimer.getId());

        partialUpdatedTimesheetTimer.start(UPDATED_START).pauseStart(UPDATED_PAUSE_START);

        restTimesheetTimerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimesheetTimer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimesheetTimer))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetTimer in the database
        List<TimesheetTimer> timesheetTimerList = timesheetTimerRepository.findAll();
        assertThat(timesheetTimerList).hasSize(databaseSizeBeforeUpdate);
        TimesheetTimer testTimesheetTimer = timesheetTimerList.get(timesheetTimerList.size() - 1);
        assertThat(testTimesheetTimer.getStart()).isEqualTo(UPDATED_START);
        assertThat(testTimesheetTimer.getPauseStart()).isEqualTo(UPDATED_PAUSE_START);
        assertThat(testTimesheetTimer.getPause()).isEqualTo(DEFAULT_PAUSE);
    }

    @Test
    @Transactional
    void fullUpdateTimesheetTimerWithPatch() throws Exception {
        // Initialize the database
        timesheetTimerRepository.saveAndFlush(timesheetTimer);

        int databaseSizeBeforeUpdate = timesheetTimerRepository.findAll().size();

        // Update the timesheetTimer using partial update
        TimesheetTimer partialUpdatedTimesheetTimer = new TimesheetTimer();
        partialUpdatedTimesheetTimer.setId(timesheetTimer.getId());

        partialUpdatedTimesheetTimer.start(UPDATED_START).pauseStart(UPDATED_PAUSE_START).pause(UPDATED_PAUSE);

        restTimesheetTimerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimesheetTimer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimesheetTimer))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetTimer in the database
        List<TimesheetTimer> timesheetTimerList = timesheetTimerRepository.findAll();
        assertThat(timesheetTimerList).hasSize(databaseSizeBeforeUpdate);
        TimesheetTimer testTimesheetTimer = timesheetTimerList.get(timesheetTimerList.size() - 1);
        assertThat(testTimesheetTimer.getStart()).isEqualTo(UPDATED_START);
        assertThat(testTimesheetTimer.getPauseStart()).isEqualTo(UPDATED_PAUSE_START);
        assertThat(testTimesheetTimer.getPause()).isEqualTo(UPDATED_PAUSE);
    }

    @Test
    @Transactional
    void patchNonExistingTimesheetTimer() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTimerRepository.findAll().size();
        timesheetTimer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimesheetTimerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timesheetTimer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timesheetTimer))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTimer in the database
        List<TimesheetTimer> timesheetTimerList = timesheetTimerRepository.findAll();
        assertThat(timesheetTimerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimesheetTimer() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTimerRepository.findAll().size();
        timesheetTimer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetTimerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timesheetTimer))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTimer in the database
        List<TimesheetTimer> timesheetTimerList = timesheetTimerRepository.findAll();
        assertThat(timesheetTimerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimesheetTimer() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTimerRepository.findAll().size();
        timesheetTimer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetTimerMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(timesheetTimer))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimesheetTimer in the database
        List<TimesheetTimer> timesheetTimerList = timesheetTimerRepository.findAll();
        assertThat(timesheetTimerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTimesheetTimer() throws Exception {
        // Initialize the database
        timesheetTimerRepository.saveAndFlush(timesheetTimer);

        int databaseSizeBeforeDelete = timesheetTimerRepository.findAll().size();

        // Delete the timesheetTimer
        restTimesheetTimerMockMvc
            .perform(delete(ENTITY_API_URL_ID, timesheetTimer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TimesheetTimer> timesheetTimerList = timesheetTimerRepository.findAll();
        assertThat(timesheetTimerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
