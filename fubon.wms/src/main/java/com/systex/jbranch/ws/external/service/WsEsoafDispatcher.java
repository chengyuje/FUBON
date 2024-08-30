package com.systex.jbranch.ws.external.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.encrypt.aes.AesEncryptDecryptUtils;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.JsonService;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.ThreadDataPool;
import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;
import com.systex.jbranch.platform.server.conversation.message.Tia;
import com.systex.jbranch.platform.server.conversation.message.Toa;
import com.systex.jbranch.platform.server.conversation.message.payload.Msg;
import com.systex.jbranch.platform.server.info.fubonuser.WmsUser;
import com.systex.jbranch.platform.server.pipeline.flex.CommonConstantHelper;
import com.systex.jbranch.platform.server.pipeline.flex.PipelineUtil;
import com.systex.jbranch.platform.server.pipeline.flex.RemotingService;
import com.systex.jbranch.servlet.EsoafDispatcher;
import com.systex.jbranch.servlet.serializer.DateSerializer;
import com.systex.jbranch.servlet.serializer.SqlDateSerializer;
import com.systex.jbranch.servlet.serializer.TimestampSerializer;
import com.systex.jbranch.servlet.vo.Header;
import com.systex.jbranch.ws.external.service.dao.ExtjlbDao;

@Controller("WsEsoafDispatcher")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@SuppressWarnings({ "unchecked", "rawtypes" , "unused"})
public class WsEsoafDispatcher {
	private Logger logger = LoggerFactory.getLogger(EsoafDispatcher.class);
	private static final long serialVersionUID = 1L;
	private static final String ENCODING = "UTF-8";
	private static final String TXN_CODE = "TxnCode";
	private static final String BIZ_CODE = "BizCode";
	private static final String INPUT = "inputVo";
	private static final String OUTPUT = "outputVo";
	private static final String HEADER = "header";
	private static final String BODY = "body";
	private static final String OUTPUT_TYPE = "outputType";
	private static final String ONLY_BODY = "onlyBody";
	private static final String ONLY_HEADER_BODY = "onlyHeaderBody";
	
	@Autowired @Qualifier("ExternalConfigFactory")
	private ExternalConfigFactory externalConfigFactory;

	@Autowired
	private JsonService jsonService;
	
	private Validator validator;
	
	public Map dispatcher(String mappingId , InputStream in , HttpServletRequest request) throws ServletException, IOException {
		ThreadDataPool.setData(PipelineUtil.HTTP_SERVLET_REQUEST, request);
		Map resultMap = new HashMap();
		Class outputCls = null;
		String tota = "";
		
		//設定ip
		String ip = PipelineUtil.getRemoteAddr();
		MDC.put(CommonConstantHelper.IP, ip);

		//傳參
		StringBuffer titaStringResult = new StringBuffer();
		JsonElement element = null;

		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, ENCODING);	
			titaStringResult.append(writer.toString());
			writer.close();
			in.close();
			
			//把json字串轉成JsonElement
			element = new JsonParser().parse(titaStringResult.toString());
			JsonElement headerJsonObject = element.getAsJsonObject().getAsJsonObject("header");
			
			if (headerJsonObject == null) {
				throw new JsonSyntaxException("缺少header");
			}
			
			Gson gson = initGon();
			Header header = gson.fromJson(headerJsonObject.toString(), Header.class);
			
			//找出對應的設定			
			ExternalConfig extConfig = externalConfigFactory.doGetExternalConfig(mappingId);
			String inputVoPath = extConfig.getInputVoPath();
			String outputVoPath = extConfig.getOutVoPath();
			String outputType = extConfig.getOutputType();
			
			if (StringUtils.isBlank(inputVoPath)) {
				throw new JBranchException("未輸入InputVO");
			}
			
			resultMap.put("config" , extConfig);
			
			//設定inputVo型態
			header.setInputVOClass(inputVoPath);
			
			//取得output型態
			outputCls = Class.forName(outputVoPath);
			
			Map<String, Object> headerMap = EsoafDispatcher.valueObjectToMap(header);
			headerMap.put("TxnCode", extConfig.getTxnCode());
			headerMap.put("BizCode", extConfig.getBizCode());
			headerMap.put(EnumTiaHeader.ApplicationID.toString() , request.getAttribute("applicationId"));
			
			ExtjlbDao extjlbDao = (ExtjlbDao) PlatformContext.getBean("ExtjlbDao");
			
			Object body = null;

			Class<?> inputVOClass = Class.forName(inputVoPath);
			JsonElement bodyJsonObject = element.getAsJsonObject().get("body");
			
			body = gson.fromJson(bodyJsonObject , 
				(bodyJsonObject instanceof JsonArray) ? 
				ArrayList.class : inputVOClass
			);
			
			ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
	        validator = validatorFactory.getValidator();
			Set<ConstraintViolation<Object>> violations = validator.validate(body);
			StringBuffer errorList = new StringBuffer();
			
	        for (ConstraintViolation violation : violations){
	            String propertyPath = violation.getPropertyPath().toString();
	            String message = violation.getMessage();
	            
	            errorList.append(message + ",");
	            logger.error(inputVoPath + " Invalid "+ propertyPath + "(" + message + ")");
	        }
	        
		    if (errorList.length() > 0) {
		    	throw new ValidationException(errorList.toString().substring(0, errorList.toString().length() - 1));
			}
			
