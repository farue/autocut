package de.farue.autocut.batch.banking;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.*;
import de.farue.autocut.domain.enumeration.ApartmentTypes;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.repository.BankTransactionRepository;
import de.farue.autocut.repository.InternalTransactionRepository;
import de.farue.autocut.service.*;
import de.farue.autocut.service.accounting.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Saldo;
import org.kapott.hbci.structures.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

@SuppressWarnings({ "FieldCanBeLocal", "unused" })
@SpringBootTest(classes = AutocutApp.class)
class BankingBatchIT {

    private static final LocalDate ANY_START = LocalDate.now().minusMonths(1);
    private static final LocalDate ANY_END = LocalDate.now().plusMonths(1);
    private static final String ANY_NO = "any no";

    @Autowired
    private BankingServiceMock bankingServiceMock;

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private InternalTransactionService internalTransactionService;

    @Autowired
    private TransactionBookService transactionBookService;

    @Autowired
    private LeaseService leaseService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private BankingBatchScheduler batchScheduler;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Autowired
    private InternalTransactionRepository internalTransactionRepository;

    @Autowired
    private AssociationService associationService;

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
        Address address = new Address().country("any country").city("any city").zip("12345").street("test street").streetNumber("123");
        addressService.save(address);

        Apartment apartment1 = new Apartment().nr("12").type(ApartmentTypes.SINGLE).maxNumberOfLeases(1).address(address);
        Lease lease1 = new Lease().nr(ANY_NO).start(ANY_START).end(ANY_END).apartment(apartment1);
        Tenant tenant1 = new Tenant().firstName("Bob").lastName("Miller").lease(lease1);
        apartmentService.save(apartment1);
        this.lease1 = leaseService.save(lease1);
        this.tenant1 = tenantService.save(tenant1);

        Apartment apartment2 = new Apartment().nr("13").type(ApartmentTypes.SHARED).maxNumberOfLeases(2).address(address);
        Lease lease2 = new Lease().nr(ANY_NO).start(ANY_START).end(ANY_END).apartment(apartment2);
        Tenant tenant2 = new Tenant().firstName("Alice").lastName("Wonderland").lease(lease2);
        apartmentService.save(apartment2);
        this.lease2 = leaseService.save(lease2);
        this.tenant2 = tenantService.save(tenant2);

        Apartment apartment3 = new Apartment().nr("14").type(ApartmentTypes.SHARED).maxNumberOfLeases(2).address(address);
        Lease lease3 = new Lease().nr(ANY_NO).start(ANY_START).end(ANY_END).apartment(apartment3);
        Tenant tenant3 = new Tenant().firstName("Chris").lastName("Black").lease(lease3);
        apartmentService.save(apartment3);
        this.lease3 = leaseService.save(lease3);
        this.tenant3 = tenantService.save(tenant3);

        Apartment apartment4 = new Apartment().nr("33").type(ApartmentTypes.SHARED).maxNumberOfLeases(2).address(address);
        Lease lease4 = new Lease().nr(ANY_NO).start(ANY_START).end(ANY_END).apartment(apartment4);
        Tenant tenant4 = new Tenant().firstName("Jet").lastName("Li").lease(lease4);
        apartmentService.save(apartment4);
        this.lease4 = leaseService.save(lease4);
        this.tenant4 = tenantService.save(tenant4);

        Lease lease5 = new Lease().nr(ANY_NO).start(ANY_START).end(ANY_END).apartment(apartment4);
        Tenant tenant5 = new Tenant().firstName("Chen").lastName("Li").lease(lease5);
        this.lease5 = leaseService.save(lease5);
        this.tenant5 = tenantService.save(tenant5);

