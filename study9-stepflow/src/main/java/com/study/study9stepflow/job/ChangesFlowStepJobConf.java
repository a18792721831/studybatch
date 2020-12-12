package com.study.study9stepflow.job;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jiayq
 * @Date 2020-12-12
 */
@EnableBatchProcessing
//@Configuration
public class ChangesFlowStepJobConf {

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

    private Job job() {
        Map<Integer, Step> temps = new HashMap<>();
        temps.put(-1, getStep(null, "0"));
        temps.put(0, getStep(0, "1"));
        temps.put(1, getStep(1, "2"));
        temps.put(2, getStep(2, "3"));
        temps.put(3, getStep(3, "4"));
        temps.put(4, getStep(4, "y"));
        return jobBuilderFactory.get("changes-flow-step-job")
                // s 开始节点,返回 0
                .start(temps.get(-1))
                // s -> "0" -> s0
                .on("0").to(temps.get(0))
                // s -> "1" -> s1
                .from(temps.get(-1)).on("1").to(temps.get(1))
                // s -> "2" -> s2
                .from(temps.get(-1)).on("2").to(temps.get(2))
                // s -> "3" -> s3
                .from(temps.get(-1)).on("3").to(temps.get(3))
                // s -> "4" -> s4
                .from(temps.get(-1)).on("4").to(temps.get(4))
                // s0 -> "1" -> s1
                .from(temps.get(0)).on("1").to(temps.get(1))
                // s0 -> "1" -> s1
                .from(temps.get(0)).on("2").to(temps.get(2))
                // s0 -> "1" -> s1
                .from(temps.get(0)).on("3").to(temps.get(3))
                // s0 -> "1" -> s1
                .from(temps.get(0)).on("4").to(temps.get(4))
                // s1 -> "2" -> s2
                .from(temps.get(1)).on("2").to(temps.get(2))
                // s1 -> "3" -> s3
                .from(temps.get(1)).on("3").to(temps.get(3))
                // s1 -> "4" -> s4
                .from(temps.get(1)).on("4").to(temps.get(4))
                // s2 -> "3" -> s3
                .from(temps.get(2)).on("3").to(temps.get(3))
                // s2 -> "4" -> s4
                .from(temps.get(2)).on("4").to(temps.get(4))
                // s3 -> "4" -> s4
                .from(temps.get(3)).on("4").to(temps.get(4))
                // s4 -> "y" -> y
                .from(temps.get(4)).on("y").to(getSteps("y", FlowExecutionStatus.COMPLETED.getName()))
                // s4 -> "n" -> n
                .from(temps.get(4)).on("n").to(getSteps("n", FlowExecutionStatus.FAILED.getName()))
                .end()
                .build();
    }

    private Step getStep(Integer x, String result) {
        return stepBuilderFactory.get("s" + (x == null ? "" : x))
                .tasklet((sc, cc) -> {
                    System.out.println(sc.getStepExecution().getStepName() + " run  >>>>>>>  ");
                    sc.setExitStatus(new ExitStatus(result));
                    return RepeatStatus.FINISHED;
                }).build();
    }

    private Step getSteps(String x, String result) {
        return stepBuilderFactory.get(x)
                .tasklet((sc, cc) -> {
                    System.out.println(sc.getStepExecution().getStepName() + " run  >>>>>>>  ");
                    sc.setExitStatus(new ExitStatus(result));
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
