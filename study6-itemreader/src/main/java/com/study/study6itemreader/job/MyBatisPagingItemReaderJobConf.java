package com.study.study6itemreader.job;

import com.study.study6itemreader.dao.CustomerQuery;
import com.study.study6itemreader.domain.CustomerIdName;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-03
 */
@EnableBatchProcessing
//@Configuration
public class MyBatisPagingItemReaderJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job, CustomerQuery customerQuery) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder()
                .addDate("date", new Date())
                .addLong("status", 1L)
                .toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("mybatis-paging-item-reader-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, MyBatisPagingItemReader<CustomerIdName> reader) {
        return stepBuilderFactory.get("mybatis-paging-item-reader-step")
                .<CustomerIdName, CustomerIdName>chunk(3)
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
    public MyBatisPagingItemReader<CustomerIdName> reader(SqlSessionFactoryBean sqlSessionFactoryBean, @Value("#{jobParameters['status']}") Integer status) throws Exception {
        return new MyBatisPagingItemReaderBuilder<CustomerIdName>()
                .sqlSessionFactory(sqlSessionFactoryBean.getObject())
                .maxItemCount(10)
                .pageSize(2)
                .parameterValues(Map.of("status", status))
                .queryId("queryCustomer")
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
