package com.systex.jbranch.app.server.fps.ins112;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS112InputVO extends PagingInputVO{
	
	private String COM_ID;//保險公司代碼
	private String IS_SALE;//商品狀態
	private String QID;//險種別
	private String PRD_ID; //商品代碼
	private String PRD_NAME;//商品名稱
	private String IS_MAIN_TYPE;	//主/附約
	private String CURR_CD; // 幣別
	
	
	
	public String getPRD_ID() {
		return PRD_ID;
	}
	public void setPRD_ID(String pRD_ID) {
		PRD_ID = pRD_ID;
	}
	public String getIS_MAIN_TYPE() {
		return IS_MAIN_TYPE;
	}
	public void setIS_MAIN_TYPE(String iS_MAIN_TYPE) {
		IS_MAIN_TYPE = iS_MAIN_TYPE;
	}
	public String getCOM_ID() {
		return COM_ID;
	}
	public String getIS_SALE() {
		return IS_SALE;
	}
	public String getQID() {
		return QID;
	}
	public String getPRD_NAME() {
		return PRD_NAME;
	}
	public void setCOM_ID(String cOM_ID) {
		COM_ID = cOM_ID;
	}
	public void setIS_SALE(String iS_SALE) {
		IS_SALE = iS_SALE;
	}
	public void setQID(String qID) {
		QID = qID;
	}
	public void setPRD_NAME(String pRD_NAME) {
		PRD_NAME = pRD_NAME;
	}
	public String getCURR_CD() {
		return CURR_CD;
	}
	public void setCURR_CD(String cURR_CD) {
		CURR_CD = cURR_CD;
	}
	
	
	

}
