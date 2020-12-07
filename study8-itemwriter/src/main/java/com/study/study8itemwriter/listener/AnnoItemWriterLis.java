package com.study.study8itemwriter.listener;

import com.study.study8itemwriter.domain.People;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.core.annotation.OnWriteError;

import java.util.List;

/**
 * @author jiayq
 * @Date 2020-12-07
 */
public class AnnoItemWriterLis {

    private static final String name = "AnnoItemWriterLis";

    @BeforeWrite
    public void beforeWrite(List<? extends People> items) {
        for (People people : items
        ) {
            if (people.getId() == 3) {
                throw new RuntimeException(" before writer people id is 3 ");
            }
        }
        items.stream().forEach(x -> System.out.println(name + " before writer : " + x));
    }

    @AfterWrite
    public void afterWrite(List<? extends People> items) {
        items.stream().forEach(x -> System.out.println(name + " after writer : " + x));
    }

    @OnWriteError
    public void onWriteError(Exception exception, List<? extends People> items) {
        System.out.println(name + " error writer : " + items.size() + " , exception Message : " + exception.getMessage());
    }

}
