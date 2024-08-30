package com.systex.jbranch.platform.server.conversation.message;

import com.systex.jbranch.platform.server.conversation.MapToaIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import com.systex.jbranch.platform.util.PrimitiveMap;
import java.util.Map;
import java.util.HashMap;

public class MapToa extends Toa implements MapToaIF {

	private IPrimitiveMap<String> toaBody = new PrimitiveMap<String>((Map)getBody());

	public IPrimitiveMap<String> Body()
	{
		return toaBody;
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
