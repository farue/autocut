package de.farue.autocut.web.rest;

import static de.farue.autocut.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.TimesheetTask;
import de.farue.autocut.repository.TimesheetTaskRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import java.math.BigDecimal;
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
 * Integration tests for the {@link TimesheetTaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class TimesheetTaskResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final Integer DEFAULT_CONSTANT = 1;
    private static final Integer UPDATED_CONSTANT = 2;

    private static final Boolean DEFAULT_CONSTANT_EDITABLE = false;
    private static final Boolean UPDATED_CONSTANT_EDITABLE = true;

    private static final BigDecimal DEFAULT_FACTOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_FACTOR = new BigDecimal(2);

    private static final Boolean DEFAULT_FACTOR_EDITABLE = false;
    private static final Boolean UPDATED_FACTOR_EDITABLE = true;

    private static final Integer DEFAULT_DEFAULT_TIMESPAN = 1;
    private static final Integer UPDATED_DEFAULT_TIMESPAN = 2;

    private static final String ENTITY_API_URL = "/api/timesheet-tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TimesheetTaskRepository timesheetTaskRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTimesheetTaskMockMvc;

    private TimesheetTask timesheetTask;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimesheetTask createEntity(EntityManager em) {
        TimesheetTask timesheetTask = new TimesheetTask()
            .name(DEFAULT_NAME)
            .enabled(DEFAULT_ENABLED)
            .constant(DEFAULT_CONSTANT)
            .constantEditable(DEFAULT_CONSTANT_EDITABLE)
            .factor(DEFAULT_FACTOR)
            .factorEditable(DEFAULT_FACTOR_EDITABLE)
            .defaultTimespan(DEFAULT_DEFAULT_TIMESPAN);
        return timesheetTask;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimesheetTask createUpdatedEntity(EntityManager em) {
        TimesheetTask timesheetTask = new TimesheetTask()
            .name(UPDATED_NAME)
            .enabled(UPDATED_ENABLED)
            .constant(UPDATED_CONSTANT)
            .constantEditable(UPDATED_CONSTANT_EDITABLE)
            .factor(UPDATED_FACTOR)
            .factorEditable(UPDATED_FACTOR_EDITABLE)
            .defaultTimespan(UPDATED_DEFAULT_TIMESPAN);
        return timesheetTask;
    }

    @BeforeEach
    public void initTest() {
        timesheetTask = createEntity(em);
    }

    @Test
    @Transactional
    void createTimesheetTask() throws Exception {
        int databaseSizeBeforeCreate = timesheetTaskRepository.findAll().size();
        // Create the TimesheetTask
        restTimesheetTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTask)))
            .andExpect(status().isCreated());

        // Validate the TimesheetTask in the database
        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeCreate + 1);
        TimesheetTask testTimesheetTask = timesheetTaskList.get(timesheetTaskList.size() - 1);
        assertThat(testTimesheetTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTimesheetTask.getEnabled()).isEqualTo(DEFAULT_ENABLED);
        assertThat(testTimesheetTask.getConstant()).isEqualTo(DEFAULT_CONSTANT);
        assertThat(testTimesheetTask.getConstantEditable()).isEqualTo(DEFAULT_CONSTANT_EDITABLE);
        assertThat(testTimesheetTask.getFactor()).isEqualByComparingTo(DEFAULT_FACTOR);
        assertThat(testTimesheetTask.getFactorEditable()).isEqualTo(DEFAULT_FACTOR_EDITABLE);
        assertThat(testTimesheetTask.getDefaultTimespan()).isEqualTo(DEFAULT_DEFAULT_TIMESPAN);
    }

    @Test
    @Transactional
    void createTimesheetTaskWithExistingId() throws Exception {
        // Create the TimesheetTask with an existing ID
        timesheetTask.setId(1L);

        int databaseSizeBeforeCreate = timesheetTaskRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimesheetTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTask)))
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTask in the database
        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = timesheetTaskRepository.findAll().size();
        // set the field null
        timesheetTask.setName(null);

        // Create the TimesheetTask, which fails.

        restTimesheetTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTask)))
            .andExpect(status().isBadRequest());

        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEnabledIsRequired() throws Exception {
        int databaseSizeBeforeTest = timesheetTaskRepository.findAll().size();
        // set the field null
        timesheetTask.setEnabled(null);

        // Create the TimesheetTask, which fails.

        restTimesheetTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTask)))
            .andExpect(status().isBadRequest());

        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkConstantIsRequired() throws Exception {
        int databaseSizeBeforeTest = timesheetTaskRepository.findAll().size();
        // set the field null
        timesheetTask.setConstant(null);

        // Create the TimesheetTask, which fails.

        restTimesheetTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTask)))
            .andExpect(status().isBadRequest());

        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkConstantEditableIsRequired() throws Exception {
        int databaseSizeBeforeTest = timesheetTaskRepository.findAll().size();
        // set the field null
        timesheetTask.setConstantEditable(null);

        // Create the TimesheetTask, which fails.

        restTimesheetTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTask)))
            .andExpect(status().isBadRequest());

        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFactorIsRequired() throws Exception {
        int databaseSizeBeforeTest = timesheetTaskRepository.findAll().size();
        // set the field null
        timesheetTask.setFactor(null);

        // Create the TimesheetTask, which fails.

        restTimesheetTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTask)))
            .andExpect(status().isBadRequest());

        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFactorEditableIsRequired() throws Exception {
        int databaseSizeBeforeTest = timesheetTaskRepository.findAll().size();
        // set the field null
        timesheetTask.setFactorEditable(null);

        // Create the TimesheetTask, which fails.

        restTimesheetTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTask)))
            .andExpect(status().isBadRequest());

        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTimesheetTasks() throws Exception {
        // Initialize the database
        timesheetTaskRepository.saveAndFlush(timesheetTask);

        // Get all the timesheetTaskList
        restTimesheetTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timesheetTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].constant").value(hasItem(DEFAULT_CONSTANT)))
            .andExpect(jsonPath("$.[*].constantEditable").value(hasItem(DEFAULT_CONSTANT_EDITABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].factor").value(hasItem(sameNumber(DEFAULT_FACTOR))))
            .andExpect(jsonPath("$.[*].factorEditable").value(hasItem(DEFAULT_FACTOR_EDITABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].defaultTimespan").value(hasItem(DEFAULT_DEFAULT_TIMESPAN)));
    }

    @Test
    @Transactional
    void getTimesheetTask() throws Exception {
        // Initialize the database
        timesheetTaskRepository.saveAndFlush(timesheetTask);

        // Get the timesheetTask
        restTimesheetTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, timesheetTask.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timesheetTask.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.constant").value(DEFAULT_CONSTANT))
            .andExpect(jsonPath("$.constantEditable").value(DEFAULT_CONSTANT_EDITABLE.booleanValue()))
            .andExpect(jsonPath("$.factor").value(sameNumber(DEFAULT_FACTOR)))
            .andExpect(jsonPath("$.factorEditable").value(DEFAULT_FACTOR_EDITABLE.booleanValue()))
            .andExpect(jsonPath("$.defaultTimespan").value(DEFAULT_DEFAULT_TIMESPAN));
    }

    @Test
    @Transactional
    void getNonExistingTimesheetTask() throws Exception {
        // Get the timesheetTask
        restTimesheetTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTimesheetTask() throws Exception {
        // Initialize the database
        timesheetTaskRepository.saveAndFlush(timesheetTask);

        int databaseSizeBeforeUpdate = timesheetTaskRepository.findAll().size();

        // Update the timesheetTask
        TimesheetTask updatedTimesheetTask = timesheetTaskRepository.findById(timesheetTask.getId()).get();
        // Disconnect from session so that the updates on updatedTimesheetTask are not directly saved in db
        em.detach(updatedTimesheetTask);
        updatedTimesheetTask
            .name(UPDATED_NAME)
            .enabled(UPDATED_ENABLED)
            .constant(UPDATED_CONSTANT)
            .constantEditable(UPDATED_CONSTANT_EDITABLE)
            .factor(UPDATED_FACTOR)
            .factorEditable(UPDATED_FACTOR_EDITABLE)
            .defaultTimespan(UPDATED_DEFAULT_TIMESPAN);

        restTimesheetTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTimesheetTask.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTimesheetTask))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetTask in the database
        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeUpdate);
        TimesheetTask testTimesheetTask = timesheetTaskList.get(timesheetTaskList.size() - 1);
        assertThat(testTimesheetTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTimesheetTask.getEnabled()).isEqualTo(UPDATED_ENABLED);
        assertThat(testTimesheetTask.getConstant()).isEqualTo(UPDATED_CONSTANT);
        assertThat(testTimesheetTask.getConstantEditable()).isEqualTo(UPDATED_CONSTANT_EDITABLE);
        assertThat(testTimesheetTask.getFactor()).isEqualByComparingTo(UPDATED_FACTOR);
        assertThat(testTimesheetTask.getFactorEditable()).isEqualTo(UPDATED_FACTOR_EDITABLE);
        assertThat(testTimesheetTask.getDefaultTimespan()).isEqualTo(UPDATED_DEFAULT_TIMESPAN);
    }

    @Test
    @Transactional
    void putNonExistingTimesheetTask() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTaskRepository.findAll().size();
        timesheetTask.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimesheetTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timesheetTask.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timesheetTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTask in the database
        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimesheetTask() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTaskRepository.findAll().size();
        timesheetTask.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timesheetTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTask in the database
        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimesheetTask() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTaskRepository.findAll().size();
        timesheetTask.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetTaskMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timesheetTask)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimesheetTask in the database
        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTimesheetTaskWithPatch() throws Exception {
        // Initialize the database
        timesheetTaskRepository.saveAndFlush(timesheetTask);

        int databaseSizeBeforeUpdate = timesheetTaskRepository.findAll().size();

        // Update the timesheetTask using partial update
        TimesheetTask partialUpdatedTimesheetTask = new TimesheetTask();
        partialUpdatedTimesheetTask.setId(timesheetTask.getId());

        partialUpdatedTimesheetTask.name(UPDATED_NAME).constant(UPDATED_CONSTANT).factor(UPDATED_FACTOR);

        restTimesheetTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimesheetTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimesheetTask))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetTask in the database
        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeUpdate);
        TimesheetTask testTimesheetTask = timesheetTaskList.get(timesheetTaskList.size() - 1);
        assertThat(testTimesheetTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTimesheetTask.getEnabled()).isEqualTo(DEFAULT_ENABLED);
        assertThat(testTimesheetTask.getConstant()).isEqualTo(UPDATED_CONSTANT);
        assertThat(testTimesheetTask.getConstantEditable()).isEqualTo(DEFAULT_CONSTANT_EDITABLE);
        assertThat(testTimesheetTask.getFactor()).isEqualByComparingTo(UPDATED_FACTOR);
        assertThat(testTimesheetTask.getFactorEditable()).isEqualTo(DEFAULT_FACTOR_EDITABLE);
        assertThat(testTimesheetTask.getDefaultTimespan()).isEqualTo(DEFAULT_DEFAULT_TIMESPAN);
    }

    @Test
    @Transactional
    void fullUpdateTimesheetTaskWithPatch() throws Exception {
        // Initialize the database
        timesheetTaskRepository.saveAndFlush(timesheetTask);

        int databaseSizeBeforeUpdate = timesheetTaskRepository.findAll().size();

        // Update the timesheetTask using partial update
        TimesheetTask partialUpdatedTimesheetTask = new TimesheetTask();
        partialUpdatedTimesheetTask.setId(timesheetTask.getId());

        partialUpdatedTimesheetTask
            .name(UPDATED_NAME)
            .enabled(UPDATED_ENABLED)
            .constant(UPDATED_CONSTANT)
            .constantEditable(UPDATED_CONSTANT_EDITABLE)
            .factor(UPDATED_FACTOR)
            .factorEditable(UPDATED_FACTOR_EDITABLE)
            .defaultTimespan(UPDATED_DEFAULT_TIMESPAN);

        restTimesheetTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimesheetTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimesheetTask))
            )
            .andExpect(status().isOk());

        // Validate the TimesheetTask in the database
        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeUpdate);
        TimesheetTask testTimesheetTask = timesheetTaskList.get(timesheetTaskList.size() - 1);
        assertThat(testTimesheetTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTimesheetTask.getEnabled()).isEqualTo(UPDATED_ENABLED);
        assertThat(testTimesheetTask.getConstant()).isEqualTo(UPDATED_CONSTANT);
        assertThat(testTimesheetTask.getConstantEditable()).isEqualTo(UPDATED_CONSTANT_EDITABLE);
        assertThat(testTimesheetTask.getFactor()).isEqualByComparingTo(UPDATED_FACTOR);
        assertThat(testTimesheetTask.getFactorEditable()).isEqualTo(UPDATED_FACTOR_EDITABLE);
        assertThat(testTimesheetTask.getDefaultTimespan()).isEqualTo(UPDATED_DEFAULT_TIMESPAN);
    }

    @Test
    @Transactional
    void patchNonExistingTimesheetTask() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTaskRepository.findAll().size();
        timesheetTask.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimesheetTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timesheetTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timesheetTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTask in the database
        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimesheetTask() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTaskRepository.findAll().size();
        timesheetTask.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timesheetTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimesheetTask in the database
        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimesheetTask() throws Exception {
        int databaseSizeBeforeUpdate = timesheetTaskRepository.findAll().size();
        timesheetTask.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimesheetTaskMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(timesheetTask))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimesheetTask in the database
        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTimesheetTask() throws Exception {
        // Initialize the database
        timesheetTaskRepository.saveAndFlush(timesheetTask);

        int databaseSizeBeforeDelete = timesheetTaskRepository.findAll().size();

        // Delete the timesheetTask
        restTimesheetTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, timesheetTask.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TimesheetTask> timesheetTaskList = timesheetTaskRepository.findAll();
        assertThat(timesheetTaskList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
