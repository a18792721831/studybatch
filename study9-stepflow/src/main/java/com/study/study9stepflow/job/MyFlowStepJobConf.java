package com.study.study9stepflow.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * @author jiayq
 * @Date 2020-12-09
 */
@EnableBatchProcessing
//@Configuration
public class MyFlowStepJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        return jobBuilderFactory.get("my-flow-step-job")
                .start(stepBuilderFactory.get("x").tasklet((sc,cc)->RepeatStatus.FINISHED).build())
//                .start(step1(stepBuilderFactory))
//                .next(step2(stepBuilderFactory))
//                .next(step3(stepBuilderFactory))
//                .next(step4(stepBuilderFactory))
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("my-flow-step-step1")
                .tasklet((sc, cc) -> {
                    System.out.println(sc.getStepExecution().getStepName() + " run >>>>> ");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step step2(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("my-flow-step-step2")
                .tasklet((sc, cc) -> {
                    System.out.println(sc.getStepExecution().getStepName() + " run >>>>> ");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step step3(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("my-flow-step-step3")
                .tasklet((sc, cc) -> {
                    System.out.println(sc.getStepExecution().getStepName() + " run >>>>> ");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step step4(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("my-flow-step-step4")
                .tasklet((sc, cc) -> {
                    System.out.println(sc.getStepExecution().getStepName() + " run >>>>> ");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
