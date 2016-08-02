package com.svail.bean;

public class Code {
	public int row;
	public int col;
	public int code;
	public int getRow(){
		return row;
	}
	public int getCol(){
		return col;
	}
	public long getCode(int row,int col){
		return code;
	}
	public void setRow(int row){
		this.row=row;
	}
	public void setCol(int col){
		this.col=col;
	}
	public void setCode(int code){
		this.code=code;
	}

}
