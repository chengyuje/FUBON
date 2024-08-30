package com.systex.jbranch.fubon.commons.http.client.callback;

import java.util.HashMap;

import org.apache.http.client.methods.CloseableHttpResponse;

import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.collection.GenericMap;

public class DefHeaderCallBack implements CallBackExcute{
	public <T> T callBack(GenericMap genericMap) {
		CloseableHttpResponse response = genericMap.get(CloseableHttpResponse.class);
		
		if (response.getHeaders("ApplicationID") != null && response.getHeaders("ApplicationID").length > 0) {
			return (T)new GenericMap().put("ApplicationID" , response.getHeaders("ApplicationID")[0].getValue()).getParamMap();
		}
		return (T)new HashMap();
	}
}