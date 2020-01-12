package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.SecurityPolicy;
import de.farue.autocut.repository.SecurityPolicyRepository;
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

import de.farue.autocut.domain.enumeration.ProtectionUnits;
import de.farue.autocut.domain.enumeration.Access;
/**
 * Integration tests for the {@link SecurityPolicyResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
public class SecurityPolicyResourceIT {

    private static final ProtectionUnits DEFAULT_PROTECTION_UNIT = ProtectionUnits.BANK_TRANSACTIONS;
    private static final ProtectionUnits UPDATED_PROTECTION_UNIT = ProtectionUnits.TENANT_MANAGEMENT;

    private static final Access DEFAULT_ACCESS = Access.READ_ALLOW;
    private static final Access UPDATED_ACCESS = Access.READ_WRITE_ALLOW;

    @Autowired
    private SecurityPolicyRepository securityPolicyRepository;

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

    private MockMvc restSecurityPolicyMockMvc;

    private SecurityPolicy securityPolicy;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SecurityPolicyResource securityPolicyResource = new SecurityPolicyResource(securityPolicyRepository);
        this.restSecurityPolicyMockMvc = MockMvcBuilders.standaloneSetup(securityPolicyResource)
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
    public static SecurityPolicy createEntity(EntityManager em) {
        SecurityPolicy securityPolicy = new SecurityPolicy()
            .protectionUnit(DEFAULT_PROTECTION_UNIT)
            .access(DEFAULT_ACCESS);
        return securityPolicy;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SecurityPolicy createUpdatedEntity(EntityManager em) {
        SecurityPolicy securityPolicy = new SecurityPolicy()
            .protectionUnit(UPDATED_PROTECTION_UNIT)
            .access(UPDATED_ACCESS);
        return securityPolicy;
    }

    @BeforeEach
    public void initTest() {
        securityPolicy = createEntity(em);
    }

    @Test
    @Transactional
    public void createSecurityPolicy() throws Exception {
        int databaseSizeBeforeCreate = securityPolicyRepository.findAll().size();

        // Create the SecurityPolicy
        restSecurityPolicyMockMvc.perform(post("/api/security-policies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(securityPolicy)))
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
    public void createSecurityPolicyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = securityPolicyRepository.findAll().size();

        // Create the SecurityPolicy with an existing ID
        securityPolicy.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSecurityPolicyMockMvc.perform(post("/api/security-policies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(securityPolicy)))
            .andExpect(status().isBadRequest());

        // Validate the SecurityPolicy in the database
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkProtectionUnitIsRequired() throws Exception {
        int databaseSizeBeforeTest = securityPolicyRepository.findAll().size();
        // set the field null
        securityPolicy.setProtectionUnit(null);

        // Create the SecurityPolicy, which fails.

        restSecurityPolicyMockMvc.perform(post("/api/security-policies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(securityPolicy)))
            .andExpect(status().isBadRequest());

        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAccessIsRequired() throws Exception {
        int databaseSizeBeforeTest = securityPolicyRepository.findAll().size();
        // set the field null
        securityPolicy.setAccess(null);

        // Create the SecurityPolicy, which fails.

        restSecurityPolicyMockMvc.perform(post("/api/security-policies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(securityPolicy)))
            .andExpect(status().isBadRequest());

        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSecurityPolicies() throws Exception {
        // Initialize the database
        securityPolicyRepository.saveAndFlush(securityPolicy);

        // Get all the securityPolicyList
        restSecurityPolicyMockMvc.perform(get("/api/security-policies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(securityPolicy.getId().intValue())))
            .andExpect(jsonPath("$.[*].protectionUnit").value(hasItem(DEFAULT_PROTECTION_UNIT.toString())))
            .andExpect(jsonPath("$.[*].access").value(hasItem(DEFAULT_ACCESS.toString())));
    }
    
    @Test
    @Transactional
    public void getSecurityPolicy() throws Exception {
        // Initialize the database
        securityPolicyRepository.saveAndFlush(securityPolicy);

        // Get the securityPolicy
        restSecurityPolicyMockMvc.perform(get("/api/security-policies/{id}", securityPolicy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(securityPolicy.getId().intValue()))
            .andExpect(jsonPath("$.protectionUnit").value(DEFAULT_PROTECTION_UNIT.toString()))
            .andExpect(jsonPath("$.access").value(DEFAULT_ACCESS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSecurityPolicy() throws Exception {
        // Get the securityPolicy
        restSecurityPolicyMockMvc.perform(get("/api/security-policies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSecurityPolicy() throws Exception {
        // Initialize the database
        securityPolicyRepository.saveAndFlush(securityPolicy);

        int databaseSizeBeforeUpdate = securityPolicyRepository.findAll().size();

        // Update the securityPolicy
        SecurityPolicy updatedSecurityPolicy = securityPolicyRepository.findById(securityPolicy.getId()).get();
        // Disconnect from session so that the updates on updatedSecurityPolicy are not directly saved in db
        em.detach(updatedSecurityPolicy);
        updatedSecurityPolicy
            .protectionUnit(UPDATED_PROTECTION_UNIT)
            .access(UPDATED_ACCESS);

        restSecurityPolicyMockMvc.perform(put("/api/security-policies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSecurityPolicy)))
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
    public void updateNonExistingSecurityPolicy() throws Exception {
        int databaseSizeBeforeUpdate = securityPolicyRepository.findAll().size();

        // Create the SecurityPolicy

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSecurityPolicyMockMvc.perform(put("/api/security-policies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(securityPolicy)))
            .andExpect(status().isBadRequest());

        // Validate the SecurityPolicy in the database
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSecurityPolicy() throws Exception {
        // Initialize the database
        securityPolicyRepository.saveAndFlush(securityPolicy);

        int databaseSizeBeforeDelete = securityPolicyRepository.findAll().size();

        // Delete the securityPolicy
        restSecurityPolicyMockMvc.perform(delete("/api/security-policies/{id}", securityPolicy.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SecurityPolicy> securityPolicyList = securityPolicyRepository.findAll();
        assertThat(securityPolicyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
