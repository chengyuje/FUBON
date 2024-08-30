package com.systex.jbranch.platform.server.conversation;

import com.systex.jbranch.platform.common.dataManager.UUID;

public interface ConversationIF {
	
	TiaHelperIF getTiaHelper();
	
	ToaHelperIF getToaHelper();
	
	UUID getUUID();
}
