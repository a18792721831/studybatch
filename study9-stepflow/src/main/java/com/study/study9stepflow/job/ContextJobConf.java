package com.study.study9stepflow.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * @author jiayq
 * @Date 2020-12-12
 */
//@Component
public class ContextJobConf {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @PostConstruct
    public void runJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job(), new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
    }

    private Job job(){
        return jobBuilderFactory.get("context-job")
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }

    private Step step1() {
        return stepBuilderFactory.get("context-job-step")
                .tasklet((sc,cc)->{
                    sc.getStepExecution().getExecutionContext().put("message", "tasklet!!!");
                    System.out.println(sc.getStepExecution().getExecutionContext().get("message"));
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    private Step step2() {
        return stepBuilderFactory.get("context-job-step")
                .tasklet((sc,cc)->{
                    sc.getStepExecution().getJobExecution().getExecutionContext().put("jmsg","job message!!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    private Step step3() {
        return stepBuilderFactory.get("context-job-step")
                .tasklet((sc,cc)->{
                    System.out.println(sc.getStepExecution().getJobExecution().getExecutionContext().get("jmsg"));
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
