package com.xjy.timevalue.service.impl;

import com.xjy.timevalue.common.utils.TimeValueUtil;
import com.xjy.timevalue.mbg.mapper.NewsMapper;
import com.xjy.timevalue.mbg.mapper.TopicMapper;
import com.xjy.timevalue.mbg.model.News;
import com.xjy.timevalue.mbg.model.Topic;
import com.xjy.timevalue.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class TFIDFServiceImpl implements TopicService {

    private final static Logger logger = LoggerFactory.getLogger(TFIDFServiceImpl.class);

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private TopicMapper topicMapper;
    //调整每个新闻的时效性以及时效性变化后的截止时间
    public boolean adjustRelativeNews(List<News> list,News fromNews){
        for(News news : list){
            double timeValue = news.getTimeValue()*0.8;
            BigDecimal bd = new BigDecimal(timeValue);
            news.setTimeValue(bd.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
            news.setEndTime(new Date((long)(news.getReleaseTime().getTime()+news.getTimeValue()*24*60*60*1000)));
            news.setInformation(news.getInformation()+"由《"+fromNews.getTitle()+"》调整时效性至"+news.getTimeValue()+"\n");
            newsMapper.updateByPrimaryKeySelective(news);
        }
        return true;
    }

    private int updateTopicAndInsertTopicNews(News news,Integer topicId){
        topicMapper.insertTopicNews(topicId,news.getId());
        topicMapper.updateCount(topicId);
        int count = topicMapper.getCount(topicId);
        return count;
    }

    private int createTopicAndInsert(News news){
        Topic topic = new Topic();
        topic.setEnergy(0.0);
        topic.setCount(1);
        topicMapper.insertTopic(topic);
        System.out.println("创建的话题id为"+topic.getId());
        topicMapper.insertTopicNews(topic.getId(),news.getId());
        return topic.getId();
    }

    public News adjustNewsByTopic(News news){
        double threhold = 0.35;
        List<Topic> list = topicMapper.getAllTopic();
        //在各个话题里找最合适的
        for(Topic topic:list){
            Integer id = topic.getId();
            List<News> newsList = newsMapper.selectNewsFromTopic(id);
            double sum = 0.0;
            int num = 0;
            boolean unrelated = false;
            for(News news1:newsList){
                Double sim = getSimilarity(news,news1);
                if(sim.equals(0.000000)){
                    unrelated = true;
                }
                sum += sim;
                num++;
            }
            if(unrelated){
                continue;
            }
            double average = sum/num;
            if(average > threhold){
                //调整话题内新闻的时效性
                adjustRelativeNews(newsList,news);
                logger.info("将" + news.getTitle()+"归入"+id+"话题");
                int count = updateTopicAndInsertTopicNews(news,id);
                logger.info("话题"+id+"目前共有"+count+"篇文章");
                return news;
            }
        }
        //没有找到属于该文章的话题，自己成为一个话题
        int topicId = createTopicAndInsert(news);
        logger.info(news.getTitle()+"没有找到相应主题"+"自己建立一个主题为"+topicId);
        return news;
    }
    public double getSimilarity(News newsA,News newsB){
        if(newsA.getContent().length() >= 500 && newsA.getContent().length()>=500){
            return getSimilarity(newsA,newsB,10,4);
        }
        return getSimilarity(newsA,newsB,3,6);
    }

    public double getSimilarity(News newsA, News newsb,int titleWeight,int count){
        String a = "";
        String b = "";
        for(int i = 0;i < titleWeight;i++){
            a += newsA.getTitle();
            b += newsb.getTitle();
        }
        a += newsA.getContent();
        b += newsb.getContent();
        a = TimeValueUtil.cleanString(a);
        b = TimeValueUtil.cleanString(b);
        return getSimilarity(a,b,count);
    }

    public double getSimilarity(String a,String b,int count){
        ArrayList<String> keyWordsFromA = TimeValueUtil.findKeyWord(a,count);
        ArrayList<String> keyWordsFromB = TimeValueUtil.findKeyWord(b,count);
        for(String s : keyWordsFromB){
            if(!keyWordsFromA.contains(s)){
                keyWordsFromA.add(s);
            }
        }
        double[] tfidfA = new double[keyWordsFromA.size()];
        double[] tfidfB = new double[keyWordsFromA.size()];
        for(int i = 0;i < keyWordsFromA.size();i++){
            tfidfA[i] = TimeValueUtil.getStringTFIDF(keyWordsFromA.get(i),a);
            tfidfB[i] = TimeValueUtil.getStringTFIDF(keyWordsFromA.get(i),b);
        }
        return calculateSimilarity(tfidfA,tfidfB);
    }
    private static double calculateSimilarity(double[] a,double[] b){
        double sum = 0;
        double squartA = 0;
        double squartB = 0;
        for(int i = 0;i < a.length;i++){
            sum += a[i]*b[i];
            squartA += Math.pow(a[i],2);
            squartB += Math.pow(b[i],2);
        }
        double value = sum/(Math.sqrt(squartA)*Math.sqrt(squartB));
        return value;
    }


    public void showResult(){
        List<Topic> topicList = topicMapper.getAllTopic();
        for(int i = 0;i < topicList.size();i++){
            Topic topic = topicList.get(i);
            System.out.println("topic"+topic.getId()+"共有"+topic.getCount()+"条新闻：");
            List<News> newsList = newsMapper.selectNewsFromTopic(topic.getId());
            for(News news:newsList){
                System.out.println(news.getTitle());
            }
        }
    }
}
