package com.study.study5step.job;

import com.study.study5step.listener.AnnoCListener;
import com.study.study5step.step.StepBuilder;
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
public class AnnoStepLisJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job annoStepJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(annoStepJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job annoStepJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, AnnoCListener annoCListener) {
        return jobBuilderFactory.get("study5-anno-listener-job")
                .start(stepBuilderFactory.get("study5-anno-listener-step")

                        .tasklet(new Tasklet() {
                            @Override
                            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                                System.out.println(chunkContext.getStepContext().getStepName() + " exec " + LocalDateTime.now());
                                return RepeatStatus.FINISHED;
                            }
                        }).listener(annoCListener).build()).build();
    }

}
