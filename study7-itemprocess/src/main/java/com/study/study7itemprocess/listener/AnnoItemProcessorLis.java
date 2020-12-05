package com.study.study7itemprocess.listener;

import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.core.annotation.BeforeProcess;
import org.springframework.batch.core.annotation.OnProcessError;

/**
 * @author jiayq
 * @Date 2020-12-05
 */
public class AnnoItemProcessorLis {

    private static final String name = "AnnoItemProcessorLis";

    @BeforeProcess
    public void beforeProcess(Integer item) {
        System.out.println(name + " before process ");
    }

    @AfterProcess
    public void afterProcess(Integer item, Integer result) {
        System.out.println(name + " before process , in : " + item + " , out : " + result);
    }

    @OnProcessError
    public void onProcessError(Integer item, Exception e) {
        System.out.println(name + " error process , item : " + item + " , exception : " + e.getMessage());
    }

}
