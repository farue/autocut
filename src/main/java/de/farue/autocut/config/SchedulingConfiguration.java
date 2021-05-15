package de.farue.autocut.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@ConditionalOnProperty(value = "application.scheduling.enable", havingValue = "true", matchIfMissing = true)
@Configuration
@EnableScheduling
public class SchedulingConfiguration implements SchedulingConfigurer {

    private final Logger log = LoggerFactory.getLogger(SchedulingConfiguration.class);

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.log.debug("Creating Scheduling Task Executor");
        taskRegistrar.setScheduler(schedulingTaskExecutor());
    }

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService schedulingTaskExecutor() {
        return Executors.newScheduledThreadPool(1);
    }
}
