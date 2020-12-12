package com.study.study9stepflow;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class Study9StepflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(Study9StepflowApplication.class, args);
    }

}
