package de.farue.autocut.web.rest;

import de.farue.autocut.domain.Coin;
import de.farue.autocut.service.CoinService;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link de.farue.autocut.domain.Coin}.
 */
@RestController
@RequestMapping("/api/washing")
public class CoinResource {

    private final Logger log = LoggerFactory.getLogger(CoinResource.class);

    private static final String ENTITY_NAME = "coin";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CoinService coinService;

    public CoinResource(CoinService coinService) {
        this.coinService = coinService;
    }

    @PostMapping("/coins")
    public ResponseEntity<Coin> buyCoin() throws URISyntaxException {
        log.debug("REST request to buy a Coin");
        Coin result = coinService.buyCoin();
        return ResponseEntity.created(new URI("/api/coins/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /coins} : Create a new coin.
     *
     * @param coin the coin to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new coin, or with status {@code 400 (Bad Request)} if the coin has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/coins")
    public ResponseEntity<Coin> createCoin(@Valid @RequestBody Coin coin) throws URISyntaxException {
        log.debug("REST request to save Coin : {}", coin);
        if (coin.getId() != null) {
            throw new BadRequestAlertException("A new coin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Coin result = coinService.save(coin);
        return ResponseEntity.created(new URI("/api/coins/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /coins} : Updates an existing coin.
     *
     * @param coin the coin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated coin,
     * or with status {@code 400 (Bad Request)} if the coin is not valid,
     * or with status {@code 500 (Internal Server Error)} if the coin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/coins")
    public ResponseEntity<Coin> updateCoin(@Valid @RequestBody Coin coin) throws URISyntaxException {
        log.debug("REST request to update Coin : {}", coin);
        if (coin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Coin result = coinService.save(coin);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, coin.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /coins} : get all the coins.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of coins in body.
     */
    @GetMapping("/coins")
    public List<Coin> getAllCoins() {
        log.debug("REST request to get all Coins");
        return coinService.findAll();
    }

    /**
     * {@code GET  /coins/:id} : get the "id" coin.
     *
     * @param id the id of the coin to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the coin, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/coins/{id}")
    public ResponseEntity<Coin> getCoin(@PathVariable Long id) {
        log.debug("REST request to get Coin : {}", id);
        Optional<Coin> coin = coinService.findOne(id);
        return ResponseUtil.wrapOrNotFound(coin);
    }

    /**
     * {@code DELETE  /coins/:id} : delete the "id" coin.
     *
     * @param id the id of the coin to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/coins/{id}")
    public ResponseEntity<Void> deleteCoin(@PathVariable Long id) {
        log.debug("REST request to delete Coin : {}", id);
        coinService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
