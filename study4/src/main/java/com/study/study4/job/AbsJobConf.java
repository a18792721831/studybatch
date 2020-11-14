package com.study.study4.job;

import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
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

import java.time.LocalTime;

/**
 * @author jiayq
 * @Date 2020-11-11
 */
@Configuration
@EnableBatchProcessing
public class AbsJobConf {

//    @Bean
//    public String runAbsJob(PlaySimpleJob playSimpleJob, SleepSimpleJob sleepSimpleJob, JobLauncher jobLauncher) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
//        jobLauncher.run(playSimpleJob, new JobParametersBuilder().addLong("id", 3L).toJobParameters());
//        jobLauncher.run(sleepSimpleJob, new JobParametersBuilder().addLong("id", 3L).toJobParameters());
//        return "";
//    }

    @Bean
    public PlaySimpleJob playSimpleJob(JobRepository jobRepository, Step absStep) {
        PlaySimpleJob playSimpleJob = new PlaySimpleJob();
        playSimpleJob.setName("study4-play-simple-job");
        playSimpleJob.setJobRepository(jobRepository);
        playSimpleJob.addStep(absStep);
        return playSimpleJob;
    }

    @Bean
    public SleepSimpleJob sleepSimpleJob(JobRepository jobRepository, Step absStep) {
        SleepSimpleJob sleepSimpleJob = new SleepSimpleJob();
        sleepSimpleJob.setName("study4-sleep-simpleo-job");
        sleepSimpleJob.setJobRepository(jobRepository);
        sleepSimpleJob.addStep(absStep);
        return sleepSimpleJob;
    }

    @Bean
    public Step absStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("study4-abs-step")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("exec ! " + LocalTime.now());
                        return RepeatStatus.FINISHED;
                    }
                }).build();

    }

}
