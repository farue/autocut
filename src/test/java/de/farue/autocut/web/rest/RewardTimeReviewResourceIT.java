package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.RewardTimeReview;
import de.farue.autocut.domain.TimesheetTime;
import de.farue.autocut.domain.enumeration.RewardTimeReviewStatus;
import de.farue.autocut.repository.RewardTimeReviewRepository;
import de.farue.autocut.security.AuthoritiesConstants;
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
 * Integration tests for the {@link RewardTimeReviewResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class RewardTimeReviewResourceIT {

    private static final RewardTimeReviewStatus DEFAULT_STATUS = RewardTimeReviewStatus.OK;
    private static final RewardTimeReviewStatus UPDATED_STATUS = RewardTimeReviewStatus.NOK;

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/reward-time-reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RewardTimeReviewRepository rewardTimeReviewRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRewardTimeReviewMockMvc;

    private RewardTimeReview rewardTimeReview;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RewardTimeReview createEntity(EntityManager em) {
        RewardTimeReview rewardTimeReview = new RewardTimeReview().status(DEFAULT_STATUS).comment(DEFAULT_COMMENT);
        // Add required entity
        TimesheetTime timesheetTime;
        if (TestUtil.findAll(em, TimesheetTime.class).isEmpty()) {
            timesheetTime = TimesheetTimeResourceIT.createEntity(em);
            em.persist(timesheetTime);
            em.flush();
        } else {
            timesheetTime = TestUtil.findAll(em, TimesheetTime.class).get(0);
        }
        rewardTimeReview.setTimesheetTime(timesheetTime);
        return rewardTimeReview;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RewardTimeReview createUpdatedEntity(EntityManager em) {
        RewardTimeReview rewardTimeReview = new RewardTimeReview().status(UPDATED_STATUS).comment(UPDATED_COMMENT);
        // Add required entity
        TimesheetTime timesheetTime;
        if (TestUtil.findAll(em, TimesheetTime.class).isEmpty()) {
            timesheetTime = TimesheetTimeResourceIT.createUpdatedEntity(em);
            em.persist(timesheetTime);
            em.flush();
        } else {
            timesheetTime = TestUtil.findAll(em, TimesheetTime.class).get(0);
        }
        rewardTimeReview.setTimesheetTime(timesheetTime);
        return rewardTimeReview;
    }

    @BeforeEach
    public void initTest() {
        rewardTimeReview = createEntity(em);
    }

    @Test
    @Transactional
    void createRewardTimeReview() throws Exception {
        int databaseSizeBeforeCreate = rewardTimeReviewRepository.findAll().size();
        // Create the RewardTimeReview
        restRewardTimeReviewMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rewardTimeReview))
            )
            .andExpect(status().isCreated());

        // Validate the RewardTimeReview in the database
        List<RewardTimeReview> rewardTimeReviewList = rewardTimeReviewRepository.findAll();
        assertThat(rewardTimeReviewList).hasSize(databaseSizeBeforeCreate + 1);
        RewardTimeReview testRewardTimeReview = rewardTimeReviewList.get(rewardTimeReviewList.size() - 1);
        assertThat(testRewardTimeReview.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testRewardTimeReview.getComment()).isEqualTo(DEFAULT_COMMENT);
    }

    @Test
    @Transactional
    void createRewardTimeReviewWithExistingId() throws Exception {
        // Create the RewardTimeReview with an existing ID
        rewardTimeReview.setId(1L);

        int databaseSizeBeforeCreate = rewardTimeReviewRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRewardTimeReviewMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rewardTimeReview))
            )
            .andExpect(status().isBadRequest());

        // Validate the RewardTimeReview in the database
        List<RewardTimeReview> rewardTimeReviewList = rewardTimeReviewRepository.findAll();
        assertThat(rewardTimeReviewList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRewardTimeReviews() throws Exception {
        // Initialize the database
        rewardTimeReviewRepository.saveAndFlush(rewardTimeReview);

        // Get all the rewardTimeReviewList
        restRewardTimeReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rewardTimeReview.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)));
    }

    @Test
    @Transactional
    void getRewardTimeReview() throws Exception {
        // Initialize the database
        rewardTimeReviewRepository.saveAndFlush(rewardTimeReview);

        // Get the rewardTimeReview
        restRewardTimeReviewMockMvc
            .perform(get(ENTITY_API_URL_ID, rewardTimeReview.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rewardTimeReview.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT));
    }

    @Test
    @Transactional
    void getNonExistingRewardTimeReview() throws Exception {
        // Get the rewardTimeReview
        restRewardTimeReviewMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRewardTimeReview() throws Exception {
        // Initialize the database
        rewardTimeReviewRepository.saveAndFlush(rewardTimeReview);

        int databaseSizeBeforeUpdate = rewardTimeReviewRepository.findAll().size();

        // Update the rewardTimeReview
        RewardTimeReview updatedRewardTimeReview = rewardTimeReviewRepository.findById(rewardTimeReview.getId()).get();
        // Disconnect from session so that the updates on updatedRewardTimeReview are not directly saved in db
        em.detach(updatedRewardTimeReview);
        updatedRewardTimeReview.status(UPDATED_STATUS).comment(UPDATED_COMMENT);

        restRewardTimeReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRewardTimeReview.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRewardTimeReview))
            )
            .andExpect(status().isOk());

        // Validate the RewardTimeReview in the database
        List<RewardTimeReview> rewardTimeReviewList = rewardTimeReviewRepository.findAll();
        assertThat(rewardTimeReviewList).hasSize(databaseSizeBeforeUpdate);
        RewardTimeReview testRewardTimeReview = rewardTimeReviewList.get(rewardTimeReviewList.size() - 1);
        assertThat(testRewardTimeReview.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testRewardTimeReview.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void putNonExistingRewardTimeReview() throws Exception {
        int databaseSizeBeforeUpdate = rewardTimeReviewRepository.findAll().size();
        rewardTimeReview.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRewardTimeReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rewardTimeReview.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rewardTimeReview))
            )
            .andExpect(status().isBadRequest());

        // Validate the RewardTimeReview in the database
        List<RewardTimeReview> rewardTimeReviewList = rewardTimeReviewRepository.findAll();
        assertThat(rewardTimeReviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRewardTimeReview() throws Exception {
        int databaseSizeBeforeUpdate = rewardTimeReviewRepository.findAll().size();
        rewardTimeReview.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRewardTimeReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rewardTimeReview))
            )
            .andExpect(status().isBadRequest());

        // Validate the RewardTimeReview in the database
        List<RewardTimeReview> rewardTimeReviewList = rewardTimeReviewRepository.findAll();
        assertThat(rewardTimeReviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRewardTimeReview() throws Exception {
        int databaseSizeBeforeUpdate = rewardTimeReviewRepository.findAll().size();
        rewardTimeReview.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRewardTimeReviewMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rewardTimeReview))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RewardTimeReview in the database
        List<RewardTimeReview> rewardTimeReviewList = rewardTimeReviewRepository.findAll();
        assertThat(rewardTimeReviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRewardTimeReviewWithPatch() throws Exception {
        // Initialize the database
        rewardTimeReviewRepository.saveAndFlush(rewardTimeReview);

        int databaseSizeBeforeUpdate = rewardTimeReviewRepository.findAll().size();

        // Update the rewardTimeReview using partial update
        RewardTimeReview partialUpdatedRewardTimeReview = new RewardTimeReview();
        partialUpdatedRewardTimeReview.setId(rewardTimeReview.getId());

        partialUpdatedRewardTimeReview.status(UPDATED_STATUS).comment(UPDATED_COMMENT);

        restRewardTimeReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRewardTimeReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRewardTimeReview))
            )
            .andExpect(status().isOk());

        // Validate the RewardTimeReview in the database
        List<RewardTimeReview> rewardTimeReviewList = rewardTimeReviewRepository.findAll();
        assertThat(rewardTimeReviewList).hasSize(databaseSizeBeforeUpdate);
        RewardTimeReview testRewardTimeReview = rewardTimeReviewList.get(rewardTimeReviewList.size() - 1);
        assertThat(testRewardTimeReview.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testRewardTimeReview.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void fullUpdateRewardTimeReviewWithPatch() throws Exception {
        // Initialize the database
        rewardTimeReviewRepository.saveAndFlush(rewardTimeReview);

        int databaseSizeBeforeUpdate = rewardTimeReviewRepository.findAll().size();

        // Update the rewardTimeReview using partial update
        RewardTimeReview partialUpdatedRewardTimeReview = new RewardTimeReview();
        partialUpdatedRewardTimeReview.setId(rewardTimeReview.getId());

        partialUpdatedRewardTimeReview.status(UPDATED_STATUS).comment(UPDATED_COMMENT);

        restRewardTimeReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRewardTimeReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRewardTimeReview))
            )
            .andExpect(status().isOk());

        // Validate the RewardTimeReview in the database
        List<RewardTimeReview> rewardTimeReviewList = rewardTimeReviewRepository.findAll();
        assertThat(rewardTimeReviewList).hasSize(databaseSizeBeforeUpdate);
        RewardTimeReview testRewardTimeReview = rewardTimeReviewList.get(rewardTimeReviewList.size() - 1);
        assertThat(testRewardTimeReview.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testRewardTimeReview.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void patchNonExistingRewardTimeReview() throws Exception {
        int databaseSizeBeforeUpdate = rewardTimeReviewRepository.findAll().size();
        rewardTimeReview.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRewardTimeReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rewardTimeReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rewardTimeReview))
            )
            .andExpect(status().isBadRequest());

        // Validate the RewardTimeReview in the database
        List<RewardTimeReview> rewardTimeReviewList = rewardTimeReviewRepository.findAll();
        assertThat(rewardTimeReviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRewardTimeReview() throws Exception {
        int databaseSizeBeforeUpdate = rewardTimeReviewRepository.findAll().size();
        rewardTimeReview.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRewardTimeReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rewardTimeReview))
            )
            .andExpect(status().isBadRequest());

        // Validate the RewardTimeReview in the database
        List<RewardTimeReview> rewardTimeReviewList = rewardTimeReviewRepository.findAll();
        assertThat(rewardTimeReviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRewardTimeReview() throws Exception {
        int databaseSizeBeforeUpdate = rewardTimeReviewRepository.findAll().size();
        rewardTimeReview.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRewardTimeReviewMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rewardTimeReview))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RewardTimeReview in the database
        List<RewardTimeReview> rewardTimeReviewList = rewardTimeReviewRepository.findAll();
        assertThat(rewardTimeReviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRewardTimeReview() throws Exception {
        // Initialize the database
        rewardTimeReviewRepository.saveAndFlush(rewardTimeReview);

        int databaseSizeBeforeDelete = rewardTimeReviewRepository.findAll().size();

        // Delete the rewardTimeReview
        restRewardTimeReviewMockMvc
            .perform(delete(ENTITY_API_URL_ID, rewardTimeReview.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RewardTimeReview> rewardTimeReviewList = rewardTimeReviewRepository.findAll();
        assertThat(rewardTimeReviewList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
