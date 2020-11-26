package com.study.study5step.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Callable;

/**
 * @author jiayq
 * @Date 2020-11-21
 */
@EnableBatchProcessing
//@Configuration
public class CallTaskJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher,Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,Step step) {
        return jobBuilderFactory.get("call-tasklet-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("callable-tasklet-step")
                .tasklet(callTasklet())
                .build();
    }

    private Tasklet callTasklet() {
        CallableTaskletAdapter adapter = new CallableTaskletAdapter();
        Callable<RepeatStatus> callable = () -> {
            System.out.println("call");
            return RepeatStatus.FINISHED;
        };
        adapter.setCallable(callable);
        return adapter;
    }

}
