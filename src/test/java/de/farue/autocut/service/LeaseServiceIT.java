package de.farue.autocut.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.domain.event.AbstractLeaseEvent;
import de.farue.autocut.domain.event.LeaseCreatedEvent;
import de.farue.autocut.domain.event.LeaseExpiredEvent;
import de.farue.autocut.domain.event.LeaseUpdatedEvent;
import de.farue.autocut.service.accounting.TransactionBookService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

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
        Lease lease = new Lease().start(LocalDate.of(2015, 10, 10)).end(LocalDate.of(2020, 9, 30)).nr("nr");
        this.lease = leaseService.save(lease);
    }

    @Nested
    @SpringBootTest(classes = AutocutApp.class)
    class GetCashTransactionBook {

        @Test
        @Transactional
        void testExistingTransactionBook() {
            TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.CASH);
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
            TransactionBook transactionBook = new TransactionBook().type(TransactionBookType.DEPOSIT);
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

    @Nested
    @SpringBootTest(classes = AutocutApp.class)
    @RecordApplicationEvents
    class Events {

        @Autowired
        private ApplicationEvents applicationEvents;

        @BeforeEach
        void setUp() {
            applicationEvents.clear();
        }

        @Test
        void testCreationShouldPublishEvent() {
            Lease newLease = new Lease().start(LocalDate.of(2010, 12, 12)).end(LocalDate.of(2020, 9, 30)).nr("nr2");
            newLease = leaseService.save(newLease);

            List<Lease> publishedLeases = applicationEvents
                .stream(LeaseCreatedEvent.class)
                .map(AbstractLeaseEvent::getLease)
                .filter(lease -> lease.getNr().equals("nr2"))
                .collect(Collectors.toList());
            assertThat(publishedLeases).hasSize(1);
            Lease publishedLease = publishedLeases.get(0);
            assertThat(publishedLease.getId()).isEqualTo(newLease.getId());

            assertThat(applicationEvents.stream(LeaseUpdatedEvent.class).count()).isZero();
        }

        @Test
        void testUpdateShouldPublishEvent() {
            lease.setNr("changedNr");
            Lease changedLease = leaseService.save(lease);

            List<Lease> publishedLeases = applicationEvents
                .stream(LeaseUpdatedEvent.class)
                .map(AbstractLeaseEvent::getLease)
                .filter(lease -> lease.getNr().equals("changedNr"))
                .collect(Collectors.toList());
            assertThat(publishedLeases).hasSize(1);
            Lease publishedLease = publishedLeases.get(0);
            assertThat(publishedLease.getId()).isEqualTo(changedLease.getId());

            assertThat(applicationEvents.stream(LeaseCreatedEvent.class).count()).isZero();
        }

        @Test
        void testExpiredLeasesShouldBeDetected() {
            Lease justExpiredLease = leaseService.save(
                new Lease().start(LocalDate.of(2010, 12, 12)).end(LocalDate.now()).nr("just expired")
            );
            Lease notExpiredLease = leaseService.save(
                new Lease().start(LocalDate.of(2010, 12, 12)).end(LocalDate.now().plusDays(7)).nr("just expired")
            );

            leaseService.leaseExpiredSchedule();

            List<LeaseExpiredEvent> events = applicationEvents.stream(LeaseExpiredEvent.class).collect(Collectors.toList());
            assertThat(events.stream().filter(ev -> ev.getLease().equals(lease))).hasSize(1);
            assertThat(events.stream().filter(ev -> ev.getLease().equals(justExpiredLease))).hasSize(1);
            assertThat(events.stream().filter(ev -> ev.getLease().equals(notExpiredLease))).isEmpty();
        }
    }
}
