package com.systex.jbranch.app.server.fps.kyc320;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * @author USER
 *
 */
public class KYC320InputVO extends PagingInputVO {

	private String CUST_ID; //客戶ID
	private String SEQ; //序號主鍵
	private Date sTime;
	private Date eTime;
	private String OUT_ACCESS; //內訪或外
	private String TYPE;
	private String INV; //網銀或行銀
	private String REC_SEQ; //錄音序號
	private String KYCcustID;
	private String KYCcreateDate;
	private String COOLING_REC_SEQ; //冷靜期錄音序號
	private String isPrintKYCEng;
	private String selectRoleID;
	private String memLoginFlag;

	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

	public String getSelectRoleID() {
		return selectRoleID;
	}

	public void setSelectRoleID(String selectRoleID) {
		this.selectRoleID = selectRoleID;
	}

	public String getCOOLING_REC_SEQ() {
		return COOLING_REC_SEQ;
	}

	public void setCOOLING_REC_SEQ(String cOOLING_REC_SEQ) {
		COOLING_REC_SEQ = cOOLING_REC_SEQ;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getTYPE() {
		return TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public Date getsTime() {
		return sTime;
	}

	public void setsTime(Date sTime) {
		this.sTime = sTime;
	}

	public String getOUT_ACCESS() {
		return OUT_ACCESS;
	}

	public void setOUT_ACCESS(String oUT_ACCESS) {
		OUT_ACCESS = oUT_ACCESS;
	}

	public String getINV() {
		return INV;
	}

	public void setINV(String iNV) {
		INV = iNV;
	}

	public String getSEQ() {
		return SEQ;
	}

	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}

	public Date geteTime() {
		return eTime;
	}

	public void seteTime(Date eTime) {
		this.eTime = eTime;
	}

	public String getREC_SEQ() {
		return REC_SEQ;
	}

	public void setREC_SEQ(String rEC_SEQ) {
		REC_SEQ = rEC_SEQ;
	}

	public void setKYCcustID(String KYCcustID) {
		this.KYCcustID = KYCcustID;
	}

	public String getKYCcustID() {
		return KYCcustID;
	}

	public void setKYCcreateDate(String KYCcreateDate) {
		this.KYCcreateDate = KYCcreateDate;
	}

	public String getKYCcreateDate() {
		return KYCcreateDate;
	}

	public String getIsPrintKYCEng() {
		return isPrintKYCEng;
	}

	public void setIsPrintKYCEng(String isPrintKYCEng) {
		this.isPrintKYCEng = isPrintKYCEng;
	}

}
