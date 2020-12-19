package com.study.study10robust.listener;

import org.springframework.batch.core.annotation.OnProcessError;
import org.springframework.batch.core.annotation.OnWriteError;

import java.util.List;

/**
 * @author jiayq
 * @Date 2020-12-16
 */
public class ItemProcessLis {

    private static final String name = "ItemProcessLis";

    @OnProcessError
    public void processError(Object item, Exception ex) {
        System.out.println(name + " exception : " + ex.getMessage() + " , item : " + item);
    }
}
