package com.study.study10robust.listener;

import org.springframework.batch.core.annotation.OnSkipInWrite;

/**
 * @author jiayq
 * @Date 2020-12-14
 */
public class SkipWriterLis<T> {

    private static final String name = "SkipWriterLis";

    @OnSkipInWrite
    public void skipWriter(T item, Exception ex) {
        System.out.println(name + " skip in writer , items = " + item + " , exception : " + ex.getMessage());
    }
}
