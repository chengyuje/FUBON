package com.systex.jbranch.app.server.fps.sso001;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
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
import com.systex.jbranch.fubon.commons.soap.HttpClientSoapFactory;
import com.systex.jbranch.fubon.commons.soap.HttpClientSoapUtils;
import com.systex.jbranch.fubon.commons.soap.SoapVo;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**業務員 SingleSignOnService*/
@Service("SSO001")
@Scope("request")
public class SSO001 extends JoinDifferentSysBizLogic {
	@Autowired 
	@Qualifier("SingleSignOnDao")
	private SingleSignOnDaoInf ssoDao;
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/** 保經帶 - 取得簽署系統 url + token **/
	public void doGeESoafWebUrl(Object body, IPrimitiveMap header) throws Exception{
		GenericMap inputGmap = new GenericMap((Map)body);
		GenericMap resultGmap = new GenericMap();
		//參數設定
		GenericMap config = getSsoDao().queryAasSsoConfig();
		resultGmap = resultGmap.putAll(takeTokenAas(inputGmap , config));
		
		if(resultGmap.isNotNull("errMsg")){
			String url  = config.getNotNullStr("LOGIN_URL");
			resultGmap.put("url", url);
		}
		else{
			String token = resultGmap.get("token");
			String url = ssoDao.queryConfig("SYS.ESOAFWeb_URL").getNotNullStr("URL");
			resultGmap.put("url", url + (StringUtils.isBlank(token) ? "" : ("?Token=" + token)));
		}

		sendRtnObject(resultGmap.getParamMap());
	}
	
	/** 行動投保SSO */
	public void takeToken(Object body, IPrimitiveMap header) throws Exception{
		sendRtnObject(doTakeToken(new GenericMap((Map)body)).getParamMap());
	}
	
