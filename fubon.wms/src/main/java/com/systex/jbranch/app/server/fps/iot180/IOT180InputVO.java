package com.systex.jbranch.app.server.fps.iot180;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT180InputVO extends PagingInputVO{	
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String ReportType;
	private String insPrdType;
	private String policyNo1;
	private String policyNo2;
	private String policyNo3;
	private String payType;
	private String fstPayWay;
	private String aocode;
	private String policyStatus;
	private String invInsId;
	private String insID;
	private String recruitID;
	private String docStatus;
	private String receiver;
	private String custID;
	private Date sCreDate;
	private Date eCreDate;
	private String insuredID;
	private String insPrdID;
	private BigDecimal ins_keyno;
	private String estIncomeLowLimit; //預估  收益金額
	private String estIncomeUpLimit;
	private String actIncomeLowLimit; //實際 收益金額
	private String actIncomeUpLimit;
	private String WorkYear; //保險工作年
	private String WorkMonth; //保險工作月
	private String progress; //人壽受理進度
	private String sWorkDate; //計績工作年月
	private String eWorkDate;
	private Date sApplyDate; //要保書填寫日期
	private Date eApplyDate;
	private Date sCloseDate; //結案日區間
	private Date eCloseDate;
	private List<Map<String,Object>> resultList;
	private String detailType;
	private String cls;
	private String uEmpID;
	private String FB_COM_YN;
	private String COMPANY_NUM;
	private String prematchSeq;
	
	public String getuEmpID() {
		return uEmpID;
	}
	public void setuEmpID(String uEmpID) {
		this.uEmpID = uEmpID;
	}
	public String getCls() {
		return cls;
	}
	public void setCls(String cls) {
		this.cls = cls;
	}
	public String getDetailType() {
		return detailType;
	}
	public void setDetailType(String detailType) {
		this.detailType = detailType;
	}
	public String getReportType() {
		return ReportType;
	}
	public void setReportType(String reportType) {
		ReportType = reportType;
	}	
	public List<Map<String, Object>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
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
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getInsPrdType() {
		return insPrdType;
	}
	public void setInsPrdType(String insPrdType) {
		this.insPrdType = insPrdType;
	}
	public String getPolicyNo1() {
		return policyNo1;
	}
	public void setPolicyNo1(String policyNo1) {
		this.policyNo1 = policyNo1;
	}
	public String getPolicyNo2() {
		return policyNo2;
	}
	public void setPolicyNo2(String policyNo2) {
		this.policyNo2 = policyNo2;
	}
	public String getPolicyNo3() {
		return policyNo3;
	}
	public void setPolicyNo3(String policyNo3) {
		this.policyNo3 = policyNo3;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getFstPayWay() {
		return fstPayWay;
	}
	public void setFstPayWay(String fstPayWay) {
		this.fstPayWay = fstPayWay;
	}
	public String getAocode() {
		return aocode;
	}
	public void setAocode(String aocode) {
		this.aocode = aocode;
	}
	public String getPolicyStatus() {
		return policyStatus;
	}
	public void setPolicyStatus(String policyStatus) {
		this.policyStatus = policyStatus;
	}
	public String getInvInsId() {
		return invInsId;
	}
	public void setInvInsId(String invInsId) {
		this.invInsId = invInsId;
	}
	public String getInsID() {
		return insID;
	}
	public void setInsID(String insID) {
		this.insID = insID;
	}
	public String getRecruitID() {
		return recruitID;
	}
	public void setRecruitID(String recruitID) {
		this.recruitID = recruitID;
	}
	public String getDocStatus() {
		return docStatus;
	}
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public Date getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}
	public Date geteCreDate() {
		return eCreDate;
	}
	public void seteCreDate(Date eCreDate) {
		this.eCreDate = eCreDate;
	}
	public String getInsuredID() {
		return insuredID;
	}
	public void setInsuredID(String insuredID) {
		this.insuredID = insuredID;
	}
	public String getInsPrdID() {
		return insPrdID;
	}
	public void setInsPrdID(String insPrdID) {
		this.insPrdID = insPrdID;
	}
	public BigDecimal getIns_keyno() {
		return ins_keyno;
	}
	public void setIns_keyno(BigDecimal ins_keyno) {
		this.ins_keyno = ins_keyno;
	}
	public String getEstIncomeLowLimit() {
		return estIncomeLowLimit;
	}
	public void setEstIncomeLowLimit(String estIncomeLowLimit) {
		this.estIncomeLowLimit = estIncomeLowLimit;
	}
	public String getEstIncomeUpLimit() {
		return estIncomeUpLimit;
	}
	public void setEstIncomeUpLimit(String estIncomeUpLimit) {
		this.estIncomeUpLimit = estIncomeUpLimit;
	}
	public String getActIncomeLowLimit() {
		return actIncomeLowLimit;
	}
	public void setActIncomeLowLimit(String actIncomeLowLimit) {
		this.actIncomeLowLimit = actIncomeLowLimit;
	}
	public String getActIncomeUpLimit() {
		return actIncomeUpLimit;
	}
	public void setActIncomeUpLimit(String actIncomeUpLimit) {
		this.actIncomeUpLimit = actIncomeUpLimit;
	}
	public String getWorkYear() {
		return WorkYear;
	}
	public void setWorkYear(String workYear) {
		WorkYear = workYear;
	}
	public String getWorkMonth() {
		return WorkMonth;
	}
	public void setWorkMonth(String workMonth) {
		WorkMonth = workMonth;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public String getsWorkDate() {
		return sWorkDate;
	}
	public void setsWorkDate(String sWorkDate) {
		this.sWorkDate = sWorkDate;
	}
	public String geteWorkDate() {
		return eWorkDate;
	}
	public void seteWorkDate(String eWorkDate) {
		this.eWorkDate = eWorkDate;
	}
	public Date getsApplyDate() {
		return sApplyDate;
	}
	public void setsApplyDate(Date sApplyDate) {
		this.sApplyDate = sApplyDate;
	}
	public Date geteApplyDate() {
		return eApplyDate;
	}
	public void seteApplyDate(Date eApplyDate) {
		this.eApplyDate = eApplyDate;
	}
	public Date getsCloseDate() {
		return sCloseDate;
	}
	public void setsCloseDate(Date sCloseDate) {
		this.sCloseDate = sCloseDate;
	}
	public Date geteCloseDate() {
		return eCloseDate;
	}
	public void seteCloseDate(Date eCloseDate) {
		this.eCloseDate = eCloseDate;
	}
	public String getFB_COM_YN() {
		return FB_COM_YN;
	}
	public void setFB_COM_YN(String fB_COM_YN) {
		FB_COM_YN = fB_COM_YN;
	}
	public String getCOMPANY_NUM() {
		return COMPANY_NUM;
	}
	public void setCOMPANY_NUM(String cOMPANY_NUM) {
		COMPANY_NUM = cOMPANY_NUM;
	}
	public String getPrematchSeq() {
		return prematchSeq;
	}
	public void setPrematchSeq(String prematchSeq) {
		this.prematchSeq = prematchSeq;
	}
	
	
}
