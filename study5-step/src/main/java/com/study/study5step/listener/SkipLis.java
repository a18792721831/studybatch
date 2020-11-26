package com.study.study5step.listener;

import org.springframework.batch.core.SkipListener;

/**
 * @author jiayq
 * @Date 2020-11-25
 */
public class SkipLis implements SkipListener {
    @Override
    public void onSkipInRead(Throwable t) {
        System.out.println(" skip read ");
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        System.out.println(" skip write ");
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        System.out.println(" skip process ");
    }
}
