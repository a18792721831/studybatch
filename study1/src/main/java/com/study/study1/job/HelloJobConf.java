package com.study.study1.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiayq
 * @Date 2020-11-05
 */
@Configuration
@EnableBatchProcessing
public class HelloJobConf {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Bean
    public Job helloJob(Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(ItemReader reader, ItemWriter writer, ItemProcessor processor) {
        return stepBuilderFactory.get("step1")
                .chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader reader() {
        return new ItemReader() {
            @Override
            public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                if (atomicInteger.intValue() > 20){
                    return null;
                }
                System.out.println("reader : hello");
                return "hello" + atomicInteger.incrementAndGet();
            }
        };
    }

    @Bean
    public ItemWriter writer() {
        return new ItemWriter() {
            @Override
            public void write(List items) throws Exception {
                items.stream().forEach(x -> System.out.println("writer : " + x));
            }
        };
    }

    @Bean
    public ItemProcessor processor() {
        return new ItemProcessor() {
            @Override
            public Object process(Object item) throws Exception {
                System.out.println("process : " + item);
                return item;
            }
        };
    }
}
