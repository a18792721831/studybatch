package com.study.study6itemreader.job;

import com.study.study6itemreader.domain.CustomerIdName;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.OraclePagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Date 2020-11-28
 */
@EnableBatchProcessing
//@Configuration
public class JdbcPagingItemReaderJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher,Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job,new JobParametersBuilder()
        .addDate("date", new Date())
        .addLong("status", 1L)
        .toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,Step step) {
        return jobBuilderFactory.get("jdbc-paging-item-reader-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, JdbcPagingItemReader reader) {
        return stepBuilderFactory.get("jdbc-paging-item-reader-step")
                .<CustomerIdName, CustomerIdName>chunk(4)
                .reader(reader)
                .processor((Function) item -> {
                    System.out.println(item);
                    return item;
                })
                .writer(items -> System.out.println("write : " + items.size()))
                .build();
    }

    @Bean
    @Autowired
    @StepScope
    public JdbcPagingItemReader<CustomerIdName> reader(@Qualifier("bossDataSource") DataSource dataSource, @Value("#{jobParameters['status']}") Integer status) {
        return new JdbcPagingItemReaderBuilder()
                .name("jdbc-paging-item-reader")
                .fetchSize(10)
                .maxItemCount(100)
                .selectClause("CUSTOMERID_PK, CUSTOMERNAMESTR")
                .fromClause("CUSTOMEREN")
                .sortKeys(Map.of("CUSTOMERID_PK", Order.ASCENDING))
                .whereClause("CUSTOMERSTATUSID = :status")
                .beanRowMapper(CustomerIdName.class)
                .parameterValues(Map.of("status", status))
                .dataSource(dataSource)
                .rowMapper((rs, rowNum) -> new CustomerIdName(rs.getLong("CUSTOMERID_PK"), rs.getString("CUSTOMERNAMESTR")))
                .build();
    }

}
