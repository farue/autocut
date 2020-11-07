package de.farue.autocut.service.accounting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;

public class BankingServiceMock extends BankingService {

    private BigDecimal currentBalance = new BigDecimal("0");
    private List<UmsLine> transactions = new ArrayList<>();

    public BankingServiceMock() {
        super(null);
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public void setTransactions(List<UmsLine> transactions) {
        this.transactions = transactions;
    }

    @Override
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    @Override
    public List<UmsLine> getTransactions() {
        return transactions;
    }
}
