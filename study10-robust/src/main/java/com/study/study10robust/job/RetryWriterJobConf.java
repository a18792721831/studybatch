package com.study.study10robust.job;

import com.study.study10robust.exception.MyException;
import com.study.study10robust.listener.*;
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
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiayq
 * @Date 2020-12-18
 */
//@Component
public class RetryWriterJobConf {


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
        return jobBuilderFactory.get("retry-writer-job")
                .start(step())
                .build();
    }

    private Step step() {
        AtomicInteger atomicInteger = new AtomicInteger();
        return stepBuilderFactory.get("retry-writer-job-step")
                .<String, String>chunk(3)
                .reader(() -> {
                    System.out.println("reader --- ");
                    return String.valueOf(atomicInteger.getAndIncrement());
//                    throw new MyException("always throw reader");
                })
                .processor(
                        new ItemProcessor<String, String>() {
                            @Override
                            public String process(String item) throws Exception {
                                System.out.println(item);
//                                throw new MyException("always throw process");
                                return item;
                            }
                        })
                .writer(items -> {
                    System.out.println(items.size());
                    throw new MyException("always throw write");
                })
                .listener(new ItemReadLis())
                .listener(new ItemProcessLis())
                .listener(new ItemWriteLis())
                .faultTolerant()
                .retryPolicy(new SimpleRetryPolicy(2, Map.of(MyException.class, true)))
                .retryLimit(2)
                .retry(MyException.class)
                .skipLimit(5)
                .skip(MyException.class)
                .skipPolicy(new LimitCheckingItemSkipPolicy(5, Map.of(MyException.class, true)))
                .listener(new RetryLis())
                .listener(new SkipReadLis<String>())
                .listener(new SkipProcessLis<String>())
                .listener(new SkipWriterLis<String>())
                .allowStartIfComplete(true)
                .build();
    }

}