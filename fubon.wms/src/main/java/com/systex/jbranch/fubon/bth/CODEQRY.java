/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
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
@Repository("codeqry")
@Scope("prototype")
public class CODEQRY extends BizLogic {
	 
	private DataAccessManager dam = null;	
	SimpleDateFormat sdfYYYYMMDD  = new SimpleDateFormat("yyyyMMdd");
	
	public void createFileBth (Object body, IPrimitiveMap<?> header) throws Exception {
		
		GenFileTools gft = new GenFileTools();
		dam = this.getDataAccessManager(); 
		QueryConditionIF queryCondition = dam.getQueryCondition();

		int dataCount;

		//定義檔案名稱及輸出的jobname
		String writeFileName = "CODE_QRY";
		String attached_name = sdfYYYYMMDD.format(new Date(new Date().getTime()+((long)(-1) * 24 * 60 * 60 * 1000)));

		String jobName = "CODE_QRY";
		String path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		StringBuffer sb = new StringBuffer();
		
		//先取得總筆數
		sb.append("SELECT COUNT(1) TOTAL_COUNT ");
		sb.append("FROM ( ");
		sb.append(genSql().toString());
		sb.append(")");

		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> listCount = dam.exeQuery(queryCondition);	
		dataCount = Integer.parseInt(listCount.get(0).get("TOTAL_COUNT").toString());
		
		String[] order = {"DATA_DATE", "NAME", "PERSON_ID", "IF_REAL_EMPID", "BRANCH_NBR", "AO_CODE", "IF_QUIT", "SA_JOB_TITLE_ID", "SA_JOB_TITLE_NM", "MARK_4_DEL", "FLAG"};


		ResultSet rs = null;
		Connection con = gft.getConnection();
		Statement s = null;
		try {
			s = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
			s.setFetchSize(3000);
			rs = gft.getRS(genSql(), s);
			gft.writeFile(writeFileName, attached_name, path, order, rs, ", ", false, true);
			gft.writeZFile("Z" + writeFileName, attached_name, path, dataCount, "", jobName, -1);
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		} 


	}

	//產檔需要的SQL
    public StringBuffer genSql() {
    	
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * ");
		sb.append("FROM ( ");
		sb.append("  SELECT ROWNUM ROWN, A.*  ");
		sb.append("  FROM ( ");
		sb.append("    SELECT TO_CHAR(SYSDATE - 1,'YYYYMMDD') AS DATA_DATE, ");
		sb.append("           INFO.EMP_NAME AS NAME, ");
		sb.append("           INFO.EMP_ID AS PERSON_ID, ");
		sb.append("           'Y' AS IF_REAL_EMPID, ");
		sb.append("           CASE WHEN INFO.BRANCH_NBR IS NULL THEN ' ' ELSE INFO.BRANCH_NBR END AS BRANCH_NBR, ");
		sb.append("           CASE WHEN INFO.AO_CODE IS NULL AND INFO.UHRM_CODE IS NOT NULL THEN INFO.UHRM_CODE ");
		sb.append("                WHEN INFO.AO_CODE IS NOT NULL AND INFO.UHRM_CODE IS NULL THEN INFO.AO_CODE ");
		sb.append("           ELSE NULL END AS AO_CODE, ");
		sb.append("           CASE WHEN MEM.JOB_RESIGN_DATE IS NOT NULL THEN 'Y' ELSE ' ' END AS IF_QUIT, ");
		sb.append("           INFO.ROLE_ID AS SA_JOB_TITLE_ID, ");
		sb.append("           INFO.JOB_TITLE_NAME AS SA_JOB_TITLE_NM, ");
		sb.append("           INFO.CODE_TYPE AS MARK_4_DEL, ");
		sb.append("           INFO.ROLE_TYPE AS IS_PRIMARY_ROLE, ");
		sb.append("           CASE WHEN MEM.DEPT_ID >= 200 AND MEM.DEPT_ID <= 900 AND TO_NUMBER(MEM.DEPT_ID) <> 806 AND TO_NUMBER(MEM.DEPT_ID) <> 810 THEN 'B' ");
		sb.append("                WHEN MEM.DEPT_ID = '031' THEN 'UH' ");
		sb.append("                WHEN MEM.DEPT_ID = '175' THEN 'BS' ");
		sb.append("           ELSE NULL END AS FLAG ");
		sb.append("    FROM VWORG_EMP_INFO INFO ");
		sb.append("    LEFT JOIN TBORG_MEMBER MEM ON INFO.EMP_ID = MEM.EMP_ID ");
		sb.append("    WHERE (INFO.AO_CODE IS NOT NULL OR INFO.UHRM_CODE IS NOT NULL) ");
		sb.append("  ) A ");
		sb.append(") ");
		
		return sb;
	}	
	
	

}


