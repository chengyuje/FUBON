package com.systex.jbranch.platform.server.conversation.message;

import java.util.HashMap;
import java.util.Map;

import com.systex.jbranch.platform.server.conversation.MapTiaIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import com.systex.jbranch.platform.util.PrimitiveMap;

public class MapTia extends Tia implements MapTiaIF {

	private IPrimitiveMap<String> tiaBody = new PrimitiveMap<String>((Map)getBody());
	
	public IPrimitiveMap<String> Body() {

		return tiaBody;
	}
	
	@Override
	public Object getBody()
	{
		if(!(super.getBody() instanceof Map))
		{
			super.setBody(new HashMap());
		}
		return super.getBody();	
	}

}
