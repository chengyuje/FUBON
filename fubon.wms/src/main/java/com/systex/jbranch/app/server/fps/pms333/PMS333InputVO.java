package com.systex.jbranch.app.server.fps.pms333;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 未掛Code客戶報表InputVO<br>
 * Comments Name : PMS333InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年09月29日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS333InputVO extends PagingInputVO {

	private String sCreDate;
	private String ao_code; // 理專
	private String branch_nbr; // 分行
	private String region_center_id; // 區域中心
	private String branch_area_id; // 營運區
	private String EMP_ID; // 員編
	private String YEARMON; // 年月
	private String dataMonth;
	private String CUST_DEGREE; // 客戶等級
	private String VIP_DEGREE;
	private String CUST_ID; // 客戶ID
	private String IND;
	private String branch;
	
	//新架構傳入參數
	private String previewType;         //報表類型-暫傳空白
	private String reportDate;          //YYYYMMDD(日報) YYYYMM(月報)
	private String aoFlag;              //只顯示理專
	private String psFlag;              //只顯示PS
	
	//新參數.檢視單一員工歷史組織
	private String empHistFlag;              //只顯示員工歷史資料


	public String getPreviewType() {
		return previewType;
	}

	public void setPreviewType(String previewType) {
		this.previewType = previewType;
	}

	public String getReportDate() {
		return reportDate;
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

	public String getEmpHistFlag() {
		return empHistFlag;
	}

	public void setEmpHistFlag(String empHistFlag) {
		this.empHistFlag = empHistFlag;
	}

	public String getIND() {
		return IND;
	}

	public void setIND(String iND) {
		IND = iND;
	}

	public String getCUST_DEGREE() {
		return CUST_DEGREE;
	}

	public String getVIP_DEGREE() {
		return VIP_DEGREE;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_DEGREE(String cUST_DEGREE) {
		CUST_DEGREE = cUST_DEGREE;
	}

	public void setVIP_DEGREE(String vIP_DEGREE) {
		VIP_DEGREE = vIP_DEGREE;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public String getYEARMON() {
		return YEARMON;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public void setYEARMON(String yEARMON) {
		YEARMON = yEARMON;
	}

	public String getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
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

	public String getDataMonth() {
		return dataMonth;
	}

	public void setDataMonth(String dataMonth) {
		this.dataMonth = dataMonth;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}
}
