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
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.domain.enumeration.TransactionKind;
import de.farue.autocut.service.accounting.TransactionBookService;

@SpringBootTest(classes = AutocutApp.class)
@Transactional
public class TransactionServiceIT {

    public static final String ANY_ISSUER = "issuer";

    @Autowired
    private TransactionService transactionService;

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

                Transaction transactionInFuture = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.FEE)
                    .bookingDate(todayPlusDays(2))
                    .valueDate(todayPlusDays(2))
                    .value(new BigDecimal("-8.20"))
                    .balanceAfter(new BigDecimal("91.8"))
                    .issuer(ANY_ISSUER);

                transactionService.save(transactionInFuture);

                Transaction transactionInBetween = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.FEE)
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .value(new BigDecimal("-12.55"))
                    .balanceAfter(new BigDecimal("87.45"))
                    .issuer(ANY_ISSUER);

                transactionService.save(transactionInBetween);

                // Transactions in reverse chronological order
                List<Transaction> transactions = transactionService.findAllForTransactionBook(transactionBook, Pageable.unpaged()).getContent();
                transactionInFuture = transactions.get(0);
                transactionInBetween = transactions.get(1);
                transactionInPast = transactions.get(2);
                assertThat(transactionInPast.getBalanceAfter()).isEqualByComparingTo("100");
                assertThat(transactionInBetween.getBalanceAfter()).isEqualByComparingTo("87.45");
                assertThat(transactionInFuture.getBalanceAfter()).isEqualByComparingTo("79.25");
            }
        }
    }

    private static Instant todayPlusDays(int days) {
        return Instant.now().truncatedTo(ChronoUnit.DAYS).plus(days, ChronoUnit.DAYS);
    }
}
