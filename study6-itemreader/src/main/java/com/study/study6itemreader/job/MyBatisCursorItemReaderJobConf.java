package com.study.study6itemreader.job;

import com.study.study6itemreader.dao.CustomerQuery;
import com.study.study6itemreader.domain.CustomerIdName;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-01
 */
@EnableBatchProcessing
//@Configuration
public class MyBatisCursorItemReaderJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job,CustomerQuery customerQuery) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder()
                .addLong("status", 0L)
                .addDate("date", new Date())
                .toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("mybatis-cursor-item-reader-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, MyBatisCursorItemReader<CustomerIdName> reader) {
        return stepBuilderFactory.get("mybatis-curosr-item-reader")
                .<CustomerIdName, CustomerIdName>chunk(6)
                .reader(reader)
                .processor((Function<CustomerIdName, CustomerIdName>) item -> {
                    System.out.println(item);
                    return item;
                })
                .writer(items -> System.out.println("writer : " + items.size()))
                .build();
    }

    @Bean
    @StepScope
    public MyBatisCursorItemReader<CustomerIdName> reader(SqlSessionFactoryBean sqlSessionFactoryBean, @Value("#{jobParameters['status']}") Integer status) throws Exception {
        return new MyBatisCursorItemReaderBuilder<CustomerIdName>()
                .saveState(true)
                .maxItemCount(100)
                .sqlSessionFactory(sqlSessionFactoryBean.getObject())
                .queryId("com.study.study6itemreader.dao.CustomerQuery.queryCustomer")
                .parameterValues(Map.of("status", status))
                .build();
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("bossDataSource") DataSource bossDataSource) {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(bossDataSource);
        factoryBean.setTypeAliasesPackage("com.study.study6itemreader.domain");
        return factoryBean;
    }
}
