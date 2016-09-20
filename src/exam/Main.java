package exam;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by ZhouXiang on 2016/9/20.
 */
public class Main {
    public static void main(String[] args)  throws Exception{

        Scanner in = new Scanner(System.in);
        while(in.hasNext()){
            String str=in.nextLine();
        }
    }
    public static void guShen(){
        Scanner in = new Scanner(System.in);
        Integer n = null;
        while((n = in.nextInt())!=null){
            int price = 1;
            int i=2;int j=3;
            for(int m=1;m<n;m++){
                if(m==i){
                    price--;
                    i=i+j;
                    j++;
                }
                else{
                    price++;}
            }
            System.out.println(price);

        }
    }
    public static void fanZhuanShuZu(){
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextInt())
        {
            int len = scanner.nextInt();
            int[] array = new int[len];
            int[] copy = new int[len];
            for(int i=0;i<len;i++)
            {
                array[i] = scanner.nextInt();
                copy[i] = array[i];
            }
            Arrays.sort(copy);
            int left = 0,right = len-1;
            while(left<len && copy[left]==array[left]) left++;
            while(right>=0 && copy[right]==array[right]) right--;


            int i;
            for(i=0;i<=right-left;i++)
            {
                if(copy[left+i]!=array[right-i])
                    break;
            }
            if(i>right-left)
                System.out.println("yes");
            else
                System.out.println("no");
        }
    }
}
