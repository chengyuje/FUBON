package com.systex.jbranch.fubon.commons.esb.vo.nfei001;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NFEI001OutputDetailsVO {
	@XmlElement
	private String AR101; // 信託帳號
	@XmlElement
	private String AR102; // 開戶日期
	@XmlElement
	private String AR103; // 帳號分行

	public String getAR101() {
		return AR101;
	}
	public void setAR101(String aR101) {
		AR101 = aR101;
	}
	public String getAR102() {
		return AR102;
	}
	public void setAR102(String aR102) {
		AR102 = aR102;
	}
	public String getAR103() {
		return AR103;
	}
	public void setAR103(String aR103) {
		AR103 = aR103;
	}
	
}
