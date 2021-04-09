package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.repository.InternalTransactionRepository;
import de.farue.autocut.service.accounting.InternalTransactionService;
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
 * Integration tests for the {@link InternalTransactionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InternalTransactionResourceIT {

    private static final TransactionType DEFAULT_TYPE = TransactionType.TRANSFER;
    private static final TransactionType UPDATED_TYPE = TransactionType.CORRECTION;

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

    private static final String ENTITY_API_URL = "/api/internal-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InternalTransactionRepository internalTransactionRepository;

    @Mock
    private InternalTransactionRepository internalTransactionRepositoryMock;

    @Mock
    private InternalTransactionService internalTransactionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInternalTransactionMockMvc;

    private InternalTransaction internalTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InternalTransaction createEntity(EntityManager em) {
        InternalTransaction internalTransaction = new InternalTransaction()
            .transactionType(DEFAULT_TYPE)
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
        internalTransaction.setTransactionBook(transactionBook);
        return internalTransaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InternalTransaction createUpdatedEntity(EntityManager em) {
        InternalTransaction internalTransaction = new InternalTransaction()
            .transactionType(UPDATED_TYPE)
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
        internalTransaction.setTransactionBook(transactionBook);
        return internalTransaction;
    }

    @BeforeEach
    public void initTest() {
        internalTransaction = createEntity(em);
    }

    @Test
    @Transactional
    void createInternalTransaction() throws Exception {
        int databaseSizeBeforeCreate = internalTransactionRepository.findAll().size();
        // Create the InternalTransaction
        restInternalTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isCreated());

        // Validate the InternalTransaction in the database
        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeCreate + 1);
        InternalTransaction testInternalTransaction = internalTransactionList.get(internalTransactionList.size() - 1);
        assertThat(testInternalTransaction.getTransactionType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testInternalTransaction.getBookingDate()).isEqualTo(DEFAULT_BOOKING_DATE);
        assertThat(testInternalTransaction.getValueDate()).isEqualTo(DEFAULT_VALUE_DATE);
        assertThat(testInternalTransaction.getValue()).isEqualByComparingTo(DEFAULT_VALUE);
        assertThat(testInternalTransaction.getBalanceAfter()).isEqualByComparingTo(DEFAULT_BALANCE_AFTER);
        assertThat(testInternalTransaction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testInternalTransaction.getServiceQulifier()).isEqualTo(DEFAULT_SERVICE_QULIFIER);
        assertThat(testInternalTransaction.getIssuer()).isEqualTo(DEFAULT_ISSUER);
        assertThat(testInternalTransaction.getRecipient()).isEqualTo(DEFAULT_RECIPIENT);
    }

    @Test
    @Transactional
    void createInternalTransactionWithExistingId() throws Exception {
        // Create the InternalTransaction with an existing ID
        internalTransaction.setId(1L);

        int databaseSizeBeforeCreate = internalTransactionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInternalTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalTransaction in the database
        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = internalTransactionRepository.findAll().size();
        // set the field null
        internalTransaction.setType(null);

        // Create the InternalTransaction, which fails.

        restInternalTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isBadRequest());

        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBookingDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = internalTransactionRepository.findAll().size();
        // set the field null
        internalTransaction.setBookingDate(null);

        // Create the InternalTransaction, which fails.

        restInternalTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isBadRequest());

        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValueDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = internalTransactionRepository.findAll().size();
        // set the field null
        internalTransaction.setValueDate(null);

        // Create the InternalTransaction, which fails.

        restInternalTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isBadRequest());

        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = internalTransactionRepository.findAll().size();
        // set the field null
        internalTransaction.setValue(null);

        // Create the InternalTransaction, which fails.

        restInternalTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isBadRequest());

        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBalanceAfterIsRequired() throws Exception {
        int databaseSizeBeforeTest = internalTransactionRepository.findAll().size();
        // set the field null
        internalTransaction.setBalanceAfter(null);

        // Create the InternalTransaction, which fails.

        restInternalTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isBadRequest());

        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIssuerIsRequired() throws Exception {
        int databaseSizeBeforeTest = internalTransactionRepository.findAll().size();
        // set the field null
        internalTransaction.setIssuer(null);

        // Create the InternalTransaction, which fails.

        restInternalTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isBadRequest());

        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInternalTransactions() throws Exception {
        // Initialize the database
        internalTransactionRepository.saveAndFlush(internalTransaction);

        // Get all the internalTransactionList
        restInternalTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(internalTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.name())))
            .andExpect(jsonPath("$.[*].bookingDate").value(hasItem(DEFAULT_BOOKING_DATE.toString())))
            .andExpect(jsonPath("$.[*].valueDate").value(hasItem(DEFAULT_VALUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())))
            .andExpect(jsonPath("$.[*].balanceAfter").value(hasItem(DEFAULT_BALANCE_AFTER.intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].serviceQulifier").value(hasItem(DEFAULT_SERVICE_QULIFIER)))
            .andExpect(jsonPath("$.[*].issuer").value(hasItem(DEFAULT_ISSUER)))
            .andExpect(jsonPath("$.[*].recipient").value(hasItem(DEFAULT_RECIPIENT)));
    }

    void getAllInternalTransactionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(internalTransactionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl<>(new ArrayList<>()));

        restInternalTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(internalTransactionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    void getAllInternalTransactionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(internalTransactionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl<>(new ArrayList<>()));

        restInternalTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(internalTransactionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getInternalTransaction() throws Exception {
        // Initialize the database
        internalTransactionRepository.saveAndFlush(internalTransaction);

        // Get the internalTransaction
        restInternalTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, internalTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(internalTransaction.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.name()))
            .andExpect(jsonPath("$.bookingDate").value(DEFAULT_BOOKING_DATE.toString()))
            .andExpect(jsonPath("$.valueDate").value(DEFAULT_VALUE_DATE.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.intValue()))
            .andExpect(jsonPath("$.balanceAfter").value(DEFAULT_BALANCE_AFTER.intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.serviceQulifier").value(DEFAULT_SERVICE_QULIFIER))
            .andExpect(jsonPath("$.issuer").value(DEFAULT_ISSUER))
            .andExpect(jsonPath("$.recipient").value(DEFAULT_RECIPIENT));
    }

    @Test
    @Transactional
    void getNonExistingInternalTransaction() throws Exception {
        // Get the internalTransaction
        restInternalTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewInternalTransaction() throws Exception {
        // Initialize the database
        internalTransactionRepository.saveAndFlush(internalTransaction);

        int databaseSizeBeforeUpdate = internalTransactionRepository.findAll().size();

        // Update the internalTransaction
        InternalTransaction updatedInternalTransaction = internalTransactionRepository.findById(internalTransaction.getId()).get();
        // Disconnect from session so that the updates on updatedInternalTransaction are not directly saved in db
        em.detach(updatedInternalTransaction);
        updatedInternalTransaction
            .transactionType(UPDATED_TYPE)
            .bookingDate(DEFAULT_BOOKING_DATE)
            .valueDate(DEFAULT_VALUE_DATE) // updating value not allowed
            .value(UPDATED_VALUE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .description(UPDATED_DESCRIPTION)
            .serviceQulifier(UPDATED_SERVICE_QULIFIER)
            .issuer(UPDATED_ISSUER)
            .recipient(UPDATED_RECIPIENT);

        restInternalTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInternalTransaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedInternalTransaction))
            )
            .andExpect(status().isOk());

        // Validate the InternalTransaction in the database
        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeUpdate);
        InternalTransaction testInternalTransaction = internalTransactionList.get(internalTransactionList.size() - 1);
        assertThat(testInternalTransaction.getTransactionType()).isEqualTo(UPDATED_TYPE);
        assertThat(testInternalTransaction.getBookingDate()).isEqualTo(DEFAULT_BOOKING_DATE);
        assertThat(testInternalTransaction.getValueDate()).isEqualTo(DEFAULT_VALUE_DATE);
        assertThat(testInternalTransaction.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testInternalTransaction.getBalanceAfter()).isEqualTo(UPDATED_BALANCE_AFTER);
        assertThat(testInternalTransaction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testInternalTransaction.getServiceQulifier()).isEqualTo(UPDATED_SERVICE_QULIFIER);
        assertThat(testInternalTransaction.getIssuer()).isEqualTo(UPDATED_ISSUER);
        assertThat(testInternalTransaction.getRecipient()).isEqualTo(UPDATED_RECIPIENT);
    }

    @Test
    @Transactional
    void putNonExistingInternalTransaction() throws Exception {
        int databaseSizeBeforeUpdate = internalTransactionRepository.findAll().size();
        internalTransaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInternalTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, internalTransaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalTransaction in the database
        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInternalTransaction() throws Exception {
        int databaseSizeBeforeUpdate = internalTransactionRepository.findAll().size();
        internalTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalTransaction in the database
        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInternalTransaction() throws Exception {
        int databaseSizeBeforeUpdate = internalTransactionRepository.findAll().size();
        internalTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalTransactionMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InternalTransaction in the database
        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInternalTransactionWithPatch() throws Exception {
        // Initialize the database
        internalTransactionRepository.saveAndFlush(internalTransaction);

        int databaseSizeBeforeUpdate = internalTransactionRepository.findAll().size();

        // Update the internalTransaction using partial update
        InternalTransaction partialUpdatedInternalTransaction = new InternalTransaction();
        partialUpdatedInternalTransaction.setId(internalTransaction.getId());

        partialUpdatedInternalTransaction.bookingDate(UPDATED_BOOKING_DATE).value(UPDATED_VALUE).issuer(UPDATED_ISSUER);

        restInternalTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInternalTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInternalTransaction))
            )
            .andExpect(status().isOk());

        // Validate the InternalTransaction in the database
        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeUpdate);
        InternalTransaction testInternalTransaction = internalTransactionList.get(internalTransactionList.size() - 1);
        assertThat(testInternalTransaction.getTransactionType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testInternalTransaction.getBookingDate()).isEqualTo(UPDATED_BOOKING_DATE);
        assertThat(testInternalTransaction.getValueDate()).isEqualTo(DEFAULT_VALUE_DATE);
        assertThat(testInternalTransaction.getValue()).isEqualByComparingTo(UPDATED_VALUE);
        assertThat(testInternalTransaction.getBalanceAfter()).isEqualByComparingTo(DEFAULT_BALANCE_AFTER);
        assertThat(testInternalTransaction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testInternalTransaction.getServiceQulifier()).isEqualTo(DEFAULT_SERVICE_QULIFIER);
        assertThat(testInternalTransaction.getIssuer()).isEqualTo(UPDATED_ISSUER);
        assertThat(testInternalTransaction.getRecipient()).isEqualTo(DEFAULT_RECIPIENT);
    }

    @Test
    @Transactional
    void fullUpdateInternalTransactionWithPatch() throws Exception {
        // Initialize the database
        internalTransactionRepository.saveAndFlush(internalTransaction);

        int databaseSizeBeforeUpdate = internalTransactionRepository.findAll().size();

        // Update the internalTransaction using partial update
        InternalTransaction partialUpdatedInternalTransaction = new InternalTransaction();
        partialUpdatedInternalTransaction.setId(internalTransaction.getId());

        partialUpdatedInternalTransaction
            .transactionType(UPDATED_TYPE)
            .bookingDate(UPDATED_BOOKING_DATE)
            .valueDate(UPDATED_VALUE_DATE)
            .value(UPDATED_VALUE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .description(UPDATED_DESCRIPTION)
            .serviceQulifier(UPDATED_SERVICE_QULIFIER)
            .issuer(UPDATED_ISSUER)
            .recipient(UPDATED_RECIPIENT);

        restInternalTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInternalTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInternalTransaction))
            )
            .andExpect(status().isOk());

        // Validate the InternalTransaction in the database
        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeUpdate);
        InternalTransaction testInternalTransaction = internalTransactionList.get(internalTransactionList.size() - 1);
        assertThat(testInternalTransaction.getTransactionType()).isEqualTo(UPDATED_TYPE);
        assertThat(testInternalTransaction.getBookingDate()).isEqualTo(UPDATED_BOOKING_DATE);
        assertThat(testInternalTransaction.getValueDate()).isEqualTo(UPDATED_VALUE_DATE);
        assertThat(testInternalTransaction.getValue()).isEqualByComparingTo(UPDATED_VALUE);
        assertThat(testInternalTransaction.getBalanceAfter()).isEqualByComparingTo(UPDATED_BALANCE_AFTER);
        assertThat(testInternalTransaction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testInternalTransaction.getServiceQulifier()).isEqualTo(UPDATED_SERVICE_QULIFIER);
        assertThat(testInternalTransaction.getIssuer()).isEqualTo(UPDATED_ISSUER);
        assertThat(testInternalTransaction.getRecipient()).isEqualTo(UPDATED_RECIPIENT);
    }

    @Test
    @Transactional
    void patchNonExistingInternalTransaction() throws Exception {
        int databaseSizeBeforeUpdate = internalTransactionRepository.findAll().size();
        internalTransaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInternalTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, internalTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalTransaction in the database
        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInternalTransaction() throws Exception {
        int databaseSizeBeforeUpdate = internalTransactionRepository.findAll().size();
        internalTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalTransaction in the database
        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInternalTransaction() throws Exception {
        int databaseSizeBeforeUpdate = internalTransactionRepository.findAll().size();
        internalTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(internalTransaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InternalTransaction in the database
        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInternalTransaction() throws Exception {
        // Initialize the database
        internalTransactionRepository.saveAndFlush(internalTransaction);

        int databaseSizeBeforeDelete = internalTransactionRepository.findAll().size();

        // Delete the internalTransaction
        restInternalTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, internalTransaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<InternalTransaction> internalTransactionList = internalTransactionRepository.findAll();
        assertThat(internalTransactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
