package com.systex.jbranch.platform.server.conversation;

import com.systex.jbranch.platform.server.conversation.message.EnumMessageType;
import com.systex.jbranch.platform.server.conversation.message.EnumShowType;


public interface ToaHelperIF {

	public static String AP_HOST_NAME = "$AP_HOST_NAME";
	public static String GW_HOST_NAME = "$GW_HOST_NAME";
	public static String TXN_DATA = "$TOTA_TXN_DATA";
	
	MapToaIF createMapToa();
	ObjectToaIF createObjectToa();
		
	void sendTOA(EnumShowType showType,
			EnumMessageType messageType,
			String msgCode,
			String msgData);
	
	void sendTOA(EnumShowType showType,
			EnumMessageType messageType,
			String msgCode,
			String msgData,
			boolean bEndBreKet);

	void sendTOA(ToaIF toa);
	
	void sendTOA(ToaIF toa, 
			boolean bEndBreKet);
}
