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
 * 
 * @author 1600216
 * @date 2016/11/16
 *
 */
@Repository("vpd00psm")
@Scope("prototype")
public class VPD00PSM extends BizLogic {
	 
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
		String  writeFileName="FC017QSP";
		String  writeFileName2="VP_CUS_AO";
		String  writeFileName3="VP_AO_INFO_ALL_CHK";
		String  attached_name="";
		String  jobName="";
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
		String[] order2=genOrder2();
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
		rs=gft.getRS(genSql(),s);
		gft.writeFile(writeFileName2, attached_name, path, order2, rs, "", false, false);
		//chk檔格式 
		StringBuffer chkLayout=new StringBuffer();
		chkLayout.append(sdfYYYYMMDD.format(new Date()));
		chkLayout.append(gft.addZeroForNum(String.valueOf(dataCount),12));
		gft.writeFileByText(writeFileName3, attached_name, path, chkLayout,false);
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		}
		System.out.println("job  end");
	}


	//產檔所需要輸出的欄位
	public String[] genOrder()
	{
		String[] order={"PERSON_ID",
				"BRANCH_NBR",
				"SEQ",  
				"EMPLID",
				"VIP_DEGREE",  
				"AO_CODE",
				"SA_ADR_CODE", 
				"PREMIUM_CODE",
				"ANNUAL_INCOM",
				"ROW_ADDED_D",
				"TEMP1",
				"TEMP2",
				"BRANCH_NBR_DISP"};
		return order;
	}
	
	public String[] genOrder2()
	{
		String[] order={"PERSON_ID",
				"BRANCH_NBR",
				"SEQ",         
				"EMPLID",
				"VIP_DEGREE_V",  
				"AO_CODE",
				"SA_ADR_CODE", 
				"ANNUAL_INCOM",
				"ROW_ADDED_D" 
				//"BILL_TYPE"
				};
		return order;
	}
	//產檔需要的SQL
    public StringBuffer genSql()
	{ 
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT * FROM ( ");
		sb.append("   SELECT ROWNUM rowN, A.* FROM ( ");
		sb.append("     SELECT RPAD(C.CUST_ID,11,' ') AS PERSON_ID ");
		sb.append("          , RPAD(COALESCE(CIF.BRA_NBR,C.BRA_NBR,'000'),3,' ') AS BRANCH_NBR ");
		sb.append("          , '00' AS SEQ ");
		sb.append("          , RPAD(EMP.CUST_ID,11,' ') AS EMPLID ");
		sb.append("          , NVL(C.VIP_DEGREE,' ') AS VIP_DEGREE ");
		sb.append("          , CASE WHEN C.VIP_DEGREE='V' THEN 'A' ELSE NVL(C.VIP_DEGREE,' ') END AS VIP_DEGREE_V ");
		sb.append("          , RPAD(C.AO_CODE,3,' ') AS AO_CODE ");
		sb.append("          , '0000' AS SA_ADR_CODE ");
		sb.append("          , ' ' AS PREMIUM_CODE ");
		sb.append("          , RPAD(NVL(C.ANNUAL_INCOME_AMT,'0'),9,'0') AS ANNUAL_INCOM ");
		sb.append("          , RPAD(TO_CHAR(C.LASTUPDATE,'YYYYMMDD'),8,' ') AS ROW_ADDED_D ");
		sb.append("          , RPAD(NVL(C.BILL_TYPE,' '),3,' ') AS BILL_TYPE ");
		sb.append("          , RPAD(' ',56,' ') AS TEMP1 ");
		sb.append("          , RPAD(' ',11,' ') AS TEMP2 ");
		sb.append("          , RPAD(NVL(C.BRA_NBR,' '),3,' ') AS BRANCH_NBR_DISP ");
		sb.append("     FROM ( SELECT CUST_ID, BRA_NBR, AO_CODE, VIP_DEGREE ");
		sb.append("                 , ANNUAL_INCOME_AMT, LASTUPDATE , BILL_TYPE ");
		sb.append("            FROM TBCRM_CUST_MAST WHERE AO_CODE IS NOT NULL ) C ");
		sb.append("     INNER JOIN TBORG_SALES_AOCODE AO ");
		sb.append("       ON AO.AO_CODE = C.AO_CODE ");
		sb.append("     INNER JOIN ( ");
		sb.append("       SELECT EMP_ID ,CUST_ID FROM TBORG_MEMBER ");
		sb.append("       WHERE SERVICE_FLAG = 'A' AND CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("         AND EMP_ID IN ( ");
		sb.append("           SELECT EMP_ID FROM TBORG_MEMBER_ROLE A ");
		sb.append("           INNER JOIN TBSYSPARAMETER B ");
		sb.append("             ON A.ROLE_ID = B.PARAM_CODE ");
		sb.append("           WHERE B.PARAM_TYPE IN ('FUBONSYS.FC_ROLE','FUBONSYS.FCH_ROLE' ))) EMP ");
		sb.append("       ON EMP.EMP_ID = AO.EMP_ID ");
		sb.append("     LEFT JOIN ( SELECT DISTINCT CUST_ID, BRA_NBR FROM TBCRM_ACCT_MAST A ");
		sb.append("                 WHERE EXISTS (SELECT 1 FROM TBCRM_CUST_MAST B WHERE B.AO_CODE IS NOT NULL AND A.CUST_ID=B.CUST_ID )) CIF ");
		sb.append("       ON C.CUST_ID=CIF.CUST_ID ) A ");
		sb.append("   ORDER BY PERSON_ID  ) ");
		return sb;
	}	
}


