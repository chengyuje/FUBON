package com.systex.jbranch.ws.external.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.encrypt.aes.AesEncryptDecryptUtils;
import com.systex.jbranch.comutil.parse.JsonUtil;

@Controller
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ExternalWebViewController {	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private WsEsoafDispatcher wsEsoafDispatcher;
	
	@Autowired @Qualifier("ExternalConfigFactory")
	private ExternalConfigFactory externalConfigFactory;
	
	
	@RequestMapping(value = "{mpid}.vw" , method = RequestMethod.GET)
	public ModelAndView forwordToWebView(
		HttpServletRequest request , 
		HttpServletResponse response , 
		@PathVariable("mpid") String mappingId
	) throws Exception{
		System.out.println("doViewStart：" + new Date().getTime());
		String initJsPath = null;
		
		request.setAttribute("mpUrl", externalConfigFactory.getWebViewPathMapping().get(mappingId));

		initJsPath = ObjectUtils.toString(externalConfigFactory.getWebViewJsMapping().get(mappingId));
		boolean isNoParam = StringUtils.isEmpty(initJsPath);
		initJsPath = isNoParam ? ObjectUtils.toString(externalConfigFactory.getWebViewJsMapping().get("DEFAULT")) : initJsPath;
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("webview");
		mav.getModel().put("initJsPath", initJsPath);
		mav.getModel().put("isNoParam", isNoParam);
		
		System.out.println("initJsPath：" + initJsPath);
		System.out.println("isNoParam：" + isNoParam);
		System.out.println("doViewEnd：" + new Date().getTime());
		
		return mav;
	}
	
	@RequestMapping(
		value = "reSysInfo/{mappingId}" , 
		method = RequestMethod.POST , 
		consumes = ExternalWebService.AP_JSON_CHARSET_UTF8,  //指定處理request的submit的類型
		produces = ExternalWebService.AP_JSON_CHARSET_UTF8   //返回的內容類型
	)
	@ResponseBody
	public String reSysInfo(
		HttpServletRequest request , 
		HttpServletResponse response , 
		@PathVariable("mappingId") String mappingId,
		@RequestBody String json
	) throws Exception{
		System.out.println("doSysInfoStart：" + new Date().getTime());
		Map param = JsonUtil.genDefaultGson().fromJson(json, HashMap.class);
		String uuid = ObjectUtils.toString(param.get("uuid"));
		uuid = decryptApid(mappingId , uuid);
		
		Matcher matcher = null;
		String wsid = null;
		String applicationId = null;
		String ip = null;
		String branchID = null;
		String tellerId = null;
		String sectionID = null;
		
		if((matcher = Pattern.compile("wsid=.*,branchID").matcher(uuid)).find()){
			wsid = matcher.group().replaceAll("(wsid=)|,branchID", "");
			String [] wsidAr = wsid.split("_");
			ip = wsidAr[0];
			applicationId = wsidAr[1];
		}
		
		if((matcher = Pattern.compile(",branchID=.*,tellerId").matcher(uuid)).find()){
			branchID = matcher.group().replaceAll("(,branchID=)|,tellerId", "");
		}
		
		if((matcher = Pattern.compile(",tellerId=.*,sectionID").matcher(uuid)).find()){
			tellerId = matcher.group().replaceAll("(,tellerId=)|,sectionID", "");
		}
		
		if((matcher = Pattern.compile(",sectionID=.*$").matcher(uuid)).find()){
			sectionID = matcher.group().replaceAll(",sectionID=", "");
		}
		
		request.setAttribute("applicationId", applicationId);
		
		//header
		GenericMap headerGmap = new GenericMap()
			.put("IS_AES", 0)
			.put("applicationId", applicationId)
			.put("isAES", 0)
			.put("ApplicationID", applicationId)
			.put("BranchID",  branchID)
			.put("WsID",  wsid)
			.put("SectionID",  sectionID)
			.put("TlrID",  tellerId);
			
		String paramJson = JsonUtil.genDefaultGson().toJson(new GenericMap()
			.put("header" , headerGmap.getParamMap())
			.put("body", new HashMap())
			.getParamMap()
		);
		
		InputStream paramStream = new ByteArrayInputStream(paramJson.getBytes(StandardCharsets.UTF_8));
		
		Map sysInfo = wsEsoafDispatcher.dispatcher("loadSysInfoData" , paramStream , request);
		sysInfo.putAll(headerGmap.getParamMap());
		System.out.println("sysInfo：" + sysInfo);
		System.out.println("doSysInfoEnd：" + new Date().getTime());
		return JsonUtil.genDefaultGson().toJson(sysInfo);
	}
	
	
	private Map doGetDefJsonParam(String applicationId){
		//header
		GenericMap headerGmap = new GenericMap()
			.put("IS_AES", 0)
			.put("applicationId", applicationId);
		
		//body
		GenericMap bodyGmap = new GenericMap();
		
		//{"header":{} , "body" :{}}
		return new GenericMap()
			.put("header" , headerGmap.getParamMap())
			.put("body", bodyGmap.getParamMap())
			.getParamMap();
	}
	
	public String decryptApid(String mappingId , String uuid) throws Exception{
		String reApid = mappingId;
		uuid = new String(Base64.decodeBase64(uuid.getBytes("utf-8")));
		
		for(int i = 0 ; i < 32 - mappingId.length(); i++){
			reApid += "0";
		}
		return uuid = AesEncryptDecryptUtils.decryptAesEcbPkcs7Padding(reApid , uuid);
	}
}

