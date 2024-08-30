package com.systex.jbranch.fubon.bth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 1.初版
 
 * @author ALLEN_1800063
 * @date 2018/04/19
 
 **/
@Repository("btiot005")
@Scope("prototype")
public class BTIOT005 extends BizLogic {
	private DataAccessManager dam = null;
	private BthFtpJobUtil bthUtil = new BthFtpJobUtil();
	private AuditLogUtil audit = null;

	String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
	public void BTIOT_005(Object body, IPrimitiveMap<?> header) throws Exception {
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		// 取得傳入參數 
		Map<String, Object> inputMap = (Map<String, Object>) body;
		Map<String, Object> jobParam = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
		
		// TBSYSFTP.FTPSETTINGID FTP畫面設定(CMMGR014)
		String ftpCode = (String) jobParam.get("ftpCode");

		//抓取SQL語法
		StringBuffer sb = new StringBuffer();				 
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		
		sb.append(" select INS_ID ");
		sb.append(" from VWIOT_MAIN ");
		sb.append(" where TRUNC(INS_SUBMIT_DATE) = TRUNC(SYSDATE) ");
		
		//執行SQL
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> dataList = dam.exeQuery(qc);
		
		
		
		//產出txt需要之參數
		String fileName = "IOT" + "_" + today + ".txt";
		String path = SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
		
		//把執行完SQL的結果產出txt
		outputTxt(path, fileName, dataList); 

	}


	private  void outputTxt(String path, String fileName, List<Map<String, Object>> dataList){
		FileWriter fw = null;
		int count = 0;
		try{
			fw = new FileWriter(path + fileName);
			for(Map<String, Object> map : dataList){
				fw.write(checkIsNull(map, "INS_ID"));
				fw.write(System.lineSeparator());
				fw.flush();;
				count++;
			}
			fw.append("TAILER" + String.format("%06d", count));
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			if(fw != null){
				try{
					fw.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}	
	
	
	/**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkIsNull(Map<String, Object> map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}
