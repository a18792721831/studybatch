package com.study.study5step.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.util.concurrent.Callable;

/**
 * @author jiayq
 * @Date 2020-11-21
 */
@EnableBatchProcessing
//@Configuration
public class TaskTrJobConf {



    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,Step step){
        return jobBuilderFactory.get("tasklet-tr-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, PlatformTransactionManager stepManager, TransactionAttribute stepAttribute) {
        return stepBuilderFactory.get("tasklet-tr-step")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("exec");
                        return RepeatStatus.FINISHED;
                    }
                })
                .transactionManager(stepManager)
                .transactionAttribute(stepAttribute)
                .build();

    }


    private Tasklet callTasklet() {
        Callable<RepeatStatus> callable = () -> {
            System.out.println("call");
            return RepeatStatus.FINISHED;
        };
        CallableTaskletAdapter adapter = new CallableTaskletAdapter();
        adapter.setCallable(callable);
        return adapter;
    }

}
