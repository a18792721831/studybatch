package com.study.study5step.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

/**
 * @author jiayq
 * @Date 2020-11-16
 */
@Component
public class StepBListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println(stepExecution.getStepName() + " Step b Listener before ");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println(stepExecution.getStepName() + " Step b Listener after ");
        return stepExecution.getExitStatus();
    }
}
