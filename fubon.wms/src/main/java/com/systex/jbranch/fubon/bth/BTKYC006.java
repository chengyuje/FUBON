package com.systex.jbranch.fubon.bth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.request.RequestContextHolder;

import com.systex.jbranch.app.server.fps.kycoperation.KycCoolingRelease;
import com.systex.jbranch.fubon.commons.CustomRequestScopeAttr;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 解除冷靜期批次作業
 * Created : 2019/11/28
 *
 **/

@SuppressWarnings("unused")
@Repository("btkyc006")
@Scope("prototype")
public class BTKYC006 extends BizLogic {
	private DataAccessManager dam = null;
	private AuditLogUtil audit = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 解除冷靜期，將C值更新
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void releaseCooling(Object body, IPrimitiveMap<?> header) throws Exception {
		// 紀錄排程監控log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
//		KycOperationJava kycOpt = (KycOperationJava) PlatformContext.getBean("kycoperation");
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		//先取得可解除冷靜期資料
		queryCondition.setQueryString("SELECT * FROM TBKYC_COOLING_PERIOD WHERE STATUS = 'C' AND TRUNC(EFFECTIVE_DATE) <= TRUNC(SYSDATE) ");
		List<Map<String, Object>> listCooling = dam.exeQuery(queryCondition);	
		
		//逐筆更新資料
		for(Map<String, Object> map : listCooling) {
			String seq = (map.get("SEQ") == null ? "" : map.get("SEQ").toString());			
			 
			KycCoolingRelease kcr = (KycCoolingRelease) PlatformContext.getBean("kyccoolingrelease");
			//更新KYC主檔, KYC主檔歷史資料, KYC當下最新風險屬性資料, 客戶主檔資料, 將此筆資料標示為"冷靜期已解除"
			kcr.coolingReleaseUpdate(seq);
		}
	}

	
	/**
	 * 解除冷靜期，將C值更新390主機
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void sendTo390(Object body, IPrimitiveMap<?> header) throws Exception {
		// 紀錄排程監控log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		try {
			//injecting Request Scope beans(sot701) outside of web request
			RequestContextHolder.setRequestAttributes(new CustomRequestScopeAttr());
			
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
			//先取得可解除冷靜期且尚未傳送主機資料
			queryCondition.setQueryString("SELECT * FROM TBKYC_COOLING_PERIOD WHERE SENT_390_STATUS = 'N' AND TRUNC(EFFECTIVE_DATE) <= TRUNC(SYSDATE) ");
			List<Map<String, Object>> listCooling = dam.exeQuery(queryCondition);	
			
			//逐筆更新資料
			for(Map<String, Object> map : listCooling) {
				String seq = (map.get("SEQ") == null ? "" : map.get("SEQ").toString());
				
				KycCoolingRelease kcr = (KycCoolingRelease) PlatformContext.getBean("kyccoolingrelease");
				//資料傳送390主機並更新此筆資料狀態			
				kcr.coolingReleaseSend390(seq);
			}
		} finally {
			//clear out the requestAttributes
			RequestContextHolder.resetRequestAttributes();
		}
	}

}

