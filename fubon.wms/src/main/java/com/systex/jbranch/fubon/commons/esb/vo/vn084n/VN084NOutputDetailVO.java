package com.systex.jbranch.fubon.commons.esb.vo.vn084n;

import javax.xml.bind.annotation.XmlElement;

public class VN084NOutputDetailVO {
	
	@XmlElement
	private String Amount; // 金額

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}
	
}
