package com.study.study6itemreader.dao;

import com.study.study6itemreader.domain.CustomerIdName;

import java.util.List;

/**
 * @author jiayq
 * @Date 2020-12-01
 */
public interface CustomerQuery {

    List<CustomerIdName> queryCustomer(Integer status);

}
