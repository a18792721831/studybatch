package com.study.study5step.job;

import com.study.study5step.listener.StepAListener;
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

/**
 * @author jiayq
 * @Date 2020-11-16
 */
@EnableBatchProcessing
//@Configuration
public class StepLisJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job lisJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(lisJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job lisJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, StepAListener stepAListener) {
        return jobBuilderFactory.get("study5--job-step-listener")
                .start(stepBuilderFactory.get("study5-step-step-listener")
                        .tasklet(new Tasklet() {
                            @Override
                            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                                System.out.println(chunkContext.getStepContext().getStepName() + "exec " + LocalDateTime.now());
                                return RepeatStatus.FINISHED;
                            }
                        }).listener(stepAListener)
                        .build()).build();
    }

}
