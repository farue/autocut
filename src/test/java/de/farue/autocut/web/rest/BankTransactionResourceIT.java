package de.farue.autocut.web.rest;

import static de.farue.autocut.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.BankAccount;
import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.repository.BankTransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BankTransactionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BankTransactionResourceIT {

    private static final Instant DEFAULT_BOOKING_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BOOKING_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_VALUE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALUE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALUE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_BALANCE_AFTER = new BigDecimal(1);
    private static final BigDecimal UPDATED_BALANCE_AFTER = new BigDecimal(2);

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CUSTOMER_REF = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER_REF = "BBBBBBBBBB";

    private static final String DEFAULT_GV_CODE = "AAAAAAAAAA";
    private static final String UPDATED_GV_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_END_TO_END = "AAAAAAAAAA";
    private static final String UPDATED_END_TO_END = "BBBBBBBBBB";

    private static final String DEFAULT_PRIMANOTA = "AAAAAAAAAA";
    private static final String UPDATED_PRIMANOTA = "BBBBBBBBBB";

    private static final String DEFAULT_CREDITOR = "AAAAAAAAAA";
    private static final String UPDATED_CREDITOR = "BBBBBBBBBB";

    private static final String DEFAULT_MANDATE = "AAAAAAAAAA";
    private static final String UPDATED_MANDATE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/bank-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Mock
    private BankTransactionRepository bankTransactionRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBankTransactionMockMvc;

    private BankTransaction bankTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BankTransaction createEntity(EntityManager em) {
        BankTransaction bankTransaction = new BankTransaction()
            .bookingDate(DEFAULT_BOOKING_DATE)
            .valueDate(DEFAULT_VALUE_DATE)
            .value(DEFAULT_VALUE)
            .balanceAfter(DEFAULT_BALANCE_AFTER)
            .type(DEFAULT_TYPE)
            .description(DEFAULT_DESCRIPTION)
            .customerRef(DEFAULT_CUSTOMER_REF)
            .gvCode(DEFAULT_GV_CODE)
            .endToEnd(DEFAULT_END_TO_END)
            .primanota(DEFAULT_PRIMANOTA)
            .creditor(DEFAULT_CREDITOR)
            .mandate(DEFAULT_MANDATE);
        // Add required entity
        BankAccount bankAccount;
        if (TestUtil.findAll(em, BankAccount.class).isEmpty()) {
            bankAccount = BankAccountResourceIT.createEntity(em);
            em.persist(bankAccount);
            em.flush();
        } else {
            bankAccount = TestUtil.findAll(em, BankAccount.class).get(0);
        }
        bankTransaction.setBankAccount(bankAccount);
        // Add required entity
        TransactionBook transactionBook;
        if (TestUtil.findAll(em, TransactionBook.class).isEmpty()) {
            transactionBook = TransactionBookResourceIT.createEntity(em);
            em.persist(transactionBook);
            em.flush();
        } else {
            transactionBook = TestUtil.findAll(em, TransactionBook.class).get(0);
        }
        bankTransaction.setTransactionBook(transactionBook);
        return bankTransaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BankTransaction createUpdatedEntity(EntityManager em) {
        BankTransaction bankTransaction = new BankTransaction()
            .bookingDate(UPDATED_BOOKING_DATE)
            .valueDate(UPDATED_VALUE_DATE)
            .value(UPDATED_VALUE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION)
            .customerRef(UPDATED_CUSTOMER_REF)
            .gvCode(UPDATED_GV_CODE)
            .endToEnd(UPDATED_END_TO_END)
            .primanota(UPDATED_PRIMANOTA)
            .creditor(UPDATED_CREDITOR)
            .mandate(UPDATED_MANDATE);
        // Add required entity
        BankAccount bankAccount;
        if (TestUtil.findAll(em, BankAccount.class).isEmpty()) {
            bankAccount = BankAccountResourceIT.createUpdatedEntity(em);
            em.persist(bankAccount);
            em.flush();
        } else {
            bankAccount = TestUtil.findAll(em, BankAccount.class).get(0);
        }
        bankTransaction.setBankAccount(bankAccount);
        // Add required entity
        TransactionBook transactionBook;
        if (TestUtil.findAll(em, TransactionBook.class).isEmpty()) {
            transactionBook = TransactionBookResourceIT.createUpdatedEntity(em);
            em.persist(transactionBook);
            em.flush();
        } else {
            transactionBook = TestUtil.findAll(em, TransactionBook.class).get(0);
        }
        bankTransaction.setTransactionBook(transactionBook);
        return bankTransaction;
    }

    @BeforeEach
    public void initTest() {
        bankTransaction = createEntity(em);
    }

    @Test
    @Transactional
    void createBankTransaction() throws Exception {
        int databaseSizeBeforeCreate = bankTransactionRepository.findAll().size();
        // Create the BankTransaction
        restBankTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isCreated());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeCreate + 1);
        BankTransaction testBankTransaction = bankTransactionList.get(bankTransactionList.size() - 1);
        assertThat(testBankTransaction.getBookingDate()).isEqualTo(DEFAULT_BOOKING_DATE);
        assertThat(testBankTransaction.getValueDate()).isEqualTo(DEFAULT_VALUE_DATE);
        assertThat(testBankTransaction.getValue()).isEqualByComparingTo(DEFAULT_VALUE);
        assertThat(testBankTransaction.getBalanceAfter()).isEqualByComparingTo(DEFAULT_BALANCE_AFTER);
        assertThat(testBankTransaction.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testBankTransaction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBankTransaction.getCustomerRef()).isEqualTo(DEFAULT_CUSTOMER_REF);
        assertThat(testBankTransaction.getGvCode()).isEqualTo(DEFAULT_GV_CODE);
        assertThat(testBankTransaction.getEndToEnd()).isEqualTo(DEFAULT_END_TO_END);
        assertThat(testBankTransaction.getPrimanota()).isEqualTo(DEFAULT_PRIMANOTA);
        assertThat(testBankTransaction.getCreditor()).isEqualTo(DEFAULT_CREDITOR);
        assertThat(testBankTransaction.getMandate()).isEqualTo(DEFAULT_MANDATE);
    }

    @Test
    @Transactional
    void createBankTransactionWithExistingId() throws Exception {
        // Create the BankTransaction with an existing ID
        bankTransaction.setId(1L);

        int databaseSizeBeforeCreate = bankTransactionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBankTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBookingDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankTransactionRepository.findAll().size();
        // set the field null
        bankTransaction.setBookingDate(null);

        // Create the BankTransaction, which fails.

        restBankTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValueDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankTransactionRepository.findAll().size();
        // set the field null
        bankTransaction.setValueDate(null);

        // Create the BankTransaction, which fails.

        restBankTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankTransactionRepository.findAll().size();
        // set the field null
        bankTransaction.setValue(null);

        // Create the BankTransaction, which fails.

        restBankTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBalanceAfterIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankTransactionRepository.findAll().size();
        // set the field null
        bankTransaction.setBalanceAfter(null);

        // Create the BankTransaction, which fails.

        restBankTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBankTransactions() throws Exception {
        // Initialize the database
        bankTransactionRepository.saveAndFlush(bankTransaction);

        // Get all the bankTransactionList
        restBankTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bankTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].bookingDate").value(hasItem(DEFAULT_BOOKING_DATE.toString())))
            .andExpect(jsonPath("$.[*].valueDate").value(hasItem(DEFAULT_VALUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(sameNumber(DEFAULT_VALUE))))
            .andExpect(jsonPath("$.[*].balanceAfter").value(hasItem(sameNumber(DEFAULT_BALANCE_AFTER))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].customerRef").value(hasItem(DEFAULT_CUSTOMER_REF)))
            .andExpect(jsonPath("$.[*].gvCode").value(hasItem(DEFAULT_GV_CODE)))
            .andExpect(jsonPath("$.[*].endToEnd").value(hasItem(DEFAULT_END_TO_END)))
            .andExpect(jsonPath("$.[*].primanota").value(hasItem(DEFAULT_PRIMANOTA)))
            .andExpect(jsonPath("$.[*].creditor").value(hasItem(DEFAULT_CREDITOR)))
            .andExpect(jsonPath("$.[*].mandate").value(hasItem(DEFAULT_MANDATE)));
    }

    void getAllBankTransactionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(bankTransactionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl<>(new ArrayList<>()));

        restBankTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bankTransactionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    void getAllBankTransactionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bankTransactionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl<>(new ArrayList<>()));

        restBankTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bankTransactionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getBankTransaction() throws Exception {
        // Initialize the database
        bankTransactionRepository.saveAndFlush(bankTransaction);

        // Get the bankTransaction
        restBankTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, bankTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bankTransaction.getId().intValue()))
            .andExpect(jsonPath("$.bookingDate").value(DEFAULT_BOOKING_DATE.toString()))
            .andExpect(jsonPath("$.valueDate").value(DEFAULT_VALUE_DATE.toString()))
            .andExpect(jsonPath("$.value").value(sameNumber(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.balanceAfter").value(sameNumber(DEFAULT_BALANCE_AFTER)))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.customerRef").value(DEFAULT_CUSTOMER_REF))
            .andExpect(jsonPath("$.gvCode").value(DEFAULT_GV_CODE))
            .andExpect(jsonPath("$.endToEnd").value(DEFAULT_END_TO_END))
            .andExpect(jsonPath("$.primanota").value(DEFAULT_PRIMANOTA))
            .andExpect(jsonPath("$.creditor").value(DEFAULT_CREDITOR))
            .andExpect(jsonPath("$.mandate").value(DEFAULT_MANDATE));
    }

    @Test
    @Transactional
    void getNonExistingBankTransaction() throws Exception {
        // Get the bankTransaction
        restBankTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBankTransaction() throws Exception {
        // Initialize the database
        bankTransactionRepository.saveAndFlush(bankTransaction);

        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();

        // Update the bankTransaction
        BankTransaction updatedBankTransaction = bankTransactionRepository.findById(bankTransaction.getId()).get();
        // Disconnect from session so that the updates on updatedBankTransaction are not directly saved in db
        em.detach(updatedBankTransaction);
        updatedBankTransaction
            .bookingDate(UPDATED_BOOKING_DATE)
            .valueDate(UPDATED_VALUE_DATE)
            .value(UPDATED_VALUE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION)
            .customerRef(UPDATED_CUSTOMER_REF)
            .gvCode(UPDATED_GV_CODE)
            .endToEnd(UPDATED_END_TO_END)
            .primanota(UPDATED_PRIMANOTA)
            .creditor(UPDATED_CREDITOR)
            .mandate(UPDATED_MANDATE);

        restBankTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBankTransaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBankTransaction))
            )
            .andExpect(status().isOk());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
        BankTransaction testBankTransaction = bankTransactionList.get(bankTransactionList.size() - 1);
        assertThat(testBankTransaction.getBookingDate()).isEqualTo(UPDATED_BOOKING_DATE);
        assertThat(testBankTransaction.getValueDate()).isEqualTo(UPDATED_VALUE_DATE);
        assertThat(testBankTransaction.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testBankTransaction.getBalanceAfter()).isEqualTo(UPDATED_BALANCE_AFTER);
        assertThat(testBankTransaction.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBankTransaction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBankTransaction.getCustomerRef()).isEqualTo(UPDATED_CUSTOMER_REF);
        assertThat(testBankTransaction.getGvCode()).isEqualTo(UPDATED_GV_CODE);
        assertThat(testBankTransaction.getEndToEnd()).isEqualTo(UPDATED_END_TO_END);
        assertThat(testBankTransaction.getPrimanota()).isEqualTo(UPDATED_PRIMANOTA);
        assertThat(testBankTransaction.getCreditor()).isEqualTo(UPDATED_CREDITOR);
        assertThat(testBankTransaction.getMandate()).isEqualTo(UPDATED_MANDATE);
    }

    @Test
    @Transactional
    void putNonExistingBankTransaction() throws Exception {
        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();
        bankTransaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBankTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bankTransaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBankTransaction() throws Exception {
        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();
        bankTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBankTransaction() throws Exception {
        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();
        bankTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankTransactionMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBankTransactionWithPatch() throws Exception {
        // Initialize the database
        bankTransactionRepository.saveAndFlush(bankTransaction);

        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();

        // Update the bankTransaction using partial update
        BankTransaction partialUpdatedBankTransaction = new BankTransaction();
        partialUpdatedBankTransaction.setId(bankTransaction.getId());

        partialUpdatedBankTransaction
            .valueDate(UPDATED_VALUE_DATE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .type(UPDATED_TYPE)
            .customerRef(UPDATED_CUSTOMER_REF)
            .creditor(UPDATED_CREDITOR)
            .mandate(UPDATED_MANDATE);

        restBankTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBankTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBankTransaction))
            )
            .andExpect(status().isOk());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
        BankTransaction testBankTransaction = bankTransactionList.get(bankTransactionList.size() - 1);
        assertThat(testBankTransaction.getBookingDate()).isEqualTo(DEFAULT_BOOKING_DATE);
        assertThat(testBankTransaction.getValueDate()).isEqualTo(UPDATED_VALUE_DATE);
        assertThat(testBankTransaction.getValue()).isEqualByComparingTo(DEFAULT_VALUE);
        assertThat(testBankTransaction.getBalanceAfter()).isEqualByComparingTo(UPDATED_BALANCE_AFTER);
        assertThat(testBankTransaction.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBankTransaction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBankTransaction.getCustomerRef()).isEqualTo(UPDATED_CUSTOMER_REF);
        assertThat(testBankTransaction.getGvCode()).isEqualTo(DEFAULT_GV_CODE);
        assertThat(testBankTransaction.getEndToEnd()).isEqualTo(DEFAULT_END_TO_END);
        assertThat(testBankTransaction.getPrimanota()).isEqualTo(DEFAULT_PRIMANOTA);
        assertThat(testBankTransaction.getCreditor()).isEqualTo(UPDATED_CREDITOR);
        assertThat(testBankTransaction.getMandate()).isEqualTo(UPDATED_MANDATE);
    }

    @Test
    @Transactional
    void fullUpdateBankTransactionWithPatch() throws Exception {
        // Initialize the database
        bankTransactionRepository.saveAndFlush(bankTransaction);

        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();

        // Update the bankTransaction using partial update
        BankTransaction partialUpdatedBankTransaction = new BankTransaction();
        partialUpdatedBankTransaction.setId(bankTransaction.getId());

        partialUpdatedBankTransaction
            .bookingDate(UPDATED_BOOKING_DATE)
            .valueDate(UPDATED_VALUE_DATE)
            .value(UPDATED_VALUE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION)
            .customerRef(UPDATED_CUSTOMER_REF)
            .gvCode(UPDATED_GV_CODE)
            .endToEnd(UPDATED_END_TO_END)
            .primanota(UPDATED_PRIMANOTA)
            .creditor(UPDATED_CREDITOR)
            .mandate(UPDATED_MANDATE);

        restBankTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBankTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBankTransaction))
            )
            .andExpect(status().isOk());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
        BankTransaction testBankTransaction = bankTransactionList.get(bankTransactionList.size() - 1);
        assertThat(testBankTransaction.getBookingDate()).isEqualTo(UPDATED_BOOKING_DATE);
        assertThat(testBankTransaction.getValueDate()).isEqualTo(UPDATED_VALUE_DATE);
        assertThat(testBankTransaction.getValue()).isEqualByComparingTo(UPDATED_VALUE);
        assertThat(testBankTransaction.getBalanceAfter()).isEqualByComparingTo(UPDATED_BALANCE_AFTER);
        assertThat(testBankTransaction.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBankTransaction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBankTransaction.getCustomerRef()).isEqualTo(UPDATED_CUSTOMER_REF);
        assertThat(testBankTransaction.getGvCode()).isEqualTo(UPDATED_GV_CODE);
        assertThat(testBankTransaction.getEndToEnd()).isEqualTo(UPDATED_END_TO_END);
        assertThat(testBankTransaction.getPrimanota()).isEqualTo(UPDATED_PRIMANOTA);
        assertThat(testBankTransaction.getCreditor()).isEqualTo(UPDATED_CREDITOR);
        assertThat(testBankTransaction.getMandate()).isEqualTo(UPDATED_MANDATE);
    }

    @Test
    @Transactional
    void patchNonExistingBankTransaction() throws Exception {
        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();
        bankTransaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBankTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bankTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBankTransaction() throws Exception {
        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();
        bankTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBankTransaction() throws Exception {
        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();
        bankTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBankTransaction() throws Exception {
        // Initialize the database
        bankTransactionRepository.saveAndFlush(bankTransaction);

        int databaseSizeBeforeDelete = bankTransactionRepository.findAll().size();

        // Delete the bankTransaction
        restBankTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, bankTransaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
