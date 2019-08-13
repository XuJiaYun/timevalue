package com.xjy.timevalue.mq;

import com.alibaba.fastjson.JSONObject;
import com.xjy.timevalue.mbg.model.News;
import com.xjy.timevalue.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "adjustQueue")
public class AdjustMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AdjustMessageConsumer.class);

    @Autowired
    private TopicService topicService;

    @RabbitHandler
    public void adjustTime(String message){
        News news = JSONObject.parseObject(message,News.class);
        //logger.info("从adjust队列中取出 "+ news.getTitle() + "进行调整");
        topicService.adjustNewsByTitleKeyWord(news);
    }
}
