package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.repository.TransactionBookRepository;
import de.farue.autocut.security.AuthoritiesConstants;
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
 * Integration tests for the {@link TransactionBookResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class TransactionBookResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final TransactionBookType DEFAULT_TYPE = TransactionBookType.CASH;
    private static final TransactionBookType UPDATED_TYPE = TransactionBookType.REVENUE;

    private static final String ENTITY_API_URL = "/api/transaction-books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionBookRepository transactionBookRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionBookMockMvc;

    private TransactionBook transactionBook;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionBook createEntity(EntityManager em) {
        TransactionBook transactionBook = new TransactionBook().name(DEFAULT_NAME).type(DEFAULT_TYPE);
        return transactionBook;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionBook createUpdatedEntity(EntityManager em) {
        TransactionBook transactionBook = new TransactionBook().name(UPDATED_NAME).type(UPDATED_TYPE);
        return transactionBook;
    }

    @BeforeEach
    public void initTest() {
        transactionBook = createEntity(em);
    }

    @Test
    @Transactional
    void createTransactionBook() throws Exception {
        int databaseSizeBeforeCreate = transactionBookRepository.findAll().size();
        // Create the TransactionBook
        restTransactionBookMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionBook))
            )
            .andExpect(status().isCreated());

        // Validate the TransactionBook in the database
        List<TransactionBook> transactionBookList = transactionBookRepository.findAll();
        assertThat(transactionBookList).hasSize(databaseSizeBeforeCreate + 1);
        TransactionBook testTransactionBook = transactionBookList.get(transactionBookList.size() - 1);
        assertThat(testTransactionBook.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTransactionBook.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void createTransactionBookWithExistingId() throws Exception {
        // Create the TransactionBook with an existing ID
        transactionBook.setId(1L);

        int databaseSizeBeforeCreate = transactionBookRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionBookMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionBook))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionBook in the database
        List<TransactionBook> transactionBookList = transactionBookRepository.findAll();
        assertThat(transactionBookList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionBookRepository.findAll().size();
        // set the field null
        transactionBook.setType(null);

        // Create the TransactionBook, which fails.

        restTransactionBookMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionBook))
            )
            .andExpect(status().isBadRequest());

        List<TransactionBook> transactionBookList = transactionBookRepository.findAll();
        assertThat(transactionBookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransactionBooks() throws Exception {
        // Initialize the database
        transactionBookRepository.saveAndFlush(transactionBook);

        // Get all the transactionBookList
        restTransactionBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionBook.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getTransactionBook() throws Exception {
        // Initialize the database
        transactionBookRepository.saveAndFlush(transactionBook);

        // Get the transactionBook
        restTransactionBookMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionBook.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactionBook.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTransactionBook() throws Exception {
        // Get the transactionBook
        restTransactionBookMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTransactionBook() throws Exception {
        // Initialize the database
        transactionBookRepository.saveAndFlush(transactionBook);

        int databaseSizeBeforeUpdate = transactionBookRepository.findAll().size();

        // Update the transactionBook
        TransactionBook updatedTransactionBook = transactionBookRepository.findById(transactionBook.getId()).get();
        // Disconnect from session so that the updates on updatedTransactionBook are not directly saved in db
        em.detach(updatedTransactionBook);
        updatedTransactionBook.name(UPDATED_NAME).type(UPDATED_TYPE);

        restTransactionBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTransactionBook.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTransactionBook))
            )
            .andExpect(status().isOk());

        // Validate the TransactionBook in the database
        List<TransactionBook> transactionBookList = transactionBookRepository.findAll();
        assertThat(transactionBookList).hasSize(databaseSizeBeforeUpdate);
        TransactionBook testTransactionBook = transactionBookList.get(transactionBookList.size() - 1);
        assertThat(testTransactionBook.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTransactionBook.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingTransactionBook() throws Exception {
        int databaseSizeBeforeUpdate = transactionBookRepository.findAll().size();
        transactionBook.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionBook.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionBook))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionBook in the database
        List<TransactionBook> transactionBookList = transactionBookRepository.findAll();
        assertThat(transactionBookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransactionBook() throws Exception {
        int databaseSizeBeforeUpdate = transactionBookRepository.findAll().size();
        transactionBook.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionBook))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionBook in the database
        List<TransactionBook> transactionBookList = transactionBookRepository.findAll();
        assertThat(transactionBookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransactionBook() throws Exception {
        int databaseSizeBeforeUpdate = transactionBookRepository.findAll().size();
        transactionBook.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionBookMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionBook))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionBook in the database
        List<TransactionBook> transactionBookList = transactionBookRepository.findAll();
        assertThat(transactionBookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransactionBookWithPatch() throws Exception {
        // Initialize the database
        transactionBookRepository.saveAndFlush(transactionBook);

        int databaseSizeBeforeUpdate = transactionBookRepository.findAll().size();

        // Update the transactionBook using partial update
        TransactionBook partialUpdatedTransactionBook = new TransactionBook();
        partialUpdatedTransactionBook.setId(transactionBook.getId());

        partialUpdatedTransactionBook.name(UPDATED_NAME);

        restTransactionBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionBook))
            )
            .andExpect(status().isOk());

        // Validate the TransactionBook in the database
        List<TransactionBook> transactionBookList = transactionBookRepository.findAll();
        assertThat(transactionBookList).hasSize(databaseSizeBeforeUpdate);
        TransactionBook testTransactionBook = transactionBookList.get(transactionBookList.size() - 1);
        assertThat(testTransactionBook.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTransactionBook.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateTransactionBookWithPatch() throws Exception {
        // Initialize the database
        transactionBookRepository.saveAndFlush(transactionBook);

        int databaseSizeBeforeUpdate = transactionBookRepository.findAll().size();

        // Update the transactionBook using partial update
        TransactionBook partialUpdatedTransactionBook = new TransactionBook();
        partialUpdatedTransactionBook.setId(transactionBook.getId());

        partialUpdatedTransactionBook.name(UPDATED_NAME).type(UPDATED_TYPE);

        restTransactionBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionBook))
            )
            .andExpect(status().isOk());

        // Validate the TransactionBook in the database
        List<TransactionBook> transactionBookList = transactionBookRepository.findAll();
        assertThat(transactionBookList).hasSize(databaseSizeBeforeUpdate);
        TransactionBook testTransactionBook = transactionBookList.get(transactionBookList.size() - 1);
        assertThat(testTransactionBook.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTransactionBook.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingTransactionBook() throws Exception {
        int databaseSizeBeforeUpdate = transactionBookRepository.findAll().size();
        transactionBook.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionBook))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionBook in the database
        List<TransactionBook> transactionBookList = transactionBookRepository.findAll();
        assertThat(transactionBookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransactionBook() throws Exception {
        int databaseSizeBeforeUpdate = transactionBookRepository.findAll().size();
        transactionBook.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionBook))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionBook in the database
        List<TransactionBook> transactionBookList = transactionBookRepository.findAll();
        assertThat(transactionBookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransactionBook() throws Exception {
        int databaseSizeBeforeUpdate = transactionBookRepository.findAll().size();
        transactionBook.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionBookMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionBook))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionBook in the database
        List<TransactionBook> transactionBookList = transactionBookRepository.findAll();
        assertThat(transactionBookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransactionBook() throws Exception {
        // Initialize the database
        transactionBookRepository.saveAndFlush(transactionBook);

        int databaseSizeBeforeDelete = transactionBookRepository.findAll().size();

        // Delete the transactionBook
        restTransactionBookMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionBook.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TransactionBook> transactionBookList = transactionBookRepository.findAll();
        assertThat(transactionBookList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
