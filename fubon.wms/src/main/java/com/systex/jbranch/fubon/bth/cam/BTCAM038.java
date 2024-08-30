package com.systex.jbranch.fubon.bth.cam;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.fubon.bth.GenFileTools;
import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * 	BTCAM038
 *	WMS-CR-20200706-02_新增保單地址、電話、EMail、手機號碼檢核控管功能
 *	
 **/

@Repository("btcam038")
@Scope("prototype")
public class BTCAM038 extends BizLogic {
	private DataAccessManager dam = null;
	private BthFtpJobUtil bthUtil = null;
	private AuditLogUtil audit = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private GenFileTools gft=new GenFileTools();
	
	public void BTCAM038(Object body, IPrimitiveMap<?> header) throws Exception {
		// 紀錄排程監控log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		// 取得傳入參數
		Map<String, Object> inputMap = (Map<String, Object>) body;
		Map<String, Object> jobParam = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
		String ftpCode = (String) jobParam.get("ftpCode");
		String fileName = (String) jobParam.get("fileName");
		String jobName="BTCAM038";
		int dataCount=0;
		ftpCode = (ftpCode == null ? "BTCAM038" : ftpCode);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		fileName = "IOT_ADDRTELMAIL_" + sdf.format(new Date());
		//String fileDate=gft.getFtpFileDate(ftpCode, true, "DES");
		
		StringBuffer sb = new StringBuffer();

		sb.append(getQuerySQL());

		ResultSet rs = null;
		Connection con = gft.getConnection();
		Statement s = null;
		try {
			s = con.createStatement(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
			s.setFetchSize(3000);
			rs = gft.getRS(sb,s);
			gft.writeFile(fileName, "csv",(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), getHeader(), rs, ",", false, true);
			rs.beforeFirst();
			while(rs.next())
			{
				dataCount++;
			}
			gft.writeZFile("Z"+fileName, "", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), dataCount, "", jobName,-1);
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		}
	}
	
	private String[] getHeader()
	{
		String[] txtHeader = {
				 "客戶ID",
				 "分行代碼",
				 "AO_CODE或員編",
				 "變動欄位名稱1",
				 "變動欄位值1",
				 "變動欄位名稱2",
				 "變動欄位值2",
				 "變動欄位名稱3",
				 "變動欄位值3",
				 "變動欄位名稱4",
				 "變動欄位值4",
				 "變動欄位名稱5",
				 "變動欄位值5",
				 "變動欄位名稱6",
				 "變動欄位值6",
				 "變動欄位名稱7",
				 "變動欄位值7",
				 "變動欄位名稱8",
				 "變動欄位值8",
				 "變動欄位名稱9",
				 "變動欄位值9",
				 "變動欄位名稱10",
				 "變動欄位值10",
				 "變動欄位名稱11",
				 "變動欄位值11",
				 "變動欄位名稱12",
				 "變動欄位值12",
				 "變動欄位名稱13",
				 "變動欄位值13",
				 "變動欄位名稱14",
				 "變動欄位值14",
				 "變動欄位名稱15",
				 "變動欄位值15",
				 "變動欄位名稱16",
				 "變動欄位值16",
				 "變動欄位名稱17",
				 "變動欄位值17",
				 "變動欄位名稱18",
				 "變動欄位值18",
				 "變動欄位名稱19",
				 "變動欄位值19",
				 "變動欄位名稱20",
				 "變動欄位值20" 
		};
		return txtHeader;
	}
	
