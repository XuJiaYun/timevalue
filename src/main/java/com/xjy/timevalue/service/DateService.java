package com.xjy.timevalue.service;


import com.xjy.timevalue.dto.TimeValueBean;

public interface DateService {
    public TimeValueBean getNewsHappenDate(TimeValueBean timeValueBean) throws Exception;

    public TimeValueBean adjustByDate(TimeValueBean timeValueBean) throws Exception;
}
