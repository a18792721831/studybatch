package com.study.study6itemreader.reader;

import org.springframework.batch.item.*;
import org.springframework.stereotype.Component;

/**
 * @author jiayq
 * @Date 2020-12-04
 */
@Component
public class MyRestartItemReader implements ItemReader<Integer>, ItemStream {

    private static final String CURRENT = "current";
    private Integer current = 0;

    @Override
    public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        System.out.println("reader : " + current);
        if (current > 20) {
            throw new Exception("more than 20 ");
        }
        return current++;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey(CURRENT)) {
            current = executionContext.getInt(CURRENT, 0);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt(CURRENT, current);
    }

    @Override
    public void close() throws ItemStreamException {

    }
}
