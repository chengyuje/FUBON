package com.systex.jbranch.app.server.fps.pms110;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

@Component
@Scope("request")
public class PMS110InputVO extends PagingInputVO {

	// === key
	private String planSeq; 			// 銷售計劃序號
	private String planYearmon; 		// 銷售計劃月份
	private String region_center_id; 	// 業務處
	private String branch_area_id; 		// 營運區
	// ===
	
	private String estPrd; 				// 預計承作商品
	private String estPrdItem; 			// 預計承做商品細項
	private String estAmt; 				// 預計承作金額
	
	private String branch_nbr; 			// 分行別
	
	private String caseSource; 			// 案件來源
	private String case_num; 			// 進件編號
	private String custId; 				// 客戶 ID
	private String custName; 			// 客戶姓名
	private String meetingDate; 		// 面談日期
	private String meetingResult; 		// 面談結果
	
	private String ex_date; 			// 預計撥款月份
	private String estDrawDate;			// 預計撥款日期
	private String drawMonth; 			// 撥款月份
	private String emp_id; 				// 業務人員員編
	private String memo;				// 備註
	
	// === 2020/12 add by ocean
	private String custSource;			// 客戶來源

	private String leadVarPhone; 		// 客戶連絡電話
	private String leadVarCTime; 		// 客戶方便聯絡時間
	private String leadVarRtnEName; 	// 客服或電銷人員轉介人員姓名
	private String leadVarRtnEID; 		// 客服或電銷人員轉介人員員編
	

	private String loanCustID; 			// 借款人身份證字號
	private String loanCustName;		// 借款人姓名
	private BigDecimal loanAmt;			// 借款金額
	// ===	
	
	private String chkLoanDate;			// 對保日期
	private String refuseReason;		// 已核不撥原因
	
	private String planStatus;			// 案件狀態
	
	// 用途未明
//	private Map excelData; 				// Excel Data
	private String reportDate;
	
	// 匯出
	private List<Map<String, String>> pipelineList; 
	private List<Map<String, String>> pilelineSumList;

	public List<Map<String, String>> getPipelineList() {
		return pipelineList;
	}

	public void setPipelineList(List<Map<String, String>> pipelineList) {
		this.pipelineList = pipelineList;
	}

	public List<Map<String, String>> getPilelineSumList() {
		return pilelineSumList;
	}

	public void setPilelineSumList(List<Map<String, String>> pilelineSumList) {
		this.pilelineSumList = pilelineSumList;
	}

	public String getPlanStatus() {
		return planStatus;
	}

	public void setPlanStatus(String planStatus) {
		this.planStatus = planStatus;
	}

	public String getRefuseReason() {
		return refuseReason;
	}

	public void setRefuseReason(String refuseReason) {
		this.refuseReason = refuseReason;
	}

	public String getChkLoanDate() {
		return chkLoanDate;
	}

	public void setChkLoanDate(String chkLoanDate) {
		this.chkLoanDate = chkLoanDate;
	}

	public String getLoanCustName() {
		return loanCustName;
	}

	public void setLoanCustName(String loanCustName) {
		this.loanCustName = loanCustName;
	}

	public BigDecimal getLoanAmt() {
		return loanAmt;
	}

	public void setLoanAmt(BigDecimal loanAmt) {
		this.loanAmt = loanAmt;
	}

	public String getDrawMonth() {
		return drawMonth;
	}

	public void setDrawMonth(String drawMonth) {
		this.drawMonth = drawMonth;
	}

	public String getPlanSeq() {
		return planSeq;
	}

	public void setPlanSeq(String planSeq) {
		this.planSeq = planSeq;
	}

	public String getEstPrdItem() {
		return estPrdItem;
	}

	public void setEstPrdItem(String estPrdItem) {
		this.estPrdItem = estPrdItem;
	}

	public String getEstAmt() {
		return estAmt;
	}

	public void setEstAmt(String estAmt) {
		this.estAmt = estAmt;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getMeetingDate() {
		return meetingDate;
	}

	public void setMeetingDate(String meetingDate) {
		this.meetingDate = meetingDate;
	}

	public String getMeetingResult() {
		return meetingResult;
	}

	public void setMeetingResult(String meetingResult) {
		this.meetingResult = meetingResult;
	}

	public String getEstDrawDate() {
		return estDrawDate;
	}

	public void setEstDrawDate(String estDrawDate) {
		this.estDrawDate = estDrawDate;
	}

	public String getReportDate() {
		return reportDate;
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

	public String getEstPrd() {
		return estPrd;
	}

	public void setEstPrd(String estPrd) {
		this.estPrd = estPrd;
	}

	public String getPlanYearmon() {
		return planYearmon;
	}

	public void setPlanYearmon(String planYearmon) {
		this.planYearmon = planYearmon;
	}

	public String getCaseSource() {
		return caseSource;
	}

	public void setCaseSource(String caseSource) {
		this.caseSource = caseSource;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getCase_num() {
		return case_num;
	}

	public void setCase_num(String case_num) {
		this.case_num = case_num;
	}

	public String getLoanCustID() {
		return loanCustID;
	}

	public void setLoanCustID(String loanCustID) {
		this.loanCustID = loanCustID;
	}

	public String getCustSource() {
		return custSource;
	}

	public void setCustSource(String custSource) {
		this.custSource = custSource;
	}

	public String getLeadVarPhone() {
		return leadVarPhone;
	}

	public void setLeadVarPhone(String leadVarPhone) {
		this.leadVarPhone = leadVarPhone;
	}

	public String getLeadVarCTime() {
		return leadVarCTime;
	}

	public void setLeadVarCTime(String leadVarCTime) {
		this.leadVarCTime = leadVarCTime;
	}

	public String getLeadVarRtnEName() {
		return leadVarRtnEName;
	}

	public void setLeadVarRtnEName(String leadVarRtnEName) {
		this.leadVarRtnEName = leadVarRtnEName;
	}

	public String getLeadVarRtnEID() {
		return leadVarRtnEID;
	}

	public void setLeadVarRtnEID(String leadVarRtnEID) {
		this.leadVarRtnEID = leadVarRtnEID;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getEx_date() {
		return ex_date;
	}

	public void setEx_date(String ex_date) {
		this.ex_date = ex_date;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

}
