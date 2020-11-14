package com.study.study4.job;

import com.study.study4.validator.ParameValidatory;
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

import java.time.LocalTime;

/**
 * @author jiayq
 * @Date 2020-11-10
 */
@EnableBatchProcessing
@Configuration
public class ParameJobConf {


//    @Bean
//    public String runJob(JobLauncher jobLauncher,Job parJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
//        jobLauncher.run(parJob,new JobParametersBuilder().addLong("id", 3L).toJobParameters());
//        return "";
//    }

    @Bean
    public Job parJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ParameValidatory parameValidatory) {
        return jobBuilderFactory.get("study4-parame")
                .validator(parameValidatory)
                .start(stepBuilderFactory.get("study4-parame")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("exec!"+ LocalTime.now());
                        return RepeatStatus.FINISHED;
                    }
                }).build()).build();
    }

}
