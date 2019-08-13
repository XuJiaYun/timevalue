package com.xjy.timevalue.service.impl;


import com.xjy.timevalue.dto.TimeValueBean;
import com.xjy.timevalue.service.DateService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DateServiceImpl implements DateService {

    private static final Pattern pattern = Pattern.compile("(((([0-9]{4})年)?([0-9]{2}|[1-9]))月([0-9]{2}|[1-9]))日");

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");

    public TimeValueBean getNewsHappenDate(TimeValueBean timeValueBean) throws Exception{
        Matcher m = pattern.matcher(timeValueBean.getTitle() + timeValueBean.getContent());
        Date now = new Date();
        Date happenDate = null;
        String s = null;
        while(m.find()){
            String dateString = null;
            if(m.group().charAt(1) == '月' || m.group().charAt(2) == '月'){
                dateString = getSysYear() + '年' + m.group();
            }else{
                dateString = m.group();
            }
            Date findDate = simpleDateFormat.parse(dateString);
            //截取到的时间比现在大，说明时间表示未来，不是事件发生时间，跳过
            if(now.compareTo(findDate) < 0){
                continue;
            }else{
                //选择距离现在最近的时间点作为事件发生时间
                if(happenDate == null || happenDate.compareTo(findDate) < 0){
                    happenDate = findDate;
                }
            }

        }
        //没有找到发现时间，用发布文章时间作为事件发生事件
        if(happenDate == null){
            happenDate = timeValueBean.getReleaseDate();
            timeValueBean.getStringBuilder().append("没有从内容中获取事件时间信息，使用文章发布时间" +
                    simpleDateFormat.format(happenDate) + "作为事件发生时间" + "\n");
            timeValueBean.setHappenDate(timeValueBean.getReleaseDate());
            return timeValueBean;
        }else{
            timeValueBean.getStringBuilder().append("从内容中获取事件发生时间为" + simpleDateFormat.format(happenDate) + "\n");
            timeValueBean.setHappenDate(happenDate);
            return timeValueBean;
        }


    }

    @Override

    public TimeValueBean adjustByDate(TimeValueBean timeValueBean) throws Exception{
        timeValueBean = getNewsHappenDate(timeValueBean);
        if(!timeValueBean.getHappenDate().equals(timeValueBean.getReleaseDate())){
            double time = timeValueBean.getTime();
            Date now = new Date();
            Date happenDate = timeValueBean.getHappenDate();
            //相差天数
            int days = (int)((now.getTime() - happenDate.getTime())/(24 * 60 * 60 * 1000));
            timeValueBean.getStringBuilder().append("新闻发生至今已" + days + "天" + "\n");
            //信息老化模型 a:系数
            double a = 0.25;
            double newtime = time * Math.exp(-1 * a * days);
            timeValueBean.getStringBuilder().append("基于新闻时间间隔调整时效性从"+time+"至"+newtime+"\n");
            timeValueBean.setTime(newtime);
        }else{
            timeValueBean.getStringBuilder().append("不基于日期对时效性进行调整\n");
        }

        return timeValueBean;
    }

    public static String getSysYear() {

        Calendar date = Calendar.getInstance();

        String year = String.valueOf(date.get(Calendar.YEAR));

        return year;

    }


}
