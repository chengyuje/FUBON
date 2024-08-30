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
@Repository("familyapply")
@Scope("prototype")
public class FAMILYAPPLY extends BizLogic {
	 
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	
	SimpleDateFormat sdfYYYYMMDD  = new SimpleDateFormat("yyyyMMdd");
	
	public void createFileBth (Object body, IPrimitiveMap<?> header) throws Exception {
		GenFileTools gft=new GenFileTools();
		dam = this.getDataAccessManager();
		int dataCount;
		QueryConditionIF queryCondition = dam.getQueryCondition();
		//System.out.println("btpms413_out go!!");
		//定義檔案名稱及輸出的jobname
		String  writeFileName="FAMILY_APPLY";
		String  attached_name="";
		String  jobName="PS_SA_FAMILY_APPLY";
	    String  path=(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		StringBuffer sb = new StringBuffer();
		//先取得總筆數 判斷是否需要拆多次執行
		sb.append("SELECT COUNT(1) TOTAL_COUNT FROM ( ");
		sb.append(genSql().toString());
		sb.append(")");
		System.out.println("SQL:"+sb.toString());
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
		gft.writeZFile("Z"+writeFileName, attached_name, path, dataCount, "", jobName,-1);
        System.out.println("job  end");
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		}
	}

	//產檔所需要輸出的欄位
	public String[] genOrder()
	{
		String[] order={"APPLY_DTTM",
				"CUST_ID_S",
				"REL_TYPE",
				"SEQNO",   
				"CUST_ID_M",
				"PRV_APL_TYPE", 
				"APPLY_STATUS",
				"CREATOR",
				"ROW_ADDED_DTTM",
				"BRA_MGR_EMP_ID",
				"CHECK_DTTM",
				"CHECK_REJ_CODE",
				"OP_MGR_EMP_ID", 
				"APPROVE_DTTM",
				"APRV_REJ_CODE",
				"ACTIVATE_DTTM",
				"BRANCH_NBR",
				"FAMILY_DEGREE"};
		return order;
	}
	//產檔需要的SQL
    public StringBuffer genSql()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT * FROM ( ");
		sb.append(" SELECT DISTINCT a.* from( ");
	
		sb.append("SELECT TO_CHAR(APL_DATE ,'YYYY/MM/DD HH24:MI:SS') as APPLY_DTTM, ");
		sb.append("CUST_ID_S, ");
		sb.append("CASE REL_TYPE ");
		sb.append("WHEN NULL THEN NULL ");
		sb.append("WHEN '00' THEN '1' ");
		sb.append("ELSE REL_TYPE END AS REL_TYPE, "); 
		sb.append("CASE WHEN REL_TYPE='00' THEN '1' ELSE TO_CHAR(SEQ+1) END as SEQNO, ");
		sb.append("CUST_ID_M, ");
		sb.append("PRV_APL_TYPE, "); 
		sb.append("'3' APPLY_STATUS, "); 
		sb.append("P.CREATOR,  ");
		sb.append("TO_CHAR(P.CREATETIME ,'YYYY/MM/DD HH24:MI:SS') as ROW_ADDED_DTTM, ");
		sb.append("BRA_MGR_EMP_ID,  "); 
		sb.append("TO_CHAR( BRA_MGR_RPL_DATE,'YYYY/MM/DD HH24:MI:SS') as CHECK_DTTM, "); 
		sb.append("'' as CHECK_REJ_CODE, "); 
		sb.append("OP_MGR_EMP_ID,  "); 
		sb.append("TO_CHAR( OP_MGR_RPL_DATE ,'YYYY/MM/DD HH24:MI:SS') as APPROVE_DTTM, ");
		sb.append("'' as APRV_REJ_CODE, "); 
		sb.append("TO_CHAR( ACT_DATE ,'YYYY/MM/DD HH24:MI:SS') as ACTIVATE_DTTM, "); 
		sb.append("E.BRANCH_NBR, ");
		sb.append("FAMILY_DEGREE ");
		sb.append("FROM TBCRM_CUST_PRV P, VWORG_BRANCH_EMP_DETAIL_INFO E ");
		sb.append("WHERE P.CREATOR = E.EMP_ID ");
		sb.append("AND CUST_ID_S IS NOT NULL AND APL_DATE IS NOT NULL ");
		sb.append("AND PRV_STATUS = 'PSN' ");
		sb.append(")a ");
		sb.append(" WHERE APPLY_DTTM IS NOT NULL AND CUST_ID_S IS NOT NULL AND CUST_ID_M IS NOT NULL ");
		sb.append("order by APPLY_DTTM ");	
		sb.append(") ");
		return sb;
	}	
	
	

	

}


