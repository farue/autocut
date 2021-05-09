package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.NetworkSwitchStatus;
import de.farue.autocut.repository.NetworkSwitchStatusRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link NetworkSwitchStatusResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class NetworkSwitchStatusResourceIT {

    private static final String DEFAULT_PORT = "AAAAAAAAAA";
    private static final String UPDATED_PORT = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_VLAN = "AAAAAAAAAA";
    private static final String UPDATED_VLAN = "BBBBBBBBBB";

    private static final String DEFAULT_SPEED = "AAAAAAAAAA";
    private static final String UPDATED_SPEED = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/network-switch-statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NetworkSwitchStatusRepository networkSwitchStatusRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNetworkSwitchStatusMockMvc;

    private NetworkSwitchStatus networkSwitchStatus;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NetworkSwitchStatus createEntity(EntityManager em) {
        NetworkSwitchStatus networkSwitchStatus = new NetworkSwitchStatus()
            .port(DEFAULT_PORT)
            .name(DEFAULT_NAME)
            .status(DEFAULT_STATUS)
            .vlan(DEFAULT_VLAN)
            .speed(DEFAULT_SPEED)
            .type(DEFAULT_TYPE)
            .timestamp(DEFAULT_TIMESTAMP);
        return networkSwitchStatus;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NetworkSwitchStatus createUpdatedEntity(EntityManager em) {
        NetworkSwitchStatus networkSwitchStatus = new NetworkSwitchStatus()
            .port(UPDATED_PORT)
            .name(UPDATED_NAME)
            .status(UPDATED_STATUS)
            .vlan(UPDATED_VLAN)
            .speed(UPDATED_SPEED)
            .type(UPDATED_TYPE)
            .timestamp(UPDATED_TIMESTAMP);
        return networkSwitchStatus;
    }

    @BeforeEach
    public void initTest() {
        networkSwitchStatus = createEntity(em);
    }

    @Test
    @Transactional
    void createNetworkSwitchStatus() throws Exception {
        int databaseSizeBeforeCreate = networkSwitchStatusRepository.findAll().size();
        // Create the NetworkSwitchStatus
        restNetworkSwitchStatusMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(networkSwitchStatus))
            )
            .andExpect(status().isCreated());

        // Validate the NetworkSwitchStatus in the database
        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeCreate + 1);
        NetworkSwitchStatus testNetworkSwitchStatus = networkSwitchStatusList.get(networkSwitchStatusList.size() - 1);
        assertThat(testNetworkSwitchStatus.getPort()).isEqualTo(DEFAULT_PORT);
        assertThat(testNetworkSwitchStatus.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testNetworkSwitchStatus.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testNetworkSwitchStatus.getVlan()).isEqualTo(DEFAULT_VLAN);
        assertThat(testNetworkSwitchStatus.getSpeed()).isEqualTo(DEFAULT_SPEED);
        assertThat(testNetworkSwitchStatus.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testNetworkSwitchStatus.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
    }

    @Test
    @Transactional
    void createNetworkSwitchStatusWithExistingId() throws Exception {
        // Create the NetworkSwitchStatus with an existing ID
        networkSwitchStatus.setId(1L);

        int databaseSizeBeforeCreate = networkSwitchStatusRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNetworkSwitchStatusMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(networkSwitchStatus))
            )
            .andExpect(status().isBadRequest());

        // Validate the NetworkSwitchStatus in the database
        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPortIsRequired() throws Exception {
        int databaseSizeBeforeTest = networkSwitchStatusRepository.findAll().size();
        // set the field null
        networkSwitchStatus.setPort(null);

        // Create the NetworkSwitchStatus, which fails.

        restNetworkSwitchStatusMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(networkSwitchStatus))
            )
            .andExpect(status().isBadRequest());

        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = networkSwitchStatusRepository.findAll().size();
        // set the field null
        networkSwitchStatus.setTimestamp(null);

        // Create the NetworkSwitchStatus, which fails.

        restNetworkSwitchStatusMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(networkSwitchStatus))
            )
            .andExpect(status().isBadRequest());

        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNetworkSwitchStatuses() throws Exception {
        // Initialize the database
        networkSwitchStatusRepository.saveAndFlush(networkSwitchStatus);

        // Get all the networkSwitchStatusList
        restNetworkSwitchStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(networkSwitchStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].port").value(hasItem(DEFAULT_PORT)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].vlan").value(hasItem(DEFAULT_VLAN)))
            .andExpect(jsonPath("$.[*].speed").value(hasItem(DEFAULT_SPEED)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())));
    }

    @Test
    @Transactional
    void getNetworkSwitchStatus() throws Exception {
        // Initialize the database
        networkSwitchStatusRepository.saveAndFlush(networkSwitchStatus);

        // Get the networkSwitchStatus
        restNetworkSwitchStatusMockMvc
            .perform(get(ENTITY_API_URL_ID, networkSwitchStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(networkSwitchStatus.getId().intValue()))
            .andExpect(jsonPath("$.port").value(DEFAULT_PORT))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.vlan").value(DEFAULT_VLAN))
            .andExpect(jsonPath("$.speed").value(DEFAULT_SPEED))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()));
    }

    @Test
    @Transactional
    void getNonExistingNetworkSwitchStatus() throws Exception {
        // Get the networkSwitchStatus
        restNetworkSwitchStatusMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewNetworkSwitchStatus() throws Exception {
        // Initialize the database
        networkSwitchStatusRepository.saveAndFlush(networkSwitchStatus);

        int databaseSizeBeforeUpdate = networkSwitchStatusRepository.findAll().size();

        // Update the networkSwitchStatus
        NetworkSwitchStatus updatedNetworkSwitchStatus = networkSwitchStatusRepository.findById(networkSwitchStatus.getId()).get();
        // Disconnect from session so that the updates on updatedNetworkSwitchStatus are not directly saved in db
        em.detach(updatedNetworkSwitchStatus);
        updatedNetworkSwitchStatus
            .port(UPDATED_PORT)
            .name(UPDATED_NAME)
            .status(UPDATED_STATUS)
            .vlan(UPDATED_VLAN)
            .speed(UPDATED_SPEED)
            .type(UPDATED_TYPE)
            .timestamp(UPDATED_TIMESTAMP);

        restNetworkSwitchStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedNetworkSwitchStatus.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedNetworkSwitchStatus))
            )
            .andExpect(status().isOk());

        // Validate the NetworkSwitchStatus in the database
        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeUpdate);
        NetworkSwitchStatus testNetworkSwitchStatus = networkSwitchStatusList.get(networkSwitchStatusList.size() - 1);
        assertThat(testNetworkSwitchStatus.getPort()).isEqualTo(UPDATED_PORT);
        assertThat(testNetworkSwitchStatus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testNetworkSwitchStatus.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testNetworkSwitchStatus.getVlan()).isEqualTo(UPDATED_VLAN);
        assertThat(testNetworkSwitchStatus.getSpeed()).isEqualTo(UPDATED_SPEED);
        assertThat(testNetworkSwitchStatus.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testNetworkSwitchStatus.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void putNonExistingNetworkSwitchStatus() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchStatusRepository.findAll().size();
        networkSwitchStatus.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNetworkSwitchStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, networkSwitchStatus.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(networkSwitchStatus))
            )
            .andExpect(status().isBadRequest());

        // Validate the NetworkSwitchStatus in the database
        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNetworkSwitchStatus() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchStatusRepository.findAll().size();
        networkSwitchStatus.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNetworkSwitchStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(networkSwitchStatus))
            )
            .andExpect(status().isBadRequest());

        // Validate the NetworkSwitchStatus in the database
        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNetworkSwitchStatus() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchStatusRepository.findAll().size();
        networkSwitchStatus.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNetworkSwitchStatusMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(networkSwitchStatus))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NetworkSwitchStatus in the database
        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNetworkSwitchStatusWithPatch() throws Exception {
        // Initialize the database
        networkSwitchStatusRepository.saveAndFlush(networkSwitchStatus);

        int databaseSizeBeforeUpdate = networkSwitchStatusRepository.findAll().size();

        // Update the networkSwitchStatus using partial update
        NetworkSwitchStatus partialUpdatedNetworkSwitchStatus = new NetworkSwitchStatus();
        partialUpdatedNetworkSwitchStatus.setId(networkSwitchStatus.getId());

        partialUpdatedNetworkSwitchStatus.port(UPDATED_PORT).name(UPDATED_NAME).speed(UPDATED_SPEED).type(UPDATED_TYPE);

        restNetworkSwitchStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNetworkSwitchStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNetworkSwitchStatus))
            )
            .andExpect(status().isOk());

        // Validate the NetworkSwitchStatus in the database
        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeUpdate);
        NetworkSwitchStatus testNetworkSwitchStatus = networkSwitchStatusList.get(networkSwitchStatusList.size() - 1);
        assertThat(testNetworkSwitchStatus.getPort()).isEqualTo(UPDATED_PORT);
        assertThat(testNetworkSwitchStatus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testNetworkSwitchStatus.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testNetworkSwitchStatus.getVlan()).isEqualTo(DEFAULT_VLAN);
        assertThat(testNetworkSwitchStatus.getSpeed()).isEqualTo(UPDATED_SPEED);
        assertThat(testNetworkSwitchStatus.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testNetworkSwitchStatus.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
    }

    @Test
    @Transactional
    void fullUpdateNetworkSwitchStatusWithPatch() throws Exception {
        // Initialize the database
        networkSwitchStatusRepository.saveAndFlush(networkSwitchStatus);

        int databaseSizeBeforeUpdate = networkSwitchStatusRepository.findAll().size();

        // Update the networkSwitchStatus using partial update
        NetworkSwitchStatus partialUpdatedNetworkSwitchStatus = new NetworkSwitchStatus();
        partialUpdatedNetworkSwitchStatus.setId(networkSwitchStatus.getId());

        partialUpdatedNetworkSwitchStatus
            .port(UPDATED_PORT)
            .name(UPDATED_NAME)
            .status(UPDATED_STATUS)
            .vlan(UPDATED_VLAN)
            .speed(UPDATED_SPEED)
            .type(UPDATED_TYPE)
            .timestamp(UPDATED_TIMESTAMP);

        restNetworkSwitchStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNetworkSwitchStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNetworkSwitchStatus))
            )
            .andExpect(status().isOk());

        // Validate the NetworkSwitchStatus in the database
        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeUpdate);
        NetworkSwitchStatus testNetworkSwitchStatus = networkSwitchStatusList.get(networkSwitchStatusList.size() - 1);
        assertThat(testNetworkSwitchStatus.getPort()).isEqualTo(UPDATED_PORT);
        assertThat(testNetworkSwitchStatus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testNetworkSwitchStatus.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testNetworkSwitchStatus.getVlan()).isEqualTo(UPDATED_VLAN);
        assertThat(testNetworkSwitchStatus.getSpeed()).isEqualTo(UPDATED_SPEED);
        assertThat(testNetworkSwitchStatus.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testNetworkSwitchStatus.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void patchNonExistingNetworkSwitchStatus() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchStatusRepository.findAll().size();
        networkSwitchStatus.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNetworkSwitchStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, networkSwitchStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(networkSwitchStatus))
            )
            .andExpect(status().isBadRequest());

        // Validate the NetworkSwitchStatus in the database
        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNetworkSwitchStatus() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchStatusRepository.findAll().size();
        networkSwitchStatus.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNetworkSwitchStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(networkSwitchStatus))
            )
            .andExpect(status().isBadRequest());

        // Validate the NetworkSwitchStatus in the database
        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNetworkSwitchStatus() throws Exception {
        int databaseSizeBeforeUpdate = networkSwitchStatusRepository.findAll().size();
        networkSwitchStatus.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNetworkSwitchStatusMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(networkSwitchStatus))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NetworkSwitchStatus in the database
        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNetworkSwitchStatus() throws Exception {
        // Initialize the database
        networkSwitchStatusRepository.saveAndFlush(networkSwitchStatus);

        int databaseSizeBeforeDelete = networkSwitchStatusRepository.findAll().size();

        // Delete the networkSwitchStatus
        restNetworkSwitchStatusMockMvc
            .perform(delete(ENTITY_API_URL_ID, networkSwitchStatus.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<NetworkSwitchStatus> networkSwitchStatusList = networkSwitchStatusRepository.findAll();
        assertThat(networkSwitchStatusList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
