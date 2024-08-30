package com.systex.jbranch.app.server.fps.ins450;

import java.math.BigDecimal;
import java.sql.Blob;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS450InputVO extends PagingInputVO{
	
	private String custID;				// 客戶ID
	private String paraType;			// 參數類型
	private String currCD;				// 幣別
	private String custRiskATR;			// 客戶風險屬性
	private String status;				// 規劃狀態
	private String sppType;				// 特定規劃項目
	private BigDecimal insprdKEYNO;		// 險種主鍵
	private String sppName;				// 規劃名稱
	private BigDecimal dAMT1;			// 每月生活所需費用(退休規劃)
	private BigDecimal dAMT2;			// 需求金額(子女教育)
	private BigDecimal avgLife;			// 平均餘命
	private BigDecimal avgRetire;		// 預計退休年齡(退休規劃)
	private BigDecimal laborINSAMT1;	// 社會保險給付(每月)
	private BigDecimal laborINSAMT2;	// 社會保險給付(一次)
	private BigDecimal person1;			// 社會福利給付(每月)
	private BigDecimal person2;			// 社會福利給付(一次)
	private BigDecimal othINSAMT1;		// 商業保險給付(每月)
	private BigDecimal othINSAMT2;		// 商業保險給付(一次)
	private BigDecimal othAMT1;			// 其他給付(每月)
	private BigDecimal othAMT2;			// 其他給付(一次)
	private String insprdID;			// 險種代碼
	private String payYear;				// 繳費年期
	private BigDecimal policyAssureAMT;	// 保額
	private String unit;				// 單位
	private BigDecimal policyFEE;		// 保費
	private BigDecimal dYear;			// 需求年數
	private BigDecimal have;			// 已備資金
	private String paraNO; 				// 參數編號
	private String keyno;				// 保險主檔主鍵
	
	private String gender;
	private String age;
	private String saveType;
	
	private BigDecimal PARA_NO;
	private String PLAN_TYPE;				//保險規劃類型
	private String checkType;				
	private String sppID;
	private BigDecimal printSEQ;
//	private String commRsYn;
//	private byte[]  pdfByteArray;
	private Blob pdfBlob;
//	private String pdfStringStream;
	private String fileName;
	private String tempFileName;
	private boolean isNew;
	
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getParaType() {
		return paraType;
	}
	public void setParaType(String paraType) {
		this.paraType = paraType;
	}
	public String getCurrCD() {
		return currCD;
	}
	public void setCurrCD(String currCD) {
		this.currCD = currCD;
	}
	public String getCustRiskATR() {
		return custRiskATR;
	}
	public void setCustRiskATR(String custRiskATR) {
		this.custRiskATR = custRiskATR;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSppType() {
		return sppType;
	}
	public void setSppType(String sppType) {
		this.sppType = sppType;
	}
	public BigDecimal getInsprdKEYNO() {
		return insprdKEYNO;
	}
	public void setInsprdKEYNO(BigDecimal insprdKEYNO) {
		this.insprdKEYNO = insprdKEYNO;
	}
	public String getSppName() {
		return sppName;
	}
	public void setSppName(String sppName) {
		this.sppName = sppName;
	}
	public BigDecimal getdAMT1() {
		return dAMT1;
	}
	public void setdAMT1(BigDecimal dAMT1) {
		this.dAMT1 = dAMT1;
	}
	public BigDecimal getdAMT2() {
		return dAMT2;
	}
	public void setdAMT2(BigDecimal dAMT2) {
		this.dAMT2 = dAMT2;
	}
	public BigDecimal getAvgLife() {
		return avgLife;
	}
	public void setAvgLife(BigDecimal avgLife) {
		this.avgLife = avgLife;
	}
	public BigDecimal getAvgRetire() {
		return avgRetire;
	}
	public void setAvgRetire(BigDecimal avgRetire) {
		this.avgRetire = avgRetire;
	}
	
	/**
	 * 社會保險給付(每月)
	 * */
	public BigDecimal getLaborINSAMT1() {
		return laborINSAMT1;
	}
	
	/**
	 * 社會保險給付(每月)
	 * */
	public void setLaborINSAMT1(BigDecimal laborINSAMT1) {
		this.laborINSAMT1 = laborINSAMT1;
	}
	
	/**
	 * 社會保險給付(一次)
	 * */
	public BigDecimal getLaborINSAMT2() {
		return laborINSAMT2;
	}
	
	/**
	 * 社會保險給付(一次)
	 * */
	public void setLaborINSAMT2(BigDecimal laborINSAMT2) {
		this.laborINSAMT2 = laborINSAMT2;
	}
	
	/**
	 * 社會福利給付(每月)
	 * */
	public BigDecimal getPerson1() {
		return person1;
	}
	
	/**
	 * 社會福利給付(每月)
	 * */
	public void setPerson1(BigDecimal person1) {
		this.person1 = person1;
	}
	
	/**
	 * 社會福利給付(一次)
	 * */
	public BigDecimal getPerson2() {
		return person2;
	}
	
	/**
	 * 社會福利給付(一次)
	 * */
	public void setPerson2(BigDecimal person2) {
		this.person2 = person2;
	}
	
	/**
	 * 商業保險給付(每月)
	 * */
	public BigDecimal getOthINSAMT1() {
		return othINSAMT1;
	}
	
	/**
	 * 商業保險給付(每月)
	 * */
	public void setOthINSAMT1(BigDecimal othINSAMT1) {
		this.othINSAMT1 = othINSAMT1;
	}
	
	/**
	 * 商業保險給付(一次)
	 * */
	public BigDecimal getOthINSAMT2() {
		return othINSAMT2;
	}
	
	/**
	 * 商業保險給付(一次)
	 * */
	public void setOthINSAMT2(BigDecimal othINSAMT2) {
		this.othINSAMT2 = othINSAMT2;
	}
	
	/**
	 * 其他給付(每月)
	 * */
	public BigDecimal getOthAMT1() {
		return othAMT1;
	}
	
	/**
	 * 其他給付(每月)
	 * */
	public void setOthAMT1(BigDecimal othAMT1) {
		this.othAMT1 = othAMT1;
	}
	
	/**
	 * 其他給付(一次)
	 * */
	public BigDecimal getOthAMT2() {
		return othAMT2;
	}
	
	/**
	 * 其他給付(一次)
	 * */
	public void setOthAMT2(BigDecimal othAMT2) {
		this.othAMT2 = othAMT2;
	}
	public String getInsprdID() {
		return insprdID;
	}
	public void setInsprdID(String insprdID) {
		this.insprdID = insprdID;
	}
	public String getPayYear() {
		return payYear;
	}
	public void setPayYear(String payYear) {
		this.payYear = payYear;
	}
	public BigDecimal getPolicyAssureAMT() {
		return policyAssureAMT;
	}
	public void setPolicyAssureAMT(BigDecimal policyAssureAMT) {
		this.policyAssureAMT = policyAssureAMT;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public BigDecimal getPolicyFEE() {
		return policyFEE;
	}
	public void setPolicyFEE(BigDecimal policyFEE) {
		this.policyFEE = policyFEE;
	}
	public BigDecimal getdYear() {
		return dYear;
	}
	public void setdYear(BigDecimal dYear) {
		this.dYear = dYear;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getSaveType() {
		return saveType;
	}
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
	public BigDecimal getHave() {
		return have;
	}
	public void setHave(BigDecimal have) {
		this.have = have;
	}
	public String getParaNO() {
		return paraNO;
	}
	public void setParaNO(String paraNO) {
		this.paraNO = paraNO;
	}
	public String getKeyno() {
		return keyno;
	}
	public void setKeyno(String keyno) {
		this.keyno = keyno;
	}
	public BigDecimal getPARA_NO() {
		return PARA_NO;
	}
	public void setPARA_NO(BigDecimal pARA_NO) {
		PARA_NO = pARA_NO;
	}
	public String getPLAN_TYPE() {
		return PLAN_TYPE;
	}
	public void setPLAN_TYPE(String pLAN_TYPE) {
		PLAN_TYPE = pLAN_TYPE;
	}
	public String getCheckType() {
		return checkType;
	}
	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}
	public String getSppID() {
		return sppID;
	}
	public void setSppID(String sppID) {
		this.sppID = sppID;
	}
	public BigDecimal getPrintSEQ() {
		return printSEQ;
	}
	public void setPrintSEQ(BigDecimal printSEQ) {
		this.printSEQ = printSEQ;
	}
	public Blob getPdfBlob() {
		return pdfBlob;
	}
	public void setPdfBlob(Blob pdfBlob) {
		this.pdfBlob = pdfBlob;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getTempFileName() {
		return tempFileName;
	}
	public void setTempFileName(String tempFileName) {
		this.tempFileName = tempFileName;
	}
	public boolean isNew() {
		return isNew;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}	
}
