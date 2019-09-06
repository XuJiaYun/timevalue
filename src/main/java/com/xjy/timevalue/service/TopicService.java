package com.xjy.timevalue.service;

import com.xjy.timevalue.mbg.model.News;

public interface TopicService {
    public News adjustNewsByTopic(News news);

    public void showResult();
}
