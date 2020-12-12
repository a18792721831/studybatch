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
@Component
public class StopJobConf {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @PostConstruct
    public void runJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job(), new JobParametersBuilder().addLong("id", 1L).toJobParameters());
    }

    private Job job() {
        Step step1 = step1();
        Step step2 = step2();
        return jobBuilderFactory.get("stop-job")
                .start(step1)
                .on("stop1").stop()
                .from(step1).on("stop1r").stopAndRestart(step1)
                .from(step1).on("*").to(step2)
                .next(step2)
                .from(step2).on("stop2").stop()
                .build().build();
    }

    private Step step1() {
        return stepBuilderFactory.get("stop-step1")
                .tasklet((sc, cc) -> {
                    sc.setExitStatus(new ExitStatus("stop1r"));
                    System.out.println(sc.getStepExecution().getStepName());
                    return RepeatStatus.FINISHED;
                })
                .allowStartIfComplete(true)
                .build();
    }

    private Step step2() {
        return stepBuilderFactory.get("stop-step2")
                .tasklet((sc, cc) -> {
                    sc.setExitStatus(new ExitStatus("stop2"));
                    System.out.println(sc.getStepExecution().getStepName());
                    return RepeatStatus.FINISHED;
                })
                .allowStartIfComplete(true)
                .build();
    }
}
