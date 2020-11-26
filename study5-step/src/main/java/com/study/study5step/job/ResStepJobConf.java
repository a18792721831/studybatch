package com.study.study5step.job;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author jiayq
 * @Date 2020-11-20
 */
@EnableBatchProcessing
//@Configuration
public class ResStepJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher,Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder().addLong("num", 8L)
        .toJobParameters());
        return "";
    }

    @Bean
    public Job job(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, InterruptedException {
        return jobBuilderFactory.get("restart-step-job")
                .start(get(stepBuilderFactory))
                .next(get(stepBuilderFactory))
                .next(get(stepBuilderFactory))
                .next(get(stepBuilderFactory))
                .next(get(stepBuilderFactory))
                .next(get(stepBuilderFactory))
                .next(get(stepBuilderFactory))
                .next(get(stepBuilderFactory))
                .build();
    }


    private Step get(StepBuilderFactory stepBuilderFactory) throws InterruptedException {
        Random random = new Random();
        TimeUnit.SECONDS.sleep(random.nextInt(10));
        return stepBuilderFactory.get("restart-stpe-" + LocalDateTime.now().getSecond())
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        String name;
                        System.out.println(name = chunkContext.getStepContext().getStepName());
                        if (name.contains(chunkContext.getStepContext().getJobParameters().get("num").toString())){
//                            throw new Exception(name);
                        }
                        return RepeatStatus.FINISHED;
                    }
                })
                .startLimit(4)
                .allowStartIfComplete(true)
                .build();
    }
}
