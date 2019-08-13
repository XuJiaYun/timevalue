package com.xjy.timevalue.mbg.mapper;

import com.xjy.timevalue.mbg.model.Topic;
import net.sf.jsqlparser.statement.select.Top;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface TopicMapper {
    public List<Topic> getAllTopic();

    public void insertTopicNews(@Param("topicId") Integer topidId,@Param("newsId") Integer newsId);

    public int updateCount(@Param("topicId") Integer topicId);

    public int insertTopic(Topic topic);

    public int getCount(@Param("topicId") Integer id);

}
