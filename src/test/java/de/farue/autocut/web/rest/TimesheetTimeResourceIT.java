package de.farue.autocut.web.rest;

import static de.farue.autocut.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.domain.TimesheetProject;
import de.farue.autocut.domain.TimesheetTask;
import de.farue.autocut.domain.TimesheetTime;
import de.farue.autocut.repository.TimesheetTimeRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TimesheetTimeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class TimesheetTimeResourceIT {

    private static final Instant DEFAULT_START = Instant.now().minus(Duration.ofHours(2)).truncatedTo(ChronoUnit.MILLIS);
    private static final Instant UPDATED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END = Instant.now().minus(Duration.ofHours(1)).truncatedTo(ChronoUnit.MILLIS);
    private static final Instant UPDATED_END = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_EFFECTIVE_TIME = 1;
    private static final Integer UPDATED_EFFECTIVE_TIME = 2;

    private static final Integer DEFAULT_PAUSE = 1;
    private static final Integer UPDATED_PAUSE = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_EDITED_FACTOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_EDITED_FACTOR = new BigDecimal(2);

    private static final Integer DEFAULT_EDITED_CONSTANT = 1;
    private static final Integer UPDATED_EDITED_CONSTANT = 2;

    private static final String ENTITY_API_URL = "/api/timesheet-times";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TimesheetTimeRepository timesheetTimeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTimesheetTimeMockMvc;

    private TimesheetTime timesheetTime;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimesheetTime createEntity(EntityManager em) {
        TimesheetTime timesheetTime = new TimesheetTime()
            .start(DEFAULT_START)
            .end(DEFAULT_END)
            .effectiveTime(DEFAULT_EFFECTIVE_TIME)
            .pause(DEFAULT_PAUSE)
            .description(DEFAULT_DESCRIPTION)
            .editedFactor(DEFAULT_EDITED_FACTOR)
            .editedConstant(DEFAULT_EDITED_CONSTANT);
        // Add required entity
        Timesheet timesheet;
        if (TestUtil.findAll(em, Timesheet.class).isEmpty()) {
            timesheet = TimesheetResourceIT.createEntity(em);
            em.persist(timesheet);
            em.flush();
        } else {
            timesheet = TestUtil.findAll(em, Timesheet.class).get(0);
        }
        timesheetTime.setTimesheet(timesheet);
        // Add required entity
        TimesheetProject timesheetProject;
        if (TestUtil.findAll(em, TimesheetProject.class).isEmpty()) {
            timesheetProject = TimesheetProjectResourceIT.createEntity(em);
            em.persist(timesheetProject);
            em.flush();
        } else {
            timesheetProject = TestUtil.findAll(em, TimesheetProject.class).get(0);
        }
        timesheetTime.setProject(timesheetProject);
        // Add required entity
        TimesheetTask timesheetTask;
        if (TestUtil.findAll(em, TimesheetTask.class).isEmpty()) {
            timesheetTask = TimesheetTaskResourceIT.createEntity(em);
            em.persist(timesheetTask);
            em.flush();
        } else {
            timesheetTask = TestUtil.findAll(em, TimesheetTask.class).get(0);
        }
        timesheetTime.setTask(timesheetTask);
        return timesheetTime;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimesheetTime createUpdatedEntity(EntityManager em) {
        TimesheetTime timesheetTime = new TimesheetTime()
            .start(UPDATED_START)
            .end(UPDATED_END)
            .effectiveTime(UPDATED_EFFECTIVE_TIME)
            .pause(UPDATED_PAUSE)
            .description(UPDATED_DESCRIPTION)
            .editedFactor(UPDATED_EDITED_FACTOR)
            .editedConstant(UPDATED_EDITED_CONSTANT);
        // Add required entity
        Timesheet timesheet;
        if (TestUtil.findAll(em, Timesheet.class).isEmpty()) {
            timesheet = TimesheetResourceIT.createUpdatedEntity(em);
            em.persist(timesheet);
            em.flush();
        } else {
            timesheet = TestUtil.findAll(em, Timesheet.class).get(0);
        }
        timesheetTime.setTimesheet(timesheet);
        // Add required entity
        TimesheetProject timesheetProject;
        if (TestUtil.findAll(em, TimesheetProject.class).isEmpty()) {
            timesheetProject = TimesheetProjectResourceIT.createUpdatedEntity(em);
            em.persist(timesheetProject);
            em.flush();
        } else {
            timesheetProject = TestUtil.findAll(em, TimesheetProject.class).get(0);
        }
        timesheetTime.setProject(timesheetProject);
        // Add required entity
        TimesheetTask timesheetTask;
        if (TestUtil.findAll(em, TimesheetTask.class).isEmpty()) {
            timesheetTask = TimesheetTaskResourceIT.createUpdatedEntity(em);
            em.persist(timesheetTask);
            em.flush();
        } else {
            timesheetTask = TestUtil.findAll(em, TimesheetTask.class).get(0);
        }
        timesheetTime.setTask(timesheetTask);
        return timesheetTime;
    }

    @BeforeEach
    public void initTest() {
        timesheetTime = createEntity(em);
    }

    @Test
    @Transactional
    void createTimesheetTime() throws Exception {
        int databaseSizeBeforeCreate = timesheetTimeRepository.findAll().size();
        // Create the TimesheetTime
        restTimesheetTimeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTime)))
            .andExpect(status().isCreated());

        // Validate the TimesheetTime in the database
        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeCreate + 1);
        TimesheetTime testTimesheetTime = timesheetTimeList.get(timesheetTimeList.size() - 1);
        assertThat(testTimesheetTime.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testTimesheetTime.getEnd()).isEqualTo(DEFAULT_END);
        assertThat(testTimesheetTime.getEffectiveTime()).isEqualTo(DEFAULT_EFFECTIVE_TIME);
        assertThat(testTimesheetTime.getPause()).isEqualTo(DEFAULT_PAUSE);
        assertThat(testTimesheetTime.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTimesheetTime.getEditedFactor()).isEqualByComparingTo(DEFAULT_EDITED_FACTOR);
        assertThat(testTimesheetTime.getEditedConstant()).isEqualTo(DEFAULT_EDITED_CONSTANT);
    }

    @Test
    @Transactional
    void createTimesheetTimeWithExistingId() throws Exception {
        // Create the TimesheetTime with an existing ID
        timesheetTime.setId(1L);

        int databaseSizeBeforeCreate = timesheetTimeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimesheetTimeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTime)))
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTime in the database
        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEffectiveTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = timesheetTimeRepository.findAll().size();
        // set the field null
        timesheetTime.setEffectiveTime(null);

        // Create the TimesheetTime, which fails.

        restTimesheetTimeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTime)))
            .andExpect(status().isBadRequest());

        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPauseIsRequired() throws Exception {
        int databaseSizeBeforeTest = timesheetTimeRepository.findAll().size();
        // set the field null
        timesheetTime.setPause(null);

        // Create the TimesheetTime, which fails.

        restTimesheetTimeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTime)))
            .andExpect(status().isBadRequest());

        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = timesheetTimeRepository.findAll().size();
        // set the field null
        timesheetTime.setDescription(null);

        // Create the TimesheetTime, which fails.

        restTimesheetTimeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTime)))
            .andExpect(status().isBadRequest());

        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTimesheetTimes() throws Exception {
        // Initialize the database
        timesheetTimeRepository.saveAndFlush(timesheetTime);

        // Get all the timesheetTimeList
        restTimesheetTimeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timesheetTime.getId().intValue())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())))
            .andExpect(jsonPath("$.[*].effectiveTime").value(hasItem(DEFAULT_EFFECTIVE_TIME)))
            .andExpect(jsonPath("$.[*].pause").value(hasItem(DEFAULT_PAUSE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].editedFactor").value(hasItem(sameNumber(DEFAULT_EDITED_FACTOR))))
            .andExpect(jsonPath("$.[*].editedConstant").value(hasItem(DEFAULT_EDITED_CONSTANT)));
    }

    @Test
    @Transactional
    void getTimesheetTime() throws Exception {
        // Initialize the database
        timesheetTimeRepository.saveAndFlush(timesheetTime);

        // Get the timesheetTime
        restTimesheetTimeMockMvc
            .perform(get(ENTITY_API_URL_ID, timesheetTime.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timesheetTime.getId().intValue()))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.end").value(DEFAULT_END.toString()))
            .andExpect(jsonPath("$.effectiveTime").value(DEFAULT_EFFECTIVE_TIME))
            .andExpect(jsonPath("$.pause").value(DEFAULT_PAUSE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.editedFactor").value(sameNumber(DEFAULT_EDITED_FACTOR)))
            .andExpect(jsonPath("$.editedConstant").value(DEFAULT_EDITED_CONSTANT));
    }

    @Test
    @Transactional
    void getNonExistingTimesheetTime() throws Exception {
        // Get the timesheetTime
        restTimesheetTimeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTimesheetTime() throws Exception {
        // Initialize the database
        timesheetTimeRepository.saveAndFlush(timesheetTime);

        int databaseSizeBeforeUpdate = timesheetTimeRepository.findAll().size();

        // Update the timesheetTime
        TimesheetTime updatedTimesheetTime = timesheetTimeRepository.findById(timesheetTime.getId()).get();
        // Disconnect from session so that the updates on updatedTimesheetTime are not directly saved in db
        em.detach(updatedTimesheetTime);
        updatedTimesheetTime
            .start(UPDATED_START)
            .end(UPDATED_END)
            .effectiveTime(UPDATED_EFFECTIVE_TIME)
            .pause(UPDATED_PAUSE)
            .description(UPDATED_DESCRIPTION)
            .editedFactor(UPDATED_EDITED_FACTOR)
            .editedConstant(UPDATED_EDITED_CONSTANT);

        restTimesheetTimeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTimesheetTime.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTimesheetTime))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetTime in the database
        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeUpdate);
        TimesheetTime testTimesheetTime = timesheetTimeList.get(timesheetTimeList.size() - 1);
        assertThat(testTimesheetTime.getStart()).isEqualTo(UPDATED_START);
        assertThat(testTimesheetTime.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testTimesheetTime.getEffectiveTime()).isEqualTo(UPDATED_EFFECTIVE_TIME);
        assertThat(testTimesheetTime.getPause()).isEqualTo(UPDATED_PAUSE);
        assertThat(testTimesheetTime.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTimesheetTime.getEditedFactor()).isEqualByComparingTo(UPDATED_EDITED_FACTOR);
        assertThat(testTimesheetTime.getEditedConstant()).isEqualTo(UPDATED_EDITED_CONSTANT);
    }

    @Test
    @Transactional
    void putNonExistingTimesheetTime() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTimeRepository.findAll().size();
        timesheetTime.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimesheetTimeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timesheetTime.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timesheetTime))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTime in the database
        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimesheetTime() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTimeRepository.findAll().size();
        timesheetTime.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetTimeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timesheetTime))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTime in the database
        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimesheetTime() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTimeRepository.findAll().size();
        timesheetTime.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetTimeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTime)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimesheetTime in the database
        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTimesheetTimeWithPatch() throws Exception {
        // Initialize the database
        timesheetTimeRepository.saveAndFlush(timesheetTime);

        int databaseSizeBeforeUpdate = timesheetTimeRepository.findAll().size();

        // Update the timesheetTime using partial update
        TimesheetTime partialUpdatedTimesheetTime = new TimesheetTime();
        partialUpdatedTimesheetTime.setId(timesheetTime.getId());

        partialUpdatedTimesheetTime.start(UPDATED_START).pause(UPDATED_PAUSE).editedFactor(UPDATED_EDITED_FACTOR);

        restTimesheetTimeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimesheetTime.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimesheetTime))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetTime in the database
        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeUpdate);
        TimesheetTime testTimesheetTime = timesheetTimeList.get(timesheetTimeList.size() - 1);
        assertThat(testTimesheetTime.getStart()).isEqualTo(UPDATED_START);
        assertThat(testTimesheetTime.getEnd()).isEqualTo(DEFAULT_END);
        assertThat(testTimesheetTime.getEffectiveTime()).isEqualTo(DEFAULT_EFFECTIVE_TIME);
        assertThat(testTimesheetTime.getPause()).isEqualTo(UPDATED_PAUSE);
        assertThat(testTimesheetTime.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTimesheetTime.getEditedFactor()).isEqualByComparingTo(UPDATED_EDITED_FACTOR);
        assertThat(testTimesheetTime.getEditedConstant()).isEqualTo(DEFAULT_EDITED_CONSTANT);
    }

    @Test
    @Transactional
    void fullUpdateTimesheetTimeWithPatch() throws Exception {
        // Initialize the database
        timesheetTimeRepository.saveAndFlush(timesheetTime);

        int databaseSizeBeforeUpdate = timesheetTimeRepository.findAll().size();

        // Update the timesheetTime using partial update
        TimesheetTime partialUpdatedTimesheetTime = new TimesheetTime();
        partialUpdatedTimesheetTime.setId(timesheetTime.getId());

        partialUpdatedTimesheetTime
            .start(UPDATED_START)
            .end(UPDATED_END)
            .effectiveTime(UPDATED_EFFECTIVE_TIME)
            .pause(UPDATED_PAUSE)
            .description(UPDATED_DESCRIPTION)
            .editedFactor(UPDATED_EDITED_FACTOR)
            .editedConstant(UPDATED_EDITED_CONSTANT);

        restTimesheetTimeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimesheetTime.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimesheetTime))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetTime in the database
        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeUpdate);
        TimesheetTime testTimesheetTime = timesheetTimeList.get(timesheetTimeList.size() - 1);
        assertThat(testTimesheetTime.getStart()).isEqualTo(UPDATED_START);
        assertThat(testTimesheetTime.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testTimesheetTime.getEffectiveTime()).isEqualTo(UPDATED_EFFECTIVE_TIME);
        assertThat(testTimesheetTime.getPause()).isEqualTo(UPDATED_PAUSE);
        assertThat(testTimesheetTime.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTimesheetTime.getEditedFactor()).isEqualByComparingTo(UPDATED_EDITED_FACTOR);
        assertThat(testTimesheetTime.getEditedConstant()).isEqualTo(UPDATED_EDITED_CONSTANT);
    }

    @Test
    @Transactional
    void patchNonExistingTimesheetTime() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTimeRepository.findAll().size();
        timesheetTime.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimesheetTimeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timesheetTime.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timesheetTime))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTime in the database
        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimesheetTime() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTimeRepository.findAll().size();
        timesheetTime.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetTimeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timesheetTime))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTime in the database
        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimesheetTime() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTimeRepository.findAll().size();
        timesheetTime.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetTimeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(timesheetTime))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimesheetTime in the database
        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @Disabled
    void deleteTimesheetTime() throws Exception {
        // Initialize the database
        timesheetTimeRepository.saveAndFlush(timesheetTime);

        int databaseSizeBeforeDelete = timesheetTimeRepository.findAll().size();

        // Delete the timesheetTime
        restTimesheetTimeMockMvc
            .perform(delete(ENTITY_API_URL_ID, timesheetTime.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TimesheetTime> timesheetTimeList = timesheetTimeRepository.findAll();
        assertThat(timesheetTimeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
