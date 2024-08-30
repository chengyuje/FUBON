package com.systex.jbranch.fubon.commons.esb.vo.nkne01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.systex.jbranch.fubon.commons.stringFormat.MappingConfig;

/**
 * Created by James on 2017/4/19.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NKNE01OutputVODetailsVO {
	
	@XmlElement 
	@MappingConfig("W-8BEN")
	private String W_8BEN; // W-8BEN Y:有效 “ “:無效
	@XmlElement
	private String RAM; // 風險預告書 Y:有效 “ “:無效
	@XmlElement
	private String SignStartDate; // W-8BEN
	@XmlElement
	private String SignEndDate; // 簽署起日
	@XmlElement
	private String FstTrade; // W-8BEN Y:首購
	@XmlElement
	private String DayFstTrade; // 簽署迄日 Y:當日首購
	
	
	public String getW_8BEN() {
		return W_8BEN;
	}
	public void setW_8BEN(String w_8ben) {
		W_8BEN = w_8ben;
	}
	public String getRAM() {
		return RAM;
	}
	public void setRAM(String rAM) {
		RAM = rAM;
	}
	public String getSignStartDate() {
		return SignStartDate;
	}
	public void setSignStartDate(String signStartDate) {
		SignStartDate = signStartDate;
	}
	public String getSignEndDate() {
		return SignEndDate;
	}
	public void setSignEndDate(String signEndDate) {
		SignEndDate = signEndDate;
	}
	public String getFstTrade() {
		return FstTrade;
	}
	public void setFstTrade(String fstTrade) {
		FstTrade = fstTrade;
	}
	public String getDayFstTrade() {
		return DayFstTrade;
	}
	public void setDayFstTrade(String dayFstTrade) {
		DayFstTrade = dayFstTrade;
	}    
	
}
