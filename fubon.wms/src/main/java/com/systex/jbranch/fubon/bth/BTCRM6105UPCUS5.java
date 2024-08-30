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
@Repository("btcrm6105upcus5")
@Scope("prototype")
public class BTCRM6105UPCUS5 extends BizLogic {
	 
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	
	SimpleDateFormat sdfYYYYMMDD  = new SimpleDateFormat("yyyyMMdd");
	
	public void createFileBth (Object body, IPrimitiveMap<?> header) throws Exception {
		GenFileTools gft = new GenFileTools();
		
		String arg = null;
		if (null != ((Map) ((Map) body).get(SchedulerHelper.JOB_PARAMETER_KEY)).get("arg")) {
			arg = (String) (((Map) ((Map) body).get(SchedulerHelper.JOB_PARAMETER_KEY)).get("arg"));
		}
		
		dam = this.getDataAccessManager();
		int dataCount;
		QueryConditionIF queryCondition = dam.getQueryCondition();
		
		//System.out.println("btpms413_out go!!");
		//定義檔案名稱及輸出的jobname
		String writeFileName = "CUST_DEGREE";
		String attached_name = "";
		String jobName = "PS_SA_CUST_DEGREE";
		String path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		StringBuffer sb = new StringBuffer();
		//先取得總筆數 判斷是否需要拆多次執行
		sb.append("SELECT COUNT(1) TOTAL_COUNT FROM ( ");
		
		if (null != arg) {
			sb.append(genSqlV2024().toString());			
		} else {
			sb.append(genSql().toString());
		}
		sb.append(" ) ");
		
		//System.out.println("SQL:"+sb.toString());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> listCount = dam.exeQuery(queryCondition);	
		dataCount=Integer.parseInt(listCount.get(0).get("TOTAL_COUNT").toString());
		String[] order = genOrder();

		System.out.println("totalCount:"+dataCount);
		ResultSet rs = null;
		Connection con = gft.getConnection();
		Statement s = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
        s.setFetchSize(3000);
		
        try {
        	if (null != arg) {
        		rs = gft.getRS(genSqlV2024(), s);
    		} else {
    			rs = gft.getRS(genSql(), s);			
    		}
        	gft.writeFile(writeFileName, attached_name, path, order, rs, ",", false, true);
        	
        } finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
	    }
        
		//chk檔格式 
		StringBuffer chkLayout=new StringBuffer();
		chkLayout.append(gft.addBlankForString(jobName, 30));
		chkLayout.append(gft.addZeroForNum(String.valueOf(dataCount),15));
		chkLayout.append(sdfYYYYMMDD.format(new Date(new Date().getTime()-((long)1 * 24 * 60 * 60 * 1000))));
		gft.writeFileByText("Z"+writeFileName, attached_name, path, chkLayout,false);
		
		System.out.println("job  end");
	}

	//產檔所需要輸出的欄位
	public String[] genOrder() {
		String[] order = {"PERSON_ID","CHANGEDTTM","PERSON_DEGREE","FAMILY_DEGREE","REALVALUE","FINISHDTTM","OPEN_KIND" };
		return order;
	}
	
	//產檔需要的SQL
    public StringBuffer genSql(){
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * FROM ( ");
		sb.append(" SELECT rownum rowN,a.* from( ");
	    sb.append(" SELECT C.CUST_ID AS PERSON_ID , ");
	    sb.append(" TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') AS CHANGEDTTM , ");
	    sb.append(" C.VIP_DEGREE AS PERSON_DEGREE , ");
	    sb.append(" NVL(C.FAMILY_DEGREE, ' ') AS FAMILY_DEGREE, ");
	    sb.append(" ' ' AS REALVALUE , ");
	    sb.append(" TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') AS FINISHDTTM , ");
	    sb.append(" ' ' AS OPEN_KIND  ");
	    sb.append(" FROM TBCRM_CUST_MAST C WHERE VIP_DEGREE IN ( 'V', 'A', 'B' ) ");
		/*
		sb.append("SELECT G.CUST_ID AS PERSON_ID ");
		sb.append(",DECODE(G.CHG_DATE,NULL,' ',TO_CHAR(G.CHG_DATE,'YYYY-MM-DD HH24:MI:SS')) AS CHANGEDTTM ");
		sb.append(",DECODE(G.NEW_DEGREE,NULL,G.ORG_DEGREE,G.NEW_DEGREE) AS PERSON_DEGREE ");
		sb.append(",G.FAMILY_DEGREE AS FAMILY_DEGREE ");
		sb.append(",' ' AS REALVALUE ");
		sb.append(",DECODE(G.DUE_DATE,NULL,' ',TO_CHAR(G.DUE_DATE,'YYYY-MM-DD HH24:MI:SS')) AS FINISHDTTM "); 
		sb.append(",' ' AS OPEN_KIND "); 
		sb.append("FROM TBCRM_CUST_VIP_DEGREE_CHGLOG G ");
	    sb.append("WHERE (G.CUST_ID,G.SEQ) IN ( ");
	    sb.append("SELECT CUST_ID,MAX(SEQ) FROM TBCRM_CUST_VIP_DEGREE_CHGLOG ");
	    sb.append("GROUP BY CUST_ID ) ");
		*/
	    
		sb.append(" )a ");
		sb.append(" order by PERSON_ID ");
		sb.append(" ) ");
	    //sb.append(" where rownum <=1000 ");
		return sb;
	}
    
    //產檔需要的SQL
    public StringBuffer genSqlV2024() {
    	StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * FROM ( ");
		sb.append(" SELECT rownum rowN,a.* from( ");		
	    sb.append(" SELECT C.CUST_ID AS PERSON_ID , ");
	    sb.append(" TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') AS CHANGEDTTM , ");
	    sb.append(" C.VIP_DEGREE AS PERSON_DEGREE , ");
	    sb.append(" NVL(C.FAMILY_DEGREE, ' ') AS FAMILY_DEGREE, ");
	    sb.append(" ' ' AS REALVALUE , ");
	    sb.append(" TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') AS FINISHDTTM , ");
	    sb.append(" ' ' AS OPEN_KIND  ");
	    sb.append(" FROM TBCRM_CUST_MAST C WHERE VIP_DEGREE IN ( 'C', 'K', 'T', 'H') ");
		sb.append(" )a ");
		sb.append(" order by PERSON_ID ");
		sb.append(" ) ");
	    return sb;
    }	
	
}


