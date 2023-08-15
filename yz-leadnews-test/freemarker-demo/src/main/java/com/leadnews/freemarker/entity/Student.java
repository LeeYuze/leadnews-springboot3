package com.leadnews.freemarker.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
@Data
public class Student {

    private String name;

    private Integer age;

    private Date birthday;

    private Double money;
}
