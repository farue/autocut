package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.PaymentAccount;
import de.farue.autocut.repository.PaymentAccountRepository;
import de.farue.autocut.service.PaymentAccountService;
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
import java.util.List;

import static de.farue.autocut.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PaymentAccountResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
public class PaymentAccountResourceIT {

    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_BALANCE = new BigDecimal(2);

    @Autowired
    private PaymentAccountRepository paymentAccountRepository;

    @Autowired
    private PaymentAccountService paymentAccountService;

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

    private MockMvc restPaymentAccountMockMvc;

    private PaymentAccount paymentAccount;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PaymentAccountResource paymentAccountResource = new PaymentAccountResource(paymentAccountService);
        this.restPaymentAccountMockMvc = MockMvcBuilders.standaloneSetup(paymentAccountResource)
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
    public static PaymentAccount createEntity(EntityManager em) {
        PaymentAccount paymentAccount = new PaymentAccount()
            .balance(DEFAULT_BALANCE);
        return paymentAccount;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentAccount createUpdatedEntity(EntityManager em) {
        PaymentAccount paymentAccount = new PaymentAccount()
            .balance(UPDATED_BALANCE);
        return paymentAccount;
    }

    @BeforeEach
    public void initTest() {
        paymentAccount = createEntity(em);
    }

    @Test
    @Transactional
    public void createPaymentAccount() throws Exception {
        int databaseSizeBeforeCreate = paymentAccountRepository.findAll().size();

        // Create the PaymentAccount
        restPaymentAccountMockMvc.perform(post("/api/payment-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentAccount)))
            .andExpect(status().isCreated());

        // Validate the PaymentAccount in the database
        List<PaymentAccount> paymentAccountList = paymentAccountRepository.findAll();
        assertThat(paymentAccountList).hasSize(databaseSizeBeforeCreate + 1);
        PaymentAccount testPaymentAccount = paymentAccountList.get(paymentAccountList.size() - 1);
        assertThat(testPaymentAccount.getBalance()).isEqualTo(DEFAULT_BALANCE);
    }

    @Test
    @Transactional
    public void createPaymentAccountWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = paymentAccountRepository.findAll().size();

        // Create the PaymentAccount with an existing ID
        paymentAccount.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentAccountMockMvc.perform(post("/api/payment-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentAccount)))
            .andExpect(status().isBadRequest());

        // Validate the PaymentAccount in the database
        List<PaymentAccount> paymentAccountList = paymentAccountRepository.findAll();
        assertThat(paymentAccountList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkBalanceIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentAccountRepository.findAll().size();
        // set the field null
        paymentAccount.setBalance(null);

        // Create the PaymentAccount, which fails.

        restPaymentAccountMockMvc.perform(post("/api/payment-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentAccount)))
            .andExpect(status().isBadRequest());

        List<PaymentAccount> paymentAccountList = paymentAccountRepository.findAll();
        assertThat(paymentAccountList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPaymentAccounts() throws Exception {
        // Initialize the database
        paymentAccountRepository.saveAndFlush(paymentAccount);

        // Get all the paymentAccountList
        restPaymentAccountMockMvc.perform(get("/api/payment-accounts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].balance").value(hasItem(DEFAULT_BALANCE.intValue())));
    }
    
    @Test
    @Transactional
    public void getPaymentAccount() throws Exception {
        // Initialize the database
        paymentAccountRepository.saveAndFlush(paymentAccount);

        // Get the paymentAccount
        restPaymentAccountMockMvc.perform(get("/api/payment-accounts/{id}", paymentAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(paymentAccount.getId().intValue()))
            .andExpect(jsonPath("$.balance").value(DEFAULT_BALANCE.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPaymentAccount() throws Exception {
        // Get the paymentAccount
        restPaymentAccountMockMvc.perform(get("/api/payment-accounts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePaymentAccount() throws Exception {
        // Initialize the database
        paymentAccountService.save(paymentAccount);

        int databaseSizeBeforeUpdate = paymentAccountRepository.findAll().size();

        // Update the paymentAccount
        PaymentAccount updatedPaymentAccount = paymentAccountRepository.findById(paymentAccount.getId()).get();
        // Disconnect from session so that the updates on updatedPaymentAccount are not directly saved in db
        em.detach(updatedPaymentAccount);
        updatedPaymentAccount
            .balance(UPDATED_BALANCE);

        restPaymentAccountMockMvc.perform(put("/api/payment-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPaymentAccount)))
            .andExpect(status().isOk());

        // Validate the PaymentAccount in the database
        List<PaymentAccount> paymentAccountList = paymentAccountRepository.findAll();
        assertThat(paymentAccountList).hasSize(databaseSizeBeforeUpdate);
        PaymentAccount testPaymentAccount = paymentAccountList.get(paymentAccountList.size() - 1);
        assertThat(testPaymentAccount.getBalance()).isEqualTo(UPDATED_BALANCE);
    }

    @Test
    @Transactional
    public void updateNonExistingPaymentAccount() throws Exception {
        int databaseSizeBeforeUpdate = paymentAccountRepository.findAll().size();

        // Create the PaymentAccount

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentAccountMockMvc.perform(put("/api/payment-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentAccount)))
            .andExpect(status().isBadRequest());

        // Validate the PaymentAccount in the database
        List<PaymentAccount> paymentAccountList = paymentAccountRepository.findAll();
        assertThat(paymentAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePaymentAccount() throws Exception {
        // Initialize the database
        paymentAccountService.save(paymentAccount);

        int databaseSizeBeforeDelete = paymentAccountRepository.findAll().size();

        // Delete the paymentAccount
        restPaymentAccountMockMvc.perform(delete("/api/payment-accounts/{id}", paymentAccount.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PaymentAccount> paymentAccountList = paymentAccountRepository.findAll();
        assertThat(paymentAccountList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
