package com.study.study7itemprocess.job;

import com.study.study7itemprocess.listener.AnnoItemProcessorLis;
import com.study.study7itemprocess.listener.MyItemProcessorLis;
import com.study.study7itemprocess.service.IntegerService;
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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiayq
 * @Date 2020-12-05
 */
@EnableBatchProcessing
@Configuration
public class IntefaceItemProcessListenerJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder()
                .addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("inteface-item-processor-listener-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, IntegerService integerService) {
        AtomicInteger atomicInteger = new AtomicInteger();
        ItemProcessorAdapter<Integer, Integer> processorAdapter = new ItemProcessorAdapter<>();
        processorAdapter.setTargetObject(integerService);
        processorAdapter.setTargetMethod("addOne");
        return stepBuilderFactory.get("inteface-item-processor-listener-step")
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
                .processor(processorAdapter)
                .writer(items -> items.forEach(x -> System.out.println("write : " + x)))
                .listener(new MyItemProcessorLis())
                .listener(new AnnoItemProcessorLis())
                .build();
    }

}
