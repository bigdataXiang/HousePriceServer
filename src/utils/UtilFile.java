package utils;

import java.io.*;

/**
 * Created by ZhouXiang on 2016/7/2.
 */
public class UtilFile {
    public static String getContent(File file){
        String content="";
        try {
            FileReader fileReader= new FileReader(file);

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
