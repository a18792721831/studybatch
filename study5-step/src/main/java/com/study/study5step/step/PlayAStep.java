package com.study.study5step.step;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

/**
 * @author jiayq
 * @Date 2020-11-14
 */
@EnableBatchProcessing
public class PlayAStep extends AbsStep {

    public PlayAStep() {
        setName("study5-abs-play-a-step");
    }

    @Override
    void beforeAbsExec(StepExecution stepExecution) {
        logger.warn(" PlayAStep before exec ");
    }

    @Override
    void afterAbsExec(StepExecution stepExecution) {
        logger.warn(" PlayAStep after exec ");
    }
}
