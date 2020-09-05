package de.farue.autocut.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.enumeration.TransactionKind;
import de.farue.autocut.repository.LeaseRepository;
import de.farue.autocut.repository.TransactionRepository;

@SpringBootTest(classes = AutocutApp.class)
@Transactional
public class TransactionServiceIT {

    public static final String ANY_ISSUER = "issuer";
    public static final String ANY_LEASE_NR = "leaseNr";
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LeaseRepository leaseRepository;

    private Lease lease;

    @BeforeEach
    void setUp() {
        lease = new Lease()
            .nr(ANY_LEASE_NR)
            .start(todayPlusDays(-5));

        leaseRepository.saveAndFlush(lease);
    }

    @Nested
    class Save {

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenTransactionWithSameValueDateAsExistingTransaction {

            @Test
            @Transactional
            void shouldCalculateBalanceCorrectly() {
                Transaction existingTransaction = new Transaction()
                    .kind(TransactionKind.CREDIT)
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .value(new BigDecimal("100"))
                    .issuer(ANY_ISSUER)
                    .lease(lease);

                transactionService.save(existingTransaction);

                Transaction newTransaction = new Transaction()
                    .kind(TransactionKind.FEE)
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .value(new BigDecimal("-8.20"))
                    .issuer(ANY_ISSUER)
                    .lease(lease);

                transactionService.save(newTransaction);

                assertThat(newTransaction.getBalanceAfter()).isEqualByComparingTo("91.80");
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenMultipleTransactions {

            @Test
            @Transactional
            void shouldCalculateBalanceCorrectly() {
                Transaction transactionInPast = new Transaction()
                    .kind(TransactionKind.CREDIT)
                    .bookingDate(todayPlusDays(-2))
                    .valueDate(todayPlusDays(-2))
                    .value(new BigDecimal("100"))
                    .issuer(ANY_ISSUER)
                    .lease(lease);

                transactionService.save(transactionInPast);

                Transaction transactionInFuture = new Transaction()
                    .kind(TransactionKind.FEE)
                    .bookingDate(todayPlusDays(2))
                    .valueDate(todayPlusDays(2))
                    .value(new BigDecimal("-8.20"))
                    .issuer(ANY_ISSUER)
                    .lease(lease);

                transactionService.save(transactionInFuture);

                Transaction transactionInBetween = new Transaction()
                    .kind(TransactionKind.FEE)
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .value(new BigDecimal("-12.55"))
                    .issuer(ANY_ISSUER)
                    .lease(lease);

                transactionService.save(transactionInBetween);

                // Transactions in reverse chronological order
                List<Transaction> allTransactions = transactionService.findAll(lease, Pageable.unpaged()).getContent();
                assertThat(allTransactions).hasSize(3);
                transactionInPast = allTransactions.get(2);
                transactionInBetween = allTransactions.get(1);
                transactionInFuture = allTransactions.get(0);
                assertThat(transactionInPast.getBalanceAfter()).isEqualByComparingTo("100");
                assertThat(transactionInBetween.getBalanceAfter()).isEqualByComparingTo("87.45");
                assertThat(transactionInFuture.getBalanceAfter()).isEqualByComparingTo("79.25");
            }
        }
    }

    @Nested
    class GetCurrentBalance {

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenNoTransactions {

            @Test
            @Transactional
            void shouldBalanceBeZero() {
                assertThat(transactionService.getCurrentBalance(lease)).isZero();
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenTransactionWithValueDateInThePast {

            @Test
            @Transactional
            void shouldBalanceHaveCorrectValue() {
                Transaction transactionInPast = new Transaction()
                    .kind(TransactionKind.CREDIT)
                    .bookingDate(todayPlusDays(-2))
                    .valueDate(todayPlusDays(-2))
                    .value(new BigDecimal("100"))
                    .issuer(ANY_ISSUER)
                    .lease(lease);

                transactionService.save(transactionInPast);

                assertThat(transactionService.getCurrentBalance(lease)).isEqualByComparingTo("100");
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenTransactionWithValueDateNow {

            @Test
            @Transactional
            void shouldBalanceHaveCorrectValue() {
                Transaction transactionNow = new Transaction()
                    .kind(TransactionKind.CREDIT)
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .value(new BigDecimal("100"))
                    .issuer(ANY_ISSUER)
                    .lease(lease);

                transactionService.save(transactionNow);

                assertThat(transactionService.getCurrentBalance(lease)).isEqualByComparingTo("100");
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenTransactionWithValueDateInFuture {

            @Test
            @Transactional
            void shouldBalanceBeZero() {
                Transaction transactionNow = new Transaction()
                    .kind(TransactionKind.CREDIT)
                    .bookingDate(todayPlusDays(2))
                    .valueDate(todayPlusDays(2))
                    .value(new BigDecimal("100"))
                    .issuer(ANY_ISSUER)
                    .lease(lease);

                transactionService.save(transactionNow);

                assertThat(transactionService.getCurrentBalance(lease)).isZero();
            }
        }
    }

    @Nested
    class GetBalanceOn {

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenTransactionsWithFixedValueDate {

            @Test
            @Transactional
            void shouldDetermineCorrectBalanceOnDate() {
                Instant transactionTime = LocalDateTime.of(2020, 1, 1, 10, 11, 12).toInstant(ZoneOffset.UTC);

                Transaction t1 = new Transaction()
                    .kind(TransactionKind.CREDIT)
                    .bookingDate(transactionTime)
                    .valueDate(transactionTime)
                    .value(new BigDecimal("100"))
                    .issuer(ANY_ISSUER)
                    .lease(lease);

                Transaction t2 = new Transaction()
                    .kind(TransactionKind.FEE)
                    .bookingDate(transactionTime.plus(5, ChronoUnit.MINUTES))
                    .valueDate(transactionTime.plus(5, ChronoUnit.MINUTES))
                    .value(new BigDecimal("-8.20"))
                    .issuer(ANY_ISSUER)
                    .lease(lease);

                transactionService.save(t1);
                transactionService.save(t2);

                assertThat(transactionService.getBalanceOn(lease, transactionTime.minus(2L, ChronoUnit.DAYS))).isZero();
                assertThat(transactionService.getBalanceOn(lease, transactionTime.minus(2L, ChronoUnit.SECONDS))).isZero();
                assertThat(transactionService.getBalanceOn(lease, transactionTime)).isEqualByComparingTo("100");
                assertThat(transactionService.getBalanceOn(lease, transactionTime.plus(2L, ChronoUnit.SECONDS))).isEqualByComparingTo("100");
                assertThat(transactionService.getBalanceOn(lease, transactionTime.plus(5, ChronoUnit.MINUTES))).isEqualByComparingTo("91.80");
            }
        }
    }

    private static Instant todayPlusDays(int days) {
        return Instant.now().truncatedTo(ChronoUnit.DAYS).plus(days, ChronoUnit.DAYS);
    }
}
