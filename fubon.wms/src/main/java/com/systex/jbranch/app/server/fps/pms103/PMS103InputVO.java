package com.systex.jbranch.app.server.fps.pms103;

import java.util.Date;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 金流已入帳未規劃<br>
 * Comments Name : pms105InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年07月06日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS103InputVO extends PagingInputVO{
	
	private String CUST_ID;   	//客戶ID
	private String PROD_TYPE; 	//商品類型
	private String TYPE;      	//狀態
	private String SEQ;       	//流水編號
	private String sTime;     	//年月
	private String ao_code;    	//AO_CODE
	private String branch_nbr; 	//分行
	private	Date goalDateS;
	private	Date goalDateE;
	private Map actionType;
	
	public String getsTime() {
		return sTime;
	}
	
	public void setsTime(String sTime) {
		this.sTime = sTime;
	}
	
	public String getSEQ() {
		return SEQ;
	}
	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public String getPROD_TYPE() {
		return PROD_TYPE;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public void setPROD_TYPE(String pROD_TYPE) {
		PROD_TYPE = pROD_TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}

	public Date getGoalDateS() {
		return goalDateS;
	}

	public void setGoalDateS(Date goalDateS) {
		this.goalDateS = goalDateS;
	}

	public Date getGoalDateE() {
		return goalDateE;
	}

	public void setGoalDateE(Date goalDateE) {
		this.goalDateE = goalDateE;
	}

	public Map getActionType() {
		return actionType;
	}

	public void setActionType(Map actionType) {
		this.actionType = actionType;
	}
	
}
