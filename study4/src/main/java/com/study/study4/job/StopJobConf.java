package com.study.study4.job;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author jiayq
 * @Date 2020-11-14
 */
@EnableScheduling
public class StopJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job stopJob, JobOperator jobOperator) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, InterruptedException, NoSuchJobException, NoSuchJobExecutionException, JobExecutionNotRunningException {
        jobLauncher.run(stopJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        TimeUnit.SECONDS.sleep(15);
        jobOperator.stop(jobOperator.getRunningExecutions(stopJob.getName()).iterator().next());
        return "";
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new ConcurrentTaskExecutor());
        return jobLauncher;
    }

    @Bean
    public Job stopJob(JobBuilderFactory jobBuilderFactory, Step stopStep1, Step stopStep2, Step stopStep3) {
        return jobBuilderFactory.get("study4-stop-job")
                .start(stopStep1)
                .next(stopStep2)
                .next(stopStep3)
                .build();
    }


    @Bean
    public Step stopStep1(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("study4-stop-step-1")
                .tasklet(new StopTasklet()).build();
    }

    @Bean
    public Step stopStep2(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("study4-stop-step-2")
                .tasklet(new StopTasklet()).build();
    }

    @Bean
    public Step stopStep3(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("study4-stop-step-3")
                .tasklet(new StopTasklet()).build();
    }

    class StopTasklet implements Tasklet {

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            int sum = 10;
            while (sum > 0) {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("stop step " + chunkContext.getStepContext().getStepName() + " exec " + LocalDateTime.now());
                sum--;
            }
            return RepeatStatus.FINISHED;
        }
    }
}
