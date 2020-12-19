package com.study.study11extend.job;

import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.partition.StepExecutionSplitter;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-19
 */
@Component
public class PartitionJobConf {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    private Step partitionStep() {
        return stepBuilderFactory.get("xx")
                // gridSize表示分区数量
                // 返回的Map表示分区规则
                .partitioner("xx", gridSize -> {
                    return Map.of("xx", new ExecutionContext());
                })
                // 分区数量
                .gridSize(3)
                // 分区划分规则
                .splitter(new StepExecutionSplitter() {

                    @Override
                    public String getStepName() {
                        return "splitter-step";
                    }

                    @Override
                    public Set<StepExecution> split(StepExecution stepExecution, int gridSize) throws JobExecutionException {
                        return Set.of(stepExecution);
                    }
                })
                // 数据合并
                .aggregator((result, stepExecutions) -> {})
                // 结果合并
                .partitionHandler(((stepSplitter, stepExecution) -> Arrays.asList(stepExecution)))
                // 处理的step
                .step(stepBuilderFactory.get("xxs")
                        .tasklet((sc, cc) -> RepeatStatus.FINISHED).build())
                .build();
    }
}
