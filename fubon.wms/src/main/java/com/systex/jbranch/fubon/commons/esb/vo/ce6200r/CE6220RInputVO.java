package com.systex.jbranch.fubon.commons.esb.vo.ce6200r;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SamTu on 2024/04/17.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CE6220RInputVO {
	@XmlElement
	private String PIN;	//ID/卡號
	@XmlElement
	private String TYPE;	//1﹕含持卡人、持卡人歸戶資料及持卡人附卡資料
	//2﹕含持卡人及持卡人附卡人資料
	//3：含持卡人所有正常卡及持卡人相關正常附卡
	//4：持卡人ECard正卡
	//5：目前持卡狀態
	public String getPIN() {
		return PIN;
	}
	public void setPIN(String pIN) {
		PIN = pIN;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	
	
}
