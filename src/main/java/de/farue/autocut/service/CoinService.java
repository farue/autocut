package de.farue.autocut.service;

import de.farue.autocut.domain.Coin;
import de.farue.autocut.repository.CoinRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Coin}.
 */
@Service
@Transactional
public class CoinService {

    private final Logger log = LoggerFactory.getLogger(CoinService.class);

    private final CoinRepository coinRepository;

    public CoinService(CoinRepository coinRepository) {
        this.coinRepository = coinRepository;
    }

    /**
     * Save a coin.
     *
     * @param coin the entity to save.
     * @return the persisted entity.
     */
    public Coin save(Coin coin) {
        log.debug("Request to save Coin : {}", coin);
        return coinRepository.save(coin);
    }

    /**
     * Get all the coins.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Coin> findAll() {
        log.debug("Request to get all Coins");
        return coinRepository.findAll();
    }


    /**
     * Get one coin by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Coin> findOne(Long id) {
        log.debug("Request to get Coin : {}", id);
        return coinRepository.findById(id);
    }

    /**
     * Delete the coin by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Coin : {}", id);
        coinRepository.deleteById(id);
    }
}
