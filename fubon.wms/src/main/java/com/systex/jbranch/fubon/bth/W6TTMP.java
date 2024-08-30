/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
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
@Repository("w6ttmp")
@Scope("prototype")
public class W6TTMP extends BizLogic {
	 
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	
	SimpleDateFormat sdfYYYYMMDD  = new SimpleDateFormat("yyyyMMdd");
	
	public void createFileBth (Object body, IPrimitiveMap<?> header) throws Exception {
		GenFileTools gft=new GenFileTools();
		dam = this.getDataAccessManager();
		int dataCount;
		QueryConditionIF queryCondition = dam.getQueryCondition();
		//呼叫pck
		System.out.println("start pck");
		callPCK("PABTH_BTCRM6102_UP_DEGREE","PR_CUST_W6T_TMP");
		System.out.println("end pck");
		//定義檔案名稱及輸出的jobname
		String  writeFileName="W6T_TMP";
		String  attached_name="";
		String  jobName="PS_SA_CUST_W6T_TMP";
        String  path=(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		StringBuffer sb = new StringBuffer();
		//先取得總筆數
		sb.append("SELECT COUNT(1) TOTAL_COUNT FROM ( ");
		sb.append(genSql().toString());
		sb.append(")");
		//System.out.println("SQL:"+sb.toString());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> listCount = dam.exeQuery(queryCondition);	
		dataCount=Integer.parseInt(listCount.get(0).get("TOTAL_COUNT").toString());
		String[] order=genOrder();
 
		System.out.println("totalCount:"+dataCount);
		ResultSet rs=null;
		Connection con=gft.getConnection();
		Statement s = null;
		try {
			s = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
	                java.sql.ResultSet.CONCUR_READ_ONLY);
			s.setFetchSize(3000);
		rs=gft.getRS(genSql(),s);
		gft.writeFile(writeFileName, attached_name, path, order, rs, ",", false, true);
    	gft.writeZFile("Z"+writeFileName, attached_name, path, dataCount, "", jobName);
		System.out.println("job  end");
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		}
	}
	//呼叫package
    public void callPCK(String pckName,String pckPR) throws DAOException, JBranchException
    {
        dam = this.getDataAccessManager();
        QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer().append("CALL " + pckName + "."+pckPR+"()");
        condition.setQueryString(sql.toString());
        dam.exeUpdate(condition);
    }
	
	//產檔所需要輸出的欄位
	public String[] genOrder()
	{
		String[] order={"PERSON_ID","CHANGE_TYPE","OLDVALUE",
				"NEWVALUE","APPLY_DTTM","DATA_DATE"};
		return order;
	}
	//產檔需要的SQL
    public StringBuffer genSql()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT * FROM ( ");
		sb.append(" SELECT rownum rowN,a.* from( ");
		
		sb.append("SELECT  CUST_ID        AS PERSON_ID ");
		sb.append(",CHANGE_TYPE           AS CHANGE_TYPE ");
		sb.append(",NVL(ORG_DEGREE,' ')   AS OLDVALUE ");
		sb.append(",NEW_DEGREE            AS NEWVALUE ");
		sb.append(",TO_CHAR(APPL_DATE,'YYYY/MM/DD HH24:MI:SS') AS APPLY_DTTM ");
		sb.append(",TO_CHAR(CHG_DATE,'YYYY/MM/DD HH24:MI:SS')  AS DATA_DATE ");
		sb.append("FROM TBCRM_CUST_W6T_TMP ");

		
		sb.append(")a ");
		sb.append("order by PERSON_ID ");
		sb.append(") ");
		//sb.append("where rownum<=1000 ");
		return sb;
	}	
	
	

	
}


