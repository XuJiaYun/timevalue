package com.xjy.timevalue.controller;


import com.github.pagehelper.PageInfo;
import com.xjy.timevalue.dto.Message;
import com.xjy.timevalue.dto.TimeValueBean;
import com.xjy.timevalue.common.utils.TimeValueUtil;
import com.xjy.timevalue.mbg.model.News;
import com.xjy.timevalue.mbg.model.Topic;
import com.xjy.timevalue.mq.AdjustMessageSender;
import com.xjy.timevalue.service.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
@CrossOrigin(origins = {"http://localhost:8085"})
@RestController
public class NewsController {

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


    @RequestMapping(value = "/test",method = RequestMethod.POST)
    public TimeValueBean getTimeValue(News news) throws Exception{
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
        BigDecimal bd = new BigDecimal(timeValue);
        news.setTimeValue(bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        news = newsService.saveNews(news);
        //向消息队列发送消息
        adjustMessageSender.sendAdjustNews(news);
        news.setTimeValue(timeValueBean.getTime());
        news.setInformation(timeValueBean.getStringBuilder().toString());

        return timeValueBean;
    }

    @RequestMapping("/test/showResult")
    public void getList(){
        topicService.showResult();
    }


    @RequestMapping(value = "/news/list/{pageNum}/{pageSize}",method = RequestMethod.GET)
    public Message listNews(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize){
        List list = newsService.listNews(pageNum,pageSize);
        PageInfo<News> pageInfo = new PageInfo<>(list);
        HashMap<String,Object> map = new HashMap<>();
        Message message = new Message();
        message.ok(1000,"ok");
        message.addData("pageNum",pageInfo.getPageNum());
        message.addData("pageSize",pageInfo.getPageSize());
        message.addData("maxPage",pageInfo.getPages());
        message.addData("newsList",list);
        message.addData("total",pageInfo.getTotal());
        return message;
    }

    @RequestMapping("/news/{id}")
    public Message getNews(@PathVariable("id")int id){
        News news = newsService.getNewsById(id);
        Message message = new Message();
        message.ok(1000,"ok");
        message.addData("news",news);
        return message;
    }
}
