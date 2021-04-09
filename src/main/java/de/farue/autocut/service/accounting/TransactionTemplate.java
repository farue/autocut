package de.farue.autocut.service.accounting;

import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionType;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransactionTemplate {

    private TransactionType type;

    private BigDecimal value;

    private String description;

    private String issuer;

    private String serviceQualifier;

    private String recipient;

    private TransactionBook transactionBook;
}
