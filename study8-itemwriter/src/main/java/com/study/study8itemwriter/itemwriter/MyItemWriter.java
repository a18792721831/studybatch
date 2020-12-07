package com.study.study8itemwriter.itemwriter;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * @author jiayq
 * @Date 2020-12-07
 */
public class MyItemWriter<T> implements ItemWriter<T> {

    @Override
    public void write(List<? extends T> items) throws Exception {
        for (T t : items) {
            if (t.toString().contains("4")) {
                throw new Exception(" items has 4");
            }
            System.out.println(" MyItemWriter : " + t);
        }
    }
}
