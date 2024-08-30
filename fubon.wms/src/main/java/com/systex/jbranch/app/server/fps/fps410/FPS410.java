package com.systex.jbranch.app.server.fps.fps410;

import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_SPPPK;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_SPPVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_SPP_HEADVO;
import com.systex.jbranch.app.server.fps.fps340.FPS340;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 特定目的投資組合計劃(STEP 2)投資組合建議
 *
 * @author Grace
 * @since 17-10-06
 */
@Component("fps410")
@Scope("request")
public class FPS410 extends FubonWmsBizLogic {

  SimpleDateFormat sdf = new SimpleDateFormat("yyMMmmss");
  private Logger logger = LoggerFactory.getLogger(FPS410.class);

  @Autowired
  @Qualifier("fps340")
  private FPS340 fps340;

  public FPS340 getFps340() {
    return fps340;
  }

  public void setFps340(FPS340 fps340) {
    this.fps340 = fps340;
  }

  /**
   * 取得查詢結果_查詢主畫面
   *
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   * @throws SQLException
   */
  @SuppressWarnings("rawtypes")
  public void inquire(Object body, IPrimitiveMap header) throws JBranchException, ParseException, SQLException {
    DataAccessManager dam = this.getDataAccessManager();
    FPS410InputVO inputVO = (FPS410InputVO) body;
    FPS410OutputVO outputVO = new FPS410OutputVO();
    String planID = inputVO.getPlanID();
    String custID = inputVO.getCustID();
    String action = inputVO.getAction();

    // 判斷cust_id和SPP_TYPE是否有值
    if (inputVO.getPlanID() != null) {
      if (("step1").equals(action)) {
        outputVO.setSettingList(getSetting(dam, planID, custID));
        outputVO.setOutputList(getReturnQuery(dam, planID, custID));

        // 績效追蹤使用
        Map<String, Object> outputMap = new HashMap<String, Object>();
        FPS340 fps340 = new FPS340();
        boolean commRsYn = inputVO.getCommRsYn();
        String fpsType = getFpsType(commRsYn);
        // 前言
        outputMap.put("briefList", fps340.getBriefList(dam, fpsType));
        // 使用指南
        outputMap.put("manualList", fps340.getManualList(dam, fpsType));
        // 商品的警語
        outputMap.put("warningList", fps340.getNoticeList(dam, planID));
        outputMap.put("rptPicture", fps340.getRptPicture(dam));
        // 規劃日期
        outputMap.put("planDate", FPSUtils.getLastUpdate(dam, planID, "SPP").toString());
        outputMap.put("achivedParamList", fps340.getAchivedParamList(dam, inputVO.getPlanID(), inputVO.getCustID()));
        outputVO.setOutputMap(outputMap);
      } else {
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT MARKET_OVERVIEW ");
        sb.append(" FROM TBFPS_MARKET_TREND ");
        sb.append(" WHERE STATUS='A' ");
        sb.append(" ORDER BY EFFECT_START_DATE DESC ");
        qc.setQueryString(sb.toString());
        List<Map<String, Object>> list = dam.exeQuery(qc);
        if (list.size() > 0) {
          outputVO.setMarketOverview(list.get(0).get("MARKET_OVERVIEW").toString());// 回傳資料(市場概況)
        } else {
          outputVO.setMarketOverview("");// 放空
        }
        outputVO.setSettingList(getSetting(dam, planID, custID));
        
        // 歷史資料
        List<Map<String, Object>> historyList = FPSUtils.getHistory(dam, planID, custID, "N");
        FPSUtils.mergeRedemption(dam, historyList);
        outputVO.setSppList(historyList);
        
        // 庫存資料
        List<Map<String, Object>> returnList = getReturnQuery(dam, planID, custID);
        FPSUtils.mergeRedemption(dam, returnList);
        outputVO.setOutputList(returnList);
        
        outputVO.setAchivedParamList(getFps340().getAchivedParamList(dam, planID, custID));
      }
    }
    outputVO.setRate(getRateFn());
    outputVO.setFeatureDescription(getFeatureDescription());

    this.sendRtnObject(outputVO);
  }

  private List<Map<String, Object>> getSppSet(String planID) throws DAOException, JBranchException {
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" WITH TREND AS ( ");
    sb.append(" SELECT TRD.TYPE, TRD.TREND ");
    sb.append(" FROM TBFPS_MARKET_TREND TR ");
    sb.append(" LEFT JOIN TBFPS_MARKET_TREND_DETAIL TRD ON TR.PARAM_NO = TRD.PARAM_NO ");
    sb.append(" WHERE TR.STATUS = 'A' ");
    sb.append(" ) ");
    sb.append("SELECT TPPS.*, ");
    sb.append("TREND.TREND AS CIS_3M ");
    sb.append("FROM TBFPS_PORTFOLIO_PLAN_SPP TPPS ");
    sb.append("LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = TPPS.PRD_ID ");
    sb.append("LEFT JOIN TREND ON TREND.TYPE = FU.INV_TARGET ");
    sb.append("WHERE TPPS.PLAN_ID = :planId ");

