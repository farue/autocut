package de.farue.autocut.service.dto;

import de.farue.autocut.domain.InternalTransaction;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class TransactionsOverviewDTO {

    private BigDecimal balanceNow;

    private BigDecimal deposit;

    private List<InternalTransaction> transactions;
}
