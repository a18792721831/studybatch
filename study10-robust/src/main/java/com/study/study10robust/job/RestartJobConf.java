package com.study.study10robust.job;

import com.study.study10robust.exception.MyException;
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author jiayq
 * @Date 2020-12-19
 */
@Component
public class RestartJobConf {

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

    public Job job() {
        return jobBuilderFactory.get("restart-job")
                .start(step1())
                .next(step2())
                // 不允许重启，即使失败
//                .preventRestart()
                .build();
    }

    public Step step1() {
        return stepBuilderFactory.get("retsart-job-step1")
                .tasklet((sc, cc) -> {
                    System.out.println(sc.getStepExecution().getStepName());
                    return RepeatStatus.FINISHED;
//                    throw new MyException("throw always tasklet");
                })
                .allowStartIfComplete(true)
                .startLimit(3)
                .build();
    }

    public Step step2() {
        return stepBuilderFactory.get("retsart-job-step2")
                .tasklet((sc, cc) -> {
                    System.out.println(sc.getStepExecution().getStepName());
//                    return RepeatStatus.FINISHED;
                    throw new MyException("throw always tasklet");
                })
                .build();
    }

}
