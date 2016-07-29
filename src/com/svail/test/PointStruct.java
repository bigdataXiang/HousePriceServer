package com.svail.test;

/**
 * Created by ZhouXiang on 2016/7/20.
 */
public class PointStruct {
    double dx;
    double dy;

    int ix;
    int iy;

    public PointStruct(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public PointStruct(int ix, int iy) {
        this.ix = ix;
        this.iy = iy;
    }

    public PointStruct(double dx, double dy,boolean round) {
        this.ix = RoundF(dx);
        this.iy = RoundF(dy);
    }

    public int RoundF(double a){
        return (int) Math.round(a);
    }
}
