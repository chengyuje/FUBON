package com.systex.jbranch.ws.external.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.systex.jbranch.app.server.fps.service.sso.WmsSsoServiceInf;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;

@RestController
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequestMapping(value = "/sso")
public class SsoExternalWebService {	
	public static final String AP_JSON_CHARSET_UTF8 = "application/json;charset=UTF-8";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
		
	@Autowired @Qualifier("WmsSsoService") 
	private WmsSsoServiceInf wmsSSOService;
	
	@RequestMapping(
		value = "/{SYS_CODE}/WmsSsoService", 
		method = RequestMethod.POST , 
		consumes = AP_JSON_CHARSET_UTF8,  //指定處理request的submit的類型
		produces = AP_JSON_CHARSET_UTF8   //返回的內容類型
	)
	@ResponseBody
	public String chkToken(
		@PathVariable("SYS_CODE") String sysCode , 
		HttpServletRequest servletRequest ,
		HttpServletResponse response , 
		@RequestBody String json
	) 
	throws Exception {
		logger.info("#[SsoExternalWebService request json] : " + json);
		
		Map param = (Map)JsonUtil.genDefaultGson().fromJson(json, HashMap.class).get("param");
//		param = new HashMap();
//		param.put("WsID", "rxErU3Dd3hGF5KdmpQgt");
//		param.put("WsPwd", "RuE8ZjtuASaYDV2mBv9Y");
//		param.put("SysCode", "SIG");
//		param.put("VerifyCode", "U0lHQDE3Mi4xOS4yNDMuMjMjNDc4MjgyMDc2MDI3MzI0MDk3Mw");
		
		String reqName = HttpServletRequest.class.getName();
		
		GenericMap paramGenMap = new GenericMap(param);
		paramGenMap.put("SYS_CODE", sysCode);
		paramGenMap.put(reqName , servletRequest);
		
		String responseStr = JsonUtil.genDefaultGson().toJson(
			wmsSSOService.tokenVerification(paramGenMap).getParamMap()
		);
		
		return responseStr;
	}	
}
