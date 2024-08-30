package com.systex.jbranch.platform.common.excel;

public class Location {
	private int x;
	private int y;
	
	Location(){
		
	}
	
	Location(int x, int y) {
		this.x = x;
		this.y = y;
	}
	int getX() {
		return x;
	}
	void setX(int x) {
		this.x = x;
	}
	int getY() {
		return y;
	}
	void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {

		return  "(" + x + "," + y + ")";
	}
}
