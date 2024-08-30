package com.systex.jbranch.fubon.commons.esb.vo.cm061435cr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created 2023/04/26
 * 高資產客戶註記
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CM061435CRInputVO {
	@XmlElement
	private String ID1; //客戶統編
	@XmlElement
	private String ID2; //授權交易人員
	@XmlElement
	private String IDTYPE1; //客戶證件型態
	@XmlElement
	private String IDTYPE2; //授權人員證件型態
	
	
	public String getID1() {
		return ID1;
	}
	public void setID1(String iD1) {
		ID1 = iD1;
	}
	public String getID2() {
		return ID2;
	}
	public void setID2(String iD2) {
		ID2 = iD2;
	}
	public String getIDTYPE1() {
		return IDTYPE1;
	}
	public void setIDTYPE1(String iDTYPE1) {
		IDTYPE1 = iDTYPE1;
	}
	public String getIDTYPE2() {
		return IDTYPE2;
	}
	public void setIDTYPE2(String iDTYPE2) {
		IDTYPE2 = iDTYPE2;
	}
	
	
}