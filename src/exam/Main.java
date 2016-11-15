package exam;

import com.sun.xml.internal.fastinfoset.util.CharArray;
import utils.FileTool;

import java.util.*;

public class Main {
    public static void main(String[] args)  throws Exception{

         //nianhao();
        zaicipaichong();

    }
    public static void nianhao(){
        Vector<String> pois= FileTool.Load("D:\\自然语言处理\\语料\\年代整理\\新建文件夹\\朝代映射_去修饰.txt","utf-8");
        Map<String,String> map=new HashMap<>();
        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            String[] array=poi.split(",");
            String chaodai=array[0];
            String nianhao=array[1];
            String niandai=array[2];
            String key=chaodai+nianhao;

            if(map.containsKey(key)){

                FileTool.Dump(key+","+niandai,"D:\\自然语言处理\\语料\\年代整理\\新建文件夹\\朝代映射_去修饰_出现重复的年号.txt","utf-8");

            }else{
                map.put(key,niandai);
            }
        }
    }

    public static void zaicipaichong(){
        Vector<String> pois= FileTool.Load("D:\\自然语言处理\\语料\\年代整理\\新建文件夹\\朝代映射_去修饰.txt","utf-8");
        Map<String,String> map=new HashMap<>();
        Map<String,String> chongfumap=new HashMap<>();
        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            String[] array=poi.split(",");
            String chaodai=array[0];
            String nianhao=array[1];
            String niandai=array[2];
            String key=chaodai+nianhao;

            if(map.containsKey(nianhao)){
                chongfumap.put(nianhao,"");
            }else{
                map.put(nianhao,niandai);
            }
        }


        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            String[] array=poi.split(",");
            String chaodai=array[0];
            String nianhao=array[1];
            String niandai=array[2];
            String key=chaodai+nianhao;

            if(chongfumap.containsKey(nianhao)){
                if(array.length==3){
                    FileTool.Dump(0+","+chaodai+","+nianhao+","+niandai,"D:\\自然语言处理\\语料\\年代整理\\新建文件夹\\朝代映射_去修饰_标记未重复.txt","utf-8");
                }else if(array.length==4){
                    FileTool.Dump(0+","+chaodai+","+nianhao+","+niandai+","+array[3],"D:\\自然语言处理\\语料\\年代整理\\新建文件夹\\朝代映射_去修饰_标记未重复.txt","utf-8");
                }

            }else{
                FileTool.Dump(1+","+chaodai+","+nianhao+","+niandai,"D:\\自然语言处理\\语料\\年代整理\\新建文件夹\\朝代映射_去修饰_标记未重复.txt","utf-8");
            }
        }
    }
}
/**
 * Scanner sc = new Scanner(System.in);
 List<Integer> array=new ArrayList<>();
 List<String> strlist=new ArrayList<>();
 while(sc.hasNext()){
 int n=sc.nextInt();
 for(int i=0;i<n;i++){
 String str=sc.nextLine();
 char[] strchar=str.toCharArray();
 for(int j=0;j<strchar.length;j++){
 strlist.add(""+strchar[j]);
 }
 //strlist=str.toCharArray();
 //str.
 int len=str.length();
 while(len>0){
 if(len>=4&&str.indexOf("Z")!=0&&str.indexOf("E")!=0&&str.indexOf("R")!=0&&str.indexOf("O")!=0){
 array.add(0);
 len=len-4;
 strlist.remove("Z");
 strlist.remove("E");
 strlist.remove("R");
 strlist.remove("O");
 }

 str=strlist.toString();
 if(len>=3&&str.indexOf("O")!=0&&str.indexOf("N")!=0&&str.indexOf("E")!=0){
 array.add(1);
 len=len-3;
 strlist.remove("O");
 strlist.remove("E");
 strlist.remove("N");
 }

 str=strlist.toString();
 if(len>=3&&str.indexOf("T")!=0&&str.indexOf("W")!=0&&str.indexOf("O")!=0){
 array.add(2);
 len=len-3;
 strlist.remove("T");
 strlist.remove("W");
 strlist.remove("O");
 }

 str=strlist.toString();
 if(len>=5&&str.indexOf("T")!=0&&str.indexOf("H")!=0&&str.indexOf("R")!=0&&str.indexOf("E")!=0&&str.indexOf("E")!=0){
 array.add(3);
 len=len-5;
 strlist.remove("T");
 strlist.remove("H");
 strlist.remove("R");
 strlist.remove("E");
 strlist.remove("E");
 }

 str=strlist.toString();
 if(len>=4&&str.indexOf("F")!=0&&str.indexOf("O")!=0&&str.indexOf("U")!=0&&str.indexOf("R")!=0){
 array.add(4);
 len=len-4;
 strlist.remove("F");
 strlist.remove("O");
 strlist.remove("U");
 strlist.remove("R");
 }

 str=strlist.toString();
 if(len>=4&&str.indexOf("F")!=0&&str.indexOf("I")!=0&&str.indexOf("V")!=0&&str.indexOf("E")!=0){
 array.add(5);
 len=len-4;
 strlist.remove("F");
 strlist.remove("I");
 strlist.remove("V");
 strlist.remove("E");
 }

 str=strlist.toString();
 if(len>=3&&str.indexOf("S")!=0&&str.indexOf("I")!=0&&str.indexOf("X")!=0){
 array.add(6);
 len=len-3;
 strlist.remove("S");
 strlist.remove("I");
 strlist.remove("X");
 }

 str=strlist.toString();
 if(len>=5&&str.indexOf("S")!=0&&str.indexOf("E")!=0&&str.indexOf("V")!=0&&str.indexOf("E")!=0&&str.indexOf("N")!=0){
 array.add(7);
 len=len-5;
 strlist.remove("S");
 strlist.remove("E");
 strlist.remove("V");
 strlist.remove("E");
 strlist.remove("N");
 }

 str=strlist.toString();
 if(len>=5&&str.indexOf("E")!=0&&str.indexOf("I")!=0&&str.indexOf("G")!=0&&str.indexOf("H")!=0&&str.indexOf("T")!=0){
 array.add(8);
 len=len-5;
 strlist.remove("E");
 strlist.remove("I");
 strlist.remove("G");
 strlist.remove("H");
 strlist.remove("T");
 }

 str=strlist.toString();
 if(len>=4&&str.indexOf("N")!=0&&str.indexOf("I")!=0&&str.indexOf("N")!=0&&str.indexOf("E")!=0){
 array.add(8);
 len=len-4;
 strlist.remove("N");
 strlist.remove("I");
 strlist.remove("N");
 strlist.remove("E");
 }
 }

 for(int m=0;m<array.size();m++){
 System.out.println(array.get(m));
 }

 }
 }
 * */
/**
 *  Map<Integer,String> root=new HashMap<>();
 Map<Integer,String> leaf=new HashMap<>();

 int len = sc.nextInt();
 for(int i=0;i<len-1;i++){
 int rt=sc.nextInt();
 int lf=sc.nextInt();

 root.put(rt,"");
 leaf.put(lf,"");
 System.out.println("rt"+rt);
 System.out.println("lf"+lf);
 }
 System.out.println(root.size()+1);
 */
