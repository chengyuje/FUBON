package com.systex.jbranch.ws.external.service.test;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonSyntaxException;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.encrypt.aes.AesEncryptDecryptUtils;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.fubon.commons.http.client.HttpClientJsonUtils;
import com.systex.jbranch.fubon.commons.http.client.callback.DefHeaderCallBack;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MobileLoginTest {
	enum TestType{
		LOCAL(doGetLocalUrl()) , 
		SIT("203.74.183.85:9801/sit");
		private String url;
		
		TestType(String url){
			this.url = url;
		}
		
		public String getUrl(){
			return url;
		}
	}
	
	//預設AES256密鑰
	static final String DEF_AES_KEY = "87654321876543218765432187654321";
	//設定是否AES256加密
	static final boolean IS_AES = false;
	//設定執行環境
	static final TestType testType = TestType.LOCAL;
	//選擇測試的環境
	static final String url = "http://" + testType.getUrl() + "/";
	//設定登入帳號
	static final String empId = "005889";
	
	public static String doGetLocalUrl(){
		InetAddress localIp = null;
		String url = null;
		try {
			localIp = InetAddress.getLocalHost();
			//TODO:port請自行依照自己的環境調整
			url = localIp.getHostAddress() + ":8080";
			url = "10.204.2.127:8080";
			url = "192.168.43.32:8080";
		} 
		catch (Exception une) {
			une.printStackTrace();
		};
		
		return url;
	}
	
	/** 取得客戶清單 **/
	public Map getCustomerList(String applicationId , Map uuid) throws Exception{
		String gotoUrl = url + "ws/mobile/getCustomerList.serv";
		Map<String, Object> headerMap = doGetReqJsonHeaderInstance(applicationId , uuid);
		Map jsonMap = new GenericMap(doGetReqJsonBodyInstance()).getParamMap();
		//jsonMap.put("empid", empId);
		String jsonBodyStr = formatToJsonStr(jsonMap , applicationId);
		return doGetContent(sendJson(gotoUrl , jsonBodyStr , headerMap) , applicationId);
	}
	
	/** 取得客戶清單 **/
	public Map getFps200(String applicationId , Map uuid) throws Exception{
		String gotoUrl = url + "ws/mobile/getFinancialRefData.serv";
		Map<String, Object> headerMap = doGetReqJsonHeaderInstance(applicationId , uuid);
		Map jsonMap = new GenericMap(doGetReqJsonBodyInstance()).getParamMap();
		jsonMap.put("custId", "C200481206");
		jsonMap.put("riskLevel", "C1-1");
		String jsonBodyStr = formatToJsonStr(jsonMap , applicationId);
		return doGetContent(sendJson(gotoUrl , jsonBodyStr , headerMap) , applicationId);
	}
	
	/** 取得客戶詳細資料 **/
	public Map getCustomerDetail(String applicationId , Map uuid) throws Exception{
		String gotoUrl = url + "ws/mobile/getCustomerDetail.serv";
		Map<String, Object> headerMap = doGetReqJsonHeaderInstance(applicationId , uuid);
		Map jsonMap = new GenericMap(doGetReqJsonBodyInstance()).getParamMap();
		jsonMap.put("custId", "T120772291");
		
		String jsonBodyStr = formatToJsonStr(jsonMap , applicationId);
		return doGetContent(sendJson(gotoUrl , jsonBodyStr , headerMap) , applicationId);
	}
	
	/** 得共用資料 **/
	public Map getReferenceData(Map uuid) throws Exception {
		String gotoUrl = url + "ws/mobile/getReferenceData.serv";
		Map<String, Object> headerMap = doGetReqJsonHeaderInstance();
		Map jsonMap = new GenericMap(doGetReqJsonBodyInstance()).getParamMap();
		String jsonBodyStr = formatToJsonStr(jsonMap , null);
		return doGetContent(sendJson(gotoUrl , jsonBodyStr , headerMap) , null);
	}
	
	/** 登出 **/
	public Map logout(String applicationId , Map uuid) throws Exception {
		String gotoUrl = url + "ws/mobile/logout.serv";
		Map<String, Object> headerMap = doGetReqJsonHeaderInstance(applicationId , uuid);
		Map jsonMap = new GenericMap(doGetReqJsonBodyInstance()).getParamMap();
		String jsonBodyStr = formatToJsonStr(jsonMap , applicationId);
		return doGetContent(sendJson(gotoUrl , jsonBodyStr , headerMap) , applicationId);
	}
	
	/** 更新登入狀態 **/
	public Map alive(String applicationId , Map uuid) throws Exception{
		String gotoUrl = url + "ws/mobile/alive.serv";
		Map<String, Object> headerMap = doGetReqJsonHeaderInstance(applicationId , uuid);
		Map jsonMap = new GenericMap(doGetReqJsonBodyInstance()).getParamMap();
		String jsonBodyStr = formatToJsonStr(jsonMap , applicationId);
		return doGetContent(sendJson(gotoUrl , jsonBodyStr , headerMap) , applicationId);
	}
	

	/** 登入 **/
	public GenericMap getLoginInfo(String applicationId , Map user) throws Exception {
		String gotoUrl = url + "ws/mobile/getLoginInfo.serv";

		// 檢核帳密並取得登入清單
		Map<String, Object> headerMap = doGetReqJsonHeaderInstance(applicationId , new HashMap());

		// 登入
		GenericMap resGmap = new GenericMap(user);

		Map jsonMap = new GenericMap(doGetReqJsonBodyInstance())
			.put("empId", resGmap.get("empId"))
			.put("roleId", resGmap.get("roleId"))
			.put("deptId", resGmap.get("deptId"))
			.put("regionCenterId", resGmap.get("regionCenterId"))
			.put("regionCenterName", resGmap.get("regionCenterName"))
			.put("branchAreaId", resGmap.get("branchAreaId"))
			.put("branchAreaName", resGmap.get("branchAreaName"))
			.put("branchNbr", resGmap.get("branchNbr"))
			.put("branchName", resGmap.get("branchName"))
			.put("isPrimaryRole", resGmap.get("isPrimaryRole"))
			.put("currentUserId", empId)
			.getParamMap();

		String jsonBodyStr = formatToJsonStr(jsonMap , applicationId);
		Map content = doGetContent(sendJson(gotoUrl , jsonBodyStr , headerMap) , applicationId);
		
		return new GenericMap()
			.put("applicationId", applicationId)
			.put("content", content);
	}

	/** 驗證ldap，並取得登入者各種腳色 **/
	public GenericMap login() throws Exception {
		String gotoUrl = url + "ws/mobile/login.serv";
		Map<String, Object> headerMap = doGetReqJsonHeaderInstance();

		Map jsonMap = doGetReqJsonBodyInstance();
		jsonMap.put("account", empId);
		
		String pwd = new String(org.apache.commons.codec.binary.Base64.encodeBase64("4321".getBytes()));
		jsonMap.put("pwd", pwd);

		String jsonBodyStr = formatToJsonStr(jsonMap);
		GenericMap response = sendJson(gotoUrl, jsonBodyStr, headerMap);
		Map header = response.get("header");
		Map content = doGetContent(response);
	
		boolean contentIsEmpty = MapUtils.isEmpty(content);
	
		return new GenericMap()
			.put("applicationId", content.get("applicationId"))
			.put("userList", contentIsEmpty ? null : content.get("userList"));
	}
	
	public Map doGetContent(GenericMap response) throws Exception{
		return doGetContent(response , null);
	}
	
	public Map doGetContent(GenericMap response , String applicationId) throws Exception{
		String result = response.getNotNullStr("body");
		GenericMap resGmap = new GenericMap(parseBody(result , applicationId));
		
		if(resGmap.nEquals("retCode", "0000")){
			throw new Exception(resGmap.getNotNullStr("retMsg"));
		}
		
		return resGmap.get("content");
	}

	/** 產生一個Map，並給予初始的共用參數，用於組request json用 **/
	public Map doGetReqJsonBodyInstance() {
		return new GenericMap().put("model", "iPad Mini")
				.put("deviceVersion", "10/11.3").put("appVersion", "")
				.getParamMap();
	}

	/** 產生一個Map，並給予初始的共用參數，用於組request json (header)用 **/
	public Map doGetReqJsonHeaderInstance() {
		return doGetReqJsonHeaderInstance(null , new HashMap());
	}

	/** 產生一個Map，並給予初始的共用參數，用於組request json (header)用 **/
	public Map doGetReqJsonHeaderInstance(String applicationId , Map uuid) {
		return new GenericMap()
			.put("applicationId", applicationId)
			.put("isAES", IS_AES ? 1 : 0)
			.putAll(new GenericMap(uuid))
			.getParamMap();
	}

	public String formatToJsonStr(Map jsonMap) throws Exception {
		return formatToJsonStr(jsonMap, null);
	}

	public Map parseBody(String body) throws Exception {
		return parseBody(body, null);
	}

	public Map parseBody(String body, String applicationId) throws JsonSyntaxException, Exception {
		System.out.println(body);
		
		if(IS_AES){
			body = AesEncryptDecryptUtils.decryptAesEcbPkcs7Padding(
				StringUtils.isBlank(applicationId) ? 
					DEF_AES_KEY : 
					StringUtils.rightPad(applicationId, 32, '1').substring(0 ,32) ,
				body
			);
		}
		System.out.println(body);
		
		return JsonUtil.genDefaultGson().fromJson(body , HashMap.class);
	}

	public String formatToJsonStr(Map jsonMap, String applicationId)throws Exception {
		System.out.println(jsonMap);
		String result = null;
		
		if (!IS_AES){
			result = JsonUtil.genDefaultGson().toJson(jsonMap);
		}
		else{			
			result = AesEncryptDecryptUtils.encryptAesEcbPkcs7Padding(
				StringUtils.isBlank(applicationId) ? 
					DEF_AES_KEY : 
					StringUtils.rightPad(applicationId, 32, '1').substring(0 ,32) , 
				JsonUtil.genDefaultGson().toJson(jsonMap));
		}
		System.out.println(result);
		
		return result;
		
	}

	public String getAppliationId(String wsid, String skey) throws Exception {
		if (StringUtils.isBlank(wsid)) {
			throw new Exception("wsid is error");
		}

		return AesEncryptDecryptUtils.encryptAes256(skey, StringUtils.rightPad(wsid, 32, '1'));
	}

	public GenericMap sendJson(String url, String json, Map<String, Object> headerMap) throws Exception {
		System.out.println("發送url : " + url);
		System.out.println("http header : ");
		
		for(String key : headerMap.keySet()){
			System.out.println(key + " : " + headerMap.get(key));
		}
		
		if(IS_AES){
			json = "{\"param\" : \"" + json + "\"}";
		}
		else{
			json = "{\"param\" : " + json + "}";
		}
		
		System.out.println("request json : " + json);
		
		GenericMap result = HttpClientJsonUtils.sendJsonRequest(url, json, 900000, headerMap , new DefHeaderCallBack());
		System.out.println("response : " + result.getParamMap());
		return result;
	}
	
	public void takeToken(String applicationId , Map uuid) throws Exception{
		String logOutUrl = url + "ws/mobile/takeToken.serv";
		Map<String, Object> headerMap = doGetReqJsonHeaderInstance(applicationId , uuid);
		Map jsonMap = new GenericMap(doGetReqJsonBodyInstance()).getParamMap();
		jsonMap.put("id", empId);
		
		String jsonBodyStr = formatToJsonStr(jsonMap , applicationId);
		GenericMap response = sendJson(logOutUrl , jsonBodyStr , headerMap);
		GenericMap resGmap = new GenericMap(parseBody(response.getNotNullStr("body") , applicationId));
		
		if(resGmap.nEquals("retCode" , "0000")){
			throw new Exception(resGmap.getNotNullStr("retMsg"));
		}

		System.out.println(resGmap.get("content"));
	}
}