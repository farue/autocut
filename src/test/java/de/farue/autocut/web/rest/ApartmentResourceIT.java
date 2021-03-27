package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.Apartment;
import de.farue.autocut.domain.enumeration.ApartmentTypes;
import de.farue.autocut.repository.ApartmentRepository;
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
 * Integration tests for the {@link ApartmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ApartmentResourceIT {

    private static final String DEFAULT_NR = "AAAAAAAAAA";
    private static final String UPDATED_NR = "BBBBBBBBBB";

    private static final ApartmentTypes DEFAULT_TYPE = ApartmentTypes.SHARED;
    private static final ApartmentTypes UPDATED_TYPE = ApartmentTypes.SINGLE;

    private static final Integer DEFAULT_MAX_NUMBER_OF_LEASES = 0;
    private static final Integer UPDATED_MAX_NUMBER_OF_LEASES = 1;

    private static final String ENTITY_API_URL = "/api/apartments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApartmentMockMvc;

    private Apartment apartment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Apartment createEntity(EntityManager em) {
        Apartment apartment = new Apartment().nr(DEFAULT_NR).type(DEFAULT_TYPE).maxNumberOfLeases(DEFAULT_MAX_NUMBER_OF_LEASES);
        return apartment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Apartment createUpdatedEntity(EntityManager em) {
        Apartment apartment = new Apartment().nr(UPDATED_NR).type(UPDATED_TYPE).maxNumberOfLeases(UPDATED_MAX_NUMBER_OF_LEASES);
        return apartment;
    }

    @BeforeEach
    public void initTest() {
        apartment = createEntity(em);
    }

    @Test
    @Transactional
    void createApartment() throws Exception {
        int databaseSizeBeforeCreate = apartmentRepository.findAll().size();
        // Create the Apartment
        restApartmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(apartment)))
            .andExpect(status().isCreated());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeCreate + 1);
        Apartment testApartment = apartmentList.get(apartmentList.size() - 1);
        assertThat(testApartment.getNr()).isEqualTo(DEFAULT_NR);
        assertThat(testApartment.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testApartment.getMaxNumberOfLeases()).isEqualTo(DEFAULT_MAX_NUMBER_OF_LEASES);
    }

    @Test
    @Transactional
    void createApartmentWithExistingId() throws Exception {
        // Create the Apartment with an existing ID
        apartment.setId(1L);

        int databaseSizeBeforeCreate = apartmentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restApartmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(apartment)))
            .andExpect(status().isBadRequest());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNrIsRequired() throws Exception {
        int databaseSizeBeforeTest = apartmentRepository.findAll().size();
        // set the field null
        apartment.setNr(null);

        // Create the Apartment, which fails.

        restApartmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(apartment)))
            .andExpect(status().isBadRequest());

        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = apartmentRepository.findAll().size();
        // set the field null
        apartment.setType(null);

        // Create the Apartment, which fails.

        restApartmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(apartment)))
            .andExpect(status().isBadRequest());

        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaxNumberOfLeasesIsRequired() throws Exception {
        int databaseSizeBeforeTest = apartmentRepository.findAll().size();
        // set the field null
        apartment.setMaxNumberOfLeases(null);

        // Create the Apartment, which fails.

        restApartmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(apartment)))
            .andExpect(status().isBadRequest());

        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllApartments() throws Exception {
        // Initialize the database
        apartmentRepository.saveAndFlush(apartment);

        // Get all the apartmentList
        restApartmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(apartment.getId().intValue())))
            .andExpect(jsonPath("$.[*].nr").value(hasItem(DEFAULT_NR)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].maxNumberOfLeases").value(hasItem(DEFAULT_MAX_NUMBER_OF_LEASES)));
    }

    @Test
    @Transactional
    void getApartment() throws Exception {
        // Initialize the database
        apartmentRepository.saveAndFlush(apartment);

        // Get the apartment
        restApartmentMockMvc
            .perform(get(ENTITY_API_URL_ID, apartment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(apartment.getId().intValue()))
            .andExpect(jsonPath("$.nr").value(DEFAULT_NR))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.maxNumberOfLeases").value(DEFAULT_MAX_NUMBER_OF_LEASES));
    }

    @Test
    @Transactional
    void getNonExistingApartment() throws Exception {
        // Get the apartment
        restApartmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewApartment() throws Exception {
        // Initialize the database
        apartmentRepository.saveAndFlush(apartment);

        int databaseSizeBeforeUpdate = apartmentRepository.findAll().size();

        // Update the apartment
        Apartment updatedApartment = apartmentRepository.findById(apartment.getId()).get();
        // Disconnect from session so that the updates on updatedApartment are not directly saved in db
        em.detach(updatedApartment);
        updatedApartment.nr(UPDATED_NR).type(UPDATED_TYPE).maxNumberOfLeases(UPDATED_MAX_NUMBER_OF_LEASES);

        restApartmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedApartment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedApartment))
            )
            .andExpect(status().isOk());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeUpdate);
        Apartment testApartment = apartmentList.get(apartmentList.size() - 1);
        assertThat(testApartment.getNr()).isEqualTo(UPDATED_NR);
        assertThat(testApartment.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testApartment.getMaxNumberOfLeases()).isEqualTo(UPDATED_MAX_NUMBER_OF_LEASES);
    }

    @Test
    @Transactional
    void putNonExistingApartment() throws Exception {
        int databaseSizeBeforeUpdate = apartmentRepository.findAll().size();
        apartment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApartmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, apartment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(apartment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchApartment() throws Exception {
        int databaseSizeBeforeUpdate = apartmentRepository.findAll().size();
        apartment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApartmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(apartment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApartment() throws Exception {
        int databaseSizeBeforeUpdate = apartmentRepository.findAll().size();
        apartment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApartmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(apartment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateApartmentWithPatch() throws Exception {
        // Initialize the database
        apartmentRepository.saveAndFlush(apartment);

        int databaseSizeBeforeUpdate = apartmentRepository.findAll().size();

        // Update the apartment using partial update
        Apartment partialUpdatedApartment = new Apartment();
        partialUpdatedApartment.setId(apartment.getId());

        partialUpdatedApartment.type(UPDATED_TYPE).maxNumberOfLeases(UPDATED_MAX_NUMBER_OF_LEASES);

        restApartmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApartment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedApartment))
            )
            .andExpect(status().isOk());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeUpdate);
        Apartment testApartment = apartmentList.get(apartmentList.size() - 1);
        assertThat(testApartment.getNr()).isEqualTo(DEFAULT_NR);
        assertThat(testApartment.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testApartment.getMaxNumberOfLeases()).isEqualTo(UPDATED_MAX_NUMBER_OF_LEASES);
    }

    @Test
    @Transactional
    void fullUpdateApartmentWithPatch() throws Exception {
        // Initialize the database
        apartmentRepository.saveAndFlush(apartment);

        int databaseSizeBeforeUpdate = apartmentRepository.findAll().size();

        // Update the apartment using partial update
        Apartment partialUpdatedApartment = new Apartment();
        partialUpdatedApartment.setId(apartment.getId());

        partialUpdatedApartment.nr(UPDATED_NR).type(UPDATED_TYPE).maxNumberOfLeases(UPDATED_MAX_NUMBER_OF_LEASES);

        restApartmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApartment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedApartment))
            )
            .andExpect(status().isOk());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeUpdate);
        Apartment testApartment = apartmentList.get(apartmentList.size() - 1);
        assertThat(testApartment.getNr()).isEqualTo(UPDATED_NR);
        assertThat(testApartment.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testApartment.getMaxNumberOfLeases()).isEqualTo(UPDATED_MAX_NUMBER_OF_LEASES);
    }

    @Test
    @Transactional
    void patchNonExistingApartment() throws Exception {
        int databaseSizeBeforeUpdate = apartmentRepository.findAll().size();
        apartment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApartmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, apartment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(apartment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApartment() throws Exception {
        int databaseSizeBeforeUpdate = apartmentRepository.findAll().size();
        apartment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApartmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(apartment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApartment() throws Exception {
        int databaseSizeBeforeUpdate = apartmentRepository.findAll().size();
        apartment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApartmentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(apartment))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteApartment() throws Exception {
        // Initialize the database
        apartmentRepository.saveAndFlush(apartment);

        int databaseSizeBeforeDelete = apartmentRepository.findAll().size();

        // Delete the apartment
        restApartmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, apartment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
