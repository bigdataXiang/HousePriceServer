package com.svail.bean;

/**
 * Created by ZhouXiang on 2016/7/18.
 */
public class Response {
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    int code;
    String content;
}
