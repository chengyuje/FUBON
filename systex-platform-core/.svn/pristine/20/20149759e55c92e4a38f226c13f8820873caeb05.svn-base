package com.systex.jbranch.platform.common.log.writer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.log.appender.JBranchDBAppender;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.ThreadDataPool;

public class SecuLogUtil {

	final public static String ADD = "1";
	final public static String UPDATE = "2";
	final public static String DELETE = "3";
	
	final public static String USER = "1";
	final public static String GROUP = "2";
	final public static String FUNCTION = "3";
	final public static String ORG = "4";
	final public static String MODULE = "5";
	final public static String USER2GROUP = "6";
	final public static String GROUP2FUNCTION = "7";
	
	final public static String ACTION = "ACTION";
	final public static String TYPE = "TYPE";
	final public static String DATA1 = "DATA1";
	final public static String DATA2 = "DATA2";
	
	private static final String SECU_LOG = "SECU_LOG";
	
	private static Logger secuLogLogger = LoggerFactory.getLogger(SECU_LOG);

	/**
	 * 資料安控
	 * @param action 動作
	 * @param type 種類
	 * @param data1 異動前vo
	 * @param data2 異動後vo
	 * @throws JBranchException
	 */
	public static void writeLog(String action, String type, VOBase data1, VOBase data2) throws JBranchException {

		Map params = new HashMap();
		
		params.put(ACTION, action);
		params.put(TYPE, type);
		if(data1 != null){
			params.put(DATA1, ObjectUtil.voToMap(data1));
		}
		if(data2 != null){
			params.put(DATA2, ObjectUtil.voToMap(data2));
		}
		
		
		ThreadDataPool.setData(JBranchDBAppender.JBRANCH_DB_APPENDER_PARAMS, params);
		
		secuLogLogger.debug("SecuLogUtil.writeLog");
	}
}
