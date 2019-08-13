package com.xjy.timevalue.service;



import com.xjy.timevalue.mbg.model.News;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewsService {
    public News saveNews(News news);

    public List findByTitleContainsKeyWord(String keyWord);

    public List listNews(int pageNum,int pageSize);

    public News getNewsById(int id);
}
