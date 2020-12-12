package com.study.study9stepflow.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author jiayq
 * @Date 2020-12-12
 */
//@Component
public class SplitFlowStepJobConf {

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
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(3);
        executor.setCorePoolSize(3);
        executor.initialize();
        return jobBuilderFactory.get("split-flow-step-job")
                .start(getStep("init", 5))
                .split(executor)
                .add(new FlowBuilder<Flow>("flow1").start(getStep("start", 10))
                                .next(getStep("fs1", 10))
                                .next(getStep("fs2", 10))
                                .build(),
                        new FlowBuilder<Flow>("flow2").start(getStep("start", 10))
                                .next(getStep("fs1", 10))
                                .next(getStep("fs2", 10))
                                .build())
                .next(stepBuilderFactory.get("clean")
                        .tasklet((sc, cc) -> {
                            executor.shutdown();
                            return RepeatStatus.FINISHED;
                        }).build())
                .on("x").stop()
                .on("xx").fail()
                .on("xxx").end()
                .build().build();

    }

    private Step getStep(String name, Integer seconds) {
        return stepBuilderFactory.get(name)
                .tasklet((sc, cc) -> {
                    System.out.println(Thread.currentThread().getName() + " step : " + sc.getStepExecution().getStepName() + " time : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:MM:SS")) + " run >>>>> sleep : " + seconds);
                    TimeUnit.SECONDS.sleep(seconds);
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
