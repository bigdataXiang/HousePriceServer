package com.reprocess.grid_100.util;

/**
 * Created by ZhouXiang on 2016/10/12.
 */
public class color {
    /**建立更密集的配色方案*/
    public static String setColorRegion(double price){
        String color="";

        if(price>9){
            color="#C70305";
        }else if(price>8&&price<=9){
            color="#EA4706";
        }else if(price>7.5&&price<=8){
            color="#E97A04";
        }else if(price>7&&price<=7.5){
            color="#E9A708";
        }else if(price>6.5&&price<=7){
            color="#E6CC05";
        }else if(price>6&&price<=6.5){
            color="#E9E507";
        }else if(price>5.5&&price<=6){
            color="#D8EB00";
        }else if(price>5&&price<=5.5){
            color="#B8E705";
        }else if(price>4.5&&price<=5){
            color="#04E738";
        }else if(price>4&&price<=4.5){
            color="#06E884";
        }else if(price>3.5&&price<=4){
            color="#08E9C7";
        }else if(price>3&&price<=3.5){
            color="#03EAE4";
        }else if(price>2.5&&price<=3){
            color="#09BAEC";
        }else if(price>2&&price<=2.5){
            color="#077BEA";
        }else{
            color="#1411D2";
        }

        return color;
    }
}
