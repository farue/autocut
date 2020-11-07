package de.farue.autocut.batch.fee;

import static de.farue.autocut.utils.BigDecimalUtil.compare;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.google.common.base.Preconditions;

import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Transaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.Transaction_;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.repository.InternalTransactionRepository;
import de.farue.autocut.service.ActivityService;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.accounting.BookingBuilder;
import de.farue.autocut.service.accounting.BookingTemplate;

public class TenantFeeCorrectingBatchProcessor extends AbstractTenantFeeBatchProcessor {

    private final InternalTransactionRepository transactionRepository;
    private final ActivityService activityService;

    private Instant bookingDate = Instant.now();
    private Instant valueDateCredit = Instant.now();
    private Instant valueDateCharge = Instant.now().plus(10, ChronoUnit.DAYS);

    public TenantFeeCorrectingBatchProcessor(LeaseService leaseService, TenantFeeServiceQualifierDataMapper serviceQualifierDataMapper,
        InternalTransactionRepository transactionRepository, ActivityService activityService) {
        super(leaseService, serviceQualifierDataMapper);
        this.transactionRepository = transactionRepository;
        this.activityService = activityService;
    }

    public Instant getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Instant getValueDateCredit() {
        return valueDateCredit;
    }

    public void setValueDateCredit(Instant valueDateCredit) {
        this.valueDateCredit = valueDateCredit;
    }

    public Instant getValueDateCharge() {
        return valueDateCharge;
    }

    public void setValueDateCharge(Instant valueDateCharge) {
        this.valueDateCharge = valueDateCharge;
    }

    @Override
    protected List<BookingTemplate> doProcess(Lease lease, TransactionBook transactionBook) {
        // TODO: Restrict to current semester
        Page<InternalTransaction> feeTransactions = transactionRepository
            .findAllByTransactionBookAndIssuer(transactionBook, TenantFeeChargingBatchProcessor.ISSUER,
                PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.desc(Transaction_.SERVICE_QULIFIER))));
        Map<LocalDate, List<InternalTransaction>> transactionsByChargePeriod = groupByChargePeriod(feeTransactions);

        List<BookingTemplate> bookingTemplates = new ArrayList<>();
        for (Entry<LocalDate, List<InternalTransaction>> entry : transactionsByChargePeriod.entrySet()) {
            LocalDate chargeDate = entry.getKey();
            List<InternalTransaction> chargePeriodTransactions = entry.getValue();

            boolean isEligibleForDiscount = activityService.isEligibleForDiscount(lease, chargeDate);
            BigDecimal chargedFee = sumValues(chargePeriodTransactions);
            BigDecimal expectedFee = activityService.getFeeValue(lease, chargeDate);
            Preconditions.checkState(compare(chargedFee).isNegative(), "Charged fee has to be a negative value");
            Preconditions.checkState(compare(expectedFee).isNegative(), "Expected fee has to be a negative value");

            BigDecimal value = expectedFee.subtract(chargedFee);
            if (!compare(value).isZero()) {
                BookingTemplate bookingTemplate = BookingBuilder.bookingTemplate()
                    .bookingDate(bookingDate)
                    .valueDate(compare(value).isPositive() ? valueDateCredit : valueDateCharge)
                    .transactionTemplate(BookingBuilder.transactionTemplate()
                        .type(TransactionType.CORRECTION)
                        .transactionBook(transactionBook)
                        .description(createDescription(chargeDate))
                        .issuer(TenantFeeChargingBatchProcessor.ISSUER)
                        .value(value)
                        .serviceQualifier(createServiceQualifierData(chargeDate, isEligibleForDiscount))
                        .build())
                    .build();
                bookingTemplates.add(bookingTemplate);
            }
        }

        return bookingTemplates.isEmpty() ? null : bookingTemplates;
    }

    private Map<LocalDate, List<InternalTransaction>> groupByChargePeriod(Page<InternalTransaction> transactions) {
        return transactions.stream()
            .collect(Collectors.groupingBy(transaction -> serviceQualifierDataMapper.map(transaction.getServiceQulifier()).getChargeDate()));
    }

    private BigDecimal sumValues(Collection<InternalTransaction> transactions) {
        return transactions.stream().map(Transaction::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
