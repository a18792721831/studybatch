package com.study.study4.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * @author jiayq
 * @Date 2020-11-10
 */
@Component
public class SecJobListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("SecJobListener before : " + jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("SecJobListener after : " + jobExecution.getJobInstance().getJobName());
    }
}
