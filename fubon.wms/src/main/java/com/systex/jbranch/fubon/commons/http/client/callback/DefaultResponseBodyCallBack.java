package com.systex.jbranch.fubon.commons.http.client.callback;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.collection.GenericMap;

public class DefaultResponseBodyCallBack implements CallBackExcute {
	@Override
	public <T> T callBack(GenericMap genericMap){
		try{
			CloseableHttpResponse response = genericMap.get(CloseableHttpResponse.class);
			
			if(response.getEntity() == null)
				throw new Exception("response entity is null");
			
			return (T)EntityUtils.toString(response.getEntity());
		}
		catch(Exception ex){
			throw new RuntimeException(ExceptionUtils.getStackTrace(ex));
		}
	}
}
