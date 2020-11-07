package de.farue.autocut.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.domain.LaundryMachineProgram;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.Transaction_;
import de.farue.autocut.domain.enumeration.LaundryMachineType;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.repository.LaundryMachineProgramRepository;
import de.farue.autocut.service.accounting.InternalTransactionService;

@SpringBootTest(classes = AutocutApp.class)
class LaundryMachineServiceIT {

    private static final String ANY_MACHINE_NAME = "Any Machine 11";
    private static final int ANY_MACHINE_IDENTIFIER = 11;

    @Autowired
    private LaundryMachineService laundryMachineService;

    @Autowired
    private LaundryMachineProgramRepository laundryMachineProgramRepository;

    @Autowired
    private LeaseService leaseService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private InternalTransactionService internalTransactionService;

    @MockBean
    private WashItClientMock washItClientMock;

    private LaundryMachine machine;
    private LaundryMachineProgram program;
    private Lease lease;
    private Tenant tenant;

    @BeforeEach
    void setUp() {
        LaundryMachine machine = new LaundryMachine()
            .type(LaundryMachineType.WASHING_MACHINE)
            .enabled(true)
            .identifier("" + ANY_MACHINE_IDENTIFIER)
            .name(ANY_MACHINE_NAME);
        this.machine = laundryMachineService.save(machine);

        LaundryMachineProgram program = new LaundryMachineProgram()
            .laundryMachine(machine)
            .name("Program")
            .subprogram("Subprogram")
            .time(10)
            .spin(1000)
            .preWash(false);
        this.program = laundryMachineProgramRepository.save(program);

        Lease lease = new Lease()
            .nr("no")
            .start(LocalDate.now())
            .end(LocalDate.now());
        this.lease = leaseService.save(lease);

        Tenant tenant = new Tenant()
            .firstName("Bob")
            .lastName("Miller")
            .lease(lease);
        this.tenant = tenantService.save(tenant);
    }

    @Test
    @Transactional
    void testSufficientFunds() {
        TransactionBook transactionBook = leaseService.getCashTransactionBook(lease);
        InternalTransaction transaction = new InternalTransaction()
            .bookingDate(Instant.now())
            .valueDate(Instant.now())
            .transactionType(TransactionType.CREDIT)
            .value(new BigDecimal("5"))
            .transactionBook(transactionBook)
            .issuer("test")
            .description("test");
        internalTransactionService.save(transaction);

        laundryMachineService.purchaseAndUnlock(tenant, machine, program);

        List<InternalTransaction> transactions = internalTransactionService
            .findAllForTransactionBook(transactionBook, PageRequest.of(0, 1, Sort.by(Order.desc(Transaction_.VALUE_DATE), Order.desc(Transaction_.ID))))
            .getContent();
        InternalTransaction washTransaction = transactions.get(0);

        assertThat(washTransaction.getValue()).isEqualByComparingTo("-0.80");
        assertThat(washTransaction.getDescription()).isEqualTo(ANY_MACHINE_NAME);
        verify(washItClientMock).activate(ANY_MACHINE_IDENTIFIER);
    }

    @Test
    @Transactional
    void testInsufficientFunds() {
        assertThatExceptionOfType(InsufficientFundsException.class).isThrownBy(() -> laundryMachineService.purchaseAndUnlock(tenant, machine, program));
    }

    @Test
    @Transactional
    void testMachineDisabled() {
        machine.setEnabled(false);
        laundryMachineService.save(machine);

        TransactionBook transactionBook = leaseService.getCashTransactionBook(lease);
        InternalTransaction transaction = new InternalTransaction()
            .bookingDate(Instant.now())
            .valueDate(Instant.now())
            .transactionType(TransactionType.CREDIT)
            .value(new BigDecimal("5"))
            .transactionBook(transactionBook)
            .issuer("test")
            .description("test");
        internalTransactionService.save(transaction);

        assertThatExceptionOfType(LaundryMachineUnavailableException.class).isThrownBy(() -> laundryMachineService.purchaseAndUnlock(tenant, machine, program));
    }
}
