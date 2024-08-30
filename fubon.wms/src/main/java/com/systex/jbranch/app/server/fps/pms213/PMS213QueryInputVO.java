package com.systex.jbranch.app.server.fps.pms213;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS213QueryInputVO.java<br>
 * Author :WKK<br>
 * Date :2016年12月5日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2016年12月5日<br>
 */
public class PMS213QueryInputVO extends PagingInputVO
{
	private String userId;
	private String custId;
	private String AOCODE;
	private String adjType;
	private String yearMon;
	private String prodCode;
	private String adjDesc;
	
	/***** 可試範圍專用START *****/
	private Date sCreDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String dataMonth; // 日期
	private String previewType; // 報表類型-暫傳空白
	private String reportDate; // YYYYMMDD(日報) YYYYMM(月報)
	private String aoFlag; // 只顯示理專
	private String psFlag; // 只顯示PS
	private String emp_id;              // 員工
	/***** 可試範圍專用END *****/
	
	/*****20170619 新增產品類別 *****/
	private String PRODTYPE;    //產品類別
	
	
	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public String getCustId()
	{
		return custId;
	}
	public void setCustId(String custId)
	{
		this.custId = custId;
	}
	public String getAOCODE()
	{
		return AOCODE;
	}
	public void setAOCODE(String aOCODE)
	{
		AOCODE = aOCODE;
	}
	public String getAdjType()
	{
		return adjType;
	}
	public void setAdjType(String adjType)
	{
		this.adjType = adjType;
	}
	public String getYearMon()
	{
		return yearMon;
	}
	public void setYearMon(String yearMon)
	{
		this.yearMon = yearMon;
	}
	public String getProdCode()
	{
		return prodCode;
	}
	public void setProdCode(String prodCode)
	{
		this.prodCode = prodCode;
	}
	public String getAdjDesc()
	{
		return adjDesc;
	}
	public void setAdjDesc(String adjDesc)
	{
		this.adjDesc = adjDesc;
	}
	public Date getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(Date sCreDate) {
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
	public String getDataMonth() {
		return dataMonth;
	}
	public void setDataMonth(String dataMonth) {
		this.dataMonth = dataMonth;
	}
	public String getPreviewType() {
		return previewType;
	}
	public void setPreviewType(String previewType) {
		this.previewType = previewType;
	}
	public String getReportDate() {
		return reportDate;
	}
	public String getPRODTYPE() {
		return PRODTYPE;
	}
	public void setPRODTYPE(String pRODTYPE) {
		PRODTYPE = pRODTYPE;
	}
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	public String getAoFlag() {
		return aoFlag;
	}
	public void setAoFlag(String aoFlag) {
		this.aoFlag = aoFlag;
	}
	public String getPsFlag() {
		return psFlag;
	}
	public void setPsFlag(String psFlag) {
		this.psFlag = psFlag;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	
}
