package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.Date;
import java.util.List;
import com.systex.jbranch.platform.common.dataManager.UUID;

public class CalFamilyGapInputVO {
	
	public CalFamilyGapInputVO()
	{
	}
	
	private String insCustID;		// 客戶ID
	private String insCustName;		// 客戶姓名
	private Date insCustBirthday;	// 客戶生日
	private String insCustGender;	// 客戶性別
	private List lstWholeLife;		// 保障領回中間檔
	private List lstExpression;		// 給付說明中間檔
	private UUID uuid;			// 接呼叫者所擁有的uuid用以取得相關資源用
	
	public String getInsCustID() {
		return insCustID;
	}
	public void setInsCustID(String insCustID) {
		this.insCustID = insCustID;
	}
	public String getInsCustName() {
		return insCustName;
	}
	public void setInsCustName(String insCustName) {
		this.insCustName = insCustName;
	}
	public Date getInsCustBirthday() {
		return insCustBirthday;
	}
	public void setInsCustBirthday(Date insCustBirthday) {
		this.insCustBirthday = insCustBirthday;
	}
	public String getInsCustGender() {
		return insCustGender;
	}
	public void setInsCustGender(String insCustGender) {
		this.insCustGender = insCustGender;
	}
	public List getLstWholeLife() {
		return lstWholeLife;
	}
	public void setLstWholeLife(List lstWholeLife) {
		this.lstWholeLife = lstWholeLife;
	}
	public List getLstExpression() {
		return lstExpression;
	}
	public void setLstExpression(List lstExpression) {
		this.lstExpression = lstExpression;
	}
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
}
