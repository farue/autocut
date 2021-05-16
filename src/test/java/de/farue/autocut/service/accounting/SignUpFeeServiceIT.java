package de.farue.autocut.service.accounting;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.IntegrationTest;
import de.farue.autocut.domain.*;
import de.farue.autocut.domain.enumeration.ApartmentTypes;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.service.ApartmentService;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.TenantService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@IntegrationTest
class SignUpFeeServiceIT {

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private LeaseService leaseService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private InternalTransactionService transactionService;

    @Autowired
    private EntityManager entityManager;

    @Nested
    class ShouldChargeSignupFee {

        @Test
        void newTenantNoExistingSignupFeeBooking() {
            Apartment apartment = new Apartment().nr("nr").type(ApartmentTypes.SINGLE).maxNumberOfLeases(1);
            apartment = apartmentService.save(apartment);
            Lease lease = new Lease().start(LocalDate.of(2015, 10, 10)).end(LocalDate.of(2020, 9, 30)).nr("nr").apartment(apartment);
            lease = leaseService.save(lease);
            Tenant tenant = new Tenant().firstName("Bob").lastName("Miller").lease(lease);
            tenant = tenantService.save(tenant);
            entityManager.detach(tenant);

            tenant.setVerified(true);
            // should trigger SignUpFeeService
            tenant = tenantService.save(tenant);

            TransactionBook transactionBook = leaseService.getCashTransactionBook(lease);
            List<InternalTransaction> transactions = transactionService
                .findAllForTransactionBook(transactionBook, Pageable.unpaged())
                .getContent();
            assertThat(transactions).hasSize(1);
            InternalTransaction transaction = transactions.get(0);
            assertThat(transaction.getTransactionType()).isEqualTo(TransactionType.FEE);
            assertThat(transaction.getDescription()).isEqualTo("i18n{transaction.descriptions.signupFee}");
            assertThat(transaction.getValue()).isEqualByComparingTo("-50");
        }

        @Test
        void newTenantInBackupApartment() {
            Apartment apartment = new Apartment().nr("nr").type(ApartmentTypes.BACKUP).maxNumberOfLeases(1);
            apartment = apartmentService.save(apartment);
            Lease lease = new Lease().start(LocalDate.of(2015, 10, 10)).end(LocalDate.of(2020, 9, 30)).nr("nr").apartment(apartment);
            lease = leaseService.save(lease);
            Tenant tenant = new Tenant().firstName("Bob").lastName("Miller").lease(lease);
            tenant = tenantService.save(tenant);
            entityManager.detach(tenant);

            tenant.setVerified(true);
            // should trigger SignUpFeeService
            tenant = tenantService.save(tenant);

            TransactionBook transactionBook = leaseService.getCashTransactionBook(lease);
            List<InternalTransaction> transactions = transactionService
                .findAllForTransactionBook(transactionBook, Pageable.unpaged())
                .getContent();
            assertThat(transactions).hasSize(1);
            InternalTransaction transaction = transactions.get(0);
            assertThat(transaction.getTransactionType()).isEqualTo(TransactionType.FEE);
            assertThat(transaction.getDescription()).isEqualTo("i18n{transaction.descriptions.signupFee}");
            assertThat(transaction.getValue()).isEqualByComparingTo("0");
        }
    }

    @Nested
    class ShouldNotChargeSignupFee {

        @Test
        void newTenantExistingSignupFeeBooking() {
            Apartment apartment = new Apartment().nr("nr").type(ApartmentTypes.SINGLE).maxNumberOfLeases(1);
            apartment = apartmentService.save(apartment);
            Lease lease = new Lease().start(LocalDate.of(2015, 10, 10)).end(LocalDate.of(2020, 9, 30)).nr("nr").apartment(apartment);
            lease = leaseService.save(lease);
            Tenant tenant = new Tenant().firstName("Bob").lastName("Miller").lease(lease);
            tenant = tenantService.save(tenant);
            entityManager.detach(tenant);

            TransactionBook transactionBook = leaseService.getCashTransactionBook(lease);
            InternalTransaction existingSignUpFeeTransaction = new InternalTransaction()
                .transactionBook(transactionBook)
                .transactionType(TransactionType.FEE)
                .bookingDate(Instant.now())
                .valueDate(Instant.now())
                .value(new BigDecimal("-1"))
                .balanceAfter(new BigDecimal("-1"))
                .issuer(SignUpFeeService.ISSUER);
            transactionService.save(existingSignUpFeeTransaction);

            tenant.setVerified(true);
            // should trigger SignUpFeeService
            tenant = tenantService.save(tenant);

            List<InternalTransaction> transactions = transactionService
                .findAllForTransactionBook(transactionBook, Pageable.unpaged())
                .getContent();
            assertThat(transactions).hasSize(1);
            InternalTransaction transaction = transactions.get(0);
            assertThat(transaction.getTransactionType()).isEqualTo(TransactionType.FEE);
            assertThat(transaction.getValue()).isEqualByComparingTo("-1");
        }
    }
}
