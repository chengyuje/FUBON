package com.systex.jbranch.app.server.fps.pms337;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 保險月底提存及月中核實報表InputVO<br>
 * Comments Name : PMS337InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年10月15日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */
public class PMS337InputVO extends PagingInputVO {

	/***** 可試範圍專用START *****/
	private String sCreDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String dataMonth; // 日期
	private String previewType; // 報表類型-暫傳空白
	private String reportDate; // YYYYMMDD(日報) YYYYMM(月報)
	private String aoFlag; // 只顯示理專
	private String psFlag; // 只顯示PS
	/***** 可試範圍專用END *****/


	private List<Map<String, Object>> list; // 儲存用list1
	private List<Map<String, Object>> list2; // 儲存用list2

	public String getDataMonth() {
		return dataMonth;
	}

	public void setDataMonth(String dataMonth) {
		this.dataMonth = dataMonth;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public List<Map<String, Object>> getList2() {
		return list2;
	}

	public void setList2(List<Map<String, Object>> list2) {
		this.list2 = list2;
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

	public void setPreviewType(String previewType) {
		this.previewType = previewType;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
	}

	public void setAoFlag(String aoFlag) {
		this.aoFlag = aoFlag;
	}

	public void setPsFlag(String psFlag) {
		this.psFlag = psFlag;
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

}
