package com.systex.jbranch.fubon.commons.esb.vo.spwebq1;

import javax.xml.bind.annotation.XmlElement;

public class SPWEBQ1OutputDetailVO {
	
	@XmlElement
	private String Amount; // 參考台幣現值

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}
	
}
