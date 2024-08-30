package com.systex.jbranch.app.server.fps.fps340;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_SPP_HEADVO;
import com.systex.jbranch.app.server.fps.fps300.FPS300;
import com.systex.jbranch.app.server.fps.fpsjlb.FPSJLB;
import com.systex.jbranch.app.server.fps.fpsjlb.dao.FpsjlbDao;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtilsPdfInputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("fps340")
@Scope("request")
public class FPS340 extends FPS300 {
  Logger logger = LoggerFactory.getLogger(this.getClass());
  String PLANSTATUS = "4";

  @Autowired
  @Qualifier("fpsjlb")
  private FPSJLB fpsjlb;
  @Autowired
  @Qualifier("fpsjlbDao")
  private FpsjlbDao fpsDao;
  @Autowired
  @Qualifier("fpsutils")
  private FPSUtils fpsUtils;

  public void inquire(Object body, IPrimitiveMap<?> header) throws Exception {
    FPS340InputVO inputVO = (FPS340InputVO) body;
    FPS340OutputVO outputVO = new FPS340OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    String custID = inputVO.getCustId();
    String planID = inputVO.getPlanId();
    String riskType = inputVO.getRiskType();
    // String riskType = inputVO.getRiskType();
    boolean commRsYn = inputVO.getCommRsYn();
    boolean hasInvest = this.hasInvest(dam, planID);
    // String fpsType = getFpsType(commRsYn, this.hasInvest(dam, planID));//先註解，待投保規則確定
    String fpsType = getFpsType(commRsYn, hasInvest);

    // email
    // outputVO.seteMail(this.getEmail(custID));
    // 前言
    outputVO.setBriefList(this.getBriefList(dam, fpsType));
    // 使用指南
    outputVO.setManualList(this.getManualList(dam, fpsType));
    // 商品的警語
    outputVO.setWarningList(this.getNoticeList(dam, planID));
    outputVO.setRptPicture(getRptPicture(dam));
    // 基金績效
    outputVO.setMFDPerformanceList(getPerformance(dam, planID));
    // 規劃日期
    outputVO.setPlanDate(FPSUtils.getLastUpdate(dam, planID, "SPP").toString());
    //
    outputVO.setAchivedParamList(this.getAchivedParamList(dam, inputVO.getPlanId(), inputVO.getCustId()));

    // 僅rebalance,有推介/績效評估報告有
    if (hasInvest && commRsYn)
      outputVO.setRedemptionList(this.getRedemptionList(dam, planID, custID));

    // set init key map for checkValidYear
    Map<String, String> keyMap = new HashMap<String, String>();
    keyMap.put("prodType", "PTYPE");
    keyMap.put("prodID", "PRD_ID");
    keyMap.put("targets", "TARGETS");

    List<Map<String, Object>> formatedStockLs = formatWeightMap(inputVO.getPrdList());

    // 1年
    if (FPSUtils.checkValidYear(dam, inputVO.getPrdList(), 1, keyMap).length == 0) {
      // 歷史年度平均報酬率
      BigDecimal yRate = FPSUtils.getYRate(dam, formatedStockLs, 120, 12);
      outputVO.setHistoryYRate(yRate == null ? null : yRate.doubleValue());
      // 波動率
      BigDecimal volatility = FPSUtils.getStandardDeviation(dam, formatedStockLs, 36, 12, false);
      outputVO.setVolatility(volatility == null ? null : volatility.doubleValue());
      // 有滿一年波動率
      BigDecimal FYvolatility = FPSUtils.getStandardDeviation(dam, formatedStockLs, 36, 12, true);
      outputVO.setFullYearVolatility(FYvolatility == null ? null : FYvolatility.doubleValue());
      // 歷史績效
      outputVO.setYearRateList(FPSUtils.historyPerformance(dam, planID));
    } else {
      outputVO.setHistoryYRate(null);
      outputVO.setVolatility(null);
      outputVO.setYearRateList(null);
    }
    // 波動度 DB
    outputVO.setRecoVolaility(this.getVolatility(dam, riskType));
    outputVO.setMFDPerformanceList(getPerformance(dam, planID));
    outputVO.setPreBusiDay(getPrevBussinessDay(dam));
    this.sendRtnObject(outputVO);
  }

