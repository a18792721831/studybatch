package com.study.study4.job;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Date;

/**
 * @author jiayq
 * @Date 2020-11-11
 */
@EnableBatchProcessing
@Configuration
public class ExJobConf {

//    @Bean
//    public String runExJob(JobLauncher jobLauncher, Job playGameSimpleJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
//        jobLauncher.run(playGameSimpleJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
//        return "";
//    }

    @Bean
    public PlayGameSimpleJob playGameSimpleJob(JobRepository jobRepository, Step playGameStep) {
        PlayGameSimpleJob playGameSimpleJob = new PlayGameSimpleJob("study4-play-game-job");
        playGameSimpleJob.setJobRepository(jobRepository);
        playGameSimpleJob.setSteps(Arrays.asList(playGameStep));
        return playGameSimpleJob;
    }

    @Bean
    public Step playGameStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("study4-play-game-step")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println(" play game step ");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }
}
