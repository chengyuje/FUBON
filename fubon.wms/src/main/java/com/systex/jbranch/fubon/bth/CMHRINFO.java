/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 1. 讀取DB產出 .yyyymmdd 
 * 2. 將.yyyymmdd 上傳FTP
 *
 */
@Repository("cmhrinfo")
@Scope("prototype")
public class CMHRINFO extends BizLogic {

	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void createFileBth(Object body, IPrimitiveMap<?> header) throws Exception {
		
		GenFileTools gft = new GenFileTools();
		ResultSet rs = null;
		Connection con = gft.getConnection();
		Statement stat = null;
		
		try {
			stat = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
			stat.setFetchSize(3000);
			
			rs = gft.getRS(genSql(), stat);
			gft.writeFile("FP", "TXT", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), genOrder(), rs, ",", false, false);

			///
			
			stat = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
			stat.setFetchSize(3000);
			
			rs = gft.getRS(genSql2(), stat);
			gft.writeFile("VIPOL", "txt", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), genOrder(), rs, ",", false, false);
		} finally {
			rs.close();
			stat.close();
			con.close();
		}
	}

	//產檔所需要輸出的欄位
	public String[] genOrder() {
		
		String[] order = { "COL_1", "COL_2", "DEPT_ID", "COL_3", "EMP_ID", "ROLE_NAME" };
		
		return order;
	}

	//產檔需要的SQL
	public StringBuffer genSql() {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT * ");
		sb.append("FROM ( ");
		sb.append("  SELECT ROWNUM AS ROWN, A.* ");
		sb.append("  FROM ( ");
		sb.append("    SELECT TO_CHAR(SYSDATE,'YYYYMMDD') AS COL_1, ");
		sb.append("           '9' AS COL_2, ");
		sb.append("           DEPT_ID, ");
		sb.append("           '1' AS COL_3, ");
		sb.append("           EMP_ID, ");
		sb.append("           ROLE_NAME ");
		sb.append("    FROM ( ");
		sb.append("      SELECT DISTINCT TO_CHAR(SYSDATE, 'YYYYMMDD'), ");
		sb.append("             CASE WHEN UB.EMP_ID IS NOT NULL THEN UB.BRANCH_NBR ELSE M.DEPT_ID END AS DEPT_ID, ");
		sb.append("             M.EMP_ID, ");
		sb.append("             RTRIM(XMLAGG(XMLELEMENT(E, R.ROLE_NAME ||'/')).EXTRACT('//text()'), '/') ROLE_NAME ");
		sb.append("      FROM TBORG_MEMBER M ");
		sb.append("      LEFT JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID ");
		sb.append("      LEFT JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID ");
		sb.append("      LEFT JOIN TBORG_UHRM_BRH UB ON M.EMP_ID = UB.EMP_ID ");
		sb.append("      GROUP BY TO_CHAR(SYSDATE, 'YYYYMMDD'), CASE WHEN UB.EMP_ID IS NOT NULL THEN UB.BRANCH_NBR ELSE M.DEPT_ID END, M.EMP_ID ");
		sb.append("    ) ");
		sb.append("  ) A ");
		sb.append(") ");
		
		return sb;
	}

	public StringBuffer genSql2() {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT * ");
		sb.append("FROM ( ");
		sb.append("  SELECT ROWNUM AS ROWN, A.* ");
		sb.append("  FROM ( ");
		sb.append("    SELECT TO_CHAR(SYSDATE, 'yyyyMMdd') AS COL_1, ");
		sb.append("           '9' COL_2, ");
		sb.append("           CASE WHEN UB.EMP_ID IS NOT NULL THEN UB.BRANCH_NBR ELSE M.DEPT_ID END AS DEPT_ID, ");
		sb.append("           '1' COL_3, ");
		sb.append("           M.EMP_ID AS EMP_ID, ");
		sb.append("           LISTAGG(NVL(R.ROLE_NAME,''), '/') WITHIN GROUP (ORDER BY R.ROLE_NAME) AS ROLE_NAME ");
		sb.append("    FROM TBORG_MEMBER M ");
		sb.append("    LEFT JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID ");
		sb.append("    LEFT JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID ");
		sb.append("    LEFT JOIN TBORG_UHRM_BRH UB ON M.EMP_ID = UB.EMP_ID ");
		sb.append("    WHERE MR.ROLE_ID IS NOT NULL ");
		sb.append("    GROUP BY TO_CHAR(SYSDATE, 'yyyyMMdd'), '9', CASE WHEN UB.EMP_ID IS NOT NULL THEN UB.BRANCH_NBR ELSE M.DEPT_ID END, '1', M.EMP_ID ");
		sb.append("    ORDER BY CASE WHEN UB.EMP_ID IS NOT NULL THEN UB.BRANCH_NBR ELSE M.DEPT_ID END, LISTAGG(NVL(R.ROLE_NAME,''), '/') WITHIN GROUP (ORDER BY R.ROLE_NAME) ");
		sb.append("  ) A ");
		sb.append(") ");
		
		return sb;
	}
}
