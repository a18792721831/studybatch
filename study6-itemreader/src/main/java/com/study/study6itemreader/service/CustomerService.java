package com.study.study6itemreader.service;

import com.study.study6itemreader.dao.CustomerQuery;
import com.study.study6itemreader.domain.CustomerIdName;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author jiayq
 * @Date 2020-12-03
 */
@Service
@Configuration
public class CustomerService {

    @Autowired
    private CustomerQuery customerQuery;

    public List<CustomerIdName> getAll() {
        List<CustomerIdName> result = customerQuery.queryCustomer(0);
        result.addAll(customerQuery.queryCustomer(1));
        return result;

    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("bossDataSource") DataSource bossDataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(bossDataSource);
        factoryBean.setTypeAliasesPackage("com.study.study6itemreader.domain");
        return factoryBean.getObject();
    }

}
