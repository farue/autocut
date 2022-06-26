package de.farue.autocut.service.dto;

import de.farue.autocut.domain.Apartment;
import java.util.List;
import lombok.Data;

@Data
public class MembershipDTO {

    private List<MemberDTO> members;
    private LeaseDTO lease;
    private TransactionBookDTO transactionBook;
    private Apartment apartment;
    private InternetDTO internet;
}
