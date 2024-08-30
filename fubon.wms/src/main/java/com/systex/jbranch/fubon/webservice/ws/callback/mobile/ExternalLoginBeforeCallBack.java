package com.systex.jbranch.fubon.webservice.ws.callback.mobile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;

import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.ws.external.service.dao.ExtjlbDao;

public class ExternalLoginBeforeCallBack extends ExternalBeforeCallBack{
	
	@Override
	public <T> T callBack(GenericMap requestData) {
		HttpServletRequest servletRequest = requestData.get(HttpServletRequest.class);
		HttpServletResponse servletResponse = requestData.get(HttpServletResponse.class);
		List<Map<String,Object>> wsConfig = doGetWsConfig();
		Map mapConf = CollectionSearchUtils.findMapInColleciton(wsConfig  , "PARAM_CODE" , "AES_DEF_KEY");
		
		//產生32字元的uuid
		String applicationId = UUID.randomUUID().toString();
		servletRequest.setAttribute("applicationId" , applicationId);
		servletResponse.addHeader("applicationId" , applicationId);
		//標記若加解密是否取預設key
		servletRequest.setAttribute("IS_DEF_KEY", "Y");
		//取得預設key
		servletRequest.setAttribute("DEF_KEY", ObjectUtils.toString(mapConf.get("PARAM_NAME")));
		
		return super.callBack(requestData);
	}
	
	public List<Map<String,Object>> doGetWsConfig(){
		ExtjlbDao extjlbDao = null;
		List<Map<String,Object>> wsConfig = null;
		
		try {
			extjlbDao = (ExtjlbDao) PlatformContext.getBean("ExtjlbDao");
		} catch (JBranchException e) {
			throw new RuntimeException("未取得bean ExtjlbDao，請洽系統管理員！");
		}
		
		try {
			if(CollectionUtils.isEmpty(wsConfig = extjlbDao.queryParameterConf("SYS.WS_CONF"))){
				throw new RuntimeException("ws設定不存在，請洽系統管理員！");
			}
		} 
		catch (JBranchException e) {
			throw new RuntimeException("ws設定不存在，請洽系統管理員！");
		}

		return wsConfig;
	}
}
