/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author 1500617
 * @date 22/11/2016
 */
@Repository("backupbth")
@Scope("prototype")
public class BackupBth extends BizLogic {
	
	private AuditLogUtil audit = null;
	private String zipProgPath;	// 7zip program
	private String tarPath;
	private int keepDay = 30;
	
	public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		// 取得傳入參數
		@SuppressWarnings("unchecked")
		Map<String, Object> inputMap = (Map<String, Object>) body;
		@SuppressWarnings("unchecked")
		Map<String, Object> jobParam = (Map<String, Object>) inputMap
				.get(SchedulerHelper.JOB_PARAMETER_KEY);

		zipProgPath = (String) jobParam.get("zipProgPath");
		tarPath = (String) jobParam.get("tarPath");
		keepDay = Integer.parseInt((String)jobParam.get("keepDay"));
		
		backup();
		cleanup();
	}
	
	/**
	 * 將目錄內的檔案用7zip打包
	 * @throws Exception 
	 */
	private void backup() throws Exception {
		List<String> cmdList = new ArrayList<String>();
		cmdList.add("cmd");
		cmdList.add("/c");
		cmdList.add(zipProgPath);
		cmdList.add("a");
		cmdList.add("-t7z");
		cmdList.add(tarPath + "bak_%date:~0,4%-%date:~5,2%-%date:~8,2%.7z");
		cmdList.add(tarPath + "*");
		
		ProcessBuilder pb = new ProcessBuilder(cmdList);
		try {
			Process p = pb.start();
			logProces(p);
			int exitValue = p.waitFor();
			audit.audit("end zip process, exit value = " + exitValue);
		} catch (Exception e) {
			audit.audit(ExceptionUtils.getStackTrace(e));
		}
	}

	/**
	 * 刪除目錄舊檔案
	 * @throws Exception 
	 */
	private void cleanup() throws Exception {
		StringBuffer sb = new StringBuffer();
		File folder = new File(tarPath);

		if (folder.isAbsolute()) {
			long now = new Date().getTime();
			long keepDayL = keepDay * 24 * 60 * 60 * 1000;
			
			for (File f : folder.listFiles()) {
				long diff = now - f.lastModified();
				if (diff > keepDayL) {
					String name = f.getAbsolutePath();
					boolean b = f.delete();
					
					if (b) {
						sb.append("delete file [" + name + "]\n");
					}
				}
			}
		}
		
		if (sb.length() > 0) {
			audit.audit(sb.toString());
		}
	}

	private void logProces(Process p) throws Exception {
		BufferedReader inReader = null;
		BufferedReader errReader = null;
		String msg;
		String errMsg;
		StringBuffer sb = new StringBuffer();
		StringBuffer errSb = new StringBuffer();

		try {
			//log Customized msg in TBSYSSCHDADMASTER.MEMO
			inReader = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF8"));
			
			while((msg = inReader.readLine()) != null) {
				sb.append(msg).append("\n");
			}
			
			//log Error in TBSYSSCHDADMASTER.DESCRIPTION
			errReader = new BufferedReader(new InputStreamReader(p.getErrorStream(), "UTF8"));
			
			while((errMsg = errReader.readLine()) != null) {
				errSb.append(errMsg).append("\n");
			}
			
			// close stream
			p.getInputStream().close();
			p.getErrorStream().close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inReader != null) {
				try {
					inReader.close();
				} catch (IOException e) {
					;
				}
			}
			
			if (errReader != null) {
				try {
					errReader.close();
				} catch (IOException e) {
					;
				}
			}
		}

		if (sb.length() > 0) {
			audit.audit(sb.toString());
		}
		
		if (errSb.length() > 0) {
			audit.audit(errSb.toString());
		}
	}
	
}
