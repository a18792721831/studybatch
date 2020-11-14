package com.study.study4.job;

import com.study.study4.validator.ParameValidatory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;

/**
 * @author jiayq
 * @Date 2020-11-10
 */
@Configuration
@EnableBatchProcessing
public class CompParJobConf {

//    @Bean
//    public String runJob(JobLauncher jobLauncher, Job compParJob) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
//        jobLauncher.run(compParJob, new JobParametersBuilder()
//                .addLong("id", 1L)
//                .addString("name", "job")
//                .addDate("date", new Date())
//                .toJobParameters());
//        return "";
//    }

    @Bean
    public Job compParJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ParameValidatory parameValidatory) {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(parameValidatory, new DefaultJobParametersValidator(new String[]{"id"}, new String[]{"name"})));
        return jobBuilderFactory.get("study4-comp-par")
                .start(stepBuilderFactory.get("study4-comp-par")
                        .tasklet(new Tasklet() {
                            @Override
                            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                                System.out.println("exec! " + LocalTime.now().toString());
                                return RepeatStatus.FINISHED;
                            }
                        }).build())
                .validator(validator)
                .build();
    }

}
