package com.systex.jbranch.platform.common.scheduler;

import java.util.Map;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterVO;
import com.systex.jbranch.platform.server.conversation.ConversationIF;

public interface DataAccessScheduleJobIF {
	public static final String BEAN_ID = "dataaccessjob";
	public void executeScheduleJob(UUID uuid, ConversationIF conversation, Map jobInfoMap, Map scheduleParaMap);
}
