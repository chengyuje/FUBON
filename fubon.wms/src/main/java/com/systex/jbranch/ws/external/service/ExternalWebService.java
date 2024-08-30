package com.systex.jbranch.ws.external.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
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

import com.systex.jbranch.app.server.fps.cmfpg000.CMFPG000;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.ws.external.service.dao.ExtjlbDaoInf;
import com.systex.jbranch.platform.server.conversation.message.payload.Msg;

@RestController
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequestMapping(value = "/ws")
public class ExternalWebService {	
	public static final String AP_JSON_CHARSET_UTF8 = "application/json;charset=UTF-8";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private WsEsoafDispatcher wsEsoafDispatcher;
	
	@Autowired
	private ExtjlbDaoInf extjlbDao;
	
	@Autowired @Qualifier("ExternalConfigFactory")
	private ExternalConfigFactory externalConfigFactory;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(
		value = "/mobile/getReferenceData", 
		method = RequestMethod.POST , 
		consumes = AP_JSON_CHARSET_UTF8,  //指定處理request的submit的類型
		produces = AP_JSON_CHARSET_UTF8   //返回的內容類型
	)
	@ResponseBody
	public String mobileGetReferenceData(
		HttpServletRequest request ,
		HttpServletResponse response , 
		@RequestBody String json
	) 
	throws Exception {
		ExternalConfig extConfig = externalConfigFactory.doGetExternalConfig("getReferenceData");
		
		String jsonStr = extConfig.getBeforeFormat().callBack(new GenericMap()
			.put("requestJson", json)
			.put(HttpServletRequest.class, request)
			.put(HttpServletResponse.class, response)
		);
		
		//回傳字串
 		String responseJson = null;
		//json請求內容
		Map map = JsonUtil.genDefaultGson().fromJson(jsonStr , HashMap.class);
		//header區塊資料
		Map headerMap = (Map)map.get("header");
		
		//轉換為串流
		Map resultMap = new HashMap();
		Map result = new HashMap();
		result.put("header", headerMap);
		
		try{
			CMFPG000 cmfpg000 = (CMFPG000) PlatformContext.getBean(CMFPG000.class.getSimpleName().toLowerCase());
			result.put("body", cmfpg000.getReferenceData());
			resultMap.put("result", result);
		}
		catch(Exception ex){
			ex.printStackTrace();
			Msg msg = new Msg();
			msg.setMsgCode("E0001");
			msg.setMsgData(ex.getMessage());
			result.put("body", msg);
			headerMap.put("OutputType" , "Message");
		}
    	
    	responseJson = extConfig.getReturnFormat().callBack(new GenericMap()
    		.put("responseObj", resultMap)
    		.put(HttpServletRequest.class, request)
    		.put(HttpServletResponse.class, response)
    	);
    	
    	return responseJson;
	}

	@RequestMapping(
		value = "/mobile/{mappingId}", 
		method = RequestMethod.POST , 
		consumes = AP_JSON_CHARSET_UTF8,  //指定處理request的submit的類型
		produces = AP_JSON_CHARSET_UTF8   //返回的內容類型
	)
	@ResponseBody
	public String callerServerMobile(
		@PathVariable("mappingId") String mappingId , 
		HttpServletRequest request ,
		HttpServletResponse response , 
		@RequestBody String json
	) 
	throws Exception {
		ExternalConfig extConfig = externalConfigFactory.doGetExternalConfig(mappingId);
		
		String jsonStr = extConfig.getBeforeFormat().callBack(new GenericMap()
			.put("requestJson", json)
			.put(HttpServletRequest.class, request)
			.put(HttpServletResponse.class, response)
		);

		System.out.println("#" + jsonStr);
		return callerServer(mappingId  , request , response , jsonStr);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(
		value = "/{mappingId}" , 
		method = RequestMethod.POST , 
		consumes = AP_JSON_CHARSET_UTF8,  //指定處理request的submit的類型
		produces = AP_JSON_CHARSET_UTF8   //返回的內容類型
	)
	@ResponseBody
	public String callerServer(
		@PathVariable("mappingId") String mappingId , 
		HttpServletRequest request , 
		HttpServletResponse response , 
		@RequestBody String json
	) 
	throws JBranchException, ServletException, IOException {		
		//回傳字串
 		String responseJson = null;
		//json請求內容
		Map map = JsonUtil.genDefaultGson().fromJson(json, HashMap.class);
		//header區塊資料
		Map headerMap = (Map)map.get("header");
		//轉換為串流
		InputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));	
		Map resultMap = null;
    	
		try{
			resultMap = wsEsoafDispatcher.dispatcher(mappingId , in , request);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		//取得處理結果
    	Map result = (Map)resultMap.get("result");
    	resultMap.put("result", result = result == null ? new HashMap() : result);
    	
    	if(result.get("header") != null){
    		((Map)result.get("header")).putAll(headerMap);
    	}
    	else{
    		result.put("header", headerMap);
    		result.put("body", new HashMap());
    	}
    	
    	ExternalConfig extConfig = (ExternalConfig) resultMap.get("config");
    			
    	responseJson = extConfig.getReturnFormat().callBack(new GenericMap()
    		.put("responseObj", resultMap)
    		.put(HttpServletRequest.class, request)
    		.put(HttpServletResponse.class, response)
    	);
    	
    	return responseJson;
	}
	
}
