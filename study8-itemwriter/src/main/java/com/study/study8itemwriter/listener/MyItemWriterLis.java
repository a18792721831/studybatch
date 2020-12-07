package com.study.study8itemwriter.listener;

import com.study.study8itemwriter.domain.People;
import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

/**
 * @author jiayq
 * @Date 2020-12-07
 */
public class MyItemWriterLis implements ItemWriteListener<People> {
    private static final String name = "MyItemWriterLis";

    @Override
    public void beforeWrite(List<? extends People> items) {
        items.stream().forEach(x -> System.out.println(name + " before writer : " + x));
    }

    @Override
    public void afterWrite(List<? extends People> items) {
        items.stream().forEach(x -> System.out.println(name + " after writer : " + x));
    }

    @Override
    public void onWriteError(Exception exception, List<? extends People> items) {
        System.out.println(name + " error writer : " + items.size() + " , exception Message : " + exception.getMessage());
    }
}
