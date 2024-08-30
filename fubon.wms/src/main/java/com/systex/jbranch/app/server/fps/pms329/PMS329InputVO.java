package com.systex.jbranch.app.server.fps.pms329;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :AUM InputVO<br>
 * Comments Name : PMS328InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年05月31日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS329InputVO extends PagingInputVO {

	private String eTimes; 			//結束年月
	private String sTimes; 			//起始年月
	private String aocode;			//理專
	private String branch; 			//分行
	private String region; 			//區域中心
	private String op; 				//營運區

	/***** 可試範圍專用START *****/
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String emp_id;
	private String dataMonth; 		// 日期
	private String previewType; 	// 報表類型-暫傳空白
	private String reportDate; 		// YYYYMMDD(日報) YYYYMM(月報)
	private String aoFlag; 			// 只顯示理專
	private String psFlag; 			// 只顯示PS
	/***** 可試範圍專用END *****/

	private String funcPage;
	
	private String NOT_EXIST_UHRM;

	public String getNOT_EXIST_UHRM() {
		return NOT_EXIST_UHRM;
	}

	public void setNOT_EXIST_UHRM(String nOT_EXIST_UHRM) {
		NOT_EXIST_UHRM = nOT_EXIST_UHRM;
	}

	public String getFuncPage() {
		return funcPage;
	}

	public void setFuncPage(String funcPage) {
		this.funcPage = funcPage;
	}

	public String getAocode() {
		return aocode;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getBranch() {
		return branch;
	}

	public String getRegion() {
		return region;
	}

	public String getOp() {
		return op;
	}

	public void setAocode(String aocode) {
		this.aocode = aocode;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String geteTimes() {
		return eTimes;
	}

	public String getsTimes() {
		return sTimes;
	}

	public void seteTimes(String eTimes) {
		this.eTimes = eTimes;
	}

	public void setsTimes(String sTimes) {
		this.sTimes = sTimes;
	}

	public String getRegion_center_id() {
		return region_center_id;
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

	public String getDataMonth() {
		return dataMonth;
	}

	public String getPreviewType() {
		return previewType;
	}

	public String getReportDate() {
		return reportDate;
	}

	public String getAoFlag() {
		return aoFlag;
	}

	public String getPsFlag() {
		return psFlag;
	}

	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
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

	public void setDataMonth(String dataMonth) {
		this.dataMonth = dataMonth;
	}

	public void setPreviewType(String previewType) {
		this.previewType = previewType;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public void setAoFlag(String aoFlag) {
		this.aoFlag = aoFlag;
	}

	public void setPsFlag(String psFlag) {
		this.psFlag = psFlag;
	}
}
