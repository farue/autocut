package de.farue.autocut.service;

import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.enumeration.TransactionKind;
import de.farue.autocut.repository.LeaseRepository;
import de.farue.autocut.repository.TransactionRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AutocutApp.class)
@Transactional
public class TransactionServiceIT {

    public static final String ANY_ISSUER = "issuer";
    public static final String ANY_LEASE_NR = "leaseNr";
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LeaseRepository leaseRepository;

    @Nested
    @SpringBootTest(classes = AutocutApp.class)
    @Transactional
    class GivenFutureTransactions {

        @Test
        void shouldUpdateBalance() {
            Lease lease = new Lease();
            lease.setNr(ANY_LEASE_NR);
            lease.setStart(todayPlusDays(-5));
            lease.setBlocked(false);

            Transaction t1 = new Transaction();
            t1.setKind(TransactionKind.FEE);
            t1.setBookingDate(todayPlusDays(-3));
            t1.setValueDate(todayPlusDays(0));
            t1.setValue(BigDecimal.valueOf(-10));
            t1.setIssuer(ANY_ISSUER);
            t1.setLease(lease);

            Transaction t2 = new Transaction();
            t2.setKind(TransactionKind.CREDIT);
            t2.setBookingDate(todayPlusDays(-2));
            t2.setValueDate(todayPlusDays(1));
            t2.setValue(BigDecimal.valueOf(+100));
            t2.setIssuer(ANY_ISSUER);
            t2.setLease(lease);

            Transaction t3 = new Transaction();
            t3.setKind(TransactionKind.CREDIT);
            t3.setBookingDate(todayPlusDays(-1));
            t3.setValueDate(todayPlusDays(-1));
            t3.setValue(BigDecimal.valueOf(+20));
            t3.setIssuer(ANY_ISSUER);
            t3.setLease(lease);

            Transaction t4 = new Transaction();
            t4.setKind(TransactionKind.CREDIT);
            t4.setBookingDate(todayPlusDays(0));
            t4.setValueDate(todayPlusDays(0));
            t4.setValue(BigDecimal.valueOf(+0.1));
            t4.setIssuer(ANY_ISSUER);
            t4.setLease(lease);

            leaseRepository.saveAndFlush(lease);
            transactionRepository.save(t1);
            transactionRepository.save(t2);
            transactionRepository.save(t3);
            transactionRepository.save(t4);
            transactionRepository.flush();

            assertThat(transactionService.getCurrentBalance(lease)).isEqualTo("10.1");
        }
    }

    private static Instant todayPlusDays(int days) {
        return Instant.now().truncatedTo(ChronoUnit.DAYS).plus(days, ChronoUnit.DAYS);
    }
}
