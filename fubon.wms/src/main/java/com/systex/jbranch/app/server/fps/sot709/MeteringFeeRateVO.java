package com.systex.jbranch.app.server.fps.sot709;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MeteringFeeRateVO {
	
	private Date effBeginDate;//有效期間起
	private Date effEndDate;//有效期間迄
	private String cntSingle;//臨櫃單筆
	private String cntReg;//臨櫃小額
	private String webSingle;//網銀單筆
	private String webRef;//網銀小額
	private BigDecimal totalCount;//優惠次數
	private BigDecimal usedCount;//已使用次數
	private String groupName;//團體名稱
	
	private List<Map<String, Object>> meteringFeeRateList;

	public Date getEffBeginDate() {
		return effBeginDate;
	}

	public void setEffBeginDate(Date effBeginDate) {
		this.effBeginDate = effBeginDate;
	}

	public Date getEffEndDate() {
		return effEndDate;
	}

	public void setEffEndDate(Date effEndDate) {
		this.effEndDate = effEndDate;
	}

	public String getCntSingle() {
		return cntSingle;
	}

	public void setCntSingle(String cntSingle) {
		this.cntSingle = cntSingle;
	}

	public String getCntReg() {
		return cntReg;
	}

	public void setCntReg(String cntReg) {
		this.cntReg = cntReg;
	}

	public String getWebSingle() {
		return webSingle;
	}

	public void setWebSingle(String webSingle) {
		this.webSingle = webSingle;
	}

	public String getWebRef() {
		return webRef;
	}

	public void setWebRef(String webRef) {
		this.webRef = webRef;
	}

	public BigDecimal getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(BigDecimal totalCount) {
		this.totalCount = totalCount;
	}

	public BigDecimal getUsedCount() {
		return usedCount;
	}

	public void setUsedCount(BigDecimal usedCount) {
		this.usedCount = usedCount;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<Map<String, Object>> getMeteringFeeRateList() {
		return meteringFeeRateList;
	}

	public void setMeteringFeeRateList(List<Map<String, Object>> meteringFeeRateList) {
		this.meteringFeeRateList = meteringFeeRateList;
	}
	
	
}
