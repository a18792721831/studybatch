package com.study.study7itemprocess.job;

import com.study.study7itemprocess.domain.People;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-04
 */
@EnableBatchProcessing
//@Configuration
public class FilterItemProcessorJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("filter-item-processor-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory) {
        AtomicLong atomicLong = new AtomicLong();
        return stepBuilderFactory.get("filter-item-processor-step")
                .<People, People>chunk(3)
                .reader(() -> atomicLong.get() > 20 ? null : new People(atomicLong.getAndIncrement(), ""))
                .processor((Function<People, People>) item -> {
                    item.setName("name" + item.getId());
                    System.out.println("processor : " + item);
                    if (item.getId() % 3 == 0) {
                        return null;
                    }
                    return item;
                })
                .writer(items -> items.stream().forEach(x -> System.out.println("writer : " + x)))
                .build();
    }

}
