package com.systex.jbranch.app.server.fps.fps323;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS323OutputVO extends PagingInputVO{
	
	private int invSum;
	private int resultBad;
	private int resultNormal;
	private int resultGood;
	private int expect;
	private String errCode;
	public int getInvSum() {
		return invSum;
	}
	public void setInvSum(int invSum) {
		this.invSum = invSum;
	}
	public int getResultBad() {
		return resultBad;
	}
	public void setResultBad(int resultBad) {
		this.resultBad = resultBad;
	}
	public int getResultNormal() {
		return resultNormal;
	}
	public void setResultNormal(int resultNormal) {
		this.resultNormal = resultNormal;
	}
	public int getResultGood() {
		return resultGood;
	}
	public void setResultGood(int resultGood) {
		this.resultGood = resultGood;
	}
	public int getExpect() {
		return expect;
	}
	public void setExpect(int expect) {
		this.expect = expect;
	}
	public String getErrCode() {
		return errCode;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
	
}
