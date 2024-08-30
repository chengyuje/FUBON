package com.systex.jbranch.fubon.commons.esb.vo.nfbrnh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 動態鎖利贖回
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRNHInputVO {
    @XmlElement
	private String CheckCode;           //確認碼             
    @XmlElement
	private String ApplyDate;             //申請日期
    @XmlElement
	private String EffDate;               //生效日
    @XmlElement
	private String KeyinNo;             //理專登錄編號       
    @XmlElement
	private String BranchNo;            //交易行             
    @XmlElement
	private String KeyinId;             //建機人員         
    @XmlElement
	private String TrustId;             //委託人統編   
    @XmlElement
	private String EviNum;            //憑證編號                   
    @XmlElement
	private String RcvAcctId;         //入帳帳號         
    @XmlElement
	private String NarratorId;        //解說人員
    @XmlElement
	private String RecSeq;        		//錄音序號
    @XmlElement
	private String BackUntNum;    		//贖回單位數
    @XmlElement
	private String BackType;          	//贖回類別 1全部贖回2部分贖回   
    @XmlElement
	private String Filler;				//保留欄位
    
    
	public String getCheckCode() {
		return CheckCode;
	}
	public void setCheckCode(String checkCode) {
		CheckCode = checkCode;
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
	public String getTrustId() {
		return TrustId;
	}
	public void setTrustId(String trustId) {
		TrustId = trustId;
	}
	public String getEviNum() {
		return EviNum;
	}
	public void setEviNum(String eviNum) {
		EviNum = eviNum;
	}
	public String getRcvAcctId() {
		return RcvAcctId;
	}
	public void setRcvAcctId(String rcvAcctId) {
		RcvAcctId = rcvAcctId;
	}
	public String getNarratorId() {
		return NarratorId;
	}
	public void setNarratorId(String narratorId) {
		NarratorId = narratorId;
	}
	public String getRecSeq() {
		return RecSeq;
	}
	public void setRecSeq(String recSeq) {
		RecSeq = recSeq;
	}
	public String getBackUntNum() {
		return BackUntNum;
	}
	public void setBackUntNum(String backUntNum) {
		BackUntNum = backUntNum;
	}
	public String getBackType() {
		return BackType;
	}
	public void setBackType(String backType) {
		BackType = backType;
	}
	public String getFiller() {
		return Filler;
	}
	public void setFiller(String filler) {
		Filler = filler;
	}
	
}
