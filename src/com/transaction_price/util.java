package com.transaction_price;

/**
 * Created by ZhouXiang on 2016/11/2.
 */
public class util {
    public static String tidyData(String str){
        String tidy=str.replace("\r\n","").replace("\n","").replace("\b","").replace("\t","").trim();
        return tidy;
    }
}
