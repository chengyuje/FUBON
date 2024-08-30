package com.systex.jbranch.app.server.fps.pms101;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 金流名單<br>
 * Comments Name : pms101InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月17日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS101InputVO extends PagingInputVO {

	private String CUST_ID; // 客戶ID
	private String CUST_NAME; // 客戶姓名
	private String PROD_TYPE; // 商品類型
	private String TYPE; // 狀態
	private String sCreDate; // 日期
	private String FLAG; // 首頁年月FLAG
	private String branch_area_id; // 營運區
	private String branch_nbr; // 分行
	private String ao_code; // 理專
	private String DATA_DATE; //明細日期

	public String getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
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

	public String getCUST_NAME() {
		return CUST_NAME;
	}

	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}

	public void setPROD_TYPE(String pROD_TYPE) {
		PROD_TYPE = pROD_TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public String getBranch_area_id() {
		return branch_area_id;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public String getAo_code() {
		return ao_code;
	}

	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getFLAG() {
		return FLAG;
	}

	public void setFLAG(String fLAG) {
		FLAG = fLAG;
	}

	public String getDATA_DATE() {
		return DATA_DATE;
	}

	public void setDATA_DATE(String dATA_DATE) {
		DATA_DATE = dATA_DATE;
	}

}
