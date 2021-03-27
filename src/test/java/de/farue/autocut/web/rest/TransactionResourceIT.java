package de.farue.autocut.web.rest;

import static de.farue.autocut.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionKind;
import de.farue.autocut.repository.TransactionRepository;
import de.farue.autocut.service.TransactionService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TransactionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TransactionResourceIT {

    private static final TransactionKind DEFAULT_KIND = TransactionKind.FEE;
    private static final TransactionKind UPDATED_KIND = TransactionKind.CREDIT;

    private static final Instant DEFAULT_BOOKING_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BOOKING_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_VALUE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALUE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALUE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_BALANCE_AFTER = new BigDecimal(1);
    private static final BigDecimal UPDATED_BALANCE_AFTER = new BigDecimal(2);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_SERVICE_QULIFIER = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE_QULIFIER = "BBBBBBBBBB";

    private static final String DEFAULT_ISSUER = "AAAAAAAAAA";
    private static final String UPDATED_ISSUER = "BBBBBBBBBB";

    private static final String DEFAULT_RECIPIENT = "AAAAAAAAAA";
    private static final String UPDATED_RECIPIENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionRepository transactionRepositoryMock;

    @Mock
    private TransactionService transactionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionMockMvc;

    private Transaction transaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .kind(DEFAULT_KIND)
            .bookingDate(DEFAULT_BOOKING_DATE)
            .valueDate(DEFAULT_VALUE_DATE)
            .value(DEFAULT_VALUE)
            .balanceAfter(DEFAULT_BALANCE_AFTER)
            .description(DEFAULT_DESCRIPTION)
            .serviceQulifier(DEFAULT_SERVICE_QULIFIER)
            .issuer(DEFAULT_ISSUER)
            .recipient(DEFAULT_RECIPIENT);
        // Add required entity
        TransactionBook transactionBook;
        if (TestUtil.findAll(em, TransactionBook.class).isEmpty()) {
            transactionBook = TransactionBookResourceIT.createEntity(em);
            em.persist(transactionBook);
            em.flush();
        } else {
            transactionBook = TestUtil.findAll(em, TransactionBook.class).get(0);
        }
        transaction.setTransactionBook(transactionBook);
        return transaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createUpdatedEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .kind(UPDATED_KIND)
            .bookingDate(UPDATED_BOOKING_DATE)
            .valueDate(UPDATED_VALUE_DATE)
            .value(UPDATED_VALUE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .description(UPDATED_DESCRIPTION)
            .serviceQulifier(UPDATED_SERVICE_QULIFIER)
            .issuer(UPDATED_ISSUER)
            .recipient(UPDATED_RECIPIENT);
        // Add required entity
        TransactionBook transactionBook;
        if (TestUtil.findAll(em, TransactionBook.class).isEmpty()) {
            transactionBook = TransactionBookResourceIT.createUpdatedEntity(em);
            em.persist(transactionBook);
            em.flush();
        } else {
            transactionBook = TestUtil.findAll(em, TransactionBook.class).get(0);
        }
        transaction.setTransactionBook(transactionBook);
        return transaction;
    }

    @BeforeEach
    public void initTest() {
        transaction = createEntity(em);
    }

    @Test
    @Transactional
    void createTransaction() throws Exception {
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        // Create the Transaction
        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getKind()).isEqualTo(DEFAULT_KIND);
        assertThat(testTransaction.getBookingDate()).isEqualTo(DEFAULT_BOOKING_DATE);
        assertThat(testTransaction.getValueDate()).isEqualTo(DEFAULT_VALUE_DATE);
        assertThat(testTransaction.getValue()).isEqualByComparingTo(DEFAULT_VALUE);
        assertThat(testTransaction.getBalanceAfter()).isEqualByComparingTo(DEFAULT_BALANCE_AFTER);
        assertThat(testTransaction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTransaction.getServiceQulifier()).isEqualTo(DEFAULT_SERVICE_QULIFIER);
        assertThat(testTransaction.getIssuer()).isEqualTo(DEFAULT_ISSUER);
        assertThat(testTransaction.getRecipient()).isEqualTo(DEFAULT_RECIPIENT);
    }

    @Test
    @Transactional
    void createTransactionWithExistingId() throws Exception {
        // Create the Transaction with an existing ID
        transaction.setId(1L);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkKindIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setKind(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBookingDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setBookingDate(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValueDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setValueDate(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setValue(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBalanceAfterIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setBalanceAfter(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIssuerIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setIssuer(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransactions() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].kind").value(hasItem(DEFAULT_KIND.toString())))
            .andExpect(jsonPath("$.[*].bookingDate").value(hasItem(DEFAULT_BOOKING_DATE.toString())))
            .andExpect(jsonPath("$.[*].valueDate").value(hasItem(DEFAULT_VALUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(sameNumber(DEFAULT_VALUE))))
            .andExpect(jsonPath("$.[*].balanceAfter").value(hasItem(sameNumber(DEFAULT_BALANCE_AFTER))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].serviceQulifier").value(hasItem(DEFAULT_SERVICE_QULIFIER)))
            .andExpect(jsonPath("$.[*].issuer").value(hasItem(DEFAULT_ISSUER)))
            .andExpect(jsonPath("$.[*].recipient").value(hasItem(DEFAULT_RECIPIENT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransactionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(transactionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(transactionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransactionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(transactionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(transactionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get the transaction
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
            .andExpect(jsonPath("$.kind").value(DEFAULT_KIND.toString()))
            .andExpect(jsonPath("$.bookingDate").value(DEFAULT_BOOKING_DATE.toString()))
            .andExpect(jsonPath("$.valueDate").value(DEFAULT_VALUE_DATE.toString()))
            .andExpect(jsonPath("$.value").value(sameNumber(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.balanceAfter").value(sameNumber(DEFAULT_BALANCE_AFTER)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.serviceQulifier").value(DEFAULT_SERVICE_QULIFIER))
            .andExpect(jsonPath("$.issuer").value(DEFAULT_ISSUER))
            .andExpect(jsonPath("$.recipient").value(DEFAULT_RECIPIENT));
    }

    @Test
    @Transactional
    void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findById(transaction.getId()).get();
        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
        em.detach(updatedTransaction);
        updatedTransaction
            .kind(UPDATED_KIND)
            .bookingDate(UPDATED_BOOKING_DATE)
            .valueDate(UPDATED_VALUE_DATE)
            .value(UPDATED_VALUE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .description(UPDATED_DESCRIPTION)
            .serviceQulifier(UPDATED_SERVICE_QULIFIER)
            .issuer(UPDATED_ISSUER)
            .recipient(UPDATED_RECIPIENT);

        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTransaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getKind()).isEqualTo(UPDATED_KIND);
        assertThat(testTransaction.getBookingDate()).isEqualTo(UPDATED_BOOKING_DATE);
        assertThat(testTransaction.getValueDate()).isEqualTo(UPDATED_VALUE_DATE);
        assertThat(testTransaction.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testTransaction.getBalanceAfter()).isEqualTo(UPDATED_BALANCE_AFTER);
        assertThat(testTransaction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTransaction.getServiceQulifier()).isEqualTo(UPDATED_SERVICE_QULIFIER);
        assertThat(testTransaction.getIssuer()).isEqualTo(UPDATED_ISSUER);
        assertThat(testTransaction.getRecipient()).isEqualTo(UPDATED_RECIPIENT);
    }

    @Test
    @Transactional
    void putNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction.bookingDate(UPDATED_BOOKING_DATE).issuer(UPDATED_ISSUER);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getKind()).isEqualTo(DEFAULT_KIND);
        assertThat(testTransaction.getBookingDate()).isEqualTo(UPDATED_BOOKING_DATE);
        assertThat(testTransaction.getValueDate()).isEqualTo(DEFAULT_VALUE_DATE);
        assertThat(testTransaction.getValue()).isEqualByComparingTo(DEFAULT_VALUE);
        assertThat(testTransaction.getBalanceAfter()).isEqualByComparingTo(DEFAULT_BALANCE_AFTER);
        assertThat(testTransaction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTransaction.getServiceQulifier()).isEqualTo(DEFAULT_SERVICE_QULIFIER);
        assertThat(testTransaction.getIssuer()).isEqualTo(UPDATED_ISSUER);
        assertThat(testTransaction.getRecipient()).isEqualTo(DEFAULT_RECIPIENT);
    }

    @Test
    @Transactional
    void fullUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction
            .kind(UPDATED_KIND)
            .bookingDate(UPDATED_BOOKING_DATE)
            .valueDate(UPDATED_VALUE_DATE)
            .value(UPDATED_VALUE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .description(UPDATED_DESCRIPTION)
            .serviceQulifier(UPDATED_SERVICE_QULIFIER)
            .issuer(UPDATED_ISSUER)
            .recipient(UPDATED_RECIPIENT);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getKind()).isEqualTo(UPDATED_KIND);
        assertThat(testTransaction.getBookingDate()).isEqualTo(UPDATED_BOOKING_DATE);
        assertThat(testTransaction.getValueDate()).isEqualTo(UPDATED_VALUE_DATE);
        assertThat(testTransaction.getValue()).isEqualByComparingTo(UPDATED_VALUE);
        assertThat(testTransaction.getBalanceAfter()).isEqualByComparingTo(UPDATED_BALANCE_AFTER);
        assertThat(testTransaction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTransaction.getServiceQulifier()).isEqualTo(UPDATED_SERVICE_QULIFIER);
        assertThat(testTransaction.getIssuer()).isEqualTo(UPDATED_ISSUER);
        assertThat(testTransaction.getRecipient()).isEqualTo(UPDATED_RECIPIENT);
    }

    @Test
    @Transactional
    void patchNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeDelete = transactionRepository.findAll().size();

        // Delete the transaction
        restTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, transaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
