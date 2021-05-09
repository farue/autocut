package de.farue.autocut.batch.fee;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Activity;
import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.Transaction_;
import de.farue.autocut.domain.enumeration.SemesterTerms;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.service.ActivityService;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.TenantService;
import de.farue.autocut.service.accounting.InternalTransactionService;
import de.farue.autocut.service.accounting.TransactionBookService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(classes = AutocutApp.class)
class TenantFeeBatchIT {

    private static final YearMonth CHARGE_PERIOD = YearMonth.of(2020, 6);
    private static final String TENANT_FIRST_NAME = "bob";
    private static final String TENANT_LAST_NAME = "miller";

    @Autowired
    private LeaseService leaseService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private TransactionBookService transactionBookService;

    @Autowired
    private InternalTransactionService transactionService;

    @Autowired
    private TenantFeeBatchScheduler batchScheduler;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private TransactionBook transactionBook;
    private Lease lease;
    private Tenant tenant;

    @BeforeEach
    void setUp() {
        Lease lease = new Lease().start(LocalDate.of(2020, 4, 10)).end(LocalDate.of(2020, 9, 30)).nr("nr");
        this.lease = leaseService.save(lease);

        Tenant tenant = new Tenant().firstName(TENANT_FIRST_NAME).lastName(TENANT_LAST_NAME).lease(lease).verified(true);
        this.tenant = tenantService.save(tenant);

        this.transactionBook = leaseService.getCashTransactionBook(lease);

        InternalTransaction transactionBeforeCharges = new InternalTransaction()
            .bookingDate(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant())
            .valueDate(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant())
            .transactionBook(transactionBook)
            .transactionType(TransactionType.CREDIT)
            .issuer("test")
            .value(new BigDecimal("50"))
            .balanceAfter(new BigDecimal("50"));
        transactionService.save(transactionBeforeCharges);

        InternalTransaction transactionAfterCharges = new InternalTransaction()
            .bookingDate(LocalDate.of(2099, 9, 1).atStartOfDay(ZoneId.systemDefault()).toInstant())
            .valueDate(LocalDate.of(2099, 9, 1).atStartOfDay(ZoneId.systemDefault()).toInstant())
            .transactionBook(transactionBook)
            .transactionType(TransactionType.DEBIT)
            .issuer("test")
            .value(new BigDecimal("-7.5"))
            .balanceAfter(new BigDecimal("42.5"));
        transactionService.save(transactionAfterCharges);
    }

    @AfterEach
    void tearDown() {
        transactionTemplate.execute(
            txInfo -> {
                TransactionBook transactionBook = transactionBookService.findOne(this.transactionBook.getId()).get();
                Lease lease = leaseService.findOne(this.lease.getId()).get();
                Tenant tenant = tenantService.findOne(this.tenant.getId()).get();

                List<InternalTransaction> createdTransactions = transactionService.findAllForTransactionBookWithLinks(transactionBook);
                Set<Transaction> linkedTransactions = new HashSet<>();
                createdTransactions.forEach(
                    transaction -> {
                        Set<Transaction> linked = transaction.getLefts();
                        linkedTransactions.addAll(linked);

                        transactionService.delete(transaction.getId());
                    }
                );
                linkedTransactions.forEach(transaction -> transactionService.delete(transaction.getId()));

                lease.removeTransactionBook(transactionBook);
                transactionBookService.delete(transactionBook.getId());
                tenantService.delete(tenant.getId());
                leaseService.delete(lease.getId());
                return null;
            }
        );
    }

    @Nested
    class ShouldChargeAccount {

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class NoPreviousCharges {

            @Test
            void testDefaultCase() throws Exception {
                batchScheduler.setChargePeriod(CHARGE_PERIOD);
                batchScheduler.launchJob();

                List<InternalTransaction> transactions = transactionService
                    .findAllForTransactionBook(
                        transactionBook,
                        PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.asc(Transaction_.VALUE_DATE), Order.asc(Transaction_.ID)))
                    )
                    .getContent();

