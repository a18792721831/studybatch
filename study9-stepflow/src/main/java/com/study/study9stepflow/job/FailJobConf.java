package com.study.study9stepflow.job;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author jiayq
 * @Date 2020-12-12
 */
//@Component
public class FailJobConf {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @PostConstruct
    public void runJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job(), new JobParametersBuilder().addLong("id", 2L).toJobParameters());
    }

    private Job job() {
        Step step1 = step1();
        Step step2 = step2();
        return jobBuilderFactory.get("fail-job")
                .start(step1)
                .on("fail").fail()
                .from(step1).on("*").to(step2)
                .next(step2)
                .from(step2).on("fail").fail()
                .build().build();
    }

    private Step step1() {
        return stepBuilderFactory.get("fail-step1")
                .tasklet((sc, cc) -> {
                    sc.setExitStatus(new ExitStatus("fail1"));
                    System.out.println(sc.getStepExecution().getStepName());
                    return RepeatStatus.FINISHED;
                })
                .allowStartIfComplete(true)
                .build();
    }

    private Step step2() {
        return stepBuilderFactory.get("fail-step2")
                .tasklet((sc, cc) -> {
                    sc.setExitStatus(new ExitStatus("fail"));
                    System.out.println(sc.getStepExecution().getStepName());
                    return RepeatStatus.FINISHED;
                })
                .allowStartIfComplete(true)
                .build();
    }
}
