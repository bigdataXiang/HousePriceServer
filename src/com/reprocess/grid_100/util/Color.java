package com.reprocess.grid_100.util;

/**
 * Created by ZhouXiang on 2016/10/12.
 */
public class Color {
    /**建立更密集的配色方案_房价*/
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

    /**房价加速度的配色方案*/
    public static String setColorRegion_Acceleration(double price){
        String color="";

        if(price>900){
            color="#C70305";
        }else if(price>800&&price<=900){
            color="#EA4706";
        }else if(price>700&&price<=800){
            color="#E97A04";
        }else if(price>600&&price<=700){
            color="#E9A708";
        }else if(price>500&&price<=600){
            color="#E6CC05";
        }else if(price>400&&price<=500){
            color="#E9E507";
        }else if(price>300&&price<=400){
            color="#D8EB00";
        }else if(price>250&&price<=300){
            color="#B8E705";
        }else if(price>200&&price<=250){
            color="#04E738";
        }else if(price>100&&price<=200){
            color="#06E884";
        }else{
            color="#08E9C7";
        }
        return color;
    }

}
