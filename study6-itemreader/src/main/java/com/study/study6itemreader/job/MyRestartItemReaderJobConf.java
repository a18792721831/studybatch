package com.study.study6itemreader.job;

import com.study.study6itemreader.reader.MyRestartItemReader;
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

import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-04
 */
@EnableBatchProcessing
//@Configuration
public class MyRestartItemReaderJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder().addLong("id", 0L).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,Step step) {
        return jobBuilderFactory.get("my-restart-item-reader-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, MyRestartItemReader reader) {
        return stepBuilderFactory.get("my-restart-item-reader-step")
                .<Integer,Integer>chunk(3)
                .reader(reader)
                .processor((Function<Integer, Integer>)item->{
                    System.out.println("process : " + item);
                    return item;
                })
                .writer(items -> System.out.println("writer : " + items.size()))
                .build();
    }

}
