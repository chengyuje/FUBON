package com.systex.jbranch.fubon.commons.cbs.vo._060440_060441;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS060441OutputVO {
	@XmlElement(name = "TxRepeat")
	private List<CBS060441OutputDetailsVO> details;
	private String Action1;
	private String DefaultString21;
	private String DefInteger41;
	private String DefaultString54;
	private String DefInteger2;
	private String DefInteger3;
	private String OffsetsFiller;
	private String DefaultString13;
	private String id_type2;
	public List<CBS060441OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<CBS060441OutputDetailsVO> details) {
		this.details = details;
	}
	public String getAction1() {
		return Action1;
	}
	public void setAction1(String action1) {
		Action1 = action1;
	}
	public String getDefaultString21() {
		return DefaultString21;
	}
	public void setDefaultString21(String defaultString21) {
		DefaultString21 = defaultString21;
	}
	public String getDefInteger41() {
		return DefInteger41;
	}
	public void setDefInteger41(String defInteger41) {
		DefInteger41 = defInteger41;
	}
	public String getDefaultString54() {
		return DefaultString54;
	}
	public void setDefaultString54(String defaultString54) {
		DefaultString54 = defaultString54;
	}
	public String getDefInteger2() {
		return DefInteger2;
	}
	public void setDefInteger2(String defInteger2) {
		DefInteger2 = defInteger2;
	}
	public String getDefInteger3() {
		return DefInteger3;
	}
	public void setDefInteger3(String defInteger3) {
		DefInteger3 = defInteger3;
	}
	public String getOffsetsFiller() {
		return OffsetsFiller;
	}
	public void setOffsetsFiller(String offsetsFiller) {
		OffsetsFiller = offsetsFiller;
	}
	public String getDefaultString13() {
		return DefaultString13;
	}
	public void setDefaultString13(String defaultString13) {
		DefaultString13 = defaultString13;
	}
	public String getId_type2() {
		return id_type2;
	}
	public void setId_type2(String id_type2) {
		this.id_type2 = id_type2;
	}

	
	
}
