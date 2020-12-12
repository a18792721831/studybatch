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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiayq
 * @Date 2020-12-09
 */
@EnableBatchProcessing
//@Configuration
public class ChangeFlowStepJobConf {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory) {
        Step[] st = new Step[5];
        Step step = stepBuilderFactory.get("change-flow-step-6").tasklet((sc, cc) -> {
            System.out.println(sc.getStepExecution().getStepName() + " run >>>>> ");
            sc.setExitStatus(ExitStatus.FAILED);
            return RepeatStatus.FINISHED;
        }).build();
        steps().toArray(st);
        return jobBuilderFactory.get("change-flow-step-job")
                .start(step)
                // 配置规则 on:配置匹配的字符串; to:匹配后执行; from:匹配谁
                // 简单来说 from 谁 on 配置了 to 执行
                // 建议使用 from-on-to
                // 否则这个关系很容易配错
                // step -> "COMPLETED" -> st[1]
                .on(FlowExecutionStatus.COMPLETED.getName()).to(st[1])
                // step -> "FAILED" -> st[0]
                .from(step).on(FlowExecutionStatus.FAILED.getName()).to(st[0])
                // st[0] -> "COMPLETED" -> st[1]
                .from(st[0]).on("COMPLETED").to(st[1])
                // step -> "*" -> st[2]
                .from(step).on("*").to(st[2])
                // st[1] -> "COMPLETED" -> st[3]
                .from(st[1]).on(FlowExecutionStatus.COMPLETED.getName()).to(st[3])
                // st[1] -> "*" -> st[4]
                .from(st[1]).on("*").to(st[4])
                .end()
                .build();
    }

    private List<Step> steps() {
        return Arrays.asList(0, 1, 2, 3, 4).stream().map(
                x -> stepBuilderFactory.get("change-flow-step-" + x).tasklet((sc, cc) -> {
                    System.out.println(sc.getStepExecution().getStepName() + " run >>>>> ");
                    return RepeatStatus.FINISHED;
                }).build())
                .collect(Collectors.toList());
    }

}
