package de.farue.autocut.service.accounting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.Transaction_;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.domain.enumeration.TransactionKind;
import de.farue.autocut.service.TransactionService;

@SpringBootTest(classes = AutocutApp.class)
@Transactional
class TransactionBookServiceIT {

    @Autowired
    private TransactionBookService transactionBookService;

    @Autowired
    private TransactionService transactionService;

    @Nested
    class Save {

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenTransactionWithSameValueDateAsExistingTransaction {

            @Test
            @Transactional
            void shouldCalculateBalanceCorrectly() {
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                BookingTemplate existingBooking = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 1")
                        .issuer("test")
                        .kind(TransactionKind.CREDIT)
                        .recipient("test transaction book")
                        .value(new BigDecimal("100"))
                        .build())
                    .build();
                transactionBookService.saveBankAccountActivity(existingBooking);

                BookingTemplate newBooking = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 2")
                        .issuer("test")
                        .kind(TransactionKind.FEE)
                        .recipient("test transaction book")
                        .value(new BigDecimal("-8.20"))
                        .build())
                    .build();
                transactionBookService.saveMemberBooking(newBooking);

                assertThat(transactionBookService.getCurrentBalance(transactionBook)).isEqualByComparingTo("91.80");
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenMultipleTransactions {

            @Test
            @Transactional
            void shouldCalculateBalanceCorrectly() {
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                BookingTemplate bookingInPast = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(-2))
                    .valueDate(todayPlusDays(-2))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 1")
                        .issuer("test")
                        .kind(TransactionKind.CREDIT)
                        .recipient("test transaction book")
                        .value(new BigDecimal("100"))
                        .build())
                    .build();
                transactionBookService.saveBankAccountActivity(bookingInPast);

