package com.study.study10robust.listener;

import org.springframework.batch.core.annotation.OnReadError;

/**
 * @author jiayq
 * @Date 2020-12-15
 */
public class ItemReadLis {

    private static final String name = "ItemReadLis";

    @OnReadError
    public void onReadError(Exception ex) {
        System.out.println(name + " exception : " + ex.getMessage());
    }

}
