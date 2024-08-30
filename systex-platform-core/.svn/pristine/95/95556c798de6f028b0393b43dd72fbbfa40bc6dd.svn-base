package com.systex.jbranch.platform.server.config;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;

public class SystemCfg {
// ------------------------------ FIELDS ------------------------------

    private static Logger logger = LoggerFactory.getLogger(SystemCfg.class);
    static final String FILE_SEPARATOR = System.getProperties().getProperty("file.separator");

// -------------------------- STATIC METHODS --------------------------

    public static String getSysFolder(EnumSysPath enumSysPath) {
        StringBuilder sb = new StringBuilder();
        sb.append(DataManager.getSystem().getRoot());
        sb.append(FILE_SEPARATOR);
        sb.append(DataManager.getSystem().getPath().get(enumSysPath.toString()));
        return sb.toString();
    }

    public static InputStream getWorkflowInputStream(String workFlowFile) throws Exception {
        InputStream is = null;
//		try {
        is = new FileInputStream(workFlowFile);
//		} catch (Exception e) {
//			String msg = StringUtil.getStackTraceAsString(e);
// 			LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_ERROR, msg);
//		}
        return is;
    }

    public static void threadLogger(String strData) {
        String threadName = Thread.currentThread().getName();
        if (logger.isDebugEnabled()) {
            logger.debug("[" + threadName + "] " + strData);
        }
    }
}
