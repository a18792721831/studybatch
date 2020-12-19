package com.study.study10robust.listener;

import org.springframework.batch.core.annotation.OnSkipInRead;

/**
 * @author jiayq
 * @Date 2020-12-14
 */
public class SkipReadLis<T> {

    private static final String name = "SkipReadLis";

    @OnSkipInRead
    public void skipRead(Throwable throwable) {
        System.out.println(name + " skip in reader exception : " + throwable.getMessage());
    }

}
