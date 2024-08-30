/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 1. 讀取DB產出 .yyyymmdd
 * 2. 將.yyyymmdd 上傳FTP
 * #1445 修改分行別邏輯 雖此處已廢棄但還是更新 避免後續查詢有問題 20230414 SamTu
 * @author 1600216
 * @date 2016/11/16
 *
 */
@Repository("aobrhbth")
@Scope("prototype")
public class AOBRHBTH extends BizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	public void createFileBth (Object body, IPrimitiveMap<?> header) throws JBranchException, IOException {
		ODS_AOBRH();
	}
	
	public void ODS_AOBRH () throws JBranchException, IOException {
				
		StringBuffer sb = new StringBuffer();
		// modify by ocean: 恢復版本至39574，並修改SQL(兼職FC的人員需涵蓋在內) 
		sb.append("SELECT INFO.AO_CODE, ");
		sb.append("       INFO.BRANCH_NBR AS BRANCH_NBR, ");
		sb.append("       JOB_RANK.ROLE_NAME AS AO_JOB_RANK, ");
		sb.append("       RPAD(NVL(MEM.EMP_PHONE_NUM,' '),15,' ') AS TEL ");
		sb.append("FROM VWORG_EMP_INFO INFO ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON INFO.EMP_ID = MEM.EMP_ID ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT MEM_ROL.EMP_ID, MEM_ROL.ROLE_ID, SYS_ROLE.ROLE_NAME ");
		sb.append("  FROM TBORG_MEMBER_ROLE MEM_ROL ");
		sb.append("  LEFT JOIN (SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME FROM TBORG_ROLE WHERE REVIEW_STATUS = 'Y' AND IS_AO = 'Y') SYS_ROLE ON MEM_ROL.ROLE_ID = SYS_ROLE.ROLE_ID ");
		sb.append("  WHERE ROLE_NAME IS NOT NULL ");
		sb.append("  GROUP BY MEM_ROL.EMP_ID, MEM_ROL.ROLE_ID, SYS_ROLE.ROLE_NAME ");
		sb.append(") JOB_RANK ON INFO.EMP_ID = JOB_RANK.EMP_ID ");
		sb.append("WHERE INFO.BRANCH_NBR >= 200 ");
		sb.append("AND INFO.BRANCH_NBR <= 900 ");
		sb.append("AND TO_NUMBER(INFO.BRANCH_NBR) <> 806 AND TO_NUMBER(INFO.BRANCH_NBR) <> 810");
		sb.append("AND JOB_RANK.ROLE_NAME IS NOT NULL ");
		
//		sb.append("SELECT AO.AO_CODE, DEFN.DEPT_ID AS BRANCH_NBR, JOB_RANK.ROLE_NAME AS AO_JOB_RANK,");
//		sb.append("RPAD(NVL(MEM.EMP_PHONE_NUM,' '),15,' ') AS TEL  ");
//		sb.append("FROM TBORG_SALES_AOCODE AO ");
//		sb.append("LEFT JOIN TBORG_MEMBER MEM ON AO.EMP_ID = MEM.EMP_ID ");
//		sb.append("LEFT JOIN ( ");
//		sb.append("SELECT MEM_ROL.EMP_ID, MEM_ROL.ROLE_ID, SYS_ROLE.ROLE_NAME ");
//		sb.append("FROM TBORG_MEMBER_ROLE MEM_ROL ");
//		sb.append("LEFT JOIN (SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME FROM TBORG_ROLE WHERE REVIEW_STATUS = 'Y' AND IS_AO = 'Y') SYS_ROLE ON MEM_ROL.ROLE_ID = SYS_ROLE.ROLE_ID ");
//		sb.append("WHERE ROLE_NAME IS NOT NULL ");
//		sb.append("GROUP BY MEM_ROL.EMP_ID, MEM_ROL.ROLE_ID, SYS_ROLE.ROLE_NAME ");
//		sb.append(") JOB_RANK ON MEM.EMP_ID = JOB_RANK.EMP_ID ");
//		sb.append("LEFT JOIN TBORG_DEFN DEFN ON DEFN.DEPT_ID = MEM.DEPT_ID AND DEFN.ORG_TYPE = '50' ");
//		sb.append("WHERE MEM.SERVICE_FLAG = 'A' ");
//		sb.append("AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
//		sb.append("AND DEFN.DEPT_ID IS NOT NULL ");
//		sb.append("AND JOB_RANK.ROLE_NAME IS NOT NULL ");

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> dataList = dam.exeQuery(queryCondition);
		
		String[] order = {"AO_CODE", "BRANCH_NBR", "AO_JOB_RANK"};
		String[] order2 = {"BRANCH_NBR","AO_CODE", "AO_JOB_RANK","TEL"};
		String[] order3 = {"AO_CODE", "BRANCH_NBR"};
		toODS("ODS_AOBRH", sdfYYYYMMDD.format(new Date()), (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), dataList, order, ",");
		toODS("AOBRH","TXT", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), dataList, order3, "");
		toODS("AOBRH_400","TXT", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), dataList, order3, "");
		toODS("P1TEL", "TXT", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), dataList, order2, "");
		ZODS_AOBRH(dataList);
		ZODS_AOBRH400(dataList);
	}
	
	public void ZODS_AOBRH400 (List<Map<String, Object>> dataList) throws JBranchException, IOException {
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("JOB_NAME", addBlankForString("ODS_AOBRH", 30));
		map.put("CNT", addZeroForNum(String.valueOf(dataList.size()), 15));
		map.put("DATA_DATE", sdfYYYYMMDD.format(new Date()));
		list.add(map);
		
		String[] order = {"DATA_DATE","CNT"};
		
		toODS("AOBRH_400_CHK", "TXT", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), list, order, "");
	}
	
	public void ZODS_AOBRH (List<Map<String, Object>> dataList) throws JBranchException, IOException {
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("JOB_NAME", addBlankForString("ODS_AOBRH", 30));
		map.put("CNT", addZeroForNum(String.valueOf(dataList.size()), 15));
		map.put("DATA_DATE", sdfYYYYMMDD.format(new Date()));
		list.add(map);
		
		String[] order = {"JOB_NAME","CNT","DATA_DATE"};
		
		toODS("ZODS_AOBRH", sdfYYYYMMDD.format(new Date()), (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), list, order, "");
	}
	
	public void updateFileBth (Object body, IPrimitiveMap<?> header) throws JBranchException, IOException {
		
		// 取得傳入參數
		Map<String, Object> inputMap = (Map<String, Object>) body;
		Map<String, Object> jobParam = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);

		// 主檔
		String ftpCode_main = (String) jobParam.get("ftpCode_main");
		String fileName_main = (String) jobParam.get("fileName_main");
		// 明細檔
		String ftpCode_detail = (String) jobParam.get("ftpCode_detail");
		String fileName_detail = (String) jobParam.get("fileName_detail");

		ftpUpload(ftpCode_detail);
		ftpUpload(ftpCode_main);
	}
	
	private void ftpUpload(String ftpCode) {
		try {
			ftpJobUtil.ftpPutFile(ftpCode);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}
	
	//匯出ods至指定路徑
	public void toODS (String file_name, String attached_name, String path, List<Map<String, Object>> list, String[] order, String separated) throws JBranchException, IOException {

		String fileName = file_name + "." + attached_name;
		

		File file1 = new File(path  + "reports\\" + fileName);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1,false),"MS950"));

		for(Map<String, Object> datas : list) {
			
			int j = 1;
			for (Integer i = 0; i < order.length; i++) {
				if("".equals(separated))
				{
					writer.append((String) (datas.get(order[i])));
				}
				else
				{
				writer.append(StringUtils.isBlank((String) datas.get(order[i])) ? "" : (String) datas.get(order[i]));
				}
				if (j++ == order.length){
				}else{
					writer.append(separated);
				}
			}

			writer.append("\r\n");
		}

		writer.flush();
		writer.close();
	}
	
	private String addZeroForNum(String str, int strLength) {
		
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append("0").append(str);// 左補0
	            // sb.append(str).append("0");//右補0
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }
	    return str;
	}
	
	private String addBlankForString(String str, int strLength) {
		
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append(str).append(" ");//右補空格
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }
	    return str;
	}
}
