package com.svail.bean;

import java.util.GregorianCalendar;

/**
 * Created by ZhouXiang on 2016/7/12.
 */
public class MultiFieldComparison {


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int code;

    public double getAverage_price() {
        return average_price;
    }

    public void setAverage_price(double average_price) {
        this.average_price = average_price;
    }

    public double average_price;
    public MultiFieldComparison(int code,double average_price){
        this.code=code;
        this.average_price=average_price;
    }

}
