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
@Repository("inssaleids02")
@Scope("prototype")
public class INSSALEIDS02 extends BizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	public void createFileBth (Object body, IPrimitiveMap<?> header) throws Exception {
		
		GenFileTools gft=new GenFileTools();
		
		String  writeFileName="INS_SALEID";
		
		StringBuffer sb = new StringBuffer();
		sb.append("select RPAD(INS_ID,9,' ') AS INS_ID,TO_CHAR(APPLY_DATE, 'YYYYMMDD')APPLY_DATE,RPAD(NVL(CUST_ID,' ') ,11,'  ') AS CUST_ID,RPAD(NVL(PROPOSER_NAME,' '),50,' ')PROPOSER_NAME, ");
		sb.append("RPAD(INSPRD_ID,4,' ') AS INSPRD_ID,LPAD(CURR_CD,3,' ') AS CURR_CD,LPAD(REAL_PREMIUM,12,' ')REAL_PREMIUM from VWIOT_MAIN ");
		sb.append("WHERE BEF_SIGN_DATE + 7 >= TRUNC(SYSDATE) ");
		sb.append("AND REG_TYPE IN ('1','2') ");
		
	    String[] writeFilerOrder = {"INS_ID","APPLY_DATE","CUST_ID","PROPOSER_NAME","INSPRD_ID","CURR_CD","REAL_PREMIUM"};	
	    ResultSet rs=null;
	    Connection con=gft.getConnection();
		Statement s = null;
		try {
			s = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
	                java.sql.ResultSet.CONCUR_READ_ONLY);
			s.setFetchSize(3000);
	    rs=gft.getRS(sb,s);
	    gft.writeFile(writeFileName,"TXT", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), writeFilerOrder, rs, "", false, true);
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		}
		
	}

}


