package com.svail.handler;

        import com.mongodb.BasicDBObject;
        import com.mongodb.DBCollection;
        import com.mongodb.DBCursor;
        import com.svail.bean.Response;
        import com.svail.db.db;
        import net.sf.json.JSONArray;
        import net.sf.json.JSONException;
        import net.sf.json.JSONObject;
        import utils.ColorUtils;
        import utils.FileTool;

        import java.util.*;

public class handler_resold_3000 {
    public static void main(String[] args){
       /* JSONObject date=new JSONObject();
        date.put("year","2015");
        date.put("month","10");
        getMaxPrice("resold_code_3000","woaiwojia",date);
        JSONArray result = getAvenragePrice(result_array);
        FileTool.Dump(result.toString(),"D:\\房地产可视化\\3000_resold.txt","utf-8");*/


        Vector<String> poi= FileTool.Load("D:\\房地产可视化\\3000_resold.txt","utf-8");
        JSONArray result =JSONArray.fromObject(poi.elementAt(0));
        String data=setColor(result);
        String resultdata=FilledGridData(data);
        FileTool.Dump(resultdata,"D:\\房地产可视化\\gridecode_3000_resold_result.txt","utf-8");
        System.out.println("ok!");

    }
    public Response get(String path, String zoom){


        JSONObject date=new JSONObject();
        date.put("year","2015");
        date.put("month","10");
        getMaxPrice("rentout_code_3000","woaiwojia",date);
        JSONArray result = getAvenragePrice(result_array);
        String data=setColor(result);
        String resultdata=FilledGridData(data);

        Response r= new Response();
        r.setCode(200);
        r.setContent(resultdata);
        return r;

    }
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
        Collections.sort(list, new CodeComparator()); // 根据网格code排序

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
     * 给每个网格的均价赋予一个颜色值
     * @param array
     */
    public static String  setColor(JSONArray array){
        int V=0;
        JSONObject backdata=new JSONObject();
        JSONArray finalresult=new JSONArray();

        System.out.println("开始计算每个网格的颜色：");
        for (int i=0;i<array.size();i++){
            JSONObject obj= (JSONObject) array.get(i);
            //System.out.print(obj);
            double price=obj.getDouble("average_price");
            String color="";

                /*float ratio= (float) ((price/pricemax)*100);
                V = Math.round(ratio);
                int [] rgb=new int[3];
                color= ColorUtils.HSV2StrRGB(0,V,100,rgb);*/

            color=setColorRegion(price);
            obj.put("color",color);

            finalresult.add(obj);
        }
        backdata.put("data",finalresult);
        return backdata.toString();
    }
    //public static JSONArray result_array = new JSONArray();
    public static List<JSONObject> result_array=new ArrayList<JSONObject>();


    public static void getMaxPrice(String collName,String source,JSONObject date){
        String str="";

        DBCollection coll = db.getDB().getCollection(collName);
        BasicDBObject document = new BasicDBObject();
        document.put("source",source);
        String year=date.getString("year");
        String month=date.getString("month");
        document.put("year",year);
        document.put("month",month);

        DBCursor cursor = coll.find(document);
        String poi="";

        List<Double> pricelist=new ArrayList<Double>();
        double price_max=0;
        int count=0;
        if(cursor.hasNext()){
            while (cursor.hasNext()) {
                count++;
                poi=cursor.next().toString();


                JSONObject obj=JSONObject.fromObject(poi);
                //System.out.println(obj);
                JSONObject result=new JSONObject();

                int code = obj.getInt("gridcode");
                result.put("code",code);

                double price = obj.getDouble("unit_price");
                result.put("price",price);

                result_array.add(result);
                System.out.println(count);
            }
            try{
                System.out.println("该月一共有数据"+result_array.size()+"条");
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public static JSONArray getAvenragePrice(List<JSONObject> array){

        JSONObject codeprice=new JSONObject();

        System.out.println("开始求每个网格的价格平均值：");
        for (int i=0;i<array.size();i++){
            //System.out.println(i);
            JSONObject poi= (JSONObject) array.get(i);
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
        System.out.println("该天一共有"+codeprice.size()+"个网格有数据");

        //遍历codeprice中的元素，求每个时间节点的价格均值
        JSONArray finalresult=new JSONArray();
        Iterator codekeys = codeprice.keys();
        while(codekeys.hasNext()){

            JSONObject price=new JSONObject();
            String code=(String)codekeys.next();
            price.put("code",code);

            List<Double> pricelist=(List<Double>) codeprice.get(code);
            double totalprice=0;
            double average_price=0;
            if(pricelist.size()!=0){
                for(int i=0;i<pricelist.size();i++){
                    totalprice+=pricelist.get(i);
                }
                average_price=totalprice/pricelist.size();
            }
            price.put("average_price",average_price);

            finalresult.add(price);
        }
        return finalresult;
    }
    //根据code进行排序
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

    public static void CaculateValuble(String folder){
        Vector<String> pois=FileTool.Load(folder,"utf-8");
        int count=0;
        for(int i=0;i<pois.size();i++){
            JSONObject obj=JSONObject.fromObject(pois.elementAt(i));
            double price =obj.getDouble("average_price");
            if(price!=0){
                count++;
            }
        }
        System.out.println("有效的数据一共有"+count);
    }

    public static String setColorRegion(double price){
        String color="";

        if(price>8){
            color="#FF0000";
        }else if(price>6&&price<=8){
            color="#FF1919";
        }else if(price>4&&price<=2){
            color="#FF3333";
        }else if(price>2&&price<=1){
            color="#FF4D4D";
        }else{
            color="#FF6666";
        }

        return color;
    }
}


