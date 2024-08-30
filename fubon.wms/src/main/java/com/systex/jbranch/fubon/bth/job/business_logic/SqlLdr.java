package com.systex.jbranch.fubon.bth.job.business_logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SqlLdr {
	
	private static String rootName = "D:\\";
	private static String ctlSuffix = ".ctl";
	private static String logSuffix = ".log";
	private static String blanks = " ";

	public static void main(String[] args) throws IOException, InterruptedException {

		// SIT >> WMSUSER/WMSUSER@172.19.243.30:5211/TWMSM
		// UAT >> WMSUSER/WMSUSERWMSUSER@172.19.243.30:5211/TWMS
		// ODS_C >> WMSUSER/(OL>0p;/@172.19.243.30:5211/TWMSC
		// 2020.11.27 避免密碼的字元@出錯 要用/"前後包起來

		// LOCAL=============================
		String userName = "FUBON_LOCAL_DB";
		String password = "oracle";
		String hostName = "localhost";
		String port = "1521";
		String serviceName = "xe";
		// LOCAL=============================

		// Fubon=============================
//		String userName = "WMSUSER";
//		String password = "1qaz@WSX";
//		String hostName = "172.19.243.28";
//		String port = "5211";
//		String serviceName = "TWMSC";
		// Fubon=============================

		String ctlName = "TBPMS_EMP_DAILY_TXN_SUM_D_SG";
		String logName = "TBPMS_EMP_DAILY_TXN_SUM_D_SG";
		String srcName = "EMP_DAILY_TXN_SUM_D_20240608.TXT";
		SqlLdr ldr = new SqlLdr();
		String command = ldr.getCommand(userName, password, hostName, port, serviceName, ctlName, logName, srcName);

		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		p.getOutputStream().close();
		ldr.printLog(logName);
	}

	private String getCommand(String username, String password, String hostName, String port, String serviceName,
			String ctlName, String logName, String srcName) {
		StringBuilder sb = new StringBuilder();
		sb.append("cmd").append(SqlLdr.blanks);
		sb.append("/c").append(SqlLdr.blanks);
		sb.append("sqlldr").append(SqlLdr.blanks);
		sb.append(username).append("/");
		sb.append("\\\"").append(password);
		sb.append("\\\"@").append(hostName);
		sb.append(":").append(port);
		sb.append("/").append(serviceName);
		sb.append(SqlLdr.blanks);
		sb.append("CONTROL=").append(SqlLdr.rootName).append(ctlName).append(SqlLdr.ctlSuffix);
		sb.append(SqlLdr.blanks);
		sb.append("LOG=").append(SqlLdr.rootName).append(logName).append(SqlLdr.logSuffix);
		sb.append(SqlLdr.blanks);
		sb.append("DATA=").append(SqlLdr.rootName).append(srcName);
		return sb.toString();
	}
	

	private void printLog(String logName) {
		StringBuilder sb = new StringBuilder();
		sb.append(SqlLdr.rootName);
		sb.append(logName).append(SqlLdr.logSuffix);
		
//		File file = new File(sb.toString().trim());
//		try (FileReader fw = new FileReader(file); 
//				BufferedReader br = new BufferedReader(fw);) {
//			String line;
//			while ((line = br.readLine()) != null) {
//				System.out.println(line.trim());
//			}
//		} catch (IOException e) {
//
//		}
		
		Path path = Paths.get(sb.toString().trim());
		try {
			List<String> source = Files.readAllLines(path, Charset.defaultCharset());
			for (String str : source) {
				System.out.println(str.trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
