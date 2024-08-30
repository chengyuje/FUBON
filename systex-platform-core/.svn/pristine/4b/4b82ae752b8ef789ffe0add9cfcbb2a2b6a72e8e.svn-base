package com.systex.jbranch.platform.server.eclient.conversation;

import com.systex.jbranch.platform.server.conversation.MapToaIF;
import com.systex.jbranch.platform.server.conversation.message.EnumMessageType;
import com.systex.jbranch.platform.server.conversation.message.EnumOutputType;
import com.systex.jbranch.platform.server.conversation.message.EnumShowType;
import com.systex.jbranch.platform.server.conversation.message.EnumToaHeader;
import com.systex.jbranch.platform.server.conversation.message.Toa;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import com.systex.jbranch.platform.util.PrimitiveMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;


/**
 * 提供單一訊息輸出、或是畫面欄位內容的資料來源
 * @author Administrator
 *
 */
public class TOAMsg extends Toa implements MapToaIF {



	enum enumMsgHeader
	{
		showType,
		messageType,
		msgCode,
		msgData
	}

	//存放訊息資料
	private String outputData="";
	//存放畫面資料
	private JSONObject outputMap = new JSONObject();


	private IPrimitiveMap<String> toaBody = new PrimitiveMap<String>(outputMap)
											{
												@Override
												public Object put(Object key,Object value)
												{
													Headers().setInt(EnumToaHeader.OutputType, EnumOutputType.Screen.ordinal());
													return super.put(key, value);
												}

												@Override
												public List getList(String key) {
													return JSONArray.toList(((JSONObject)map).getJSONArray(key));
												}

												@Override
												public void setList(String key, List value) {
													Headers().setInt(EnumToaHeader.OutputType, EnumOutputType.Screen.ordinal());
													for (Object o : value) {
														((JSONObject)map).accumulate(key, o.toString());
													}
												}
											};


	public IPrimitiveMap<String> Body() {

		return toaBody;
	}


	/**
	 *
	 * @param showType	訊息呈現方式
	 * @param messageType	訊息種類
	 * @param msgCode	訊息代號
	 * @param msgData	訊息內容
	 */
	public void setMsg(EnumShowType showType,
			EnumMessageType messageType,
			String msgCode,
			String msgData)
	{
		this.Headers().setInt(EnumToaHeader.OutputType, EnumOutputType.Message.ordinal());
		//將其餘參數組成 JSON 格式
		int showTypeOrd = showType.ordinal();
		int messageTypeOrd = messageType.ordinal();
		JSONObject obj = new JSONObject();
		obj.put(enumMsgHeader.showType.toString(), showTypeOrd);
		obj.put(enumMsgHeader.messageType.toString(), messageTypeOrd);
		obj.put(enumMsgHeader.msgCode.toString(), msgCode);
		obj.put(enumMsgHeader.msgData.toString(), msgData);
		outputData = obj.toString();
	}
	/**
	 * 設定單一欄位值
	 * @param fieldId 畫面欄位代號
	 * @param fieldValue 欄位輸出值
	 */
	public void addScrData(String fieldId, String fieldValue)
	{
		this.Headers().setInt(EnumToaHeader.OutputType, EnumOutputType.Screen.ordinal());
		outputMap.put(fieldId, fieldValue);
	}
	/**
	 * 複數欄位值
	 * @param fieldId	畫面欄位代號
	 * @param fieldList 欄位輸出 List
	 */
	public void addScrData(String fieldId, List<String> fieldList)
	{
		this.Headers().setInt(EnumToaHeader.OutputType, EnumOutputType.Screen.ordinal());
		for (String fieldValue : fieldList) {
			outputMap.accumulate(fieldId, fieldValue);
		}
	}

	@Override
	public String toString() {
		if (outputMap.size() > 0) {
			return outputMap.toString();
		} else {
			return outputData;
		}
	}
}
