package com.study.study10robust.job;

import com.study.study10robust.exception.MyException;
import com.study.study10robust.listener.ItemProcessLis;
import com.study.study10robust.listener.ItemWriteLis;
import com.study.study10robust.listener.SkipProcessLis;
import com.study.study10robust.listener.SkipWriterLis;
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
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-14
 */
//@Component
public class SkipWriterJobConf {

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
        return jobBuilderFactory.get("skip-write-job")
                .start(step()).build();

    }

    private Step step() {
        AtomicInteger atomicInteger = new AtomicInteger();
        return stepBuilderFactory.get("skip-write-job-step")
                .<String, String>chunk(3)
                .reader(() -> "x")
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        System.out.println(item);
//                        throw new MyException("always throw process");
                        return item;
                    }
                })
                .writer(items -> {
                    System.out.println(items.size());
                    throw new MyException("always throw write");
                })
                // 处理异常
                .listener(new ItemProcessLis())
                // 写异常
                .listener(new ItemWriteLis())
                // 启动高级特性
                .faultTolerant()
                // 允许跳过的最大行数
                .skipLimit(5)
                // 允许跳过的异常
                .skip(MyException.class)
                // 配置跳过的策略:最大记录数10，跳过的异常的映射
                .skipPolicy(new LimitCheckingItemSkipPolicy(5, Map.of(MyException.class, true)))
                .listener(new SkipProcessLis<String>())
                .listener(new SkipWriterLis<String>())
                .allowStartIfComplete(true)
                .build();
    }
}
