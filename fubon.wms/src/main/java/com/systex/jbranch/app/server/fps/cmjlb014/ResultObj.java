package com.systex.jbranch.app.server.fps.cmjlb014;

/**
 * @author Walker
 * 
 */
public class ResultObj {

	
	private String resultCode;
	private String resultStr;
	private double[][] resultArray;
	private String[] resultStrArray;
	
	

	public String getResultCode() {
		return resultCode;
	}


	public String getResultStr() {
		return resultStr;
	}


	public double[][] getResultArray() {
		return resultArray;
	}


	public String[] getResultStrArray() {
		return resultStrArray;
	}

	ResultObj(String code, String reason) {

		this.resultCode = code;
		this.resultStr = reason;
	}

	ResultObj(String code, String reason, double arr) {
		this.resultCode = code;
		this.resultStr = reason;
		this.resultArray = new double[][] { { arr } };
	}

	ResultObj(String code, String reason, double[] arr) {
		this.resultCode = code;
		this.resultStr = reason;
		this.resultArray = new double[][] { arr };
	}

	ResultObj(String code, String reason, double[][] arr) {
		this.resultCode = code;
		this.resultStr = reason;
		this.resultArray = arr;
	}

	ResultObj(String code, String reason, double[][] arr, String[] strArr) {
		this.resultCode = code;
		this.resultStr = reason;
		this.resultArray = arr;
		this.resultStrArray = strArr;
	}

	

}
