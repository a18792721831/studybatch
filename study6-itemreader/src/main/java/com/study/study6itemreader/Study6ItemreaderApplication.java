package com.study.study6itemreader;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.study.study6itemreader.dao")
public class Study6ItemreaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(Study6ItemreaderApplication.class, args);
    }

}
