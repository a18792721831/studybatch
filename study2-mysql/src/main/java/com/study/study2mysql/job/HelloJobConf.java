package com.study.study2mysql.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiayq
 * @Date 2020-11-05
 */
@Configuration
@EnableBatchProcessing
public class HelloJobConf {

    private static final Logger logger = LoggerFactory.getLogger(HelloJobConf.class);

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Bean
    public String runJob(JobLauncher jobLauncher,Job helloJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(helloJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    @Autowired
    public JobRepositoryFactoryBean jobRepositoryFactoryBean(DataSource dataSource, PlatformTransactionManager transactionManager) {
        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setTransactionManager(transactionManager);
        jobRepositoryFactoryBean.setDataSource(dataSource);
        try {
            jobRepositoryFactoryBean.afterPropertiesSet();
        } catch (Exception e) {
            logger.error("create job repository factory bean error : {}", e.getMessage());
        }
        return jobRepositoryFactoryBean;
    }

    @Bean
    @Autowired
    public JobRepository jobRepository(JobRepositoryFactoryBean jobRepositoryFactoryBean) {
        JobRepository jobRepository = null;
        try {
            jobRepository = jobRepositoryFactoryBean.getObject();
        } catch (Exception e) {
            logger.error("create job repository error : {}", e.getMessage());
        }
        return jobRepository;
    }

    @Bean
    @Autowired
    public Job helloJob(Step step1, JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("hello-job")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    @Autowired
    public Step step1(ItemReader reader, ItemWriter writer, ItemProcessor processor, StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("step1")
                .chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @Autowired
    public ItemReader reader() {
        return new ItemReader() {
            @Override
            public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                if (atomicInteger.intValue() > 20) {
                    return null;
                }
                System.out.println("reader : hello");
                return "hello" + atomicInteger.incrementAndGet();
            }
        };
    }

    @Bean
    public ItemWriter writer() {
        return new ItemWriter() {
            @Override
            public void write(List items) throws Exception {
                items.stream().forEach(x -> System.out.println("writer : " + x));
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
}
