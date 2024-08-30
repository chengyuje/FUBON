package com.systex.jbranch.fubon.commons.esb.vo.vn084n1;

import javax.xml.bind.annotation.XmlElement;

public class VN084N1OutputDetailVO {
	
	@XmlElement
	private String Amount; // 金額

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

}
