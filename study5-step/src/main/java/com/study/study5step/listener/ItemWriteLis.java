package com.study.study5step.listener;

import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

/**
 * @author jiayq
 * @Date 2020-11-25
 */
public class ItemWriteLis implements ItemWriteListener {
    @Override
    public void beforeWrite(List items) {
        System.out.println("8. item write before");
    }

    @Override
    public void afterWrite(List items) {
        System.out.println("9. item write after");
    }

    @Override
    public void onWriteError(Exception exception, List items) {
        System.out.println("00000. item write error");
    }
}
