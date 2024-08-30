package com.systex.jbranch.fubon.bth.crm;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
@Component("btcrm990")
@Scope("prototype")
public class BTCRM990 extends BizLogic {
	private DataAccessManager dam = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	//target產檔
	public void genFileFromTarget(Object body, IPrimitiveMap<?> header) throws Exception {
		//撈取要寫入檔案的資料
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT COM.COMPLAIN_LIST_ID, ");
		sql.append("(CASE COM.GRADE WHEN '1' THEN 'G1' WHEN '2' THEN 'G2' WHEN '3' THEN 'G3' END) AS GRADE, ");
		sql.append("(CASE COM.CASE_TYPE WHEN 'OTH' THEN 'OT' ELSE CASE_TYPE END) AS CASE_TYPE, ");
		sql.append("CRE.DEPT_ID, DEFN1.DEPT_NAME, COM.BRANCH_NBR, DEFN2.DEPT_NAME AS BRANCH_NAME, ");
		sql.append("(CASE WHEN COM.HANDLE_STEP = 'D' THEN 'D' ELSE ");
		sql.append("(CASE WHEN COM.REAL_END_DATE IS NOT NULL THEN 'C' ELSE ");
		sql.append("(CASE WHEN F.COUNT = 1 THEN 'A' ELSE 'B' END) END) END) AS HANDLE_STATUS, ");
		sql.append("TO_CHAR(COM.HAPPEN_DATE, 'YYYYMMDD') AS HAPPEN_DATE, ");
		sql.append("TO_CHAR(COM.END_DATE, 'YYYYMMDD') AS END_DATE, ");
		sql.append("(CASE WHEN COMPLAIN_SOURCE < 10 THEN ('0' || COMPLAIN_SOURCE) ");
		sql.append("WHEN COMPLAIN_SOURCE BETWEEN '10' AND '12' THEN '10' ELSE COMPLAIN_SOURCE END) AS COMPLAIN_SOURCE, ");
		sql.append("(CASE WHEN COM.COMPLAIN_TYPE BETWEEN 1 AND 8 THEN '08' ELSE ");
		sql.append("(CASE COM.COMPLAIN_TYPE WHEN '9' THEN '06' WHEN '10' THEN '13' ");
		sql.append("WHEN '11' THEN NULL WHEN '12' THEN '15' WHEN '13' THEN '05' WHEN '14' THEN '12' ");
		sql.append("WHEN '15' THEN '04' WHEN '16' THEN '16' WHEN '17' THEN '14' WHEN '18' THEN '16' ");
		sql.append("WHEN '23' THEN '17' ELSE ");
		sql.append("(CASE WHEN COM.COMPLAIN_TYPE BETWEEN 19 AND 22 THEN NULL END) END) END) AS COMPLAIN_TYPE, ");
		sql.append("COM.CUST_NAME, COM.CUST_ID, COM.PHONE, TO_CHAR(COM.BIRTHDATE, 'YYYYMMDD') AS BIRTHDATE, ");
		sql.append("(CASE COM.OCCUP WHEN '1' THEN '07' WHEN '2' THEN '09' WHEN '3' THEN '10' ");
		sql.append("WHEN '4' THEN '03' WHEN '5' THEN '04' WHEN '6' THEN '05' WHEN '7' THEN '06' ");
		sql.append("WHEN '8' THEN '01' WHEN '9' THEN '02' WHEN '10' THEN '08' WHEN '11' THEN '12' ");
		sql.append("WHEN '12' THEN '11' WHEN '13' THEN '14' WHEN '14' THEN '13' WHEN '15' THEN '16' ");
		sql.append("WHEN '16' THEN '15' WHEN '17' THEN '18' WHEN '18' THEN '17' WHEN '19' THEN '19' ");
		sql.append("ELSE NULL END) AS OCCUP, ");
		sql.append("(CASE COM.EDUCATION WHEN '1' THEN '06' WHEN '2' THEN '05' WHEN '3' THEN '04' ");
		sql.append("WHEN '4' THEN '03' WHEN '5' THEN '02' WHEN '6' THEN '01' WHEN '7' THEN '07' ");
		sql.append("END) AS EDUCATION, COM.TOTAL_ASSET, COM.EMP_ID, COM.EMP_NAME, ");
		sql.append("(CASE COM.SERVICE_YN WHEN '01' THEN 'Y' WHEN '02' THEN 'N' END) AS SERVICE_YN, ");
		sql.append("(COM.COMPLAIN_PRODUCT || '/' || COM.COMPLAIN_PRODUCT_CURRENCY || '/' || ");
		sql.append("COM.COMPLAIN_PRODUCT_AMOUN) AS COMPLAIN_PRODUCT_INFO, ");
		sql.append("TO_CHAR(COM.BUY_DATE, 'YYYYMMDD') AS BUY_DATE, ");
		sql.append("(COM.PROBLEM_DESCRIBE || COM.CUST_DESCRIBE) AS CUST_DESCRIBE, ");
		sql.append("COM.HANDLE_CONDITION1, COM.HANDLE_CONDITION2, ");
		sql.append("(COM.HANDLE_CONDITION3 || COM.HANDLE_CONDITION4) AS HANDLE_CONDITION3, ");
		sql.append("TO_CHAR(COM.REAL_END_DATE, 'YYYYMMDD') AS REAL_END_DATE, COM.EDITOR_CONDITION4, ");
		sql.append("MEM.EMP_NAME AS EDITOR4_EMP_NAME, MEM.EMP_CELL_NUM, ");
		sql.append("(CASE WHEN COM.REAL_END_DATE IS NOT NULL THEN 'Y' ELSE 'N' END) AS END_YN, ");
		sql.append("TO_CHAR(COM.CREATETIME, 'YYYYMMDD') AS CREATE_DATE, ");
		sql.append("TO_CHAR(COM.CREATETIME, 'HHMMSS') AS CREATE_TIME, ");
		sql.append("COM.CREATOR, MEM2.EMP_NAME AS CREATOR_NAME, ");
		sql.append("TO_CHAR(COM.LASTUPDATE, 'YYYYMMDD') AS LASTUPDATE_DATE, ");
		sql.append("TO_CHAR(COM.LASTUPDATE, 'HHMMSS') AS LASTUPDATE_TIME, ");
		sql.append("COM.MODIFIER, MEM3.EMP_NAME AS MODIFIER_NAME ");
		sql.append("FROM TBCRM_CUST_COMPLAIN COM ");
		sql.append("LEFT JOIN TBORG_MEMBER CRE ON COM.CREATOR = CRE.EMP_ID ");
		sql.append("LEFT JOIN TBORG_DEFN DEFN1 ON CRE.DEPT_ID = DEFN1.DEPT_ID ");
		sql.append("LEFT JOIN TBORG_DEFN DEFN2 ON COM.BRANCH_NBR = DEFN2.DEPT_ID ");
		sql.append("LEFT JOIN (SELECT COMPLAIN_LIST_ID, COUNT(*) AS COUNT ");
		sql.append("FROM TBCRM_CUST_COMPLAIN_FLOW GROUP BY COMPLAIN_LIST_ID) F ");
		sql.append("ON COM.COMPLAIN_LIST_ID = F.COMPLAIN_LIST_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER MEM ON COM.EDITOR_CONDITION4 = MEM.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER MEM2 ON COM.CREATOR = MEM2.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER MEM3 ON COM.MODIFIER = MEM3.EMP_ID ");
		sql.append("WHERE TO_CHAR(COM.LASTUPDATE, 'YYYYMMDD') ");
		sql.append("BETWEEN TO_CHAR(add_months(SYSDATE, -12), 'YYYYMMDD') ");
		sql.append("AND TO_CHAR(SYSDATE, 'YYYYMMDD') ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list  = dam.exeQuery(queryCondition);
		
		List<String> fwList = new ArrayList<String>();
		String[] txtHeader = { "COMPLAIN_LIST_ID" ,"GRADE" ,"CASE_TYPE", "DEPT_ID", 
						       "DEPT_NAME", "BRANCH_NBR", "BRANCH_NAME", "HANDLE_STATUS", 
							   "HAPPEN_DATE", "END_DATE", "COMPLAIN_SOURCE", "COMPLAIN_TYPE", 
							   "CUST_NAME", "CUST_ID", "PHONE", "BIRTHDATE", 
							   "OCCUP", "EDUCATION", "TOTAL_ASSET", "EMP_ID", 
							   "EMP_NAME", "SERVICE_YN", "COMPLAIN_PRODUCT_INFO", "BUY_DATE",
							   "CUST_DESCRIBE", "HANDLE_CONDITION1", "HANDLE_CONDITION2", "HANDLE_CONDITION3", 
							   "REAL_END_DATE", "EDITOR_CONDITION4", "EDITOR4_EMP_NAME", "EMP_CELL_NUM",
							   "END_YN", "CREATE_DATE", "CREATE_TIME", "CREATOR", 
							   "CREATOR_NAME", "LASTUPDATE_DATE", "LASTUPDATE_TIME", "MODIFIER", "MODIFIER_NAME"};
		
		for (Map<String, Object> map : list) {
			StringBuffer temp = new StringBuffer();
			int i = 1;
			for(String key : txtHeader){
				temp.append(ObjectUtils.toString(map.get(key)));
				if(i != txtHeader.length){
					temp.append("^|^");
					i++;
				}
			}
			fwList.add(temp.toString());
		}
		StringBuffer endMenu = new StringBuffer();
		int i = list.size();
		String sysDate = sdf.format(new Date());
		String list_size = String.valueOf(i);
		
		endMenu.append("END");
		endMenu.append(sysDate);
		endMenu.append(list_size);
		
		fwList.add(endMenu.toString());
		
		//產生檔案
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
		String filename = "CUST_COMPLAIN_LIST_" + sdf.format(new Date());
		FileWriter fw = new FileWriter(tempPath + filename + ".TXT");
		
		if (fwList.size() > 0) {
			for(String str : fwList) {
				fw.write(str+"\r\n");
			}
		}
		fw.close();	
	}
}