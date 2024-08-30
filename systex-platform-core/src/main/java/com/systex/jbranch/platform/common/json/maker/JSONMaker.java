package com.systex.jbranch.platform.common.json.maker;

import java.util.Iterator;
import java.util.Set;

import com.systex.jbranch.platform.common.json.JSONObject;



public class JSONMaker {
	//private Logger logger = SysLogger.getLogger();
	public JSONMaker() {}
	
	/**
	 * 產生Header和Body的JSON
	 * @param object
	 * @return
	 */
	public String make2ServerJson(JSONObject object) throws Exception{
		StringBuffer jsonString = new StringBuffer();
		JSONObject header = (JSONObject) object.get("Header");
		jsonString.append("{\"Header\":{");
		if (header.size() > 0) {
			Set keySet = header.keySet();
			Iterator its = keySet.iterator();
			int i = 0;
			while (its.hasNext()) {
				String key = (String) its.next();
				jsonString.append("\"").append(key).append("\":\"").append(header.get(key)).append("\"");
				if (i<keySet.size()-1) {
					i++;
					jsonString.append(",");
				}
			}
		}
		jsonString.append("},");
		JSONObject body = (JSONObject) object.get("Body");
		jsonString.append("\"Body\":{");
		if (header.size() > 0) {
			Set keySet = body.keySet();
			Iterator its = keySet.iterator();
			int i = 0;
			while (its.hasNext()) {
				String key = (String) its.next();
				jsonString.append("\"").append(key).append("\":\"").append(body.get(key)).append("\"");
				if (i<keySet.size()-1) {
					i++;
					jsonString.append(",");
				}
			}
		}
		jsonString.append("}}");
		//logger.debug(jsonString.toString());
		
		return jsonString.toString();
	}
	
	
	/**
	 * 產生畫面的JSON
	 * @param object
	 * @return
	 */
	public String makeScrnJson(JSONObject object) throws Exception{
		StringBuffer jsonString = new StringBuffer();
		String retCode = (String) object.get("RetCode");
		String retMsg = (String) object.get("RetMsg");
		String reqSUPV = (String) object.get("ReqSUPV");
		String nextScreen = (String) object.get("NextScreen");
		JSONObject screen = (JSONObject) object.get("Screen");
		jsonString.append("{\"RetCode\":\"").append(retCode).append("\",");
		jsonString.append("\"RetMsg\":\"").append(retMsg).append("\",");
		jsonString.append("\"ReqSUPV\":\"").append(reqSUPV).append("\",");
		jsonString.append("\"NextScreen\":\"").append(nextScreen);
		jsonString.append("\",");
		jsonString.append("\"Screen\":{");
		if (screen.size() > 0) {
			Set keySet = screen.keySet();
			Iterator its = keySet.iterator();
			int i = 0;
			while (its.hasNext()) {
				String key = (String) its.next();
				jsonString.append("\"").append(key).append("\":\"").append(screen.get(key)).append("\"");
				if (i<keySet.size()-1) {
					i++;
					jsonString.append(",");
				}
			}
		}
		jsonString.append("}}");
		//logger.debug(jsonString.toString());
		
		return jsonString.toString();
	}

}
