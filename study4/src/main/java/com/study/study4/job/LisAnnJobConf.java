package com.study.study4.job;

import com.study.study4.listener.AnnJobListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
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
@EnableBatchProcessing
@Configuration
public class LisAnnJobConf{

//    @Bean
//    public String runJob(JobLauncher jobLauncher,Job annJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
//        jobLauncher.run(annJob, new JobParametersBuilder().addLong("id", 2L).toJobParameters());
//        return "";
//    }

    @Bean
    public Job annJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        return jobBuilderFactory.get("study4-anno")
                .start(stepBuilderFactory.get("study-anno")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("exec");
                        return RepeatStatus.FINISHED;
                    }
                })
                        .listener(new AnnJobListener())
                        .build())
//                .listener(annJobListener)
                .listener(new JobExecutionListenerSupport())
                .build();
    }

}
