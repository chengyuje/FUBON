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

import com.systex.jbranch.app.server.fps.service.inssig.WmsInsSigServiceInf;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;

@RestController
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequestMapping(value = "/inssig")
public class InsSigExternalWebService {	
	public static final String AP_JSON_CHARSET_UTF8 = "application/json;charset=UTF-8";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data;charset=UTF-8";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
		
	@Autowired @Qualifier("WmsInsSigService") 
	private WmsInsSigServiceInf wmsInsSigService;
	
	/**
	 * 取得JWT
	 * @param sysCode
	 * @param servletRequest
	 * @param response
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(
		value = "/{SYS_CODE}/getWmsJWT", 
		method = RequestMethod.POST , 
		consumes = AP_JSON_CHARSET_UTF8,  //指定處理request的submit的類型
		produces = AP_JSON_CHARSET_UTF8   //返回的內容類型
	)
	@ResponseBody
	public String getWmsJWT(
		@PathVariable("SYS_CODE") String sysCode , 
		HttpServletRequest servletRequest ,
		HttpServletResponse response , 
		@RequestBody String json
	) 
	throws Exception {
		logger.info("#[InsSigExternalWebService getWmsJWT request json] : " + json);
		
		Map param = (Map)JsonUtil.genDefaultGson().fromJson(json, HashMap.class);
		
		String reqName = HttpServletRequest.class.getName();
		
		GenericMap paramGenMap = new GenericMap(param);
		paramGenMap.put(reqName , servletRequest);
		
		String responseStr = JsonUtil.genDefaultGson().toJson(
				wmsInsSigService.getWmsJWT(paramGenMap).getParamMap()
		);
		
		return responseStr;
	}
	
	/**
	 * 回傳待簽署清單
	 * @param sysCode
	 * @param servletRequest
	 * @param response
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(
		value = "/{SYS_CODE}/getSignList", 
		method = RequestMethod.POST , 
		consumes = AP_JSON_CHARSET_UTF8,  //指定處理request的submit的類型
		produces = AP_JSON_CHARSET_UTF8   //返回的內容類型
	)
	@ResponseBody
	public String getSignList(
		@PathVariable("SYS_CODE") String sysCode , 
		HttpServletRequest servletRequest ,
		HttpServletResponse response , 
		@RequestBody String json 
	) 
	throws Exception {
		logger.info("#[InsSigExternalWebService getSignList request json] : " + json);
		
		Map param = (Map)JsonUtil.genDefaultGson().fromJson(json, HashMap.class);
				
		String reqName = HttpServletRequest.class.getName();
		GenericMap paramGenMap = new GenericMap(param);
		paramGenMap.put(reqName , servletRequest);
		
		String responseStr = JsonUtil.genDefaultGson().toJson(
				wmsInsSigService.getSignList(paramGenMap).getParamMap()
		);
		
		return responseStr;
	}	
	
	/**
	 * 人壽傳入簽署後狀態資料
	 * @param sysCode
	 * @param servletRequest
	 * @param response
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(
			value = "/{SYS_CODE}/setCaseStatus", 
			method = RequestMethod.POST , 
			consumes = AP_JSON_CHARSET_UTF8,  //指定處理request的submit的類型
			produces = AP_JSON_CHARSET_UTF8   //返回的內容類型
		)
	@ResponseBody
	public String setCaseStatus(
		@PathVariable("SYS_CODE") String sysCode , 
		HttpServletRequest servletRequest ,
		HttpServletResponse response , 
		@RequestBody String json
	) 
	throws Exception {
		logger.info("#[InsSigExternalWebService setCaseStatus request json] : " + json);
		
		Map param = (Map)JsonUtil.genDefaultGson().fromJson(json, HashMap.class);
				
		String reqName = HttpServletRequest.class.getName();
		GenericMap paramGenMap = new GenericMap(param);
		paramGenMap.put(reqName , servletRequest);
		
		String responseStr = JsonUtil.genDefaultGson().toJson(
				wmsInsSigService.setCaseStatus(paramGenMap).getParamMap()
		);
		
		return responseStr;
	}
	
	/**
	 * 人壽傳入簽署後電子要保書PDF
	 * @param sysCode
	 * @param servletRequest
	 * @param response
	 * @param pdfFile
	 * @param caseId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(
			value = "/{SYS_CODE}/setCasePDF", 
			method = RequestMethod.POST , 
			consumes = MULTIPART_FORM_DATA,  //指定處理request的submit的類型
			produces = AP_JSON_CHARSET_UTF8   //返回的內容類型
		)
	@ResponseBody
	public String setCasePDF(
		@PathVariable("SYS_CODE") String sysCode , 
		HttpServletRequest servletRequest ,
		HttpServletResponse response,
		@RequestParam("pdfFile") MultipartFile pdfFile,
		@RequestParam("caseId") String caseId
	) 
	throws Exception {
		logger.info("#[InsSigExternalWebService setCasePDF request caseId] : " + caseId);
				
		String reqName = HttpServletRequest.class.getName();
		GenericMap paramGenMap = new GenericMap();
		paramGenMap.put(reqName , servletRequest);
		paramGenMap.put("caseId" , caseId);
		paramGenMap.put("pdfFile" , pdfFile);

		String responseStr = JsonUtil.genDefaultGson().toJson(
				wmsInsSigService.setCasePDF(paramGenMap).getParamMap()
		);
		
		return responseStr;
	}
}
