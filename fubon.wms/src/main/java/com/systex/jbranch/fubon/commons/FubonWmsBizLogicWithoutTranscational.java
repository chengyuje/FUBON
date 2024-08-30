package com.systex.jbranch.fubon.commons;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.log.appender.JBranchDBAppender;
import com.systex.jbranch.platform.common.util.ThreadDataPool;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.common.errMsg.MsgGet;
import com.systex.jbranch.platform.server.conversation.ConversationIF;
import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component
public class FubonWmsBizLogicWithoutTranscational extends BizLogic {

	/**需要寫EJ**/
	private static final String JRNTYPE_WRITE_EJ = "1";  
	
	public static final String ABDBMON = "abdbmonSessionFactory";
	public static final String ABDBHIS = "abdbhisSessionFactory";
	protected int recordOfPage = 20;
	
	protected Logger logger = LoggerFactory.getLogger(FubonWmsBizLogicWithoutTranscational.class);
	
	@Override
    public DataAccessManager getDataAccessManager(String dbID) throws JBranchException{

    	return new DataAccessManager(dbID);
    }

	@Override
	public Object getCommonVariable(String key) throws JBranchException {
		if (FubonSystemVariableConsts.AVAILAREALIST.equals(key)) {
			return getUserVariable(key);
		}

		if (SystemVariableConsts.AVAILBRANCHLIST.equals(key)) {
			return getUserVariable(key);
		}

		if (SystemVariableConsts.LOGINBRH.equals(key)) {
			return getUserVariable(key);
		}
		
		if (FubonSystemVariableConsts.AVAILREGIONLIST.equals(key)) {
			return getUserVariable(key);
		}
		
		if (FubonSystemVariableConsts.MEM_LOGIN_FLAG.equals(key)) {
			return getUserVariable(key);
		}
		
		return super.getCommonVariable(key);
	};
	
	protected void initUUID() {
		if(uuid == null){
			uuid = (UUID) ThreadDataPool.getData(ThreadDataPool.KEY_UUID);
		}
	}

	@Override
	public void execute(Map transientVars, Map args, PropertySet ps)
			throws WorkflowException {
		initUUID();
		conversation = (ConversationIF) transientVars.get(VAR_CONVERSATION);
		IPrimitiveMap header = conversation.getTiaHelper().getTia().Headers();
		//交易前先塞MDC欄位，容許交易自行覆蓋
		putEJParam(args, header);
		try {
			super.execute(transientVars, args, ps);
		} catch (WorkflowException e) {
			e.printStackTrace();
			throw e; //為交易結束final固定觸發EJ寫try catch，故exception直接往外丟
		} finally{
			//觸發EJ
			writeEJ();
		}
	}
 
    public int getRecordOfPage() {
		return recordOfPage;
	}

	public void setRecordOfPage(int recordOfPage) {
		this.recordOfPage = recordOfPage;
	}
	
	/*
	 * 取得電文序號
	 */
	public String getEsbSeq() throws JBranchException {
		DataAccessManager dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		condition.setQueryString("SELECT SQ_TBSYS_ESB_LOG_HSTANO.NEXTVAL FROM DUAL");
		List<Map<String, Object>> list = dam.exeQuery(condition);
		BigDecimal nextVal = (BigDecimal)list.get(0).get("NEXTVAL");
		String formatStr = "%07d";
		return String.format(formatStr, nextVal.intValueExact());
	}
	
	/**
	 * 紀錄EJ
	 */
	private void writeEJ() {
		try {
			String jrnType = DataManager.getTransactionDefinition(uuid).getJrnType();
			logger.debug("txnCode: " + DataManager.getTransactionDefinition(uuid).getTxnCode());
			if (StringUtils.equals(jrnType, JRNTYPE_WRITE_EJ)) {
				//取得EJ Log Appender
				Logger ejLogger = LoggerFactory.getLogger("WMS_EJ_LOG");
				//觸發寫EJ Log
				ejLogger.info("writeEJ");
			}
		} catch (Exception e) {
			//catch後僅記錄log，EJ記錄失敗不可影響正常交易
			logger.error("Write EJ Error!!!", e);
		}
	}
	

	/**
	 * EJ欄位塞值
	 * @param args
	 * @param header
	 */
	private void putEJParam(Map args, IPrimitiveMap header) {
		try {
			String jrnType = DataManager.getTransactionDefinition(uuid).getJrnType();
			if (StringUtils.equals(jrnType, JRNTYPE_WRITE_EJ)) {
				WorkStation ws = DataManager.getWorkStation(uuid);//取得工作站資訊
				
				//將需要紀錄資料塞入MDC中(key值須對應log.xml mdcKeyArray)
				MDC.put("BRCHID", header.getStr(EnumTiaHeader.BranchID));
				MDC.put("WSID",ws.getWsIP() );
				MDC.put("TXNCODE", header.getStr(EnumTiaHeader.TxnCode));
				MDC.put("TELLERID",header.getStr(EnumTiaHeader.TlrID));
				//MDC.put("DEPID", "");
				MDC.put("ROLEID",(String)SysInfo.getInfoValue(SystemVariableConsts.LOGINROLE));
				MDC.put("CUSTOMERID", header.getStr(EnumTiaHeader.CustomerID));
				//MDC.put("CUSTOMERNAME","");
				MDC.put("BIZCODENAME", header.getStr(EnumTiaHeader.BizCode));
				//MDC.put("MEMO","");
				//MDC.put("RESULTDATA","");
				
				//非String的資料使用ThreadDataPool傳入(key值須對應log.xml mdcKeyArray)
				Map<String, Object> ejParam = new HashMap<String, Object>();//儲存非字串資料
				ejParam.put("TXNDATETIME", new Timestamp(System.currentTimeMillis()));
				ThreadDataPool.setData(JBranchDBAppender.JBRANCH_DB_APPENDER_PARAMS, ejParam);
			}
		      
		} catch (Exception e) {
			//catch後僅記錄log，EJ記錄失敗不可影響正常交易
			logger.error("Write EJ Error!!!", e);
		}
	}
	
	/**
	 * 錯誤訊息帶參數
	 * @param msgCode
	 * @return
	 * @throws JBranchException
	 */
	public String getMsg(String msgCode) throws JBranchException {
		try {
			return getMsg(msgCode, new ArrayList<String>());
		} catch(JBranchException e){
			logger.error(e.getMessage(), e);
			return "";
		}
	}
	
	/**
	 * 錯誤訊息帶參數
	 * @param msgCode
	 * @param errMsgList
	 * @return
	 * @throws JBranchException
	 */
	public String getMsg(String msgCode, List<String> errMsgList) throws JBranchException {
		try{
			MsgGet msg = new MsgGet();
			return msg.getMsg(msgCode, errMsgList);
		}
		catch(JBranchException e){
			logger.error(e.getMessage(), e);
			return "";
		}
	}

}
