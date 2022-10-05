package de.farue.autocut.service;

import static de.farue.autocut.utils.BigDecimalUtil.compare;

import de.farue.autocut.domain.Compensation;
import de.farue.autocut.domain.TimesheetStatement;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class CompensationCalculator {

    private BigDecimal totalCompensation;
    private BigDecimal minPerPerson;
    private BigDecimal maxPerPerson;

    public CompensationCalculator(BigDecimal totalCompensation, BigDecimal minPerPerson, BigDecimal maxPerPerson) {
        this.totalCompensation = totalCompensation;
        this.minPerPerson = minPerPerson;
        this.maxPerPerson = maxPerPerson;
    }

    public List<Compensation> calculate(Collection<TimesheetStatement> statements) {
        if (statements.size() == 0) {
            return Collections.emptyList();
        }

        Set<Compensation> unorderedData = statements.stream().map(this::map).collect(Collectors.toSet());
        List<Compensation> data = unorderedData.stream().sorted(Comparator.comparingLong(Compensation::getWorkedTime).reversed()).toList();

        BigDecimal toBeDistributed = totalCompensation.add(
            data.stream().map(Compensation::getMonetaryBenefit).reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        for (int i = 0; i < 11; i++) {
            if (i == 10) {
                System.out.println("Failed to distribute compensation in " + i + " rounds.");
                break;
            }

            distribute(data, toBeDistributed, maxPerPerson, i == 0);
            applyBoundaries(data, minPerPerson, maxPerPerson);

            BigDecimal distributed = data.stream().map(Compensation::getCompensation).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal remaining = totalCompensation.subtract(distributed);
            if (compare(remaining).isZero()) {
                break;
            }
            toBeDistributed = remaining;
        }

        // calculate rate as money per hour
        data.forEach(c -> c.setRate(c.getRate().multiply(BigDecimal.valueOf(3600))));

        return data;
    }

    private void distribute(List<Compensation> data, BigDecimal toBeDistributed, BigDecimal maxPerPerson, boolean initialDistribution) {
        long remainingTotalTime = data
            .stream()
            .filter(c -> compare(c.getEffectiveCompensation()).isSmallerThan(maxPerPerson))
            .mapToLong(Compensation::getWorkedTime)
            .sum();
        if (remainingTotalTime == 0) {
            return;
        }
        BigDecimal remainingCompensation = toBeDistributed;
        BigDecimal rate = remainingCompensation.divide(BigDecimal.valueOf(remainingTotalTime), 10, RoundingMode.HALF_EVEN);
        for (Compensation c : data) {
            remainingTotalTime -= c.getWorkedTime();

            c.setCalculatedCompensation(
                c.getCalculatedCompensation().add(BigDecimal.valueOf(c.getWorkedTime()).multiply(rate).setScale(2, RoundingMode.HALF_EVEN))
            );
            if (initialDistribution) {
                c.setUnboundedCompensation(c.getCalculatedCompensation());
            }
            c.setEffectiveCompensation(c.getMonetaryBenefit().max(c.getCalculatedCompensation()));
            remainingCompensation = remainingCompensation.subtract(c.getEffectiveCompensation());

            // calculate actual rate
            calculateRate(c);
            c.setCompensation(c.getEffectiveCompensation().subtract(c.getMonetaryBenefit()).setScale(2, RoundingMode.HALF_EVEN));
        }
    }

    private void applyBoundaries(List<Compensation> data, BigDecimal min, BigDecimal max) {
        for (Compensation c : data) {
            if (compare(c.getCompensation()).isGreaterThan(max)) {
                c.setCompensation(max);
            } else if (compare(c.getCompensation()).isSmallerThan(min)) {
                c.setCompensation(BigDecimal.ZERO);
            }

            c.setEffectiveCompensation(c.getCompensation().add(c.getMonetaryBenefit()));
            calculateRate(c);
        }
    }

    private void calculateRate(Compensation c) {
        BigDecimal workedTime = BigDecimal.valueOf(c.getWorkedTime());
        c.setRate(c.getWorkedTime() > 0 ? c.getEffectiveCompensation().divide(workedTime, 10, RoundingMode.HALF_EVEN) : BigDecimal.ZERO);
    }

    private Compensation map(TimesheetStatement statement) {
        Compensation c = new Compensation();
        c.setWorkedTime(statement.getWorkedTime());
        c.setTimesheetId(statement.getTimesheetId());
        return c;
    }
}
