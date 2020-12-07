package com.study.study8itemwriter.job;

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
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.ColumnMapItemPreparedStatementSetter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

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
public class JdbcBatchItemWriterJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder()
                .addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("jdbc-batch-item-writer-step")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, JdbcBatchItemWriter<People> writer) {
        AtomicLong atomicLong = new AtomicLong();
        return stepBuilderFactory.get("jdbc-batch-item-writer-step")
                .<People, People>chunk(3)
                .reader(() -> atomicLong.get() > 10 ? null : new People(null, "name : " + atomicLong.getAndIncrement()))
                .processor((Function<People, People>) item -> {
                    System.out.println("process : " + item);
                    return item;
                })
                .writer(writer)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<People> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<People>()
                .dataSource(dataSource)
//                .sql("insert into people(id,name) values(null,?)")
//                .itemPreparedStatementSetter(((item, ps) -> ps.setString(1, item.getName())))
                .sql("insert into people(id,name) values(null,:name)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .assertUpdates(false)
                .build();
    }

}
