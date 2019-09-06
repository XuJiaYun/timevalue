package com.xjy.timevalue.service.impl;


import com.xjy.timevalue.common.utils.TimeValueUtil;
import com.xjy.timevalue.dto.TimeValueBean;
import com.xjy.timevalue.service.ClassificationService;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.tensorflow.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeMap;

@Service
public class ClassificationServiceImpl implements ClassificationService {
    //private static String path = "D:\\新浪新闻文本分类\\THUCNews\\model_pb_3";
    //private static String path = "D:\\新浪新闻文本分类\\THUCNews\\froze\\model.pb";
    private static String realPath = ClassificationServiceImpl.class.getClassLoader().getResource("").getPath();
    private static String path = realPath+"\\static\\model.pb";
    //private static String word2vecpath = "D:\\新浪新闻文本分类\\THUCNews\\vocabulary4.txt";
    private static String word2vecpath = realPath+"\\static\\vocabulary4.txt";
    private static String tags = "20190726";
    private static ArrayList<String> labelList = new ArrayList();
    private static TreeMap<Character,Integer> map = new TreeMap<Character, Integer>();
    private static SavedModelBundle b;
    private static Graph graph = new Graph();

    static {
        try {
            initService();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //初始化操作，加载词汇表，读取模型
    private static void initService() throws IOException {
        labelList.add("体育");
        labelList.add("娱乐");
        labelList.add("教育");
        labelList.add("时政");
        labelList.add("游戏");
        labelList.add("社会");
        labelList.add("科技");
        labelList.add("财经");
        int count = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(word2vecpath), "UTF-8"));
        String str;
        while((str = br.readLine()) != null){
            if(count == 0){
                count++;
                continue;
            }
            if(str.length() > 0){
                map.put(str.charAt(0),count++);
            }

        }
        br.close();
        graph.importGraphDef(IOUtils.toByteArray(new FileInputStream(path)));
    }
    @Override
    public TimeValueBean classify(TimeValueBean timeValueBean){
        String content = timeValueBean.getTitle()+timeValueBean.getContent();
        String cleanContent = TimeValueUtil.cleanString(content);
        int[] idList = word2id(cleanContent);
        int[][] inputArray = new int[1][10000];
        for(int i = idList.length-1, j = inputArray[0].length-1;i >= 0 && j >= 0;i--,j--){
            inputArray[0][j] = idList[i];
        }
        Session tfSession = new Session(graph);
        Operation operationPredict = graph.operation("predict_Y");
        Output output = new Output(operationPredict,0);
        Tensor input = Tensor.create(inputArray);
        Tensor out = tfSession.runner().feed("X_holder",input).fetch(output).run().get(0);
        float [][] ans = new float[1][8];
        out.copyTo(ans);
        int i = getMax(ans[0]);
        timeValueBean.setCatId(i);
        timeValueBean.setCategoryName(TimeValueBean.cat2name[i]);
        timeValueBean.setTime(TimeValueBean.cat2time[i]);
        StringBuilder sb = timeValueBean.getStringBuilder();
        sb.append("初始化类别为"+TimeValueBean.cat2name[i]+"\n");
        sb.append("初始化时效为"+TimeValueBean.cat2time[i]+"\n");
        timeValueBean.setContent(content);
        timeValueBean.setCleanContent(cleanContent);
        return timeValueBean;
    }


    public static int getMax(float[] a){
        float M=0;
        int index2=0;
        for(int i=0;i<a.length;i++){
            if(a[i]>M){
                M=a[i];
                index2=i;
            }
        }
        return index2;
    }




    private static int[] word2id(String word){
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0;i < word.length();i++){
            if(map.containsKey(word.charAt(i))){
                list.add(map.get(word.charAt(i)));
            }
        }
        int[] intArr = list.stream().mapToInt(Integer::intValue).toArray();
        return intArr;
    }
}
