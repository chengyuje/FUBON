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
@Repository("ccyntep0return")
@Scope("prototype")
public class CCYNTEP0RETURN extends BizLogic {
	 
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
		String  writeFileName="CCY_RETURN";
		String  attached_name="";
		String  path=(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		//String  attached_name=sdfYYYYMMDD.format(new Date());
		String  jobName="CCYNTEP0_RETURN";
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
		gft.writeFile(writeFileName, attached_name, path, order, rs, "", true, false);
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
		String[] order={"FIELD1","FIELD_EMAIL","FIELD3",
				"FIELD4","FIELD5","FIELD6","FIELD7",
				"FIELD8","FIELD_CHAR","SALES_PERSON","AO_CODE"};
		return order;
	}
	//產檔需要的SQL
    public StringBuffer genSql()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT * FROM ( ");
		sb.append(" SELECT rownum rowN,a.* from( ");
	
		sb.append("SELECT RPAD(NVL(FIELD1,' '),2,' ') AS FIELD1, ");
		sb.append("CASE WHEN FIELD5 is null THEN RPAD(' ',68,' ') ");
		sb.append("ELSE RPAD(TM.EMP_EMAIL_ADDRESS,68,' ') END ");
		sb.append("AS FIELD_EMAIL, ");
		sb.append("RPAD(NVL(FIELD3,' '),11,' ') AS FIELD3, ");
		sb.append("RPAD(NVL(REPLACE(FIELD4,'　',''),' '),12,' ') AS FIELD4, ");
		sb.append("RPAD(NVL(FIELD5,' '),11,' ') AS FIELD5, ");
		sb.append("RPAD(NVL(FIELD6,' '),6,' ')  AS FIELD6, ");
		sb.append("RPAD(NVL(REPLACE(FIELD7,'　',''),' '),12,' ') AS FIELD7, ");
		sb.append("RPAD(NVL(FIELD8,' '),22,' ') AS FIELD8, ");
		sb.append("RPAD(NVL(FIELD_CHAR,' '),74,' ') AS FIELD_CHAR, ");
		sb.append("RPAD(' ',11,' ') AS SALES_PERSON, ");
		sb.append("RPAD(' ',3,' ') AS AO_CODE ");
		sb.append("FROM TBCAM_WORLD_CARD_L TWC ");
		sb.append("LEFT JOIN TBORG_MEMBER TM ON TM.EMP_ID =TWC.FIELD5 ");
		
		sb.append(")a ");
		//sb.append("order by PERSON_ID ");
		sb.append(") ");
		//sb.append("where rownum<=1000 ");
		return sb;
	}	
	
	

	
	
}


