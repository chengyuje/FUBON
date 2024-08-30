package com.systex.jbranch.platform.common.log.appender;

import java.sql.Blob;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.log.LogHelper;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.ThreadDataPool;

public class JBranchDBAppender extends AppenderBase<ILoggingEvent> {

	final public static String JBRANCH_DB_APPENDER_PARAMS = "jbranch_db_appender_params";
	protected Logger logger = LoggerFactory.getLogger(JBranchDBAppender.class);
	protected DataAccessManager dam;
	private String oracleIdentify;
	private String oracleSequence;
	private String tableName;
	private String columnNameArray;
	private String mdcKeyArray;
	private boolean useVO;

	public String getOracleIdentify() {
		return oracleIdentify;
	}

	public void setOracleIdentify(String oracleIdentify) {
		this.oracleIdentify = oracleIdentify;
	}

	public String getOracleSequence() {
		return oracleSequence;
	}

	public void setOracleSequence(String oracleSequence) {
		this.oracleSequence = oracleSequence;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnNameArray() {
		return columnNameArray;
	}

	public void setColumnNameArray(String columnNameArray) {
		this.columnNameArray = columnNameArray;
	}

	public String getMdcKeyArray() {
		return mdcKeyArray;
	}

	public void setMdcKeyArray(String mdcKeyArray) {
		this.mdcKeyArray = mdcKeyArray;
	}

	@Override
	protected void append(ILoggingEvent event) {
		
		Map<String, Object> params = (Map<String, Object>) ThreadDataPool.getData(JBRANCH_DB_APPENDER_PARAMS);
		try {
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			
//			UUID uuid = (UUID) ThreadDataPool.getData(ThreadDataPool.KEY_UUID);

//			Branch branch = DataManager.getBranch(uuid);
//			WorkStation ws = DataManager.getWorkStation(uuid);
//			User user = DataManager.getUser(uuid);
//			ServerTransaction st = DataManager.getServerTransaction(uuid);
//			TransactionDefinition td = DataManager.getTransactionDefinition(uuid);
//			
//			String brchID = branch.getBrchID();
//			String wsID = ws.getWsID();
//			String txnCode = td.getTxnCode();
//			String tellerID = user.getUserID();
//			String depID = ""; //尚未實作
//			String roleID = user.getUserAuth(); 
//			String bizCodeName = (String) st.getPlatFormVO().getVar(PlatformVOHelper._BIZCODENAME);
//			String memo = (String) st.getPlatFormVO().getVar(PlatformVOHelper._EJMEMO);
			Map mdcParams = MDC.getCopyOfContextMap();
			if(params == null){
				params = new HashMap();
			}
			if(mdcParams != null){
				params.putAll(mdcParams);
			}
//			params.put(LogHelper.BRANCH_ID, brchID);
//			params.put(LogHelper.WS_ID, wsID);
//			params.put(LogHelper.TXN_CODE, txnCode);
//			params.put("TLRNO", tellerID);
//			params.put(LogHelper.ROLE_ID, roleID);
//			params.put(LogHelper.BIZ_CODE_NAME, bizCodeName);
//			params.put(LogHelper.MEMO, memo);
//			params.put(LogHelper.NOW, ts);
//			MDC.setContextMap(params);
			if(tableName == null || columnNameArray == null){
				logger.warn("無法取得JBranchDBAppender所需參數tableName與columnNameArray");
				return;
			}
			
			dam = new DataAccessManager();
			dam.setAutoCommit(true);
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			String identifySql = "";
			
			StringBuffer sql = new StringBuffer();
			sql.append("insert into " + tableName + "(");
			if(oracleIdentify != null){
				identifySql = oracleIdentify;
				sql.append(identifySql + ",");
			}
			sql.append(columnNameArray + ",VERSION,CREATETIME,CREATOR,LASTUPDATE,MODIFIER)");
			sql.append("values(");
			String[] columnNames = columnNameArray.split(",");
			String[] mdcKeys = mdcKeyArray.split(",");
			if(columnNames.length != mdcKeys.length){
				logger.warn("LoggerName=" + event.getLoggerName() + ",columnNameArray與mdcKeyArray長度不符");
				return;
			}
			if(oracleSequence != null){
				identifySql = oracleSequence + ".nextVal";
				sql.append(identifySql + ",");
			}
			for (int i = 0; i < columnNames.length; i++) {
				String columnName = columnNames[i].trim();
				String mdcKey = mdcKeys[i].trim();
				Object insertObject = params.get(mdcKey);
//				if (columnName.equals("")) {
//					Blob byteArrToBlob = ObjectUtil.byteArrToBlob(ObjectUtil.getBytes(insertObject));
//					
//				}
				if(insertObject == null){
					sql.append("null");
				}else{
					
//					if(mdcKey.startsWith("$")){
////						sql.append("?");
//						sql.append(":" + columnName);
//						byte[] bytes = ObjectUtil.getBytes(insertObject);
//						insertObject = ObjectUtil.byteArrToBlob(bytes);
//						logger.debug("insertObject={}", insertObject);
////						qc.setBinary(1, bytes);
//						qc.setObject(columnName, bytes);
//					}else{
						sql.append(":" + columnName);
						qc.setObject(columnName, insertObject);						
//					}
				}
				logger.debug("COLUMN=" + columnName + ",MDC key=" + mdcKeys[i] + ",value=" + insertObject);
				sql.append(",");

			}
			
			sql.append(":VERSION,:CREATETIME,:CREATOR,:LASTUPDATE,:MODIFIER)");
			
			String tellerId = (String) params.get(LogHelper.TLR_ID);
//			if (tellerId == null) {
//				tellerId = (String) params.get(LogHelper.TLR_ID+"_2");
//			}
			qc.setQueryString(sql.toString());
			qc.setObject("VERSION", 0);
			qc.setObject("CREATETIME", ts);
			qc.setObject("CREATOR", tellerId);
			qc.setObject("LASTUPDATE", ts);
			qc.setObject("MODIFIER", tellerId);

			dam.exeUpdate(qc);
			
			ThreadDataPool.setData(JBRANCH_DB_APPENDER_PARAMS, new HashMap());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
