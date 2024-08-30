package com.systex.jbranch.platform.common.scheduler;

import java.util.Map;

import org.hibernate.SessionFactory;

import com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterVO;

public interface JDBCScheduleJobIF {
	public static final String BEAN_ID = "jdbcjob";
	public void executeScheduleJob(SessionFactory sessionFactory, 
					AuditLogUtil audit, Map jobInfoMap, Map scheduleParaMap, TbsysschdadmasterVO mvo) throws Exception;
}
