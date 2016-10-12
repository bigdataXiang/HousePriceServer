package com.reprocess.grid_100.util;

/**
 * Created by ZhouXiang on 2016/10/12.
 */
public class Source {

    public static String getSource(String s){
        String source="";
        if(s.equals("我爱我家")){
            source="woaiwojia";
        }else if(s.equals("房天下")){
            source="fang";
        }else if(s.equals("安居客")){
            source="anjuke";
        }else if(s.equals("链家")){
            source="lianjia";
        }
        return source;
    }
}
