package com.xjy.timevalue.service;


import com.xjy.timevalue.dto.TimeValueBean;
import com.xjy.timevalue.mbg.model.News;


public interface TimeValueService {
    public TimeValueBean getTimeValue(News news)throws Exception;
}
