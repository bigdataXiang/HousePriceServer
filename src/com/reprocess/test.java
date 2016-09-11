package com.reprocess;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class test {
    public static void main(String[] args){


    }

}

/**
 *  Scanner cin = new Scanner(System.in);
 int T=0,MaxMem=0;
 int count=0;
 int[] nums=new int[2];

 nums[0]=cin.nextInt();
 nums[1]=cin.nextInt();

 T=nums[0];
 MaxMem=nums[1];

 //System.out.println(nums[0]);
 //System.out.println(nums[1]);

 cin = new Scanner(System.in);
 String[] order=new String[T];
 count=0;

 for(int i=0;i<order.length;i++){
 order[i]=cin.nextLine();
 //System.out.println(order[i]);
 }
 // System.out.println(order[1]);

 int size=0;
 String temp_order="";
 int ok=0;
 Map<Integer,Integer> map=new HashMap<>();
 int p=0;
 int temp_MaxMem=0;
 int del_value=0;
 for(int i=0;i<order.length;i++){

 temp_order=order[i];
 if(temp_order.startsWith("new")){

 size=Integer.parseInt(temp_order.substring(temp_order.indexOf("new")+"new".length()).replace(" ",""));
 MaxMem=MaxMem-size;

 if(MaxMem>0){
 ok++;
 map.put(ok,size);
 System.out.println(ok);

 }else{
 MaxMem=MaxMem+size;
 System.out.println("NULL");
 }

 }else if(temp_order.startsWith("del")){


 p=Integer.parseInt(temp_order.substring(temp_order.indexOf("del")+"del".length()).replace(" ",""));
 if(map.containsKey(p)){
 del_value=map.get(p);
 temp_MaxMem+=del_value;
 }else{
 System.out.println("ILLEGAL_OPERATION");
 }


 }else if(temp_order.startsWith("def")){

 MaxMem=MaxMem+temp_MaxMem;
 temp_MaxMem=0;
 }

 }
 */
