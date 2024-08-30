package com.systex.jbranch.platform.common.scheduler;

import java.sql.Connection;
import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface DataAccessPreConditionIF {
	void process( Map jobParaMap, Map scheduleParaMap) throws JBranchException;
}
