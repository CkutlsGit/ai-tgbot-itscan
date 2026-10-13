package ru.itscan.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {


    @Bean(name = "primaryDataSource")
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "vectorDataSource")
    @ConfigurationProperties("vector.datasource")
    public DataSource vectorDataSource() {
        return DataSourceBuilder.create().build();
    }
}
