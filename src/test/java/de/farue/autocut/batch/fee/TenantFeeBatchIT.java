package de.farue.autocut.batch.fee;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.support.TransactionTemplate;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Activity;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.Transaction_;
import de.farue.autocut.domain.enumeration.SemesterTerms;
import de.farue.autocut.domain.enumeration.TransactionKind;
import de.farue.autocut.service.ActivityService;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.TenantService;
import de.farue.autocut.service.TransactionService;
import de.farue.autocut.service.accounting.TransactionBookService;

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
    private TransactionService transactionService;

    @Autowired
    private TenantFeeBatchScheduler batchScheduler;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EntityManager entityManager;

    private TransactionBook transactionBook;
    private Lease lease;
    private Tenant tenant;

    @BeforeEach
    void setUp() {
        Lease lease = new Lease()
            .start(LocalDate.of(2020, 4, 10))
            .end(LocalDate.of(2020, 9, 30))
            .nr("nr");
        this.lease = leaseService.save(lease);

        Tenant tenant = new Tenant()
            .firstName(TENANT_FIRST_NAME)
            .lastName(TENANT_LAST_NAME)
            .lease(lease)
            .verified(true);
        this.tenant = tenantService.save(tenant);

        this.transactionBook = leaseService.getCashTransactionBook(lease);

        Transaction transactionBeforeCharges = new Transaction()
            .bookingDate(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant())
            .valueDate(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant())
            .transactionBook(transactionBook)
            .kind(TransactionKind.CREDIT)
            .issuer("test")
            .value(new BigDecimal("50"))
            .balanceAfter(new BigDecimal("50"));
        transactionService.save(transactionBeforeCharges);

        Transaction transactionAfterCharges = new Transaction()
            .bookingDate(LocalDate.of(2099, 9, 1).atStartOfDay(ZoneId.systemDefault()).toInstant())
            .valueDate(LocalDate.of(2099, 9, 1).atStartOfDay(ZoneId.systemDefault()).toInstant())
            .transactionBook(transactionBook)
            .kind(TransactionKind.DEBIT)
            .issuer("test")
            .value(new BigDecimal("-7.5"))
            .balanceAfter(new BigDecimal("42.5"));
        transactionService.save(transactionAfterCharges);
    }

    @AfterEach
    void tearDown() {
        transactionTemplate.execute(txInfo -> {
            TransactionBook transactionBook = transactionBookService.findOne(this.transactionBook.getId()).get();
            Lease lease = leaseService.findOne(this.lease.getId()).get();
            Tenant tenant = tenantService.findOne(this.tenant.getId()).get();

            Page<Transaction> createdTransactions = transactionService.findAllForTransactionBook(transactionBook, Pageable.unpaged());
            Set<Transaction> linkedTransactions = new HashSet<>();
            createdTransactions.forEach(transaction -> {
                Set<Transaction> linked = transaction.getLefts();
                linkedTransactions.addAll(linked);
                linked.forEach(linkedTransaction -> {
                    linkedTransaction.setLefts(new HashSet<>());
                    transactionService.save(linkedTransaction);
                });
                transaction.setLefts(new HashSet<>());
                transactionService.save(transaction);

                transactionService.delete(transaction.getId());
            });
            linkedTransactions.forEach(transaction -> transactionService.delete(transaction.getId()));

            lease.removeTransactionBook(transactionBook);
            transactionBookService.delete(transactionBook.getId());
            tenantService.delete(tenant.getId());
            leaseService.delete(lease.getId());
            return null;
        });
    }

    @Nested
    class ShouldChargeAccount {

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class NoPreviousCharges{

            @Test
            void testDefaultCase() throws Exception {
                batchScheduler.setChargePeriod(CHARGE_PERIOD);
                batchScheduler.launchJob();

                List<Transaction> transactions = transactionBookService.findAllTransactionsForTransactionBook(transactionBook, PageRequest
                    .of(0, Integer.MAX_VALUE, Sort.by(Order.asc(Transaction_.VALUE_DATE), Order.asc(Transaction_.ID))))
                    .getContent();

                Transaction transactionBeforeCharges = transactions.get(0);
                assertThat(transactionBeforeCharges.getValue()).isEqualByComparingTo("50");
                assertThat(transactionBeforeCharges.getBalanceAfter()).isEqualByComparingTo("50");

                Transaction chargeApr = transactions.get(1);
                assertThat(chargeApr.getValue()).isEqualByComparingTo("-5");
                assertThat(chargeApr.getBalanceAfter()).isEqualByComparingTo("45");
                assertThat(chargeApr.getDescription()).isEqualTo("4/2020");
                assertThat(chargeApr.getBookingDate()).isBefore(chargeApr.getValueDate());
                assertThat(chargeApr.getServiceQulifier()).isEqualTo("2020-04-10;false");

                Transaction chargeMay = transactions.get(2);
                assertThat(chargeMay.getValue()).isEqualByComparingTo("-5");
                assertThat(chargeMay.getBalanceAfter()).isEqualByComparingTo("40");
                assertThat(chargeMay.getDescription()).isEqualTo("5/2020");
                assertThat(chargeMay.getBookingDate()).isBefore(chargeApr.getValueDate());
                assertThat(chargeMay.getServiceQulifier()).isEqualTo("2020-05-10;false");

                Transaction chargeJun = transactions.get(3);
                assertThat(chargeJun.getValue()).isEqualByComparingTo("-5");
                assertThat(chargeJun.getBalanceAfter()).isEqualByComparingTo("35");
                assertThat(chargeJun.getDescription()).isEqualTo("6/2020");
                assertThat(chargeJun.getBookingDate()).isBefore(chargeApr.getValueDate());
                assertThat(chargeJun.getServiceQulifier()).isEqualTo("2020-06-10;false");

                Transaction transactionAfterCharges = transactions.get(4);
                assertThat(transactionAfterCharges.getValue()).isEqualByComparingTo("-7.5");
                assertThat(transactionAfterCharges.getBalanceAfter()).isEqualByComparingTo("27.5");
            }
        }

        @Nested
        @SpringBootTest(classes = AutocutApp.class)
        class PreviousCharges {

            private Activity activity;

            @BeforeEach
            void setUp() {
                // we ignore for a moment that the lease had not started by this time
                Activity activity = new Activity()
                    .year(2019)
                    .term(SemesterTerms.WINTER_TERM)
                    .tenant(tenant);
                this.activity = activityService.save(activity);
            }

            @AfterEach
            void tearDown() {
                activityService.delete(activity.getId());
            }

            @Test
            void testNoChangeInActivity() throws Exception {
                Transaction chargeApr = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.FEE)
                    .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                    .bookingDate(LocalDate.of(2020, 4, 10).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .valueDate(LocalDate.of(2020, 4, 20).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .value(new BigDecimal("-2.5"))
                    .balanceAfter(new BigDecimal("47.5"))
                    .description("4/2020")
                    .serviceQulifier("2020-04-10;true");
                transactionService.save(chargeApr);

                Transaction chargeMay = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.FEE)
                    .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                    .bookingDate(LocalDate.of(2020, 5, 10).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .valueDate(LocalDate.of(2020, 5, 20).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .value(new BigDecimal("-2.5"))
                    .balanceAfter(new BigDecimal("45"))
                    .description("5/2020")
                    .serviceQulifier("2020-05-10;true");
                transactionService.save(chargeMay);

                Activity activity = activityService.findOne(this.activity.getId()).get();
                activity.setDiscount(true);
                activityService.save(activity);

                batchScheduler.setChargePeriod(CHARGE_PERIOD);
                batchScheduler.launchJob();

                List<Transaction> transactions = transactionBookService.findAllTransactionsForTransactionBook(transactionBook, PageRequest
                    .of(0, Integer.MAX_VALUE, Sort.by(Order.asc(Transaction_.VALUE_DATE), Order.asc(Transaction_.ID))))
                    .getContent();

                Transaction transactionBeforeCharges = transactions.get(0);
                assertThat(transactionBeforeCharges.getValue()).isEqualByComparingTo("50");
                assertThat(transactionBeforeCharges.getBalanceAfter()).isEqualByComparingTo("50");

                chargeApr = transactions.get(1);
                assertThat(chargeApr.getValue()).isEqualByComparingTo("-2.5");
                assertThat(chargeApr.getBalanceAfter()).isEqualByComparingTo("47.5");
                assertThat(chargeApr.getDescription()).isEqualTo("4/2020");
                assertThat(chargeApr.getBookingDate()).isBefore(chargeApr.getValueDate());
                assertThat(chargeApr.getServiceQulifier()).isEqualTo("2020-04-10;true");

                chargeMay = transactions.get(2);
                assertThat(chargeMay.getValue()).isEqualByComparingTo("-2.5");
                assertThat(chargeMay.getBalanceAfter()).isEqualByComparingTo("45");
                assertThat(chargeMay.getDescription()).isEqualTo("5/2020");
                assertThat(chargeMay.getBookingDate()).isBefore(chargeMay.getValueDate());
                assertThat(chargeMay.getServiceQulifier()).isEqualTo("2020-05-10;true");

                Transaction chargeJun = transactions.get(3);
                assertThat(chargeJun.getValue()).isEqualByComparingTo("-2.5");
                assertThat(chargeJun.getBalanceAfter()).isEqualByComparingTo("42.5");
                assertThat(chargeJun.getDescription()).isEqualTo("6/2020");
                assertThat(chargeJun.getBookingDate()).isBefore(chargeJun.getValueDate());
                assertThat(chargeJun.getServiceQulifier()).isEqualTo("2020-06-10;true");

                Transaction transactionAfterCharges = transactions.get(4);
                assertThat(transactionAfterCharges.getValue()).isEqualByComparingTo("-7.5");
                assertThat(transactionAfterCharges.getBalanceAfter()).isEqualByComparingTo("35");
            }

            @Test
            void testChangesInActivity() throws Exception {
                Transaction chargeApr = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.FEE)
                    .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                    .bookingDate(LocalDate.of(2020, 4, 10).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .valueDate(LocalDate.of(2020, 4, 20).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .value(new BigDecimal("-2.5"))
                    .balanceAfter(new BigDecimal("47.5"))
                    .description("4/2020")
                    .serviceQulifier("2020-04-10;true");
                transactionService.save(chargeApr);

                Transaction chargeMay1 = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.FEE)
                    .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                    .bookingDate(LocalDate.of(2020, 5, 10).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .valueDate(LocalDate.of(2020, 5, 20).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .value(new BigDecimal("-3"))
                    .balanceAfter(new BigDecimal("44.5"))
                    .description("5/2020")
                    .serviceQulifier("2020-05-10;false");
                transactionService.save(chargeMay1);

                Transaction chargeMay2 = new Transaction()
                    .transactionBook(transactionBook)
                    .kind(TransactionKind.FEE)
                    .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                    .bookingDate(LocalDate.of(2020, 5, 11).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .valueDate(LocalDate.of(2020, 5, 21).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .value(new BigDecimal("-5"))
                    .balanceAfter(new BigDecimal("39.5"))
                    .description("5/2020")
                    .serviceQulifier("2020-05-10;false");
                transactionService.save(chargeMay2);

                Activity activity = activityService.findOne(this.activity.getId()).get();
                activity.setDiscount(false);
                activityService.save(activity);

                batchScheduler.setChargePeriod(CHARGE_PERIOD);
                batchScheduler.launchJob();

                List<Transaction> transactions = transactionBookService.findAllTransactionsForTransactionBook(transactionBook, PageRequest
                    .of(0, Integer.MAX_VALUE, Sort.by(Order.asc(Transaction_.VALUE_DATE), Order.asc(Transaction_.ID))))
                    .getContent();

                Transaction transactionBeforeCharges = transactions.get(0);
                assertThat(transactionBeforeCharges.getValue()).isEqualByComparingTo("50");
                assertThat(transactionBeforeCharges.getBalanceAfter()).isEqualByComparingTo("50");

                Transaction originalChargeApr = transactions.get(1);
                assertThat(originalChargeApr.getValue()).isEqualByComparingTo("-2.5");
                assertThat(originalChargeApr.getBalanceAfter()).isEqualByComparingTo("47.5");

                Transaction originalChargeMay1 = transactions.get(2);
                assertThat(originalChargeMay1.getValue()).isEqualByComparingTo("-3");
                assertThat(originalChargeMay1.getBalanceAfter()).isEqualByComparingTo("44.5");

                Transaction originalChargeMay2 = transactions.get(3);
                assertThat(originalChargeMay2.getValue()).isEqualByComparingTo("-5");
                assertThat(originalChargeMay2.getBalanceAfter()).isEqualByComparingTo("39.5");

                Transaction chargeMayCorrection = transactions.get(4);
                assertThat(chargeMayCorrection.getKind()).isEqualTo(TransactionKind.CORRECTION);
                assertThat(chargeMayCorrection.getValue()).isEqualByComparingTo("3");
                assertThat(chargeMayCorrection.getBalanceAfter()).isEqualByComparingTo("42.5");
                assertThat(chargeMayCorrection.getDescription()).isEqualTo("5/2020");
                assertThat(chargeMayCorrection.getBookingDate()).isEqualTo(chargeMayCorrection.getValueDate()); // credit immediately
                assertThat(chargeMayCorrection.getServiceQulifier()).isEqualTo("2020-05-10;false");

                Transaction chargeAprCorrection = transactions.get(5);
                assertThat(chargeMayCorrection.getKind()).isEqualTo(TransactionKind.CORRECTION);
                assertThat(chargeAprCorrection.getValue()).isEqualByComparingTo("-2.5");
                assertThat(chargeAprCorrection.getBalanceAfter()).isEqualByComparingTo("40");
                assertThat(chargeAprCorrection.getDescription()).isEqualTo("4/2020");
                assertThat(chargeAprCorrection.getBookingDate()).isBefore(chargeAprCorrection.getValueDate()); // additional charge delayed
                assertThat(chargeAprCorrection.getServiceQulifier()).isEqualTo("2020-04-10;false");

                Transaction chargeJun = transactions.get(6);
                assertThat(chargeJun.getValue()).isEqualByComparingTo("-5");
                assertThat(chargeJun.getBalanceAfter()).isEqualByComparingTo("35");
                assertThat(chargeJun.getDescription()).isEqualTo("6/2020");
                assertThat(chargeJun.getBookingDate()).isBefore(chargeJun.getValueDate());
                assertThat(chargeJun.getServiceQulifier()).isEqualTo("2020-06-10;false");

                Transaction transactionAfterCharges = transactions.get(7);
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

            assertThat(transactionBookService.findAllTransactionsForTransactionBook(transactionBook, Pageable.unpaged())).hasSize(2);
        }

        @Test
        void testLeaseEndBeforeChargePeriod() throws Exception {
            Transaction chargeApr = new Transaction()
                .transactionBook(transactionBook)
                .kind(TransactionKind.FEE)
                .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                .bookingDate(LocalDate.of(2020, 4, 10).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .valueDate(LocalDate.of(2020, 4, 20).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .value(new BigDecimal("-2.5"))
                .balanceAfter(new BigDecimal("47.5"))
                .description("4/2020")
                .serviceQulifier("2020-04-10;true");
            transactionService.save(chargeApr);

            Transaction chargeMay1 = new Transaction()
                .transactionBook(transactionBook)
                .kind(TransactionKind.FEE)
                .issuer(AbstractTenantFeeBatchProcessor.ISSUER)
                .bookingDate(LocalDate.of(2020, 5, 10).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .valueDate(LocalDate.of(2020, 5, 20).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .value(new BigDecimal("-3"))
                .balanceAfter(new BigDecimal("44.5"))
                .description("5/2020")
                .serviceQulifier("2020-05-10;false");
            transactionService.save(chargeMay1);

            Lease lease = leaseService.findOne(TenantFeeBatchIT.this.lease.getId()).get();
            lease.setEnd(LocalDate.of(2020, 6, 1));
            leaseService.save(lease);

            batchScheduler.setChargePeriod(CHARGE_PERIOD);
            batchScheduler.launchJob();

            List<Transaction> transactions = transactionBookService.findAllTransactionsForTransactionBook(transactionBook, PageRequest
                .of(0, Integer.MAX_VALUE, Sort.by(Order.asc(Transaction_.VALUE_DATE), Order.asc(Transaction_.ID))))
                .getContent();

            Transaction transactionBeforeCharges = transactions.get(0);
            assertThat(transactionBeforeCharges.getValue()).isEqualByComparingTo("50");
            assertThat(transactionBeforeCharges.getBalanceAfter()).isEqualByComparingTo("50");

            Transaction originalChargeApr = transactions.get(1);
            assertThat(originalChargeApr.getValue()).isEqualByComparingTo("-2.5");
            assertThat(originalChargeApr.getBalanceAfter()).isEqualByComparingTo("47.5");

            Transaction originalChargeMay = transactions.get(2);
            assertThat(originalChargeMay.getValue()).isEqualByComparingTo("-3");
            assertThat(originalChargeMay.getBalanceAfter()).isEqualByComparingTo("44.5");

            // Corrections should still be booked even if Lease has expired
            Transaction chargeAprCorrection = transactions.get(3);
            assertThat(chargeAprCorrection.getKind()).isEqualTo(TransactionKind.CORRECTION);
            assertThat(chargeAprCorrection.getValue()).isEqualByComparingTo("-2.5");
            assertThat(chargeAprCorrection.getBalanceAfter()).isEqualByComparingTo("42");
            assertThat(chargeAprCorrection.getDescription()).isEqualTo("4/2020");
            assertThat(chargeAprCorrection.getBookingDate()).isBefore(chargeAprCorrection.getValueDate());
            assertThat(chargeAprCorrection.getServiceQulifier()).isEqualTo("2020-04-10;false");

            Transaction chargeMayCorrection = transactions.get(4);
            assertThat(chargeMayCorrection.getKind()).isEqualTo(TransactionKind.CORRECTION);
            assertThat(chargeMayCorrection.getValue()).isEqualByComparingTo("-2");
            assertThat(chargeMayCorrection.getBalanceAfter()).isEqualByComparingTo("40");
            assertThat(chargeMayCorrection.getDescription()).isEqualTo("5/2020");
            assertThat(chargeMayCorrection.getBookingDate()).isBefore(chargeMayCorrection.getValueDate());
            assertThat(chargeMayCorrection.getServiceQulifier()).isEqualTo("2020-05-10;false");

            Transaction transactionAfterCharges = transactions.get(5);
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

            assertThat(transactionBookService.findAllTransactionsForTransactionBook(transactionBook, Pageable.unpaged())).hasSize(2);
        }
    }
}
