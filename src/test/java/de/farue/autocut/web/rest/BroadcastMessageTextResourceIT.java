package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.BroadcastMessage;
import de.farue.autocut.domain.BroadcastMessageText;
import de.farue.autocut.repository.BroadcastMessageTextRepository;
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
 * Integration tests for the {@link BroadcastMessageTextResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class BroadcastMessageTextResourceIT {

    private static final String DEFAULT_LANG_KEY = "AA";
    private static final String UPDATED_LANG_KEY = "BB";

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/broadcast-message-texts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BroadcastMessageTextRepository broadcastMessageTextRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBroadcastMessageTextMockMvc;

    private BroadcastMessageText broadcastMessageText;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BroadcastMessageText createEntity(EntityManager em) {
        BroadcastMessageText broadcastMessageText = new BroadcastMessageText().langKey(DEFAULT_LANG_KEY).text(DEFAULT_TEXT);
        // Add required entity
        BroadcastMessage broadcastMessage;
        if (TestUtil.findAll(em, BroadcastMessage.class).isEmpty()) {
            broadcastMessage = BroadcastMessageResourceIT.createEntity(em);
            em.persist(broadcastMessage);
            em.flush();
        } else {
            broadcastMessage = TestUtil.findAll(em, BroadcastMessage.class).get(0);
        }
        broadcastMessageText.setMessage(broadcastMessage);
        return broadcastMessageText;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BroadcastMessageText createUpdatedEntity(EntityManager em) {
        BroadcastMessageText broadcastMessageText = new BroadcastMessageText().langKey(UPDATED_LANG_KEY).text(UPDATED_TEXT);
        // Add required entity
        BroadcastMessage broadcastMessage;
        if (TestUtil.findAll(em, BroadcastMessage.class).isEmpty()) {
            broadcastMessage = BroadcastMessageResourceIT.createUpdatedEntity(em);
            em.persist(broadcastMessage);
            em.flush();
        } else {
            broadcastMessage = TestUtil.findAll(em, BroadcastMessage.class).get(0);
        }
        broadcastMessageText.setMessage(broadcastMessage);
        return broadcastMessageText;
    }

    @BeforeEach
    public void initTest() {
        broadcastMessageText = createEntity(em);
    }

    @Test
    @Transactional
    void createBroadcastMessageText() throws Exception {
        int databaseSizeBeforeCreate = broadcastMessageTextRepository.findAll().size();
        // Create the BroadcastMessageText
        restBroadcastMessageTextMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessageText))
            )
            .andExpect(status().isCreated());

        // Validate the BroadcastMessageText in the database
        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeCreate + 1);
        BroadcastMessageText testBroadcastMessageText = broadcastMessageTextList.get(broadcastMessageTextList.size() - 1);
        assertThat(testBroadcastMessageText.getLangKey()).isEqualTo(DEFAULT_LANG_KEY);
        assertThat(testBroadcastMessageText.getText()).isEqualTo(DEFAULT_TEXT);
    }

    @Test
    @Transactional
    void createBroadcastMessageTextWithExistingId() throws Exception {
        // Create the BroadcastMessageText with an existing ID
        broadcastMessageText.setId(1L);

        int databaseSizeBeforeCreate = broadcastMessageTextRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBroadcastMessageTextMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessageText))
            )
            .andExpect(status().isBadRequest());

        // Validate the BroadcastMessageText in the database
        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLangKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = broadcastMessageTextRepository.findAll().size();
        // set the field null
        broadcastMessageText.setLangKey(null);

        // Create the BroadcastMessageText, which fails.

        restBroadcastMessageTextMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessageText))
            )
            .andExpect(status().isBadRequest());

        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = broadcastMessageTextRepository.findAll().size();
        // set the field null
        broadcastMessageText.setText(null);

        // Create the BroadcastMessageText, which fails.

        restBroadcastMessageTextMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessageText))
            )
            .andExpect(status().isBadRequest());

        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBroadcastMessageTexts() throws Exception {
        // Initialize the database
        broadcastMessageTextRepository.saveAndFlush(broadcastMessageText);

        // Get all the broadcastMessageTextList
        restBroadcastMessageTextMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(broadcastMessageText.getId().intValue())))
            .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANG_KEY)))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)));
    }

    @Test
    @Transactional
    void getBroadcastMessageText() throws Exception {
        // Initialize the database
        broadcastMessageTextRepository.saveAndFlush(broadcastMessageText);

        // Get the broadcastMessageText
        restBroadcastMessageTextMockMvc
            .perform(get(ENTITY_API_URL_ID, broadcastMessageText.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(broadcastMessageText.getId().intValue()))
            .andExpect(jsonPath("$.langKey").value(DEFAULT_LANG_KEY))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT));
    }

    @Test
    @Transactional
    void getNonExistingBroadcastMessageText() throws Exception {
        // Get the broadcastMessageText
        restBroadcastMessageTextMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBroadcastMessageText() throws Exception {
        // Initialize the database
        broadcastMessageTextRepository.saveAndFlush(broadcastMessageText);

        int databaseSizeBeforeUpdate = broadcastMessageTextRepository.findAll().size();

        // Update the broadcastMessageText
        BroadcastMessageText updatedBroadcastMessageText = broadcastMessageTextRepository.findById(broadcastMessageText.getId()).get();
        // Disconnect from session so that the updates on updatedBroadcastMessageText are not directly saved in db
        em.detach(updatedBroadcastMessageText);
        updatedBroadcastMessageText.langKey(UPDATED_LANG_KEY).text(UPDATED_TEXT);

        restBroadcastMessageTextMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBroadcastMessageText.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBroadcastMessageText))
            )
            .andExpect(status().isOk());

        // Validate the BroadcastMessageText in the database
        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeUpdate);
        BroadcastMessageText testBroadcastMessageText = broadcastMessageTextList.get(broadcastMessageTextList.size() - 1);
        assertThat(testBroadcastMessageText.getLangKey()).isEqualTo(UPDATED_LANG_KEY);
        assertThat(testBroadcastMessageText.getText()).isEqualTo(UPDATED_TEXT);
    }

    @Test
    @Transactional
    void putNonExistingBroadcastMessageText() throws Exception {
        int databaseSizeBeforeUpdate = broadcastMessageTextRepository.findAll().size();
        broadcastMessageText.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBroadcastMessageTextMockMvc
            .perform(
                put(ENTITY_API_URL_ID, broadcastMessageText.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessageText))
            )
            .andExpect(status().isBadRequest());

        // Validate the BroadcastMessageText in the database
        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBroadcastMessageText() throws Exception {
        int databaseSizeBeforeUpdate = broadcastMessageTextRepository.findAll().size();
        broadcastMessageText.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBroadcastMessageTextMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessageText))
            )
            .andExpect(status().isBadRequest());

        // Validate the BroadcastMessageText in the database
        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBroadcastMessageText() throws Exception {
        int databaseSizeBeforeUpdate = broadcastMessageTextRepository.findAll().size();
        broadcastMessageText.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBroadcastMessageTextMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(broadcastMessageText))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BroadcastMessageText in the database
        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBroadcastMessageTextWithPatch() throws Exception {
        // Initialize the database
        broadcastMessageTextRepository.saveAndFlush(broadcastMessageText);

        int databaseSizeBeforeUpdate = broadcastMessageTextRepository.findAll().size();

        // Update the broadcastMessageText using partial update
        BroadcastMessageText partialUpdatedBroadcastMessageText = new BroadcastMessageText();
        partialUpdatedBroadcastMessageText.setId(broadcastMessageText.getId());

        partialUpdatedBroadcastMessageText.text(UPDATED_TEXT);

        restBroadcastMessageTextMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBroadcastMessageText.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBroadcastMessageText))
            )
            .andExpect(status().isOk());

        // Validate the BroadcastMessageText in the database
        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeUpdate);
        BroadcastMessageText testBroadcastMessageText = broadcastMessageTextList.get(broadcastMessageTextList.size() - 1);
        assertThat(testBroadcastMessageText.getLangKey()).isEqualTo(DEFAULT_LANG_KEY);
        assertThat(testBroadcastMessageText.getText()).isEqualTo(UPDATED_TEXT);
    }

    @Test
    @Transactional
    void fullUpdateBroadcastMessageTextWithPatch() throws Exception {
        // Initialize the database
        broadcastMessageTextRepository.saveAndFlush(broadcastMessageText);

        int databaseSizeBeforeUpdate = broadcastMessageTextRepository.findAll().size();

        // Update the broadcastMessageText using partial update
        BroadcastMessageText partialUpdatedBroadcastMessageText = new BroadcastMessageText();
        partialUpdatedBroadcastMessageText.setId(broadcastMessageText.getId());

        partialUpdatedBroadcastMessageText.langKey(UPDATED_LANG_KEY).text(UPDATED_TEXT);

        restBroadcastMessageTextMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBroadcastMessageText.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBroadcastMessageText))
            )
            .andExpect(status().isOk());

        // Validate the BroadcastMessageText in the database
        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeUpdate);
        BroadcastMessageText testBroadcastMessageText = broadcastMessageTextList.get(broadcastMessageTextList.size() - 1);
        assertThat(testBroadcastMessageText.getLangKey()).isEqualTo(UPDATED_LANG_KEY);
        assertThat(testBroadcastMessageText.getText()).isEqualTo(UPDATED_TEXT);
    }

    @Test
    @Transactional
    void patchNonExistingBroadcastMessageText() throws Exception {
        int databaseSizeBeforeUpdate = broadcastMessageTextRepository.findAll().size();
        broadcastMessageText.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBroadcastMessageTextMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, broadcastMessageText.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessageText))
            )
            .andExpect(status().isBadRequest());

        // Validate the BroadcastMessageText in the database
        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBroadcastMessageText() throws Exception {
        int databaseSizeBeforeUpdate = broadcastMessageTextRepository.findAll().size();
        broadcastMessageText.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBroadcastMessageTextMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessageText))
            )
            .andExpect(status().isBadRequest());

        // Validate the BroadcastMessageText in the database
        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBroadcastMessageText() throws Exception {
        int databaseSizeBeforeUpdate = broadcastMessageTextRepository.findAll().size();
        broadcastMessageText.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBroadcastMessageTextMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(broadcastMessageText))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BroadcastMessageText in the database
        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBroadcastMessageText() throws Exception {
        // Initialize the database
        broadcastMessageTextRepository.saveAndFlush(broadcastMessageText);

        int databaseSizeBeforeDelete = broadcastMessageTextRepository.findAll().size();

        // Delete the broadcastMessageText
        restBroadcastMessageTextMockMvc
            .perform(delete(ENTITY_API_URL_ID, broadcastMessageText.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BroadcastMessageText> broadcastMessageTextList = broadcastMessageTextRepository.findAll();
        assertThat(broadcastMessageTextList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
