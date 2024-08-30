package com.systex.jbranch.fubon.commons.cbs.inf;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CoreTodoClient {

	public static void main(String[] args) throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://10.7.20.71:8085/getCoreTodo");
		httpPost.setHeader("Content-type", "application/json");
		StringEntity se = new StringEntity("{\"CUST_ID\":\"F101761142\"}", "UTF-8");
		httpPost.setEntity(se);
		HttpResponse resp = httpClient.execute(httpPost);
		HttpEntity entity = resp.getEntity();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(EntityUtils.toString(entity));
		System.out.println(jsonNode.get("TODO").asText());
	}

}
