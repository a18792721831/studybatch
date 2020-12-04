package com.study.study6itemreader.manage;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author jiayq
 * @Date 2020-11-27
 */
@Configuration
public class DataSourceManage {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.druid.mysql")
    public DruidDataSource mysqlDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid.boss")
    public DruidDataSource bossDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

}
