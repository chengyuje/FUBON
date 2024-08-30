package com.systex.jbranch.platform.server.eclient.conversation.broadcast;

import java.util.HashMap;
import com.systex.jbranch.platform.server.conversation.message.Toa;
import com.systex.jbranch.platform.server.conversation.message.EnumToaHeader;
import com.systex.jbranch.platform.server.conversation.message.EnumOutputType;


public class TOABroadcast extends Toa {
	
	public 	enum EnumBroadCastType
	{
		PF01,		//中心連離線記號
		AP01,		//匯入序號
		AP02,		//後送序號
		AP03		//任一序號
	}	
	
	//private JSONObject outputMap = new JSONObject();
	private int timeOut=0;
	private HashMap<String,Object> broadcastData = null;
	private String outputData = null;
	
	public TOABroadcast() {
		this.Headers().setInt(EnumToaHeader.OutputType, EnumOutputType.BroadCast.ordinal());
	}		
	
	public void setBroadcastData(HashMap<String,Object> aryBroadcastData,
			EnumBroadCastType enumType, 
			int nTimeout, String sMsgData)
	{
		outputData = enumType.toString() + "=" + sMsgData;
		broadcastData = aryBroadcastData;
		timeOut = nTimeout;
	}
	
	public String toString() {
		return outputData;
	}

	public HashMap<String, Object> getBroadcastData() {
		return broadcastData;
	}
	public int getTimeOut() {
		return timeOut;
	}
}
