package de.farue.autocut.service.accounting;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.domain.enumeration.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = AutocutApp.class)
@Transactional
class TransactionBookServiceIT {

    @Autowired
    private TransactionBookService transactionBookService;

    @Autowired
    private InternalTransactionService transactionService;

    @Nested
    class GetCurrentBalance {

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenNoTransactions {

            @Test
            @Transactional
            void shouldBalanceBeZero() {
                TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                assertThat(transactionBookService.getCurrentBalance(transactionBook)).isZero();
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenTransactionWithValueDateInThePast {

            @Test
            @Transactional
            void shouldBalanceHaveCorrectValue() {
                TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                BookingTemplate booking = BookingBuilder
                    .bookingTemplate()
                    .bookingDate(todayPlusDays(-2))
                    .valueDate(todayPlusDays(-2))
                    .transactionTemplate(
                        BookingBuilder
                            .transactionTemplate()
                            .transactionBook(transactionBook)
                            .description("Test transaction 1")
                            .issuer("test")
                            .type(TransactionType.CORRECTION)
                            .recipient("test transaction book")
                            .value(new BigDecimal("100"))
                            .build()
                    )
                    .build();
                transactionService.saveWithContraTransaction(booking);

                assertThat(transactionBookService.getCurrentBalance(transactionBook)).isEqualByComparingTo("100");
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenTransactionWithValueDateNow {

            @Test
            @Transactional
            void shouldBalanceHaveCorrectValue() {
                TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                BookingTemplate booking = BookingBuilder
                    .bookingTemplate()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionTemplate(
                        BookingBuilder
                            .transactionTemplate()
                            .transactionBook(transactionBook)
                            .description("Test transaction 1")
                            .issuer("test")
                            .type(TransactionType.CORRECTION)
                            .recipient("test transaction book")
                            .value(new BigDecimal("100"))
                            .build()
                    )
                    .build();
                transactionService.saveWithContraTransaction(booking);

                assertThat(transactionBookService.getCurrentBalance(transactionBook)).isEqualByComparingTo("100");
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenTransactionWithValueDateInFuture {

            @Test
            @Transactional
            void shouldBalanceBeZero() {
                TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                BookingTemplate booking = BookingBuilder
                    .bookingTemplate()
                    .bookingDate(todayPlusDays(2))
                    .valueDate(todayPlusDays(2))
                    .transactionTemplate(
                        BookingBuilder
                            .transactionTemplate()
                            .transactionBook(transactionBook)
                            .description("Test transaction 1")
                            .issuer("test")
                            .type(TransactionType.CORRECTION)
                            .recipient("test transaction book")
                            .value(new BigDecimal("100"))
                            .build()
                    )
                    .build();
                transactionService.saveWithContraTransaction(booking);

                assertThat(transactionBookService.getCurrentBalance(transactionBook)).isZero();
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenMultipleTransactionBooks {

            @Test
            @Transactional
            void shouldBalanceBeDifferentForTransactionBooks() {
                TransactionBook transactionBook1 = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook1);

                BookingTemplate bookingTransactionBook1 = BookingBuilder
                    .bookingTemplate()
                    .bookingDate(todayPlusDays(-1))
                    .valueDate(todayPlusDays(-1))
                    .transactionTemplate(
                        BookingBuilder
                            .transactionTemplate()
                            .transactionBook(transactionBook1)
                            .description("Test transaction 1")
                            .issuer("test")
                            .type(TransactionType.CORRECTION)
                            .recipient("test transaction book")
                            .value(new BigDecimal("100"))
                            .build()
                    )
                    .build();
                transactionService.saveWithContraTransaction(bookingTransactionBook1);

                TransactionBook transactionBook2 = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook2);

                BookingTemplate bookingTransactionBook2 = BookingBuilder
                    .bookingTemplate()
                    .bookingDate(todayPlusDays(-1))
                    .valueDate(todayPlusDays(-1))
                    .transactionTemplate(
                        BookingBuilder
                            .transactionTemplate()
                            .transactionBook(transactionBook2)
                            .description("Test transaction 1")
                            .issuer("test")
                            .type(TransactionType.CORRECTION)
                            .recipient("test transaction book")
                            .value(new BigDecimal("50"))
                            .build()
                    )
                    .build();
                transactionService.saveWithContraTransaction(bookingTransactionBook2);

                assertThat(transactionBookService.getCurrentBalance(transactionBook1)).isEqualByComparingTo("100");
                assertThat(transactionBookService.getCurrentBalance(transactionBook2)).isEqualByComparingTo("50");
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
                TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                Instant transactionTime = LocalDateTime.of(2020, 1, 1, 10, 11, 12).toInstant(ZoneOffset.UTC);

                BookingTemplate booking1 = BookingBuilder
                    .bookingTemplate()
                    .bookingDate(transactionTime)
                    .valueDate(transactionTime)
                    .transactionTemplate(
                        BookingBuilder
                            .transactionTemplate()
                            .transactionBook(transactionBook)
                            .description("Test transaction 1")
                            .issuer("test")
                            .type(TransactionType.CORRECTION)
                            .recipient("test transaction book")
                            .value(new BigDecimal("100"))
                            .build()
                    )
                    .build();
                transactionService.saveWithContraTransaction(booking1);

                BookingTemplate booking2 = BookingBuilder
                    .bookingTemplate()
                    .bookingDate(transactionTime.plus(5, ChronoUnit.MINUTES))
                    .valueDate(transactionTime.plus(5, ChronoUnit.MINUTES))
                    .transactionTemplate(
                        BookingBuilder
                            .transactionTemplate()
                            .transactionBook(transactionBook)
                            .description("Test transaction 2")
                            .issuer("test")
                            .type(TransactionType.FEE)
                            .recipient("test transaction book")
                            .value(new BigDecimal("-8.20"))
                            .build()
                    )
                    .build();
                transactionService.saveWithContraTransaction(booking2);

                assertThat(transactionBookService.getBalanceOn(transactionBook, transactionTime.minus(2L, ChronoUnit.DAYS))).isZero();
                assertThat(transactionBookService.getBalanceOn(transactionBook, transactionTime.minus(2L, ChronoUnit.SECONDS))).isZero();
                assertThat(transactionBookService.getBalanceOn(transactionBook, transactionTime)).isEqualByComparingTo("100");
                assertThat(transactionBookService.getBalanceOn(transactionBook, transactionTime.plus(2L, ChronoUnit.SECONDS)))
                    .isEqualByComparingTo("100");
                assertThat(transactionBookService.getBalanceOn(transactionBook, transactionTime.plus(5, ChronoUnit.MINUTES)))
                    .isEqualByComparingTo("91.80");
            }
        }
    }

    private static Instant todayPlusDays(int days) {
        return Instant.now().truncatedTo(ChronoUnit.DAYS).plus(days, ChronoUnit.DAYS);
    }
}
