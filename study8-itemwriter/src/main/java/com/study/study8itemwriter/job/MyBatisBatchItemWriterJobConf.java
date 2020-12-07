package com.study.study8itemwriter.job;

import com.study.study8itemwriter.dao.PeopleDao;
import com.study.study8itemwriter.domain.People;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-07
 */
@EnableBatchProcessing
//@Configuration
public class MyBatisBatchItemWriterJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job, PeopleDao peopleDao) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder()
                .addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("mybatis-batch-item-writer-step")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, MyBatisBatchItemWriter<People> writer) {
        AtomicLong atomicLong = new AtomicLong();
        return stepBuilderFactory.get("mybatis-batch-item-writer-step")
                .<People, People>chunk(3)
                .reader(() -> atomicLong.get() > 10 ? null : new People(null, "mybatis : " + atomicLong.getAndIncrement()))
                .processor((Function<People, People>) item -> {
                    System.out.println("mybatis : " + item);
                    return item;
                })
                .writer(writer)
                .build();
    }

    @Bean
    public MyBatisBatchItemWriter<People> writer(SqlSessionFactoryBean sqlSessionFactoryBean) throws Exception {
        return new MyBatisBatchItemWriterBuilder<People>()
                .sqlSessionFactory(sqlSessionFactoryBean.getObject())
                .assertUpdates(false)
                .statementId("addPeople")
                .build();
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource bossDataSource) {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(bossDataSource);
        factoryBean.setTypeAliasesPackage("com.study.study8itemwriter.domain");
        return factoryBean;
    }

}
