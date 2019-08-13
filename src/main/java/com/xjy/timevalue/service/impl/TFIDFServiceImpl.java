package com.xjy.timevalue.service.impl;

import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import com.xjy.timevalue.common.utils.TimeValueUtil;
import com.xjy.timevalue.mbg.mapper.NewsMapper;
import com.xjy.timevalue.mbg.mapper.TopicMapper;
import com.xjy.timevalue.mbg.model.News;
import com.xjy.timevalue.mbg.model.Topic;
import com.xjy.timevalue.service.TopicService;
import net.sf.jsqlparser.statement.select.Top;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.alibaba.druid.sql.ast.SQLPartitionValue.Operator.List;
@Service
public class TFIDFServiceImpl implements TopicService {

    private final static Logger logger = LoggerFactory.getLogger(TFIDFServiceImpl.class);

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private TopicMapper topicMapper;

    public boolean adjustRelativeNews(List<News> list){
        for(News news : list){
            newsMapper.adjustTimeValue(0.8,news.getId());
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

    public News adjustNewsByTitleKeyWord(News news){
        double threhold = 0.4;
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
                adjustRelativeNews(newsList);
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
        return getSimilarity(newsA,newsB,3);
    }

    public double getSimilarity(News newsA, News newsb,int titleWeight){
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
        return getSimilarity(a,b,5);
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

    public static void main(String[] args) {
        TFIDFServiceImpl service = new TFIDFServiceImpl();
        String a = "1、徐帆冯小刚。冯小刚是国内知名导演，徐帆也是一位演技出众的演员，虽然两个人结婚很多年了，但是至今没有孩子，有人说是因为冯小刚患有白癜风，担心会遗传给孩子。2、杨丽萍。杨丽萍国家一级舞蹈艺术家，可以说她是为舞蹈而生的，并且为了自己心爱的舞蹈事业，放弃了生孩子做妈妈。刘嘉玲。刘嘉玲自从与梁朝伟结婚以来，一直未生育过孩子，其中的原因也成了网友们热议的话题，或许是因为两个人的事业心太强，不想要孩子吧。4、刘雪华。刘雪华与张佩华、刘德凯都有过记忆深刻的感情，刘雪华怀了第2个男朋友刘德凯的孩子，然而却发现刘德凯劈腿了法国女郎安琪，当时她精神恍惚，不慎摔倒流产，再也不能生育。5、刘晓庆。刘晓庆这一生可以说是很传奇了，她有过4次的婚姻，还进过监狱，60岁了还扮演16岁的少女。不过令人意外的是，她有过这么多次婚姻，却至今没有生下一个孩子，她的理由是地球上的人太多了。\n";
        String b = "随着现在社会的发展，每个家庭基本上都会过着宽裕且幸福的生活，当我们生了孩子之后，也会给孩子最好的物质基础，但是这样就会导致很多孩子由于父母的溺爱变得越来越自私和叛逆。很多家长由于没有及时的教育孩子就会让他们这种唯我独尊的想法变得越来越大。这样的孩子长大之后只会给父母带来更多的烦恼，因为他们不懂得自律，不懂得反省，只觉得自己做的都是对的，这样只会让孩子在将来长大自己面对事情的时候越来越吃亏，所以父母应该懂得及时的教育好自己的孩子，这样才能让自己以后上省点心，让孩子变得越来越优秀。所以家长们就要注意了，当孩子出现这几种表现的时候就已经被惯坏了，家长们就需要收收孩子的脾性了。不尊重长辈现在的孩子跟过去的孩子相比差距很大，在过去，我们会尊重自己的长辈，但是放在现在这个年代小孩子们普遍都不会尊重自己的长辈。这其中很大的原因就是因为长辈太过溺爱自己的孩子所造成的，当孩子犯了一件错误之后。父母或长辈由于太心疼自己的孩子就不会去跟孩子计较什么，所以这就会导致孩子变得更加自私，自大，唯我独尊了，所以他们有的时候甚至还会辱骂自己的父母或者长辈。这样的孩子长大之后，由于缺乏孝顺的理念，对自己的亲人自然就会很不好了。不听父亲的话绝大部分的孩子在家里面都是非常害怕自己的父亲的，因为在父亲面前就不会那么好说话了，父亲不像母亲一样，在面对孩子错误的时候心软，而是用自己的威严与理智去教育孩子，所以说在孩子的面前父亲是有绝对威严性的。但是当你的孩子，如果连自己父亲的话都不听的时候就证明这个家里已经没有人能管得住孩子了，这样的话就会让孩子变得越来越狂妄自大，如果我们不及时教育好孩子的话，还会让他们做出更多不让父母伤心的事情来。做事情太过随意每个家里都会有自己的家规，这对于孩子的教育来说，十分重要，但是如果你的孩子在家里面做事情随心所欲没有任何规矩而言的话，就会让孩子变得越来越叛逆了。俗话说，无规矩不成方圆，孩子如果从小生活在一个随意的环境当中，那么他就只会变得对自身没有要求，而且很随意了，如果孩子不能在吃饭的时候规规矩矩地坐在餐桌上吃饭，不能在睡觉的时候安分的睡觉，就证明孩子已经被父母给惯坏了，这个时候父母也是需要多加教育的。所以说，孩子若是出现以上表现了，家长可要严格教育孩子，不然受累的还是自己\n";
        double value = service.getSimilarity(TimeValueUtil.cleanString(a),TimeValueUtil.cleanString(b),7);
        System.out.println(String.format("%f",value));
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
