package com.study.study5step.job;

import org.springframework.batch.core.*;
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
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiayq
 * @Date 2020-11-21
 */
@EnableBatchProcessing
//@Configuration
public class ChunkTimeJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher,Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,Step step) {
        return jobBuilderFactory.get("chunk-time-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("chunk-time-step")
                .<Long,Long>chunk(100)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    private ItemProcessor<Long,Long> processor(){
        return new ItemProcessor<Long, Long>() {
            @Override
            public Long process(Long item) throws Exception {
                return item;
            }
        };
    }

    private ItemWriter<Long> writer() {
        return new ItemWriter<Long>() {
            @Override
            public void write(List<? extends Long> items) throws Exception {
                System.out.println(items.size());
            }
        };
    }

    private ItemReader<Long> reader(){
        List<Long> longs = new ArrayList<>(1000);
        for (long i = 0; i < 1000; i++) {
            longs.add(i);
        }
        return new ListItemReader<Long>(longs);
    }

}
