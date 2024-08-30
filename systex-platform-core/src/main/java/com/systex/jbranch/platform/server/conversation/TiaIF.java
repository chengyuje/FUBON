package com.systex.jbranch.platform.server.conversation;

import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;
import com.systex.jbranch.platform.util.IPrimitiveMap;

public interface TiaIF {

	IPrimitiveMap<EnumTiaHeader> Headers();
}
