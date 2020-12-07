package com.study.study8itemwriter.itemwriter;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jiayq
 * @Date 2020-12-07
 */
public class RestartItemWriter<T> implements ItemWriter<T>, ItemStream {

    private Integer number = 0;
    private static final String CURRENT = "CURRENT";

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey(CURRENT)) {
            number = executionContext.getInt(CURRENT, 0);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt(CURRENT, number);
    }

    @Override
    public void close() throws ItemStreamException {

    }

    @Override
    public void write(List<? extends T> items) throws Exception {
        Pattern pattern = Pattern.compile("\\d");
        for (T t : items) {
            Matcher matcher = pattern.matcher(t.toString());
            if (matcher.find()) {
                int id = Integer.parseInt(matcher.group().trim());
                if (id < number) {
                    continue;
                }
                if (id == 4) {
                    throw new Exception(" items has 4");
                }
                System.out.println(" MyItemWriter : " + t);
                number++;
            }
        }
    }
}
