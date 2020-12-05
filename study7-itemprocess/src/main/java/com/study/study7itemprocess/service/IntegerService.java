package com.study.study7itemprocess.service;

import org.springframework.stereotype.Service;

/**
 * @author jiayq
 * @Date 2020-12-05
 */
@Service
public class IntegerService {

    public Integer addOne(Integer item) {
        return ++item;
    }

}
