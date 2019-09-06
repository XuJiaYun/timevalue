package com.xjy.timevalue.service;


import com.xjy.timevalue.mbg.model.News;

import java.util.List;

public interface NewsService {
    public News saveNews(News news);

    public List findByTitleContainsKeyWord(String keyWord);

    public List listNews(int pageNum,int pageSize,String title);

    public News getNewsById(int id);

    public int deleteNews(int id);

    public boolean batchRemove(Integer[] ids);

    public List listAllNews();

    public int updateNews(News news);
}
