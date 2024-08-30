package com.systex.jbranch.app.server.fps.pms315;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 保單續期保費名單InputVO<br>
 * Comments Name : PMS315InputVO.java<br>
 * Author :Miler<br>
 * Date :2017年08月21日 <br>
 * Version : 1.01 <br>
 * Editor : Miler<br>
 * Editor Date : 2017年08月21日<br>
 */
public class PMS315InputVO extends PagingInputVO{
	private String sCreDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String descr254;
	private String seq;
	
	private String CUST_ID;        //要保人ID
	private String CUST_NAME;     //要保人姓名
	private String POLICY_NO;      //保單號碼
	private String POLICY_SEQ;     //保單序號
	private String PAID_STAT;     //繳費狀態
	private Date PAID_DATES;     //應繳日期起
	private Date PAID_DATEE;     //應繳日期訖
	private Date FST_ACT_DTS;    //預計第一次扣款日起
	private Date FST_ACT_DTE;    //預計第一次扣款日迄
	private String MOP_T;          //繳別
	
	
	
	private List<Map<String, Object>> list;
	
	public List<Map<String, Object>> getList() {
		return list;
	}
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getDescr254() {
		return descr254;
	}
	public void setDescr254(String descr254) {
		this.descr254 = descr254;
	}
	public String getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
	}
	public String getRegion_center_id() {
		return region_center_id;
	}
	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}
	public String getBranch_area_id() {
		return branch_area_id;
	}
	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}
	public String getBranch_nbr() {
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getCUST_ID() {
		return CUST_ID;
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
	public String getPOLICY_NO() {
		return POLICY_NO;
	}
	public void setPOLICY_NO(String pOLICY_NO) {
		POLICY_NO = pOLICY_NO;
	}
	public String getPOLICY_SEQ() {
		return POLICY_SEQ;
	}
	public void setPOLICY_SEQ(String pOLICY_SEQ) {
		POLICY_SEQ = pOLICY_SEQ;
	}
	public String getPAID_STAT() {
		return PAID_STAT;
	}
	public void setPAID_STAT(String pAID_STAT) {
		PAID_STAT = pAID_STAT;
	}
	public Date getPAID_DATES() {
		return PAID_DATES;
	}
	public void setPAID_DATES(Date pAID_DATES) {
		PAID_DATES = pAID_DATES;
	}
	public Date getPAID_DATEE() {
		return PAID_DATEE;
	}
	public void setPAID_DATEE(Date pAID_DATEE) {
		PAID_DATEE = pAID_DATEE;
	}
	public Date getFST_ACT_DTS() {
		return FST_ACT_DTS;
	}
	public void setFST_ACT_DTS(Date fST_ACT_DTS) {
		FST_ACT_DTS = fST_ACT_DTS;
	}
	public Date getFST_ACT_DTE() {
		return FST_ACT_DTE;
	}
	public void setFST_ACT_DTE(Date fST_ACT_DTE) {
		FST_ACT_DTE = fST_ACT_DTE;
	}
	public String getMOP_T() {
		return MOP_T;
	}
	public void setMOP_T(String mOP_T) {
		MOP_T = mOP_T;
	}
	
}