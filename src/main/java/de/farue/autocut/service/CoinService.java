package de.farue.autocut.service;

import de.farue.autocut.domain.Coin;
import de.farue.autocut.domain.CoinFactory;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.enumeration.TransactionKind;
import de.farue.autocut.repository.CoinRepository;
import de.farue.autocut.repository.TenantRepository;
import de.farue.autocut.repository.UserRepository;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.utils.BigDecimalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Coin}.
 */
@Service
@Transactional
public class CoinService {

    private final Logger log = LoggerFactory.getLogger(CoinService.class);

    // TODO: property
    private static final BigDecimal COIN_PRICE = new BigDecimal("0.80");

    private final TransactionService transactionService;

    private final CoinRepository coinRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    private final CoinFactory coinFactory;

    public CoinService(TransactionService transactionService, CoinRepository coinRepository,
                       UserRepository userRepository, TenantRepository tenantRepository, CoinFactory coinFactory) {
        this.transactionService = transactionService;
        this.coinRepository = coinRepository;
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.coinFactory = coinFactory;
    }

    public Coin buyCoin() {
        log.debug("Request to buy Coin");
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(tenantRepository::findOneByUser)
            .ifPresent(tenant -> {
                BigDecimal currentBalance = transactionService.getCurrentBalance(tenant);
                if (BigDecimalUtil.isNegative(currentBalance.subtract(COIN_PRICE))) {
                    // TODO: New exception class
                    throw new RuntimeException("Not enough funds");
                }
                Instant purchaseDate = Instant.now();
                Coin coin = coinFactory.createNewCoin();
                coin.setDatePurchase(purchaseDate);
                coin.setTenant(tenant.getLease());

                Transaction transaction = new Transaction();
                transaction.setKind(TransactionKind.PURCHASE);
                transaction.setAmount(COIN_PRICE.negate());
                transaction.setBookingDate(purchaseDate);
                transaction.setValueDate(purchaseDate);
                transaction.setDescription("Washing coin: " + redactToken(coin.getToken()));
                transaction.setLease(tenant.getLease());

                transactionService.addTransactionWithBalanceCheck(transaction);
                coinRepository.save(coin);
            });
        return null;
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

    private String redactToken(String token) {
        return token.substring(0, 2) + "xxxx" + token.substring(token.length() - 1);
    }
}
