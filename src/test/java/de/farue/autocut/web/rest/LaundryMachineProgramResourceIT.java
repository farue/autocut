package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.domain.LaundryMachineProgram;
import de.farue.autocut.domain.LaundryProgram;
import de.farue.autocut.repository.LaundryMachineProgramRepository;
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
 * Integration tests for the {@link LaundryMachineProgramResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class LaundryMachineProgramResourceIT {

    private static final Integer DEFAULT_TIME = 1;
    private static final Integer UPDATED_TIME = 2;

    private static final String ENTITY_API_URL = "/api/laundry-machine-programs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LaundryMachineProgramRepository laundryMachineProgramRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLaundryMachineProgramMockMvc;

    private LaundryMachineProgram laundryMachineProgram;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaundryMachineProgram createEntity(EntityManager em) {
        LaundryMachineProgram laundryMachineProgram = new LaundryMachineProgram().time(DEFAULT_TIME);
        // Add required entity
        LaundryProgram laundryProgram;
        if (TestUtil.findAll(em, LaundryProgram.class).isEmpty()) {
            laundryProgram = LaundryProgramResourceIT.createEntity(em);
            em.persist(laundryProgram);
            em.flush();
        } else {
            laundryProgram = TestUtil.findAll(em, LaundryProgram.class).get(0);
        }
        laundryMachineProgram.setProgram(laundryProgram);
        // Add required entity
        LaundryMachine laundryMachine;
        if (TestUtil.findAll(em, LaundryMachine.class).isEmpty()) {
            laundryMachine = LaundryMachineResourceIT.createEntity(em);
            em.persist(laundryMachine);
            em.flush();
        } else {
            laundryMachine = TestUtil.findAll(em, LaundryMachine.class).get(0);
        }
        laundryMachineProgram.setMachine(laundryMachine);
        return laundryMachineProgram;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaundryMachineProgram createUpdatedEntity(EntityManager em) {
        LaundryMachineProgram laundryMachineProgram = new LaundryMachineProgram().time(UPDATED_TIME);
        // Add required entity
        LaundryProgram laundryProgram;
        if (TestUtil.findAll(em, LaundryProgram.class).isEmpty()) {
            laundryProgram = LaundryProgramResourceIT.createUpdatedEntity(em);
            em.persist(laundryProgram);
            em.flush();
        } else {
            laundryProgram = TestUtil.findAll(em, LaundryProgram.class).get(0);
        }
        laundryMachineProgram.setProgram(laundryProgram);
        // Add required entity
        LaundryMachine laundryMachine;
        if (TestUtil.findAll(em, LaundryMachine.class).isEmpty()) {
            laundryMachine = LaundryMachineResourceIT.createUpdatedEntity(em);
            em.persist(laundryMachine);
            em.flush();
        } else {
            laundryMachine = TestUtil.findAll(em, LaundryMachine.class).get(0);
        }
        laundryMachineProgram.setMachine(laundryMachine);
        return laundryMachineProgram;
    }

    @BeforeEach
    public void initTest() {
        laundryMachineProgram = createEntity(em);
    }

    @Test
    @Transactional
    void createLaundryMachineProgram() throws Exception {
        int databaseSizeBeforeCreate = laundryMachineProgramRepository.findAll().size();
        // Create the LaundryMachineProgram
        restLaundryMachineProgramMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram))
            )
            .andExpect(status().isCreated());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeCreate + 1);
        LaundryMachineProgram testLaundryMachineProgram = laundryMachineProgramList.get(laundryMachineProgramList.size() - 1);
        assertThat(testLaundryMachineProgram.getTime()).isEqualTo(DEFAULT_TIME);
    }

    @Test
    @Transactional
    void createLaundryMachineProgramWithExistingId() throws Exception {
        // Create the LaundryMachineProgram with an existing ID
        laundryMachineProgram.setId(1L);

        int databaseSizeBeforeCreate = laundryMachineProgramRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLaundryMachineProgramMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = laundryMachineProgramRepository.findAll().size();
        // set the field null
        laundryMachineProgram.setTime(null);

        // Create the LaundryMachineProgram, which fails.

        restLaundryMachineProgramMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram))
            )
            .andExpect(status().isBadRequest());

        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLaundryMachinePrograms() throws Exception {
        // Initialize the database
        laundryMachineProgramRepository.saveAndFlush(laundryMachineProgram);

        // Get all the laundryMachineProgramList
        restLaundryMachineProgramMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(laundryMachineProgram.getId().intValue())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME)));
    }

    @Test
    @Transactional
    void getLaundryMachineProgram() throws Exception {
        // Initialize the database
        laundryMachineProgramRepository.saveAndFlush(laundryMachineProgram);

        // Get the laundryMachineProgram
        restLaundryMachineProgramMockMvc
            .perform(get(ENTITY_API_URL_ID, laundryMachineProgram.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(laundryMachineProgram.getId().intValue()))
            .andExpect(jsonPath("$.time").value(DEFAULT_TIME));
    }

    @Test
    @Transactional
    void getNonExistingLaundryMachineProgram() throws Exception {
        // Get the laundryMachineProgram
        restLaundryMachineProgramMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLaundryMachineProgram() throws Exception {
        // Initialize the database
        laundryMachineProgramRepository.saveAndFlush(laundryMachineProgram);

        int databaseSizeBeforeUpdate = laundryMachineProgramRepository.findAll().size();

        // Update the laundryMachineProgram
        LaundryMachineProgram updatedLaundryMachineProgram = laundryMachineProgramRepository.findById(laundryMachineProgram.getId()).get();
        // Disconnect from session so that the updates on updatedLaundryMachineProgram are not directly saved in db
        em.detach(updatedLaundryMachineProgram);
        updatedLaundryMachineProgram.time(UPDATED_TIME);

        restLaundryMachineProgramMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLaundryMachineProgram.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLaundryMachineProgram))
            )
            .andExpect(status().isOk());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeUpdate);
        LaundryMachineProgram testLaundryMachineProgram = laundryMachineProgramList.get(laundryMachineProgramList.size() - 1);
        assertThat(testLaundryMachineProgram.getTime()).isEqualTo(UPDATED_TIME);
    }

    @Test
    @Transactional
    void putNonExistingLaundryMachineProgram() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineProgramRepository.findAll().size();
        laundryMachineProgram.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaundryMachineProgramMockMvc
            .perform(
                put(ENTITY_API_URL_ID, laundryMachineProgram.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLaundryMachineProgram() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineProgramRepository.findAll().size();
        laundryMachineProgram.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaundryMachineProgramMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLaundryMachineProgram() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineProgramRepository.findAll().size();
        laundryMachineProgram.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaundryMachineProgramMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLaundryMachineProgramWithPatch() throws Exception {
        // Initialize the database
        laundryMachineProgramRepository.saveAndFlush(laundryMachineProgram);

        int databaseSizeBeforeUpdate = laundryMachineProgramRepository.findAll().size();

        // Update the laundryMachineProgram using partial update
        LaundryMachineProgram partialUpdatedLaundryMachineProgram = new LaundryMachineProgram();
        partialUpdatedLaundryMachineProgram.setId(laundryMachineProgram.getId());

        restLaundryMachineProgramMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLaundryMachineProgram.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLaundryMachineProgram))
            )
            .andExpect(status().isOk());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeUpdate);
        LaundryMachineProgram testLaundryMachineProgram = laundryMachineProgramList.get(laundryMachineProgramList.size() - 1);
        assertThat(testLaundryMachineProgram.getTime()).isEqualTo(DEFAULT_TIME);
    }

    @Test
    @Transactional
    void fullUpdateLaundryMachineProgramWithPatch() throws Exception {
        // Initialize the database
        laundryMachineProgramRepository.saveAndFlush(laundryMachineProgram);

        int databaseSizeBeforeUpdate = laundryMachineProgramRepository.findAll().size();

        // Update the laundryMachineProgram using partial update
        LaundryMachineProgram partialUpdatedLaundryMachineProgram = new LaundryMachineProgram();
        partialUpdatedLaundryMachineProgram.setId(laundryMachineProgram.getId());

        partialUpdatedLaundryMachineProgram.time(UPDATED_TIME);

        restLaundryMachineProgramMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLaundryMachineProgram.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLaundryMachineProgram))
            )
            .andExpect(status().isOk());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeUpdate);
        LaundryMachineProgram testLaundryMachineProgram = laundryMachineProgramList.get(laundryMachineProgramList.size() - 1);
        assertThat(testLaundryMachineProgram.getTime()).isEqualTo(UPDATED_TIME);
    }

    @Test
    @Transactional
    void patchNonExistingLaundryMachineProgram() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineProgramRepository.findAll().size();
        laundryMachineProgram.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaundryMachineProgramMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, laundryMachineProgram.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLaundryMachineProgram() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineProgramRepository.findAll().size();
        laundryMachineProgram.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaundryMachineProgramMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLaundryMachineProgram() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineProgramRepository.findAll().size();
        laundryMachineProgram.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaundryMachineProgramMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLaundryMachineProgram() throws Exception {
        // Initialize the database
        laundryMachineProgramRepository.saveAndFlush(laundryMachineProgram);

        int databaseSizeBeforeDelete = laundryMachineProgramRepository.findAll().size();

        // Delete the laundryMachineProgram
        restLaundryMachineProgramMockMvc
            .perform(delete(ENTITY_API_URL_ID, laundryMachineProgram.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
