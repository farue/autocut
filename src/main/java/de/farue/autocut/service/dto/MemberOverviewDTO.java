package de.farue.autocut.service.dto;

import de.farue.autocut.domain.Apartment;
import lombok.Data;

@Data
public class MemberOverviewDTO {

    private MemberDTO member;
    private LeaseDTO lease;
    private TransactionBookDTO transactionBook;
    private Apartment apartment;
    private InternetDTO internet;
}
