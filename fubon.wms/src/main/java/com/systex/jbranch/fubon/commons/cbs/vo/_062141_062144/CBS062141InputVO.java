package com.systex.jbranch.fubon.commons.cbs.vo._062141_062144;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS062141InputVO {
	
	private String  Action = "A"; //功能
	private String IdNo; //證件號碼
	private String IdType; //證件類型
	private String ColType; //擔保品大類
	private String ColNo; //擔保品編號
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
	}
	public String getIdNo() {
		return IdNo;
	}
	public void setIdNo(String idNo) {
		IdNo = idNo;
	}
	public String getIdType() {
		return IdType;
	}
	public void setIdType(String idType) {
		IdType = idType;
	}
	public String getColType() {
		return ColType;
	}
	public void setColType(String colType) {
		ColType = colType;
	}
	public String getColNo() {
		return ColNo;
	}
	public void setColNo(String colNo) {
		ColNo = colNo;
	}
	
	

}
