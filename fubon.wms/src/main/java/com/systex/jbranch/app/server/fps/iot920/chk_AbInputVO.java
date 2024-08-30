package com.systex.jbranch.app.server.fps.iot920;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class chk_AbInputVO extends PagingInputVO{
	
	private String REAL_PREMIUM;	//實收保費
	private String PAY_TYPE;		//繳別
	private String MOP2;			//分期繳繳別
	private String AB_EXCH_RATE;	//非常態交易匯率
	private String UNDER_YN;		//弱勢戶
	private String PRO_YN;			//專業投資人
	private String FirstBuy_YN;		//是否為首購
	
	
	
	public String getMOP2() {
		return MOP2;
	}
	public void setMOP2(String mOP2) {
		MOP2 = mOP2;
	}
	public String getREAL_PREMIUM() {
		return REAL_PREMIUM;
	}
	public String getPAY_TYPE() {
		return PAY_TYPE;
	}
	public String getAB_EXCH_RATE() {
		return AB_EXCH_RATE;
	}
	public String getUNDER_YN() {
		return UNDER_YN;
	}
	public String getPRO_YN() {
		return PRO_YN;
	}
	public String getFirstBuy_YN() {
		return FirstBuy_YN;
	}
	public void setREAL_PREMIUM(String rEAL_PREMIUM) {
		REAL_PREMIUM = rEAL_PREMIUM;
	}
	public void setPAY_TYPE(String pAY_TYPE) {
		PAY_TYPE = pAY_TYPE;
	}
	public void setAB_EXCH_RATE(String aB_EXCH_RATE) {
		AB_EXCH_RATE = aB_EXCH_RATE;
	}
	public void setUNDER_YN(String uNDER_YN) {
		UNDER_YN = uNDER_YN;
	}
	public void setPRO_YN(String pRO_YN) {
		PRO_YN = pRO_YN;
	}
	public void setFirstBuy_YN(String firstBuy_YN) {
		FirstBuy_YN = firstBuy_YN;
	}
	
	

}