                BookingTemplate bookingInFuture = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(2))
                    .valueDate(todayPlusDays(2))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 2")
                        .issuer("test")
                        .kind(TransactionKind.FEE)
                        .recipient("test transaction book")
                        .value(new BigDecimal("-8.20"))
                        .build())
                    .build();
                transactionBookService.saveMemberBooking(bookingInFuture);

                BookingTemplate bookingInBetween = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 3")
                        .issuer("test")
                        .kind(TransactionKind.FEE)
                        .recipient("test transaction book")
                        .value(new BigDecimal("-12.55"))
                        .build())
                    .build();
                transactionBookService.saveMemberBooking(bookingInBetween);


                assertThat(transactionBookService.getCurrentBalance(transactionBook)).isEqualByComparingTo("87.45");

                // Transactions in reverse chronological order
                List<Transaction> allTransactions = transactionService
                    .findAllForTransactionBook(transactionBook, PageRequest.of(0, 3, Sort.by(Order.desc(Transaction_.VALUE_DATE), Order.desc(Transaction_.ID))))
                    .getContent();
                assertThat(allTransactions).hasSize(3);
                Transaction transactionInPast = allTransactions.get(2);
                Transaction transactionInBetween = allTransactions.get(1);
                Transaction transactionInFuture = allTransactions.get(0);
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
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
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
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                BookingTemplate booking = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(-2))
                    .valueDate(todayPlusDays(-2))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 1")
                        .issuer("test")
                        .kind(TransactionKind.CREDIT)
                        .recipient("test transaction book")
                        .value(new BigDecimal("100"))
                        .build())
                    .build();
                transactionBookService.saveBankAccountActivity(booking);

                assertThat(transactionBookService.getCurrentBalance(transactionBook)).isEqualByComparingTo("100");
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenTransactionWithValueDateNow {

            @Test
            @Transactional
            void shouldBalanceHaveCorrectValue() {
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                BookingTemplate booking = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 1")
                        .issuer("test")
                        .kind(TransactionKind.CREDIT)
                        .recipient("test transaction book")
                        .value(new BigDecimal("100"))
                        .build())
                    .build();
                transactionBookService.saveBankAccountActivity(booking);

                assertThat(transactionBookService.getCurrentBalance(transactionBook)).isEqualByComparingTo("100");
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenTransactionWithValueDateInFuture {

            @Test
            @Transactional
            void shouldBalanceBeZero() {
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                BookingTemplate booking = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(2))
                    .valueDate(todayPlusDays(2))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 1")
                        .issuer("test")
                        .kind(TransactionKind.CREDIT)
                        .recipient("test transaction book")
                        .value(new BigDecimal("100"))
                        .build())
                    .build();
                transactionBookService.saveBankAccountActivity(booking);

                assertThat(transactionBookService.getCurrentBalance(transactionBook)).isZero();
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class GivenMultipleTransactionBooks {

            @Test
            @Transactional
            void shouldBalanceBeDifferentForTransactionBooks() {
                TransactionBook transactionBook1 = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook1);

                BookingTemplate bookingTransactionBook1 = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(-1))
                    .valueDate(todayPlusDays(-1))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook1)
                        .description("Test transaction 1")
                        .issuer("test")
                        .kind(TransactionKind.CREDIT)
                        .recipient("test transaction book")
                        .value(new BigDecimal("100"))
                        .build())
                    .build();
                transactionBookService.saveBankAccountActivity(bookingTransactionBook1);

                TransactionBook transactionBook2 = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook2);

                BookingTemplate bookingTransactionBook2 = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(-1))
                    .valueDate(todayPlusDays(-1))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook2)
                        .description("Test transaction 1")
                        .issuer("test")
                        .kind(TransactionKind.CREDIT)
                        .recipient("test transaction book")
                        .value(new BigDecimal("50"))
                        .build())
                    .build();
                transactionBookService.saveBankAccountActivity(bookingTransactionBook2);

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
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                Instant transactionTime = LocalDateTime.of(2020, 1, 1, 10, 11, 12).toInstant(ZoneOffset.UTC);

                BookingTemplate booking1 = BookingBuilder.bookingTemplate()
                    .bookingDate(transactionTime)
                    .valueDate(transactionTime)
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 1")
                        .issuer("test")
                        .kind(TransactionKind.CREDIT)
                        .recipient("test transaction book")
                        .value(new BigDecimal("100"))
                        .build())
                    .build();
                transactionBookService.saveBankAccountActivity(booking1);

                BookingTemplate booking2 = BookingBuilder.bookingTemplate()
                    .bookingDate(transactionTime.plus(5, ChronoUnit.MINUTES))
                    .valueDate(transactionTime.plus(5, ChronoUnit.MINUTES))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 2")
                        .issuer("test")
                        .kind(TransactionKind.FEE)
                        .recipient("test transaction book")
                        .value(new BigDecimal("-8.20"))
                        .build())
                    .build();
                transactionBookService.saveMemberBooking(booking2);

                assertThat(transactionBookService.getBalanceOn(transactionBook, transactionTime.minus(2L, ChronoUnit.DAYS))).isZero();
                assertThat(transactionBookService.getBalanceOn(transactionBook, transactionTime.minus(2L, ChronoUnit.SECONDS))).isZero();
                assertThat(transactionBookService.getBalanceOn(transactionBook, transactionTime)).isEqualByComparingTo("100");
                assertThat(transactionBookService.getBalanceOn(transactionBook, transactionTime.plus(2L, ChronoUnit.SECONDS))).isEqualByComparingTo("100");
                assertThat(transactionBookService.getBalanceOn(transactionBook, transactionTime.plus(5, ChronoUnit.MINUTES))).isEqualByComparingTo("91.80");
            }
        }
    }

    @Nested
    class MemberBooking {

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class ShouldCreateCorrectContraBooking {

            @Test
            @Transactional
            void whenBookingIsFee() {
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                BookingTemplate booking = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 1")
                        .issuer("test")
                        .kind(TransactionKind.FEE)
                        .recipient("test transaction book")
                        .value(new BigDecimal("-10"))
                        .build())
                    .build();

                List<Transaction> cashTransactionsBefore = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnCashTransactionBook(), Pageable.unpaged())
                    .getContent();

                transactionBookService.saveMemberBooking(booking);

                List<Transaction> cashTransactionsAfter = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                Assertions.assertThat(cashTransactionsBefore).hasSameElementsAs(cashTransactionsAfter);

                List<Transaction> revenueTransactions = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();
                Transaction revenueTransaction = revenueTransactions.get(revenueTransactions.size() - 1);
                assertThat(revenueTransaction.getDescription()).isNull();
                assertThat(revenueTransaction.getIssuer()).isEqualTo("TransactionBookService");
                assertThat(revenueTransaction.getKind()).isEqualTo(TransactionKind.CREDIT);
                assertThat(revenueTransaction.getRecipient()).isNull();
                assertThat(revenueTransaction.getValue()).isEqualByComparingTo("10");

                List<Transaction> memberTransactions = transactionService.findAllForTransactionBook(transactionBook, Pageable.unpaged()).getContent();
                Transaction transaction = memberTransactions.get(memberTransactions.size() - 1);
                assertThat(revenueTransaction.getLefts()).contains(transaction);
                assertThat(transaction.getLefts()).contains(revenueTransaction);
            }

            @Test
            @Transactional
            void whenBookingIsDebit() {
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                BookingTemplate booking = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 1")
                        .issuer("test")
                        .kind(TransactionKind.DEBIT)
                        .recipient("test transaction book")
                        .value(new BigDecimal("-10"))
                        .build())
                    .build();

                List<Transaction> revenueTransactionsBefore = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();

                transactionBookService.saveMemberBooking(booking);

                List<Transaction> revenueTransactionsAfter = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();
                Assertions.assertThat(revenueTransactionsBefore).hasSameElementsAs(revenueTransactionsAfter);

                List<Transaction> cashTransactions = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                Transaction cashTransaction = cashTransactions.get(cashTransactions.size() - 1);
                assertThat(cashTransaction.getDescription()).isNull();
                assertThat(cashTransaction.getIssuer()).isEqualTo("TransactionBookService");
                assertThat(cashTransaction.getKind()).isEqualTo(TransactionKind.DEBIT);
                assertThat(cashTransaction.getRecipient()).isNull();
                assertThat(cashTransaction.getValue()).isEqualByComparingTo("-10");

                List<Transaction> memberTransactions = transactionService.findAllForTransactionBook(transactionBook, Pageable.unpaged()).getContent();
                Transaction transaction = memberTransactions.get(memberTransactions.size() - 1);
                assertThat(cashTransaction.getLefts()).contains(transaction);
                assertThat(transaction.getLefts()).contains(cashTransaction);
            }

            @Test
            @Transactional
            void whenBookingIsPurchase() {
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                BookingTemplate booking = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 1")
                        .issuer("test")
                        .kind(TransactionKind.PURCHASE)
                        .recipient("test transaction book")
                        .value(new BigDecimal("-10"))
                        .build())
                    .build();

                List<Transaction> cashTransactionsBefore = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnCashTransactionBook(), Pageable.unpaged())
                    .getContent();

                transactionBookService.saveMemberBooking(booking);

                List<Transaction> cashTransactionsAfter = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                Assertions.assertThat(cashTransactionsBefore).hasSameElementsAs(cashTransactionsAfter);

                List<Transaction> revenueTransactions = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();
                Transaction revenueTransaction = revenueTransactions.get(revenueTransactions.size() - 1);
                assertThat(revenueTransaction.getDescription()).isNull();
                assertThat(revenueTransaction.getIssuer()).isEqualTo("TransactionBookService");
                assertThat(revenueTransaction.getKind()).isEqualTo(TransactionKind.CREDIT);
                assertThat(revenueTransaction.getRecipient()).isNull();
                assertThat(revenueTransaction.getValue()).isEqualByComparingTo("10");

                List<Transaction> memberTransactions = transactionService.findAllForTransactionBook(transactionBook, Pageable.unpaged()).getContent();
                Transaction transaction = memberTransactions.get(memberTransactions.size() - 1);
                assertThat(revenueTransaction.getLefts()).contains(transaction);
                assertThat(transaction.getLefts()).contains(revenueTransaction);
            }

            @Test
            @Transactional
            void whenBookingIsCorrection() {
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                BookingTemplate booking = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 1")
                        .issuer("test")
                        .kind(TransactionKind.FEE)
                        .recipient("test transaction book")
                        .value(new BigDecimal("-10"))
                        .build())
                    .build();

                List<Transaction> cashTransactionsBefore = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnCashTransactionBook(), Pageable.unpaged())
                    .getContent();

                transactionBookService.saveMemberBooking(booking);

                List<Transaction> cashTransactionsAfter = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                Assertions.assertThat(cashTransactionsBefore).hasSameElementsAs(cashTransactionsAfter);

                List<Transaction> revenueTransactions = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();
                Transaction revenueTransaction = revenueTransactions.get(revenueTransactions.size() - 1);
                assertThat(revenueTransaction.getDescription()).isNull();
                assertThat(revenueTransaction.getIssuer()).isEqualTo("TransactionBookService");
                assertThat(revenueTransaction.getKind()).isEqualTo(TransactionKind.CREDIT);
                assertThat(revenueTransaction.getRecipient()).isNull();
                assertThat(revenueTransaction.getValue()).isEqualByComparingTo("10");

                List<Transaction> memberTransactions = transactionService.findAllForTransactionBook(transactionBook, Pageable.unpaged()).getContent();
                Transaction transaction = memberTransactions.get(memberTransactions.size() - 1);
                assertThat(revenueTransaction.getLefts()).contains(transaction);
                assertThat(transaction.getLefts()).contains(revenueTransaction);
            }

            @Test
            @Transactional
            void whenBookingIsTransfer() {
                TransactionBook transactionBookOrigin = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBookOrigin);

                TransactionBook transactionBookTarget = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBookTarget);

                BookingTemplate booking = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBookOrigin)
                        .description("Test transaction 1")
                        .issuer("user 1")
                        .kind(TransactionKind.TRANSFER)
                        .recipient("user 2")
                        .value(new BigDecimal("-10"))
                        .build())
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBookTarget)
                        .description("Test transaction 1")
                        .issuer("user 1")
                        .kind(TransactionKind.TRANSFER)
                        .recipient("user 2")
                        .value(new BigDecimal("10"))
                        .build())
                    .build();

                List<Transaction> cashTransactionsBefore = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                List<Transaction> revenueTransactionsBefore = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();

                transactionBookService.saveMemberBooking(booking);

                List<Transaction> cashTransactionsAfter = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                List<Transaction> revenueTransactionsAfter = transactionService
                    .findAllForTransactionBook(transactionBookService.getOwnRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();
                Assertions.assertThat(cashTransactionsBefore).hasSameElementsAs(cashTransactionsAfter);
                Assertions.assertThat(revenueTransactionsBefore).hasSameElementsAs(revenueTransactionsAfter);

                List<Transaction> originTransactions = transactionService.findAllForTransactionBook(transactionBookOrigin, Pageable.unpaged()).getContent();
                List<Transaction> targetTransactions = transactionService.findAllForTransactionBook(transactionBookTarget, Pageable.unpaged()).getContent();
                Transaction targetTransaction = targetTransactions.get(targetTransactions.size() - 1);
                Transaction originTransaction = originTransactions.get(originTransactions.size() - 1);
                assertThat(originTransaction.getLefts()).contains(targetTransaction);
                assertThat(targetTransaction.getLefts()).contains(originTransaction);
            }

            @Test
            @Transactional
            void whenBookingIsCredit() {
                TransactionBook transactionBook = new TransactionBook()
                    .type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                BookingTemplate booking = BookingBuilder.bookingTemplate()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .transactionBook(transactionBook)
                        .description("Test transaction 1")
                        .issuer("test")
                        .kind(TransactionKind.CREDIT)
                        .recipient("test transaction book")
                        .value(new BigDecimal("10"))
                        .build())
                    .build();

                assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> transactionBookService.saveMemberBooking(booking));
            }
        }
    }

    private static Instant todayPlusDays(int days) {
        return Instant.now().truncatedTo(ChronoUnit.DAYS).plus(days, ChronoUnit.DAYS);
    }

}
