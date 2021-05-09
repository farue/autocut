package de.farue.autocut.batch.fee;

import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.Transaction_;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.repository.InternalTransactionRepository;
import de.farue.autocut.service.ActivityService;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.accounting.BookingBuilder;
import de.farue.autocut.service.accounting.BookingTemplate;
import de.farue.autocut.utils.DateUtil;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class TenantFeeChargingBatchProcessor extends AbstractTenantFeeBatchProcessor {

    private InternalTransactionRepository transactionRepository;
    private ActivityService activityService;

    public TenantFeeChargingBatchProcessor(
        InternalTransactionRepository transactionRepository,
        LeaseService leaseService,
        ActivityService activityService,
        TenantFeeServiceQualifierDataMapper serviceQualifierDataMapper
    ) {
        super(leaseService, serviceQualifierDataMapper);
        this.transactionRepository = transactionRepository;
        this.activityService = activityService;
    }

    private Instant bookingDate = Instant.now();

    private Instant valueDate = Instant.now().plus(10, ChronoUnit.DAYS);

    private YearMonth chargePeriod = YearMonth.now();

    public Instant getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Instant getValueDate() {
        return valueDate;
    }

    public void setValueDate(Instant valueDate) {
        this.valueDate = valueDate;
    }

    public YearMonth getChargePeriod() {
        return chargePeriod;
    }

    public void setChargePeriod(YearMonth chargePeriod) {
        this.chargePeriod = chargePeriod;
    }

    @Value("#{jobParameters['chargeperiod'] ?: T(java.time.YearMonth).now().toString()}")
    public void setChargePeriod(String chargePeriodString) {
        this.chargePeriod = YearMonth.parse(chargePeriodString);
    }

    @Override
    protected List<BookingTemplate> doProcess(Lease lease, TransactionBook transactionBook) {
        LocalDate firstDateToCharge = findFirstDateToCharge(transactionBook)
            .orElseGet(
                () -> {
                    LocalDate leaseStart = lease.getStart();
                    LocalDate systemStart = LocalDate.of(2020, 4, 1);
                    return DateUtil.max(leaseStart, systemStart);
                }
            );

        LocalDate currentDateToCharge = chargePeriod.atDay(firstDateToCharge.getDayOfMonth());
        LocalDate leaseEnd = lease.getEnd();
        // #83: Don't charge if e.g. currentDateToCharge=01.05.2020 and leaseEnd=01.05.2020
        if (!currentDateToCharge.isBefore(leaseEnd)) {
            return null;
        }

        LocalDate lastDateToCharge = DateUtil.min(currentDateToCharge, leaseEnd);
        if (firstDateToCharge.isAfter(lastDateToCharge)) {
            return null;
        }

        // since the "until date" in datesUntil is exclusive, add a day
        return firstDateToCharge
            .datesUntil(lastDateToCharge.plusDays(1), Period.ofMonths(1))
            .map(
                chargeDate ->
                    BookingBuilder
                        .bookingTemplate()
                        .bookingDate(bookingDate)
                        .valueDate(valueDate)
                        .transactionTemplate(
                            BookingBuilder
                                .transactionTemplate()
                                .type(TransactionType.FEE)
                                .transactionBook(transactionBook)
                                .description(createDescription(chargeDate))
                                .issuer(ISSUER)
                                .serviceQualifier(
                                    createServiceQualifierData(chargeDate, activityService.isEligibleForDiscount(lease, chargeDate))
                                )
                                .value(activityService.getFeeValue(lease, chargeDate))
                                .build()
                        )
                        .build()
            )
            .collect(Collectors.toList());
    }

    private Optional<LocalDate> findFirstDateToCharge(TransactionBook transactionBook) {
        return findLastFeeCharge(transactionBook)
            .map(transaction -> serviceQualifierDataMapper.map(transaction.getServiceQulifier()))
            .map(TenantFeeServiceQualifierData::getChargeDate)
            .map(yearMonth -> yearMonth.plusMonths(1));
    }

    private Optional<InternalTransaction> findLastFeeCharge(TransactionBook transactionBook) {
        return transactionRepository
            .findAllByTransactionBookAndIssuer(
                transactionBook,
                ISSUER,
                PageRequest.of(0, 1, Sort.by(Order.desc(Transaction_.SERVICE_QULIFIER)))
            )
            .stream()
            .findFirst();
    }
}