  /**
   * 依首作/非首作、有推介/無推介判斷規劃書類型
   * 
   * @param commRsYn
   *          推介
   * @param hasInvest
   *          首作
   * @return
   */
  private String getFpsType(boolean commRsYn, boolean hasInvest) {
    StringBuilder result = new StringBuilder();
    result.append("S");
    String commRsString = String.valueOf(commRsYn);
    switch (commRsString) {
    case "true":// 有推介
      if (hasInvest)// 非首作
        result.append("4");
      else
        // 首作
        result.append("2");
      break;
    case "false":// 無推介
      if (hasInvest)// 非首作
        result.append("3");
      else
        // 首作
        result.append("1");
      break;
    }

    return result.toString();
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getBriefList(DataAccessManager dam, String fpsType) throws DAOException, JBranchException {
    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuilder sb = new StringBuilder();
    sb.append(" SELECT MANUAL.*");
    sb.append(" FROM TBFPS_RPT_PARA_HEAD HE");
    sb.append(" LEFT JOIN TBFPS_OTHER_PARA_MANUAL MANUAL ON MANUAL.PARAM_NO = HE.PARAM_NO");
    sb.append(" WHERE HE.STATUS = 'A' AND MANUAL.DESC_TYPE = 'F'");
    sb.append(" AND FPS_TYPE = :fpsType");  // 依不同規劃書
    queryCondition.setQueryString(sb.toString());
    queryCondition.setObject("fpsType", fpsType);
    return dam.exeQuery(queryCondition);
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getManualList(DataAccessManager dam, String fpsType) throws DAOException, JBranchException {
    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuilder sb = new StringBuilder();
    sb.append(" SELECT MANUAL.*");
    sb.append(" FROM TBFPS_RPT_PARA_HEAD HE");
    sb.append(" LEFT JOIN TBFPS_OTHER_PARA_MANUAL MANUAL ON MANUAL.PARAM_NO = HE.PARAM_NO");
    sb.append(" WHERE HE.STATUS = 'A' AND MANUAL.DESC_TYPE = 'M'");
    sb.append(" AND FPS_TYPE = :fpsType"); // -- 依不同規劃書
    sb.append(" ORDER BY MANUAL.RANK");
    queryCondition.setQueryString(sb.toString());
    queryCondition.setObject("fpsType", fpsType);
    return dam.exeQuery(queryCondition);
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getWarningList(DataAccessManager dam) throws DAOException, JBranchException {
    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuilder sb = new StringBuilder();
    sb.append(" SELECT CASE WHEN WARN.PRD_TYPE='ETF' THEN 'ETF'");
    sb.append("		WHEN WARN.PRD_TYPE='FUND' THEN '基金'");
    sb.append("		WHEN WARN.PRD_TYPE='INS' THEN '保險'");
    sb.append("   ELSE '固定收益商品' END AS PRD_TYPE, WARNING");
    sb.append(" FROM TBFPS_RPT_PARA_HEAD HE");
    sb.append(" LEFT JOIN TBFPS_OTHER_PARA_WARNING WARN ON WARN.PARAM_NO = HE.PARAM_NO");
    sb.append(" WHERE HE.STATUS = 'A'");
    sb.append(" ORDER BY WARN.PRD_TYPE, WARN.RANK");
    queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    queryCondition.setQueryString(sb.toString());
    return dam.exeQuery(queryCondition);
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getRptPicture(DataAccessManager dam) throws DAOException, JBranchException, SQLException {
    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuilder sb = new StringBuilder();
    sb.append(" SELECT HE.*");
    sb.append(" FROM TBFPS_RPT_PARA_HEAD HE");
    sb.append(" WHERE HE.STATUS = 'A'");

    queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    queryCondition.setQueryString(sb.toString());
    List<Map<String, Object>> list = dam.exeQuery(queryCondition);
    for (Map<String, Object> map : list) {
      if (map.get("RPT_PIC") != null) {
        Blob blob = (Blob) map.get("RPT_PIC");
        int blobLength = (int) blob.length();
        byte[] blobAsBytes = blob.getBytes(1, blobLength);
        map.put("RPT_PIC", blobAsBytes);
        blob.free();
      }
    }
    return list;
  }

  // 波動度
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> calVolatility(DataAccessManager dam, String planID)
      throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT SPP.PRD_ID, SUM((SPP.PORTFOLIO_RATIO/100))WEIGHT,");
    sb.append(" (SELECT DISTINCT PRD_TYPE FROM TBFPS_PRD_RETURN_M WHERE PRD_TYPE = 'MFD') PRD_TYPE");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_SPP SPP");
    sb.append(" WHERE  PLAN_ID = :planID");
    sb.append(" AND PTYPE = 'MFD'");
    sb.append(" GROUP BY PRD_ID");

    qc.setObject("planID", planID);
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> list = dam.exeQuery(qc);

    for (Map<String, Object> cell : list) {
      cell.put("WEIGHT", Double.parseDouble(cell.get("WEIGHT").toString()));
    }
    return list;
  }

  private String getPlanDate(DataAccessManager dam, String planId) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT to_char(CREATETIME,'yyyy/MM/dd') as CREATETIME ");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD INV");
    sb.append(" WHERE  PLAN_ID = :planID");
    qc.setObject("planID", planId);
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> list = dam.exeQuery(qc);
    return (String) list.get(0).get("CREATETIME");
  }

  // 波動率
  private String getVolatility(DataAccessManager dam, String custRisk) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT DE.VOLATILITY ");
    sb.append(" FROM TBFPS_CUSTRISK_VOLATILITY_HEAD HE ");
    sb.append(" INNER JOIN TBFPS_CUSTRISK_VOLATILITY DE on HE.PARAM_NO = DE.PARAM_NO ");
    sb.append(" where HE.STATUS = 'A' ");
    sb.append(" and he.effect_start_date <= sysdate ");
    sb.append(" and DE.CUST_RISK_ATR = :custRisk ");
    if (StringUtils.isNotBlank(custRisk)) {
    	custRisk = custRisk.substring(0, 2);
    }
    qc.setObject("custRisk", custRisk);
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> list = dam.exeQuery(qc);
    return list.get(0).get("VOLATILITY") != null ? list.get(0).get("VOLATILITY").toString() : "";
  }

