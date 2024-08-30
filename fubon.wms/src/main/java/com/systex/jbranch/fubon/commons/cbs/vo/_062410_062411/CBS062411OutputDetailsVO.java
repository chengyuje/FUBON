package com.systex.jbranch.fubon.commons.cbs.vo._062410_062411;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS062411OutputDetailsVO {
	private String DocNo;
	private String SignDate;
	private String EffDate;
	private String DocName;

	public CBS062411OutputDetailsVO() {
		this.DocNo = "";
		this.SignDate = "";
		this.EffDate = "";
		this.DocName = "";

	}

	public String getDocNo() {
		return DocNo;
	}

	public void setDocNo(String docNo) {
		DocNo = docNo;
	}

	public String getSignDate() {
		return SignDate;
	}

	public void setSignDate(String signDate) {
		SignDate = signDate;
	}

	public String getEffDate() {
		return EffDate;
	}

	public void setEffDate(String effDate) {
		EffDate = effDate;
	}

	public String getDocName() {
		return DocName;
	}

	public void setDocName(String docName) {
		DocName = docName;
	}

}
