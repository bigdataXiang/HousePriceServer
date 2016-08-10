package com.reprocess.grid_100;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.bean.Response;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

import static com.reprocess.grid_100.GridMerge.codeMapping100toN00;
import static com.reprocess.grid_100.ResoldGridClassify.setColorRegion;
import static utils.UtilFile.printArray;

/**
 * Created by ZhouXiang on 2016/8/9.
 */
public class CallInterestGrid {
    public static void main(String[] args){
        JSONObject condition=new JSONObject();
        condition.put("rowmax",520);
        condition.put("rowmin",499);
        condition.put("colmax",840);
        condition.put("colmin",830);
        condition.put("year",2015);
        condition.put("month",10);
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_100");

        CallMongo(condition);
    }
    public Response get(String path, String body){
        JSONObject obj=JSONObject.fromObject(body);

        double west=obj.getDouble("west");
        double east=obj.getDouble("east");
        double south=obj.getDouble("south");
        double north=obj.getDouble("north");

        double width=0.023571999999994375;//每两千米的经度差
        double length=0.018007999999994695;//每两千米的纬度差

        int colmin=(int) Math.ceil((west-115.417284)/width);
        int colmax=(int)Math.ceil((east-115.417284)/width);
        int rowmin=(int)Math.ceil((south-39.438283)/length);
        int rowmax=(int)Math.ceil((north-39.438283)/length);

        JSONObject condition=new JSONObject();
        condition.put("rowmax",rowmax);
        condition.put("rowmin",rowmin);
        condition.put("colmax",colmax);
        condition.put("colmin",colmin);
        condition.put("year",2015);
        condition.put("month",10);
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_100");

        String resultdata=CallMongo(condition);

        Response r= new Response();
        r.setCode(200);
        r.setContent(resultdata);
        return r;

    }
    public static String CallMongo(JSONObject condition){

        String collName=condition.getString("export_collName");
        DBCollection coll = db.getDB().getCollection(collName);
        BasicDBObject document = new BasicDBObject();

        int year=condition.getInt("year");
        int month=condition.getInt("month");
        String source=condition.getString("source");

        document.put("year",year);
        document.put("month",month);
        document.put("source",source);
        List code_array=new ArrayList<>();
        BasicDBObject cond=new BasicDBObject();
        cond.put("$gte",condition.getInt("rowmin"));
        cond.put("$lte",condition.getInt("rowmax"));
        document.put("row",cond);

        cond=new BasicDBObject();
        cond.put("$gte",condition.getInt("colmin"));
        cond.put("$lte",condition.getInt("colmax"));
        document.put("col",cond);

        System.out.println(document);
        code_array=coll.find(document).toArray();
        int size=code_array.size();
        System.out.println(size);
        printArray(code_array);

        BasicDBObject doc;
        int row_doc;
        int col_doc;
        int[] result_doc;
        List code_array_after=new ArrayList<>();

        Map<Integer,Code_Price_RowCol> map= new HashMap<>();
        Code_Price_RowCol cpr;
        List<Double> pricelist;
        double average_price;
        int code;
        int row;
        int col;
        for(int i=0;i<code_array.size();i++){

            doc= (BasicDBObject) code_array.get(i);
            doc.remove("_id");
            row_doc=doc.getInt("row");
            col_doc=doc.getInt("col");

            result_doc=codeMapping100toN00(row_doc,col_doc,5);
            row=result_doc[0];
            doc.put("row",row);
            col=result_doc[1];
            doc.put("col",col);
            code=result_doc[2];
            doc.put("code",code);
            code_array_after.add(doc);

            average_price=doc.getDouble("average_price");
            if (map.containsKey(code)) {

                cpr = map.get(code);
                pricelist=cpr.getPricelist();
                pricelist.add(average_price);
                cpr.setPricelist(pricelist);
                map.put(code,cpr);
            }else{
                pricelist = new ArrayList<Double>();
                pricelist.add(average_price);

                cpr=new Code_Price_RowCol();
                cpr.setCode(""+code);
                cpr.setCol(col);
                cpr.setRow(row);
                cpr.setPricelist(pricelist);
                map.put(code,cpr);
            }
        }



        System.out.println(code_array_after.size());
        printArray(code_array_after);

        Iterator it=map.keySet().iterator();
        String color;
        JSONArray jsonArray=new JSONArray();
        JSONObject jsonObject;
        if(it.hasNext()){
            while (it.hasNext()){
                double totalprice=0;
                code=(int)it.next();
                cpr=map.get(code);
                pricelist=cpr.getPricelist();
                System.out.println(pricelist);
                if (pricelist.size() != 0) {
                    int count = 0;//统计pricelist中均价不为0的数目
                    for (int i = 0; i < pricelist.size(); i++) {
                        double price = pricelist.get(i);
                        if (price != 0) {
                            totalprice += price;
                            count++;
                        }

                    }
                    if(count!=0){
                        average_price = totalprice / count;
                    }else{
                        average_price=0;
                    }
                }else{
                    average_price=0;
                }
                row=cpr.getRow();
                col=cpr.getCol();
                color=setColorRegion(average_price);

                jsonObject=new JSONObject();
                jsonObject.put("code",code);
                jsonObject.put("average_price",average_price);
                jsonObject.put("color",color);
                jsonObject.put("row",row);
                jsonObject.put("col",col);
                jsonArray.add(jsonObject);

                System.out.println(jsonObject);
            }
        }

        JSONObject object= new JSONObject();
        object.put("data",jsonArray);
        System.out.println(object);

        return object.toString();
    }
}