  // 歷史績效表現
  private List<Map<String, Object>> historyPerformance(DataAccessManager dam, String planID)
      throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT YEAR,ROUND(SUM(YADDP)/");
    sb.append(" (SELECT COUNT(SPP.PRD_ID)");
    sb.append("   FROM TBFPS_PORTFOLIO_PLAN_SPP SPP");
    sb.append("   WHERE PLAN_ID = :planID),4) RETURN_ANN_M");
    sb.append(" FROM(");
    sb.append("   SELECT SUBSTR(YEAR,1,4)YEAR , PRD_ID,SUM(PERCENT)YADDP");
    sb.append("   FROM(");
    sb.append("     SELECT M.DATA_YEARMONTH YEAR,M.PRD_ID,(M.RETURN_1Y*S.PORTFOLIO_RATIO/100) PERCENT");
    sb.append("     FROM TBFPS_PRD_RETURN_M M");
    sb.append("     LEFT JOIN (SELECT PRD_ID, PORTFOLIO_RATIO");
    sb.append("       FROM TBFPS_PORTFOLIO_PLAN_SPP");
    sb.append("       WHERE PLAN_ID = :planID) S ON M.PRD_ID = S.PRD_ID");
    sb.append("     WHERE M.PRD_ID IN(");
    sb.append("       SELECT SPP.PRD_ID");
    sb.append("       FROM TBFPS_PORTFOLIO_PLAN_SPP SPP");
    sb.append("       WHERE PLAN_ID = :planID)");
    sb.append("     AND M.DATA_YEARMONTH");
    sb.append("     BETWEEN TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYY')||'-01','YYYY-MM'),-120),'YYYYMM')");
    sb.append("       AND TO_CHAR(SYSDATE,'YYYYMM')");
    sb.append("     GROUP BY M.PRD_ID,M.DATA_YEARMONTH,(M.RETURN_1Y*S.PORTFOLIO_RATIO/100)");
    sb.append("   )");
    sb.append("   GROUP BY PRD_ID,SUBSTR(YEAR,1,4)");
    sb.append("   )");
    sb.append(" GROUP BY YEAR");
    sb.append(" HAVING COUNT(*) = (SELECT COUNT(DISTINCT SPP.PRD_ID)");
    sb.append("   FROM TBFPS_PORTFOLIO_PLAN_SPP SPP");
    sb.append("   WHERE PLAN_ID = :planID)");
    sb.append(" ORDER BY YEAR");

    qc.setObject("planID", planID);
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  // 歷史績效
  private List<Map<String, Object>> getPerformance(DataAccessManager dam, String planID)
      throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT distinct RM.PRD_ID, '基金' PRD_TYPE, FU.FUND_CNAME AS PRD_CNAME, FU.CURRENCY_STD_ID AS CURRENCY_TYPE, RM.RETURN_3M, RM.RETURN_6M, ");
    sb.append(" RM.RETURN_1Y, RM.RETURN_2Y, RM.RETURN_3Y, RM.RETURN_LTM, RM.VOLATILITY, PARAM.PARAM_NAME FREQUENCY, decode(RD.PRD_NAV, null, '--', RD.PRD_NAV) PRD_NAV ");
    sb.append(" FROM TBFPS_PRD_RETURN_M RM ");
    sb.append(" LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = RM.PRD_ID ");
    sb.append(" INNER JOIN TBFPS_PORTFOLIO_PLAN_SPP PLAN_INV on PLAN_INV.PRD_ID = RM.PRD_ID and PLAN_ID = :planID AND PLAN_INV.PTYPE='MFD' ");
    sb.append(" LEFT JOIN TBSYSPARAMETER PARAM ON PARAM.PARAM_TYPE = 'FPS.DIVIDEND_FREQUENCY' and FU.DIVIDEND_FREQUENCY = PARAM.PARAM_CODE ");
    sb.append(" LEFT JOIN TBFPS_PRD_RETURN_D RD ON RD.DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBFPS_PRD_RETURN_D WHERE PRD_TYPE = 'MFD' and PRD_ID = PLAN_INV.PRD_ID) and RD.PRD_ID = RM.PRD_ID and RD.PRD_TYPE = 'MFD' "); 
    sb.append(" WHERE RM.DATA_YEARMONTH = (SELECT MAX(DATA_YEARMONTH) FROM TBFPS_PRD_RETURN_M WHERE PRD_TYPE = 'MFD' and PRD_ID = PLAN_INV.PRD_ID) ");
    sb.append(" AND RM.PRD_TYPE = 'MFD' ");
    
    qc.setObject("planID", planID);
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  /**
   * 參數
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws IOException
   */
  public void getParameter(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
    DataAccessManager dam = this.getDataAccessManager();
    FPS340OutputVO outputVO = new FPS340OutputVO();
    FPS340InputVO inputVO = (FPS340InputVO) body;
    outputVO.setOutputList(getNoticeList(dam, inputVO.getPlanId()));
    this.sendRtnObject(outputVO);
  }

  public List<Map<String, Object>> getNoticeList(DataAccessManager dam, String planId) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT WARN.PRD_TYPE,WARN.RANK,WARN.WARNING	");
    sb.append(" FROM TBFPS_RPT_PARA_HEAD HE	");
    sb.append(" LEFT JOIN TBFPS_OTHER_PARA_WARNING WARN ON WARN.PARAM_NO = HE.PARAM_NO	");
    sb.append(" LEFT JOIN TBFPS_PORTFOLIO_PLAN_SPP INV ON	");
    sb.append(" (CASE WHEN INV.PTYPE = 'MFD' THEN 'FUND'	");
    sb.append(" WHEN INV.PTYPE='BND' THEN 'BOND' ELSE INV.PTYPE END) = WARN.PRD_TYPE	");
    sb.append(" WHERE HE.STATUS = 'A' AND INV.PLAN_ID = :planId	");
    sb.append(" GROUP BY WARN.PRD_TYPE, WARN.RANK, WARN.WARNING	");
    sb.append(" ORDER BY WARN.PRD_TYPE, WARN.RANK");
    qc.setObject("planId", planId);
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  /**
   * 檢核
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws IOException
   */
  public void checker(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
    DataAccessManager dam = this.getDataAccessManager();
    FPS340InputVO inputVO = (FPS340InputVO) body;

    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    queryCondition.setObject("planId", inputVO.getPlanId());
    queryCondition.setQueryString("SELECT PLAN_STATUS FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD WHERE PLAN_ID = :planId ");
    List<Map<String, String>> list = dam.exeQuery(queryCondition);
    this.sendRtnObject(list.get(0).get("PLAN_STATUS"));
  }

  /**
   * 取得查詢結果_績效追蹤與投組調整Tab
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  @SuppressWarnings({ "unchecked" })
  public List<Map<String, Object>> getAchivedParamList(DataAccessManager dam, String planId, String custId) throws JBranchException, ParseException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    StringBuffer sb = new StringBuffer();
    sb.append("WITH BASE AS ( ");
    sb.append("SELECT DE.SPP_ACHIVE_RATE_1, DE.SPP_ACHIVE_RATE_2 ");
    sb.append("FROM TBFPS_OTHER_PARA_HEAD HE ");
    sb.append("INNER JOIN TBFPS_OTHER_PARA DE ON HE.PARAM_NO = DE.PARAM_NO ");
    sb.append("WHERE HE.STATUS ='A' ");
    sb.append("FETCH FIRST 1 ROWS ONLY ");
    sb.append("), ");
    sb.append("TPPS AS (");
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
    sb.append(") ");
    sb.append("SELECT ");
    sb.append("(SELECT SPP_ACHIVE_RATE_1 FROM BASE) AS SPP_ACHIVE_RATE_1, ");
    sb.append("(SELECT SPP_ACHIVE_RATE_2 FROM BASE) AS SPP_ACHIVE_RATE_2, ");
    sb.append("RS.TRACE_V_FLAG AS TRACE_V_FLAG,");
    sb.append("RS.REVIEW_V_FLAG AS REVIEW_V_FLAG, ");
    sb.append("RS.TRACE_P_FLAG AS TRACE_P_FLAG,");
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
    sb.append("PS.RISK_ATTR, ");
    sb.append("PS.SPP_TYPE, ");
    sb.append("RS.CREATETIME ");
    sb.append("from TBFPS_SPP_PRD_RETURN_HEAD RS ");
    sb.append("left join TBFPS_PORTFOLIO_PLAN_SPP_HEAD PS ON RS.PLAN_ID = PS.PLAN_ID ");
    sb.append("where RS.CUST_ID = :custId ");
    qc.setObject("custId", custId);

    if (StringUtils.isNotBlank(planId)) {
      sb.append(" AND RS.PLAN_ID = :planId ");
      qc.setObject("planId", planId);
    }
    qc.setQueryString(sb.toString());

    List<Map<String, Object>> resultList = dam.exeQuery(qc);

    if (CollectionUtils.isNotEmpty(resultList)) {
      for (int i = 0; i < resultList.size(); i++) {
        GenericMap resultGMap = new GenericMap(resultList.get(i));
        // 報酬率重算
        resultGMap.put("RETURN_RATE",
            FPSUtils.getInterestReturnRate(resultGMap.getBigDecimal("MARKET_VALUE"), resultGMap.getBigDecimal("INV_AMT_CURRENT")));

        // 目標年期
        int targetYear = FPSUtils.getInvestmentYear(dam, resultGMap.getNotNullStr("PLAN_ID"));

        // 未來剩餘月份
        int futureMonth = FPSUtils.getRemainingMonth(resultGMap.getDate("CREATETIME"), targetYear);
        
        // 已購入=定期定額到目標日期所投入的所有金額
        //		FPSUtils.getCertificateIdList: 已購入定期定額的憑證編號        
        BigDecimal purchaseFutureValue = FPSUtils.getPurchaseFutureValue(dam, custId, FPSUtils.getCertificateIdList(dam, planId), futureMonth);
        
        // 未來投入金額 (未購入 + 已購入)
        //		FPSUtils.getHistory: 尚未下單資料
        //BigDecimal futureQuota = FPSUtils.getFutureQuota(FPSUtils.getHistory(dam, planId, custId, "N"),futureMonth, purchaseFutureValue);
        //#6308 應達目標不計算未下單金額
        BigDecimal futureQuota = FPSUtils.getFutureQuota(new ArrayList<Map<String, Object>>(),futureMonth, purchaseFutureValue);

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
    return resultList;
  }

  @SuppressWarnings("unchecked")
  private String checkPrint(DataAccessManager dam, String planID) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT COUNT(*) CHECKER");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE");
    sb.append(" WHERE PLAN_ID = :planID");

    qc.setObject("planID", planID);
    qc.setQueryString(sb.toString());
    return ((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("CHECKER").toString();
  }

  public void updateHead(Object body, IPrimitiveMap header) throws JBranchException {
    FPS340InputVO inputVO = (FPS340InputVO) body;
    FPS340OutputVO outputVO = new FPS340OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    TBFPS_PORTFOLIO_PLAN_SPP_HEADVO vo = (TBFPS_PORTFOLIO_PLAN_SPP_HEADVO) dam
        .findByPKey(TBFPS_PORTFOLIO_PLAN_SPP_HEADVO.TABLE_UID, inputVO.getPlanId());
    if (vo != null) {
      vo.setPLAN_STATUS(inputVO.getPlanStatus());// 規劃狀態
      vo.setSTEP_STAUS("3");// 步驟
      dam.update(vo);
    } else {
      throw new APException("ehl_01_common_009"); // 查無資料
    }
    this.sendRtnObject(outputVO);
  }

  /**
   * generatePdf
   * 
   * @throws Exception
   */
  public void generatePdf(Object body, IPrimitiveMap<?> header) throws Exception {
    DataAccessManager dam = this.getDataAccessManager();
    FPSUtilsPdfInputVO inputVO = (FPSUtilsPdfInputVO) body;

    BigDecimal printSEQ = inputVO.getPrintSEQ();
    String action = inputVO.getAction();
    String planID = inputVO.getPlanID();
    String custID = inputVO.getCustID();
    String fileName = inputVO.getFileName();
    String aoCode = inputVO.getAoCode();
    String tempFileName = inputVO.getTempFileName();
    String isFps410 = inputVO.getIsFps410();
    String sppType = ("Y").equals(isFps410) ? "PMT" : "SPP";

    if (printSEQ.compareTo(BigDecimal.ZERO) == -1) {
      printSEQ = FPSUtils.newPDFFile(dam, custID, aoCode, fileName, tempFileName, planID, sppType, action);
    }

    if (("resend").equals(action)) {
      sendMail(dam, FPSUtils.getPDFFile(dam, planID, printSEQ, sppType), planID, custID, printSEQ, sppType);
    } else if (("download").equals(action)) {
      downloadFile(dam, FPSUtils.getPDFFile(dam, planID, printSEQ, sppType), planID, printSEQ, sppType);
    }

    this.sendRtnObject(printSEQ);
  }

  // do file
  public void downloadFile(DataAccessManager dam, List<Map<String, Object>> result, String planID, BigDecimal seq, String sppType)
      throws JBranchException {
    if (!result.isEmpty()) {
      String filename = (String) result.get(0).get("FILE_NAME");
      byte[] blobarray;
      try {
        blobarray = ObjectUtil.blobToByteArr((Blob) result.get(0).get("PLAN_PDF_FILE"));
      } catch (Exception e) {
        e.printStackTrace();
        throw new APException("文件下載錯誤！"); // 文件下載錯誤！
      }
      String temp_path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
      String temp_path_relative = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH_RELATIVE);
      FileOutputStream download_file = null;
      try {
        download_file = new FileOutputStream(new File(temp_path, filename));
        download_file.write(blobarray);
        FPSUtils.execLog(dam, planID, seq, "P", sppType);
      } catch (Exception e) {
        e.printStackTrace();
        throw new APException("文件下載錯誤！"); // 下載檔案失敗
      } finally {
        try {
          download_file.close();
        } catch (Exception e) {
          e.printStackTrace();
          // IGNORE
        }
      }
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      Date now = new Date();
      notifyClientToDownloadFile(temp_path_relative + "//" + filename, sdf.format(now) + (StringUtils.equals("PMT", sppType) ? "績效追蹤報告書" : "目標理財規劃書"));
    } else {
      throw new APException("無可下載的檔案!"); // 無可下載的檔案
    }
  }

