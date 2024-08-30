package com.systex.jbranch.fubon.bth;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.DataAccessPostConditionIF;
import com.systex.jbranch.platform.common.scheduler.ScheduleUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.conversation.ConversationIF;
import com.systex.jbranch.platform.server.conversation.MapTiaIF;
import com.systex.jbranch.platform.server.conversation.ObjectTiaIF;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.DataAccessPostConditionIF;

@Repository("BthMain2ODS")
@Scope("prototype")
public class BthMain2ODS implements DataAccessPostConditionIF {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataAccessManager dam = null;
    public static final String VAR_CONVERSATION = "Conversation";

    @Override
	public void process(Map jobParaMap, Map scheduleParaMap) throws JBranchException {
		this.dam = PlatformContext.getBean(DataAccessManager.class);
		String jobid = (String) jobParaMap.get(SchedulerHelper.JOB_INFO_KEY_JOBID);
		Map<String, Object> ods = this.getODSData(jobid);
		if (ods == null) {
			logger.info("ODS未設定{} Job", jobid);
			return;
		}
		
		// 判斷SQL
		String sqlCmd = (String) ods.get("SQLCMD");
		String checkCondition = (String) ods.get("CHECKCONDITION");
		String field_s = (String) ods.get("FIELD_S");
		String field_e = (String) ods.get("FIELD_E");
		// 條件成立，進行Re-Run
		if (!this.reRunCheck(sqlCmd, checkCondition, field_s, field_e)) {
			int repeat = ((java.math.BigDecimal) ods.get("REPEAT")).intValue();
			long repeatInterval = ((java.math.BigDecimal) ods.get("REPEATINTERVAL")).longValue();
			Object job = jobParaMap.get(SchedulerHelper.JOB_INSTANCE);
			for (int i=0; i<repeat; i++) {
				try {
					logger.info("AFTER repeat {} / {}", i, repeat);
					Thread.sleep(repeatInterval * 1000);
					this.reRunJob(job, jobParaMap);
					if (this.reRunCheck(sqlCmd, checkCondition, field_s, field_e)) {
						// Re Run OK 
						logger.info("{} re-run OK", jobid);
				        Map transientVars = (Map) jobParaMap.get("transientVars");
				        ConversationIF conversation = (ConversationIF) transientVars.get(VAR_CONVERSATION);
				        Object body = null;

				        if (conversation.getTiaHelper().getTia() instanceof MapTiaIF) {
				            body = ((MapTiaIF) conversation.getTiaHelper().getTia()).Body();
				        }
				        else if (conversation.getTiaHelper().getTia() instanceof ObjectTiaIF) {
				            body = ((ObjectTiaIF) conversation.getTiaHelper().getTia()).getBody();
				        }
				        AuditLogUtil audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
				        audit.audit("0");    // 成功 --> 設為0在DATA_ACCESS_JOB才能視為成功
				        return;
					}
				} catch (Exception e) {
					logger.warn("Re-Run Error", e);
					throw new JBranchException(e.getMessage());
				}
			}
			throw new JBranchException("Re-Run " + jobid + " Failure!!");
		}
	}

    /**
     * 取得ODS的內容
     * @param jobid - JOB ID為key
     * @return
     */
    private Map<String, Object> getODSData(String jobid) {
    	List<Map<String, Object>> result = null;
    	try {
	    	QueryConditionIF queryCondition = this.dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    	queryCondition.setObject("JOBID", jobid);
	    	queryCondition.setQueryString("select * from tbsysods where jobid = :JOBID and CHK_TYPE='A'");
        	result = this.dam.exeQuery(queryCondition);
    	} catch (Exception e) {
    		logger.warn("Query err", e);
    	}
    	if (result == null || result.isEmpty()) return null;
    	return result.get(0);
    }
    
    /**
     * 檢查Re-Run後再Re-Run條件是否成立。
     * @param sql - 檢查的SQL
     * @param checkCondition - 檢查等式，若null就判斷SQL是否傳回0筆，是表示Re-Run成功，不必再Re-Run。
     * @param field_s - 起始值，checkCondition為=、>、>=、<、<=、<>、!=及between
     * @param field_e - 截止值，只用於between
     * @return true為Re-run條件成立，表示要再Re-Run；false表示Re-run成功，表示Job結束不必再Re-Run
     */
    private boolean reRunCheck(String sql, String checkCondition, String field_s, String field_e) {
    	try {
	    	QueryConditionIF queryCondition = this.dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    	queryCondition.setQueryString(sql);
	    	List<Map<String, Object>>  result = this.dam.exeQuery(queryCondition);
	    	if (null == checkCondition || "".equals(checkCondition.trim()))
	    		return !(result == null || result.isEmpty()) ;
    		else {
    			if (result != null && !result.isEmpty()) {
    				Object value = result.get(0).values().toArray()[0];		// 取得select count(*)的值
    				checkCondition = checkCondition.replaceAll(" ", "");	// 清空白
    				if ("IN".equalsIgnoreCase(checkCondition)) {
    					if (field_s != null) {
	    					String[] inValues = field_s.split(",");
	    					for (String inValue : inValues) {
	    						if (inValue.equals(value.toString())) return true;
	    					}
    					}
    					return false;
    				} else {
	    				int count = Integer.parseInt(value.toString());
	    				int start = Integer.parseInt(field_s);
	    				if ("=".equals(checkCondition)) {
	    					return count == start;
	    				} else if (">".equals(checkCondition)) {
	    					return count > start;
	    				} else if (">=".equals(checkCondition)) {
	    					return count >= start;
	    				} else if ("<".equals(checkCondition)) {
	    					return count < start;
	    				} else if ("<=".equals(checkCondition)) {
	    					return count <= start;
	    				} else if ("<>".equals(checkCondition) || "!=".equals(checkCondition)) {
	    					return count != start;
	    				} else if ("between".equalsIgnoreCase(checkCondition)) {
	    					int end = Integer.parseInt(field_e);
	    					return count >= start && count <= end;
	    				}
    				}
    				return true;		// 預設滿足SQLCMD
    			} else {
    				return true;
    			}
    		}
    	} catch (Exception e) {
    		logger.warn("Query err", e);
    		return true;
    	}
    }
    
    /**
     * 重新執行Job，係模擬原先執行Job的寫法
     * @param job - 來自TBSYSSCHDJOB的內容
     * @param jobInfoMap - 來自TBSYSSCHDJOB的parameters值並以分號分隔參數
     * @throws Exception
     */
    private void reRunJob(Object job, Map jobInfoMap) throws Exception {
		//取得job相關參數
        String parameterStr = (String) jobInfoMap.get(SchedulerHelper.JOB_INFO_KEY_PARAMETER);
        Map jobParaMap = ScheduleUtil.getParameter(parameterStr);

        //取得欲呼叫的method名稱
        String methodName = (String) jobParaMap.get("method");
        if (methodName == null || methodName.trim().length() == 0) {
            throw new Exception("method name not found");
        }
        BizLogic biz = (BizLogic) job;
        biz.execute((Map) jobInfoMap.get("transientVars"),(Map) jobInfoMap.get("args"), null);
    }

}
