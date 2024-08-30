package com.systex.jbranch.app.server.fps.pms000;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 共用<br>
 * Comments Name : pms000InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年12月29日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS000InputVO extends PagingInputVO {
	
    private String rptYearMon;          // 可視範圍年月 
    private String fileName;            // 檔案名稱
	private String CUST_ID;             // 客戶ID
	private String CUST_NAME;           // 客戶姓名
	private String TYPE;                // 狀態
	private String dataMonth;           // 日期
	private String rc_id;               // 業務處
	private String op_id;               // 營運區
	private String br_id;               // 分行
	private String emp_id;              // 員工
	private Date sCreDate;              // 起始日期
	private Date eCreDate;              // 結束日期
	private List<Map<String, Object>> list;
	private List<Map<String, Object>> list2;
	private int month;             
	
	//新架構傳入參數
	private String previewType;         //報表類型-暫傳空白
	private String reportDate;          //YYYYMMDD(日報) YYYYMM(月報)
	private String aoFlag;              //只顯示理專
	private String psFlag;              //只顯示PS
	
	//新參數.檢視單一員工歷史組織
	private String empHistFlag;              //只顯示員工歷史資料
		
	//2017/06/27   	start給獎勵金用               只抓消金本身資訊     範例  297470 ps 登入  只看到自己本身                           
	private String psFlagType;
	//2017/06/27   	end給獎勵金用               只抓消金本身資訊     範例  297470 ps 登入  只看到自己本身              
	
	
	
	public String getEmpHistFlag() {
		return empHistFlag;
	}
	public void setEmpHistFlag(String empHistFlag) {
		this.empHistFlag = empHistFlag;
	}
	public String getCUST_ID() {
		return CUST_ID;
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


	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public String getOp_id() {
		return op_id;
	}

	public String getBr_id() {
		return br_id;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setOp_id(String op_id) {
		this.op_id = op_id;
	}

	public void setBr_id(String br_id) {
		this.br_id = br_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getRptYearMon() {
		return rptYearMon;
	}

	public String getFileName() {
		return fileName;
	}

	public String getRc_id() {
		return rc_id;
	}

	public Date getsCreDate() {
		return sCreDate;
	}

	public Date geteCreDate() {
		return eCreDate;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public List<Map<String, Object>> getList2() {
		return list2;
	}
	
	public int getMonth() {
		return month;
	}
	
	public void setRptYearMon(String rptYearMon) {
		this.rptYearMon = rptYearMon;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setRc_id(String rc_id) {
		this.rc_id = rc_id;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public void seteCreDate(Date eCreDate) {
		this.eCreDate = eCreDate;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public void setList2(List<Map<String, Object>> list2) {
		this.list2 = list2;
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
	public String getPsFlagType() {
		return psFlagType;
	}
	public void setPsFlagType(String psFlagType) {
		this.psFlagType = psFlagType;
	}
	public void setMonth(int month) {
		this.month = month;
	}

	
}
