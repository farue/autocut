package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.LaundryMachineProgram;
import de.farue.autocut.repository.LaundryMachineProgramRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link LaundryMachineProgramResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class LaundryMachineProgramResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_TIME = 1;
    private static final Integer UPDATED_TIME = 2;

    private static final Integer DEFAULT_TEMPERATURE = 1;
    private static final Integer UPDATED_TEMPERATURE = 2;

    private static final Integer DEFAULT_SPIN = 1;
    private static final Integer UPDATED_SPIN = 2;

    private static final Boolean DEFAULT_PRE_WASH = false;
    private static final Boolean UPDATED_PRE_WASH = true;

    private static final Boolean DEFAULT_PROTECT = false;
    private static final Boolean UPDATED_PROTECT = true;

    private static final Boolean DEFAULT_SHORT_CYCLE = false;
    private static final Boolean UPDATED_SHORT_CYCLE = true;

    private static final Boolean DEFAULT_WRINKLE = false;
    private static final Boolean UPDATED_WRINKLE = true;

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
        LaundryMachineProgram laundryMachineProgram = new LaundryMachineProgram()
            .name(DEFAULT_NAME)
            .time(DEFAULT_TIME)
            .temperature(DEFAULT_TEMPERATURE)
            .spin(DEFAULT_SPIN)
            .preWash(DEFAULT_PRE_WASH)
            .protect(DEFAULT_PROTECT)
            .shortCycle(DEFAULT_SHORT_CYCLE)
            .wrinkle(DEFAULT_WRINKLE);
        return laundryMachineProgram;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaundryMachineProgram createUpdatedEntity(EntityManager em) {
        LaundryMachineProgram laundryMachineProgram = new LaundryMachineProgram()
            .name(UPDATED_NAME)
            .time(UPDATED_TIME)
            .temperature(UPDATED_TEMPERATURE)
            .spin(UPDATED_SPIN)
            .preWash(UPDATED_PRE_WASH)
            .protect(UPDATED_PROTECT)
            .shortCycle(UPDATED_SHORT_CYCLE)
            .wrinkle(UPDATED_WRINKLE);
        return laundryMachineProgram;
    }

    @BeforeEach
    public void initTest() {
        laundryMachineProgram = createEntity(em);
    }

    @Test
    @Transactional
    public void createLaundryMachineProgram() throws Exception {
        int databaseSizeBeforeCreate = laundryMachineProgramRepository.findAll().size();

        // Create the LaundryMachineProgram
        restLaundryMachineProgramMockMvc.perform(post("/api/laundry-machine-programs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram)))
            .andExpect(status().isCreated());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeCreate + 1);
        LaundryMachineProgram testLaundryMachineProgram = laundryMachineProgramList.get(laundryMachineProgramList.size() - 1);
        assertThat(testLaundryMachineProgram.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLaundryMachineProgram.getTime()).isEqualTo(DEFAULT_TIME);
        assertThat(testLaundryMachineProgram.getTemperature()).isEqualTo(DEFAULT_TEMPERATURE);
        assertThat(testLaundryMachineProgram.getSpin()).isEqualTo(DEFAULT_SPIN);
        assertThat(testLaundryMachineProgram.isPreWash()).isEqualTo(DEFAULT_PRE_WASH);
        assertThat(testLaundryMachineProgram.isProtect()).isEqualTo(DEFAULT_PROTECT);
        assertThat(testLaundryMachineProgram.isShortCycle()).isEqualTo(DEFAULT_SHORT_CYCLE);
        assertThat(testLaundryMachineProgram.isWrinkle()).isEqualTo(DEFAULT_WRINKLE);
    }

    @Test
    @Transactional
    public void createLaundryMachineProgramWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = laundryMachineProgramRepository.findAll().size();

        // Create the LaundryMachineProgram with an existing ID
        laundryMachineProgram.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLaundryMachineProgramMockMvc.perform(post("/api/laundry-machine-programs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram)))
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = laundryMachineProgramRepository.findAll().size();
        // set the field null
        laundryMachineProgram.setName(null);

        // Create the LaundryMachineProgram, which fails.

        restLaundryMachineProgramMockMvc.perform(post("/api/laundry-machine-programs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram)))
            .andExpect(status().isBadRequest());

        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = laundryMachineProgramRepository.findAll().size();
        // set the field null
        laundryMachineProgram.setTime(null);

        // Create the LaundryMachineProgram, which fails.

        restLaundryMachineProgramMockMvc.perform(post("/api/laundry-machine-programs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram)))
            .andExpect(status().isBadRequest());

        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLaundryMachinePrograms() throws Exception {
        // Initialize the database
        laundryMachineProgramRepository.saveAndFlush(laundryMachineProgram);

        // Get all the laundryMachineProgramList
        restLaundryMachineProgramMockMvc.perform(get("/api/laundry-machine-programs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(laundryMachineProgram.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME)))
            .andExpect(jsonPath("$.[*].temperature").value(hasItem(DEFAULT_TEMPERATURE)))
            .andExpect(jsonPath("$.[*].spin").value(hasItem(DEFAULT_SPIN)))
            .andExpect(jsonPath("$.[*].preWash").value(hasItem(DEFAULT_PRE_WASH.booleanValue())))
            .andExpect(jsonPath("$.[*].protect").value(hasItem(DEFAULT_PROTECT.booleanValue())))
            .andExpect(jsonPath("$.[*].shortCycle").value(hasItem(DEFAULT_SHORT_CYCLE.booleanValue())))
            .andExpect(jsonPath("$.[*].wrinkle").value(hasItem(DEFAULT_WRINKLE.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getLaundryMachineProgram() throws Exception {
        // Initialize the database
        laundryMachineProgramRepository.saveAndFlush(laundryMachineProgram);

        // Get the laundryMachineProgram
        restLaundryMachineProgramMockMvc.perform(get("/api/laundry-machine-programs/{id}", laundryMachineProgram.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(laundryMachineProgram.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.time").value(DEFAULT_TIME))
            .andExpect(jsonPath("$.temperature").value(DEFAULT_TEMPERATURE))
            .andExpect(jsonPath("$.spin").value(DEFAULT_SPIN))
            .andExpect(jsonPath("$.preWash").value(DEFAULT_PRE_WASH.booleanValue()))
            .andExpect(jsonPath("$.protect").value(DEFAULT_PROTECT.booleanValue()))
            .andExpect(jsonPath("$.shortCycle").value(DEFAULT_SHORT_CYCLE.booleanValue()))
            .andExpect(jsonPath("$.wrinkle").value(DEFAULT_WRINKLE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingLaundryMachineProgram() throws Exception {
        // Get the laundryMachineProgram
        restLaundryMachineProgramMockMvc.perform(get("/api/laundry-machine-programs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLaundryMachineProgram() throws Exception {
        // Initialize the database
        laundryMachineProgramRepository.saveAndFlush(laundryMachineProgram);

        int databaseSizeBeforeUpdate = laundryMachineProgramRepository.findAll().size();

        // Update the laundryMachineProgram
        LaundryMachineProgram updatedLaundryMachineProgram = laundryMachineProgramRepository.findById(laundryMachineProgram.getId()).get();
        // Disconnect from session so that the updates on updatedLaundryMachineProgram are not directly saved in db
        em.detach(updatedLaundryMachineProgram);
        updatedLaundryMachineProgram
            .name(UPDATED_NAME)
            .time(UPDATED_TIME)
            .temperature(UPDATED_TEMPERATURE)
            .spin(UPDATED_SPIN)
            .preWash(UPDATED_PRE_WASH)
            .protect(UPDATED_PROTECT)
            .shortCycle(UPDATED_SHORT_CYCLE)
            .wrinkle(UPDATED_WRINKLE);

        restLaundryMachineProgramMockMvc.perform(put("/api/laundry-machine-programs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedLaundryMachineProgram)))
            .andExpect(status().isOk());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeUpdate);
        LaundryMachineProgram testLaundryMachineProgram = laundryMachineProgramList.get(laundryMachineProgramList.size() - 1);
        assertThat(testLaundryMachineProgram.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLaundryMachineProgram.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testLaundryMachineProgram.getTemperature()).isEqualTo(UPDATED_TEMPERATURE);
        assertThat(testLaundryMachineProgram.getSpin()).isEqualTo(UPDATED_SPIN);
        assertThat(testLaundryMachineProgram.isPreWash()).isEqualTo(UPDATED_PRE_WASH);
        assertThat(testLaundryMachineProgram.isProtect()).isEqualTo(UPDATED_PROTECT);
        assertThat(testLaundryMachineProgram.isShortCycle()).isEqualTo(UPDATED_SHORT_CYCLE);
        assertThat(testLaundryMachineProgram.isWrinkle()).isEqualTo(UPDATED_WRINKLE);
    }

    @Test
    @Transactional
    public void updateNonExistingLaundryMachineProgram() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineProgramRepository.findAll().size();

        // Create the LaundryMachineProgram

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaundryMachineProgramMockMvc.perform(put("/api/laundry-machine-programs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryMachineProgram)))
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachineProgram in the database
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLaundryMachineProgram() throws Exception {
        // Initialize the database
        laundryMachineProgramRepository.saveAndFlush(laundryMachineProgram);

        int databaseSizeBeforeDelete = laundryMachineProgramRepository.findAll().size();

        // Delete the laundryMachineProgram
        restLaundryMachineProgramMockMvc.perform(delete("/api/laundry-machine-programs/{id}", laundryMachineProgram.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LaundryMachineProgram> laundryMachineProgramList = laundryMachineProgramRepository.findAll();
        assertThat(laundryMachineProgramList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
