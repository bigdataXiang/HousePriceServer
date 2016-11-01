package com.transaction_price;

import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/11/1.
 */
public class LianJia {
    public static void main(String[] args){
        processData("D:\\test\\lianjia\\有问题的数据.txt");
    }
    public static void processData(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String p=pois.elementAt(i);


                String[] poi=p.split(";");

                /*for(int ii=0;ii<poi.length;ii++){
                    System.out.println(poi[ii]);
                }*/
                JSONObject obj=new JSONObject();
                int j;
                //System.out.println(pois.elementAt(i));
            try{
                for(j=0;j<poi.length;j++){
                    if(j==0){
                        String[] str=poi[j].split(" ");
                        obj.put("community",str[0]);
                        obj.put("house_type",str[1]);
                        obj.put("area",str[2].replace("平米",""));
                        continue;
                    }else if(j==1){
                        //System.out.println(poi[j]);
                        String[] str=poi[j].replace(" ","").split("\\|");
                        /*for(int ss=0;ss<str.length;ss++){
                            System.out.println(str[ss]);
                        }*/

                        obj.put("direction",str[0]);
                        obj.put("fitment",str[1]);
                        obj.put("elevator",str[2]);
                        continue;
                    }else if(j==2){
                        obj.put("time",poi[j]);
                        continue;
                    }else if(j==3){
                        obj.put("price",poi[j]);
                        continue;
                    }else if(j==5){
                        String[] str=poi[j].split(" ");
                        if(str.length>1){
                            obj.put("floor",str[0]);
                            obj.put("built_year",str[1]);
                        }else if(str.length==1){
                            obj.put("floor",str[0]);
                        }

                        continue;
                    }else if(j==6){
                        obj.put("source",poi[j]);
                        continue;
                    }else if(j==7){
                        obj.put("unit_prcie",poi[j]);
                        continue;
                    }else if(j==9){
                        obj.put("traffic",poi[j]);
                        continue;
                    }
                }
                //System.out.println(i+":"+obj);
                FileTool.Dump(obj.toString(),"D:\\test\\lianjia\\处理后的成交数据_2.txt","utf-8");

            }catch (ArrayIndexOutOfBoundsException e){
                //FileTool.Dump(p,"D:\\test\\lianjia\\有问题的数据.txt","utf-8");
                System.out.println(e.getMessage());
            }

        }
    }
}
