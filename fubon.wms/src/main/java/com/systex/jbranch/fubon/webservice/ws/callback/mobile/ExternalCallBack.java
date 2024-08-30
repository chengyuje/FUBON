package com.systex.jbranch.fubon.webservice.ws.callback.mobile;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;

import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.encrypt.aes.AesEncryptDecryptUtils;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.platform.server.conversation.message.payload.Msg;
import com.systex.jbranch.ws.external.service.ExternalErrorMsg;
import com.systex.jbranch.ws.external.service.ExternalUtil;

public class ExternalCallBack implements CallBackExcute{
	/** 回傳結果處理
	 * 	retCode	： 錯誤代碼
	 * 		0000	成功
	 * 		0001	登入狀態失效，登出返回登入畫面
	 * 		EXXX	相關的錯誤訊息
	 **	retMsg	：  若有錯誤回復之錯誤訊息，無錯誤則為空值
	 **	content	：  回傳內容
	 **/
	@SuppressWarnings("unchecked")
	@Override
	public <T> T callBack(GenericMap responseData) {
		Map responseObj = responseData.get("responseObj");
		GenericMap genericMap = new GenericMap(responseObj);
		HttpServletRequest request = responseData.get(HttpServletRequest.class);
		HttpServletResponse response = responseData.get(HttpServletResponse.class);
		
		//回傳結果
		GenericMap resultGmap = new GenericMap((Map)genericMap.get("result"));
		GenericMap headerContent = new GenericMap((Map)resultGmap.get("header"));
		String responseJson = JsonUtil.genDefaultGson().toJson(excute(resultGmap , headerContent).getParamMap());
		
		GenericMap header = new GenericMap((Map)resultGmap.get("header"));
		response.addHeader("applicationId", header.getNotNullStr("applicationId"));
		
		T result = (T)responseJson;
		
		//加密
		if("1".equals(request.getHeader("isAES"))){
			try {
				result = (T)AesEncryptDecryptUtils.encryptAesEcbPkcs7Padding(ExternalUtil.doGetAesKey(request) , responseJson);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public GenericMap excute(GenericMap resultGmap , GenericMap headerContent){
		//錯誤訊息處理
		return headerContent.equals("OutputType" , "Message") ?
			excuteErrMsg(resultGmap , headerContent)://錯誤訊息處理
			excuteAction(resultGmap , headerContent);//各功能回傳處理
	}
	
	public GenericMap excuteErrMsg(GenericMap resultGmap , GenericMap headerContent){
		Msg errorMessage = resultGmap.get("body");
		String msgData = ObjectUtils.toString(errorMessage.getMsgData());
		
		return msgData.matches(".*pf_sessionmanager_error_007_00_001.*") ? 
				genRtnGmap("0001" , "登入狀態失效，登出返回登入畫面" , "") : 
				genRtnGmap(errorMessage.getMsgCode() , errorMessage.getMsgData() , "");
	}
	
	public GenericMap excuteAction(GenericMap resultGmap , GenericMap headerContent){
		Object body = resultGmap.get("body");
		
		return body instanceof ExternalErrorMsg ?
			excuteSuccessForExternalErrorMsg((ExternalErrorMsg)body) : //客製化錯誤訊息處理
			excuteSuccess(resultGmap , body); //無錯誤時處理
		
	}
	
	public GenericMap excuteSuccess(GenericMap resultGmap , Object body){
		return genRtnGmap("0000" , "成功" , body);
	}
	
	public GenericMap excuteSuccessForExternalErrorMsg(ExternalErrorMsg exMsg){				
		return genRtnGmap(exMsg.getRtnCode() , exMsg.getRtnMsg() , exMsg.getContent());
	}
	
	public GenericMap genRtnGmap(String errorCode , String rtnMsg , Object content){
		rtnMsg = ObjectUtils.toString(rtnMsg);
		String busErPt = "ErrorCode:.*(ErrorMsg:.*)?(ErrorContent.*)?";
		
		GenericMap result = null;
		
		if(rtnMsg.matches(".*" + busErPt)){
			Matcher matcher = Pattern.compile(busErPt).matcher(rtnMsg);
			
			if(matcher.find()){
				String rtnStr = matcher.group();
				String errCode = rtnStr.replaceAll("ErrorCode:", "").replaceAll(",(ErrorMsg|ErrorContent):.*", "");
				String errMsg = "";
				String errContent = "";
				
				if(rtnStr.matches(".*ErrorMsg:.*")){
					if((matcher = Pattern.compile("ErrorMsg:.*").matcher(rtnStr)).find()){
						errMsg = matcher.group().replaceAll(",ErrorContent.*", "").replaceAll("ErrorMsg:", "");;
					}
				}
				
				if(rtnStr.matches(".*ErrorContent.*")){
					if((matcher = Pattern.compile("ErrorContent.*").matcher(rtnStr)).find()){
						errContent = matcher.group().replaceAll(",ErrorMsg.*", "").replaceAll("ErrorContent:", "");
					}
				}
				
				return new GenericMap().put("retCode" , errCode).put("retMsg" ,  errMsg).put("content" , errContent);		
			}
		}
		
		return new GenericMap().put("retCode" , errorCode).put("retMsg" , rtnMsg).put("content" , content);						
	}
}
