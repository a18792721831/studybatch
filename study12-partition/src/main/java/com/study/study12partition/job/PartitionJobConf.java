package com.study.study12partition.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.SimpleStepExecutionSplitter;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author jiayq
 * @Date 2020-12-21
 */
@Slf4j
@Component
public class PartitionJobConf {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;

    private ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    @PostConstruct
    public void runJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job(), new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
    }

    private Job job() {
        return jobBuilderFactory.get("partition-job")
                // 分区操作执行
                .start(step())
                // 线程池销毁操作
                .next(stepBuilderFactory.get("clear-executor")
                        .tasklet((sc, cc) -> {
                            log.info("线程池销毁");
                            executor.shutdown();
                            return RepeatStatus.FINISHED;
                        }).build())
                .build();
    }

    private Step step() {
        // 设置线程池最大的线程数量
        executor.setMaxPoolSize(10);
        // 设置核心线程数量
        executor.setCorePoolSize(10);
        // 线程池初始化
        executor.initialize();
        // 数据分区
        Partitioner partitioner = gridSize -> {
            HashMap<String, ExecutionContext> result = new HashMap<>();
            result.put("one", new ExecutionContext(Map.of("key", "one", "begin", 1, "end", 10, "sum", 0)));
            result.put("two", new ExecutionContext(Map.of("key", "two", "begin", 11, "end", 20, "sum", 0)));
            result.put("three", new ExecutionContext(Map.of("key", "three", "begin", 21, "end", 30, "sum", 0)));
            result.put("four", new ExecutionContext(Map.of("key", "four", "begin", 31, "end", 40, "sum", 0)));
            result.put("five", new ExecutionContext(Map.of("key", "five", "begin", 41, "end", 50, "sum", 0)));
            result.put("six", new ExecutionContext(Map.of("key", "six", "begin", 51, "end", 60, "sum", 0)));
            result.put("seven", new ExecutionContext(Map.of("key", "seven", "begin", 61, "end", 70, "sum", 0)));
            result.put("eight", new ExecutionContext(Map.of("key", "eight", "begin", 71, "end", 80, "sum", 0)));
            result.put("nine", new ExecutionContext(Map.of("key", "nine", "begin", 81, "end", 90, "sum", 0)));
            result.put("ten", new ExecutionContext(Map.of("key", "ten", "begin", 91, "end", 100, "sum", 0)));
            return result;
        };
        String stepName = "partition-step";
        // 分区内线程处理的操作
        TaskletStep step = stepBuilderFactory.get("real-step")
                .tasklet((sc, cc) -> {
                    // 获取参数传递上下文
                    ExecutionContext executionContext = sc.getStepExecution().getExecutionContext();
                    // 获取本次tasklet将要处理的数字
                    int begin = executionContext.getInt("begin");
                    // 将下次tasklet要处理的数字放入
                    executionContext.putInt("begin", begin + 1);
                    // 如果本次tasklet将要处理的数字达到了截止值
                    if (begin > sc.getStepExecution().getExecutionContext().getInt("end")) {
                        // 输出本次tasklet处理的数字
                        log.info(" stepName : " + sc.getStepExecution().getExecutionContext().get("key") + " over : " + begin);
                        // 结束本线程的tasklet
                        return RepeatStatus.FINISHED;
                    }
                    // 将本次tasklet的值，。汇总到本线程的tasklet的和里面
                    executionContext.putInt("sum", executionContext.getInt("sum") + begin);
                    // 计算完一个数字，需要睡眠2秒
                    TimeUnit.SECONDS.sleep(2);
                    // 输出本次tasklet处理的数字
                    log.info(" stepName : " + sc.getStepExecution().getExecutionContext().get("key") + " number : " + begin);
                    // 还未计算完成，继续下一次tasklet
                    return RepeatStatus.CONTINUABLE;
                })
                .build();
        // 创建线程处理调度
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        // 总共10个线程
        handler.setGridSize(10);
        // 线程池中需要处理的操作
        handler.setStep(step);
        // 设置线程池
        handler.setTaskExecutor(executor);
        return stepBuilderFactory.get(stepName)
                // 设置本step是一个分区step
                .partitioner(stepName, partitioner)
                // 传入简单的拆分器(随机拆分)
                .splitter(new SimpleStepExecutionSplitter(jobRepository, true, stepName, partitioner))
                // 设置分区数量(这里应该是重复了)
                .gridSize(10)
                // 设置线程处理调度
                .partitionHandler(handler)
                // 设置线程池(这里应该是重复了)
                .taskExecutor(executor)
                // 分区线程计算结果汇总
                .aggregator(((result, executions) -> {
                    executions.forEach(x -> {
                        log.info(x.getExecutionContext().get("key").toString());
                    });
                    result.getExecutionContext().put("over", "over");
                    // 汇总结果
                    result.getExecutionContext().put("sum", executions.stream().map(x -> x.getExecutionContext().getInt("sum")).reduce((x1, x2) -> x1 + x2).get());
                    log.info("sum = " + result.getExecutionContext().getInt("sum"));
                }))
                .build();
    }

}
