package com.systex.jbranch.app.server.fps.fps324;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_SPPPK;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_SPPVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_SPP_HEADVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_SPP_PLAN_HELP_EVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_SPP_PLAN_HELP_RVO;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 特定目的投資組合計劃(STEP 2)投資組合建議
 * 
 * @author Grace
 * @since 17-10-06
 */
@Component("fps324")
@Scope("request")
public class FPS324 extends FubonWmsBizLogic {

  SimpleDateFormat sdf = new SimpleDateFormat("yyMMmmss");
  private Logger logger = LoggerFactory.getLogger(FPS324.class);

  /**
   * 取得查詢結果_查詢主畫面
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void inquire(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
    FPS324InputVO inputVO = (FPS324InputVO) body;
    FPS324OutputVO outputVO = new FPS324OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
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

    // 判斷cust_id和SPP_TYPE是否有值
    if (StringUtils.equals("Y", inputVO.getIsChange())) {
      deleteAllPrd(dam, inputVO.getPlanID());
      outputVO.setOutputList(getMainQuery(inputVO));
    } else if (inputVO.getPlanID() != null) {
      outputVO.setSettingList(getSetting(inputVO));
      outputVO.setOutputList(getHistory(inputVO));
      // 規劃日期
      outputVO.setPlanDate(FPSUtils.getLastUpdate(dam, inputVO.getPlanID(), "SPP").toString());
    } else {
      outputVO.setOutputList(new ArrayList<Map<String, Object>>());
    }
    outputVO.setRecommendList(getMainQuery(inputVO));
    outputVO.setRate(getRateFn());
    outputVO.setFeatureDescription(getFeatureDescription(dam));

    this.sendRtnObject(outputVO);
  }

  private void deleteAllPrd(DataAccessManager dam, String planId) throws DAOException, JBranchException{
    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer("DELETE TBFPS_PORTFOLIO_PLAN_SPP WHERE PLAN_ID = :planId ");
    queryCondition.setObject("planId", planId);
    queryCondition.setQueryString(sb.toString());
    dam.exeUpdate(queryCondition);
  }

  private List<Map<String, Object>> getSetting(FPS324InputVO inputVO) throws JBranchException{
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT PLAN_ID, CUST_ID, SPP_TYPE, RISK_ATTR, VOL_RISK_ATTR, INV_PLAN_NAME, INV_PERIOD, INV_AMT_ONETIME, INV_AMT_PER_MONTH, INV_AMT_TARGET, VALID_FLAG, TRACE_FLAG, STEP_STAUS, ");
    sb.append(" CASE WHEN HE.PLAN_STATUS = 'ACTIVE' THEN 'ACTIVE' ");
    sb.append(" 	 WHEN HE.PLAN_STATUS = 'PLAN_STEP' THEN 'PLAN_STEP' ");
    sb.append(" 	 WHEN HE.PLAN_STATUS = 'PRINT_REJECT' THEN 'PRINT_REJECT' ");
    sb.append(" 	 ELSE CASE WHEN (SELECT COUNT(*) FROM (SELECT (PURCHASE_ORG_AMT_ORDER - PURCHASE_ORG_AMT) AS CHECK_AMT FROM TBFPS_PORTFOLIO_PLAN_SPP WHERE PLAN_ID = HE.PLAN_ID) WHERE CHECK_AMT >= 0) > 0 THEN 'PRINT_ORDER' ELSE 'PRINT_THINK' END END ");
    sb.append(" AS PLAN_STATUS ");
    sb.append("FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD HE WHERE PLAN_ID = :planID ");
    qc.setObject("planID", inputVO.getPlanID());
    qc.setQueryString(sb.toString());

    return dam.exeQuery(qc);
  }

  public void sugQuery(Object body, IPrimitiveMap header) throws JBranchException{
    FPS324InputVO inputVO = (FPS324InputVO) body;
    FPS324OutputVO outputVo = new FPS324OutputVO();

    outputVo.setOutputList(getMainQuery(inputVO));
    this.sendRtnObject(outputVo);
  }

  // recommend model portfolio
  public List<Map<String, Object>> getMainQuery(FPS324InputVO inputVO) throws DAOException, JBranchException{
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    // 主查詢
    sb.append(" WITH GEN AS ( ");
    sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_1'), ");
    sb.append(" SML AS ( ");
    sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_2'), ");
    sb.append(" SYSPAR AS ( ");
    sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FPS.FUND_TYPE'), ");
    sb.append(" AREA AS ( ");
    sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.MKT_TIER3'), ");
    sb.append(" TREND AS ( ");
    sb.append(" SELECT TRD.TYPE, TRD.TREND ");
    sb.append(" FROM TBFPS_MARKET_TREND TR ");
    sb.append(" LEFT JOIN TBFPS_MARKET_TREND_DETAIL TRD ON TR.PARAM_NO = TRD.PARAM_NO ");
    sb.append(" WHERE TR.STATUS = 'A'), ");
    sb.append(" BASE_INS AS ( ");
    sb.append(" SELECT DISTINCT BASE.PRD_ID, BASE.CURR_CD, INSINFO.BASE_AMT_OF_PURCHASE, ");
    sb.append(" LISTAGG(BASE.KEY_NO, ',') WITHIN GROUP (ORDER BY BASE.KEY_NO) AS KEY_NO ");
    sb.append(" FROM TBPRD_INS BASE ");
    sb.append(" LEFT JOIN TBPRD_INSINFO INSINFO ON BASE.PRD_ID = INSINFO.PRD_ID ");
    sb.append(" WHERE BASE.INS_TYPE IN ('1', '2') GROUP BY BASE.PRD_ID, BASE.CURR_CD, INSINFO.BASE_AMT_OF_PURCHASE), ");
    sb.append(" INS_TG AS ( ");
    sb.append(" SELECT DE.SEQNO, ");
    sb.append("   LISTAGG(INS.TARGET_ID, '/') WITHIN GROUP (ORDER BY INS.TARGET_ID) AS TARGETS ");
    sb.append("   FROM TBFPS_MODEL_ALLOCATION_INS INS ");
    sb.append("   LEFT JOIN TBFPS_MODEL_ALLOCATION DE ON INS.SEQNO = DE.SEQNO ");
    sb.append("   GROUP BY DE.SEQNO ");
    sb.append(" ) ");
    sb.append(" SELECT ");
    sb.append(" FUIN.STOCK_BOND_TYPE, ");
    sb.append(" DE.INV_PRD_TYPE, ");
    sb.append(" DE.INV_PRD_TYPE_2, ");
    sb.append(" DE.PRD_TYPE AS PTYPE, ");
    sb.append(" VWP.PRD_ID AS PRD_ID, ");
    sb.append(" CASE WHEN TRUNC(SYSDATE) BETWEEN FUIN.MAIN_PRD_SDATE AND FUIN.MAIN_PRD_EDATE THEN 'Y' ");
    sb.append("      WHEN TRUNC(SYSDATE) BETWEEN FUIN.RAISE_FUND_SDATE AND FUIN.RAISE_FUND_EDATE THEN 'Y' ");
    sb.append(" ELSE 'N' END AS MAIN_PRD, ");
    sb.append(" VWP.PNAME AS PRD_CNAME, ");
    sb.append(" VWP.RISKCATE_ID AS RISK_TYPE, ");
    sb.append(" DE.CURRENCY_STD_ID AS CURRENCY_TYPE, ");
    sb.append(" DE.INV_PERCENT, ");
    sb.append(" TREND.TREND AS CIS_3M, ");
    sb.append(" CASE DE.PRD_TYPE ");
    sb.append(" WHEN 'MFD' THEN GEN.PARAM_NAME ");
    sb.append(" WHEN 'INS' THEN TO_CHAR(BASE_INS.BASE_AMT_OF_PURCHASE) ");
    sb.append(" ELSE NULL END AS GEN_SUBS_MINI_AMT_FOR, ");
    sb.append(" SML.PARAM_NAME AS SML_SUBS_MINI_AMT_FOR, ");
    sb.append(" FU.FUND_TYPE, ");
    sb.append(" SYSPAR.PARAM_NAME AS FUND_TYPE_NAME, ");
    sb.append(" FU.INV_TARGET AS MF_MKT_CAT, ");
    sb.append(" AREA.PARAM_NAME AS NAME, ");
    sb.append(" CASE WHEN DE.PRD_TYPE = 'INS' THEN BASE_INS.KEY_NO ");
    sb.append("  ELSE NULL END AS KEY_NO, ");
    sb.append(" INS_TG.TARGETS ");
    sb.append(" FROM TBFPS_MODEL_ALLOCATION_HEAD HE ");
    sb.append(" INNER JOIN TBFPS_MODEL_ALLOCATION DE ON DE.PARAM_NO = HE.PARAM_NO ");
    sb.append(" LEFT JOIN VWPRD_MASTER VWP ON VWP.PTYPE = DE.PRD_TYPE AND VWP.PRD_ID = DE.PRD_ID AND VWP.CURRENCY_STD_ID = DE.CURRENCY_STD_ID ");
    sb.append(" LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = DE.PRD_ID ");
    sb.append(" LEFT JOIN TBPRD_FUNDINFO FUIN ON FUIN.PRD_ID = FU.PRD_ID ");
    sb.append(" LEFT JOIN GEN ON GEN.PARAM_CODE = FU.CURRENCY_STD_ID ");
    sb.append(" LEFT JOIN SML ON SML.PARAM_CODE = FU.CURRENCY_STD_ID ");
    sb.append(" LEFT JOIN SYSPAR ON SYSPAR.PARAM_CODE = FU.FUND_TYPE ");
    sb.append(" LEFT JOIN TREND ON TREND.TYPE = FU.INV_TARGET ");
    sb.append(" LEFT JOIN AREA ON AREA.PARAM_CODE = TREND.TYPE ");
    sb.append(" LEFT JOIN BASE_INS ON BASE_INS.PRD_ID = DE.PRD_ID AND BASE_INS.CURR_CD = DE.CURRENCY_STD_ID ");
    sb.append(" LEFT JOIN INS_TG ON INS_TG.SEQNO = DE.SEQNO ");
    sb.append(" WHERE HE.STATUS = 'A' ");
    sb.append(" AND HE.SETTING_TYPE = '1' ");
    sb.append(" AND HE.EFFECT_START_DATE <= SYSDATE + 1");
    sb.append(" AND DE.CUST_RISK_ATR = :riskType ");
    sb.append(" AND SUBSTR(VWP.RISKCATE_ID,2) <= :riskLevel ");
    sb.append(" AND DE.INV_PRD_TYPE = '3' ");
    if (StringUtils.isNotBlank(inputVO.getINV_AMT_TYPE())) {
      sb.append(" AND HE.INV_AMT_TYPE = :invType ");
      qc.setObject("invType", inputVO.getINV_AMT_TYPE());
    }

    // 查詢結果
    String riskType = inputVO.getRiskType() == null ? "" : inputVO.getRiskType();
    qc.setObject("riskType", riskType);
    qc.setObject("riskLevel", riskType.substring(1, 2));
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  // history model portfolio fake
  public List<Map<String, Object>> getHistory(FPS324InputVO inputVO) throws DAOException, JBranchException{
    DataAccessManager dam = this.getDataAccessManager();
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
    sb.append(" FUIN.STOCK_BOND_TYPE, S.CERTIFICATE_ID, ");
    sb.append(" S.PLAN_ID, S.SEQNO, S.PTYPE, S.PRD_ID, ");
    sb.append(" CASE WHEN S.PTYPE = 'MFD' THEN '1' ");
    sb.append(" 	 WHEN S.PTYPE = 'INS' THEN '2' ");
    sb.append(" 	 END  AS P_TYPE, ");
    sb.append(" CASE WHEN S.PTYPE = 'MFD' AND TRUNC(SYSDATE) BETWEEN FUIN.MAIN_PRD_SDATE AND FUIN.MAIN_PRD_EDATE THEN 'Y' ");
    sb.append("      WHEN S.PTYPE = 'MFD' AND TRUNC(SYSDATE) BETWEEN FUIN.RAISE_FUND_SDATE AND FUIN.RAISE_FUND_EDATE THEN 'Y' ");
    sb.append("      WHEN S.PTYPE = 'INS' AND VWP.PRD_RANK IS NOT NULL THEN 'Y' ");
    sb.append("      ELSE 'N' END AS MAIN_PRD, ");
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
    sb.append(" JOIN TBFPS_PORTFOLIO_PLAN_SPP S ON S.PLAN_ID = SH.PLAN_ID ");
    sb.append(" LEFT JOIN VWPRD_MASTER VWP ON VWP.PRD_ID = S.PRD_ID AND VWP.PTYPE = S.PTYPE AND VWP.CURRENCY_STD_ID = S.CURRENCY_TYPE ");
    sb.append(" LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = S.PRD_ID ");
    sb.append(" LEFT JOIN TBPRD_FUNDINFO FUIN ON FUIN.PRD_ID = FU.PRD_ID ");
    sb.append(" LEFT JOIN GEN ON GEN.PARAM_CODE = FU.CURRENCY_STD_ID ");
    sb.append(" LEFT JOIN SML ON SML.PARAM_CODE = FU.CURRENCY_STD_ID ");
    sb.append(" LEFT JOIN SYSPAR ON SYSPAR.PARAM_CODE = FU.FUND_TYPE ");
    sb.append(" LEFT JOIN TREND ON TREND.TYPE = FU.INV_TARGET ");
    sb.append(" LEFT JOIN AREA ON AREA.PARAM_CODE = TREND.TYPE ");
    sb.append(" LEFT JOIN BASE_INS ON BASE_INS.PRD_ID = S.PRD_ID AND BASE_INS.CURR_CD = S.CURRENCY_TYPE ");
    // sb.append(" WHERE SH.PLAN_STATUS = 'PLAN_STEP' AND SH.CUST_ID = :custId AND
    // SH.PLAN_ID = :planId ");
    sb.append(" WHERE SH.CUST_ID = :custId AND SH.PLAN_ID = :planId ");
    sb.append(" ORDER BY P_TYPE ");

    // 查詢結果
    qc.setObject("custId", inputVO.getCustId());
    qc.setObject("planId", inputVO.getPlanID());
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  // 組合特色說明
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getFeatureDescription(DataAccessManager dam) throws DAOException, JBranchException{
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

  /**
   * 投資組合
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws IOException
   */
  public void getModels(Object body, IPrimitiveMap<?> header) throws JBranchException{
    FPS324InputVO inputVO = (FPS324InputVO) body;
    FPS324OutputVO outputVO = new FPS324OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    String riskType = inputVO.getRiskType();
    String invType = inputVO.getINV_AMT_TYPE();

    if (StringUtils.isBlank(riskType)) {
      throw new APException("沒有RiskType");
    }
    outputVO.setOutputList(modelQuery(dam, riskType, invType));

    this.sendRtnObject(outputVO);
  }

