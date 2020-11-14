package com.study.study4.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.StartLimitExceededException;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.repository.JobRestartException;

/**
 * @author jiayq
 * @Date 2020-11-11
 */
public abstract class AbsSimpleJob extends SimpleJob {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    abstract void beforeExec();

    @Override
    protected void doExecute(JobExecution execution) throws JobInterruptedException, JobRestartException, StartLimitExceededException {
        beforeExec();
        log.info(" do execute before");
        super.doExecute(execution);
        log.info(" do execute after");
        afterExec();
    }

    abstract void afterExec();
}
