package com.study.study5step.listener;

import org.springframework.batch.core.ItemProcessListener;

/**
 * @author jiayq
 * @Date 2020-11-25
 */
public class ItemProcessLis implements ItemProcessListener {
    @Override
    public void beforeProcess(Object item) {
        System.out.println("6. item process before");
    }

    @Override
    public void afterProcess(Object item, Object result) {
        System.out.println("7. item process after");
    }

    @Override
    public void onProcessError(Object item, Exception e) {
        System.out.println("000. item process error");
    }
}
