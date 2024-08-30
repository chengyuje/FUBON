package com.systex.jbranch.app.server.fps.iot930;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBIOT_MAINVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.ValidUtil;
import com.systex.jbranch.fubon.commons.http.client.HttpClientJsonUtils;
import com.systex.jbranch.fubon.commons.http.client.callback.DefHeaderCallBack;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * IOT930
 *
 */
@Component("iot930")
@Scope("request")
public class IOT930 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/***
	 * 根據案件編號，取得北富銀視訊投保_調閱影音檔URL List
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void GetMAPPEVideo(Object body, IPrimitiveMap<Object> header) throws Exception {
		IOT930InputVO inputVO = (IOT930InputVO) body;
		IOT930OutputVO outputVO = new IOT930OutputVO();		

		// 接收回傳資料
		String webSerivceReturn = new String();
		Map<String, Object> eVideoList = new HashMap<String, Object>(); //E_VideoList
		List<Map<String, Object>> eVideoStream = new ArrayList<Map<String, Object>>(); //E_VideoStream

		eVideoList = getMAPPEVideoList(inputVO.getCASE_ID());
		if (eVideoList.get("ReturnCode") != null
				&& eVideoList.get("ReturnCode").toString().equals("00")
				&& StringUtils.isNotBlank(eVideoList.get("Result").toString())) {				
			eVideoStream = getMAPPEVideoStream((Map<String, Object>) eVideoList.get("Result"), inputVO.getCASE_ID());
			
		} else {
			throw new JBranchException("MAPP E_VideoList API錯誤。ReturnCode:" + eVideoList.get("ReturnCode") + ", Message: " + eVideoList.get("Message"));
		}
			
		outputVO.setMAPPEVideoList(eVideoStream);
		
		sendRtnObject(outputVO);
	}
	
	/***
	 * MAPP視訊投保影音檔Web Service(E_VideoList)_調閱影音檔清單
	 * @param caseId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getMAPPEVideoList(String caseId) throws Exception {
		// 讀取XML參數表中的值
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> paramMap = xmlInfo.doGetVariable("IOT.GETMAPPCASE_EVIDEO", FormatHelper.FORMAT_3);
		String sysChannelId = ObjectUtils.toString(paramMap.get("LIST_SYS_CHANNEL_ID"));
		String sysCode = ObjectUtils.toString(paramMap.get("LIST_SYSCODE"));
		String wsID = ObjectUtils.toString(paramMap.get("LIST_WS_ID"));
		String wsPwd = ObjectUtils.toString(paramMap.get("LIST_WS_PWD"));
		String url = ObjectUtils.toString(paramMap.get("LIST_URL"));

		// 加入Header欄位
		Map<String, Object> header = new HashMap<String, Object>();
		header.put("Content-Type", "application/json");
		header.put("SYS_CHANNEL_ID", sysChannelId);
				
		// 需傳入之參數
		String jsonRequestData = "{ " 
				+ "\"WsID\":\""     + wsID    + "\", "
				+ "\"WsPwd\":\""    + wsPwd   + "\", " 
				+ "\"SysCode\":\""  + sysCode + "\", " 
				+ "\"CaseId\":\""   + caseId  + "\"" 
			+ "}";

		// 接收回傳資料
		String webSerivceReturn = new String();
		Map<String, Object> webserviceData = new HashMap<String, Object>();

		logger.info("IOT930 getMAPPEVideoList begins:");
		logger.info("IOT930 getMAPPEVideoList URL: " + url);
		logger.info("IOT930 getMAPPEVideoList jsonRequestData: " + jsonRequestData);
		logger.info("IOT930 getMAPPEVideoList header: " + header);
		
		GenericMap result = HttpClientJsonUtils.sendJsonRequest(url, jsonRequestData, 900000000, header, new DefHeaderCallBack());
		
		webSerivceReturn = (String) result.getParamMap().get("body");
		logger.info("IOT930 getMAPPEVideoList returns: result: " + webSerivceReturn);
		
		webserviceData = JsonUtil.genDefaultGson().fromJson(webSerivceReturn, HashMap.class);
		
		logger.info("IOT930 getMAPPEVideoList ends.");
		
		return webserviceData;
	}
	
	/***
	 * MAPP視訊投保影音檔Web Service(E_VideoStream)_取得影音檔播放URL
	 * 由E_VideoList取得的Wowza_Id，取得影音檔URL
	 * @param caseId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getMAPPEVideoStream(Map<String, Object> videoList, String caseId) throws Exception {
		List<Map<String, Object>> returnObj = new ArrayList<Map<String, Object>>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmm");
		// 讀取XML參數表中的值
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> paramMap = xmlInfo.doGetVariable("IOT.GETMAPPCASE_EVIDEO", FormatHelper.FORMAT_3);
		String sysChannelId = ObjectUtils.toString(paramMap.get("STREAM_SYS_CHANNEL_ID"));
		String sysCode = ObjectUtils.toString(paramMap.get("STREAM_SYSCODE"));
		String wsID = ObjectUtils.toString(paramMap.get("STREAM_WS_ID"));
		String wsPwd = ObjectUtils.toString(paramMap.get("STREAM_WS_PWD"));
		String url = ObjectUtils.toString(paramMap.get("STREAM_URL"));

		// 加入Header欄位
		Map<String, Object> header = new HashMap<String, Object>();
		header.put("Content-Type", "application/json");
		header.put("SYS_CHANNEL_ID", sysChannelId);
		
		List<Map<String, Object>> wsResult = (List<Map<String, Object>>) videoList.get("VideoList");
		for(Map<String, Object> videoMap : wsResult) {
			List<Map<String, Object>> fileMap = (List<Map<String, Object>>) videoMap.get("FileList");
			for(Map<String, Object> fmap : fileMap) {
				String wowzaid = ObjectUtils.toString(fmap.get("Wowza_Id"));
				String enterTimeStr = ObjectUtils.toString(fmap.get("EnterTime"));
				Timestamp enterTime = new java.sql.Timestamp(dateFormat.parse(enterTimeStr).getTime());
				
				// 需傳入之參數
				String jsonRequestData = "{ " 
						+ "\"WsID\":\""     + wsID    + "\", "
						+ "\"WsPwd\":\""    + wsPwd   + "\", " 
						+ "\"SysCode\":\""  + sysCode + "\", " 
						+ "\"SysCode\":\""  + sysCode + "\", " 
						+ "\"playerType\":\"Dash\", " 
						+ "\"wowzaid\":\""  + wowzaid + "\", " 
						+ "\"CaseId\":\""   + caseId  + "\"" 
					+ "}";
				
				// 接收回傳資料
				String webSerivceReturn = new String();
				Map<String, Object> vStream = new HashMap<String, Object>();
	
				logger.info("IOT930 getMAPPEVideoStream begins:");
				logger.info("IOT930 getMAPPEVideoStream URL: " + url);
				logger.info("IOT930 getMAPPEVideoStream jsonRequestData: " + jsonRequestData);
				logger.info("IOT930 getMAPPEVideoStream header: " + header);
				
				GenericMap result = HttpClientJsonUtils.sendJsonRequest(url, jsonRequestData, 900000000, header, new DefHeaderCallBack());
				
				webSerivceReturn = (String) result.getParamMap().get("body");
				logger.info("IOT930 getMAPPEVideoStream returns: result: " + webSerivceReturn);
				
				vStream = JsonUtil.genDefaultGson().fromJson(webSerivceReturn, HashMap.class);
				
				logger.info("IOT930 getMAPPEVideoStream ends.");
				
				if (vStream.get("ReturnCode") != null
						&& vStream.get("ReturnCode").toString().equals("00")
						&& StringUtils.isNotBlank(vStream.get("Result").toString())) {				
					String streamPage = (String) ((Map<String, Object>) vStream.get("Result")).get("streamPage");
					
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("Wowza_Id", wowzaid);
					map.put("EnterTime", enterTime);
					map.put("streamPage", streamPage);
					
					returnObj.add(map);
				} else {
					throw new JBranchException("MAPP E_VideoStream API錯誤。ReturnCode:" + vStream.get("ReturnCode") + ", Message: " + vStream.get("Message"));
				}
			}
		}
		
		return returnObj;
	}

}
