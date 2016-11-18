package utils;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.util.*;

/**
 * Created by ZhouXiang on 2016/7/2.
 */
public class UtilFile {
    public static String path="E:\\房地产可视化\\近一年数据分类汇总\\anjuke\\rentout\\json\\tidy\\";
    public static void main(String[] args) throws IOException {
        UtilFile.filecut(path+"anjuke_rentout_2015_1015_3.txt");
    }

    public static class TimeComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            JSONObject p1 = (JSONObject) object1; // 强制转换
            //System.out.println(p1);
            JSONObject p2 = (JSONObject) object2;

            String date1=(String)p1.get("date");
            String date2=(String)p2.get("date");

            String[] dates=date1.split("-");

            int year1=Integer.parseInt(dates[0]);
            int month1=Integer.parseInt(dates[1]);
            int day1=1;
            if(dates.length==2){
                day1=1;
            }else if(dates.length==3){
                day1=Integer.parseInt(dates[2]);
            }
            GregorianCalendar calendar1=new GregorianCalendar(year1,month1,day1);
            // System.out.println(calendar1);

            dates=date2.split("-");
            int year2=Integer.parseInt(dates[0]);
            int month2=Integer.parseInt(dates[1]);
            int day2=1;
            if(dates.length==2){
                day2=1;
            }else if(dates.length==3){
                day2=Integer.parseInt(dates[2]);
            }
            GregorianCalendar calendar2=new GregorianCalendar(year2,month2,day2);

            return calendar1.compareTo(calendar2);

        }
    }
    public static class CodeComparator implements Comparator {
        public int compare(Object object1, Object object2) {

            JSONObject obj1=JSONObject.fromObject(object1);
            JSONObject obj2=JSONObject.fromObject(object2);

            int code1=obj1.getInt("code");
            int code2=obj2.getInt("code");

            int flag = new Integer(code1).compareTo(new Integer(code2));
            return flag;
        }
    }
    public static class Average_PriceComparator implements Comparator {
        public int compare(Object object1, Object object2) {

            JSONObject obj1=JSONObject.fromObject(object1);
            JSONObject obj2=JSONObject.fromObject(object2);

            double price1=obj1.getDouble("average_price");
            double price2=obj2.getDouble("average_price");

            int flag = new Double(price1).compareTo(new Double(price2));
            return flag;
        }
    }
    public static class RComparator implements Comparator {
        public int compare(Object object1, Object object2) {

            JSONObject obj1=JSONObject.fromObject(object1);
            JSONObject obj2=JSONObject.fromObject(object2);

            double price1=obj1.getDouble("r");
            double price2=obj2.getDouble("r");

            int flag = new Double(price1).compareTo(new Double(price2));
            return flag;
        }
    }



    public static void printArray(int[] array){
        for(int i=0;i<array.length;i++){
           System.out.println(array[i]);
        }
    }
    public static void printArray_JSON(List<JSONObject> array){
        for(int i=0;i<array.size();i++){
            JSONObject document= (JSONObject) array.get(i);
            System.out.println(array.get(i));
            //FileTool.Dump(array.get(i).toString(),"E:\\房地产可视化\\to100\\test.txt","utf-8");
        }
    }
    public static void printArray_BasicDB(List<BasicDBObject> array){
        for(int i=0;i<array.size();i++){
            BasicDBObject document= (BasicDBObject) array.get(i);
            System.out.println(array.get(i));
            FileTool.Dump(array.get(i).toString(),"E:\\房地产可视化\\to100\\第二种.txt","utf-8");
        }
    }


    public static List JSONArrayToList(JSONArray array){

        List<JSONObject> list = new ArrayList<JSONObject>();

        JSONObject obj;
        for(int i=0;i<array.size();i++){
            obj=(JSONObject)array.get(i);
            list.add(obj);
        }
        return list;
    }
    public static void filecut(String files){

        try {
            File file = new File(files);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader reader = null;
            String tempString = null;
            int temp = 0;
            reader = new BufferedReader(isr);

            while ((tempString = reader.readLine()) != null && temp < 86308) {

                FileTool.Dump(tempString,files.replace(".txt","")+"_1.txt","utf-8");
                temp++;
            }
            System.out.println("文件1划分完毕！");
            while ((tempString = reader.readLine()) != null && ((temp >= 86308) && (temp < 172616))) {

                FileTool.Dump(tempString,files.replace(".txt","")+"_2.txt","utf-8");
                temp++;
            }
            System.out.println("文件2划分完毕！");
            while ((tempString = reader.readLine()) != null && (temp >= 172616)) {

                FileTool.Dump(tempString,files.replace(".txt","")+"_3.txt","utf-8");
                temp++;
            }
            System.out.println("文件3划分完毕！");

        }catch (NullPointerException e1) {
            e1.printStackTrace();
            e1.getMessage();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static String delect_content_inBrackets(String item,String left,String right){
        String result="";
        if (item != null)
        {
            int ss = item.indexOf(left);
            while (ss != -1)
            {
                int ee = item.indexOf(right, ss + 1);
                if (ee != -1)
                {
                    String sub = item.substring(ss, ee + 1);
                    item = item.replace(sub, "");
                }
                else
                    break;
                ss = item.indexOf(left, ss);
            }
        }

        result=item;
        return result;
    }
    public static String getContent(File file){
        String content="";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while((line = br.readLine()) != null){
                content+=line+"\r\n";
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return content;
    }

}
