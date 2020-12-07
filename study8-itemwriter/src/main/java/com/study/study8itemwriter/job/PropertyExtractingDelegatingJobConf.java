package com.study.study8itemwriter.job;

import com.study.study8itemwriter.domain.People;
import com.study.study8itemwriter.service.PeopleService;
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
import org.springframework.batch.item.adapter.PropertyExtractingDelegatingItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-07
 */
@EnableBatchProcessing
//@Configuration
public class PropertyExtractingDelegatingJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder()
                .addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("pro-item-writer-adapter-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, PropertyExtractingDelegatingItemWriter<People> writer) {
        AtomicLong atomicLong = new AtomicLong();
        return stepBuilderFactory.get("pro-item-writer-adapter-step")
                .<People, People>chunk(3)
                .reader(() -> atomicLong.get() > 5 ? null : new People(null, " " + atomicLong.getAndIncrement()))
                .processor((Function<People, People>) item -> {
                    System.out.println("process : " + item);
                    return item;
                })
                .writer(writer)
                .build();
    }

    @Bean
    public PropertyExtractingDelegatingItemWriter<People> writer(PeopleService peopleService) {
        PropertyExtractingDelegatingItemWriter<People> writer = new PropertyExtractingDelegatingItemWriter<>();
        writer.setTargetObject(peopleService);
        writer.setTargetMethod("printName");
        writer.setFieldsUsedAsTargetMethodArguments(new String[]{"name"});
        return writer;
    }

}
