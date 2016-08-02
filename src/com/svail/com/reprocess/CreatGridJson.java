package com.svail.com.reprocess;

import com.svail.handler.handler_3000;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.*;

/**
 * Created by ZhouXiang on 2016/8/2.
 */
public class CreatGridJson {
    public static void main(String[] args){

        //1.选择具体某一个月的房价数据,获得每条数据的code和grid
        getCodePrice("D:\\房地产可视化\\woaiwojia_resold_2016_0428_code.txt");

        //2.得到每个网格的均价
        JSONArray result = getAvenragePrice(result_array);

        //3.给有房价值的每个网格赋颜色值
        String data=setColor(result);

        //4.给无房价值的每个网格统一赋值灰色，并将所有的网格code进行排序
        String resultdata=FilledGridData(data);

        //5.将处理好的值存于本地
        FileTool.Dump(resultdata,"D:\\房地产可视化\\gridecode_3000_result.txt","utf-8");
        System.out.println("ok!");

    }
    public static List<JSONObject> result_array=new ArrayList<JSONObject>();

    /**
     * 第一步：
     * result_array中装了每条poi的code和price
     * @param file
     */
    public static void getCodePrice(String file){

        JSONObject obj;
        JSONObject result;
        double unit_price;
        int code ;
        int i=0;

        Vector<String> pois=FileTool.Load(file,"utf-8");
        try{
            for(i=2199;i<pois.size();i++){
                String poi=pois.elementAt(i);
                obj=JSONObject.fromObject(poi);
                result=new JSONObject();

                code = obj.getInt("gridcode");
                result.put("code",code);


                if(obj.containsKey("unit_price")){
                    String temp=obj.getString("unit_price").replace("元","");
                    if(temp.length()!=0){
                        unit_price=Double.parseDouble(temp);
                    }else {
                        unit_price=0;
                    }

                }else{
                    unit_price=0;
                }
                result.put("price",unit_price);
                result_array.add(result);
            }

        }catch(JSONException e){
             System.out.println(i);
        }


    }

    /**
     * 第二步：
     * 求每个网格的房价平均值
     * @param array
     * @return
     */
    public static JSONArray getAvenragePrice(List<JSONObject> array){

        JSONObject codeprice=new JSONObject();
        JSONObject poi;
        System.out.println("开始求每个网格的价格平均值：");
        for (int i=0;i<array.size();i++){
            poi= (JSONObject) array.get(i);
            String code=poi.getString("code");

            if(codeprice.containsKey(code)){
                List<Double> pricelist= (List<Double>) codeprice.get(code);
                double price=poi.getDouble("price");
                pricelist.add(price);
                codeprice.put(code,pricelist);

            }else{
                List<Double> pricelist=new ArrayList<Double>();
                double price=poi.getDouble("price");
                pricelist.add(price);
                codeprice.put(code,pricelist);
            }
        }
        //计算实际有数据的网格的个数
        System.out.println("共有"+codeprice.size()+"个网格有数据");

        //遍历codeprice中的元素，求每个网格的价格均值
        JSONArray finalresult=new JSONArray();
        Iterator codekeys = codeprice.keys();
        List<Double> pricelist;
        while(codekeys.hasNext()){

            JSONObject code_averagePrice=new JSONObject();
            String code=(String)codekeys.next();
            code_averagePrice.put("code",code);

            pricelist=(List<Double>) codeprice.get(code);
            double totalprice=0;
            double average_price=0;
            if(pricelist.size()!=0){
                int count=0;//统计pricelist中均价不为0的数目
                for(int i=0;i<pricelist.size();i++){
                    double price=pricelist.get(i);
                    if(price!=0){
                        totalprice+=price;
                        count++;
                    }

                }
                average_price=totalprice/count;
            }
            code_averagePrice.put("average_price",average_price);

            finalresult.add(code_averagePrice);
        }
        return finalresult;
    }

    /**
     * 第三步：
     * 给有房价值的每个网格的均价赋予一个颜色值
     * @param array
     */
    public static String  setColor(JSONArray array){
        JSONObject backdata=new JSONObject();
        JSONArray finalresult=new JSONArray();

        System.out.println("开始计算每个网格的颜色：");
        for (int i=0;i<array.size();i++){
            JSONObject obj= (JSONObject) array.get(i);
            //System.out.print(obj);
            double price=obj.getDouble("average_price");
            String color="";

            color=setColorRegion(price);
            obj.put("color",color);

            finalresult.add(obj);
        }
        backdata.put("data",finalresult);
        return backdata.toString();
    }

    /**
     * 第四步：
     * 填充房价值为空的网格的颜色，并对所有网格进行排序
     * @param data
     * @return
     */
    public static String FilledGridData(String data){
        System.out.println("开始填充值为空的网格的颜色：");
        String str="";
        JSONObject data_obj=JSONObject.fromObject(data);
        JSONArray data_array=data_obj.getJSONArray("data");
        JSONArray result_obj=new JSONArray();

        List<JSONObject> list = new ArrayList<JSONObject>(); //对时间进行排序的list
        JSONObject codekey=new JSONObject();
        for(int i=0;i<data_array.size();i++){
            JSONObject obj= (JSONObject) data_array.get(i);
            list.add(obj);

            String code=obj.getString("code");
            codekey.put(code,"");
        }

        for(int i=1;i<3601;i++){
            String codeindex=""+i;
            if(!codekey.containsKey(codeindex)){
                JSONObject obj= new JSONObject();
                obj.put("code",codeindex);
                obj.put("average_price",0);
                obj.put("color","#BFBFBF");
                list.add(obj);
            }
        }

        //System.out.println("开始排序：");
        Collections.sort(list, new handler_3000.CodeComparator()); // 根据网格code排序

        JSONArray resultarray=new JSONArray();
        Iterator it=list.iterator();
        while(it.hasNext()){
            JSONObject poi= (JSONObject) it.next();
            resultarray.add(poi);
            //FileTool.Dump(poi.toString(),"D:/gridcolor.txt","utf-8");
        }
        JSONObject resultobj=new JSONObject();
        resultobj.put("data",resultarray);
        str=resultobj.toString();

        return str;
    }




    /**
     * 比较两个poi中对应的两个code的大小，并对这两个poi进行排序
     */
    static class CodeComparator implements Comparator {
        public int compare(Object object1, Object object2) {

            JSONObject obj1=JSONObject.fromObject(object1);
            JSONObject obj2=JSONObject.fromObject(object2);

            int code1=obj1.getInt("code");
            int code2=obj2.getInt("code");

            int flag = new Integer(code1).compareTo(new Integer(code2));
            return flag;
        }
    }

    public static String setColorRegion(double price){
        String color="";

        if(price>10){
            color="#FF0000";
        }else if(price>9&&price<=10){
            color="#FF0D0D";
        }else if(price>8&&price<=9){
            color="#FF1919";
        }else if(price>7&&price<=8){
            color="#FF2626";
        }else if(price>6&&price<=7){
            color="#FF3333";
        }else if(price>5&&price<=6){
            color="#FF4040";
        }else if(price>4&&price<=5){
            color="#FF4D4D";
        }else if(price>3&&price<=4){
            color="#FF6666";
        }else if(price>2&&price<=3){
            color="#FF8080";
        }else if(price>1&&price<=2){
            color="#FF9999";
        }else{
            color="#FFB3B3";
        }
        return color;
    }
}
