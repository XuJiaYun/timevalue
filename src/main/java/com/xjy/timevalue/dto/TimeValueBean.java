package com.xjy.timevalue.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;

@Data
public class TimeValueBean implements Serializable {
    public final static int SPORTS_NEWS_TIMEVALUE = 0;
    public final static int ENTERTAINMENT_NEWS_TIMEVALUE = 1;
    public final static int EDU_NEWS_TIMEVALUE = 2;
    public final static int POLITICAL_NEWS_TIMEVALUE = 3;
    public final static int GAME_NEWS_TIMEVALUE = 4;
    public final static int SOCIAL_NEWS_TIMEVALUE = 5;
    public final static int TECH_NEWS_TIMEVALUE = 6;
    public final static int FIN_NEWS_TIMEVALUE = 7;
    public final static double[] cat2time = {1.5,2.0,5.0,3.0,3.0,3.0,4.0,2.0};
    public final static String[] cat2name = {"体育","娱乐","教育","政治","游戏","社会","科技","金融"};

    private String categoryName;
    private int catId;
    private String title;
    private String author;
    private String content;
    private String cleanContent;
    private double time;
    private LinkedHashMap keywords;
    private StringBuilder stringBuilder;
    private Date releaseDate;
    private Date happenDate;

    public TimeValueBean(){
        this.stringBuilder = new StringBuilder();
    }


}
