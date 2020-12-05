package com.study.study7itemprocess.job;

import com.study.study7itemprocess.listener.CountItemProcessorLis;
import com.study.study7itemprocess.vilidat.MyValidator;
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
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiayq
 * @Date 2020-12-05
 */
@EnableBatchProcessing
//@Configuration
public class ComposeiteItemProcessorJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder()
                .addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("composite-item-processor-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory) {
        AtomicInteger atomicInteger = new AtomicInteger();
        CompositeItemProcessor<Integer, Integer> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(Arrays.asList(processor1(), processor2()));
        return stepBuilderFactory.get("composite-item-processor-step")
                .<Integer, Integer>chunk(3)
                .reader(new ItemReader<Integer>() {
                    @Override
                    public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        if (atomicInteger.get() > 10) {
                            return null;
                        }
                        return atomicInteger.getAndIncrement();
                    }
                })
                .processor(compositeItemProcessor)
                .writer(items -> items.forEach(x -> System.out.println("write : " + x)))
                .build();
    }

    private ItemProcessor<Integer, Integer> processor1() {
        return new ItemProcessor<Integer, Integer>() {
            @Override
            public Integer process(Integer item) throws Exception {
                System.out.println("processor1 : " + item);
                return item;
            }
        };
    }

    private ItemProcessor<Integer, Integer> processor2() {
        return new ItemProcessor<Integer, Integer>() {
            @Override
            public Integer process(Integer item) throws Exception {
                System.out.println("processor2 : " + item);
                return item;
            }
        };
    }
}
