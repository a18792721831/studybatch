package com.study.study4.job;

import io.micrometer.core.instrument.util.TimeUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author jiayq
 * @Date 2020-11-12
 */
@EnableBatchProcessing
@Configuration
public class LauJobConf {

//    @Bean
//    public String runLauJob(JobLauncher jobLauncher,Job lauJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
//        System.out.println("start " + LocalDateTime.now());
//        jobLauncher.run(lauJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
//        System.out.println("over " + LocalDateTime.now());
//        return "";
//    }

    @Bean
    public Job lauJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        return jobBuilderFactory.get("study4-lau-job")
                .start(stepBuilderFactory.get("study4-lau-step")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        TimeUnit.SECONDS.sleep(10);
                        return RepeatStatus.FINISHED;
                    }
                }).build())
                .build();
    }

}
