package com.systex.jbranch.app.server.fps.pms353;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS353InputVO extends PagingInputVO {

	private String CUST_ID; // 客戶ID
	private String PROD_TYPE; // 商品類型
	private String TYPE; // 狀態
	private BigDecimal seq; // pms353 sql序號
	private Date sCreDate;	//主查詢起日
	private Date eCreDate;	//主查詢訖
	private String valid;
	private String rptName;
	private String rptExplain;
	private String marqueeFlag;
	private String marqueeTxt;
	private String fileName;
	private String realFileName;
	private String roles;	//角色
	private String uploadFlag;	//上傳flag Y/N
	private Date eDate; // 訖日
	private Date sDate; // 起日
	private String PRJID; // 正確序號
	private String PRJ_SEQ; // 專案編號
	private String PRD_ID; // 序號
	private String PRJ_NAME; // 專案名稱
	private String BT_DT; // 更新日期
	private String BRANCH_NBR; // 分行

	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}

	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public String getPROD_TYPE() {
		return PROD_TYPE;
	}

	public String getTYPE() {
		return TYPE;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public void setPROD_TYPE(String pROD_TYPE) {
		PROD_TYPE = pROD_TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public BigDecimal getSeq() {
		return seq;
	}

	public Date getsCreDate() {
		return sCreDate;
	}

	public Date geteCreDate() {
		return eCreDate;
	}

	public String getValid() {
		return valid;
	}

	public String getRptName() {
		return rptName;
	}

	public String getRptExplain() {
		return rptExplain;
	}

	public String getMarqueeFlag() {
		return marqueeFlag;
	}

	public String getMarqueeTxt() {
		return marqueeTxt;
	}

	public String getFileName() {
		return fileName;
	}

	public String getRealFileName() {
		return realFileName;
	}

	public String getRoles() {
		return roles;
	}

	public String getUploadFlag() {
		return uploadFlag;
	}

	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public void seteCreDate(Date eCreDate) {
		this.eCreDate = eCreDate;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public void setRptName(String rptName) {
		this.rptName = rptName;
	}

	public void setRptExplain(String rptExplain) {
		this.rptExplain = rptExplain;
	}

	public void setMarqueeFlag(String marqueeFlag) {
		this.marqueeFlag = marqueeFlag;
	}

	public void setMarqueeTxt(String marqueeTxt) {
		this.marqueeTxt = marqueeTxt;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setRealFileName(String realFileName) {
		this.realFileName = realFileName;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public void setUploadFlag(String uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

	public Date geteDate() {
		return eDate;
	}

	public Date getsDate() {
		return sDate;
	}

	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}

	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}

	public String getPRJ_SEQ() {
		return PRJ_SEQ;
	}

	public String getPRD_ID() {
		return PRD_ID;
	}

	public String getPRJ_NAME() {
		return PRJ_NAME;
	}

	public String getBT_DT() {
		return BT_DT;
	}

	public void setPRJ_SEQ(String pRJ_SEQ) {
		PRJ_SEQ = pRJ_SEQ;
	}

	public void setPRD_ID(String pRD_ID) {
		PRD_ID = pRD_ID;
	}

	public void setPRJ_NAME(String pRJ_NAME) {
		PRJ_NAME = pRJ_NAME;
	}

	public void setBT_DT(String bT_DT) {
		BT_DT = bT_DT;
	}

	public String getPRJID() {
		return PRJID;
	}

	public void setPRJID(String pRJID) {
		PRJID = pRJID;
	}

}
