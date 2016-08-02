package com.reprocess;

import com.mongodb.BasicDBObject;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/8/2.
 */
public class DataCheck {
    public static String path="";
    /*E:\房地产可视化\近一年数据分类汇总\fang\resold\json\tidy\fang_resold_tidy\*/

    public static void main(String[] args) throws IOException {
        Vector<String> filenames= FileTool.Load(path+"filename.txt","UTF-8");
        for(int i=0;i<filenames.size();i++){
            String filename=filenames.elementAt(i);

            System.out.println("开始检查第"+i+"个文件");
            Vector<String> rds = FileTool.Load(path+filename,"UTF-8");
            int n = 0;
            try{
                for (n = 0; n < rds.size(); n ++) {
                        String element="";

                        element=rds.elementAt(n);
                        JSONObject element_obj=JSONObject.fromObject(element);
                        double price=0;
                        double area=0;
                        double unit_price=0;

                        if(element_obj.containsKey("price")){
                            if(!element_obj.get("price").equals("null")){
                                price=Double.parseDouble(element_obj.get("price").toString().replace("万", "").replace("元", "").replace("/", "").replace("月", ""));
                            }
                        }
                        if(element_obj.containsKey("area")){
                            if(!element_obj.get("area").equals("null")){

                                if(element_obj.get("area").toString().length()!=0){
                                    area=Double.parseDouble(element_obj.get("area").toString());
                                }

                            }
                        }
                        if(element_obj.containsKey("unit_price")){
                            if(area!=0){
                                unit_price=price/area;
                            }
                            element_obj.put("unit_price",unit_price);
                        }
                    FileTool.Dump(element_obj.toString(),path+"\\checked\\"+filename,"utf-8");
                 }
            }catch(NumberFormatException e){
                e.printStackTrace();
                System.out.println("NumberFormatException:"+n);
            }catch(ClassCastException e){
                System.out.println("ClassCastException:"+i);
            }
        }
    }
}
