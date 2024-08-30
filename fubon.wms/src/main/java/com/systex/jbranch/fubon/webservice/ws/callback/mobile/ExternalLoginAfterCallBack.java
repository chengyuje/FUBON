package com.systex.jbranch.fubon.webservice.ws.callback.mobile;

import java.util.Map;

import com.systex.jbranch.comutil.collection.GenericMap;

public class ExternalLoginAfterCallBack extends ExternalCallBack{
	public GenericMap excuteSuccess(GenericMap resultGmap , Object body){
		GenericMap header = new GenericMap((Map)resultGmap.get("header"));
		((Map)body).put("applicationId", header.getNotNullStr("applicationId"));
		return genRtnGmap("0000" , "成功" , body);
	}
}
