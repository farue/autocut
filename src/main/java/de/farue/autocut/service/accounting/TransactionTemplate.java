package de.farue.autocut.service.accounting;

import java.math.BigDecimal;

import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionKind;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransactionTemplate {

    private TransactionKind kind;

    private BigDecimal value;

    private String description;

    private String issuer;

    private String recipient;

    private TransactionBook transactionBook;

}
