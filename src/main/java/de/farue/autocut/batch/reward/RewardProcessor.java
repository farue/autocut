package de.farue.autocut.batch.reward;

import de.farue.autocut.domain.RewardPayout;
import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.service.RewardPayoutService;
import de.farue.autocut.service.RewardTimeReviewService;
import de.farue.autocut.service.TimesheetTimeService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

public class RewardProcessor implements ItemProcessor<Timesheet, RewardPayout> {

    private static final int REWARD_UNIT_HOURS = 15;
    private static final int REWARD_UNIT_AMOUNT = 5;

    private final TimesheetTimeService timesheetTimeService;
    private final RewardTimeReviewService rewardTimeReviewService;
    private final RewardPayoutService rewardPayoutService;

    private Instant timestamp;

    public RewardProcessor(
        TimesheetTimeService timesheetTimeService,
        RewardTimeReviewService rewardTimeReviewService,
        RewardPayoutService rewardPayoutService
    ) {
        this.timesheetTimeService = timesheetTimeService;
        this.rewardTimeReviewService = rewardTimeReviewService;
        this.rewardPayoutService = rewardPayoutService;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Value("#{jobParameters['run.timestamp']}")
    public void setTimestamp(String str) {
        this.timestamp = Instant.parse(str);
    }

    @Override
    public RewardPayout process(Timesheet timesheet) {
        Instant earliest = Instant.EPOCH;
        Instant latest = timestamp.atZone(ZoneId.systemDefault()).minusMonths(1).toInstant();
        long sumTime = timesheetTimeService.getSumTime(timesheet, earliest, latest);
        long timeExcluded = rewardTimeReviewService.timeExcludedFromReward(timesheet, earliest, latest);
        long totalTimeToReward = sumTime - timeExcluded;

        if (totalTimeToReward < 0) {
            throw new IllegalStateException(
                "More time excluded than has been booked for timesheet " +
                timesheet +
                ". Booked: " +
                sumTime +
                ", Excluded: " +
                timeExcluded
            );
        }

        long rewardedTime = rewardPayoutService.rewardedTime(timesheet);
        long timeToReward = totalTimeToReward - rewardedTime;
        if (timeToReward <= 0) {
            return null;
        }

        int rewardsCount = (int) (timeToReward / (REWARD_UNIT_HOURS * 60 * 60));
        if (rewardsCount == 0) {
            return null;
        }
        int rewardsTime = rewardsCount * REWARD_UNIT_HOURS * 60 * 60;
        BigDecimal rewardingAmount = BigDecimal.valueOf((long) rewardsCount * REWARD_UNIT_AMOUNT);

        return new RewardPayout().timesheet(timesheet).time(rewardsTime).amount(rewardingAmount).timestamp(timestamp);
    }
}
