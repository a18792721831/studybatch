package com.study.study8itemwriter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.study.study8itemwriter.dao")
public class Study8ItemwriterApplication {

    public static void main(String[] args) {
        SpringApplication.run(Study8ItemwriterApplication.class, args);
    }

}