        bankingServiceMock.setTransactions(createTestTransactions());
    }

    @Test
    void testNoPreviousTransactionsExist() throws Exception {
        batchScheduler.launchJob();

        List<BankTransaction> transactions = bankTransactionService.findAllForTransactionBookWithLinks(
            associationService.getCashTransactionBook()
        );

        BankTransaction transaction1 = transactions.get(0);
        assertThat(transaction1.getType()).isEqualTo("ZINSEN/ENTG.");
        assertThat(transaction1.getValue()).isEqualByComparingTo("-7.30");
        assertThat(transaction1.getBalanceAfter()).isEqualByComparingTo("22.70");
        assertThat(transaction1.getBookingDate()).isEqualTo(getInstant(2020, 1, 1));
        assertThat(transaction1.getValueDate()).isEqualTo(getInstant(2020, 1, 1));
        assertThat(transaction1.getLefts()).isEmpty();

        BankTransaction transaction2 = transactions.get(1);
        assertThat(transaction2.getType()).isEqualTo("GUTSCHR.SEPA");
        assertThat(transaction2.getValue()).isEqualByComparingTo("15");
        assertThat(transaction2.getBalanceAfter()).isEqualByComparingTo("37.70");
        assertThat(transaction2.getBookingDate()).isEqualTo(getInstant(2020, 1, 2));
        assertThat(transaction2.getValueDate()).isEqualTo(getInstant(2020, 1, 2));
        assertThat(transaction2.getLefts()).hasSize(1);

        InternalTransaction contraTransaction1 = (InternalTransaction) transaction2.getLefts().iterator().next();
        TransactionBook transactionBookChen = leaseService.getCashTransactionBook(leaseService.findOne(lease5.getId()).get());
        assertThat(contraTransaction1.getTransactionBook()).isEqualTo(transactionBookChen);
        assertThat(contraTransaction1.getValue()).isEqualByComparingTo("15");
        assertThat(contraTransaction1.getBookingDate()).isCloseTo(Instant.now(), within(1, ChronoUnit.DAYS));
        assertThat(contraTransaction1.getValueDate()).isCloseTo(Instant.now(), within(1, ChronoUnit.DAYS));

        BankTransaction transaction3 = transactions.get(2);
        assertThat(transaction3.getType()).isEqualTo("SEPA ÜBERW.");
        assertThat(transaction3.getValue()).isEqualByComparingTo("-10");
        assertThat(transaction3.getBalanceAfter()).isEqualByComparingTo("27.70");
        assertThat(transaction3.getBookingDate()).isEqualTo(getInstant(2020, 1, 2));
        assertThat(transaction3.getValueDate()).isEqualTo(getInstant(2020, 1, 2));
        assertThat(transaction3.getLefts()).hasSize(1);

        InternalTransaction contraTransaction2 = (InternalTransaction) transaction3.getLefts().iterator().next();
        TransactionBook transactionBookAlice = leaseService.getCashTransactionBook(leaseService.findOne(lease2.getId()).get());
        assertThat(contraTransaction2.getTransactionBook()).isEqualTo(transactionBookAlice);
        assertThat(contraTransaction2.getValue()).isEqualByComparingTo("-10");
        assertThat(contraTransaction2.getBookingDate()).isCloseTo(Instant.now(), within(1, ChronoUnit.DAYS));
        assertThat(contraTransaction2.getValueDate()).isCloseTo(Instant.now(), within(1, ChronoUnit.DAYS));
    }

    @Test
    void testTransactionsAlreadyExist() throws Exception {
        List<Long> transactionIds = transactionTemplate.execute(txInfo -> {
            BankTransaction existingTransaction1 = new BankTransaction()
                .customerRef("NONREF")
                .gvCode("805")
                .type("ZINSEN/ENTG.")
                .bankAccount(associationService.getBankAccount())
                .bookingDate(getInstant(2020, 1, 1))
                .valueDate(getInstant(2020, 1, 1))
                .value(new BigDecimal("-7.30"))
                .balanceAfter(new BigDecimal("22.70"))
                .description("")
                .transactionBook(associationService.getCashTransactionBook());
            existingTransaction1 = bankTransactionService.save(existingTransaction1);

            BankAccount existingBankAccount = new BankAccount().name("Mr Chen Wu Li").iban("DE13570501204826655542").bic("MALADE51KOB");
            existingBankAccount = bankAccountService.save(existingBankAccount);

            BankTransaction existingTransaction2 = new BankTransaction()
                .customerRef("NONREF")
                .gvCode("166")
                .type("GUTSCHR.SEPA")
                .bankAccount(associationService.getBankAccount())
                .contraBankAccount(existingBankAccount)
                .bookingDate(getInstant(2020, 1, 2))
                .valueDate(getInstant(2020, 1, 2))
                .value(new BigDecimal("15"))
                .balanceAfter(new BigDecimal("37.70"))
                .description("Referenz NOTPROVIDED Verwendungszweck Chen Li test street 123-33")
                .transactionBook(associationService.getCashTransactionBook());
            existingTransaction2 = bankTransactionService.save(existingTransaction2);

            TransactionBook transactionBookChen = leaseService.getCashTransactionBook(leaseService.findOne(lease5.getId()).get());
            InternalTransaction existingContraTransaction = new InternalTransaction()
                .issuer("BankTransactionService")
                .transactionType(TransactionType.CREDIT)
                .bookingDate(getInstant(2020, 2, 3))
                .valueDate(getInstant(2020, 2, 3))
                .value(new BigDecimal("15"))
                .description("Bank transfer 2020-01-02")
                .transactionBook(transactionBookChen);
            existingContraTransaction = internalTransactionService.save(existingContraTransaction);

            existingTransaction2.link(existingContraTransaction);

            return List.of(existingTransaction1.getId(), existingTransaction2.getId(), existingContraTransaction.getId());
        });

        batchScheduler.launchJob();

        List<BankTransaction> transactions = bankTransactionService.findAllForTransactionBookWithLinks(
            associationService.getCashTransactionBook()
        );

        assertThat(transactions).hasSize(3);
        BankTransaction transaction1 = transactions.get(0);
        assertThat(transaction1.getId()).isEqualTo(transactionIds.get(0));

        BankTransaction transaction2 = transactions.get(1);
        assertThat(transaction2.getId()).isEqualTo(transactionIds.get(1));
        assertThat(transaction2.getLefts()).hasSize(1);

        Transaction contraTransaction1 = transaction2.getLefts().iterator().next();
        assertThat(contraTransaction1.getId()).isEqualTo(transactionIds.get(2));
        AssertionsForClassTypes.assertThat(contraTransaction1.getBookingDate()).isEqualTo(getInstant(2020, 2, 3));
        AssertionsForClassTypes.assertThat(contraTransaction1.getValueDate()).isEqualTo(getInstant(2020, 2, 3));

        BankTransaction transaction3 = transactions.get(2);
        assertThat(transaction3.getType()).isEqualTo("SEPA ÜBERW.");
        assertThat(transaction3.getValue()).isEqualByComparingTo("-10");
        assertThat(transaction3.getBalanceAfter()).isEqualByComparingTo("27.70");
        assertThat(transaction3.getLefts()).hasSize(1);

        InternalTransaction contraTransaction2 = (InternalTransaction) transaction3.getLefts().iterator().next();
        TransactionBook transactionBookAlice = leaseService.getCashTransactionBook(leaseService.findOne(lease2.getId()).get());
        assertThat(contraTransaction2.getTransactionBook()).isEqualTo(transactionBookAlice);
        assertThat(contraTransaction2.getValue()).isEqualByComparingTo("-10");
        assertThat(contraTransaction2.getBookingDate()).isCloseTo(Instant.now(), within(1, ChronoUnit.DAYS));
        assertThat(contraTransaction2.getValueDate()).isCloseTo(Instant.now(), within(1, ChronoUnit.DAYS));
    }

    @AfterEach
    void tearDown() {
        transactionTemplate.execute(txInfo -> {
            deleteTenantAndTransitiveEntities(tenant1);
            deleteTenantAndTransitiveEntities(tenant2);
            deleteTenantAndTransitiveEntities(tenant3);
            deleteTenantAndTransitiveEntities(tenant4);
            deleteTenantAndTransitiveEntities(tenant5);
            List<BankTransaction> bankTransactions = bankTransactionService.findAllForTransactionBookWithLinks(
                associationService.getCashTransactionBook()
            );
            deleteTransactions(bankTransactions);
            bankAccountService
                .findByIban("DE13570501204826655542")
                .ifPresent(bankAccount -> bankAccountService.delete(bankAccount.getId()));
            bankAccountService
                .findByIban("DE28258513358289433438")
                .ifPresent(bankAccount -> bankAccountService.delete(bankAccount.getId()));
            return null;
        });
    }

    private List<UmsLine> createTestTransactions() {
        UmsLine t1 = new UmsLine();
        t1.valuta = getDate(2020, 1, 1);
        t1.bdate = getDate(2020, 1, 1);
        t1.value = new Value(new BigDecimal("-7.30"));
        t1.isStorno = false;
        t1.saldo = new Saldo();
        t1.saldo.value = new Value(new BigDecimal("22.70"));
        t1.saldo.timestamp = getDate(2020, 1, 1);
        t1.customerref = "NONREF";
        t1.instref = "";
        t1.gvcode = "805";
        t1.text = "ZINSEN/ENTG.";
        t1.usage = new ArrayList<>();
        t1.addkey = "000";

        UmsLine t2 = new UmsLine();
        t2.valuta = getDate(2020, 1, 2);
        t2.bdate = getDate(2020, 1, 2);
        t2.value = new Value(new BigDecimal("15"));
        t2.isStorno = false;
        t2.saldo = new Saldo();
        t2.saldo.value = new Value(new BigDecimal("37.70"));
        t2.saldo.timestamp = getDate(2020, 1, 2);
        t2.customerref = "NONREF";
        t2.instref = "";
        t2.gvcode = "166";
        t2.text = "GUTSCHR.SEPA";
        t2.usage = List.of("Referenz NOTPROVIDED", "Verwendungszweck", "Chen Li test street 123-33");
        t2.other = new Konto();
        t2.other.name = "Mr Chen Wu";
        t2.other.name2 = "Li";
        t2.other.bic = "MALADE51KOB";
        t2.other.iban = "DE13570501204826655542";
        t2.addkey = "000";

        UmsLine t3 = new UmsLine();
        t3.valuta = getDate(2020, 1, 2);
        t3.bdate = getDate(2020, 1, 2);
        t3.value = new Value(new BigDecimal("-10"));
        t3.isStorno = false;
        t3.saldo = new Saldo();
        t3.saldo.value = new Value(new BigDecimal("27.70"));
        t3.saldo.timestamp = getDate(2020, 1, 2);
        t3.customerref = "NONREF";
        t3.instref = "";
        t3.gvcode = "116";
        t3.text = "SEPA ÜBERW.";
        t3.usage = List.of("Alice Wonderland 2WG Nummer 31-13");
        t3.other = new Konto();
        t3.other.name = "Alice Wonderland";
        t3.other.bic = "NOLADE21DAN";
        t3.other.iban = "DE28258513358289433438";
        t3.addkey = "000";

        return List.of(t1, t2, t3);
    }

    private Date getDate(int year, int month, int dayOfMonth) {
        return Date.from(getInstant(year, month, dayOfMonth));
    }

    private Instant getInstant(int year, int month, int dayOfMonth) {
        return LocalDate.of(year, month, dayOfMonth).atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    private void deleteTenantAndTransitiveEntities(Tenant tenant) {
        tenantService
            .findOne(tenant.getId())
            .ifPresent(loadedTenant -> {
                Lease lease = loadedTenant.getLease();
                leaseService
                    .findOne(lease.getId())
                    .ifPresent(loadedLease -> {
                        Set<TransactionBook> transactionBooks = loadedLease.getTransactionBooks();
                        loadedLease.setTransactionBooks(new HashSet<>());
                        leaseService.save(loadedLease);
                        tenantService.delete(tenant.getId());
                        leaseService.delete(loadedLease.getId());
                        for (TransactionBook transactionBook : transactionBooks) {
                            List<InternalTransaction> transactions = internalTransactionService.findAllForTransactionBookWithLinks(
                                transactionBook
                            );
                            transactions.forEach(internalTransactionService::delete);
                            transactionBookService.delete(transactionBook.getId());
                        }
                        Apartment apartment = loadedLease.getApartment();
                        if (leaseService.findAll().stream().noneMatch(l -> l.getApartment().equals(apartment))) {
                            apartmentService.delete(apartment.getId());
                        }
                    });
            });
    }

    private void deleteTransactions(Collection<? extends Transaction> transactions) {
        Set<Transaction> linkedTransactions = new HashSet<>();
        transactions.forEach(transaction -> {
            Set<Transaction> linked = transaction.getLefts();
            linkedTransactions.addAll(linked);
            delete(transaction);
        });
        linkedTransactions.forEach(this::delete);
    }

    private void save(Transaction transaction) {
        if (transaction instanceof InternalTransaction) {
            internalTransactionRepository.save((InternalTransaction) transaction);
        } else {
            bankTransactionRepository.save((BankTransaction) transaction);
        }
    }

    private void delete(Transaction transaction) {
        if (transaction instanceof InternalTransaction) {
            internalTransactionRepository.deleteById(transaction.getId());
        } else {
            bankTransactionRepository.deleteById(transaction.getId());
        }
    }
}
