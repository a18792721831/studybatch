package com.study.study4.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;

/**
 * @author jiayq
 * @Date 2020-11-10
 */
@Component
public class ParameValidatory implements
        JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        System.out.println(parameters.getParameters());
        System.out.println("ParameValidatory + " + parameters.getClass().getSimpleName());
    }
}
