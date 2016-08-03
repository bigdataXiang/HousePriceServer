package com.reprocess;

import com.mongodb.BasicDBObject;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/8/2.
 */
public class DataCheck {
    public static String path="E:\\房地产可视化\\近一年数据分类汇总\\fang\\rentout\\json\\fang_rentout_tidy\\checked\\";
    /*E:\房地产可视化\近一年数据分类汇总\fang\resold\json\tidy\fang_resold_tidy\*/

    public static void main(String[] args) throws IOException {
        checkArea(path+"fang_rentout_2016_0520.txt_false.txt_ok.txt");
    }
    public static void checkArea(String file){
        Vector<String> filenames= FileTool.Load(file,"UTF-8");
        JSONObject element_obj=new JSONObject();
        int i=0;
        for(i=0;i<filenames.size();i++){
            try {
                String element = filenames.elementAt(i);
                element_obj = JSONObject.fromObject(element);
                String floor = element_obj.getString("direction").replace("�","");
                element_obj.put("floor", floor);
                element_obj.remove("direction");
            FileTool.Dump(element_obj.toString(), file + "_ok.txt", "utf-8");
            }catch (JSONException e){
                System.out.println(i);
                e.printStackTrace();
            }

        }
    }
    public static void checkUnitPrice(){
        Vector<String> filenames= FileTool.Load(path+"filename.txt","UTF-8");
        for(int i=0;i<filenames.size();i++){
            String filename=filenames.elementAt(i);

            System.out.println("开始检查第"+i+"个文件");
            Vector<String> rds = FileTool.Load(path+filename,"UTF-8");
            int n = 0;
            JSONObject element_obj=new JSONObject();
            for (n = 0; n < rds.size(); n ++) {
                try {
                    String element = "";

                    element = rds.elementAt(n);
                    element_obj = JSONObject.fromObject(element);
                    double price = 0;
                    double area = 0;
                    double unit_price = 0;

                    if (element_obj.containsKey("price")) {
                        if (!element_obj.get("price").equals("null")) {
                            price = Double.parseDouble(element_obj.get("price").toString().replace("万", "").replace("元", "").replace("/", "").replace("月", ""));
                        }
                    }
                    if (element_obj.containsKey("area")) {
                        if (!element_obj.get("area").equals("null")) {

                            if (element_obj.get("area").toString().length() != 0) {
                                area = Double.parseDouble(element_obj.get("area").toString());
                            }

                        }
                    }
                    if (element_obj.containsKey("unit_price")) {
                        if (area != 0) {
                            unit_price = price / area;
                        }
                        element_obj.put("unit_price", unit_price);
                    }
                    FileTool.Dump(element_obj.toString(), path + "\\checked\\" + filename, "utf-8");
                }catch(NumberFormatException e){
                    e.printStackTrace();
                    System.out.println("NumberFormatException:"+n);
                    FileTool.Dump(element_obj.toString(), path + "\\checked\\" + filename+"_false.txt", "utf-8");
                }catch(ClassCastException e){
                    e.printStackTrace();
                    System.out.println("ClassCastException:"+i);
                }
            }
        }
    }
}
