package com.systex.jbranch.fubon.webservice.rs;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.fubon.commons.http.client.HttpClientJsonUtils;

@Service("seniorCitizenClientRS")
@Scope("request")
public class SeniorCitizenClientRS {
	
	/*
	 * 取得JSON
	 * 
	 * url：參數URL
	 *   樂齡專案：SYS.SENIOR_CITIZEN_URL
	 * inputGmap:傳入參數
	 * 
	 * API回傳：{KEY:VALUE}
	 */
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Map<String, Object> getMap(String url, GenericMap inputGmap) throws Exception {
		
		Gson gson = JsonUtil.genDefaultGson();
		
		GenericMap genericMap = HttpClientJsonUtils.sendDefJsonRequest(url, gson.toJson(inputGmap.getParamMap()), null);
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(genericMap.getNotNullStr("body"));
				
		Map<String, Object> map = objectMapper.convertValue(jsonNode, Map.class);
		
		return map;
	}
	

	/*
	 * 取得JSON
	 * 
	 * url：參數URL
	 *   樂齡專案：SYS.SENIOR_CITIZEN_URL
	 * inputGmap:傳入參數
	 * 
	 * API回傳：[{KEY:VALUE}, {KEY:VALUE}, ...]
	 */
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<Map<String, Object>> getList(String url, GenericMap inputGmap) throws Exception {
		
		Gson gson = JsonUtil.genDefaultGson();
		
		GenericMap genericMap = HttpClientJsonUtils.sendDefJsonRequest(url, gson.toJson(inputGmap.getParamMap()), null);
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(genericMap.getNotNullStr("body"));
				
		List<Map<String, Object>> list = objectMapper.convertValue(jsonNode, List.class);
			
		return list;
	}

}