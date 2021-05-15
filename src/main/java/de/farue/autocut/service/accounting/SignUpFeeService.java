package de.farue.autocut.service.accounting;

import static de.farue.autocut.utils.BigDecimalUtil.modify;

import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.ApartmentTypes;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.domain.event.TenantVerifiedEvent;
import de.farue.autocut.repository.InternalTransactionRepository;
import de.farue.autocut.service.GlobalSettingService;
import de.farue.autocut.service.LeaseService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class SignUpFeeService {

    public static final String ISSUER = "SignupFeeService";
    public static final String FEE_KEY_PREFIX = "signup-fee";

    private final Logger log = LoggerFactory.getLogger(SignUpFeeService.class);

    private final InternalTransactionRepository transactionRepository;
    private final InternalTransactionService transactionService;
    private final LeaseService leaseService;
    private final GlobalSettingService globalSettingService;

    public SignUpFeeService(
        InternalTransactionRepository transactionRepository,
        InternalTransactionService transactionService,
        LeaseService leaseService,
        GlobalSettingService globalSettingService
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
        this.leaseService = leaseService;
        this.globalSettingService = globalSettingService;
    }

    @EventListener
    public void handleTenantVerified(TenantVerifiedEvent event) {
        Tenant tenant = event.getTenant();
        Lease lease = tenant.getLease();
        if (lease == null) {
            log.warn("Sign up fee cannot be charged because tenant has no associated lease: {}", tenant);
            return;
        }

        TransactionBook transactionBook = leaseService.getCashTransactionBook(lease);
        findFeeCharge(transactionBook)
            .ifPresentOrElse(
                feeCharge -> {
                    log.debug("Sign up fee has already been charged for tenant {}: {}", tenant, feeCharge);
                },
                () -> {
                    ApartmentTypes apartmentType = lease.getApartment().getType();

                    Instant bookingDate = Instant.now();
                    Instant valueDate = bookingDate.plus(10, ChronoUnit.DAYS);
                    BookingTemplate bookingTemplate = BookingBuilder
                        .bookingTemplate()
                        .bookingDate(bookingDate)
                        .valueDate(valueDate)
                        .transactionTemplate(
                            BookingBuilder
                                .transactionTemplate()
                                .type(TransactionType.FEE)
                                .transactionBook(transactionBook)
                                .issuer(ISSUER)
                                .description("i18n{transaction.descriptions.signupFee}")
                                .value(getFeeValue(apartmentType))
                                .build()
                        )
                        .build();
                    transactionService.saveWithContraTransaction(bookingTemplate);
                }
            );
    }

    private BigDecimal getFeeValue(ApartmentTypes apartmentTypes) {
        BigDecimal feeValue = globalSettingService.getValue(FEE_KEY_PREFIX + "." + apartmentTypes.name());
        return modify(feeValue).negative();
    }

    private Optional<InternalTransaction> findFeeCharge(TransactionBook transactionBook) {
        return transactionRepository.findAllByTransactionBookAndIssuer(transactionBook, ISSUER, PageRequest.of(0, 1)).stream().findFirst();
    }
}
