package com.xjy.timevalue.controller;


import com.github.pagehelper.PageInfo;
import com.xjy.timevalue.dto.Message;
import com.xjy.timevalue.dto.TimeValueBean;
import com.xjy.timevalue.mbg.model.News;
import com.xjy.timevalue.mq.AdjustMessageSender;
import com.xjy.timevalue.service.NewsService;
import com.xjy.timevalue.service.TimeValueService;
import com.xjy.timevalue.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
@CrossOrigin(origins = {"http://localhost:8084"})
@RestController
public class NewsController {

    @Autowired
    AdjustMessageSender adjustMessageSender;
    @Autowired
    private NewsService newsService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private TimeValueService timeValueService;


    //传入新闻，设置其时效性，存入数据库并进行调整
    @RequestMapping(value = "/news/insert",method = RequestMethod.POST)
    public Message getTimeValue(@RequestBody News news) throws Exception{
        TimeValueBean timeValueBean = timeValueService.getTimeValue(news);
        Message message = new Message();
        message.ok(1000,"ok");
        message.addData("timeValueBean",timeValueBean);
        return message;
    }

    @RequestMapping("/test/showResult")
    public void getList(){
        topicService.showResult();
    }


    //列表展示新闻
    @RequestMapping(value = "/news/list/{pageNum}/{pageSize}",method = RequestMethod.GET)
    public Message listNews(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize,
                            @RequestParam(value = "title",required = false) String title){
        List list = newsService.listNews(pageNum,pageSize,title);
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

    //查看新闻
    @RequestMapping(value = "/news/{id}",method = RequestMethod.GET)
    public Message getNewsById(@PathVariable("id")int id){
        News news = newsService.getNewsById(id);
        Message message = new Message();
        message.ok(1000,"ok");
        message.addData("news",news);
        return message;
    }

    //删除新闻
    @RequestMapping(value = "/news/{id}",method = RequestMethod.DELETE)
    public Message removeNewsById(@PathVariable("id") int id){
        newsService.deleteNews(id);
        Message message = new Message();
        message.ok(1000,"ok");
        return message;
    }

    //批量删除新闻
    @RequestMapping(value = "/news/batchRemove",method = RequestMethod.DELETE)
    public Message batchRemove(@RequestParam(value = "ids") Integer[] ids){
        newsService.batchRemove(ids);
        Message message = new Message();
        message.ok(1000,"ok");
        return message;
    }


}
