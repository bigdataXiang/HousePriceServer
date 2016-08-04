package utils;

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
