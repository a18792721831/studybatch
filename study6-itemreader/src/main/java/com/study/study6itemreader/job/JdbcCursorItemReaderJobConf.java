package com.study.study6itemreader.job;

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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

/**
 * @author jiayq
 * @Date 2020-11-27
 */
@EnableBatchProcessing
//@Configuration
public class JdbcCursorItemReaderJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder().addDate("date", new Date())
                .addLong("status", 1L)
                .toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("jdbc-=cursor-item-reader-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, JdbcCursorItemReader subscriberIdReader) {
        return stepBuilderFactory.get("jdbc-cursor-item-reader-step")
                .<Long, Long>chunk(3)
                .reader(subscriberIdReader)
                .processor(processor())
                .writer(writer())
                .build();
    }

    private ItemWriter<CustomerIdName> writer() {
        return new ItemWriter<CustomerIdName>() {

            @Override
            public void write(List<? extends CustomerIdName> items) throws Exception {
                System.out.println("write : " + items.size());
            }
        };
    }

    private ItemProcessor<CustomerIdName, CustomerIdName> processor() {
        return new ItemProcessor<CustomerIdName, CustomerIdName>() {
            @Override
            public CustomerIdName process(CustomerIdName item) throws Exception {
                System.out.println(item);
                return item;
            }
        };
    }

    @Bean
    @Autowired
    @StepScope
    public JdbcCursorItemReader<CustomerIdName> subscriberIdReader(@Qualifier("bossDataSource") DataSource bossDataSource,@Value("#{jobParameters['status']}") Integer status) {
        return new JdbcCursorItemReaderBuilder<CustomerIdName>()
                .name("subscriberIdReader")
                .sql("select cus.CUSTOMERID_PK, cus.CUSTOMERNAMESTR\n" +
                        "from CUSTOMEREN cus\n" +
                        "where cus.CUSTOMERSTATUSID = ?")
                .rowMapper((rs, rowNum) -> new CustomerIdName(rs.getLong("CUSTOMERID_PK"), rs.getString("CUSTOMERNAMESTR")))
                .preparedStatementSetter((ps)->ps.setInt(1, status))
                .dataSource(bossDataSource)
                .fetchSize(10) // 一次从数据库取10行
                .ignoreWarnings(true) // 忽略取数异常
                .maxRows(100) // 最多取100行
                .queryTimeout(60) // 查询超时时间为60s
                .build();
    }


}
