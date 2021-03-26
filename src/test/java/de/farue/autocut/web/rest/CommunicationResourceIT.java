package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.Communication;
import de.farue.autocut.repository.CommunicationRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link CommunicationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CommunicationResourceIT {

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/communications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CommunicationRepository communicationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommunicationMockMvc;

    private Communication communication;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Communication createEntity(EntityManager em) {
        Communication communication = new Communication().subject(DEFAULT_SUBJECT).text(DEFAULT_TEXT).note(DEFAULT_NOTE).date(DEFAULT_DATE);
        return communication;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Communication createUpdatedEntity(EntityManager em) {
        Communication communication = new Communication().subject(UPDATED_SUBJECT).text(UPDATED_TEXT).note(UPDATED_NOTE).date(UPDATED_DATE);
        return communication;
    }

    @BeforeEach
    public void initTest() {
        communication = createEntity(em);
    }

    @Test
    @Transactional
    void createCommunication() throws Exception {
        int databaseSizeBeforeCreate = communicationRepository.findAll().size();
        // Create the Communication
        restCommunicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(communication)))
            .andExpect(status().isCreated());

        // Validate the Communication in the database
        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeCreate + 1);
        Communication testCommunication = communicationList.get(communicationList.size() - 1);
        assertThat(testCommunication.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testCommunication.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testCommunication.getNote()).isEqualTo(DEFAULT_NOTE);
        assertThat(testCommunication.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void createCommunicationWithExistingId() throws Exception {
        // Create the Communication with an existing ID
        communication.setId(1L);

        int databaseSizeBeforeCreate = communicationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommunicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(communication)))
            .andExpect(status().isBadRequest());

        // Validate the Communication in the database
        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSubjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = communicationRepository.findAll().size();
        // set the field null
        communication.setSubject(null);

        // Create the Communication, which fails.

        restCommunicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(communication)))
            .andExpect(status().isBadRequest());

        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = communicationRepository.findAll().size();
        // set the field null
        communication.setDate(null);

        // Create the Communication, which fails.

        restCommunicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(communication)))
            .andExpect(status().isBadRequest());

        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCommunications() throws Exception {
        // Initialize the database
        communicationRepository.saveAndFlush(communication);

        // Get all the communicationList
        restCommunicationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(communication.getId().intValue())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getCommunication() throws Exception {
        // Initialize the database
        communicationRepository.saveAndFlush(communication);

        // Get the communication
        restCommunicationMockMvc
            .perform(get(ENTITY_API_URL_ID, communication.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(communication.getId().intValue()))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCommunication() throws Exception {
        // Get the communication
        restCommunicationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCommunication() throws Exception {
        // Initialize the database
        communicationRepository.saveAndFlush(communication);

        int databaseSizeBeforeUpdate = communicationRepository.findAll().size();

        // Update the communication
        Communication updatedCommunication = communicationRepository.findById(communication.getId()).get();
        // Disconnect from session so that the updates on updatedCommunication are not directly saved in db
        em.detach(updatedCommunication);
        updatedCommunication.subject(UPDATED_SUBJECT).text(UPDATED_TEXT).note(UPDATED_NOTE).date(UPDATED_DATE);

        restCommunicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCommunication.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCommunication))
            )
            .andExpect(status().isOk());

        // Validate the Communication in the database
        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeUpdate);
        Communication testCommunication = communicationList.get(communicationList.size() - 1);
        assertThat(testCommunication.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testCommunication.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testCommunication.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testCommunication.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingCommunication() throws Exception {
        int databaseSizeBeforeUpdate = communicationRepository.findAll().size();
        communication.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommunicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, communication.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(communication))
            )
            .andExpect(status().isBadRequest());

        // Validate the Communication in the database
        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCommunication() throws Exception {
        int databaseSizeBeforeUpdate = communicationRepository.findAll().size();
        communication.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(communication))
            )
            .andExpect(status().isBadRequest());

        // Validate the Communication in the database
        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCommunication() throws Exception {
        int databaseSizeBeforeUpdate = communicationRepository.findAll().size();
        communication.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunicationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(communication)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Communication in the database
        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommunicationWithPatch() throws Exception {
        // Initialize the database
        communicationRepository.saveAndFlush(communication);

        int databaseSizeBeforeUpdate = communicationRepository.findAll().size();

        // Update the communication using partial update
        Communication partialUpdatedCommunication = new Communication();
        partialUpdatedCommunication.setId(communication.getId());

        partialUpdatedCommunication.subject(UPDATED_SUBJECT).text(UPDATED_TEXT).note(UPDATED_NOTE).date(UPDATED_DATE);

        restCommunicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommunication.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommunication))
            )
            .andExpect(status().isOk());

        // Validate the Communication in the database
        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeUpdate);
        Communication testCommunication = communicationList.get(communicationList.size() - 1);
        assertThat(testCommunication.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testCommunication.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testCommunication.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testCommunication.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateCommunicationWithPatch() throws Exception {
        // Initialize the database
        communicationRepository.saveAndFlush(communication);

        int databaseSizeBeforeUpdate = communicationRepository.findAll().size();

        // Update the communication using partial update
        Communication partialUpdatedCommunication = new Communication();
        partialUpdatedCommunication.setId(communication.getId());

        partialUpdatedCommunication.subject(UPDATED_SUBJECT).text(UPDATED_TEXT).note(UPDATED_NOTE).date(UPDATED_DATE);

        restCommunicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommunication.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommunication))
            )
            .andExpect(status().isOk());

        // Validate the Communication in the database
        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeUpdate);
        Communication testCommunication = communicationList.get(communicationList.size() - 1);
        assertThat(testCommunication.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testCommunication.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testCommunication.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testCommunication.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingCommunication() throws Exception {
        int databaseSizeBeforeUpdate = communicationRepository.findAll().size();
        communication.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommunicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, communication.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(communication))
            )
            .andExpect(status().isBadRequest());

        // Validate the Communication in the database
        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCommunication() throws Exception {
        int databaseSizeBeforeUpdate = communicationRepository.findAll().size();
        communication.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(communication))
            )
            .andExpect(status().isBadRequest());

        // Validate the Communication in the database
        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCommunication() throws Exception {
        int databaseSizeBeforeUpdate = communicationRepository.findAll().size();
        communication.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunicationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(communication))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Communication in the database
        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCommunication() throws Exception {
        // Initialize the database
        communicationRepository.saveAndFlush(communication);

        int databaseSizeBeforeDelete = communicationRepository.findAll().size();

        // Delete the communication
        restCommunicationMockMvc
            .perform(delete(ENTITY_API_URL_ID, communication.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Communication> communicationList = communicationRepository.findAll();
        assertThat(communicationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
