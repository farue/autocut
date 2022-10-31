package de.farue.autocut.config;

import de.farue.autocut.batch.reward.RewardProcessor;
import de.farue.autocut.batch.reward.RewardWriter;
import de.farue.autocut.domain.Lease_;
import de.farue.autocut.domain.RewardPayout;
import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.repository.RewardPayoutRepository;
import de.farue.autocut.repository.TimesheetRepository;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.RewardPayoutService;
import de.farue.autocut.service.RewardTimeReviewService;
import de.farue.autocut.service.TimesheetTimeService;
import de.farue.autocut.service.accounting.InternalTransactionService;
import de.farue.autocut.service.accounting.TransactionBookService;
import java.util.Map;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

@Configuration
public class RewardBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job rewardJob(JobRepository jobRepository, Step rewardStep) {
        return jobBuilderFactory.get("rewardJob").repository(jobRepository).preventRestart().start(rewardStep).build();
    }

    @Bean
    public Step rewardStep(
        ItemReader<Timesheet> rewardReader,
        ItemProcessor<Timesheet, RewardPayout> rewardProcessor,
        ItemWriter<RewardPayout> rewardWriter
    ) {
        return stepBuilderFactory
            .get("rewardStep")
            .<Timesheet, RewardPayout>chunk(10)
            .reader(rewardReader)
            .processor(rewardProcessor)
            .writer(rewardWriter)
            .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Timesheet> rewardReader(TimesheetRepository timesheetRepository) {
        RepositoryItemReader<Timesheet> reader = new RepositoryItemReader<>();
        reader.setRepository(timesheetRepository);
        reader.setSort(Map.of(Lease_.ID, Sort.Direction.ASC));
        reader.setMethodName("findAll");
        return reader;
    }

    @Bean
    @StepScope
    public RewardProcessor rewardProcessor(
        TimesheetTimeService timesheetTimeService,
        RewardTimeReviewService rewardTimeReviewService,
        RewardPayoutService rewardPayoutService
    ) {
        return new RewardProcessor(timesheetTimeService, rewardTimeReviewService, rewardPayoutService);
    }

    @Bean
    @StepScope
    public RewardWriter rewardWriter(
        RewardPayoutRepository rewardPayoutRepository,
        TransactionBookService transactionBookService,
        LeaseService leaseService,
        InternalTransactionService internalTransactionService
    ) {
        return new RewardWriter(rewardPayoutRepository, transactionBookService, leaseService, internalTransactionService);
    }
}
