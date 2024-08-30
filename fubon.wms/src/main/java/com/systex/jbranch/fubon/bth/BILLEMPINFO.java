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
@Repository("billempinfo")
@Scope("prototype")
public class BILLEMPINFO extends BizLogic {
	 
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
		String  writeFileName="BILL_EMPINFO";
		String  attached_name="TXT";
		//String  attached_name=sdfYYYYMMDD.format(new Date());
		String  jobName="BILL_EMPINFO";
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


		//chk檔格式
		StringBuffer chkLayout=new StringBuffer();
		chkLayout.append(gft.addZeroForNum("0",14));
		Date fileDate=new Date();
		chkLayout.append(sdfYYYYMMDD.format(new Date(fileDate.getTime()-((long)1 * 24 * 60 * 60 * 1000))).substring(0,6));
		chkLayout.append(gft.addZeroForNum(String.valueOf(dataCount),9));
		chkLayout.append(gft.addBlankForString(" ", 17));
		chkLayout.append("\r\n"); 
		gft.writeFileByText(writeFileName, attached_name, path, chkLayout,false);
		ResultSet rs = null;
		Connection conn=gft.getConnection();
		Statement s = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                java.sql.ResultSet.CONCUR_READ_ONLY);
        s.setFetchSize(3000);
		try {
		 rs=gft.getRS(genSql(),s);
		gft.writeFile(writeFileName, attached_name, path, order, rs, "", true, false);
		System.out.println("job  end");
		rs.close();
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (conn != null) try { conn.close(); } catch (Exception e) {}
		}
	}



	//產檔所需要輸出的欄位
	public String[] genOrder()
	{
		String[] order={"PERSON_ID","BRANCH_NBR","DISPLAYNAME"};
		return order;
	}
	//產檔需要的SQL
    public StringBuffer genSql()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT * FROM ( ");
		sb.append(" SELECT rownum rowN,a.* from( ");
		
		sb.append("SELECT RPAD(NVL(C.CUST_ID,' '),11,' ')         AS PERSON_ID ");
		sb.append(",RPAD(NVL(C.BRANCH_NBR,' '),3,' ')             AS BRANCH_NBR ");
		sb.append(",RPAD(NVL(EMP.EMP_NAME,' '),32,' ')            AS DISPLAYNAME ");
		sb.append("FROM TBPMS_CUST_AO_ME C ");
		sb.append("INNER JOIN TBORG_SALES_AOCODE AO ");
		sb.append("ON AO.AO_CODE = C.AO_CODE ");
		sb.append("AND AO.TYPE <> '3' ");
		sb.append("INNER JOIN (SELECT A.EMP_ID EMP_ID, B.CUST_NAME EMP_NAME FROM TBORG_MEMBER A, TBCRM_CUST_MAST B WHERE A.CUST_ID = B.CUST_ID) EMP ");
		sb.append("ON EMP.EMP_ID = AO.EMP_ID ");
		//sb.append("WHERE YEARMONTH ='201703' ");
		sb.append("WHERE YEARMONTH =TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMM') ");
		sb.append("AND TRIM(C.AO_CODE) IS NOT NULL ");

		
		sb.append(")a ");
		sb.append("order by PERSON_ID ");
		sb.append(") ");
		//sb.append("where rownum<=1000 ");
		return sb;
	}	
	
	

	
}