	/** 行動投保SSO */
	public GenericMap doTakeToken(GenericMap inputGmap) throws Exception{
		Gson gson = JsonUtil.genDefaultGson();
		String id = inputGmap.getNotNullStr("id");
		String errorMsg  = null;
		
		GenericMap resultGmap = new GenericMap();
		String mappUrl = null;
		String downMappUrl = null;
		String token = null;
		
//		try{
			//取業務員資訊
			GenericMap memberInf = new GenericMap(getSsoDao().queryMemberInfo(id));//加密類型
			//登入者
			String custId = memberInf.getNotNullStr("CUST_ID").trim();
			//SSO config
			GenericMap ssoConfig = getSsoDao().querySsoConfig();
			//取得AES編碼類型-128、192、256
			int encryptType = ssoConfig.getBigDecimal("ENCRYPT_TYPE").intValue();
			//加密KEY
			String secretKey = ssoConfig.getNotNullStr("SKEY");
			
			//若無取得業務員資訊
			if(MapUtils.isEmpty(memberInf.getParamMap())){
				throw new JBranchException("data not found");
			}
			
			String url = ssoConfig.getNotNullStr("URL");
			byte[] bkey = AesEncryptDecryptUtils.secretKeyToByteArray("utf-8" , 16 , secretKey);
			
			//帳號
			inputGmap.put("WsID" , ssoConfig.getNotNullStr("ACC"));
			//密碼
			inputGmap.put("WsPwd" , ssoConfig.getNotNullStr("PW"));
			//系統代號
			inputGmap.put("SysCode" , ssoConfig.getNotNullStr("SYSCODE"));
			//機器的ip
			inputGmap.put("ClientIP" , InetAddress.getLocalHost().getHostAddress());
			//ID - AES
			inputGmap.put("Id" , AesEncryptDecryptUtils.encryptAesEcbPkcs7Padding(bkey , custId));
			//姓名 - AES
			inputGmap.put("Name" , AesEncryptDecryptUtils.encryptAesEcbPkcs7Padding(bkey , memberInf.getNotNullStr("EMP_NAME")));
			//通路別
			inputGmap.put("Organize" , ssoConfig.getNotNullStr("ORGANIZE"));
			//體系
			inputGmap.put("System" , ssoConfig.getNotNullStr("SYSTEM"));
			//單位代號
			inputGmap.put("DeptNo" , "8A1" + memberInf.getNotNullStr("DEPT_ID"));
			//單位名稱
			inputGmap.put("DeptName" , memberInf.getNotNullStr("DEPT_NAME"));
	
			logger.info("inputGmap.getParamMap():" + gson.toJson(inputGmap.getParamMap()));
			logger.info("url:" + url);
			
			GenericMap reusult = HttpClientJsonUtils.sendDefJsonRequest(url , gson.toJson(inputGmap.getParamMap()) , null);
			String jsonStr = reusult.getNotNullStr("body");
			
			logger.info("jsonStr:" + jsonStr);
			
			mappUrl = ssoConfig.getNotNullStr("MAPP_URL") + "SysCode=TPFB_APP&Token=";
			downMappUrl = "itms-services://?action=download-manifest&url=" + ssoConfig.getNotNullStr("DOWN_MAPP_URL");
			
			logger.info("mappUrl:" + mappUrl);
			logger.info("downMappUrl:" + downMappUrl);
			
			GenericMap reusultBody = new GenericMap((Map)gson.fromJson(jsonStr , HashMap.class));
			Map result = reusultBody.get("Result");
			
			logger.info("result:" + result.toString());
			
			if("00".equals(reusultBody.getNotNullStr("ReturnCode"))){
				token = ObjectUtils.toString(result.get("AccessToken"));
				mappUrl += (token = new String(Base64.encodeBase64(token.getBytes())));
			}
			else{
				resultGmap.put("errMsg" , reusultBody.getNotNullStr("Message"));
			}
		
//		}catch(Exception ex){
//			resultGmap.put("errMsg" , ex.getMessage());
//		}
		
		logger.info("mappUrl:" + mappUrl);
		logger.info("downMappUrl:" + downMappUrl);
		logger.info("token:" + token);
			
		return resultGmap.put("MAPP_URL", mappUrl).put("DOWN_MAPP_URL", downMappUrl).put("TOKEN", token);
	}
	
	
	/**保經代SSO*/
	public GenericMap takeTokenAas(GenericMap inputGmap , GenericMap config) throws Exception{
		GenericMap rtnGmap = new GenericMap();
		
		try{
			String id = inputGmap.getNotNullStr("id");
			//取業務員資訊
			GenericMap memberInf = new GenericMap(getSsoDao().queryMemberInfo(id));//加密類型
			String memberId = memberInf.getNotNullStr("CUST_ID").trim();
			//發送的資料內容
			String requestStr = "";
			//ws回應內容
			String responseStr = null;
			//解析response用的doc物件
			Document doc = null;
			
			//aas ws地址
			String wsUrl = config.getNotNullStr("WS_URL");	
			//密鑰
			String sysKey = config.getNotNullStr("SKEY");
			//加密類型(加密長度)
			int encryptType = config.getBigDecimal("ENCRYPT_TYPE").intValue();
			//AES編碼偏移量
			String ivParameter = config.getNotNullStr("IV_PARAMETER"); 
			
			//Web Services服務帳號(參數1)
			String wsId = config.getNotNullStr("ACC");
			String wsIdAes = AesEncryptDecryptUtils.encryptAesCbcPkcs5PaddingAscii(sysKey , wsId , ivParameter , encryptType); //(AES256加密)
			//Web Services服務密碼(參數2)
			String wsPwd = config.getNotNullStr("PW");
			String wsPwdAes = AesEncryptDecryptUtils.encryptAesCbcPkcs5PaddingAscii(sysKey , wsPwd , ivParameter , encryptType);//(AES256加密)
			//系統代號
			String sysCode = config.getNotNullStr("SYSCODE");
			String sysCodeAes = AesEncryptDecryptUtils.encryptAesCbcPkcs5PaddingAscii(sysKey , sysCode , ivParameter , encryptType);//(AES256加密)
			//使用者ip(本地ip)
			String localAddress = InetAddress.getLocalHost().getHostAddress();
			String localAddressAes = AesEncryptDecryptUtils.encryptAesCbcPkcs5PaddingAscii(sysKey , localAddress , ivParameter , encryptType);//(AES256加密)
			
			//身分類型
			String identity = config.getNotNullStr("DEF_IDENTITY");			
			//業務員ID(AES256加密)
			String encryptId = AesEncryptDecryptUtils.encryptAesCbcPkcs5PaddingAscii(sysKey , memberId , ivParameter , encryptType);
			//串送字串
			String encryptIdType = encryptId + "#" + identity;
			
			for(String val : new String[]{wsIdAes , wsPwdAes , sysCodeAes , localAddressAes , encryptIdType}){
				requestStr = requestStr + val + "|";
			}
			
			SoapVo vo = new SoapVo();
			vo.setUrl(wsUrl);
			vo.getHeaders().put("SOAPAction" , "");
			vo.setRequestCharSet(StandardCharsets.UTF_8);
			vo.setReponseCharSet(StandardCharsets.UTF_8);
	
			Element getTokenKey = DocumentHelper.createElement("getTokenKey");		
			getTokenKey.addNamespace("" , "http://www.w3.org/2001/XMLSchema");
			getTokenKey.addElement("getTokenKeyRequest").setText(requestStr);
			//發送AAS WS
			vo = HttpClientSoapUtils.sendSoap(HttpClientSoapFactory.SOAP_1_1.requestBuilder(vo , getTokenKey));
			//取得回應內容
			doc = DocumentHelper.parseText(vo.getSoapResponseData());
			logger.info("resposne : " + doc.asXML());
			
			Node doGetTokenKeyReturndocNode = doc.selectSingleNode("//*[local-name()='getTokenKeyReturn']");
			
			if(doGetTokenKeyReturndocNode == null || StringUtils.isBlank(doGetTokenKeyReturndocNode.getText()) || doGetTokenKeyReturndocNode.getText().indexOf("|") == -1){
				throw new Exception("getTokenKeyReturn is no data");
			}
			
			//解析訊息
			responseStr = ObjectUtils.toString(doGetTokenKeyReturndocNode.getText());
			String [] results = responseStr.split("\\|");
			String rtnCode = responseStr.split("\\|")[0];
			String rtnMsg = responseStr.split("\\|")[1];
			
			
			if(!"00".equals(rtnCode)){
				rtnGmap.put("errMsg", "錯誤代碼[" + rtnCode +"]:" + rtnMsg);
			}
			
			rtnGmap.put("token", rtnMsg);
		}catch(Exception ex){
			rtnGmap.put("errMsg", ex.getMessage());
		}
		
		
		return rtnGmap;
	}


	public SingleSignOnDaoInf getSsoDao() {
		return ssoDao;
	}

	public void setSsoDao(SingleSignOnDaoInf ssoDao) {
		this.ssoDao = ssoDao;
	}
}
