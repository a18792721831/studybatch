package com.study.study6itemreader.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

/**
 * @author jiayq
 * @Date 2020-12-03
 */
@Component
public class MyItemReader implements ItemReader<Integer> {

    private Integer number = 0;

    @Override
    public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        number++;
        if (number <= 20) {
            System.out.println("reader : " + number);
            return number;
        }
        throw new Exception(" more than 20 ");
    }
}
