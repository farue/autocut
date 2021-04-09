package de.farue.autocut.batch.banking;

import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.service.accounting.BankTransactionService;
import java.util.List;
import org.springframework.batch.item.ItemWriter;

public class BankingBatchWriter implements ItemWriter<BankTransaction> {

    private final BankTransactionService transactionService;

    public BankingBatchWriter(BankTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void write(List<? extends BankTransaction> bankTransactions) {
        for (BankTransaction bankTransaction : bankTransactions) {
            transactionService.saveWithContraTransaction(bankTransaction);
        }
    }
}
