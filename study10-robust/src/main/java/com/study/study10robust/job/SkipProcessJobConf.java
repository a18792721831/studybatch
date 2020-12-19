package com.study.study10robust.job;

import com.study.study10robust.exception.MyException;
import com.study.study10robust.listener.ItemProcessLis;
import com.study.study10robust.listener.SkipProcessLis;
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiayq
 * @Date 2020-12-14
 */
//@Component
public class SkipProcessJobConf {

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
        return jobBuilderFactory.get("skip-process-job")
                .start(step()).build();

    }

    private Step step() {
        AtomicInteger atomicInteger = new AtomicInteger();
        return stepBuilderFactory.get("skip-process-job-step")
                .<String, String>chunk(3)
                .reader(() -> "x")
//                .processor((Function<String, String>) item -> {
//                    System.out.println(item);
//                    throw new MyException("always throw process");
//                })
                .processor(new ItemProcessor<String, String>(){

                    @Override
                    public String process(String item) throws Exception {
                        System.out.println(item);
                        throw new MyException("always throw process");
                    }
                })
                .writer(items -> {
                    System.out.println(items.size());
                })
                // 处理异常
                .listener(new ItemProcessLis())
                // 启动高级特性
                .faultTolerant()
                // 允许跳过的最大行数
                .skipLimit(5)
                // 允许跳过的异常
                .skip(MyException.class)
                // 配置跳过的策略:最大记录数10，跳过的异常的映射
                .skipPolicy(new LimitCheckingItemSkipPolicy(5, Map.of(MyException.class, true)))
                .listener(new SkipProcessLis<String>())
                .allowStartIfComplete(true)
                .build();
    }
}
