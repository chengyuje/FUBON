/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
 *
 */
@Repository("fwlfn120")
@Scope("prototype")
public class FWLFN120 extends BizLogic {

	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void createFileBth(Object body, IPrimitiveMap<?> header) throws Exception {

		GenFileTools gft = new GenFileTools();
		StringBuffer sb = new StringBuffer();
		
		sb = new StringBuffer();
		sb.append("SELECT POLICY_SEQ, ");
		sb.append("       ID_DUP, ");
		sb.append("       ID_NO, ");
		sb.append("       APPL_ID, ");
		sb.append("       UNIT_CODE, ");
		sb.append("       SERVANT_ID, ");
		sb.append("       BRH_MGR_ID, ");
		sb.append("       INS_SALE_ID, ");
		sb.append("       CUST_ID, ");
		sb.append("       AREA_MGR_ID, ");
		sb.append("       BRH_SMGR1_ID, ");
		sb.append("       BRH_SMGR2_ID, ");
		sb.append("       BRH_SMGR3_ID, ");
		sb.append("       AO_CODE, ");
		sb.append("       CASE WHEN LENGTH(BRANCH_COD) = 3 THEN BRANCH_COD ");
		sb.append("            WHEN LENGTH(BRANCH_COD) = 5 THEN SUBSTR(BRANCH_COD, 3, 3) ");
		sb.append("       ELSE SUBSTR(BRANCH_COD, 1, 3) ");
		sb.append("       END BRANCH_COD, ");
		sb.append("       PERSON_DEGREE, ");
		sb.append("       SERVICE_DEGREE, ");
		sb.append("       UHRM_ID, ");
		sb.append("       UHRM_MNG_ID, ");
		sb.append("       UHRM_SMGR1_ID, ");
		sb.append("       UHRM_SMGR2_ID, ");
		sb.append("       UHRM_SMGR3_ID ");
		sb.append("FROM ( ");
		sb.append("  SELECT ROWNUM AS ROWN, ");
		sb.append("         POLICY_SEQ, ");
		sb.append("         ID_DUP, ");
		sb.append("         ID_NO, ");
		sb.append("         APPL_ID, ");
		sb.append("         UNIT_CODE, ");
		sb.append("         SERVANT_ID, ");
		sb.append("         BRH_MGR_ID, ");
		sb.append("         INS_SALE_ID, ");
		sb.append("         CUST_ID, ");
		sb.append("         AREA_MGR_ID, ");
		sb.append("         BRH_SMGR1_ID, ");
		sb.append("         BRH_SMGR2_ID, ");
		sb.append("         BRH_SMGR3_ID, ");
		sb.append("         AO_CODE, ");
		sb.append("         BRANCH_COD, ");
		sb.append("         PERSON_DEGREE, ");
		sb.append("         SERVICE_DEGREE, ");
		sb.append("         UHRM_ID, ");
		sb.append("         UHRM_MNG_ID, ");
		sb.append("         UHRM_SMGR1_ID, ");
		sb.append("         UHRM_SMGR2_ID, ");
		sb.append("         UHRM_SMGR3_ID ");
		sb.append("  FROM TBPMS_FWLFN120 ");
		sb.append("  WHERE SNAP_DATE = TRUNC(SYSDATE) ");
		sb.append(") ");
		
		String[] order = { "POLICY_SEQ", "ID_DUP", "ID_NO", "APPL_ID", "UNIT_CODE", "SERVANT_ID", "BRH_MGR_ID", "INS_SALE_ID", "CUST_ID", "AREA_MGR_ID", "BRH_SMGR1_ID", "BRH_SMGR2_ID", "BRH_SMGR3_ID", "AO_CODE", "BRANCH_COD", "PERSON_DEGREE", "SERVICE_DEGREE", "UHRM_ID", "UHRM_MNG_ID", "UHRM_SMGR1_ID", "UHRM_SMGR2_ID", "UHRM_SMGR3_ID" };
		ResultSet rs = null;
		Connection con = gft.getConnection();
		Statement s = null;
		try {
			s = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
			s.setFetchSize(3000);
			rs = gft.getRS(sb, s);
			gft.writeFile("FWLFN110", "TXT", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), order, rs, ",", false, false);
		} finally {
			rs.close();
			s.close();
			con.close();
		}
	}
}
