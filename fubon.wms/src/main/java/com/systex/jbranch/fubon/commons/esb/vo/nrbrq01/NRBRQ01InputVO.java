package com.systex.jbranch.fubon.commons.esb.vo.nrbrq01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Carley on 2017/05/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NRBRQ01InputVO {
	@XmlElement
	private String CustId;		//客戶ID
	@XmlElement
	private String CurAcc;		//信託帳號
	@XmlElement
	private String StartDt;		//交易起日
	@XmlElement
	private String EndDt;		//交易迄日
	@XmlElement
	private String TrustAcct;	//交易類型 0=全部、1=買進、2=賣出、3=除權/除息、A=取信託帳號
	
	public String getCustId() {
		return CustId;
	}
	public void setCustId(String custId) {
		CustId = custId;
	}
	public String getCurAcc() {
		return CurAcc;
	}
	public void setCurAcc(String curAcc) {
		CurAcc = curAcc;
	}
	public String getStartDt() {
		return StartDt;
	}
	public void setStartDt(String startDt) {
		StartDt = startDt;
	}
	public String getEndDt() {
		return EndDt;
	}
	public void setEndDt(String endDt) {
		EndDt = endDt;
	}
	public String getTrustAcct() {
		return TrustAcct;
	}
	public void setTrustAcct(String trustAcct) {
		TrustAcct = trustAcct;
	}
	
	
}