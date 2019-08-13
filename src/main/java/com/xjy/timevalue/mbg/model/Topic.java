package com.xjy.timevalue.mbg.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Topic implements Serializable {
    private Integer id;
    private Double energy;
    private Integer count;
}
