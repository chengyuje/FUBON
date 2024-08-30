/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author 1500617
 * @date 7/6/2016
 */
@Repository("testbth")
@Scope("prototype")
public class TESTBTH extends BizLogic {
	private AuditLogUtil audit = null;
	
	/** job key for StoredProcedure parameter */
	private final String nArg1 = "arg1";

	public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		//取得傳入參數
		@SuppressWarnings("unchecked")
		Map<String, Object> inputMap = (Map<String, Object>) body;
		@SuppressWarnings("unchecked")
		Map<String, Object> jobParameter = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
		String vArg1 = (String)jobParameter.get(nArg1);
		
		audit.audit("Hello " + this.getClass().getSimpleName() + ", " + nArg1 + " = " + vArg1);
	}
}
