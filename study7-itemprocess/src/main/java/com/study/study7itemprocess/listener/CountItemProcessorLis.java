package com.study.study7itemprocess.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;

/**
 * @author jiayq
 * @Date 2020-12-05
 */
public class CountItemProcessorLis {

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("skip : " + stepExecution.getSkipCount());
        System.out.println("filter : " + stepExecution.getFilterCount());
        return stepExecution.getExitStatus();
    }

}
