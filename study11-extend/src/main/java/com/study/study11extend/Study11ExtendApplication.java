package com.study.study11extend;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class Study11ExtendApplication {

    public static void main(String[] args) {
        SpringApplication.run(Study11ExtendApplication.class, args);
    }

}
