package com.systex.jbranch.fubon.bth.crm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.systex.jbranch.fubon.bth.GenFileTools;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("btcrm6106")
@Scope("prototype")
public class BTCRM6106 extends BizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void exportCustData(Object body, IPrimitiveMap<?> header) throws Exception {
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		dam = this.getDataAccessManager();
//		File file =new File("conf/context.xml");
//		//File file =new File("src/test/resources/META-INF/context.xml");本機
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        DocumentBuilder db = dbf.newDocumentBuilder();
//        Document document = db.parse(file);
//        NodeList nodeList = document.getElementsByTagName("Resource");
//        String driver = nodeList.item(0).getAttributes().getNamedItem("driverClassName").getNodeValue();
//        String url = nodeList.item(0).getAttributes().getNamedItem("url").getNodeValue();
//        String user = nodeList.item(0).getAttributes().getNamedItem("username").getNodeValue();
//        String password = nodeList.item(0).getAttributes().getNamedItem("password").getNodeValue();
    	Connection conn = null;
		
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";

		try{
//			   Class.forName(driver);
	           /*  
			   conn = 
	               DriverManager.getConnection(url, 
	                                  user, password);
	         */
				com.systex.jbranch.platform.common.dataaccess.datasource.DataSource ds = new
		                com.systex.jbranch.platform.common.dataaccess.datasource.DataSource();
		        SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
				DataSource dataSource = SessionFactoryUtils.getDataSource(sf);
				conn = dataSource.getConnection();
	            Statement s = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
	                    java.sql.ResultSet.CONCUR_READ_ONLY);
	            s.setFetchSize(3000);
	            StringBuffer tempSql = new StringBuffer();
	            StringBuffer sql = new StringBuffer();
         // PS_SA_CUST_INFO.yyyymmdd
            if(conn != null && !conn.isClosed()) {
             
               
        		sql.append("SELECT M.CUST_ID as PERSON_ID, case when M.BRA_NBR is null then '00999' else '00' || M.BRA_NBR end as BRA_NBR, NVL(M.AO_CODE, ' ') as AO_CODE, NVL(M.VIP_DEGREE, ' ') as VIP_DEGREE, NVL(M.BILL_TYPE, ' ') as BILL_TYPE, NVL(M.CUST_RISK_ATR, ' ') as PORTFOLIO_TYPE ");
        		sql.append("FROM (SELECt * FROM TBCRM_CUST_MAST WHERE BRA_NBR IS NOT NULL AND AO_CODE IS NOT NULL ) M ");
        		sql.append("LEFT JOIN VWORG_AO_INFO E ON M.AO_CODE = E.AO_CODE ");
        		sql.append("LEFT JOIN TBCRM_CUST_NOTE N ON  M.CUST_ID = N.CUST_ID ");
                ResultSet rs = s.executeQuery(sql.toString());
                String fileName = tempPath + "PS_SA_CUST_INFO." + sdf.format(new Date());
        		File file1 = new File(fileName);
        		BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1,false),"MS950"));
                int number = 0;
                StringBuffer temp = new StringBuffer("");
                while (rs.next()) {
    				
                	temp.append( "\"" + ObjectUtils.toString(rs.getObject("PERSON_ID")) + "\"");
                	temp.append( ",\"" + ObjectUtils.toString(rs.getObject("BRA_NBR")) + "\"");
                	temp.append( ",\"" + ObjectUtils.toString(rs.getObject("AO_CODE")) + "\"");
                	temp.append( ",\"" + ObjectUtils.toString(rs.getObject("VIP_DEGREE")) + "\"");
                	temp.append( ",\"" + ObjectUtils.toString(rs.getObject("BILL_TYPE")) + "\"");
                	temp.append( ",\"" + ObjectUtils.toString(rs.getObject("PORTFOLIO_TYPE")) + "\""+"\r\n");
    				
    		
    					
    				number++;
    				if(number%5000==0){
    					fw.write(temp.toString());
    					temp.setLength(0);
    					number=0;
    				}
    				
                }
                fw.write(temp.toString());
                fw.close();
            }
    		
    		
    		// ZPS_SA_CUST_INFO.yyyymmdd
    		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    		tempSql=sql;
    		sql = new StringBuffer();
    		sql.append("SELECT COUNT(1) AS 資料筆數, to_char(sysdate, 'YYYYMMDD') AS 資料日期 ");
    		sql.append("FROM ( ");
    		sql.append(tempSql.toString());
    		sql.append(" ) ");
    		queryCondition.setQueryString(sql.toString());
    		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
    		// gen csv
    		String fileName2 = tempPath + "ZPS_SA_CUST_INFO." + sdf.format(new Date());
    		File file2 = new File(fileName2);
    		BufferedWriter fw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2,false),"MS950"));
    		for(Map<String, Object> map : list2) {
    			String temp = "";
    			temp += String.format("%-30s", "PS_SA_CUST_INFO");
    			temp += String.format("%015d", new BigDecimal(ObjectUtils.toString(map.get("資料筆數"))).intValue());
    			temp += ObjectUtils.toString(map.get("資料日期"));
    			
    			fw2.write(temp+"\r\n");
    		}
    		fw2.close();
    		
    		// PS_SA_CUST_INFO_HS.yyyymmdd
    		 if(conn != null && !conn.isClosed()) {
    	            sql = new StringBuffer();
    	    		sql.append("SELECT M.CUST_ID, case when M.BRA_NBR is null then '00999' else '00' || M.BRA_NBR end as BRA_NBR, to_char(sysdate, 'YYYYMM') AS YEARMONTH, CASE WHEN M.AO_CODE IS NULL OR LENGTH(M.AO_CODE)=0 THEN ' ' ELSE M.AO_CODE END AO_CODE, CASE WHEN M.VIP_DEGREE IS NULL OR LENGTH(M.VIP_DEGREE)=0 THEN ' ' ELSE M.VIP_DEGREE END VIP_DEGREE, CASE WHEN M.BILL_TYPE IS NULL OR LENGTH(M.BILL_TYPE)=0 THEN ' ' ELSE M.BILL_TYPE END BILL_TYPE ");
    	    		sql.append("FROM TBCRM_CUST_MAST M WHERE M.BRA_NBR IS NOT NULL AND M.AO_CODE IS NOT NULL ");
    	            ResultSet rs = s.executeQuery(sql.toString());
    	            String fileName3 = tempPath + "PS_SA_CUST_INFO_HS." + sdf.format(new Date());
    	    		File file3 = new File(fileName3);
    	    		BufferedWriter fw3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file3,false),"MS950"));
    	     	    int number = 0;
    	            StringBuffer temp = new StringBuffer("");
    	            while (rs.next()) {
    					
    	            	temp.append( "\"" + ObjectUtils.toString(rs.getObject("CUST_ID")) + "\"");
    	            	temp.append( ",\"" + ObjectUtils.toString(rs.getObject("BRA_NBR")) + "\"");
    	            	temp.append( ",\"" + ObjectUtils.toString(rs.getObject("YEARMONTH")) + "\"");
    	            	temp.append( ",\"" + ObjectUtils.toString(rs.getObject("AO_CODE")) + "\"");
    	            	temp.append( ",\"" + ObjectUtils.toString(rs.getObject("VIP_DEGREE")) + "\"");
    	            	temp.append( ",\"" + ObjectUtils.toString(rs.getObject("BILL_TYPE")) + "\""+"\r\n");
    						
    					number++;
    					if(number%5000==0){
    						fw3.write(temp.toString());
    						temp.setLength(0);
    						number=0;
    					}
    					
    	            }
    	            fw3.write(temp.toString());
    	            fw3.close();
    	        }
    		
    		// ZPS_SA_CUST_INFO_HS.yyyymmdd
    		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    		tempSql=sql;
    		sql = new StringBuffer();
    		sql.append("SELECT COUNT(1) AS 資料筆數, to_char(sysdate, 'YYYYMMDD') AS 資料日期 ");
    		sql.append("FROM ( ");
    		sql.append(tempSql.toString());
    		sql.append(" ) ");
    		queryCondition.setQueryString(sql.toString());
    		List<Map<String, Object>> list4 = dam.exeQuery(queryCondition);
    		// gen csv
    		String fileName4 = tempPath + "ZPS_SA_CUST_INFO_HS." + sdf.format(new Date());
    		File file4 = new File(fileName4);
    		BufferedWriter fw4 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file4,false),"MS950"));
    		for(Map<String, Object> map : list4) {
    			String temp = "";
    			temp += String.format("%-30s", "PS_SA_CUST_INFO_HS");
    			temp += String.format("%015d", new BigDecimal(ObjectUtils.toString(map.get("資料筆數"))).intValue());
    			temp += ObjectUtils.toString(map.get("資料日期"));
    			
    			fw4.write(temp+"\r\n");
    		}
    		fw4.close();
    		
    		// PS_SA_CUST_CIF_HIS.yyyymmdd
    		 if(conn != null && !conn.isClosed()) {
    	         
    	             sql = new StringBuffer();
    	             sql.append("SELECT CUST_ID, case when BRA_NBR is null then ' ' else '00' || BRA_NBR end as BRA_NBR, DATA_YEAR||DATA_MONTH as YEARM, NVL(LDAY_AUM_AMT, 0) as LDAY_AUM_AMT,NVL(VIP_DEGREE, ' ') as VIP_DEGREE, NVL(SAV_AUM, 0) as SAV_AUM, NVL(CD_AUM, 0) as CD_AUM, NVL(CHK_AUM, 0) as CHK_AUM, NVL(FSAV_AUM, 0) as FSAV_AUM, NVL(FCD_AUM, 0) as FCD_AUM, NVL(FDCD_AUM, 0) as FDCD_AUM, NVL(RP_AUM, 0) as RP_AUM, NVL(BFOND_AUM, 0) as BFOND_AUM, NVL(DOM_FUND_AUM, 0) as DOM_FUND_AUM, NVL(OVS_COMM_AUM, 0) as OVS_COMM_AUM, NVL(OVS_STK_AUM, 0) as OVS_STK_AUM, NVL(OVS_FUND_AUM, 0) as OVS_FUND_AUM, NVL(INS_AUM, 0) as INS_AUM, NVL(LN_AUM, 0) as LN_AUM, to_char(LAST_DAY(to_date( DATA_YEAR||DATA_MONTH||'01' , 'YYYYMMDD')), 'YYYYMMDDHH24MISS') as DATA_DATE, NVL(CMA_AUM, 0) as CMA_AUM, NVL(ENTRUST_AUM, 0) as ENTRUST_AUM, NVL(PORTFOLIO_AUM, 0) as PORTFOLIO_AUM, NVL(MNY_TRST_AUM, 0) as MNY_TRST_AUM, NVL(GOLD_BOOK_AUM, 0) as GOLD_BOOK_AUM, 0 AS MON_AUM_20, 0 AS MON_AUM_21, 0 AS MON_AUM_22, 0 AS MON_AUM_23, 0 AS MON_AUM_24, 0 AS MON_AUM_25, 0 AS MON_AUM_26, 0 AS MON_AUM_27, 0 AS MON_AUM_28, 0 AS MON_AUM_29, 0 AS MON_AUM_30, 0 AS MON_AUM_31, 0 AS MON_AUM_32, 0 AS MON_AUM_33, 0 AS MON_AUM_34, 0 AS MON_AUM_35, 0 AS MON_AUM_36, 0 AS MON_AUM_37, 0 AS MON_AUM_38, 0 AS MON_AUM_39, 0 AS MON_AUM_40 ");
    	             sql.append("FROM TBCRM_CUST_AUM_MONTHLY_HIST  ");
    	             sql.append("WHERE BRA_NBR IS NOT NULL AND DATA_YEAR||DATA_MONTH=(SELECT max(DATA_YEAR||DATA_MONTH) from TBCRM_CUST_AUM_MONTHLY_HIST) ");
    	             logger.info("5566NO1");
    	             logger.info("-----------開始-------------");
    	             logger.info("20170613下午五點兩分");
    	             logger.info(sql.toString());
    	             logger.info("-----------結束-------------");
    	             ResultSet rs = s.executeQuery(sql.toString());
    	        	String fileName5 = tempPath + "PS_SA_CUST_CIF_HIS." + sdf.format(new Date());
    	    		File file5 = new File(fileName5);
    	    		BufferedWriter fw5 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file5,false),"MS950"));
    	 
    	            int number = 0;
    	            StringBuffer temp = new StringBuffer("");
    	            while (rs.next()) {
    					
    	            	temp.append("\"" + ObjectUtils.toString(rs.getObject("CUST_ID")) + "\"");
    	            	temp.append(",\"" + ObjectUtils.toString(rs.getObject("BRA_NBR")) + "\"");
    	            	temp.append(",\"" + ObjectUtils.toString(rs.getObject("YEARM")) + "\"");
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("LDAY_AUM_AMT")));
    	            	temp.append(",\"" + ObjectUtils.toString(rs.getObject("VIP_DEGREE")) + "\"");
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("SAV_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("CD_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("CHK_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("FSAV_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("FCD_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("FDCD_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("RP_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("BFOND_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("DOM_FUND_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("OVS_COMM_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("OVS_STK_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("OVS_FUND_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("INS_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("LN_AUM")));
    	            	temp.append(",\"" + ObjectUtils.toString(rs.getObject("DATA_DATE")) + "\"");
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("CMA_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("ENTRUST_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("PORTFOLIO_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MNY_TRST_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("GOLD_BOOK_AUM")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_20")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_21")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_22")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_23")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_24")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_25")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_26")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_27")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_28")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_29")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_30")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_31")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_32")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_33")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_34")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_35")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_36")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_37")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_38")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_39")));
    	            	temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_40"))+"\r\n");
    						
    					number++;
    					if(number%5000==0){
    						fw5.write(temp.toString());
    						temp.setLength(0);
    						number=0;
    					}
    					
    	            }
    	            fw5.write(temp.toString());
    	            fw5.close();
    	        }
    		
    		// ZPS_SA_CUST_CIF_HIS.yyyymmdd
    		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    		tempSql=sql;
    		sql = new StringBuffer();
    		sql.append("SELECT COUNT(1) AS 資料筆數, to_char(sysdate, 'YYYYMMDD') AS 資料日期 ");
    		sql.append("FROM ( ");
    		sql.append(tempSql.toString());
    		sql.append(" ) ");
    		queryCondition.setQueryString(sql.toString());
    		List<Map<String, Object>> list6 = dam.exeQuery(queryCondition);
    		// gen csv
    		String fileName6 = tempPath + "ZPS_SA_CUST_CIF_HIS." + sdf.format(new Date());
    		File file6 = new File(fileName6);
    		BufferedWriter fw6 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file6,false),"MS950"));
    		for(Map<String, Object> map : list6) {
    			String temp = "";
    			temp += String.format("%-30s", "PS_SA_CUST_CIF_HIS");
    			temp += String.format("%015d", new BigDecimal(ObjectUtils.toString(map.get("資料筆數"))).intValue());
    			temp += ObjectUtils.toString(map.get("資料日期"));
    			
    			fw6.write(temp+"\r\n");
    		}
    		fw6.close();
		}
