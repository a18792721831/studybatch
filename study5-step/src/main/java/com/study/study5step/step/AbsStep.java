package com.study.study5step.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDateTime;

/**
 * @author jiayq
 * @Date 2020-11-14
 */
public abstract class AbsStep extends TaskletStep{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public AbsStep() {
        setTasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                int sum = Integer.valueOf(chunkContext.getStepContext().getJobParameters().get("time").toString());
                while (sum > 0) {
                    System.out.println(chunkContext.getStepContext().getStepName() + " exec : " + LocalDateTime.now());
                    sum--;
                }
                return RepeatStatus.FINISHED;
            }
        });
    }

    @Override
    protected void doExecute(StepExecution stepExecution) throws Exception {
        beforeAbsExec(stepExecution);
        logger.warn("abstract before real exec ");
        super.doExecute(stepExecution);
        logger.warn("abstract after real exec ");
        afterAbsExec(stepExecution);
    }

    abstract void beforeAbsExec(StepExecution stepExecution);

    abstract void afterAbsExec(StepExecution stepExecution);
}
