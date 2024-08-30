package com.systex.jbranch.app.server.fps.iot190;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * @author USER
 *
 */
public class IOT190InputVO extends PagingInputVO {

	private String CUST_ID; // 要保人ID
	private String BRANCH_AREA_ID; // 區別
	private String BRANCH_AREA_NAME; // 區別名
	private String INSPRD_ID; // 險種代碼
	private String BRANCH_NBR; // 分行
	private String BRANCH_NAME; // 分行名
	private Date KEYIN_DATE_B; // 鍵機日(起)
	private Date KEYIN_DATE_E; // 鍵機日(迄)
	private String INS_ID; // 保險文件編號
	private String REG_TYPE; // 送件類型
	private String STATUS; // 文件簽收狀態
	private String POLICY_NO1; // 保單號碼1
	private String POLICY_NO2; // 保單號碼2
	private String POLICY_NO3; // 保單號碼3
	private String INSURED_ID; // 被保人/立約人ID
	private String FXD_PRODNAME; // 適用專案
	private String PROD_PERIOD;
	private String FB_COM_YN;
	private String COMPANY_NUM;

	public String getPROD_PERIOD() {
		return PROD_PERIOD;
	}

	public void setPROD_PERIOD(String pROD_PERIOD) {
		PROD_PERIOD = pROD_PERIOD;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public String getBRANCH_AREA_ID() {
		return BRANCH_AREA_ID;
	}

	public String getINSPRD_ID() {
		return INSPRD_ID;
	}

	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}

	public String getINS_ID() {
		return INS_ID;
	}

	public String getREG_TYPE() {
		return REG_TYPE;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public String getPOLICY_NO1() {
		return POLICY_NO1;
	}

	public String getPOLICY_NO2() {
		return POLICY_NO2;
	}

	public String getPOLICY_NO3() {
		return POLICY_NO3;
	}

	public String getINSURED_ID() {
		return INSURED_ID;
	}

	public String getFXD_PRODNAME() {
		return FXD_PRODNAME;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public void setBRANCH_AREA_ID(String bRANCH_AREA_ID) {
		BRANCH_AREA_ID = bRANCH_AREA_ID;
	}

	public void setINSPRD_ID(String iNSPRD_ID) {
		INSPRD_ID = iNSPRD_ID;
	}

	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}

	public void setINS_ID(String iNS_ID) {
		INS_ID = iNS_ID;
	}

	public void setREG_TYPE(String rEG_TYPE) {
		REG_TYPE = rEG_TYPE;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public void setPOLICY_NO1(String pOLICY_NO1) {
		POLICY_NO1 = pOLICY_NO1;
	}

	public void setPOLICY_NO2(String pOLICY_NO2) {
		POLICY_NO2 = pOLICY_NO2;
	}

	public void setPOLICY_NO3(String pOLICY_NO3) {
		POLICY_NO3 = pOLICY_NO3;
	}

	public void setINSURED_ID(String iNSURED_ID) {
		INSURED_ID = iNSURED_ID;
	}

	public void setFXD_PRODNAME(String fXD_PRODNAME) {
		FXD_PRODNAME = fXD_PRODNAME;
	}

	public Date getKEYIN_DATE_B() {
		return KEYIN_DATE_B;
	}

	public Date getKEYIN_DATE_E() {
		return KEYIN_DATE_E;
	}

	public void setKEYIN_DATE_B(Date kEYIN_DATE_B) {
		KEYIN_DATE_B = kEYIN_DATE_B;
	}

	public void setKEYIN_DATE_E(Date kEYIN_DATE_E) {
		KEYIN_DATE_E = kEYIN_DATE_E;
	}

	public String getBRANCH_AREA_NAME() {
		return BRANCH_AREA_NAME;
	}

	public void setBRANCH_AREA_NAME(String bRANCH_AREA_NAME) {
		BRANCH_AREA_NAME = bRANCH_AREA_NAME;
	}

	public String getBRANCH_NAME() {
		return BRANCH_NAME;
	}

	public void setBRANCH_NAME(String bRANCH_NAME) {
		BRANCH_NAME = bRANCH_NAME;
	}

	public String getFB_COM_YN() {
		return FB_COM_YN;
	}

	public void setFB_COM_YN(String fB_COM_YN) {
		FB_COM_YN = fB_COM_YN;
	}

	public String getCOMPANY_NUM() {
		return COMPANY_NUM;
	}

	public void setCOMPANY_NUM(String cOMPANY_NUM) {
		COMPANY_NUM = cOMPANY_NUM;
	}

}
