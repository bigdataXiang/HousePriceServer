package com.transaction_price;

import com.mongodb.DBCollection;
import com.svail.db.db;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.Vector;

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

        /*for(int i=0;i<regions_lianjia.length;i++){
            getResoldApartmentInfo_lianjia(regions_lianjia[i]);
            String str="已经抓取完"+i+":"+regions_lianjia[i];
            FileTool.Dump(str,"D:\\test\\lianjia\\finishlog.txt","utf-8");
        }*/


/*//将所有小区的信息混合到Deals_community表中，方便查询检索
        Vector<String> pois=FileTool.Load("D:\\小论文\\dealdata\\小区名\\小区集合.txt","utf-8");
        for(int i=1260;i<pois.size();i++){
            String poi=pois.elementAt(i);
            DealPriceFitting.communityResearch(poi);
            System.out.println(i);
        }*/

        //将所有小区的成交数据与挂牌数据联合起来
        /*Vector<String> pois=FileTool.Load("D:\\小论文\\dealdata\\小区名\\小区集合.txt","utf-8");
        for(int i=7666;i<pois.size();i++){
            String poi=pois.elementAt(i);
            DealPriceFitting.queryCommunityTransaction(poi);
            System.out.println("数据联合："+i);
        }*/

        DealPriceFitting.findHangoutDeals("D:\\小论文\\dealdata\\小区名\\成交数据_挂牌数据\\成交数据_挂牌数据.txt");
    }
}
