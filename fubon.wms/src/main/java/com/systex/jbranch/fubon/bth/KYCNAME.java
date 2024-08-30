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
@Repository("kycname")
@Scope("prototype")
public class KYCNAME extends BizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	public void createFileBth (Object body, IPrimitiveMap<?> header) throws Exception {
		
		GenFileTools gft=new GenFileTools();
		//System.out.println("KYCNAME go!!");
		String  writeFileName="KYC_NAME";

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT RPAD(CUST_RL_ID||RL_NAME,12,' ') as CNAME ");       
		sb.append("FROM TBKYC_QUESTIONNAIRE_RISK_LEVEL ");        
		sb.append("WHERE RL_VERSION = ");
		sb.append("(      SELECT n.RL_VERSION    from TBSYS_QUESTIONNAIRE n ");   
		sb.append("where n.STATUS = '02'    and n.QUEST_TYPE = '02' ");        
		sb.append("and ACTIVE_DATE <= sysdate ");
		sb.append("and RL_VERSION IS NOT NULL ");
		sb.append("order by QST_NO fetch first 1 rows only) ");       
		sb.append("order by RL_UP_RATE ");
	    String[] writeFilerOrder = {"CNAME"};
	    ResultSet rs=null;
	    Connection con=gft.getConnection();
		Statement s = null;
		try { 
			s = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
	                java.sql.ResultSet.CONCUR_READ_ONLY);
			s.setFetchSize(3000);
	    rs=gft.getRS(sb,s);
	    gft.writeFile(writeFileName, "TXT", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), writeFilerOrder, rs, "", false, false);
		//System.out.println("vpTeller List:"+list.size());
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		}
	}
	
}


