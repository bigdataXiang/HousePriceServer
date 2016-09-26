package exam;

import com.sun.xml.internal.fastinfoset.util.CharArray;

import java.util.*;

public class Main {
    public static void main(String[] args)  throws Exception{
        Scanner sc = new Scanner(System.in);
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



    }
}
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
