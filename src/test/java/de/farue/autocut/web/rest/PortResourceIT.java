package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.InternetAccess;
import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.domain.Port;
import de.farue.autocut.repository.PortRepository;
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
 * Integration tests for the {@link PortResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
public class PortResourceIT {

    private static final Integer DEFAULT_NUMBER = 1;
    private static final Integer UPDATED_NUMBER = 2;

    @Autowired
    private PortRepository portRepository;

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

    private MockMvc restPortMockMvc;

    private Port port;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PortResource portResource = new PortResource(portRepository);
        this.restPortMockMvc = MockMvcBuilders.standaloneSetup(portResource)
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
    public static Port createEntity(EntityManager em) {
        Port port = new Port()
            .number(DEFAULT_NUMBER);
        // Add required entity
        InternetAccess internetAccess;
        if (TestUtil.findAll(em, InternetAccess.class).isEmpty()) {
            internetAccess = InternetAccessResourceIT.createEntity(em);
            em.persist(internetAccess);
            em.flush();
        } else {
            internetAccess = TestUtil.findAll(em, InternetAccess.class).get(0);
        }
        port.setInternetAccess(internetAccess);
        // Add required entity
        NetworkSwitch networkSwitch;
        if (TestUtil.findAll(em, NetworkSwitch.class).isEmpty()) {
            networkSwitch = NetworkSwitchResourceIT.createEntity(em);
            em.persist(networkSwitch);
            em.flush();
        } else {
            networkSwitch = TestUtil.findAll(em, NetworkSwitch.class).get(0);
        }
        port.setNetworkSwitch(networkSwitch);
        return port;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Port createUpdatedEntity(EntityManager em) {
        Port port = new Port()
            .number(UPDATED_NUMBER);
        // Add required entity
        InternetAccess internetAccess;
        if (TestUtil.findAll(em, InternetAccess.class).isEmpty()) {
            internetAccess = InternetAccessResourceIT.createUpdatedEntity(em);
            em.persist(internetAccess);
            em.flush();
        } else {
            internetAccess = TestUtil.findAll(em, InternetAccess.class).get(0);
        }
        port.setInternetAccess(internetAccess);
        // Add required entity
        NetworkSwitch networkSwitch;
        if (TestUtil.findAll(em, NetworkSwitch.class).isEmpty()) {
            networkSwitch = NetworkSwitchResourceIT.createUpdatedEntity(em);
            em.persist(networkSwitch);
            em.flush();
        } else {
            networkSwitch = TestUtil.findAll(em, NetworkSwitch.class).get(0);
        }
        port.setNetworkSwitch(networkSwitch);
        return port;
    }

    @BeforeEach
    public void initTest() {
        port = createEntity(em);
    }

    @Test
    @Transactional
    public void createPort() throws Exception {
        int databaseSizeBeforeCreate = portRepository.findAll().size();

        // Create the Port
        restPortMockMvc.perform(post("/api/ports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(port)))
            .andExpect(status().isCreated());

        // Validate the Port in the database
        List<Port> portList = portRepository.findAll();
        assertThat(portList).hasSize(databaseSizeBeforeCreate + 1);
        Port testPort = portList.get(portList.size() - 1);
        assertThat(testPort.getNumber()).isEqualTo(DEFAULT_NUMBER);
    }

    @Test
    @Transactional
    public void createPortWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = portRepository.findAll().size();

        // Create the Port with an existing ID
        port.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPortMockMvc.perform(post("/api/ports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(port)))
            .andExpect(status().isBadRequest());

        // Validate the Port in the database
        List<Port> portList = portRepository.findAll();
        assertThat(portList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = portRepository.findAll().size();
        // set the field null
        port.setNumber(null);

        // Create the Port, which fails.

        restPortMockMvc.perform(post("/api/ports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(port)))
            .andExpect(status().isBadRequest());

        List<Port> portList = portRepository.findAll();
        assertThat(portList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPorts() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        // Get all the portList
        restPortMockMvc.perform(get("/api/ports?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(port.getId().intValue())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)));
    }

    @Test
    @Transactional
    public void getPort() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        // Get the port
        restPortMockMvc.perform(get("/api/ports/{id}", port.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(port.getId().intValue()))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER));
    }

    @Test
    @Transactional
    public void getNonExistingPort() throws Exception {
        // Get the port
        restPortMockMvc.perform(get("/api/ports/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePort() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        int databaseSizeBeforeUpdate = portRepository.findAll().size();

        // Update the port
        Port updatedPort = portRepository.findById(port.getId()).get();
        // Disconnect from session so that the updates on updatedPort are not directly saved in db
        em.detach(updatedPort);
        updatedPort
            .number(UPDATED_NUMBER);

        restPortMockMvc.perform(put("/api/ports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPort)))
            .andExpect(status().isOk());

        // Validate the Port in the database
        List<Port> portList = portRepository.findAll();
        assertThat(portList).hasSize(databaseSizeBeforeUpdate);
        Port testPort = portList.get(portList.size() - 1);
        assertThat(testPort.getNumber()).isEqualTo(UPDATED_NUMBER);
    }

    @Test
    @Transactional
    public void updateNonExistingPort() throws Exception {
        int databaseSizeBeforeUpdate = portRepository.findAll().size();

        // Create the Port

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPortMockMvc.perform(put("/api/ports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(port)))
            .andExpect(status().isBadRequest());

        // Validate the Port in the database
        List<Port> portList = portRepository.findAll();
        assertThat(portList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePort() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        int databaseSizeBeforeDelete = portRepository.findAll().size();

        // Delete the port
        restPortMockMvc.perform(delete("/api/ports/{id}", port.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Port> portList = portRepository.findAll();
        assertThat(portList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
