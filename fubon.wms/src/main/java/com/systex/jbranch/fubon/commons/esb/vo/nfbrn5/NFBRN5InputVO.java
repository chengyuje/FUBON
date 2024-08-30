package com.systex.jbranch.fubon.commons.esb.vo.nfbrn5;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRN5InputVO {
	@XmlElement
	private String CheckCode;
	@XmlElement
	private String ErrorCode;
	@XmlElement
	private String ErrorMsg;
	@XmlElement
	private String ApplyDate;
	@XmlElement
	private String EffDate;
	@XmlElement
	private String KeyinNo;
	@XmlElement
	private String BranchNo;
	@XmlElement
	private String KeyinId;
	@XmlElement
	private String CustId;
	@XmlElement
	private String ChangeItem;
	@XmlElement
	private String DBUType;
	@XmlElement
	private String NarratorId;
	@XmlElement
	private String ProspectusType;
	@XmlElement
	private String RecSeq;
	@XmlElement
	private String Filler;
	@XmlElement
	private String MinorityYN;

	@XmlElement(name = "TxRepeat")
	private List<NFBRN5InputVODetails> details;

	public String getCheckCode() {
		return CheckCode;
	}

	public void setCheckCode(String checkCode) {
		CheckCode = checkCode;
	}

	public String getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}

	public String getErrorMsg() {
		return ErrorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		ErrorMsg = errorMsg;
	}

	public String getApplyDate() {
		return ApplyDate;
	}

	public void setApplyDate(String applyDate) {
		ApplyDate = applyDate;
	}

	public String getEffDate() {
		return EffDate;
	}

	public void setEffDate(String effDate) {
		EffDate = effDate;
	}

	public String getKeyinNo() {
		return KeyinNo;
	}

	public void setKeyinNo(String keyinNo) {
		KeyinNo = keyinNo;
	}

	public String getBranchNo() {
		return BranchNo;
	}

	public void setBranchNo(String branchNo) {
		BranchNo = branchNo;
	}

	public String getKeyinId() {
		return KeyinId;
	}

	public void setKeyinId(String keyinId) {
		KeyinId = keyinId;
	}

	public String getCustId() {
		return CustId;
	}

	public void setCustId(String custId) {
		CustId = custId;
	}

	public String getChangeItem() {
		return ChangeItem;
	}

	public void setChangeItem(String changeItem) {
		ChangeItem = changeItem;
	}

	public String getDBUType() {
		return DBUType;
	}

	public void setDBUType(String DBUType) {
		this.DBUType = DBUType;
	}

	public String getNarratorId() {
		return NarratorId;
	}

	public void setNarratorId(String narratorId) {
		NarratorId = narratorId;
	}

	public String getProspectusType() {
		return ProspectusType;
	}

	public void setProspectusType(String prospectusType) {
		ProspectusType = prospectusType;
	}

	public String getRecSeq() {
		return RecSeq;
	}

	public void setRecSeq(String recSeq) {
		RecSeq = recSeq;
	}

	public String getFiller() {
		return Filler;
	}

	public void setFiller(String filler) {
		Filler = filler;
	}

	public String getMinorityYN() {
		return MinorityYN;
	}

	public void setMinorityYN(String minorityYN) {
		MinorityYN = minorityYN;
	}

	public List<NFBRN5InputVODetails> getDetails() {
		return details;
	}
	public void setDetails(List<NFBRN5InputVODetails> details) {
		this.details = details;
	}
}
