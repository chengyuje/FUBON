package com.systex.jbranch.app.server.fps.fpsutils;

import com.google.gson.Gson;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILEPK;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILEVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILE_LOGPK;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILE_LOGVO;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeUtility;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("fpsutils")
@Scope("request")
public class FPSUtils extends FubonWmsBizLogic {

    // ===================== 理規共用方法 ============================

    /**
     * 檢查理專有無權限存取理規模組
     * 
     * @param dam
     * @param empID
     * @return Boolean
     * @throws DAOException
     * @throws JBranchException
     */
    @SuppressWarnings("unchecked")
    public static Boolean hasEmpFpsAuth(DataAccessManager dam, String empID) throws DAOException, JBranchException{
        if (StringUtils.isBlank(empID)) {
            return false;
        }

        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT COUNT(DISTINCT DETAIL.EMP_ID) CNT FROM TBFPS_AUTHORITY HEAD");
        sb.append(" INNER JOIN TBFPS_AUTHORITY_DETAIL DETAIL ON DETAIL.PARAM_NO = HEAD.PARAM_NO");
        sb.append(" WHERE 1 = 1");
        sb.append(" AND TRUNC(HEAD.EFFECT_START_DATE) <= TRUNC(SYSDATE)");
        sb.append(" AND HEAD.STATUS = 'A'");
        sb.append(" AND DETAIL.EMP_ID = :empID");

        qc.setObject("empID", empID);
        qc.setQueryString(sb.toString());

        return ((BigDecimal) ((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("CNT")).intValue() > 0;
    }

    /**
     * 從target list取得刪除列表
     * 
     * @param dam
     * @param targetLs
     * @param planID
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> filterDeleteList(DataAccessManager dam, Set<BigDecimal> existSet,
            String planID, String sppType) throws DAOException, JBranchException{

        BigDecimal[] existArr = existSet.toArray(new BigDecimal[existSet.size()]);

        // get delest seqNos
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();

        sb.append(" SELECT SEQNO, PRD_ID FROM TBFPS_PORTFOLIO_PLAN_" + sppType);
        sb.append(" WHERE PLAN_ID = :planID");
        qc.setObject("planID", planID);
        
        if ("SPP".equals(sppType)) {
            sb.append(" AND ((ORDER_STATUS IS NOT NULL AND ORDER_STATUS <> 'Y') or ORDER_STATUS is null) ");
        }

        if (existArr.length > 0) {
            sb.append(" AND SEQNO NOT IN :seqNo");
            qc.setObject("seqNo", existArr);
        }

        qc.setQueryString(sb.toString());
        return dam.exeQuery(qc);
    }

    /**
     * 檢查商品有無滿x年
     * 
     * @param dam
     * @param prods
     *            商品們
     * @param years
     *            需共同年
     * @return String[] 未滿年份之商品們
     * @throws DAOException
     * @throws JBranchException
     */
    public static String[] checkValidYear(DataAccessManager dam, List<Map<String, Object>> prods, int years,
            Map<String, String> keyMap) throws DAOException, JBranchException{
        String[] errorStrings = new String[] { "無商品" };
        if (prods == null || prods.size() <= 0) {
            return errorStrings;
        }
        Map<String, String> initKeyMap = new HashMap<String, String>();
        initKeyMap.put("prodType", "PRD_TYPE");
        initKeyMap.put("prodID", "PRD_ID");
        initKeyMap.put("targets", "TARGETS");

        Map<String, List<String>> groupProdIDMap = groupProdIDList(prods, keyMap == null ? initKeyMap : keyMap, true);
        if (groupProdIDMap == null)
            return new String[0];

        int inputCnt = 0;
        // check null
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (MapUtils.isNotEmpty(groupProdIDMap)) {
            for (Entry<String, List<String>> entry : groupProdIDMap.entrySet()) {
                if (entry.getValue() != null) {
                    inputCnt += entry.getValue().size();
                    paramMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        
        //首作-如果都是奈米投會無商品則跳過檢查
        if(paramMap.isEmpty()){
           return new String[0];
        }
        else{
        	int mfdListCount = (List) paramMap.get("mfdList") == null ? 0 : ((List) paramMap.get("mfdList")).size();
            int etfListCount = (List) paramMap.get("etfList") == null ? 0 : ((List) paramMap.get("etfList")).size();
            int insListCount = (List) paramMap.get("insList") == null ? 0 : ((List) paramMap.get("insList")).size();
            if(mfdListCount == 0 && etfListCount == 0 && insListCount == 0) return new String[0];
        }
                	
        List<Map<String, Object>> result = doQuery(dam, paramMap, sqlProdExistResource(paramMap, years));

        int minus = inputCnt - result.size();
        Set<String> invalid = new HashSet<String>();
        int i = 0;
        if (minus > 0) {
            Map<String, List<String>> resultGroupProdIDMap = groupProdIDList(result, initKeyMap, false);
            if (MapUtils.isNotEmpty(groupProdIDMap)) {
                for (Entry<String, List<String>> entry : groupProdIDMap.entrySet()) {
                    if (entry.getValue() != null) {
                        for (String prd : entry.getValue()) {
                            if (resultGroupProdIDMap == null || !ArrayUtils.contains(resultGroupProdIDMap.get(entry
                                                                                                                   .getKey())
                                                                                                         .toArray(),
                                    prd)) {
                                invalid.add(prd);
                            }
                        }
                    }
                }
            }
        }

        return invalid.toArray(new String[invalid.size()]);
    }

    /**
     * 查詢 共同區間 + 所有資料
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    private static String sqlProdExistResource(Map<String, Object> param, int years){
        StringBuffer sql = new StringBuffer();
        int mfdListCount = (List) param.get("mfdList") == null ? 0 : ((List) param.get("mfdList")).size();
        int etfListCount = (List) param.get("etfList") == null ? 0 : ((List) param.get("etfList")).size();
        int insListCount = (List) param.get("insList") == null ? 0 : ((List) param.get("insList")).size();

        if (mfdListCount != 0) {
            sql.append(" select RM1.PRD_ID PRD_ID, 'MFD' PRD_TYPE from TBFPS_PRD_RETURN_M RM1 ");
            sql.append(" where 1 =1  ");
            sql.append(" and RM1.PRD_ID in(:mfdList) and RM1.PRD_TYPE = 'MFD'  ");
            sql.append(" and RM1.DATA_YEARMONTH = TO_CHAR(ADD_MONTHS(SYSDATE,-" + (years * 12) + "),'YYYYMM') ");
        } else {
            param.remove("mfdList");
        }

        if (sql.length() != 0 && etfListCount != 0) {
            sql.append(" union ");
        }

        if (etfListCount != 0) {
            sql.append(" select RM2.PRD_ID PRD_ID, 'ETF' PRD_TYPE from TBFPS_PRD_RETURN_M RM2 ");
            sql.append(" where 1 =1  ");
            sql.append(" and RM2.PRD_ID in(:etfList) and RM2.PRD_TYPE = 'ETF'  ");
            sql.append(" and RM2.DATA_YEARMONTH = TO_CHAR(ADD_MONTHS(SYSDATE,-" + (years * 12) + "),'YYYYMM') ");
        } else {
            param.remove("etfList");
        }

        if (sql.length() != 0 && insListCount != 0) {
            sql.append(" union ");
        }

        if (insListCount != 0) {
            sql.append(" select RM3.PRD_ID PRD_ID, 'INS' PRD_TYPE from TBFPS_PRD_RETURN_M RM3 ");
            sql.append(" where 1 =1  ");
            sql.append(" and RM3.PRD_ID in(:insList) and RM3.PRD_TYPE = 'INS'  ");
            sql.append(" and RM3.DATA_YEARMONTH = TO_CHAR(ADD_MONTHS(SYSDATE,-" + (years * 12) + "),'YYYYMM') ");
        } else {
            param.remove("insList");
        }

        if (sql.length() != 0) {
            sql.append(" order by PRD_TYPE");
        }
        return sql.toString();
    }

    // 取得最後修改日期為規劃日期
    @SuppressWarnings("unchecked")
    public static Date getLastUpdate(DataAccessManager dam, String planID, String sppType) throws DAOException,
            JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT MAX(LASTUPDATE) LASTUPDATE FROM TBFPS_PORTFOLIO_PLAN_" + sppType);
        sb.append(" WHERE PLAN_ID = :planID");

        qc.setObject("planID", planID);
        qc.setQueryString(sb.toString());

        List<Map<String, Object>> result = dam.exeQuery(qc);
        Date d = (Date) ((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("LASTUPDATE");
        if (d != null) {
            return d;
        } else {
            qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            sb = new StringBuilder();
            sb.append(" SELECT MAX(LASTUPDATE) LASTUPDATE FROM TBFPS_PORTFOLIO_PLAN_" + sppType + "_HEAD");
            sb.append(" WHERE PLAN_ID = :planID");

            qc.setObject("planID", planID);
            qc.setQueryString(sb.toString());

            return (Date) ((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("LASTUPDATE");
        }
    }

    // 取得建立日期 為完成日期
    @SuppressWarnings("unchecked")
    public static Date getCreatetime(DataAccessManager dam, String planID) throws DAOException, JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT CREATETIME FROM TBFPS_SPP_PRD_RETURN_HEAD ");
        sb.append(" WHERE PLAN_ID = :planID ");

        qc.setObject("planID", planID);
        qc.setQueryString(sb.toString());

        return (Date) ((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("CREATETIME");
    }

    // ===================== 理規產出PDF ============================

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getPDFFile(DataAccessManager dam, String planID, BigDecimal seq,
            String sppType) throws DAOException, JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT A.PLAN_PDF_FILE, A.FILE_NAME, A.SEQ_NO");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE A");
        sb.append(" WHERE A.PLAN_ID = :planID");
        sb.append(" AND A.ENCRYPT = 'Y'");
        sb.append(" AND A.PLAN_TYPE = :sppType");
        sb.append(" AND A.SEQ_NO = :seq");

        qc.setObject("sppType", sppType);
        qc.setObject("planID", planID);
        qc.setObject("seq", seq);
        qc.setQueryString(sb.toString());
        return dam.exeQuery(qc);
    }

    // new file
    public static BigDecimal newPDFFile(DataAccessManager dam, String custID, String aoCode, String fileName, String tempFileName,
            String planID, String sppType, String action) throws JBranchException{
        BigDecimal seqNo = new BigDecimal(getSN("FPS_" + sppType + "_FILE"));

        if (StringUtils.isNotBlank(planID)) {
            try {
                String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
                byte[] regularData = Files.readAllBytes(new File(tempPath, tempFileName).toPath());

                if (regularData.length <= 0) {
                    throw new APException("無PDF資料"); //
                }
                Blob unEncryptBlob = ObjectUtil.byteArrToBlob(regularData);

                String encryString = custID;
                
                // encrypt
                byte[] encryptData = setEncryption(encryString, regularData).toByteArray();
                Blob encryptBlob = ObjectUtil.byteArrToBlob(encryptData);

                for (String YN : new String[] { "Y", "N" }) {
                    TBFPS_PORTFOLIO_PLAN_FILEPK pk = new TBFPS_PORTFOLIO_PLAN_FILEPK();
                    pk.setPLAN_ID(planID);
                    pk.setPLAN_TYPE(sppType);
                    pk.setSEQ_NO(seqNo);
                    pk.setENCRYPT(YN);
                    TBFPS_PORTFOLIO_PLAN_FILEVO vo = new TBFPS_PORTFOLIO_PLAN_FILEVO();
                    vo.setcomp_id(pk);
                    vo.setFILE_NAME(fileName);
                    if (("Y").equals(YN)) {
                        vo.setPLAN_PDF_FILE(encryptBlob);
                    } else if (("N").equals(YN)) {
                        vo.setPLAN_PDF_FILE(unEncryptBlob);
                    }
                    dam.create(vo);
                }
            } catch (SQLException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return seqNo;
    }

    // 取得該客戶的Email
    public static String getCustEmail(DataAccessManager dam, String custID) throws DAOException, JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();

        sb.append(" SELECT EMAIL ");
        sb.append(" FROM TBCRM_CUST_CONTACT ");
        sb.append(" WHERE CUST_ID = :custID ");

        qc.setQueryString(sb.toString());
        qc.setObject("custID", custID);

        List<Map<String, Object>> result = dam.exeQuery(qc);
        if (result.isEmpty())
            return "";
        return (String) result.get(0).get("EMAIL");
    }

    // E-mail regex
    public static boolean isEmail(String email){
        Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher = emailPattern.matcher(email);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * Pdf file encryption with password. Byte in byte out
     * 
     * @param ByteArrayInputStream
     *            input
     * @return ByteArrayOutputStream out
     */
    public static ByteArrayOutputStream setEncryption(String custID, byte[] pdfData){
        String password = custID;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfReader reader = new PdfReader(pdfData);
            // PdfStamper stamper = new PdfStamper(reader, new
            // FileOutputStream("C:/Users/1700433.SYSTEX/Desktop/test.pdf"));
            PdfStamper stamper = new PdfStamper(reader, out);
            stamper.setEncryption(password.getBytes(), "World".getBytes(), PdfWriter.ALLOW_PRINTING,
                    PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
            stamper.close();
            reader.close();
            return out;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 使用者行為紀錄
    public static void execLog(DataAccessManager dam, String planID, BigDecimal seq, String execType, String sppType)
            throws JBranchException{
        TBFPS_PORTFOLIO_PLAN_FILE_LOGPK pk = new TBFPS_PORTFOLIO_PLAN_FILE_LOGPK();
        pk.setPLAN_ID(planID);
        pk.setPLAN_TYPE(sppType);
        pk.setSEQ_NO(seq);
        pk.setLOG_SEQ(getSeq(dam, planID, seq, sppType));
        TBFPS_PORTFOLIO_PLAN_FILE_LOGVO vo = new TBFPS_PORTFOLIO_PLAN_FILE_LOGVO();
        vo.setcomp_id(pk);
        vo.setEXEC_TYPE(execType);

        dam.create(vo);
    }

    // 取得該insert的log_seq
    private static BigDecimal getSeq(DataAccessManager dam, String planID, BigDecimal seq, String sppType) throws JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT MAX(LOG_SEQ) LOG_SEQ FROM TBFPS_PORTFOLIO_PLAN_FILE_LOG ");
        sb.append(" WHERE PLAN_ID = :planID ");
        sb.append(" AND SEQ_NO = :seqNO ");
        sb.append(" AND PLAN_TYPE = :planType ");
        sb.append(" GROUP BY PLAN_ID,PLAN_TYPE,SEQ_NO ");

        qc.setQueryString(sb.toString());
        qc.setObject("planID", planID);
        qc.setObject("seqNO", seq);
        qc.setObject("planType", sppType);
//        qc.setObject("planType", planID.substring(0, 3));
        List<Map<String, Object>> result = dam.exeQuery(qc);
        if (result.isEmpty())
            return new BigDecimal(1);
        BigDecimal num = ((BigDecimal) result.get(0).get("LOG_SEQ")).add(new BigDecimal(1));
        return num;
    }

    // test save encryption
    @SuppressWarnings("unused")
    private void testSaveEncryption(Blob blob, String fileName) throws SQLException, IOException{
        File blobFile = new File(fileName);
        FileOutputStream outStream = new FileOutputStream(blobFile);
        InputStream inStream = blob.getBinaryStream();

        int length = -1;
        int size = (int) blob.length();
        byte[] buffer = new byte[size];

        while ((length = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, length);
            outStream.flush();
        }

        inStream.close();
        outStream.close();
    }

    // 流水號
    public static String getSN(String type) throws JBranchException{
        SerialNumberUtil sn = new SerialNumberUtil();
        String seqNum = "";
        try {
            seqNum = sn.getNextSerialNumber(type);
        } catch (Exception e) {
            sn.createNewSerial(type, "00000000000000", null, null, null, 1, new Long("999999999999"), "y", new Long(
                    "0"), null);
            seqNum = sn.getNextSerialNumber(type);
        }
        return seqNum;
    }

    // ===================== 理規計算 歷史績效表現 ============================
    public static List<Map<String, Object>> historyPerformance(DataAccessManager dam, String planID)
            throws DAOException, JBranchException{
        StringBuffer sppTempPrd = new StringBuffer();
        sppTempPrd.append("SELECT  M.PRD_ID, PORTFOLIO_RATIO / 100 AS PORTFOLIO_RATIO, "
                + "CASE WHEN SUBSTR(MAX(DATA_YEARMONTH), 5, 6) = '12' THEN SUBSTR(MAX(DATA_YEARMONTH), 1, 4) ELSE TO_CHAR(TO_NUMBER(SUBSTR(MAX(DATA_YEARMONTH), 1, 4)) - 1) END || '12' AS MAX_Y, "
                + "CASE WHEN SUBSTR(MIN(DATA_YEARMONTH), 5, 6) = '01' THEN SUBSTR(MIN(DATA_YEARMONTH), 1, 4) ELSE TO_CHAR(TO_NUMBER(SUBSTR(MIN(DATA_YEARMONTH), 1, 4)) + 1) END || '01' AS MIN_Y "
                + "FROM TBFPS_PRD_RETURN_M M " + "LEFT JOIN TBFPS_PORTFOLIO_PLAN_SPP SPP ON SPP.PRD_ID = M.PRD_ID "
                + "WHERE PLAN_ID = '" + planID + "' GROUP BY M.PRD_ID, PORTFOLIO_RATIO ");
        String sppTemp = "WITH SPP_TEMP AS( " + sppTempPrd + ")";
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer(sppTemp);

        sb.append("SELECT DATA_YEAR AS YEAR, SUM(RETURN_ANN_M) AS RETURN_ANN_M FROM ( ");
        sb.append(" SELECT SUBSTR(DATA_YEARMONTH, 1, 4) AS DATA_YEAR,RETURN_1Y * PORTFOLIO_RATIO AS RETURN_ANN_M ");
        sb.append(" FROM TBFPS_PRD_RETURN_M M ");
        sb.append(" LEFT JOIN  SPP_TEMP S ON M.PRD_ID = S.PRD_ID ");
        sb.append(" WHERE M.PRD_ID IN(SELECT SPP.PRD_ID FROM SPP_TEMP SPP  ) ");
        sb.append(
                " AND M.DATA_YEARMONTH >= (SELECT CASE WHEN (TO_NUMBER(MIN(MAX_Y))- TO_NUMBER(MAX(MIN_Y)) > 911) THEN TO_CHAR(TO_NUMBER(MIN(MAX_Y)) - 911) ELSE MAX(MIN_Y) END FROM SPP_TEMP) ");
        sb.append(" AND M.DATA_YEARMONTH <= (SELECT MIN(MAX_Y) FROM SPP_TEMP) ");
        sb.append(" AND SUBSTR(M.DATA_YEARMONTH, 5, 6) = '12') ");
        sb.append("GROUP BY DATA_YEAR ORDER BY DATA_YEAR ");

        qc.setQueryString(sb.toString());
        return dam.exeQuery(qc);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> historyPerformance(DataAccessManager dam, List<Map<String, Object>> prds)
            throws DAOException, JBranchException{
        StringBuffer sppTempPrd = new StringBuffer();
        for (Map<String, Object> getOne : prds) {
            sppTempPrd.append("UNION ");
            sppTempPrd.append("SELECT PRD_ID, " + (Double) getOne.get("WEIGHT") + " AS PORTFOLIO_RATIO, "
                    + "CASE WHEN SUBSTR(MAX(DATA_YEARMONTH), 5, 6) = '12' THEN SUBSTR(MAX(DATA_YEARMONTH), 1, 4) ELSE TO_CHAR(TO_NUMBER(SUBSTR(MAX(DATA_YEARMONTH), 1, 4)) - 1) END || '12' AS MAX_Y, "
                    + "CASE WHEN SUBSTR(MIN(DATA_YEARMONTH), 5, 6) = '01' THEN SUBSTR(MIN(DATA_YEARMONTH), 1, 4) ELSE TO_CHAR(TO_NUMBER(SUBSTR(MIN(DATA_YEARMONTH), 1, 4)) + 1) END || '01' AS MIN_Y "
                    + "FROM TBFPS_PRD_RETURN_M " + "WHERE PRD_ID ='" + getOne.get("PRD_ID") + "' GROUP BY PRD_ID ");
        }
        String sppTemp = "WITH SPP_TEMP AS( " + sppTempPrd.substring(6) + ")";
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer(sppTemp);

        sb.append("SELECT DATA_YEAR AS YEAR, SUM(RETURN_ANN_M) AS RETURN_ANN_M FROM ( ");
        sb.append(" SELECT SUBSTR(DATA_YEARMONTH, 1, 4) AS DATA_YEAR,RETURN_1Y * PORTFOLIO_RATIO AS RETURN_ANN_M ");
        sb.append(" FROM TBFPS_PRD_RETURN_M M ");
        sb.append(" LEFT JOIN  SPP_TEMP S ON M.PRD_ID = S.PRD_ID ");
        sb.append(" WHERE M.PRD_ID IN(SELECT SPP.PRD_ID FROM SPP_TEMP SPP  ) ");
        sb.append(
                " AND M.DATA_YEARMONTH >= (SELECT CASE WHEN (TO_NUMBER(MIN(MAX_Y))- TO_NUMBER(MAX(MIN_Y)) > 911) THEN TO_CHAR(TO_NUMBER(MIN(MAX_Y)) - 911) ELSE MAX(MIN_Y) END FROM SPP_TEMP) ");
        sb.append(" AND M.DATA_YEARMONTH <= (SELECT MIN(MAX_Y) FROM SPP_TEMP) ");
        sb.append(" AND SUBSTR(M.DATA_YEARMONTH, 5, 6) = '12') ");
        sb.append("GROUP BY DATA_YEAR ORDER BY DATA_YEAR ");

        qc.setQueryString(sb.toString());
        return dam.exeQuery(qc);
    }

    // ===================== 理規計算 報酬率 ============================
    /**
     * 執行取得 歷史年度平均報酬率 --%
     * 
     * @param dam
     * @param planId
     * @return
     */
    public static BigDecimal getYRate(DataAccessManager dam, List<Map<String, Object>> mfdEtfInsList, int maxMonth,
            int minMonth){
        BigDecimal yRate = BigDecimal.ZERO;
        try {
            if (CollectionUtils.isEmpty(mfdEtfInsList)) {
                return yRate;
            }

            // 群組化所有資料
            Map<String, List<String>> groupProdIDMap = groupProdIDList(mfdEtfInsList);

            // 來源資料有誤無法組合 例如 INS 裡面的 TAGETS 是 NULL
            if (MapUtils.isEmpty(groupProdIDMap)) {
                return null;
            }

            // 完整一年只需要取為值為前年12
            String year = null, month = null;
            year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
            year = "0".equals(month) ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1) : year;
            month = "0".equals(month) ? "12" : (month.length() == 1 ? "0" + month : month);
            String currentYearMonth = year + month;

            // 取得共同區間
            List<Map<String, Object>> intervalList = getCommonIntervalList(dam, groupProdIDMap, mfdEtfInsList, maxMonth,
                    false, currentYearMonth);

            // 如果沒有共同區間 或是 不足一年(12個月)
            if (CollectionUtils.isEmpty(intervalList) || intervalList.size() < minMonth) {
                return null;
            }

            String[] intervalStartEnd = getCommonInterval(intervalList);

            // 取得共同區間 + 所有資料
            List<Map<String, Object>> getDataResource = getYRateDataResource(dam, groupProdIDMap, mfdEtfInsList,
                    intervalStartEnd);

            if (CollectionUtils.isEmpty(getDataResource)) {
                return yRate;
            }

            // 處理資料並運算
            // 先分類
            Map<String, List<Map<String, Object>>> groupMap = groupCombine(mfdEtfInsList, getDataResource);

            // 分配權重
            Map<String, BigDecimal> weightsPercentMap = weightsPercent(mfdEtfInsList);

            for (Entry<String, List<Map<String, Object>>> entry : groupMap.entrySet()) {

                System.out.println(entry.getKey());
                // 累進報酬率
                BigDecimal yRateGrandTotal = getYRate(entry.getValue());
                System.out.println("累進報酬率 :" + yRateGrandTotal);

                // 要開根號的
                BigDecimal divideYear = new BigDecimal(12).divide(new BigDecimal(entry.getValue().size()), 6,
                        BigDecimal.ROUND_HALF_UP);
                System.out.println("要開根號的 :" + divideYear);

                // 年化報酬率
                BigDecimal yRateYear = new BigDecimal(Math.pow((BigDecimal.ONE.add(yRateGrandTotal)).doubleValue(),
                        divideYear.doubleValue())).subtract(BigDecimal.ONE).setScale(6, BigDecimal.ROUND_HALF_UP);
                System.out.println("年化報酬率 :" + yRateYear);

                // 權重
                System.out.println("權重 :" + weightsPercentMap.get(entry.getKey()).setScale(6,
                        BigDecimal.ROUND_HALF_UP));

                // 年化 * 權重
                yRate = yRate.add(yRateYear.multiply(weightsPercentMap.get(entry.getKey())));
                System.out.println("年化 * 權重 :" + yRateYear.multiply(weightsPercentMap.get(entry.getKey())).setScale(6,
                        BigDecimal.ROUND_HALF_UP));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        System.out.println("年化報酬率：" + yRate.setScale(4, BigDecimal.ROUND_HALF_UP));
        return yRate.setScale(4, BigDecimal.ROUND_HALF_UP);
    }

    public static Map<String, List<String>> groupProdIDList(List<Map<String, Object>> mfdEtfInsList){
        Map<String, List<String>> groupProdIdMap = new HashMap<String, List<String>>();
        List<String> insList = new ArrayList<String>();
        List<String> mfdList = new ArrayList<String>();
        List<String> etfList = new ArrayList<String>();
        for (Map<String, Object> mfdEtfInsMap : mfdEtfInsList) {
            if ("INS".equals(ObjectUtils.toString(mfdEtfInsMap.get("PRD_TYPE")).toUpperCase()) && mfdEtfInsMap.get(
                    "TARGETS") == null) {
                return null;
            }
            switch (ObjectUtils.toString(mfdEtfInsMap.get("PRD_TYPE")).toUpperCase()) {
            case "INS": {
                insList.addAll(Arrays.asList(ObjectUtils.toString(mfdEtfInsMap.get("TARGETS")).split("/")));
                break;
            }
            case "MFD": {
                mfdList.add(ObjectUtils.toString(mfdEtfInsMap.get("PRD_ID")));
                break;
            }
            case "ETF": {
                etfList.add(ObjectUtils.toString(mfdEtfInsMap.get("PRD_ID")));
                break;
            }
            default: {
                break;
            }
            }
        }
        groupProdIdMap.put("insList", insList);
        groupProdIdMap.put("mfdList", mfdList);
        groupProdIdMap.put("etfList", etfList);
        return groupProdIdMap;
    }

    public static Map<String, List<String>> groupProdIDList(List<Map<String, Object>> mfdEtfInsList,
            Map<String, String> keyMap, Boolean target2ID){
        Map<String, List<String>> groupProdIdMap = new HashMap<String, List<String>>();
        List<String> insList = new ArrayList<String>();
        List<String> mfdList = new ArrayList<String>();
        List<String> etfList = new ArrayList<String>();
        for (Map<String, Object> mfdEtfInsMap : mfdEtfInsList) {
            if ("INS".equals(ObjectUtils.toString(mfdEtfInsMap.get(keyMap.get("prodType"))).toUpperCase()) && (target2ID
                    && mfdEtfInsMap.get(keyMap.get("targets")) == null)) {
                return null;
            }
            switch (ObjectUtils.toString(mfdEtfInsMap.get(keyMap.get("prodType"))).toUpperCase()) {
            case "INS": {
                if (target2ID)
                    insList.addAll(Arrays.asList(ObjectUtils.toString(mfdEtfInsMap.get(keyMap.get("targets"))).split(
                            "/")));
                else
                    insList.add(ObjectUtils.toString(mfdEtfInsMap.get(keyMap.get("prodID"))));
                break;
            }
            case "MFD": {
                mfdList.add(ObjectUtils.toString(mfdEtfInsMap.get(keyMap.get("prodID"))));
                break;
            }
            case "ETF": {
                etfList.add(ObjectUtils.toString(mfdEtfInsMap.get(keyMap.get("prodID"))));
                break;
            }
            default: {
                break;
            }
            }
        }
        groupProdIdMap.put("insList", insList);
        groupProdIdMap.put("mfdList", mfdList);
        groupProdIdMap.put("etfList", etfList);
        return groupProdIdMap;
    }

    /**
     * 取得共同區間清單
     * 
     * @param dam
     * @param planId
     * @return
     * @throws DAOException
     * @throws JBranchException
     * @throws ParseException
     */
    public static List<Map<String, Object>> getCommonIntervalList(DataAccessManager dam,
            Map<String, List<String>> groupProdIDMap, List<Map<String, Object>> mfdEtfInsList, int showMaxSize,
            boolean fullYear, String currentYearMonth) throws DAOException, JBranchException, ParseException{
        Map<String, Object> paramMap = prepareParament(mfdEtfInsList, groupProdIDMap, null);
        String sql = sqlCommonInterval(paramMap, showMaxSize, fullYear, currentYearMonth);
        if (sql == null) {
            return null;
        }
        List<Map<String, Object>> intervalList = doQuery(dam, paramMap, sqlCommonInterval(paramMap, showMaxSize,
                fullYear, currentYearMonth));

        if (intervalList.size() <= 0) return null; 
        String starD = ObjectUtils.toString(intervalList.get(0).get("DATA_YEARMONTH"));
        String endD = ObjectUtils.toString(intervalList.get(intervalList.size() - 1).get("DATA_YEARMONTH"));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.setTime(sdf.parse(starD));
        c2.setTime(sdf.parse(endD));

        int ymCount = c2.get(fullYear ? Calendar.YEAR : Calendar.MONTH) - c1.get(fullYear ? Calendar.YEAR
                : Calendar.MONTH);
        if (!fullYear) {
            ymCount = (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) * 12 + ymCount + 1;
        } else {
            ymCount += 1;
        }

        if (ymCount != intervalList.size())
            return null;

        return intervalList;
    }

    /**
     * 取得共同區間
     * 
     * @param dam
     * @param planId
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public static String[] getCommonInterval(List<Map<String, Object>> intervalList) throws DAOException,
            JBranchException{
        String[] interval = new String[2];

        if (CollectionUtils.isEmpty(intervalList)) {
            return null;
        } else {
            interval[0] = ObjectUtils.toString(intervalList.get(0).get("DATA_YEARMONTH"));
            interval[1] = ObjectUtils.toString(intervalList.get(intervalList.size() - 1).get("DATA_YEARMONTH"));
        }

        return interval;
    }

    /**
     * 取得共同區間 + 所有資料
     * 
     * @param dam
     * @param planId
     * @param mfdEtfInsList
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public static List<Map<String, Object>> getYRateDataResource(DataAccessManager dam,
            Map<String, List<String>> groupProdIDMap, List<Map<String, Object>> mfdEtfInsList, String[] interval)
            throws DAOException, JBranchException{
        if (interval == null) {
            return null;
        } else {
            Map<String, Object> paramMap = prepareParament(mfdEtfInsList, groupProdIDMap, interval);
            return doQuery(dam, paramMap, sqlYRateResource(paramMap));
        }
    }

    /**
     * 參數組合共同區間 + 所有清單資料
     * 
     * @param mfdEtfInsList
     * @param planId
     * @return
     */
    private static Map<String, Object> prepareParament(List<Map<String, Object>> mfdEtfInsList,
            Map<String, List<String>> groupProdIDMap, String[] intervalArray){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (MapUtils.isNotEmpty(groupProdIDMap)) {
            for (Entry<String, List<String>> entry : groupProdIDMap.entrySet()) {
                if (entry.getValue() != null) {
                    paramMap.put(entry.getKey(), entry.getValue());
                }

                // 我取區間的
                if (intervalArray == null) {
                    Set<String> noRepSet = new HashSet<>();
                    noRepSet.addAll(entry.getValue());
                    paramMap.put(entry.getKey() + "Count", noRepSet.size());
                }
            }
        }

        // 我不是取區間的
        if (intervalArray != null) {
            paramMap.put("startInterval", intervalArray[0]);
            paramMap.put("endInterval", intervalArray[1]);
        }
        return paramMap;
    }

    /**
     * 執行 Query
     * 
     * @param dam
     * @param paramMap
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static List<Map<String, Object>> doQuery(DataAccessManager dam, Map<String, Object> paramMap, String sql)
            throws DAOException, JBranchException{
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        queryCondition.setQueryString(sql);

        if (paramMap != null) {
            for (Entry entry : paramMap.entrySet()) {
                queryCondition.setObject((String) entry.getKey(), entry.getValue());
            }
        }

        return dam.exeQuery(queryCondition);
    }

    /**
     * 單一品項的累進報酬率 算式: 第一月 為 月報酬率 / 100 第二月 為 (1+(第一月結果))*(1+第二月/100)-1 第三月 為
     * (1+(第二月結果))*(1+第三月/100)-1 以此類推
     * 
     * @param yRateDataResourceList
     * @return
     */
    public static BigDecimal getYRate(List<Map<String, Object>> yRateDataResourceList){
        BigDecimal yRate = BigDecimal.ZERO;

        // 要先排序 >> 原因如果大於 120 個月，取最近的120個月 (10年)
         Collections.sort(yRateDataResourceList , getComparatorObj(new String[]{"DATEMONTH"}));

        // 只有一年單純處理
        BigDecimal firstYear = ((BigDecimal) yRateDataResourceList.get(0).get("ONEMONTH")).divide(new BigDecimal(100));
        if (yRateDataResourceList.size() == 1)
            return firstYear;
        yRate = firstYear;

        // 接下來第二年都使用第一年的加一後與當年的計算
        for (int i = 1; i < yRateDataResourceList.size(); i++) {
            // 超過十年(120個月)後面的不算
            if (i >= 120)
                break;
            yRate = BigDecimal.ONE.add(yRate);
            BigDecimal currentYear = BigDecimal.ONE.add(((BigDecimal) yRateDataResourceList.get(i).get("ONEMONTH"))
                                                                                                                   .divide(new BigDecimal(
                                                                                                                           100)));
            yRate = yRate.multiply(currentYear).subtract(BigDecimal.ONE);
        }
        return yRate.setScale(6, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 將 rYear 的 resource 的 來源進行分組
     * 
     * @param yRateDataResourceList
     * @return
     */
    public static Map<String, List<Map<String, Object>>> groupCombine(List<Map<String, Object>> mfdEtfInsList,
            List<Map<String, Object>> yRateDataResourceList){
        Map<String, Set<Map<String, Object>>> groupMap = new HashMap<String, Set<Map<String, Object>>>();
        for (Map<String, Object> map : yRateDataResourceList) {
            if ("INS".equals(map.get("PRDTYPE"))) {
                for (Map<String, Object> mfdEtfInsMap : mfdEtfInsList) {
                    groupCombineByIns(mfdEtfInsMap, map, groupMap);
                }
            } else {
                if (groupMap.containsKey(map.get("PRDID"))) {
                    groupMap.get(map.get("PRDID")).add(map);
                } else {
                    Set<Map<String, Object>> groupList = new HashSet<Map<String, Object>>();
                    groupList.add(map);
                    groupMap.put((String) map.get("PRDID"), groupList);
                }
            }
        }

        Map<String, List<Map<String, Object>>> reGroupMap = new HashMap<String, List<Map<String, Object>>>();
        for (Entry<String, Set<Map<String, Object>>> entry : groupMap.entrySet()) {
            reGroupMap.put(entry.getKey(), new ArrayList<Map<String, Object>>(entry.getValue()));
        }

        return reGroupMap;
    }

    /**
     * 單純給 保險的商品進行比對 因為保險的來說 不一樣的 PRD_ID 有一樣的 TARGETS，TARGETS 的月報酬率是一樣的
     * 但是之後的權重不一樣，統一都用 PRD_ID + TARGET 當 key (單，切過/的)
     * 
     * @param mfdEtfInsMap
     *            TBFPS_PORTFOLIO_PLAN_INV 表的
     * @param map
     *            TBFPS_PRD_RETURN_M 表的
     * @param groupMap
     *            統一分組的 Map
     */
    private static void groupCombineByIns(Map<String, Object> mfdEtfInsMap, Map<String, Object> map,
            Map<String, Set<Map<String, Object>>> groupListMap){
        if ("INS".equals(mfdEtfInsMap.get("PRD_TYPE"))) {
            String[] targets = ObjectUtils.toString(mfdEtfInsMap.get("TARGETS")).split("/");
            String prdIdTarget = (String) map.get("PRDID"); // TBFPS_PRD_RETURN_M 表的
            String prdId = (String) mfdEtfInsMap.get("PRD_ID"); // TBFPS_PORTFOLIO_PLAN_INV表的
            for (String target : targets) {
                if (target.equals(prdIdTarget)) {
                    if (groupListMap.containsKey(prdId + prdIdTarget)) {
                        groupListMap.get(prdId + prdIdTarget).add(map);
                    } else {
                        Set<Map<String, Object>> groupList = new HashSet<Map<String, Object>>();
                        groupList.add(map);
                        groupListMap.put(prdId + prdIdTarget, groupList);
                    }
                }
            }
        }
    }

    /**
     * 權重分別
     * 
     * @param mfdEtfInsList
     * @return
     */
    public static Map<String, BigDecimal> weightsPercent(List<Map<String, Object>> mfdEtfInsList){
        Map<String, BigDecimal> weightsMap = new HashMap<String, BigDecimal>();
        // BigDecimal totalPercent = totalPercent(mfdEtfInsList);
        for (Map<String, Object> map : mfdEtfInsList) {
            BigDecimal currentPercent = new BigDecimal(ObjectUtils.toString(map.get("WEIGHT")));
            if ("INS".equals(map.get("PRD_TYPE"))) {
                String[] targets = ObjectUtils.toString(map.get("TARGETS")).split("/");
                for (String target : targets) {
                    repeatPrd(weightsMap, (String) map.get("PRD_ID") + target, currentPercent.divide(new BigDecimal(
                            targets.length), 6, BigDecimal.ROUND_HALF_UP));
                }
            } else {
                repeatPrd(weightsMap, (String) map.get("PRD_ID"), currentPercent);
            }
        }
        return weightsMap;
    }

    /**
     * 權重重複合併
     * 
     * @param weightsMap
     * @param prd
     * @param currentPercent
     */
    public static void repeatPrd(Map<String, BigDecimal> weightsMap, String prd, BigDecimal currentPercent){
        BigDecimal existsPercent = weightsMap.containsKey(prd) ? weightsMap.get(prd) : BigDecimal.ZERO;
        weightsMap.put(prd, existsPercent.add(currentPercent));
    }

    /**
     * 權重分別 2 用比例算權重
     * 
     * @param mfdEtfInsList
     * @return
     */
    public static Map<String, BigDecimal> weightsPercent(List<Map<String, Object>> mfdEtfInsList,
            BigDecimal totalPercent){
        Map<String, BigDecimal> weightsMap = new HashMap<String, BigDecimal>();
        // BigDecimal totalPercent = totalPercent(mfdEtfInsList);
        for (Map<String, Object> map : mfdEtfInsList) {
            BigDecimal currentPercent = new BigDecimal(ObjectUtils.toString(map.get("INV_PERCENT")));
            if ("INS".equals(map.get("PRD_TYPE"))) {
                String[] targets = ObjectUtils.toString(map.get("TARGETS")).split("/");
                for (String target : targets) {
                    repeatPrd(weightsMap, (String) map.get("PRD_ID") + target, currentPercent.divide(new BigDecimal(
                            targets.length), 6, BigDecimal.ROUND_HALF_UP).divide(totalPercent, 6,
                                    BigDecimal.ROUND_HALF_UP));
                }
            } else {
                repeatPrd(weightsMap, (String) map.get("PRD_ID"), currentPercent.divide(totalPercent, 6,
                        BigDecimal.ROUND_HALF_UP));
            }
        }
        return weightsMap;
    }

    /**
     * 取得總比例
     * 
     * @param mfdEtfInsList
     * @return
     */
    private static BigDecimal totalPercent(List<Map<String, Object>> mfdEtfInsList){
        // BigDecimal totalPercent = BigDecimal.ZERO;
        // for (Map<String, Object> map : mfdEtfInsList) {
        // totalPercent =
        // totalPercent.add(((BigDecimal)map.get("WEIGHT"))).setScale(0,
        // BigDecimal.ROUND_HALF_UP));
        // }
        // return totalPercent;
        return null;
    }

    /**
     * 查詢 MFD + ETF+ INS 的 SQL
     * 
     * @return
     */
    private static String sqlMfdEtfIns(){
        StringBuffer sql = new StringBuffer();
        sql.append(" select * from TBFPS_PORTFOLIO_PLAN_INV where 1 = 1 and PLAN_ID = :planId and INV_PRD_TYPE = '3'");
        return sql.toString();
    }

    /**
     * 查詢 共同區間
     * 
     * @return
     */
    private static String sqlCommonInterval(Map<String, Object> param, int showMaxSize, boolean fullYear,
            String currentYearMonth){
        String year = null, month = null;

        StringBuffer sql = new StringBuffer();
        int mfdListCount = param.get("mfdListCount") == null ? 0 : (int) param.get("mfdListCount");
        int etfListCount = param.get("etfListCount") == null ? 0 : (int) param.get("etfListCount");
        int insListCount = param.get("insListCount") == null ? 0 : (int) param.get("insListCount");

        if ((mfdListCount + etfListCount + insListCount) == 0) {
            return null;
        }

        sql.append(" SELECT t.* FROM (");
        // sql.append(" select RM.DATA_YEARMONTH from TBFPS_PORTFOLIO_PLAN_INV PINV ");
        // sql.append(" inner join TBFPS_PRD_RETURN_M RM ");
        // sql.append(" on RM.PRD_ID = PINV.PRD_ID and RM.PRD_TYPE = PINV.PRD_TYPE ");
        // sql.append(" where 1=1 and PINV.PLAN_ID = :planId and PINV.INV_PRD_TYPE = '3'
        // and PINV.TARGETS is null and RM.DATA_YEARMONTH <= :currentYearMonth");
        // sql.append(" group by RM.DATA_YEARMONTH ");
        // sql.append(" having count(RM.DATA_YEARMONTH) = (select count(1) from
        // TBFPS_PORTFOLIO_PLAN_INV where 1=1 and PLAN_ID = :planId and INV_PRD_TYPE =
        // '3' and TARGETS is null) ");

        if (mfdListCount != 0) {
            sql.append(" select RM1.DATA_YEARMONTH from TBFPS_PRD_RETURN_M RM1 ");
            sql.append(
                    " where RM1.PRD_ID in(:mfdList) and RM1.PRD_TYPE = 'MFD' and RM1.DATA_YEARMONTH <= :currentYearMonth");

            // 完整一年只需要取為值為12的月份 && 要的年報酬不能是空的
            if (fullYear) {
                sql.append(" AND   rm1.data_yearmonth like '%12' ");
                sql.append(" AND   rm1.return_1y is not null ");
            }
            sql.append(" group by RM1.DATA_YEARMONTH ");
            sql.append(" having count(RM1.DATA_YEARMONTH) = :mfdListCount ");
        } else {
            param.remove("mfdList");
            param.remove("mfdListCount");
        }

        if (mfdListCount != 0 && etfListCount != 0) {
            sql.append(" intersect "); // 聯集
        }

        if (etfListCount != 0) {
            sql.append(" select RM2.DATA_YEARMONTH from TBFPS_PRD_RETURN_M RM2 ");
            sql.append(
                    " where RM2.PRD_ID in(:etfList) and RM2.PRD_TYPE = 'ETF' and RM2.DATA_YEARMONTH <= :currentYearMonth");

            // 完整一年只需要取為值為12的月份 && 要的年報酬不能是空的
            if (fullYear) {
                sql.append(" AND   rm2.data_yearmonth like '%12' ");
                sql.append(" AND   rm2.return_1y is not null ");
            }
            sql.append(" group by RM2.DATA_YEARMONTH ");
            sql.append(" having count(RM2.DATA_YEARMONTH) = :etfListCount ");
        } else {
            param.remove("etfList");
            param.remove("etfListCount");
        }

        if ((mfdListCount != 0 || etfListCount != 0) && insListCount != 0) {
            sql.append(" intersect "); // 聯集
        }

        if (insListCount != 0) {
            sql.append(" select RM3.DATA_YEARMONTH from TBFPS_PRD_RETURN_M RM3 ");
            sql.append(
                    " where RM3.PRD_ID in(:insList) and RM3.PRD_TYPE = 'INS' and RM3.DATA_YEARMONTH <= :currentYearMonth");

            // 完整一年只需要取為值為12的月份 && 要的年報酬不能是空的
            if (fullYear) {
                sql.append(" AND   rm3.data_yearmonth like '%12' ");
                sql.append(" AND   rm3.return_1y is not null ");
            }
            sql.append(" group by RM3.DATA_YEARMONTH ");
            sql.append(" having count(RM3.DATA_YEARMONTH) = :insListCount ");
        } else {
            param.remove("insList");
            param.remove("insListCount");
        }

        param.put("currentYearMonth", currentYearMonth);
        param.put("showMaxSize", showMaxSize);

        sql.append(" order by DATA_YEARMONTH desc ");
        sql.append(" ) t WHERE ROWNUM <= :showMaxSize order by DATA_YEARMONTH ");

        System.out.println(new Gson().toJson(param));
        System.out.println(sql);
        return sql.toString();
    }

    /**
     * 查詢 共同區間 + 所有資料
     * 
     * @return
     */
    private static String sqlYRateResource(Map<String, Object> param){
        StringBuffer sql = new StringBuffer();
        // sql.append(" select RM.PRD_ID PRDID, RM.PRD_TYPE PRDTYPE, RM.DATA_YEARMONTH
        // DATEMONTH, RM.RETURN_1M ONEMONTH from TBFPS_PRD_RETURN_M RM ");
        // sql.append(" inner join TBFPS_PORTFOLIO_PLAN_INV PINV ");
        // sql.append(" on RM.PRD_ID = PINV.PRD_ID and RM.PRD_TYPE = PINV.PRD_TYPE ");
        // sql.append(" where 1 =1 ");
        // sql.append(" and PLAN_ID = :planId and INV_PRD_TYPE = '3' and TARGETS is null
        // ");
        // sql.append(" and RM.DATA_YEARMONTH >= :startInterval and
        // RM.DATA_YEARMONTH<=:endInterval ");
        int mfdListCount = (List) param.get("mfdList") == null ? 0 : ((List) param.get("mfdList")).size();
        int etfListCount = (List) param.get("etfList") == null ? 0 : ((List) param.get("etfList")).size();
        int insListCount = (List) param.get("insList") == null ? 0 : ((List) param.get("insList")).size();

        if (mfdListCount != 0) {
            sql.append(
                    " select RM1.PRD_ID PRDID, 'MFD' PRDTYPE, RM1.DATA_YEARMONTH DATEMONTH, RM1.RETURN_1M ONEMONTH from TBFPS_PRD_RETURN_M RM1  ");
            sql.append(" where 1 =1  ");
            sql.append(" and RM1.PRD_ID in(:mfdList) and RM1.PRD_TYPE = 'MFD'  ");
            sql.append(" and RM1.DATA_YEARMONTH >= :startInterval and RM1.DATA_YEARMONTH<=:endInterval ");
        } else {
            param.remove("mfdList");
        }

        if (sql.length() != 0 && etfListCount != 0) {
            sql.append(" union ");
        }

        if (etfListCount != 0) {
            sql.append(
                    " select RM2.PRD_ID PRDID, 'ETF' PRDTYPE, RM2.DATA_YEARMONTH DATEMONTH, RM2.RETURN_1M ONEMONTH from TBFPS_PRD_RETURN_M RM2  ");
            sql.append(" where 1 =1  ");
            sql.append(" and RM2.PRD_ID in(:etfList) and RM2.PRD_TYPE = 'ETF'  ");
            sql.append(" and RM2.DATA_YEARMONTH >= :startInterval and RM2.DATA_YEARMONTH<=:endInterval ");
        } else {
            param.remove("etfList");
        }

        if (sql.length() != 0 && insListCount != 0) {
            sql.append(" union ");
        }

        if (insListCount != 0) {
            sql.append(
                    " select RM3.PRD_ID PRDID, 'INS' PRDTYPE, RM3.DATA_YEARMONTH DATEMONTH, RM3.RETURN_1M ONEMONTH from TBFPS_PRD_RETURN_M RM3  ");
            sql.append(" where 1 =1  ");
            sql.append(" and RM3.PRD_ID in(:insList) and RM3.PRD_TYPE = 'INS'  ");
            sql.append(" and RM3.DATA_YEARMONTH >= :startInterval and RM3.DATA_YEARMONTH<=:endInterval ");
        } else {
            param.remove("insList");
        }

        if (sql.length() != 0) {
            sql.append(" order by PRDID desc, DATEMONTH desc");
        }
        return sql.toString();
    }

    // ===================== 理規計算 標準差 ============================
    /**
     * 執行取得 投資組合標準差 --%
     * 
     * @param dam
     * @param planId
     * @return
     */
    public static BigDecimal getStandardDeviation(DataAccessManager dam, List<Map<String, Object>> mfdEtfInsList,
            int maxMonth, int mixMonth){
        return getStandardDeviation(dam, mfdEtfInsList, maxMonth, mixMonth, false);
    }

    /**
     * 執行取得 投資組合標準差 --% 多載
     * 
     * @param dam
     * @param planId
     * @return
     */
    public static BigDecimal getStandardDeviation(DataAccessManager dam, List<Map<String, Object>> mfdEtfInsList,
            int maxMonth, int mixMonth, boolean currentEndYear){
        List<Map<String, Object>> allDataList = new ArrayList<Map<String, Object>>();
        Double[][] correlationArray = null;
        Double[][] multiplyCorrelationArray = null;
        try {
            if (CollectionUtils.isEmpty(mfdEtfInsList)) {
                return null;
            }

            // 群組化所有資料
            Map<String, List<String>> groupProdIDMap = groupProdIDList(mfdEtfInsList);

            // 來源資料有誤無法組合 例如 INS 裡面的 TAGETS 是 NULL
            if (MapUtils.isEmpty(groupProdIDMap)) {
                return null;
            }

            String year = null, month = null;
            if (currentEndYear) {
                year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1);
                month = "12";
            } else {
                year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
                year = "0".equals(month) ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1) : year;
                month = "0".equals(month) ? "12" : (month.length() == 1 ? "0" + month : month);
            }
            String currentYearMonth = year + month;

            // 取得共同區間
            List<Map<String, Object>> intervalList = getCommonIntervalList(dam, groupProdIDMap, mfdEtfInsList, maxMonth,
                    false, currentYearMonth);

            // 如果沒有共同區間 或是 不足一年(12個月)
            if (CollectionUtils.isEmpty(intervalList) || intervalList.size() < mixMonth) {
                return null;
            }

            String[] intervalStartEnd = getCommonInterval(intervalList);

            // 取得共同區間 + 所有資料
            List<Map<String, Object>> getDataResource = getYRateDataResource(dam, groupProdIDMap, mfdEtfInsList,
                    intervalStartEnd);

            if (CollectionUtils.isEmpty(getDataResource)) {
                return null;
            }

            // 處理資料並運算
            // 取得所有資料
            Map<String, List<Map<String, Object>>> fromMap = groupCombine(mfdEtfInsList, getDataResource);

            // 分配權重
            Map<String, BigDecimal> weightsMap = weightsPercent(mfdEtfInsList);

            // 資料準備
            prepareDataSource(fromMap, weightsMap, allDataList);

            // 計算相關係數
            correlationArray = getPearsonsCorrelation(allDataList);

            // 組合相關係數
            multiplyCorrelationArray = getMultiplyPearsonsCorrelation(allDataList, correlationArray);
            for (int i = 0; i < allDataList.size(); i++) {
                for (int j = 0; j < allDataList.size(); j++) {
                    System.out.println("當前 CORRELATION" + i + "" + j + ":" + new BigDecimal(
                            correlationArray[i][j] == null ? 0 : correlationArray[i][j]).setScale(8,
                                    BigDecimal.ROUND_HALF_UP));
                    System.out.println("當前 MULTIPLY_CORRELATION" + i + "" + j + ":" + new BigDecimal(
                            multiplyCorrelationArray[i][j] == null ? 0 : multiplyCorrelationArray[i][j]).setScale(8,
                                    BigDecimal.ROUND_HALF_UP));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        // 統合所有資訊
        BigDecimal sdValue = finalStandardDeviation(allDataList, multiplyCorrelationArray);
        System.out.println("標準差: " + sdValue);
        return sdValue;
    }

    /**
     * 將資料整理如下格式
     * prod1.put("id", "prod1"); prod1.put("array", doubleArray1);
     * prod1.put("weights", 0.4d); // 權重 prod1.put("monthSd", new
     * StandardDeviation().evaluate(ArrayUtils.toPrimitive(doubleArray1))); //
     * 月報酬標準差 prod1.put("yearSd", ((double)prod1.get("monthSd")) * Math.sqrt(12)
     * /100); // 年化標準差 prod1.put("yearVal", Math.pow((double)prod1.get("yearSd"),
     * 2)); // 年化變異數 prod1.put("weightPow", Math.pow((double)prod1.get("weights"),
     * 2)); // 權重平方 prod1.put("multiply", (double)prod1.get("yearVal") *
     * (double)prod1.get("weightPow")); // 乘積 prodList.add(prod1);
     * 
     * @param fromMap
     *            來源資料
     * @param weightsMap
     *            權重資料
     * @param toList
     *            整理後的資料
     */
    private static void prepareDataSource(Map<String, List<Map<String, Object>>> fromMap,
            Map<String, BigDecimal> weightsMap, List<Map<String, Object>> toList){

        for (Entry<String, List<Map<String, Object>>> entry : fromMap.entrySet()) {
            Map<String, Object> singleDataMap = new HashMap<String, Object>();
            doGetSet(singleDataMap, entry.getKey(), getSingleDataArray(entry.getValue()), weightsMap.get(entry.getKey())
                                                                                                    .doubleValue());
            toList.add(singleDataMap);
        }
        System.out.println(new Gson().toJson(fromMap));
    }

    /**
     * 單一一筆的資料轉製成 Double[]
     * 
     * @param singleList
     *            單一一筆清單
     * @return
     */
    private static Double[] getSingleDataArray(List<Map<String, Object>> singleList){
    	Collections.sort(singleList , getComparatorObj(new String[]{"DATEMONTH"}));
        Double[] singleDataArray = new Double[singleList.size()];
        int i = 0;
        for (Map<String, Object> singleMap : singleList) {
            singleDataArray[i] = ((BigDecimal) singleMap.get("ONEMONTH")).doubleValue();
            i += 1;
        }
        return singleDataArray;
    }

    /**
     * Map 格式組合
     * 
     * @param singleDataMap
     *            單一的 Map
     * @param id
     *            追蹤用 ID (就算後就沒啥用處了)
     * @param doubleArray
     *            主要資料 array
     * @param weights
     *            權重
     */
    private static void doGetSet(Map<String, Object> singleDataMap, String id, Double[] doubleArray, Double weights){
        singleDataMap.put("id", id);
        singleDataMap.put("array", doubleArray);
        singleDataMap.put("weights", weights);  // 權重
        singleDataMap.put("monthSd", new StandardDeviation().evaluate(ArrayUtils.toPrimitive(doubleArray)));  // 月報酬標準差
        singleDataMap.put("yearSd", ((double) singleDataMap.get("monthSd")) * Math.sqrt(12) / 100);   // 年化標準差
        singleDataMap.put("yearVal", Math.pow((double) singleDataMap.get("yearSd"), 2));   // 年化變異數
        singleDataMap.put("weightPow", Math.pow((double) singleDataMap.get("weights"), 2));  // 權重平方
        singleDataMap.put("multiply", (double) singleDataMap.get("yearVal") * (double) singleDataMap.get("weightPow")); // 乘積
        System.out.println("=====================" + id + "=====================");
        System.out.println("權重:       " + new BigDecimal(singleDataMap.get("weights").toString()).setScale(8,
                BigDecimal.ROUND_HALF_UP));
        System.out.println("月報酬標準差:   " + new BigDecimal(singleDataMap.get("monthSd").toString()).setScale(8,
                BigDecimal.ROUND_HALF_UP));
        System.out.println("年化標準差:  " + new BigDecimal(singleDataMap.get("yearSd").toString()).setScale(8,
                BigDecimal.ROUND_HALF_UP));
        System.out.println("年化變異數:  " + new BigDecimal(singleDataMap.get("yearVal").toString()).setScale(8,
                BigDecimal.ROUND_HALF_UP));
        System.out.println("權重平方:     " + new BigDecimal(singleDataMap.get("weightPow").toString()).setScale(8,
                BigDecimal.ROUND_HALF_UP));
        System.out.println("乘積:       " + new BigDecimal(singleDataMap.get("multiply").toString()).setScale(8,
                BigDecimal.ROUND_HALF_UP));
        System.out.println("==========================================");
    }

    /**
     * 針對對應的兩份資料進行相關係數矩陣運算 - 迴圈邏輯
     * 
     * @param allDataList
     *            所有整理過的資料
     * @return
     */
    private static Double[][] getPearsonsCorrelation(List<Map<String, Object>> allDataList){
        int size = allDataList.size();
        Double[][] correlArray = new Double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                Double[] currentArray = (Double[]) allDataList.get(i).get("array");
                Double[] nextArray = (Double[]) allDataList.get(j).get("array");
                correlArray[i][j] = correlationValue(currentArray, nextArray);
            }
        }
        return correlArray;
    }

    /**
     * 針對對應的兩份資料進行相關係數矩陣運算 - 實作
     * 
     * @param currentArray
     *            當前資料清單
     * @param nextArray
     *            下一筆資料清單
     * @return
     */
    private static Double correlationValue(Double[] currentArray, Double[] nextArray){
        return new PearsonsCorrelation().correlation(ArrayUtils.toPrimitive(currentArray), ArrayUtils.toPrimitive(
                nextArray));
    }

    /**
     * 各項權重與相關係數相乘矩陣 - 迴圈邏輯
     * 
     * @param allDataList
     *            所有整理過的資料
     * @param correlationValueArray
     *            所有資料的相關係數矩陣
     * @return
     */
    private static Double[][] getMultiplyPearsonsCorrelation(List<Map<String, Object>> allDataList,
            Double[][] correlationValueArray){
        int size = allDataList.size();
        Double[][] multiplyCorrelArray = new Double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                double iWeight = (double) allDataList.get(i).get("weights");
                double jWeight = (double) allDataList.get(j).get("weights");
                double iYearSd = (double) allDataList.get(i).get("yearSd");
                double jYearSd = (double) allDataList.get(j).get("yearSd");
                double pCorrel = correlationValueArray[i][j];
                multiplyCorrelArray[i][j] = multiplyCorrelationValue(iWeight, jWeight, iYearSd, jYearSd, pCorrel);
            }
        }
        return multiplyCorrelArray;
    }

    /**
     * 各項權重與相關係數相乘矩陣 - 實作
     * 
     * @param currentWeights
     *            當前權重
     * @param nextWeights
     *            下一組權重
     * @param currentSD
     *            當前標準差
     * @param nextSD
     *            下一組標準差
     * @param pc
     *            相關係數
     * @return
     */
    private static Double multiplyCorrelationValue(Double currentWeights, Double nextWeights, Double currentSD,
            Double nextSD, Double pc){
        return 2 * currentWeights * nextWeights * currentSD * nextSD * pc;
    }

    /**
     * 統合所有資料進行加總
     * 
     * @param allDataList
     *            所有整理過的資料
     * @param multiplyCorrelationArray
     *            相乘後的相關係數矩陣
     * @return
     */
    private static BigDecimal finalStandardDeviation(List<Map<String, Object>> allDataList,
            Double[][] multiplyCorrelationArray){
        double sum = 0.0;
        for (int i = 0; i < allDataList.size(); i++) {
            for (int j = i + 1; j < allDataList.size(); j++) {
                sum += multiplyCorrelationArray[i][j];
            }
            sum += (double) allDataList.get(i).get("multiply");
        }
        return new BigDecimal(Math.sqrt(sum)).setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 所有商品轉成 清單
     * 
     * @param prdList
     * @param key
     *            商品類型
     * @return
     */
    @Deprecated
    public static String[] getPrdArray(List<Map<String, Object>> prdList, String key){
        Set<String> prdArr = new HashSet<String>();
        for (Map<String, Object> prd : prdList) {
            if ("INS".equals(prd.get(key))) {
                prdArr.addAll(Arrays.asList(ObjectUtils.toString(prd.get("TARGETS")).split("/")));
            } else {
                prdArr.add(prd.get("PRD_ID").toString());
            }
        }

        String[] stringArray = (String[]) Arrays.copyOf(prdArr.toArray(), prdArr.toArray().length, String[].class);
        return stringArray;
    }

    // ======================= 歷史績效表現 (非特定、特定) =======================

    /**
     * 1.取得績效年度報酬率 只能取完整的年度 (共同區間傳 true) 2.只能是從前年底開始計算 (共同區間傳 true) 報酬率最多 10 年 最少
     * 1 年 (共同區間傳 10, 1) : 原因單位是年 標準差最多 3 年 最少 1年 (共同區間傳 36, 12) 計算 "年度" 報酬率 直接取
     * 前一年的 12 月份資料 (共同區間傳 true) 根據權重計算
     * 
     * @param dam
     * @param mfdEtfInsList
     * @param maxYear
     * @param mixYear
     * @return
     */
    public static List<Map<String, Object>> getReturnAnnM(DataAccessManager dam,
            List<Map<String, Object>> mfdEtfInsList, int maxYear, int mixYear){

        try {
            // 群組化所有資料
            Map<String, List<String>> groupProdIDMap = groupProdIDList(mfdEtfInsList);

            // 來源資料有誤無法組合 例如 INS 裡面的 TAGETS 是 NULL
            if (MapUtils.isEmpty(groupProdIDMap)) {
                return null;
            }

            // 分配權重
            Map<String, BigDecimal> weightsMap = weightsPercent(mfdEtfInsList);

            String year = null, month = null;
            year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1);
            month = "12";
            String currentYearMonth = year + month;

            // 取得共同區間
            List<Map<String, Object>> intervalList = getCommonIntervalList(dam, groupProdIDMap, mfdEtfInsList, maxYear,
                    true, currentYearMonth);

            // 如果沒有共同區間 或是 不足一年(1年)
            if (CollectionUtils.isEmpty(intervalList) || intervalList.size() < mixYear) {
                return null;
            }

            List<String> years = new ArrayList<String>();

            for (Map<String, Object> intervalMap : intervalList) {
                years.add(ObjectUtils.toString(intervalMap.get("DATA_YEARMONTH")));
            }

            Map<String, Object> sqlParam = new HashMap<String, Object>();
            sqlParam.put("years", years);
            sqlParam.put("prdIds", FPSUtils.getPrdArray(mfdEtfInsList, "PRD_TYPE"));

            return doQuery(dam, sqlParam, getReturnAnnMSql(getWeightByPrdId(mfdEtfInsList), sqlParam));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 這也是處理權重的分配~ 這方法也是算權重 weightsPercent 只是 INS PRD_ID 為 PRD_ID + TARGET
     * 一個map就是一個商品 key 為 prodid value為權重
     * 
     * @param mfdEtfInsList
     * @return
     */
    private static List<Map<String, Object>> getWeightByPrdId(List<Map<String, Object>> mfdEtfInsList){
        List<Map<String, Object>> weightByPrdIdList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> mfdEtfInsMap : mfdEtfInsList) {
            if ("INS".equals(mfdEtfInsMap.get("PRD_TYPE"))) {
                String[] targets = ObjectUtils.toString(mfdEtfInsMap.get("TARGETS")).split("/");
                for (String target : targets) {
                    Map<String, Object> weightMap = new HashMap<String, Object>();
                    weightMap.put(target, (new GenericMap(mfdEtfInsMap).getBigDecimal("WEIGHT")).divide(new BigDecimal(
                            targets.length), 8, BigDecimal.ROUND_HALF_UP));
                    // weightMap.put(target, (new
                    // BigDecimal((double)mfdEtfInsMap.get("WEIGHT"))).divide(new
                    // BigDecimal(targets.length), 8, BigDecimal.ROUND_HALF_UP));
                    weightByPrdIdList.add(weightMap);
                }
            } else {
            	String prdKey =  ObjectUtils.toString(mfdEtfInsMap.get("PRD_ID"));
            	Map<String, Object> weightMap = null;
            	for(Map<String, Object> tempMap : weightByPrdIdList) {
            		if(tempMap.containsKey(prdKey)) {
            			weightMap = tempMap;
            		}
            	}
                if(weightMap != null) {
                	weightMap.put(prdKey, new GenericMap(weightMap).getBigDecimal(prdKey).add(new GenericMap(mfdEtfInsMap).getBigDecimal("WEIGHT")));
                } else {
                	weightMap = new HashMap<String, Object>();
                	weightMap.put(prdKey, mfdEtfInsMap.get("WEIGHT"));
                }
                weightByPrdIdList.add(weightMap);
            }
        }
        return weightByPrdIdList;
    }

    /**
     * 取得年度報酬率 SQL
     * 
     * @param mfdEtfInsList
     * @param param
     * @return
     */
    private static String getReturnAnnMSql(List<Map<String, Object>> weightByPrdIdList, Map<String, Object> param){
        StringBuffer sql = new StringBuffer();
        sql.append(" select SUBSTR(data_yearmonth, 1, 4) YEAR, sum(decode(prd_id, ");
        int i = 0;
        for (Map<String, Object> weightByPrdIdMap : weightByPrdIdList) {
            sql.append(":prdId" + i + ", :weight" + i + " * return_1y, ");
            Set<Entry<String, Object>> entry = weightByPrdIdMap.entrySet();
            param.put("prdId" + i, entry.iterator().next().getKey());
            param.put("weight" + i, entry.iterator().next().getValue());
            i++;
        }
        sql.append(" 0))RETURN_ANN_M ");
        sql.append(" from  tbfps_prd_return_m ");
        sql.append(" where data_yearmonth in(:years) and prd_id IN (:prdIds) group by data_yearmonth ");
        return sql.toString();
    }

    /**
     * 總行推薦歷史績效 計算 入口
     * 
     * @param dam
     * @param historyList
     *            歷史資料紀錄
     * @return
     * @throws DAOException
     * @throws JBranchException
     * @throws ParseException
     */
    public static List<Map<String, Object>> getModelPortfolio(DataAccessManager dam,
            List<Map<String, Object>> historyList, String riskType) throws DAOException, JBranchException,
            ParseException{
        String year = null, month = null;
        year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
        year = "0".equals(month) ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1) : year;
        month = "0".equals(month) ? "12" : (month.length() == 1 ? "0" + month : month);
        String currentYearMonth = year + month;
        System.out.println("SENGY" + new Gson().toJson(historyList));
        List<Map<String, Object>> modelPortfolioList = getModelPortfolio(dam, historyList, riskType, currentYearMonth);

        // 整理資料
        for (Map<String, Object> historyMap : historyList) {
            Map<String, Object> modelPortfolioMap = CollectionSearchUtils.findMapInColleciton(modelPortfolioList,
                    "DATA_YEARMONTH", historyMap.get("DATA_YEARMONTH"));
            historyMap.put("RETURN_ANN", modelPortfolioMap.get("RETURN_ANN"));
            historyMap.put("VOLATILITY", modelPortfolioMap.get("VOLATILITY"));
        }

        return historyList;
    }

    /**
     * 總行推薦歷史績效 計算
     * 
     * @param dam
     * @param historyList
     *            歷史資料紀錄
     * @return
     * @throws DAOException
     * @throws JBranchException
     * @throws ParseException
     */
    public static List<Map<String, Object>> getModelPortfolio(DataAccessManager dam,
            List<Map<String, Object>> historyList, String riskType, String currentYearMonth) throws DAOException,
            JBranchException, ParseException{
        List<Map<String, Object>> modelPortfolioList = new ArrayList<Map<String, Object>>();

        // 資料分組
        // X Month ~ 今天 -1 的月份
        Map<String, List<Map<String, Object>>> reCombineMap = new HashMap<String, List<Map<String, Object>>>();
        Map<String, BigDecimal> totalPercentMap = new HashMap<String, BigDecimal>();
        for (Map<String, Object> historyMap : historyList) {
            String keyMonth = ObjectUtils.toString(historyMap.get("DATA_YEARMONTH"));
            List<Map<String, Object>> reCombineList = null;
            if (reCombineMap.containsKey(keyMonth)) {
                reCombineList = reCombineMap.get(keyMonth);
            } else {
                reCombineList = new ArrayList<Map<String, Object>>();
                reCombineMap.put(keyMonth, reCombineList);
                totalPercentMap.put(keyMonth, BigDecimal.ZERO);
            }
            reCombineList.add(historyMap);
            totalPercentMap.put(keyMonth, totalPercentMap.get(keyMonth).add((BigDecimal) historyMap.get(
                    "INV_PERCENT")));

        }

        // 分配後權重重算
        Map<String, Map<String, BigDecimal>> weightMap = new HashMap<String, Map<String, BigDecimal>>();

        List<Map<String, Object>> returnModelPortfolioList = new ArrayList<Map<String, Object>>();
        System.out.println("SENGY" + new Gson().toJson(reCombineMap));
        for (Entry<String, List<Map<String, Object>>> entry : reCombineMap.entrySet()) {
            Map<String, Object> returnModelPortfolioMap = new HashMap<String, Object>();
            String monthKey = entry.getKey();
            List<Map<String, Object>> mfdEtfInsList = entry.getValue();
            weightMap.put(monthKey, weightsPercent(mfdEtfInsList, totalPercentMap.get(monthKey)));

            // 群組化所有資料
            Map<String, List<String>> groupProdIDMap = groupProdIDList(mfdEtfInsList);

            // 來源資料有誤無法組合 例如 INS 裡面的 TAGETS 是 NULL
            if (MapUtils.isEmpty(groupProdIDMap)) {
                returnModelPortfolioMap.put("DATA_YEARMONTH", monthKey);
                returnModelPortfolioMap.put("CUST_RISK_ATR", riskType);
                returnModelPortfolioMap.put("RETURN_ANN", null);
                modelPortfolioList.add(returnModelPortfolioMap);
                continue;
            }

            // 取得共同區間 + 所有資料
            // 添加判斷 (如果開始時間大於當前時間 兩個都是當前時間)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            Date bt = sdf.parse(monthKey);
            Date et = sdf.parse(currentYearMonth);
            List<Map<String, Object>> getDataResource = getYRateDataResource(dam, groupProdIDMap, mfdEtfInsList,
                    new String[] { monthKey, currentYearMonth });

            if (CollectionUtils.isEmpty(getDataResource)) {
                returnModelPortfolioMap.put("DATA_YEARMONTH", monthKey);
                returnModelPortfolioMap.put("CUST_RISK_ATR", riskType);
                returnModelPortfolioMap.put("RETURN_ANN", null);
                modelPortfolioList.add(returnModelPortfolioMap);
                continue;
            }

            // 取得共同區月數
            int startYear = Integer.parseInt((bt.before(et) ? monthKey : currentYearMonth).substring(0, 4));
            int startMonth = Integer.parseInt((bt.before(et) ? monthKey : currentYearMonth).substring(4));
            int endYear = Integer.parseInt(currentYearMonth.substring(0, 4));
            int endMonth = Integer.parseInt(currentYearMonth.substring(4));
            
            

            int result;
            if (startYear == endYear) {
                result = endMonth - startMonth;
            } else {
                result = 12 * (endYear - startYear) + endMonth - startMonth;
            }

            // 處理資料並運算
            // 先分類
            // {0225=[
            // {DATEMONTH=201804, PRDTYPE=MFD, PRDID=0225, ONEMONTH=-0.0765},
            // {DATEMONTH=201803, PRDTYPE=MFD, PRDID=0225, ONEMONTH=-0.0765}],
            // ULPADSD1=[
            // {DATEMONTH=201804, PRDTYPE=INS, PRDID=DSD1, ONEMONTH=-0.77},
            // {DATEMONTH=201803, PRDTYPE=INS, PRDID=DSD1, ONEMONTH=-0.26}]
            Map<String, List<Map<String, Object>>> groupMap = groupCombine(mfdEtfInsList, getDataResource);
            double[] mRateArr = new double[result + 1];
            int i = 0; // 年份比數
            
            if(startMonth == 9) {
            	System.out.println("SENGY" + new Gson().toJson(groupMap));
            }

            // 計算每一期的月報酬率 (算完權重了)
            for (Entry<String, List<Map<String, Object>>> allEntry : groupMap.entrySet()) {
            	Collections.sort(allEntry.getValue() , getComparatorObj(new String[]{"DATEMONTH"}));
                for (Map<String, Object> allData : allEntry.getValue()) {
                    mRateArr[i] += ((BigDecimal) allData.get("ONEMONTH")).divide(new BigDecimal(100))
                                                                         .multiply(weightMap.get(monthKey).get(allEntry
                                                                                                                       .getKey()))
                                                                         .doubleValue();
                    i++;
                }
                i = 0;
            }

            // 計算累計報酬率 = (1+月報/100) * (1+月報/100) -1
            double yTempRate = 1d;
            for (Double dMRate : mRateArr) {
                yTempRate = yTempRate * (dMRate + 1);
            }

            // 計算標準差 = 每期報酬率 計算標準差 開根號 12 月
            double std = 0.0d;
            std = new StandardDeviation().evaluate(mRateArr);

            returnModelPortfolioMap.put("DATA_YEARMONTH", monthKey);
            returnModelPortfolioMap.put("CUST_RISK_ATR", riskType);
            returnModelPortfolioMap.put("RETURN_ANN", new BigDecimal(yTempRate - 1).multiply(new BigDecimal(100))
                                                                                   .setScale(4,
                                                                                           BigDecimal.ROUND_HALF_UP));
            returnModelPortfolioMap.put("VOLATILITY", std * Math.sqrt(12) * 100);
            modelPortfolioList.add(returnModelPortfolioMap);
        }

        return modelPortfolioList;
    }

    /**
     * 績效追蹤即時計算 含息報酬率 市值(含息) / 本金 -1
     * 
     * @param interestMarketPrice
     *            市值(含息)
     * @param principal
     *            本金
     * @return
     */
    public static BigDecimal getInterestReturnRate(BigDecimal interestMarketPrice, BigDecimal principal){
        BigDecimal interestReturnRate = BigDecimal.ZERO;
        if (BigDecimal.ZERO.equals(principal)) {
            return BigDecimal.ZERO;
        } else {
            interestReturnRate = interestMarketPrice.divide(principal, 6, BigDecimal.ROUND_HALF_UP).subtract(
                    BigDecimal.ONE);
            return interestReturnRate.multiply(new BigDecimal(100));
        }
    }

    /**
     * 績效追蹤即時計算 應達目標 公式 目標金額 - 庫存 總投資天期的 前半 * 0.45 * (已投資天數/總投資天期的一半) 0.45 100% +
     * 後半 *0.55 * ((已投資天數-總投資天期的一半)/總投資天期的一半)
     * 
     * @param startDate
     * @param targetMoney
     * @param stock
     * @param year
     * @return
     */
    public static BigDecimal getAchievementTarget(Date startDate, BigDecimal targetMoney, BigDecimal stock,
            BigDecimal futureQuota, int year){
        BigDecimal achievementTarget = BigDecimal.ZERO;
        long halfDate = year * 365 / 2 + 1; // 二分法的日期
        long pastDay = (new Date().getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
        pastDay = (pastDay - 2) > 0 ? (pastDay - 2) : 0;
        BigDecimal investmentMoney = targetMoney.subtract(stock).subtract(futureQuota); // 投資可操作的
                                                                                        // $$
        BigDecimal fortyFive = investmentMoney.multiply(new BigDecimal(0.45));
        BigDecimal fiftyFive = investmentMoney.multiply(new BigDecimal(0.55));

        if (pastDay <= halfDate) {
            // 0.45 的 目標 * 已投天數 / 一半日期
            achievementTarget = fortyFive.multiply(new BigDecimal(pastDay)).divide(new BigDecimal(halfDate), 6,
                    BigDecimal.ROUND_HALF_UP);
        } else {
            // 0.45 的 目標 * 1 + 0.55 的 目標 * (已投天數-一半日期) / 一半日期
            achievementTarget = fortyFive.add(fiftyFive.multiply(new BigDecimal(pastDay - halfDate)).divide(
                    new BigDecimal(halfDate), 6, BigDecimal.ROUND_HALF_UP));
        }

        return achievementTarget.add(stock);
    }

    /**
     * 績效追蹤 達成率
     * 
     * @param interestMarketPrice
     * @param achievementTarget
     * @return
     */
    public static BigDecimal getAchievementRate(BigDecimal interestMarketPrice, BigDecimal achievementTarget){
        BigDecimal achievementRate = BigDecimal.ZERO;
        if (BigDecimal.ZERO.compareTo(achievementTarget) == 0) {
            return BigDecimal.ZERO;
        } else {
            achievementRate = interestMarketPrice.divide(achievementTarget, 6, BigDecimal.ROUND_HALF_UP).multiply(
                    new BigDecimal(100));
            return achievementRate;
        }
    }

    /**
     * 取得 投資年期
     * 
     * @param dam
     * @param planId
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public static int getInvestmentYear(DataAccessManager dam, String planId) throws DAOException, JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT INV_PERIOD FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD");
        sb.append(" WHERE PLAN_ID = :planID");

        qc.setObject("planID", planId);
        qc.setQueryString(sb.toString());

        List<Map<String, Object>> resultList = dam.exeQuery(qc);
        int year = 0;
        if (CollectionUtils.isNotEmpty(resultList)) {
            GenericMap gmap = new GenericMap(resultList.get(0));
            year = gmap.getBigDecimal("INV_PERIOD").intValue();
        }
        return year;
    }

    /**
     * 取得未來投入金額 (未購入 + 已購入)
     * 
     * @param futureQuoatList
     * @param monthCount
     * @param purchaseFutureValue
     * @return
     */
    public static BigDecimal getFutureQuota(List<Map<String, Object>> futureQuoatList, int monthCount,
            BigDecimal purchaseFutureValue){
        BigDecimal futureQuota = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(futureQuoatList)) {
            for (Map<String, Object> futureQuotaMap : futureQuoatList) {
            	if(futureQuotaMap.get("PURCHASE_TWD_AMT") != null) {
	                if ("2".equals(ObjectUtils.toString(futureQuotaMap.get("INV_TYPE")))) {
	                    futureQuota = futureQuota.add(((BigDecimal) futureQuotaMap.get("PURCHASE_TWD_AMT")).multiply(
	                            new BigDecimal(monthCount)));
	                } else {
	                    futureQuota = futureQuota.add(((BigDecimal) futureQuotaMap.get("PURCHASE_TWD_AMT")));
	                }
	            }
            }
        }

        // 未購入 + 已購入
        futureQuota = futureQuota.add(purchaseFutureValue);
        return futureQuota;
    }

    /**
     * 取得剩餘月份
     * 
     * @param startDate
     * @param targetYear
     * @return
     */
    public static int getRemainingMonth(Date startDate, int targetYear){
        // 日期處理 今日的下個月開始
        // 投資到期日 (開始日 + targetYear - 一天)
        Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(startDate);
        targetDate.add(Calendar.YEAR, targetYear);

        Calendar nextDate = Calendar.getInstance();
        nextDate.setTime(new Date());
        nextDate.add(Calendar.MONTH, 1);

        int year = targetDate.get(Calendar.YEAR) - nextDate.get(Calendar.YEAR);
        int month = targetDate.get(Calendar.MONTH) - nextDate.get(Calendar.MONTH);

        int monthCount = year * 12 + month;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (sdf.format(new Date()).equals(sdf.format(startDate))) {
            monthCount += 1;
        }

        return monthCount;
    }

    public static Date getPurchasedDate(DataAccessManager dam, String planId) throws DAOException, JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT CREATETIME FROM TBFPS_SPP_PRD_RETURN_HEAD");
        sb.append(" WHERE PLAN_ID = :planID");
        qc.setObject("planID", planId);
        qc.setQueryString(sb.toString());

        List<Map<String, Object>> resultList = dam.exeQuery(qc);
        Date createDate = new Date();
        if (CollectionUtils.isNotEmpty(resultList)) {
            GenericMap gmap = new GenericMap(resultList.get(0));
            createDate = gmap.getDate("CREATETIME");
        }
        return createDate;
    }

    /**
     * get spp table query 未下單資料
     * 
     * @param dam
     * @param planID
     * @param CustID
     * @param orderStatus
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getHistory(DataAccessManager dam, String planID, String CustID,
            String orderStatus) throws DAOException, JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        // 主查詢
        sb.append(" WITH GEN AS ( ");
        sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_1' ");
        sb.append(" ), ");
        sb.append(" SML AS ( ");
        sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_2' ");
        sb.append(" ), ");
        sb.append(" SYSPAR AS ( ");
        sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FPS.FUND_TYPE' ");
        sb.append(" ), ");
        sb.append(" AREA AS ( ");
        sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.MKT_TIER3' ");
        sb.append(" ), ");
        sb.append(" TREND AS ( ");
        sb.append(" SELECT TRD.TYPE, TRD.TREND ");
        sb.append(" FROM TBFPS_MARKET_TREND TR ");
        sb.append(" LEFT JOIN TBFPS_MARKET_TREND_DETAIL TRD ON TR.PARAM_NO = TRD.PARAM_NO ");
        sb.append(" WHERE TR.STATUS = 'A' ");
        sb.append(" ), ");
        sb.append(" BASE_INS AS ( ");
        sb.append(" SELECT DISTINCT BASE.PRD_ID, BASE.CURR_CD, INSINFO.BASE_AMT_OF_PURCHASE, ");
        sb.append(" LISTAGG(BASE.KEY_NO, ',') WITHIN GROUP (ORDER BY BASE.KEY_NO) AS KEY_NO ");
        sb.append(" FROM TBPRD_INS BASE ");
        sb.append(" LEFT JOIN TBPRD_INSINFO INSINFO ON BASE.PRD_ID = INSINFO.PRD_ID ");
        sb.append(" WHERE BASE.INS_TYPE IN ('1', '2') GROUP BY BASE.PRD_ID, BASE.CURR_CD, INSINFO.BASE_AMT_OF_PURCHASE) ");
        sb.append(" SELECT ");
        sb.append(" S.PLAN_ID, S.SEQNO, S.PTYPE, S.PRD_ID, ");
        sb.append(" CASE WHEN S.PTYPE = 'MFD' THEN '1' ");
        sb.append(" 	 WHEN S.PTYPE = 'INS' THEN '2' ");
        sb.append(" 	 END  AS P_TYPE, ");
        sb.append(" CASE WHEN S.PTYPE = 'MFD' AND SYSDATE BETWEEN FUIN.MAIN_PRD_SDATE AND FUIN.MAIN_PRD_EDATE THEN 'Y' ");
        sb.append("   WHEN S.PTYPE = 'MFD' AND SYSDATE BETWEEN FUIN.RAISE_FUND_SDATE AND FUIN.RAISE_FUND_EDATE THEN 'Y' ");
        sb.append("   WHEN S.PTYPE = 'INS' AND VWP.PRD_RANK IS NOT NULL THEN 'Y' ");
        sb.append("   ELSE 'N' END AS MAIN_PRD, ");
        sb.append(" VWP.PNAME AS PRD_CNAME, VWP.RISKCATE_ID AS RISK_TYPE, ");
        sb.append(" VWP.CURRENCY_STD_ID AS CURRENCY_TYPE, ");
        sb.append(" S.TRUST_CURR, S.PURCHASE_ORG_AMT, S.PURCHASE_TWD_AMT, ");
        sb.append(" S.PORTFOLIO_RATIO, S.LIMIT_ORG_AMT, ");
        sb.append(" S.TXN_TYPE, S.INV_TYPE, S.EX_RATE, TREND.TREND AS CIS_3M, ");
        sb.append(" CASE S.PTYPE ");
        sb.append(" WHEN 'MFD' THEN GEN.PARAM_NAME ");
        sb.append(" WHEN 'INS' THEN TO_CHAR(BASE_INS.BASE_AMT_OF_PURCHASE) ");
        sb.append(" ELSE NULL END AS GEN_SUBS_MINI_AMT_FOR, ");
        sb.append(" SML.PARAM_NAME AS SML_SUBS_MINI_AMT_FOR, ");
        sb.append(" FU.FUND_TYPE, ");
        sb.append(" SYSPAR.PARAM_NAME AS FUND_TYPE_NAME, ");
        sb.append(" FU.INV_TARGET AS MF_MKT_CAT, ");
        sb.append(" AREA.PARAM_NAME AS NAME, ");
        sb.append(" CASE WHEN S.PTYPE = 'INS' THEN BASE_INS.KEY_NO ");
        sb.append("  ELSE NULL END AS KEY_NO, ");
        sb.append(" S.PURCHASE_ORG_AMT_ORDER,S.PURCHASE_TWD_AMT_ORDER, S.TARGETS ");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD SH ");
        sb.append(" INNER JOIN TBFPS_PORTFOLIO_PLAN_SPP S ON S.PLAN_ID = SH.PLAN_ID ");
        sb.append(" LEFT JOIN VWPRD_MASTER VWP ON VWP.PRD_ID = S.PRD_ID AND VWP.PTYPE = S.PTYPE AND VWP.CURRENCY_STD_ID = S.CURRENCY_TYPE ");
        sb.append(" LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = S.PRD_ID ");
        sb.append(" LEFT JOIN TBPRD_FUNDINFO FUIN ON FUIN.PRD_ID = FU.PRD_ID ");
        sb.append(" LEFT JOIN GEN ON GEN.PARAM_CODE = FU.CURRENCY_STD_ID ");
        sb.append(" LEFT JOIN SML ON SML.PARAM_CODE = FU.CURRENCY_STD_ID ");
        sb.append(" LEFT JOIN SYSPAR ON SYSPAR.PARAM_CODE = FU.FUND_TYPE ");
        sb.append(" LEFT JOIN TREND ON TREND.TYPE = FU.INV_TARGET ");
        sb.append(" LEFT JOIN AREA ON AREA.PARAM_CODE = TREND.TYPE ");
        sb.append(" LEFT JOIN BASE_INS ON BASE_INS.PRD_ID = S.PRD_ID AND BASE_INS.CURR_CD = S.CURRENCY_TYPE ");
        sb.append(" WHERE SH.CUST_ID = :CustID ");
        sb.append(" AND SH.PLAN_ID = :planID ");

        sb.append("AND (S.ORDER_STATUS != 'Y' OR S.ORDER_STATUS IS NULL) ");
        sb.append(" ORDER BY P_TYPE ");

        // 查詢結果
        qc.setObject("CustID", CustID);
        qc.setObject("planID", planID);
        qc.setQueryString(sb.toString());
        
        List<Map<String, Object>> list = dam.exeQuery(qc);
        
       	return list;
    }

    /**
     * 取得已購入的憑證編號 (定額的)
     * 
     * @param dam
     * @param planId
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public static List<String> getCertificateIdList(DataAccessManager dam, String planId) throws DAOException,
            JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer();
        sql.append(" with tempBATCHSEQ as ( ");
        sql.append(" select distinct PD.BATCH_SEQ from TBSOT_NF_PURCHASE_D PD where 1=1");
        sql.append(" and PD.TRADE_SUB_TYPE = '2' ");
        sql.append(" and PD.BATCH_SEQ in (select BATCH_SEQ from TBFPS_PLANID_MAPPING where plan_id = :planId)) "); // --
                                                                                                                   // 找出是定額的
        sql.append(
                " select CERTIFICATE_ID from TBFPS_PLANID_MAPPING where BATCH_SEQ in(select * from tempBATCHSEQ) and plan_id = :planId ");

        qc.setObject("planId", planId);
        qc.setQueryString(sql.toString());
        List<Map<String, Object>> resultList = dam.exeQuery(qc);

        List<String> certificateIdList = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (Map<String, Object> resultMap : resultList) {
                certificateIdList.add(ObjectUtils.toString(resultMap.get("CERTIFICATE_ID")));
            }
        }

        return certificateIdList;
    }

    /**
     * get spp table query 已下單資料
     * 
     * @param dam
     * @param custId
     * @param cretList
     * @param monthCount
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public static BigDecimal getPurchaseFutureValue(DataAccessManager dam, String custId, List<String> cretList,
            int monthCount) throws DAOException, JBranchException{
        if (CollectionUtils.isEmpty(cretList))
            return BigDecimal.ZERO;

        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer();
        sql.append(
                " select sum(INV_COST_TWD) SUM_INV_COST_TWD from TBCRM_AST_INV_DTL where 1=1 and TXN_TYPE = '1' and cust_id = :custId ");
        sql.append(
                " and DATA_DATE between LAST_DAY(ADD_MONTHS(trunc(SYSDATE), -2)) + 1 and LAST_DAY(ADD_MONTHS(trunc(SYSDATE), -1)) ");
        sql.append(" and cert_nbr in (:cretList) group by CERT_NBR ");

        qc.setObject("custId", custId);
        qc.setObject("cretList", cretList);
        qc.setQueryString(sql.toString());
        List<Map<String, Object>> resultList = dam.exeQuery(qc);

        BigDecimal futureValue = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (Map<String, Object> resultMap : resultList) {
                BigDecimal sumInvCostTWD = new GenericMap(resultMap).getBigDecimal("SUM_INV_COST_TWD");
                futureValue = futureValue.add(sumInvCostTWD.multiply(new BigDecimal(monthCount)));
            }
        }

        return futureValue;
    }

    /**
     * 取得 前一個月下單的資料 各品項單筆分開計算
     * 
     * @param dam
     * @param custId
     * @param cretList
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public static List<Map<String, Object>> getPurchaseFutureList(DataAccessManager dam, String custId,
            List<String> cretList) throws DAOException, JBranchException{
        if (CollectionUtils.isEmpty(cretList))
            return new ArrayList<Map<String, Object>>();

        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer();
        sql.append(" with TEMP_COST_TWD as ( ");
        sql.append(" select sum(INV_COST_TWD) SUM_INV_COST_TWD, CERT_NBR from TBCRM_AST_INV_DTL ");
        sql.append(" where 1=1 and TXN_TYPE = '1' and cust_id = :custId ");
        sql.append(
                " and DATA_DATE between LAST_DAY(ADD_MONTHS(trunc(SYSDATE), -2)) + 1 and LAST_DAY(ADD_MONTHS(trunc(SYSDATE), -1)) ");
        sql.append(" and cert_nbr in (:cretList) group by CERT_NBR), ");
        sql.append(" TEMP_PROD_CERT as( ");
        sql.append(
                " select PROD_ID, CERT_NBR from MVFPS_AST_ALLPRD_DETAIL where cust_id = :custId and CERT_NBR in (:cretList)) ");
        sql.append(
                " select TEMP_PROD_CERT.PROD_ID PRD_ID, sum(TEMP_COST_TWD.SUM_INV_COST_TWD) PURCHASE_TWD_AMT from TEMP_PROD_CERT ");
        sql.append(" inner join TEMP_COST_TWD on TEMP_PROD_CERT.CERT_NBR = TEMP_COST_TWD.CERT_NBR ");
        sql.append(" group by TEMP_PROD_CERT.PROD_ID ");

        qc.setObject("custId", custId);
        qc.setObject("cretList", cretList);
        qc.setQueryString(sql.toString());
        List<Map<String, Object>> resultList = dam.exeQuery(qc);

        return CollectionUtils.isNotEmpty(resultList) ? resultList : new ArrayList<Map<String, Object>>();
    }

    /**
     * 實際調用 PortRtnSim 前需要先進行資料整理 & 計算
     * 
     * @param dam
     * @param mfdInsList
     * @param custId
     * @param planId
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public static Map<String, Object> beforeDoPortRtnSim(DataAccessManager dam, List<Map<String, Object>> mfdInsList,
            String custId, String planId) throws DAOException, JBranchException{
        List<Map<String, Object>>[] groupArray = doGroupSinglePeriod(mfdInsList);
        List<String> cretList = getCertificateIdList(dam, planId);
        List<Map<String, Object>> dbList = getPurchaseFutureList(dam, custId, cretList);

        BigDecimal[] totalArray = getTotalSinglePeriod(groupArray[0], groupArray[1], dbList);
        List<Map<String, Object>>[] newDataArray = resetAllData(groupArray, dbList, totalArray);

        Map<String, Object> prepareDataMap = new HashMap<String, Object>();
        prepareDataMap.put("purchasedValueArray", totalArray);
        prepareDataMap.put("purchasedListArray", newDataArray);

        return prepareDataMap;
    }

    /**
     * 分組
     * 單筆組 / 定額組
     * 
     * @param mfdInsList
     * @return
     */
    public static List<Map<String, Object>>[] doGroupSinglePeriod(List<Map<String, Object>> mfdInsList){
        List<Map<String, Object>>[] groupArray = new ArrayList[2];
        List<Map<String, Object>> singleList = new ArrayList<Map<String, Object>>(); // 已購入定 & 單筆 ( 已購 + 未購 )
        List<Map<String, Object>> periodList = new ArrayList<Map<String, Object>>(); // 定 ( 已購 + 未購 )

        for (Map<String, Object> mfdInsMap : mfdInsList) {
            // 是定額 我只管未購
        	if("2".equals(ObjectUtils.toString(mfdInsMap.get("INV_TYPE")))) {
        		if(StringUtils.isNotEmpty(ObjectUtils.toString(mfdInsMap.get("ORDER_STATUS")))) {
        			singleList.add(mfdInsMap);
        		} else {
        			periodList.add(mfdInsMap);
        		}
        	} else {
        		singleList.add(mfdInsMap);
        	}
        }
        groupArray[0] = singleList;
        groupArray[1] = periodList;
        return groupArray;
    }

    /**
     * 計算單筆/定額的總金額
     * 單筆為 單筆(未購 + 已購) + 定額(已購庫存)
     * 定額為 撈取交易明細資料
     * 
     * @param singleList1
     *            所有認列的單筆 >> 單筆(未購 + 已購) + 定額(已購庫存)
     * @param dbList2
     *            撈取交易明細資料的結果
     * @return
     */
    public static BigDecimal[] getTotalSinglePeriod(List<Map<String, Object>> singleList,
            List<Map<String, Object>> periodList, List<Map<String, Object>> dbList){
        BigDecimal[] totalPurchaseArray = new BigDecimal[2];
        // 單筆的總和
        BigDecimal totalPurchaseSingle = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(singleList)) {
            for (Map<String, Object> singleMap : singleList) {
                totalPurchaseSingle = totalPurchaseSingle.add(new GenericMap(singleMap).getBigDecimal(
                        "PURCHASE_TWD_AMT"));
            }
        }

        // 定額的總和 (從交易明細來)
        BigDecimal totalPurchasePeriod = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(dbList)) {
            for (Map<String, Object> dbMap : dbList) {
                totalPurchasePeriod = totalPurchasePeriod.add(new GenericMap(dbMap).getBigDecimal("PURCHASE_TWD_AMT"));
            }
        }

        if (CollectionUtils.isNotEmpty(periodList)) {
            for (Map<String, Object> periodMap : periodList) {
                totalPurchasePeriod = totalPurchasePeriod.add(new GenericMap(periodMap).getBigDecimal(
                        "PURCHASE_TWD_AMT"));
            }
        }

        totalPurchaseArray[0] = totalPurchaseSingle;
        totalPurchaseArray[1] = totalPurchasePeriod;
        return totalPurchaseArray;
    }

    /**
     * 重新產生新的格式資料
     * 用於可以直接套公用 Function
     * 
     * @param groupArray
     *            舊資料
     * @param dbList
     *            資料庫交易明細
     * @param totalPurchaseArray
     *            總和資料
     * @return
     */
    public static List<Map<String, Object>>[] resetAllData(List<Map<String, Object>>[] groupArray,
            List<Map<String, Object>> dbList, BigDecimal[] totalPurchaseArray){
        List<Map<String, Object>>[] newGroupArray = new ArrayList[2];
        List<Map<String, Object>> newGroupSingleList = new ArrayList<Map<String, Object>>(); // 單筆資料新容器
        List<Map<String, Object>> newGroupPeriodList = new ArrayList<Map<String, Object>>(); // 定額資料新容器

        List<Map<String, Object>> groupSingleList = groupArray[0]; // 單筆資料
        List<Map<String, Object>> groupPeriodList = groupArray[1]; // 定額資料

        // 建立新的資料來源
        // 單筆
        if (totalPurchaseArray[0].compareTo(BigDecimal.ZERO) == 1) {
            newGroupValueSet(groupSingleList, newGroupSingleList, totalPurchaseArray[0], false);
        }

        // 定額
        if (totalPurchaseArray[1].compareTo(BigDecimal.ZERO) == 1) {
            newGroupValueSet(groupPeriodList, newGroupPeriodList, totalPurchaseArray[1], false);
            newGroupValueSet(dbList, newGroupPeriodList, totalPurchaseArray[1], true);
        }

        newGroupArray[0] = newGroupSingleList;
        newGroupArray[1] = newGroupPeriodList;
        return newGroupArray;
    }

    /**
     * 產生共同使用格式
     * 
     * @param oldList
     * @param newList
     * @param totalPurchase
     * @param isDB
     */
    private static void newGroupValueSet(List<Map<String, Object>> oldList, List<Map<String, Object>> newList,
            BigDecimal totalPurchase, boolean isDB){
        for (Map<String, Object> oldMap : oldList) {
            Map<String, Object> newGroupMap = new HashMap<String, Object>();
            newGroupMap.put("PRD_ID", oldMap.get("PRD_ID"));
            newGroupMap.put("PRD_TYPE", isDB ? "MFD" : oldMap.get("PRD_TYPE"));
            newGroupMap.put("TARGETS", isDB ? null : oldMap.get("TARGETS"));
            newGroupMap.put("WEIGHT", new GenericMap(oldMap).getBigDecimal("PURCHASE_TWD_AMT").divide(totalPurchase, 6,
                    BigDecimal.ROUND_HALF_UP));
            newList.add(newGroupMap);
        }
    }

    /**
     * 統一郵件發送觸發
     * 
     * @param result
     * @param email
     * @param mailContent
     * @return
     * @throws Exception
     */
    public static boolean sendMain(DataAccessManager dam, List<Map<String, Object>> result, String email, String[] mailContent) throws Exception{
        boolean isSuccess = false;
        if (CollectionUtils.isNotEmpty(result)) {
            FubonSendJavaMail send = new FubonSendJavaMail();
            FubonMail mail = new FubonMail();

            Map<String, Object> data = new HashMap<>();
//            String fileName = ObjectUtils.toString(result.get(0).get("FILE_NAME"));
            String fileName = mailContent[0].replace("/", "").replace(" ", "") + ".pdf";
            Blob filePDF = (Blob) result.get(0).get("PLAN_PDF_FILE");
            data.put(MimeUtility.encodeText(fileName), ObjectUtil.blobToByteArr(filePDF));

            // 設定收件者
            if (StringUtils.isBlank(email))
                throw new APException("沒有聯絡資訊!");

            // Email格式檢查
            if (FPSUtils.isEmail(email) == false) {
                throw new APException("信箱Email格式錯誤");
            }

            // Email 格式正確則寄信
            mail.setSubject(mailContent[0]);
//            mail.setFromMail("Nkstock@fbt.com");
//            mail.setFromName("台北富邦銀行");
            
            // 取得寄件者email與地點名稱
            QueryConditionIF sender_mail = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            sender_mail.setQueryString(" SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SYS.MAIL' AND PARAM_CODE = 'from' ");
            List<Map<String, String>> sender_mail_list = dam.exeQuery(sender_mail);
            String[] sender_mail_data = ObjectUtils.toString(sender_mail_list.get(0).get("PARAM_NAME")).split(",");
            
            Map<String, Object> annexData = new HashMap<String, Object>();
            mail.setFromName(sender_mail_data[1]);
            mail.setFromMail(sender_mail_data[0]);

            List<Map<String, String>> lstMailTo = new ArrayList<Map<String, String>>();
            Map<String, String> mailMap = new HashMap<String, String>();
            mailMap.put(FubonSendJavaMail.MAIL, email);
            lstMailTo.add(mailMap);
            mail.setLstMailTo(lstMailTo);

            mail.setContent(mailContent[1]);
            send.sendMail(mail, data);

            isSuccess = true;
            return isSuccess;

        } else {
            throw new APException("無可下載的檔案"); // 無可下載的檔案
        }
    }

    /**
     * Email 資料內容格式化處理
     * 
     * @param type
     * @return
     */
    public static String[] getMailContent(int type){
        String[] mailContent = new String[2];
        String subject = "";
        StringBuffer content = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String today = sdf.format(new Date());
        switch (type) {
        case 0: {
            subject = "全資產理財規劃書";
            break;
        }
        case 1: {
            subject = "目標理財規劃書";
            break;
        }
        case 2: {
            subject = "目標理財績效報告書";
            break;
        }
        }

        content.append("<P>").append("親愛的客戶您好，提供您");
        content.append(subject);
        content.append("參閱(如附件)，").append("</p>");
        content.append("<P>").append("如有需要，您可聯繫所屬的理財專員檢視既有的資產配置。").append("</p>");
        content.append("<br/>");
        content.append("<P style=\"margin-left: 305px;\">").append("台北富邦商業銀行 敬啟").append("</p>");

        mailContent[0] = today + " " + subject;
        mailContent[1] = content.toString();
        return mailContent;
    }

    // 給效率前緣用的 (暫時保留)
    @Deprecated
    public static BigDecimal getYRateByModelProtfolio(Map<String, BigDecimal> weightMap, String[] rtnPrd,
            double[][] rateArrays){
        BigDecimal yRateValue = BigDecimal.ZERO;
        int i = 0;
        for (double[] onePrdRateArrays : rateArrays) {
            String prdKey = rtnPrd[i];
            System.out.println(prdKey);
            // 累進報酬率
            BigDecimal yRateGrandTotal = getYRate(onePrdRateArrays);
            System.out.println("累進報酬率 :" + yRateGrandTotal);

            // 要開根號的
            BigDecimal divideYear = new BigDecimal(12).divide(new BigDecimal(onePrdRateArrays.length), 6,
                    BigDecimal.ROUND_HALF_UP);
            System.out.println("要開根號的 :" + divideYear);

            // 年化報酬率
            BigDecimal yRateYear = new BigDecimal(Math.pow((BigDecimal.ONE.add(yRateGrandTotal)).doubleValue(),
                    divideYear.doubleValue())).subtract(BigDecimal.ONE).setScale(6, BigDecimal.ROUND_HALF_UP);
            System.out.println("年化報酬率 :" + yRateYear);

            // 權重
            System.out.println("權重 :" + weightMap.get(prdKey).setScale(6, BigDecimal.ROUND_HALF_UP));

            // 年化 * 權重
            yRateValue = yRateValue.add(yRateYear.multiply(weightMap.get(prdKey)));
            System.out.println("年化 * 權重 :" + yRateYear.multiply(weightMap.get(prdKey)).setScale(6,
                    BigDecimal.ROUND_HALF_UP));

            i++;
        }
        return yRateValue;
    }

    // 單純的 double[]計算 年化報酬率
    // 給效率前緣用的 (暫時保留)
    @Deprecated
    private static BigDecimal getYRate(double[] rateArrays){
        BigDecimal yRate = BigDecimal.ZERO;

        // 要先排序 >> 原因如果大於 120 個月，取最近的120個月 (10年)
        // Collections.sort(yRateDataResourceList , getComparatorObj(new
        // String[]{"DATEMONTH"}));

        // 只有一年單純處理 new BigDecimal(rateArrays[0])
        BigDecimal firstYear = (new BigDecimal(rateArrays[0])).divide(new BigDecimal(100));
        if (rateArrays.length == 1)
            return firstYear;
        yRate = firstYear;

        // 接下來第二年都使用第一年的加一後與當年的計算
        for (int i = 1; i < rateArrays.length; i++) {
            // 超過十年(120個月)後面的不算
            if (i >= 120)
                break;
            yRate = BigDecimal.ONE.add(yRate);
            BigDecimal currentYear = BigDecimal.ONE.add((new BigDecimal(rateArrays[i])).divide(new BigDecimal(100)));
            yRate = yRate.multiply(currentYear).subtract(BigDecimal.ONE);
        }
        return yRate.setScale(6, BigDecimal.ROUND_HALF_UP);
    }

    public static Comparator getComparatorObj(final String [] keys){
		return new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int result = 0;
				
				for(String key : keys){
					result = ObjectUtils.toString(o1.get(key)).compareTo(ObjectUtils.toString(o2.get(key)));
					
					if(result != 0){
						return result;
					} 
				}
				
				return result;
			}
		};
	}
    
    /** 贖回門檻 (全資產 + 績效追蹤 共用)*/
    
    // Merge LIST 跟 取得的贖回 list 
    public static void mergeRedemption(DataAccessManager dam, List<Map<String, Object>> list) throws DAOException, JBranchException {
	  Map<String, String> mapping = new HashMap<String, String>();
  	  mapping.put("MFD", "TBPRD_FUND");
  	  mapping.put("ETF", "TBPRD_ETF");
  	  Map<String, List<String>> getProdMap = getProdMap(list);
  	  
  	  for(Entry<String, String> entry : mapping.entrySet()) {
  		  Map<String, BigDecimal> redemptionMap = getRedemptionValue(dam, entry.getKey(), mapping.get(entry.getKey()), getProdMap.get(entry.getKey()));
  		  for(Map<String, Object> mergeMap : list) {
  			  BigDecimal value = redemptionMap.get(mergeMap.get("PRD_ID"));
  			  if(value != null) {
  				  mergeMap.put("SML_SUBS_MINI_AMT_FOR", value);
  			  }
  		  }
  	  }
  	  System.out.println("");
    }
    
    // 取得 MFD / ETF 所有的商品
    private static  Map<String, List<String>> getProdMap(List<Map<String, Object>> list) {
  	  Map<String, List<String>> prdMap = new HashMap<String, List<String>>();
  	  for(Map<String, Object> map : list) {
  		  String pType = ObjectUtils.toString(map.get("PTYPE"));
  		  switch(pType) {
  			  case "MFD": {
  				  setPrdFunc(prdMap, map, pType);
  				  break;
  			  }
  			  case "ETF": {
  				  setPrdFunc(prdMap, map, pType);
  				  break;
  			  }
  		  }
  	  }
  	  return prdMap;
    }
    
    private static void setPrdFunc(Map<String, List<String>> prdMap, Map<String, Object> map, String type) {
  	  List<String> prdList = null;
  	  prdList = prdMap.get(type) == null ? new ArrayList<String>() : prdMap.get(type);
  	  prdList.add(ObjectUtils.toString(map.get("PRD_ID")));
  	  prdMap.put(type, prdList);
    }
    
    // 實際取得資料
    private static Map<String, BigDecimal> getRedemptionValue(DataAccessManager dam, String type, String table, List<String> prdList) throws DAOException, JBranchException {
  	  Map<String, BigDecimal> redemptionValue = new HashMap<String, BigDecimal>();
  	  QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
  	  qc.setQueryString(redemptionValueSQL(type, table));
  	  qc.setObject("prdList", prdList);
  	  List<Map<String, Object>> resultList = dam.exeQuery(qc);
  	  if(CollectionUtils.isNotEmpty(resultList)) {
  		  for(Map<String, Object> map : resultList) {
  			  redemptionValue.put(ObjectUtils.toString(map.get("PRD_ID")), (new GenericMap(map).getBigDecimal("REDEMPTION")));
  		  }
  	  }
  	  return redemptionValue;
    }
    
    // 取得的 SQL
    private final static String redemptionValueSQL(String type, String table) {
  	  StringBuffer sb = new StringBuffer();
  	  sb.append(" with PRD_NAV as(");
  	  sb.append(" select prd_id, prd_nav ");
  	  sb.append(" from TBFPS_PRD_RETURN_D ");
  	  sb.append(" where prd_type = '").append(type).append("' and prd_id in (:prdList)");
  	  sb.append(" and DATA_DATE = (select max(data_date) ");
  	  sb.append(" from TBFPS_PRD_RETURN_D ");
  	  sb.append(" where prd_type = '").append(type).append("' and prd_id in (:prdList)))");
  	  sb.append(" select PRD_NAV.PRD_ID, ceil(");
  	  sb.append("MFD".equals(type) ? "1/power(10,nvl(TBPRD_FUND.FUS07,0))" : "TBPRD_ETF.TXN_UNIT");
  	  sb.append(" * PRD_NAV.prd_nav/100)*100 REDEMPTION from ");
  	  sb.append(table);
  	  sb.append(" inner join PRD_NAV on ").append(table).append(".PRD_ID = PRD_NAV.PRD_ID");
  	  return sb.toString();
    }
}
