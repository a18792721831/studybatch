package com.study.study8itemwriter.job;

import com.study.study8itemwriter.domain.People;
import com.study.study8itemwriter.listener.AnnoItemWriterLis;
import com.study.study8itemwriter.listener.MyItemWriterLis;
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

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-07
 */
@EnableBatchProcessing
@Configuration
public class MyItemWriterLisJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder()
                .addLong("id", 2L).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("my-item-writer-lis-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory) {
        AtomicLong atomicLong = new AtomicLong();
        return stepBuilderFactory.get("my-item-writer-lis-step")
                .<People, People>chunk(3)
                .reader(() -> atomicLong.get() > 5 ? null : new People(atomicLong.getAndIncrement(), "lis"))
                .processor((Function<People, People>) item -> {
                    System.out.println(" process : " + item);
                    return item;
                })
                .writer(items -> {
                    for (People people : items) {
                        if (people.getId() == 4) {
                            throw new RuntimeException("people id is 4");
                        }
                        System.out.println(" writer : " + people);
                    }
                })
                .listener(new MyItemWriterLis())
                .listener(new AnnoItemWriterLis())
                .build();
    }

}