  // get init model
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> modelQuery(DataAccessManager dam, String riskType, String invType)
      throws DAOException, JBranchException{
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" WITH GEN AS ( ");
    sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_1'), ");
    sb.append(" SML AS ( ");
    sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_2'), ");
    sb.append(" SYSPAR AS ( ");
    sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FPS.FUND_TYPE'), ");
    sb.append(" AREA AS ( ");
    sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.MKT_TIER3'), ");
    sb.append(" TREND AS ( ");
    sb.append(" SELECT TRD.TYPE, TRD.TREND ");
    sb.append(" FROM TBFPS_MARKET_TREND TR ");
    sb.append(" LEFT JOIN TBFPS_MARKET_TREND_DETAIL TRD ON TR.PARAM_NO = TRD.PARAM_NO ");
    sb.append(" WHERE TR.STATUS = 'A'), ");
    sb.append(" BASE_INS AS ( ");
    sb.append(" SELECT DISTINCT BASE.PRD_ID, BASE.CURR_CD, INSINFO.BASE_AMT_OF_PURCHASE, ");
    sb.append(" LISTAGG(BASE.KEY_NO, ',') WITHIN GROUP (ORDER BY BASE.KEY_NO) AS KEY_NO ");
    sb.append(" FROM TBPRD_INS BASE ");
    sb.append(" LEFT JOIN TBPRD_INSINFO INSINFO ON BASE.PRD_ID = INSINFO.PRD_ID ");
    sb.append(" WHERE BASE.INS_TYPE IN ('1', '2') GROUP BY BASE.PRD_ID, BASE.CURR_CD, INSINFO.BASE_AMT_OF_PURCHASE), ");
    sb.append(" INS_TG AS ( ");
    sb.append(" SELECT DE.SEQNO, ");
    sb.append("   LISTAGG(INS.TARGET_ID, '/') WITHIN GROUP (ORDER BY INS.TARGET_ID) AS TARGETS ");
    sb.append("   FROM TBFPS_MODEL_ALLOCATION_INS INS ");
    sb.append("   LEFT JOIN TBFPS_MODEL_ALLOCATION DE ON INS.SEQNO = DE.SEQNO ");
    sb.append("   GROUP BY DE.SEQNO ");
    sb.append(" ) ");
    sb.append(" SELECT ");
    sb.append(" DE.INV_PRD_TYPE, ");
    sb.append(" DE.INV_PRD_TYPE_2, ");
    sb.append(" DE.PRD_TYPE AS PTYPE, ");
    sb.append(" VWP.PRD_ID AS PRD_ID, ");
    sb.append(" CASE WHEN TRUNC(SYSDATE) BETWEEN FUIN.MAIN_PRD_SDATE AND FUIN.MAIN_PRD_EDATE THEN 'Y' ");
    sb.append("      WHEN TRUNC(SYSDATE) BETWEEN FUIN.RAISE_FUND_SDATE AND FUIN.RAISE_FUND_EDATE THEN 'Y' ");
    sb.append(" ELSE 'N' END AS MAIN_PRD, ");
    sb.append(" VWP.PNAME AS PRD_CNAME, ");
    sb.append(" VWP.RISKCATE_ID AS RISK_TYPE, ");
    sb.append(" DE.CURRENCY_STD_ID AS CURRENCY_TYPE, ");
    sb.append(" DE.INV_PERCENT, ");
    sb.append(" TREND.TREND AS CIS_3M, ");
    sb.append(" CASE DE.PRD_TYPE ");
    sb.append(" WHEN 'MFD' THEN GEN.PARAM_NAME ");
    sb.append(" WHEN 'INS' THEN TO_CHAR(BASE_INS.BASE_AMT_OF_PURCHASE) ");
    sb.append(" ELSE NULL END AS GEN_SUBS_MINI_AMT_FOR, ");
    sb.append(" SML.PARAM_NAME AS SML_SUBS_MINI_AMT_FOR, ");
    sb.append(" FU.FUND_TYPE, ");
    sb.append(" SYSPAR.PARAM_NAME AS FUND_TYPE_NAME, ");
    sb.append(" FU.INV_TARGET AS MF_MKT_CAT, ");
    sb.append(" AREA.PARAM_NAME AS NAME, ");
    sb.append(" CASE WHEN DE.PRD_TYPE = 'INS' THEN BASE_INS.KEY_NO ");
    sb.append("  ELSE NULL END AS KEY_NO, ");
    sb.append(" INS_TG.TARGETS ");
    sb.append(" FROM TBFPS_MODEL_ALLOCATION_HEAD HE ");
    sb.append(" INNER JOIN TBFPS_MODEL_ALLOCATION DE ON DE.PARAM_NO = HE.PARAM_NO ");
    sb.append(" LEFT JOIN VWPRD_MASTER VWP ON VWP.PTYPE = DE.PRD_TYPE AND VWP.PRD_ID = DE.PRD_ID AND VWP.CURRENCY_STD_ID = DE.CURRENCY_STD_ID ");
    sb.append(" LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = DE.PRD_ID ");
    sb.append(" LEFT JOIN TBPRD_FUNDINFO FUIN ON FUIN.PRD_ID = FU.PRD_ID ");
    sb.append(" LEFT JOIN GEN ON GEN.PARAM_CODE = FU.CURRENCY_STD_ID ");
    sb.append(" LEFT JOIN SML ON SML.PARAM_CODE = FU.CURRENCY_STD_ID ");
    sb.append(" LEFT JOIN SYSPAR ON SYSPAR.PARAM_CODE = FU.FUND_TYPE ");
    sb.append(" LEFT JOIN TREND ON TREND.TYPE = FU.INV_TARGET ");
    sb.append(" LEFT JOIN AREA ON AREA.PARAM_CODE = TREND.TYPE ");
    sb.append(" LEFT JOIN BASE_INS ON BASE_INS.PRD_ID = DE.PRD_ID AND BASE_INS.CURR_CD = DE.CURRENCY_STD_ID ");
    sb.append(" LEFT JOIN INS_TG ON INS_TG.SEQNO = DE.SEQNO ");
    sb.append(" WHERE HE.STATUS = 'A' ");
    sb.append(" AND HE.SETTING_TYPE = '1' ");
    sb.append(" AND HE.EFFECT_START_DATE <= SYSDATE + 1");
    sb.append(" AND DE.CUST_RISK_ATR = :riskType ");
    sb.append(" AND SUBSTR(VWP.RISKCATE_ID,2) <= :riskLevel ");
    sb.append(" AND DE.INV_PRD_TYPE = '3' ");
    if (StringUtils.isNotBlank(invType)) {
      sb.append(" AND HE.INV_AMT_TYPE = :invType ");
      qc.setObject("invType", invType);
    }
    qc.setObject("riskType", riskType);
    qc.setObject("riskLevel", riskType.substring(1, 2));
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  /**
   * 儲存
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  public void save(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
    FPS324InputVO inputVO = (FPS324InputVO) body;

    DataAccessManager dam = this.getDataAccessManager();
    WorkStation ws = DataManager.getWorkStation(uuid);
    String userID = ws.getUser().getUserID();
    String planID = inputVO.getPlanID();
    Boolean isCreate = false;
    if (StringUtils.isBlank(planID)) {
      // isCreate
      planID = create(inputVO, userID);
      inputVO.setPlanID(planID);
      isCreate = true;
    } else {
      update(inputVO, userID);
    }
    if (StringUtils.equalsIgnoreCase("Y", inputVO.getSppTypeDelete())) {
      delHelp(inputVO.getPlanID());
    } else {
      switch (inputVO.getSppType()) {
      case "EDUCATION":
        saveFPS321(inputVO);
        break;
      case "RETIRE":
        saveFPS322(inputVO);
        break;
      default:
        break;
      }
    }

    // products
    if (inputVO.getPrdList() != null && StringUtils.isNotBlank(planID)) {
      // get exist seqNos
      Set<BigDecimal> existSet = new HashSet<BigDecimal>();
      // get filtered targetLs
      for (FPS324PrdInputVO item : inputVO.getPrdList()) {
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

      for (FPS324PrdInputVO prd : inputVO.getPrdList()) {
        if (prd.getSEQNO() == null) {
          create(dam, prd, planID);
        } else {
          update(dam, prd, planID);
        }
      }
    }

    this.sendRtnObject(planID);
  }

  // 刪除計畫資料 by planID & !='Y'
  public boolean deletePlanDetails(DataAccessManager dam, String planID) throws DAOException, JBranchException{
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    try {
      sb.append("DELETE FROM TBFPS_PORTFOLIO_PLAN_SPP ");
      sb.append("WHERE PLAN_ID = :planID ");
      sb.append("AND (ORDER_STATUS != 'Y' OR ORDER_STATUS IS NULL) ");
      qc.setObject("planID", planID);

      qc.setQueryString(sb.toString());
      dam.exeUpdate(qc);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public void delHelp(String planId) throws JBranchException{
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sbE = new StringBuffer("DELETE TBFPS_SPP_PLAN_HELP_E WHERE PLAN_ID = :planId ");
    StringBuffer sbR = new StringBuffer("DELETE TBFPS_SPP_PLAN_HELP_R WHERE PLAN_ID = :planId ");
    queryCondition.setObject("planId", planId);
    queryCondition.setQueryString(sbE.toString());
    dam.exeUpdate(queryCondition);
    queryCondition.setQueryString(sbR.toString());
    dam.exeUpdate(queryCondition);
  }

  // FPS324 Plan
  public String create(FPS324InputVO inputVO, String userID) throws JBranchException{
    DataAccessManager dam = this.getDataAccessManager();
    Calendar cal = Calendar.getInstance();
    String year = String.valueOf(cal.get(Calendar.YEAR));
    String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
    String date = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
    String seq = "SPP" + year + month + date + String.format("%05d", Long.parseLong(getSEQ(dam, "Plan")));

    TBFPS_PORTFOLIO_PLAN_SPP_HEADVO vo = putPlanSppHeadVO(new TBFPS_PORTFOLIO_PLAN_SPP_HEADVO(), inputVO, userID);
    vo.setPLAN_ID(seq);// 投組編號
    vo.setCUST_ID(inputVO.getCustId());// 客戶ID
    vo.setSPP_TYPE(inputVO.getSppType());// 特定規劃項目
    vo.setPLAN_STATUS("PLAN_STEP");// 規劃狀態
    vo.setVALID_FLAG(null);// 成立失效註記
    vo.setTRACE_FLAG(null);// 追蹤註記
    vo.setSTEP_STAUS(null);// 步驟

    dam.create(vo);

    return seq;
  }

  // 更新 Plan
  public void update(FPS324InputVO inputVO, String userID) throws JBranchException, ParseException{
    DataAccessManager dam = this.getDataAccessManager();
    TBFPS_PORTFOLIO_PLAN_SPP_HEADVO vo = putPlanSppHeadVO((TBFPS_PORTFOLIO_PLAN_SPP_HEADVO) dam.findByPKey(
        TBFPS_PORTFOLIO_PLAN_SPP_HEADVO.TABLE_UID, inputVO.getPlanID()), inputVO, userID);

    dam.update(vo);
  }

  // 刪除 Plan
  public void delete(FPS324InputVO inputVO, String userID) throws JBranchException, ParseException{
    DataAccessManager dam = this.getDataAccessManager();
    TBFPS_PORTFOLIO_PLAN_SPP_HEADVO vo = (TBFPS_PORTFOLIO_PLAN_SPP_HEADVO) dam.findByPKey(
        TBFPS_PORTFOLIO_PLAN_SPP_HEADVO.TABLE_UID, inputVO.getPlanID());

    dam.delete(vo);
  }

  private TBFPS_PORTFOLIO_PLAN_SPP_HEADVO putPlanSppHeadVO(TBFPS_PORTFOLIO_PLAN_SPP_HEADVO vo, FPS324InputVO inputVO,
      String userID){
    vo.setCUST_ID(inputVO.getCustId());
    vo.setSPP_TYPE(inputVO.getSppType());
    vo.setRISK_ATTR(inputVO.getRISK_ATTR());									// 客戶風險屬性等級
    vo.setVOL_RISK_ATTR(inputVO.getVOL_RISK_ATTR());							// 投組波動風險承受度
    vo.setINV_PLAN_NAME(inputVO.getPlanName());									// 投資計劃名稱
    vo.setINV_PERIOD(new BigDecimal(inputVO.getINV_PERIOD()));					// 投資期間(年)
    vo.setINV_AMT_ONETIME(new BigDecimal(inputVO.getINV_AMT_ONETIME()));		// 投資金額(單筆/元)
    vo.setINV_AMT_PER_MONTH(new BigDecimal(inputVO.getINV_AMT_PER_MONTH()));	// 投資金額(每月定額/元)
    vo.setINV_AMT_TARGET(new BigDecimal(inputVO.getINV_AMT_TARGET()));			// 目標所需資金(元)(台幣)
//    vo.setPLAN_STATUS("PLAN_STEP");// 規劃狀態(app會將已成立變成規劃中．．．)
    vo.setSTEP_STAUS(inputVO.getSTEP_STAUS());									// 步驟
    vo.setModifier(userID);
    vo.setLastupdate(new Timestamp(System.currentTimeMillis()));
    return vo;
  }

  // 新增 Product
  public String create(DataAccessManager dam, FPS324PrdInputVO inputVO, String planID) throws JBranchException{
    String seq = getSEQ(dam, "Product");
    TBFPS_PORTFOLIO_PLAN_SPPPK pk = new TBFPS_PORTFOLIO_PLAN_SPPPK();
    pk.setSEQNO(new BigDecimal(seq));
    pk.setPLAN_ID(planID);
    TBFPS_PORTFOLIO_PLAN_SPPVO vo = putPlanSppVO(new TBFPS_PORTFOLIO_PLAN_SPPVO(), inputVO);
    vo.setcomp_id(pk);
    vo.setSTATUS("S");
    vo.setPURCHASE_ORG_AMT_ORDER(new BigDecimal(0));
    vo.setPURCHASE_TWD_AMT_ORDER(new BigDecimal(0));
    dam.create(vo);

    return seq;
  }

  // 更新 Product
  public void update(DataAccessManager dam, FPS324PrdInputVO inputVO, String planID) throws JBranchException,
      APException{
    TBFPS_PORTFOLIO_PLAN_SPPPK pk = new TBFPS_PORTFOLIO_PLAN_SPPPK();
    pk.setSEQNO(inputVO.getSEQNO());
    pk.setPLAN_ID(planID);
    TBFPS_PORTFOLIO_PLAN_SPPVO vo = (TBFPS_PORTFOLIO_PLAN_SPPVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_SPPVO.TABLE_UID,
        pk);
    vo.setSTATUS(inputVO.getSTATUS());
    if (vo != null) {
      dam.update(putPlanSppVO(vo, inputVO));
    } else {
      throw new APException("ehl_01_common_007"); // 更新失敗
    }
  }

  // 刪除 Product
  public void delete(DataAccessManager dam, String planID, BigDecimal seqNo) throws JBranchException, APException{
    TBFPS_PORTFOLIO_PLAN_SPPPK pk = new TBFPS_PORTFOLIO_PLAN_SPPPK();
    pk.setSEQNO(seqNo);
    pk.setPLAN_ID(planID);
    TBFPS_PORTFOLIO_PLAN_SPPVO vo = (TBFPS_PORTFOLIO_PLAN_SPPVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_SPPVO.TABLE_UID,
        pk);
    if (vo != null) {
      dam.delete(vo);
    } else {
      throw new APException("ehl_01_common_005"); // 刪除失敗
    }
  }

  private TBFPS_PORTFOLIO_PLAN_SPPVO putPlanSppVO(TBFPS_PORTFOLIO_PLAN_SPPVO vo, FPS324PrdInputVO inputVO){
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
    vo.setORDER_STATUS(inputVO.getORDER_STATUS());
    // 1 新增商品 2 理財標的建議 3加入庫存
    vo.setPRD_SOURCE_FLAG(inputVO.getPRD_SOURCE_FLAG());
    vo.setTARGETS(inputVO.getTARGETS());

    return vo;
  }

  /**
   * FPS324 取得匯率
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  public void getRate(Object body, IPrimitiveMap header) throws JBranchException{
    FPS324OutputVO outputVO = new FPS324OutputVO();

    outputVO.setRate(getRateFn());
    sendRtnObject(outputVO);
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getRateFn() throws DAOException, JBranchException{
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(dam.QUERY_LANGUAGE_TYPE_VAR_SQL);

    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT CUR_COD, BUY_RATE FROM TBPMS_IQ053 ");
    sb.append(" WHERE MTN_DATE = (SELECT MAX (MTN_DATE) FROM TBPMS_IQ053) ");
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  /**
   * FPS324_STOCK 取得帳號
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  @SuppressWarnings("unchecked")
  public void getAccount(Object body, IPrimitiveMap header) throws JBranchException{
    FPS324InputVO inputVO = (FPS324InputVO) body;
    FPS324OutputVO outputVO = new FPS324OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    qc.setQueryString(" SELECT DISTINCT acc_no FROM TBSFA_DAT_AST_FUND WHERE CUST_ID = :custId ").setObject("custId",
        inputVO.getCustId());

    outputVO.setOutputList(dam.exeQuery(qc));
    sendRtnObject(outputVO);
  }

  /**
   * FPS324_STOCK 取得查詢結果
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  @SuppressWarnings("unchecked")
  public void inquireStock(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
    FPS324OutputVO outputVO = new FPS324OutputVO();
    FPS324InputVO inputVO = (FPS324InputVO) body;
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    StringBuffer sb = new StringBuffer();
    sb.append(
        " SELECT MAIN.PTYPE,FUND.ACC_NO,Substr(FUND.PRD_ID, 1, 4) SUB_PRD_ID,FUND.PRD_ID as Stock_PRD_ID,FUND.PRD_ID,FUND.PRD_NAME,FUND.BUY_DATE, ");
    sb.append(" FUND.RISK_LEVEL,FUND.CUR_ID,FUND.BUY_CUR_ID,FUND.UNIT_BUY,FUND.NAV_DATE,FUND.NAV,FUND.AVG_FUND_NAV, ");
    sb.append(
        " FUND.AMT_NOW_FOR,FUND.AMT_NOW_TWD,FUND.AMT_BUY,CAT.MF_MKT_CAT,CAT.name,SUG.CIS_3M,B.GEN_SUBS_MINI_AMT_FOR ");
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

    qc.setObject("LoginID", inputVO.getCustId());

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
  public void avgAge(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
    FPS324OutputVO outputVO = new FPS324OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    // 查詢
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT PARAM_CODE, PARAM_NAME, PARAM_DESC ");
    sb.append(" FROM tbsysparameter ");
    sb.append(" WHERE PARAM_TYPE = 'FPS.INS_AVERAGE' ");

    // 查詢結果
    queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    queryCondition.setQueryString(sb.toString());
    List<Map<String, Object>> rtnList = dam.exeQuery(queryCondition);
    outputVO.setOutputList(rtnList);
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
  public void checkName(Object body, IPrimitiveMap header) throws JBranchException{
    FPS324InputVO inputVO = (FPS324InputVO) body;
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT COUNT(*) AS CHECKER ");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD ");
    sb.append(" WHERE INV_PLAN_NAME = :planName ");
    qc.setObject("planName", inputVO.getPlanName());

    sb.append(" AND CUST_ID = :custId");
    qc.setObject("custId", inputVO.getCustId());

    if (StringUtils.isNotBlank(inputVO.getPlanID())) {
      sb.append(" AND PLAN_ID <> :planid");
      qc.setObject("planid", inputVO.getPlanID());
    }

    // 查詢結果
    qc.setQueryString(sb.toString());
    int checker = Integer.parseInt(((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("CHECKER").toString());

    this.sendRtnObject(checker);
  }

  /**
   * FPS321 子女教育 學費 生活費
   * 
   * @param body
   * @param header
   * @throws JBranchException
   */
  public void getSchoolPara(Object body, IPrimitiveMap<Object> header) throws JBranchException{
    FPS324OutputVO outputVO = new FPS324OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT DE.*  ");
    sb.append(" FROM TBFPS_OTHER_PARA_HEAD HE ");
    sb.append(" INNER JOIN TBFPS_OTHER_PARA DE ON HE.PARAM_NO = DE.PARAM_NO ");
    sb.append(" WHERE HE.STATUS = 'A' ");
    qc.setQueryString(sb.toString());
    outputVO.setOutputList(dam.exeQuery(qc));
    this.sendRtnObject(outputVO);
  }

