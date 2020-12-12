package com.study.study9stepflow.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
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
 * @Date 2020-12-10
 */
@EnableBatchProcessing
//@Configuration
public class DeciFlowJobConf {

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
        temps.put(-1, getStep("s"));
        temps.put(0, getStep("s0"));
        temps.put(1, getStep("s1"));
        temps.put(2, getStep("s2"));
        temps.put(3, getStep("s3"));
        temps.put(4, getStep("s4"));
        temps.put(5, getStep("y"));
        temps.put(6, getStep("n"));
        Map<Integer, JobExecutionDecider> deciderMap = new HashMap<>();
        deciderMap.put(-1, getDec("start"));
        deciderMap.put(0, getDec("1"));
        deciderMap.put(1, getDec("2"));
        deciderMap.put(2, getDec("3"));
        deciderMap.put(3, getDec("4"));
        deciderMap.put(4, getDec("y"));
        deciderMap.put(5, getDec("n"));
        return jobBuilderFactory.get("changes-flow-step-job")
                // step[s] 开始节点
                .start(temps.get(-1))
                // dec[start] 开始节点的控制节点
                .start(deciderMap.get(-1))
                // dec[start] -> step[s0]
                .from(deciderMap.get(-1)).on("start").to(temps.get(0))
                // dec[*] -> step[n]
                .from(deciderMap.get(-1)).on("*").to(temps.get(6))
                // step[s0] -> dec[0]
                .from(temps.get(0)).on("*").to(deciderMap.get(0))
                // dec[0] -> step[s1]
                .from(deciderMap.get(0)).on("1").to(temps.get(1))
                // step[s1] -> dec[1]
                .from(temps.get(1)).on("*").to(deciderMap.get(1))
                // dec[1] -> step[s2]
                .from(deciderMap.get(1)).on("2").to(temps.get(2))
                // step[s2] -> dec[2]
                .from(temps.get(2)).on("*").to(deciderMap.get(2))
                // dec[2] -> step[s3]
                .from(deciderMap.get(2)).on("3").to(temps.get(3))
                // step[s3] -> dec[3]
                .from(temps.get(3)).on("*").to(deciderMap.get(3))
                // dec[3] -> step[s4]
                .from(deciderMap.get(3)).on("4").to(temps.get(4))
                // step[s4] -> dec[4]
                .from(temps.get(4)).on("*").to(deciderMap.get(4))
                // dec[4] -> step[y]
                .from(deciderMap.get(4)).on("y").to(temps.get(5))
                .end()
                .build();
    }

    private Step getStep(String x) {
        return stepBuilderFactory.get(x)
                .tasklet((sc, cc) -> {
                    System.out.println(sc.getStepExecution().getStepName() + " run  >>>>>>>  ");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    private JobExecutionDecider getDec(String result) {
        return (je,se) -> {
            if (se == null) {
                System.out.println(je.getJobInstance().getJobName() + " -> " + result);
                return new FlowExecutionStatus(result);
            } else {
                System.out.println(se.getStepName() + " status : " + se.getExitStatus().getExitCode() + " -> " + result);
                return new FlowExecutionStatus(result);
            }
        };
    }

}
