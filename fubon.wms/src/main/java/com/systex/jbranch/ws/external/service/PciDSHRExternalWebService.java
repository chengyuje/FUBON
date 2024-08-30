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
import com.systex.jbranch.app.server.fps.service.pcidshr.WmsPciDSHRServiceInf;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;

/***
 * 保險作業管理系統(產險Property and Casualty Insurance)與北富銀保險資料檢核需求Web Service
 * @author 1800036
 *
 */

@RestController
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequestMapping(value = "/pcidshr")
public class PciDSHRExternalWebService {	
	public static final String AP_JSON_CHARSET_UTF8 = "application/json;charset=UTF-8";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data;charset=UTF-8";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
		
	@Autowired @Qualifier("WmsPciDSHRService") 
	private WmsPciDSHRServiceInf wmsPciDSHRService;	
	
		
	/**
	 * 保險作業管理系統(產險Property and Casualty Insurance)與北富銀保險資料檢核需求Web Service
	 * @param sysCode
	 * @param servletRequest
	 * @param response
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(
			value = "/{SYS_CODE}/validatePciData", 
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
		logger.info("#[PciDSHRExternalWebService validatePciData request json] : " + json);
		
		Map param = (Map)JsonUtil.genDefaultGson().fromJson(json, HashMap.class);
				
		String reqName = HttpServletRequest.class.getName();
		GenericMap paramGenMap = new GenericMap(param);
		paramGenMap.put(reqName , servletRequest);
		
		String responseStr = JsonUtil.genDefaultGson().toJson(
				wmsPciDSHRService.validatePciData(paramGenMap).getParamMap()
		);
		
		return responseStr;
	}
	
}
