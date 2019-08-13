package com.xjy.timevalue.service;

import com.xjy.timevalue.dto.TimeValueBean;
import com.xjy.timevalue.mbg.model.News;

public interface TopicService {
    public News adjustNewsByTitleKeyWord(News news);

    public void showResult();
}
