package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.WashHistory;
import de.farue.autocut.repository.WashHistoryRepository;
import de.farue.autocut.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static de.farue.autocut.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link WashHistoryResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
public class WashHistoryResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_RESERVATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESERVATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private WashHistoryRepository washHistoryRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restWashHistoryMockMvc;

    private WashHistory washHistory;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final WashHistoryResource washHistoryResource = new WashHistoryResource(washHistoryRepository);
        this.restWashHistoryMockMvc = MockMvcBuilders.standaloneSetup(washHistoryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WashHistory createEntity(EntityManager em) {
        WashHistory washHistory = new WashHistory()
            .date(DEFAULT_DATE)
            .reservation(DEFAULT_RESERVATION);
        return washHistory;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WashHistory createUpdatedEntity(EntityManager em) {
        WashHistory washHistory = new WashHistory()
            .date(UPDATED_DATE)
            .reservation(UPDATED_RESERVATION);
        return washHistory;
    }

    @BeforeEach
    public void initTest() {
        washHistory = createEntity(em);
    }

    @Test
    @Transactional
    public void createWashHistory() throws Exception {
        int databaseSizeBeforeCreate = washHistoryRepository.findAll().size();

        // Create the WashHistory
        restWashHistoryMockMvc.perform(post("/api/wash-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(washHistory)))
            .andExpect(status().isCreated());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        WashHistory testWashHistory = washHistoryList.get(washHistoryList.size() - 1);
        assertThat(testWashHistory.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testWashHistory.getReservation()).isEqualTo(DEFAULT_RESERVATION);
    }

    @Test
    @Transactional
    public void createWashHistoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = washHistoryRepository.findAll().size();

        // Create the WashHistory with an existing ID
        washHistory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWashHistoryMockMvc.perform(post("/api/wash-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(washHistory)))
            .andExpect(status().isBadRequest());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = washHistoryRepository.findAll().size();
        // set the field null
        washHistory.setDate(null);

        // Create the WashHistory, which fails.

        restWashHistoryMockMvc.perform(post("/api/wash-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(washHistory)))
            .andExpect(status().isBadRequest());

        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWashHistories() throws Exception {
        // Initialize the database
        washHistoryRepository.saveAndFlush(washHistory);

        // Get all the washHistoryList
        restWashHistoryMockMvc.perform(get("/api/wash-histories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(washHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].reservation").value(hasItem(DEFAULT_RESERVATION.toString())));
    }
    
    @Test
    @Transactional
    public void getWashHistory() throws Exception {
        // Initialize the database
        washHistoryRepository.saveAndFlush(washHistory);

        // Get the washHistory
        restWashHistoryMockMvc.perform(get("/api/wash-histories/{id}", washHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(washHistory.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.reservation").value(DEFAULT_RESERVATION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingWashHistory() throws Exception {
        // Get the washHistory
        restWashHistoryMockMvc.perform(get("/api/wash-histories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWashHistory() throws Exception {
        // Initialize the database
        washHistoryRepository.saveAndFlush(washHistory);

        int databaseSizeBeforeUpdate = washHistoryRepository.findAll().size();

        // Update the washHistory
        WashHistory updatedWashHistory = washHistoryRepository.findById(washHistory.getId()).get();
        // Disconnect from session so that the updates on updatedWashHistory are not directly saved in db
        em.detach(updatedWashHistory);
        updatedWashHistory
            .date(UPDATED_DATE)
            .reservation(UPDATED_RESERVATION);

        restWashHistoryMockMvc.perform(put("/api/wash-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWashHistory)))
            .andExpect(status().isOk());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeUpdate);
        WashHistory testWashHistory = washHistoryList.get(washHistoryList.size() - 1);
        assertThat(testWashHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testWashHistory.getReservation()).isEqualTo(UPDATED_RESERVATION);
    }

    @Test
    @Transactional
    public void updateNonExistingWashHistory() throws Exception {
        int databaseSizeBeforeUpdate = washHistoryRepository.findAll().size();

        // Create the WashHistory

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWashHistoryMockMvc.perform(put("/api/wash-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(washHistory)))
            .andExpect(status().isBadRequest());

        // Validate the WashHistory in the database
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteWashHistory() throws Exception {
        // Initialize the database
        washHistoryRepository.saveAndFlush(washHistory);

        int databaseSizeBeforeDelete = washHistoryRepository.findAll().size();

        // Delete the washHistory
        restWashHistoryMockMvc.perform(delete("/api/wash-histories/{id}", washHistory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WashHistory> washHistoryList = washHistoryRepository.findAll();
        assertThat(washHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
