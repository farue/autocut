package de.farue.autocut.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.Transaction_;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.domain.enumeration.TransactionKind;
import de.farue.autocut.repository.TransactionRepository;
import de.farue.autocut.service.accounting.TransactionBookService;

@SpringBootTest(classes = AutocutApp.class)
@Transactional
public class TransactionServiceIT {

    public static final String ANY_ISSUER = "issuer";

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionBookService transactionBookService;

    @Nested
    class Save {

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenMultipleTransactions {

            @Test
            @Transactional
            void shouldCalculateBalanceOfLaterTransactionsCorrectly() {
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                Transaction transactionInPast = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.CREDIT)
                    .bookingDate(todayPlusDays(-2))
                    .valueDate(todayPlusDays(-2))
                    .value(new BigDecimal("100"))
                    .balanceAfter(new BigDecimal("100"))
                    .issuer(ANY_ISSUER);
                transactionService.save(transactionInPast);

                Transaction transactionInThreeDays = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.FEE)
                    .bookingDate(todayPlusDays(3))
                    .valueDate(todayPlusDays(3))
                    .value(new BigDecimal("-8.20"))
                    .balanceAfter(new BigDecimal("91.8"))
                    .issuer(ANY_ISSUER);
                transactionService.save(transactionInThreeDays);

                Transaction transactionInTwoDays1 = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.FEE)
                    .bookingDate(todayPlusDays(2))
                    .valueDate(todayPlusDays(2))
                    .value(new BigDecimal("-1"))
                    .balanceAfter(new BigDecimal("99"))
                    .issuer(ANY_ISSUER);
                transactionService.save(transactionInTwoDays1);

                // Transaction with exactly the same booking and value date
                Transaction transactionInTwoDays2 = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.CREDIT)
                    .bookingDate(transactionInTwoDays1.getBookingDate())
                    .valueDate(transactionInTwoDays1.getValueDate())
                    .value(new BigDecimal("2.50"))
                    .balanceAfter(new BigDecimal("101.5"))
                    .issuer(ANY_ISSUER);
                transactionService.save(transactionInTwoDays2);

                Transaction transactionToday = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.FEE)
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .value(new BigDecimal("-12.55"))
                    .balanceAfter(new BigDecimal("87.45"))
                    .issuer(ANY_ISSUER);
                transactionService.save(transactionToday);

                // Transactions in reverse chronological order
                List<Transaction> transactions = transactionService
                    .findAllForTransactionBook(transactionBook, PageRequest.of(0, 5, Sort.by(Order.desc(Transaction_.VALUE_DATE), Order.desc(Transaction_.ID))))
                    .getContent();
                transactionInThreeDays = transactions.get(0);
                transactionInTwoDays2 = transactions.get(1);
                transactionInTwoDays1 = transactions.get(2);
                transactionToday = transactions.get(3);
                transactionInPast = transactions.get(4);
                assertThat(transactionInPast.getBalanceAfter()).isEqualByComparingTo("100");
                assertThat(transactionToday.getBalanceAfter()).isEqualByComparingTo("87.45");
                assertThat(transactionInTwoDays1.getBalanceAfter()).isEqualByComparingTo("86.45");
                assertThat(transactionInTwoDays2.getBalanceAfter()).isEqualByComparingTo("88.95");
                assertThat(transactionInThreeDays.getBalanceAfter()).isEqualByComparingTo("80.75");
            }
        }
    }

    @Nested
    class UpdateBalanceInLaterTransactions {

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class TransactionsWithSameValueDate {

            @Test
            @Transactional
            void shouldNotUpdateBalance() {
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                // If db precision is milliseconds, rounding occurs: #47
                Instant timestamp = Instant.parse("2020-01-01T00:00:00.567890000Z");

                Transaction transaction1 = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.CREDIT)
                    .bookingDate(timestamp)
                    .valueDate(timestamp)
                    .value(new BigDecimal("10"))
                    .balanceAfter(new BigDecimal("10"))
                    .issuer(ANY_ISSUER);
                transaction1 = transactionRepository.save(transaction1);

                Transaction transaction2 = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.FEE)
                    .bookingDate(timestamp)
                    .valueDate(timestamp)
                    .value(new BigDecimal("-1"))
                    .balanceAfter(new BigDecimal("9"))
                    .issuer(ANY_ISSUER);
                transaction2 = transactionRepository.save(transaction2);
                transactionService.updateBalanceInLaterTransactions(transaction2);

                Transaction transaction3 = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.CREDIT)
                    .bookingDate(timestamp)
                    .valueDate(timestamp)
                    .value(new BigDecimal("3"))
                    .balanceAfter(new BigDecimal("12"))
                    .issuer(ANY_ISSUER);
                transaction3 = transactionService.save(transaction3);
                transactionService.updateBalanceInLaterTransactions(transaction2);

                assertThat(transaction1.getBalanceAfter()).isEqualByComparingTo("10");
                assertThat(transaction2.getBalanceAfter()).isEqualByComparingTo("9");
                assertThat(transaction3.getBalanceAfter()).isEqualByComparingTo("12");
            }
        }
    }

    private static Instant todayPlusDays(int days) {
        return Instant.now().truncatedTo(ChronoUnit.DAYS).plus(days, ChronoUnit.DAYS);
    }
}
