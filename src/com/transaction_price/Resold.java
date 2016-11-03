package com.transaction_price;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import net.sf.json.JSONObject;
import utils.FileTool;
import utils.HTMLTool;
import utils.Tool;

import static com.transaction_price.util.tidyData;

/**
 * Created by ZhouXiang on 2016/10/30.
 */
public class Resold {
    private static String BJ_RESOLDS = "RESOLD";
    public static String LOG = "D:\\test\\woaiwojia\\";
    public static String FOLDER1="D:\\test\\fang\\子区域\\"+"房天下二手房成交数据1102.txt";
    public static String FOLDER2="D:\\test\\fang\\子区域\\"+"房天下二手房成交数据1102_zhoubian.txt";
    public static String regions_5i5j[] = {
            "/anzhen/","/aolinpikegongyuan/","/beishatan/","/beiyuan/","/baiziwan/","/changying/",
            "/cbd/","/chaoqing/","/chaoyangbeilu/","/chaoyanggongyuan/","/chaoyangmen/","/dashanzi/",
            "/dongba/","/dongbalizhuang/","/dingfuzhuang/","/dongdaqiao/","/dawanglu/","/dougezhuang/",
            "/fatou/","/ganluyuan/","/gaobeidian/","/guanzhuang/","/gongti/","/guomao/","/guozhan/",
            "/huajiadi/","/hujialou/","/hongmiao/","/huixinxijie/","/hepingjie/","/huaweiqiao/","/jianxiangqiao/",
            "/jiuxianqiao/","/jingsong/","/jianguomenwai/","/laiguangying/","/liufang/","/madian/","/panjiayuan/",
            "/shaoyaoju/","/shifoying/","/sihui/","/shuangqiao/","/shibalidian/","/sanlitun/","/shilipu/",
            "/sifangqiao/","/taiyanggong/","/tuanjiehu/","/tianshuiyuan/","/wangjing/","/xibahe/","/yaao/",
            "/yayuncun/","/yayuncunxiaoying/","/yansha/","/zuojiazhuang/","/haidian/","/fengtai/","/dongcheng/",
            "/xicheng/","/shijingshan/","/daxing/","/tongzhou/","/shunyi/","/changping/","/beijingzhoubian/",
    };
    public static String RESOLDAPARTMENT_URL_5i5j = "http://bj.5i5j.com/exchange";
    public static String DOMAIN_URL_5i5j = "http://bj.5i5j.com";
    public static String RESOLDAPARTMENT_URL_fang="http://esf.fang.com";


    public static String regions_fang[] = {
            "/chengjiao-a01/", "/chengjiao-a00/", "/chengjiao-a06/", "/chengjiao-a02/", "/chengjiao-a03/", "/chengjiao-a04/",
            "/chengjiao-a05/", "/chengjiao-a07/", "/chengjiao-a012/", "/chengjiao-a0585/", "/chengjiao-a010/", "/chengjiao-a011/",
            "/chengjiao-a08/", "/chengjiao-a013/", "/chengjiao-a09/", "/chengjiao-a014/", "/chengjiao-a015/", "/chengjiao-a016/",
            "/chengjiao-a0987/", "/chengjiao-a011817/",
    };
    public static String regions_lianjia[]={
            "dongcheng","xicheng","chaoyang","haidian","fengtai","shijingshan","tongzhou","changping","daxing","yizhuangkaifaqu","shunyi","fangshan",
            "mentougou","pinggu","huairou","miyun","yanqing","yanjiao"};

    public static void main(String[] args){

        Vector<String> regions=FileTool.Load("D:\\test\\fang\\子区域\\fang.txt","utf-8");

        JSONObject obj=JSONObject.fromObject(regions.elementAt(0));
        System.out.println("一共有"+obj.size()+"个子区域");
        Iterator<String> it=obj.values().iterator();
        String url="";
        int i=0;
        while (it.hasNext()){
            url=it.next();
            getResoldApartmentInfo_fang(url);
            String str="已经抓取完"+i+":"+url;
            FileTool.Dump(str,"D:\\test\\fang\\子区域\\finishlog.txt","utf-8");
            i++;
        }
    }

