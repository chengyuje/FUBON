package com.systex.jbranch.platform.common.log.writer;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.log.appender.JBranchDBAppender;
import com.systex.jbranch.platform.common.util.ThreadDataPool;

public class FPSEJUtil {
	
	private static final String EJ_LOG = "EJ_LOG";
	
	private static Logger ejLogger = LoggerFactory.getLogger(EJ_LOG);
	
	final public static String CUSTOMERID = "CUSTOMERID";
	final public static String CUSTOMERNAME = "CUSTOMERNAME";
	final public static String RESULTDATA = "RESULTDATA";
	
	/**
	 * 軌跡紀錄(TBSYSFPSEJ)
	 * @throws JBranchException
	 */
	public static void writeEJ() throws JBranchException {
		writeEJ(null);
	}
	
	/**
	 * 軌跡紀錄(TBSYSFPSEJ)
	 * @param custId 客戶ID
	 * @param custName 客戶姓名
	 * @param resultData 結果
	 * @throws JBranchException
	 */
	public static void writeEJ(String resultData) throws JBranchException {

		Map params = new HashMap();
		params.put(RESULTDATA, resultData);

		ThreadDataPool.setData(JBranchDBAppender.JBRANCH_DB_APPENDER_PARAMS, params);
		
		ejLogger.debug("FPSEJUtil.writeEJ");
	}
}
