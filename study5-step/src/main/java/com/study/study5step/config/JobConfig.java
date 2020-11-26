package com.study.study5step.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
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
    public JobRepositoryFactoryBean jobRepositoryFactoryBean(DataSource dataSource, PlatformTransactionManager jobRepoManager) {
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
    public TransactionAttribute stepAttribute() {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        // 隔离级别为默认
        attribute.setIsolationLevelName("ISOLATION_DEFAULT");
        // 传播方式为REQUIRED(没有事务则新建)
        attribute.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        // 设置超时时间
        attribute.setTimeout(30);
        return attribute;
    }

    @Bean
    public PlatformTransactionManager stepManager(DataSource dataSource) {
        DataSourceTransactionManager manager = new DataSourceTransactionManager();
        manager.setDataSource(dataSource);
        manager.setDefaultTimeout(60);
        return manager;
    }

    @Bean
    public PlatformTransactionManager jobRepoManager(DataSource dataSource) {
        DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);
        manager.setDefaultTimeout(10);
        return manager;
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
