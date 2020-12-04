package com.study.study6itemreader.adapter;

import com.study.study6itemreader.domain.CustomerIdName;
import com.study.study6itemreader.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jiayq
 * @Date 2020-12-03
 */
@Component
public class CustomerQueryAdapter {

    private List<CustomerIdName> data = new LinkedList<>();

    @Autowired
    private CustomerService customerService;

    @PostConstruct
    public void init() {
        data.addAll(customerService.getAll());
    }

    public CustomerIdName getCustomer() {
        CustomerIdName result = null;
        if (!data.isEmpty()) {
            result = data.remove(0);
        }
        System.out.print("reader : " + result);
        return result;
    }

}
