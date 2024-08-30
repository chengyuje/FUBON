package com.systex.jbranch.platform.server.conversation.message;

import com.systex.jbranch.platform.server.conversation.ObjectToaIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import com.systex.jbranch.platform.util.PrimitiveMap;

public class Toa extends Tioa implements ObjectToaIF {
		
	private IPrimitiveMap<EnumToaHeader> toaHeaders = new PrimitiveMap<EnumToaHeader>(getHeaders());
	
	public IPrimitiveMap<EnumToaHeader> Headers()
	{
		return toaHeaders;
	}
}