//		   catch(ClassNotFoundException e) {
//			   logger.error(e.getMessage(), e);
//	        }
	        catch(SQLException e) { 
	        	logger.error(e.getMessage(), e);
	        } finally{
	        	conn.close();
	        }
	}
	
	public void exportCustVIPChangeData(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		GenFileTools gft = new GenFileTools();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date batchDate = new Date();
		//batchDate=sdf.parse("20171031");
        Date lastDate;
        String paramDate = sdf.format(batchDate);
		Calendar cal = Calendar.getInstance();
        //取得當月月底日
		cal.setTime(batchDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        lastDate = new Timestamp(cal.getTimeInMillis());
        
        //若當日為月底 則區間條件為當日與次月一日(月底結算之上下車的CHDATE為次月1日)
        if (sdf.format(batchDate).equals(sdf.format(lastDate))) {   
        	cal.add(Calendar.DATE, 1);
        	String nextDate = sdf.format(new Timestamp(cal.getTimeInMillis()));
        	paramDate = sdf.format(batchDate) + "-" + nextDate;
        }
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		//queryCondition.setQueryString(sql.toString());
		//List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		System.out.println("Start upcs4!!");
		String fileName = "CUST_DEGREE.TXT";
		String[] order = {"PERSON_ID","PERSON_DEGREE"};
		File file1 = new File(fileName);
		int dataCount = 0;
		ResultSet rs = null;
		Connection con = gft.getConnection();
		Statement s = null;
		try {
			s = con.createStatement(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
			s.setFetchSize(3000);
			rs = gft.getRS(getCustVIPChangeDataSql(paramDate),s);
			gft.writeFile(fileName, "",(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), order, rs, "", false, true);
			rs.beforeFirst();
			while (rs.next()) {
				dataCount++;
			}
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		}
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append(" SELECT TO_CHAR(SYSDATE, 'YYYYMMDD') DATA_DATE, COUNT(*) FROM ( ");
		sql.append(" SELECT CUST_ID FROM TBCRM_CUST_VIP_DEGREE_CHGLOG A WHERE RETURN_400_YN = 'U' ");
		sql.append(" AND (TRUNC(CHG_DATE) = TRUNC(SYSDATE) OR (CHG_DATE IS NULL AND TRUNC(APPL_DATE) = TRUNC(SYSDATE))) ");
		sql.append(" GROUP BY CUST_ID) ");
		
//		sql.append("SELECT to_char(sysdate, 'YYYYMMDD') AS DATA_DATE1, COUNT(1) AS CNT FROM TBCRM_CUST_VIP_DEGREE_CHGLOG ");
//		sql.append("WHERE RETURN_400_YN = 'U' ");
//		sql.append("AND ((TRUNC(CHG_DATE) = TRUNC(SYSDATE)) OR ( CHG_DATE IS NULL AND TRUNC(APPL_DATE) = TRUNC(SYSDATE))) ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		File file2 = new File(tempPath + "Z1CUST_DEGREE.TXT");
		BufferedWriter fw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2,false),"MS950"));		
		File file3 = new File(tempPath + "Z2CUST_DEGREE.TXT");
		BufferedWriter fw3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file3,false),"MS950"));		
		File file4 = new File(tempPath + "Z3CUST_DEGREE.TXT");
		BufferedWriter fw4 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file4,false),"MS950"));		
		File file5 = new File(tempPath + "Z4CUST_DEGREE.TXT");
		BufferedWriter fw5 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file5,false),"MS950"));		
		String temp = "";
		temp += ObjectUtils.toString(paramDate.split("-")[0]);
		temp += LenString(ObjectUtils.toString(dataCount), 8);
		fw2.write(temp+"\r\n");
		fw3.write(temp+"\r\n");
		fw4.write(temp+"\r\n");
		fw5.write(temp+"\r\n");
		fw2.close();fw3.close();fw4.close();fw5.close();
        // 產檔後，更新狀態
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(getCustVIPChangeDataUpdateSql(paramDate).toString());
		dam.exeUpdate(queryCondition);
	}
	
	private StringBuffer getCustVIPChangeDataUpdateSql(String paramDate) {
		StringBuffer sb=new StringBuffer();
		String params="= TRUNC(SYSDATE) ";
		if (paramDate != null) {
			if (paramDate.split("-").length == 2) {
				params="  BETWEEN TO_DATE('" + paramDate.split("-")[0] + "','YYYYMMDD') AND TO_DATE('" + paramDate.split("-")[1] + "','YYYYMMDD') ";
				
			} else {
				params="= TO_DATE('"+paramDate+"','YYYYMMDD') ";	
			}
		}
		sb.append("UPDATE TBCRM_CUST_VIP_DEGREE_CHGLOG SET RETURN_400_YN = 'Y' WHERE RETURN_400_YN = 'U' AND ((TRUNC(CHG_DATE) ");
		sb.append(params);
		sb.append(") ");
		sb.append("OR ( CHG_DATE IS NULL AND TRUNC(APPL_DATE) ");
		sb.append(params);
		sb.append(")) ");
		System.out.println("updateSql:"+sb.toString());
		return sb;
	}
	
	private StringBuffer getCustVIPChangeDataSql(String paramDate) {
		StringBuffer sb = new StringBuffer();
		String params = " = TRUNC(SYSDATE) ";
		if (paramDate != null) {
			if (paramDate.split("-").length == 2) {
				params = " BETWEEN TO_DATE('" + paramDate.split("-")[0] + "','YYYYMMDD') AND TO_DATE('" + paramDate.split("-")[1] + "','YYYYMMDD') ";
			} else {
				params = " = TO_DATE('" + paramDate + "', 'YYYYMMDD') ";	
			}
		}
		
		sb.append(" WITH BASE AS ( ");
		sb.append(" SELECT CUST_ID FROM TBCRM_CUST_VIP_DEGREE_CHGLOG A WHERE RETURN_400_YN = 'U' ");
		sb.append(" AND (TRUNC(CHG_DATE) ");
		sb.append(params);
		sb.append(" OR (CHG_DATE IS NULL AND TRUNC(APPL_DATE) ");
		sb.append(params);
		sb.append(" )) GROUP BY CUST_ID), ");
		sb.append(" AND (TRUNC(CHG_DATE) ");
		sb.append(params);
		sb.append(" OR (CHG_DATE IS NULL AND TRUNC(APPL_DATE) ");
		sb.append(params);
		sb.append(" )) GROUP BY CUST_ID), ");
		sb.append(" PERSON AS (SELECT A.CUST_ID, NVL(A.NEW_DEGREE, B.VIP_DEGREE) P_DEGREE, ");
		sb.append(" DECODE(NVL(A.NEW_DEGREE, B.VIP_DEGREE), NULL, 0, 'C', 1, 'K', 2, 'T', 3, 'H', 4) P_VALUE ");
		sb.append(" FROM TBCRM_CUST_VIP_DEGREE_CHGLOG A, TBCRM_CUST_MAST B ");
		sb.append(" WHERE A.CUST_ID = B.CUST_ID ");
		sb.append(" AND RETURN_400_YN = 'U' AND (TRUNC(CHG_DATE)  ");
		sb.append(params);
		sb.append(" OR (CHG_DATE IS NULL AND TRUNC(APPL_DATE) ");
		sb.append(params);
		sb.append(" )) ");
		sb.append(" AND A.CHG_TYPE = '1'), ");
		sb.append(" FAMILY AS (SELECT A.CUST_ID, NVL(A.NEW_DEGREE, B.FAMILY_DEGREE) F_DEGREE, ");
		sb.append(" DECODE(NVL(A.NEW_DEGREE, B.FAMILY_DEGREE), NULL, 0, 'C', 1, 'K', 2, 'T', 3, 'H', 4) F_VALUE ");
		sb.append(" FROM TBCRM_CUST_VIP_DEGREE_CHGLOG A, TBCRM_CUST_MAST B ");
		sb.append(" WHERE A.CUST_ID = B.CUST_ID ");
		sb.append(" AND RETURN_400_YN = 'U' AND (TRUNC(CHG_DATE) ");
		sb.append(params);
		sb.append(" OR (CHG_DATE IS NULL AND TRUNC(APPL_DATE) ");
		sb.append(params);
		sb.append(" )) ");
		sb.append(" AND A.CHG_TYPE = '3') ");
		sb.append(" SELECT B.CUST_ID, CASE WHEN NVL(P_VALUE, 0) >= NVL(F_VALUE, 0) THEN P_DEGREE ELSE F_DEGREE END BEST_DEGREE ");
		sb.append(" FROM BASE B ");
		sb.append(" LEFT JOIN PERSON P ON B.CUST_ID = P.CUST_ID ");
		sb.append(" LEFT JOIN FAMILY F ON B.CUST_ID = F.CUST_ID ");
		sb.append(" ORDER BY B.CUST_ID ");
		System.out.println(sb.toString());
		
		return sb;
	}
	
