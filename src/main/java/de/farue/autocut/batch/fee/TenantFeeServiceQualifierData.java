package de.farue.autocut.batch.fee;

import java.time.LocalDate;
import lombok.Data;

@Data
public class TenantFeeServiceQualifierData {

    private LocalDate chargeDate;
    private boolean discount;
}
