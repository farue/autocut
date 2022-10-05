package de.farue.autocut.domain;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class Compensation {

    private long timesheetId;
    private long workedTime = 0L;

    /**
     * Monetary benefits one got, such as team dinners. If factored in, it causes little
     * amounts of worked hours to be already compensated by the monetary benefits. So in
     * general the discharge is lower for people with hours below the average and higher for
     * people with hours above average.
     */
    private BigDecimal monetaryBenefit = BigDecimal.ZERO;

    /**
     * The actual rate of how well one is compensated, €/h.
     */
    private BigDecimal rate;

    /**
     * Compensation used to calculate
     */
    private BigDecimal calculatedCompensation = BigDecimal.ZERO;

    /**
     * The compensation one would be entitled to without regarding monetary benefits and
     * min and max limits.
     */
    private BigDecimal unboundedCompensation = BigDecimal.ZERO;

    /**
     * The effective compensation one receives, that is compensation after applying min
     * and max limits, including monetary benefits.
     */
    private BigDecimal effectiveCompensation = BigDecimal.ZERO;

    /**
     * The actual compensation one can request from StW.
     */
    private BigDecimal compensation = BigDecimal.ZERO;
}
