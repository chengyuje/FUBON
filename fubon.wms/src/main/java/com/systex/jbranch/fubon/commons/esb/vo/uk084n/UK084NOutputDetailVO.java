package com.systex.jbranch.fubon.commons.esb.vo.uk084n;

import javax.xml.bind.annotation.XmlElement;

public class UK084NOutputDetailVO {

	@XmlElement
	private String Amount; // 台幣現值
	
	public String getAmount() {
		return Amount;
	}

	public void setAR101(String Amount) {
		this.Amount = Amount;
	}
}
