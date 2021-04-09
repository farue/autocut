package de.farue.autocut.batch.banking;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import de.farue.autocut.domain.BankAccount;
import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.repository.BankAccountRepository;
import de.farue.autocut.service.accounting.BankTransactionService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.structures.Konto;
import org.springframework.batch.item.ItemProcessor;

public class BankingBatchProcessor implements ItemProcessor<UmsLine, BankTransaction> {

    private final BankAccountRepository bankAccountRepository;
    private final BankTransactionService bankTransactionService;

    private final Map<BankTransaction, Integer> transactionCounterMap = new HashMap<>();

    private BankAccount referenceAccount;
    private TransactionBook referenceTransactionBook;

    public BankingBatchProcessor(BankAccountRepository bankAccountRepository, BankTransactionService bankTransactionService) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankTransactionService = bankTransactionService;
    }

    public BankAccount getReferenceAccount() {
        return referenceAccount;
    }

    public void setReferenceAccount(BankAccount referenceAccount) {
        this.referenceAccount = referenceAccount;
    }

    public TransactionBook getReferenceTransactionBook() {
        return referenceTransactionBook;
    }

    public void setReferenceTransactionBook(TransactionBook referenceTransactionBook) {
        this.referenceTransactionBook = referenceTransactionBook;
    }

    @Override
    public BankTransaction process(UmsLine transaction) {
        Preconditions.checkNotNull(referenceAccount);
        Preconditions.checkNotNull(referenceTransactionBook);

        BankTransaction bankTransaction = new BankTransaction()
            .bookingDate(transaction.bdate.toInstant())
            .valueDate(transaction.valuta.toInstant())
            .value(transaction.value.getBigDecimalValue())
            .balanceAfter(transaction.saldo.value.getBigDecimalValue())
            .description(Joiner.on(" ").skipNulls().join(transaction.usage))
            .customerRef(transaction.customerref)
            .gvCode(transaction.gvcode)
            .type(transaction.text)
            .endToEnd(transaction.endToEndId)
            .primanota(transaction.primanota)
            .creditor(transaction.id)
            .bankAccount(referenceAccount)
            .contraBankAccount(getContraBankAccount(transaction).orElse(null))
            .transactionBook(referenceTransactionBook);

        transactionCounterMap.computeIfAbsent(bankTransaction, bankTransactionService::countExisting);
        int count = transactionCounterMap.get(bankTransaction);
        if (count > 0) {
            // a transaction with the same characteristics already exists, so just decrement the counter and skip this transaction
            transactionCounterMap.put(bankTransaction, count - 1);
            return null;
        } else {
            return bankTransaction;
        }
    }

    private Optional<BankAccount> getContraBankAccount(UmsLine transaction) {
        Konto contraKonto = transaction.other;

        if (contraKonto == null) {
            return Optional.empty();
        }

        String iban = StringUtils.deleteWhitespace(contraKonto.iban);
        String bic = StringUtils.deleteWhitespace(contraKonto.bic);
        String name = Joiner.on(" ").skipNulls().join(contraKonto.name, contraKonto.name2);

        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(iban) || StringUtils.isEmpty(bic)) {
            return Optional.empty();
        }

        return Optional.of(
            bankAccountRepository
                .findFirstByIban(iban)
                .orElseGet(() -> bankAccountRepository.save(new BankAccount().name(name).iban(iban).bic(bic)))
        );
    }
}
