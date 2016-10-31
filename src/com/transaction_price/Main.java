package com.transaction_price;

import utils.FileTool;

/**
 * Created by ZhouXiang on 2016/10/31.
 */
public class Main extends Resold{
    public static String regions_lianjia[]={
            "dongcheng","xicheng","chaoyang","haidian","fengtai","shijingshan","tongzhou","changping","daxing","yizhuangkaifaqu","shunyi","fangshan",
            "mentougou","pinggu","huairou","miyun","yanqing","yanjiao"};
    public static String regions_fang[] = {
            "/chengjiao-a01/", "/chengjiao-a00/", "/chengjiao-a06/", "/chengjiao-a02/", "/chengjiao-a03/", "/chengjiao-a04/",
            "/chengjiao-a05/", "/chengjiao-a07/", "/chengjiao-a012/", "/chengjiao-a0585/", "/chengjiao-a010/", "/chengjiao-a011/",
            "/chengjiao-a08/", "/chengjiao-a013/", "/chengjiao-a09/", "/chengjiao-a014/", "/chengjiao-a015/", "/chengjiao-a016/",
            "/chengjiao-a0987/", "/chengjiao-a011817/",
    };
    public static void main(String[] args){

        for(int i=0;i<regions_lianjia.length;i++){
            getResoldApartmentInfo_lianjia(regions_lianjia[i]);
            String str="已经抓取完"+i+":"+regions_lianjia[i];
            FileTool.Dump(str,"D:\\test\\lianjia\\finishlog.txt","utf-8");
        }


    }
}
