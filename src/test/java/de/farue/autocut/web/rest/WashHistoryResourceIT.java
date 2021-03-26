package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.WashHistory;
import de.farue.autocut.domain.enumeration.WashHistoryStatus;
import de.farue.autocut.repository.WashHistoryRepository;
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
 * Integration tests for the {@link WashHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WashHistoryResourceIT {

    private static final Instant DEFAULT_USING_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_USING_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_RESERVATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESERVATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final WashHistoryStatus DEFAULT_STATUS = WashHistoryStatus.OPEN;
    private static final WashHistoryStatus UPDATED_STATUS = WashHistoryStatus.EXPIRED;

    private static final String ENTITY_API_URL = "/api/wash-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WashHistoryRepository washHistoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWashHistoryMockMvc;

    private WashHistory washHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WashHistory createEntity(EntityManager em) {
        WashHistory washHistory = new WashHistory()
            .usingDate(DEFAULT_USING_DATE)
            .reservationDate(DEFAULT_RESERVATION_DATE)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .status(DEFAULT_STATUS);
        return washHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WashHistory createUpdatedEntity(EntityManager em) {
        WashHistory washHistory = new WashHistory()
            .usingDate(UPDATED_USING_DATE)
            .reservationDate(UPDATED_RESERVATION_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .status(UPDATED_STATUS);
        return washHistory;
    }

    @BeforeEach
    public void initTest() {
        washHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createWashHistory() throws Exception {
        int databaseSizeBeforeCreate = washHistoryRepository.findAll().size();
        // Create the WashHistory
        restWashHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(washHistory)))
            .andExpect(status().isCreated());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        WashHistory testWashHistory = washHistoryList.get(washHistoryList.size() - 1);
        assertThat(testWashHistory.getUsingDate()).isEqualTo(DEFAULT_USING_DATE);
        assertThat(testWashHistory.getReservationDate()).isEqualTo(DEFAULT_RESERVATION_DATE);
        assertThat(testWashHistory.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
        assertThat(testWashHistory.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createWashHistoryWithExistingId() throws Exception {
        // Create the WashHistory with an existing ID
        washHistory.setId(1L);

        int databaseSizeBeforeCreate = washHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWashHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(washHistory)))
            .andExpect(status().isBadRequest());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWashHistories() throws Exception {
        // Initialize the database
        washHistoryRepository.saveAndFlush(washHistory);

        // Get all the washHistoryList
        restWashHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(washHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].usingDate").value(hasItem(DEFAULT_USING_DATE.toString())))
            .andExpect(jsonPath("$.[*].reservationDate").value(hasItem(DEFAULT_RESERVATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getWashHistory() throws Exception {
        // Initialize the database
        washHistoryRepository.saveAndFlush(washHistory);

        // Get the washHistory
        restWashHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, washHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(washHistory.getId().intValue()))
            .andExpect(jsonPath("$.usingDate").value(DEFAULT_USING_DATE.toString()))
            .andExpect(jsonPath("$.reservationDate").value(DEFAULT_RESERVATION_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingWashHistory() throws Exception {
        // Get the washHistory
        restWashHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewWashHistory() throws Exception {
        // Initialize the database
        washHistoryRepository.saveAndFlush(washHistory);

        int databaseSizeBeforeUpdate = washHistoryRepository.findAll().size();

        // Update the washHistory
        WashHistory updatedWashHistory = washHistoryRepository.findById(washHistory.getId()).get();
        // Disconnect from session so that the updates on updatedWashHistory are not directly saved in db
        em.detach(updatedWashHistory);
        updatedWashHistory
            .usingDate(UPDATED_USING_DATE)
            .reservationDate(UPDATED_RESERVATION_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .status(UPDATED_STATUS);

        restWashHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWashHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedWashHistory))
            )
            .andExpect(status().isOk());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeUpdate);
        WashHistory testWashHistory = washHistoryList.get(washHistoryList.size() - 1);
        assertThat(testWashHistory.getUsingDate()).isEqualTo(UPDATED_USING_DATE);
        assertThat(testWashHistory.getReservationDate()).isEqualTo(UPDATED_RESERVATION_DATE);
        assertThat(testWashHistory.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
        assertThat(testWashHistory.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingWashHistory() throws Exception {
        int databaseSizeBeforeUpdate = washHistoryRepository.findAll().size();
        washHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWashHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, washHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(washHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWashHistory() throws Exception {
        int databaseSizeBeforeUpdate = washHistoryRepository.findAll().size();
        washHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWashHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(washHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWashHistory() throws Exception {
        int databaseSizeBeforeUpdate = washHistoryRepository.findAll().size();
        washHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWashHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(washHistory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWashHistoryWithPatch() throws Exception {
        // Initialize the database
        washHistoryRepository.saveAndFlush(washHistory);

        int databaseSizeBeforeUpdate = washHistoryRepository.findAll().size();

        // Update the washHistory using partial update
        WashHistory partialUpdatedWashHistory = new WashHistory();
        partialUpdatedWashHistory.setId(washHistory.getId());

        partialUpdatedWashHistory.reservationDate(UPDATED_RESERVATION_DATE);

        restWashHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWashHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWashHistory))
            )
            .andExpect(status().isOk());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeUpdate);
        WashHistory testWashHistory = washHistoryList.get(washHistoryList.size() - 1);
        assertThat(testWashHistory.getUsingDate()).isEqualTo(DEFAULT_USING_DATE);
        assertThat(testWashHistory.getReservationDate()).isEqualTo(UPDATED_RESERVATION_DATE);
        assertThat(testWashHistory.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
        assertThat(testWashHistory.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateWashHistoryWithPatch() throws Exception {
        // Initialize the database
        washHistoryRepository.saveAndFlush(washHistory);

        int databaseSizeBeforeUpdate = washHistoryRepository.findAll().size();

        // Update the washHistory using partial update
        WashHistory partialUpdatedWashHistory = new WashHistory();
        partialUpdatedWashHistory.setId(washHistory.getId());

        partialUpdatedWashHistory
            .usingDate(UPDATED_USING_DATE)
            .reservationDate(UPDATED_RESERVATION_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .status(UPDATED_STATUS);

        restWashHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWashHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWashHistory))
            )
            .andExpect(status().isOk());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeUpdate);
        WashHistory testWashHistory = washHistoryList.get(washHistoryList.size() - 1);
        assertThat(testWashHistory.getUsingDate()).isEqualTo(UPDATED_USING_DATE);
        assertThat(testWashHistory.getReservationDate()).isEqualTo(UPDATED_RESERVATION_DATE);
        assertThat(testWashHistory.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
        assertThat(testWashHistory.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingWashHistory() throws Exception {
        int databaseSizeBeforeUpdate = washHistoryRepository.findAll().size();
        washHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWashHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, washHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(washHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWashHistory() throws Exception {
        int databaseSizeBeforeUpdate = washHistoryRepository.findAll().size();
        washHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWashHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(washHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWashHistory() throws Exception {
        int databaseSizeBeforeUpdate = washHistoryRepository.findAll().size();
        washHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWashHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(washHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWashHistory() throws Exception {
        // Initialize the database
        washHistoryRepository.saveAndFlush(washHistory);

        int databaseSizeBeforeDelete = washHistoryRepository.findAll().size();

        // Delete the washHistory
        restWashHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, washHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