                Transaction transactionBeforeCharges = transactions.get(0);
                assertThat(transactionBeforeCharges.getValue()).isEqualByComparingTo("50");
                assertThat(transactionBeforeCharges.getBalanceAfter()).isEqualByComparingTo("50");

                Transaction chargeApr = transactions.get(1);
                assertThat(chargeApr.getValue()).isEqualByComparingTo("-5");
                assertThat(chargeApr.getBalanceAfter()).isEqualByComparingTo("45");
                assertThat(chargeApr.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 4/2020");
                assertThat(chargeApr.getBookingDate()).isBefore(chargeApr.getValueDate());
                assertThat(chargeApr.getServiceQulifier()).isEqualTo("2020-04-10;false");

                Transaction chargeMay = transactions.get(2);
                assertThat(chargeMay.getValue()).isEqualByComparingTo("-5");
                assertThat(chargeMay.getBalanceAfter()).isEqualByComparingTo("40");
                assertThat(chargeMay.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 5/2020");
                assertThat(chargeMay.getBookingDate()).isBefore(chargeApr.getValueDate());
                assertThat(chargeMay.getServiceQulifier()).isEqualTo("2020-05-10;false");

                Transaction chargeJun = transactions.get(3);
                assertThat(chargeJun.getValue()).isEqualByComparingTo("-5");
                assertThat(chargeJun.getBalanceAfter()).isEqualByComparingTo("35");
                assertThat(chargeJun.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 6/2020");
                assertThat(chargeJun.getBookingDate()).isBefore(chargeApr.getValueDate());
                assertThat(chargeJun.getServiceQulifier()).isEqualTo("2020-06-10;false");

                Transaction transactionAfterCharges = transactions.get(4);
                assertThat(transactionAfterCharges.getValue()).isEqualByComparingTo("-7.5");
                assertThat(transactionAfterCharges.getBalanceAfter()).isEqualByComparingTo("27.5");
            }

            @Test
            void testNegativeBalance() throws Exception {
                InternalTransaction transactionNegativeBalance = new InternalTransaction()
                    .bookingDate(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .valueDate(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.PURCHASE)
                    .issuer("test")
                    .value(new BigDecimal("-70"))
                    .balanceAfter(new BigDecimal("-20"));
                transactionService.save(transactionNegativeBalance);

                batchScheduler.setChargePeriod(CHARGE_PERIOD);
                batchScheduler.launchJob();

                List<InternalTransaction> transactions = transactionService
                    .findAllForTransactionBook(
                        transactionBook,
                        PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.asc(Transaction_.VALUE_DATE), Order.asc(Transaction_.ID)))
                    )
                    .getContent();

                Transaction transactionBeforeCharges = transactions.get(0);
                assertThat(transactionBeforeCharges.getValue()).isEqualByComparingTo("50");
                assertThat(transactionBeforeCharges.getBalanceAfter()).isEqualByComparingTo("50");

                Transaction transactionNegativeBalanceLoaded = transactions.get(1);
                assertThat(transactionNegativeBalanceLoaded.getValue()).isEqualByComparingTo("-70");
                assertThat(transactionNegativeBalanceLoaded.getBalanceAfter()).isEqualByComparingTo("-20");

                Transaction chargeApr = transactions.get(2);
                assertThat(chargeApr.getValue()).isEqualByComparingTo("-5");
                assertThat(chargeApr.getBalanceAfter()).isEqualByComparingTo("-25");
                assertThat(chargeApr.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 4/2020");
                assertThat(chargeApr.getBookingDate()).isBefore(chargeApr.getValueDate());
                assertThat(chargeApr.getServiceQulifier()).isEqualTo("2020-04-10;false");

                Transaction chargeMay = transactions.get(3);
                assertThat(chargeMay.getValue()).isEqualByComparingTo("-5");
                assertThat(chargeMay.getBalanceAfter()).isEqualByComparingTo("-30");
                assertThat(chargeMay.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 5/2020");
                assertThat(chargeMay.getBookingDate()).isBefore(chargeApr.getValueDate());
                assertThat(chargeMay.getServiceQulifier()).isEqualTo("2020-05-10;false");

                Transaction chargeJun = transactions.get(4);
                assertThat(chargeJun.getValue()).isEqualByComparingTo("-5");
                assertThat(chargeJun.getBalanceAfter()).isEqualByComparingTo("-35");
                assertThat(chargeJun.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 6/2020");
                assertThat(chargeJun.getBookingDate()).isBefore(chargeApr.getValueDate());
                assertThat(chargeJun.getServiceQulifier()).isEqualTo("2020-06-10;false");

                Transaction transactionAfterCharges = transactions.get(5);
                assertThat(transactionAfterCharges.getValue()).isEqualByComparingTo("-7.5");
                assertThat(transactionAfterCharges.getBalanceAfter()).isEqualByComparingTo("-42.5");
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class PreviousCharges {

            private Activity activity;

            @BeforeEach
            void setUp() {
                // we ignore for a moment that the lease had not started by this time
                Activity activity = new Activity().year(2019).term(SemesterTerms.WINTER_TERM).tenant(tenant);
                this.activity = activityService.save(activity);
            }

            @AfterEach
            void tearDown() {
                activityService.delete(activity.getId());
            }

            @Test
            void testNoChangeInActivity() throws Exception {
                InternalTransaction chargeApr = new InternalTransaction()
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.FEE)
                    .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                    .bookingDate(LocalDate.of(2020, 4, 10).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .valueDate(LocalDate.of(2020, 4, 20).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .value(new BigDecimal("-2.5"))
                    .balanceAfter(new BigDecimal("47.5"))
                    .description("i18n{transaction.descriptions.tenantFee} 4/2020")
                    .serviceQulifier("2020-04-10;true");
                transactionService.save(chargeApr);

                InternalTransaction chargeMay = new InternalTransaction()
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.FEE)
                    .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                    .bookingDate(LocalDate.of(2020, 5, 10).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .valueDate(LocalDate.of(2020, 5, 20).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .value(new BigDecimal("-2.5"))
                    .balanceAfter(new BigDecimal("45"))
                    .description("i18n{transaction.descriptions.tenantFee} 5/2020")
                    .serviceQulifier("2020-05-10;true");
                transactionService.save(chargeMay);

                Activity activity = activityService.findOne(this.activity.getId()).get();
                activity.setDiscount(true);
                activityService.save(activity);

                batchScheduler.setChargePeriod(CHARGE_PERIOD);
                batchScheduler.launchJob();

                List<InternalTransaction> transactions = transactionService
                    .findAllForTransactionBook(
                        transactionBook,
                        PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.asc(Transaction_.VALUE_DATE), Order.asc(Transaction_.ID)))
                    )
                    .getContent();

                Transaction transactionBeforeCharges = transactions.get(0);
                assertThat(transactionBeforeCharges.getValue()).isEqualByComparingTo("50");
                assertThat(transactionBeforeCharges.getBalanceAfter()).isEqualByComparingTo("50");

                chargeApr = transactions.get(1);
                assertThat(chargeApr.getValue()).isEqualByComparingTo("-2.5");
                assertThat(chargeApr.getBalanceAfter()).isEqualByComparingTo("47.5");
                assertThat(chargeApr.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 4/2020");
                assertThat(chargeApr.getBookingDate()).isBefore(chargeApr.getValueDate());
                assertThat(chargeApr.getServiceQulifier()).isEqualTo("2020-04-10;true");

                chargeMay = transactions.get(2);
                assertThat(chargeMay.getValue()).isEqualByComparingTo("-2.5");
                assertThat(chargeMay.getBalanceAfter()).isEqualByComparingTo("45");
                assertThat(chargeMay.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 5/2020");
                assertThat(chargeMay.getBookingDate()).isBefore(chargeMay.getValueDate());
                assertThat(chargeMay.getServiceQulifier()).isEqualTo("2020-05-10;true");

                Transaction chargeJun = transactions.get(3);
                assertThat(chargeJun.getValue()).isEqualByComparingTo("-2.5");
                assertThat(chargeJun.getBalanceAfter()).isEqualByComparingTo("42.5");
                assertThat(chargeJun.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 6/2020");
                assertThat(chargeJun.getBookingDate()).isBefore(chargeJun.getValueDate());
                assertThat(chargeJun.getServiceQulifier()).isEqualTo("2020-06-10;true");

                Transaction transactionAfterCharges = transactions.get(4);
                assertThat(transactionAfterCharges.getValue()).isEqualByComparingTo("-7.5");
                assertThat(transactionAfterCharges.getBalanceAfter()).isEqualByComparingTo("35");
            }

            @Test
            void testChangesInActivity() throws Exception {
                InternalTransaction chargeApr = new InternalTransaction()
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.FEE)
                    .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                    .bookingDate(LocalDate.of(2020, 4, 10).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .valueDate(LocalDate.of(2020, 4, 20).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .value(new BigDecimal("-2.5"))
                    .balanceAfter(new BigDecimal("47.5"))
                    .description("i18n{transaction.descriptions.tenantFee} 4/2020")
                    .serviceQulifier("2020-04-10;true");
                transactionService.save(chargeApr);

                InternalTransaction chargeMay1 = new InternalTransaction()
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.FEE)
                    .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                    .bookingDate(LocalDate.of(2020, 5, 10).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .valueDate(LocalDate.of(2020, 5, 20).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .value(new BigDecimal("-3"))
                    .balanceAfter(new BigDecimal("44.5"))
                    .description("i18n{transaction.descriptions.tenantFee} 5/2020")
                    .serviceQulifier("2020-05-10;false");
                transactionService.save(chargeMay1);

                InternalTransaction chargeMay2 = new InternalTransaction()
                    .transactionBook(transactionBook)
                    .transactionType(TransactionType.FEE)
                    .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                    .bookingDate(LocalDate.of(2020, 5, 11).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .valueDate(LocalDate.of(2020, 5, 21).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .value(new BigDecimal("-5"))
                    .balanceAfter(new BigDecimal("39.5"))
                    .description("i18n{transaction.descriptions.tenantFee} 5/2020")
                    .serviceQulifier("2020-05-10;false");
                transactionService.save(chargeMay2);

                Activity activity = activityService.findOne(this.activity.getId()).get();
                activity.setDiscount(false);
                activityService.save(activity);

                batchScheduler.setChargePeriod(CHARGE_PERIOD);
                batchScheduler.launchJob();

                List<InternalTransaction> transactions = transactionService
                    .findAllForTransactionBook(
                        transactionBook,
                        PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.asc(Transaction_.VALUE_DATE), Order.asc(Transaction_.ID)))
                    )
                    .getContent();

                InternalTransaction transactionBeforeCharges = transactions.get(0);
                assertThat(transactionBeforeCharges.getValue()).isEqualByComparingTo("50");
                assertThat(transactionBeforeCharges.getBalanceAfter()).isEqualByComparingTo("50");

                InternalTransaction originalChargeApr = transactions.get(1);
                assertThat(originalChargeApr.getValue()).isEqualByComparingTo("-2.5");
                assertThat(originalChargeApr.getBalanceAfter()).isEqualByComparingTo("47.5");

                InternalTransaction originalChargeMay1 = transactions.get(2);
                assertThat(originalChargeMay1.getValue()).isEqualByComparingTo("-3");
                assertThat(originalChargeMay1.getBalanceAfter()).isEqualByComparingTo("44.5");

                InternalTransaction originalChargeMay2 = transactions.get(3);
                assertThat(originalChargeMay2.getValue()).isEqualByComparingTo("-5");
                assertThat(originalChargeMay2.getBalanceAfter()).isEqualByComparingTo("39.5");

                InternalTransaction chargeMayCorrection = transactions.get(4);
                assertThat(chargeMayCorrection.getTransactionType()).isEqualTo(TransactionType.CORRECTION);
                assertThat(chargeMayCorrection.getValue()).isEqualByComparingTo("3");
                assertThat(chargeMayCorrection.getBalanceAfter()).isEqualByComparingTo("42.5");
                assertThat(chargeMayCorrection.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 5/2020");
                assertThat(chargeMayCorrection.getBookingDate()).isEqualTo(chargeMayCorrection.getValueDate()); // credit immediately
                assertThat(chargeMayCorrection.getServiceQulifier()).isEqualTo("2020-05-10;false");

                InternalTransaction chargeAprCorrection = transactions.get(5);
                assertThat(chargeMayCorrection.getTransactionType()).isEqualTo(TransactionType.CORRECTION);
                assertThat(chargeAprCorrection.getValue()).isEqualByComparingTo("-2.5");
                assertThat(chargeAprCorrection.getBalanceAfter()).isEqualByComparingTo("40");
                assertThat(chargeAprCorrection.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 4/2020");
                assertThat(chargeAprCorrection.getBookingDate()).isBefore(chargeAprCorrection.getValueDate()); // additional charge delayed
                assertThat(chargeAprCorrection.getServiceQulifier()).isEqualTo("2020-04-10;false");

                InternalTransaction chargeJun = transactions.get(6);
                assertThat(chargeJun.getValue()).isEqualByComparingTo("-5");
                assertThat(chargeJun.getBalanceAfter()).isEqualByComparingTo("35");
                assertThat(chargeJun.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 6/2020");
                assertThat(chargeJun.getBookingDate()).isBefore(chargeJun.getValueDate());
                assertThat(chargeJun.getServiceQulifier()).isEqualTo("2020-06-10;false");

                InternalTransaction transactionAfterCharges = transactions.get(7);
                assertThat(transactionAfterCharges.getValue()).isEqualByComparingTo("-7.5");
                assertThat(transactionAfterCharges.getBalanceAfter()).isEqualByComparingTo("27.5");
            }
        }
    }

    @Nested
    @SpringBootTest(classes = AutocutApp.class)
    class ShouldNotChargeAccount {

        @Test
        void testLeaseStartAfterChargePeriod() throws Exception {
            Lease lease = leaseService.findOne(TenantFeeBatchIT.this.lease.getId()).get();
            lease.setStart(LocalDate.of(2020, 7, 1));
            leaseService.save(lease);

            batchScheduler.setChargePeriod(CHARGE_PERIOD);
            batchScheduler.launchJob();

            assertThat(transactionService.findAllForTransactionBook(transactionBook, Pageable.unpaged())).hasSize(2);
        }

        @Test
        void testLeaseEndBeforeChargePeriod() throws Exception {
            InternalTransaction chargeApr = new InternalTransaction()
                .transactionBook(transactionBook)
                .transactionType(TransactionType.FEE)
                .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                .bookingDate(LocalDate.of(2020, 4, 10).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .valueDate(LocalDate.of(2020, 4, 20).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .value(new BigDecimal("-2.5"))
                .balanceAfter(new BigDecimal("47.5"))
                .description("i18n{transaction.descriptions.tenantFee} 4/2020")
                .serviceQulifier("2020-04-10;true");
            transactionService.save(chargeApr);

            InternalTransaction chargeMay1 = new InternalTransaction()
                .transactionBook(transactionBook)
                .transactionType(TransactionType.FEE)
                .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                .bookingDate(LocalDate.of(2020, 5, 10).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .valueDate(LocalDate.of(2020, 5, 20).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .value(new BigDecimal("-3"))
                .balanceAfter(new BigDecimal("44.5"))
                .description("i18n{transaction.descriptions.tenantFee} 5/2020")
                .serviceQulifier("2020-05-10;false");
            transactionService.save(chargeMay1);

            Lease lease = leaseService.findOne(TenantFeeBatchIT.this.lease.getId()).get();
            lease.setEnd(LocalDate.of(2020, 6, 1));
            leaseService.save(lease);

            batchScheduler.setChargePeriod(CHARGE_PERIOD);
            batchScheduler.launchJob();

            List<InternalTransaction> transactions = transactionService
                .findAllForTransactionBook(
                    transactionBook,
                    PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.asc(Transaction_.VALUE_DATE), Order.asc(Transaction_.ID)))
                )
                .getContent();

            InternalTransaction transactionBeforeCharges = transactions.get(0);
            assertThat(transactionBeforeCharges.getValue()).isEqualByComparingTo("50");
            assertThat(transactionBeforeCharges.getBalanceAfter()).isEqualByComparingTo("50");

            InternalTransaction originalChargeApr = transactions.get(1);
            assertThat(originalChargeApr.getValue()).isEqualByComparingTo("-2.5");
            assertThat(originalChargeApr.getBalanceAfter()).isEqualByComparingTo("47.5");

            InternalTransaction originalChargeMay = transactions.get(2);
            assertThat(originalChargeMay.getValue()).isEqualByComparingTo("-3");
            assertThat(originalChargeMay.getBalanceAfter()).isEqualByComparingTo("44.5");

            // Corrections should still be booked even if Lease has expired
            InternalTransaction chargeAprCorrection = transactions.get(3);
            assertThat(chargeAprCorrection.getTransactionType()).isEqualTo(TransactionType.CORRECTION);
            assertThat(chargeAprCorrection.getValue()).isEqualByComparingTo("-2.5");
            assertThat(chargeAprCorrection.getBalanceAfter()).isEqualByComparingTo("42");
            assertThat(chargeAprCorrection.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 4/2020");
            assertThat(chargeAprCorrection.getBookingDate()).isBefore(chargeAprCorrection.getValueDate());
            assertThat(chargeAprCorrection.getServiceQulifier()).isEqualTo("2020-04-10;false");

            InternalTransaction chargeMayCorrection = transactions.get(4);
            assertThat(chargeMayCorrection.getTransactionType()).isEqualTo(TransactionType.CORRECTION);
            assertThat(chargeMayCorrection.getValue()).isEqualByComparingTo("-2");
            assertThat(chargeMayCorrection.getBalanceAfter()).isEqualByComparingTo("40");
            assertThat(chargeMayCorrection.getDescription()).isEqualTo("i18n{transaction.descriptions.tenantFee} 5/2020");
            assertThat(chargeMayCorrection.getBookingDate()).isBefore(chargeMayCorrection.getValueDate());
            assertThat(chargeMayCorrection.getServiceQulifier()).isEqualTo("2020-05-10;false");

            InternalTransaction transactionAfterCharges = transactions.get(5);
            assertThat(transactionAfterCharges.getValue()).isEqualByComparingTo("-7.5");
            assertThat(transactionAfterCharges.getBalanceAfter()).isEqualByComparingTo("32.5");
        }

        @Test
        void testTenantNotVerified() throws Exception {
            Tenant tenant = tenantService.findOne(TenantFeeBatchIT.this.tenant.getId()).get();
            tenant.setVerified(false);
            tenantService.save(tenant);

            batchScheduler.setChargePeriod(CHARGE_PERIOD);
            batchScheduler.launchJob();

            assertThat(transactionService.findAllForTransactionBook(transactionBook, Pageable.unpaged())).hasSize(2);
        }
    }
}
