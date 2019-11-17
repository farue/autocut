package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.TenantCommunication;
import de.farue.autocut.repository.TenantCommunicationRepository;
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
 * Integration tests for the {@link TenantCommunicationResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
public class TenantCommunicationResourceIT {

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private TenantCommunicationRepository tenantCommunicationRepository;

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

    private MockMvc restTenantCommunicationMockMvc;

    private TenantCommunication tenantCommunication;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TenantCommunicationResource tenantCommunicationResource = new TenantCommunicationResource(tenantCommunicationRepository);
        this.restTenantCommunicationMockMvc = MockMvcBuilders.standaloneSetup(tenantCommunicationResource)
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
    public static TenantCommunication createEntity(EntityManager em) {
        TenantCommunication tenantCommunication = new TenantCommunication()
            .text(DEFAULT_TEXT)
            .date(DEFAULT_DATE);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createEntity(em);
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        tenantCommunication.setTenant(tenant);
        return tenantCommunication;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TenantCommunication createUpdatedEntity(EntityManager em) {
        TenantCommunication tenantCommunication = new TenantCommunication()
            .text(UPDATED_TEXT)
            .date(UPDATED_DATE);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createUpdatedEntity(em);
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        tenantCommunication.setTenant(tenant);
        return tenantCommunication;
    }

    @BeforeEach
    public void initTest() {
        tenantCommunication = createEntity(em);
    }

    @Test
    @Transactional
    public void createTenantCommunication() throws Exception {
        int databaseSizeBeforeCreate = tenantCommunicationRepository.findAll().size();

        // Create the TenantCommunication
        restTenantCommunicationMockMvc.perform(post("/api/tenant-communications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tenantCommunication)))
            .andExpect(status().isCreated());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeCreate + 1);
        TenantCommunication testTenantCommunication = tenantCommunicationList.get(tenantCommunicationList.size() - 1);
        assertThat(testTenantCommunication.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testTenantCommunication.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    public void createTenantCommunicationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tenantCommunicationRepository.findAll().size();

        // Create the TenantCommunication with an existing ID
        tenantCommunication.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTenantCommunicationMockMvc.perform(post("/api/tenant-communications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tenantCommunication)))
            .andExpect(status().isBadRequest());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = tenantCommunicationRepository.findAll().size();
        // set the field null
        tenantCommunication.setDate(null);

        // Create the TenantCommunication, which fails.

        restTenantCommunicationMockMvc.perform(post("/api/tenant-communications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tenantCommunication)))
            .andExpect(status().isBadRequest());

        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTenantCommunications() throws Exception {
        // Initialize the database
        tenantCommunicationRepository.saveAndFlush(tenantCommunication);

        // Get all the tenantCommunicationList
        restTenantCommunicationMockMvc.perform(get("/api/tenant-communications?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenantCommunication.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    public void getTenantCommunication() throws Exception {
        // Initialize the database
        tenantCommunicationRepository.saveAndFlush(tenantCommunication);

        // Get the tenantCommunication
        restTenantCommunicationMockMvc.perform(get("/api/tenant-communications/{id}", tenantCommunication.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tenantCommunication.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTenantCommunication() throws Exception {
        // Get the tenantCommunication
        restTenantCommunicationMockMvc.perform(get("/api/tenant-communications/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTenantCommunication() throws Exception {
        // Initialize the database
        tenantCommunicationRepository.saveAndFlush(tenantCommunication);

        int databaseSizeBeforeUpdate = tenantCommunicationRepository.findAll().size();

        // Update the tenantCommunication
        TenantCommunication updatedTenantCommunication = tenantCommunicationRepository.findById(tenantCommunication.getId()).get();
        // Disconnect from session so that the updates on updatedTenantCommunication are not directly saved in db
        em.detach(updatedTenantCommunication);
        updatedTenantCommunication
            .text(UPDATED_TEXT)
            .date(UPDATED_DATE);

        restTenantCommunicationMockMvc.perform(put("/api/tenant-communications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTenantCommunication)))
            .andExpect(status().isOk());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeUpdate);
        TenantCommunication testTenantCommunication = tenantCommunicationList.get(tenantCommunicationList.size() - 1);
        assertThat(testTenantCommunication.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testTenantCommunication.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingTenantCommunication() throws Exception {
        int databaseSizeBeforeUpdate = tenantCommunicationRepository.findAll().size();

        // Create the TenantCommunication

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantCommunicationMockMvc.perform(put("/api/tenant-communications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tenantCommunication)))
            .andExpect(status().isBadRequest());

        // Validate the TenantCommunication in the database
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTenantCommunication() throws Exception {
        // Initialize the database
        tenantCommunicationRepository.saveAndFlush(tenantCommunication);

        int databaseSizeBeforeDelete = tenantCommunicationRepository.findAll().size();

        // Delete the tenantCommunication
        restTenantCommunicationMockMvc.perform(delete("/api/tenant-communications/{id}", tenantCommunication.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TenantCommunication> tenantCommunicationList = tenantCommunicationRepository.findAll();
        assertThat(tenantCommunicationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
