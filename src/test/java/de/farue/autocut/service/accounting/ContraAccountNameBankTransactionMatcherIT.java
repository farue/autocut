package de.farue.autocut.service.accounting;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.BankAccount;
import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.TenantService;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
@SpringBootTest(classes = AutocutApp.class)
class ContraAccountNameBankTransactionMatcherIT {

    private static final LocalDate ANY_START = LocalDate.now().minusMonths(1);
    private static final LocalDate ANY_END = LocalDate.now().plusMonths(1);
    private static final String ANY_NO = "any no";
    private static final String ANY_DESCRIPTION = "any description";
    private static final BigDecimal ANY_VALUE = new BigDecimal("5");
    private static final BigDecimal ANY_BALANCE_AFTER = new BigDecimal("20");
    private static final Instant ANY_BOOKING_DATE = Instant.now();
    private static final Instant ANY_VALUE_DATE = Instant.now();
    private static final String ANY_TYPE = "any type";

    @Autowired
    private LeaseService leaseService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    @Qualifier("referenceBankAccount")
    private BankAccount referenceBankAccount;

    @Autowired
    @Qualifier("referenceCashTransactionBook")
    private TransactionBook referenceCashTransactionBook;

    @Autowired
    private ContraAccountNameBankTransactionMatcher matcher;

    private Lease lease1;
    private Tenant tenant1;
    private Lease lease2;
    private Tenant tenant2;
    private Lease lease3;
    private Tenant tenant3;
    private Lease lease4;
    private Tenant tenant4;
    private Lease lease5;
    private Tenant tenant5;

    @BeforeEach
    void setUp() {
        Lease lease1 = new Lease()
            .nr(ANY_NO)
            .start(ANY_START)
            .end(ANY_END);
        Tenant tenant1 = new Tenant()
            .firstName("Bob")
            .lastName("Miller")
            .lease(lease1);
        this.lease1 = leaseService.save(lease1);
        this.tenant1 = tenantService.save(tenant1);

        Lease lease2 = new Lease()
            .nr(ANY_NO)
            .start(ANY_START)
            .end(ANY_END);
        Tenant tenant2 = new Tenant()
            .firstName("Alice")
            .lastName("Wonderland")
            .lease(lease2);
        this.lease2 = leaseService.save(lease2);
        this.tenant2 = tenantService.save(tenant2);

        Lease lease3 = new Lease()
            .nr(ANY_NO)
            .start(ANY_START)
            .end(ANY_END);
        Tenant tenant3 = new Tenant()
            .firstName("Chris")
            .lastName("Black")
            .lease(lease3);
        this.lease3 = leaseService.save(lease3);
        this.tenant3 = tenantService.save(tenant3);

        Lease lease4 = new Lease()
            .nr(ANY_NO)
            .start(ANY_START)
            .end(ANY_END);
        Tenant tenant4 = new Tenant()
            .firstName("Jet")
            .lastName("Li")
            .lease(lease4);
        this.lease4 = leaseService.save(lease4);
        this.tenant4 = tenantService.save(tenant4);

        Lease lease5 = new Lease()
            .nr(ANY_NO)
            .start(ANY_START)
            .end(ANY_END);
        Tenant tenant5 = new Tenant()
            .firstName("Chen")
            .lastName("Li")
            .lease(lease5);
        this.lease5 = leaseService.save(lease5);
        this.tenant5 = tenantService.save(tenant5);
    }

    @Test
    @Transactional
    void testExactNameMatch() {
        BankAccount contraBankAccount = new BankAccount()
            .bic("MALADE51KOB")
            .iban("DE13570501204826655542")
            .name("Chris Black");
        BankTransaction bankTransaction = new BankTransaction()
            .type(ANY_TYPE)
            .bookingDate(ANY_BOOKING_DATE)
            .valueDate(ANY_VALUE_DATE)
            .value(ANY_VALUE)
            .balanceAfter(ANY_BALANCE_AFTER)
            .description(ANY_DESCRIPTION)
            .bankAccount(referenceBankAccount)
            .contraBankAccount(contraBankAccount)
            .transactionBook(referenceCashTransactionBook);

        TransactionBook transactionBook = matcher.findMatch(bankTransaction).get();

        assertThat(lease3.getTransactionBooks()).contains(transactionBook);
    }

    @Test
    @Transactional
    void testNoTenantNameMatch() {
        BankAccount contraBankAccount = new BankAccount()
            .bic("MALADE51KOB")
            .iban("DE13570501204826655542")
            .name("Dean Martin");
        BankTransaction bankTransaction = new BankTransaction()
            .type(ANY_TYPE)
            .bookingDate(ANY_BOOKING_DATE)
            .valueDate(ANY_VALUE_DATE)
            .value(ANY_VALUE)
            .balanceAfter(ANY_BALANCE_AFTER)
            .description(ANY_DESCRIPTION)
            .bankAccount(referenceBankAccount)
            .contraBankAccount(contraBankAccount)
            .transactionBook(referenceCashTransactionBook);

        Optional<TransactionBook> transactionBookOptional = matcher.findMatch(bankTransaction);

        assertThat(transactionBookOptional).isEmpty();
    }
}
