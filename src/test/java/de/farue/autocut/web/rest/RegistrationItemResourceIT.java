package de.farue.autocut.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.RegistrationItem;
import de.farue.autocut.repository.RegistrationItemRepository;
import de.farue.autocut.service.RegistrationItemService;

/**
 * Integration tests for the {@link RegistrationItemResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class RegistrationItemResourceIT {

    private static final String DEFAULT_ITEM = "AAAAAAAAAA";
    private static final String UPDATED_ITEM = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    @Autowired
    private RegistrationItemRepository registrationItemRepository;

    @Autowired
    private RegistrationItemService registrationItemService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRegistrationItemMockMvc;

    private RegistrationItem registrationItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RegistrationItem createEntity(EntityManager em) {
        RegistrationItem registrationItem = new RegistrationItem()
            .item(DEFAULT_ITEM)
            .contentType(DEFAULT_CONTENT_TYPE)
            .content(DEFAULT_CONTENT);
        return registrationItem;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RegistrationItem createUpdatedEntity(EntityManager em) {
        RegistrationItem registrationItem = new RegistrationItem()
            .item(UPDATED_ITEM)
            .contentType(UPDATED_CONTENT_TYPE)
            .content(UPDATED_CONTENT);
        return registrationItem;
    }

    @BeforeEach
    public void initTest() {
        registrationItem = createEntity(em);
    }

    @Test
    @Transactional
    public void createRegistrationItem() throws Exception {
        int databaseSizeBeforeCreate = registrationItemRepository.findAll().size();
        // Create the RegistrationItem
        restRegistrationItemMockMvc.perform(post("/api/registration-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(registrationItem)))
            .andExpect(status().isCreated());

        // Validate the RegistrationItem in the database
        List<RegistrationItem> registrationItemList = registrationItemRepository.findAll();
        assertThat(registrationItemList).hasSize(databaseSizeBeforeCreate + 1);
        RegistrationItem testRegistrationItem = registrationItemList.get(registrationItemList.size() - 1);
        assertThat(testRegistrationItem.getItem()).isEqualTo(DEFAULT_ITEM);
        assertThat(testRegistrationItem.getContentType()).isEqualTo(DEFAULT_CONTENT_TYPE);
        assertThat(testRegistrationItem.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    public void createRegistrationItemWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = registrationItemRepository.findAll().size();

        // Create the RegistrationItem with an existing ID
        registrationItem.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRegistrationItemMockMvc.perform(post("/api/registration-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(registrationItem)))
            .andExpect(status().isBadRequest());

        // Validate the RegistrationItem in the database
        List<RegistrationItem> registrationItemList = registrationItemRepository.findAll();
        assertThat(registrationItemList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkItemIsRequired() throws Exception {
        int databaseSizeBeforeTest = registrationItemRepository.findAll().size();
        // set the field null
        registrationItem.setItem(null);

        // Create the RegistrationItem, which fails.


        restRegistrationItemMockMvc.perform(post("/api/registration-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(registrationItem)))
            .andExpect(status().isBadRequest());

        List<RegistrationItem> registrationItemList = registrationItemRepository.findAll();
        assertThat(registrationItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkContentTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = registrationItemRepository.findAll().size();
        // set the field null
        registrationItem.setContentType(null);

        // Create the RegistrationItem, which fails.


        restRegistrationItemMockMvc.perform(post("/api/registration-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(registrationItem)))
            .andExpect(status().isBadRequest());

        List<RegistrationItem> registrationItemList = registrationItemRepository.findAll();
        assertThat(registrationItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = registrationItemRepository.findAll().size();
        // set the field null
        registrationItem.setContent(null);

        // Create the RegistrationItem, which fails.


        restRegistrationItemMockMvc.perform(post("/api/registration-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(registrationItem)))
            .andExpect(status().isBadRequest());

        List<RegistrationItem> registrationItemList = registrationItemRepository.findAll();
        assertThat(registrationItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRegistrationItems() throws Exception {
        // Initialize the database
        registrationItemRepository.saveAndFlush(registrationItem);

        // Get all the registrationItemList
        restRegistrationItemMockMvc.perform(get("/api/registration-items?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(registrationItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].item").value(hasItem(DEFAULT_ITEM)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));
    }

    @Test
    @Transactional
    public void getRegistrationItem() throws Exception {
        // Initialize the database
        registrationItemRepository.saveAndFlush(registrationItem);

        // Get the registrationItem
        restRegistrationItemMockMvc.perform(get("/api/registration-items/{id}", registrationItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(registrationItem.getId().intValue()))
            .andExpect(jsonPath("$.item").value(DEFAULT_ITEM))
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT));
    }
    @Test
    @Transactional
    public void getNonExistingRegistrationItem() throws Exception {
        // Get the registrationItem
        restRegistrationItemMockMvc.perform(get("/api/registration-items/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRegistrationItem() throws Exception {
        // Initialize the database
        registrationItemService.save(registrationItem);

        int databaseSizeBeforeUpdate = registrationItemRepository.findAll().size();

        // Update the registrationItem
        RegistrationItem updatedRegistrationItem = registrationItemRepository.findById(registrationItem.getId()).get();
        // Disconnect from session so that the updates on updatedRegistrationItem are not directly saved in db
        em.detach(updatedRegistrationItem);
        updatedRegistrationItem
            .item(UPDATED_ITEM)
            .contentType(UPDATED_CONTENT_TYPE)
            .content(UPDATED_CONTENT);

        restRegistrationItemMockMvc.perform(put("/api/registration-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedRegistrationItem)))
            .andExpect(status().isOk());

        // Validate the RegistrationItem in the database
        List<RegistrationItem> registrationItemList = registrationItemRepository.findAll();
        assertThat(registrationItemList).hasSize(databaseSizeBeforeUpdate);
        RegistrationItem testRegistrationItem = registrationItemList.get(registrationItemList.size() - 1);
        assertThat(testRegistrationItem.getItem()).isEqualTo(UPDATED_ITEM);
        assertThat(testRegistrationItem.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
        assertThat(testRegistrationItem.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void updateNonExistingRegistrationItem() throws Exception {
        int databaseSizeBeforeUpdate = registrationItemRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRegistrationItemMockMvc.perform(put("/api/registration-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(registrationItem)))
            .andExpect(status().isBadRequest());

        // Validate the RegistrationItem in the database
        List<RegistrationItem> registrationItemList = registrationItemRepository.findAll();
        assertThat(registrationItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRegistrationItem() throws Exception {
        // Initialize the database
        registrationItemService.save(registrationItem);

        int databaseSizeBeforeDelete = registrationItemRepository.findAll().size();

        // Delete the registrationItem
        restRegistrationItemMockMvc.perform(delete("/api/registration-items/{id}", registrationItem.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RegistrationItem> registrationItemList = registrationItemRepository.findAll();
        assertThat(registrationItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
