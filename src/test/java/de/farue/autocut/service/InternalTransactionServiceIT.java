package de.farue.autocut.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.Transaction_;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.repository.InternalTransactionRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import de.farue.autocut.service.accounting.BookingBuilder;
import de.farue.autocut.service.accounting.BookingTemplate;
import de.farue.autocut.service.accounting.InternalTransactionService;
import de.farue.autocut.service.accounting.TransactionBookService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@IntegrationTest
@WithMockUser(authorities = { AuthoritiesConstants.ADMIN, AuthoritiesConstants.VIEW_TRANSACTIONS, AuthoritiesConstants.EDIT_TRANSACTIONS })
public class InternalTransactionServiceIT {

    public static final String ANY_ISSUER = "issuer";

    @Autowired
    private InternalTransactionService transactionService;

    @Autowired
    private InternalTransactionRepository transactionRepository;

    @Autowired
    private TransactionBookService transactionBookService;

    @Autowired
    private AssociationService associationService;

    @Nested
    class Save {

        @Nested
        class GivenMultipleTransactions {

            @Nested
            class GivenTransactionWithSameValueDateAsExistingTransaction {

                @Test
                @Transactional
                void shouldCalculateBalanceCorrectly() {
                    TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.CASH);
                    transactionBookService.save(transactionBook);

                    BookingTemplate existingBooking = BookingBuilder
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
                    transactionService.saveWithContraTransaction(existingBooking);

                    BookingTemplate newBooking = BookingBuilder
                        .bookingTemplate()
                        .bookingDate(todayPlusDays(0))
                        .valueDate(todayPlusDays(0))
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
                    transactionService.saveWithContraTransaction(newBooking);

                    assertThat(transactionBookService.getCurrentBalance(transactionBook)).isEqualByComparingTo("91.80");
                }
            }

            @Test
            @Transactional
            void shouldCalculateBalanceOfLaterTransactionsCorrectly() {
                TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                InternalTransaction transactionInPast = new InternalTransaction()
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.CREDIT)
                    .bookingDate(todayPlusDays(-2))
                    .valueDate(todayPlusDays(-2))
                    .value(new BigDecimal("100"))
                    .balanceAfter(new BigDecimal("100"))
                    .issuer(ANY_ISSUER);
                transactionService.save(transactionInPast);

                InternalTransaction transactionInThreeDays = new InternalTransaction()
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.FEE)
                    .bookingDate(todayPlusDays(3))
                    .valueDate(todayPlusDays(3))
                    .value(new BigDecimal("-8.20"))
                    .balanceAfter(new BigDecimal("91.8"))
                    .issuer(ANY_ISSUER);
                transactionService.save(transactionInThreeDays);

