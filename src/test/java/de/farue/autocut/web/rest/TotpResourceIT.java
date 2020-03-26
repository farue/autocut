package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Totp;
import de.farue.autocut.repository.TotpRepository;
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

/**
 * Integration tests for the {@link TotpResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
public class TotpResourceIT {

    private static final String DEFAULT_SECRET = "AAAAAAAAAA";
    private static final String UPDATED_SECRET = "BBBBBBBBBB";

    @Autowired
    private TotpRepository totpRepository;

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

    private MockMvc restTotpMockMvc;

    private Totp totp;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TotpResource totpResource = new TotpResource(totpRepository);
        this.restTotpMockMvc = MockMvcBuilders.standaloneSetup(totpResource)
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
    public static Totp createEntity(EntityManager em) {
        Totp totp = new Totp()
            .secret(DEFAULT_SECRET);
        return totp;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Totp createUpdatedEntity(EntityManager em) {
        Totp totp = new Totp()
            .secret(UPDATED_SECRET);
        return totp;
    }

    @BeforeEach
    public void initTest() {
        totp = createEntity(em);
    }

    @Test
    @Transactional
    public void createTotp() throws Exception {
        int databaseSizeBeforeCreate = totpRepository.findAll().size();

        // Create the Totp
        restTotpMockMvc.perform(post("/api/totps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(totp)))
            .andExpect(status().isCreated());

        // Validate the Totp in the database
        List<Totp> totpList = totpRepository.findAll();
        assertThat(totpList).hasSize(databaseSizeBeforeCreate + 1);
        Totp testTotp = totpList.get(totpList.size() - 1);
        assertThat(testTotp.getSecret()).isEqualTo(DEFAULT_SECRET);
    }

    @Test
    @Transactional
    public void createTotpWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = totpRepository.findAll().size();

        // Create the Totp with an existing ID
        totp.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTotpMockMvc.perform(post("/api/totps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(totp)))
            .andExpect(status().isBadRequest());

        // Validate the Totp in the database
        List<Totp> totpList = totpRepository.findAll();
        assertThat(totpList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkSecretIsRequired() throws Exception {
        int databaseSizeBeforeTest = totpRepository.findAll().size();
        // set the field null
        totp.setSecret(null);

        // Create the Totp, which fails.

        restTotpMockMvc.perform(post("/api/totps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(totp)))
            .andExpect(status().isBadRequest());

        List<Totp> totpList = totpRepository.findAll();
        assertThat(totpList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTotps() throws Exception {
        // Initialize the database
        totpRepository.saveAndFlush(totp);

        // Get all the totpList
        restTotpMockMvc.perform(get("/api/totps?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(totp.getId().intValue())))
            .andExpect(jsonPath("$.[*].secret").value(hasItem(DEFAULT_SECRET)));
    }
    
    @Test
    @Transactional
    public void getTotp() throws Exception {
        // Initialize the database
        totpRepository.saveAndFlush(totp);

        // Get the totp
        restTotpMockMvc.perform(get("/api/totps/{id}", totp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(totp.getId().intValue()))
            .andExpect(jsonPath("$.secret").value(DEFAULT_SECRET));
    }

    @Test
    @Transactional
    public void getNonExistingTotp() throws Exception {
        // Get the totp
        restTotpMockMvc.perform(get("/api/totps/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTotp() throws Exception {
        // Initialize the database
        totpRepository.saveAndFlush(totp);

        int databaseSizeBeforeUpdate = totpRepository.findAll().size();

        // Update the totp
        Totp updatedTotp = totpRepository.findById(totp.getId()).get();
        // Disconnect from session so that the updates on updatedTotp are not directly saved in db
        em.detach(updatedTotp);
        updatedTotp
            .secret(UPDATED_SECRET);

        restTotpMockMvc.perform(put("/api/totps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTotp)))
            .andExpect(status().isOk());

        // Validate the Totp in the database
        List<Totp> totpList = totpRepository.findAll();
        assertThat(totpList).hasSize(databaseSizeBeforeUpdate);
        Totp testTotp = totpList.get(totpList.size() - 1);
        assertThat(testTotp.getSecret()).isEqualTo(UPDATED_SECRET);
    }

    @Test
    @Transactional
    public void updateNonExistingTotp() throws Exception {
        int databaseSizeBeforeUpdate = totpRepository.findAll().size();

        // Create the Totp

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTotpMockMvc.perform(put("/api/totps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(totp)))
            .andExpect(status().isBadRequest());

        // Validate the Totp in the database
        List<Totp> totpList = totpRepository.findAll();
        assertThat(totpList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTotp() throws Exception {
        // Initialize the database
        totpRepository.saveAndFlush(totp);

        int databaseSizeBeforeDelete = totpRepository.findAll().size();

        // Delete the totp
        restTotpMockMvc.perform(delete("/api/totps/{id}", totp.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Totp> totpList = totpRepository.findAll();
        assertThat(totpList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
