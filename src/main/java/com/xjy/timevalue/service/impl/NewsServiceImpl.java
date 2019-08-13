package com.xjy.timevalue.service.impl;



import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xjy.timevalue.mbg.mapper.NewsMapper;
import com.xjy.timevalue.mbg.model.News;
import com.xjy.timevalue.mbg.model.NewsExample;
import com.xjy.timevalue.service.NewsService;
import com.xjy.timevalue.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private RedisService redisService;

    @Value("${redis.key.prefix.News}")
    private String NEWS_PREFIX_KEY;

    @Value("${redis.key.expire.News}")
    private Long NEWS_EXPIRE;

    @Override
    public News saveNews(News news) {
         newsMapper.insert(news);
         return news;
    }

    public List<News> findByTitleContainsKeyWord(String keyword){
        List<News> list = newsMapper.selectByTitleLike(keyword);
        return list;
    }

    @Override
    public List listNews(int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<News> newsList = newsMapper.listAllNewsByReleaseTime();
        return newsList;
    }

    @Override
    public News getNewsById(int id) {
        String content = redisService.get(NEWS_PREFIX_KEY + id);
        if(content != null){
            return JSONObject.parseObject(content,News.class);
        }
        News news = newsMapper.selectByPrimaryKey(id);
        redisService.set(NEWS_PREFIX_KEY + news.getId(),JSONObject.toJSONString(news));
        redisService.expire(NEWS_PREFIX_KEY + news.getId(),NEWS_EXPIRE);
        return news;
    }
}
