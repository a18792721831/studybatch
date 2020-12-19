package com.study.study10robust.listener;

import org.springframework.batch.core.annotation.OnSkipInProcess;

/**
 * @author jiayq
 * @Date 2020-12-14
 */
public class SkipProcessLis<T> {

    private static final String name = "SkipProcessLis";

    @OnSkipInProcess
    public void skipProcess(T item, Throwable throwable) {
        System.out.println(name + " skip in process , item = " + item + " , exception : " + throwable.getMessage());
    }



}
