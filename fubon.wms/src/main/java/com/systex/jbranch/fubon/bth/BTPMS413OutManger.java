/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 1. 讀取DB產出 .yyyymmdd
 * 2. 將.yyyymmdd 上傳FTP
 * 
 * @author 1600216
 * @date 2016/11/16
 *
 */
@Repository("btpms413outmanger")
@Scope("prototype")
public class BTPMS413OutManger extends BizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	public void createFileBth (Object body, IPrimitiveMap<?> header) throws Exception {
		GenFileTools gft =new GenFileTools();
		/*
		String fileName = ("FWLEN110.txt");
		FileWriter writer = new FileWriter((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH)  + "reports\\" + fileName);
		writer.flush();
		writer.close();		
		*/
		//System.out.println("btpms413_out go!!");
       //處理VP_MANAGER
				String  vpManagerFileName ="VP_MANAGER";	
				StringBuffer sb = new StringBuffer();
				String  path=(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				sb.append("SELECT P.BRANCH_NBR,EMP.EMP_ID,EMP.EMP_NAME,EMP.EMP_EMAIL_ADDRESS ");
				sb.append("FROM (SELECT DISTINCT BRANCH_NBR FROM TBPMS_BRANCH_IP WHERE COM_TYPE = '2') P ");
				sb.append("INNER JOIN TBORG_DEFN ORG ");
				sb.append("ON ORG.DEPT_ID = P.BRANCH_NBR ");
				sb.append("LEFT JOIN ( ");
				sb.append("SELECT M.DEPT_ID,M.EMP_ID,M.EMP_NAME,R.ROLE_ID,M.EMP_EMAIL_ADDRESS ");
				sb.append(",RANK() OVER(PARTITION BY M.DEPT_ID ORDER BY R.ROLE_ID) AS RN ");
				sb.append("FROM TBORG_MEMBER M ");
				sb.append("INNER JOIN TBORG_MEMBER_ROLE R ");
				sb.append("ON R.EMP_ID = M.EMP_ID ");
				sb.append("AND R.ROLE_ID IN ('A161','A149','ABRU') ");
				sb.append("AND R.IS_PRIMARY_ROLE='Y' ");
				sb.append("UNION ALL ");
				sb.append("SELECT PM.DEPT_ID,M.EMP_ID,M.EMP_NAME,R.ROLE_ID,M.EMP_EMAIL_ADDRESS ");
				sb.append(",RANK() OVER(PARTITION BY PM.DEPT_ID ORDER BY R.ROLE_ID) AS RN ");
				sb.append("FROM TBORG_MEMBER M ");
				sb.append("INNER JOIN TBORG_MEMBER_ROLE R ");
				sb.append("ON R.EMP_ID = M.EMP_ID ");
				sb.append("AND R.ROLE_ID IN ('A161') ");
				sb.append("AND R.IS_PRIMARY_ROLE='N' ");
				sb.append("LEFT JOIN TBORG_MEMBER_PLURALISM PM ");
				sb.append("ON PM.EMP_ID = M.EMP_ID ");
				sb.append("AND TRUNC(SYSDATE) BETWEEN PM.EFFDT AND NVL(PM.TERDTE,TRUNC(SYSDATE)) ");
				sb.append("WHERE PM.DEPT_ID IS NOT NULL ");
				sb.append(") EMP ");
				sb.append("ON EMP.DEPT_ID = ORG.DEPT_ID ");
				sb.append("AND RN=1 ");
				sb.append("ORDER BY P.BRANCH_NBR ");

				String[] vpManagerOrder ={"BRANCH_NBR","EMP_ID","EMP_NAME","EMP_EMAIL_ADDRESS"};
				ResultSet rs=null; 
				Connection con=gft.getConnection();
				Statement s = null;
				try {
					s = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
			                java.sql.ResultSet.CONCUR_READ_ONLY);
					s.setFetchSize(3000);
				rs=gft.getRS(sb,s);
				gft.writeFile(vpManagerFileName, "TXT", path, vpManagerOrder, rs, ",", false, false);
				} finally {
					if (rs != null) try { rs.close(); } catch (Exception e) {}
					if (s != null) try { s.close(); } catch (Exception e) {}
					if (con != null) try { con.close(); } catch (Exception e) {}
				}//System.out.println("vpManager List:"+list.size());
	}
	
}
