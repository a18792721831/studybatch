package com.study.study4.job;

/**
 * @author jiayq
 * @Date 2020-11-11
 */
public class PlaySimpleJob extends AbsSimpleJob {
    @Override
    void beforeExec() {
        log.info(" play simple before ");
    }

    @Override
    void afterExec() {
        log.info(" play simple after ");
    }
}
