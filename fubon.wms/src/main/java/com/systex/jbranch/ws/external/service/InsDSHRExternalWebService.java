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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.systex.jbranch.app.server.fps.service.insdshr.WmsInsDSHRServiceInf;
import com.systex.jbranch.app.server.fps.service.inssig.WmsInsSigServiceInf;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;

/***
 * 富壽與北富銀資料共享以減少新契約照會需求
 * @author 1800036
 *
 */

@RestController
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequestMapping(value = "/insdshr")
public class InsDSHRExternalWebService {	
	public static final String AP_JSON_CHARSET_UTF8 = "application/json;charset=UTF-8";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data;charset=UTF-8";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
		
	@Autowired @Qualifier("WmsInsDSHRService") 
	private WmsInsDSHRServiceInf wmsInsDSHRService;	
	
	/**
	 * 人壽端回傳業管要保書暫存資料
	 * @param sysCode
	 * @param servletRequest
	 * @param response
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(
		value = "/{SYS_CODE}/setCaseSaveData", 
		method = RequestMethod.POST , 
		consumes = AP_JSON_CHARSET_UTF8,  //指定處理request的submit的類型
		produces = AP_JSON_CHARSET_UTF8   //返回的內容類型
	)
	@ResponseBody
	public String setCaseSaveData(
		@PathVariable("SYS_CODE") String sysCode , 
		HttpServletRequest servletRequest ,
		HttpServletResponse response , 
		@RequestBody String json 
	) 
	throws Exception {
		logger.info("#[InsDSHRExternalWebService setCaseSaveData request json] : " + json);
		
		Map param = (Map)JsonUtil.genDefaultGson().fromJson(json, HashMap.class);
				
		String reqName = HttpServletRequest.class.getName();
		GenericMap paramGenMap = new GenericMap(param);
		paramGenMap.put(reqName , servletRequest);
		
		String responseStr = JsonUtil.genDefaultGson().toJson(
				wmsInsDSHRService.setCaseSaveData(paramGenMap).getParamMap()
		);
		
		logger.info("#[InsDSHRExternalWebService setCaseSaveData return json] : " + responseStr);
		
		return responseStr;
	}	
	
	/**
	 * 人壽端取得行動要保書檢核
	 * @param sysCode
	 * @param servletRequest
	 * @param response
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(
			value = "/{SYS_CODE}/validateInsData", 
			method = RequestMethod.POST , 
			consumes = AP_JSON_CHARSET_UTF8,  //指定處理request的submit的類型
			produces = AP_JSON_CHARSET_UTF8   //返回的內容類型
		)
	@ResponseBody
	public String validateInsData(
		@PathVariable("SYS_CODE") String sysCode , 
		HttpServletRequest servletRequest ,
		HttpServletResponse response , 
		@RequestBody String json
	) 
	throws Exception {
		logger.info("#[InsDSHRExternalWebService validateInsData request json] : " + json);
		
		Map param = (Map)JsonUtil.genDefaultGson().fromJson(json, HashMap.class);
				
		String reqName = HttpServletRequest.class.getName();
		GenericMap paramGenMap = new GenericMap(param);
		paramGenMap.put(reqName , servletRequest);
		
		String responseStr = JsonUtil.genDefaultGson().toJson(
				wmsInsDSHRService.validateInsData(paramGenMap).getParamMap()
		);
		
		logger.info("#[InsDSHRExternalWebService validateInsData return json] : " + responseStr);
		
		return responseStr;
	}
	
}
