package com.systex.jbranch.app.server.fps.fps400;

import com.systex.jbranch.app.server.fps.fps200.FPS200;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * (特定目的)績效追蹤及投資組合追蹤_清單
 *
 * @author Juan
 * @since 18-3-29
 */
@Component("fps400")
@Scope("request")
public class FPS400 extends FubonWmsBizLogic {

  SimpleDateFormat sdf = new SimpleDateFormat("yyMMmmss");

  /**
   * 取得查詢結果_績效追蹤與投組調整Tab
   *
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  private StringBuffer inquire_sql(FPS400InputVO inputVO, QueryConditionIF qc) throws Exception {
    // old code
    StringBuffer sb = new StringBuffer();
    sb.append("WITH BASE AS ( ");
    sb.append("SELECT DE.SPP_ACHIVE_RATE_1, DE.SPP_ACHIVE_RATE_2 ");
    sb.append("FROM TBFPS_OTHER_PARA_HEAD HE ");
    sb.append("INNER JOIN TBFPS_OTHER_PARA DE ON HE.PARAM_NO = DE.PARAM_NO ");
    sb.append("WHERE HE.STATUS ='A' ");
    sb.append("FETCH FIRST 1 ROWS ONLY ");
    sb.append("), ");
    sb.append("TPPS AS ( ");
    sb.append("SELECT PLAN_ID, (POLICY_NO ||'-'|| TRIM(TO_CHAR(POLICY_SEQ  ,'00')) || CASE WHEN ID_DUP <> ' ' THEN '-' || ID_DUP END ) AS CERT_NBR ");
    sb.append("FROM TBFPS_PORTFOLIO_PLAN_SPP WHERE PLAN_ID IN (SELECT PLAN_ID FROM TBFPS_SPP_PRD_RETURN_HEAD WHERE CUST_ID = :custId) AND POLICY_NO IS NOT NULL ");
    sb.append("UNION ");
    sb.append("SELECT PLAN_ID, CERTIFICATE_ID AS CERT_NBR FROM TBFPS_PLANID_MAPPING WHERE PLAN_ID IN (SELECT PLAN_ID FROM TBFPS_SPP_PRD_RETURN_HEAD WHERE CUST_ID = :custId) ");
    sb.append("), ");
    sb.append("VAAD AS ( ");
    sb.append("SELECT TPPS.PLAN_ID AS PLAN_ID, VAAD.INV_AMT_TWD ");
    sb.append("FROM MVFPS_AST_ALLPRD_DETAIL VAAD ");
    sb.append("LEFT JOIN TPPS ON VAAD.CERT_NBR = TPPS.CERT_NBR ");
    sb.append("WHERE CUST_ID = :custId ");
    sb.append("AND AST_TYPE IN ('07', '08', '09', '14') ");
    sb.append("), ");
    sb.append(" LOG AS ( ");
    sb.append("   SELECT COUNT(*) EXEC_CNT,");
    sb.append("   TO_CHAR(MAX(FILE_LOG.CREATETIME), 'YYYY/MM/DD') EXEC_TIME,");
    sb.append("   FILE_LOG.EXEC_TYPE,");
    sb.append("   FILE_LOG.PLAN_TYPE,");
    sb.append("   FILE_LOG.PLAN_ID");
    sb.append("   FROM TBFPS_PORTFOLIO_PLAN_FILE_LOG FILE_LOG");
    sb.append("   WHERE FILE_LOG.PLAN_TYPE = 'SPP'");
    sb.append("   GROUP BY FILE_LOG.PLAN_ID, FILE_LOG.PLAN_TYPE, FILE_LOG.EXEC_TYPE) ");
    sb.append("SELECT ");
    sb.append("(SELECT SPP_ACHIVE_RATE_1 FROM BASE) AS SPP_ACHIVE_RATE_1, ");
    sb.append("(SELECT SPP_ACHIVE_RATE_2 FROM BASE) AS SPP_ACHIVE_RATE_2, ");
    sb.append("RS.TRACE_V_FLAG AS TRACE_V_FLAG, ");
    sb.append("RS.REVIEW_V_FLAG AS REVIEW_V_FLAG, ");
    sb.append("RS.TRACE_P_FLAG AS TRACE_P_FLAG, ");
    sb.append("RS.REVIEW_P_FLAG AS REVIEW_P_FLAG, ");
    sb.append("CASE WHEN RS.HIT_RATE < (SELECT SPP_ACHIVE_RATE_1 FROM BASE) THEN '落後' ");
    sb.append(" WHEN RS.HIT_RATE >= (SELECT SPP_ACHIVE_RATE_2 FROM BASE) THEN '符合進度' ");
    sb.append("ELSE '微幅落後' END AS HIT_RATE_DESC, ");
    sb.append("CASE WHEN RS.HIT_RATE < (SELECT SPP_ACHIVE_RATE_1 FROM BASE) THEN '-1' ");
    sb.append(" WHEN RS.HIT_RATE >= (SELECT SPP_ACHIVE_RATE_2 FROM BASE) THEN '1' ");
    sb.append("ELSE '0' END AS HIT_RATE_FLAG, ");
    sb.append("PS.INV_PLAN_NAME, ");
    sb.append("PS.INV_AMT_TARGET, ");
    sb.append("(SELECT SUM(NVL(INV_AMT_TWD, 0)) FROM VAAD WHERE PLAN_ID = RS.PLAN_ID) AS INV_AMT_CURRENT, ");
    sb.append("RS.MARKET_VALUE, ");
    sb.append("RS.RETURN_RATE, ");
    sb.append("RS.AMT_TARGET, ");
    sb.append("RS.HIT_RATE, ");
    sb.append("RS.PLAN_ID, ");
    sb.append("PS.VOL_RISK_ATTR, ");
    sb.append("PS.SPP_TYPE, ");
    sb.append("RS.CREATETIME, ");
    sb.append("   NVL((");
    sb.append("     SELECT LOG.EXEC_CNT");
    sb.append("     FROM LOG");
    sb.append("     WHERE RS.PLAN_ID = LOG.PLAN_ID");
    sb.append("     AND LOG.EXEC_TYPE = 'E'), 0) IS_EMAIL, ");
    sb.append("   NVL((");
    sb.append("     SELECT LOG.EXEC_CNT");
    sb.append("     FROM LOG");
    sb.append("     WHERE RS.PLAN_ID = LOG.PLAN_ID");
    sb.append("     AND LOG.EXEC_TYPE = 'P'), 0) IS_PRINT ");
    sb.append("from TBFPS_SPP_PRD_RETURN_HEAD RS ");
    sb.append("left join TBFPS_PORTFOLIO_PLAN_SPP_HEAD PS ON RS.PLAN_ID = PS.PLAN_ID ");
    sb.append("where RS.CUST_ID = :custId ");
    qc.setObject("custId", inputVO.getCustID());

    if (StringUtils.isNotBlank(inputVO.getPlanID())) {
      sb.append(" AND RS.PLAN_ID = :planId ");
      qc.setObject("planId", inputVO.getPlanID());
    }

    return sb;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void inquire(Object body, IPrimitiveMap header) throws Exception {
    FPS400InputVO inputVO = (FPS400InputVO) body;
    FPS400OutputVO outputVO = new FPS400OutputVO();
    DataAccessManager dam = this.getDataAccessManager();

    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = inquire_sql(inputVO, qc);
    qc.setQueryString(sb.toString());

    List<Map<String, Object>> resultList = dam.exeQuery(qc);

    if (CollectionUtils.isNotEmpty(resultList)) {
      for (int i = 0; i < resultList.size(); i++) {
        GenericMap resultGMap = new GenericMap(resultList.get(i));
        // 報酬率重算
        resultGMap.put("RETURN_RATE", FPSUtils.getInterestReturnRate(resultGMap.getBigDecimal("MARKET_VALUE"), resultGMap.getBigDecimal("INV_AMT_CURRENT")));

        // 目標年期
        int targetYear = FPSUtils.getInvestmentYear(dam, resultGMap.getNotNullStr("PLAN_ID"));
        outputVO.setTargetYear(targetYear);
        
        // 未來剩餘月份
        int futureMonth = FPSUtils.getRemainingMonth(resultGMap.getDate("CREATETIME"), targetYear);
        
        // 已購入
        BigDecimal purchaseFutureValue = FPSUtils.getPurchaseFutureValue(dam, inputVO.getCustID(), FPSUtils.getCertificateIdList(dam, resultGMap.getNotNullStr("PLAN_ID")), futureMonth);
        
        // 未來投入金額 (未購入 + 已購入)
        // 不含未購入 FPSUtils.getHistory(dam, resultGMap.getNotNullStr("PLAN_ID"), inputVO.getCustID(), "N")
        BigDecimal futureQuota = FPSUtils.getFutureQuota(
        		new ArrayList<Map<String, Object>>(), futureMonth, purchaseFutureValue);

        // 應達目標重算
        resultGMap.put("AMT_TARGET", FPSUtils.getAchievementTarget(resultGMap.getDate("CREATETIME"), resultGMap.getBigDecimal("INV_AMT_TARGET"),
            resultGMap.getBigDecimal("INV_AMT_CURRENT"), futureQuota, targetYear));

        // 達成率重算
        BigDecimal hitRate = FPSUtils.getAchievementRate(resultGMap.getBigDecimal("MARKET_VALUE"), resultGMap.getBigDecimal("AMT_TARGET"));
        resultGMap.put("HIT_RATE", hitRate);
        
        if (hitRate.compareTo(resultGMap.getBigDecimal("SPP_ACHIVE_RATE_2")) != -1) {
        	resultGMap.put("HIT_RATE_FLAG", 1);
        	resultGMap.put("HIT_RATE_DESC", "符合進度");
        } else if (hitRate.compareTo(resultGMap.getBigDecimal("SPP_ACHIVE_RATE_1")) != -1) {
        	resultGMap.put("HIT_RATE_FLAG", 0);
        	resultGMap.put("HIT_RATE_DESC", "微幅落後");
        } else {
        	resultGMap.put("HIT_RATE_FLAG", -1);
        	resultGMap.put("HIT_RATE_DESC", "落後");
        }
      }
    }
    
    if(StringUtils.isNotBlank(inputVO.getPlanID())) {
    	// 成立日期
//        outputVO.setPlanDate(FPSUtils.getCreatetime(dam, inputVO.getPlanID()).toString());
        outputVO.setPlanDate(new SimpleDateFormat("yyyy/MM/dd").format(FPSUtils.getCreatetime(dam, inputVO.getPlanID())));
    }
    
    outputVO.setOutputList(resultList);
    FPS200 fps200 = (FPS200) PlatformContext.getBean("fps200");
    outputVO.setPreBusiDay(fps200.getPrevBussinessDay(dam));

    // get Rate
    // getAchiveRate(outputVO, dam, qc);
    this.sendRtnObject(outputVO);
  }

  public List<Map<String, Object>> api_inquire(FPS400InputVO inputVO) throws Exception {
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    qc.setQueryString(inquire_sql(inputVO, qc).toString());

    List<Map<String, Object>> resultList = dam.exeQuery(qc);
    if (CollectionUtils.isNotEmpty(resultList)) {
      for (int i = 0; i < resultList.size(); i++) {
        GenericMap resultGMap = new GenericMap(resultList.get(i));
        // 報酬率重算
        resultGMap.put("RETURN_RATE", FPSUtils.getInterestReturnRate(resultGMap.getBigDecimal("MARKET_VALUE"), resultGMap.getBigDecimal("INV_AMT_CURRENT")));
        
        // 目標年期
        int targetYear = FPSUtils.getInvestmentYear(dam, resultGMap.getNotNullStr("PLAN_ID"));

        // 未來剩餘月份
        int futureMonth = FPSUtils.getRemainingMonth(resultGMap.getDate("CREATETIME"), targetYear);
        
        // 已購入
        BigDecimal purchaseFutureValue = FPSUtils.getPurchaseFutureValue(dam, inputVO.getCustID(), FPSUtils.getCertificateIdList(dam, resultGMap.getNotNullStr("PLAN_ID")), futureMonth);
        
        // 未來投入金額 (未購入 + 已購入)
        // 不含未購入 FPSUtils.getHistory(dam, resultGMap.getNotNullStr("PLAN_ID"), inputVO.getCustID(), "N")
        BigDecimal futureQuota = FPSUtils.getFutureQuota(
        		new ArrayList<Map<String, Object>>(), futureMonth, purchaseFutureValue);

        // 應達目標重算
        resultGMap.put("AMT_TARGET", FPSUtils.getAchievementTarget(resultGMap.getDate("CREATETIME"), resultGMap.getBigDecimal("INV_AMT_TARGET"),
            resultGMap.getBigDecimal("INV_AMT_CURRENT"), futureQuota, targetYear));

        // 達成率重算
        resultGMap.put("HIT_RATE", FPSUtils.getAchievementRate(resultGMap.getBigDecimal("MARKET_VALUE"), resultGMap.getBigDecimal("AMT_TARGET")));
      }
    }

    return resultList;
  }

  private StringBuffer inquireHis_sql(FPS400InputVO inputVO, QueryConditionIF queryCondition) throws Exception {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT TO_CHAR(A.DATA_DATE, 'YYYY/MM/DD') AS DATA_DATE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, A.INV_CRCY_TYPE AS VALU_CRCY_TYPE, ");
    sb.append("NVL(A.INV_COST_ORGD, 0) AS INV_COST_ORGD, NVL(A.REF_AMT_ORGD, 0) AS REF_AMT_ORGD, NVL(SUM(B.TXN_DIVID_ORGD),0) AS TXN_DIVID_ORGD, ");
    sb.append("(NVL(SUM(B.TXN_DIVID_ORGD),0) + NVL(A.REF_AMT_ORGD, 0)) * 100 / NVL(A.INV_COST_ORGD, 0) - 100 AS RATIO_ORGD, ");
    sb.append("NVL(A.INV_COST_TWD, 0) AS INV_COST_TWD, NVL(A.REF_AMT_TWD, 0) AS REF_AMT_TWD, NVL(SUM(B.TXN_DIVID_TWD),0) AS TXN_DIVID_TWD, ");
    sb.append("(NVL(SUM(B.TXN_DIVID_TWD),0) + NVL(A.REF_AMT_TWD, 0)) * 100 / NVL(A.INV_COST_TWD, 0) - 100 AS RATIO_TWD ");
    sb.append("FROM TBCRM_AST_INV_DTL A ");
    sb.append("LEFT JOIN TBCRM_AST_INV_FUND_DIVID B ON A.CUST_ID = B.CUST_ID AND A.CERT_NBR = B.CERT_NBR AND A.PROD_ID = B.FUND_CODE ");
    sb.append("INNER JOIN TBFPS_PLANID_MAPPING MA ON A.CERT_NBR = MA.CERTIFICATE_ID AND MA.PLAN_ID = :planId ");
    sb.append("WHERE A.TXN_TYPE = '2' ");	// -- 贖回
    sb.append("AND A.CUST_ID = :custId ");
    sb.append("AND A.PROD_TYPE in ('PD01', 'PD02', 'PD03', 'PD04', 'PD06') ");

    if (inputVO.getsDate() != null && inputVO.geteDate() != null) {
      sb.append("AND A.DATA_DATE BETWEEN :sDate AND :eDate ");
      queryCondition.setObject("sDate", inputVO.getsDate());
      queryCondition.setObject("eDate", inputVO.geteDate());
    } else if (inputVO.getsDate() != null) {
      sb.append("AND A.DATA_DATE >= :sDate ");
      queryCondition.setObject("sDate", inputVO.getsDate());
    } else if (inputVO.geteDate() != null) {
      sb.append("AND A.DATA_DATE <= :eDate ");
      queryCondition.setObject("eDate", inputVO.geteDate());
    }
    sb.append("GROUP BY A.DATA_DATE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, A.INV_CRCY_TYPE, A.INV_COST_ORGD, A.REF_AMT_ORGD, A.INV_COST_TWD, A.REF_AMT_TWD ");
    sb.append("ORDER BY DATA_DATE DESC ");
    if (StringUtils.equals("Y", inputVO.getFlagYN())) {
      sb.append("FETCH FIRST 10 ROW ONLY ");
    }
    queryCondition.setObject("planId", inputVO.getPlanID());
    queryCondition.setObject("custId", inputVO.getCustID());

    return sb;
  }

  public void inquireHis(Object body, IPrimitiveMap header) throws Exception {
    FPS400InputVO inputVO = (FPS400InputVO) body;
    FPS400OutputVO outputVO = new FPS400OutputVO();
    DataAccessManager dam = this.getDataAccessManager();

    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = inquireHis_sql(inputVO, queryCondition);
    queryCondition.setQueryString(sb.toString());
    List list = dam.exeQuery(queryCondition);
    outputVO.setOutputList(dam.exeQuery(queryCondition));

    this.sendRtnObject(outputVO);
  }

  public List<Map<String, Object>> api_inquireHis(FPS400InputVO inputVO) throws Exception {
    DataAccessManager dam = this.getDataAccessManager();

    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    queryCondition.setQueryString(inquireHis_sql(inputVO, queryCondition).toString());

    return dam.exeQuery(queryCondition);
  }

  /**
   * 取得特定目的理財目前達成率(%) 取得查詢結果
   *
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  public void getAchiveRate(FPS400OutputVO outputVO, DataAccessManager dam,
      QueryConditionIF qc) throws JBranchException, ParseException {
    dam = this.getDataAccessManager();
    qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    // 查詢
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT SPP_ACHIVE_RATE FROM TBFPS_OTHER_PARA C ");
    sb.append(" JOIN TBFPS_OTHER_PARA_HEAD D ON C.param_no = D.param_no ");
    sb.append(" WHERE D.status = 'A' ");
    sb.append(" AND D.effect_start_date = ( ");
    sb.append(" SELECT max(effect_start_date) ");
    sb.append(" FROM TBFPS_OTHER_PARA_HEAD ");
    sb.append(" WHERE status = 'A' ");
    sb.append(" AND effect_start_date <=SYSDATE) ");

    qc.setQueryString(sb.toString());
    List<Map<String, Object>> rateList = dam.exeQuery(qc);
    outputVO.setAchiveRate(((BigDecimal) rateList.get(0).get("SPP_ACHIVE_RATE"))
        .intValue());
  }

  /**
   * 列印
   *
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  public void print(Object body, IPrimitiveMap header) throws JBranchException,
      ParseException {
    FPS400InputVO inputVO = (FPS400InputVO) body;
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuilder sb = new StringBuilder();

    sb.append(" SELECT A.PLAN_PDF_FILE, A.FILE_NAME");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE A");
    sb.append(" WHERE A.PLAN_ID = :planId");
    sb.append(" AND A.ENCRYPT = 'Y'");
    sb.append(" AND A.PLAN_TYPE = 'SPP'");
    sb.append(" AND A.SEQ_NO = :seq");
    qc.setObject("planId", inputVO.getPlanID());
    qc.setObject("seq", inputVO.getSEQNO());
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> result = dam.exeQuery(qc);

    if (!result.isEmpty()) {
      String filename = (String) result.get(0).get("FILE_NAME");
      byte[] blobarray;
      try {
        blobarray = ObjectUtil.blobToByteArr((Blob) result.get(0).get("PLAN_PDF_FILE"));
      } catch (Exception e) {
        throw new APException("ehl_01_common_035"); // 文件下載錯誤！
      }

      String temp_path = (String) SysInfo
          .getInfoValue(SystemVariableConsts.TEMP_PATH);
      String temp_path_relative = (String) SysInfo
          .getInfoValue(SystemVariableConsts.TEMP_PATH_RELATIVE);

      FileOutputStream download_file = null;
      try {
        download_file = new FileOutputStream(new File(temp_path, filename));
        download_file.write(blobarray);
      } catch (Exception e) {
        throw new APException("ehl_01_common_036"); // 下載檔案失敗
      } finally {
        try {
          download_file.close();
        } catch (Exception e) {
          // IGNORE
        }
      }
      notifyClientToDownloadFile(temp_path_relative + "//" + filename, filename);
      this.sendRtnObject(null);;
    } else {
      throw new APException("ehl_01_common_036"); // 無可下載的檔案
    }
  }

  /**
   * 取得計劃書查詢結果
   *
   * @param body
   * @param header
   * @throws JBranchException
   */
  private StringBuffer inquireProposal_sql(FPS400InputVO inputVO, QueryConditionIF qc) throws Exception {
    StringBuffer sb = new StringBuffer();
    sb.append(" WITH TMP AS (");
    sb.append("   SELECT");
    sb.append("   COUNT (*) EXEC_CNT,");
    sb.append("   TO_CHAR(MAX(FILE_LOG.CREATETIME),'YYYY/MM/DD') EXEC_TIME,");
    sb.append("   FILE_LOG.EXEC_TYPE,");
    sb.append("   FILE_LOG.SEQ_NO,");
    sb.append("   FILE_LOG.PLAN_TYPE,");
    sb.append("   FILE_LOG.PLAN_ID");
    sb.append("   FROM TBFPS_PORTFOLIO_PLAN_FILE_LOG FILE_LOG");
    sb.append("   WHERE (FILE_LOG.PLAN_TYPE = 'SPP' OR FILE_LOG.PLAN_TYPE = 'PMT')");
    sb.append("   GROUP BY FILE_LOG.PLAN_ID, FILE_LOG.PLAN_TYPE, FILE_LOG.SEQ_NO, FILE_LOG.EXEC_TYPE");
    sb.append(" )");
    sb.append(" SELECT");
    sb.append(" PLAN_FILE.PLAN_ID,");
    sb.append(" PLAN_FILE.PLAN_TYPE,");
    sb.append(" PLAN_FILE.SEQ_NO,");
    sb.append(" PLAN_FILE.CREATETIME,");
    sb.append(" HE.CUST_ID,");
    sb.append(" NVL((");
    sb.append("   SELECT TMP.EXEC_CNT FROM TMP");
    sb.append("   WHERE PLAN_FILE.PLAN_ID = TMP.PLAN_ID");
    sb.append("   AND PLAN_FILE.SEQ_NO = TMP.SEQ_NO");
    sb.append("   AND PLAN_FILE.PLAN_TYPE = TMP.PLAN_TYPE");
    sb.append("   AND TMP.EXEC_TYPE = 'E'");
    sb.append(" ),0) IS_EMAIL,");
    sb.append(" NVL((");
    sb.append("   SELECT TMP.EXEC_CNT FROM TMP");
    sb.append("   WHERE PLAN_FILE.PLAN_ID = TMP.PLAN_ID");
    sb.append("   AND PLAN_FILE.SEQ_NO = TMP.SEQ_NO");
    sb.append("   AND PLAN_FILE.PLAN_TYPE = TMP.PLAN_TYPE");
    sb.append("   AND TMP.EXEC_TYPE = 'P'");
    sb.append(" ),0) IS_PRINT");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE PLAN_FILE");
    sb.append(" LEFT JOIN TBFPS_PORTFOLIO_PLAN_SPP_HEAD HE");
    sb.append(" ON HE.PLAN_ID = PLAN_FILE.PLAN_ID");
    sb.append(" WHERE PLAN_FILE.ENCRYPT = 'Y'");
    sb.append(" AND (PLAN_FILE.PLAN_TYPE = 'SPP' OR PLAN_FILE.PLAN_TYPE = 'PMT')");
    sb.append(" AND PLAN_FILE.PLAN_ID = :planId");
    qc.setObject("planId", inputVO.getPlanID());
    sb.append(" ORDER BY PLAN_FILE.CREATETIME DESC");

    return sb;
  }

