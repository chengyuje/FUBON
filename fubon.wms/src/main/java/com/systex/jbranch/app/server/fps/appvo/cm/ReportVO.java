package com.systex.jbranch.app.server.fps.appvo.cm;

import com.systex.jbranch.app.server.fps.appvo.fp.FundReportVO;
import com.systex.jbranch.app.server.fps.appvo.ins.InsReportVO;


public class ReportVO {
	private String custID; 				//客戶ID
	private String custName; 			//客戶姓名(即要保人姓名)
	private String custRiskAttr; 		//客戶風險屬性代碼
	private String riskAttrName; 		//風險屬性名稱
	private String reportType; 			//產出報表選項{1: 基金規劃建議書,2: 保險規劃建議書(含DM),3: 保險規劃建議書(不含DM)}
	private String reportDate; 			//報表產生日期
	private String printer; 			//列印人員
	private String xml200P;				//基金建議書是否可列印
	private String xml200F;				//基金建議書是否可存檔
	private String xml300P;				//保險建議書是否可列印
	private String xml300F;				//保險建議書是否可存檔
	private FundReportVO fundReportVO; 	//基金規劃書會用到的資料VO
	private InsReportVO insReportVO; 	//保險規劃書會用到的資料VO
	
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustRiskAttr() {
		return custRiskAttr;
	}
	public void setCustRiskAttr(String custRiskAttr) {
		this.custRiskAttr = custRiskAttr;
	}
	public String getRiskAttrName() {
		return riskAttrName;
	}
	public void setRiskAttrName(String riskAttrName) {
		this.riskAttrName = riskAttrName;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getReportDate() {
		return reportDate;
	}
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	public String getXml200P() {
		return xml200P;
	}
	public void setXml200P(String xml200P) {
		this.xml200P = xml200P;
	}
	public String getXml200F() {
		return xml200F;
	}
	public void setXml200F(String xml200F) {
		this.xml200F = xml200F;
	}
	public String getXml300P() {
		return xml300P;
	}
	public void setXml300P(String xml300P) {
		this.xml300P = xml300P;
	}
	public String getXml300F() {
		return xml300F;
	}
	public void setXml300F(String xml300F) {
		this.xml300F = xml300F;
	}
	public String getPrinter() {
		return printer;
	}
	public void setPrinter(String printer) {
		this.printer = printer;
	}
	public FundReportVO getFundReportVO() {
		return fundReportVO;
	}
	public void setFundReportVO(FundReportVO fundReportVO) {
		this.fundReportVO = fundReportVO;
	}
	public InsReportVO getInsReportVO() {
		return insReportVO;
	}
	public void setInsReportVO(InsReportVO insReportVO) {
		this.insReportVO = insReportVO;
	}

}
