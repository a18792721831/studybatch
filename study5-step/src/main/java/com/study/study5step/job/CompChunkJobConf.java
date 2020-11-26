package com.study.study5step.job;

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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.policy.CompletionPolicySupport;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiayq
 * @Date 2020-11-23
 */
@EnableBatchProcessing
//@Configuration
public class CompChunkJobConf {

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Bean
    public String jobRun(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder().addLong("id", 1L).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,Step step) {
        return jobBuilderFactory.get("comp-chunk-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("comp-chunk-step")
//                .<Integer,Integer>chunk(3)
                .chunk(new MyPolicy())
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();

    }

    private ItemWriter<Integer> writer() {
        return new ItemWriter<Integer>() {
            @Override
            public void write(List<? extends Integer> items) throws Exception {
                System.out.println("writer : " + items);
            }
        };
    }

    private ItemProcessor processor() {
        return new ItemProcessor<Integer, Integer>() {
            @Override
            public Integer process(Integer item) throws Exception {
                System.out.println("process : <" + item + "> " + atomicInteger.incrementAndGet());
                return null;
            }
        };
    }

    private ItemReader<Integer> reader() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add(i);
        }
        System.out.println("raeder : " + result);
        return new ListItemReader<>(result);
    }
    class MyPolicy extends CompletionPolicySupport {
        @Override
        public boolean isComplete(RepeatContext context) {
            if (context.getStartedCount() % 3 == 0) {
                return true;
            } else {
                return false;
            }
        }
    }
}

