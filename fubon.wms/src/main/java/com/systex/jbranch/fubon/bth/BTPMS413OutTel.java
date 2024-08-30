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
@Repository("btpms413outtel")
@Scope("prototype")
public class BTPMS413OutTel extends BizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	public void createFileBth (Object body, IPrimitiveMap<?> header) throws Exception {	
		//String  vpTellerFileName="VP_TELLER";   
		String  vpTellerFileName="VP_TELLER";
		GenFileTools gft =new GenFileTools();
		String  path=(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		// 處理 VP_TELLER
		StringBuffer sb = new StringBuffer();		
		sb.append("SELECT P.BRANCH_NBR AS BRANCH_NBR ");
		sb.append(",P.EMP_ID ");
		sb.append(",P.EMP_NAME ");
		sb.append(",AO.AO_CODE ");
		sb.append(",M.CUST_ID PERSON_ID ");
		sb.append(",P.IPADDRESS ");
		sb.append("FROM TBPMS_BRANCH_IP P ");
		sb.append("LEFT JOIN TBORG_MEMBER M ");
		sb.append("ON M.EMP_ID = P.EMP_ID ");
		sb.append("LEFT JOIN TBORG_SALES_AOCODE AO ");
		sb.append("ON AO.EMP_ID = M.EMP_ID ");
		sb.append("AND (AO.REVIEW_STATUS='Y' ");
		sb.append("OR (AO.TYPE ='2' AND AO.ACTIVE_DATE IS NOT NULL) ) ");
		sb.append("WHERE P.COM_TYPE = '2' ");
		sb.append("AND P.EMP_ID IS NOT NULL ");
		sb.append("ORDER BY P.BRANCH_NBR,P.EMP_ID ");
	    String[] vpTellerOrder = {"BRANCH_NBR","EMP_ID","EMP_NAME","AO_CODE","PERSON_ID","IPADDRESS"};	
		ResultSet rs=null;
		Connection con=gft.getConnection(); 
		Statement s = null;
		try {
			s = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
	                java.sql.ResultSet.CONCUR_READ_ONLY);
			s.setFetchSize(3000);
		rs=gft.getRS(sb,s);
		gft.writeFile(vpTellerFileName, "TXT", path, vpTellerOrder, rs, ",", false, false);
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		}
		//System.out.println("vpTeller List:"+list.size());
		
	}

}
