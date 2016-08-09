package com.reprocess.grid_100;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhouXiang on 2016/8/9.
 */
public class Code_Price_RowCol {
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;

    public List<Double> getPricelist() {
        return pricelist;
    }

    public void setPricelist(List<Double> pricelist) {
        this.pricelist = pricelist;
    }

    List<Double> pricelist=new ArrayList<>();

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    int row;

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    int col;
}
