package com.systex.jbranch.fubon.commons.esb.vo.nfei002;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NFEI002OutputDetailsVO {
	@XmlElement
	private String AR101; // 記錄專業投資人註記
	@XmlElement
	private String AR102; // 已簽訂推介同意書
	@XmlElement
	private String AR103; // 異動分行
	@XmlElement
	private String AR104; // 異動日期
	@XmlElement
	private String AR105; // 維護時間
	@XmlElement
	private String AR106; // 維護經辦
	@XmlElement
	private String AR107; // 覆核主管
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
	public String getAR104() {
		return AR104;
	}
	public void setAR104(String aR104) {
		AR104 = aR104;
	}
	public String getAR105() {
		return AR105;
	}
	public void setAR105(String aR105) {
		AR105 = aR105;
	}
	public String getAR106() {
		return AR106;
	}
	public void setAR106(String aR106) {
		AR106 = aR106;
	}
	public String getAR107() {
		return AR107;
	}
	public void setAR107(String aR107) {
		AR107 = aR107;
	}

	
}
