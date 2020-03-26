package de.farue.autocut.web.rest;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Coin;
import de.farue.autocut.repository.CoinRepository;
import de.farue.autocut.service.CoinService;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static de.farue.autocut.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CoinResource} REST controller.
 */
@SpringBootTest(classes = AutocutApp.class)
public class CoinResourceIT {

    private static final String DEFAULT_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_PURCHASE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_PURCHASE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_REDEEM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_REDEEM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private CoinRepository coinRepository;

    @Autowired
    private CoinService coinService;

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

    private MockMvc restCoinMockMvc;

    private Coin coin;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CoinResource coinResource = new CoinResource(coinService);
        this.restCoinMockMvc = MockMvcBuilders.standaloneSetup(coinResource)
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
    public static Coin createEntity(EntityManager em) {
        Coin coin = new Coin()
            .token(DEFAULT_TOKEN)
            .datePurchase(DEFAULT_DATE_PURCHASE)
            .dateRedeem(DEFAULT_DATE_REDEEM);
        return coin;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coin createUpdatedEntity(EntityManager em) {
        Coin coin = new Coin()
            .token(UPDATED_TOKEN)
            .datePurchase(UPDATED_DATE_PURCHASE)
            .dateRedeem(UPDATED_DATE_REDEEM);
        return coin;
    }

    @BeforeEach
    public void initTest() {
        coin = createEntity(em);
    }

    @Test
    @Transactional
    public void createCoin() throws Exception {
        int databaseSizeBeforeCreate = coinRepository.findAll().size();

        // Create the Coin
        restCoinMockMvc.perform(post("/api/coins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(coin)))
            .andExpect(status().isCreated());

        // Validate the Coin in the database
        List<Coin> coinList = coinRepository.findAll();
        assertThat(coinList).hasSize(databaseSizeBeforeCreate + 1);
        Coin testCoin = coinList.get(coinList.size() - 1);
        assertThat(testCoin.getToken()).isEqualTo(DEFAULT_TOKEN);
        assertThat(testCoin.getDatePurchase()).isEqualTo(DEFAULT_DATE_PURCHASE);
        assertThat(testCoin.getDateRedeem()).isEqualTo(DEFAULT_DATE_REDEEM);
    }

    @Test
    @Transactional
    public void createCoinWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = coinRepository.findAll().size();

        // Create the Coin with an existing ID
        coin.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCoinMockMvc.perform(post("/api/coins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(coin)))
            .andExpect(status().isBadRequest());

        // Validate the Coin in the database
        List<Coin> coinList = coinRepository.findAll();
        assertThat(coinList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkTokenIsRequired() throws Exception {
        int databaseSizeBeforeTest = coinRepository.findAll().size();
        // set the field null
        coin.setToken(null);

        // Create the Coin, which fails.

        restCoinMockMvc.perform(post("/api/coins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(coin)))
            .andExpect(status().isBadRequest());

        List<Coin> coinList = coinRepository.findAll();
        assertThat(coinList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCoins() throws Exception {
        // Initialize the database
        coinRepository.saveAndFlush(coin);

        // Get all the coinList
        restCoinMockMvc.perform(get("/api/coins?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coin.getId().intValue())))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)))
            .andExpect(jsonPath("$.[*].datePurchase").value(hasItem(DEFAULT_DATE_PURCHASE.toString())))
            .andExpect(jsonPath("$.[*].dateRedeem").value(hasItem(DEFAULT_DATE_REDEEM.toString())));
    }
    
    @Test
    @Transactional
    public void getCoin() throws Exception {
        // Initialize the database
        coinRepository.saveAndFlush(coin);

        // Get the coin
        restCoinMockMvc.perform(get("/api/coins/{id}", coin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(coin.getId().intValue()))
            .andExpect(jsonPath("$.token").value(DEFAULT_TOKEN))
            .andExpect(jsonPath("$.datePurchase").value(DEFAULT_DATE_PURCHASE.toString()))
            .andExpect(jsonPath("$.dateRedeem").value(DEFAULT_DATE_REDEEM.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCoin() throws Exception {
        // Get the coin
        restCoinMockMvc.perform(get("/api/coins/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCoin() throws Exception {
        // Initialize the database
        coinService.save(coin);

        int databaseSizeBeforeUpdate = coinRepository.findAll().size();

        // Update the coin
        Coin updatedCoin = coinRepository.findById(coin.getId()).get();
        // Disconnect from session so that the updates on updatedCoin are not directly saved in db
        em.detach(updatedCoin);
        updatedCoin
            .token(UPDATED_TOKEN)
            .datePurchase(UPDATED_DATE_PURCHASE)
            .dateRedeem(UPDATED_DATE_REDEEM);

        restCoinMockMvc.perform(put("/api/coins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCoin)))
            .andExpect(status().isOk());

        // Validate the Coin in the database
        List<Coin> coinList = coinRepository.findAll();
        assertThat(coinList).hasSize(databaseSizeBeforeUpdate);
        Coin testCoin = coinList.get(coinList.size() - 1);
        assertThat(testCoin.getToken()).isEqualTo(UPDATED_TOKEN);
        assertThat(testCoin.getDatePurchase()).isEqualTo(UPDATED_DATE_PURCHASE);
        assertThat(testCoin.getDateRedeem()).isEqualTo(UPDATED_DATE_REDEEM);
    }

    @Test
    @Transactional
    public void updateNonExistingCoin() throws Exception {
        int databaseSizeBeforeUpdate = coinRepository.findAll().size();

        // Create the Coin

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCoinMockMvc.perform(put("/api/coins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(coin)))
            .andExpect(status().isBadRequest());

        // Validate the Coin in the database
        List<Coin> coinList = coinRepository.findAll();
        assertThat(coinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCoin() throws Exception {
        // Initialize the database
        coinService.save(coin);

        int databaseSizeBeforeDelete = coinRepository.findAll().size();

        // Delete the coin
        restCoinMockMvc.perform(delete("/api/coins/{id}", coin.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Coin> coinList = coinRepository.findAll();
        assertThat(coinList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
