package com.systex.jbranch.fubon.bth;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 1. 讀取DB產出 .yyyymmdd
 * 2. 將.yyyymmdd 上傳FTP
 * 
 * @author 1600216
 * @date 2016/11/16
 *
 */
@Repository("profitdeg")
@Scope("prototype")
public class PROFITDEG extends BizLogic {
	 
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	private String format="Big5";
	SimpleDateFormat sdfYYYYMMDD  = new SimpleDateFormat("yyyyMMdd");
	
	public void createFileBth (Object body, IPrimitiveMap<?> header) throws JBranchException, IOException,Exception {
		GenFileTools gft=new GenFileTools();
		dam = this.getDataAccessManager();
		int dataCount;
		QueryConditionIF queryCondition = dam.getQueryCondition();
		//System.out.println("btpms413_out go!!");
		String  writeFileName="PROFIT_DEG";
		String  attached_name="TXT";
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
		gft.writeFile(writeFileName, attached_name, path, order, rs, "", false, false);
		
		//chk檔格式
		StringBuffer chkLayout=new StringBuffer();
		chkLayout.append(sdfYYYYMMDD.format(new Date()));
		chkLayout.append(gft.addZeroForNum(String.valueOf(dataCount),8));
		
		gft.writeFileByText("Z1"+writeFileName, attached_name, path,chkLayout,false);
		gft.writeFileByText("Z2"+writeFileName, attached_name, path,chkLayout,false);
		gft.writeFileByText("Z3"+writeFileName, attached_name, path,chkLayout,false);
		gft.writeFileByText("Z4"+writeFileName, attached_name, path,chkLayout,false);
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
		String[] order={"YEARMONTH","PERSON_ID","PROFIT_LST_Y_SEG","PROFIT_ONE_Y_SED","LST_Y"
				         ,"ONE_Y","PROFIT_BEST_Y_SEG","ONE_Y_FLOW_PCAS_TX","BTYPE" };
		return order;
	}
	//產檔需要的SQL
    public StringBuffer genSql()
	{
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT ");
		sb.append("RPAD(A.DATA_YEAR||A.DATA_MONTH,8,' ') AS YEARMONTH, ");
		sb.append("RPAD(A.CUST_ID,45,' ') AS PERSON_ID, ");
		sb.append("RPAD(NVL(A.PRFT_LAST_YEAR_NOTE, B.PRFT_LAST_YEAR_NOTE),3,' ') AS PROFIT_LST_Y_SEG, ");
		sb.append("RPAD(A.PRFT_NEWEST_YEAR_NOTE,3,' ') AS PROFIT_ONE_Y_SED, ");
		sb.append("RPAD(A.DATA_PERIOD_LAST_YEAR,20,' ') AS LST_Y, ");
		sb.append("RPAD(A.DATA_PERIOD_NEWEST_YEAR,20,' ') AS ONE_Y, ");
		sb.append("RPAD(A.BETTER_PRFT_NOTE,3,' ') AS PROFIT_BEST_Y_SEG, ");
		sb.append("TO_CHAR(ROUND(A.PRCH_AMT_LAST_YEAR*100,0),'FM0000000000000000') AS ONE_Y_FLOW_PCAS_TX, ");
		sb.append("CASE WHEN A.PRCH_AMT_LAST_YEAR >= 0 THEN '+' ELSE '-' END AS BTYPE ");
		sb.append("FROM TBCRM_CUST_CON_NOTE A, (SELECT * FROM TBCRM_CUST_CON_NOTE WHERE DATA_YEAR||DATA_MONTH=(SELECT DECODE(MAX(DATA_MONTH)-1, 0, MAX(DATA_YEAR)-1, MAX(DATA_YEAR))||LPAD(DECODE(MAX(DATA_MONTH)-1, 0, 12, MAX(DATA_MONTH)-1), 2, 0) FROM TBCRM_CUST_CON_NOTE )) B ");
		sb.append("WHERE 1=1 ");
		sb.append("AND A.CUST_ID = B.CUST_ID (+) ");
		sb.append("AND A.DATA_YEAR||A.DATA_MONTH=(SELECT MAX(DATA_YEAR||DATA_MONTH) FROM TBCRM_CUST_CON_NOTE ) ");
		sb.append("ORDER BY PERSON_ID ");
		return sb;
	}	
	

}