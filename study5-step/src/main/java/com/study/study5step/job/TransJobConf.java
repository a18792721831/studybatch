package com.study.study5step.job;

import com.study.study5step.exception.LessZoreException;
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
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jiayq
 * @Date 2020-11-24
 */
@EnableBatchProcessing
//@Configuration
public class TransJobConf {

    private Integer integer = 0;

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder().addLong("id", 28L).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("trans-job-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("tran-step-step")
                .<Integer, Integer>chunk(3)
                .reader(reader())
                .processor(processor())
                .writer(writer())
//                .readerIsTransactionalQueue()
                .faultTolerant()
                .processorNonTransactional()
                .retry(LessZoreException.class)
                .retryLimit(10)
                .build();
    }

    private ItemWriter<Integer> writer() {
        return new ItemWriter<Integer>() {
            @Override
            public void write(List<? extends Integer> items) throws Exception {
                for (Integer item : items) {
                    System.out.println("item = <" + item + "> ### ");
                    if (item == 3) {
                        throw new LessZoreException(" it's time !");
                    }
                }
            }
        };
    }

    private ItemProcessor processor() {
        return new ItemProcessor<Integer, Integer>() {
            @Override
            public Integer process(Integer item) throws Exception {
                return item;
            }
        };
    }

    private ItemReader<Integer> reader() {
        return new ItemReader<Integer>() {
            @Override
            public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                if (integer > 10) {
                    return null;
                }
                return integer++;
            }
        };
    }
}
