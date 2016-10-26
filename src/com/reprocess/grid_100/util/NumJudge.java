package com.reprocess.grid_100.util;

import java.math.BigDecimal;

/**
 * Created by ZhouXiang on 2016/10/26.
 */
public class NumJudge {
    public static boolean isNum(String str) {

        try {
            new BigDecimal(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
