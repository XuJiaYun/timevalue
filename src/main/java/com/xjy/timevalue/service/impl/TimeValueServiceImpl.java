package com.xjy.timevalue.service.impl;

import com.xjy.timevalue.common.utils.TimeValueUtil;
import com.xjy.timevalue.dto.Message;
import com.xjy.timevalue.dto.TimeValueBean;
import com.xjy.timevalue.mbg.model.News;
import com.xjy.timevalue.mq.AdjustMessageSender;
import com.xjy.timevalue.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class TimeValueServiceImpl implements TimeValueService {

    @Autowired
    AdjustMessageSender adjustMessageSender;
    @Autowired
    private ClassificationService classificationService;
    @Autowired
    private DateService dateService;
    @Autowired
    private NewsService newsService;
    @Autowired
    private TopicService topicService;

    @Override
    public TimeValueBean getTimeValue(News news)throws Exception{
        System.out.println(news.getTitle());
        news.setTitle(news.getTitle().trim());
        news.setContent(news.getContent().trim());
        TimeValueBean timeValueBean = new TimeValueBean();
        timeValueBean.setTitle(news.getTitle());
        timeValueBean.setContent(news.getContent());
        timeValueBean.setReleaseDate(news.getReleaseTime());
        timeValueBean.setAuthor(news.getAuthor());
        try{
            timeValueBean = classificationService.classify(timeValueBean);
        }catch (Exception e){
            e.printStackTrace();
        }
        timeValueBean.setKeywords(TimeValueUtil.findKeyWordMap(timeValueBean.getTitle()+timeValueBean.getCleanContent(),5));
        timeValueBean = dateService.adjustByDate(timeValueBean);
        double timeValue = timeValueBean.getTime();
        if(timeValue == 0){
            //内容已失效 直接返回
            return timeValueBean;
        }
        BigDecimal bd = new BigDecimal(timeValue);
        news.setTimeValue(bd.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
        news.setEndTime(new Date((long)(news.getReleaseTime().getTime()+news.getTimeValue()*24*60*60*1000)));
        news.setInformation(timeValueBean.getStringBuilder().toString());
        news = newsService.saveNews(news);
        //向消息队列发送消息
        adjustMessageSender.sendAdjustNews(news);
        return timeValueBean;
    }
}
