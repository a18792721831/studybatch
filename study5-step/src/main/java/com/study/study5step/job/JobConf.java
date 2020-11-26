package com.study.study5step.job;

import com.study.study5step.step.PlayAStep;
import com.study.study5step.step.PlayBStep;
import com.study.study5step.step.StepBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * @author jiayq
 * @Date 2020-11-14
 */
@EnableBatchProcessing
//@Configuration
public class JobConf {

    @Bean
    public String runAbsJob(JobLauncher jobLauncher, Job absJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(absJob, new JobParametersBuilder()
                .addLong("time", 10L)
                .addDate("date", new Date())
                .toJobParameters());
        return "";
    }

    @Bean
    public Job absJob(JobBuilderFactory jobBuilderFactory, StepBuilder stepBuilder) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return jobBuilderFactory.get("study5-abs-job")
                .start(stepBuilder.get(PlayAStep.class))
                .next(stepBuilder.get(PlayBStep.class))
                .validator(new DefaultJobParametersValidator(new String[]{"time"}, new String[]{}))
                .build();
    }

}
