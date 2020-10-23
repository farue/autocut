package de.farue.autocut.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.service.accounting.TransactionBookService;

@SpringBootTest(classes = AutocutApp.class)
class LeaseServiceIT {

    @Autowired
    private LeaseService leaseService;

    @Autowired
    private TransactionBookService transactionBookService;

    @Autowired
    private EntityManager entityManager;

    private Lease lease;

    @BeforeEach
    void setUp() {
        Lease lease = new Lease()
            .start(LocalDate.of(2015, 10, 10))
            .end(LocalDate.of(2020, 9, 30))
            .nr("nr");
        this.lease = leaseService.save(lease);
    }

    @Nested
    @SpringBootTest(classes = AutocutApp.class)
    class GetCashTransactionBook {

        @Test
        @Transactional
        void testExistingTransactionBook() {
            TransactionBook transactionBook = new TransactionBook()
                .type(TransactionBookType.CASH);
            transactionBookService.save(transactionBook);

            lease.addTransactionBook(transactionBook);
            leaseService.save(lease);

            TransactionBook loadedTransactionBook = leaseService.getCashTransactionBook(lease);
            assertThat(loadedTransactionBook).isEqualTo(transactionBook);

            // clear cache so we are getting fresh entities
            entityManager.flush();
            entityManager.clear();

            Lease loadedLease = leaseService.findOne(LeaseServiceIT.this.lease.getId()).get();
            assertThat(leaseService.getCashTransactionBook(loadedLease)).isEqualTo(transactionBook);
        }

        @Test
        @Transactional
        void testNoExistingTransactionBook() {
            TransactionBook transactionBook = leaseService.getCashTransactionBook(lease);
            assertThat(transactionBook.getType()).isEqualTo(TransactionBookType.CASH);

            // clear cache so we are getting fresh entities
            entityManager.flush();
            entityManager.clear();

            Lease loadedLease = leaseService.findOne(LeaseServiceIT.this.lease.getId()).get();
            assertThat(leaseService.getCashTransactionBook(loadedLease)).isEqualTo(transactionBook);
        }
    }

    @Nested
    @SpringBootTest(classes = AutocutApp.class)
    class GetDepositTransactionBook {

        @Test
        @Transactional
        void testExistingTransactionBook() {
            TransactionBook transactionBook = new TransactionBook()
                .type(TransactionBookType.DEPOSIT);
            transactionBookService.save(transactionBook);

            lease.addTransactionBook(transactionBook);
            leaseService.save(lease);

            TransactionBook loadedTransactionBook = leaseService.getDepositTransactionBook(lease);
            assertThat(loadedTransactionBook).isEqualTo(transactionBook);

            // clear cache so we are getting fresh entities
            entityManager.flush();
            entityManager.clear();

            Lease loadedLease = leaseService.findOne(LeaseServiceIT.this.lease.getId()).get();
            assertThat(leaseService.getDepositTransactionBook(loadedLease)).isEqualTo(transactionBook);
        }

        @Test
        @Transactional
        void testNoExistingTransactionBook() {
            TransactionBook transactionBook = leaseService.getDepositTransactionBook(lease);
            assertThat(transactionBook.getType()).isEqualTo(TransactionBookType.DEPOSIT);

            // clear cache so we are getting fresh entities
            entityManager.flush();
            entityManager.clear();

            Lease loadedLease = leaseService.findOne(LeaseServiceIT.this.lease.getId()).get();
            assertThat(leaseService.getDepositTransactionBook(loadedLease)).isEqualTo(transactionBook);
        }
    }

}
