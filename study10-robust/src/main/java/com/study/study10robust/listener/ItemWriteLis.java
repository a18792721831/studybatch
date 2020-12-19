package com.study.study10robust.listener;

import org.springframework.batch.core.annotation.OnWriteError;

import java.util.List;

/**
 * @author jiayq
 * @Date 2020-12-16
 */
public class ItemWriteLis {

    private static final String name = "ItemWriteLis";

    @OnWriteError
    public void onWriteError(Exception e,List<Object> items){
        System.out.println(name + " exception : " + e.getMessage() + " , items.size = " + items.size());
    }

}
