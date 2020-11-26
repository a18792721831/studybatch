package com.study.study5step.job;

import com.study.study5step.listener.StepAListener;
import com.study.study5step.listener.StepBListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.listener.CompositeStepExecutionListener;
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
public class ComStepLisJobCOnf {

    @Bean
    public String runJob(JobLauncher jobLauncher,Job comLisJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(comLisJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job comLisJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                         StepAListener stepAListener, StepBListener stepBListener) {
        CompositeStepExecutionListener listener = new CompositeStepExecutionListener();
        listener.register(stepAListener);
        listener.register(stepBListener);
        return jobBuilderFactory.get("study5-step-job-com-job")
                .start(stepBuilderFactory.get("study5-step-step-com-lis")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println(chunkContext.getStepContext().getStepName() + " exec " + LocalDateTime.now());
                        return RepeatStatus.FINISHED;
                    }
                }).listener(listener).build()).build();
    }

}
