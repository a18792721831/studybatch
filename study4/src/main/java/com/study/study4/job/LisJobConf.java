package com.study.study4.job;

import com.study.study4.listener.JobListener;
import org.springframework.batch.core.*;
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

/**
 * @author jiayq
 * @Date 2020-11-09
 */
@Configuration
@EnableBatchProcessing
public class LisJobConf {

//    @Bean
//    public String runJob(JobLauncher jobLauncher, Job lisJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
//        jobLauncher.run(lisJob, new JobParametersBuilder().addLong("id", 1L).toJobParameters());
//        return "";
//    }

    @Bean
    public Job lisJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, JobListener jobListener) {
        return jobBuilderFactory.get("study4-lis")
                .start(stepBuilderFactory.get("study4-step")
                        .tasklet(new Tasklet() {
                            @Override
                            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                                System.out.println("exec");
                                return RepeatStatus.FINISHED;
                            }
                        }).build())
                .listener(jobListener)
                .build();
    }

}
