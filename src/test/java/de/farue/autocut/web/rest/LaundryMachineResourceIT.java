package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.repository.LaundryMachineRepository;

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

import de.farue.autocut.domain.enumeration.LaundryMachineType;
/**
 * Integration tests for the {@link LaundryMachineResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class LaundryMachineResourceIT {

    private static final String DEFAULT_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFIER = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LaundryMachineType DEFAULT_TYPE = LaundryMachineType.WASHING_MACHINE;
    private static final LaundryMachineType UPDATED_TYPE = LaundryMachineType.DRYER;

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

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
    public void createLaundryMachine() throws Exception {
        int databaseSizeBeforeCreate = laundryMachineRepository.findAll().size();
        // Create the LaundryMachine
        restLaundryMachineMockMvc.perform(post("/api/laundry-machines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryMachine)))
            .andExpect(status().isCreated());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeCreate + 1);
        LaundryMachine testLaundryMachine = laundryMachineList.get(laundryMachineList.size() - 1);
        assertThat(testLaundryMachine.getIdentifier()).isEqualTo(DEFAULT_IDENTIFIER);
        assertThat(testLaundryMachine.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLaundryMachine.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testLaundryMachine.isEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    @Transactional
    public void createLaundryMachineWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = laundryMachineRepository.findAll().size();

        // Create the LaundryMachine with an existing ID
        laundryMachine.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLaundryMachineMockMvc.perform(post("/api/laundry-machines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryMachine)))
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkIdentifierIsRequired() throws Exception {
        int databaseSizeBeforeTest = laundryMachineRepository.findAll().size();
        // set the field null
        laundryMachine.setIdentifier(null);

        // Create the LaundryMachine, which fails.


        restLaundryMachineMockMvc.perform(post("/api/laundry-machines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryMachine)))
            .andExpect(status().isBadRequest());

        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = laundryMachineRepository.findAll().size();
        // set the field null
        laundryMachine.setName(null);

        // Create the LaundryMachine, which fails.


        restLaundryMachineMockMvc.perform(post("/api/laundry-machines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryMachine)))
            .andExpect(status().isBadRequest());

        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = laundryMachineRepository.findAll().size();
        // set the field null
        laundryMachine.setType(null);

        // Create the LaundryMachine, which fails.


        restLaundryMachineMockMvc.perform(post("/api/laundry-machines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryMachine)))
            .andExpect(status().isBadRequest());

        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEnabledIsRequired() throws Exception {
        int databaseSizeBeforeTest = laundryMachineRepository.findAll().size();
        // set the field null
        laundryMachine.setEnabled(null);

        // Create the LaundryMachine, which fails.


        restLaundryMachineMockMvc.perform(post("/api/laundry-machines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryMachine)))
            .andExpect(status().isBadRequest());

        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLaundryMachines() throws Exception {
        // Initialize the database
        laundryMachineRepository.saveAndFlush(laundryMachine);

        // Get all the laundryMachineList
        restLaundryMachineMockMvc.perform(get("/api/laundry-machines?sort=id,desc"))
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
    public void getLaundryMachine() throws Exception {
        // Initialize the database
        laundryMachineRepository.saveAndFlush(laundryMachine);

        // Get the laundryMachine
        restLaundryMachineMockMvc.perform(get("/api/laundry-machines/{id}", laundryMachine.getId()))
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
    public void getNonExistingLaundryMachine() throws Exception {
        // Get the laundryMachine
        restLaundryMachineMockMvc.perform(get("/api/laundry-machines/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLaundryMachine() throws Exception {
        // Initialize the database
        laundryMachineRepository.saveAndFlush(laundryMachine);

        int databaseSizeBeforeUpdate = laundryMachineRepository.findAll().size();

        // Update the laundryMachine
        LaundryMachine updatedLaundryMachine = laundryMachineRepository.findById(laundryMachine.getId()).get();
        // Disconnect from session so that the updates on updatedLaundryMachine are not directly saved in db
        em.detach(updatedLaundryMachine);
        updatedLaundryMachine
            .identifier(UPDATED_IDENTIFIER)
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .enabled(UPDATED_ENABLED);

        restLaundryMachineMockMvc.perform(put("/api/laundry-machines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedLaundryMachine)))
            .andExpect(status().isOk());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeUpdate);
        LaundryMachine testLaundryMachine = laundryMachineList.get(laundryMachineList.size() - 1);
        assertThat(testLaundryMachine.getIdentifier()).isEqualTo(UPDATED_IDENTIFIER);
        assertThat(testLaundryMachine.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLaundryMachine.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testLaundryMachine.isEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    @Transactional
    public void updateNonExistingLaundryMachine() throws Exception {
        int databaseSizeBeforeUpdate = laundryMachineRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaundryMachineMockMvc.perform(put("/api/laundry-machines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryMachine)))
            .andExpect(status().isBadRequest());

        // Validate the LaundryMachine in the database
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLaundryMachine() throws Exception {
        // Initialize the database
        laundryMachineRepository.saveAndFlush(laundryMachine);

        int databaseSizeBeforeDelete = laundryMachineRepository.findAll().size();

        // Delete the laundryMachine
        restLaundryMachineMockMvc.perform(delete("/api/laundry-machines/{id}", laundryMachine.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LaundryMachine> laundryMachineList = laundryMachineRepository.findAll();
        assertThat(laundryMachineList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
