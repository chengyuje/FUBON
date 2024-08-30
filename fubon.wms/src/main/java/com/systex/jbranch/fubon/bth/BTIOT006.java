package com.systex.jbranch.fubon.bth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
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
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 1.初版
 
 * @author 1900150
 * @date 2019/06/21
 
 **/
@Repository("btiot006")
@Scope("prototype")
public class BTIOT006 extends BizLogic {
	private DataAccessManager dam = null;
	private BthFtpJobUtil bthUtil = new BthFtpJobUtil();
	private AuditLogUtil audit = null;

	String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
	public void BTIOT_006(Object body, IPrimitiveMap<?> header) throws Exception {
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		//抓取SQL語法
		StringBuffer sb = new StringBuffer();				 
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		
		
		sb.append(" WITH BASE AS (SELECT TO_CHAR(M.KEYIN_DATE, 'YYYYMMDD') AS KEYIN_DATE,TO_CHAR(M.APPLY_DATE, 'YYYYMMDD') AS APPLY_DATE,  ");
		sb.append(" 			   INFO.OP_BATCH_NO, M.INS_ID, INFO.CREATOR AS EMP_ID,   ");
		sb.append(" 			   CONCAT(CONCAT(SUBSTR(INFO.OP_BATCH_NO, 8, 3), ' '),D.DEPT_NAME) AS BRANCH_NAME ,   ");
		sb.append(" 			   MEM.EMP_EMAIL_ADDRESS, 'INS' AS INS_PROD_EMAIL  ");
		sb.append(" 		FROM TBIOT_MAIN M  ");
		sb.append(" 		LEFT JOIN TBIOT_BATCH_INFO INFO ON M.BATCH_INFO_KEYNO = INFO.BATCH_INFO_KEYNO  ");
		sb.append(" 		LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = INFO.CREATOR   ");
		sb.append(" 		LEFT JOIN TBORG_DEFN D ON SUBSTR(INFO.OP_BATCH_NO, 8, 3) = D.DEPT_ID  ");
		sb.append(" 		WHERE M.STATUS = '30'  ");
		sb.append(" 		  AND INFO.OP_BATCH_DATE < (SELECT PABTH_UTIL.FC_getBusiDay( TRUNC(SYSDATE) , 'TWD', -2) FROM DUAL) ");
		sb.append(" 		UNION ALL  ");
		sb.append(" 		SELECT TO_CHAR(TEMP.KEYIN_DATE, 'YYYYMMDD') AS KEYIN_DATE, TO_CHAR(TEMP.APPLY_DATE, 'YYYYMMDD') AS APPLY_DATE,  ");
		sb.append(" 		 TEMP.OP_BATCH_NO, TEMP.INS_ID,   ");
		sb.append(" 		 EINFO.EMP_ID, TEMP.BRANCH_NAME,   ");
		sb.append(" 		 MEM.EMP_EMAIL_ADDRESS ,NULL AS INS_PROD_EMAIL  ");
		sb.append(" 		FROM  ");
		sb.append(" 			( SELECT M.KEYIN_DATE,M.APPLY_DATE, INFO.OP_BATCH_NO,M.INS_ID, SUBSTR(INFO.OP_BATCH_NO, 8, 3) AS DEPT_ID,  ");
		sb.append(" 				 CONCAT(CONCAT(SUBSTR(INFO.OP_BATCH_NO, 8, 3),' ') ,D.DEPT_NAME) AS BRANCH_NAME  ");
		sb.append(" 			FROM TBIOT_MAIN M  ");
		sb.append(" 			LEFT JOIN TBIOT_BATCH_INFO INFO ON M.BATCH_INFO_KEYNO = INFO.BATCH_INFO_KEYNO  ");
		sb.append(" 			LEFT JOIN TBORG_DEFN D ON SUBSTR(INFO.OP_BATCH_NO, 8, 3) = D.DEPT_ID  ");
		sb.append(" 			WHERE M.STATUS = '30'  ");
		sb.append(" 			  AND INFO.OP_BATCH_DATE <(SELECT PABTH_UTIL.FC_getBusiDay( TRUNC(SYSDATE) , 'TWD', -2) FROM DUAL)  )TEMP  ");
		sb.append(" 		LEFT JOIN VWORG_EMP_INFO EINFO ON TEMP.DEPT_ID IN EINFO.BRANCH_NBR   ");
		sb.append(" 			  AND ROLE_ID IN (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'IOT.EMP_EMAIL')  ");
		sb.append(" 		LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = EINFO.EMP_ID   ");
		sb.append(" 		WHERE MEM.EMP_ID IS NOT NULL  ");
		sb.append(" 		ORDER BY OP_BATCH_NO,EMP_ID ) ");
		sb.append(" SELECT OP_BATCH_NO, INS_PROD_EMAIL, ");
		sb.append(" 	   EMP_ID,EMP_EMAIL_ADDRESS, ");
		sb.append(" 	   listagg(KEYIN_DATE,',') within group(order by KEYIN_DATE) as KEYIN_DATE ,  ");
		sb.append(" 	   listagg(APPLY_DATE,',') within group(order by APPLY_DATE) as APPLY_DATE , ");
		sb.append(" 	   BRANCH_NAME, ");
		sb.append(" 	   listagg(INS_ID,',') within group(order by INS_ID) as INS_ID  ");
		sb.append(" FROM BASE GROUP BY OP_BATCH_NO, BRANCH_NAME, EMP_EMAIL_ADDRESS, INS_PROD_EMAIL, EMP_ID ");
			
		qc.setQueryString(sb.toString());
		List<Map<String, String>> dataList = dam.exeQuery(qc);
		List<Map<String, String>> mailList = new ArrayList<Map<String,String>>();
		Map<String, String> mailMap = new HashMap<String, String>();
		

		
		if(dataList.size() > 0){
			for(Map<String, String> data :  dataList){
				
				mailList = new ArrayList<Map<String,String>>();
			    mailMap = new HashMap<String, String>();
				
			    String[] KDateList = data.get("KEYIN_DATE").split(",");
				String[] ADateList = data.get("APPLY_DATE").split(",");
				String[] InsList = data.get("INS_ID").split(",");
				
				if(data.get("EMP_EMAIL_ADDRESS") != null){
					mailMap.put(FubonSendJavaMail.MAIL, data.get("EMP_EMAIL_ADDRESS"));
					mailList.add(mailMap);
					
					// send
					if(mailList.size() > 0) {
						FubonSendJavaMail sendMail = new FubonSendJavaMail();
						FubonMail mail = new FubonMail();
						Map<String, Object> annexData = new HashMap<String, Object>();
						mail.setLstMailTo(mailList);
						
						//設定信件主旨					
						mail.setSubject("保險打包送件逾期未點收提醒");
						
						//設定信件內容
						String content = "提醒!保險打包送件後於已逾三工作天尚未點收，請確認人件是否寄送，若已寄出，請與保險商品處確認是否已送達。";
						content += "<br>";
						content += "<table border=\"solid\" style=\"text-align:center; border-collapse:collapse;\" >";
						content += "<tr style=\"font-weight:bold;\">";
						content += "<td>鍵機日</td>";
						content += "<td>要保填寫日</td>";
						content += "<td>送件批號</td>";
						content += "<td>文件編號</td>";
						content += "<td>分行名稱</td>";
						content += "</tr>";
						
						for(int i = 0 ; i < KDateList.length; i++){
							content += "<tr>";
							content += "<td>" + KDateList[i] + "</td>";
							content += "<td>" + ADateList[i] + "</td>";
							content += "<td>" + data.get("OP_BATCH_NO") + "</td>";
							content += "<td>" + InsList[i] + "</td>";
							content += "<td>" + data.get("BRANCH_NAME") + "</td>";
							content += "</tr>";
						}
						
						content += "</table>";
						mail.setContent(content);
						
						//寄出信件
						sendMail.sendMail(mail,annexData);
					}
				}
				
			    
				if(data.get("INS_PROD_EMAIL") != null){
					mailList = new ArrayList<Map<String,String>>();
				    mailMap = new HashMap<String, String>();
					// 保險商品處公務信箱
				    XmlInfo xmlInfo = new XmlInfo();
					Map<String, String> insProdMap = xmlInfo.doGetVariable("IOT.INS_PROD_EMAIL", FormatHelper.FORMAT_3);

					mailMap.put(FubonSendJavaMail.MAIL, insProdMap.get("1"));
					mailList.add(mailMap);
					
					// send
					if(mailList.size() > 0) {
						FubonSendJavaMail sendMail = new FubonSendJavaMail();
						FubonMail mail = new FubonMail();
						Map<String, Object> annexData = new HashMap<String, Object>();
						mail.setLstMailTo(mailList);
						
						//設定信件主旨					
						mail.setSubject("保險打包送件逾期未點收提醒");
						
						//設定信件內容
						String content = "提醒!保險打包送件後於已逾三工作天尚未點收，請確認人件是否寄送，若已寄出，請與保險商品處確認是否已送達。";
						content += "<br>";
						content += "<table border=\"solid\" style=\"text-align:center; border-collapse:collapse;\" >";
						content += "<tr style=\"font-weight:bold;\">";
						content += "<td>鍵機日</td>";
						content += "<td>要保填寫日</td>";
						content += "<td>送件批號</td>";
						content += "<td>文件編號</td>";
						content += "<td>分行名稱</td>";
						content += "</tr>";
						
						for(int i = 0 ; i < KDateList.length; i++){
							content += "<tr>";
							content += "<td>" + KDateList[i] + "</td>";
							content += "<td>" + ADateList[i] + "</td>";
							content += "<td>" + data.get("OP_BATCH_NO") + "</td>";
							content += "<td>" + InsList[i] + "</td>";
							content += "<td>" + data.get("BRANCH_NAME") + "</td>";
							content += "</tr>";
						}
						
						content += "</table>";
						mail.setContent(content);
						
						//寄出信件
						sendMail.sendMail(mail,annexData);
					}
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
