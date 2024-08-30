package com.systex.jbranch.app.server.fps.pms204;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 主管評估頻率維護<br>
 * Comments Name : pms204InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月27日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS204InputVO extends PagingInputVO{
	private String id;
	private String MAIN_COM_NBR;
	private String REL_COM_NBR;
	private String eTime;
	private String SEQ;
	private String MTD_ACH_RATE_S;
	private String MTD_ACH_RATE_E;
	private String MTD_ACH_RATE_SO;
	private String MTD_ACH_RATE_EO;
	private String JOB_TITLE_ID;
	private String COACHING_POINT_A;
	private String COACHING_POINT_B;
	private String APOINT;
	private String BPOINT;
	private String CPOINT;
	private String DPOINT;
	private String COACHING_POINT_C;
	private String COACHING_POINT_D;
	private String COACHING_STATE;
	private String PLAN_YEARMON;
	private String ADD_MONTH;
	
	private String OLD_PLAN_YEARMON;
	private String OLD_JOB_TITLE_ID ;
	private String OLD_MTD_ACH_RATE_SO ;
	private String OLD_MTD_ACH_RATE_EO;
	
	public String getADD_MONTH() {
		return ADD_MONTH;
	}
	public void setADD_MONTH(String aDD_MONTH) {
		ADD_MONTH = aDD_MONTH;
	}
	public String getOLD_PLAN_YEARMON() {
		return OLD_PLAN_YEARMON;
	}
	public void setOLD_PLAN_YEARMON(String oLD_PLAN_YEARMON) {
		OLD_PLAN_YEARMON = oLD_PLAN_YEARMON;
	}
	public String getOLD_JOB_TITLE_ID() {
		return OLD_JOB_TITLE_ID;
	}
	public void setOLD_JOB_TITLE_ID(String oLD_JOB_TITLE_ID) {
		OLD_JOB_TITLE_ID = oLD_JOB_TITLE_ID;
	}
	public String getOLD_MTD_ACH_RATE_SO() {
		return OLD_MTD_ACH_RATE_SO;
	}
	public void setOLD_MTD_ACH_RATE_SO(String oLD_MTD_ACH_RATE_SO) {
		OLD_MTD_ACH_RATE_SO = oLD_MTD_ACH_RATE_SO;
	}
	public String getOLD_MTD_ACH_RATE_EO() {
		return OLD_MTD_ACH_RATE_EO;
	}
	public void setOLD_MTD_ACH_RATE_EO(String oLD_MTD_ACH_RATE_EO) {
		OLD_MTD_ACH_RATE_EO = oLD_MTD_ACH_RATE_EO;
	}
	
	
	
	public String getCOACHING_STATE() {
		return COACHING_STATE;
	}
	public void setCOACHING_STATE(String cOACHING_STATE) {
		COACHING_STATE = cOACHING_STATE;
	}
	public String getId() {
		return id;
	}
	public String getMAIN_COM_NBR() {
		return MAIN_COM_NBR;
	}
	public String getREL_COM_NBR() {
		return REL_COM_NBR;
	}
	public String geteTime() {
		return eTime;
	}
	public String getSEQ() {
		return SEQ;
	}
	public String getMTD_ACH_RATE_S() {
		return MTD_ACH_RATE_S;
	}
	public String getMTD_ACH_RATE_E() {
		return MTD_ACH_RATE_E;
	}
	public String getJOB_TITLE_ID() {
		return JOB_TITLE_ID;
	}
	public String getCOACHING_POINT_A() {
		return COACHING_POINT_A;
	}
	public String getCOACHING_POINT_B() {
		return COACHING_POINT_B;
	}
	public String getCOACHING_POINT_C() {
		return COACHING_POINT_C;
	}
	public String getCOACHING_POINT_D() {
		return COACHING_POINT_D;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setMAIN_COM_NBR(String mAIN_COM_NBR) {
		MAIN_COM_NBR = mAIN_COM_NBR;
	}
	public void setREL_COM_NBR(String rEL_COM_NBR) {
		REL_COM_NBR = rEL_COM_NBR;
	}
	public void seteTime(String eTime) {
		this.eTime = eTime;
	}
	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}
	public void setMTD_ACH_RATE_S(String mTD_ACH_RATE_S) {
		MTD_ACH_RATE_S = mTD_ACH_RATE_S;
	}
	public void setMTD_ACH_RATE_E(String mTD_ACH_RATE_E) {
		MTD_ACH_RATE_E = mTD_ACH_RATE_E;
	}
	public void setJOB_TITLE_ID(String jOB_TITLE_ID) {
		JOB_TITLE_ID = jOB_TITLE_ID;
	}
	public void setCOACHING_POINT_A(String cOACHING_POINT_A) {
		COACHING_POINT_A = cOACHING_POINT_A;
	}
	public void setCOACHING_POINT_B(String cOACHING_POINT_B) {
		COACHING_POINT_B = cOACHING_POINT_B;
	}
	public void setCOACHING_POINT_C(String cOACHING_POINT_C) {
		COACHING_POINT_C = cOACHING_POINT_C;
	}
	public void setCOACHING_POINT_D(String cOACHING_POINT_D) {
		COACHING_POINT_D = cOACHING_POINT_D;
	}
	public String getAPOINT() {
		return APOINT;
	}
	public String getBPOINT() {
		return BPOINT;
	}
	public String getCPOINT() {
		return CPOINT;
	}
	public String getPLAN_YEARMON() {
		return PLAN_YEARMON;
	}
	public void setPLAN_YEARMON(String pLAN_YEARMON) {
		PLAN_YEARMON = pLAN_YEARMON;
	}
	public String getDPOINT() {
		return DPOINT;
	}
	public void setAPOINT(String aPOINT) {
		APOINT = aPOINT;
	}
	public void setBPOINT(String bPOINT) {
		BPOINT = bPOINT;
	}
	public String getMTD_ACH_RATE_SO() {
		return MTD_ACH_RATE_SO;
	}
	public String getMTD_ACH_RATE_EO() {
		return MTD_ACH_RATE_EO;
	}
	public void setMTD_ACH_RATE_SO(String mTD_ACH_RATE_SO) {
		MTD_ACH_RATE_SO = mTD_ACH_RATE_SO;
	}
	public void setMTD_ACH_RATE_EO(String mTD_ACH_RATE_EO) {
		MTD_ACH_RATE_EO = mTD_ACH_RATE_EO;
	}
	public void setCPOINT(String cPOINT) {
		CPOINT = cPOINT;
	}
	public void setDPOINT(String dPOINT) {
		DPOINT = dPOINT;
	}
	
}
