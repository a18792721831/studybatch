package com.study.study6itemreader.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

import javax.sql.DataSource;

/**
 * @author jiayq
 * @Date 2020-11-16
 */
@Configuration
public class JobConfig {

    private static final Logger logger = LoggerFactory.getLogger(JobConfig.class);

    @Bean
    public JobRepositoryFactoryBean jobRepositoryFactoryBean(@Qualifier("mysqlDataSource") DataSource dataSource, PlatformTransactionManager jobRepoManager) {
        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setTransactionManager(jobRepoManager);
        jobRepositoryFactoryBean.setDataSource(dataSource);
        try {
            jobRepositoryFactoryBean.afterPropertiesSet();
        } catch (Exception e) {
            logger.error("create job repository factory bean error : {}", e.getMessage());
        }
        return jobRepositoryFactoryBean;
    }

    @Bean
    @Primary
    public JobRepository jobRepository(JobRepositoryFactoryBean jobRepositoryFactoryBean) {
        JobRepository jobRepository = null;
        try {
            jobRepository = jobRepositoryFactoryBean.getObject();
        } catch (Exception e) {
            logger.error("create job repository error : {}", e.getMessage());
        }
        return jobRepository;
    }

}
