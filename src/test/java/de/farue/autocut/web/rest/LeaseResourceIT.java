package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.repository.LeaseRepository;
import de.farue.autocut.service.LeaseService;

/**
 * Integration tests for the {@link LeaseResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class LeaseResourceIT {

    private static final String DEFAULT_NR = "AAAAAAAAAA";
    private static final String UPDATED_NR = "BBBBBBBBBB";

    private static final Instant DEFAULT_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_BLOCKED = false;
    private static final Boolean UPDATED_BLOCKED = true;

    private static final byte[] DEFAULT_PICTURE_CONTRACT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PICTURE_CONTRACT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PICTURE_CONTRACT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PICTURE_CONTRACT_CONTENT_TYPE = "image/png";

    @Autowired
    private LeaseRepository leaseRepository;

    @Mock
    private LeaseRepository leaseRepositoryMock;

    @Mock
    private LeaseService leaseServiceMock;

    @Autowired
    private LeaseService leaseService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLeaseMockMvc;

    private Lease lease;

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
            .blocked(DEFAULT_BLOCKED)
            .pictureContract(DEFAULT_PICTURE_CONTRACT)
            .pictureContractContentType(DEFAULT_PICTURE_CONTRACT_CONTENT_TYPE);
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
            .blocked(UPDATED_BLOCKED)
            .pictureContract(UPDATED_PICTURE_CONTRACT)
            .pictureContractContentType(UPDATED_PICTURE_CONTRACT_CONTENT_TYPE);
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
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(lease)))
            .andExpect(status().isCreated());

        // Validate the Lease in the database
        List<Lease> leaseList = leaseRepository.findAll();
        assertThat(leaseList).hasSize(databaseSizeBeforeCreate + 1);
        Lease testLease = leaseList.get(leaseList.size() - 1);
        assertThat(testLease.getNr()).isEqualTo(DEFAULT_NR);
        assertThat(testLease.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testLease.getEnd()).isEqualTo(DEFAULT_END);
        assertThat(testLease.isBlocked()).isEqualTo(DEFAULT_BLOCKED);
        assertThat(testLease.getPictureContract()).isEqualTo(DEFAULT_PICTURE_CONTRACT);
        assertThat(testLease.getPictureContractContentType()).isEqualTo(DEFAULT_PICTURE_CONTRACT_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createLeaseWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = leaseRepository.findAll().size();

        // Create the Lease with an existing ID
        lease.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLeaseMockMvc.perform(post("/api/leases")
            .contentType(MediaType.APPLICATION_JSON)
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
            .contentType(MediaType.APPLICATION_JSON)
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
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(lease)))
            .andExpect(status().isBadRequest());

        List<Lease> leaseList = leaseRepository.findAll();
        assertThat(leaseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEndIsRequired() throws Exception {
        int databaseSizeBeforeTest = leaseRepository.findAll().size();
        // set the field null
        lease.setEnd(null);

        // Create the Lease, which fails.


        restLeaseMockMvc.perform(post("/api/leases")
            .contentType(MediaType.APPLICATION_JSON)
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
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lease.getId().intValue())))
            .andExpect(jsonPath("$.[*].nr").value(hasItem(DEFAULT_NR)))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())))
            .andExpect(jsonPath("$.[*].blocked").value(hasItem(DEFAULT_BLOCKED.booleanValue())))
            .andExpect(jsonPath("$.[*].pictureContractContentType").value(hasItem(DEFAULT_PICTURE_CONTRACT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].pictureContract").value(hasItem(Base64Utils.encodeToString(DEFAULT_PICTURE_CONTRACT))));
    }

    @SuppressWarnings({"unchecked"})
    public void getAllLeasesWithEagerRelationshipsIsEnabled() throws Exception {
        when(leaseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLeaseMockMvc.perform(get("/api/leases?eagerload=true"))
            .andExpect(status().isOk());

        verify(leaseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllLeasesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(leaseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLeaseMockMvc.perform(get("/api/leases?eagerload=true"))
            .andExpect(status().isOk());

        verify(leaseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getLease() throws Exception {
        // Initialize the database
        leaseRepository.saveAndFlush(lease);

        // Get the lease
        restLeaseMockMvc.perform(get("/api/leases/{id}", lease.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lease.getId().intValue()))
            .andExpect(jsonPath("$.nr").value(DEFAULT_NR))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.end").value(DEFAULT_END.toString()))
            .andExpect(jsonPath("$.blocked").value(DEFAULT_BLOCKED.booleanValue()))
            .andExpect(jsonPath("$.pictureContractContentType").value(DEFAULT_PICTURE_CONTRACT_CONTENT_TYPE))
            .andExpect(jsonPath("$.pictureContract").value(Base64Utils.encodeToString(DEFAULT_PICTURE_CONTRACT)));
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
        leaseService.save(lease);

        int databaseSizeBeforeUpdate = leaseRepository.findAll().size();

        // Update the lease
        Lease updatedLease = leaseRepository.findById(lease.getId()).get();
        // Disconnect from session so that the updates on updatedLease are not directly saved in db
        em.detach(updatedLease);
        updatedLease
            .nr(UPDATED_NR)
            .start(UPDATED_START)
            .end(UPDATED_END)
            .blocked(UPDATED_BLOCKED)
            .pictureContract(UPDATED_PICTURE_CONTRACT)
            .pictureContractContentType(UPDATED_PICTURE_CONTRACT_CONTENT_TYPE);

        restLeaseMockMvc.perform(put("/api/leases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedLease)))
            .andExpect(status().isOk());

        // Validate the Lease in the database
        List<Lease> leaseList = leaseRepository.findAll();
        assertThat(leaseList).hasSize(databaseSizeBeforeUpdate);
        Lease testLease = leaseList.get(leaseList.size() - 1);
        assertThat(testLease.getNr()).isEqualTo(UPDATED_NR);
        assertThat(testLease.getStart()).isEqualTo(UPDATED_START);
        assertThat(testLease.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testLease.isBlocked()).isEqualTo(UPDATED_BLOCKED);
        assertThat(testLease.getPictureContract()).isEqualTo(UPDATED_PICTURE_CONTRACT);
        assertThat(testLease.getPictureContractContentType()).isEqualTo(UPDATED_PICTURE_CONTRACT_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingLease() throws Exception {
        int databaseSizeBeforeUpdate = leaseRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeaseMockMvc.perform(put("/api/leases")
            .contentType(MediaType.APPLICATION_JSON)
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
        leaseService.save(lease);

        int databaseSizeBeforeDelete = leaseRepository.findAll().size();

        // Delete the lease
        restLeaseMockMvc.perform(delete("/api/leases/{id}", lease.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Lease> leaseList = leaseRepository.findAll();
        assertThat(leaseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
