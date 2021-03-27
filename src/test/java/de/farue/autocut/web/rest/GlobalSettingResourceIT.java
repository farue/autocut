package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.GlobalSetting;
import de.farue.autocut.repository.GlobalSettingRepository;
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
 * Integration tests for the {@link GlobalSettingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GlobalSettingResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/global-settings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GlobalSettingRepository globalSettingRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGlobalSettingMockMvc;

    private GlobalSetting globalSetting;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GlobalSetting createEntity(EntityManager em) {
        GlobalSetting globalSetting = new GlobalSetting().key(DEFAULT_KEY).value(DEFAULT_VALUE).valueType(DEFAULT_VALUE_TYPE);
        return globalSetting;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GlobalSetting createUpdatedEntity(EntityManager em) {
        GlobalSetting globalSetting = new GlobalSetting().key(UPDATED_KEY).value(UPDATED_VALUE).valueType(UPDATED_VALUE_TYPE);
        return globalSetting;
    }

    @BeforeEach
    public void initTest() {
        globalSetting = createEntity(em);
    }

    @Test
    @Transactional
    void createGlobalSetting() throws Exception {
        int databaseSizeBeforeCreate = globalSettingRepository.findAll().size();
        // Create the GlobalSetting
        restGlobalSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(globalSetting)))
            .andExpect(status().isCreated());

        // Validate the GlobalSetting in the database
        List<GlobalSetting> globalSettingList = globalSettingRepository.findAll();
        assertThat(globalSettingList).hasSize(databaseSizeBeforeCreate + 1);
        GlobalSetting testGlobalSetting = globalSettingList.get(globalSettingList.size() - 1);
        assertThat(testGlobalSetting.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testGlobalSetting.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testGlobalSetting.getValueType()).isEqualTo(DEFAULT_VALUE_TYPE);
    }

    @Test
    @Transactional
    void createGlobalSettingWithExistingId() throws Exception {
        // Create the GlobalSetting with an existing ID
        globalSetting.setId(1L);

        int databaseSizeBeforeCreate = globalSettingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGlobalSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(globalSetting)))
            .andExpect(status().isBadRequest());

        // Validate the GlobalSetting in the database
        List<GlobalSetting> globalSettingList = globalSettingRepository.findAll();
        assertThat(globalSettingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllGlobalSettings() throws Exception {
        // Initialize the database
        globalSettingRepository.saveAndFlush(globalSetting);

        // Get all the globalSettingList
        restGlobalSettingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(globalSetting.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].valueType").value(hasItem(DEFAULT_VALUE_TYPE)));
    }

    @Test
    @Transactional
    void getGlobalSetting() throws Exception {
        // Initialize the database
        globalSettingRepository.saveAndFlush(globalSetting);

        // Get the globalSetting
        restGlobalSettingMockMvc
            .perform(get(ENTITY_API_URL_ID, globalSetting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(globalSetting.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.valueType").value(DEFAULT_VALUE_TYPE));
    }

    @Test
    @Transactional
    void getNonExistingGlobalSetting() throws Exception {
        // Get the globalSetting
        restGlobalSettingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGlobalSetting() throws Exception {
        // Initialize the database
        globalSettingRepository.saveAndFlush(globalSetting);

        int databaseSizeBeforeUpdate = globalSettingRepository.findAll().size();

        // Update the globalSetting
        GlobalSetting updatedGlobalSetting = globalSettingRepository.findById(globalSetting.getId()).get();
        // Disconnect from session so that the updates on updatedGlobalSetting are not directly saved in db
        em.detach(updatedGlobalSetting);
        updatedGlobalSetting.key(UPDATED_KEY).value(UPDATED_VALUE).valueType(UPDATED_VALUE_TYPE);

        restGlobalSettingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGlobalSetting.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGlobalSetting))
            )
            .andExpect(status().isOk());

        // Validate the GlobalSetting in the database
        List<GlobalSetting> globalSettingList = globalSettingRepository.findAll();
        assertThat(globalSettingList).hasSize(databaseSizeBeforeUpdate);
        GlobalSetting testGlobalSetting = globalSettingList.get(globalSettingList.size() - 1);
        assertThat(testGlobalSetting.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testGlobalSetting.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testGlobalSetting.getValueType()).isEqualTo(UPDATED_VALUE_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingGlobalSetting() throws Exception {
        int databaseSizeBeforeUpdate = globalSettingRepository.findAll().size();
        globalSetting.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGlobalSettingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, globalSetting.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(globalSetting))
            )
            .andExpect(status().isBadRequest());

        // Validate the GlobalSetting in the database
        List<GlobalSetting> globalSettingList = globalSettingRepository.findAll();
        assertThat(globalSettingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGlobalSetting() throws Exception {
        int databaseSizeBeforeUpdate = globalSettingRepository.findAll().size();
        globalSetting.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGlobalSettingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(globalSetting))
            )
            .andExpect(status().isBadRequest());

        // Validate the GlobalSetting in the database
        List<GlobalSetting> globalSettingList = globalSettingRepository.findAll();
        assertThat(globalSettingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGlobalSetting() throws Exception {
        int databaseSizeBeforeUpdate = globalSettingRepository.findAll().size();
        globalSetting.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGlobalSettingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(globalSetting)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GlobalSetting in the database
        List<GlobalSetting> globalSettingList = globalSettingRepository.findAll();
        assertThat(globalSettingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGlobalSettingWithPatch() throws Exception {
        // Initialize the database
        globalSettingRepository.saveAndFlush(globalSetting);

        int databaseSizeBeforeUpdate = globalSettingRepository.findAll().size();

        // Update the globalSetting using partial update
        GlobalSetting partialUpdatedGlobalSetting = new GlobalSetting();
        partialUpdatedGlobalSetting.setId(globalSetting.getId());

        partialUpdatedGlobalSetting.value(UPDATED_VALUE);

        restGlobalSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGlobalSetting.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGlobalSetting))
            )
            .andExpect(status().isOk());

        // Validate the GlobalSetting in the database
        List<GlobalSetting> globalSettingList = globalSettingRepository.findAll();
        assertThat(globalSettingList).hasSize(databaseSizeBeforeUpdate);
        GlobalSetting testGlobalSetting = globalSettingList.get(globalSettingList.size() - 1);
        assertThat(testGlobalSetting.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testGlobalSetting.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testGlobalSetting.getValueType()).isEqualTo(DEFAULT_VALUE_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateGlobalSettingWithPatch() throws Exception {
        // Initialize the database
        globalSettingRepository.saveAndFlush(globalSetting);

        int databaseSizeBeforeUpdate = globalSettingRepository.findAll().size();

        // Update the globalSetting using partial update
        GlobalSetting partialUpdatedGlobalSetting = new GlobalSetting();
        partialUpdatedGlobalSetting.setId(globalSetting.getId());

        partialUpdatedGlobalSetting.key(UPDATED_KEY).value(UPDATED_VALUE).valueType(UPDATED_VALUE_TYPE);

        restGlobalSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGlobalSetting.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGlobalSetting))
            )
            .andExpect(status().isOk());

        // Validate the GlobalSetting in the database
        List<GlobalSetting> globalSettingList = globalSettingRepository.findAll();
        assertThat(globalSettingList).hasSize(databaseSizeBeforeUpdate);
        GlobalSetting testGlobalSetting = globalSettingList.get(globalSettingList.size() - 1);
        assertThat(testGlobalSetting.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testGlobalSetting.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testGlobalSetting.getValueType()).isEqualTo(UPDATED_VALUE_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingGlobalSetting() throws Exception {
        int databaseSizeBeforeUpdate = globalSettingRepository.findAll().size();
        globalSetting.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGlobalSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, globalSetting.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(globalSetting))
            )
            .andExpect(status().isBadRequest());

        // Validate the GlobalSetting in the database
        List<GlobalSetting> globalSettingList = globalSettingRepository.findAll();
        assertThat(globalSettingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGlobalSetting() throws Exception {
        int databaseSizeBeforeUpdate = globalSettingRepository.findAll().size();
        globalSetting.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGlobalSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(globalSetting))
            )
            .andExpect(status().isBadRequest());

        // Validate the GlobalSetting in the database
        List<GlobalSetting> globalSettingList = globalSettingRepository.findAll();
        assertThat(globalSettingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGlobalSetting() throws Exception {
        int databaseSizeBeforeUpdate = globalSettingRepository.findAll().size();
        globalSetting.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGlobalSettingMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(globalSetting))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GlobalSetting in the database
        List<GlobalSetting> globalSettingList = globalSettingRepository.findAll();
        assertThat(globalSettingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGlobalSetting() throws Exception {
        // Initialize the database
        globalSettingRepository.saveAndFlush(globalSetting);

        int databaseSizeBeforeDelete = globalSettingRepository.findAll().size();

        // Delete the globalSetting
        restGlobalSettingMockMvc
            .perform(delete(ENTITY_API_URL_ID, globalSetting.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GlobalSetting> globalSettingList = globalSettingRepository.findAll();
        assertThat(globalSettingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
