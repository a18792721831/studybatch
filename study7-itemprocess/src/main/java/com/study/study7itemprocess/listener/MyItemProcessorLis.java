package com.study.study7itemprocess.listener;

import org.springframework.batch.core.ItemProcessListener;

/**
 * @author jiayq
 * @Date 2020-12-05
 */
public class MyItemProcessorLis implements ItemProcessListener<Integer, Integer> {
    private static final String name = "MyItemProcessorLis";

    @Override
    public void beforeProcess(Integer item) {
        if (item == 6 ) {
            throw new RuntimeException("item is 6");
        }
        System.out.println(name + " before process ");
    }

    @Override
    public void afterProcess(Integer item, Integer result) {
        System.out.println(name + " before process , in : " + item + " , out : " + result);
    }

    @Override
    public void onProcessError(Integer item, Exception e) {
        System.out.println(name + " error process , item : " + item + " , exception : " + e.getMessage());
    }
}
