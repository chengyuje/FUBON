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
@Repository("mgm")
@Scope("prototype")
public class MGM extends BizLogic {
	 
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
		String  writeFileName="MGM";
		//String  attached_name="TXT";
		String  attached_name="";
		String  jobName="PS_FP_MGM";
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
		gft.writeFile(writeFileName, attached_name, path, order, rs, ",", false, true);
		String fileDate=gft.getFtpFileDate("MGM", true, "DES");

		StringBuffer zFileLayout=new StringBuffer();
		zFileLayout.append(gft.addBlankForString(jobName, 30));
		zFileLayout.append(gft.addZeroForNum(String.valueOf(dataCount),15));
		zFileLayout.append(fileDate);
		gft.writeFileByText("Z"+writeFileName, attached_name, path, zFileLayout,false);
		
		//gft.writeZFile("Z"+writeFileName, attached_name, path, dataCount, "", jobName);
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
		String[] order={"ACTIVITY_ID",
				"CUST_ID",
				"CUST_NAME",
				"CUST_BRA",
				"REFER_ID",
				"REFER_NAME",
				"REFER_TEL_H",
				"REFER_INFO",
				"REFER_DATE",
				"DOUBLE_CHECK",
				"DOUBLE_CHECK_SUBMIT",
				"DOUBLE_CHECK_SUBMIT_DATE",
				"REMARK",
				"TABLE_A",
				"TABLE_B",
				"TABLE_A_SUBMIT",
				"TABLE_B_SUBMIT",
				"TABLE_A_SUBMIT_DATE",
				"TABLE_B_SUBMIT_DATE",
				"POINT_BATCH",
				"POINT_CHECK",
				"POINT_CHECK_SUBMIT",
				"POINT_CHECK_SUBMIT_DATE",
				"REWARD_POINT",
				"MEMO",
				"CREATOR",
				"CREATETIME",
				"MODIFIER",
				"LASTUPDATE",
				"ACTIVITY_NAME"};
		return order;
	}
	//產檔需要的SQL
    public StringBuffer genSql()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT * FROM ( ");
		sb.append(" SELECT rownum rowN,a.* from( ");
		 
		sb.append("SELECT DISTINCT ACTIVITY_ID, ");
		sb.append("CUST_ID, ");
		sb.append("CUST_NAME, ");
		sb.append("CUST_BRA, ");
		sb.append("REFER_ID, ");
		sb.append("REFER_NAME, ");
		sb.append("REFER_TEL_H, ");
		sb.append("REFER_INFO, ");
		sb.append("TO_CHAR(REFER_DATE,'YYYYMMDD') AS REFER_DATE, ");
		sb.append("DOUBLE_CHECK, ");
		sb.append("DOUBLE_CHECK_SUBMIT, ");
		sb.append("TO_CHAR(DOUBLE_CHECK_SUBMIT_DATE,'YYYYMMDD') AS DOUBLE_CHECK_SUBMIT_DATE, ");
		sb.append("REMARK, ");
		sb.append("TABLE_A, ");
		sb.append("TABLE_B, ");
		sb.append("TABLE_A_SUBMIT, ");
		sb.append("TABLE_B_SUBMIT, ");
		sb.append("TO_CHAR(TABLE_A_SUBMIT_DATE,'YYYYMMDD') AS TABLE_A_SUBMIT_DATE, ");
		sb.append("TO_CHAR(TABLE_B_SUBMIT_DATE,'YYYYMMDD') AS TABLE_B_SUBMIT_DATE, ");
		sb.append("POINT_BATCH, ");
		sb.append("POINT_CHECK, ");
		sb.append("POINT_CHECK_SUBMIT, ");
		sb.append("TO_CHAR(POINT_CHECK_SUBMIT_DATE,'YYYYMMDD') AS POINT_CHECK_SUBMIT_DATE, ");
		sb.append("REWARD_POINT, ");
		sb.append("MEMO, ");
		sb.append("CREATOR, ");
		sb.append("TO_CHAR(CREATETIME,'YYYYMMDD') AS CREATETIME, ");
		sb.append("MODIFIER, ");
		sb.append("TO_CHAR(LASTUPDATE,'YYYYMMDD') AS LASTUPDATE, ");
		sb.append("ACTIVITY_NAME  ");
		sb.append("FROM peopsoft.PS_FP_MGM2014 ");
		sb.append("WHERE ACTIVITY_NAME IN ('2016MGM','2017MGM') ");

		
		sb.append(")a ");
		//sb.append("order by PERSON_ID ");
		sb.append(") ");
		//sb.append("where rownum<=1000 ");
		return sb;
	}	
	
	

	
}


