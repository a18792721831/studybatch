package com.study.study4.job;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author jiayq
 * @Date 2020-11-12
 */
@EnableBatchProcessing
public class SyncJobConf {

    @Autowired
    private JobOperator jobOperator;

    @Bean
    public String runLauJob(JobLauncher jobLauncher, Job lauJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, InterruptedException, NoSuchJobExecutionException, JobExecutionNotRunningException, NoSuchJobException {
        System.out.println("start " + LocalDateTime.now());
        jobLauncher.run(lauJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        System.out.println("over " + LocalDateTime.now());
        return "";
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new ConcurrentTaskExecutor());
        return jobLauncher;
    }

    @Bean
    public Job lauJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        return jobBuilderFactory.get("study4-lau-job")
                .start(stepBuilderFactory.get("study4-lau-step")
                        .tasklet(new Tasklet() {
                            @Override
                            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                                System.out.println("exec ! " + LocalDateTime.now());
                                TimeUnit.SECONDS.sleep(5);
                                jobOperator.stop(jobOperator.getRunningExecutions(chunkContext.getStepContext().getJobName()).iterator().next());
                                return RepeatStatus.FINISHED;
                            }
                        }).build())
                .build();
    }

}
