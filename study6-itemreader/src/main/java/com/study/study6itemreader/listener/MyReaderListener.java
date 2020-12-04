package com.study.study6itemreader.listener;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

/**
 * @author jiayq
 * @Date 2020-12-04
 */
@Component
public class MyReaderListener implements ItemReadListener {

    private static final String name = "MyReaderListener";

    @Override
    public void beforeRead() {
        System.out.println(name + " before read ");
    }

    @Override
    public void afterRead(Object item) {
        System.out.println(name + " after read : " + item);
        if (item.equals(11)) {
            throw new RuntimeException(" item is 11 ");
        }
    }

    @Override
    public void onReadError(Exception ex) {
        System.out.println(name + " exception read : " + ex.getMessage());
    }
}
