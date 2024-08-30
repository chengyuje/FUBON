package com.systex.jbranch.ws.external.service.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.fubon.commons.http.client.HttpClientJsonUtils;
import com.systex.jbranch.fubon.commons.http.client.callback.DefHeaderCallBack;
import com.systex.jbranch.commons.soap.HttpClientSoapUtils;


public class WsTest {
	private final String DEF_AES_KEY = "87654321876543218765432187654321";
	private final boolean IS_AES = true;
	private static String ip = null;
	// private final String url = "http://" + ip + "/wms/";
	private final String url = "http://" + ip + ":8080/";
	private final String empId = "186660";

	
	
	public static void main(String...args) throws Exception{
		//test1();
		String url = "https://mappplus.fbl.com.tw:8480/UTApi3/Api/Case/E_GetAgentInfo";
		Map map = new HashMap();
		   
		GenericMap result = HttpClientJsonUtils.sendJsonRequest(
		   url , 
		   "{ \"WsID\":\"46hajwPQphcHYtPqZYBfWQ\", \"WsPwd\":\"H2PRPmNwToOXymI77rfA7Q\", \"SysCode\":\"TPFB_APP\", \"ClientIP\":\"10.42.70.1\", \"CaseId\":\"J059793930\" }", 
		   900000, new HashMap() , 
		   new DefHeaderCallBack()
		 );
		
		System.out.println(result.getParamMap());
	}
	
	
	public static void test1() throws Exception{
		String url = "http://10.42.70.194/FBMappWebService/FBMappWebService.asmx";
		Map<String, Object> body = new HashMap();
		body.put("WsID" , "46hajwPQphcHYtPqZYBfWQ"); 
		body.put("WsPwd" , "H2PRPmNwToOXymI77rfA7Q"); 
		body.put("SysCode" , "TPFB_APP"); 
		body.put("ClientIP" , "10.42.70.1"); 
		body.put("CaseId" , "J059793930");
		
		//MAP TO JSON STR
		String json = JsonUtil.genDefaultGson().toJson(body);
		
		//SNED REST WEB SERVICE
		//System.out.println(sendJson(url, json, new HashMap()).getParamMap());
		
		

		String testJson = "{"
		+  "	    \"CODE\":\"200\","
		+  "	    \"ERROR_MESSAGE\":\"\","
		+  "	    \"CASE_DATA\":{"
		+  "			\"CASE_ID\":\"J001002003\","
		+  "			\"DATA_DISP\":\"733162054\","
		+  "			\"SING_SALE_ID\":\"M222222085\","
		+  "			\"I_ID\":\"A223898832\","
		+  "			\"I_NAME\":\"彥Ｘ-給付&帳號9999999999999999999999\","
		+  "			\"A_ID\":\"A141222003\","
		+  "			\"A_NAME\":\"彥Ｘ-附加附約\","
		+  "			\"A_BIRTH\":\"0700606\","
		+  "			\"MINS_KIND\":\"XWO1\","
		+  "			\"MINS_PAYMENT_YEAR\":\"65\","
		+  "			\"MINS_PREMIUM\":\"35000\","
		+  "			\"MOP\":\"A\","
		+  "			\"AINS_KIND\":[\"HKR\",	\"HSA5\",	\"XHR1\"],"
		+  "			\"ONEAINS_KIND\":[\"ADC\",\"AHR\",\"211R\"],"
		+  "			\"SMINS_EXEMPT\":[\"WPA\"],"
		+  "			\"FPAY_DIND\":\"2\","
		+  "			\"DECLARE_DATE\":\"1050302\","
		+  "			\"FUND_CODE1\":[\"F0692\",	\"NGB10\",	\"MLE20\"],"
		+  "			\"INST_PREM_UALL_PERT1\":[\"F0692\",	\"NGB10\",	\"MLE20\"]"
		+  "		}"
		+  "	}";

		//JSON STR TO MAP
		Map map = JsonUtil.genDefaultGson().fromJson(testJson, HashMap.class);
		String caseIdString = ((Map)map.get("CASE_DATA")).get("CASE_ID").toString();
		System.out.println(map);
	}


		
	
	
	public static GenericMap sendJson(String url, String json, Map<String, Object> headerMap) throws Exception {
		CloseableHttpClient httpclient = url.matches("^https.*") ? 
			HttpClientSoapUtils.createSSLInsecureClient() : HttpClientBuilder.create().build();
			
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(json, "utf-8"));
		httpPost.setHeader("Content-type", "application/json");

		GenericMap responseGmap = new GenericMap();
		
		for (String key : headerMap.keySet()) {
			httpPost.setHeader(key, ObjectUtils.toString(headerMap.get(key)));
		}

		CloseableHttpResponse response = null;
		int time = 300000;
		RequestConfig config = RequestConfig.custom().setSocketTimeout(time)
			.setConnectTimeout(time).setConnectionRequestTimeout(time)
			.build();

		try {
			httpPost.setConfig(config);
			// logger.info("send ws start");
			response = httpclient.execute(httpPost);

			if (response.getHeaders("ApplicationID") != null && response.getHeaders("ApplicationID").length > 0) {
				responseGmap.put("ApplicationID" , response.getHeaders("ApplicationID")[0].getValue());
			}

			// logger.info("send ws end");
			if (response != null && response.getEntity() != null) {
				return responseGmap.put("body" , EntityUtils.toString(response.getEntity(), "utf-8"));
			}
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseGmap;
	}
}
