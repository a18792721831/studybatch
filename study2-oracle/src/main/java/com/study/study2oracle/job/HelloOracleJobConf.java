package com.study.study2oracle.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiayq
 * @Date 2020-11-06
 */
@Configuration
@EnableBatchProcessing
public class HelloOracleJobConf {

    private static final Logger log = LoggerFactory.getLogger(HelloOracleJobConf.class);

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Bean
    public String runJob(JobLauncher jobLauncher,Job helloJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(helloJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public JobRepositoryFactoryBean jobRepositoryFactoryBean(DataSource dataSource, PlatformTransactionManager transactionManager) {
        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setTransactionManager(transactionManager);
        jobRepositoryFactoryBean.setDataSource(dataSource);
        jobRepositoryFactoryBean.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
        try {
            jobRepositoryFactoryBean.afterPropertiesSet();
        } catch (Exception e) {
            log.error("创建jobRepositoryFactoryBean异常：{}", e.getMessage());
        }
        return jobRepositoryFactoryBean;
    }

    @Bean
    public JobRepository jobRepository(JobRepositoryFactoryBean jobRepositoryFactoryBean) {
        JobRepository jobRepository = null;
        try {
            jobRepository = jobRepositoryFactoryBean.getObject();
        } catch (Exception e) {
            log.error("创建jobRepository异常{}", e.getMessage());
        }
        return jobRepository;
    }

    @Bean
    public ItemReader reader() {
        return new ItemReader() {
            @Override
            public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                if (atomicInteger.intValue() > 20) {
                    return null;
                }
                int s = atomicInteger.incrementAndGet();
                System.out.println("reader : " + s);
                return s;
            }
        };
    }

    @Bean
    public ItemProcessor processor() {
        return new ItemProcessor() {
            @Override
            public Object process(Object item) throws Exception {
                System.out.println("process : " + item);
                return item;
            }
        };
    }

    @Bean
    public ItemWriter writer() {
        return new ItemWriter() {
            @Override
            public void write(List items) throws Exception {
                System.out.println("writer : " + items);
            }
        };
    }

    @Bean
    public Job helloJob(Step step1, JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("hello-job")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(ItemReader reader, ItemProcessor processor, ItemWriter writer, StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("step1")
                .chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


}
