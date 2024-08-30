package com.systex.jbranch.platform.server.conversation.message;

import com.systex.jbranch.platform.server.conversation.ObjectTiaIF;
import com.systex.jbranch.platform.server.conversation.message.MapTia;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import com.systex.jbranch.platform.util.PrimitiveMap;
import java.util.Map;

public class Tia extends Tioa implements ObjectTiaIF{

	private IPrimitiveMap<EnumTiaHeader> tiaHeaders = new PrimitiveMap<EnumTiaHeader>(getHeaders());
		
	public IPrimitiveMap<EnumTiaHeader> Headers()
	{
		return tiaHeaders;
	}
	
	public static Tia valueOf(Tia tia)
	{
		if(tia.getBody() instanceof Map)
		{
			MapTia mapTia = new MapTia();
			mapTia.setHeaders(tia.getHeaders());
			mapTia.setBody(tia.getBody());
			return mapTia;
		}
		return tia;
	}
}
