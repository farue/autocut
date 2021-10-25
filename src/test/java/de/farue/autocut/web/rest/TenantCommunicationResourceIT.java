package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.TenantCommunication;
import de.farue.autocut.repository.TenantCommunicationRepository;
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
 * Integration tests for the {@link TenantCommunicationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TenantCommunicationResourceIT {

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/tenant-communications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TenantCommunicationRepository tenantCommunicationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTenantCommunicationMockMvc;

    private TenantCommunication tenantCommunication;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TenantCommunication createEntity(EntityManager em) {
        TenantCommunication tenantCommunication = new TenantCommunication()
            .subject(DEFAULT_SUBJECT)
            .text(DEFAULT_TEXT)
            .note(DEFAULT_NOTE)
            .date(DEFAULT_DATE);
        return tenantCommunication;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TenantCommunication createUpdatedEntity(EntityManager em) {
        TenantCommunication tenantCommunication = new TenantCommunication()
            .subject(UPDATED_SUBJECT)
            .text(UPDATED_TEXT)
            .note(UPDATED_NOTE)
            .date(UPDATED_DATE);
        return tenantCommunication;
    }

    @BeforeEach
    public void initTest() {
        tenantCommunication = createEntity(em);
    }

    @Test
    @Transactional
    void createTenantCommunication() throws Exception {
        int databaseSizeBeforeCreate = tenantCommunicationRepository.findAll().size();
        // Create the TenantCommunication
        restTenantCommunicationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenantCommunication))
            )
            .andExpect(status().isCreated());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeCreate + 1);
        TenantCommunication testTenantCommunication = tenantCommunicationList.get(tenantCommunicationList.size() - 1);
        assertThat(testTenantCommunication.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testTenantCommunication.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testTenantCommunication.getNote()).isEqualTo(DEFAULT_NOTE);
        assertThat(testTenantCommunication.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void createTenantCommunicationWithExistingId() throws Exception {
        // Create the TenantCommunication with an existing ID
        tenantCommunication.setId(1L);

        int databaseSizeBeforeCreate = tenantCommunicationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTenantCommunicationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenantCommunication))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSubjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = tenantCommunicationRepository.findAll().size();
        // set the field null
        tenantCommunication.setSubject(null);

        // Create the TenantCommunication, which fails.

        restTenantCommunicationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenantCommunication))
            )
            .andExpect(status().isBadRequest());

        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = tenantCommunicationRepository.findAll().size();
        // set the field null
        tenantCommunication.setDate(null);

        // Create the TenantCommunication, which fails.

        restTenantCommunicationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenantCommunication))
            )
            .andExpect(status().isBadRequest());

        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTenantCommunications() throws Exception {
        // Initialize the database
        tenantCommunicationRepository.saveAndFlush(tenantCommunication);

        // Get all the tenantCommunicationList
        restTenantCommunicationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenantCommunication.getId().intValue())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getTenantCommunication() throws Exception {
        // Initialize the database
        tenantCommunicationRepository.saveAndFlush(tenantCommunication);

        // Get the tenantCommunication
        restTenantCommunicationMockMvc
            .perform(get(ENTITY_API_URL_ID, tenantCommunication.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tenantCommunication.getId().intValue()))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTenantCommunication() throws Exception {
        // Get the tenantCommunication
        restTenantCommunicationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTenantCommunication() throws Exception {
        // Initialize the database
        tenantCommunicationRepository.saveAndFlush(tenantCommunication);

        int databaseSizeBeforeUpdate = tenantCommunicationRepository.findAll().size();

        // Update the tenantCommunication
        TenantCommunication updatedTenantCommunication = tenantCommunicationRepository.findById(tenantCommunication.getId()).get();
        // Disconnect from session so that the updates on updatedTenantCommunication are not directly saved in db
        em.detach(updatedTenantCommunication);
        updatedTenantCommunication.subject(UPDATED_SUBJECT).text(UPDATED_TEXT).note(UPDATED_NOTE).date(UPDATED_DATE);

        restTenantCommunicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTenantCommunication.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTenantCommunication))
            )
            .andExpect(status().isOk());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeUpdate);
        TenantCommunication testTenantCommunication = tenantCommunicationList.get(tenantCommunicationList.size() - 1);
        assertThat(testTenantCommunication.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testTenantCommunication.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testTenantCommunication.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testTenantCommunication.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingTenantCommunication() throws Exception {
        int databaseSizeBeforeUpdate = tenantCommunicationRepository.findAll().size();
        tenantCommunication.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantCommunicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tenantCommunication.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tenantCommunication))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTenantCommunication() throws Exception {
        int databaseSizeBeforeUpdate = tenantCommunicationRepository.findAll().size();
        tenantCommunication.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantCommunicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tenantCommunication))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTenantCommunication() throws Exception {
        int databaseSizeBeforeUpdate = tenantCommunicationRepository.findAll().size();
        tenantCommunication.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantCommunicationMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenantCommunication))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTenantCommunicationWithPatch() throws Exception {
        // Initialize the database
        tenantCommunicationRepository.saveAndFlush(tenantCommunication);

        int databaseSizeBeforeUpdate = tenantCommunicationRepository.findAll().size();

        // Update the tenantCommunication using partial update
        TenantCommunication partialUpdatedTenantCommunication = new TenantCommunication();
        partialUpdatedTenantCommunication.setId(tenantCommunication.getId());

        partialUpdatedTenantCommunication.text(UPDATED_TEXT).date(UPDATED_DATE);

        restTenantCommunicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenantCommunication.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTenantCommunication))
            )
            .andExpect(status().isOk());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeUpdate);
        TenantCommunication testTenantCommunication = tenantCommunicationList.get(tenantCommunicationList.size() - 1);
        assertThat(testTenantCommunication.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testTenantCommunication.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testTenantCommunication.getNote()).isEqualTo(DEFAULT_NOTE);
        assertThat(testTenantCommunication.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateTenantCommunicationWithPatch() throws Exception {
        // Initialize the database
        tenantCommunicationRepository.saveAndFlush(tenantCommunication);

        int databaseSizeBeforeUpdate = tenantCommunicationRepository.findAll().size();

        // Update the tenantCommunication using partial update
        TenantCommunication partialUpdatedTenantCommunication = new TenantCommunication();
        partialUpdatedTenantCommunication.setId(tenantCommunication.getId());

        partialUpdatedTenantCommunication.subject(UPDATED_SUBJECT).text(UPDATED_TEXT).note(UPDATED_NOTE).date(UPDATED_DATE);

        restTenantCommunicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenantCommunication.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTenantCommunication))
            )
            .andExpect(status().isOk());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeUpdate);
        TenantCommunication testTenantCommunication = tenantCommunicationList.get(tenantCommunicationList.size() - 1);
        assertThat(testTenantCommunication.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testTenantCommunication.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testTenantCommunication.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testTenantCommunication.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingTenantCommunication() throws Exception {
        int databaseSizeBeforeUpdate = tenantCommunicationRepository.findAll().size();
        tenantCommunication.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantCommunicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tenantCommunication.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tenantCommunication))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTenantCommunication() throws Exception {
        int databaseSizeBeforeUpdate = tenantCommunicationRepository.findAll().size();
        tenantCommunication.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantCommunicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tenantCommunication))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTenantCommunication() throws Exception {
        int databaseSizeBeforeUpdate = tenantCommunicationRepository.findAll().size();
        tenantCommunication.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantCommunicationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tenantCommunication))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTenantCommunication() throws Exception {
        // Initialize the database
        tenantCommunicationRepository.saveAndFlush(tenantCommunication);

        int databaseSizeBeforeDelete = tenantCommunicationRepository.findAll().size();

        // Delete the tenantCommunication
        restTenantCommunicationMockMvc
            .perform(delete(ENTITY_API_URL_ID, tenantCommunication.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
