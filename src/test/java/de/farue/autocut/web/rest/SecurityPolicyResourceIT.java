package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.SecurityPolicy;
import de.farue.autocut.domain.enumeration.Access;
import de.farue.autocut.domain.enumeration.ProtectionUnits;
import de.farue.autocut.repository.SecurityPolicyRepository;
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
 * Integration tests for the {@link SecurityPolicyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SecurityPolicyResourceIT {

    private static final ProtectionUnits DEFAULT_PROTECTION_UNIT = ProtectionUnits.BANK_TRANSACTIONS;
    private static final ProtectionUnits UPDATED_PROTECTION_UNIT = ProtectionUnits.TENANT_MANAGEMENT;

    private static final Access DEFAULT_ACCESS = Access.READ_ALLOW;
    private static final Access UPDATED_ACCESS = Access.READ_WRITE_ALLOW;

    private static final String ENTITY_API_URL = "/api/security-policies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SecurityPolicyRepository securityPolicyRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSecurityPolicyMockMvc;

    private SecurityPolicy securityPolicy;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SecurityPolicy createEntity(EntityManager em) {
        SecurityPolicy securityPolicy = new SecurityPolicy().protectionUnit(DEFAULT_PROTECTION_UNIT).access(DEFAULT_ACCESS);
        return securityPolicy;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SecurityPolicy createUpdatedEntity(EntityManager em) {
        SecurityPolicy securityPolicy = new SecurityPolicy().protectionUnit(UPDATED_PROTECTION_UNIT).access(UPDATED_ACCESS);
        return securityPolicy;
    }

    @BeforeEach
    public void initTest() {
        securityPolicy = createEntity(em);
    }

    @Test
    @Transactional
    void createSecurityPolicy() throws Exception {
        int databaseSizeBeforeCreate = securityPolicyRepository.findAll().size();
        // Create the SecurityPolicy
        restSecurityPolicyMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(securityPolicy))
            )
            .andExpect(status().isCreated());

        // Validate the SecurityPolicy in the database
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeCreate + 1);
        SecurityPolicy testSecurityPolicy = securityPolicyList.get(securityPolicyList.size() - 1);
        assertThat(testSecurityPolicy.getProtectionUnit()).isEqualTo(DEFAULT_PROTECTION_UNIT);
        assertThat(testSecurityPolicy.getAccess()).isEqualTo(DEFAULT_ACCESS);
    }

    @Test
    @Transactional
    void createSecurityPolicyWithExistingId() throws Exception {
        // Create the SecurityPolicy with an existing ID
        securityPolicy.setId(1L);

        int databaseSizeBeforeCreate = securityPolicyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSecurityPolicyMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(securityPolicy))
            )
            .andExpect(status().isBadRequest());

        // Validate the SecurityPolicy in the database
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkProtectionUnitIsRequired() throws Exception {
        int databaseSizeBeforeTest = securityPolicyRepository.findAll().size();
        // set the field null
        securityPolicy.setProtectionUnit(null);

        // Create the SecurityPolicy, which fails.

        restSecurityPolicyMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(securityPolicy))
            )
            .andExpect(status().isBadRequest());

        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAccessIsRequired() throws Exception {
        int databaseSizeBeforeTest = securityPolicyRepository.findAll().size();
        // set the field null
        securityPolicy.setAccess(null);

        // Create the SecurityPolicy, which fails.

        restSecurityPolicyMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(securityPolicy))
            )
            .andExpect(status().isBadRequest());

        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSecurityPolicies() throws Exception {
        // Initialize the database
        securityPolicyRepository.saveAndFlush(securityPolicy);

        // Get all the securityPolicyList
        restSecurityPolicyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(securityPolicy.getId().intValue())))
            .andExpect(jsonPath("$.[*].protectionUnit").value(hasItem(DEFAULT_PROTECTION_UNIT.toString())))
            .andExpect(jsonPath("$.[*].access").value(hasItem(DEFAULT_ACCESS.toString())));
    }

    @Test
    @Transactional
    void getSecurityPolicy() throws Exception {
        // Initialize the database
        securityPolicyRepository.saveAndFlush(securityPolicy);

        // Get the securityPolicy
        restSecurityPolicyMockMvc
            .perform(get(ENTITY_API_URL_ID, securityPolicy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(securityPolicy.getId().intValue()))
            .andExpect(jsonPath("$.protectionUnit").value(DEFAULT_PROTECTION_UNIT.toString()))
            .andExpect(jsonPath("$.access").value(DEFAULT_ACCESS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSecurityPolicy() throws Exception {
        // Get the securityPolicy
        restSecurityPolicyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSecurityPolicy() throws Exception {
        // Initialize the database
        securityPolicyRepository.saveAndFlush(securityPolicy);

        int databaseSizeBeforeUpdate = securityPolicyRepository.findAll().size();

        // Update the securityPolicy
        SecurityPolicy updatedSecurityPolicy = securityPolicyRepository.findById(securityPolicy.getId()).get();
        // Disconnect from session so that the updates on updatedSecurityPolicy are not directly saved in db
        em.detach(updatedSecurityPolicy);
        updatedSecurityPolicy.protectionUnit(UPDATED_PROTECTION_UNIT).access(UPDATED_ACCESS);

        restSecurityPolicyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSecurityPolicy.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSecurityPolicy))
            )
            .andExpect(status().isOk());

        // Validate the SecurityPolicy in the database
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeUpdate);
        SecurityPolicy testSecurityPolicy = securityPolicyList.get(securityPolicyList.size() - 1);
        assertThat(testSecurityPolicy.getProtectionUnit()).isEqualTo(UPDATED_PROTECTION_UNIT);
        assertThat(testSecurityPolicy.getAccess()).isEqualTo(UPDATED_ACCESS);
    }

    @Test
    @Transactional
    void putNonExistingSecurityPolicy() throws Exception {
        int databaseSizeBeforeUpdate = securityPolicyRepository.findAll().size();
        securityPolicy.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSecurityPolicyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, securityPolicy.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(securityPolicy))
            )
            .andExpect(status().isBadRequest());

        // Validate the SecurityPolicy in the database
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSecurityPolicy() throws Exception {
        int databaseSizeBeforeUpdate = securityPolicyRepository.findAll().size();
        securityPolicy.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSecurityPolicyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(securityPolicy))
            )
            .andExpect(status().isBadRequest());

        // Validate the SecurityPolicy in the database
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSecurityPolicy() throws Exception {
        int databaseSizeBeforeUpdate = securityPolicyRepository.findAll().size();
        securityPolicy.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSecurityPolicyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(securityPolicy)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SecurityPolicy in the database
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSecurityPolicyWithPatch() throws Exception {
        // Initialize the database
        securityPolicyRepository.saveAndFlush(securityPolicy);

        int databaseSizeBeforeUpdate = securityPolicyRepository.findAll().size();

        // Update the securityPolicy using partial update
        SecurityPolicy partialUpdatedSecurityPolicy = new SecurityPolicy();
        partialUpdatedSecurityPolicy.setId(securityPolicy.getId());

        restSecurityPolicyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSecurityPolicy.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSecurityPolicy))
            )
            .andExpect(status().isOk());

        // Validate the SecurityPolicy in the database
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeUpdate);
        SecurityPolicy testSecurityPolicy = securityPolicyList.get(securityPolicyList.size() - 1);
        assertThat(testSecurityPolicy.getProtectionUnit()).isEqualTo(DEFAULT_PROTECTION_UNIT);
        assertThat(testSecurityPolicy.getAccess()).isEqualTo(DEFAULT_ACCESS);
    }

    @Test
    @Transactional
    void fullUpdateSecurityPolicyWithPatch() throws Exception {
        // Initialize the database
        securityPolicyRepository.saveAndFlush(securityPolicy);

        int databaseSizeBeforeUpdate = securityPolicyRepository.findAll().size();

        // Update the securityPolicy using partial update
        SecurityPolicy partialUpdatedSecurityPolicy = new SecurityPolicy();
        partialUpdatedSecurityPolicy.setId(securityPolicy.getId());

        partialUpdatedSecurityPolicy.protectionUnit(UPDATED_PROTECTION_UNIT).access(UPDATED_ACCESS);

        restSecurityPolicyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSecurityPolicy.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSecurityPolicy))
            )
            .andExpect(status().isOk());

        // Validate the SecurityPolicy in the database
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeUpdate);
        SecurityPolicy testSecurityPolicy = securityPolicyList.get(securityPolicyList.size() - 1);
        assertThat(testSecurityPolicy.getProtectionUnit()).isEqualTo(UPDATED_PROTECTION_UNIT);
        assertThat(testSecurityPolicy.getAccess()).isEqualTo(UPDATED_ACCESS);
    }

    @Test
    @Transactional
    void patchNonExistingSecurityPolicy() throws Exception {
        int databaseSizeBeforeUpdate = securityPolicyRepository.findAll().size();
        securityPolicy.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSecurityPolicyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, securityPolicy.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(securityPolicy))
            )
            .andExpect(status().isBadRequest());

        // Validate the SecurityPolicy in the database
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSecurityPolicy() throws Exception {
        int databaseSizeBeforeUpdate = securityPolicyRepository.findAll().size();
        securityPolicy.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSecurityPolicyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(securityPolicy))
            )
            .andExpect(status().isBadRequest());

        // Validate the SecurityPolicy in the database
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSecurityPolicy() throws Exception {
        int databaseSizeBeforeUpdate = securityPolicyRepository.findAll().size();
        securityPolicy.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSecurityPolicyMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(securityPolicy))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SecurityPolicy in the database
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSecurityPolicy() throws Exception {
        // Initialize the database
        securityPolicyRepository.saveAndFlush(securityPolicy);

        int databaseSizeBeforeDelete = securityPolicyRepository.findAll().size();

        // Delete the securityPolicy
        restSecurityPolicyMockMvc
            .perform(delete(ENTITY_API_URL_ID, securityPolicy.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
