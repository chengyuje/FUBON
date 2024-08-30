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
@Repository("vpqualify")
@Scope("prototype")
public class VPQUALIFY extends BizLogic {
	 
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
		String  writeFileName="VP_QUALIFY";
		String  attached_name="TXT";
		String  jobName="VP_QUALIFY";
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
		gft.writeFile(writeFileName, attached_name, path, order, rs, "", false, false);
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
		String[] order={"CUST_ID", 
				"BRANCH_CODE",
				"Q6",
				"DATA_DATE",
				"VP1",
				"P_TYPE"};
		return order;
	}
	//產檔需要的SQL
    public StringBuffer genSql()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT * FROM ( ");
		sb.append(" SELECT rownum rowN,a.* from( ");
		
        sb.append("SELECT RPAD(CUST_ID,20,' ') AS CUST_ID, ");
		sb.append("RPAD(BRANCH_CODE,10,' ') AS BRANCH_CODE, "); 
		sb.append("RPAD('Q6',3,' ') AS Q6, "); 
		sb.append("RPAD(TO_CHAR(SYSDATE,'YYYYMMDD')||'000000',38,' ') AS DATA_DATE, "); 
		sb.append("RPAD('VP1',10,' ') VP1, "); 
		sb.append("RPAD(P_TYPE,80,' ') AS P_TYPE "); 
		sb.append("FROM (  ");
		sb.append("SELECT CUST_ID,BRANCH_CODE,  ");
		sb.append("LISTAGG(NVL(P_TYPE,''), ' ') WITHIN GROUP (ORDER BY P_TYPE) AS P_TYPE  ");
		sb.append("FROM (  ");
        
		sb.append("SELECT C.BDAF2  AS CUST_ID, ");
		sb.append("C.BDAFL  AS BRANCH_CODE, ");  
		sb.append("C.BDA02  AS P_TYPE ");
		sb.append("FROM (SELECT A.BDAF2, "); 
		sb.append("SUBSTR(A.BD160,1,3)BDAFL, "); 
		sb.append("B.BDA02, "); 
		sb.append("A.BDAF1, "); 
		sb.append("A.MDATETIME, "); 
		sb.append("SYSDATE SDATE "); 
		sb.append("FROM ODS_BANK.BDS160@ODSTOWMS A, "); 
		sb.append("ODS_BANK.BDS010@ODSTOWMS B ");
		sb.append("WHERE A.BDAF1=B.BDA01) C "); 
		sb.append("LEFT OUTER JOIN ODS_BANK.BDS019@ODSTOWMS D ON (C.BDAF1=D.BDAS1) ");
		sb.append("WHERE C.BDA02='SN' "); 
		sb.append("AND TO_CHAR(C.MDATETIME, 'YYYYMMDD') >='20050201' "); 
		sb.append("UNION ALL "); 		

		sb.append("SELECT distinct CUSTOMERNO CUST_ID , "); 
		sb.append("BRANCHCODE BRANCH_CODE, ");  
		sb.append("'DCI' P_TYPE "); 
		sb.append("FROM ODS_BANK.DCD001_DAY@ODSTOWMS "); 
		sb.append("WHERE SNAP_DATE = (SELECT MAX(SNAP_DATE ) "); 
		sb.append("FROM ODS_BANK.DCD001_DAY@ODSTOWMS ) "); 
		sb.append("UNION ALL "); 
        
		sb.append("SELECT distinct A.IVID, ");  
		sb.append("SUBSTR(A.IVBRH,1,3), "); 
		sb.append("'SD' TYPE "); 
		sb.append("FROM (SELECT * "); 
		sb.append("FROM ODS_BANK.SDINVMP0_DAY@ODSTOWMS "); 
		sb.append("WHERE TO_CHAR(SNAP_DATE,'YYYYMMDD') >='20060201'  ");
		sb.append("AND SUBSTR(SDPRD,3,2) = 'WM') A  ");
		sb.append("LEFT OUTER JOIN (SELECT *  ");
		sb.append("FROM ODS_BANK.SDNPDMP0_ME@ODSTOWMS "); 
		sb.append("WHERE SUBSTR(SDPRD,3,2) = 'WM' "); 
		sb.append("AND SNAP_DATE = (SELECT MAX(SNAP_DATE) "); 
		sb.append("FROM ODS_BANK.SDNPDMP0_ME@ODSTOWMS)) B "); 
		sb.append("ON (A.SDPRD =B.SDPRD) "); 
		sb.append("UNION ALL "); 
        
		sb.append("SELECT distinct APPL_ID, "); 
		sb.append("SUBSTR(BRANCH_NBR,4,3), "); 
		sb.append("'INS' TYPE "); 
		sb.append("FROM TBCRM_NPOLM "); 
		sb.append("WHERE ITEM_REMRK='F' "); 
		sb.append(") "); 
		sb.append("GROUP BY CUST_ID,BRANCH_CODE) "); 
		sb.append(")a ");
		//sb.append("order by PERSON_ID ");
		sb.append(") ");
		return sb;
	}	

}


