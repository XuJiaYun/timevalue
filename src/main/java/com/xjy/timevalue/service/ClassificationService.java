package com.xjy.timevalue.service;


import com.xjy.timevalue.dto.TimeValueBean;

public interface ClassificationService {
    public TimeValueBean classify(TimeValueBean timeValueBean) throws Exception;
}
