package com.systex.jbranch.platform.common.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class PathUtil {
	private static String FILE_SEPARATOR = System.getProperties().getProperty("file.separator");
	private static char[] charArray = FILE_SEPARATOR.toCharArray();
	private static String REPORT_PATH = "\\temp\\reports".replace('\\', charArray[0]);
	
	private static String getRoot() {
		return DataManager.getSystem().getRoot().replace('\\', charArray[0]);
	}

	private static String getTransaction() {
		return DataManager.getSystem().getPath().get("OutputReport").toString().replace('\\', charArray[0]);
	}
	
	/**
	 * @param txnCode 交易代碼
	 * @param reportID 報表id
	 * @param extendName 副檔名
	 * @return 絕對路徑
	 * @throws JBranchException
	 */
	public static String getReportPath(String txnCode, String reportID, String extendName)
			throws JBranchException {

			StringBuilder strReportFile = new StringBuilder();
			String strRoot = getRoot(); 
			String strTransaction = getTransaction();
			String DIR_OUTPUTREPORT = "Report";
			strReportFile.append(strRoot).append(FILE_SEPARATOR).append(
					strTransaction).append(
					DataManager.getTransactionDefinition(txnCode).getSysType()
							.trim()).append(FILE_SEPARATOR).append(txnCode)
					.append(FILE_SEPARATOR).append(DIR_OUTPUTREPORT).append(
							FILE_SEPARATOR).append(txnCode).append("_").append(
							reportID).append("." + extendName);
			return strReportFile.toString();
	}

	/**
	 * @param txnCode 交易代碼
	 * @return 絕對路徑
	 */
	public static String getSavePath(String txnCode) {

		String time = getCurrentTime("yyyyMMddhhmmssSSS");
		UUID uuidNew = new UUID();
		String dir = getServerHome() + REPORT_PATH;
		File dirFile = new File(dir);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		String savePath = dirFile.getPath() + "\\".replace('\\', charArray[0]) + txnCode + "_" + uuidNew.toString() + "_" + time + ".xls";
		
		return savePath;
	}

	private static String getCurrentTime(String format) {
		SimpleDateFormat sdFormat = new SimpleDateFormat(format);
		Date currentTime = new Date();
		String time = sdFormat.format(currentTime);
		return time;
	}
	
	public static String getServerHome(){
		return DataManager.getRealPath().replace('\\', charArray[0]);
	}
}
