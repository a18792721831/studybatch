package com.study.study11extend.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiayq
 * @Date 2020-12-19
 */
public class MySyncTasklet implements Tasklet {

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public synchronized RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        int number = atomicInteger.getAndIncrement();
        System.out.println(contribution.getStepExecution().getStepName() + number + " , thread : " +
                Thread.currentThread().getName() + " , time : " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " >>> sleep 10s .");
        TimeUnit.SECONDS.sleep(10);
        if (number > 20) {
            return RepeatStatus.FINISHED;
        }
        return RepeatStatus.CONTINUABLE;
    }
}