    qc.setObject("planId", planID);
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  // 取得header
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getSetting(DataAccessManager dam, String planID, String custId) throws JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append("WITH ");
    sb.append("TPPS AS (");
    sb.append("SELECT (POLICY_NO ||'-'|| TRIM(TO_CHAR(POLICY_SEQ  ,'00')) || CASE WHEN ID_DUP <> ' ' THEN '-' || ID_DUP END ) AS CERT_NBR ");
    sb.append("FROM TBFPS_PORTFOLIO_PLAN_SPP WHERE PLAN_ID = :planId AND POLICY_NO IS NOT NULL ");
    sb.append("UNION ");
    sb.append("SELECT CERTIFICATE_ID AS CERT_NBR FROM TBFPS_PLANID_MAPPING WHERE PLAN_ID = :planId ");
    sb.append("), ");
    sb.append("VAAD AS ( ");
    sb.append("SELECT :planId AS PLAN_ID, NVL(VAAD.INV_TYPE, '1') AS INV_TYPE, VAAD.INV_AMT_TWD ");
    sb.append("FROM MVFPS_AST_ALLPRD_DETAIL VAAD ");
    sb.append("WHERE CUST_ID = :custId ");
    sb.append("AND AST_TYPE IN ('07', '08', '09', '14') ");
    sb.append("AND VAAD.CERT_NBR IN (SELECT CERT_NBR FROM TPPS) ");
    sb.append(") ");
    sb.append("SELECT TPPSH.*, ");
    sb.append("NVL((SELECT SUM(INV_AMT_TWD) FROM VAAD WHERE INV_TYPE = '1'), 0) AS ONETIME_R, ");
    sb.append("NVL((SELECT SUM(INV_AMT_TWD) FROM VAAD WHERE INV_TYPE = '2'), 0) AS PERMONTH_R ");
    sb.append("FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD TPPSH ");
    sb.append("WHERE TPPSH.PLAN_ID = :planId ");
    qc.setObject("planId", planID);
    qc.setObject("custId", custId);
    qc.setQueryString(sb.toString());

