package com.reprocess.grid_50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.reprocess.grid_100.Code_Price_RowCol;
import com.reprocess.grid_100.util.Color;
import com.reprocess.grid_100.util.RowColCalculation;
import com.reprocess.grid_100.util.SetCondition;
import com.svail.bean.Response;
import com.svail.db.db;
import net.sf.json.JSONObject;
import utils.UtilFile;

import java.util.*;

import static com.reprocess.grid_100.GridMerge.codeMapping100toN00;

/**
 * Created by ZhouXiang on 2016/8/9.
 */
public class CallInterestGrid {
    public static void main(String[] args){
        //{"west":115.73135375976562,"east":117.04971313476562,"south":39.665442308708904,"north":40.14791364536759,"zoom":11,"gridTime":"2015年10月","source":"我爱我家"}
        double width=5.892999999998593E-4;//每50m的经度差
        double length=4.501999999998674E-4;//每50m的纬度差

        int colmin=(int)Math.ceil((116.29062652587892-115.417284)/width);
        int colmax=(int)Math.ceil((116.4554214477539-115.417284)/width);
        int rowmin=(int)Math.ceil((39.90657598772841-39.438283)/length);
        int rowmax=(int)Math.ceil((39.965477436645436-39.438283)/length);

        JSONObject condition=new JSONObject();
        condition.put("N",1);
        condition.put("rowmax",rowmax);
        condition.put("rowmin",rowmin);
        condition.put("colmax",colmax);
        condition.put("colmin",colmin);
        condition.put("year",2015);
        condition.put("month",10);
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_50_Interpolation");

        CallMongo(condition);
        //getLngLat(53,75,10);

    }
    public Response get(String body){

        JSONObject condition= SetCondition.setCallInterestGrid_50(body);
        String resultdata=CallMongo(condition);

        Response r= new Response();
        r.setCode(200);
        r.setContent(resultdata);
        return r;

    }

    public static String CallMongo(JSONObject condition){
        int N=condition.getInt("N");

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
        int rowmin=condition.getInt("rowmin");
        int rowmax=condition.getInt("rowmax");
        int colmin=condition.getInt("colmin");
        int colmax=condition.getInt("colmax");

        //将小网格合并成大网格
        int r_min=(int) Math.ceil((double)rowmin/N);
        int r_max=(int) Math.ceil((double)rowmax/N);
        int c_min=(int) Math.ceil((double)colmin/N);
        int c_max=(int) Math.ceil((double)colmax/N);

        //根据大网格调用需要的小网格
        rowmin=(r_min-1)*N+1;
        rowmax=r_max*N;
        colmin=(c_min-1)*N+1;
        colmax=c_max*N;

        cond.put("$gte",rowmin);
        cond.put("$lte",rowmax);
        document.put("row",cond);

        cond=new BasicDBObject();
        cond.put("$gte",colmin);
        cond.put("$lte",colmax);
        document.put("col",cond);

        //System.out.println(document);
        code_array=coll.find(document).toArray();

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

            result_doc=RowColCalculation.codeMapping50toN50(row_doc,col_doc,N);
            row=result_doc[0];
            doc.put("row",row);
            col=result_doc[1];
            doc.put("col",col);
            code=result_doc[2];
            doc.put("code",code);
            code_array_after.add(doc);

            average_price=doc.getDouble("price");
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


        Iterator it=map.keySet().iterator();
        String color;
        List< JSONObject> jsonArray=new ArrayList<>();
        JSONObject jsonObject;
        JSONObject codekey=new JSONObject();
        if(it.hasNext()){
            while (it.hasNext()){
                double totalprice=0;
                code=(int)it.next();
                cpr=map.get(code);
                pricelist=cpr.getPricelist();
                //System.out.println(pricelist);
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
                color = Color.setColorRegion(average_price);//使用中秋节设计的配色方案

                jsonObject=new JSONObject();
                jsonObject.put("code",code);
                jsonObject.put("average_price",average_price);
                jsonObject.put("color",color);
                jsonObject.put("row",row);
                jsonObject.put("col",col);
                jsonObject.put("corners", RowColCalculation.getLngLat(row,col,N));
                jsonArray.add(jsonObject);

               // System.out.println(jsonObject);
                codekey.put(""+code,"");
            }
        }


        //将小网格合并成大网格
        JSONObject nullobj;

        for(int i=r_min;i<=r_max;i++) {
            for (int j =c_min; j<=c_max; j++) {
                String codeindex=""+(j + (2000/N) * (i - 1));
                if(!codekey.containsKey(codeindex)){
                    nullobj=new JSONObject();
                    nullobj.put("code",Integer.parseInt(codeindex));
                    nullobj.put("average_price",0);
                    nullobj.put("row",i);
                    nullobj.put("col",j);
                    nullobj.put("color","");
                    nullobj.put("corners","");
                    jsonArray.add(nullobj);
                }

            }
        }

        Collections.sort(jsonArray, new UtilFile.CodeComparator()); // 根据网格code排序
        JSONObject object= new JSONObject();
        object.put("r_min",r_min);
        object.put("r_max",r_max);
        object.put("c_min",c_min);
        object.put("c_max",c_max);
        object.put("N",N);
        object.put("data",jsonArray);

        System.out.println(object.toString());

        return object.toString();
    }

}
