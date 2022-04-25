package de.farue.autocut.service.timesheet;

import static de.farue.autocut.utils.BigDecimalUtil.compare;

import de.farue.autocut.domain.TimesheetTask;
import de.farue.autocut.domain.TimesheetTime;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

public class TimesheetTimeCalculator {

    private final TimesheetTime time;
    private Integer constant;
    private BigDecimal factor;
    private Integer workedTime;
    private Integer effectiveTime;

    public TimesheetTimeCalculator(TimesheetTime time) {
        this.time = time;
    }

    public static TimesheetTimeCalculator calculate(TimesheetTime time) {
        return new TimesheetTimeCalculator(time);
    }

    public Integer getConstant() {
        if (constant == null) {
            initConstant();
        }
        return constant;
    }

    public BigDecimal getFactor() {
        if (factor == null) {
            initFactor();
        }
        return factor;
    }

    public int getWorkedTime() {
        if (workedTime == null) {
            workedTime = calculateWorkedTime();
        }
        return workedTime;
    }

    public int getEffectiveTime() {
        if (effectiveTime == null) {
            effectiveTime = calculateEffectiveTime();
        }
        return effectiveTime;
    }

    private void initConstant() {
        if (constant == null) {
            TimesheetTask task = time.getTask();
            constant = time.getEditedConstant() != null ? time.getEditedConstant() : task.getConstant();
        }
    }

    private void initFactor() {
        if (factor == null) {
            TimesheetTask task = time.getTask();
            factor = time.getEditedFactor() != null ? time.getEditedFactor() : task.getFactor();
        }
    }

    private int calculateWorkedTime() {
        if (compare(getFactor()).isZero()) {
            return getConstant();
        }
        long diff = time.getStart().until(time.getEnd(), ChronoUnit.SECONDS);
        BigDecimal workedTime = new BigDecimal(diff).subtract(new BigDecimal(time.getPause()));
        return workedTime.toBigInteger().intValueExact();
    }

    private int calculateEffectiveTime() {
        BigDecimal factoredWorkedTime = getFactor().multiply(new BigDecimal(getWorkedTime()));
        BigDecimal effectiveTime = factoredWorkedTime.add(new BigDecimal(getConstant()));
        return effectiveTime.toBigInteger().intValueExact();
    }
}
