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
import com.systex.jbranch.platform.common.scheduler.DataAccessPreConditionIF;
import com.systex.jbranch.platform.common.scheduler.ScheduleUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.conversation.ConversationIF;
import com.systex.jbranch.platform.server.conversation.MapTiaIF;
import com.systex.jbranch.platform.server.conversation.ObjectTiaIF;

@Repository("BthMain2PreODS")
@Scope("prototype")
public class BthMain2PreODS implements DataAccessPreConditionIF {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataAccessManager dam = null;

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
		// 條件不成立，進行Re-Run
		if (!this.reCheck(sqlCmd, checkCondition, field_s, field_e)) {
			int repeat = ((java.math.BigDecimal) ods.get("REPEAT")).intValue();
			long repeatInterval = ((java.math.BigDecimal) ods.get("REPEATINTERVAL")).longValue();
			for (int i=0; i<repeat; i++) {
				try {
					logger.info("BEFORE repeat {} / {}", i, repeat);
					Thread.sleep(repeatInterval * 1000);
					if (this.reCheck(sqlCmd, checkCondition, field_s, field_e)) {
				        return;
					}
				} catch (Exception e) {
					logger.warn("Re-Run Error", e);
					throw new JBranchException(e.getMessage());
				}
			}
			throw new JBranchException("Pre-Run " + jobid + " Failure!!");
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
	    	queryCondition.setQueryString("select * from tbsysods where jobid = :JOBID and CHK_TYPE='B'");
        	result = this.dam.exeQuery(queryCondition);
    	} catch (Exception e) {
    		logger.warn("Query err", e);
    	}
    	if (result == null || result.isEmpty()) return null;
    	return result.get(0);
    }
    
    /**
     * 檢查Re-Check後再Re-Check條件是否成立。
     * @param sql - 檢查的SQL
     * @param checkCondition - 檢查等式，若null就判斷SQL是否傳回0筆，是表示Re-Run成功，不必再Re-Run。
     * @param field_s - 起始值，checkCondition為=、>、>=、<、<=、<>、!=、in及between
     * @param field_e - 截止值，只用於between
     * @return true為Re-run條件成立，表示要再Re-Run；false表示Re-run成功，表示Job結束不必再Re-Run
     */
    private boolean reCheck(String sql, String checkCondition, String field_s, String field_e) {
    	try {
	    	QueryConditionIF queryCondition = this.dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    	queryCondition.setQueryString(sql);
	    	List<Map<String, Object>>  result = this.dam.exeQuery(queryCondition);
	    	if (null == checkCondition || "".equals(checkCondition.trim())) {
	    		return !(result == null || result.isEmpty()) ;
	    	} else {
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
}
