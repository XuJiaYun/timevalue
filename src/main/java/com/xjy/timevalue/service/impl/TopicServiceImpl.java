package com.xjy.timevalue.service.impl;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import com.xjy.timevalue.common.utils.TimeValueUtil;
import com.xjy.timevalue.mbg.model.News;
import com.xjy.timevalue.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
@Service
public class TopicServiceImpl {

    private static int count = 0;

    private static final Logger log = LoggerFactory.getLogger(TopicServiceImpl.class);

    @Autowired
    private NewsService newsService;

    private static Word2VecModel model;
    static{
        init();
    }

    private static void init(){
        try{
            model = Word2VecModel.fromTextFile(new File("D:\\新浪新闻文本分类\\THUCNews\\embedding1.txt"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public double adjustTime(News newsBean){
        return 0.0;
    }

    public News adjustNewsByTitleKeyWord(News news){
        ArrayList<String> keyWordsFromTitle = TimeValueUtil.findKeyWord(TimeValueUtil.cleanString(news.getTitle()), 5);
        ArrayList<String> keyWordsFromContent = TimeValueUtil.findKeyWord(TimeValueUtil.cleanString(news.getContent()),3);
        HashSet<News> set = new HashSet<>();
        for(String keyWord : keyWordsFromTitle){
            List<News> newsBeans = newsService.findByTitleContainsKeyWord(keyWord);
            for(News newsFound:newsBeans){
                if(newsFound.getContent().equals(news.getContent())){
                    continue;
                }
                set.add(newsFound);
            }
        }
        //log.info("根据" + news.getTitle()+"题目关键词找到了"+set.size()+"篇相关文章");
        for(News newsFound:set){
            long time = System.currentTimeMillis();
            ArrayList<String> keyWords = TimeValueUtil.findKeyWord(TimeValueUtil.cleanString(newsFound.getContent()),3);
            //看两篇文章的内容相似程度
            int sim = getSimilarityCountByKeyWords(keyWordsFromContent,keyWords,0.7);
            //如果有3个关键词中有2个相近，认为是同一个主题
            if(sim >= 1){
                adjustTime(news);
                count++;
                log.info("根据"+news.getTitle()+"调整了"+newsFound.getTitle()+"的时效性");
                log.info("目前已经调整了"+count+"个新闻的时效性");
            }

        }
        return news;
    }

    private int getSimilarityCountByKeyWords(ArrayList<String> list1,ArrayList<String> list2,double threshold){
        int result = 0;
        for(int i = 0;i < list1.size();i++){
            double max = 0;
            int index = 0;
            String s1 = list1.get(i);
            for(int j = 0;j < list2.size();j++){
                String s2 = list2.get(j);
                double temp = getSimilarity(s1,s2);
                if(temp > max){
                    max = temp;
                    index = j;
                }
            }
            if(max > threshold){
                result++;
                list2.remove(index);
            }
        }
        return result;
    }

    private double getSimilarity(String a,String b){
        Searcher searcher = model.forSearch();
        double result = 0;
        try{
            result = searcher.cosineDistance(a,b);
        }catch (Exception e){
        }
        return result;
    }

}
