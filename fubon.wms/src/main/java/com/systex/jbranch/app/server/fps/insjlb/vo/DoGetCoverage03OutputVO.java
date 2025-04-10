package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;

@SuppressWarnings("rawtypes")
public class DoGetCoverage03OutputVO {
	private List lstPolicyDetail;	// 保單明細資訊
	private List lstPolicyMaster;	// 保單主檔
	private List lstWholeLife;		// 保障領回中間檔
	private List lstExpression;		// 給付說明中間檔
	private List lstPremPerMonth;	// 逐月保費中間檔
	private List lstPremDetail;		// 保費明細中間檔
	private List lstNewReportExpression;// 給付編號流水檔
	private List lstTmpExpression;	// 檢視總表合計用
	private List lstAllPolicyObj;	// 
	private List lstWholeLifeDtl;	// 客戶姓名+流水號多筆
	
	private List lstLogTable;		// 錯誤資訊
	private List lstPayYear;		// 主附約繳費年期

	public List getLstPolicyDetail() {
		return lstPolicyDetail;
	}
	public void setLstPolicyDetail(List lstPolicyDetail) {
		this.lstPolicyDetail = lstPolicyDetail;
	}
	public List getLstPolicyMaster() {
		return lstPolicyMaster;
	}
	public void setLstPolicyMaster(List lstPolicyMaster) {
		this.lstPolicyMaster = lstPolicyMaster;
	}
	public List getLstWholeLife() {
		return lstWholeLife;
	}
	public void setLstWholeLife(List lstWholeLife) {
		this.lstWholeLife = lstWholeLife;
	}
	public List getLstExpression() {
		return lstExpression;
	}
	public void setLstExpression(List lstExpression) {
		this.lstExpression = lstExpression;
	}
	public List getLstPremPerMonth() {
		return lstPremPerMonth;
	}
	public void setLstPremPerMonth(List lstPremPerMonth) {
		this.lstPremPerMonth = lstPremPerMonth;
	}
	public List getLstPremDetail() {
		return lstPremDetail;
	}
	public void setLstPremDetail(List lstPremDetail) {
		this.lstPremDetail = lstPremDetail;
	}
	public List getLstNewReportExpression() {
		return lstNewReportExpression;
	}
	public void setLstNewReportExpression(List lstNewReportExpression) {
		this.lstNewReportExpression = lstNewReportExpression;
	}
	public List getLstTmpExpression() {
		return lstTmpExpression;
	}
	public void setLstTmpExpression(List lstTmpExpression) {
		this.lstTmpExpression = lstTmpExpression;
	}
	public List getLstAllPolicyObj() {
		return lstAllPolicyObj;
	}
	public void setLstAllPolicyObj(List lstAllPolicyObj) {
		this.lstAllPolicyObj = lstAllPolicyObj;
	}
	public List getLstWholeLifeDtl() {
		return lstWholeLifeDtl;
	}
	public void setLstWholeLifeDtl(List lstWholeLifeDtl) {
		this.lstWholeLifeDtl = lstWholeLifeDtl;
	}
	public List getLstLogTable() {
		return lstLogTable;
	}
	public void setLstLogTable(List lstLogTable) {
		this.lstLogTable = lstLogTable;
	}
	public List getLstPayYear() {
		return lstPayYear;
	}
	public void setLstPayYear(List lstPayYear) {
		this.lstPayYear = lstPayYear;
	}
}
