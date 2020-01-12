package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.PaymentAccount;
import de.farue.autocut.repository.LeaseRepository;
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
 * Integration tests for the {@link LeaseResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
public class LeaseResourceIT {

    private static final String DEFAULT_NR = "AAAAAAAAAA";
    private static final String UPDATED_NR = "BBBBBBBBBB";

    private static final Instant DEFAULT_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private LeaseRepository leaseRepository;

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

    private MockMvc restLeaseMockMvc;

    private Lease lease;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LeaseResource leaseResource = new LeaseResource(leaseRepository);
        this.restLeaseMockMvc = MockMvcBuilders.standaloneSetup(leaseResource)
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
    public static Lease createEntity(EntityManager em) {
        Lease lease = new Lease()
            .nr(DEFAULT_NR)
            .start(DEFAULT_START)
            .end(DEFAULT_END)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        // Add required entity
        PaymentAccount paymentAccount;
        if (TestUtil.findAll(em, PaymentAccount.class).isEmpty()) {
            paymentAccount = PaymentAccountResourceIT.createEntity(em);
            em.persist(paymentAccount);
            em.flush();
        } else {
            paymentAccount = TestUtil.findAll(em, PaymentAccount.class).get(0);
        }
        lease.setAccount(paymentAccount);
        return lease;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lease createUpdatedEntity(EntityManager em) {
        Lease lease = new Lease()
            .nr(UPDATED_NR)
            .start(UPDATED_START)
            .end(UPDATED_END)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        // Add required entity
        PaymentAccount paymentAccount;
        if (TestUtil.findAll(em, PaymentAccount.class).isEmpty()) {
            paymentAccount = PaymentAccountResourceIT.createUpdatedEntity(em);
            em.persist(paymentAccount);
            em.flush();
        } else {
            paymentAccount = TestUtil.findAll(em, PaymentAccount.class).get(0);
        }
        lease.setAccount(paymentAccount);
        return lease;
    }

    @BeforeEach
    public void initTest() {
        lease = createEntity(em);
    }

    @Test
    @Transactional
    public void createLease() throws Exception {
        int databaseSizeBeforeCreate = leaseRepository.findAll().size();

        // Create the Lease
        restLeaseMockMvc.perform(post("/api/leases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lease)))
            .andExpect(status().isCreated());

        // Validate the Lease in the database
        List<Lease> leaseList = leaseRepository.findAll();
        assertThat(leaseList).hasSize(databaseSizeBeforeCreate + 1);
        Lease testLease = leaseList.get(leaseList.size() - 1);
        assertThat(testLease.getNr()).isEqualTo(DEFAULT_NR);
        assertThat(testLease.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testLease.getEnd()).isEqualTo(DEFAULT_END);
        assertThat(testLease.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testLease.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testLease.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testLease.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void createLeaseWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = leaseRepository.findAll().size();

        // Create the Lease with an existing ID
        lease.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLeaseMockMvc.perform(post("/api/leases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lease)))
            .andExpect(status().isBadRequest());

        // Validate the Lease in the database
        List<Lease> leaseList = leaseRepository.findAll();
        assertThat(leaseList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNrIsRequired() throws Exception {
        int databaseSizeBeforeTest = leaseRepository.findAll().size();
        // set the field null
        lease.setNr(null);

        // Create the Lease, which fails.

        restLeaseMockMvc.perform(post("/api/leases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lease)))
            .andExpect(status().isBadRequest());

        List<Lease> leaseList = leaseRepository.findAll();
        assertThat(leaseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStartIsRequired() throws Exception {
        int databaseSizeBeforeTest = leaseRepository.findAll().size();
        // set the field null
        lease.setStart(null);

        // Create the Lease, which fails.

        restLeaseMockMvc.perform(post("/api/leases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lease)))
            .andExpect(status().isBadRequest());

        List<Lease> leaseList = leaseRepository.findAll();
        assertThat(leaseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = leaseRepository.findAll().size();
        // set the field null
        lease.setCreatedBy(null);

        // Create the Lease, which fails.

        restLeaseMockMvc.perform(post("/api/leases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lease)))
            .andExpect(status().isBadRequest());

        List<Lease> leaseList = leaseRepository.findAll();
        assertThat(leaseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreatedDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = leaseRepository.findAll().size();
        // set the field null
        lease.setCreatedDate(null);

        // Create the Lease, which fails.

        restLeaseMockMvc.perform(post("/api/leases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lease)))
            .andExpect(status().isBadRequest());

        List<Lease> leaseList = leaseRepository.findAll();
        assertThat(leaseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLeases() throws Exception {
        // Initialize the database
        leaseRepository.saveAndFlush(lease);

        // Get all the leaseList
        restLeaseMockMvc.perform(get("/api/leases?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lease.getId().intValue())))
            .andExpect(jsonPath("$.[*].nr").value(hasItem(DEFAULT_NR)))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getLease() throws Exception {
        // Initialize the database
        leaseRepository.saveAndFlush(lease);

        // Get the lease
        restLeaseMockMvc.perform(get("/api/leases/{id}", lease.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(lease.getId().intValue()))
            .andExpect(jsonPath("$.nr").value(DEFAULT_NR))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.end").value(DEFAULT_END.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLease() throws Exception {
        // Get the lease
        restLeaseMockMvc.perform(get("/api/leases/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLease() throws Exception {
        // Initialize the database
        leaseRepository.saveAndFlush(lease);

        int databaseSizeBeforeUpdate = leaseRepository.findAll().size();

        // Update the lease
        Lease updatedLease = leaseRepository.findById(lease.getId()).get();
        // Disconnect from session so that the updates on updatedLease are not directly saved in db
        em.detach(updatedLease);
        updatedLease
            .nr(UPDATED_NR)
            .start(UPDATED_START)
            .end(UPDATED_END)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restLeaseMockMvc.perform(put("/api/leases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLease)))
            .andExpect(status().isOk());

        // Validate the Lease in the database
        List<Lease> leaseList = leaseRepository.findAll();
        assertThat(leaseList).hasSize(databaseSizeBeforeUpdate);
        Lease testLease = leaseList.get(leaseList.size() - 1);
        assertThat(testLease.getNr()).isEqualTo(UPDATED_NR);
        assertThat(testLease.getStart()).isEqualTo(UPDATED_START);
        assertThat(testLease.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testLease.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testLease.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testLease.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testLease.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingLease() throws Exception {
        int databaseSizeBeforeUpdate = leaseRepository.findAll().size();

        // Create the Lease

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeaseMockMvc.perform(put("/api/leases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lease)))
            .andExpect(status().isBadRequest());

        // Validate the Lease in the database
        List<Lease> leaseList = leaseRepository.findAll();
        assertThat(leaseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLease() throws Exception {
        // Initialize the database
        leaseRepository.saveAndFlush(lease);

        int databaseSizeBeforeDelete = leaseRepository.findAll().size();

        // Delete the lease
        restLeaseMockMvc.perform(delete("/api/leases/{id}", lease.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Lease> leaseList = leaseRepository.findAll();
        assertThat(leaseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