  public void queryFPS321(Object body, IPrimitiveMap header) throws JBranchException{
    FPS324InputVO inputVO = (FPS324InputVO) body;
    FPS324OutputVO outputVO = new FPS324OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    if (StringUtils.isNotBlank(inputVO.getPlanID())) {
      StringBuffer sb = new StringBuffer();
      sb.append(" SELECT * ");
      sb.append(" FROM TBFPS_SPP_PLAN_HELP_E ");
      sb.append(" WHERE PLAN_ID = :planID ");
      qc.setObject("planID", inputVO.getPlanID());
      qc.setQueryString(sb.toString());
      List<Map<String, Object>> list = dam.exeQuery(qc);
      outputVO.setOutputList(list);
    }
    this.sendRtnObject(outputVO);
  }

  public void saveFPS322(FPS324InputVO inputVO) throws JBranchException{
    FPS324OutputVO outputVO = new FPS324OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT * ");
    sb.append(" FROM TBFPS_SPP_PLAN_HELP_R ");
    sb.append(" WHERE PLAN_ID = :planID ");
    qc.setObject("planID", inputVO.getPlanID());
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> list = dam.exeQuery(qc);
    if (list.size() == 0) {
      TBFPS_SPP_PLAN_HELP_RVO vo = putPlanHelpRVO(new TBFPS_SPP_PLAN_HELP_RVO(), inputVO);
      dam.create(vo);
    } else {
      TBFPS_SPP_PLAN_HELP_RVO vo = putPlanHelpRVO((TBFPS_SPP_PLAN_HELP_RVO) dam.findByPKey(
          TBFPS_SPP_PLAN_HELP_RVO.TABLE_UID, inputVO.getPlanID()), inputVO);
      dam.update(vo);
    }
  }

