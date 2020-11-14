package com.study.study4.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiayq
 * @Date 2020-11-11
 */
@EnableBatchProcessing
@Configuration
public class ScoJobConf {

    private AtomicInteger atomicInteger = new AtomicInteger();

//    @Bean
//    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
//        jobLauncher.run(job, new JobParametersBuilder()
//                .addString("stepName", "study4-sco-step")
//                .addString("name", "   sco   ")
//                .addLong("time", 10L)
//                .addString("writeMessage", " write msg ")
//                .addString("processMessage", " process msg")
//                .addDate("date", new Date())
//                .toJobParameters());
//        return "";
//    }


    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("study4-sco-job")
                .start(step)
                .validator(new DefaultJobParametersValidator(new String[]{"stepName", "name", "time"}, new String[]{"writeMessage", "processMessage", "date"}))
                .build();
    }

    @Bean
    @JobScope
    public Step step(ItemReader<String> reader, ItemProcessor<String, String> processor, ItemWriter<String> writer, StepBuilderFactory stepBuilderFactory, @Value("#{jobParameters['stepName']}") String stepName) {
        return stepBuilderFactory.get(stepName)
                .<String, String>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<String, String> processor(@Value("#{jobParameters['processMessage']}") String processMessage) {
        return new ItemProcessor<String, String>() {
            @Override
            public String process(String item) throws Exception {
                System.out.println(processMessage + item);
                return item;
            }
        };
    }

    @Bean
    @StepScope
    public ItemWriter<String> writer(@Value("#{jobParameters['writeMessage']}") String writeMessage) {
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                items.stream().forEach(x -> System.out.println(writeMessage + x));
            }
        };
    }

    @Bean
    @StepScope
    public ItemReader<String> reader(@Value("#{jobParameters['name']}") String name, @Value("#{jobParameters['time']}") int time) {
        ItemReader reader = new ItemReader<String>() {
            @Override
            public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                if (atomicInteger.incrementAndGet() > 20) {
                    return null;
                }
                return name;
            }
        };
        return reader;
    }

}
