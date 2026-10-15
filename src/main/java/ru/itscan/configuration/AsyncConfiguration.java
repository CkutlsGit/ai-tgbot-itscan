package ru.itscan.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.itscan.configuration.property.AsyncProperties;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfiguration {

    @Bean("botExecutor")
    public Executor botExecutor(AsyncProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.corePoolSize());
        executor.setMaxPoolSize(properties.maxPoolSize());
        executor.setQueueCapacity(properties.queueCapacity());
        executor.setThreadNamePrefix(properties.threadPrefix());
        executor.initialize();

        return executor;
    }
}
