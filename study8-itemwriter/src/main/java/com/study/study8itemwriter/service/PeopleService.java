package com.study.study8itemwriter.service;

import com.study.study8itemwriter.domain.People;
import org.springframework.stereotype.Service;

/**
 * @author jiayq
 * @Date 2020-12-07
 */
@Service
public class PeopleService {

    public void print(People people) {
        System.out.println(" peopleService : " + people);
    }

    public void printName(String name) {
        System.out.println(" peopleService name : " + name);
    }

}
