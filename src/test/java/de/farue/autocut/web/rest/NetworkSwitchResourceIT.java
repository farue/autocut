package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.domain.Port;
import de.farue.autocut.repository.NetworkSwitchRepository;
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
 * Integration tests for the {@link NetworkSwitchResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
public class NetworkSwitchResourceIT {

    private static final String DEFAULT_SWITCH_INTERFACE = "AAAAAAAAAA";
    private static final String UPDATED_SWITCH_INTERFACE = "BBBBBBBBBB";

    @Autowired
    private NetworkSwitchRepository networkSwitchRepository;

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

    private MockMvc restNetworkSwitchMockMvc;

    private NetworkSwitch networkSwitch;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final NetworkSwitchResource networkSwitchResource = new NetworkSwitchResource(networkSwitchRepository);
        this.restNetworkSwitchMockMvc = MockMvcBuilders.standaloneSetup(networkSwitchResource)
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
    public static NetworkSwitch createEntity(EntityManager em) {
        NetworkSwitch networkSwitch = new NetworkSwitch()
            .switchInterface(DEFAULT_SWITCH_INTERFACE);
        // Add required entity
        Port port;
        if (TestUtil.findAll(em, Port.class).isEmpty()) {
            port = PortResourceIT.createEntity(em);
            em.persist(port);
            em.flush();
        } else {
            port = TestUtil.findAll(em, Port.class).get(0);
        }
        networkSwitch.getPorts().add(port);
        return networkSwitch;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NetworkSwitch createUpdatedEntity(EntityManager em) {
        NetworkSwitch networkSwitch = new NetworkSwitch()
            .switchInterface(UPDATED_SWITCH_INTERFACE);
        // Add required entity
        Port port;
        if (TestUtil.findAll(em, Port.class).isEmpty()) {
            port = PortResourceIT.createUpdatedEntity(em);
            em.persist(port);
            em.flush();
        } else {
            port = TestUtil.findAll(em, Port.class).get(0);
        }
        networkSwitch.getPorts().add(port);
        return networkSwitch;
    }

    @BeforeEach
    public void initTest() {
        networkSwitch = createEntity(em);
    }

    @Test
    @Transactional
    public void createNetworkSwitch() throws Exception {
        int databaseSizeBeforeCreate = networkSwitchRepository.findAll().size();

        // Create the NetworkSwitch
        restNetworkSwitchMockMvc.perform(post("/api/network-switches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(networkSwitch)))
            .andExpect(status().isCreated());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeCreate + 1);
        NetworkSwitch testNetworkSwitch = networkSwitchList.get(networkSwitchList.size() - 1);
        assertThat(testNetworkSwitch.getSwitchInterface()).isEqualTo(DEFAULT_SWITCH_INTERFACE);
    }

    @Test
    @Transactional
    public void createNetworkSwitchWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = networkSwitchRepository.findAll().size();

        // Create the NetworkSwitch with an existing ID
        networkSwitch.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restNetworkSwitchMockMvc.perform(post("/api/network-switches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(networkSwitch)))
            .andExpect(status().isBadRequest());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkSwitchInterfaceIsRequired() throws Exception {
        int databaseSizeBeforeTest = networkSwitchRepository.findAll().size();
        // set the field null
        networkSwitch.setSwitchInterface(null);

        // Create the NetworkSwitch, which fails.

        restNetworkSwitchMockMvc.perform(post("/api/network-switches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(networkSwitch)))
            .andExpect(status().isBadRequest());

        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllNetworkSwitches() throws Exception {
        // Initialize the database
        networkSwitchRepository.saveAndFlush(networkSwitch);

        // Get all the networkSwitchList
        restNetworkSwitchMockMvc.perform(get("/api/network-switches?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(networkSwitch.getId().intValue())))
            .andExpect(jsonPath("$.[*].switchInterface").value(hasItem(DEFAULT_SWITCH_INTERFACE)));
    }

    @Test
    @Transactional
    public void getNetworkSwitch() throws Exception {
        // Initialize the database
        networkSwitchRepository.saveAndFlush(networkSwitch);

        // Get the networkSwitch
        restNetworkSwitchMockMvc.perform(get("/api/network-switches/{id}", networkSwitch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(networkSwitch.getId().intValue()))
            .andExpect(jsonPath("$.switchInterface").value(DEFAULT_SWITCH_INTERFACE));
    }

    @Test
    @Transactional
    public void getNonExistingNetworkSwitch() throws Exception {
        // Get the networkSwitch
        restNetworkSwitchMockMvc.perform(get("/api/network-switches/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNetworkSwitch() throws Exception {
        // Initialize the database
        networkSwitchRepository.saveAndFlush(networkSwitch);

        int databaseSizeBeforeUpdate = networkSwitchRepository.findAll().size();

        // Update the networkSwitch
        NetworkSwitch updatedNetworkSwitch = networkSwitchRepository.findById(networkSwitch.getId()).get();
        // Disconnect from session so that the updates on updatedNetworkSwitch are not directly saved in db
        em.detach(updatedNetworkSwitch);
        updatedNetworkSwitch
            .switchInterface(UPDATED_SWITCH_INTERFACE);

        restNetworkSwitchMockMvc.perform(put("/api/network-switches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedNetworkSwitch)))
            .andExpect(status().isOk());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeUpdate);
        NetworkSwitch testNetworkSwitch = networkSwitchList.get(networkSwitchList.size() - 1);
        assertThat(testNetworkSwitch.getSwitchInterface()).isEqualTo(UPDATED_SWITCH_INTERFACE);
    }

    @Test
    @Transactional
    public void updateNonExistingNetworkSwitch() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchRepository.findAll().size();

        // Create the NetworkSwitch

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNetworkSwitchMockMvc.perform(put("/api/network-switches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(networkSwitch)))
            .andExpect(status().isBadRequest());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteNetworkSwitch() throws Exception {
        // Initialize the database
        networkSwitchRepository.saveAndFlush(networkSwitch);

        int databaseSizeBeforeDelete = networkSwitchRepository.findAll().size();

        // Delete the networkSwitch
        restNetworkSwitchMockMvc.perform(delete("/api/network-switches/{id}", networkSwitch.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
