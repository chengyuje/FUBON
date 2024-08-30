package com.systex.jbranch.app.server.fps.fps220;

import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_INV_HEADVO;
import com.systex.jbranch.app.server.fps.fps200.FPS200;
import com.systex.jbranch.app.server.fps.fpsjlb.FPSJLB;
import com.systex.jbranch.app.server.fps.fpsjlb.dao.FpsjlbDao;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.fubon.commons.fitness.ProdFitness;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("fps220")
@Scope("request")
public class FPS220 extends FPS200 {
  Logger logger = LoggerFactory.getLogger(this.getClass());
  final String PLAN_STEP = "2";
  @Autowired
  @Qualifier("fpsjlb")
  private FPSJLB fpsjlb;
  @Autowired
  @Qualifier("fpsjlbDao")
  private FpsjlbDao fpsDao;

  public void inquire(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
    FPS220InputVO inputVO = (FPS220InputVO) body;
    FPS220OutputVO outputVO = new FPS220OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    String riskType = inputVO.getRiskType();
    String planID = inputVO.getPlanID();
    String custID = inputVO.getCustID();

    if (StringUtils.isNotBlank(planID)) {
      outputVO.setOutputList(getHis(dam, planID));
      outputVO.setHasInvest(hasInvest(dam, planID));
    } else {
      throw new APException("no planID");
    }

    // set init key map for checkValidYear
    Map<String, String> keyMap = new HashMap<String, String>();
    keyMap.put("prodType", "PTYPE");
    keyMap.put("prodID", "PRD_ID");
    keyMap.put("targets", "TARGETS");

    if (outputVO.isHasInvest() && outputVO.getOutputList().size() > 0) {
      List<Map<String, Object>> stockList = calStockVolaility(dam, custID);
      List<Map<String, Object>> formatedStockLs = formatWeightMap(stockList);
      if (FPSUtils.checkValidYear(dam, stockList, 1, keyMap).length == 0) {
        // 庫存波動率
        BigDecimal stockVolatility = FPSUtils.getStandardDeviation(dam, formatedStockLs, 36, 12, false);
        outputVO.setStockVolatility(stockVolatility == null ? null : stockVolatility.doubleValue());
      }
      outputVO.setCntFixed(hasSISN(dam, custID));
    }

    if (StringUtils.isNotBlank(inputVO.getRiskType())) {
      outputVO.setRecoVolatility(getRecoVolaility(dam, riskType));
      outputVO.setMarket(getMarketOverview(dam, riskType));
      outputVO.setInitModelPortfolioList(getModel(dam, riskType));
    } else {
      throw new APException("no riskType"); //
    }

    this.sendRtnObject(outputVO);
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getHis(DataAccessManager dam, String planID) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT ");
    sb.append(" NVL(PORTFOLIO1_RATIO, 0) PORTFOLIO1_RATIO, ");
    sb.append(" NVL(PORTFOLIO2_RATIO, 0) PORTFOLIO2_RATIO, ");
    sb.append(" NVL(PORTFOLIO3_RATIO, 0) PORTFOLIO3_RATIO, ");
    sb.append(" NVL(PORTFOLIO1_AMT, 0) PORTFOLIO1_AMT, ");
    sb.append(" NVL(PORTFOLIO2_AMT, 0) PORTFOLIO2_AMT, ");
    sb.append(" NVL(PORTFOLIO3_AMT, 0) PORTFOLIO3_AMT, ");
    sb.append(" NVL(DEPOSIT_AMT, 0) DEPOSIT_AMT, ");
    sb.append(" NVL(SOW_AMT, 0) SOW_AMT, ");
    sb.append(" NVL(CASH_YEAR_AMT, 0) CASH_YEAR_AMT, ");
    sb.append(" NVL(INS_SAV_AMT, 0) INS_SAV_AMT, ");
    sb.append(" NVL(INS_POLICY_AMT, 0) INS_POLICY_AMT, ");
    sb.append(" NVL(ANNUITY_INS_AMT, 0) ANNUITY_INS_AMT, ");
    sb.append(" NVL(FIXED_INCOME_AMT, 0) FIXED_INCOME_AMT, ");
    sb.append(" NVL(FUND_AMT, 0) FUND_AMT, ");
    sb.append(" NVL(PLAN_AMT, 0) PLAN_AMT ");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_INV_HEAD ");
    sb.append(" WHERE PLAN_ID = :planID ");
    qc.setObject("planID", planID);

    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  // model portfolio
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getModel(DataAccessManager dam, String riskType)
      throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" WITH BASE AS ( ");
    sb.append(" SELECT HE.PARAM_NO ");
    sb.append(" FROM TBFPS_MODEL_ALLOCATION_HEAD HE ");
    sb.append(" WHERE HE.STATUS = 'A' ");
    sb.append(" AND HE.SETTING_TYPE = '2' ");
    sb.append(" AND HE.EFFECT_START_DATE <= TRUNC(SYSDATE) + 1 ");
    sb.append(" ) ");
    sb.append(" SELECT DE.* ");
    sb.append(" FROM TBFPS_MODEL_ALLOCATION DE ");
    sb.append(" INNER JOIN BASE on BASE.PARAM_NO = DE.PARAM_NO ");
    sb.append(" WHERE DE.CUST_RISK_ATR = :riskType ");
    qc.setObject("riskType", riskType);

    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  @SuppressWarnings("unchecked")
  private String getMarketOverview(DataAccessManager dam, String riskType) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT MARKET_OVERVIEW ");
    sb.append(" FROM TBFPS_MARKET_TREND ");
    sb.append(" WHERE STATUS='A' ");
    sb.append(" ORDER BY EFFECT_START_DATE DESC ");
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> result = dam.exeQuery(qc);
    return result.size() > 0 ? result.get(0).get("MARKET_OVERVIEW").toString() : "";
  }

  // 參數波動度
  @SuppressWarnings("unchecked")
  public String getRecoVolaility(DataAccessManager dam, String riskType) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT DE.VOLATILITY ");
    sb.append(" FROM TBFPS_CUSTRISK_VOLATILITY_HEAD HE ");
    sb.append(" INNER JOIN TBFPS_CUSTRISK_VOLATILITY DE on HE.PARAM_NO = DE.PARAM_NO ");
    sb.append(" where HE.STATUS = 'A' ");
    sb.append(" and he.effect_start_date <= sysdate ");
    sb.append(" and DE.CUST_RISK_ATR = :custRisk ");
    qc.setObject("custRisk", riskType);

    qc.setQueryString(sb.toString());
    List<Map<String, Object>> result = dam.exeQuery(qc);
    return result.size() > 0 ? result.get(0).get("VOLATILITY").toString() : "";
  }

  @SuppressWarnings("unchecked")
  public int hasSISN(DataAccessManager dam, String custID) throws DAOException, JBranchException {
    // count SISN items
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT COUNT(*) CHECKER");
    sb.append(" FROM MVFPS_AST_ALLPRD_DETAIL MAIN");
    sb.append(" WHERE CUST_ID = :custID");
    sb.append(" AND AST_TYPE IN ('10','15')");

    qc.setObject("custID", custID);
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> result = dam.exeQuery(qc);
    return result.size() > 0 ? Integer.parseInt(result.get(0).get("CHECKER").toString()) : 0;
  }

  // 客戶庫存波動度
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> calStockVolaility(DataAccessManager dam, String custID)
      throws DAOException, JBranchException {
    // get stock items
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" WITH");
    sb.append(" INS_TARGET AS (");
    sb.append("   SELECT (M.POLICY_NBR ||'-'|| TRIM(TO_CHAR(M.POLICY_SEQ  ,'00')) || CASE WHEN M.ID_DUP <> ' ' THEN '-' || M.ID_DUP END ) AS CERT_NBR, ");
    sb.append("   M.PRD_ID, LISTAGG(D.INV_TARGET_NO, '/') WITHIN GROUP (ORDER BY D.POLICY_NO) AS TARGETS ");
    sb.append("   FROM TBCRM_AST_INS_MAST M");
    sb.append("   INNER JOIN TBCRM_AST_INS_TARGET D ON M.POLICY_NBR = D.POLICY_NO AND M.POLICY_SEQ = D.POLICY_SEQ");
    sb.append("   WHERE M.CUST_ID = :custID");
    sb.append("   GROUP BY M.PRD_ID, (M.POLICY_NBR ||'-'|| TRIM(TO_CHAR(M.POLICY_SEQ ,'00')) || CASE WHEN M.ID_DUP <> ' ' THEN '-' || M.ID_DUP END )");
    sb.append(" )");
    sb.append(" SELECT ");
    sb.append(" M.PROD_ID AS PRD_ID, ROUND(SUM(M.NOW_AMT_TWD),0) AS PURCHASE_TWD_AMT,");
    sb.append(" PRD.PTYPE AS PTYPE, INS_TARGET.TARGETS");
    sb.append(" FROM MVFPS_AST_ALLPRD_DETAIL M");
    sb.append(" LEFT JOIN VWPRD_MASTER PRD ON PRD.PRD_ID = M.PROD_ID AND PRD.PTYPE != 'STOCK'");
    sb.append(" LEFT JOIN INS_TARGET ON INS_TARGET.CERT_NBR = M.CERT_NBR");
    sb.append(" LEFT JOIN TBFPS_PLANID_MAPPING MAPPING ON MAPPING.CERTIFICATE_ID = M.CERT_NBR");
    sb.append(" WHERE CUST_ID = :custID");
    sb.append(" AND (AST_TYPE IN ('07','08','09','12')");
    sb.append(" OR (AST_TYPE = '14' AND M.INS_TYPE = '2'))");
    sb.append(" AND M.NOW_AMT_TWD > 0");
    sb.append(" AND MAPPING.CERTIFICATE_ID IS NULL");
    sb.append(" GROUP BY M.PROD_ID, PRD.PTYPE, INS_TARGET.TARGETS");
    qc.setObject("custID", custID);

    qc.setQueryString(sb.toString());

    return dam.exeQuery(qc);
  }

  /**
   * 存款部位上限
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws DAOException
   */
  public void getDepositParam(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
    DataAccessManager dam = this.getDataAccessManager();
    int depositAUM = 0;

    List<Map<String, Object>> resultList = getOtherPara(dam);
    if (resultList.size() > 0) {
      depositAUM = Integer.parseInt(resultList.get(0).get("DEPOSIT_AUM").toString());
    }
    this.sendRtnObject(depositAUM);
  }

  /**
   * 檢查固定收益商品金額
   * 
   * @param body
   * @param header
   * @throws Exception
   * @throws IOException
   */
  public void checkFixed(Object body, IPrimitiveMap<?> header) throws Exception {
    FPS220InputVO inputVO = (FPS220InputVO) body;
    DataAccessManager dam = this.getDataAccessManager();
    int checkFixed = checkFixedLogic(dam, inputVO.getFixedAmt(), inputVO.getCustID(), inputVO.getRiskType());
    this.sendRtnObject(checkFixed);
  }

  public int checkFixedLogic(DataAccessManager dam, BigDecimal amt, String custID, String riskType) throws Exception {
    List<Map<String, Object>> rateList = getFxRate(dam, "USD");
    List<Map<String, Object>> prodList = new ArrayList<Map<String, Object>>();
    BigDecimal USD_Rate = rateList.size() > 0 ? new BigDecimal(rateList.get(0).get("BUY_RATE").toString())
        : new BigDecimal(0);
    if (USD_Rate.compareTo(BigDecimal.ZERO) == 0 || amt.compareTo(BigDecimal.ZERO) == 0) {
      throw new Exception("0");
    }

    Map<String, Object> fixedParam = getFixedParam(dam);

    // SI/SN 最低申購金額台幣
    BigDecimal SISNmin = ((BigDecimal) fixedParam.get("SISN_BASE_PURCHASE")).multiply(new BigDecimal(10000))
        .multiply(((BigDecimal) fixedParam.get("EXCHANGE_RATE")).add(new BigDecimal(100))).divide(new BigDecimal(100))
        .multiply(USD_Rate);
    // BND 最低申購金額台幣
    BigDecimal BNDmin = ((BigDecimal) fixedParam.get("BOND_BASE_PURCHASE")).multiply(new BigDecimal(10000))
        .multiply(((BigDecimal) fixedParam.get("EXCHANGE_RATE")).add(new BigDecimal(100))).divide(new BigDecimal(100))
        .multiply(USD_Rate);

    try {
      // 初始化適配檢核
      ProdFitness prodFitness = (ProdFitness) PlatformContext.getBean("ProdFitness");
      // 客戶資料適配檢核
      if (prodFitness.validCustBondSISN(custID).getIsError()) {
        // 客戶資料適配檢核失敗
        return 0;
      }

      if (amt.compareTo(SISNmin) > 0) {
        prodList = getSN(dam, riskType, prodFitness);
        if (prodList.size() <= 0) {
          prodList = getSI(dam, riskType, prodFitness, false, "S");
        }
        if (prodList.size() <= 0 && amt.compareTo(BNDmin) > 0) {
          prodList = getBND(dam, riskType, prodFitness, false, "S");
        }
      }
    } catch (JBranchException e) {
      e.printStackTrace();
      throw new APException("電文失敗");
    }

    return prodList.size();
  }

  // 固定收益參數
  public Map<String, Object> getFixedParam(DataAccessManager dam) throws DAOException, JBranchException {
    Map<String, Object> fixedParam = new HashMap<String, Object>();

    List<Map<String, Object>> resultList = getOtherPara(dam);
    if (resultList.size() > 0) {
      fixedParam.put("FUND_AUM", resultList.get(0).get("FUND_AUM"));
      fixedParam.put("SISN_BASE_PURCHASE", resultList.get(0).get("SISN_BASE_PURCHASE"));
      fixedParam.put("BOND_BASE_PURCHASE", resultList.get(0).get("BOND_BASE_PURCHASE"));
      fixedParam.put("EXCHANGE_RATE", resultList.get(0).get("EXCHANGE_RATE"));
    }
    return fixedParam;
  }

  /**
   * 儲存
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws IOException
   */
  public void save(Object body, IPrimitiveMap<?> header) throws JBranchException {
    this.sendRtnObject(exeSave(body)); // 新增成功
  }

  protected void update(Object body, DataAccessManager dam, String planID) throws DAOException, JBranchException {
    FPS220InputVO inputVO = (FPS220InputVO) body;

    TBFPS_PORTFOLIO_PLAN_INV_HEADVO vo = (TBFPS_PORTFOLIO_PLAN_INV_HEADVO) dam
        .findByPKey(TBFPS_PORTFOLIO_PLAN_INV_HEADVO.TABLE_UID, planID);
    if (vo != null) {
      dam.update(putPlanInvHeadVO(vo, inputVO));
    } else {
      throw new APException("ehl_01_common_009"); // 查無資料
    }

  }

  private TBFPS_PORTFOLIO_PLAN_INV_HEADVO putPlanInvHeadVO(TBFPS_PORTFOLIO_PLAN_INV_HEADVO vo, FPS220InputVO inputVO) {
    vo.setPLAN_STEP(StringUtils.isNotBlank(inputVO.getStep()) ? inputVO.getStep() : PLAN_STEP);
    vo.setPORTFOLIO1_RATIO(inputVO.getDepositPct());
    vo.setPORTFOLIO2_RATIO(inputVO.getFixedPct());
    vo.setPORTFOLIO3_RATIO(inputVO.getStockPct());

    vo.setPORTFOLIO1_AMT(inputVO.getDepositAmt());
    vo.setPORTFOLIO2_AMT(inputVO.getFixedAmt());
    vo.setPORTFOLIO3_AMT(inputVO.getStockAmt());
    return vo;
  }

}
