package com.transaction_price;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import utils.FileTool;
import utils.Tool;

import java.util.*;

/**
 * Created by ZhouXiang on 2016/10/31.
 */
public class AiWuJiWu {
    public static String[] regions_aiwujiwu={
                            "id12440","id12441","id12442","g1id12443","id12444","id12446","id12447","id12448",
                            "id12449","id12450"
                           };
    public static void main(String[] args){
        /*for(int i=0;i<regions_aiwujiwu.length;i++){
            getResoldApartmentInfo_aiwujiwu(regions_aiwujiwu[i]);
            String str="已经抓取完"+i+":"+regions_aiwujiwu[i];
            FileTool.Dump(str,"D:\\test\\aiwujiwu\\finishlog_id.txt","utf-8");
        }*/

        /*duplicateRemoval("D:\\test\\aiwujiwu\\小区id.txt");
        getDealInfo();*/
       // classifiedTransactionData("D:\\小论文\\dealdata\\AiWuJiWu\\成交记录.txt");
        importToMongo("D:\\小论文\\dealdata\\AiWuJiWu\\step1_AllDeals.txt");

    }
    //获取爱屋及乌的小区的id
    public static void getResoldApartmentInfo_aiwujiwu(String region){

        for(int page=1;page<=100;page++){

            String url = "https://www.iwjw.com/sale/beijing/g1" + region+"p"+page+"/";
            Vector<String> urls = new Vector<String>();
            Set<String> visited = new TreeSet<String>();
            urls.add(url);

            Parser parser = new Parser();

            while (urls.size() > 0) {
                // 解析页面
                url = urls.get(0);

                urls.remove(0);
                visited.add(url);

                //String content = HTMLTool.fetchURL(url, "gb2312", "get");//gb2312
                String content = Tool.fetchURL(url);
                //System.out.println(content);

                if (content == null) {
                    continue;
                }
                try {
                    parser.setInputHTML(content);
                    parser.setEncoding("utf-8");

                    NodeFilter filter = new TagNameFilter("a");
                    NodeList nodes = parser.extractAllNodesThatMatch(filter);

                    int urlnodes_size=nodes.size();
                    System.out.println(urlnodes_size);
                    if(urlnodes_size!=0){
                        for (int n = 0; n < nodes.size(); n ++)
                        {
                            TagNode tn=(TagNode)nodes.elementAt(n);
                            String purl = tn.getAttribute("href");
                            if(purl!=null){
                                if(purl.startsWith("/estate/sale/")){
                                    System.out.println(purl);
                                    JSONObject obj=new JSONObject();
                                    String estateId=purl.substring(purl.indexOf("/estate/sale/")+"/estate/sale/".length());
                                    obj.put("estateId",estateId);
                                    String str=tn.toPlainTextString().replace("\r\n","").replace("\n","").replace("\b","").replace("\t","").trim();
                                    obj.put("name",str);
                                    FileTool.Dump(obj.toString(),"D:\\test\\aiwujiwu\\小区id.txt","utf-8");
                                }
                            }else {
                                continue;
                            }



                        }
                    }
                    try {
                        Thread.sleep(500 * ((int) (Math
                                .max(1, Math.random() * 3))));
                    } catch (final InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                }catch (ParserException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }catch(NullPointerException e){
                    System.out.println(e.getMessage());
                }

            }
        }

    }

    public static Map<String,String> id_name=new HashMap<>();
    public static Map<String,String> name_id=new HashMap<>();
    public static Set<String> estateIds=new HashSet<>();
    //处理小区id，排重
    public static void duplicateRemoval(String file){
        Vector<String> ids=FileTool.Load(file,"utf-8");

        //System.out.println(ids.size());
        for(int i=0;i<ids.size();i++){
            JSONObject obj=JSONObject.fromObject(ids.elementAt(i));
            String estateId=obj.getString("estateId").replace("/","");
            //System.out.println(estateId);
            String name=obj.getString("name");

            estateIds.add(estateId);
            id_name.put(estateId,name);
            name_id.put(name,estateId);

        }
    }
    public static void getDealInfo(){
        Iterator<String> it=estateIds.iterator();
        String url="https://www.iwjw.com/ehs.action";
        int houseType;

        int size=20;
        JSONArray trades;
        String estateId;

        List<String> ids=new ArrayList<>();
        while (it.hasNext()){
            estateId=it.next();
            ids.add(estateId);
        }

        for(int i=2063;i<=ids.size();i++){//第一层循环以id为循环
            String monotor="开始第"+i+"个id抓取";
            FileTool.Dump(monotor,"D:\\test\\aiwujiwu\\抓取进程记录.txt","utf-8");
            estateId=ids.get(i);
            String community=id_name.get(estateId);
            int page=1;

            String parameter="estateId="+estateId+"&houseType="+2+"&size="+size+"&page="+page;//+"&page="+page+"&size="+size
            String s=HttpRequest.sendGet(url,parameter);
            System.out.println(s);

            JSONObject obj=JSONObject.fromObject(s);
            JSONObject data=obj.getJSONObject("data");
            int pages=data.getInt("total");

            List<JSONObject> deals=new ArrayList<>();
            JSONObject result=new JSONObject();
            if(pages>20){
                while(pages>0){//第三层循环是以页数为循环

                    parameter="estateId="+estateId+"&houseType="+2+"&size="+size+"&page="+page;
                    s=HttpRequest.sendGet(url,parameter);

                    obj=JSONObject.fromObject(s);
                    data=obj.getJSONObject("data");
                    if(data.containsKey("trades")){
                        trades=data.getJSONArray("trades");
                        System.out.println(trades.size());
                        for(int t=0;t<trades.size();t++){

                            deals.add((JSONObject) trades.get(t));
                        }
                    }
                    page++;
                    pages=pages-size;

                    try {
                        Thread.sleep(1000 * ((int) (Math
                                .max(1, Math.random() * 3))));
                    } catch (final InterruptedException e1) {
                        e1.printStackTrace();
                    } catch (NullPointerException e1) {

                        e1.printStackTrace();
                    }
                }
                result.put("community",community);
                result.put("estateId",estateId);
                result.put("data",deals);
                System.out.println("deals:"+deals.size());
                FileTool.Dump(result.toString(),"D:\\test\\aiwujiwu\\成交记录.txt","utf-8");
            }else {
                if(data.containsKey("trades")){
                    trades=data.getJSONArray("trades");
                    System.out.println(trades.size());
                    result.put("community",community);
                    result.put("estateId",estateId);
                    result.put("data",deals);
                    System.out.println("trades:"+trades.size());
                    FileTool.Dump(result.toString(),"D:\\test\\aiwujiwu\\成交记录.txt","utf-8");
                }
            }
        }
    }

    //step1:把数据整理成一条一条的形式，写到step1_AllDeals.txt中
    public static void classifiedTransactionData(String file){
        Vector<String> communities=FileTool.Load(file,"utf-8");
        for(int i=0;i<communities.size();i++){
            String poi=communities.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String community=obj.getString("community");
            JSONArray data=obj.getJSONArray("data");
            System.out.println(data.size());

            if(data.size()>0){
                for(int j=0;j<data.size();j++){
                    String deal=data.get(j).toString();
                    //System.out.println(deal);

                    JSONObject deal_obj=JSONObject.fromObject(deal);
                    deal_obj.put("community",community);
                    FileTool.Dump(deal_obj.toString(),"D:\\小论文\\dealdata\\AiWuJiWu\\AllDeals.txt","utf-8");
                }
            }


        }
    }

    //step2:把数据导入数据库，进行排重
    public static void importToMongo(String file){
        DBCollection coll = db.getDB().getCollection("Deals_aiwujiwu");
        BasicDBObject document;
        int bedroomSum;
        String contract;
        String floorStr;
        String floor;
        int houseType;
        int layers;
        int livingRoomSum;
        double price;
        double unitPrice=0;
        double spaceArea;
        String community;

        int documentcount=0;

        Vector<String> pois=FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);

            document = new BasicDBObject();

            if(obj.containsKey("bedroomSum")){
                bedroomSum=obj.getInt("bedroomSum");
                document.put("bedroomSum",bedroomSum);
            }

            if(obj.containsKey("contract")){
                contract=obj.getString("contract");
                document.put("contract",contract);

                String[] time=contract.split("-");
                String year=time[0];
                String month=time[1];
                String day=time[2];

                if(month.startsWith("0")){
                    month=month.substring(1);
                }
                if(day.startsWith("0")){
                    day=day.substring(1);
                }
                document.put("year",Integer.parseInt(year));
                document.put("month",Integer.parseInt(month));
                document.put("day",Integer.parseInt(day));
            }

            if(obj.containsKey("floorStr")){
                floorStr=obj.getString("floorStr");
                if(floorStr.indexOf("/")!=-1){
                    floor=floorStr.substring(0,floorStr.indexOf("/"));
                    document.put("floor",floor);
                }

            }

            if(obj.containsKey("houseType")){
                houseType=obj.getInt("houseType");
                document.put("houseType",houseType);
            }

            if(obj.containsKey("layers")){
                layers=obj.getInt("layers");
                document.put("layers",layers);
            }

            if(obj.containsKey("livingRoomSum")){
                livingRoomSum=obj.getInt("livingRoomSum");
                document.put("livingRoomSum",livingRoomSum);
            }

            if(obj.containsKey("price")){
                price=obj.getDouble("price");
                document.put("price",price);
            }

            if(obj.containsKey("spaceArea")){
                spaceArea=obj.getDouble("spaceArea");
                document.put("spaceArea",spaceArea);
            }

            if(obj.containsKey("unitPrice")){
                String str=obj.getString("unitPrice");
                if(str.indexOf("万/平")!=-1){
                    str=str.replace("万/平","");
                    unitPrice=Double.parseDouble(str);
                }else if(str.indexOf("元/平")!=-1){
                    str=str.replace("元/平","");
                    unitPrice=Double.parseDouble(str)*0.0001;
                }

                document.put("unitPrice",unitPrice);
            }

            if(obj.containsKey("community")){
                community=obj.getString("community");
                document.put("community",community);
            }

            DBCursor rls =coll.find(document);
            if(rls == null || rls.size() == 0){
                documentcount++;
                coll.insert(document);
            }else{
                System.out.println("该数据已经存在!");
            }

        }
        System.out.println("共导入"+documentcount+"条数据");
    }
}
