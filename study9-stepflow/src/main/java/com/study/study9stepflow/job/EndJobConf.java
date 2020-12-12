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
import java.util.Date;

/**
 * @author jiayq
 * @Date 2020-12-12
 */
//@Component
public class EndJobConf {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @PostConstruct
    public void runJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job(), new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
    }

    private Job job() {
        Step step1 = step1();
        Step step2 = step2();
        return jobBuilderFactory.get("end-job")
                .start(step1)
                .on("quit").end("step1")
                .from(step1).on("*").to(step2)
                .next(step2)
                .from(step2).on("ok").end("step2")
                .build().build();
    }

    private Step step1(){
        return stepBuilderFactory.get("end-step1")
                .tasklet((sc,cc)->{
//                    sc.setExitStatus(new ExitStatus("quit"));
                    System.out.println(sc.getStepExecution().getStepName());
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    private Step step2() {
        return stepBuilderFactory.get("end-step2")
                .tasklet((sc,cc)->{
                    sc.setExitStatus(new ExitStatus("ok"));
                    System.out.println(sc.getStepExecution().getStepName());
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
