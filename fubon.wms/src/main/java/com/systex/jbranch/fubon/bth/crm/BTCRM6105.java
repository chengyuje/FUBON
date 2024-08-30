package com.systex.jbranch.fubon.bth.crm;

import com.systex.jbranch.fubon.bth.GenFileTools;
import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSFTPVO;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
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
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Component("btcrm6105")
@Scope("prototype")
public class BTCRM6105 extends BizLogic {
    private AuditLogUtil audit = null;
    private DataAccessManager dam = null;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    public static Calendar calendar = Calendar.getInstance();

    public void PS_SA_CUST_INFO(Object body) throws Exception {

        // 取得傳入參數
        @SuppressWarnings("unchecked")
        Map < String, Object > inputMap = (Map < String, Object > ) body;
        @SuppressWarnings("unchecked")
        Map < String, Object > jobParam = (Map < String, Object > ) inputMap
            .get(SchedulerHelper.JOB_PARAMETER_KEY);


        dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        Connection conn = null;
        String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
//        String data = "PS_SA_CUST_INFO." + String.valueOf(getThisYear()) + month(getThisMonth() - 1) + "01";
//        String valid = "ZPS_SA_CUST_INFO." + String.valueOf(getThisYear()) + month(getThisMonth() - 1) + "01";
        String data = "CUST_INFO" ;
        String valid = "ZCUST_INFO" ;

        try {
            //Class.forName(getDriverName(body));
            GenFileTools gft=new GenFileTools();  
            conn =
            		gft.getConnection();
            Statement s = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                java.sql.ResultSet.CONCUR_READ_ONLY);
            s.setFetchSize(3000);
            StringBuffer tempSql = new StringBuffer();
            StringBuffer sql = new StringBuffer();

            // PS_SA_CUST_INFO.yyyymmdd
            if (conn != null && !conn.isClosed()) {


                sql.append("SELECT M.CUST_ID as PERSON_ID, NVL(M.BRA_NBR, '999') as BRA_NBR, NVL(M.AO_CODE, ' ') as AO_CODE, NVL(M.VIP_DEGREE, ' ') as VIP_DEGREE,"
                		+ " NVL(M.BILL_TYPE, ' ') as BILL_TYPE, NVL(M.CUST_RISK_ATR, ' ') as PORTFOLIO_TYPE ");
                sql.append("FROM (SELECt * FROM TBCRM_CUST_MAST WHERE BRA_NBR IS NOT NULL AND AO_CODE IS NOT NULL ) M ");
                sql.append("LEFT JOIN VWORG_AO_INFO E ON M.AO_CODE = E.AO_CODE ");
                sql.append("LEFT JOIN TBCRM_CUST_NOTE N ON  M.CUST_ID = N.CUST_ID ");
                ResultSet rs = s.executeQuery(sql.toString());
                String fileName = tempPath;
        		File file = new File(fileName + data);
        		BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false),"MS950"));
                int number = 0;
                StringBuffer temp = new StringBuffer("");
                while (rs.next()) {

                    temp.append("\"" + ObjectUtils.toString(rs.getObject("PERSON_ID")) + "\"");
                    temp.append(",\"" + ObjectUtils.toString(rs.getObject("BRA_NBR")) + "\"");
                    temp.append(",\"" + ObjectUtils.toString(rs.getObject("AO_CODE")) + "\"");
                    temp.append(",\"" + ObjectUtils.toString(rs.getObject("VIP_DEGREE")) + "\"");
                    temp.append(",\"" + ObjectUtils.toString(rs.getObject("BILL_TYPE")) + "\"");
                    temp.append(",\"" + ObjectUtils.toString(rs.getObject("PORTFOLIO_TYPE")) + "\"" + "\r\n");

                    number++;
                    if (number % 5000 == 0) {
                        fw.write(temp.toString());
                        temp.setLength(0);
                        number = 0;
                    }

                }
                fw.write(temp.toString());
                fw.close();
            }
            // ZPS_SA_CUST_INFO.yyyymmdd
            QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            tempSql = sql;
            sql = new StringBuffer();
            sql.append("SELECT COUNT(1) AS 資料筆數, to_char(sysdate-1, 'YYYYMMDD') AS 資料日期 ");
            sql.append("FROM ( ");
            sql.append(tempSql.toString());
            sql.append(" ) ");
            queryCondition.setQueryString(sql.toString());
            List < Map < String, Object >> list2 = dam.exeQuery(queryCondition);
            // gen csv
            String fileName2 = tempPath;
    		File file2 = new File(fileName2 + valid);
    		BufferedWriter fw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2,false),"MS950"));

            for (Map < String, Object > map: list2) {
                String temp = "";
                temp += String.format("%-30s", "PS_SA_CUST_INFO");
                temp += String.format("%015d", new BigDecimal(ObjectUtils.toString(map.get("資料筆數"))).intValue());
                temp += ObjectUtils.toString(map.get("資料日期"));

                fw2.write(temp + "\r\n");
            }
            fw2.close();
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            //設定特定檔名
            //setFileName("CRM6105_12_exCUST_01", data);
            //setFileName("CRM6105_12_exCUST_02", valid);

            conn.close();
        }




    }

    public void PS_SA_CUST_INFO_HS(Object body) throws Exception {

        // 取得傳入參數
        @SuppressWarnings("unchecked")
        Map < String, Object > inputMap = (Map < String, Object > ) body;
        @SuppressWarnings("unchecked")
        Map < String, Object > jobParam = (Map < String, Object > ) inputMap
            .get(SchedulerHelper.JOB_PARAMETER_KEY);


        dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        Connection conn = null;
        String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
//        String data = "PS_SA_CUST_INFO_HS." + String.valueOf(getThisYear()) + month(getThisMonth()) + day(getThisDay());
//        String valid = "ZPS_SA_CUST_INFO_HS." + String.valueOf(getThisYear()) + month(getThisMonth()) + day(getThisDay());
        String data = "CUST_INFO_HS"; 
        String valid = "ZCUST_INFO_HS";

        try {
            //Class.forName(getDriverName(body));
            GenFileTools gft=new GenFileTools();  
            conn =
            		gft.getConnection();
            Statement s = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                java.sql.ResultSet.CONCUR_READ_ONLY);
            s.setFetchSize(3000);
            StringBuffer tempSql = new StringBuffer();
            StringBuffer sql = new StringBuffer();

            // PS_SA_CUST_INFO_HS.yyyymmdd
            if (conn != null && !conn.isClosed()) {
                sql = new StringBuffer();
                sql.append("SELECT M.CUST_ID, NVL(M.BRA_NBR, '999') as BRA_NBR, to_char(sysdate-1, 'YYYYMM') AS YEARMONTH, "
                		+ "CASE WHEN M.AO_CODE IS NULL OR LENGTH(M.AO_CODE)=0 THEN ' ' ELSE M.AO_CODE END AO_CODE, "
                		+ "CASE WHEN M.VIP_DEGREE IS NULL OR LENGTH(M.VIP_DEGREE)=0 THEN ' ' ELSE M.VIP_DEGREE END VIP_DEGREE, "
                		+ "CASE WHEN M.BILL_TYPE IS NULL OR LENGTH(M.BILL_TYPE)=0 THEN ' ' ELSE M.BILL_TYPE END BILL_TYPE ");
                sql.append("FROM TBCRM_CUST_MAST M WHERE M.BRA_NBR IS NOT NULL AND M.AO_CODE IS NOT NULL ");
                ResultSet rs = s.executeQuery(sql.toString());
                String fileName3 = tempPath;
        		File file3 = new File(fileName3 + data);
        		BufferedWriter fw3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file3,false),"MS950"));
 
                int number = 0;
                StringBuffer temp = new StringBuffer("");
                while (rs.next()) {

                    temp.append("\"" + ObjectUtils.toString(rs.getObject("CUST_ID")) + "\"");
                    temp.append(",\"" + ObjectUtils.toString(rs.getObject("BRA_NBR")) + "\"");
                    temp.append(",\"" + ObjectUtils.toString(rs.getObject("YEARMONTH")) + "\"");
                    temp.append(",\"" + ObjectUtils.toString(rs.getObject("AO_CODE")) + "\"");
                    temp.append(",\"" + ObjectUtils.toString(rs.getObject("VIP_DEGREE")) + "\"");
                    temp.append(",\"" + ObjectUtils.toString(rs.getObject("BILL_TYPE")) + "\"" + "\r\n");

                    number++;
                    if (number % 5000 == 0) {
                        fw3.write(temp.toString());
                        temp.setLength(0);
                        number = 0;
                    }

                }
                fw3.write(temp.toString());
                fw3.close();
            }

            // ZPS_SA_CUST_INFO_HS.yyyymmdd
            QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            tempSql = sql;
            sql = new StringBuffer();
            sql.append("SELECT COUNT(1) AS 資料筆數, to_char(sysdate-1, 'YYYYMMDD') AS 資料日期 ");
            sql.append("FROM ( ");
            sql.append(tempSql.toString());
            sql.append(" ) ");
            queryCondition.setQueryString(sql.toString());
            List < Map < String, Object >> list4 = dam.exeQuery(queryCondition);
            // gen csv
            String fileName4 = tempPath;

    		File file4 = new File(fileName4 + valid);
    		BufferedWriter fw4 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file4,false),"MS950"));

            for (Map < String, Object > map: list4) {
                String temp = "";
                temp += String.format("%-30s", "PS_SA_CUST_INFO_HS");
                temp += String.format("%015d", new BigDecimal(ObjectUtils.toString(map.get("資料筆數"))).intValue());
                temp += ObjectUtils.toString(map.get("資料日期"));

                fw4.write(temp + "\r\n");
            }
            fw4.close();

        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {

            //設定特定檔名
            //setFileName("CRM6105_12_exCUST_03", data);
            //setFileName("CRM6105_12_exCUST_04", valid);
            conn.close();
        }




    }

    public void PS_SA_CUST_CIF_HIS(Object body) throws Exception {
        // 取得傳入參數
        @SuppressWarnings("unchecked")
        Map < String, Object > inputMap = (Map < String, Object > ) body;
        @SuppressWarnings("unchecked")
        Map < String, Object > jobParam = (Map < String, Object > ) inputMap
            .get(SchedulerHelper.JOB_PARAMETER_KEY);


        dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        Connection conn = null;
        String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";

        String data = "PS_SA_CUST_CIF_HIS." + getFirstDayOfMonth();
        String valid = "ZPS_SA_CUST_CIF_HIS." + getFirstDayOfMonth();
//        String data = "CUST_CIF_HIS";
//        String valid = "ZCUST_CIF_HIS";
        try {
            //Class.forName(getDriverName(body));
            GenFileTools gft=new GenFileTools();  
            conn =
            		gft.getConnection();
            Statement s = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                java.sql.ResultSet.CONCUR_READ_ONLY);
            s.setFetchSize(3000);
            StringBuffer tempSql = new StringBuffer();
            StringBuffer sql = new StringBuffer();
            QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            sql.append("SELECT COUNT(1) TOTAL_COUNT FROM TBCRM_FA_ACT_CUST_BRN_SG WHERE DATA_YEARMONTH=TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMM') ");
            queryCondition.setQueryString(sql.toString());
            List < Map < String, Object >> listCount = dam.exeQuery(queryCondition);
            int dataCount=Integer.parseInt(listCount.get(0).get("TOTAL_COUNT").toString());
            //檔案有來才產檔 否則不執行
            if(dataCount>0)
            {
            // PS_SA_CUST_CIF_HIS.yyyymmdd
            if (conn != null && !conn.isClosed()) {
                System.out.println("newSQl");
                sql = new StringBuffer();
                sql.append("SELECT ");
                sql.append("A.PERSON_ID AS CUST_ID, ");
                sql.append("NVL(A.BRANCH_NBR, ' ') as BRA_NBR, ");
                sql.append("TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMM') AS YEARM, ");
                sql.append("NVL(TOTAL_ASSET_BAL,0) AS LDAY_AUM_AMT, ");
                sql.append("' ' AS VIP_DEGREE, ");
                sql.append("NVL(MON_ACCT_SAVING_AU,0) AS SAV_AUM, ");
                sql.append("NVL(MON_ATERM_DEP_AUM, 0) as CD_AUM, ");
                sql.append("NVL(MON_ACCT_CHECK_AUM, 0) as CHK_AUM, ");
                sql.append("NVL(MON_ASAVING_CUR_AU, 0) as FSAV_AUM, ");
                sql.append("NVL(MON_ACOMP_CUR_AUM, 0) as FCD_AUM, ");
                sql.append("NVL(MON_TR_DEP_CUR_AUM, 0) as FDCD_AUM, ");
                sql.append("NVL(MON_ACCT_RP_AUM, 0) as RP_AUM, ");
                sql.append("NVL(MON_FUND_BD_TP_AUM, 0) as BFOND_AUM, ");
                sql.append("NVL(MON_FUND_OTHER_AUM, 0) as DOM_FUND_AUM, ");
                sql.append("NVL(MON_STRUCTURE_AUM, 0) as OVS_COMM_AUM, ");
                sql.append("NVL(MON_STOCK_FORE_AUM, 0) as OVS_STK_AUM, ");
                sql.append("NVL(MON_FUND_FORE_AUM, 0) as OVS_FUND_AUM, ");
                sql.append("NVL(MON_INSURANCE_AUM, 0) as INS_AUM, ");
                sql.append("NVL(MON_ACCT_LOAN_AUM, 0) as LN_AUM, ");
                sql.append("to_char(LAST_DAY(ADD_MONTHS(SYSDATE,-1)), 'YYYYMMDDHH24MISS') as DATA_DATE, ");
                sql.append("NVL(MON_TMS_AUM, 0) as CMA_AUM, ");
                sql.append("NVL(MON_AUM_16, 0) as ENTRUST_AUM, ");
                sql.append("NVL(MON_AUM_17, 0) as PORTFOLIO_AUM, ");
                sql.append("NVL(MON_AUM_18, 0) as MNY_TRST_AUM, ");
                sql.append("NVL(MON_AUM_19, 0) as GOLD_BOOK_AUM, ");
                sql.append("0 AS MON_AUM_20, ");
                sql.append("0 AS MON_AUM_21, ");
                sql.append("0 AS MON_AUM_22, ");
                sql.append("0 AS MON_AUM_23, ");
                sql.append("0 AS MON_AUM_24, ");
                sql.append("0 AS MON_AUM_25, ");
                sql.append("0 AS MON_AUM_26, ");
                sql.append("0 AS MON_AUM_27, ");
                sql.append("0 AS MON_AUM_28, ");
                sql.append("0 AS MON_AUM_29, ");
                sql.append("0 AS MON_AUM_30, ");
                sql.append("0 AS MON_AUM_31, ");
                sql.append("0 AS MON_AUM_32, ");
                sql.append("0 AS MON_AUM_33, ");
                sql.append("0 AS MON_AUM_34, ");
                sql.append("0 AS MON_AUM_35, ");
                sql.append("0 AS MON_AUM_36, ");
                sql.append("0 AS MON_AUM_37, ");
                sql.append("0 AS MON_AUM_38, ");
                sql.append("0 AS MON_AUM_39, ");
                sql.append("0 AS MON_AUM_40  ");         
                sql.append("FROM TBCRM_PS_SA_CUST_CIF_SG A ");
                sql.append("LEFT JOIN ");
                sql.append("(SELECT DISTINCT CUST_ID FROM TBPMS_CUST_AO_ME WHERE 1=1 ");
                sql.append("AND YEARMONTH=TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMM') ");
                sql.append("AND NVL(TRIM(AO_CODE),'000') <>'000') B ");
                sql.append("ON A.PERSON_ID=B.CUST_ID ");
                sql.append("LEFT JOIN (SELECT * FROM TBCRM_FA_ACT_CUST_BRN_SG WHERE DATA_YEARMONTH=TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMM')) C ");
                sql.append("ON  A.PERSON_ID=C.CUST_ID ");
                sql.append("AND A.BRANCH_NBR=C.BRA_NBR ");
                //sql.append("--LEFT JOIN (SELECT CUST_ID,BRA_NBR,VIP_DEGREE FROM TBCRM_CUST_MAST WHERE AO_CODE IS NOT NULL ) M ");
                //sql.append("--ON A.PERSON_ID=M.CUST_ID AND A.BRANCH_NBR=M.BRA_NBR ");
                sql.append("WHERE ");
                //sql.append("--排除 TBCRM_FA_ACT_CUST_BRN 沒有 而且AUM=0的資料 \n");
                sql.append("NOT(C.CUST_ID IS NULL AND A.TOTAL_ASSET_BAL=0 AND B.CUST_ID IS NOT NULL) ");
                //sql.append(" AND ROWNUM <=100 ");
                sql.append("UNION ALL ");
                //sql.append("--TBCRM_FA_ACT_CUST_BRN 有但TBCRM_PS_SA_CUST_CIF_SG 沒有的資料 \n");
                sql.append("SELECT ");
                sql.append("C.CUST_ID AS CUST_ID, ");
                sql.append("NVL(C.BRA_NBR, ' ') as BRA_NBR, ");
                sql.append("TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMM') AS YEARM, ");
                sql.append("NVL(TOTAL_ASSET_BAL,0) AS LDAY_AUM_AMT, ");
                sql.append("' ' VIP_DEGREE, ");
                sql.append("NVL(MON_ACCT_SAVING_AU,0) AS SAV_AUM, ");
                sql.append("NVL(MON_ATERM_DEP_AUM, 0) as CD_AUM, ");
                sql.append("NVL(MON_ACCT_CHECK_AUM, 0) as CHK_AUM, ");
                sql.append("NVL(MON_ASAVING_CUR_AU, 0) as FSAV_AUM, ");
                sql.append("NVL(MON_ACOMP_CUR_AUM, 0) as FCD_AUM, ");
                sql.append("NVL(MON_TR_DEP_CUR_AUM, 0) as FDCD_AUM, ");
                sql.append("NVL(MON_ACCT_RP_AUM, 0) as RP_AUM, ");
                sql.append("NVL(MON_FUND_BD_TP_AUM, 0) as BFOND_AUM, ");
                sql.append("NVL(MON_FUND_OTHER_AUM, 0) as DOM_FUND_AUM, ");
                sql.append("NVL(MON_STRUCTURE_AUM, 0) as OVS_COMM_AUM, ");
                sql.append("NVL(MON_STOCK_FORE_AUM, 0) as OVS_STK_AUM, ");
                sql.append("NVL(MON_FUND_FORE_AUM, 0) as OVS_FUND_AUM, ");
                sql.append("NVL(MON_INSURANCE_AUM, 0) as INS_AUM, ");
                sql.append("NVL(MON_ACCT_LOAN_AUM, 0) as LN_AUM, ");
                sql.append("to_char(LAST_DAY(ADD_MONTHS(SYSDATE,-1)), 'YYYYMMDDHH24MISS') as DATA_DATE, ");
                sql.append("NVL(MON_TMS_AUM, 0) as CMA_AUM, ");
                sql.append("NVL(MON_AUM_16, 0) as ENTRUST_AUM, ");
                sql.append("NVL(MON_AUM_17, 0) as PORTFOLIO_AUM, ");
                sql.append("NVL(MON_AUM_18, 0) as MNY_TRST_AUM, ");
                sql.append("NVL(MON_AUM_19, 0) as GOLD_BOOK_AUM, ");
                sql.append("0 AS MON_AUM_20, ");
                sql.append("0 AS MON_AUM_21, ");
                sql.append("0 AS MON_AUM_22, ");
                sql.append("0 AS MON_AUM_23, ");
                sql.append("0 AS MON_AUM_24, ");
                sql.append("0 AS MON_AUM_25, ");
                sql.append("0 AS MON_AUM_26, ");
                sql.append("0 AS MON_AUM_27, ");
                sql.append("0 AS MON_AUM_28, ");
                sql.append("0 AS MON_AUM_29, ");
                sql.append("0 AS MON_AUM_30, ");
                sql.append("0 AS MON_AUM_31, ");
                sql.append("0 AS MON_AUM_32, ");
                sql.append("0 AS MON_AUM_33, ");
                sql.append("0 AS MON_AUM_34, ");
                sql.append("0 AS MON_AUM_35, ");
                sql.append("0 AS MON_AUM_36, ");
                sql.append("0 AS MON_AUM_37, ");
                sql.append("0 AS MON_AUM_38, ");
                sql.append("0 AS MON_AUM_39, ");
                sql.append("0 AS MON_AUM_40 ");              
                sql.append("FROM (SELECT * FROM TBCRM_FA_ACT_CUST_BRN_SG C WHERE ");
                sql.append("DATA_YEARMONTH=TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMM') ");
                sql.append("AND NOT EXISTS(SELECT 1 FROM TBCRM_PS_SA_CUST_CIF_SG A WHERE ");
                sql.append("               A.PERSON_ID=C.CUST_ID ");
                sql.append("               AND A.BRANCH_NBR=C.BRA_NBR  ");
                sql.append("              ) ");
                sql.append(") C ");
                sql.append("LEFT JOIN (SELECT * FROM TBCRM_PS_SA_CUST_CIF_SG WHERE 1=2) A ");
                sql.append("ON  A.PERSON_ID=C.CUST_ID ");
                sql.append("AND A.BRANCH_NBR=C.BRA_NBR  ");
                //sql.append(" AND ROWNUM <=100 ");
                
                ResultSet rs = s.executeQuery(sql.toString());
                String fileName5 = tempPath;

        		File file5 = new File(fileName5 + data);
        		BufferedWriter fw5 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file5,false),"MS950"));
                int number = 0;
                StringBuffer temp = new StringBuffer("");
                System.out.println("WriteFile!");
               
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
                    temp.append("," + ObjectUtils.toString(rs.getObject("MON_AUM_40")) + "\r\n");

                    number++;
                    if (number % 5000 == 0) {
                        fw5.write(temp.toString());
                        temp.setLength(0);
                        number = 0;
                    }

                }
                
                fw5.write(temp.toString());
                fw5.close();
            }
            

            // ZPS_SA_CUST_CIF_HIS.yyyymmdd
            queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            tempSql = sql;
            sql = new StringBuffer();
            sql.append("SELECT COUNT(1) AS 資料筆數, TO_CHAR(LAST_DAY(ADD_MONTHS(SYSDATE, -2)) + 1,'YYYYMMDD') AS 資料日期 ");
            sql.append("FROM ( ");
            sql.append(tempSql.toString());
            sql.append(" ) ");
            System.out.println("count sql:"+sql.toString());
            queryCondition.setQueryString(sql.toString());
            List < Map < String, Object >> list6 = dam.exeQuery(queryCondition);
            // gen csv
            String fileName6 = tempPath;
            System.out.println("zfileName:"+fileName6);
    		File file6 = new File(fileName6 + valid);
    		BufferedWriter fw6 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file6,false),"MS950"));

            for (Map < String, Object > map: list6) {
                String temp = "";
                temp += String.format("%-30s", "PS_SA_CUST_CIF_HIS");
                temp += String.format("%015d", new BigDecimal(ObjectUtils.toString(map.get("資料筆數"))).intValue());
                temp += ObjectUtils.toString(map.get("資料日期"));

                fw6.write(temp + "\r\n");
            }
            fw6.close();
            }
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {

            //設定特定檔名
            //setFileName("CRM6105_12_exCUST_05", data);
            //setFileName("CRM6105_12_exCUST_06", valid);
            conn.close();
        }

        

    }


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
                if ((StrLen - LenCtrl - 1) >= 2) {
                    returnString = returnString + inputStr.substring(i, i + 1);
                    LenCtrl = LenCtrl + 2;
                }
            } else {
                if ((StrLen - LenCtrl) >= 1) {
                    returnString = returnString + inputStr.substring(i, i + 1);
                    LenCtrl++;
                }
            }
            i++;
        }
        // 現有字串以後補空白
        while (LenCtrl < StrLen) {
            returnString = "0" + returnString;
            LenCtrl++;
        }
        return returnString;
    }

    /**
     *	@param TBSYSFTP 特定FTP設定
     * @param fileName  檔案名稱
     * @author 威揚
     * <br/>
     * 按照USER特定的命名規則
     * @throws JBranchException 
     */

    private void setFileName(String TBSYSFTP, String fileName) throws JBranchException {
        dam = this.getDataAccessManager();
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        TBSYSFTPVO tbsysftpvo = new TBSYSFTPVO();
        tbsysftpvo = (TBSYSFTPVO) dam.findByPKey(TBSYSFTPVO.TABLE_UID, TBSYSFTP);
        tbsysftpvo.setDESFILENAME(fileName);
        tbsysftpvo.setSRCFILENAME(fileName);
        dam.update(tbsysftpvo);
    }
    //取得NODELIST
    private NodeList getNodeList() throws ParserConfigurationException, SAXException, IOException {
//            File file = new File("src/test/resources/META-INF/context.xml"); //本機
            File file = new File("conf/context.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            NodeList nodeList = document.getElementsByTagName("Resource");
            return nodeList;
        }
        //取得DRIVER NAME
    private String getDriverName(Object body) throws Exception {
            String driver = getNodeList().item(0).getAttributes().getNamedItem("driverClassName").getNodeValue();
            return driver;

        }
        //取得CONNECTION
    private Connection getConn(Object body) throws Exception {
        /*String driver = getNodeList().item(0).getAttributes().getNamedItem("driverClassName").getNodeValue();
        String url = getNodeList().item(0).getAttributes().getNamedItem("url").getNodeValue();
        String user = getNodeList().item(0).getAttributes().getNamedItem("username").getNodeValue();
        String password = getNodeList().item(0).getAttributes().getNamedItem("password").getNodeValue();
        Class.forName(driver);
        Connection conn =
            DriverManager.getConnection(url,
                user, password);
        */
    	Connection conn = null;
		com.systex.jbranch.platform.common.dataaccess.datasource.DataSource ds = new
                com.systex.jbranch.platform.common.dataaccess.datasource.DataSource();
        SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
		DataSource dataSource = SessionFactoryUtils.getDataSource(sf);
		conn = dataSource.getConnection();
    	return conn;
    }

    /**
     * 取得今年年份
     * @return
     */
    private int getThisYear() {
    	calendar.add(calendar.MONTH,-1);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 取得當月月份
     * @return
     */
    private int getThisMonth() {

        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 
     * */
    private String month(int mon) {
            String val;
            if (mon < 10) {
                val = 0 + String.valueOf(mon);
            } else {
                val = String.valueOf(mon);
            }
            return val;
        }
        /**
         * 
         * */
    private String day(int day) {
            String val;
            if (day < 10) {
                val = 0 + String.valueOf(day);
            } else {
                val = String.valueOf(day);
            }
            return val;
        }
        /**
         * 取得今天日期 
         * @return
         */
    private static int getThisDay() {
            return calendar.get(Calendar.DAY_OF_MONTH);
        }
        /** 
         *  
         * @param year 
         *            int 年份 
         * @param month 
         *            int 月份 
         *  
         * @return int 某年某月的最后一天 
         */
    private int getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        // 某年某月的最后一天  
        return cal.getActualMaximum(Calendar.DATE);
    }
    
    //上月第一天
    private String getFirstDayOfMonth() {
	    Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		
	    String strDateFrom = sdf.format(calendar.getTime());  
	    
	    return strDateFrom;
    }
}