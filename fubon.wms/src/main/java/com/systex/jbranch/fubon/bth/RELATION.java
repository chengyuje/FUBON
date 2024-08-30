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
@Repository("relation")
@Scope("prototype")
public class RELATION extends BizLogic {
	 
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
		//String  writeFileName="PS_SA_RELATION";
		//String  attached_name=sdfYYYYMMDD.format(new Date());
		//String  jobName="PS_SA_RELATION";
		String  writeFileName="RELATION";
		String  attached_name="";
		String  jobName="PS_SA_RELATION";
        String  path=(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		StringBuffer sb = new StringBuffer();
		//先取得總筆數 判斷是否需要拆多次執行
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
		gft.writeFile(writeFileName, attached_name, path, order, rs, ", ", false, true);
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
		String[] order={"PERSON_ID","BRANCH_NBR","SEQNUM",
				"PERSON_ID2","NAME","BIRTHDATE","RELATION_TYPE",
				"MEMO","SA_MAJOR_REL"
				};
		return order;
	}
	//產檔需要的SQL
    public StringBuffer genSql()
	{
    	System.out.println("getSql");
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT * FROM ( ");
		sb.append(" SELECT rownum rowN,a.* from( ");
		    
		sb.append("SELECT R.CUST_ID_M    AS PERSON_ID ");
		sb.append(",case when M.BRA_NBR is null then ' ' else M.BRA_NBR end            AS BRANCH_NBR ");
		sb.append(",R.SEQ                AS SEQNUM ");
		sb.append(",R.CUST_ID_S          AS PERSON_ID2 ");
		sb.append(",NVL(M2.CUST_NAME,' ')         AS NAME ");
		sb.append(",CASE WHEN M2.BIRTH_DATE=NULL THEN '' ELSE NVL(TO_CHAR(M2.BIRTH_DATE,'yyyy/mm/dd'),'') END AS BIRTHDATE ");
		sb.append(",NVL(R.REL_TYPE,' ')           AS RELATION_TYPE "); 
		sb.append(",' '                   AS MEMO ");          
		sb.append(",' '                   AS SA_MAJOR_REL ");  
		sb.append("FROM TBCRM_CUST_REL R ");
		sb.append("INNER JOIN TBCRM_CUST_MAST M ");
		sb.append("ON M.CUST_ID = R.CUST_ID_M ");
		sb.append("INNER JOIN TBCRM_CUST_MAST M2 ");
		sb.append("ON M2.CUST_ID = R.CUST_ID_S ");
		sb.append(")a ");
		//sb.append("order by PERSON_ID ");
		sb.append(") ");
		//sb.append("where rownum<=1000 ");
		return sb;
	}	
	
	

	
	
}


