package com.systex.jbranch.ws.external.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

public class ExternalUtil {
	public static String getApplicationId(HttpServletRequest request){
		//登入後app自行傳入的ApplicationId
		String appId = ObjectUtils.toString(request.getHeader("applicationId"));
		//登入時自動產生的ApplicationId
		String loginAppId = ObjectUtils.toString(request.getAttribute("applicationId"));
		//取得當下的ApplicationId
		return StringUtils.isBlank(appId) ? loginAppId : appId;
	}
	
	public static boolean isDefKey(HttpServletRequest request){
		return "Y".equals(request.getAttribute("IS_DEF_KEY"));
	}
	
	public static String doGetAesKey(HttpServletRequest request){
		return isDefKey(request) ? 
			ObjectUtils.toString(request.getAttribute("DEF_KEY")) : 
			StringUtils.rightPad(getApplicationId(request) , 32 , '1').substring(0 , 32);
	}
}
