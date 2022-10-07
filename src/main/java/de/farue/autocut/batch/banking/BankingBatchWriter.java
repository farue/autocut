package de.farue.autocut.batch.banking;

import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.service.accounting.BankTransactionService;
import de.farue.autocut.utils.BigDecimalUtil;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.batch.item.ItemWriter;

public class BankingBatchWriter implements ItemWriter<BankTransaction> {

    private static final Pattern INVOICE_PATTERN = Pattern.compile(".*(ER|EB|Eig|EZ)20\\d{2}-\\d{3}.*");

    private final BankTransactionService transactionService;

    public BankingBatchWriter(BankTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void write(List<? extends BankTransaction> bankTransactions) {
        for (BankTransaction bankTransaction : bankTransactions) {
            if (
                BigDecimalUtil.compare(bankTransaction.getValue()).isNegative() &&
                bankTransaction.getDescription() != null &&
                INVOICE_PATTERN.matcher(bankTransaction.getDescription()).matches()
            ) {
                transactionService.saveWithoutContraTransaction(bankTransaction);
            } else {
                transactionService.saveWithContraTransaction(bankTransaction);
            }
        }
    }
}
