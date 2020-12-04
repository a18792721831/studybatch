package com.study.study6itemreader.listener;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

/**
 * @author jiayq
 * @Date 2020-12-04
 */
@Component
public class MySecondReadListener implements ItemReadListener<Integer> {

    private static final String name = "MySecondReadListener";

    @Override
    public void beforeRead() {
        System.out.println(name + " before read ");
    }

    @Override
    public void afterRead(Integer item) {
        System.out.println(name + " after read item : " + item);
    }

    @Override
    public void onReadError(Exception ex) {
        System.out.println(name + " exception read exception message : " + ex.getMessage());
    }
}
