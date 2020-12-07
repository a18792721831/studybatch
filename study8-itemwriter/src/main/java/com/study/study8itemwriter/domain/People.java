package com.study.study8itemwriter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * @author jiayq
 * @Date 2020-12-05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "people")
@Component
@Scope("prototype")
public class People {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
