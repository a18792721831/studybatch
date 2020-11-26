package com.study.study5step.listener;

import org.springframework.batch.core.ItemReadListener;

/**
 * @author jiayq
 * @Date 2020-11-25
 */
public class ItemReadLis implements ItemReadListener {
    @Override
    public void beforeRead() {
        System.out.println("4. item reader before");
    }

    @Override
    public void afterRead(Object item) {
        System.out.println("5. item reader after");
    }

    @Override
    public void onReadError(Exception ex) {
        System.out.println("00. item error");
    }
}
