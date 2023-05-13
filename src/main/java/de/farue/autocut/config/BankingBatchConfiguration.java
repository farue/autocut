package de.farue.autocut.config;

import de.farue.autocut.batch.banking.BankingBatchProcessor;
import de.farue.autocut.batch.banking.BankingBatchReader;
import de.farue.autocut.batch.banking.BankingBatchWriter;
import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.repository.BankAccountRepository;
import de.farue.autocut.service.AssociationService;
import de.farue.autocut.service.accounting.BankTransactionService;
import de.farue.autocut.service.accounting.BankingService;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BankingBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job bankingJob(JobRepository jobRepository, Step bankingStep) {
        return jobBuilderFactory
            .get("bankingJob")
            .repository(jobRepository)
            // we can't guarantee that the reader will always provide the same results upon restart
            .preventRestart()
            .start(bankingStep)
            .build();
    }

    @Bean
    public Step bankingStep(
        ItemReader<UmsLine> bankingBatchReader,
        ItemProcessor<UmsLine, BankTransaction> bankingBatchProcessor,
        ItemWriter<BankTransaction> bankingBatchWriter
    ) {
        return stepBuilderFactory
            .get("bankingStep")
            .<UmsLine, BankTransaction>chunk(10)
            .reader(bankingBatchReader)
            .processor(bankingBatchProcessor)
            .writer(bankingBatchWriter)
            .build();
    }

    @Bean
    @StepScope
    public BankingBatchReader bankingBatchReader(BankingService bankingService) {
        return new BankingBatchReader(bankingService);
    }

    @Bean
    @StepScope
    public BankingBatchProcessor bankingBatchProcessor(
        BankAccountRepository bankAccountRepository,
        BankTransactionService bankTransactionService,
        AssociationService associationService
    ) {
        return new BankingBatchProcessor(bankAccountRepository, bankTransactionService, associationService);
    }

    @Bean
    @StepScope
    public BankingBatchWriter bankingBatchWriter(BankTransactionService transactionService) {
        return new BankingBatchWriter(transactionService);
    }
}