			Tia origiTia = new Tia();
			origiTia.setHeaders(headerMap);
			origiTia.setBody(body);
			
			//@Autowired @Qualifier("WsRemotingService")
			//private 
			WsRemotingService wsRemotingService = (WsRemotingService) PlatformContext.getBean("WsRemotingService");
			List<Toa> flexToaList = wsRemotingService.invoke(origiTia);
			int toaLen = flexToaList.size();
			
			if(ONLY_BODY.equals(outputType) || ONLY_HEADER_BODY.equals(outputType)){
				for(int i = 0; i < toaLen; i++){
					Toa toa = (Toa) flexToaList.get(i);
	        		Object object = toa.getBody();
	        		
	        		//當有回傳值，或是例外訊息時，將訊息帶入
	        		boolean isActionResult = object != null && ( 
						object.getClass().isAssignableFrom(outputCls) ||
						object.getClass().isAssignableFrom(ExternalErrorMsg.class) ||
						Msg.class.isAssignableFrom(object.getClass())
					);
	        		
	        		if(isActionResult){
	        			Map resultHeaderBody = new HashMap();
	        			Map headerMaps = toa.getHeaders();
	        			resultHeaderBody.put(BODY , object);
						
	        			if(ONLY_HEADER_BODY.equals(outputType)){
	        				resultHeaderBody.put(HEADER , toa.getHeaders());
	        			}
	        			
	        			resultMap.put("result", resultHeaderBody);
	        			
	        			return resultMap;
	        		}
				}
			}
			else {
				List list = new ArrayList();
				for(int i = 0; i < toaLen; i++){
					Toa toa = (Toa) flexToaList.get(i);
	        		Object object = toa.getBody();
	        		
	        		//當有回傳值，或是例外訊息時，將訊息帶入
	        		boolean isActionResult = object != null && ( 
						object.getClass().isAssignableFrom(outputCls) ||
						object.getClass().isAssignableFrom(ExternalErrorMsg.class) ||
						Msg.class.isAssignableFrom(object.getClass())
					);
	        		
	        		if(isActionResult){
	        			Map headerMaps = toa.getHeaders();
	        			resultMap.put(BODY , object);
	        			resultMap.put(HEADER , toa.getHeaders());
	        			list.add(resultMap);
	        		}
				}
				
				resultMap.put("result", list);
			}

		}catch (ClassNotFoundException e) {
			String errorMessage = "找不到InputVO:" + e.getMessage();
			logger.error(errorMessage, e);
			tota = getErrorMessageJson(errorMessage);
		}catch (ValidationException e) {
			String[] errorMessages = e.getMessage().split(",");
			logger.error(e.getMessage(), e);
			tota = getErrorMessageJson(errorMessages);
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			tota = getErrorMessageJson(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			tota = getErrorMessageJson(e.getMessage());
		} catch (JBranchException e) {
			logger.error(e.getMessage(), e);
			tota = getErrorMessageJson(e.getMessage());
		} catch(JsonSyntaxException e){
			String errorMessage = "Json格式錯誤:"+e.getMessage();
			logger.error(errorMessage, e);
			tota = getErrorMessageJson(errorMessage);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			tota = getErrorMessageJson(e.getMessage());
		} finally{
			if(StringUtils.isNotBlank(tota)){
				tota = "[" + tota + "]";
			}
			
			logger.debug("tota={}", tota);
		} 

		return resultMap;
	}
	
	public CallBackExcute doGetResponse(Map config){
		String outputType = ObjectUtils.toString(config.get(OUTPUT_TYPE));
		return null;
	}
	
	private Gson initGon() {
		return new GsonBuilder()
		.registerTypeAdapter(java.util.Date.class, new DateSerializer())
		.registerTypeAdapter(java.sql.Date.class, new SqlDateSerializer())
		.registerTypeAdapter(java.sql.Timestamp.class, new TimestampSerializer())
		.serializeNulls()
		.create();
	}
	
	public String parseToJson(Object obj){
		return jsonService.toJson(obj);
	}
	
	private String getErrorMessageJson(String errorMessage) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("{\"header\":{\"EndBracket\":false,\"OutputType\":\"Message\"}, \"body\":{\"showType\":\"Show\",\"messageType\":\"Error\",\"msgCode\":\"Z999\",\"msgData\":" + jsonService.toJson(errorMessage) + "}},");
		sb.append("{\"header\":{\"EndBracket\":false,\"TXN_DATA\":{},\"GW_HOST_NAME\":\"\",\"OutputType\":\"EndBracket\",\"AP_HOST_NAME\":\"\"}, \"body\":{\"result\":\"error\"}}");
		sb.append("]");
		return sb.toString();
	}
	
	private String getErrorMessageJson(String[] errorMessages) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("{\"header\":{\"EndBracket\":false,\"OutputType\":\"Message\"}, \"body\":{\"showType\":\"Show\",\"messageType\":\"Error\",\"msgCode\":\"Z999\",\"msgData\":" + jsonService.toJson(errorMessages) + "}},");
		sb.append("{\"header\":{\"EndBracket\":false,\"TXN_DATA\":{},\"GW_HOST_NAME\":\"\",\"OutputType\":\"EndBracket\",\"AP_HOST_NAME\":\"\"}, \"body\":{\"result\":\"error\"}}");
		sb.append("]");
		return sb.toString();
	}
}