//	private StringBuffer getCustVIPChangeDataSql() {
//		StringBuffer sb = new StringBuffer();
//		sb.append(" WITH BASE AS ( ");
//		sb.append(" SELECT CUST_ID FROM TBCRM_CUST_VIP_DEGREE_CHGLOG A WHERE RETURN_400_YN = 'U' ");
//		sb.append(" AND (TRUNC(CHG_DATE) = TRUNC(SYSDATE) OR (CHG_DATE IS NULL AND TRUNC(APPL_DATE) = TRUNC(SYSDATE))) GROUP BY CUST_ID), ");
//		sb.append(" PERSON AS (SELECT A.CUST_ID, NVL(A.NEW_DEGREE, B.VIP_DEGREE) P_DEGREE, ");
//		sb.append(" DECODE(NVL(A.NEW_DEGREE, B.VIP_DEGREE), NULL, 0, 'C', 1, 'K', 2, 'T', 3, 'H', 4) P_VALUE ");
//		sb.append(" FROM TBCRM_CUST_VIP_DEGREE_CHGLOG A, TBCRM_CUST_MAST B ");
//		sb.append(" WHERE A.CUST_ID = B.CUST_ID ");
//		sb.append(" AND RETURN_400_YN = 'U' AND (TRUNC(CHG_DATE) = TRUNC(SYSDATE) OR (CHG_DATE IS NULL AND TRUNC(APPL_DATE) = TRUNC(SYSDATE))) ");
//		sb.append(" AND A.CHG_TYPE = '1'), ");
//		sb.append(" FAMILY AS (SELECT A.CUST_ID, NVL(A.NEW_DEGREE, B.FAMILY_DEGREE) F_DEGREE, ");
//		sb.append(" DECODE(NVL(A.NEW_DEGREE, B.FAMILY_DEGREE), NULL, 0, 'C', 1, 'K', 2, 'T', 3, 'H', 4) F_VALUE ");
//		sb.append(" FROM TBCRM_CUST_VIP_DEGREE_CHGLOG A, TBCRM_CUST_MAST B ");
//		sb.append(" WHERE A.CUST_ID = B.CUST_ID ");
//		sb.append(" AND RETURN_400_YN = 'U' AND (TRUNC(CHG_DATE) = TRUNC(SYSDATE) OR (CHG_DATE IS NULL AND TRUNC(APPL_DATE) = TRUNC(SYSDATE))) ");
//		sb.append(" AND A.CHG_TYPE = '3') ");
//		sb.append(" SELECT B.CUST_ID, CASE WHEN NVL(P_VALUE, 0) >= NVL(F_VALUE, 0) THEN P_DEGREE ELSE F_DEGREE END BEST_DEGREE ");
//		sb.append(" FROM BASE B ");
//		sb.append(" LEFT JOIN PERSON P ON B.CUST_ID = P.CUST_ID ");
//		sb.append(" LEFT JOIN FAMILY F ON B.CUST_ID = F.CUST_ID ");
//		sb.append(" ORDER BY B.CUST_ID ");
//		System.out.println(sb.toString());
//		return sb;
//	}
	
	private String LenString(String inputStr, int StrLen) {
		String returnString = "";
	    int LenCtrl = 0;
	    int inputStrLen;
	    int i = 0;
	    
	    if (StringUtils.isBlank(inputStr)) {
	    	inputStrLen = 0;
	    	inputStr = "";
	    } else
	    	inputStrLen = inputStr.length();
	    
	    while (inputStrLen > i) {
	    	//判斷該字元是否為雙位元資料
	    	if (Integer.parseInt(Integer.toString(inputStr.charAt(i), 16), 16) >= 128) {
	    		if ( (StrLen - LenCtrl - 1) >= 2) {
	    			returnString = returnString + inputStr.substring(i, i + 1);
	    			LenCtrl = LenCtrl + 2;
	    		}
	        }
	    	else {
	    		if ( (StrLen - LenCtrl) >= 1 ) {
	    			returnString = returnString + inputStr.substring(i, i + 1);
	    			LenCtrl++;
	    		}
	        }
	    	i++;
	    }
	    // 現有字串以後補空白
	    while(LenCtrl < StrLen) {
	    	returnString = "0" + returnString;
	    	LenCtrl++;
	    }
	    return returnString;
	}
}