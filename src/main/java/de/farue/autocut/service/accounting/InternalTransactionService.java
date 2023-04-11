package de.farue.autocut.service.accounting;

import static de.farue.autocut.utils.BigDecimalUtil.compare;

import de.farue.autocut.domain.*;
import de.farue.autocut.domain.enumeration.TransactionType;
import de.farue.autocut.domain.event.BalanceChangeToNegativeEvent;
import de.farue.autocut.domain.event.BalanceChangeToPositiveEvent;
import de.farue.autocut.domain.event.InternalTransactionCreatedEvent;
import de.farue.autocut.domain.event.InternalTransactionEffectiveEvent;
import de.farue.autocut.repository.InternalTransactionRepository;
import de.farue.autocut.repository.TransactionRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.InsufficientFundsException;
import de.farue.autocut.service.ScheduledJobService;
import de.farue.autocut.service.TransactionService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class InternalTransactionService extends TransactionService<InternalTransaction> {

    private static final String TRANSACTION_EFFECTIVE_JOB_NAME = "transactionEffectiveSchedule";
    private static final LocalDate MIN_DATE = LocalDate.of(1970, 1, 1);

    private final Logger log = LoggerFactory.getLogger(InternalTransactionService.class);

    private final InternalTransactionRepository transactionRepository;
    private final InternalBookingContraTransactionProvider internalBookingContraTransactionProvider;
    private final ApplicationEventPublisher publisher;
    private final ScheduledJobService scheduledJobService;

    public InternalTransactionService(
        InternalTransactionRepository transactionRepository,
        TransactionBookService transactionBookService,
        InternalBookingContraTransactionProvider internalBookingContraTransactionProvider,
        ApplicationEventPublisher publisher,
        ScheduledJobService scheduledJobService
    ) {
        super(transactionBookService);
        this.transactionRepository = transactionRepository;
        this.internalBookingContraTransactionProvider = internalBookingContraTransactionProvider;
        this.publisher = publisher;
        this.scheduledJobService = scheduledJobService;
    }

    @Override
    protected TransactionRepository<InternalTransaction> getRepository() {
        return transactionRepository;
    }

    @Override
    public InternalTransaction save(InternalTransaction transaction) {
        validate(transaction);
        return super.save(transaction);
    }

    public void saveWithContraTransaction(BookingTemplate bookingTemplate) {
        saveBooking(bookingTemplate, internalBookingContraTransactionProvider);
    }

    public void reverse(Long id) {
        findOne(id).ifPresent(this::reverse);
    }

    public void reverse(InternalTransaction transaction) {
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.EDIT_TRANSACTIONS)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Instant now = Instant.now();
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        transactions.addAll(transaction.getRights());
        List<InternalTransaction> reversedTransactions = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t instanceof InternalTransaction) {
                Optional<InternalTransaction> existingReverseTransaction = transactionRepository.findOneByTransactionBookAndDescription(
                    t.getTransactionBook(),
                    createReverseTransactionDescription(t)
                );
                if (existingReverseTransaction.isPresent()) {
                    throw new RuntimeException("Reverse transaction already exists: " + existingReverseTransaction.get());
                }

                InternalTransaction reverseTransaction = new InternalTransaction()
                    .transactionBook(t.getTransactionBook())
                    .issuer("TransactionService")
                    .transactionType(TransactionType.CORRECTION)
                    .bookingDate(now)
                    .valueDate(now)
                    .value(t.getValue().negate())
                    .description("i18n{transaction.descriptions.reverse} #" + t.getId());
                reversedTransactions.add(reverseTransaction);
            } else if (t instanceof BankTransaction) {
                throw new RuntimeException("Reversal of bank transactions not supported.");
            }
        }

        for (int i = 0; i < reversedTransactions.size(); i++) {
            for (int j = i + 1; j < reversedTransactions.size(); j++) {
                reversedTransactions.get(i).link(reversedTransactions.get(j));
            }
        }

        reversedTransactions.forEach(this::setBalanceAfter);
        reversedTransactions.forEach(this::updateBalanceInLaterTransactions);

        transactionRepository.saveAll(reversedTransactions);

        reversedTransactions.stream().map(InternalTransactionCreatedEvent::new).forEach(publisher::publishEvent);
    }

    // Fired every 10 minutes
    @Scheduled(cron = "0 */10 * * * ?")
    public void transactionEffectiveSchedule() {
        long jobId = this.scheduledJobService.createNewScheduledJob(TRANSACTION_EFFECTIVE_JOB_NAME);
        Instant dataStartTime =
            this.scheduledJobService.findLastCompletedScheduledJob(TRANSACTION_EFFECTIVE_JOB_NAME)
                .map(ScheduledJob::getDataEndTime)
                .orElse(MIN_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Instant dataEndTime = Instant.now();
        this.scheduledJobService.setDataStartTime(jobId, dataStartTime);
        this.scheduledJobService.setDataEndTime(jobId, dataEndTime);

        List<InternalTransaction> newEffectiveTransactions = this.getRepository().findAllByValueDateBetween(dataStartTime, dataEndTime);
        if (!newEffectiveTransactions.isEmpty()) {
            log.debug("Found new effective transactions. Firing InternalTransactionEffectiveEvent for: {}", newEffectiveTransactions);
        } else {
            log.debug("Found no new effective transactions.");
        }

        this.scheduledJobService.setJobRunning(jobId);
        newEffectiveTransactions.stream().map(InternalTransactionEffectiveEvent::new).forEach(this.publisher::publishEvent);
        this.scheduledJobService.setJobCompleted(jobId);
    }

    @EventListener
    public void fireBalanceChangeSignEvent(InternalTransactionEffectiveEvent e) {
        InternalTransaction transaction = e.getTransaction();
        BigDecimal balance = transaction.getBalanceAfter();
        this.findTransactionImmediatelyBefore(transaction)
            .ifPresent(previousTransaction -> {
                BigDecimal previousBalance = previousTransaction.getBalanceAfter();
                BigDecimal currentBalance = transactionBookService.getCurrentBalance(transaction.getTransactionBook());
                if (compare(previousBalance).isNegative() && compare(balance).isPositive()) {
                    BalanceChangeToPositiveEvent event = new BalanceChangeToPositiveEvent(
                        previousTransaction,
                        transaction,
                        compare(currentBalance).isNegative()
                    );
                    log.debug("Balance changed to positive. Firing {}", event);
                    this.publisher.publishEvent(event);
                } else if (compare(previousBalance).isPositive() && compare(balance).isNegative()) {
                    BalanceChangeToNegativeEvent event = new BalanceChangeToNegativeEvent(
                        previousTransaction,
                        transaction,
                        compare(currentBalance).isNegative()
                    );
                    log.debug("Balance changed to negative. Firing {}", event);
                    this.publisher.publishEvent(event);
                }
            });
    }

    @Override
    protected void partialUpdate(InternalTransaction existingTransaction, InternalTransaction transaction) {
        if (transaction.getIssuer() != null) {
            existingTransaction.setIssuer(transaction.getIssuer());
        }
        if (transaction.getRecipient() != null) {
            existingTransaction.setRecipient(transaction.getRecipient());
        }
    }

    private void saveBooking(BookingTemplate bookingTemplate, InternalBookingContraTransactionProvider contraTransactionsProvider) {
        validate(bookingTemplate);

        List<InternalTransaction> bookingTransactions = mapToTransactions(bookingTemplate);
        List<InternalTransaction> contraTransactions = contraTransactionsProvider.calculateContraTransactions(
            bookingTransactions,
            bookingTemplate.getBookingDate(),
            bookingTemplate.getValueDate()
        );

        List<InternalTransaction> transactions = new ArrayList<>();
        transactions.addAll(bookingTransactions);
        transactions.addAll(contraTransactions);

        for (int i = 0; i < transactions.size(); i++) {
            for (int j = i + 1; j < transactions.size(); j++) {
                transactions.get(i).link(transactions.get(j));
            }
        }

        transactions.forEach(this::setBalanceAfter);
        bookingTransactions.forEach(this::checkBalances);
        transactions.forEach(this::updateBalanceInLaterTransactions);

        transactionRepository.saveAll(transactions);

        transactions.stream().map(InternalTransactionCreatedEvent::new).forEach(publisher::publishEvent);
    }

    private void checkBalances(InternalTransaction transaction) {
        boolean illegalBalance =
            switch (transaction.getTransactionType()) {
                case DEBIT, PURCHASE, TRANSFER -> compare(transaction.getValue()).isNegative() &&
                compare(transaction.getBalanceAfter()).isNegative();
                default -> false;
            };
        if (illegalBalance) {
            throw new InsufficientFundsException();
        }
    }

    private void setBalanceAfter(InternalTransaction transaction) {
        transactionBookService.setBalanceAfter(transaction);
    }

    private List<InternalTransaction> mapToTransactions(BookingTemplate bookingTemplate) {
        return bookingTemplate
            .getTransactionTemplates()
            .stream()
            .map(transactionTemplate ->
                new InternalTransaction()
                    .transactionType(transactionTemplate.getType())
                    .bookingDate(bookingTemplate.getBookingDate())
                    .valueDate(bookingTemplate.getValueDate())
                    .value(transactionTemplate.getValue())
                    .transactionBook(transactionTemplate.getTransactionBook())
                    .description(transactionTemplate.getDescription())
                    .issuer(transactionTemplate.getIssuer())
                    .serviceQulifier(transactionTemplate.getServiceQualifier())
                    .recipient(transactionTemplate.getRecipient())
            )
            .collect(Collectors.toList());
    }

    private void validate(InternalTransaction transaction) {
        if (transaction.getTransactionType() == TransactionType.CREDIT) {
            if (compare(transaction.getValue()).isNegative()) {
                throw new IllegalArgumentException(
                    String.format(
                        "InternalTransaction type %s requires the value not to be negative, but was %s",
                        transaction.getTransactionType(),
                        transaction.getValue()
                    )
                );
            }
        } else if (
            transaction.getTransactionType() == TransactionType.DEBIT ||
            transaction.getTransactionType() == TransactionType.FEE ||
            transaction.getTransactionType() == TransactionType.PURCHASE
        ) {
            if (compare(transaction.getValue()).isPositive()) {
                throw new IllegalArgumentException(
                    String.format(
                        "InternalTransaction type %s requires the value not to be positive, but was %s",
                        transaction.getTransactionType(),
                        transaction.getValue()
                    )
                );
            }
        }
    }

    private void validate(BookingTemplate bookingTemplate) {
        // assert no multiple transactions per transaction book
        Set<TransactionBook> transactionTemplateSet = new HashSet<>();
        for (TransactionTemplate transactionTemplate : bookingTemplate.getTransactionTemplates()) {
            if (!transactionTemplateSet.add(transactionTemplate.getTransactionBook())) {
                throw new IllegalArgumentException(
                    "Booking template contains multiple transactions for transaction book " + transactionTemplate.getTransactionBook()
                );
            }
        }

        // assert transaction kind and value match
        for (TransactionTemplate transactionTemplate : bookingTemplate.getTransactionTemplates()) {
            if (transactionTemplate.getType() == TransactionType.CREDIT) {
                if (compare(transactionTemplate.getValue()).isNegative()) {
                    throw new IllegalArgumentException(
                        String.format(
                            "InternalTransaction type %s requires the value not to be negative, but was %s",
                            transactionTemplate.getType(),
                            transactionTemplate.getValue()
                        )
                    );
                }
            } else if (
                transactionTemplate.getType() == TransactionType.DEBIT ||
                transactionTemplate.getType() == TransactionType.FEE ||
                transactionTemplate.getType() == TransactionType.PURCHASE
            ) {
                if (compare(transactionTemplate.getValue()).isPositive()) {
                    throw new IllegalArgumentException(
                        String.format(
                            "InternalTransaction type %s requires the value not to be positive, but was %s",
                            transactionTemplate.getType(),
                            transactionTemplate.getValue()
                        )
                    );
                }
            }
        }
    }

    private String createReverseTransactionDescription(Transaction transaction) {
        return "i18n{transaction.descriptions.reverse} #" + transaction.getId();
    }
}
