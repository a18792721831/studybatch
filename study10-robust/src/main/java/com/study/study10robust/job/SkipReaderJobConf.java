package com.study.study10robust.job;

import com.study.study10robust.exception.MyException;
import com.study.study10robust.listener.ItemReadLis;
import com.study.study10robust.listener.SkipReadLis;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-14
 */
//@Component
public class SkipReaderJobConf {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @PostConstruct
    public void runJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job(), new JobParametersBuilder().addLong("id", 3L).toJobParameters());
    }

    private Job job() {
        return jobBuilderFactory.get("skip-reader-job")
                .start(step()).build();

    }

    private Step step() {
        return stepBuilderFactory.get("skip-job-step")
                .<String, String>chunk(3)
                .reader(() -> {
                    System.out.println(" reader ");
                    throw new RuntimeException(" always reader throw");
                })
                .processor((Function<String, String>) x -> x)
                .writer(items -> System.out.println(items.size()))
                // 使用itemReader#onReadError拦截器
                .listener(new ItemReadLis())
                // 启动高级特性
                .faultTolerant()
                // 允许跳过的最大行数
                .skipLimit(5)
                // 允许跳过的异常
                .skip(MyException.class)
                // 配置跳过的策略:最大记录数10，跳过的异常的映射
                // skipPolicy = skipLimit + skip
                // skipPolicy和skipLimit+skip 二选一即可
                .skipPolicy(new LimitCheckingItemSkipPolicy(7, Map.of(RuntimeException.class, true)))
                // 设置跳过拦截器(ItemReader)
                .listener(new SkipReadLis<String>())
                .allowStartIfComplete(true)
                .build();
    }

}