  // 轉寄
  public void sendMail(DataAccessManager dam, List<Map<String, Object>> result, String planID, String custID, BigDecimal seq, String sppType)
      throws Exception {
	
	  // 特定 or 績效
	  boolean isSuccess = FPSUtils.sendMain(dam, result, FPSUtils.getCustEmail(dam, custID), FPSUtils.getMailContent(StringUtils.equals("PMT", sppType) ? 2 : 1));
	  if(isSuccess) {
		  FPSUtils.execLog(dam, planID, seq, "E", sppType);
	  } else {
		  throw new APException("E-Mail 發送失敗!");
	  }
  }

  //
  // // update head
  // protected void update(Object body, DataAccessManager dam, String planID)
  // throws DAOException, JBranchException {
  // FPS340InputVO inputVO = (FPS340InputVO) body;
  // TBFPS_PORTFOLIO_PLAN_INV_HEADVO vo = (TBFPS_PORTFOLIO_PLAN_INV_HEADVO) dam
  // .findByPKey(TBFPS_PORTFOLIO_PLAN_INV_HEADVO.TABLE_UID, planID);
  // if (vo != null) {
  // dam.update(putPlanInvHeadVO(vo, inputVO));
  // } else {
  // throw new APException("ehl_01_common_009"); // 查無資料
  // }
  // }
  //
  // private TBFPS_PORTFOLIO_PLAN_INV_HEADVO
  // putPlanInvHeadVO(TBFPS_PORTFOLIO_PLAN_INV_HEADVO vo, FPS340InputVO inputVO)
  // {
  // vo.setPLAN_STATUS(inputVO.getPlanStatus());
  // return vo;
  // }

