package de.farue.autocut.service.dto;

import de.farue.autocut.domain.enumeration.TransactionBookType;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TransactionBookDTO {

    private Long id;
    private String name;
    private TransactionBookType type;
    private BigDecimal balance;
}
