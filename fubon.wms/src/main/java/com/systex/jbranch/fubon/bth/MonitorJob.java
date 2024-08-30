package com.systex.jbranch.fubon.bth;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author 1700212
 * @date 15/01/2018
 */
@Repository("monitorjob")
@Scope("prototype")
public class MonitorJob extends BizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(MonitorJob.class);
	
    public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
    	this.dam = this.getDataAccessManager(); 
    	String sql1 = "select param_name from tbsysparameter where param_type='MONITOR.JOB' and param_code='days'";
    	QueryConditionIF queryCondition2 = this.dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    	queryCondition2.setQueryString(sql1);
    	List<Map<String, Object>> list1 = this.dam.exeQuery(queryCondition2);
    	String days = "0.5";
    	if (list1 != null && !list1.isEmpty()) {
    		days = (String) list1.get(0).get("PARAM_NAME");
    		try {
    			Double.parseDouble(days);
    		} catch (Exception e) {
    			logger.warn("days must be numeric!!");
    			days = "0.5";
    		}
    	}
    	
    	QueryConditionIF queryCondition = this.dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    	String sql = "select * from tbsysschd where SCHEDULEID in (select distinct SCHEDULEID from TBSYSSCHDADMASTER where CREATETIME between sysdate - " + days + " and sysdate)";
    	queryCondition.setQueryString(sql);
    	List<Map<String, Object>> list = this.dam.exeQuery(queryCondition);
    	if (list == null || list.isEmpty()) {
    		// 空的也要寄信
    		this.sendMail("前" + days + "日皆無批次執行記錄");
    		throw new JBranchException("前" + days + "日皆無批次執行記錄");
    	}
    	
    	StringBuffer sb = new StringBuffer();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	for (Map<String, Object> vo : list) {
    		String scheduleId = (String) vo.get("SCHEDULEID");
    		sb.append("<h4>");
    		sb.append(scheduleId); 
    		sb.append(" ");
    		sb.append((String) vo.get("SCHEDULENAME"));
    		sb.append("[");
    		sb.append((String) vo.get("PROCESSOR"));
    		sb.append("]</h4><ul>");
    		
    		StringBuffer sbSQL = new StringBuffer();
    		sbSQL.append("select a.JOBID, a.starttime, a.endtime, a.type, a.status, a.result, a.description, b.MESSAGE ");
    		sbSQL.append("from TBSYSSCHDADMASTER a left JOIN tbsysschdaddetail b on a.auditid=b.auditid and a.SCHEDULEID=b.SCHEDULEID ");
    		sbSQL.append("where a.createtime between sysdate - " + days + " and sysdate and a.SCHEDULEID='" + scheduleId + "' ");
    		sbSQL.append("order by a.SCHEDULEID, b.createtime ");

    		QueryConditionIF queryCondition1 = this.dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    		queryCondition1.setQueryString(sbSQL.toString());
        	List<Map<String, Object>> admasters = this.dam.exeQuery(queryCondition1);
        	for (Map<String, Object> mastVO : admasters) {
        		sb.append("<li>");
        		sb.append((String) mastVO.get("JOBID"));
        		sb.append(" (");
        		sb.append(sdf.format((Timestamp) mastVO.get("STARTTIME")));
        		sb.append(" ~ ");
        		sb.append(mastVO.get("ENDTIME") == null ? "NONE " : sdf.format((Timestamp) mastVO.get("ENDTIME")));
        		sb.append(") ");
        		sb.append("<ul>");
        		sb.append("<li>模式: ");
        		sb.append("1".equals((String) mastVO.get("TYPE")) ? "自動" : "手動");
        		sb.append("</li><li>狀態: ");
        		if ("2".equals((String) mastVO.get("STATUS"))) {
        			sb.append("1".equals((String) mastVO.get("RESULT")) ? "成功" : "失敗");
        			sb.append("</li><li>訊息: ");
        			sb.append(((String) mastVO.get("MESSAGE")).replaceAll("\n", "<br>"));
        			sb.append("</li>");
        			String descr = (String) mastVO.get("DESCRIPTION");
        			if (descr != null && !"".equals(descr.trim()) ) {
        				sb.append("<li>描述: ");
        				sb.append(descr.replaceAll("\n", "<br>"));
        				sb.append("</li>");
        			}
        		} else {
        			sb.append("運行中");
        			sb.append("</li>");
        		}
        		sb.append("</li>");
        		sb.append("</ul>");
        	}
    		sb.append("</ul>");
    	}
    	this.sendMail(sb.toString());
    }
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private void sendMail(String content) throws Exception {
    	logger.info("MAIL Context:\n" +content);
    	String sql = "select param_name from tbsysparameter where param_type='MONITOR.JOB' and param_code='senders'";
    	if (this.dam == null) this.dam = this.getDataAccessManager(); 
    	QueryConditionIF queryCondition2 = this.dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    	queryCondition2.setQueryString(sql);
    	List<Map<String, Object>> list = this.dam.exeQuery(queryCondition2);
    	if (list == null || list.isEmpty()) {
    		throw new JBranchException("未設定[MONITOR.JOB]系統參數");
    	}
    	String param_name = (String) list.get(0).get("PARAM_NAME");
    	if (param_name == null || "".equals(param_name.trim())) throw new JBranchException("未設定[MONITOR.JOB]的senders參數");
    	String[] senders = param_name.split(","); 
    	FubonSendJavaMail sendMail = new FubonSendJavaMail();
		FubonMail mail = new FubonMail();
		
		List<Map<String, String>> mailList = new ArrayList<Map<String,String>>();
		for (String email : senders) {
			logger.info("send to {}", email);
			Map<String, String> mailMap = new HashMap<String, String>();
			mailMap.put(FubonSendJavaMail.MAIL, email);
			mailList.add(mailMap);
		}
		mail.setSubject(sdf.format(new Date()) + "排程監控回報");
		mail.setLstMailTo(mailList);
		mail.setContentType("text/html");
		mail.setContent(content);
		Map<String, Object> annexData = new HashMap<String, Object>();
		sendMail.sendMail(mail, annexData);
    }
}