    return dam.exeQuery(qc);
  }

  // get return table query 庫存
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getReturnQuery(DataAccessManager dam, String planID, String custID) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    // 主查詢
    sb.append("WITH ");
    sb.append("TPPS AS (");
    sb.append("SELECT (POLICY_NO ||'-'|| TRIM(TO_CHAR(POLICY_SEQ  ,'00')) || CASE WHEN ID_DUP <> ' ' THEN '-' || ID_DUP END ) AS CERT_NBR ");
    sb.append("FROM TBFPS_PORTFOLIO_PLAN_SPP WHERE PLAN_ID = :planId AND POLICY_NO IS NOT NULL ");
    sb.append("UNION ");
    sb.append("SELECT CERTIFICATE_ID AS CERT_NBR FROM TBFPS_PLANID_MAPPING WHERE PLAN_ID = :planId ");
    sb.append("), ");
    sb.append("VAAD AS ( ");
    sb.append("SELECT CASE VAAD.AST_TYPE WHEN '14' THEN 'INS' ELSE 'MFD' END AS PTYPE, VAAD.* ");
    sb.append("FROM MVFPS_AST_ALLPRD_DETAIL VAAD ");
    sb.append("WHERE CUST_ID = :custId ");
    sb.append("AND AST_TYPE IN ('07', '08', '09', '14') ");
    sb.append("AND VAAD.CERT_NBR IN (SELECT CERT_NBR FROM TPPS) ");
    sb.append("), ");
    sb.append("SYSPAR AS ( ");
    sb.append("SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FPS.FUND_TYPE' ");
    sb.append("), ");
    sb.append("GEN AS ( ");
    sb.append("SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_1' ");
    sb.append("), ");
    sb.append("SML AS ( ");
    sb.append("SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_2' ");
    sb.append(" ), ");
    sb.append("AREA AS ( ");
    sb.append("SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.MKT_TIER3' ");
    sb.append(" ), ");
    sb.append("TREND AS ( ");
    sb.append("SELECT TRD.TYPE, TRD.TREND ");
    sb.append("FROM TBFPS_MARKET_TREND TR ");
    sb.append("LEFT JOIN TBFPS_MARKET_TREND_DETAIL TRD ON TR.PARAM_NO = TRD.PARAM_NO ");
    sb.append("WHERE TR.STATUS = 'A'), ");
    sb.append("BASE_INS AS (  ");
    sb.append(" SELECT DISTINCT BASE.PRD_ID, BASE.CURR_CD, INSINFO.BASE_AMT_OF_PURCHASE, ");
    sb.append(" LISTAGG(BASE.KEY_NO, ',') WITHIN GROUP (ORDER BY BASE.KEY_NO) AS KEY_NO   ");
    sb.append(" FROM TBPRD_INS BASE ");
    sb.append(" LEFT JOIN TBPRD_INSINFO INSINFO ON BASE.PRD_ID = INSINFO.PRD_ID ");
    sb.append(" WHERE BASE.INS_TYPE IN ('1', '2') GROUP BY BASE.PRD_ID, BASE.CURR_CD, INSINFO.BASE_AMT_OF_PURCHASE ");
    sb.append(") ");
    sb.append("SELECT ");
    sb.append("SPP.SEQNO, ");
    sb.append("VWP.CURRENCY_STD_ID,");
    sb.append("VAAD.CERT_NBR,");
    sb.append("VAAD.PTYPE, ");
    sb.append("VAAD.PROD_ID AS PRD_ID, ");
    sb.append("CASE WHEN VAAD.PTYPE = 'MFD' THEN '1' ");
    sb.append(" 	 WHEN VAAD.PTYPE = 'INS' THEN '2' ");
    sb.append(" 	 END  AS P_TYPE, ");
    sb.append(" CASE WHEN VAAD.PTYPE = 'MFD' AND TRUNC(SYSDATE) BETWEEN FUIN.MAIN_PRD_SDATE AND FUIN.MAIN_PRD_EDATE THEN 'Y' ");
    sb.append("      WHEN VAAD.PTYPE = 'MFD' AND TRUNC(SYSDATE) BETWEEN FUIN.RAISE_FUND_SDATE AND FUIN.RAISE_FUND_EDATE THEN 'Y' ");
    sb.append("      WHEN VAAD.PTYPE = 'INS' AND VWP.PRD_RANK IS NOT NULL THEN 'Y' ");
    sb.append(" ELSE 'N' END AS MAIN_PRD, ");
    sb.append("VWP.PNAME AS PRD_CNAME, ");
    sb.append(":planId AS PLAN_ID, ");
    sb.append("VWP.RISKCATE_ID AS RISK_TYPE, ");
    sb.append("VAAD.CUR_ID_ORI AS CURRENCY_TYPE, ");
    sb.append("VAAD.CUR_ID AS TRUST_CURR, ");
    sb.append("VAAD.INV_AMT, ");
    sb.append("VAAD.INV_AMT_TWD, ");
    sb.append("VAAD.RTN_RATE_WD, ");
    sb.append("VAAD.RTN_RATE_WD_TWD, ");
    sb.append("TO_CHAR(VAAD.DATA_DATE, 'YYYY/MM/DD') AS DATA_DATE, ");
    sb.append("CASE WHEN VAAD.CUR_ID = 'TWD' THEN NOW_AMT_TWD ");
    sb.append("  ELSE NOW_AMT END AS PURCHASE_ORG_AMT, ");
    sb.append("VAAD.NOW_AMT_TWD AS PURCHASE_TWD_AMT, ");
    sb.append("CASE WHEN VAAD.CUR_ID = 'TWD' THEN NOW_AMT_TWD ");
    sb.append("  ELSE NOW_AMT END AS STORE_RAW, ");
    sb.append("VAAD.NOW_AMT_TWD AS STORE_NTD, ");
    sb.append("VAAD.EXCH_RATE AS EX_RATE, ");
    sb.append("VAAD.INV_TYPE AS INV_TYPE, ");
    sb.append("NVL2(VAAD.INV_TYPE, 'N' || VAAD.INV_TYPE, null) AS TXN_TYPE, ");
    sb.append("'Y' AS ORDER_STATUS, ");
    sb.append("FU.FUND_TYPE, ");
    sb.append("SYSPAR.PARAM_NAME AS FUND_TYPE_NAME, ");
    sb.append("FU.INV_TARGET AS MF_MKT_CAT, ");
    sb.append("AREA.PARAM_NAME AS NAME, ");
    sb.append("TREND.TREND AS CIS_3M, ");
    sb.append("SPP.TARGETS AS TARGETS,");
    sb.append("CASE VAAD.PTYPE ");
    sb.append(" WHEN 'MFD' THEN GEN.PARAM_NAME ");
    sb.append(" WHEN 'INS' THEN TO_CHAR(BASE_INS.BASE_AMT_OF_PURCHASE) ");
    sb.append(" ELSE NULL END AS GEN_SUBS_MINI_AMT_FOR, ");
    sb.append("SML.PARAM_NAME AS SML_SUBS_MINI_AMT_FOR, ");
    sb.append("CASE WHEN VAAD.PTYPE = 'INS' THEN BASE_INS.KEY_NO ");
    sb.append(" ELSE NULL END AS KEY_NO FROM VAAD VAAD ");
    sb.append("LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = VAAD.PROD_ID ");
    sb.append("LEFT JOIN GEN ON GEN.PARAM_CODE = FU.CURRENCY_STD_ID ");
    sb.append("LEFT JOIN SML ON SML.PARAM_CODE = FU.CURRENCY_STD_ID ");
    sb.append("LEFT JOIN SYSPAR ON SYSPAR.PARAM_CODE = FU.FUND_TYPE ");
    sb.append("LEFT JOIN TREND ON TREND.TYPE = FU.INV_TARGET ");
    sb.append("LEFT JOIN AREA ON AREA.PARAM_CODE = TREND.TYPE ");
    sb.append("LEFT JOIN VWPRD_MASTER VWP ON VWP.PRD_ID = VAAD.PROD_ID AND VWP.PTYPE = VAAD.PTYPE AND VWP.CURRENCY_STD_ID = VAAD.CUR_ID_ORI ");
    sb.append("LEFT JOIN TBPRD_FUNDINFO FUIN ON FUIN.PRD_ID = FU.PRD_ID ");
    sb.append("LEFT JOIN BASE_INS ON BASE_INS.PRD_ID = VAAD.PROD_ID AND BASE_INS.CURR_CD = VAAD.CUR_ID_ORI ");
    sb.append("LEFT JOIN TBFPS_PORTFOLIO_PLAN_SPP SPP ON VAAD.CERT_NBR = SPP.CERTIFICATE_ID ");
    sb.append("ORDER BY VAAD.PROD_ID ");

    // 查詢結果
    qc.setObject("planId", planID);
    qc.setObject("custId", custID);
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  // 組合特色說明
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getFeatureDescription() throws DAOException, JBranchException {
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT DE.FEATURE_DESC");
    sb.append(" FROM TBFPS_OTHER_PARA_HEAD HE");
    sb.append(" INNER JOIN TBFPS_OTHER_PARA DE ON HE.PARAM_NO = DE.PARAM_NO");
    sb.append(" WHERE HE.STATUS = 'A'");
    sb.append(" AND EFFECT_START_DATE <= SYSDATE");
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  // 商品績效&歷史交易明細
  public void inquirePrint(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
    FPS410InputVO inputVO = (FPS410InputVO) body;
    FPS410OutputVO outputVO = new FPS410OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    Map<String, Object> outputMap = new HashMap<String, Object>();

    sb = new StringBuffer();
    sb.append("SELECT * FROM (");
    sb.append("SELECT  TO_CHAR(A.DATA_DATE, 'YYYY/MM/DD') AS DATA_DATE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, A.INV_CRCY_TYPE AS VALU_CRCY_TYPE, A.INV_COST_ORGD, A.REF_AMT_ORGD, SUM(NVL(B.TXN_DIVID_ORGD, 0)) AS TXN_DIVID_ORGD, (SUM(NVL(B.TXN_DIVID_ORGD, 0)) + A.REF_AMT_ORGD) * 100 / A.INV_COST_ORGD - 100 AS RATIO ");
    sb.append("FROM TBCRM_AST_INV_DTL A ");
    sb.append("LEFT JOIN TBCRM_AST_INV_FUND_DIVID B ON A.CUST_ID = B.CUST_ID AND A.CERT_NBR = B.CERT_NBR AND A.PROD_ID = B.FUND_CODE ");
    sb.append("INNER JOIN TBFPS_PLANID_MAPPING MA ON A.CERT_NBR = MA.CERTIFICATE_ID AND MA.PLAN_ID = :planId ");
    sb.append("WHERE A.TXN_TYPE = '2' ");	// -- 贖回
    sb.append("AND A.CUST_ID = :custId ");
    sb.append("AND A.PROD_TYPE in ('PD01', 'PD02', 'PD03', 'PD04', 'PD06') "); // 基金
    sb.append("GROUP BY A.DATA_DATE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, A.INV_CRCY_TYPE, A.INV_COST_ORGD, A.REF_AMT_ORGD ");
    sb.append("ORDER BY DATA_DATE DESC ");
    sb.append(")WHERE ROWNUM <= 10 ");
    queryCondition.setObject("planId", inputVO.getPlanID());
    queryCondition.setObject("custId", inputVO.getCustID());
    queryCondition.setQueryString(sb.toString());
    outputMap.put("his", dam.exeQuery(queryCondition));

    queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    sb = new StringBuffer();
    sb.append("SELECT TPPS.PRD_ID, VWP.PNAME AS PRD_CNAME, NVL(RETURN_3M, 0) AS RETURN_3M, NVL(RETURN_6M, 0) AS RETURN_6M, NVL(RETURN_1Y, 0) AS RETURN_1Y, NVL(RETURN_2Y, 0) AS RETURN_2Y, NVL(RETURN_3Y, 0) AS RETURN_3Y, NVL(VOLATILITY, 0) AS VOLATILITY ");
    sb.append("FROM TBFPS_PORTFOLIO_PLAN_SPP TPPS ");
    sb.append("LEFT JOIN TBFPS_PRD_RETURN_M TPRM ON TPPS.PRD_ID = TPRM.PRD_ID AND DATA_YEARMONTH = (SELECT MAX(DATA_YEARMONTH) FROM TBFPS_PRD_RETURN_M WHERE PRD_ID = TPRM.PRD_ID) ");
    sb.append("LEFT JOIN VWPRD_MASTER VWP ON VWP.PRD_ID = TPPS.PRD_ID AND VWP.PTYPE = TPPS.PTYPE AND VWP.CURRENCY_STD_ID = TPPS.CURRENCY_TYPE  ");
    sb.append("WHERE  (TPPS.ORDER_STATUS IS NULL OR TPPS.ORDER_STATUS != 'Y') ");
    sb.append("AND PLAN_ID = :planId ");
    queryCondition.setObject("planId", inputVO.getPlanID());
    queryCondition.setQueryString(sb.toString());
    outputMap.put("returnM", dam.exeQuery(queryCondition));

    outputVO.setOutputMap(outputMap);
    this.sendRtnObject(outputVO);
  }

  /**
   * 儲存
   *
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  public void save(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
    FPS410InputVO inputVO = (FPS410InputVO) body;
    DataAccessManager dam = this.getDataAccessManager();
    String planID = inputVO.getPlanID();

    // 更新header
    if (StringUtils.isBlank(planID)) {
      throw new APException("查無計畫"); // error xml
    } else {
      update(dam, inputVO);

      // 更新products delete all !='Y' then create
      if (inputVO.getPrdList() != null && StringUtils.isNotBlank(planID)) {
        // get exist seqNos
        Set<BigDecimal> existSet = new HashSet<BigDecimal>();
        // get filtered targetLs
        for (FPS410PrdInputVO item : inputVO.getPrdList()) {
          BigDecimal seq = item.getSEQNO();
          if (seq != null) {
            existSet.add(seq);
          }
        }
        // get delete list
        List<Map<String, Object>> deleteSeqNoLs = FPSUtils.filterDeleteList(dam, existSet, planID, "SPP");
        // delete
        for (Map<String, Object> del : deleteSeqNoLs) {
          delete(dam, planID, (BigDecimal) del.get("SEQNO"));
        }

        for (FPS410PrdInputVO prd : inputVO.getPrdList()) {
          if (!StringUtils.equals("Y", prd.getORDER_STATUS()) && prd.getSEQNO() == null) {
            create(dam, prd, planID);
          } else if (prd.getSEQNO() != null){
            update(dam, prd, planID);
          }
        }
      }
    }
    this.sendRtnObject(new Object[] { planID, fps340.getAchivedParamList(dam, inputVO.getPlanID(), inputVO.getCustID()) });
  }

  // 刪除計畫資料 by planID & !='Y'
  public boolean deletePlanDetails(DataAccessManager dam, String planID) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    try {
      sb.append(" DELETE FROM TBFPS_PORTFOLIO_PLAN_SPP");
      sb.append(" WHERE PLAN_ID = :planID");
      sb.append(" AND (ORDER_STATUS != 'Y' OR ORDER_STATUS IS NULL)");
      qc.setObject("planID", planID);

      qc.setQueryString(sb.toString());
      dam.exeUpdate(qc);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  // FPS410 Plan
  public String create(DataAccessManager dam, FPS410InputVO inputVO, String userID) throws JBranchException {
    Calendar cal = Calendar.getInstance();
    String year = String.valueOf(cal.get(Calendar.YEAR));
    String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
    String date = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
    String seq = "SPP" + year + month + date + String.format("%05d", Long.parseLong(getSEQ(dam, "Plan")));

    TBFPS_PORTFOLIO_PLAN_SPP_HEADVO vo = putPlanSppHeadVO(new TBFPS_PORTFOLIO_PLAN_SPP_HEADVO(), inputVO);
    vo.setPLAN_ID(seq);// 投組編號
    vo.setCUST_ID(inputVO.getCustID());// 客戶ID
    vo.setSPP_TYPE(inputVO.getSppType());// 特定規劃項目
    vo.setPLAN_STATUS("PLAN_STEP");// 規劃狀態
    vo.setVALID_FLAG(null);// 成立失效註記
    vo.setTRACE_FLAG(null);// 追蹤註記
    vo.setSTEP_STAUS(null);// 步驟

    dam.create(vo);

    return seq;
  }

  // 更新 Plan
  public void update(DataAccessManager dam, FPS410InputVO inputVO) throws JBranchException, ParseException {
    TBFPS_PORTFOLIO_PLAN_SPP_HEADVO vo = putPlanSppHeadVO((TBFPS_PORTFOLIO_PLAN_SPP_HEADVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_SPP_HEADVO.TABLE_UID, inputVO.getPlanID()), inputVO);

    dam.update(vo);
  }

  // 刪除 Plan
  public void delete(DataAccessManager dam, FPS410InputVO inputVO) throws JBranchException, ParseException {
    TBFPS_PORTFOLIO_PLAN_SPP_HEADVO vo = (TBFPS_PORTFOLIO_PLAN_SPP_HEADVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_SPP_HEADVO.TABLE_UID, inputVO.getPlanID());

    dam.delete(vo);
  }

  private TBFPS_PORTFOLIO_PLAN_SPP_HEADVO putPlanSppHeadVO(TBFPS_PORTFOLIO_PLAN_SPP_HEADVO vo, FPS410InputVO inputVO) {
    vo.setCUST_ID(inputVO.getCustID());
    vo.setSPP_TYPE(inputVO.getSppType());
    vo.setRISK_ATTR(inputVO.getRISK_ATTR());// 客戶風險屬性等級
    vo.setVOL_RISK_ATTR(inputVO.getVOL_RISK_ATTR());// 投組波動風險承受度
    vo.setINV_PLAN_NAME(inputVO.getPlanName());// 投資計劃名稱
    vo.setINV_PERIOD(new BigDecimal(inputVO.getINV_PERIOD()));// 投資期間(年)
    vo.setINV_AMT_ONETIME(new BigDecimal(inputVO.getINV_AMT_ONETIME()));// 投資金額(單筆/元)
    vo.setINV_AMT_PER_MONTH(new BigDecimal(inputVO.getINV_AMT_PER_MONTH()));// 投資金額(每月定額/元)
    vo.setINV_AMT_TARGET(new BigDecimal(inputVO.getINV_AMT_TARGET()));// 目標所需資金(元)(台幣)

    return vo;
  }

  // 新增 Product
  public String create(DataAccessManager dam, FPS410PrdInputVO inputVO, String planID) throws JBranchException {
    String seq = getSEQ(dam, "Product");
    TBFPS_PORTFOLIO_PLAN_SPPPK pk = new TBFPS_PORTFOLIO_PLAN_SPPPK();
    pk.setSEQNO(new BigDecimal(seq));
    pk.setPLAN_ID(planID);
    TBFPS_PORTFOLIO_PLAN_SPPVO vo = putPlanSppVO(new TBFPS_PORTFOLIO_PLAN_SPPVO(), inputVO);
    vo.setcomp_id(pk);
    vo.setSTATUS("S");
    dam.create(vo);

    return seq;
  }

  // 更新 Product
  public void update(DataAccessManager dam, FPS410PrdInputVO inputVO, String planID) throws JBranchException, APException {
    TBFPS_PORTFOLIO_PLAN_SPPPK pk = new TBFPS_PORTFOLIO_PLAN_SPPPK();
    pk.setSEQNO(inputVO.getSEQNO());
    pk.setPLAN_ID(planID);
    TBFPS_PORTFOLIO_PLAN_SPPVO vo = (TBFPS_PORTFOLIO_PLAN_SPPVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_SPPVO.TABLE_UID, pk);
    vo.setSTATUS(inputVO.getSTATUS());
    if (vo != null) {
      dam.update(putPlanSppVO(vo, inputVO));
    } else {
      throw new APException("ehl_01_common_007"); // 更新失敗
    }
  }

  // 刪除 Product
  public void delete(DataAccessManager dam, String planID, BigDecimal seqNo) throws JBranchException, APException {
    TBFPS_PORTFOLIO_PLAN_SPPPK pk = new TBFPS_PORTFOLIO_PLAN_SPPPK();
    pk.setSEQNO(seqNo);
    pk.setPLAN_ID(planID);
    TBFPS_PORTFOLIO_PLAN_SPPVO vo = (TBFPS_PORTFOLIO_PLAN_SPPVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_SPPVO.TABLE_UID, pk);
    if (vo != null) {
      dam.delete(vo);
    } else {
      throw new APException("ehl_01_common_005"); // 刪除失敗
    }
  }

  private TBFPS_PORTFOLIO_PLAN_SPPVO putPlanSppVO(TBFPS_PORTFOLIO_PLAN_SPPVO vo, FPS410PrdInputVO inputVO) {
    vo.setPRD_ID(inputVO.getPRD_ID());
    vo.setPTYPE(inputVO.getPTYPE());
    vo.setRISK_TYPE(inputVO.getRISK_TYPE());
    vo.setCURRENCY_TYPE(inputVO.getCURRENCY_TYPE());
    vo.setTRUST_CURR(inputVO.getTRUST_CURR());
    vo.setMARKET_CIS(inputVO.getMARKET_CIS());
    vo.setPURCHASE_ORG_AMT(inputVO.getPURCHASE_ORG_AMT());
    vo.setPURCHASE_TWD_AMT(inputVO.getPURCHASE_TWD_AMT());
    vo.setPORTFOLIO_RATIO(inputVO.getPORTFOLIO_RATIO());
    vo.setLIMIT_ORG_AMT(inputVO.getLIMIT_ORG_AMT());
    vo.setPORTFOLIO_TYPE(inputVO.getPORTFOLIO_TYPE());
    vo.setTXN_TYPE(inputVO.getTXN_TYPE());
    vo.setINV_TYPE(inputVO.getINV_TYPE());
    vo.setEX_RATE(inputVO.getEX_RATE());
    vo.setPURCHASE_ORG_AMT_ORDER(inputVO.getPURCHASE_ORG_AMT_ORDER());
    vo.setPURCHASE_TWD_AMT_ORDER(inputVO.getPURCHASE_TWD_AMT_ORDER());
    vo.setORDER_STATUS(inputVO.getORDER_STATUS());
    vo.setTARGETS(inputVO.getTARGETS());

    // vo.setCERTIFICATE_ID(inputVO.getCERTIFICATE_ID());

    return vo;
  }

  /**
   * FPS410 商品替換規則
   *
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  @SuppressWarnings("unchecked")
  public void switchPRD(Object body, IPrimitiveMap header) throws JBranchException {
    FPS410InputVO inputVO = (FPS410InputVO) body;
    FPS410OutputVO outputVO = new FPS410OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(dam.QUERY_LANGUAGE_TYPE_VAR_SQL);

    StringBuffer sb = new StringBuffer();
    sb.append("SELECT A.IS_PRIMARY_SALE,A.IS_BEST_CHOICE,A.IS_RETIRED,A.IS_EDUCATION, ");
    sb.append("A.IS_BUYHOUSE,A.IS_SAVINGS,A.IS_DIVIDEND,A.PRD_ID,A.PRD_CNAME, ");
    sb.append("A.IS_CAM_PROD,A.PTYPE,A.CURRENCY_TYPE,A.RISK_TYPE,D.NAME,F.CIS_3M,B.GEN_SUBS_MINI_AMT_FOR,B.SML_SUBS_MINI_AMT_FOR,D.MF_MKT_CAT ");
    sb.append("FROM TBPRD_PRODUCT_MAIN A ");
    sb.append("JOIN TBPRD_FUND B ON A.PRD_ID = B.PRD_ID ");
    sb.append("JOIN TBPRD_FUND_MKT_PRD_MAPPING C ON B.prd_id = C.prd_id ");
    sb.append("JOIN TBPRD_FUND_MKT_CAT D ON C.mf_mkt_cat = D.mf_mkt_cat ");
    sb.append("LEFT JOIN TBPRD_FUND_MKT_SUGGEST F ON C.MF_MKT_CAT = F.MF_MKT_CAT ");
    sb.append("LEFT JOIN (SELECT DISTINCT PARAM_CODE,REGEXP_SUBSTR(PARAM_NAME,'[^,]+',1,LEVEL) PARAM_NAME ");
    sb.append("FROM (SELECT PARAM_CODE,PARAM_NAME FROM  TBSYSPARAMETER WHERE PARAM_TYPE='FPS.RISK_ATTR_MAPPING') ");
    sb.append("CONNECT BY REGEXP_SUBSTR(PARAM_NAME,'[^,]+',1,LEVEL) IS NOT NULL)E ON E.PARAM_CODE = A.RISK_TYPE ");
    sb.append("WHERE E.PARAM_NAME = :risk AND A.prd_id <> :prdId AND D.MF_MKT_CAT = :mfmktcat ");
    sb.append("AND A.PTYPE IN ('FUND_D', 'FUND_O', 'FUND_C') ");
    sb.append("AND B.WORKFLOW_SEQ IS NULL AND A.IS_AVAILABLE= 'Y' ");
    sb.append("AND A.IS_PRIMARY_SALE ='Y' AND A.workflow_seq IS NULL ");

    qc.setObject("risk", inputVO.getRiskType());
    qc.setObject("prdID", inputVO.getPrdID());
    qc.setObject("mfmktcat", inputVO.getMfmktcat());
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> list = dam.exeQuery(qc);
    outputVO.setOutputList(list);// 回傳資料

    this.sendRtnObject(outputVO);
  }

  /**
   * FPS410 取得匯率
   *
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  public void getRate(Object body, IPrimitiveMap header) throws JBranchException {
    FPS410OutputVO outputVO = new FPS410OutputVO();

    outputVO.setRate(getRateFn());
    sendRtnObject(outputVO);
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getRateFn() throws DAOException, JBranchException {
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(dam.QUERY_LANGUAGE_TYPE_VAR_SQL);

    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT CUR_COD, BUY_RATE FROM TBPMS_IQ053 ");
    sb.append(" WHERE MTN_DATE = (SELECT MAX (MTN_DATE) FROM TBPMS_IQ053) ");
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  /**
   * FPS410_STOCK 取得帳號
   *
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  @SuppressWarnings("unchecked")
  public void getAccount(Object body, IPrimitiveMap header) throws JBranchException {
    FPS410InputVO inputVO = (FPS410InputVO) body;
    FPS410OutputVO outputVO = new FPS410OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    qc.setQueryString(" SELECT DISTINCT acc_no FROM TBSFA_DAT_AST_FUND WHERE CUST_ID = :CustID ").setObject("CustID", inputVO.getCustID());

    outputVO.setOutputList(dam.exeQuery(qc));
    sendRtnObject(outputVO);
  }

  /**
   * FPS410_STOCK 取得查詢結果
   *
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  @SuppressWarnings("unchecked")
  public void inquireStock(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
    FPS410OutputVO outputVO = new FPS410OutputVO();
    FPS410InputVO inputVO = (FPS410InputVO) body;
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT MAIN.PTYPE,FUND.ACC_NO,Substr(FUND.PRD_ID, 1, 4) SUB_PRD_ID,FUND.PRD_ID as Stock_PRD_ID,FUND.PRD_ID,FUND.PRD_NAME,FUND.BUY_DATE, ");
    sb.append(" FUND.RISK_LEVEL,FUND.CUR_ID,FUND.BUY_CUR_ID,FUND.UNIT_BUY,FUND.NAV_DATE,FUND.NAV,FUND.AVG_FUND_NAV, ");
    sb.append(" FUND.AMT_NOW_FOR,FUND.AMT_NOW_TWD,FUND.AMT_BUY,CAT.MF_MKT_CAT,CAT.name,SUG.CIS_3M,B.GEN_SUBS_MINI_AMT_FOR ");
    sb.append(" FROM TBSFA_DAT_AST_FUND FUND ");
    sb.append(" LEFT JOIN TBPRD_FUND B ON Substr(FUND.PRD_ID, 1, 4) = B.PRD_ID ");
    sb.append(" LEFT JOIN TBPRD_PRODUCT_MAIN MAIN ON B.PRD_ID = MAIN.PRD_ID ");
    sb.append(" LEFT JOIN TBPRD_FUND_MKT_PRD_MAPPING MAP ON MAP.PRD_ID = MAIN.PRD_ID ");
    sb.append(" LEFT JOIN TBPRD_FUND_MKT_CAT CAT ON MAP.MF_MKT_CAT = CAT.MF_MKT_CAT ");
    sb.append(" LEFT JOIN TBPRD_FUND_MKT_SUGGEST SUG ON CAT.MF_MKT_CAT = SUG.MF_MKT_CAT ");
    sb.append(" WHERE FUND.ACC_NO = :LoginID ");
    sb.append(" AND FUND.PRD_ID NOT IN ( ");
    sb.append(" SELECT b.FND_CODE FROM TBFPS_SPP_PRD_RETURN_HEAD a ");
    sb.append(" JOIN TBFPS_SPP_PRD_RETURN b ON a.plan_id = b.plan_id ");
    sb.append(" WHERE a.status not in ('D' , 'C') ");
    sb.append(" ) ");

    qc.setObject("LoginID", inputVO.getCustID());

    rtnPaginQuery(dam, qc, sb, outputVO, inputVO);
    // this.sendRtnObject(outputVO);
  }

  /**
   * FPS322 取得平均餘命 取得查詢結果
   *
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  public void avgAge(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
    FPS410OutputVO outputVO = new FPS410OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    // 查詢
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT LIFE_AGE FROM TBFPS_OTHER_PARA A ");
    sb.append(" JOIN TBFPS_OTHER_PARA_HEAD b ON A.param_no = b.param_no ");
    sb.append(" WHERE b.status = 'A' ");
    sb.append(" AND b.effect_start_date = ( ");
    sb.append(" SELECT MAX(effect_start_date) ");
    sb.append(" FROM TBFPS_OTHER_PARA_HEAD ");
    sb.append(" WHERE status = 'A' ");
    sb.append(" AND effect_start_date <=SYSDATE) ");

    // 查詢結果
    queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    queryCondition.setQueryString(sb.toString());
    List<Map<String, Object>> ageList = dam.exeQuery(queryCondition);
    outputVO.setAvgAge(((BigDecimal) ageList.get(0).get("LIFE_AGE")).intValue());// 回傳資料

    this.sendRtnObject(outputVO);
  }

  /**
   * FPS320 檢查重複命名-新增模式 取得查詢結果
   *
   * @param body
   * @param header
   * @throws JBranchException
   */
  @SuppressWarnings("unchecked")
  public void checkName(Object body, IPrimitiveMap header) throws JBranchException {
    FPS410InputVO inputVO = (FPS410InputVO) body;
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT COUNT(*) AS CHECKER ");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD ");
    sb.append(" WHERE INV_PLAN_NAME = :planName ");
    // sb.append(" AND VALID_FLAG = 'Y' ");
    qc.setObject("planName", inputVO.getPlanName());

    if (StringUtils.isNotBlank(inputVO.getPlanID())) {
      sb.append(" AND PLAN_ID <> :planid");
      qc.setObject("planid", inputVO.getPlanID());
    }

    // 查詢結果
    qc.setQueryString(sb.toString());
    int checker = Integer.parseInt(((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("CHECKER").toString());

    this.sendRtnObject(checker);
  }

  // 執行查詢分頁回傳
  @SuppressWarnings("unchecked")
  private void rtnPaginQuery(DataAccessManager dam, QueryConditionIF qc, StringBuffer sb, FPS410OutputVO outputVO, FPS410InputVO inputVO) throws DAOException, JBranchException {
    qc.setQueryString(sb.toString());
    ResultIF pageList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
    outputVO.setOutputList(pageList);// 回傳資料
    outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
    outputVO.setTotalPage(pageList.getTotalPage());// 總頁次
    outputVO.setTotalRecord(pageList.getTotalRecord());// 總筆數
    this.sendRtnObject(outputVO);
  }

  // 產生流水號
  @SuppressWarnings("unchecked")
  public String getSEQ(DataAccessManager dam, String type) throws JBranchException {
    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sql = new StringBuffer();
    if (type.equals("Plan")) {
      sql.append("SELECT TBFPS_PORTFOLIO_PLAN_SPP_H_SEQ.NEXTVAL AS SEQ FROM DUAL");
    } else if (type.equals("Product")) {
      sql.append("SELECT TBFPS_PORTFOLIO_PLAN_SPP_SEQ.NEXTVAL AS SEQ FROM DUAL");
    }
    queryCondition.setQueryString(sql.toString());
    List<Map<String, Object>> seqNO = dam.exeQuery(queryCondition);
    return seqNO.get(0).get("SEQ").toString();
  }

  /**
   * 依首作/非首作、有推介/無推介判斷規劃書類型
   *
   * @param commRsYn
   *          推介
   * @return
   */
  private String getFpsType(boolean commRsYn) {
    if (commRsYn) {
      return "N4";
    }
    return "N3";
  }

}
