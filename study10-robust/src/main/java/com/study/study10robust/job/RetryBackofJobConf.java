package com.study.study10robust.job;

import com.study.study10robust.exception.MyException;
import com.study.study10robust.listener.RetryLis;
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
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;

/**
 * @author jiayq
 * @Date 2020-12-19
 */
//@Component
public class RetryBackofJobConf {

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
        return jobBuilderFactory.get("retry-backoff-job")
                .start(step())
                .build();
    }

    private Step step() {
        return stepBuilderFactory.get("retry-backoff-tasklet")
                .tasklet((sc, cc) -> {
                    System.out.println("tasklet");
                    RetryCallback<String, MyException> retryCallback = rc -> {
                        System.out.println("retry call back");
                        throw new MyException("always throw retry call back");
                    };
                    BackOffPolicy backOffPolicy = new BackOffPolicy() {

                        @Override
                        public BackOffContext start(RetryContext context) {
                            System.out.println("backoff policy start");
                            return new BackOffContext() {
                            };
                        }

                        @Override
                        public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
                            System.out.println("backoff policy backOff");
                        }
                    };
                    System.out.println("retry call back after");
                    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3, Map.of(MyException.class, true));
                    RetryTemplate retryTemplate = new RetryTemplate();
                    retryTemplate.setRetryPolicy(retryPolicy);
                    retryTemplate.setBackOffPolicy(backOffPolicy);
                    retryTemplate.setListeners(new RetryListener[]{new RetryLis()});
                    retryTemplate.execute(retryCallback);
//                    retryTemplate.execute(retryCallback, new DefaultRetryState("retry state"));
                    System.out.println("tasklet finish");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