  public void inquireProposal(Object body, IPrimitiveMap header) throws Exception {
    FPS400InputVO inputVO = (FPS400InputVO) body;
    FPS400OutputVO outputVO = new FPS400OutputVO();
    DataAccessManager dam = this.getDataAccessManager();

    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = inquireProposal_sql(inputVO, qc);

    rtnQuery(dam, qc, sb, outputVO);
  }

  public List<Map<String, Object>> api_inquireProposal(FPS400InputVO inputVO) throws Exception {
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    qc.setQueryString(inquireProposal_sql(inputVO, qc).toString());

    return dam.exeQuery(qc);
  }

  // 執行查詢回傳
  private void rtnQuery(DataAccessManager dam, QueryConditionIF qc, StringBuffer sb, FPS400OutputVO outputVO) throws DAOException, JBranchException {
    qc.setQueryString(sb.toString());
    outputVO.setOutputList(dam.exeQuery(qc));// 回傳資料
    this.sendRtnObject(outputVO);
  }

  /**
   * 預覽圖
   *
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  public void preview(Object body, IPrimitiveMap header)
      throws JBranchException, ParseException {
    FPS400InputVO inputVO = (FPS400InputVO) body;
    FPS400OutputVO outputVO = new FPS400OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuilder sb = new StringBuilder();

    sb.append(" SELECT PLAN_PDF_FILE, FILE_NAME");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE");
    sb.append(" WHERE PLAN_ID = :planId");
    sb.append(" AND ENCRYPT = 'N'");
    sb.append(" AND PLAN_TYPE = 'SPP'");
    sb.append(" AND SEQ_NO = :seqNo");
    qc.setObject("planId", inputVO.getPlanID());
    qc.setObject("seqNo", inputVO.getSEQNO());
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> result = dam.exeQuery(qc);

    if (!result.isEmpty()) {
      String filename = (String) result.get(0).get("FILE_NAME");
      byte[] blobarray;
      try {
        blobarray = ObjectUtil.blobToByteArr((Blob) result.get(0).get("PLAN_PDF_FILE"));
        String download_file = Base64Utils.encodeToString(blobarray);
        outputVO.setBase64(download_file);
        this.sendRtnObject(outputVO);
      } catch (Exception e) {
        throw new APException("ehl_01_common_035"); // 文件下載錯誤！
      }
    } else {
      throw new APException("ehl_01_common_036"); // 無可下載的檔案
    }
  }

  /**
   * 轉寄
   *
   * @param body
   * @param header
   * @throws Exception
   */
  public void sendMail(Object body, IPrimitiveMap header) throws Exception {
    FPS400InputVO inputVO = (FPS400InputVO) body;
    FPS400OutputVO outputVO = new FPS400OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT FILE_NAME, PLAN_PDF_FILE");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE");
    sb.append(" WHERE PLAN_ID = :planId");
    sb.append(" AND PLAN_TYPE = 'SPP'");
    sb.append(" AND ENCRYPT = 'Y'");
    sb.append(" AND SEQ_NO = :seqNo");
    qc.setObject("planId", inputVO.getPlanID());
    qc.setObject("seqNo", inputVO.getSEQNO());
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> result = dam.exeQuery(qc);
    
