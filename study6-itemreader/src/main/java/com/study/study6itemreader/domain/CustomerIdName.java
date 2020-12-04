package com.study.study6itemreader.domain;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author jiayq
 * @Date 2020-11-28
 */
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "CUSTOMEREN")
@Component
@Scope("prototype")
public class CustomerIdName {
    @Id
    @Column(name = "CUSTOMERID_PK")
    private Long id;
    @Column(name = "CUSTOMERNAMESTR")
    private String name;
}
