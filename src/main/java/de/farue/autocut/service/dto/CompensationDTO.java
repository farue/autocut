package de.farue.autocut.service.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CompensationDTO {

    private TimesheetDTO timesheet;
    private long workedTime = 0L;
    private BigDecimal monetaryBenefit = BigDecimal.ZERO;
    private BigDecimal rate;
    private BigDecimal unboundedCompensation = BigDecimal.ZERO;
    private BigDecimal effectiveCompensation = BigDecimal.ZERO;
    private BigDecimal compensation = BigDecimal.ZERO;
}
