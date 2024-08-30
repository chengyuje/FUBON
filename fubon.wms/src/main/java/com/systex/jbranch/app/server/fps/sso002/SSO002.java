package com.systex.jbranch.app.server.fps.sso002;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.encrypt.aes.AesEncryptDecryptUtils;
import com.systex.jbranch.comutil.io.JoinDifferentSysBizLogic;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.comutil.sso.dao.SingleSignOnDaoInf;
import com.systex.jbranch.fubon.commons.http.client.HttpClientJsonUtils;
import com.systex.jbranch.fubon.commons.http.client.callback.DefHeaderCallBack;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**人壽視訊投保簽署系統 SingleSignOnService*/
@Service("SSO002")
@Scope("request")
public class SSO002 extends JoinDifferentSysBizLogic {
	@Autowired 
	@Qualifier("SingleSignOnDao")
	private SingleSignOnDaoInf ssoDao;
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/** 人壽視訊投保簽署系統SSO */
	public void takeToken(Object body, IPrimitiveMap header) throws Exception{
		sendRtnObject(doTakeToken(new GenericMap((Map)body)).getParamMap());
	}
	
	/** 人壽視訊投保簽署系統SSO */
	public GenericMap doTakeToken(GenericMap inputGmap) throws Exception{
		Gson gson = JsonUtil.genDefaultGson();
		String empId = inputGmap.getNotNullStr("id");
		
		GenericMap resultGmap = new GenericMap();
		String insSigUrl = null;
		String token = null;
				
		//SSO config
		GenericMap ssoConfig = getSsoDao().queryInsSigSsoConfig();
		//加密KEY
		String secretKey = ssoConfig.getNotNullStr("SKEY");
		//系統代號
		String sysCode = ssoConfig.getNotNullStr("SYS_CODE");
		//系統Channel代號
		String channelId = ssoConfig.getNotNullStr("SYS_CHANNEL_ID");
		
		String url = ssoConfig.getNotNullStr("GET_TOKEN_URL");
		byte[] bkey = AesEncryptDecryptUtils.secretKeyToByteArray("utf-8" , 16 , secretKey);
		
		//Header欄位
		Map<String, Object> header = new HashMap<String, Object>();
		header.put("SYS_CHANNEL_ID", channelId);
				
		//Body欄位：帳號
		inputGmap.put("WsID" , ssoConfig.getNotNullStr("ACC"));
		//Body欄位：密碼
		inputGmap.put("WsPwd" , ssoConfig.getNotNullStr("PW"));
		//Body欄位：ID - AES
		inputGmap.put("EmployeeID" , AesEncryptDecryptUtils.encryptAesEcbPkcs7Padding(bkey , empId));

		logger.info("inputGmap.getParamMap():" + gson.toJson(inputGmap.getParamMap()));
		logger.info("url:" + url);
		
		GenericMap resultGetToken = HttpClientJsonUtils.sendJsonRequest(url, gson.toJson(inputGmap.getParamMap()), 900000000, header, new DefHeaderCallBack());
		String jsonStr = resultGetToken.getNotNullStr("body");
		
		logger.info("jsonStr:" + jsonStr);
		
		//UAT測試:https://mappplus.fbl.com.tw:8480/EWeb/Login?Syscode=ESign&TokenKeyValue={0}&Channel=BA0001
		insSigUrl = ssoConfig.getNotNullStr("INSSIG_URL") + "?Syscode=" + sysCode + "&Channel=" + channelId + "&TokenKeyValue=";
		
		logger.info("insSigUrl:" + insSigUrl);
		
		GenericMap reusultBody = new GenericMap((Map)gson.fromJson(jsonStr , HashMap.class));
		Map result = reusultBody.get("Result");
		
		logger.info("result:" + ObjectUtils.toString(result));
		
		if("00".equals(reusultBody.getNotNullStr("ReturnCode"))){
			token = ObjectUtils.toString(result.get("AccessToken"));
			insSigUrl += token;
		}
		else{
			resultGmap.put("errCode", reusultBody.getNotNullStr("ReturnCode"));
			resultGmap.put("errMsg", reusultBody.getNotNullStr("Message"));
		}
		
		logger.info("token:" + token);
		
		return resultGmap.put("INSSIG_URL", insSigUrl).put("TOKEN", token);
	}

	public SingleSignOnDaoInf getSsoDao() {
		return ssoDao;
	}

	public void setSsoDao(SingleSignOnDaoInf ssoDao) {
		this.ssoDao = ssoDao;
	}
}
