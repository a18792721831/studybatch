package com.study.study5step.job;

import com.study.study5step.exception.LessZoreException;
import com.study.study5step.listener.RetryLis;
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
import org.springframework.batch.core.step.skip.CompositeSkipPolicy;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiayq
 * @Date 2020-11-21
 */
@EnableBatchProcessing
//@Configuration
public class RetryJobConf {

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder().addLong("date", 10L).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("chunk-skip-jobx")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, RetryLis retryLis) {
        return stepBuilderFactory.get("chunk-skip-step")
                .<Long, Long>chunk(3)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                .retry(LessZoreException.class)
                .retryLimit(3)
                .retryPolicy(new SimpleRetryPolicy(3, Map.of(LessZoreException.class, true)))
                .skipLimit(5)
                .skip(LessZoreException.class)
                .skipPolicy(new LimitCheckingItemSkipPolicy(5,Map.of(LessZoreException.class,true)))
                .allowStartIfComplete(true)
                .build();
    }

    private ItemProcessor<Long, Long> processor() {
        return new ItemProcessor<Long, Long>() {
            @Override
            public Long process(Long item) throws Exception {
                System.out.println(" process <" + item + "> " + atomicInteger.incrementAndGet());
                throw new LessZoreException(item + " = 0 ");
            }
        };
    }

    private ItemWriter<Long> writer() {
        return new ItemWriter<Long>() {
            @Override
            public void write(List<? extends Long> items) throws Exception {
                System.out.println("writer : " + items);
            }
        };
    }

    private ItemReader<Long> reader() {
        int num = 10;
        List<Long> longs = new ArrayList<>(num);
        while (--num > 0) {
            longs.add((long) num);
        }
        ListItemReader<Long> longListItemReader = new ListItemReader<>(longs);
        System.out.println("reader : " + longs);
        return longListItemReader;
    }

}
