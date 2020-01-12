package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.PaymentEntry;
import de.farue.autocut.repository.PaymentEntryRepository;
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
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static de.farue.autocut.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PaymentEntryResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
public class PaymentEntryResourceIT {

    private static final BigDecimal DEFAULT_BALANCE_BEFORE = new BigDecimal(1);
    private static final BigDecimal UPDATED_BALANCE_BEFORE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_BALANCE_AFTER = new BigDecimal(1);
    private static final BigDecimal UPDATED_BALANCE_AFTER = new BigDecimal(2);

    private static final BigDecimal DEFAULT_PAYMENT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PAYMENT = new BigDecimal(2);

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private PaymentEntryRepository paymentEntryRepository;

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

    private MockMvc restPaymentEntryMockMvc;

    private PaymentEntry paymentEntry;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PaymentEntryResource paymentEntryResource = new PaymentEntryResource(paymentEntryRepository);
        this.restPaymentEntryMockMvc = MockMvcBuilders.standaloneSetup(paymentEntryResource)
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
    public static PaymentEntry createEntity(EntityManager em) {
        PaymentEntry paymentEntry = new PaymentEntry()
            .balanceBefore(DEFAULT_BALANCE_BEFORE)
            .balanceAfter(DEFAULT_BALANCE_AFTER)
            .payment(DEFAULT_PAYMENT)
            .date(DEFAULT_DATE)
            .description(DEFAULT_DESCRIPTION);
        return paymentEntry;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentEntry createUpdatedEntity(EntityManager em) {
        PaymentEntry paymentEntry = new PaymentEntry()
            .balanceBefore(UPDATED_BALANCE_BEFORE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .payment(UPDATED_PAYMENT)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION);
        return paymentEntry;
    }

    @BeforeEach
    public void initTest() {
        paymentEntry = createEntity(em);
    }

    @Test
    @Transactional
    public void createPaymentEntry() throws Exception {
        int databaseSizeBeforeCreate = paymentEntryRepository.findAll().size();

        // Create the PaymentEntry
        restPaymentEntryMockMvc.perform(post("/api/payment-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentEntry)))
            .andExpect(status().isCreated());

        // Validate the PaymentEntry in the database
        List<PaymentEntry> paymentEntryList = paymentEntryRepository.findAll();
        assertThat(paymentEntryList).hasSize(databaseSizeBeforeCreate + 1);
        PaymentEntry testPaymentEntry = paymentEntryList.get(paymentEntryList.size() - 1);
        assertThat(testPaymentEntry.getBalanceBefore()).isEqualTo(DEFAULT_BALANCE_BEFORE);
        assertThat(testPaymentEntry.getBalanceAfter()).isEqualTo(DEFAULT_BALANCE_AFTER);
        assertThat(testPaymentEntry.getPayment()).isEqualTo(DEFAULT_PAYMENT);
        assertThat(testPaymentEntry.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testPaymentEntry.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createPaymentEntryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = paymentEntryRepository.findAll().size();

        // Create the PaymentEntry with an existing ID
        paymentEntry.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentEntryMockMvc.perform(post("/api/payment-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentEntry)))
            .andExpect(status().isBadRequest());

        // Validate the PaymentEntry in the database
        List<PaymentEntry> paymentEntryList = paymentEntryRepository.findAll();
        assertThat(paymentEntryList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkBalanceBeforeIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentEntryRepository.findAll().size();
        // set the field null
        paymentEntry.setBalanceBefore(null);

        // Create the PaymentEntry, which fails.

        restPaymentEntryMockMvc.perform(post("/api/payment-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentEntry)))
            .andExpect(status().isBadRequest());

        List<PaymentEntry> paymentEntryList = paymentEntryRepository.findAll();
        assertThat(paymentEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBalanceAfterIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentEntryRepository.findAll().size();
        // set the field null
        paymentEntry.setBalanceAfter(null);

        // Create the PaymentEntry, which fails.

        restPaymentEntryMockMvc.perform(post("/api/payment-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentEntry)))
            .andExpect(status().isBadRequest());

        List<PaymentEntry> paymentEntryList = paymentEntryRepository.findAll();
        assertThat(paymentEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPaymentIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentEntryRepository.findAll().size();
        // set the field null
        paymentEntry.setPayment(null);

        // Create the PaymentEntry, which fails.

        restPaymentEntryMockMvc.perform(post("/api/payment-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentEntry)))
            .andExpect(status().isBadRequest());

        List<PaymentEntry> paymentEntryList = paymentEntryRepository.findAll();
        assertThat(paymentEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentEntryRepository.findAll().size();
        // set the field null
        paymentEntry.setDate(null);

        // Create the PaymentEntry, which fails.

        restPaymentEntryMockMvc.perform(post("/api/payment-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentEntry)))
            .andExpect(status().isBadRequest());

        List<PaymentEntry> paymentEntryList = paymentEntryRepository.findAll();
        assertThat(paymentEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPaymentEntries() throws Exception {
        // Initialize the database
        paymentEntryRepository.saveAndFlush(paymentEntry);

        // Get all the paymentEntryList
        restPaymentEntryMockMvc.perform(get("/api/payment-entries?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].balanceBefore").value(hasItem(DEFAULT_BALANCE_BEFORE.intValue())))
            .andExpect(jsonPath("$.[*].balanceAfter").value(hasItem(DEFAULT_BALANCE_AFTER.intValue())))
            .andExpect(jsonPath("$.[*].payment").value(hasItem(DEFAULT_PAYMENT.intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
    
    @Test
    @Transactional
    public void getPaymentEntry() throws Exception {
        // Initialize the database
        paymentEntryRepository.saveAndFlush(paymentEntry);

        // Get the paymentEntry
        restPaymentEntryMockMvc.perform(get("/api/payment-entries/{id}", paymentEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(paymentEntry.getId().intValue()))
            .andExpect(jsonPath("$.balanceBefore").value(DEFAULT_BALANCE_BEFORE.intValue()))
            .andExpect(jsonPath("$.balanceAfter").value(DEFAULT_BALANCE_AFTER.intValue()))
            .andExpect(jsonPath("$.payment").value(DEFAULT_PAYMENT.intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    public void getNonExistingPaymentEntry() throws Exception {
        // Get the paymentEntry
        restPaymentEntryMockMvc.perform(get("/api/payment-entries/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePaymentEntry() throws Exception {
        // Initialize the database
        paymentEntryRepository.saveAndFlush(paymentEntry);

        int databaseSizeBeforeUpdate = paymentEntryRepository.findAll().size();

        // Update the paymentEntry
        PaymentEntry updatedPaymentEntry = paymentEntryRepository.findById(paymentEntry.getId()).get();
        // Disconnect from session so that the updates on updatedPaymentEntry are not directly saved in db
        em.detach(updatedPaymentEntry);
        updatedPaymentEntry
            .balanceBefore(UPDATED_BALANCE_BEFORE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .payment(UPDATED_PAYMENT)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION);

        restPaymentEntryMockMvc.perform(put("/api/payment-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPaymentEntry)))
            .andExpect(status().isOk());

        // Validate the PaymentEntry in the database
        List<PaymentEntry> paymentEntryList = paymentEntryRepository.findAll();
        assertThat(paymentEntryList).hasSize(databaseSizeBeforeUpdate);
        PaymentEntry testPaymentEntry = paymentEntryList.get(paymentEntryList.size() - 1);
        assertThat(testPaymentEntry.getBalanceBefore()).isEqualTo(UPDATED_BALANCE_BEFORE);
        assertThat(testPaymentEntry.getBalanceAfter()).isEqualTo(UPDATED_BALANCE_AFTER);
        assertThat(testPaymentEntry.getPayment()).isEqualTo(UPDATED_PAYMENT);
        assertThat(testPaymentEntry.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPaymentEntry.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingPaymentEntry() throws Exception {
        int databaseSizeBeforeUpdate = paymentEntryRepository.findAll().size();

        // Create the PaymentEntry

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentEntryMockMvc.perform(put("/api/payment-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentEntry)))
            .andExpect(status().isBadRequest());

        // Validate the PaymentEntry in the database
        List<PaymentEntry> paymentEntryList = paymentEntryRepository.findAll();
        assertThat(paymentEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePaymentEntry() throws Exception {
        // Initialize the database
        paymentEntryRepository.saveAndFlush(paymentEntry);

        int databaseSizeBeforeDelete = paymentEntryRepository.findAll().size();

        // Delete the paymentEntry
        restPaymentEntryMockMvc.perform(delete("/api/payment-entries/{id}", paymentEntry.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PaymentEntry> paymentEntryList = paymentEntryRepository.findAll();
        assertThat(paymentEntryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
