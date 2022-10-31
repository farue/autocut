package de.farue.autocut.batch.reward;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.RewardPayout;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.repository.RewardPayoutRepository;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.accounting.BookingBuilder;
import de.farue.autocut.service.accounting.InternalTransactionService;
import de.farue.autocut.service.accounting.TransactionBookService;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;

public class RewardWriter implements ItemWriter<RewardPayout> {

    public static final String ISSUER = "RewardService";

    private final RewardPayoutRepository rewardPayoutRepository;
    private final TransactionBookService transactionBookService;
    private final LeaseService leaseService;
    private final InternalTransactionService internalTransactionService;

    private Instant timestamp;

    public RewardWriter(
        RewardPayoutRepository rewardPayoutRepository,
        TransactionBookService transactionBookService,
        LeaseService leaseService,
        InternalTransactionService internalTransactionService
    ) {
        this.rewardPayoutRepository = rewardPayoutRepository;
        this.transactionBookService = transactionBookService;
        this.leaseService = leaseService;
        this.internalTransactionService = internalTransactionService;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Value("#{jobParameters['run.timestamp']}")
    public void setTimestamp(String str) {
        this.timestamp = Instant.parse(str);
    }

    @Override
    public void write(List<? extends RewardPayout> items) throws Exception {
        rewardPayoutRepository.saveAll(items);
        items
            .stream()
            .map(payout -> {
                Tenant member = payout.getTimesheet().getMember();
                Lease lease = member.getLease();
                TransactionBook transactionBook = leaseService.getCashTransactionBook(lease);

                return BookingBuilder
                    .bookingTemplate()
                    .bookingDate(timestamp)
                    .valueDate(timestamp)
                    .transactionTemplate(
                        BookingBuilder
                            .transactionTemplate()
                            .type(TransactionType.CREDIT)
                            .transactionBook(transactionBook)
                            .description(createDescription(payout.getTime()))
                            .issuer(ISSUER)
                            .value(payout.getAmount())
                            .build()
                    )
                    .build();
            })
            .forEach(internalTransactionService::saveWithContraTransaction);
    }

    private String createDescription(int time) {
        Duration duration = Duration.of(time, ChronoUnit.SECONDS);
        return "i18n{transaction.descriptions.reward} " + String.format("%d:%02d", duration.toHours(), duration.toMinutesPart());
    }
}
