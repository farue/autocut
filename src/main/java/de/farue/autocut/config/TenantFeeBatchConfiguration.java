package de.farue.autocut.config;

import de.farue.autocut.batch.ItemListWriter;
import de.farue.autocut.batch.fee.TenantFeeBatchWriter;
import de.farue.autocut.batch.fee.TenantFeeChargingBatchProcessor;
import de.farue.autocut.batch.fee.TenantFeeServiceQualifierDataMapper;
import de.farue.autocut.batch.fee.TenantFeeUnverifiedTenantSkippingProcessor;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Lease_;
import de.farue.autocut.repository.InternalTransactionRepository;
import de.farue.autocut.repository.LeaseRepository;
import de.farue.autocut.service.ActivityService;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.accounting.BookingTemplate;
import de.farue.autocut.service.accounting.InternalTransactionService;
import java.util.List;
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
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;

@Configuration
public class TenantFeeBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job tenantFeeJob(JobRepository jobRepository, Step chargingFeeStep) {
        return jobBuilderFactory.get("tenantFeeJob").repository(jobRepository).start(chargingFeeStep).build();
    }

    @Bean
    public Step chargingFeeStep(
        ItemReader<Lease> tenantFeeReader,
        ItemProcessor<Lease, List<BookingTemplate>> tenantFeeCompositeChargingProcessor,
        ItemWriter<List<BookingTemplate>> writer
    ) {
        return stepBuilderFactory
            .get("chargingFeeStep")
            .<Lease, List<BookingTemplate>>chunk(10)
            .reader(tenantFeeReader)
            .processor(tenantFeeCompositeChargingProcessor)
            .writer(writer)
            .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Lease> tenantFeeReader(LeaseRepository leaseRepository) {
        RepositoryItemReader<Lease> reader = new RepositoryItemReader<>();
        reader.setRepository(leaseRepository);
        reader.setSort(Map.of(Lease_.ID, Direction.ASC));
        reader.setMethodName("findAll");
        return reader;
    }

    @Bean
    @StepScope
    public CompositeItemProcessor<Lease, List<BookingTemplate>> tenantFeeCompositeChargingProcessor(
        ItemProcessor<Lease, Lease> tenantFeeEnverifiedTenantSkippingProcessor,
        ItemProcessor<Lease, List<BookingTemplate>> tenantFeeChargingProcessor
    ) {
        CompositeItemProcessor<Lease, List<BookingTemplate>> compositeProcessor = new CompositeItemProcessor<>();
        compositeProcessor.setDelegates(List.of(tenantFeeEnverifiedTenantSkippingProcessor, tenantFeeChargingProcessor));
        return compositeProcessor;
    }

    @Bean
    @StepScope
    public TenantFeeUnverifiedTenantSkippingProcessor tenantFeeEnverifiedTenantSkippingProcessor() {
        return new TenantFeeUnverifiedTenantSkippingProcessor();
    }

    @Bean
    @StepScope
    public TenantFeeChargingBatchProcessor tenantFeeChargingProcessor(
        InternalTransactionRepository transactionRepository,
        LeaseService leaseService,
        ActivityService activityService,
        TenantFeeServiceQualifierDataMapper serviceQualifierDataMapper
    ) {
        return new TenantFeeChargingBatchProcessor(transactionRepository, leaseService, activityService, serviceQualifierDataMapper);
    }

    @Bean
    @StepScope
    public ItemListWriter<BookingTemplate> tenantFeeItemListWriter(ItemWriter<BookingTemplate> tenantFeeBookingTemplateItemWriter) {
        return new ItemListWriter<>(tenantFeeBookingTemplateItemWriter);
    }

    @Bean
    @StepScope
    public TenantFeeBatchWriter tenantFeeBookingTemplateItemWriter(InternalTransactionService transactionService) {
        return new TenantFeeBatchWriter(transactionService);
    }
}
