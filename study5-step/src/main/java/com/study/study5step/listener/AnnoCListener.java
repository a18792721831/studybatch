package com.study.study5step.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author jiayq
 * @Date 2020-11-16
 */
@Component
public class AnnoCListener {

    @BeforeStep
    public void before(StepExecution stepExecution){
        System.out.println(stepExecution.getStepName() + " exec Anno Listener " + LocalDateTime.now());
    }

    @AfterStep
    public ExitStatus after(StepExecution stepExecution) {
        System.out.println(stepExecution.getStepName() + " exec Anno Listener " + LocalDateTime.now());
        return stepExecution.getExitStatus();
    }

}
