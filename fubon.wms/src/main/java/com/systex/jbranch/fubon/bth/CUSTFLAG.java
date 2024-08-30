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
@Repository("custflag")
@Scope("prototype")
public class CUSTFLAG extends BizLogic {
	 
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
		String  writeFileName="CFLAG";
		String  attached_name="";
		//String  attached_name=sdfYYYYMMDD.format(new Date());
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
		//chk檔格式 
		StringBuffer chkLayout=new StringBuffer();
		chkLayout.append(gft.addBlankForString("PS_SA_CUST_FLAG", 30));
		chkLayout.append(gft.addZeroForNum(String.valueOf(dataCount),15));
		chkLayout.append(sdfYYYYMMDD.format((new Date().getTime()+((long)-1 * 24 * 60 * 60 * 1000))));
		gft.writeFileByText("Z"+writeFileName, attached_name, path, chkLayout,false);

		System.out.println("job  end");
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		}
	}


	public String[] genOrder()
	{
		String[] order={"CUST_ID","FLAG","DATA_DATE"};
		return order;
	}
	//產檔需要的SQL
    public StringBuffer genSql()
	{

		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT * FROM ( ");
		sb.append(" SELECT rownum rowN,a.* from( ");
		
		sb.append(" SELECT CN.CUST_ID ");
		sb.append(" ,CASE WHEN CN.PROF_INVESTOR_YN = 'Y' THEN 'P' END AS FLAG ");
		sb.append(" ,TO_CHAR(SYSDATE,'YYYYMMDD') AS DATA_DATE ");
		sb.append(" FROM TBCRM_CUST_NOTE CN ");
		sb.append(" WHERE CN.PROF_INVESTOR_YN = 'Y' ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT CN.CUST_ID ");
		sb.append(" ,CASE WHEN CN.SIGN_AGMT_YN = 'N' THEN 'I' END AS FLAG ");
		sb.append(" ,TO_CHAR(SYSDATE,'YYYYMMDD') AS DATA_DATE ");
		sb.append(" FROM TBCRM_CUST_NOTE CN ");
		sb.append(" WHERE CN.SIGN_AGMT_YN = 'N' ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT CN.CUST_ID ");
		sb.append(" ,CASE WHEN CN.SP_CUST_YN = 'Y' THEN 'V' END AS FLAG ");
		sb.append(" ,TO_CHAR(SYSDATE,'YYYYMMDD') AS DATA_DATE ");
		sb.append(" FROM TBCRM_CUST_NOTE CN  ");    
		sb.append(" WHERE CN.SP_CUST_YN = 'Y' ");
		sb.append(" UNION ALL "); 
		sb.append(" SELECT CN.CUST_ID ");
		sb.append(" ,CASE WHEN CN.COMM_RS_YN = 'Y' THEN 'X1' END AS FLAG ");
		sb.append(" ,TO_CHAR(SYSDATE,'YYYYMMDD') AS DATA_DATE ");
		sb.append(" FROM TBCRM_CUST_NOTE CN ");     
		sb.append(" WHERE CN.COMM_RS_YN = 'Y' ");
		sb.append(" UNION ALL "); 
		sb.append(" SELECT CN.CUST_ID ");
		sb.append(" ,CASE WHEN CN.COMM_NS_YN = 'Y' THEN 'NS' END AS FLAG ");
		sb.append(" ,TO_CHAR(SYSDATE,'YYYYMMDD') AS DATA_DATE ");
		sb.append(" FROM TBCRM_CUST_NOTE CN ");
		sb.append(" WHERE CN.COMM_NS_YN = 'Y' ");
		
		sb.append(")a WHERE SUBSTR(CUST_ID,1,1)<> ' ' order by CUST_ID ) ");
		return sb;
	}	
	
	

	

}


