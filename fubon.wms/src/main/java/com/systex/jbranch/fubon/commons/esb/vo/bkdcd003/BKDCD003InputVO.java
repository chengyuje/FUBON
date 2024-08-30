package com.systex.jbranch.fubon.commons.esb.vo.bkdcd003;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/06.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class BKDCD003InputVO {
	@XmlElement
	private String CUSTID;	//客戶ID
	@XmlElement
	private String TradeStatus;	//交易狀態
	public String getCUSTID() {
		return CUSTID;
	}
	public void setCUSTID(String cUSTID) {
		CUSTID = cUSTID;
	}
	public String getTradeStatus() {
		return TradeStatus;
	}
	public void setTradeStatus(String tradeStatus) {
		TradeStatus = tradeStatus;
	}

	
}