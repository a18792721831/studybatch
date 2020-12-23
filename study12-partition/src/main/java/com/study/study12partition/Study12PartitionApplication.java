package com.study.study12partition;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class Study12PartitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(Study12PartitionApplication.class, args);
    }

}
