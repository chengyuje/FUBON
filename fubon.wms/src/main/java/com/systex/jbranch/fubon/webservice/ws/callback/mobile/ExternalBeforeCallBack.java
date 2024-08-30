package com.systex.jbranch.fubon.webservice.ws.callback.mobile;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.encrypt.aes.AesEncryptDecryptUtils;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.ws.external.service.ExternalUtil;

public class ExternalBeforeCallBack implements CallBackExcute{
	public static final String IS_AES = "1";
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T callBack(GenericMap requestData) {
		HttpServletRequest request = requestData.get(HttpServletRequest.class);
		request.setAttribute("mobile", "mobile");
		
		String isAesStr = request.getHeader("isAES");
		boolean isAes = IS_AES.equals(isAesStr);
		
		//是否使用aes加解密
		String json = requestData.get("requestJson");
		Map jsonMap = JsonUtil.genDefaultGson().fromJson(json, java.util.HashMap.class);
		
		if(isAes){
			json = (String)jsonMap.get("param");
		}
		else{
			json = JsonUtil.genDefaultGson().toJson(jsonMap.get("param"));
		}
		
		//取得applicationId
		String applicationId = ExternalUtil.getApplicationId(request);
		
		//如果要加解密
		try {
			json = isAes ? AesEncryptDecryptUtils.decryptAesEcbPkcs7Padding(ExternalUtil.doGetAesKey(request) , json) : json;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
		//重整後的請求json格式：{"header" : {} , "body" : {}}
		Map reqJson = new HashMap();
		//重整後的header區塊
		Map newHeader = new HashMap();
		//重整前後的body區塊，一開始進來的body需要加工
		Map sourceBody = JsonUtil.genDefaultGson().fromJson(json, HashMap.class);
		//回傳結果
		String requestJson = null;
		
		//header
		newHeader.put("applicationId",  applicationId);
		newHeader.put("isAES", isAesStr);		
		newHeader.put("ApplicationID",  applicationId);
		newHeader.put("BranchID",  request.getHeader("branchID"));
		newHeader.put("WsID",  request.getHeader("wsId"));
		newHeader.put("SectionID",  request.getHeader("sectionID"));
		newHeader.put("TlrID",  request.getHeader("tellerId"));
		
		//處理input共用變數，將其移至header，並從body移除
		for(String key : new String[]{"model" , "deviceVersion" , "appVersion"}){
			newHeader.put(key , sourceBody.get(key));
			sourceBody.remove(key);
		}
				
		//埋入ApplicationID
		request.setAttribute("applicationId" , applicationId);
		//標記為行動裝置登入
		request.setAttribute(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN , "mobile");
		
		//組合要重整的請求json內容
		reqJson.put("header", newHeader);
		reqJson.put("body", sourceBody);
		requestJson = JsonUtil.genDefaultGson().toJson(reqJson);
		
		return (T)requestJson;
	}
}
