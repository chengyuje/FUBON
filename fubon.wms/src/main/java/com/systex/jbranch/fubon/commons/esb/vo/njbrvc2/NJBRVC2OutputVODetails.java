package com.systex.jbranch.fubon.commons.esb.vo.njbrvc2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/29.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NJBRVC2OutputVODetails {
    @XmlElement
	private String BatchEffDate; //批次生效日期
    @XmlElement
	private String BatchEffTime; //批次生效時間
    @XmlElement
	private String EntrustStatus; //委託狀態
    
	public String getBatchEffDate() {
		return BatchEffDate;
	}
	public void setBatchEffDate(String batchEffDate) {
		BatchEffDate = batchEffDate;
	}
	public String getBatchEffTime() {
		return BatchEffTime;
	}
	public void setBatchEffTime(String batchEffTime) {
		BatchEffTime = batchEffTime;
	}
	public String getEntrustStatus() {
		return EntrustStatus;
	}
	public void setEntrustStatus(String entrustStatus) {
		EntrustStatus = entrustStatus;
	}
	        
}
