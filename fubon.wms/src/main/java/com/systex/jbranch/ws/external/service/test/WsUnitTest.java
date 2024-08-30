package com.systex.jbranch.ws.external.service.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;

import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.encrypt.aes.AesEncryptDecryptUtils;
import com.systex.jbranch.comutil.parse.JsonUtil;

public class WsUnitTest {
	
	public static void main(String...args) throws Exception{
//		String pwd = new String(org.apache.commons.codec.binary.Base64.encodeBase64("1234".getBytes()));
//		System.out.println(pwd);
//		pwd = new String(org.apache.commons.codec.binary.Base64.encodeBase64("1234".getBytes("UTF-8")));
//		System.out.println(pwd);
		
		//new WsUnitTest().testGetReferenceData();
		//new WsUnitTest().logInOut();
		new WsUnitTest().logInOut();
		
		
		String code = new WsUnitTest().logInOut();//"wsid=192.168.43.32_fa2f361d-bf52-46ec-b793-e3dedd59b50a,branchID=704,tellerId=005889,sectionID=77da276b-4665-4e3b-b39f-a32c78b59844";
		//code = "wsid=192.168.43.32_21e5a840-cfcf-441f-babc-6e0d83fe3292,branchID=704,tellerId=005889,sectionID=ef23243c-d8d0-4fb7-9a62-657be3cfa87b";
		String menuId = "INS112";
		String reApid = menuId;
		for(int i = 0 ; i < 32 - menuId.length(); i++){
			reApid += "0";
		}
		
		System.out.println(reApid);
		
		code = AesEncryptDecryptUtils.encryptAesEcbPkcs7Padding(reApid , code);
		System.out.println(code);
		code = new String(org.apache.commons.codec.binary.Base64.encodeBase64(code.getBytes("utf-8")));
		System.out.println(code);
		String itemUrl = MobileLoginTest.url + menuId + ".vw?uuid=" + code;
		System.out.println(itemUrl);
		
	}
	
	public void logInOut(int loginUserIdx , CallBackExcute callBack) throws Exception{
		MobileLoginTest test = new MobileLoginTest();
		Map uuid = new HashMap();
		System.out.println("驗證LDAP並取得代辦清單-----------START");
		GenericMap paramData = test.login();
		String applicationId = paramData.getNotNullStr("applicationId");
		List userList = paramData.get("userList");
		System.out.println("驗證LDAP並取得代辦清單-----------END\n\n");
		
		System.out.println("登入-----------START");
		System.out.println(test.getLoginInfo(applicationId , (Map)userList.get(loginUserIdx)));
		System.out.println("登入-----------END\n\n");
		
		//執行各自要測試的程式
		callBack.callBack(new GenericMap()
			.put(MobileLoginTest.class , test)
			.put("applicationId" , applicationId)
		);
		
		
		
		System.out.println("#applicationId = " + applicationId);
		System.out.println("登出-----------START");
		System.out.println(test.logout(applicationId , uuid));
		System.out.println("登出-----------END\n\n");
		
		System.out.println("end");
	}
	

