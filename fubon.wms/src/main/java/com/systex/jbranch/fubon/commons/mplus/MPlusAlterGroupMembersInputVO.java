package com.systex.jbranch.fubon.commons.mplus;

import com.systex.jbranch.fubon.commons.mplus.cons.MPlusCons;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.MultipartEntity;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.SecretKeyType;

public class MPlusAlterGroupMembersInputVO extends MPlusAlertGroupInputVO{
	@MultipartEntity(secretKeyType = SecretKeyType.METHOD , secretKeyValue = MPlusCons.GET_AES_KEY_MEHOTD)
	private String alterParam;//Email或員編, 可多筆, 需加密。
	
	public String getAlterParam() {
		return alterParam;
	}

	public void setAlterParam(String alterParam) {
		this.alterParam = alterParam;
	}
	
	
}
