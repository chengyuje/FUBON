package com.systex.jbranch.app.server.fps.iot360;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT360InputVO extends PagingInputVO{
	
	


	private String INS_ID            ;
	private Date KEYIN_DATE_FROM   ;
	private Date KEYIN_DATE_TO     ;
	private String BRANCH_NBR        ;
	private String CUST_ID           ;
	private String INSURED_ID        ;
	private Date APPLY_DATE        ;
	private String PPT_TYPE          ; //險種代碼
	public String getINS_ID() {
		return INS_ID;
	}
	public Date getKEYIN_DATE_FROM() {
		return KEYIN_DATE_FROM;
	}
	public Date getKEYIN_DATE_TO() {
		return KEYIN_DATE_TO;
	}
	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public String getINSURED_ID() {
		return INSURED_ID;
	}
	public Date getAPPLY_DATE() {
		return APPLY_DATE;
	}
	public String getPPT_TYPE() {
		return PPT_TYPE;
	}
	public void setINS_ID(String iNS_ID) {
		INS_ID = iNS_ID;
	}
	public void setKEYIN_DATE_FROM(Date kEYIN_DATE_FROM) {
		KEYIN_DATE_FROM = kEYIN_DATE_FROM;
	}
	public void setKEYIN_DATE_TO(Date kEYIN_DATE_TO) {
		KEYIN_DATE_TO = kEYIN_DATE_TO;
	}
	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public void setINSURED_ID(String iNSURED_ID) {
		INSURED_ID = iNSURED_ID;
	}
	public void setAPPLY_DATE(Date aPPLY_DATE) {
		APPLY_DATE = aPPLY_DATE;
	}
	public void setPPT_TYPE(String pPT_TYPE) {
		PPT_TYPE = pPT_TYPE;
	}


	
	



	
	
}
