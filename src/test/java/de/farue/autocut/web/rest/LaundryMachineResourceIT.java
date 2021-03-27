package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.domain.enumeration.LaundryMachineType;
import de.farue.autocut.repository.LaundryMachineRepository;
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
 * Integration tests for the {@link LaundryMachineResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LaundryMachineResourceIT {

    private static final String DEFAULT_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFIER = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LaundryMachineType DEFAULT_TYPE = LaundryMachineType.WASHING_MACHINE;
    private static final LaundryMachineType UPDATED_TYPE = LaundryMachineType.DRYER;

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final String ENTITY_API_URL = "/api/laundry-machines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LaundryMachineRepository laundryMachineRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLaundryMachineMockMvc;

    private LaundryMachine laundryMachine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaundryMachine createEntity(EntityManager em) {
        LaundryMachine laundryMachine = new LaundryMachine()
            .identifier(DEFAULT_IDENTIFIER)
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .enabled(DEFAULT_ENABLED);
        return laundryMachine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaundryMachine createUpdatedEntity(EntityManager em) {
        LaundryMachine laundryMachine = new LaundryMachine()
            .identifier(UPDATED_IDENTIFIER)
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .enabled(UPDATED_ENABLED);
        return laundryMachine;
    }

    @BeforeEach
    public void initTest() {
        laundryMachine = createEntity(em);
    }

    @Test
    @Transactional
    void createLaundryMachine() throws Exception {
        int databaseSizeBeforeCreate = laundryMachineRepository.findAll().size();
        // Create the LaundryMachine
        restLaundryMachineMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(laundryMachine))
            )
            .andExpect(status().isCreated());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeCreate + 1);
        LaundryMachine testLaundryMachine = laundryMachineList.get(laundryMachineList.size() - 1);
        assertThat(testLaundryMachine.getIdentifier()).isEqualTo(DEFAULT_IDENTIFIER);
        assertThat(testLaundryMachine.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLaundryMachine.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testLaundryMachine.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    @Transactional
    void createLaundryMachineWithExistingId() throws Exception {
        // Create the LaundryMachine with an existing ID
        laundryMachine.setId(1L);

        int databaseSizeBeforeCreate = laundryMachineRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLaundryMachineMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(laundryMachine))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIdentifierIsRequired() throws Exception {
        int databaseSizeBeforeTest = laundryMachineRepository.findAll().size();
        // set the field null
        laundryMachine.setIdentifier(null);

        // Create the LaundryMachine, which fails.

        restLaundryMachineMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(laundryMachine))
            )
            .andExpect(status().isBadRequest());

        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = laundryMachineRepository.findAll().size();
        // set the field null
        laundryMachine.setName(null);

        // Create the LaundryMachine, which fails.

        restLaundryMachineMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(laundryMachine))
            )
            .andExpect(status().isBadRequest());

        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = laundryMachineRepository.findAll().size();
        // set the field null
        laundryMachine.setType(null);

        // Create the LaundryMachine, which fails.

        restLaundryMachineMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(laundryMachine))
            )
            .andExpect(status().isBadRequest());

        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEnabledIsRequired() throws Exception {
        int databaseSizeBeforeTest = laundryMachineRepository.findAll().size();
        // set the field null
        laundryMachine.setEnabled(null);

        // Create the LaundryMachine, which fails.

        restLaundryMachineMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(laundryMachine))
            )
            .andExpect(status().isBadRequest());

        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLaundryMachines() throws Exception {
        // Initialize the database
        laundryMachineRepository.saveAndFlush(laundryMachine);

        // Get all the laundryMachineList
        restLaundryMachineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&enabled=false"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(laundryMachine.getId().intValue())))
            .andExpect(jsonPath("$.[*].identifier").value(hasItem(DEFAULT_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())));
    }

    @Test
    @Transactional
    void getLaundryMachine() throws Exception {
        // Initialize the database
        laundryMachineRepository.saveAndFlush(laundryMachine);

        // Get the laundryMachine
        restLaundryMachineMockMvc
            .perform(get(ENTITY_API_URL_ID, laundryMachine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(laundryMachine.getId().intValue()))
            .andExpect(jsonPath("$.identifier").value(DEFAULT_IDENTIFIER))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingLaundryMachine() throws Exception {
        // Get the laundryMachine
        restLaundryMachineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLaundryMachine() throws Exception {
        // Initialize the database
        laundryMachineRepository.saveAndFlush(laundryMachine);

        int databaseSizeBeforeUpdate = laundryMachineRepository.findAll().size();

        // Update the laundryMachine
        LaundryMachine updatedLaundryMachine = laundryMachineRepository.findById(laundryMachine.getId()).get();
        // Disconnect from session so that the updates on updatedLaundryMachine are not directly saved in db
        em.detach(updatedLaundryMachine);
        updatedLaundryMachine.identifier(UPDATED_IDENTIFIER).name(UPDATED_NAME).type(UPDATED_TYPE).enabled(UPDATED_ENABLED);

        restLaundryMachineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLaundryMachine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLaundryMachine))
            )
            .andExpect(status().isOk());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeUpdate);
        LaundryMachine testLaundryMachine = laundryMachineList.get(laundryMachineList.size() - 1);
        assertThat(testLaundryMachine.getIdentifier()).isEqualTo(UPDATED_IDENTIFIER);
        assertThat(testLaundryMachine.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLaundryMachine.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testLaundryMachine.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    @Transactional
    void putNonExistingLaundryMachine() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineRepository.findAll().size();
        laundryMachine.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaundryMachineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, laundryMachine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(laundryMachine))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLaundryMachine() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineRepository.findAll().size();
        laundryMachine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaundryMachineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(laundryMachine))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLaundryMachine() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineRepository.findAll().size();
        laundryMachine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaundryMachineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(laundryMachine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLaundryMachineWithPatch() throws Exception {
        // Initialize the database
        laundryMachineRepository.saveAndFlush(laundryMachine);

        int databaseSizeBeforeUpdate = laundryMachineRepository.findAll().size();

        // Update the laundryMachine using partial update
        LaundryMachine partialUpdatedLaundryMachine = new LaundryMachine();
        partialUpdatedLaundryMachine.setId(laundryMachine.getId());

        partialUpdatedLaundryMachine.name(UPDATED_NAME).enabled(UPDATED_ENABLED);

        restLaundryMachineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLaundryMachine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLaundryMachine))
            )
            .andExpect(status().isOk());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeUpdate);
        LaundryMachine testLaundryMachine = laundryMachineList.get(laundryMachineList.size() - 1);
        assertThat(testLaundryMachine.getIdentifier()).isEqualTo(DEFAULT_IDENTIFIER);
        assertThat(testLaundryMachine.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLaundryMachine.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testLaundryMachine.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    @Transactional
    void fullUpdateLaundryMachineWithPatch() throws Exception {
        // Initialize the database
        laundryMachineRepository.saveAndFlush(laundryMachine);

        int databaseSizeBeforeUpdate = laundryMachineRepository.findAll().size();

        // Update the laundryMachine using partial update
        LaundryMachine partialUpdatedLaundryMachine = new LaundryMachine();
        partialUpdatedLaundryMachine.setId(laundryMachine.getId());

        partialUpdatedLaundryMachine.identifier(UPDATED_IDENTIFIER).name(UPDATED_NAME).type(UPDATED_TYPE).enabled(UPDATED_ENABLED);

        restLaundryMachineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLaundryMachine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLaundryMachine))
            )
            .andExpect(status().isOk());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeUpdate);
        LaundryMachine testLaundryMachine = laundryMachineList.get(laundryMachineList.size() - 1);
        assertThat(testLaundryMachine.getIdentifier()).isEqualTo(UPDATED_IDENTIFIER);
        assertThat(testLaundryMachine.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLaundryMachine.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testLaundryMachine.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    @Transactional
    void patchNonExistingLaundryMachine() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineRepository.findAll().size();
        laundryMachine.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaundryMachineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, laundryMachine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(laundryMachine))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLaundryMachine() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineRepository.findAll().size();
        laundryMachine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaundryMachineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(laundryMachine))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLaundryMachine() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineRepository.findAll().size();
        laundryMachine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaundryMachineMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(laundryMachine))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLaundryMachine() throws Exception {
        // Initialize the database
        laundryMachineRepository.saveAndFlush(laundryMachine);

        int databaseSizeBeforeDelete = laundryMachineRepository.findAll().size();

        // Delete the laundryMachine
        restLaundryMachineMockMvc
            .perform(delete(ENTITY_API_URL_ID, laundryMachine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
