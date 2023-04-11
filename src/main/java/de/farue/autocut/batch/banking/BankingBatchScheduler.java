package de.farue.autocut.batch.banking;

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
public class BankingBatchScheduler {

    private final Job bankingJob;
    private final JobLauncher jobLauncher;

    public BankingBatchScheduler(Job bankingJob, JobLauncher jobLauncher) {
        this.bankingJob = bankingJob;
        this.jobLauncher = jobLauncher;
    }

    // Every hour between 7 and 20 and at 1, every day
    @Scheduled(cron = "0 0 7-20/1,1 * * ?")
    @RunWithAuthorities(role = RoleEnum.SYSTEM)
    public void launchJob() throws Exception {
        jobLauncher.run(bankingJob, jobParameters());
    }

    private JobParameters jobParameters() {
        JobParametersBuilder parametersBuilder = new JobParametersBuilder();
        parametersBuilder.addString("run.timestamp", Instant.now().toString());
        return parametersBuilder.toJobParameters();
    }
}
