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
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-07
 */
@EnableBatchProcessing
//@Configuration
public class CompositeItemWriterJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job, PeopleDao peopleDao) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder()
                .addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("compo-batch-item-writer-step")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, MyBatisBatchItemWriter<People> myBatisWriter1, MyBatisBatchItemWriter<People> myBatisWriter2) {
        AtomicLong atomicLong = new AtomicLong();
        CompositeItemWriter<People> writer = new CompositeItemWriterBuilder<People>().delegates(Arrays.asList(myBatisWriter1, myBatisWriter2))
                .ignoreItemStream(true).build();
        return stepBuilderFactory.get("compo-batch-item-writer-step")
                .<People, People>chunk(3)
                .reader(() -> atomicLong.get() > 5 ? null : new People(null, "compo : " + atomicLong.getAndIncrement()))
                .processor((Function<People, People>) item -> {
                    System.out.println("compo : " + item);
                    return item;
                })
                .writer(writer)
                .build();
    }

    @Bean
    public MyBatisBatchItemWriter<People> myBatisWriter1(SqlSessionFactoryBean sqlSessionFactoryBean) throws Exception {
        return new MyBatisBatchItemWriterBuilder<People>()
                .sqlSessionFactory(sqlSessionFactoryBean.getObject())
                .assertUpdates(false)
                .statementId("addPeople")
                .build();
    }

    @Bean
    public MyBatisBatchItemWriter<People> myBatisWriter2(SqlSessionFactoryBean sqlSessionFactoryBean) throws Exception {
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
