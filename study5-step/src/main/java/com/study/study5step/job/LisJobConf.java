package com.study.study5step.job;

import com.study.study5step.exception.LessZoreException;
import com.study.study5step.listener.*;
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
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-11-25
 */
@EnableBatchProcessing
@Configuration
public class LisJobConf {

    private Integer integer = 0;

    @Bean
    public String runJob(JobLauncher jobLauncher,Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job,new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        return "";
    }
    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,Step step) {
        return jobBuilderFactory.get("lis-job-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("lis-step-step")
                .<Integer,Integer>chunk(3)
                .reader(()-> {
                    if (integer > 10) {
                        return null;
                    }
                    return integer++;
                })
                .processor(processor())
                .writer(items -> System.out.println(items.size()))
                .listener(new ItemReadLis())
                .listener(new ItemProcessLis())
                .listener(new ItemWriteLis())
                .faultTolerant()
                .listener(new ChunkLis())
                .listener(new SkipLis())
                .listener(new RetryLis())
                .retry(LessZoreException.class)
                .retryLimit(3)
                .skip(LessZoreException.class)
                .skipLimit(2)
                .build();
    }

    private ItemProcessor<Integer,Integer> processor() {
        return new ItemProcessor<Integer, Integer>() {
            @Override
            public Integer process(Integer item) throws Exception {
                System.out.println("process = " + item);
                if(item == 3) {
                    throw new LessZoreException(" it's time 3 ");
                }
                return item;
            }
        };
    }
}