  public void saveFPS321(FPS324InputVO inputVO) throws JBranchException{
    FPS324OutputVO outputVO = new FPS324OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT * ");
    sb.append(" FROM TBFPS_SPP_PLAN_HELP_E ");
    sb.append(" WHERE PLAN_ID = :planID ");
    qc.setObject("planID", inputVO.getPlanID());
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> list = dam.exeQuery(qc);
    if (list.size() == 0) {
      // 新增子女教育資料
      TBFPS_SPP_PLAN_HELP_EVO vo = putPlanHelpEVO(new TBFPS_SPP_PLAN_HELP_EVO(), inputVO);
      dam.create(vo);
    } else {
      TBFPS_SPP_PLAN_HELP_EVO vo = putPlanHelpEVO((TBFPS_SPP_PLAN_HELP_EVO) dam.findByPKey(
          TBFPS_SPP_PLAN_HELP_EVO.TABLE_UID, inputVO.getPlanID()), inputVO);
      dam.update(vo);
    }
  }

  private TBFPS_SPP_PLAN_HELP_RVO putPlanHelpRVO(TBFPS_SPP_PLAN_HELP_RVO vo, FPS324InputVO inputVO){
    vo.setPLAN_ID(inputVO.getPlanID());
    vo.setRETIREMENT_AGE(inputVO.getRETIREMENT_AGE());
    vo.setRETIRE_FEE(inputVO.getRETIRE_FEE());
    vo.setPREPARE_FEE(inputVO.getPREPARE_FEE());
    vo.setSOCIAL_INS_FEE_1(inputVO.getSOCIAL_INS_FEE_1());
    vo.setSOCIAL_INS_FEE_2(inputVO.getSOCIAL_INS_FEE_2());
    vo.setSOCIAL_WELFARE_FEE_1(inputVO.getSOCIAL_WELFARE_FEE_1());
    vo.setSOCIAL_WELFARE_FEE_2(inputVO.getSOCIAL_WELFARE_FEE_2());
    vo.setCOMM_INS_FEE_1(inputVO.getCOMM_INS_FEE_1());
    vo.setCOMM_INS_FEE_2(inputVO.getCOMM_INS_FEE_2());
    vo.setOTHER_FEE_1(inputVO.getOTHER_FEE_1());
    vo.setOTHER_FEE_2(inputVO.getOTHER_FEE_2());
    vo.setHERITAGE(inputVO.getHERITAGE());
    return vo;
  }

