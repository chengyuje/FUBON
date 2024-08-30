package com.systex.jbranch.fubon.commons.esb.vo.ajw084;

import javax.xml.bind.annotation.XmlElement;

public class AJW084OutputDetailVO {

	@XmlElement
	private String BondAmt1; // 海外債金額
	@XmlElement
	private String BondAmt2; // 境外結構式商品SN金額\

	public String getBondAmt1() {
		return BondAmt1;
	}

	public void setBondAmt1(String bondAmt1) {
		BondAmt1 = bondAmt1;
	}

	public String getBondAmt2() {
		return BondAmt2;
	}

	public void setBondAmt2(String bondAmt2) {
		BondAmt2 = bondAmt2;
	}

}
