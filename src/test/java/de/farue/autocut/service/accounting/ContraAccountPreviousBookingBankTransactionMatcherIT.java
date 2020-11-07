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
import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.repository.BankAccountRepository;
import de.farue.autocut.repository.BankTransactionRepository;
import de.farue.autocut.repository.InternalTransactionRepository;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.TenantService;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
@SpringBootTest(classes = AutocutApp.class)
class ContraAccountPreviousBookingBankTransactionMatcherIT {

    private static final LocalDate ANY_START = LocalDate.now().minusMonths(1);
    private static final LocalDate ANY_END = LocalDate.now().plusMonths(1);
    private static final String ANY_NO = "any no";
    private static final String ANY_DESCRIPTION = "any description";
    private static final BigDecimal ANY_VALUE = new BigDecimal("5");
    private static final BigDecimal ANY_BALANCE_AFTER = new BigDecimal("20");
    private static final Instant ANY_BOOKING_DATE = Instant.now();
    private static final Instant ANY_VALUE_DATE = Instant.now();
    private static final String ANY_TYPE = "any type";
    private static final String ANY_ISSUER = "any issuer";
    public static final String ANY_OTHER_NO = "any other no";

    @Autowired
    private LeaseService leaseService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private InternalTransactionRepository internalTransactionRepository;

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    @Qualifier("referenceBankAccount")
    private BankAccount referenceBankAccount;

    @Autowired
    @Qualifier("referenceCashTransactionBook")
    private TransactionBook referenceCashTransactionBook;

    @Autowired
    private ContraAccountPreviousBookingBankTransactionMatcher matcher;

    private Lease lease1;
    private Tenant tenant1;

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
    }

    @Test
    @Transactional
    void testNoPreviousBooking() {
        BankAccount contraBankAccount = new BankAccount()
            .bic("MALADE51KOB")
            .iban("DE13570501204826655542")
            .name("Bob Miller");
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

    @Test
    @Transactional
    void testPreviousBookingsLinkedToSingleTenant() {
        BankAccount contraBankAccount = new BankAccount()
            .bic("MALADE51KOB")
            .iban("DE13570501204826655542")
            .name("Bob Miller");
        contraBankAccount = bankAccountRepository.save(contraBankAccount);

        BankTransaction previousBankTransaction = new BankTransaction()
            .type(ANY_TYPE)
            .bookingDate(ANY_BOOKING_DATE)
            .valueDate(ANY_VALUE_DATE)
            .value(ANY_VALUE)
            .balanceAfter(ANY_BALANCE_AFTER)
            .description(ANY_DESCRIPTION)
            .bankAccount(referenceBankAccount)
            .contraBankAccount(contraBankAccount)
            .transactionBook(referenceCashTransactionBook);
        InternalTransaction previousContraTransaction = new InternalTransaction()
            .transactionType(TransactionType.CREDIT)
            .bookingDate(ANY_BOOKING_DATE)
            .valueDate(ANY_VALUE_DATE)
            .value(ANY_VALUE)
            .balanceAfter(ANY_BALANCE_AFTER)
            .issuer(ANY_ISSUER)
            .description(ANY_DESCRIPTION)
            .transactionBook(leaseService.getCashTransactionBook(lease1));
        previousBankTransaction.addLeft(previousContraTransaction);
        previousContraTransaction.addLeft(previousBankTransaction);

        bankTransactionRepository.save(previousBankTransaction);
        internalTransactionRepository.save(previousContraTransaction);

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

        assertThat(lease1.getTransactionBooks()).contains(transactionBook);
    }

    @Test
    @Transactional
    void testPreviousBookingsLinkedToMultipleTenants() {
        BankAccount contraBankAccount = new BankAccount()
            .bic("MALADE51KOB")
            .iban("DE13570501204826655542")
            .name("Bob Miller");
        contraBankAccount = bankAccountRepository.save(contraBankAccount);

        Lease lease2 = new Lease()
            .nr(ANY_OTHER_NO)
            .start(ANY_START)
            .end(ANY_END);
        lease2 = leaseService.save(lease2);

        BankTransaction previousBankTransaction1 = new BankTransaction()
            .type(ANY_TYPE)
            .bookingDate(ANY_BOOKING_DATE)
            .valueDate(ANY_VALUE_DATE)
            .value(ANY_VALUE)
            .balanceAfter(ANY_BALANCE_AFTER)
            .description(ANY_DESCRIPTION)
            .bankAccount(referenceBankAccount)
            .contraBankAccount(contraBankAccount)
            .transactionBook(referenceCashTransactionBook);
        InternalTransaction previousContraTransaction1 = new InternalTransaction()
            .transactionType(TransactionType.CREDIT)
            .bookingDate(ANY_BOOKING_DATE)
            .valueDate(ANY_VALUE_DATE)
            .value(ANY_VALUE)
            .balanceAfter(ANY_BALANCE_AFTER)
            .issuer(ANY_ISSUER)
            .description(ANY_DESCRIPTION)
            .transactionBook(leaseService.getCashTransactionBook(lease2));
        previousBankTransaction1.addLeft(previousContraTransaction1);
        previousContraTransaction1.addLeft(previousBankTransaction1);

        bankTransactionRepository.save(previousBankTransaction1);
        internalTransactionRepository.save(previousContraTransaction1);

        BankTransaction previousBankTransaction2 = new BankTransaction()
            .type(ANY_TYPE)
            .bookingDate(ANY_BOOKING_DATE)
            .valueDate(ANY_VALUE_DATE)
            .value(ANY_VALUE)
            .balanceAfter(ANY_BALANCE_AFTER)
            .description(ANY_DESCRIPTION)
            .bankAccount(referenceBankAccount)
            .contraBankAccount(contraBankAccount)
            .transactionBook(referenceCashTransactionBook);
        InternalTransaction previousContraTransaction2 = new InternalTransaction()
            .transactionType(TransactionType.CREDIT)
            .bookingDate(ANY_BOOKING_DATE)
            .valueDate(ANY_VALUE_DATE)
            .value(ANY_VALUE)
            .balanceAfter(ANY_BALANCE_AFTER)
            .issuer(ANY_ISSUER)
            .description(ANY_DESCRIPTION)
            .transactionBook(leaseService.getCashTransactionBook(lease1));
        previousBankTransaction2.addLeft(previousContraTransaction2);
        previousContraTransaction2.addLeft(previousBankTransaction2);

        bankTransactionRepository.save(previousBankTransaction2);
        internalTransactionRepository.save(previousContraTransaction2);

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