  private TBFPS_SPP_PLAN_HELP_EVO putPlanHelpEVO(TBFPS_SPP_PLAN_HELP_EVO vo, FPS324InputVO inputVO){
    vo.setPLAN_ID(inputVO.getPlanID());
    vo.setUNIVERSITY(inputVO.getUNIVERSITY());
    vo.setUNIVERSITY_FEE_EDU(inputVO.getUNIVERSITY_FEE_EDU());
    vo.setUNIVERSITY_FEE_LIFE(inputVO.getUNIVERSITY_FEE_LIFE());
    vo.setUNIVERSITY_YEAR(inputVO.getUNIVERSITY_YEAR());
    vo.setMASTER(inputVO.getMASTER());
    vo.setMASTER_FEE_EDU(inputVO.getMASTER_FEE_EDU());
    vo.setMASTER_FEE_LIFE(inputVO.getMASTER_FEE_LIFE());
    vo.setMASTER_YEAR(inputVO.getMASTER_YEAR());
    vo.setPHD(inputVO.getPHD());
    vo.setPHD_FEE_EDU(inputVO.getPHD_FEE_EDU());
    vo.setPHD_FEE_LIFE(inputVO.getPHD_FEE_LIFE());
    vo.setPHD_YEAR(inputVO.getPHD_YEAR());
    return vo;
  }

  // 執行查詢分頁回傳
  @SuppressWarnings("unchecked")
  private void rtnPaginQuery(DataAccessManager dam, QueryConditionIF qc, StringBuffer sb, FPS324OutputVO outputVO,
      FPS324InputVO inputVO) throws DAOException, JBranchException{
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
  public String getSEQ(DataAccessManager dam, String type) throws JBranchException{
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

}
