package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Apartment;
import de.farue.autocut.repository.ApartmentRepository;
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

import de.farue.autocut.domain.enumeration.ApartmentTypes;
/**
 * Integration tests for the {@link ApartmentResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
public class ApartmentResourceIT {

    private static final String DEFAULT_APARTMENT_NR = "AAAAAAAAAA";
    private static final String UPDATED_APARTMENT_NR = "BBBBBBBBBB";

    private static final ApartmentTypes DEFAULT_APARTMENT_TYPE = ApartmentTypes.SHARED;
    private static final ApartmentTypes UPDATED_APARTMENT_TYPE = ApartmentTypes.SINGLE;

    private static final Integer DEFAULT_MAX_NUMBER_OF_LEASES = 0;
    private static final Integer UPDATED_MAX_NUMBER_OF_LEASES = 1;

    @Autowired
    private ApartmentRepository apartmentRepository;

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

    private MockMvc restApartmentMockMvc;

    private Apartment apartment;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ApartmentResource apartmentResource = new ApartmentResource(apartmentRepository);
        this.restApartmentMockMvc = MockMvcBuilders.standaloneSetup(apartmentResource)
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
    public static Apartment createEntity(EntityManager em) {
        Apartment apartment = new Apartment()
            .apartmentNr(DEFAULT_APARTMENT_NR)
            .apartmentType(DEFAULT_APARTMENT_TYPE)
            .maxNumberOfLeases(DEFAULT_MAX_NUMBER_OF_LEASES);
        return apartment;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Apartment createUpdatedEntity(EntityManager em) {
        Apartment apartment = new Apartment()
            .apartmentNr(UPDATED_APARTMENT_NR)
            .apartmentType(UPDATED_APARTMENT_TYPE)
            .maxNumberOfLeases(UPDATED_MAX_NUMBER_OF_LEASES);
        return apartment;
    }

    @BeforeEach
    public void initTest() {
        apartment = createEntity(em);
    }

    @Test
    @Transactional
    public void createApartment() throws Exception {
        int databaseSizeBeforeCreate = apartmentRepository.findAll().size();

        // Create the Apartment
        restApartmentMockMvc.perform(post("/api/apartments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(apartment)))
            .andExpect(status().isCreated());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeCreate + 1);
        Apartment testApartment = apartmentList.get(apartmentList.size() - 1);
        assertThat(testApartment.getApartmentNr()).isEqualTo(DEFAULT_APARTMENT_NR);
        assertThat(testApartment.getApartmentType()).isEqualTo(DEFAULT_APARTMENT_TYPE);
        assertThat(testApartment.getMaxNumberOfLeases()).isEqualTo(DEFAULT_MAX_NUMBER_OF_LEASES);
    }

    @Test
    @Transactional
    public void createApartmentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = apartmentRepository.findAll().size();

        // Create the Apartment with an existing ID
        apartment.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restApartmentMockMvc.perform(post("/api/apartments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(apartment)))
            .andExpect(status().isBadRequest());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkApartmentNrIsRequired() throws Exception {
        int databaseSizeBeforeTest = apartmentRepository.findAll().size();
        // set the field null
        apartment.setApartmentNr(null);

        // Create the Apartment, which fails.

        restApartmentMockMvc.perform(post("/api/apartments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(apartment)))
            .andExpect(status().isBadRequest());

        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkApartmentTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = apartmentRepository.findAll().size();
        // set the field null
        apartment.setApartmentType(null);

        // Create the Apartment, which fails.

        restApartmentMockMvc.perform(post("/api/apartments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(apartment)))
            .andExpect(status().isBadRequest());

        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMaxNumberOfLeasesIsRequired() throws Exception {
        int databaseSizeBeforeTest = apartmentRepository.findAll().size();
        // set the field null
        apartment.setMaxNumberOfLeases(null);

        // Create the Apartment, which fails.

        restApartmentMockMvc.perform(post("/api/apartments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(apartment)))
            .andExpect(status().isBadRequest());

        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllApartments() throws Exception {
        // Initialize the database
        apartmentRepository.saveAndFlush(apartment);

        // Get all the apartmentList
        restApartmentMockMvc.perform(get("/api/apartments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(apartment.getId().intValue())))
            .andExpect(jsonPath("$.[*].apartmentNr").value(hasItem(DEFAULT_APARTMENT_NR)))
            .andExpect(jsonPath("$.[*].apartmentType").value(hasItem(DEFAULT_APARTMENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].maxNumberOfLeases").value(hasItem(DEFAULT_MAX_NUMBER_OF_LEASES)));
    }
    
    @Test
    @Transactional
    public void getApartment() throws Exception {
        // Initialize the database
        apartmentRepository.saveAndFlush(apartment);

        // Get the apartment
        restApartmentMockMvc.perform(get("/api/apartments/{id}", apartment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(apartment.getId().intValue()))
            .andExpect(jsonPath("$.apartmentNr").value(DEFAULT_APARTMENT_NR))
            .andExpect(jsonPath("$.apartmentType").value(DEFAULT_APARTMENT_TYPE.toString()))
            .andExpect(jsonPath("$.maxNumberOfLeases").value(DEFAULT_MAX_NUMBER_OF_LEASES));
    }

    @Test
    @Transactional
    public void getNonExistingApartment() throws Exception {
        // Get the apartment
        restApartmentMockMvc.perform(get("/api/apartments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateApartment() throws Exception {
        // Initialize the database
        apartmentRepository.saveAndFlush(apartment);

        int databaseSizeBeforeUpdate = apartmentRepository.findAll().size();

        // Update the apartment
        Apartment updatedApartment = apartmentRepository.findById(apartment.getId()).get();
        // Disconnect from session so that the updates on updatedApartment are not directly saved in db
        em.detach(updatedApartment);
        updatedApartment
            .apartmentNr(UPDATED_APARTMENT_NR)
            .apartmentType(UPDATED_APARTMENT_TYPE)
            .maxNumberOfLeases(UPDATED_MAX_NUMBER_OF_LEASES);

        restApartmentMockMvc.perform(put("/api/apartments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedApartment)))
            .andExpect(status().isOk());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeUpdate);
        Apartment testApartment = apartmentList.get(apartmentList.size() - 1);
        assertThat(testApartment.getApartmentNr()).isEqualTo(UPDATED_APARTMENT_NR);
        assertThat(testApartment.getApartmentType()).isEqualTo(UPDATED_APARTMENT_TYPE);
        assertThat(testApartment.getMaxNumberOfLeases()).isEqualTo(UPDATED_MAX_NUMBER_OF_LEASES);
    }

    @Test
    @Transactional
    public void updateNonExistingApartment() throws Exception {
        int databaseSizeBeforeUpdate = apartmentRepository.findAll().size();

        // Create the Apartment

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApartmentMockMvc.perform(put("/api/apartments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(apartment)))
            .andExpect(status().isBadRequest());

        // Validate the Apartment in the database
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteApartment() throws Exception {
        // Initialize the database
        apartmentRepository.saveAndFlush(apartment);

        int databaseSizeBeforeDelete = apartmentRepository.findAll().size();

        // Delete the apartment
        restApartmentMockMvc.perform(delete("/api/apartments/{id}", apartment.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Apartment> apartmentList = apartmentRepository.findAll();
        assertThat(apartmentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
