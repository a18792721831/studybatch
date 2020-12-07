package com.study.study8itemwriter.job;

import com.study.study8itemwriter.Study8ItemwriterApplication;
import com.study.study8itemwriter.domain.People;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Propagation;

import javax.sql.DataSource;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-05
 */
@EnableBatchProcessing
//@Configuration
public class JpaItemWriterJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder()
                .addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("jpa-item-writer-step")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, JpaItemWriter<People> writer,JpaTransactionManager jpaTransactionManager) {
        AtomicLong atomicLong = new AtomicLong();
        return stepBuilderFactory.get("jpa-item-writer-step")
                .<People, People>chunk(3)
                .reader(() -> atomicLong.get() > 10 ? null : new People(null, "jpa : " + atomicLong.getAndIncrement()))
                .processor((Function<People, People>) item -> {
                    System.out.println("jpa : " + item);
                    return item;
                })
                .writer(writer)
                .transactionManager(jpaTransactionManager)
                .build();
    }

    @Bean
    public JpaItemWriter<People> writer(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        return new JpaItemWriterBuilder<People>()
                .entityManagerFactory(entityManagerFactoryBean.getObject())
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource dataSource, EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .packages(Study8ItemwriterApplication.class)
                .build();
    }

    @Bean
    public JpaTransactionManager transactionManager(DataSource dataSource) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
