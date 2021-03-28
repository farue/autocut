package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.repository.NetworkSwitchRepository;
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
 * Integration tests for the {@link NetworkSwitchResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NetworkSwitchResourceIT {

    private static final String DEFAULT_INTERFACE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_INTERFACE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SSH_HOST = "AAAAAAAAAA";
    private static final String UPDATED_SSH_HOST = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/network-switches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

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
        NetworkSwitch networkSwitch = new NetworkSwitch().interfaceName(DEFAULT_INTERFACE_NAME).sshHost(DEFAULT_SSH_HOST);
        return networkSwitch;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NetworkSwitch createUpdatedEntity(EntityManager em) {
        NetworkSwitch networkSwitch = new NetworkSwitch().interfaceName(UPDATED_INTERFACE_NAME).sshHost(UPDATED_SSH_HOST);
        return networkSwitch;
    }

    @BeforeEach
    public void initTest() {
        networkSwitch = createEntity(em);
    }

    @Test
    @Transactional
    void createNetworkSwitch() throws Exception {
        int databaseSizeBeforeCreate = networkSwitchRepository.findAll().size();
        // Create the NetworkSwitch
        restNetworkSwitchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(networkSwitch)))
            .andExpect(status().isCreated());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeCreate + 1);
        NetworkSwitch testNetworkSwitch = networkSwitchList.get(networkSwitchList.size() - 1);
        assertThat(testNetworkSwitch.getInterfaceName()).isEqualTo(DEFAULT_INTERFACE_NAME);
        assertThat(testNetworkSwitch.getSshHost()).isEqualTo(DEFAULT_SSH_HOST);
    }

    @Test
    @Transactional
    void createNetworkSwitchWithExistingId() throws Exception {
        // Create the NetworkSwitch with an existing ID
        networkSwitch.setId(1L);

        int databaseSizeBeforeCreate = networkSwitchRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNetworkSwitchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(networkSwitch)))
            .andExpect(status().isBadRequest());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkInterfaceNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = networkSwitchRepository.findAll().size();
        // set the field null
        networkSwitch.setInterfaceName(null);

        // Create the NetworkSwitch, which fails.

        restNetworkSwitchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(networkSwitch)))
            .andExpect(status().isBadRequest());

        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSshHostIsRequired() throws Exception {
        int databaseSizeBeforeTest = networkSwitchRepository.findAll().size();
        // set the field null
        networkSwitch.setSshHost(null);

        // Create the NetworkSwitch, which fails.

        restNetworkSwitchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(networkSwitch)))
            .andExpect(status().isBadRequest());

        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNetworkSwitches() throws Exception {
        // Initialize the database
        networkSwitchRepository.saveAndFlush(networkSwitch);

        // Get all the networkSwitchList
        restNetworkSwitchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(networkSwitch.getId().intValue())))
            .andExpect(jsonPath("$.[*].interfaceName").value(hasItem(DEFAULT_INTERFACE_NAME)))
            .andExpect(jsonPath("$.[*].sshHost").value(hasItem(DEFAULT_SSH_HOST)));
    }

    @Test
    @Transactional
    void getNetworkSwitch() throws Exception {
        // Initialize the database
        networkSwitchRepository.saveAndFlush(networkSwitch);

        // Get the networkSwitch
        restNetworkSwitchMockMvc
            .perform(get(ENTITY_API_URL_ID, networkSwitch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(networkSwitch.getId().intValue()))
            .andExpect(jsonPath("$.interfaceName").value(DEFAULT_INTERFACE_NAME))
            .andExpect(jsonPath("$.sshHost").value(DEFAULT_SSH_HOST));
    }

    @Test
    @Transactional
    void getNonExistingNetworkSwitch() throws Exception {
        // Get the networkSwitch
        restNetworkSwitchMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewNetworkSwitch() throws Exception {
        // Initialize the database
        networkSwitchRepository.saveAndFlush(networkSwitch);

        int databaseSizeBeforeUpdate = networkSwitchRepository.findAll().size();

        // Update the networkSwitch
        NetworkSwitch updatedNetworkSwitch = networkSwitchRepository.findById(networkSwitch.getId()).get();
        // Disconnect from session so that the updates on updatedNetworkSwitch are not directly saved in db
        em.detach(updatedNetworkSwitch);
        updatedNetworkSwitch.interfaceName(UPDATED_INTERFACE_NAME).sshHost(UPDATED_SSH_HOST);

        restNetworkSwitchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedNetworkSwitch.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedNetworkSwitch))
            )
            .andExpect(status().isOk());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeUpdate);
        NetworkSwitch testNetworkSwitch = networkSwitchList.get(networkSwitchList.size() - 1);
        assertThat(testNetworkSwitch.getInterfaceName()).isEqualTo(UPDATED_INTERFACE_NAME);
        assertThat(testNetworkSwitch.getSshHost()).isEqualTo(UPDATED_SSH_HOST);
    }

    @Test
    @Transactional
    void putNonExistingNetworkSwitch() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchRepository.findAll().size();
        networkSwitch.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNetworkSwitchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, networkSwitch.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(networkSwitch))
            )
            .andExpect(status().isBadRequest());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNetworkSwitch() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchRepository.findAll().size();
        networkSwitch.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNetworkSwitchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(networkSwitch))
            )
            .andExpect(status().isBadRequest());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNetworkSwitch() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchRepository.findAll().size();
        networkSwitch.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNetworkSwitchMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(networkSwitch)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNetworkSwitchWithPatch() throws Exception {
        // Initialize the database
        networkSwitchRepository.saveAndFlush(networkSwitch);

        int databaseSizeBeforeUpdate = networkSwitchRepository.findAll().size();

        // Update the networkSwitch using partial update
        NetworkSwitch partialUpdatedNetworkSwitch = new NetworkSwitch();
        partialUpdatedNetworkSwitch.setId(networkSwitch.getId());

        partialUpdatedNetworkSwitch.sshHost(UPDATED_SSH_HOST);

        restNetworkSwitchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNetworkSwitch.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNetworkSwitch))
            )
            .andExpect(status().isOk());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeUpdate);
        NetworkSwitch testNetworkSwitch = networkSwitchList.get(networkSwitchList.size() - 1);
        assertThat(testNetworkSwitch.getInterfaceName()).isEqualTo(DEFAULT_INTERFACE_NAME);
        assertThat(testNetworkSwitch.getSshHost()).isEqualTo(UPDATED_SSH_HOST);
    }

    @Test
    @Transactional
    void fullUpdateNetworkSwitchWithPatch() throws Exception {
        // Initialize the database
        networkSwitchRepository.saveAndFlush(networkSwitch);

        int databaseSizeBeforeUpdate = networkSwitchRepository.findAll().size();

        // Update the networkSwitch using partial update
        NetworkSwitch partialUpdatedNetworkSwitch = new NetworkSwitch();
        partialUpdatedNetworkSwitch.setId(networkSwitch.getId());

        partialUpdatedNetworkSwitch.interfaceName(UPDATED_INTERFACE_NAME).sshHost(UPDATED_SSH_HOST);

        restNetworkSwitchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNetworkSwitch.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNetworkSwitch))
            )
            .andExpect(status().isOk());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeUpdate);
        NetworkSwitch testNetworkSwitch = networkSwitchList.get(networkSwitchList.size() - 1);
        assertThat(testNetworkSwitch.getInterfaceName()).isEqualTo(UPDATED_INTERFACE_NAME);
        assertThat(testNetworkSwitch.getSshHost()).isEqualTo(UPDATED_SSH_HOST);
    }

    @Test
    @Transactional
    void patchNonExistingNetworkSwitch() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchRepository.findAll().size();
        networkSwitch.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNetworkSwitchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, networkSwitch.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(networkSwitch))
            )
            .andExpect(status().isBadRequest());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNetworkSwitch() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchRepository.findAll().size();
        networkSwitch.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNetworkSwitchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(networkSwitch))
            )
            .andExpect(status().isBadRequest());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNetworkSwitch() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchRepository.findAll().size();
        networkSwitch.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNetworkSwitchMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(networkSwitch))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NetworkSwitch in the database
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNetworkSwitch() throws Exception {
        // Initialize the database
        networkSwitchRepository.saveAndFlush(networkSwitch);

        int databaseSizeBeforeDelete = networkSwitchRepository.findAll().size();

        // Delete the networkSwitch
        restNetworkSwitchMockMvc
            .perform(delete(ENTITY_API_URL_ID, networkSwitch.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<NetworkSwitch> networkSwitchList = networkSwitchRepository.findAll();
        assertThat(networkSwitchList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
