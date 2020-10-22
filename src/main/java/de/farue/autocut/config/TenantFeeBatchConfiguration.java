package de.farue.autocut.config;

import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
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
import org.springframework.data.domain.Sort.Direction;

import de.farue.autocut.batch.ItemListWriter;
import de.farue.autocut.batch.fee.TenantFeeBatchWriter;
import de.farue.autocut.batch.fee.TenantFeeChargingBatchProcessor;
import de.farue.autocut.batch.fee.TenantFeeCorrectingBatchProcessor;
import de.farue.autocut.batch.fee.TenantFeeServiceQualifierDataMapper;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Lease_;
import de.farue.autocut.repository.LeaseRepository;
import de.farue.autocut.repository.TransactionRepository;
import de.farue.autocut.service.ActivityService;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.accounting.BookingTemplate;
import de.farue.autocut.service.accounting.TransactionBookService;

@Configuration
@EnableBatchProcessing
public class TenantFeeBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job tenantFeeJob(JobRepository jobRepository, Step correctingFeeStep, Step chargingFeeStep) {
        return jobBuilderFactory.get("tenantFeeJob")
            .repository(jobRepository)
            .start(correctingFeeStep)
            .next(chargingFeeStep)
            .build();
    }

    @Bean
    public Step correctingFeeStep(ItemReader<Lease> reader, ItemProcessor<Lease, List<BookingTemplate>> correctingProcessor, ItemWriter<List<BookingTemplate>> writer) {
        return stepBuilderFactory.get("correctingFeeStep")
            .<Lease, List<BookingTemplate>>chunk(10)
            .reader(reader)
            .processor(correctingProcessor)
            .writer(writer)
            .build();
    }

    @Bean
    public Step chargingFeeStep(ItemReader<Lease> reader, ItemProcessor<Lease, List<BookingTemplate>> chargingProcessor, ItemWriter<List<BookingTemplate>> writer) {
        return stepBuilderFactory.get("chargingFeeStep")
            .<Lease, List<BookingTemplate>>chunk(10)
            .reader(reader)
            .processor(chargingProcessor)
            .writer(writer)
            .build();
    }

    @Bean
    @StepScope
    public ItemReader<Lease> reader(LeaseRepository leaseRepository) {
        RepositoryItemReader<Lease> reader = new RepositoryItemReader<>();
        reader.setRepository(leaseRepository);
        reader.setSort(Map.of(Lease_.ID, Direction.ASC));
        reader.setMethodName("findAll");
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Lease, List<BookingTemplate>> correctingProcessor(TransactionRepository transactionRepository, LeaseService leaseService,
        ActivityService activityService, TenantFeeServiceQualifierDataMapper serviceQualifierDataMapper) {
        return new TenantFeeCorrectingBatchProcessor(leaseService, serviceQualifierDataMapper, transactionRepository, activityService);
    }

    @Bean
    @StepScope
    public ItemProcessor<Lease, List<BookingTemplate>> chargingProcessor(TransactionRepository transactionRepository, LeaseService leaseService,
        ActivityService activityService, TenantFeeServiceQualifierDataMapper serviceQualifierDataMapper) {
        return new TenantFeeChargingBatchProcessor(transactionRepository, leaseService, activityService, serviceQualifierDataMapper);
    }

    @Bean
    @StepScope
    public ItemWriter<List<BookingTemplate>> itemListWriter(ItemWriter<BookingTemplate> bookingTemplateItemWriter) {
        return new ItemListWriter<>(bookingTemplateItemWriter);
    }

    @Bean
    @StepScope
    public ItemWriter<BookingTemplate> bookingTemplateItemWriter(TransactionBookService transactionBookService) {
        return new TenantFeeBatchWriter(transactionBookService);
    }

}
