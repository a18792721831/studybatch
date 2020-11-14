package com.study.study4.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

/**
 * @author jiayq
 * @Date 2020-11-14
 */
public class BusStopListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution.setTerminateOnly();
        return ExitStatus.STOPPED;
    }
}
