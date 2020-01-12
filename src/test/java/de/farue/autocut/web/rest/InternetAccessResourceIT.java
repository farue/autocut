package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.InternetAccess;
import de.farue.autocut.domain.Port;
import de.farue.autocut.repository.InternetAccessRepository;
import de.farue.autocut.service.InternetAccessService;
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
import java.util.List;

import static de.farue.autocut.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link InternetAccessResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
public class InternetAccessResourceIT {

    private static final Boolean DEFAULT_BLOCKED = false;
    private static final Boolean UPDATED_BLOCKED = true;

    private static final String DEFAULT_IP_1 = "AAAAAAAAAA";
    private static final String UPDATED_IP_1 = "BBBBBBBBBB";

    private static final String DEFAULT_IP_2 = "AAAAAAAAAA";
    private static final String UPDATED_IP_2 = "BBBBBBBBBB";

    @Autowired
    private InternetAccessRepository internetAccessRepository;

    @Autowired
    private InternetAccessService internetAccessService;

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

    private MockMvc restInternetAccessMockMvc;

    private InternetAccess internetAccess;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final InternetAccessResource internetAccessResource = new InternetAccessResource(internetAccessService);
        this.restInternetAccessMockMvc = MockMvcBuilders.standaloneSetup(internetAccessResource)
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
    public static InternetAccess createEntity(EntityManager em) {
        InternetAccess internetAccess = new InternetAccess()
            .blocked(DEFAULT_BLOCKED)
            .ip1(DEFAULT_IP_1)
            .ip2(DEFAULT_IP_2);
        // Add required entity
        Port port;
        if (TestUtil.findAll(em, Port.class).isEmpty()) {
            port = PortResourceIT.createEntity(em);
            em.persist(port);
            em.flush();
        } else {
            port = TestUtil.findAll(em, Port.class).get(0);
        }
        internetAccess.setPort(port);
        return internetAccess;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InternetAccess createUpdatedEntity(EntityManager em) {
        InternetAccess internetAccess = new InternetAccess()
            .blocked(UPDATED_BLOCKED)
            .ip1(UPDATED_IP_1)
            .ip2(UPDATED_IP_2);
        // Add required entity
        Port port;
        if (TestUtil.findAll(em, Port.class).isEmpty()) {
            port = PortResourceIT.createUpdatedEntity(em);
            em.persist(port);
            em.flush();
        } else {
            port = TestUtil.findAll(em, Port.class).get(0);
        }
        internetAccess.setPort(port);
        return internetAccess;
    }

    @BeforeEach
    public void initTest() {
        internetAccess = createEntity(em);
    }

    @Test
    @Transactional
    public void createInternetAccess() throws Exception {
        int databaseSizeBeforeCreate = internetAccessRepository.findAll().size();

        // Create the InternetAccess
        restInternetAccessMockMvc.perform(post("/api/internet-accesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(internetAccess)))
            .andExpect(status().isCreated());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeCreate + 1);
        InternetAccess testInternetAccess = internetAccessList.get(internetAccessList.size() - 1);
        assertThat(testInternetAccess.isBlocked()).isEqualTo(DEFAULT_BLOCKED);
        assertThat(testInternetAccess.getIp1()).isEqualTo(DEFAULT_IP_1);
        assertThat(testInternetAccess.getIp2()).isEqualTo(DEFAULT_IP_2);
    }

    @Test
    @Transactional
    public void createInternetAccessWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = internetAccessRepository.findAll().size();

        // Create the InternetAccess with an existing ID
        internetAccess.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restInternetAccessMockMvc.perform(post("/api/internet-accesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(internetAccess)))
            .andExpect(status().isBadRequest());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkBlockedIsRequired() throws Exception {
        int databaseSizeBeforeTest = internetAccessRepository.findAll().size();
        // set the field null
        internetAccess.setBlocked(null);

        // Create the InternetAccess, which fails.

        restInternetAccessMockMvc.perform(post("/api/internet-accesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(internetAccess)))
            .andExpect(status().isBadRequest());

        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIp1IsRequired() throws Exception {
        int databaseSizeBeforeTest = internetAccessRepository.findAll().size();
        // set the field null
        internetAccess.setIp1(null);

        // Create the InternetAccess, which fails.

        restInternetAccessMockMvc.perform(post("/api/internet-accesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(internetAccess)))
            .andExpect(status().isBadRequest());

        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIp2IsRequired() throws Exception {
        int databaseSizeBeforeTest = internetAccessRepository.findAll().size();
        // set the field null
        internetAccess.setIp2(null);

        // Create the InternetAccess, which fails.

        restInternetAccessMockMvc.perform(post("/api/internet-accesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(internetAccess)))
            .andExpect(status().isBadRequest());

        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllInternetAccesses() throws Exception {
        // Initialize the database
        internetAccessRepository.saveAndFlush(internetAccess);

        // Get all the internetAccessList
        restInternetAccessMockMvc.perform(get("/api/internet-accesses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(internetAccess.getId().intValue())))
            .andExpect(jsonPath("$.[*].blocked").value(hasItem(DEFAULT_BLOCKED.booleanValue())))
            .andExpect(jsonPath("$.[*].ip1").value(hasItem(DEFAULT_IP_1)))
            .andExpect(jsonPath("$.[*].ip2").value(hasItem(DEFAULT_IP_2)));
    }
    
    @Test
    @Transactional
    public void getInternetAccess() throws Exception {
        // Initialize the database
        internetAccessRepository.saveAndFlush(internetAccess);

        // Get the internetAccess
        restInternetAccessMockMvc.perform(get("/api/internet-accesses/{id}", internetAccess.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(internetAccess.getId().intValue()))
            .andExpect(jsonPath("$.blocked").value(DEFAULT_BLOCKED.booleanValue()))
            .andExpect(jsonPath("$.ip1").value(DEFAULT_IP_1))
            .andExpect(jsonPath("$.ip2").value(DEFAULT_IP_2));
    }

    @Test
    @Transactional
    public void getNonExistingInternetAccess() throws Exception {
        // Get the internetAccess
        restInternetAccessMockMvc.perform(get("/api/internet-accesses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInternetAccess() throws Exception {
        // Initialize the database
        internetAccessService.save(internetAccess);

        int databaseSizeBeforeUpdate = internetAccessRepository.findAll().size();

        // Update the internetAccess
        InternetAccess updatedInternetAccess = internetAccessRepository.findById(internetAccess.getId()).get();
        // Disconnect from session so that the updates on updatedInternetAccess are not directly saved in db
        em.detach(updatedInternetAccess);
        updatedInternetAccess
            .blocked(UPDATED_BLOCKED)
            .ip1(UPDATED_IP_1)
            .ip2(UPDATED_IP_2);

        restInternetAccessMockMvc.perform(put("/api/internet-accesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedInternetAccess)))
            .andExpect(status().isOk());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeUpdate);
        InternetAccess testInternetAccess = internetAccessList.get(internetAccessList.size() - 1);
        assertThat(testInternetAccess.isBlocked()).isEqualTo(UPDATED_BLOCKED);
        assertThat(testInternetAccess.getIp1()).isEqualTo(UPDATED_IP_1);
        assertThat(testInternetAccess.getIp2()).isEqualTo(UPDATED_IP_2);
    }

    @Test
    @Transactional
    public void updateNonExistingInternetAccess() throws Exception {
        int databaseSizeBeforeUpdate = internetAccessRepository.findAll().size();

        // Create the InternetAccess

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInternetAccessMockMvc.perform(put("/api/internet-accesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(internetAccess)))
            .andExpect(status().isBadRequest());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteInternetAccess() throws Exception {
        // Initialize the database
        internetAccessService.save(internetAccess);

        int databaseSizeBeforeDelete = internetAccessRepository.findAll().size();

        // Delete the internetAccess
        restInternetAccessMockMvc.perform(delete("/api/internet-accesses/{id}", internetAccess.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
