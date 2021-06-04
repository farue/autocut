package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.LaundryProgram;
import de.farue.autocut.repository.LaundryProgramRepository;
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
 * Integration tests for the {@link LaundryProgramResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class LaundryProgramResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SUBPROGRAM = "AAAAAAAAAA";
    private static final String UPDATED_SUBPROGRAM = "BBBBBBBBBB";

    private static final Integer DEFAULT_SPIN = 1;
    private static final Integer UPDATED_SPIN = 2;

    private static final Boolean DEFAULT_PRE_WASH = false;
    private static final Boolean UPDATED_PRE_WASH = true;

    private static final Boolean DEFAULT_PROTECT = false;
    private static final Boolean UPDATED_PROTECT = true;

    private static final String ENTITY_API_URL = "/api/laundry-programs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LaundryProgramRepository laundryProgramRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLaundryProgramMockMvc;

    private LaundryProgram laundryProgram;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaundryProgram createEntity(EntityManager em) {
        LaundryProgram laundryProgram = new LaundryProgram()
            .name(DEFAULT_NAME)
            .subprogram(DEFAULT_SUBPROGRAM)
            .spin(DEFAULT_SPIN)
            .preWash(DEFAULT_PRE_WASH)
            .protect(DEFAULT_PROTECT);
        return laundryProgram;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaundryProgram createUpdatedEntity(EntityManager em) {
        LaundryProgram laundryProgram = new LaundryProgram()
            .name(UPDATED_NAME)
            .subprogram(UPDATED_SUBPROGRAM)
            .spin(UPDATED_SPIN)
            .preWash(UPDATED_PRE_WASH)
            .protect(UPDATED_PROTECT);
        return laundryProgram;
    }

    @BeforeEach
    public void initTest() {
        laundryProgram = createEntity(em);
    }

    @Test
    @Transactional
    void createLaundryProgram() throws Exception {
        int databaseSizeBeforeCreate = laundryProgramRepository.findAll().size();
        // Create the LaundryProgram
        restLaundryProgramMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(laundryProgram))
            )
            .andExpect(status().isCreated());

        // Validate the LaundryProgram in the database
        List<LaundryProgram> laundryProgramList = laundryProgramRepository.findAll();
        assertThat(laundryProgramList).hasSize(databaseSizeBeforeCreate + 1);
        LaundryProgram testLaundryProgram = laundryProgramList.get(laundryProgramList.size() - 1);
        assertThat(testLaundryProgram.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLaundryProgram.getSubprogram()).isEqualTo(DEFAULT_SUBPROGRAM);
        assertThat(testLaundryProgram.getSpin()).isEqualTo(DEFAULT_SPIN);
        assertThat(testLaundryProgram.getPreWash()).isEqualTo(DEFAULT_PRE_WASH);
        assertThat(testLaundryProgram.getProtect()).isEqualTo(DEFAULT_PROTECT);
    }

    @Test
    @Transactional
    void createLaundryProgramWithExistingId() throws Exception {
        // Create the LaundryProgram with an existing ID
        laundryProgram.setId(1L);

        int databaseSizeBeforeCreate = laundryProgramRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLaundryProgramMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(laundryProgram))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryProgram in the database
        List<LaundryProgram> laundryProgramList = laundryProgramRepository.findAll();
        assertThat(laundryProgramList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = laundryProgramRepository.findAll().size();
        // set the field null
        laundryProgram.setName(null);

        // Create the LaundryProgram, which fails.

        restLaundryProgramMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(laundryProgram))
            )
            .andExpect(status().isBadRequest());

        List<LaundryProgram> laundryProgramList = laundryProgramRepository.findAll();
        assertThat(laundryProgramList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLaundryPrograms() throws Exception {
        // Initialize the database
        laundryProgramRepository.saveAndFlush(laundryProgram);

        // Get all the laundryProgramList
        restLaundryProgramMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(laundryProgram.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].subprogram").value(hasItem(DEFAULT_SUBPROGRAM)))
            .andExpect(jsonPath("$.[*].spin").value(hasItem(DEFAULT_SPIN)))
            .andExpect(jsonPath("$.[*].preWash").value(hasItem(DEFAULT_PRE_WASH.booleanValue())))
            .andExpect(jsonPath("$.[*].protect").value(hasItem(DEFAULT_PROTECT.booleanValue())));
    }

    @Test
    @Transactional
    void getLaundryProgram() throws Exception {
        // Initialize the database
        laundryProgramRepository.saveAndFlush(laundryProgram);

        // Get the laundryProgram
        restLaundryProgramMockMvc
            .perform(get(ENTITY_API_URL_ID, laundryProgram.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(laundryProgram.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.subprogram").value(DEFAULT_SUBPROGRAM))
            .andExpect(jsonPath("$.spin").value(DEFAULT_SPIN))
            .andExpect(jsonPath("$.preWash").value(DEFAULT_PRE_WASH.booleanValue()))
            .andExpect(jsonPath("$.protect").value(DEFAULT_PROTECT.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingLaundryProgram() throws Exception {
        // Get the laundryProgram
        restLaundryProgramMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLaundryProgram() throws Exception {
        // Initialize the database
        laundryProgramRepository.saveAndFlush(laundryProgram);

        int databaseSizeBeforeUpdate = laundryProgramRepository.findAll().size();

        // Update the laundryProgram
        LaundryProgram updatedLaundryProgram = laundryProgramRepository.findById(laundryProgram.getId()).get();
        // Disconnect from session so that the updates on updatedLaundryProgram are not directly saved in db
        em.detach(updatedLaundryProgram);
        updatedLaundryProgram
            .name(UPDATED_NAME)
            .subprogram(UPDATED_SUBPROGRAM)
            .spin(UPDATED_SPIN)
            .preWash(UPDATED_PRE_WASH)
            .protect(UPDATED_PROTECT);

        restLaundryProgramMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLaundryProgram.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLaundryProgram))
            )
            .andExpect(status().isOk());

        // Validate the LaundryProgram in the database
        List<LaundryProgram> laundryProgramList = laundryProgramRepository.findAll();
        assertThat(laundryProgramList).hasSize(databaseSizeBeforeUpdate);
        LaundryProgram testLaundryProgram = laundryProgramList.get(laundryProgramList.size() - 1);
        assertThat(testLaundryProgram.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLaundryProgram.getSubprogram()).isEqualTo(UPDATED_SUBPROGRAM);
        assertThat(testLaundryProgram.getSpin()).isEqualTo(UPDATED_SPIN);
        assertThat(testLaundryProgram.getPreWash()).isEqualTo(UPDATED_PRE_WASH);
        assertThat(testLaundryProgram.getProtect()).isEqualTo(UPDATED_PROTECT);
    }

    @Test
    @Transactional
    void putNonExistingLaundryProgram() throws Exception {
        int databaseSizeBeforeUpdate = laundryProgramRepository.findAll().size();
        laundryProgram.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaundryProgramMockMvc
            .perform(
                put(ENTITY_API_URL_ID, laundryProgram.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(laundryProgram))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryProgram in the database
        List<LaundryProgram> laundryProgramList = laundryProgramRepository.findAll();
        assertThat(laundryProgramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLaundryProgram() throws Exception {
        int databaseSizeBeforeUpdate = laundryProgramRepository.findAll().size();
        laundryProgram.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaundryProgramMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(laundryProgram))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryProgram in the database
        List<LaundryProgram> laundryProgramList = laundryProgramRepository.findAll();
        assertThat(laundryProgramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLaundryProgram() throws Exception {
        int databaseSizeBeforeUpdate = laundryProgramRepository.findAll().size();
        laundryProgram.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaundryProgramMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(laundryProgram)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LaundryProgram in the database
        List<LaundryProgram> laundryProgramList = laundryProgramRepository.findAll();
        assertThat(laundryProgramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLaundryProgramWithPatch() throws Exception {
        // Initialize the database
        laundryProgramRepository.saveAndFlush(laundryProgram);

        int databaseSizeBeforeUpdate = laundryProgramRepository.findAll().size();

        // Update the laundryProgram using partial update
        LaundryProgram partialUpdatedLaundryProgram = new LaundryProgram();
        partialUpdatedLaundryProgram.setId(laundryProgram.getId());

        partialUpdatedLaundryProgram.name(UPDATED_NAME).preWash(UPDATED_PRE_WASH).protect(UPDATED_PROTECT);

        restLaundryProgramMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLaundryProgram.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLaundryProgram))
            )
            .andExpect(status().isOk());

        // Validate the LaundryProgram in the database
        List<LaundryProgram> laundryProgramList = laundryProgramRepository.findAll();
        assertThat(laundryProgramList).hasSize(databaseSizeBeforeUpdate);
        LaundryProgram testLaundryProgram = laundryProgramList.get(laundryProgramList.size() - 1);
        assertThat(testLaundryProgram.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLaundryProgram.getSubprogram()).isEqualTo(DEFAULT_SUBPROGRAM);
        assertThat(testLaundryProgram.getSpin()).isEqualTo(DEFAULT_SPIN);
        assertThat(testLaundryProgram.getPreWash()).isEqualTo(UPDATED_PRE_WASH);
        assertThat(testLaundryProgram.getProtect()).isEqualTo(UPDATED_PROTECT);
    }

    @Test
    @Transactional
    void fullUpdateLaundryProgramWithPatch() throws Exception {
        // Initialize the database
        laundryProgramRepository.saveAndFlush(laundryProgram);

        int databaseSizeBeforeUpdate = laundryProgramRepository.findAll().size();

        // Update the laundryProgram using partial update
        LaundryProgram partialUpdatedLaundryProgram = new LaundryProgram();
        partialUpdatedLaundryProgram.setId(laundryProgram.getId());

        partialUpdatedLaundryProgram
            .name(UPDATED_NAME)
            .subprogram(UPDATED_SUBPROGRAM)
            .spin(UPDATED_SPIN)
            .preWash(UPDATED_PRE_WASH)
            .protect(UPDATED_PROTECT);

        restLaundryProgramMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLaundryProgram.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLaundryProgram))
            )
            .andExpect(status().isOk());

        // Validate the LaundryProgram in the database
        List<LaundryProgram> laundryProgramList = laundryProgramRepository.findAll();
        assertThat(laundryProgramList).hasSize(databaseSizeBeforeUpdate);
        LaundryProgram testLaundryProgram = laundryProgramList.get(laundryProgramList.size() - 1);
        assertThat(testLaundryProgram.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLaundryProgram.getSubprogram()).isEqualTo(UPDATED_SUBPROGRAM);
        assertThat(testLaundryProgram.getSpin()).isEqualTo(UPDATED_SPIN);
        assertThat(testLaundryProgram.getPreWash()).isEqualTo(UPDATED_PRE_WASH);
        assertThat(testLaundryProgram.getProtect()).isEqualTo(UPDATED_PROTECT);
    }

    @Test
    @Transactional
    void patchNonExistingLaundryProgram() throws Exception {
        int databaseSizeBeforeUpdate = laundryProgramRepository.findAll().size();
        laundryProgram.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaundryProgramMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, laundryProgram.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(laundryProgram))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryProgram in the database
        List<LaundryProgram> laundryProgramList = laundryProgramRepository.findAll();
        assertThat(laundryProgramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLaundryProgram() throws Exception {
        int databaseSizeBeforeUpdate = laundryProgramRepository.findAll().size();
        laundryProgram.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaundryProgramMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(laundryProgram))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryProgram in the database
        List<LaundryProgram> laundryProgramList = laundryProgramRepository.findAll();
        assertThat(laundryProgramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLaundryProgram() throws Exception {
        int databaseSizeBeforeUpdate = laundryProgramRepository.findAll().size();
        laundryProgram.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaundryProgramMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(laundryProgram))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LaundryProgram in the database
        List<LaundryProgram> laundryProgramList = laundryProgramRepository.findAll();
        assertThat(laundryProgramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLaundryProgram() throws Exception {
        // Initialize the database
        laundryProgramRepository.saveAndFlush(laundryProgram);

        int databaseSizeBeforeDelete = laundryProgramRepository.findAll().size();

        // Delete the laundryProgram
        restLaundryProgramMockMvc
            .perform(delete(ENTITY_API_URL_ID, laundryProgram.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LaundryProgram> laundryProgramList = laundryProgramRepository.findAll();
        assertThat(laundryProgramList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
