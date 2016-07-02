package utils;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;

public class UtilHttp{
    public static String getBody(HttpExchange exchange){
        InputStream inputStream=exchange.getRequestBody();//除了get以外的方法时候,将信息放入body中
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb=new StringBuilder();
        String line="";
        try{
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
        }catch(IOException e){
            e.getMessage();
        }
        return sb.toString();
    }

    public static void setResponse(HttpExchange exchange,int Code,String content) throws IOException {

//        Headers responseHeaders = exchange.getResponseHeaders();
//        responseHeaders.set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(Code, 0);
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(content.getBytes());
        responseBody.close();
    }
}