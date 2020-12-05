package com.study.study7itemprocess.job;

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

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-04
 */
@EnableBatchProcessing
//@Configuration
public class TypeItemProcessorJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("type-item-processor-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory) {
        AtomicInteger atomicInteger = new AtomicInteger();
        return stepBuilderFactory.get("type-item-processor-step")
                .<Integer, String>chunk(3)
                .reader(() -> atomicInteger.get() > 20 ? null : atomicInteger.getAndIncrement())
                .processor((Function<Integer, String>) item -> {
                    System.out.println("processor : " + item);
                    return item.toString() + " process ";
                })
                .writer(items -> items.stream().forEach(x->System.out.println("writer : " + x)))
                .build();
    }

}
