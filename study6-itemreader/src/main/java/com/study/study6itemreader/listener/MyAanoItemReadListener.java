package com.study.study6itemreader.listener;

import org.springframework.batch.core.annotation.AfterRead;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.batch.core.annotation.OnReadError;

/**
 * @author jiayq
 * @Date 2020-12-04
 */
public class MyAanoItemReadListener {

    private static final String name = "MyAanoItemReadListener";

    @BeforeRead
    public void before(){
        System.out.println(name + " before ");
    }

    @AfterRead
    public void after(Object item){
        System.out.println(name + " after read item : " + item);
    }

    @OnReadError
    public void onError(Exception e) {
        System.out.println(name + " exception read message : " + e.getMessage());
    }

}
