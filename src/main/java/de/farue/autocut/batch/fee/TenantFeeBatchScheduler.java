package de.farue.autocut.batch.fee;

import de.farue.autocut.security.RoleEnum;
import de.farue.autocut.security.RunWithAuthorities;
import java.time.Instant;
import java.time.YearMonth;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TenantFeeBatchScheduler {

    private final Job tenantFeeJob;
    private final JobLauncher jobLauncher;

    private YearMonth chargePeriod;

    public TenantFeeBatchScheduler(Job tenantFeeJob, JobLauncher jobLauncher) {
        this.tenantFeeJob = tenantFeeJob;
        this.jobLauncher = jobLauncher;
    }

    public YearMonth getChargePeriod() {
        return chargePeriod;
    }

    public void setChargePeriod(YearMonth chargePeriod) {
        this.chargePeriod = chargePeriod;
    }

    // Every day at 3 am. Scheduled frequently to charge new accounts quickly.
    @Scheduled(cron = "0 0/1 * * * ?")
    @RunWithAuthorities(role = RoleEnum.SYSTEM)
    public void launchJob() throws Exception {
        jobLauncher.run(tenantFeeJob, jobParameters());
    }

    private JobParameters jobParameters() {
        JobParametersBuilder parametersBuilder = new JobParametersBuilder();
        YearMonth chargePeriod = this.chargePeriod != null ? this.chargePeriod : YearMonth.now();
        parametersBuilder.addString("run.timestamp", Instant.now().toString());
        parametersBuilder.addString("chargeperiod", chargePeriod.toString());
        return parametersBuilder.toJobParameters();
    }
}
