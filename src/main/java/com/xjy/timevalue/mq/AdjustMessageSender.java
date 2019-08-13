package com.xjy.timevalue.mq;

import com.alibaba.fastjson.JSONObject;
import com.xjy.timevalue.mbg.model.News;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdjustMessageSender {

    private static final Logger logger = LoggerFactory.getLogger(AdjustMessageSender.class);

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void sendAdjustNews(News news){
        String message = JSONObject.toJSONString(news);
        rabbitTemplate.convertAndSend("adjustQueue",message);
        //logger.info("向adjustQueue中放入 "+ news.getTitle()+" 等待调整相关主题新闻");

    }

}
