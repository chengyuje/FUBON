package com.systex.jbranch.fubon.commons.mplus;

import com.systex.jbranch.fubon.commons.mplus.cons.MPlusCons;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.MultipartEntity;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.SecretKeyType;

public class MPlusAlertGroupInputVO extends MPlusEmployeeInputVO{
	private String action;//create: 建立公司群組/modify: 異動公司群組/dismiss: 解散公司群組
	@MultipartEntity(secretKeyType = SecretKeyType.METHOD , secretKeyValue = MPlusCons.GET_AES_KEY_MEHOTD)
	private String hostParam;//組長 Email或組長員工編號，需加密 action=create 時必填
	@MultipartEntity(secretKeyType = SecretKeyType.METHOD , secretKeyValue = MPlusCons.GET_AES_KEY_MEHOTD)
	private String groupId;//群組 ID，需加密  action=modify/dismiss 時必填 
	@MultipartEntity(secretKeyType = SecretKeyType.METHOD , secretKeyValue = MPlusCons.GET_AES_KEY_MEHOTD)
	private String groupName;//群組名稱，需加密 action=create 時必填 

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getHostParam() {
		return hostParam;
	}
	public void setHostParam(String hostParam) {
		this.hostParam = hostParam;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
