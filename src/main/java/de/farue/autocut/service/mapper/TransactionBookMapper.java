package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.service.dto.TransactionBookDTO;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface TransactionBookMapper {
    TransactionBookDTO fromTransactionBook(TransactionBook transactionBook, BigDecimal balance);
}
