package com.study.study6itemreader.job;

import com.study.study6itemreader.Study6ItemreaderApplication;
import com.study.study6itemreader.domain.CustomerIdName;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-11-30
 */
@EnableBatchProcessing
//@Configuration
public class JpaPagingItemReaderJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder()
                .addDate("date", new Date())
                .addLong("status", 1L)
                .toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("jpa-paging-item-reader-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, JpaPagingItemReader reader) {
        return stepBuilderFactory.get("jpa-paging-item-reader-step")
                .<CustomerIdName, CustomerIdName>chunk(5)
                .reader(reader)
                .processor((Function) item -> {
                    System.out.println(item);
                    return item;
                })
                .writer(items -> System.out.println("writer : " + items.size()))
                .build();

    }

    @Bean
    @StepScope
    public JpaPagingItemReader<CustomerIdName> reader(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean, @Value("#{jobParameters['status']}") Integer status) {
        return new JpaPagingItemReaderBuilder<CustomerIdName>()
                .name("jpa-paging-item-rader")
                .entityManagerFactory(entityManagerFactoryBean.getObject())
                .queryString("select c\n" +
                        "from CustomerIdName c\n" +
                        "where CUSTOMERSTATUSID = :status")
                .parameterValues(Map.of("status", status))
                .maxItemCount(100)
                .pageSize(10)
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(@Qualifier("bossDataSource") DataSource bossDataSource, EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(bossDataSource)
                .packages(Study6ItemreaderApplication.class)
                .build();
    }

}
