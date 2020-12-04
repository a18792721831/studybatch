package com.study.study6itemreader.job;

import com.study.study6itemreader.adapter.CustomerQueryAdapter;
import com.study.study6itemreader.domain.CustomerIdName;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2020-12-03
 */
@EnableBatchProcessing
//@Configuration
public class ItemReaderAdapterJobConf {

    @Bean
    public String runJob(JobLauncher jobLauncher, Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParametersBuilder()
                .addDate("date", new Date())
                .toJobParameters());
        return "";
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("item-reader-adapter-job")
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, ItemReaderAdapter<CustomerIdName> reader) {
        return stepBuilderFactory.get("item-reader-adapter-step")
                .<CustomerIdName, CustomerIdName>chunk(4)
                .reader(reader)
                .processor((Function<CustomerIdName, CustomerIdName>) item -> {
                    System.out.println(item);
                    return item;
                })
                .writer(items -> System.out.println("writer : " + items.size()))
                .build();
    }

    @Bean
    public ItemReaderAdapter<CustomerIdName> reader(CustomerQueryAdapter adapter) {
        ItemReaderAdapter<CustomerIdName> reader = new ItemReaderAdapter<>();
        reader.setTargetObject(adapter);
        reader.setTargetMethod("getCustomer");
        return reader;
    }

}
