package de.farue.autocut.batch.banking;

import de.farue.autocut.service.accounting.BankingService;
import java.util.List;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.springframework.batch.item.ItemReader;

public class BankingBatchReader implements ItemReader<UmsLine> {

    private final BankingService bankingService;

    private List<UmsLine> transactions;
    private int itemCount = 0;

    public BankingBatchReader(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @Override
    public UmsLine read() {
        if (transactions == null) {
            transactions = bankingService.getTransactions();
            if (transactions == null) {
                return null;
            }
        }

        if (transactions.size() <= itemCount) {
            return null;
        }

        return transactions.get(itemCount++);
    }
}
