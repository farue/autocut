package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.BroadcastMessage;
import de.farue.autocut.domain.enumeration.BroadcastMessageType;
import de.farue.autocut.repository.BroadcastMessageRepository;
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
 * Integration tests for the {@link BroadcastMessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class BroadcastMessageResourceIT {

    private static final BroadcastMessageType DEFAULT_TYPE = BroadcastMessageType.PRIMARY;
    private static final BroadcastMessageType UPDATED_TYPE = BroadcastMessageType.SECONDARY;

    private static final Instant DEFAULT_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_USERS_ONLY = false;
    private static final Boolean UPDATED_USERS_ONLY = true;

    private static final Boolean DEFAULT_DISMISSIBLE = false;
    private static final Boolean UPDATED_DISMISSIBLE = true;

    private static final String ENTITY_API_URL = "/api/broadcast-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BroadcastMessageRepository broadcastMessageRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBroadcastMessageMockMvc;

    private BroadcastMessage broadcastMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BroadcastMessage createEntity(EntityManager em) {
        BroadcastMessage broadcastMessage = new BroadcastMessage()
            .type(DEFAULT_TYPE)
            .start(DEFAULT_START)
            .end(DEFAULT_END)
            .usersOnly(DEFAULT_USERS_ONLY)
            .dismissible(DEFAULT_DISMISSIBLE);
        return broadcastMessage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BroadcastMessage createUpdatedEntity(EntityManager em) {
        BroadcastMessage broadcastMessage = new BroadcastMessage()
            .type(UPDATED_TYPE)
            .start(UPDATED_START)
            .end(UPDATED_END)
            .usersOnly(UPDATED_USERS_ONLY)
            .dismissible(UPDATED_DISMISSIBLE);
        return broadcastMessage;
    }

    @BeforeEach
    public void initTest() {
        broadcastMessage = createEntity(em);
    }

    @Test
    @Transactional
    void createBroadcastMessage() throws Exception {
        int databaseSizeBeforeCreate = broadcastMessageRepository.findAll().size();
        // Create the BroadcastMessage
        restBroadcastMessageMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(broadcastMessage))
            )
            .andExpect(status().isCreated());

        // Validate the BroadcastMessage in the database
        List<BroadcastMessage> broadcastMessageList = broadcastMessageRepository.findAll();
        assertThat(broadcastMessageList).hasSize(databaseSizeBeforeCreate + 1);
        BroadcastMessage testBroadcastMessage = broadcastMessageList.get(broadcastMessageList.size() - 1);
        assertThat(testBroadcastMessage.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testBroadcastMessage.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testBroadcastMessage.getEnd()).isEqualTo(DEFAULT_END);
        assertThat(testBroadcastMessage.getUsersOnly()).isEqualTo(DEFAULT_USERS_ONLY);
        assertThat(testBroadcastMessage.getDismissible()).isEqualTo(DEFAULT_DISMISSIBLE);
    }

    @Test
    @Transactional
    void createBroadcastMessageWithExistingId() throws Exception {
        // Create the BroadcastMessage with an existing ID
        broadcastMessage.setId(1L);

        int databaseSizeBeforeCreate = broadcastMessageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBroadcastMessageMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(broadcastMessage))
            )
            .andExpect(status().isBadRequest());

        // Validate the BroadcastMessage in the database
        List<BroadcastMessage> broadcastMessageList = broadcastMessageRepository.findAll();
        assertThat(broadcastMessageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = broadcastMessageRepository.findAll().size();
        // set the field null
        broadcastMessage.setType(null);

        // Create the BroadcastMessage, which fails.

        restBroadcastMessageMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(broadcastMessage))
            )
            .andExpect(status().isBadRequest());

        List<BroadcastMessage> broadcastMessageList = broadcastMessageRepository.findAll();
        assertThat(broadcastMessageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBroadcastMessages() throws Exception {
        // Initialize the database
        broadcastMessageRepository.saveAndFlush(broadcastMessage);

        // Get all the broadcastMessageList
        restBroadcastMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(broadcastMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())))
            .andExpect(jsonPath("$.[*].usersOnly").value(hasItem(DEFAULT_USERS_ONLY.booleanValue())))
            .andExpect(jsonPath("$.[*].dismissible").value(hasItem(DEFAULT_DISMISSIBLE.booleanValue())));
    }

    @Test
    @Transactional
    void getBroadcastMessage() throws Exception {
        // Initialize the database
        broadcastMessageRepository.saveAndFlush(broadcastMessage);

        // Get the broadcastMessage
        restBroadcastMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, broadcastMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(broadcastMessage.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.end").value(DEFAULT_END.toString()))
            .andExpect(jsonPath("$.usersOnly").value(DEFAULT_USERS_ONLY.booleanValue()))
            .andExpect(jsonPath("$.dismissible").value(DEFAULT_DISMISSIBLE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingBroadcastMessage() throws Exception {
        // Get the broadcastMessage
        restBroadcastMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBroadcastMessage() throws Exception {
        // Initialize the database
        broadcastMessageRepository.saveAndFlush(broadcastMessage);

        int databaseSizeBeforeUpdate = broadcastMessageRepository.findAll().size();

        // Update the broadcastMessage
        BroadcastMessage updatedBroadcastMessage = broadcastMessageRepository.findById(broadcastMessage.getId()).get();
        // Disconnect from session so that the updates on updatedBroadcastMessage are not directly saved in db
        em.detach(updatedBroadcastMessage);
        updatedBroadcastMessage
            .type(UPDATED_TYPE)
            .start(UPDATED_START)
            .end(UPDATED_END)
            .usersOnly(UPDATED_USERS_ONLY)
            .dismissible(UPDATED_DISMISSIBLE);

        restBroadcastMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBroadcastMessage.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBroadcastMessage))
            )
            .andExpect(status().isOk());

        // Validate the BroadcastMessage in the database
        List<BroadcastMessage> broadcastMessageList = broadcastMessageRepository.findAll();
        assertThat(broadcastMessageList).hasSize(databaseSizeBeforeUpdate);
        BroadcastMessage testBroadcastMessage = broadcastMessageList.get(broadcastMessageList.size() - 1);
        assertThat(testBroadcastMessage.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBroadcastMessage.getStart()).isEqualTo(UPDATED_START);
        assertThat(testBroadcastMessage.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testBroadcastMessage.getUsersOnly()).isEqualTo(UPDATED_USERS_ONLY);
        assertThat(testBroadcastMessage.getDismissible()).isEqualTo(UPDATED_DISMISSIBLE);
    }

    @Test
    @Transactional
    void putNonExistingBroadcastMessage() throws Exception {
        int databaseSizeBeforeUpdate = broadcastMessageRepository.findAll().size();
        broadcastMessage.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBroadcastMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, broadcastMessage.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessage))
            )
            .andExpect(status().isBadRequest());

        // Validate the BroadcastMessage in the database
        List<BroadcastMessage> broadcastMessageList = broadcastMessageRepository.findAll();
        assertThat(broadcastMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBroadcastMessage() throws Exception {
        int databaseSizeBeforeUpdate = broadcastMessageRepository.findAll().size();
        broadcastMessage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBroadcastMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessage))
            )
            .andExpect(status().isBadRequest());

        // Validate the BroadcastMessage in the database
        List<BroadcastMessage> broadcastMessageList = broadcastMessageRepository.findAll();
        assertThat(broadcastMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBroadcastMessage() throws Exception {
        int databaseSizeBeforeUpdate = broadcastMessageRepository.findAll().size();
        broadcastMessage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBroadcastMessageMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(broadcastMessage))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BroadcastMessage in the database
        List<BroadcastMessage> broadcastMessageList = broadcastMessageRepository.findAll();
        assertThat(broadcastMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBroadcastMessageWithPatch() throws Exception {
        // Initialize the database
        broadcastMessageRepository.saveAndFlush(broadcastMessage);

        int databaseSizeBeforeUpdate = broadcastMessageRepository.findAll().size();

        // Update the broadcastMessage using partial update
        BroadcastMessage partialUpdatedBroadcastMessage = new BroadcastMessage();
        partialUpdatedBroadcastMessage.setId(broadcastMessage.getId());

        partialUpdatedBroadcastMessage.type(UPDATED_TYPE).usersOnly(UPDATED_USERS_ONLY).dismissible(UPDATED_DISMISSIBLE);

        restBroadcastMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBroadcastMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBroadcastMessage))
            )
            .andExpect(status().isOk());

        // Validate the BroadcastMessage in the database
        List<BroadcastMessage> broadcastMessageList = broadcastMessageRepository.findAll();
        assertThat(broadcastMessageList).hasSize(databaseSizeBeforeUpdate);
        BroadcastMessage testBroadcastMessage = broadcastMessageList.get(broadcastMessageList.size() - 1);
        assertThat(testBroadcastMessage.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBroadcastMessage.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testBroadcastMessage.getEnd()).isEqualTo(DEFAULT_END);
        assertThat(testBroadcastMessage.getUsersOnly()).isEqualTo(UPDATED_USERS_ONLY);
        assertThat(testBroadcastMessage.getDismissible()).isEqualTo(UPDATED_DISMISSIBLE);
    }

    @Test
    @Transactional
    void fullUpdateBroadcastMessageWithPatch() throws Exception {
        // Initialize the database
        broadcastMessageRepository.saveAndFlush(broadcastMessage);

        int databaseSizeBeforeUpdate = broadcastMessageRepository.findAll().size();

        // Update the broadcastMessage using partial update
        BroadcastMessage partialUpdatedBroadcastMessage = new BroadcastMessage();
        partialUpdatedBroadcastMessage.setId(broadcastMessage.getId());

        partialUpdatedBroadcastMessage
            .type(UPDATED_TYPE)
            .start(UPDATED_START)
            .end(UPDATED_END)
            .usersOnly(UPDATED_USERS_ONLY)
            .dismissible(UPDATED_DISMISSIBLE);

        restBroadcastMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBroadcastMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBroadcastMessage))
            )
            .andExpect(status().isOk());

        // Validate the BroadcastMessage in the database
        List<BroadcastMessage> broadcastMessageList = broadcastMessageRepository.findAll();
        assertThat(broadcastMessageList).hasSize(databaseSizeBeforeUpdate);
        BroadcastMessage testBroadcastMessage = broadcastMessageList.get(broadcastMessageList.size() - 1);
        assertThat(testBroadcastMessage.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBroadcastMessage.getStart()).isEqualTo(UPDATED_START);
        assertThat(testBroadcastMessage.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testBroadcastMessage.getUsersOnly()).isEqualTo(UPDATED_USERS_ONLY);
        assertThat(testBroadcastMessage.getDismissible()).isEqualTo(UPDATED_DISMISSIBLE);
    }

    @Test
    @Transactional
    void patchNonExistingBroadcastMessage() throws Exception {
        int databaseSizeBeforeUpdate = broadcastMessageRepository.findAll().size();
        broadcastMessage.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBroadcastMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, broadcastMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessage))
            )
            .andExpect(status().isBadRequest());

        // Validate the BroadcastMessage in the database
        List<BroadcastMessage> broadcastMessageList = broadcastMessageRepository.findAll();
        assertThat(broadcastMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBroadcastMessage() throws Exception {
        int databaseSizeBeforeUpdate = broadcastMessageRepository.findAll().size();
        broadcastMessage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBroadcastMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessage))
            )
            .andExpect(status().isBadRequest());

        // Validate the BroadcastMessage in the database
        List<BroadcastMessage> broadcastMessageList = broadcastMessageRepository.findAll();
        assertThat(broadcastMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBroadcastMessage() throws Exception {
        int databaseSizeBeforeUpdate = broadcastMessageRepository.findAll().size();
        broadcastMessage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBroadcastMessageMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessage))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BroadcastMessage in the database
        List<BroadcastMessage> broadcastMessageList = broadcastMessageRepository.findAll();
        assertThat(broadcastMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBroadcastMessage() throws Exception {
        // Initialize the database
        broadcastMessageRepository.saveAndFlush(broadcastMessage);

        int databaseSizeBeforeDelete = broadcastMessageRepository.findAll().size();

        // Delete the broadcastMessage
        restBroadcastMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, broadcastMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BroadcastMessage> broadcastMessageList = broadcastMessageRepository.findAll();
        assertThat(broadcastMessageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
