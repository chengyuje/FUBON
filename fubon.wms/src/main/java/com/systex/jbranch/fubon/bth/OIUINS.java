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
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
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
@Repository("oiuins")
@Scope("prototype")
public class OIUINS extends BizLogic {
	 
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
		// 取得傳入參數
		Map<String, Object> inputMap = (Map<String, Object>) body;
		Map<String, Object> jobParam = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
		String dateParam = (String) jobParam.get("dateParam");
		System.out.println(dateParam);
		//定義檔案名稱及輸出的jobname
		String  writeFileName="OIU_INS";
		String  attached_name="";
		String  jobName="OIU_INS";
        String  path=(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		StringBuffer sb = new StringBuffer();
		//先取得總筆數 判斷是否需要拆多次執行
		sb.append("SELECT COUNT(1) TOTAL_COUNT FROM ( ");
		sb.append(genSql(dateParam).toString());
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
		rs=gft.getRS(genSql(dateParam),s);
		gft.writeFile(writeFileName, attached_name, path, order, rs, ",", false, false);
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
		String[] order={"INS_ID", 
				"CUST_ID",
				"INSURED_ID",
				"BRANCH_NBR"};
		return order;
	}
	//產檔需要的SQL
    public StringBuffer genSql(String dateParam)
	{ 
		StringBuffer sb=new StringBuffer();
		String param= "= TO_CHAR(SYSDATE-1,'YYYYMMDD') ";

		if(dateParam!=null)
		{
			if(dateParam.split("-").length==2)
			{
				param =" between '"+dateParam.split("-")[0]+"' AND '"+dateParam.split("-")[1]+"' "; 
			}
			else
			{
				param ="='"+dateParam+"' ";
			}
		}
		//System.out.println("param:"+param);
		sb.append(" SELECT * FROM ( ");
		sb.append(" SELECT rownum rowN,a.* from( ");
		
		sb.append("SELECT A.INS_ID as INS_ID, ");
		sb.append("A.CUST_ID as CUST_ID, "); 
		sb.append("A.INSURED_ID as INSURED_ID, "); 
		sb.append("'8A1'|| case when length(A.BRANCH_NBR) = 3 then A.BRANCH_NBR            when length(A.BRANCH_NBR) = 5 then substr(A.BRANCH_NBR,3,3)            else substr(A.BRANCH_NBR,1,3)            end BRANCH_NBR "); 
		sb.append("FROM TBIOT_MAIN A "); 
		sb.append("INNER JOIN TBIOT_BATCH_INFO B ");
		sb.append("ON A.BATCH_INFO_KEYNO=B.BATCH_INFO_KEYNO ");
		sb.append("AND TO_CHAR(B.INS_RCV_DATE,'YYYYMMDD') ");
		sb.append(param);
		sb.append("WHERE A.INSPRD_KEYNO IN (SELECT INSPRD_KEYNO FROM TBPRD_INS_MAIN WHERE INSPRD_ID LIKE '%FSO%') "); 
		//sb.append("AND A.MODIFIER <> 'VP1' ");
		
		/*
		sb.append("SELECT rpad(A.INS_ID,17,' ') as INS_ID, ");
		sb.append("rpad(A.CUST_ID,11,' ') as CUST_ID, ");
		sb.append("rpad(A.INSURED_ID,11,' ') as INSURED_ID, ");
		sb.append("rpad(A.BRANCH_NBR,10,' ') as BRANCH_NBR ");
		sb.append("FROM TBIOT_MAIN A ");
		sb.append("WHERE A.INSPRD_KEYNO IN (SELECT INSPRD_KEYNO FROM TBPRD_INS_MAIN WHERE INSPRD_ID LIKE '%FSO%') ");
		sb.append("AND TRUNC(A.LASTUPDATE) = TRUNC(SYSDATE)-1 ");
		sb.append("AND A.MODIFIER <> 'VP1' ");
		*/
		sb.append(")a ");
		//sb.append("order by PERSON_ID ");
		sb.append(") ");
		//System.out.println(sb.toString());
		return sb;
	}	
	
	

	
}


