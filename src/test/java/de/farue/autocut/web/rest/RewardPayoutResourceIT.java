package de.farue.autocut.web.rest;

import static de.farue.autocut.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.RewardPayout;
import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.repository.RewardPayoutRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import java.math.BigDecimal;
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
 * Integration tests for the {@link RewardPayoutResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class RewardPayoutResourceIT {

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final Integer DEFAULT_TIME = 1;
    private static final Integer UPDATED_TIME = 2;

    private static final String ENTITY_API_URL = "/api/reward-payouts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RewardPayoutRepository rewardPayoutRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRewardPayoutMockMvc;

    private RewardPayout rewardPayout;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RewardPayout createEntity(EntityManager em) {
        RewardPayout rewardPayout = new RewardPayout().timestamp(DEFAULT_TIMESTAMP).amount(DEFAULT_AMOUNT).time(DEFAULT_TIME);
        // Add required entity
        Timesheet timesheet;
        if (TestUtil.findAll(em, Timesheet.class).isEmpty()) {
            timesheet = TimesheetResourceIT.createEntity(em);
            em.persist(timesheet);
            em.flush();
        } else {
            timesheet = TestUtil.findAll(em, Timesheet.class).get(0);
        }
        rewardPayout.setTimesheet(timesheet);
        return rewardPayout;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RewardPayout createUpdatedEntity(EntityManager em) {
        RewardPayout rewardPayout = new RewardPayout().timestamp(UPDATED_TIMESTAMP).amount(UPDATED_AMOUNT).time(UPDATED_TIME);
        // Add required entity
        Timesheet timesheet;
        if (TestUtil.findAll(em, Timesheet.class).isEmpty()) {
            timesheet = TimesheetResourceIT.createUpdatedEntity(em);
            em.persist(timesheet);
            em.flush();
        } else {
            timesheet = TestUtil.findAll(em, Timesheet.class).get(0);
        }
        rewardPayout.setTimesheet(timesheet);
        return rewardPayout;
    }

    @BeforeEach
    public void initTest() {
        rewardPayout = createEntity(em);
    }

    @Test
    @Transactional
    void createRewardPayout() throws Exception {
        int databaseSizeBeforeCreate = rewardPayoutRepository.findAll().size();
        // Create the RewardPayout
        restRewardPayoutMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rewardPayout)))
            .andExpect(status().isCreated());

        // Validate the RewardPayout in the database
        List<RewardPayout> rewardPayoutList = rewardPayoutRepository.findAll();
        assertThat(rewardPayoutList).hasSize(databaseSizeBeforeCreate + 1);
        RewardPayout testRewardPayout = rewardPayoutList.get(rewardPayoutList.size() - 1);
        assertThat(testRewardPayout.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testRewardPayout.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testRewardPayout.getTime()).isEqualTo(DEFAULT_TIME);
    }

    @Test
    @Transactional
    void createRewardPayoutWithExistingId() throws Exception {
        // Create the RewardPayout with an existing ID
        rewardPayout.setId(1L);

        int databaseSizeBeforeCreate = rewardPayoutRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRewardPayoutMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rewardPayout)))
            .andExpect(status().isBadRequest());

        // Validate the RewardPayout in the database
        List<RewardPayout> rewardPayoutList = rewardPayoutRepository.findAll();
        assertThat(rewardPayoutList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRewardPayouts() throws Exception {
        // Initialize the database
        rewardPayoutRepository.saveAndFlush(rewardPayout);

        // Get all the rewardPayoutList
        restRewardPayoutMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rewardPayout.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME)));
    }

    @Test
    @Transactional
    void getRewardPayout() throws Exception {
        // Initialize the database
        rewardPayoutRepository.saveAndFlush(rewardPayout);

        // Get the rewardPayout
        restRewardPayoutMockMvc
            .perform(get(ENTITY_API_URL_ID, rewardPayout.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rewardPayout.getId().intValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.time").value(DEFAULT_TIME));
    }

    @Test
    @Transactional
    void getNonExistingRewardPayout() throws Exception {
        // Get the rewardPayout
        restRewardPayoutMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRewardPayout() throws Exception {
        // Initialize the database
        rewardPayoutRepository.saveAndFlush(rewardPayout);

        int databaseSizeBeforeUpdate = rewardPayoutRepository.findAll().size();

        // Update the rewardPayout
        RewardPayout updatedRewardPayout = rewardPayoutRepository.findById(rewardPayout.getId()).get();
        // Disconnect from session so that the updates on updatedRewardPayout are not directly saved in db
        em.detach(updatedRewardPayout);
        updatedRewardPayout.timestamp(UPDATED_TIMESTAMP).amount(UPDATED_AMOUNT).time(UPDATED_TIME);

        restRewardPayoutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRewardPayout.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRewardPayout))
            )
            .andExpect(status().isOk());

        // Validate the RewardPayout in the database
        List<RewardPayout> rewardPayoutList = rewardPayoutRepository.findAll();
        assertThat(rewardPayoutList).hasSize(databaseSizeBeforeUpdate);
        RewardPayout testRewardPayout = rewardPayoutList.get(rewardPayoutList.size() - 1);
        assertThat(testRewardPayout.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testRewardPayout.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testRewardPayout.getTime()).isEqualTo(UPDATED_TIME);
    }

    @Test
    @Transactional
    void putNonExistingRewardPayout() throws Exception {
        int databaseSizeBeforeUpdate = rewardPayoutRepository.findAll().size();
        rewardPayout.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRewardPayoutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rewardPayout.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rewardPayout))
            )
            .andExpect(status().isBadRequest());

        // Validate the RewardPayout in the database
        List<RewardPayout> rewardPayoutList = rewardPayoutRepository.findAll();
        assertThat(rewardPayoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRewardPayout() throws Exception {
        int databaseSizeBeforeUpdate = rewardPayoutRepository.findAll().size();
        rewardPayout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRewardPayoutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rewardPayout))
            )
            .andExpect(status().isBadRequest());

        // Validate the RewardPayout in the database
        List<RewardPayout> rewardPayoutList = rewardPayoutRepository.findAll();
        assertThat(rewardPayoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRewardPayout() throws Exception {
        int databaseSizeBeforeUpdate = rewardPayoutRepository.findAll().size();
        rewardPayout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRewardPayoutMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rewardPayout)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RewardPayout in the database
        List<RewardPayout> rewardPayoutList = rewardPayoutRepository.findAll();
        assertThat(rewardPayoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRewardPayoutWithPatch() throws Exception {
        // Initialize the database
        rewardPayoutRepository.saveAndFlush(rewardPayout);

        int databaseSizeBeforeUpdate = rewardPayoutRepository.findAll().size();

        // Update the rewardPayout using partial update
        RewardPayout partialUpdatedRewardPayout = new RewardPayout();
        partialUpdatedRewardPayout.setId(rewardPayout.getId());

        partialUpdatedRewardPayout.amount(UPDATED_AMOUNT);

        restRewardPayoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRewardPayout.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRewardPayout))
            )
            .andExpect(status().isOk());

        // Validate the RewardPayout in the database
        List<RewardPayout> rewardPayoutList = rewardPayoutRepository.findAll();
        assertThat(rewardPayoutList).hasSize(databaseSizeBeforeUpdate);
        RewardPayout testRewardPayout = rewardPayoutList.get(rewardPayoutList.size() - 1);
        assertThat(testRewardPayout.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testRewardPayout.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testRewardPayout.getTime()).isEqualTo(DEFAULT_TIME);
    }

    @Test
    @Transactional
    void fullUpdateRewardPayoutWithPatch() throws Exception {
        // Initialize the database
        rewardPayoutRepository.saveAndFlush(rewardPayout);

        int databaseSizeBeforeUpdate = rewardPayoutRepository.findAll().size();

        // Update the rewardPayout using partial update
        RewardPayout partialUpdatedRewardPayout = new RewardPayout();
        partialUpdatedRewardPayout.setId(rewardPayout.getId());

        partialUpdatedRewardPayout.timestamp(UPDATED_TIMESTAMP).amount(UPDATED_AMOUNT).time(UPDATED_TIME);

        restRewardPayoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRewardPayout.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRewardPayout))
            )
            .andExpect(status().isOk());

        // Validate the RewardPayout in the database
        List<RewardPayout> rewardPayoutList = rewardPayoutRepository.findAll();
        assertThat(rewardPayoutList).hasSize(databaseSizeBeforeUpdate);
        RewardPayout testRewardPayout = rewardPayoutList.get(rewardPayoutList.size() - 1);
        assertThat(testRewardPayout.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testRewardPayout.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testRewardPayout.getTime()).isEqualTo(UPDATED_TIME);
    }

    @Test
    @Transactional
    void patchNonExistingRewardPayout() throws Exception {
        int databaseSizeBeforeUpdate = rewardPayoutRepository.findAll().size();
        rewardPayout.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRewardPayoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rewardPayout.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rewardPayout))
            )
            .andExpect(status().isBadRequest());

        // Validate the RewardPayout in the database
        List<RewardPayout> rewardPayoutList = rewardPayoutRepository.findAll();
        assertThat(rewardPayoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRewardPayout() throws Exception {
        int databaseSizeBeforeUpdate = rewardPayoutRepository.findAll().size();
        rewardPayout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRewardPayoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rewardPayout))
            )
            .andExpect(status().isBadRequest());

        // Validate the RewardPayout in the database
        List<RewardPayout> rewardPayoutList = rewardPayoutRepository.findAll();
        assertThat(rewardPayoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRewardPayout() throws Exception {
        int databaseSizeBeforeUpdate = rewardPayoutRepository.findAll().size();
        rewardPayout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRewardPayoutMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(rewardPayout))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RewardPayout in the database
        List<RewardPayout> rewardPayoutList = rewardPayoutRepository.findAll();
        assertThat(rewardPayoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRewardPayout() throws Exception {
        // Initialize the database
        rewardPayoutRepository.saveAndFlush(rewardPayout);

        int databaseSizeBeforeDelete = rewardPayoutRepository.findAll().size();

        // Delete the rewardPayout
        restRewardPayoutMockMvc
            .perform(delete(ENTITY_API_URL_ID, rewardPayout.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RewardPayout> rewardPayoutList = rewardPayoutRepository.findAll();
        assertThat(rewardPayoutList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
