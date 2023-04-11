package de.farue.autocut.service.accounting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.*;
import de.farue.autocut.security.AuthoritiesConstants;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.TenantService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({ "FieldCanBeLocal", "unused" })
@Transactional
@IntegrationTest
@WithMockUser(authorities = { AuthoritiesConstants.ADMIN, AuthoritiesConstants.VIEW_TRANSACTIONS })
public class PurposeBankTransactionMatcherIT {

    private static final LocalDate ANY_START = LocalDate.now().minusMonths(1);
    private static final LocalDate ANY_END = LocalDate.now().plusMonths(1);
    private static final String ANY_NO = "any no";
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
    private BankAccountService bankAccountService;

    @Autowired
    @Qualifier("referenceBankAccount")
    private BankAccount referenceBankAccount;

    @Autowired
    @Qualifier("referenceCashTransactionBook")
    private TransactionBook referenceCashTransactionBook;

    @Autowired
    private PurposeBankTransactionMatcher matcher;

    @MockBean(name = "tenantPurposeMatchCandidateProvider")
    private MatchCandidateProvider matchCandidateProvider;

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

    private BankAccount contraBankAccount;

    @BeforeEach
    void setUp() {
        Lease lease1 = new Lease().nr(ANY_NO).start(ANY_START).end(ANY_END);
        Tenant tenant1 = new Tenant().firstName("Bob").lastName("Miller").lease(lease1);
        this.lease1 = leaseService.save(lease1);
        this.tenant1 = tenantService.save(tenant1);

        Lease lease2 = new Lease().nr(ANY_NO).start(ANY_START).end(ANY_END);
        Tenant tenant2 = new Tenant().firstName("Alice").lastName("Wonderland").lease(lease2);
        this.lease2 = leaseService.save(lease2);
        this.tenant2 = tenantService.save(tenant2);

        Lease lease3 = new Lease().nr(ANY_NO).start(ANY_START).end(ANY_END);
        Tenant tenant3 = new Tenant().firstName("Chris").lastName("Black").lease(lease3);
        Tenant otherTenant3 = new Tenant().firstName("Jack").lastName("Black").lease(lease3);
        this.lease3 = leaseService.save(lease3);
        this.tenant3 = tenantService.save(tenant3);
        this.tenant3 = tenantService.save(otherTenant3);

        Lease lease4 = new Lease().nr(ANY_NO).start(ANY_START).end(ANY_END);
        Tenant tenant4 = new Tenant().firstName("Jet").lastName("Li").lease(lease4);
        this.lease4 = leaseService.save(lease4);
        this.tenant4 = tenantService.save(tenant4);

        Lease lease5 = new Lease().nr(ANY_NO).start(ANY_START).end(ANY_END);
        Tenant tenant5 = new Tenant().firstName("Chen").lastName("Li").lease(lease5);
        this.lease5 = leaseService.save(lease5);
        this.tenant5 = tenantService.save(tenant5);

        BankAccount contraBankAccount = new BankAccount().bic("MALADE51KOB").iban("DE13570501204826655542").name("Chris Black");
        this.contraBankAccount = bankAccountService.save(contraBankAccount);

        when(matchCandidateProvider.buildMatchCandidates(tenant1)).thenReturn(Set.of("Bob Miller 123 45", "Miller 123 45"));
        when(matchCandidateProvider.buildMatchCandidates(tenant2)).thenReturn(Set.of("Alice Wonderland 123 33", "Wonderland 123 33"));
        when(matchCandidateProvider.buildMatchCandidates(tenant3)).thenReturn(Set.of("Chris Black 123 21", "Black 123 21"));
        when(matchCandidateProvider.buildMatchCandidates(otherTenant3)).thenReturn(Set.of("Jack Black 123 21", "Black 123 21"));
        when(matchCandidateProvider.buildMatchCandidates(tenant4)).thenReturn(Set.of("Jet Li 123 11", "Li 123 11"));
        when(matchCandidateProvider.buildMatchCandidates(tenant5)).thenReturn(Set.of("Chen Li 123 11", "Li 123 11"));
    }

    @Test
    void testNameMatchInDescription() {
        BankTransaction bankTransaction = new BankTransaction()
            .type(ANY_TYPE)
            .bookingDate(ANY_BOOKING_DATE)
            .valueDate(ANY_VALUE_DATE)
            .value(ANY_VALUE)
            .balanceAfter(ANY_BALANCE_AFTER)
            .description("Transaction? made by-Chris & last name Black apartment 123/21")
            .bankAccount(referenceBankAccount)
            .contraBankAccount(contraBankAccount)
            .transactionBook(referenceCashTransactionBook);

        TransactionBook transactionBook = matcher.findMatch(bankTransaction).get();

        assertThat(lease3.getTransactionBooks()).contains(transactionBook);
    }

    @Test
    void testNoTenantNameMatch() {
        BankTransaction bankTransaction = new BankTransaction()
            .type(ANY_TYPE)
            .bookingDate(ANY_BOOKING_DATE)
            .valueDate(ANY_VALUE_DATE)
            .value(ANY_VALUE)
            .balanceAfter(ANY_BALANCE_AFTER)
            .description("Alan Bob 123 44")
            .bankAccount(referenceBankAccount)
            .contraBankAccount(contraBankAccount)
            .transactionBook(referenceCashTransactionBook);

        Optional<TransactionBook> transactionBookOptional = matcher.findMatch(bankTransaction);

        assertThat(transactionBookOptional).isEmpty();
    }

    @Test
    void testMultipleTenantNamesOfDifferentLeasesMatch() {
        BankTransaction bankTransaction = new BankTransaction()
            .type(ANY_TYPE)
            .bookingDate(ANY_BOOKING_DATE)
            .valueDate(ANY_VALUE_DATE)
            .value(ANY_VALUE)
            .balanceAfter(ANY_BALANCE_AFTER)
            .description("Li 123 11")
            .bankAccount(referenceBankAccount)
            .contraBankAccount(contraBankAccount)
            .transactionBook(referenceCashTransactionBook);

        Optional<TransactionBook> transactionBookOptional = matcher.findMatch(bankTransaction);

        assertThat(transactionBookOptional).isEmpty();
    }

    @Test
    void testMultipleTenantNamesOfSameLeaseMatch() {
        BankTransaction bankTransaction = new BankTransaction()
            .type(ANY_TYPE)
            .bookingDate(ANY_BOOKING_DATE)
            .valueDate(ANY_VALUE_DATE)
            .value(ANY_VALUE)
            .balanceAfter(ANY_BALANCE_AFTER)
            .description("Black 123 11")
            .bankAccount(referenceBankAccount)
            .contraBankAccount(contraBankAccount)
            .transactionBook(referenceCashTransactionBook);

        Optional<TransactionBook> transactionBookOptional = matcher.findMatch(bankTransaction);

        assertThat(lease3.getTransactionBooks()).contains(transactionBookOptional.get());
    }
}
