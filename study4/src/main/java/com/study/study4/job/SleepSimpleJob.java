package com.study.study4.job;

/**
 * @author jiayq
 * @Date 2020-11-11
 */
public class SleepSimpleJob extends AbsSimpleJob {
    @Override
    void beforeExec() {
        log.info(" sleep simple before ");
    }

    @Override
    void afterExec() {
        log.info(" sleep simple after ");
    }
}
