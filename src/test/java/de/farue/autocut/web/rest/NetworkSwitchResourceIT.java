package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.repository.NetworkSwitchRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link NetworkSwitchResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class NetworkSwitchResourceIT {

    private static final String DEFAULT_INTERFACE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_INTERFACE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SSH_HOST = "AAAAAAAAAA";
    private static final String UPDATED_SSH_HOST = "BBBBBBBBBB";

    private static final Integer DEFAULT_SSH_PORT = 0;
    private static final Integer UPDATED_SSH_PORT = 1;

    private static final String DEFAULT_SSH_KEY = "AAAAAAAAAA";
    private static final String UPDATED_SSH_KEY = "BBBBBBBBBB";

    @Autowired
    private NetworkSwitchRepository networkSwitchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNetworkSwitchMockMvc;

    private NetworkSwitch networkSwitch;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NetworkSwitch createEntity(EntityManager em) {
        NetworkSwitch networkSwitch = new NetworkSwitch()
            .interfaceName(DEFAULT_INTERFACE_NAME)
            .sshHost(DEFAULT_SSH_HOST)
            .sshPort(DEFAULT_SSH_PORT)
            .sshKey(DEFAULT_SSH_KEY);
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
            .interfaceName(UPDATED_INTERFACE_NAME)
            .sshHost(UPDATED_SSH_HOST)
            .sshPort(UPDATED_SSH_PORT)
            .sshKey(UPDATED_SSH_KEY);
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
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(networkSwitch)))
            .andExpect(status().isCreated());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeCreate + 1);
        NetworkSwitch testNetworkSwitch = networkSwitchList.get(networkSwitchList.size() - 1);
        assertThat(testNetworkSwitch.getInterfaceName()).isEqualTo(DEFAULT_INTERFACE_NAME);
        assertThat(testNetworkSwitch.getSshHost()).isEqualTo(DEFAULT_SSH_HOST);
        assertThat(testNetworkSwitch.getSshPort()).isEqualTo(DEFAULT_SSH_PORT);
        assertThat(testNetworkSwitch.getSshKey()).isEqualTo(DEFAULT_SSH_KEY);
    }

    @Test
    @Transactional
    public void createNetworkSwitchWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = networkSwitchRepository.findAll().size();

        // Create the NetworkSwitch with an existing ID
        networkSwitch.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restNetworkSwitchMockMvc.perform(post("/api/network-switches")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(networkSwitch)))
            .andExpect(status().isBadRequest());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkInterfaceNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = networkSwitchRepository.findAll().size();
        // set the field null
        networkSwitch.setInterfaceName(null);

        // Create the NetworkSwitch, which fails.

        restNetworkSwitchMockMvc.perform(post("/api/network-switches")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(networkSwitch)))
            .andExpect(status().isBadRequest());

        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSshHostIsRequired() throws Exception {
        int databaseSizeBeforeTest = networkSwitchRepository.findAll().size();
        // set the field null
        networkSwitch.setSshHost(null);

        // Create the NetworkSwitch, which fails.

        restNetworkSwitchMockMvc.perform(post("/api/network-switches")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(networkSwitch)))
            .andExpect(status().isBadRequest());

        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSshPortIsRequired() throws Exception {
        int databaseSizeBeforeTest = networkSwitchRepository.findAll().size();
        // set the field null
        networkSwitch.setSshPort(null);

        // Create the NetworkSwitch, which fails.

        restNetworkSwitchMockMvc.perform(post("/api/network-switches")
            .contentType(MediaType.APPLICATION_JSON)
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
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(networkSwitch.getId().intValue())))
            .andExpect(jsonPath("$.[*].interfaceName").value(hasItem(DEFAULT_INTERFACE_NAME)))
            .andExpect(jsonPath("$.[*].sshHost").value(hasItem(DEFAULT_SSH_HOST)))
            .andExpect(jsonPath("$.[*].sshPort").value(hasItem(DEFAULT_SSH_PORT)))
            .andExpect(jsonPath("$.[*].sshKey").value(hasItem(DEFAULT_SSH_KEY.toString())));
    }
    
    @Test
    @Transactional
    public void getNetworkSwitch() throws Exception {
        // Initialize the database
        networkSwitchRepository.saveAndFlush(networkSwitch);

        // Get the networkSwitch
        restNetworkSwitchMockMvc.perform(get("/api/network-switches/{id}", networkSwitch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(networkSwitch.getId().intValue()))
            .andExpect(jsonPath("$.interfaceName").value(DEFAULT_INTERFACE_NAME))
            .andExpect(jsonPath("$.sshHost").value(DEFAULT_SSH_HOST))
            .andExpect(jsonPath("$.sshPort").value(DEFAULT_SSH_PORT))
            .andExpect(jsonPath("$.sshKey").value(DEFAULT_SSH_KEY.toString()));
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
            .interfaceName(UPDATED_INTERFACE_NAME)
            .sshHost(UPDATED_SSH_HOST)
            .sshPort(UPDATED_SSH_PORT)
            .sshKey(UPDATED_SSH_KEY);

        restNetworkSwitchMockMvc.perform(put("/api/network-switches")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedNetworkSwitch)))
            .andExpect(status().isOk());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeUpdate);
        NetworkSwitch testNetworkSwitch = networkSwitchList.get(networkSwitchList.size() - 1);
        assertThat(testNetworkSwitch.getInterfaceName()).isEqualTo(UPDATED_INTERFACE_NAME);
        assertThat(testNetworkSwitch.getSshHost()).isEqualTo(UPDATED_SSH_HOST);
        assertThat(testNetworkSwitch.getSshPort()).isEqualTo(UPDATED_SSH_PORT);
        assertThat(testNetworkSwitch.getSshKey()).isEqualTo(UPDATED_SSH_KEY);
    }

    @Test
    @Transactional
    public void updateNonExistingNetworkSwitch() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchRepository.findAll().size();

        // Create the NetworkSwitch

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNetworkSwitchMockMvc.perform(put("/api/network-switches")
            .contentType(MediaType.APPLICATION_JSON)
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
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
