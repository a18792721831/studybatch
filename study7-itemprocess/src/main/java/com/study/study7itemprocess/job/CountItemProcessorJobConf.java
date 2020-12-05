package com.study.study7itemprocess.job;

import com.study.study7itemprocess.listener.CountItemProcessorLis;
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
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiayq
 * @Date 2020-12-05
 */
@EnableBatchProcessing
//@Configuration
public class CountItemProcessorJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder()
                .addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("count-item-processor-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory) {
        AtomicInteger atomicInteger = new AtomicInteger();
        return stepBuilderFactory.get("count-item-processor-step")
                .<Integer, Integer>chunk(3)
                .reader(new ItemReader<Integer>() {
                    @Override
                    public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        if (atomicInteger.get() > 50) {
                            return null;
                        }
                        return atomicInteger.getAndIncrement();
                    }
                })
                .processor(new ItemProcessor<Integer, Integer>() {
                    @Override
                    public Integer process(Integer item) throws Exception {
                        if (item % 3 == 0) {
                            throw new Exception(" item % 3 is 0");
                        } else if (item % 2 == 0) {
                            return null;
                        }
                        return item;
                    }
                })
                .writer(items -> items.forEach(x -> System.out.println("write : " + x)))
                .faultTolerant()
                .skipLimit(10)
                .skip(Exception.class)
                .listener(new CountItemProcessorLis())
                .build();
    }

}