	@Test
	public void getCustomerList() throws Exception{
		System.out.println("testGetReferenceData");
		logInOut(0 , new CallBackExcute(){
			public <T> T callBack(GenericMap genericMap) {
				MobileLoginTest test = genericMap.get(MobileLoginTest.class);
				try {
					test.getCustomerList(genericMap.getNotNullStr("applicationId") , (Map)genericMap.get("uuid"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
	
	@Test
	public String logInOut() throws Exception{
		MobileLoginTest test = new MobileLoginTest();
		String applicationId = null;
		Map uuid = new HashMap();
		
		try{
			System.out.println("驗證LDAP並取得代辦清單-----------START");
			GenericMap paramData = test.login();
			applicationId = paramData.getNotNullStr("applicationId");
			List userList = paramData.get("userList");			
			System.out.println("驗證LDAP並取得代辦清單-----------END\n\n");
			
			System.out.println("登入-----------START");
			GenericMap loginInfoMap = test.getLoginInfo(applicationId , (Map)userList.get(0));
			System.out.println(loginInfoMap);
			
			uuid = (Map) ((Map)loginInfoMap.get("content")).get("uuid");
			System.out.println("登入-----------END\n\n");
			
			GenericMap uuidGmap = new GenericMap(uuid);
		
			String code = "wsid=" 		+ uuidGmap.getNotNullStr("wsId") 		+ "," + 
						  "branchID=" 	+ uuidGmap.getNotNullStr("branchID") 	+ "," +
						  "tellerId=" 	+ uuidGmap.getNotNullStr("tellerId") 	+ "," + 
						  "sectionID=" 	+ uuidGmap.getNotNullStr("sectionID");
			
			System.out.println(code);
			
			//test.getFps200(applicationId, uuid);
			
			return code;
		
//			System.out.println("getCustomerList-----------START");
//			System.out.println(test.getCustomerList(applicationId , uuid));
//			System.out.println("getCustomerList-----------END\n\n");
//			System.out.println("end");
//			
//			System.out.println("getCustomerList-----------START");
//			System.out.println(test.getCustomerDetail(applicationId, uuid));
//			System.out.println("getCustomerList-----------END\n\n");
//			System.out.println("end");
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
//			if(applicationId != null){
//				System.out.println("登出-----------START");
//				System.out.println(test.logout(applicationId , uuid)); 
//				System.out.println("登出-----------END\n\n");
//			}
		}
		
		return null;
		

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSsoGenToken() throws Exception {
		MobileLoginTest mobileTest = new MobileLoginTest();
		String loginUrl = MobileLoginTest.url + "sso/SIG/WmsSsoService.serv";
		System.out.println(loginUrl);
		
		String verifyDeCode = new String(Base64.decode("U0lHQDEwLjIwNC4yLjEyNyM2MzY1NDEjQjAxMyM4OTM3MTk4NTg0NjI5ODQ5NTg4"));
		String sysCode = verifyDeCode.split("@")[0];
		verifyDeCode = new String(Base64.encode((sysCode + "#" + verifyDeCode.substring(verifyDeCode.lastIndexOf("#") + 1)).getBytes()));
		
		Map parameter = new HashMap();
		parameter.put("WsID", "rxErU3Dd3hGF5KdmpQgt");
		parameter.put("WsPwd", "RuE8ZjtuASaYDV2mBv9Y");
		parameter.put("SysCode", "SIG");
		parameter.put("VerifyCode", verifyDeCode);
		
		String jsonParam = JsonUtil.genDefaultGson().toJson(parameter); 
		System.out.println(jsonParam);
		
		System.out.println(mobileTest.sendJson(loginUrl , jsonParam , new HashMap()));
	}

	

	/**行動投保Mapp取token所會使用的加解密**/
	@Test
	public void testAesEcbPkcs7Padding1() throws Exception {
		String secretKey = "fbim";
		String val = "測試案例1 - this is test 1";
		byte[] bkey = AesEncryptDecryptUtils.secretKeyToByteArray("utf-8" , 16 , secretKey);
		System.out.println(secretKey);
		System.out.println(val = AesEncryptDecryptUtils.encryptAesEcbPkcs7Padding(bkey , val));
		System.out.println(val = AesEncryptDecryptUtils.decryptAesEcbPkcs7Padding(bkey , val));
		
		secretKey = "fubonMappSSO";
		System.out.println(secretKey);
		bkey = AesEncryptDecryptUtils.secretKeyToByteArray("utf-8" , 16 , secretKey);
		System.out.println(val = AesEncryptDecryptUtils.encryptAesEcbPkcs7Padding(bkey , val));
		System.out.println(AesEncryptDecryptUtils.decryptAesEcbPkcs7Padding(bkey , val));
	}

	/**AAS所使用的加解密**/
	@Test
	public void testAesCbcPkcs5PaddingAscii() throws Exception {
		String cSrc = "h38Rr/oGrTvpn2dBcsAYog==";
		String iv = "c782dc4c098c66cb";
		String key = "1QAZ2WSX3EDC4RFV";
		cSrc = AesEncryptDecryptUtils.decryptAesCbcPkcs5PaddingAscii(key , cSrc , iv , 128);
		System.out.println(cSrc);
		
		cSrc = "{\"user\":\"中文測試\"}";
		cSrc = AesEncryptDecryptUtils.encryptAesCbcPkcs5PaddingAscii(key , cSrc , iv , 128);
		System.out.println(cSrc);
		cSrc = AesEncryptDecryptUtils.decryptAesCbcPkcs5PaddingAscii(key , cSrc , iv , 128);
		System.out.println(cSrc);
	}

	
	/**IPAD介接WS時所用的加解密**/
	@Test
	public void testdecryptAesEcbPkcs7Padding2() throws Exception {
		String val = "L8DaO96j3dulxI5VZ0DyTixlqejrp82NKojvT4xH5RrTTYKPecSi+knYvIpqVzfgbn/TkTZo/RiLIC5ZcHVVx7AMFXgtKzWm8gGOq9Hsc5HBi2Zn7Fmgf1KzjytLvAjCqWPHUuN8m/BXq4IIsrHLhmGoJQQGvXe+j9bU4dx0XyQ=";
		System.out.println(val = AesEncryptDecryptUtils.decryptAesEcbPkcs7Padding("87654321876543218765432187654321", val));
		
		String key = UUID.randomUUID().toString().substring(0, 32);
		val = "{\"user\":\"中文測試\"}";
		System.out.println(val = AesEncryptDecryptUtils.encryptAesEcbPkcs7Padding(key, val));
		System.out.println(val = AesEncryptDecryptUtils.decryptAesEcbPkcs7Padding(key, val));
	}
}
