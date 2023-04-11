package de.farue.autocut.batch.reward;

import de.farue.autocut.security.RoleEnum;
import de.farue.autocut.security.RunWithAuthorities;
import java.time.Instant;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RewardBatchScheduler {

    private final Job rewardJob;
    private final JobLauncher jobLauncher;

    public RewardBatchScheduler(Job rewardJob, JobLauncher jobLauncher) {
        this.rewardJob = rewardJob;
        this.jobLauncher = jobLauncher;
    }

    // Every day at 0:00
    @Scheduled(cron = "0 0 0 * * ?")
    @RunWithAuthorities(role = RoleEnum.SYSTEM)
    public void launchJob() throws Exception {
        jobLauncher.run(rewardJob, jobParameters());
    }

    private JobParameters jobParameters() {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("run.timestamp", Instant.now().toString());
        return jobParametersBuilder.toJobParameters();
    }
}