	private StringBuffer getQuerySQL() {
		StringBuffer sb=new StringBuffer();
		
		sb.append(" SELECT ");
		sb.append(" '客戶ID'				AS 客戶ID, ");
		sb.append(" '分行代碼'           AS 分行代碼, ");
		sb.append(" 'AO_CODE或員編'      AS AO_CODE或員編, ");
		sb.append(" '變動欄位名稱1'      AS 變動欄位名稱1, ");
		sb.append(" '變動欄位值1'        AS 變動欄位值1, ");
		sb.append(" '變動欄位名稱2'      AS 變動欄位名稱2, ");
		sb.append(" '變動欄位值2'        AS 變動欄位值2, ");
		sb.append(" '變動欄位名稱3'      AS 變動欄位名稱3, ");
		sb.append(" '變動欄位值3'        AS 變動欄位值3, ");
		sb.append(" '變動欄位名稱4'      AS 變動欄位名稱4, ");
		sb.append(" '變動欄位值4'        AS 變動欄位值4, ");
		sb.append(" '變動欄位名稱5'      AS 變動欄位名稱5, ");
		sb.append(" '變動欄位值5'        AS 變動欄位值5, ");
		sb.append(" '變動欄位名稱6'      AS 變動欄位名稱6, ");
		sb.append(" '變動欄位值6'        AS 變動欄位值6, ");
		sb.append(" '變動欄位名稱7'      AS 變動欄位名稱7, ");
		sb.append(" '變動欄位值7'        AS 變動欄位值7, ");
		sb.append(" '變動欄位名稱8'      AS 變動欄位名稱8, ");
		sb.append(" '變動欄位值8'        AS 變動欄位值8, ");
		sb.append(" '變動欄位名稱9'      AS 變動欄位名稱9, ");
		sb.append(" '變動欄位值9'        AS 變動欄位值9, ");
		sb.append(" '變動欄位名稱10'     AS 變動欄位名稱10, ");
		sb.append(" '變動欄位值10'       AS 變動欄位值10, ");
		sb.append(" '變動欄位名稱11'     AS 變動欄位名稱11, ");
		sb.append(" '變動欄位值11'       AS 變動欄位值11, ");
		sb.append(" '變動欄位名稱12'     AS 變動欄位名稱12, ");
		sb.append(" '變動欄位值12'       AS 變動欄位值12, ");
		sb.append(" '變動欄位名稱13'     AS 變動欄位名稱13, ");
		sb.append(" '變動欄位值13'       AS 變動欄位值13, ");
		sb.append(" '變動欄位名稱14'     AS 變動欄位名稱14, ");
		sb.append(" '變動欄位值14'       AS 變動欄位值14, ");
		sb.append(" '變動欄位名稱15'     AS 變動欄位名稱15, ");
		sb.append(" '變動欄位值15'       AS 變動欄位值15, ");
		sb.append(" '變動欄位名稱16'     AS 變動欄位名稱16, ");
		sb.append(" '變動欄位值16'       AS 變動欄位值16, ");
		sb.append(" '變動欄位名稱17'     AS 變動欄位名稱17, ");
		sb.append(" '變動欄位值17'       AS 變動欄位值17, ");
		sb.append(" '變動欄位名稱18'     AS 變動欄位名稱18, ");
		sb.append(" '變動欄位值18'       AS 變動欄位值18, ");
		sb.append(" '變動欄位名稱19'     AS 變動欄位名稱19, ");
		sb.append(" '變動欄位值19'       AS 變動欄位值19, ");
		sb.append(" '變動欄位名稱20'     AS 變動欄位名稱20, ");
		sb.append(" '變動欄位值20'       AS 變動欄位值20, ");
		sb.append(" 0 AS ORDER_NO ");
		sb.append(" FROM DUAL ");
		sb.append(" UNION ");
		sb.append(" SELECT ");
		sb.append(" CUST_ID           AS 客戶ID, ");
		sb.append(" BRANCH_ID         AS 分行代碼, ");
		sb.append(" EMP_ID            AS AO_CODE或員編, ");
		sb.append(" VAR_FIELD_LABEL1  AS 變動欄位名稱1, ");
		sb.append(" VAR_FIELD_VALUE1  AS 變動欄位值1, ");
		sb.append(" VAR_FIELD_LABEL2  AS 變動欄位名稱2, ");
		sb.append(" VAR_FIELD_VALUE2  AS 變動欄位值2, ");
		sb.append(" VAR_FIELD_LABEL3  AS 變動欄位名稱3, ");
		sb.append(" VAR_FIELD_VALUE3  AS 變動欄位值3, ");
		sb.append(" VAR_FIELD_LABEL4  AS 變動欄位名稱4, ");
		sb.append(" VAR_FIELD_VALUE4  AS 變動欄位值4, ");
		sb.append(" VAR_FIELD_LABEL5  AS 變動欄位名稱5, ");
		sb.append(" VAR_FIELD_VALUE5  AS 變動欄位值5, ");
		sb.append(" VAR_FIELD_LABEL6  AS 變動欄位名稱6, ");
		sb.append(" VAR_FIELD_VALUE6  AS 變動欄位值6, ");
		sb.append(" VAR_FIELD_LABEL7  AS 變動欄位名稱7, ");
		sb.append(" VAR_FIELD_VALUE7  AS 變動欄位值7, ");
		sb.append(" VAR_FIELD_LABEL8  AS 變動欄位名稱8, ");
		sb.append(" VAR_FIELD_VALUE8  AS 變動欄位值8, ");
		sb.append(" VAR_FIELD_LABEL9  AS 變動欄位名稱9, ");
		sb.append(" VAR_FIELD_VALUE9  AS 變動欄位值9, ");
		sb.append(" VAR_FIELD_LABEL10 AS 變動欄位名稱10, ");
		sb.append(" VAR_FIELD_VALUE10 AS 變動欄位值10, ");
		sb.append(" VAR_FIELD_LABEL11 AS 變動欄位名稱11, ");
		sb.append(" VAR_FIELD_VALUE11 AS 變動欄位值11, ");
		sb.append(" VAR_FIELD_LABEL12 AS 變動欄位名稱12, ");
		sb.append(" VAR_FIELD_VALUE12 AS 變動欄位值12, ");
		sb.append(" VAR_FIELD_LABEL13 AS 變動欄位名稱13, ");
		sb.append(" VAR_FIELD_VALUE13 AS 變動欄位值13, ");
		sb.append(" VAR_FIELD_LABEL14 AS 變動欄位名稱14, ");
		sb.append(" VAR_FIELD_VALUE14 AS 變動欄位值14, ");
		sb.append(" VAR_FIELD_LABEL15 AS 變動欄位名稱15, ");
		sb.append(" VAR_FIELD_VALUE15 AS 變動欄位值15, ");
		sb.append(" VAR_FIELD_LABEL16 AS 變動欄位名稱16, ");
		sb.append(" VAR_FIELD_VALUE16 AS 變動欄位值16, ");
		sb.append(" VAR_FIELD_LABEL17 AS 變動欄位名稱17, ");
		sb.append(" VAR_FIELD_VALUE17 AS 變動欄位值17, ");
		sb.append(" VAR_FIELD_LABEL18 AS 變動欄位名稱18, ");
		sb.append(" VAR_FIELD_VALUE18 AS 變動欄位值18, ");
		sb.append(" VAR_FIELD_LABEL19 AS 變動欄位名稱19, ");
		sb.append(" VAR_FIELD_VALUE19 AS 變動欄位值19, ");
		sb.append(" VAR_FIELD_LABEL20 AS 變動欄位名稱20, ");
		sb.append(" VAR_FIELD_VALUE20 AS 變動欄位值20, ");
		sb.append(" 1 AS ORDER_NO ");
		sb.append(" FROM TBIOT_ADDRTELMAIL ");
		sb.append(" WHERE YYYYMM = TO_CHAR(SYSDATE, 'YYYYMM') ");
		sb.append(" ORDER BY ORDER_NO ");
		
		return sb;
	}

}
