package com.study.study7itemprocess.vilidat;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

/**
 * @author jiayq
 * @Date 2020-12-05
 */
public class MyValidator implements Validator<Integer> {
    @Override
    public void validate(Integer integer) throws ValidationException {
        if (integer % 5 == 0) {
            throw new ValidationException(" integer is % 5 ");
        }
    }
}
