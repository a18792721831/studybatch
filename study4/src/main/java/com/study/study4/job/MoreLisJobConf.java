package com.study.study4.job;

import com.study.study4.listener.AnnJobListener;
import com.study.study4.listener.JobListener;
import com.study.study4.listener.SecJobListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.listener.CompositeJobExecutionListener;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;

/**
 * @author jiayq
 * @Date 2020-11-10
 */
@EnableBatchProcessing
@Configuration
public class MoreLisJobConf {

//    @Bean
//    public String jobRunner(JobLauncher jobLauncher,Job moreLisJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
//        jobLauncher.run(moreLisJob, new JobParametersBuilder().addLong("id", 1L).toJobParameters());
//        return "";
//    }

    @Bean
    public Job moreLisJob(AnnJobListener annJobListener, JobListener jobListener, SecJobListener secJobListener, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        CompositeJobExecutionListener listener = new CompositeJobExecutionListener();
        listener.register(annJobListener);
        listener.register(jobListener);
        listener.register(secJobListener);
        return jobBuilderFactory.get("study4-more-listener")
                .start(stepBuilderFactory.get("study4-more-listener")
                        .tasklet(new Tasklet() {
                            @Override
                            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                                System.out.println("exec!" + LocalTime.now().toString());
                                return RepeatStatus.FINISHED;
                            }
                        }).build())
                .listener(listener)
                .build();
    }

}
