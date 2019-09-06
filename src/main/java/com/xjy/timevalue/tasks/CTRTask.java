package com.xjy.timevalue.tasks;

import com.xjy.timevalue.mbg.model.News;
import com.xjy.timevalue.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
@EnableScheduling
public class CTRTask {

    private final static Logger logger = LoggerFactory.getLogger(CTRTask.class);

    @Autowired
    private NewsService newsService;

    DateFormat df = DateFormat.getDateTimeInstance();

    @Scheduled(fixedRate = 7200000)
    public void runCTRTask(){
        List<News> newsList = newsService.listAllNews();
        for(News news:newsList){
            //如果新闻含有“今天”字眼则不会根据CTR延长它的时效性
            if(!news.getContent().substring(0,news.getContent().length()>20?20:news.getContent().length()).contains("今天")){
                int showContent = new Random().nextInt(3000)+1;
                int click = new Random().nextInt((int)(showContent*0.3)+1);
                double CTR = (click*1.0)/showContent;
                BigDecimal bd = new BigDecimal(CTR);
                CTR = bd.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
                if(CTR > 0.15 && showContent >= 2000){
                    news.setTimeValue(news.getTimeValue()+0.5);
                    news.setEndTime(new Date((long)(news.getReleaseTime().getTime()+news.getTimeValue()*24*60*60*1000)));
                    news.setInformation(news.getInformation()+df.format(new Date()) + ": "+"根据CTR +"+CTR+"调整时效性至"+news.getTimeValue()+"\n");
                    logger.info(df.format(new Date()) + ": "+"根据CTR +"+CTR+"调整"+news.getTitle()+"时效性至"+news.getTimeValue());
                    newsService.updateNews(news);
                }
            }

        }
    }

}