    //抓取我爱我家二手房成交数据
    static JSONObject jsonObjArr = new JSONObject();
    public static void getResoldApartmentInfo_5i5j(String region) {

        Vector<String> log = null;
        synchronized(BJ_RESOLDS) {
            log = FileTool.Load(LOG + File.separator + region + "_resold.log", "UTF-8");
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");//

        java.util.Date latestdate = null;
        Date newest = null;

        if (log != null) {
            try {
                latestdate = sdf.parse(log.elementAt(0));
                latestdate = new Date(latestdate.getTime() - 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String url = RESOLDAPARTMENT_URL_5i5j + region;
        Vector<String> urls = new Vector<String>();

        Set<String> visited = new TreeSet<String>();
        urls.add(url);

        Parser parser = new Parser();
        boolean quit = false;

        while (urls.size() > 0) {
            url = urls.get(0);

            urls.remove(0);
            visited.add(url);

            String content = HTMLTool.fetchURL(url, "utf-8","get");
            //System.out.println("下一页-->"+url);
            if (content == null) {
                continue;
            }
            try {

                parser.setInputHTML(content);
                parser.setEncoding("utf-8");


                NodeFilter filter=new OrFilter(new AndFilter(new TagNameFilter("h2"),new HasParentFilter(new AndFilter(new TagNameFilter("div"),new HasAttributeFilter("class","list-info")))),new AndFilter(new TagNameFilter("li"),new HasAttributeFilter("class","publish")));

                NodeList nodes = parser.extractAllNodesThatMatch(filter);
                //这样提取的nodes[i=2n]是链接，nodes[i=2n+1]是发布时间
                if (nodes != null && nodes.size()>0) {
                    for (int n = 0; n < nodes.size(); n+=2) {//你n每次加上2

                        TagNode tn=(TagNode)nodes.elementAt(n);

                        String purl=((TagNode)tn.getChildren().elementAt(0)).getAttribute("href");
                        if(purl.startsWith("/exchange")) {
                            purl=DOMAIN_URL_5i5j+purl;
                            String poi2 = parseResold_5i5j(purl);
                            if (poi2 == null)
                                continue;
                            String poi=poi2.replace("&nbsp;", "").replace("&nbsp", "").replace("()", "");
                            System.out.println(poi);

                            if (poi != null) {

                                JSONObject jsonObject = JSONObject.fromObject(poi);
                                String tm=jsonObject.get("time").toString();
                                try {
                                    Date date = sdf.parse(tm);
                                    if (latestdate != null) {
                                        if (date.before(latestdate)) {
                                            quit = true;
                                        } else if (newest == null) {
                                            newest = date;
                                        } else {
                                            if (newest.before(date))
                                                newest = date;
                                        }

                                    }
                                } catch (ParseException e) {

                                    e.printStackTrace();

                                    newest = new Date();
                                }



                                if (quit) {
                                    break;
                                } else {
                                    synchronized(BJ_RESOLDS)

                                    {
                                        poi.replace(" ", "").replace("\r\n","").replace("\n","").replace("\b","").replace("\t","").trim();
                                        if(url.indexOf("/beijingzhoubian/")!=-1)
                                            FileTool.Dump(poi,FOLDER2, "UTF-8");
                                        else
                                            FileTool.Dump(poi,FOLDER1, "UTF-8");
                                        //System.out.println(poi);

                                    }
                                }
                            }

                            try {
                                Thread.sleep(500 * ((int) (Math
                                        .max(1, Math.random() * 3))));
                            } catch (final InterruptedException e1) {
                                e1.printStackTrace();
                            } catch (NullPointerException e1) {

                                e1.printStackTrace();
                            }
                        }
                    }


                    parser.reset();

                    filter=new AndFilter(new TagNameFilter("a"),new HasParentFilter(new AndFilter(new TagNameFilter("div"),new HasAttributeFilter("class","list-page"))));
                    nodes = parser.extractAllNodesThatMatch(filter);

                    if (nodes != null && nodes.size()>0) {
                        TagNode tni = (TagNode) nodes.elementAt(nodes.size()-1);
                        String href = DOMAIN_URL_5i5j+tni.getAttribute("href");
                        if ( href != null&&href.startsWith("http")) {
                            if (!visited.contains(href)) {
                                int kk = 0;
                                for (; kk < urls.size(); kk ++) {
                                    if (urls.elementAt(kk).equalsIgnoreCase( href)) {
                                        break;
                                    }
                                }

                                if (kk == urls.size())
                                    urls.add(href);
                            }
                        }


                    }
                    parser.reset();
                    if (quit)
                        break;
                }
            } catch (ParserException e1) {

                e1.printStackTrace();
            }
        }

        synchronized(BJ_RESOLDS) {
            File f = new File(LOG + File.separator + region + ".log");
            f.delete();
            if (newest != null) {
                FileTool.Dump(sdf.format(newest), LOG + File.separator + region + ".log", "UTF-8");
            }
        }
    }
    public static String parseResold_5i5j(String url) {

        String content = HTMLTool.fetchURL(url, "utf-8","get");//GB2312是汉字书写国家标准。
        System.out.println(content);
        System.out.println(url);
        //jsonObjArr=new JSONObject();
        Parser parser = new Parser();
        if (content == null) {
            return null;
        }

        String poi ="";
        try {

            parser.setInputHTML(content);
            parser.setEncoding("utf-8");


            NodeFilter filter=new AndFilter(new TagNameFilter("h2"),new HasParentFilter(new AndFilter(new TagNameFilter("div"),new HasAttributeFilter("class","house-main"))));
            NodeList nodes= parser.extractAllNodesThatMatch(filter);

        } catch (ParserException e1) {

            e1.printStackTrace();
        }
        if (poi != null) {
            poi = poi.replace("&nbsp;", "").replace("&nbsp", "");
            int ss = poi.indexOf("[");
            while (ss != -1) {
                int ee = poi.indexOf("]", ss + 1);
                if (ee != -1) {
                    String sub = poi.substring(ss, ee + 1);
                    poi = poi.replace(sub, "");
                } else
                    break;
                ss = poi.indexOf("[", ss);
            }
        }
        jsonObjArr.put("url",url);
        poi=jsonObjArr.toString();
        return  poi;
    }

    public static JSONObject obj=new JSONObject();
    //抓取房天下二手房成交数据
    public static void getResoldApartmentInfo_fang(String region){
        int page_count=0;
        Vector<String> log = null;
         synchronized(BJ_RESOLDS) {
            log = FileTool.Load(LOG + File.separator + region + "_resold.log", "UTF-8");
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");

        java.util.Date latestdate = null;
        Date newest = null;

        if (log != null)
        {
            try {
                latestdate = sdf.parse(log.elementAt(0));
                latestdate = new Date(latestdate.getTime() - 1);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        String url = RESOLDAPARTMENT_URL_fang + region;
        //String url ="http://esf.fang.com/chengjiao-a01-b040/i32/";
        Vector<String> urls = new Vector<String>();

        Set<String> visited = new TreeSet<String>();
        urls.add(url);

        Parser parser = new Parser();
        boolean quit = false;

        while (urls.size() > 0)
        {
            int current_page=(++page_count);
            System.out.println("访问该区域的第"+current_page+"页数据");
            // 解析页面
            url = urls.get(0);

            urls.remove(0);
            visited.add(url);

            //String content = HTMLTool.fetchURL(url, "gb2312", "get");//gb2312
            String content = Tool.fetchURL(url);

            if (content == null)
            {
                continue;
            }
            try {

                parser.setInputHTML(content);
                //System.out.println(content);
                parser.setEncoding("gb18030");

                HasParentFilter parentFilter = new HasParentFilter(new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "houseList")));
                NodeFilter filter = new AndFilter(new TagNameFilter("dl"), new AndFilter(parentFilter, new HasAttributeFilter("class","list rel")));
                NodeList nodes = parser.extractAllNodesThatMatch(filter);
                int houseList_size=nodes.size();

                parser.reset();
                parentFilter = new HasParentFilter(new AndFilter(new TagNameFilter("p"), new HasAttributeFilter("class", "title")));
                filter = new AndFilter(new TagNameFilter("a"), new AndFilter(new AndFilter(parentFilter, new HasAttributeFilter("title")), new HasAttributeFilter("href")));
                nodes = parser.extractAllNodesThatMatch(filter);
                int urlnodes_size=nodes.size();



                if (urlnodes_size != 0)
                {
                    int  purl_count=0;

                    //先处理那些有链接的数据
                    for (int n = 0; n < urlnodes_size; n ++)
                    {
                        TagNode tn = (TagNode)nodes.elementAt(n);
                        String title=tn.toPlainTextString().replace("\r\n","").replace("\n","").replace("\b","").replace("\t","").trim();
                        obj=new JSONObject();
                        obj.put("title",title);
                        String purl = tn.getAttribute("href");
                        if (purl.startsWith("/chengjiao"))
                        {
                            ++purl_count;//每获取一条自增1
                            System.out.println("访问该"+current_page+"页下的第"+purl_count+"条数据");
                            parser.reset();
                            NodeFilter filter1 =new HasAttributeFilter("class", "time");
                            NodeList nodes1 = parser.extractAllNodesThatMatch(filter1);

                            if(nodes1.size()!=0){
                                for (int nn = 0; nn < nodes1.size(); nn ++)
                                {
                                    if(nn==n){
                                        TagNode tnn = (TagNode)nodes1.elementAt(nn);
                                        String str=tnn.toPlainTextString().replace(" ", "").replace("\r\n","").replace("\n","").replace("\b","").replace("\t","").trim();
                                        obj.put("time",str);
                                        break;
                                    }
                                }
                            }
                            parser.reset();
                            filter1 =new HasAttributeFilter("class", "tag mt5");
                            nodes1 = parser.extractAllNodesThatMatch(filter1);

                            if(nodes1.size()!=0){
                                for (int nnn = 0; nnn < nodes1.size(); nnn ++)
                                {
                                    if(nnn==n){
                                        TagNode tnn = (TagNode)nodes1.elementAt(nnn);
                                        String str=tnn.toPlainTextString().replace(" ", "").replace("\r\n","").replace("\n","").replace("\b","").replace("\t","").trim();
                                        obj.put("dealweb",str);
                                        break;
                                    }
                                }
                            }
                            String deal_url="http://esf.fang.com" + purl;
                            obj.put("url",deal_url);
                            parseResold_fang(deal_url);
                            String poi =obj.toString();
                            if (poi != null) {
                                if (quit){
                                    break;
                                } else {
                                    synchronized(BJ_RESOLDS)
                                    {

                                        if(url.indexOf("a0987")!=-1||url.indexOf("a011817")!=-1)
                                            FileTool.Dump(poi,FOLDER2, "UTF-8");
                                        else
                                            FileTool.Dump(poi,FOLDER1, "UTF-8");
                                        //System.out.println(poi);
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
                        }
                    }


                    //接下来处理那些只有信息没有链接的数据
                    if(urlnodes_size<houseList_size){
                        parser.reset();
                        NodeFilter filter1 =new AndFilter(new TagNameFilter("dd"),new HasAttributeFilter("class", "info rel floatr"));
                        NodeList nodes1 = parser.extractAllNodesThatMatch(filter1);
                        int title_size=nodes1.size();

                        if(title_size>0){
                            for(int t=0;t<title_size;t++){
                                if(t==(purl_count)){

                                    TagNode tn=(TagNode)nodes1.elementAt(t);
                                    NodeList children=tn.getChildren ();
                                    //System.out.println(children.size());

                                    if(children.size()!=0){

                                        JSONObject obj=new JSONObject();
                                        for(int c=0;c<children.size();c++){
                                            try{
                                                TagNode c_tn=(TagNode)children.elementAt(c);
                                                String attribute=c_tn.getAttribute("class");

                                                if(attribute.equals("title")){
                                                    String str=tidyData(c_tn.toPlainTextString());
                                                    //System.out.println(str);
                                                    String[] infos=str.split(" ");
                                                    obj.put("community",infos[0]);
                                                    obj.put("house_type",infos[1]);
                                                    obj.put("area",infos[2].replace("平米",""));
                                                }else if(attribute.equals("mt18")){
                                                    String str=tidyData(c_tn.toPlainTextString()).replace(" ","");
                                                    //System.out.println(str);
                                                    String[] infos=str.split("\\|");
                                                    obj.put("direction",infos[0]);
                                                    obj.put("floor",infos[1]);
                                                }else if(attribute.equals("mt15")){
                                                    String str=tidyData(c_tn.toPlainTextString().replace(" ",""));
                                                    //System.out.println(str);
                                                    obj.put("location",str);
                                                }else if(attribute.equals("area alignR")){
                                                    String str=tidyData(c_tn.toPlainTextString().replace(" ",""));
                                                    //System.out.println(str);
                                                    obj.put("time",str);
                                                }else if(attribute.equals("moreInfo")){
                                                    String str=tidyData(c_tn.toPlainTextString().replace(" ","").replace("/�O",""));
                                                    //System.out.println(str);
                                                    String[] infos=str.split("万");
                                                    obj.put("price",infos[0]);
                                                    obj.put("unit_price",infos[1].replace("元",""));
                                                }

                                            }catch(ClassCastException e){
                                                e.getMessage();
                                            }
                                        }

                                        FileTool.Dump(obj.toString(),"D:\\test\\fang\\子区域\\房天下二手房成交数据1102_市场信息.txt","utf-8");

                                    }
                                    ++purl_count;
                                    System.out.println("访问"+current_page+"页下的第"+purl_count+"条数据");

                                }
                            }
                        }
                    }
                }else{

                    parser.reset();
                    NodeFilter filter1 =new AndFilter(new TagNameFilter("dd"),new HasAttributeFilter("class", "info rel floatr"));
                    NodeList nodes1 = parser.extractAllNodesThatMatch(filter1);
                    int title_size=nodes1.size();

                    int purl_count=0;
                    if(title_size>0){
                        for(int t=0;t<title_size;t++){

                                TagNode tn=(TagNode)nodes1.elementAt(t);
                                NodeList children=tn.getChildren ();
                                //System.out.println(children.size());

                                if(children.size()!=0){

                                    JSONObject obj=new JSONObject();
                                    for(int c=0;c<children.size();c++){
                                        try{
                                            TagNode c_tn=(TagNode)children.elementAt(c);
                                            String attribute=c_tn.getAttribute("class");

                                            if(attribute.equals("title")){
                                                String str=tidyData(c_tn.toPlainTextString());
                                                //System.out.println(str);
                                                String[] infos=str.split(" ");
                                                obj.put("community",infos[0]);
                                                obj.put("house_type",infos[1]);
                                                obj.put("area",infos[2].replace("平米",""));
                                            }else if(attribute.equals("mt18")){
                                                String str=tidyData(c_tn.toPlainTextString()).replace(" ","");
                                                //System.out.println(str);
                                                String[] infos=str.split("\\|");
                                                obj.put("direction",infos[0]);
                                                obj.put("floor",infos[1]);
                                            }else if(attribute.equals("mt15")){
                                                String str=tidyData(c_tn.toPlainTextString().replace(" ",""));
                                                //System.out.println(str);
                                                obj.put("location",str);
                                            }else if(attribute.equals("area alignR")){
                                                String str=tidyData(c_tn.toPlainTextString().replace(" ",""));
                                                //System.out.println(str);
                                                obj.put("time",str);
                                            }else if(attribute.equals("moreInfo")){
                                                String str=tidyData(c_tn.toPlainTextString().replace(" ","").replace("/�O",""));
                                                //System.out.println(str);
                                                String[] infos=str.split("万");
                                                obj.put("price",infos[0]);
                                                obj.put("unit_price",infos[1].replace("元",""));
                                            }

                                        }catch(ClassCastException e){
                                            e.getMessage();
                                        }
                                    }

                                    FileTool.Dump(obj.toString(),"D:\\test\\fang\\子区域\\房天下二手房成交数据1102_市场信息.txt","utf-8");

                                }
                                ++purl_count;
                                System.out.println("访问"+current_page+"页下的第"+purl_count+"条数据");
                        }
                    }

                }

                parser.reset();

                filter = new AndFilter(new TagNameFilter("a"),new HasAttributeFilter("id","PageControl1_hlk_next") );
                nodes = parser.extractAllNodesThatMatch(filter);

                if (nodes != null)
                {
                    String turl = ((TagNode)nodes.elementAt(0)).getAttribute("href");
                    if (!visited.contains("http://esf.fang.com" + turl))
                    {
                        int kk = 0;
                        for (; kk < urls.size(); kk ++)
                        {
                            if (urls.elementAt(kk).equalsIgnoreCase("http://esf.fang.com" + turl))
                            {
                                break;
                            }
                        }

                        if (kk == urls.size())
                            urls.add("http://esf.fang.com" + turl);
                    }

                }

                if (quit)
                    break;
            }
            catch (ParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }catch(NullPointerException e){
                System.out.println(e.getMessage());
            }
        }

        System.out.println(region+"区域访问的网页数目："+visited.size());
    }
    public static void parseResold_fang(String url){
        String content = Tool.fetchURL(url);
        //System.out.println(content);
        Parser parser = new Parser();

        try {

            parser.setInputHTML(content);
            //System.out.println(content);
            //System.out.println(url);
            parser.setEncoding("gb2312");

            NodeFilter filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "mainBoxLinforwrap"));
            NodeList nodes = parser.extractAllNodesThatMatch(filter);
            if (nodes.size()!=0)
            {
                //System.out.println(nodes.elementAt(i).toPlainTextString());
                TagNode tagNode=(TagNode)nodes.elementAt(0);
                NodeList childs=tagNode.getChildren ();
                //System.out.println(childs.size());
                if(childs.size()!=0){
                    String str=childs.elementAt(1).toPlainTextString().replace(" ", "").replace("\r\n","").replace("\n","").replace("\b","").replace("\t","").trim();
                    int ss=str.indexOf("签约价格");
                    int ee=str.indexOf("单价");
                    if(ss!=-1&&ee!=-1){
                        String substr=str.substring(ss+"签约价格".length(),ee);
                        obj.put("price",substr);
                    }
                    ss=str.indexOf("单价");
                    ee=str.indexOf("朝向");
                    if(ss!=-1&&ee!=-1){
                        String substr=str.substring(ss+"单价".length(),ee).replace("�O","").replace("/","");
                        obj.put("unitprice",substr);
                    }
                    ss=str.indexOf("朝向");
                    ee=str.indexOf("面积：");
                    if(ss!=-1&&ee!=-1){
                        String substr=str.substring(ss+"朝向".length(),ee);
                        obj.put("direction",substr);
                    }
                    ss=str.indexOf("面积：");
                    ee=str.indexOf("楼层：");
                    if(ss!=-1&&ee!=-1){
                        String substr=str.substring(ss+"面积：".length(),ee).replace("�O","");
                        obj.put("area",substr);
                    }
                    ss=str.indexOf("楼层：");
                    ee=str.indexOf("小区：");
                    if(ss!=-1&&ee!=-1){
                        String substr=str.substring(ss+"楼层：".length(),ee);
                        obj.put("floor",substr);
                    }
                    ss=str.indexOf("小区：");
                    if(ss!=-1){
                        String substr=str.substring(ss+"小区：".length());
                        obj.put("community",substr);
                    }
                }
            }
        } catch (ParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }


    //抓取链家网二手房成交数据
    public static void getResoldApartmentInfo_lianjia(String region){

        //http://bj.lianjia.com/chengjiao/dongcheng/pg1/
        for(int page=1;page<=100;page++){

            String url = "http://bj.lianjia.com/chengjiao/" + region+"/pg"+page+"/";
            Vector<String> urls = new Vector<String>();
            Set<String> visited = new TreeSet<String>();
            urls.add(url);

            Parser parser = new Parser();
            boolean quit = false;

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

                    HasParentFilter parentFilter = new HasParentFilter(new AndFilter(new TagNameFilter("ul"), new HasAttributeFilter("class", "listContent")));
                    NodeFilter filter = new AndFilter(new TagNameFilter("li"),parentFilter);
                    NodeList nodes = parser.extractAllNodesThatMatch(filter);

                    int urlnodes_size=nodes.size();
                    //System.out.println(urlnodes_size);
                    if(urlnodes_size!=0){
                        for (int n = 0; n < nodes.size(); n ++)
                        {
                            String total="";
                            TagNode tn=(TagNode)nodes.elementAt(n);
                            String str=tn.toPlainTextString().replace("\r\n","").replace("\n","").replace("\b","").replace("\t","").trim();
                            //System.out.println(str);
                            String[] info=str.split("  ");

                            for(int i=0;i<info.length;i++){
                                if(info[i].length()!=0){
                                    total+=info[i].replace(";","")+";";
                                    //System.out.println(i+":"+info[i]);
                                }
                            }
                            System.out.println(total);
                            if(region.indexOf("yanjiao")==-1){
                                FileTool.Dump(total,"D:\\test\\lianjia\\二手房成交数据.txt","utf-8");
                            }else {
                                FileTool.Dump(total,"D:\\test\\lianjia\\二手房成交数据_燕郊.txt","utf-8");
                            }

                            try {
                                Thread.sleep(500 * ((int) (Math
                                        .max(1, Math.random() * 3))));
                            } catch (final InterruptedException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }

                        }
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
