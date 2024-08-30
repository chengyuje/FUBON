package com.systex.jbranch.platform.server.pipeline.flex;

import com.systex.jbranch.platform.server.conversation.MapToaIF;
import com.systex.jbranch.platform.server.conversation.ObjectToaIF;
import com.systex.jbranch.platform.server.conversation.ToaHelperIF;
import com.systex.jbranch.platform.server.conversation.ToaIF;
import com.systex.jbranch.platform.server.conversation.message.EnumMessageType;
import com.systex.jbranch.platform.server.conversation.message.EnumOutputType;
import com.systex.jbranch.platform.server.conversation.message.EnumShowType;
import com.systex.jbranch.platform.server.conversation.message.EnumToaHeader;
import com.systex.jbranch.platform.server.conversation.message.MapToa;
import com.systex.jbranch.platform.server.conversation.message.Toa;
import com.systex.jbranch.platform.server.conversation.message.payload.Msg;

public abstract class ToaHelper implements ToaHelperIF {

	public MapToaIF createMapToa() {

		return new MapToa();
	}

	public ObjectToaIF createObjectToa() {

		return new Toa();
	}

	public void sendTOA(ToaIF toa) 
	{
		sendTOA(toa,false);		
	}

	public void sendTOA(EnumShowType showType, EnumMessageType messageType, String msgCode,String msgData) 
	{
		sendTOA(showType,messageType,msgCode,msgData,false);
	}

	public void sendTOA(EnumShowType showType, EnumMessageType messageType, String msgCode,String msgData, boolean endBracket)
	{
		ObjectToaIF toa = createObjectToa();
		toa.Headers().setStr(EnumToaHeader.OutputType, EnumOutputType.Message.toString());
		Msg msg = new Msg();
		msg.setShowType(showType);
		msg.setMessageType(messageType);
		msg.setMsgCode(msgCode);
		msg.setMsgData(msgData);
		toa.setBody(msg);
		sendTOA(toa,endBracket);
	}
}
