package com.systex.jbranch.fubon.commons.mplus;

import com.systex.jbranch.fubon.commons.mplus.cons.MPlusCons;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.MultipartEntity;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.SecretKeyType;

public class MPlusDelEmployeeInputVO extends MPlusEmployeeInputVO{
	@MultipartEntity(secretKeyType = SecretKeyType.METHOD , secretKeyValue = MPlusCons.GET_AES_KEY_MEHOTD)
	private String alterParam; //Email 或員編, 可多筆, 需加密。 單筆的格式："1234567" or "xxx@yyy.zzz" 多筆之間以半形逗號分隔："1234567, 1233444,1255555" or "xx1@yyy.zzz,xx2@yyy.zzz" 
	
    public String getAlterParam() {
		return alterParam;
	}
    
	public void setAlterParam(String alterParam) {
		this.alterParam = alterParam;
	}
}