                InternalTransaction transactionInTwoDays1 = new InternalTransaction()
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.FEE)
                    .bookingDate(todayPlusDays(2))
                    .valueDate(todayPlusDays(2))
                    .value(new BigDecimal("-1"))
                    .balanceAfter(new BigDecimal("99"))
                    .issuer(ANY_ISSUER);
                transactionService.save(transactionInTwoDays1);

                // Transaction with exactly the same booking and value date
                InternalTransaction transactionInTwoDays2 = new InternalTransaction()
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.CREDIT)
                    .bookingDate(transactionInTwoDays1.getBookingDate())
                    .valueDate(transactionInTwoDays1.getValueDate())
                    .value(new BigDecimal("2.50"))
                    .balanceAfter(new BigDecimal("101.5"))
                    .issuer(ANY_ISSUER);
                transactionService.save(transactionInTwoDays2);

                InternalTransaction transactionToday = new InternalTransaction()
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.FEE)
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .value(new BigDecimal("-12.55"))
                    .balanceAfter(new BigDecimal("87.45"))
                    .issuer(ANY_ISSUER);
                transactionService.save(transactionToday);

                // Transactions in reverse chronological order
                List<InternalTransaction> transactions = transactionService
                    .findAllForTransactionBook(
                        transactionBook,
                        PageRequest.of(0, 5, Sort.by(Order.desc(Transaction_.VALUE_DATE), Order.desc(Transaction_.ID)))
                    )
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
    class SaveBooking {

        @Nested
        class ShouldCreateCorrectContraBooking {

            @Test
            @Transactional
            void whenBookingIsFee() {
                TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                InternalTransaction initialTransaction = new InternalTransaction()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionBook(transactionBook)
                    .description("Initial transaction")
                    .issuer("test")
                    .transactionType(TransactionType.CREDIT)
                    .recipient("test transaction book")
                    .value(new BigDecimal("100"))
                    .balanceAfter(new BigDecimal("100"));
                transactionService.save(initialTransaction);

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
                            .type(TransactionType.FEE)
                            .recipient("test transaction book")
                            .value(new BigDecimal("-10"))
                            .build()
                    )
                    .build();

                List<InternalTransaction> cashTransactionsBefore = transactionService
                    .findAllForTransactionBook(associationService.getCashTransactionBook(), Pageable.unpaged())
                    .getContent();

                transactionService.saveWithContraTransaction(booking);

                List<InternalTransaction> cashTransactionsAfter = transactionService
                    .findAllForTransactionBook(associationService.getCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                Assertions.assertThat(cashTransactionsBefore).hasSameElementsAs(cashTransactionsAfter);

                List<InternalTransaction> revenueTransactions = transactionService
                    .findAllForTransactionBook(associationService.getRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();
                InternalTransaction revenueTransaction = revenueTransactions.get(revenueTransactions.size() - 1);
                assertThat(revenueTransaction.getDescription()).isNull();
                assertThat(revenueTransaction.getIssuer()).isEqualTo("TransactionBookService");
                assertThat(revenueTransaction.getTransactionType()).isEqualTo(TransactionType.CREDIT);
                assertThat(revenueTransaction.getRecipient()).isNull();
                assertThat(revenueTransaction.getValue()).isEqualByComparingTo("10");

                List<InternalTransaction> memberTransactions = transactionService
                    .findAllForTransactionBook(transactionBook, Pageable.unpaged())
                    .getContent();
                Transaction transaction = memberTransactions.get(memberTransactions.size() - 1);
                assertThat(revenueTransaction.getLefts()).contains(transaction);
                assertThat(transaction.getLefts()).contains(revenueTransaction);
            }

            @Test
            @Transactional
            void whenBookingIsDebit() {
                TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                InternalTransaction initialTransaction = new InternalTransaction()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionBook(transactionBook)
                    .description("Initial transaction")
                    .issuer("test")
                    .transactionType(TransactionType.CREDIT)
                    .recipient("test transaction book")
                    .value(new BigDecimal("100"))
                    .balanceAfter(new BigDecimal("100"));
                transactionService.save(initialTransaction);

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
                            .type(TransactionType.DEBIT)
                            .recipient("test transaction book")
                            .value(new BigDecimal("-10"))
                            .build()
                    )
                    .build();

                List<InternalTransaction> revenueTransactionsBefore = transactionService
                    .findAllForTransactionBook(associationService.getRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();

                transactionService.saveWithContraTransaction(booking);

                List<InternalTransaction> revenueTransactionsAfter = transactionService
                    .findAllForTransactionBook(associationService.getRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();
                Assertions.assertThat(revenueTransactionsBefore).hasSameElementsAs(revenueTransactionsAfter);

                List<InternalTransaction> cashTransactions = transactionService
                    .findAllForTransactionBook(associationService.getCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                InternalTransaction cashTransaction = cashTransactions.get(cashTransactions.size() - 1);
                assertThat(cashTransaction.getDescription()).isNull();
                assertThat(cashTransaction.getIssuer()).isEqualTo("TransactionBookService");
                assertThat(cashTransaction.getTransactionType()).isEqualTo(TransactionType.DEBIT);
                assertThat(cashTransaction.getRecipient()).isNull();
                assertThat(cashTransaction.getValue()).isEqualByComparingTo("-10");

                List<InternalTransaction> memberTransactions = transactionService
                    .findAllForTransactionBook(transactionBook, Pageable.unpaged())
                    .getContent();
                Transaction transaction = memberTransactions.get(memberTransactions.size() - 1);
                assertThat(cashTransaction.getLefts()).contains(transaction);
                assertThat(transaction.getLefts()).contains(cashTransaction);
            }

            @Test
            @Transactional
            void whenBookingIsPurchase() {
                TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                InternalTransaction initialTransaction = new InternalTransaction()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionBook(transactionBook)
                    .description("Initial transaction")
                    .issuer("test")
                    .transactionType(TransactionType.CREDIT)
                    .recipient("test transaction book")
                    .value(new BigDecimal("100"))
                    .balanceAfter(new BigDecimal("100"));
                transactionService.save(initialTransaction);

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
                            .type(TransactionType.PURCHASE)
                            .recipient("test transaction book")
                            .value(new BigDecimal("-10"))
                            .build()
                    )
                    .build();

                List<InternalTransaction> cashTransactionsBefore = transactionService
                    .findAllForTransactionBook(associationService.getCashTransactionBook(), Pageable.unpaged())
                    .getContent();

                transactionService.saveWithContraTransaction(booking);

                List<InternalTransaction> cashTransactionsAfter = transactionService
                    .findAllForTransactionBook(associationService.getCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                Assertions.assertThat(cashTransactionsBefore).hasSameElementsAs(cashTransactionsAfter);

                List<InternalTransaction> revenueTransactions = transactionService
                    .findAllForTransactionBook(associationService.getRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();
                InternalTransaction revenueTransaction = revenueTransactions.get(revenueTransactions.size() - 1);
                assertThat(revenueTransaction.getDescription()).isNull();
                assertThat(revenueTransaction.getIssuer()).isEqualTo("TransactionBookService");
                assertThat(revenueTransaction.getTransactionType()).isEqualTo(TransactionType.CREDIT);
                assertThat(revenueTransaction.getRecipient()).isNull();
                assertThat(revenueTransaction.getValue()).isEqualByComparingTo("10");

                List<InternalTransaction> memberTransactions = transactionService
                    .findAllForTransactionBook(transactionBook, Pageable.unpaged())
                    .getContent();
                Transaction transaction = memberTransactions.get(memberTransactions.size() - 1);
                assertThat(revenueTransaction.getLefts()).contains(transaction);
                assertThat(transaction.getLefts()).contains(revenueTransaction);
            }

            @Test
            @Transactional
            void whenBookingIsCorrection() {
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
                            .value(new BigDecimal("10"))
                            .build()
                    )
                    .build();

                List<InternalTransaction> cashTransactionsBefore = transactionService
                    .findAllForTransactionBook(associationService.getCashTransactionBook(), Pageable.unpaged())
                    .getContent();

                transactionService.saveWithContraTransaction(booking);

                List<InternalTransaction> cashTransactionsAfter = transactionService
                    .findAllForTransactionBook(associationService.getCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                Assertions.assertThat(cashTransactionsBefore).hasSameElementsAs(cashTransactionsAfter);

                List<InternalTransaction> revenueTransactions = transactionService
                    .findAllForTransactionBook(associationService.getRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();
                InternalTransaction revenueTransaction = revenueTransactions.get(revenueTransactions.size() - 1);
                assertThat(revenueTransaction.getDescription()).isNull();
                assertThat(revenueTransaction.getIssuer()).isEqualTo("TransactionBookService");
                assertThat(revenueTransaction.getTransactionType()).isEqualTo(TransactionType.CORRECTION);
                assertThat(revenueTransaction.getRecipient()).isNull();
                assertThat(revenueTransaction.getValue()).isEqualByComparingTo("-10");

                List<InternalTransaction> memberTransactions = transactionService
                    .findAllForTransactionBook(transactionBook, Pageable.unpaged())
                    .getContent();
                Transaction transaction = memberTransactions.get(memberTransactions.size() - 1);
                assertThat(revenueTransaction.getLefts()).contains(transaction);
                assertThat(transaction.getLefts()).contains(revenueTransaction);
            }

            @Test
            @Transactional
            void whenBookingIsTransfer() {
                TransactionBook transactionBookOrigin = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBookOrigin);

                TransactionBook transactionBookTarget = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBookTarget);

                InternalTransaction initialTransaction = new InternalTransaction()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionBook(transactionBookOrigin)
                    .description("Initial transaction")
                    .issuer("test")
                    .transactionType(TransactionType.CREDIT)
                    .recipient("test transaction book")
                    .value(new BigDecimal("100"))
                    .balanceAfter(new BigDecimal("100"));
                transactionService.save(initialTransaction);

                BookingTemplate booking = BookingBuilder
                    .bookingTemplate()
                    .bookingDate(todayPlusDays(0))
                    .valueDate(todayPlusDays(0))
                    .transactionTemplate(
                        BookingBuilder
                            .transactionTemplate()
                            .transactionBook(transactionBookOrigin)
                            .description("Test transaction 1")
                            .issuer("user 1")
                            .type(TransactionType.TRANSFER)
                            .recipient("user 2")
                            .value(new BigDecimal("-10"))
                            .build()
                    )
                    .transactionTemplate(
                        BookingBuilder
                            .transactionTemplate()
                            .transactionBook(transactionBookTarget)
                            .description("Test transaction 1")
                            .issuer("user 1")
                            .type(TransactionType.TRANSFER)
                            .recipient("user 2")
                            .value(new BigDecimal("10"))
                            .build()
                    )
                    .build();

                List<InternalTransaction> cashTransactionsBefore = transactionService
                    .findAllForTransactionBook(associationService.getCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                List<InternalTransaction> revenueTransactionsBefore = transactionService
                    .findAllForTransactionBook(associationService.getRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();

                transactionService.saveWithContraTransaction(booking);

                List<InternalTransaction> cashTransactionsAfter = transactionService
                    .findAllForTransactionBook(associationService.getCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                List<InternalTransaction> revenueTransactionsAfter = transactionService
                    .findAllForTransactionBook(associationService.getRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();
                Assertions.assertThat(cashTransactionsBefore).hasSameElementsAs(cashTransactionsAfter);
                Assertions.assertThat(revenueTransactionsBefore).hasSameElementsAs(revenueTransactionsAfter);

                List<InternalTransaction> originTransactions = transactionService
                    .findAllForTransactionBook(transactionBookOrigin, Pageable.unpaged())
                    .getContent();
                List<InternalTransaction> targetTransactions = transactionService
                    .findAllForTransactionBook(transactionBookTarget, Pageable.unpaged())
                    .getContent();
                Transaction targetTransaction = targetTransactions.get(targetTransactions.size() - 1);
                Transaction originTransaction = originTransactions.get(originTransactions.size() - 1);
                assertThat(originTransaction.getLefts()).contains(targetTransaction);
                assertThat(targetTransaction.getLefts()).contains(originTransaction);
            }

            @Test
            @Transactional
            void whenBookingIsCredit() {
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
                            .type(TransactionType.CREDIT)
                            .recipient("test transaction book")
                            .value(new BigDecimal("10"))
                            .build()
                    )
                    .build();

                List<InternalTransaction> cashTransactionsBefore = transactionService
                    .findAllForTransactionBook(associationService.getCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                List<InternalTransaction> revenueTransactionsBefore = transactionService
                    .findAllForTransactionBook(associationService.getRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();

                transactionService.saveWithContraTransaction(booking);

                List<InternalTransaction> cashTransactionsAfter = transactionService
                    .findAllForTransactionBook(associationService.getCashTransactionBook(), Pageable.unpaged())
                    .getContent();
                Assertions.assertThat(cashTransactionsBefore).hasSameElementsAs(cashTransactionsAfter);

                List<InternalTransaction> revenueTransactions = transactionService
                    .findAllForTransactionBook(associationService.getRevenueTransactionBook(), Pageable.unpaged())
                    .getContent();
                InternalTransaction revenueTransaction = revenueTransactions.get(revenueTransactions.size() - 1);
                assertThat(revenueTransaction.getDescription()).isNull();
                assertThat(revenueTransaction.getIssuer()).isEqualTo("TransactionBookService");
                assertThat(revenueTransaction.getTransactionType()).isEqualTo(TransactionType.DEBIT);
                assertThat(revenueTransaction.getRecipient()).isNull();
                assertThat(revenueTransaction.getValue()).isEqualByComparingTo("-10");

                List<InternalTransaction> memberTransactions = transactionService
                    .findAllForTransactionBook(transactionBook, Pageable.unpaged())
                    .getContent();
                Transaction transaction = memberTransactions.get(memberTransactions.size() - 1);
                assertThat(transaction.getValue()).isEqualByComparingTo("10");
                assertThat(revenueTransaction.getLefts()).contains(transaction);
                assertThat(transaction.getLefts()).contains(revenueTransaction);
            }
        }
    }

    @Nested
    class UpdateBalanceInLaterTransactions {

        @Nested
        class TransactionsWithSameValueDate {

            @Test
            @Transactional
            void shouldNotUpdateBalance() {
                TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.CASH);
                transactionBookService.save(transactionBook);

                // If db precision is milliseconds, rounding occurs: #47
                Instant timestamp = Instant.parse("2020-01-01T00:00:00.567890000Z");

                InternalTransaction transaction1 = new InternalTransaction()
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.CREDIT)
                    .bookingDate(timestamp)
                    .valueDate(timestamp)
                    .value(new BigDecimal("10"))
                    .balanceAfter(new BigDecimal("10"))
                    .issuer(ANY_ISSUER);
                transaction1 = transactionRepository.save(transaction1);

                InternalTransaction transaction2 = new InternalTransaction()
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.FEE)
                    .bookingDate(timestamp)
                    .valueDate(timestamp)
                    .value(new BigDecimal("-1"))
                    .balanceAfter(new BigDecimal("9"))
                    .issuer(ANY_ISSUER);
                transaction2 = transactionRepository.save(transaction2);
                transactionService.updateBalanceInLaterTransactions(transaction2);

                InternalTransaction transaction3 = new InternalTransaction()
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.CREDIT)
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

    @Nested
    class Delete {

        @Test
        @Transactional
        void shouldCalculateBalanceOfLaterTransactionsCorrectly() {
            TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.CASH);
            transactionBookService.save(transactionBook);

            InternalTransaction transactionInPast = new InternalTransaction()
                .transactionBook(transactionBook)
                .transactionType(TransactionType.CREDIT)
                .bookingDate(todayPlusDays(-2))
                .valueDate(todayPlusDays(-2))
                .value(new BigDecimal("100"))
                .balanceAfter(new BigDecimal("100"))
                .issuer(ANY_ISSUER);
            transactionRepository.save(transactionInPast);

            InternalTransaction transactionToday = new InternalTransaction()
                .transactionBook(transactionBook)
                .transactionType(TransactionType.FEE)
                .bookingDate(todayPlusDays(0))
                .valueDate(todayPlusDays(0))
                .value(new BigDecimal("-12.55"))
                .balanceAfter(new BigDecimal("87.45"))
                .issuer(ANY_ISSUER);
            transactionRepository.save(transactionToday);

            InternalTransaction transactionInTwoDays1 = new InternalTransaction()
                .transactionBook(transactionBook)
                .transactionType(TransactionType.FEE)
                .bookingDate(todayPlusDays(2))
                .valueDate(todayPlusDays(2))
                .value(new BigDecimal("-1"))
                .balanceAfter(new BigDecimal("86.45"))
                .issuer(ANY_ISSUER);
            transactionRepository.save(transactionInTwoDays1);

            // Transaction with exactly the same booking and value date
            InternalTransaction transactionInTwoDays2 = new InternalTransaction()
                .transactionBook(transactionBook)
                .transactionType(TransactionType.CREDIT)
                .bookingDate(transactionInTwoDays1.getBookingDate())
                .valueDate(transactionInTwoDays1.getValueDate())
                .value(new BigDecimal("2.50"))
                .balanceAfter(new BigDecimal("88.95"))
                .issuer(ANY_ISSUER);
            transactionRepository.save(transactionInTwoDays2);

            InternalTransaction transactionInThreeDays = new InternalTransaction()
                .transactionBook(transactionBook)
                .transactionType(TransactionType.FEE)
                .bookingDate(todayPlusDays(3))
                .valueDate(todayPlusDays(3))
                .value(new BigDecimal("-8.20"))
                .balanceAfter(new BigDecimal("80.75"))
                .issuer(ANY_ISSUER);
            transactionRepository.save(transactionInThreeDays);

            transactionService.delete(transactionInTwoDays1.getId());

            // Transactions in reverse chronological order
            List<InternalTransaction> transactions = transactionService
                .findAllForTransactionBook(
                    transactionBook,
                    PageRequest.of(0, 5, Sort.by(Order.desc(Transaction_.VALUE_DATE), Order.desc(Transaction_.ID)))
                )
                .getContent();
            transactionInThreeDays = transactions.get(0);
            transactionInTwoDays2 = transactions.get(1);
            transactionToday = transactions.get(2);
            transactionInPast = transactions.get(3);
            assertThat(transactionInPast.getBalanceAfter()).isEqualByComparingTo("100");
            assertThat(transactionToday.getBalanceAfter()).isEqualByComparingTo("87.45");
            assertThat(transactionInTwoDays2.getBalanceAfter()).isEqualByComparingTo("89.95");
            assertThat(transactionInThreeDays.getBalanceAfter()).isEqualByComparingTo("81.75");
        }
    }

    private static Instant todayPlusDays(int days) {
        return Instant.now().truncatedTo(ChronoUnit.DAYS).plus(days, ChronoUnit.DAYS);
    }
}
