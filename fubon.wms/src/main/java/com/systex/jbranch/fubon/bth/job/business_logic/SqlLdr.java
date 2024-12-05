package com.systex.jbranch.fubon.bth.job.business_logic;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SqlLdr {

	private static String ROOTNAME = "D:\\";
	private static String CTL_SUFFIX = ".ctl";
	private static String LOG_SUFFIX = ".log";
	private static String BLANKS = " ";

	public static void main(String[] args) throws IOException, InterruptedException {
		// SIT >> WMSUSER/WMSUSER@172.19.243.30:5211/TWMSM
		// UAT >> WMSUSER/WMSUSERWMSUSER@172.19.243.30:5211/TWMS
		// ODS_C >> WMSUSER/(OL>0p;/@172.19.243.30:5211/TWMSC
		// 2020.11.27 避免密碼的字元@出錯 要用/"前後包起來

//		String db_name = "LOCAL";
		String db_name = "FUBON";

		DatabaseInfo info = new DatabaseInfo(db_name);

		String ctlName = "TBPRD_ELN_LINK_PROD";
		String logName = "TBPRD_ELN_LINK_PROD";
		String srcName = "UNDERLYING_20240906.csv";
		
		SqlLdr ldr = new SqlLdr();
		String command = ldr.getCommand(info, ctlName, logName, srcName);

//		StringTokenizer st = new StringTokenizer(command, SqlLdr.BLANKS);
//		String[] command_array = new String[st.countTokens()];
//		for(int i = 0; st.hasMoreTokens(); i++) {
//			command_array[i] = st.nextToken();
//		}
//
//		ProcessBuilder pb = new ProcessBuilder(command_array);	
//		Process p = pb.start();
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		p.getOutputStream().close();
		ldr.printLog(logName);

	}

	private String getCommand(DatabaseInfo info, String ctlName, String logName, String srcName) {
		StringBuilder sb = new StringBuilder();
		sb.append("cmd").append(SqlLdr.BLANKS);
		sb.append("/c").append(SqlLdr.BLANKS);
		sb.append("sqlldr").append(SqlLdr.BLANKS);
		sb.append(info.getUserName()).append("/");
		sb.append("\\\"").append(info.getPassword());
		sb.append("\\\"@").append(info.getHostName());
		sb.append(":").append(info.getPort());
		sb.append("/").append(info.getServiceName());
		sb.append(SqlLdr.BLANKS);
		sb.append("CONTROL=").append(SqlLdr.ROOTNAME).append(ctlName).append(SqlLdr.CTL_SUFFIX);
		sb.append(SqlLdr.BLANKS);
		sb.append("LOG=").append(SqlLdr.ROOTNAME).append(logName).append(SqlLdr.LOG_SUFFIX);
		sb.append(SqlLdr.BLANKS);
		sb.append("DATA=").append(SqlLdr.ROOTNAME).append(srcName);
//		sb.append(SqlLdr.BLANKS);
//		sb.append("CHARACTERSET AL32UTF8");
		return sb.toString();
	}

	private void printLog(String logName) {
		StringBuilder sb = new StringBuilder();
		sb.append(SqlLdr.ROOTNAME);
		sb.append(logName).append(SqlLdr.LOG_SUFFIX);

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

class DatabaseInfo {

	private String userName;
	private String password;
	private String hostName;
	private String port;
	private String serviceName;

	public DatabaseInfo(String db_name) {
		if ("LOCAL".equals(db_name)) {
			this.setUserName("FUBON_LOCAL_DB");
			this.setPassword("oracle");
			this.setHostName("localhost");
			this.setPort("1521");
			this.setServiceName("xe");
		} else if ("FUBON".equals(db_name)) {
			this.setUserName("WMSUSER");
			this.setPassword("1qaz@WSX");
			this.setHostName("172.19.243.28");
			this.setPort("5211");
			this.setServiceName("TWMSC");
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
