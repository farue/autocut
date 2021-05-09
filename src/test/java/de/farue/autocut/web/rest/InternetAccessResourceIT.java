package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.InternetAccess;
import de.farue.autocut.repository.InternetAccessRepository;
import de.farue.autocut.security.AuthoritiesConstants;
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
 * Integration tests for the {@link InternetAccessResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class InternetAccessResourceIT {

    private static final String DEFAULT_IP_1 = "AAAAAAAAAA";
    private static final String UPDATED_IP_1 = "BBBBBBBBBB";

    private static final String DEFAULT_IP_2 = "AAAAAAAAAA";
    private static final String UPDATED_IP_2 = "BBBBBBBBBB";

    private static final String DEFAULT_SWITCH_INTERFACE = "AAAAAAAAAA";
    private static final String UPDATED_SWITCH_INTERFACE = "BBBBBBBBBB";

    private static final Integer DEFAULT_PORT = 1;
    private static final Integer UPDATED_PORT = 2;

    private static final String ENTITY_API_URL = "/api/internet-accesses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InternetAccessRepository internetAccessRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInternetAccessMockMvc;

    private InternetAccess internetAccess;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InternetAccess createEntity(EntityManager em) {
        InternetAccess internetAccess = new InternetAccess()
            .ip1(DEFAULT_IP_1)
            .ip2(DEFAULT_IP_2)
            .switchInterface(DEFAULT_SWITCH_INTERFACE)
            .port(DEFAULT_PORT);
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
            .ip1(UPDATED_IP_1)
            .ip2(UPDATED_IP_2)
            .switchInterface(UPDATED_SWITCH_INTERFACE)
            .port(UPDATED_PORT);
        return internetAccess;
    }

    @BeforeEach
    public void initTest() {
        internetAccess = createEntity(em);
    }

    @Test
    @Transactional
    void createInternetAccess() throws Exception {
        int databaseSizeBeforeCreate = internetAccessRepository.findAll().size();
        // Create the InternetAccess
        restInternetAccessMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internetAccess))
            )
            .andExpect(status().isCreated());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeCreate + 1);
        InternetAccess testInternetAccess = internetAccessList.get(internetAccessList.size() - 1);
        assertThat(testInternetAccess.getIp1()).isEqualTo(DEFAULT_IP_1);
        assertThat(testInternetAccess.getIp2()).isEqualTo(DEFAULT_IP_2);
        assertThat(testInternetAccess.getSwitchInterface()).isEqualTo(DEFAULT_SWITCH_INTERFACE);
        assertThat(testInternetAccess.getPort()).isEqualTo(DEFAULT_PORT);
    }

    @Test
    @Transactional
    void createInternetAccessWithExistingId() throws Exception {
        // Create the InternetAccess with an existing ID
        internetAccess.setId(1L);

        int databaseSizeBeforeCreate = internetAccessRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInternetAccessMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internetAccess))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIp1IsRequired() throws Exception {
        int databaseSizeBeforeTest = internetAccessRepository.findAll().size();
        // set the field null
        internetAccess.setIp1(null);

        // Create the InternetAccess, which fails.

        restInternetAccessMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internetAccess))
            )
            .andExpect(status().isBadRequest());

        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIp2IsRequired() throws Exception {
        int databaseSizeBeforeTest = internetAccessRepository.findAll().size();
        // set the field null
        internetAccess.setIp2(null);

        // Create the InternetAccess, which fails.

        restInternetAccessMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internetAccess))
            )
            .andExpect(status().isBadRequest());

        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSwitchInterfaceIsRequired() throws Exception {
        int databaseSizeBeforeTest = internetAccessRepository.findAll().size();
        // set the field null
        internetAccess.setSwitchInterface(null);

        // Create the InternetAccess, which fails.

        restInternetAccessMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internetAccess))
            )
            .andExpect(status().isBadRequest());

        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPortIsRequired() throws Exception {
        int databaseSizeBeforeTest = internetAccessRepository.findAll().size();
        // set the field null
        internetAccess.setPort(null);

        // Create the InternetAccess, which fails.

        restInternetAccessMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internetAccess))
            )
            .andExpect(status().isBadRequest());

        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInternetAccesses() throws Exception {
        // Initialize the database
        internetAccessRepository.saveAndFlush(internetAccess);

        // Get all the internetAccessList
        restInternetAccessMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(internetAccess.getId().intValue())))
            .andExpect(jsonPath("$.[*].ip1").value(hasItem(DEFAULT_IP_1)))
            .andExpect(jsonPath("$.[*].ip2").value(hasItem(DEFAULT_IP_2)))
            .andExpect(jsonPath("$.[*].switchInterface").value(hasItem(DEFAULT_SWITCH_INTERFACE)))
            .andExpect(jsonPath("$.[*].port").value(hasItem(DEFAULT_PORT)));
    }

    @Test
    @Transactional
    void getInternetAccess() throws Exception {
        // Initialize the database
        internetAccessRepository.saveAndFlush(internetAccess);

        // Get the internetAccess
        restInternetAccessMockMvc
            .perform(get(ENTITY_API_URL_ID, internetAccess.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(internetAccess.getId().intValue()))
            .andExpect(jsonPath("$.ip1").value(DEFAULT_IP_1))
            .andExpect(jsonPath("$.ip2").value(DEFAULT_IP_2))
            .andExpect(jsonPath("$.switchInterface").value(DEFAULT_SWITCH_INTERFACE))
            .andExpect(jsonPath("$.port").value(DEFAULT_PORT));
    }

    @Test
    @Transactional
    void getNonExistingInternetAccess() throws Exception {
        // Get the internetAccess
        restInternetAccessMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewInternetAccess() throws Exception {
        // Initialize the database
        internetAccessRepository.saveAndFlush(internetAccess);

        int databaseSizeBeforeUpdate = internetAccessRepository.findAll().size();

        // Update the internetAccess
        InternetAccess updatedInternetAccess = internetAccessRepository.findById(internetAccess.getId()).get();
        // Disconnect from session so that the updates on updatedInternetAccess are not directly saved in db
        em.detach(updatedInternetAccess);
        updatedInternetAccess.ip1(UPDATED_IP_1).ip2(UPDATED_IP_2).switchInterface(UPDATED_SWITCH_INTERFACE).port(UPDATED_PORT);

        restInternetAccessMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInternetAccess.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedInternetAccess))
            )
            .andExpect(status().isOk());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeUpdate);
        InternetAccess testInternetAccess = internetAccessList.get(internetAccessList.size() - 1);
        assertThat(testInternetAccess.getIp1()).isEqualTo(UPDATED_IP_1);
        assertThat(testInternetAccess.getIp2()).isEqualTo(UPDATED_IP_2);
        assertThat(testInternetAccess.getSwitchInterface()).isEqualTo(UPDATED_SWITCH_INTERFACE);
        assertThat(testInternetAccess.getPort()).isEqualTo(UPDATED_PORT);
    }

    @Test
    @Transactional
    void putNonExistingInternetAccess() throws Exception {
        int databaseSizeBeforeUpdate = internetAccessRepository.findAll().size();
        internetAccess.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInternetAccessMockMvc
            .perform(
                put(ENTITY_API_URL_ID, internetAccess.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(internetAccess))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInternetAccess() throws Exception {
        int databaseSizeBeforeUpdate = internetAccessRepository.findAll().size();
        internetAccess.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternetAccessMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(internetAccess))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInternetAccess() throws Exception {
        int databaseSizeBeforeUpdate = internetAccessRepository.findAll().size();
        internetAccess.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternetAccessMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internetAccess)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInternetAccessWithPatch() throws Exception {
        // Initialize the database
        internetAccessRepository.saveAndFlush(internetAccess);

        int databaseSizeBeforeUpdate = internetAccessRepository.findAll().size();

        // Update the internetAccess using partial update
        InternetAccess partialUpdatedInternetAccess = new InternetAccess();
        partialUpdatedInternetAccess.setId(internetAccess.getId());

        partialUpdatedInternetAccess.ip1(UPDATED_IP_1).switchInterface(UPDATED_SWITCH_INTERFACE);

        restInternetAccessMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInternetAccess.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInternetAccess))
            )
            .andExpect(status().isOk());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeUpdate);
        InternetAccess testInternetAccess = internetAccessList.get(internetAccessList.size() - 1);
        assertThat(testInternetAccess.getIp1()).isEqualTo(UPDATED_IP_1);
        assertThat(testInternetAccess.getIp2()).isEqualTo(DEFAULT_IP_2);
        assertThat(testInternetAccess.getSwitchInterface()).isEqualTo(UPDATED_SWITCH_INTERFACE);
        assertThat(testInternetAccess.getPort()).isEqualTo(DEFAULT_PORT);
    }

    @Test
    @Transactional
    void fullUpdateInternetAccessWithPatch() throws Exception {
        // Initialize the database
        internetAccessRepository.saveAndFlush(internetAccess);

        int databaseSizeBeforeUpdate = internetAccessRepository.findAll().size();

        // Update the internetAccess using partial update
        InternetAccess partialUpdatedInternetAccess = new InternetAccess();
        partialUpdatedInternetAccess.setId(internetAccess.getId());

        partialUpdatedInternetAccess.ip1(UPDATED_IP_1).ip2(UPDATED_IP_2).switchInterface(UPDATED_SWITCH_INTERFACE).port(UPDATED_PORT);

        restInternetAccessMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInternetAccess.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInternetAccess))
            )
            .andExpect(status().isOk());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeUpdate);
        InternetAccess testInternetAccess = internetAccessList.get(internetAccessList.size() - 1);
        assertThat(testInternetAccess.getIp1()).isEqualTo(UPDATED_IP_1);
        assertThat(testInternetAccess.getIp2()).isEqualTo(UPDATED_IP_2);
        assertThat(testInternetAccess.getSwitchInterface()).isEqualTo(UPDATED_SWITCH_INTERFACE);
        assertThat(testInternetAccess.getPort()).isEqualTo(UPDATED_PORT);
    }

    @Test
    @Transactional
    void patchNonExistingInternetAccess() throws Exception {
        int databaseSizeBeforeUpdate = internetAccessRepository.findAll().size();
        internetAccess.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInternetAccessMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, internetAccess.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(internetAccess))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInternetAccess() throws Exception {
        int databaseSizeBeforeUpdate = internetAccessRepository.findAll().size();
        internetAccess.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternetAccessMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(internetAccess))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInternetAccess() throws Exception {
        int databaseSizeBeforeUpdate = internetAccessRepository.findAll().size();
        internetAccess.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternetAccessMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(internetAccess))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InternetAccess in the database
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInternetAccess() throws Exception {
        // Initialize the database
        internetAccessRepository.saveAndFlush(internetAccess);

        int databaseSizeBeforeDelete = internetAccessRepository.findAll().size();

        // Delete the internetAccess
        restInternetAccessMockMvc
            .perform(delete(ENTITY_API_URL_ID, internetAccess.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<InternetAccess> internetAccessList = internetAccessRepository.findAll();
        assertThat(internetAccessList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
