package com.study.study5step.step;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

/**
 * @author jiayq
 * @Date 2020-11-14
 */
@EnableBatchProcessing
public class PlayBStep extends AbsStep {

    public PlayBStep() {
        setName("study5-abs-play-b-step");
    }
    @Override
    void beforeAbsExec(StepExecution stepExecution) {
        logger.warn(" PlayBStep before exec ");
    }

    @Override
    void afterAbsExec(StepExecution stepExecution) {
        logger.warn(" PlayBStep after exec ");
    }
}