    // 績效追蹤的
    boolean isSuccess = FPSUtils.sendMain(dam, result, getCustEmail(inputVO.getCustID()), FPSUtils.getMailContent(2));
    if(isSuccess) {
    	// TODO 待俊諺確認 seq string or bigdecimal
//    	FPSUtils.execLog(dam, inputVO.getPlanID(), inputVO.getSEQNO(), "E", "PMT");
    } else {
    	throw new APException("E-Mail 發送失敗!");
    }
  }

  // 取得該客戶的Email
  private String getCustEmail(String custId) throws DAOException,
      JBranchException {
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam
        .getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT EMAIL ");
    sb.append(" FROM TBCRM_CUST_CONTACT ");
    sb.append(" WHERE CUST_ID = :custId ");
    qc.setQueryString(sb.toString());
    qc.setObject("custId", custId);
    List<Map<String, Object>> result = dam.exeQuery(qc);
    return (String) result.get(0).get("EMAIL");
  }

  // 信箱Email格式檢查
  private boolean isEmail(String email) {
    Pattern emailPattern = Pattern
        .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    Matcher matcher = emailPattern.matcher(email);
    if (matcher.find()) {
      return true;
    }
    return false;
  }

  private StringBuffer setFlag_sql(FPS400InputVO inputVO, QueryConditionIF querycondition) throws Exception {
    // old code
    StringBuffer sb = new StringBuffer();
    sb.append("UPDATE TBFPS_SPP_PRD_RETURN_HEAD SET ");
    sb.append(StringUtils.equals("tp", inputVO.getFlagType()) ? "TRACE_P_FLAG " : "TRACE_V_FLAG ");
    if (StringUtils.equals("review", inputVO.getAction())) {
      if (StringUtils.equals("A", inputVO.getFlagYN())) {
        sb.append("= 'N', ");
        sb.append(StringUtils.equals("tp", inputVO.getFlagType()) ? "REVIEW_P_FLAG " : "REVIEW_V_FLAG ");
        sb.append("= 'A' ");
      } else {
        sb.append("= 'Y', ");
        sb.append(StringUtils.equals("tp", inputVO.getFlagType()) ? "REVIEW_P_FLAG " : "REVIEW_V_FLAG ");
        sb.append("= null ");
      }
    } else {
      if (StringUtils.equals("Y", inputVO.getFlagYN())) {
        sb.append("= 'Y', ");
        sb.append(StringUtils.equals("tp", inputVO.getFlagType()) ? "REVIEW_P_FLAG " : "REVIEW_V_FLAG ");
        sb.append("= null ");
      } else {
        sb.append("= 'N', ");
        sb.append(StringUtils.equals("tp", inputVO.getFlagType()) ? "REVIEW_P_FLAG " : "REVIEW_V_FLAG ");
        sb.append("= 'P' ");
      }
    }
    sb.append("WHERE PLAN_ID = :planId ");
    querycondition.setObject("planId", inputVO.getPlanID());

    return sb;
  }

  public void setFlag(Object body, IPrimitiveMap header) throws Exception {
    FPS400InputVO inputVO = (FPS400InputVO) body;
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF querycondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = setFlag_sql(inputVO, querycondition);
    querycondition.setQueryString(sb.toString());
    dam.exeUpdate(querycondition);

    this.sendRtnObject(null);
  }

  public void api_setFlag(FPS400InputVO inputVO) throws Exception {
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF querycondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    querycondition.setQueryString(setFlag_sql(inputVO, querycondition).toString());
    dam.exeUpdate(querycondition);
  }

}