  /**
   * Pdf file encryption with password. Byte in byte out
   * 
   * @param ByteArrayInputStream
   *          input
   * @return ByteArrayOutputStream out
   */
  public ByteArrayOutputStream setEncryption(FPS340InputVO inputVO) {
    String password = inputVO.getCustId();
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PdfReader reader = new PdfReader(inputVO.getPdfBlob());
      // PdfStamper stamper = new PdfStamper(reader, new
      // FileOutputStream("C:/Users/1700433.SYSTEX/Desktop/test.pdf"));
      PdfStamper stamper = new PdfStamper(reader, out);
      stamper.setEncryption(password.getBytes(), "World".getBytes(),
          PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
      stamper.close();
      reader.close();
      return out;
    } catch (Exception e) {

      e.printStackTrace();
    }
    return null;
  }

  // 有無投保
  @SuppressWarnings("unchecked")
  public boolean hasInvest(DataAccessManager dam, String planID) throws NumberFormatException, DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append("  SELECT count(*) AS GET_COUNT FROM TBFPS_SPP_PRD_RETURN A		");
    sb.append("      LEFT JOIN TBFPS_PORTFOLIO_PLAN_SPP_HEAD B ON A.PLAN_ID = B.PLAN_ID	");
    sb.append(" WHERE A.PLAN_ID = :planID AND B.VALID_FLAG = 'Y'	");

