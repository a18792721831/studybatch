package com.study.study5step.step;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.InvocationTargetException;

/**
 * @author jiayq
 * @Date 2020-11-16
 */
@EnableBatchProcessing
@Component
public class StepBuilder {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    public AbsStep get(Class clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object instance = clazz.getDeclaredConstructor().newInstance();
        if (instance instanceof AbsStep) {
            AbsStep step = (AbsStep) instance;
            step.setJobRepository(jobRepository);
            step.setTransactionManager(transactionManager);
            return step;
        } else{
            throw new NoSuchMethodException("");
        }
    }

}
