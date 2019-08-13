package com.xjy.timevalue.common.utils;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.qianxinyao.analysis.jieba.keyword.Keyword;
import com.qianxinyao.analysis.jieba.keyword.TFIDFAnalyzer;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class TimeValueUtil {
    private static TFIDFAnalyzer analyzer = new TFIDFAnalyzer();

    private static final Pattern pattern = Pattern.compile("[^A-Za-z0-9 \u4e00-\u9fa5]");

    public static LinkedHashMap<String,Double> findKeyWordMap(String content,int topN) {
        List<Keyword> list = analyzer.analyze(content,topN);
        LinkedHashMap<String,Double> map = new LinkedHashMap<>();
        for(Keyword keyword:list){
            map.put(keyword.getName(),keyword.getTfidfvalue());
        }
        return map;
    }

    public static ArrayList<String> findKeyWord(String content, int topN) {
        ArrayList<String> arrayList = new ArrayList<>();
        List<Keyword> list = analyzer.analyze(content,topN);
        for(Keyword keyword:list){
            arrayList.add(keyword.getName());
        }
        return arrayList;
    }

    public static double getStringTFIDF(String word,String content){
        return analyzer.getTFIDFFromContent(word,content);
    }

    public static String cleanString(String content){
        return pattern.matcher(content).replaceAll("");
    }

}