    qc.setObject("planID", planID);
    qc.setQueryString(sb.toString());
    List<Map<String, BigDecimal>> list = dam.exeQuery(qc);
    if (list.get(0).get("GET_COUNT").compareTo(BigDecimal.ZERO) > 0)
      return true;
    else
      return false;
  }

  // 計算新商品權重
  public List<Map<String, Object>> formatWeightMap(List<Map<String, Object>> ls) {
    List<Map<String, Object>> wmLs = new ArrayList<Map<String, Object>>();

    for (Map<String, Object> item : ls) {
      Map<String, Object> wm = new HashMap<String, Object>();
      wm.put("PRD_ID", item.get("PRD_ID"));
      wm.put("PRD_TYPE", item.get("PTYPE"));
      wm.put("TARGETS", item.get("TARGETS"));
      wm.put("WEIGHT", new BigDecimal(ObjectUtils.toString(item.get("INV_PERCENT"))).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
      wmLs.add(wm);
    }
    return wmLs;
  }

  public List<Map<String, Object>> getRedemptionList(DataAccessManager dam, String planId, String custId) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT C.DATA_DATE , C.PROD_NAME , C.INV_CRCY_TYPE ,	");
    sb.append("                 C.INV_COST_ORGD , C.REF_AMT_ORGD , SUM(D.TXN_DIVID_ORGD) TXN_DIVID_ORGD	");
    sb.append("    FROM TBFPS_SPP_PRD_RETURN A	");
    sb.append("     LEFT JOIN TBFPS_SPP_PRD_RETURN_HEAD B		");
    sb.append("        ON A.PLAN_ID = B.PLAN_ID	");
    sb.append("   LEFT JOIN TBCRM_AST_INV_DTL C	");
    sb.append("       ON A.PTYPE = 'MFD' AND C.PROD_TYPE IN ('PD01','PD02','PD03','PD04','PD06')	");
    sb.append("    AND A.PRD_ID = C.PROD_ID		");
    sb.append("    AND B.CUST_ID = C.CUST_ID AND C.TXN_TYPE='2'		");
    sb.append("    LEFT JOIN TBCRM_AST_INV_FUND_DIVID D ON C.CUST_ID = D.CUST_ID AND	");
    sb.append("                                                                                                            C.CERT_NBR = D.CERT_NBR AND	");
    sb.append("                                                                                                            C.PROD_ID = D.FUND_CODE	");
    sb.append("    WHERE A.PLAN_ID = :planId AND B.CUST_ID = :custId");
    sb.append("     GROUP BY C.DATA_DATE , C.PROD_NAME , C.INV_CRCY_TYPE , C.INV_COST_ORGD ,	");
    sb.append("              C.INV_COST_ORGD , C.REF_AMT_ORGD	");

    qc.setObject("planId", planId);
    qc.setObject("custId", custId);
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);

  }
}
