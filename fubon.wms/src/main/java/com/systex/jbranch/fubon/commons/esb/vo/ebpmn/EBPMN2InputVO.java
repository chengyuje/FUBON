package com.systex.jbranch.fubon.commons.esb.vo.ebpmn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType; 

/**
 * AI BANK快速下單 – 發送電文至AI BANK, 行事曆表單上可以出現快速申購之訊息
 * Created by Jeff on 2024/06/27.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EBPMN2InputVO {
	@XmlElement
	private String notifyType; // 通知類型
	@XmlElement
	private String custId; // 統一編號
	@XmlElement
	private String setAmount; // 客戶設定金額
	@XmlElement
	private String applySeq; // 申購批號

	public String getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getSetAmount() {
		return setAmount;
	}

	public void setSetAmount(String setAmount) {
		this.setAmount = setAmount;
	}

	public String getApplySeq() {
		return applySeq;
	}

	public void setApplySeq(String applySeq) {
		this.applySeq = applySeq;
	}
}
