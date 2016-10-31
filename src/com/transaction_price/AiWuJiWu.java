package com.transaction_price;

import net.sf.json.JSONObject;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import utils.FileTool;
import utils.Tool;

import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/10/31.
 */
public class AiWuJiWu {
    public static String[] regions_aiwujiwu={
                            "id12440","id12441","id12442","g1id12443","id12444","id12446","id12447","id12448",
                            "id12449","id12450"
                           };
    public static void main(String[] args){
        for(int i=0;i<regions_aiwujiwu.length;i++){
            getResoldApartmentInfo_aiwujiwu(regions_aiwujiwu[i]);
            String str="已经抓取完"+i+":"+regions_aiwujiwu[i];
            FileTool.Dump(str,"D:\\test\\aiwujiwu\\finishlog_id.txt","utf-8");
        }

    }
    public static void getResoldApartmentInfo_aiwujiwu(String region){

        for(int page=1;page<=100;page++){

            String url = "https://www.iwjw.com/sale/beijing/g1" + region+"p"+page+"/";
            Vector<String> urls = new Vector<String>();
            Set<String> visited = new TreeSet<String>();
            urls.add(url);

            Parser parser = new Parser();

            while (urls.size() > 0) {
                // 解析页面
                url = urls.get(0);

                urls.remove(0);
                visited.add(url);

                //String content = HTMLTool.fetchURL(url, "gb2312", "get");//gb2312
                String content = Tool.fetchURL(url);
                //System.out.println(content);

                if (content == null) {
                    continue;
                }
                try {
                    parser.setInputHTML(content);
                    parser.setEncoding("utf-8");

                    NodeFilter filter = new TagNameFilter("a");
                    NodeList nodes = parser.extractAllNodesThatMatch(filter);

                    int urlnodes_size=nodes.size();
                    System.out.println(urlnodes_size);
                    if(urlnodes_size!=0){
                        for (int n = 0; n < nodes.size(); n ++)
                        {
                            TagNode tn=(TagNode)nodes.elementAt(n);
                            String purl = tn.getAttribute("href");
                            if(purl!=null){
                                if(purl.startsWith("/estate/sale/")){
                                    System.out.println(purl);
                                    JSONObject obj=new JSONObject();
                                    String estateId=purl.substring(purl.indexOf("/estate/sale/")+"/estate/sale/".length());
                                    obj.put("estateId",estateId);
                                    String str=tn.toPlainTextString().replace("\r\n","").replace("\n","").replace("\b","").replace("\t","").trim();
                                    obj.put("name",str);
                                    FileTool.Dump(obj.toString(),"D:\\test\\aiwujiwu\\小区id.txt","utf-8");
                                }
                            }else {
                                continue;
                            }



                        }
                    }
                    try {
                        Thread.sleep(500 * ((int) (Math
                                .max(1, Math.random() * 3))));
                    } catch (final InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                }catch (ParserException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }catch(NullPointerException e){
                    System.out.println(e.getMessage());
                }

            }
        }

    }
}
