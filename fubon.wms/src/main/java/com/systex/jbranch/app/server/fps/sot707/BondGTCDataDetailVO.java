package com.systex.jbranch.app.server.fps.sot707;

import java.math.BigDecimal;
import java.util.Date;

public class BondGTCDataDetailVO {
	private Date  	BatchEffDate;	//批次生效日期
	private String  BatchEffTime;	//批次生效時間
	private String  EntrustStatus;	//委託狀態
	
	public Date getBatchEffDate() {
		return BatchEffDate;
	}
	public void setBatchEffDate(Date batchEffDate) {
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
