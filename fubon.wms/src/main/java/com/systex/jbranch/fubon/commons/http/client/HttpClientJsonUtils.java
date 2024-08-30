package com.systex.jbranch.fubon.commons.http.client;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.http.client.callback.DefaultResponseBodyCallBack;
import com.systex.jbranch.fubon.commons.soap.HttpClientSoapUtils;

public class HttpClientJsonUtils extends HttpClientSoapUtils{
	private static Logger logger = LoggerFactory.getLogger(HttpClientJsonUtils.class);
	
	public static GenericMap sendDefJsonRequest(
		String url, String json , Map<String, Object> headerMap
	) 
	throws Exception {
		int time = 900000;
		return sendJsonRequest(url, json, time, headerMap, null , new DefaultResponseBodyCallBack()) ;
	}
	
	public static GenericMap sendJson(
		String url, String json, int time , Map<String, Object> headerMap
	) 
	throws Exception {
		return sendJsonRequest(url, json, time, headerMap, null , new DefaultResponseBodyCallBack()) ;
	}
	
	public static GenericMap sendJsonRequest(
		String url, String json, int time , Map<String, Object> headerMap, CallBackExcute responseHeaderCallBack
	) 
	throws Exception {
		return sendJsonRequest(url, json, time, headerMap, responseHeaderCallBack, new DefaultResponseBodyCallBack()) ;
	}
	
	public static GenericMap sendJsonRequest(
		String url, String json, int time , Map<String, Object> headerMap,
		CallBackExcute responseHeaderCallBack,
		CallBackExcute responseBodyCallBack
	) 
	throws Exception {
		CloseableHttpClient httpclient = url.matches("^https.*") ? 
			createSSLInsecureClient() : HttpClientBuilder.create().build();
		
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(json, "utf-8"));
		httpPost.setHeader("Content-type", "application/json;charset=UTF-8");

		if(MapUtils.isNotEmpty(headerMap)){
			for (String key : headerMap.keySet()) {
				httpPost.setHeader(key, ObjectUtils.toString(headerMap.get(key)));
			}
		}
		
		//回傳物件
		GenericMap responseGmap = new GenericMap();
		CloseableHttpResponse response = null;
		GenericMap responseCallBackParam = new GenericMap();
		
		try {
			httpPost.setConfig(
				RequestConfig.custom().setSocketTimeout(time)
				.setConnectTimeout(time).setConnectionRequestTimeout(time)
				.build()
			);
			
			if ((response = httpclient.execute(httpPost)) == null)
				throw new Exception("response is null");
			
			responseCallBackParam.put(CloseableHttpResponse.class , response);
			
			//處理header
			if(responseHeaderCallBack != null)
				responseGmap.put("header", responseHeaderCallBack.callBack(responseCallBackParam));
			
			//處理body
			if(responseBodyCallBack != null)
				responseGmap.put("body", responseBodyCallBack.callBack(responseCallBackParam));
		} 
		finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return responseGmap;
	}
}
