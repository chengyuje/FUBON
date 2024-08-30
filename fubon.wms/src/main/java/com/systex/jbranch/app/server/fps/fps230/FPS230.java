package com.systex.jbranch.app.server.fps.fps230;

import com.google.gson.Gson;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_INVPK;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_INVVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_INV_HEADVO;
import com.systex.jbranch.app.server.fps.fps200.FPS200;
import com.systex.jbranch.app.server.fps.fps210.FPS210;
import com.systex.jbranch.app.server.fps.fpsprod.FPSProd;
import com.systex.jbranch.app.server.fps.fpsprod.FPSProdInputVO;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtilsOutputVO;
import com.systex.jbranch.fubon.commons.fitness.ProdFitness;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

@Component("fps230")
@Scope("request")
public class FPS230 extends FPS200 {
  Logger logger = LoggerFactory.getLogger(this.getClass());
  final String PLAN_STEP = "3";
  final BigDecimal FIXED3W = new BigDecimal(30000);
  final BigDecimal FIXED1W = new BigDecimal(10000);
  
  // 驗證值
  final BigDecimal AMT_VALUE = new BigDecimal(300000);
  @Autowired
  @Qualifier("fps210")
  private FPS210 fps210;

  public void inquire(Object body, IPrimitiveMap<?> header) throws Exception {
    FPS230InputVO inputVO = (FPS230InputVO) body;
    FPS230OutputVO outputVO = new FPS230OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    String riskType = inputVO.getRiskType();
    String planID = inputVO.getPlanID();
    String custID = inputVO.getCustID();

    String isPro = inputVO.getIsPro();
    String isOBU = inputVO.getOBU();
    List<String> errorList = new ArrayList<String>();

    if (StringUtils.isBlank(planID) || StringUtils.isBlank(riskType)) {
      throw new APException("沒有PlanID || 沒有RiskType"); //
    }
    outputVO.setHasInvest(hasInvest(dam, planID));
    List<Map<String, Object>> his = hisQuery(dam, planID); //取得客戶庫存明細
    List<Map<String, Object>> headList = fps210.getCustPlan(dam, planID);
    Map<String, Object> headMap = headList.get(0);
    BigDecimal fixedAmt = ((BigDecimal) headMap.get("PORTFOLIO2_RATIO")).multiply((BigDecimal) headMap.get("PLAN_AMT")).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
    // inputVO.getFixedAmt();
//    System.out.println("================================="); 
//    System.out.println(custID); 
//    System.out.println(riskType); 
//    System.out.println(fixedAmt); 
//    System.out.println(isPro); 
//    System.out.println(isOBU); 
    List<Map<String, Object>> fixedModel = null;
    try {
      fixedModel = fixedModelQuery(dam, custID, riskType, fixedAmt, isPro.equals("Y"), "B");
      if (fixedModel == null) {
        errorList.add("SI/SN適配電文錯誤，無法配置推薦SI/SN商品。");
      }
    } catch (Exception e) {
      errorList.add("SI/SN適配電文錯誤，無法配置推薦SI/SN商品。");
    }

    // portfolio 要分兩邊 A.類債券[BOND, SI, MFD, ETF] B.類股票[MFD, ETF, INS]
    List<Map<String, Object>> allModelPortfolio = modelQuery(dam, riskType, null, isOBU, isPro);
    Map<String, List<Map<String, Object>>> groupByStockBondTypeMap = groupByStockBondType(allModelPortfolio);
    
    // 類債券 (額外加工計算 比例)
    List<Map<String, Object>> modelPortfolioB = mergeModelWithFixed(groupByStockBondTypeMap.get("B"), fixedModel);
    reCalPct(modelPortfolioB, fixedAmt, dam);
    
    // 類股票
    List<Map<String, Object>> modelPortfolioS = mergeModelWithFixed(groupByStockBondTypeMap.get("S"), new ArrayList<Map<String, Object>>());
    List<Map<String, Object>> modelPortfolio = new ArrayList<Map<String, Object>>();
    modelPortfolio.addAll(modelPortfolioB);
    modelPortfolio.addAll(modelPortfolioS);

    outputVO.setMarket(getMarketOverview(dam, riskType));
    outputVO.setOutputList(!outputVO.isHasInvest() && his.size() <= 0 ? modelPortfolio : his);
    System.out.println("his");
    System.out.println(new Gson().toJson(his));
//    System.out.println("outputVO");
//    System.out.println(new Gson().toJson(outputVO.getOutputList()));
    outputVO.setInitModelPortfolioList(modelPortfolio);
    outputVO.setHis(his.size() > 0);  //有無庫存
    outputVO.setFxRateList(getFxRate(dam, ""));
    outputVO.setFeatureDescription(getFeatureDescription(dam));
    outputVO.setVolatility(getLimitVolatility(dam, riskType).doubleValue());
    // 庫存
    if (StringUtils.isNotBlank(inputVO.getCustID())) {
      outputVO.setHisStocksList(getHisStock(dam, inputVO.getCustID()));
    }

    outputVO.setErrorList(errorList.toArray(new String[errorList.size()]));
    
    outputVO.setStockRiskLevel(getCustRisKPct(dam));

    this.sendRtnObject(outputVO);
  }
  
  // 後端處裡
  public List<Map<String, Object>> mergeModelWithFixed(List<Map<String, Object>> modelQuery, List<Map<String, Object>> fixedModelQuery, Map<String, Object> headMap) {
    BigDecimal depositAmt = ((BigDecimal) headMap.get("PORTFOLIO1_RATIO")).multiply((BigDecimal) headMap.get("PLAN_AMT")).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
    BigDecimal fixedAmt = ((BigDecimal) headMap.get("PORTFOLIO2_RATIO")).multiply((BigDecimal) headMap.get("PLAN_AMT")).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
    BigDecimal stockAmt = ((BigDecimal) headMap.get("PORTFOLIO3_RATIO")).multiply((BigDecimal) headMap.get("PLAN_AMT")).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);

    List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
    int fixedCnt = fixedModelQuery.size();
    for (Map<String, Object> model : modelQuery) {
      switch (model.get("INV_PRD_TYPE").toString()) {
      case "1":
        if (depositAmt.compareTo(BigDecimal.ZERO) == 1) {
          // todo: 在這裡組是否顯示?
          resultList.add(model);
        }
        break;
      case "2":
        if (fixedAmt.compareTo(BigDecimal.ZERO) == 1 && fixedCnt > 0) {
          for (Map<String, Object> fixed : fixedModelQuery) {
            fixed.put("INV_PRD_TYPE", "2");
            fixed.put("INV_PERCENT", ((BigDecimal) fixed.get("pct")).multiply(((BigDecimal) model.get("INV_PERCENT"))).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            if (("BND").equals(fixed.get("PTYPE"))) {
              fixed.put("MAIN_PRD", StringUtils.isNotBlank(fixed.get("PRD_RANK").toString()) ? "Y" : "N");
            } else {
              fixed.put("MAIN_PRD", fixed.get("BUSIDAY").toString());
            }
            resultList.add(fixed);
          }
        }
        break;
      case "3":
        if (stockAmt.compareTo(BigDecimal.ZERO) == 1) {
          // todo: 在這裡組是否顯示?
          resultList.add(model);
        }
        break;
      }
    }
    return resultList;
  }

  // 前端處裡
  public List<Map<String, Object>> mergeModelWithFixed(List<Map<String, Object>> modelQuery, List<Map<String, Object>> fixedModelQuery) {
    List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
    int fixedCnt = fixedModelQuery == null ? 0 : fixedModelQuery.size();
    for (Map<String, Object> model : modelQuery) {
      switch (model.get("STOCK_BOND_TYPE").toString()) {
      case "B":
//        if (fixedCnt > 0) {
//          for (Map<String, Object> fixed : fixedModelQuery) {
//            fixed.put("INV_PRD_TYPE", "2");
//            fixed.put("INV_PERCENT", ((BigDecimal) fixed.get("pct")).multiply(((BigDecimal) model.get("INV_PERCENT"))).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
//            if (("BND").equals(fixed.get("PTYPE"))) {
//              fixed.put("MAIN_PRD", StringUtils.isNotBlank(fixed.get("PRD_RANK").toString()) ? "Y" : "N");
//            } else {
//              fixed.put("MAIN_PRD", fixed.get("BUSIDAY").toString());
//            }
//          }
//        }
        resultList.add(model);
        break;
      case "S":
        resultList.add(model);
        break;
      }
    }
    if(CollectionUtils.isNotEmpty(fixedModelQuery)) {
      if (fixedCnt > 0) {
        for (Map<String, Object> fixed : fixedModelQuery) {
          fixed.put("INV_PRD_TYPE", "2");
          if (("BND").equals(fixed.get("PTYPE"))) {
            fixed.put("MAIN_PRD", StringUtils.isNotBlank(fixed.get("PRD_RANK").toString()) ? "Y" : "N");
          } else {
            fixed.put("MAIN_PRD", fixed.get("BUSIDAY").toString());
          }
          resultList.add(fixed);
        }
      }
    }
    
    
    return resultList;
  }
  
  public void getFeatureDescription(Object body, IPrimitiveMap<?> header) throws Exception {
	  FPS230OutputVO outputVO = new FPS230OutputVO();
	  DataAccessManager dam = this.getDataAccessManager();
	  outputVO.setFeatureDescription(getFeatureDescription(dam));
	  this.sendRtnObject(outputVO);
  }

  // 組合特色說明
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getFeatureDescription(DataAccessManager dam) throws DAOException, JBranchException {
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

  // get init model
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> modelQuery(DataAccessManager dam, String riskType, String invType, String isOBU, String isPro) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" WITH");
    sb.append(" GEN AS (");
    sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_1'),");
    sb.append(" SML AS (");
    sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_2'),");
    sb.append(" ETF AS(");
    sb.append(" SELECT PRD_ID, TXN_AMOUNT, INV_TARGET FROM TBPRD_ETF WHERE IS_SALE IS NULL),");
    sb.append(" SYSPAR AS (");
    sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FPS.FUND_TYPE'),");
    sb.append(" AREA AS (");
    sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.MKT_TIER1'),");
    sb.append(" TREND AS (");
    sb.append(" SELECT TRD.TYPE, TRD.TREND");
    sb.append(" FROM TBFPS_MARKET_TREND TR");
    sb.append(" LEFT JOIN TBFPS_MARKET_TREND_DETAIL TRD ON TR.PARAM_NO = TRD.PARAM_NO");
    sb.append(" WHERE TR.STATUS = 'A'),");
    sb.append(" SAVING AS (");
    sb.append(" SELECT '1' AS INV_PRD_TYPE, '3' AS INV_PRD_TYPE_2, TBPRD_INS.* FROM TBPRD_INS");
    sb.append(" WHERE INS_TYPE = '1' AND PRD_RANK IS NOT NULL ORDER BY PRD_RANK FETCH FIRST ROW ONLY),");
    sb.append(" BASE_INS AS (");
    sb.append(" SELECT DISTINCT BASE.PRD_ID, BASE.CURR_CD, INSINFO.BASE_AMT_OF_PURCHASE,");
    sb.append("   LISTAGG(BASE.KEY_NO, ',') WITHIN GROUP (ORDER BY BASE.KEY_NO) AS KEY_NO  ");
    sb.append("   FROM TBPRD_INS BASE");
    sb.append("   LEFT JOIN TBPRD_INSINFO INSINFO ON BASE.PRD_ID = INSINFO.PRD_ID");
    sb.append("   WHERE BASE.INS_TYPE IN ('1', '2') GROUP BY BASE.PRD_ID, BASE.CURR_CD, INSINFO.BASE_AMT_OF_PURCHASE),");
    sb.append(" INS_TG AS (");
    sb.append(" SELECT DE.SEQNO,");
    sb.append("   LISTAGG(INS.TARGET_ID, '/') WITHIN GROUP (ORDER BY INS.TARGET_ID) AS TARGETS");
    sb.append("   FROM TBFPS_MODEL_ALLOCATION_INS INS");
    sb.append("   LEFT JOIN TBFPS_MODEL_ALLOCATION DE ON INS.SEQNO = DE.SEQNO");
    sb.append("   GROUP BY DE.SEQNO");
    sb.append(" )");
    sb.append(" SELECT");
    sb.append(" DE.STOCK_BOND_TYPE,");
    sb.append(" DE.INV_PRD_TYPE,");
    sb.append(" CASE WHEN DE.PRD_TYPE = 'NANO' THEN NANO.INV_LEVEL ELSE DE.INV_PRD_TYPE_2 END INV_PRD_TYPE_2,");
    sb.append(" CASE WHEN DE.INV_PRD_TYPE='1' AND DE.INV_PRD_TYPE_2='3' THEN 'INS'");
    sb.append("      ELSE DE.PRD_TYPE END AS PTYPE,");
    sb.append(" CASE WHEN DE.INV_PRD_TYPE='1' AND DE.INV_PRD_TYPE_2='3' THEN SAVING.PRD_ID");
    sb.append("      ELSE VWP.PRD_ID END AS PRD_ID,");
    sb.append(" CASE WHEN SYSDATE BETWEEN FUIN.MAIN_PRD_SDATE AND FUIN.MAIN_PRD_EDATE THEN 'Y'");
    sb.append("      WHEN SYSDATE BETWEEN FUIN.RAISE_FUND_SDATE AND FUIN.RAISE_FUND_EDATE THEN 'Y'");
    sb.append(" ELSE 'N' END AS MAIN_PRD,");
    sb.append(" CASE WHEN DE.INV_PRD_TYPE='1' AND DE.INV_PRD_TYPE_2='3' THEN SAVING.INSPRD_NAME");
    sb.append("      ELSE VWP.PNAME END AS PRD_CNAME,");
    sb.append(" VWP.RISKCATE_ID AS RISK_TYPE,");
    sb.append(" CASE WHEN DE.INV_PRD_TYPE='1' AND DE.INV_PRD_TYPE_2='3' THEN SAVING.CURR_CD");
    sb.append("      ELSE DE.CURRENCY_STD_ID END AS CURRENCY_TYPE,");
    sb.append(" DE.INV_PERCENT,");
    sb.append(" CASE WHEN TREND.TREND IS NOT NULL THEN TREND.TREND");
    sb.append("      WHEN TREND_ETF.TREND IS NOT NULL THEN TREND_ETF.TREND");
    sb.append("      END AS CIS_3M,");
    sb.append(" CASE");
    sb.append("   WHEN PTYPE = 'MFD' THEN GEN.PARAM_NAME");
    sb.append("   WHEN PTYPE = 'ETF' THEN ETF.TXN_AMOUNT");
    sb.append("   WHEN PTYPE = 'INS' THEN TO_CHAR(BASE_INS.BASE_AMT_OF_PURCHASE)");
    sb.append("   WHEN DE.INV_PRD_TYPE = '1' AND DE.INV_PRD_TYPE_2='3' THEN ");
    sb.append("     (SELECT TO_CHAR(BASE_INS.BASE_AMT_OF_PURCHASE) FROM BASE_INS WHERE PRD_ID = SAVING.PRD_ID)");
    sb.append("   ELSE NULL END AS GEN_SUBS_MINI_AMT_FOR,");
    sb.append(" SML.PARAM_NAME AS SML_SUBS_MINI_AMT_FOR,");
    sb.append(" FU.FUND_TYPE,");
    sb.append(" SYSPAR.PARAM_NAME AS FUND_TYPE_NAME,");
    sb.append(" FU.INV_AREA AS MF_MKT_CAT,");
    sb.append(" AREA.PARAM_NAME AS NAME,");
    sb.append(" CASE WHEN DE.PRD_TYPE = 'INS' THEN BASE_INS.KEY_NO");
    sb.append("      WHEN DE.INV_PRD_TYPE='1' AND DE.INV_PRD_TYPE_2='3' THEN TO_CHAR(SAVING.KEY_NO)");
    sb.append("      ELSE NULL END AS KEY_NO,");
    sb.append(" INS_TG.TARGETS");
    sb.append(" FROM TBFPS_MODEL_ALLOCATION_HEAD HE");
    sb.append(" INNER JOIN TBFPS_MODEL_ALLOCATION DE ON DE.PARAM_NO = HE.PARAM_NO");
    sb.append(" LEFT JOIN VWPRD_MASTER VWP ON VWP.PTYPE = DE.PRD_TYPE AND VWP.PRD_ID = DE.PRD_ID");
    sb.append(" LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = DE.PRD_ID");
    sb.append(" LEFT JOIN TBPRD_FUNDINFO FUIN ON FUIN.PRD_ID = FU.PRD_ID");
    sb.append(" LEFT JOIN GEN ON GEN.PARAM_CODE = (decode(FUIN.FUS20,'C','TWD_DOMESTIC',FU.CURRENCY_STD_ID))");
    sb.append(" LEFT JOIN SML ON SML.PARAM_CODE = FU.CURRENCY_STD_ID");
    sb.append(" LEFT JOIN SYSPAR ON SYSPAR.PARAM_CODE = FU.FUND_TYPE");
    sb.append(" LEFT JOIN TREND ON TREND.TYPE = FU.INV_TARGET");
    sb.append(" LEFT JOIN AREA ON AREA.PARAM_CODE = TREND.TYPE");
    sb.append(" LEFT JOIN BASE_INS ON BASE_INS.PRD_ID = DE.PRD_ID");
    sb.append(" LEFT JOIN ETF ON ETF.PRD_ID = DE.PRD_ID");
    sb.append(" LEFT JOIN TREND TREND_ETF ON TREND_ETF.TYPE = ETF.INV_TARGET");
    sb.append(" LEFT JOIN SAVING ON DE.INV_PRD_TYPE_2 = SAVING.INV_PRD_TYPE_2 ");
    sb.append("     AND DE.INV_PRD_TYPE = SAVING.INV_PRD_TYPE");
    sb.append(" LEFT JOIN INS_TG ON INS_TG.SEQNO = DE.SEQNO");
    sb.append(" LEFT JOIN TBPRD_NANO NANO ON NANO.PRD_ID = DE.PRD_ID "); //新增奈米投
    sb.append(" WHERE HE.STATUS = 'A'");
    sb.append(" AND HE.SETTING_TYPE = '2'");
    sb.append(" AND HE.EFFECT_START_DATE <= SYSDATE + 1");
    sb.append(" AND PTYPE <> 'INS'");
    sb.append(" AND DE.CUST_RISK_ATR = :riskType");

    if (StringUtils.isNotBlank(invType)) {
      sb.append(" AND DE.INV_PRD_TYPE = :invType ");
      qc.setObject("invType", invType);
    }

    qc.setObject("riskType", riskType);
    qc.setQueryString(sb.toString());

    List<Map<String, Object>> result = validModelQuery(dam, dam.exeQuery(qc), riskType, isOBU, isPro);
    putInsMinValue(dam, result);
    FPSUtils.mergeRedemption(dam, result);
    return result;
  }

  private List<Map<String, Object>> validModelQuery(DataAccessManager dam, List<Map<String, Object>> origin, String riskType, String isOBU, String isPro) throws DAOException, JBranchException {
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    FPSProdInputVO inputVO = new FPSProdInputVO();
    inputVO.setOBU(isOBU);
    inputVO.setIsPro(isPro);
    inputVO.setRiskType(riskType);

    List<Map<String, Object>> prodList = null;
    for (Map<String, Object> item : origin) {
      inputVO.setStockBondType(ObjectUtils.toString(item.get("STOCK_BOND_TYPE")));	
      switch (ObjectUtils.toString(item.get("PTYPE"))) {
      case "MFD":
        inputVO.setFund_id(ObjectUtils.toString(item.get("PRD_ID")));
        prodList = FPSProd.getMFD(dam, inputVO);
        break;
      case "INS":
        inputVO.setInsID(ObjectUtils.toString(item.get("PRD_ID")));
        if (ObjectUtils.toString(item.get("INV_PRD_TYPE")).equals("1"))
          prodList = FPSProd.getSavingIns(dam, inputVO);
        else
          prodList = FPSProd.getInvestIns(dam, inputVO);
        break;
      case "BND":
        inputVO.setBondID(ObjectUtils.toString(item.get("PRD_ID")));
        prodList = FPSProd.getBond(dam, inputVO);
        break;
      case "SI":
        inputVO.setSIID(ObjectUtils.toString(item.get("PRD_ID")));
        prodList = FPSProd.getSI(dam, inputVO);
        break;
      case "SN":
        inputVO.setSNID(ObjectUtils.toString(item.get("PRD_ID")));
        prodList = FPSProd.getSN(dam, inputVO);
        break;
      case "ETF":
        inputVO.setEtfID(ObjectUtils.toString(item.get("PRD_ID")));
        prodList = FPSProd.getETF(dam, inputVO);
        break;
      default:
        result.add(item);
        break;
      }
      if (prodList != null && prodList.size() > 0) {
        result.add(item);
      }
      prodList = null;
    }

    return result;
  }

  // get his products
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> hisQuery(DataAccessManager dam, String planID) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" WITH ");
    sb.append(" SYSPAR AS ( ");
    sb.append("   SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FPS.FUND_TYPE' ), ");
    sb.append(" BASE_INS AS ( ");
    sb.append("   SELECT DISTINCT BASE.PRD_ID, BASE.CURR_CD, INSINFO.BASE_AMT_OF_PURCHASE,");
    sb.append("   LISTAGG(BASE.KEY_NO, ',') WITHIN GROUP (ORDER BY BASE.KEY_NO) AS KEY_NO  ");
    sb.append("   FROM TBPRD_INS BASE");
    sb.append("   LEFT JOIN TBPRD_INSINFO INSINFO ON BASE.PRD_ID = INSINFO.PRD_ID");
    sb.append("   WHERE BASE.INS_TYPE IN ('1', '2') GROUP BY BASE.PRD_ID, BASE.CURR_CD, INSINFO.BASE_AMT_OF_PURCHASE), ");
    sb.append(" GEN AS ( ");
    sb.append("   SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_1'), ");
    sb.append(" SML AS ( ");
    sb.append("   SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_2'), ");
    sb.append(" ETF AS( ");
    sb.append("   SELECT PRD_ID, TXN_AMOUNT, INV_TARGET FROM TBPRD_ETF WHERE IS_SALE IS NULL), ");
    sb.append(" TREND AS ( ");
    sb.append("   SELECT TRD.TYPE, TRD.TREND ");
    sb.append("   FROM TBFPS_MARKET_TREND TR ");
    sb.append("   LEFT JOIN TBFPS_MARKET_TREND_DETAIL TRD ON TR.PARAM_NO = TRD.PARAM_NO ");
    sb.append("   WHERE TR.STATUS = 'A') ");
    sb.append(" SELECT ");
    sb.append(" MAIN.PLAN_ID, MAIN.SEQNO, MAIN.PRD_ID, MAIN.PRD_TYPE AS PTYPE, MAIN.PRD_NAME AS PRD_CNAME, ");
    sb.append(" MAIN.RISKCATE_ID AS RISK_TYPE, MAIN.PROD_CURR AS CURRENCY_TYPE, ");
    sb.append(" MAIN.TRUST_CURR, MAIN.PURCHASE_ORG_AMT, ");
    sb.append(" MAIN.PURCHASE_TWD_AMT, MAIN.PORTFOLIO_RATIO AS INV_PERCENT, ");
    sb.append(" MAIN.LIMIT_ORG_AMT, MAIN.PORTFOLIO_TYPE, MAIN.INV_PRD_TYPE_2, ");
    sb.append(" SYSPAR.PARAM_NAME AS FUND_TYPE_NAME, MAIN.TXN_TYPE, MAIN.INV_TYPE, ");
    sb.append(" MAIN.INVENTORY_ORG_AMT AS NOW_AMT, MAIN.INVENTORY_TWD_AMT AS NOW_AMT_TWD, ");
    sb.append(" MAIN.PURCHASE_ORG_AMT_ORDER, MAIN.RDM_ORG_AMT_ORDER,");
    sb.append(" CASE WHEN TREND.TREND IS NOT NULL THEN TREND.TREND");
    sb.append("      WHEN TREND_ETF.TREND IS NOT NULL THEN TREND_ETF.TREND");
    sb.append("      END AS CIS_3M,");
    sb.append(" CASE WHEN PRD.PRD_RANK IS NOT NULL OR PTYPE = 'NANO' THEN 'Y' ");
    sb.append("  ELSE 'N' END AS MAIN_PRD, ");
    sb.append(" CASE PTYPE ");
    sb.append("   WHEN 'MFD' THEN GEN.PARAM_NAME ");
    sb.append("   WHEN 'ETF' THEN ETF.TXN_AMOUNT ");
    sb.append("   WHEN 'BND' THEN TO_CHAR(BNDINFO.BASE_AMT_OF_PURCHASE)");
    sb.append("   WHEN 'SI' THEN TO_CHAR(SIINFO.BASE_AMT_OF_PURCHASE)");
    sb.append("   WHEN 'SN' THEN TO_CHAR(SNINFO.BASE_AMT_OF_PURCHASE)");
    sb.append("   WHEN 'INS' THEN TO_CHAR(BASE_INS.BASE_AMT_OF_PURCHASE)");
    sb.append("   ELSE NULL END AS GEN_SUBS_MINI_AMT_FOR,");
    sb.append(" CASE PTYPE ");
    sb.append("   WHEN 'MFD' THEN SML.PARAM_NAME ");  //最低申購門檻 SOT.NF_MIN_BUY_AMT_1:單筆, SOT.NF_MIN_BUY_AMT_2:定期定額,SOT.NF_MIN_BUY_AMT_3:定期不定額
    sb.append("   WHEN 'SN' THEN TO_CHAR(SNINFO.BASE_AMT_OF_BUYBACK)");
    sb.append("   ELSE NULL END AS SML_SUBS_MINI_AMT_FOR,");
    sb.append(" CASE PTYPE");
    sb.append("   WHEN 'BND' THEN (BNDINFO.UNIT_OF_VALUE)");
    sb.append("   WHEN 'SN' THEN (SNINFO.UNIT_OF_VALUE)");
    sb.append("   ELSE NULL END AS PRD_UNIT,");
    sb.append(" CASE WHEN MAIN.PRD_TYPE = 'INS' THEN BASE_INS.KEY_NO ");
    sb.append("  ELSE NULL END AS KEY_NO,");
    sb.append(" MAIN.TARGETS,");
    sb.append(" MAIN.CERTIFICATE_ID AS CERT_NBR,");
    
    sb.append(" case when BASE_INS.PRD_ID is not null then 'S' ");
    sb.append(" when BNDINFO.STOCK_BOND_TYPE  is not null then BNDINFO.STOCK_BOND_TYPE ");
    sb.append(" when SIINFO.STOCK_BOND_TYPE  is not null then SIINFO.STOCK_BOND_TYPE ");
    sb.append(" when SNINFO.STOCK_BOND_TYPE  is not null then SNINFO.STOCK_BOND_TYPE ");
    sb.append(" when NANOINFO.STOCK_BOND_TYPE  is not null then NANOINFO.STOCK_BOND_TYPE "); //新增奈米投
    sb.append(" when PRD.STOCK_BOND_TYPE  is not null then PRD.STOCK_BOND_TYPE end as STOCK_BOND_TYPE, ");
    
    sb.append(" decode(case when BASE_INS.PRD_ID is not null then 'S' ");
    sb.append(" when BNDINFO.STOCK_BOND_TYPE  is not null then BNDINFO.STOCK_BOND_TYPE ");
    sb.append(" when SIINFO.STOCK_BOND_TYPE  is not null then SIINFO.STOCK_BOND_TYPE ");
    sb.append(" when SNINFO.STOCK_BOND_TYPE  is not null then SNINFO.STOCK_BOND_TYPE ");
    sb.append(" when NANOINFO.STOCK_BOND_TYPE  is not null then NANOINFO.STOCK_BOND_TYPE "); //新增奈米投
    sb.append(" when PRD.STOCK_BOND_TYPE  is not null then PRD.STOCK_BOND_TYPE end ,'B','2','3') as INV_PRD_TYPE ");
    
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_INV MAIN ");
    sb.append(" LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = MAIN.PRD_ID ");
    sb.append(" LEFT JOIN TBPRD_FUNDINFO FUIN ON FUIN.PRD_ID = MAIN.PRD_ID ");
    sb.append(" LEFT JOIN SYSPAR ON SYSPAR.PARAM_CODE = FU.FUND_TYPE ");
    sb.append(" LEFT JOIN BASE_INS ON BASE_INS.PRD_ID = MAIN.PRD_ID AND BASE_INS.CURR_CD = MAIN.TRUST_CURR");
    sb.append(" LEFT JOIN TREND ON TREND.TYPE = FU.INV_TARGET ");
    sb.append(" LEFT JOIN GEN ON GEN.PARAM_CODE = (decode(FUIN.FUS20,'C','TWD_DOMESTIC',FU.CURRENCY_STD_ID)) ");
    sb.append(" LEFT JOIN SML ON SML.PARAM_CODE = FU.CURRENCY_STD_ID ");
    sb.append(" LEFT JOIN ETF ON ETF.PRD_ID = MAIN.PRD_ID ");
    sb.append(" LEFT JOIN TREND TREND_ETF ON TREND_ETF.TYPE = ETF.INV_TARGET  ");
    sb.append(" LEFT JOIN TBPRD_BONDINFO BNDINFO ON BNDINFO.PRD_ID = MAIN.PRD_ID");
    sb.append(" LEFT JOIN TBPRD_SIINFO SIINFO ON SIINFO.PRD_ID = MAIN.PRD_ID");
    sb.append(" LEFT JOIN TBPRD_SNINFO SNINFO ON SNINFO.PRD_ID = MAIN.PRD_ID");
    sb.append(" LEFT JOIN TBPRD_NANO NANOINFO ON NANOINFO.INV_LEVEL = MAIN.INV_PRD_TYPE_2 AND NANOINFO.IS_SALE = 'Y'");
    sb.append(" LEFT JOIN VWPRD_MASTER PRD ON PRD.PRD_ID = MAIN.PRD_ID AND PRD.PTYPE != 'STOCK' ");
//    sb.append(" AND PRD.CURRENCY_STD_ID = MAIN.TRUST_CURR");

    sb.append(" WHERE PLAN_ID = :planID ");
    sb.append(" AND (MAIN.PRD_TYPE = 'NANO' OR PRD.PTYPE <> 'INS') "); //2020-1-31 BY JACKY ADD
    qc.setObject("planID", planID);
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> result = dam.exeQuery(qc);
    putInsMinValue(dam, result);
    FPSUtils.mergeRedemption(dam, result);
    return result;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getHisStock(DataAccessManager dam, String custID) throws DAOException, JBranchException {
    // CRM.AST_ALLPRD_TYPE : AST code
    // '01','02','04','05', 儲蓄
    // '07','08','09','12' 基金 ETF
    // '10','15', 債券 SI SN
    // '14' 保險
	// '18' 奈米投  2020-1-30 by Jacky add
    // Todo: stock invest ins targets
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" WITH");
    sb.append(" GEN AS");
    sb.append("   (SELECT PARAM_CODE,");
    sb.append("           PARAM_NAME");
    sb.append("    FROM TBSYSPARAMETER");
    sb.append("    WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_1'),");
    sb.append(" SML AS");
    sb.append("   (SELECT PARAM_CODE,");
    sb.append("           PARAM_NAME");
    sb.append("    FROM TBSYSPARAMETER");
    sb.append("    WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_2'),");
    sb.append(" ETF AS");
    sb.append("   (SELECT PRD_ID, TXN_AMOUNT, INV_TARGET, STOCK_BOND_TYPE");
    sb.append("    FROM TBPRD_ETF");
    sb.append("    WHERE IS_SALE IS NULL),");
    sb.append(" SYSPAR AS");
    sb.append("   (SELECT PARAM_CODE,");
    sb.append("           PARAM_NAME");
    sb.append("    FROM TBSYSPARAMETER");
    sb.append("    WHERE PARAM_TYPE = 'FPS.FUND_TYPE'),");
    sb.append(" AREA AS");
    sb.append("   (SELECT PARAM_CODE,");
    sb.append("           PARAM_NAME");
    sb.append("    FROM TBSYSPARAMETER");
    sb.append("    WHERE PARAM_TYPE = 'PRD.MKT_TIER1'),");
    sb.append(" TREND AS");
    sb.append("   (SELECT TRD.TYPE,");
    sb.append("           TRD.TREND");
    sb.append("    FROM TBFPS_MARKET_TREND TR");
    sb.append("    LEFT JOIN TBFPS_MARKET_TREND_DETAIL TRD ON TR.PARAM_NO = TRD.PARAM_NO");
    sb.append("    WHERE TR.STATUS = 'A'),");
    sb.append(" BASE_INS AS");
    sb.append("   (SELECT DISTINCT BASE.PRD_ID,");
    sb.append("                    BASE.CURR_CD,");
    sb.append("                    INSINFO.BASE_AMT_OF_PURCHASE,");
    sb.append("                    BASE.INS_TYPE,");
    sb.append("                    'S' STOCK_BOND_TYPE,");
    sb.append("                    LISTAGG(BASE.KEY_NO, ',') WITHIN");
    sb.append("    GROUP (ORDER BY BASE.KEY_NO) AS KEY_NO");
    sb.append("    FROM TBPRD_INS BASE");
    sb.append("    LEFT JOIN TBPRD_INSINFO INSINFO ON BASE.PRD_ID = INSINFO.PRD_ID");
    sb.append("    WHERE BASE.INS_TYPE IN ('1', '2')");
    sb.append("    GROUP BY BASE.PRD_ID,");
    sb.append("             BASE.CURR_CD,");
    sb.append("             INSINFO.BASE_AMT_OF_PURCHASE,");
    sb.append("             BASE.INS_TYPE, 'S'),");
    sb.append(" INS_TARGET AS (");
    sb.append("   SELECT (M.POLICY_NBR ||'-'|| TRIM(TO_CHAR(M.POLICY_SEQ  ,'00')) || CASE WHEN M.ID_DUP <> ' ' THEN '-' || M.ID_DUP END ) AS CERT_NBR, ");
    sb.append("   M.PRD_ID, LISTAGG(D.INV_TARGET_NO, '/') WITHIN GROUP (ORDER BY D.POLICY_NO) AS TARGETS ");
    sb.append("   FROM TBCRM_AST_INS_MAST M");
    sb.append("   INNER JOIN TBCRM_AST_INS_TARGET D ON M.POLICY_NBR = D.POLICY_NO AND M.POLICY_SEQ = D.POLICY_SEQ");
    sb.append("   WHERE M.CUST_ID = :custID");
    sb.append("   GROUP BY M.PRD_ID, (M.POLICY_NBR ||'-'|| TRIM(TO_CHAR(M.POLICY_SEQ ,'00')) || CASE WHEN M.ID_DUP <> ' ' THEN '-' || M.ID_DUP END )");
    sb.append(" )");
    sb.append(" SELECT MAIN.AST_TYPE,");
    sb.append("        MAIN.PROD_ID AS PRD_ID,");
    sb.append("        NVL(PRD.PNAME, MAIN.PROD_NAME) AS PRD_CNAME,");
    sb.append("        MAIN.CUR_ID AS TRUST_CURR,");
//    sb.append("        MAIN.NOW_AMT,");
    sb.append("        (CASE WHEN MAIN.CUR_ID = 'TWD' THEN MAIN.NOW_AMT_TWD ELSE MAIN.NOW_AMT END) AS NOW_AMT,");
    sb.append("        MAIN.NOW_AMT_TWD,");
    sb.append("        MAIN.INS_TYPE,");
    
//    sb.append("        CASE AST_TYPE");
//    sb.append("            WHEN '01' THEN '1'");
//    sb.append("            WHEN '02' THEN '1'");
//    sb.append("            WHEN '04' THEN '1'");
//    sb.append("            WHEN '05' THEN '1'");
//    sb.append("            WHEN '07' THEN '3'");
//    sb.append("            WHEN '08' THEN '3'");
//    sb.append("            WHEN '09' THEN '3'");
//    sb.append("            WHEN '12' THEN '3'");
//    sb.append("            WHEN '10' THEN '2'");
//    sb.append("            WHEN '15' THEN '2'");
//    sb.append("            WHEN '14' THEN CASE MAIN.INS_TYPE");
//    sb.append("                               WHEN '1' THEN '1'");
//    sb.append("                               ELSE '3' END");
//    sb.append("        END AS INV_PRD_TYPE,");
    
    sb.append("        case when ETF.PRD_ID is not null then ETF.STOCK_BOND_TYPE");
    sb.append("        when BASE_INS.PRD_ID  is not null then BASE_INS.STOCK_BOND_TYPE");
    sb.append("        when FUIN.PRD_ID  is not null then FUIN.STOCK_BOND_TYPE");
    sb.append("        when BNDINFO.PRD_ID  is not null then BNDINFO.STOCK_BOND_TYPE");
    sb.append("        when SIINFO.PRD_ID  is not null then SIINFO.STOCK_BOND_TYPE");
    sb.append("        when SNINFO.PRD_ID  is not null then SNINFO.STOCK_BOND_TYPE");
    sb.append("        when NANOINFO.PRD_ID  is not null then NANOINFO.STOCK_BOND_TYPE ");
    sb.append("        end as STOCK_BOND_TYPE,");
  
    sb.append("        decode(");
    sb.append("        case when ETF.PRD_ID is not null then ETF.STOCK_BOND_TYPE");
    sb.append("        when BASE_INS.PRD_ID  is not null then BASE_INS.STOCK_BOND_TYPE");
    sb.append("        when FUIN.PRD_ID  is not null then FUIN.STOCK_BOND_TYPE");
    sb.append("        when BNDINFO.PRD_ID  is not null then BNDINFO.STOCK_BOND_TYPE");
    sb.append("        when SIINFO.PRD_ID  is not null then SIINFO.STOCK_BOND_TYPE");
    sb.append("        when SNINFO.PRD_ID  is not null then SNINFO.STOCK_BOND_TYPE ");
    sb.append("        when NANOINFO.PRD_ID  is not null then NANOINFO.STOCK_BOND_TYPE ");
    sb.append("        end ,'B','2','3') as INV_PRD_TYPE,");
    
    sb.append("        CASE");
    sb.append("            WHEN MAIN.AST_TYPE = '14'");
    sb.append("                 AND MAIN.INS_TYPE = '1' THEN '3'");
    sb.append("            ELSE ''");
    sb.append("        END AS INV_PRD_TYPE_2,");
    sb.append("        CASE");
    sb.append("            WHEN TREND.TREND IS NOT NULL THEN TREND.TREND");
    sb.append("            WHEN TREND_ETF.TREND IS NOT NULL THEN TREND_ETF.TREND");
    sb.append("        END AS CIS_3M,");
    sb.append("        CASE WHEN MAIN.AST_TYPE = '18' THEN 'NANO' ELSE PRD.PTYPE END PTYPE,");
    sb.append("        CASE WHEN MAIN.AST_TYPE = '18' THEN NANOINFO.RISKCATE_ID ELSE PRD.RISKCATE_ID END AS RISK_TYPE,");
    sb.append("        MAIN.CUR_ID_ORI AS CURRENCY_TYPE,");
    sb.append("        CASE");
    sb.append("            WHEN PRD.PRD_RANK IS NOT NULL OR MAIN.AST_TYPE = '18' THEN 'Y'");
    sb.append("            ELSE 'N'");
    sb.append("        END AS MAIN_PRD,");
    sb.append("        CASE PRD.PTYPE");
    sb.append("            WHEN 'MFD' THEN GEN.PARAM_NAME");
    sb.append("            WHEN 'ETF' THEN ETF.TXN_AMOUNT");
    sb.append("            WHEN 'BND' THEN TO_CHAR(BNDINFO.BASE_AMT_OF_PURCHASE)");
    sb.append("            WHEN 'SI' THEN TO_CHAR(SIINFO.BASE_AMT_OF_PURCHASE)");
    sb.append("            WHEN 'SN' THEN TO_CHAR(SNINFO.BASE_AMT_OF_PURCHASE)");
    sb.append("            WHEN 'INS' THEN TO_CHAR(BASE_INS.BASE_AMT_OF_PURCHASE)");
    sb.append("            ELSE NULL");
    sb.append("        END AS GEN_SUBS_MINI_AMT_FOR,");
    sb.append("        CASE PRD.PTYPE");
    sb.append("            WHEN 'MFD' THEN SML.PARAM_NAME");
    sb.append("            WHEN 'SN' THEN TO_CHAR(SNINFO.BASE_AMT_OF_BUYBACK)");
    sb.append("            ELSE NULL");
    sb.append("        END AS SML_SUBS_MINI_AMT_FOR,");
    sb.append("        CASE PTYPE");
    sb.append("            WHEN 'BND' THEN (BNDINFO.UNIT_OF_VALUE)");
    sb.append("            WHEN 'SN' THEN (SNINFO.UNIT_OF_VALUE)");
    sb.append("            ELSE NULL");
    sb.append("        END AS PRD_UNIT,");
    sb.append("        CASE");
    sb.append("            WHEN PRD.PTYPE = 'INS' THEN BASE_INS.KEY_NO");
    sb.append("            ELSE NULL");
    sb.append("        END AS KEY_NO,");
    sb.append(" MAIN.CERT_NBR,");
    sb.append(" MAIN.INV_AMT,");
    sb.append(" MAIN.INV_AMT_TWD,");
    sb.append(" MAIN.RTN_RATE_WD,");
    sb.append(" MAIN.RTN_RATE_WD_TWD,");
    sb.append(" MAIN.DATA_DATE,");
    sb.append(" INS_TARGET.TARGETS");
    sb.append(" FROM MVFPS_AST_ALLPRD_DETAIL MAIN");
    sb.append(" LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = MAIN.PROD_ID");
    sb.append(" LEFT JOIN TBPRD_FUNDINFO FUIN ON FUIN.PRD_ID = FU.PRD_ID");
    sb.append(" LEFT JOIN GEN ON GEN.PARAM_CODE = (decode(FUIN.FUS20,'C','TWD_DOMESTIC',FU.CURRENCY_STD_ID))");
    sb.append(" LEFT JOIN SML ON SML.PARAM_CODE = FU.CURRENCY_STD_ID");
    sb.append(" LEFT JOIN ETF ON ETF.PRD_ID = MAIN.PROD_ID");
    sb.append(" LEFT JOIN SYSPAR ON SYSPAR.PARAM_CODE = FU.FUND_TYPE");
    sb.append(" LEFT JOIN TREND ON TREND.TYPE = FU.INV_TARGET");
    sb.append(" LEFT JOIN AREA ON AREA.PARAM_CODE = TREND.TYPE");
    sb.append(" LEFT JOIN BASE_INS ON BASE_INS.PRD_ID = MAIN.PROD_ID AND BASE_INS.CURR_CD = MAIN.CUR_ID");
    sb.append(" LEFT JOIN TREND TREND_ETF ON TREND_ETF.TYPE = ETF.INV_TARGET");
    sb.append(" LEFT JOIN TBPRD_BONDINFO BNDINFO ON BNDINFO.PRD_ID = MAIN.PROD_ID");
    sb.append(" LEFT JOIN TBPRD_SIINFO SIINFO ON SIINFO.PRD_ID = MAIN.PROD_ID");
    sb.append(" LEFT JOIN TBPRD_SNINFO SNINFO ON SNINFO.PRD_ID = MAIN.PROD_ID");
    sb.append(" LEFT JOIN TBPRD_NANO NANOINFO ON NANOINFO.INV_LEVEL = MAIN.INS_TYPE AND NANOINFO.IS_SALE = 'Y'"); //2020-4-15 林家琪說奈米投的投資策略每一個level只會有一檔
    sb.append(" LEFT JOIN VWPRD_MASTER PRD ON PRD.PRD_ID = MAIN.PROD_ID AND PRD.PTYPE != 'STOCK' AND PRD.CURRENCY_STD_ID = MAIN.CUR_ID_ORI");
    sb.append(" LEFT JOIN TBFPS_PLANID_MAPPING MAPPING ON MAPPING.CERTIFICATE_ID = MAIN.CERT_NBR");
    sb.append(" LEFT JOIN INS_TARGET ON INS_TARGET.CERT_NBR = MAIN.CERT_NBR");
//    sb.append(" LEFT JOIN (SELECT CUR_COD, BUY_RATE FROM TBPMS_IQ053 WHERE MTN_DATE = (SELECT MAX (MTN_DATE) FROM TBPMS_IQ053)) IQ053 on IQ053.CUR_COD = MAIN.CUR_ID");
    sb.append(" WHERE MAIN.CUST_ID = :custID");
    sb.append("   AND MAIN.AST_TYPE IN ('07', '08', '09', '12', '10', '15', '14', '18')"); //2020-1-31 by Jacky 新增奈米投
    sb.append("   AND MAIN.NOW_AMT != 0");
    sb.append("   AND (MAIN.AST_TYPE = '18' OR PRD.PTYPE <> 'INS')");
    sb.append("   AND MAPPING.CERTIFICATE_ID IS NULL");

    qc.setObject("custID", custID);

    qc.setQueryString(sb.toString());

    List<Map<String, Object>> result = dam.exeQuery(qc);
    putInsMinValue(dam, result);
    FPSUtils.mergeRedemption(dam, result);
    return result;
  }

  // 檢核固定收益商品
  // return fixed list
  public List<Map<String, Object>> fixedModelQuery(DataAccessManager dam, String custID, String riskType, BigDecimal amt, boolean isPro, String stockBondType) throws Exception {
	   
    BigDecimal USD_Amt = null;
    // 推薦商品
    List<Map<String, Object>> fixedList = new ArrayList<Map<String, Object>>();

    try {
      // 初始化適配檢核
      ProdFitness prodFitness = (ProdFitness) PlatformContext.getBean("ProdFitness");
      // 匯率
      List<Map<String, Object>> rateList = getFxRate(dam, "USD");
      BigDecimal USD_Rate = rateList.size() > 0 ? new BigDecimal(rateList.get(0).get("BUY_RATE").toString()) : new BigDecimal(0);
      USD_Amt = amt.divide(USD_Rate, 2, BigDecimal.ROUND_DOWN);
      
      BigDecimal usdAmtValue = AMT_VALUE.divide(USD_Rate, 2, BigDecimal.ROUND_DOWN); // 檢核 TWD 轉 USD
      BigDecimal amount3W1W = FIXED3W.add(FIXED1W); // BOND + SI 
      
      // 客戶資料適配檢核
      if (prodFitness.validCustBondSISN(custID).getIsError()) {
        // 客戶資料適配檢核失敗
        System.out.println("客戶資料適配檢核失敗");
        return fixedList;
      }
      
      if(amt.intValue() < AMT_VALUE.intValue()) {
    	  // 全買債基金
      } else {
    	  if(USD_Amt.compareTo(amount3W1W) > 0 && USD_Amt.subtract(amount3W1W).compareTo(usdAmtValue) >= 0) {
    		  // 可以買 BOND + SI + 債基類商品
    		  fixedList.addAll(filterFixedMinWithPct(getBND(dam, riskType, prodFitness, isPro, stockBondType), USD_Amt.subtract(usdAmtValue), 1, fixedList.size() <= 0 ? 100 : 75));
    		  fixedList.addAll(filterFixedMinWithPct(getSI(dam, riskType, prodFitness, isPro, stockBondType), USD_Amt.subtract(usdAmtValue), 1, 100));
    	  } else if (USD_Amt.compareTo(FIXED1W) > 0 && USD_Amt.subtract(FIXED1W).compareTo(usdAmtValue) >= 0){
    		// 可以買 SI + 債基類商品
    		  fixedList.addAll(filterFixedMinWithPct(getSI(dam, riskType, prodFitness, isPro, stockBondType), USD_Amt.subtract(usdAmtValue), 1, 100));
    	  } else {
    		// 只能買 債基類的
    	  }
      }

    } catch (JBranchException e) {
      e.printStackTrace();
      return null;
      // throw new APException("電文失敗");
    }
    return fixedList;
  }

  // put ins limit value
  @SuppressWarnings("unchecked")
  private void putInsMinValue(DataAccessManager dam, List<Map<String, Object>> totalList) throws DAOException, JBranchException {
    if (totalList.size() <= 0) {
      return;
    }
    List<String> keyNos = new ArrayList<String>();
    for (Map<String, Object> ins : totalList) {
      if (ins.get("KEY_NO") == null) {
        continue;
      }
      // get ins keyNos
      String keyNo = ins.get("KEY_NO").toString().split(",")[0];
      if (StringUtils.isBlank(keyNo)) {
        continue;
      } else {
        // test
        // ins.put("KEY_NO", "608");
        ins.put("KEY_NO", keyNo);
        keyNos.add(keyNo);
      }
    }

    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    // 最低保額
    sb.append(" WITH");
    sb.append(" BASE AS (");
    sb.append("   SELECT");
    sb.append("   CASE WHEN PARAM_CODE = '4' THEN 10000 ");
    sb.append("        WHEN PARAM_CODE = '3' THEN 1000");
    sb.append("        WHEN PARAM_CODE = '2' THEN 100");
    sb.append("        ELSE 1 END AS INS_UNIT,");
    sb.append("   PARAM_CODE");
    sb.append("   FROM TBSYSPARAMETER");
    sb.append("   WHERE PARAM_TYPE= 'INS.UNIT')");
    sb.append(" SELECT B.KEY_NO, B.POLICY_AMT_MIN, C.PRD_UNIT,");
    sb.append(" B.POLICY_AMT_MIN*BASE.INS_UNIT AS BASE_RESULT");
    sb.append(" FROM TBINS_PARA_HEADER A");
    sb.append(" INNER JOIN TBPRD_INS_SUGGEST B ON A.PARA_NO = B.PARA_NO");
    sb.append(" INNER JOIN TBPRD_INS C ON B.KEY_NO = C.KEY_NO");
    sb.append(" LEFT JOIN BASE ON BASE.PARAM_CODE = C.PRD_UNIT");
    sb.append(" WHERE A.PARA_TYPE = '5'"); // 退休
    sb.append(" AND A.STATUS = 'A'");
    sb.append(" AND A.EFFECT_DATE <= SYSDATE");
    sb.append(" AND NVL(A.EXPIRY_DATE, SYSDATE) >= TRUNC(SYSDATE)");
    sb.append(" AND B.KEY_NO IN ('" + StringUtils.join(keyNos, "','") + "')");

    qc.setQueryString(sb.toString());
    List<Map<String, Object>> minLimitList = dam.exeQuery(qc);

    // put in hashMap for mapping
    Map<String, Double> minListMap = new HashedMap();
    for (Map<String, Object> min : minLimitList) {
      minListMap.put(min.get("KEY_NO").toString(), Double.valueOf(min.get("BASE_RESULT").toString()));
    }

    // put min in insList
    for (Map<String, Object> ins : totalList) {
      if (ins.get("KEY_NO") == null) {
        continue;
      }
      if (minListMap.get(ins.get("KEY_NO")) != null) {
        ins.put("GEN_SUBS_MINI_AMT_FOR", minListMap.get("KEY_NO"));
      }
    }
  }

  /**
   * 歷史績效投資組合
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   * @throws IOException
   */
  public void historyModels(Object body, IPrimitiveMap<?> header) throws JBranchException, ParseException {
    DataAccessManager dam = this.getDataAccessManager();
    FPS230InputVO inputVO = (FPS230InputVO) body;
    FPS230OutputVO outputVO = new FPS230OutputVO();
    String riskType = inputVO.getRiskType();
    String sppType = inputVO.getSppType();
    String stockBondType = inputVO.getStockBondType();
    // IS/IB
    if("INV".equals(sppType)) {
    	sppType = "I" + stockBondType;
    } 
    outputVO.setHistoryModelList(getHistoryModel(dam, riskType, sppType));

    this.sendRtnObject(outputVO);
  }

  public List<Map<String, Object>> getHistoryModel(DataAccessManager dam, String riskType, String sppType) throws JBranchException, ParseException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    // 只取現年月往前一個月 一年之資料
    String year = null, month = null;
    year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
    year = "0".equals(month) ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1) : year;
    month = "0".equals(month) ? "12" : (month.length() == 1 ? "0" + month : month);
    String currentYearMonth = year + month;

    Calendar lastYearMonthCal = Calendar.getInstance();
    lastYearMonthCal.setTime(new Date());
    lastYearMonthCal.add(Calendar.YEAR, -1);
    lastYearMonthCal.add(Calendar.MONTH, 1);
    year = String.valueOf(lastYearMonthCal.get(Calendar.YEAR));
    month = String.valueOf(lastYearMonthCal.get(Calendar.MONTH));
    year = "0".equals(month) ? String.valueOf(lastYearMonthCal.get(Calendar.YEAR) - 1) : year;
    month = "0".equals(month) ? "12" : (month.length() == 1 ? "0" + month : month);
    String lastYearMonth = year + month;

    StringBuffer sb = new StringBuffer();
    sb.append(" WITH ");
    sb.append(" INS_TG AS( ");
    sb.append(" SELECT DE.SEQNO, DE.PARAM_NO, DE.PRD_ID, ");
    sb.append(" LISTAGG(INS.TARGET_ID, '/') WITHIN GROUP (ORDER BY INS.TARGET_ID) AS TARGETS ");
    sb.append(" FROM TBFPS_MODEL_ALLOCATION_INS INS ");
    sb.append(" LEFT JOIN TBFPS_MODEL_ALLOCATION DE ON INS.SEQNO = DE.SEQNO ");
    sb.append(" GROUP BY DE.SEQNO, DE.PARAM_NO, DE.PRD_ID) ");
    sb.append(" SELECT ");
    sb.append(" DE.DATA_YEARMONTH, DE.PRD_TYPE AS PRD_TYPE, ");
    sb.append(" DE.PRD_NAME AS PRD_CNAME, DE.INV_PERCENT, ");
    sb.append(" DE.PRD_ID, IT1.TARGETS, DE.CUST_RISK_ATR ");
    sb.append(" FROM TBFPS_HIS_PERF_TARGET DE ");
    sb.append(" LEFT JOIN INS_TG IT1 ON DE.PRD_TYPE = 'INS' ");
    sb.append(" AND IT1.PRD_ID = DE.PRD_ID AND DE.PARAM_NO = IT1.PARAM_NO ");
    sb.append(" WHERE DE.PLAN_TYPE = :sppType AND DE.CUST_RISK_ATR = :riskType ");
    sb.append(" AND DE.DATA_YEARMONTH >= :startDate AND DE.DATA_YEARMONTH <= :endDate");
    sb.append(" ORDER by DATA_YEARMONTH, PRD_TYPE ");
    qc.setObject("sppType", sppType);
    qc.setObject("riskType", riskType);
    qc.setObject("startDate", lastYearMonth);
    qc.setObject("endDate", currentYearMonth);
    qc.setQueryString(sb.toString());
    System.out.println("SENGY:" + sppType + ";" + riskType + ";" + lastYearMonth  + ";" +  currentYearMonth);
    return FPSUtils.getModelPortfolio(dam, dam.exeQuery(qc), riskType);
  }

  /**
   * 投資組合 TODO
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws IOException
   */
  public void checkVolatility(Object body, IPrimitiveMap<?> header) throws JBranchException {
    DataAccessManager dam = this.getDataAccessManager();
    FPS230InputVO inputVO = (FPS230InputVO) body;
    FPS230OutputVO outputVO = new FPS230OutputVO();
    FPSUtilsOutputVO invalidOutputVO = new FPSUtilsOutputVO();
    invalidOutputVO.setError(true);

    List<Map<String, Object>> prodList = new ArrayList<Map<String, Object>>();

    for (FPS230ProdInputVO prdInputVO : inputVO.getProdList()) {
      Map<String, Object> prd = new HashedMap();
      prd.put("PRD_ID", prdInputVO.getPRD_ID());
      prd.put("PTYPE", prdInputVO.getPRD_TYPE());
      prd.put("TARGETS", prdInputVO.getTargets());
      prd.put("PURCHASE_TWD_AMT", prdInputVO.getPURCHASE_TWD_AMT());
      prodList.add(prd);
    }

    // set init key map for checkValidYear
    Map<String, String> keyMap = new HashMap<String, String>();
    keyMap.put("prodType", "PTYPE");
    keyMap.put("prodID", "PRD_ID");
    keyMap.put("targets", "TARGETS");

    String[] prdArray = FPSUtils.getPrdArray(prodList, "PTYPE");
    List<Map<String, Object>> formatedStockLs = formatWeightMap(prodList);

    // 1年
    if (FPSUtils.checkValidYear(dam, prodList, 1, keyMap).length == 0) {
      // 波動率
      BigDecimal volatility = FPSUtils.getStandardDeviation(dam, formatedStockLs, 36, 12, false);
      outputVO.setVolatility(volatility == null ? null : volatility.doubleValue());
    } else {
      outputVO.setVolatility(null);
    }
    this.sendRtnObject(outputVO);
  }

  @SuppressWarnings("unchecked")
  private BigDecimal getLimitVolatility(DataAccessManager dam, String riskType) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT DE.VOLATILITY FROM TBFPS_CUSTRISK_VOLATILITY_HEAD HEAD");
    sb.append(" JOIN TBFPS_CUSTRISK_VOLATILITY DE ON HEAD.PARAM_NO = DE.PARAM_NO");
    sb.append(" WHERE 1 = 1");
    sb.append(" AND HEAD.STATUS = 'A'");
    sb.append(" AND DE.CUST_RISK_ATR = :riskType");
    qc.setObject("riskType", riskType);

    qc.setQueryString(sb.toString());
    return (BigDecimal) ((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("VOLATILITY");
  }

  /**
   * 投資組合
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws IOException
   */
  public void getModels(Object body, IPrimitiveMap<?> header) throws JBranchException {
    FPS230InputVO inputVO = (FPS230InputVO) body;
    FPS230OutputVO outputVO = new FPS230OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    String riskType = inputVO.getRiskType();
    String isPro = inputVO.getIsPro();
    String isOBU = inputVO.getOBU();
    
    String planID = inputVO.getPlanID();
    String custID = inputVO.getCustID();
    List<String> errorList = new ArrayList<String>();

    if (StringUtils.isBlank(riskType)) {
      throw new APException("沒有RiskType"); //
    }
    
    List<Map<String, Object>> headList = fps210.getCustPlan(dam, planID);
    Map<String, Object> headMap = headList.get(0);
    BigDecimal fixedAmt = ((BigDecimal) headMap.get("PORTFOLIO2_RATIO")).multiply((BigDecimal) headMap.get("PLAN_AMT")).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
    
    List<Map<String, Object>> fixedModel = null;
    try {
      fixedModel = fixedModelQuery(dam, custID, riskType, fixedAmt, isPro.equals("Y"), "B");
      if (fixedModel == null) {
        errorList.add("SI/SN適配電文錯誤，無法配置推薦SI/SN商品。");
      }
    } catch (Exception e) {
      errorList.add("SI/SN適配電文錯誤，無法配置推薦SI/SN商品。");
    }
    
    // portfolio 要分兩邊 A.類債券[BOND, SI, MFD, ETF] B.類股票[MFD, ETF, INS]
    List<Map<String, Object>> allModelPortfolio = modelQuery(dam, riskType, null, isOBU, isPro);
    Map<String, List<Map<String, Object>>> groupByStockBondTypeMap = groupByStockBondType(allModelPortfolio);
    
    // 類債券 (額外加工計算 比例)
    List<Map<String, Object>> modelPortfolioB = mergeModelWithFixed(groupByStockBondTypeMap.get("B"), fixedModel);
    reCalPct(modelPortfolioB, fixedAmt, dam);
    
    // 類股票
    List<Map<String, Object>> modelPortfolioS = mergeModelWithFixed(groupByStockBondTypeMap.get("S"), new ArrayList<Map<String, Object>>());
    List<Map<String, Object>> modelPortfolio = new ArrayList<Map<String, Object>>();
    modelPortfolio.addAll(modelPortfolioB);
    modelPortfolio.addAll(modelPortfolioS);
    outputVO.setOutputList(modelPortfolio);

    this.sendRtnObject(outputVO);
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
    // head
    String planID = exeSave(body);

    // details
    DataAccessManager dam = this.getDataAccessManager();
    FPS230InputVO inputVO = (FPS230InputVO) body;

    if (inputVO.getProdList() != null && StringUtils.isNotBlank(planID)) {
      // get exist seqNos
      Set<BigDecimal> existSet = new HashSet<BigDecimal>();
      // get filtered targetLs
      for (FPS230ProdInputVO item : inputVO.getProdList()) {
        BigDecimal seq = item.getSEQNO();
        if (seq != null) {
          existSet.add(seq);
        }
      }
      // get delete list
      List<Map<String, Object>> deleteSeqNoLs = FPSUtils.filterDeleteList(dam, existSet, planID, "INV");
      // delete
      for (Map<String, Object> del : deleteSeqNoLs) {
        delete(dam, planID, (BigDecimal) del.get("SEQNO"), ObjectUtils.toString(del.get("PRD_ID")));
      }

      for (FPS230ProdInputVO prod : inputVO.getProdList()) {
        if (prod.getSEQNO() == null) {
          BigDecimal invID = new BigDecimal(getSEQ(dam, "FPS_INV_DETAIL"));
          create(prod, dam, planID, invID);
        } else {
          update(prod, dam, planID);
        }
      }
    }
    this.sendRtnObject(planID); // 新增成功
  }

  // update head
  protected void update(Object body, DataAccessManager dam, String planID) throws DAOException, JBranchException {
    FPS230InputVO inputVO = (FPS230InputVO) body;
    TBFPS_PORTFOLIO_PLAN_INV_HEADVO vo = (TBFPS_PORTFOLIO_PLAN_INV_HEADVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_INV_HEADVO.TABLE_UID, planID);
    if (vo != null) {
      dam.update(putPlanInvHeadVO(vo, inputVO));
    } else {
      throw new APException("ehl_01_common_009"); // 查無資料
    }
  }

  private TBFPS_PORTFOLIO_PLAN_INV_HEADVO putPlanInvHeadVO(TBFPS_PORTFOLIO_PLAN_INV_HEADVO vo, FPS230InputVO inputVO) {
    vo.setPLAN_STEP(StringUtils.isNotBlank(inputVO.getStep()) ? inputVO.getStep() : PLAN_STEP);
    return vo;
  }

  // create detail
  public void create(FPS230ProdInputVO inputVO, DataAccessManager dam, String planID, BigDecimal invID) throws JBranchException {
    TBFPS_PORTFOLIO_PLAN_INVPK pk = new TBFPS_PORTFOLIO_PLAN_INVPK();
    pk.setPLAN_ID(planID);
    pk.setSEQNO(invID);
    pk.setPRD_ID(inputVO.getPRD_ID());
    TBFPS_PORTFOLIO_PLAN_INVVO vo = new TBFPS_PORTFOLIO_PLAN_INVVO();
    if (planID != null && invID != null) {
      vo.setcomp_id(pk);
      dam.create(putPlanInvVO(vo, inputVO));
    } else {
      throw new APException("ehl_01_common_008"); // 新增失敗
    }
  }

  // update detail
  private void update(FPS230ProdInputVO inputVO, DataAccessManager dam, String planID) throws DAOException, APException {
    TBFPS_PORTFOLIO_PLAN_INVPK pk = new TBFPS_PORTFOLIO_PLAN_INVPK();
    pk.setPLAN_ID(planID);
    pk.setSEQNO(inputVO.getSEQNO());
    pk.setPRD_ID(inputVO.getPRD_ID());
    TBFPS_PORTFOLIO_PLAN_INVVO vo = (TBFPS_PORTFOLIO_PLAN_INVVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_INVVO.TABLE_UID, pk);
    if (vo != null) {
      dam.update(putPlanInvVO(vo, inputVO));
    } else {
      throw new APException("ehl_01_common_007"); // 更新失敗
    }
  }

  // delete detail
  private void delete(DataAccessManager dam, String planID, BigDecimal invID, String prdID) throws DAOException, APException {
    TBFPS_PORTFOLIO_PLAN_INVPK pk = new TBFPS_PORTFOLIO_PLAN_INVPK();
    pk.setPLAN_ID(planID);
    pk.setSEQNO(invID);
    pk.setPRD_ID(prdID);
    TBFPS_PORTFOLIO_PLAN_INVVO vo = (TBFPS_PORTFOLIO_PLAN_INVVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_INVVO.TABLE_UID, pk);
    if (vo != null) {
      dam.delete(vo);
    } else {
      throw new APException("ehl_01_common_005"); // 刪除失敗
    }
  }

  private TBFPS_PORTFOLIO_PLAN_INVVO putPlanInvVO(TBFPS_PORTFOLIO_PLAN_INVVO vo, FPS230ProdInputVO inputVO) {
    vo.setPRD_TYPE(inputVO.getPRD_TYPE());
    vo.setPRD_NAME(inputVO.getPRD_NAME());
    vo.setRISKCATE_ID(inputVO.getRISKCATE_ID());
    vo.setPROD_CURR(inputVO.getPROD_CURR());
    vo.setTRUST_CURR(inputVO.getTRUST_CURR());
    vo.setMARKET_CIS(inputVO.getMARKET_CIS());
    vo.setPURCHASE_ORG_AMT(inputVO.getPURCHASE_ORG_AMT());
    vo.setPURCHASE_TWD_AMT(inputVO.getPURCHASE_TWD_AMT());
    vo.setINVENTORY_ORG_AMT(inputVO.getINVENTORY_ORG_AMT());
    vo.setINVENTORY_TWD_AMT(inputVO.getINVENTORY_TWD_AMT());
    vo.setPORTFOLIO_RATIO(inputVO.getPORTFOLIO_RATIO());
    vo.setLIMIT_ORG_AMT(inputVO.getLIMIT_ORG_AMT());
    vo.setPORTFOLIO_TYPE(inputVO.getPORTFOLIO_TYPE());
    vo.setINV_PRD_TYPE_2(inputVO.getINV_PRD_TYPE_2());
    vo.setINV_PRD_TYPE(inputVO.getINV_PRD_TYPE());
    vo.setTXN_TYPE(inputVO.getTXN_TYPE());
    vo.setINV_TYPE(inputVO.getINV_TYPE());
    vo.setTARGETS(inputVO.getTargets());
    vo.setCERTIFICATE_ID(inputVO.getCERT_NBR());

    return vo;
  }

  // 取>最低申購金額 設pct 取幾位
  private List<Map<String, Object>> filterFixedMinWithPct(List<Map<String, Object>> oriList, BigDecimal amt, int num, int pct) {
    List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
    int cnt = 0;
    for (Map<String, Object> row : oriList) {
      if (amt.compareTo((BigDecimal) row.get("GEN_SUBS_MINI_AMT_FOR")) >= 0) {
        row.put("pct", new BigDecimal(pct));
        newList.add(row);
        cnt += 1;
      }
      if (cnt == num) {
        break;
      }
    }
    return newList;
  }
  
  // 取市場概況 FPS220 搬過來的
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
  
  // 分組 類債券/類股票
  // 之前用 INV_PRD_TYPE 分區塊，現在用 B / S
  public Map<String, List<Map<String, Object>>> groupByStockBondType(List<Map<String, Object>> allModelPorfolio) {
	  Map<String, List<Map<String, Object>>> newGroup = new HashMap<String, List<Map<String, Object>>>();
	  List<Map<String, Object>> bondTypeList = new ArrayList<Map<String, Object>>();
	  List<Map<String, Object>> stockTypeList = new ArrayList<Map<String, Object>>();
	  for(Map<String, Object> tempMap : allModelPorfolio) {
		  if("B".equals(tempMap.get("STOCK_BOND_TYPE"))) {
			  bondTypeList.add(tempMap);
		  } else {
			  tempMap.put("STOCK_BOND_TYPE", "S");
			  tempMap.put("INV_PRD_TYPE", "3");
			  stockTypeList.add(tempMap);
		  }
	  }
	  
	  newGroup.put("B", bondTypeList);
	  newGroup.put("S", stockTypeList);
	  return newGroup;
  }
  
  // 因為有牽扯 固定收益的商品 BOND / SI 所以需要 全部從算 比例
  // 重算邏輯 有 BOND SI 的先計算 剩下的計算 基金 ETF 
  // 如果沒有 BOND / SI 則 基金 ETF 根據原比例計算出金額後 分掉 多出來的錢再重算比例
  public void reCalPct(List<Map<String, Object>> prodList, BigDecimal totalAmt, DataAccessManager dam) throws DAOException, JBranchException {
	  
	  BigDecimal noBondTotalPct = BigDecimal.ZERO;
	  List<Map<String, Object>> bondList = new ArrayList<Map<String, Object>>();
	  List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
	  for(Map<String, Object> prodMap :prodList) {
		  if("2".equals(prodMap.get("INV_PRD_TYPE"))) {
			  bondList.add(prodMap);
		  } else if("3".equals(prodMap.get("INV_PRD_TYPE"))) {
			  tempList.add(prodMap);
			  noBondTotalPct = noBondTotalPct.add(new BigDecimal(ObjectUtils.toString(prodMap.get("INV_PERCENT"))));
		  }
		  prodMap.put("STOCK_BOND_TYPE", "B");
		  prodMap.put("INV_PRD_TYPE", "2");
	  }
	  BigDecimal usedPct = BigDecimal.ZERO;
	  BigDecimal lastTotalAmt = totalAmt;
	  if(bondList.size() >0) {
		  for(Map<String, Object> bondMap : bondList) {
			  List<Map<String, Object>> rateList = getFxRate(dam, ObjectUtils.toString(bondMap.get("CURRENCY_TYPE")));
			  BigDecimal rate = rateList.size() > 0 ? new BigDecimal(rateList.get(0).get("BUY_RATE").toString()) : new BigDecimal(0);
			  BigDecimal pct = new BigDecimal(bondMap.get("GEN_SUBS_MINI_AMT_FOR").toString()).multiply(rate).multiply(new BigDecimal(100)).divide(totalAmt, 8, BigDecimal.ROUND_HALF_UP);
			  bondMap.put("INV_PERCENT", pct);
			  usedPct = usedPct.add(pct);
			  lastTotalAmt = lastTotalAmt.subtract(new BigDecimal(bondMap.get("GEN_SUBS_MINI_AMT_FOR").toString()).multiply(rate).setScale(0, BigDecimal.ROUND_HALF_UP));
		  }
	  }
	  
	  for(Map<String, Object> tempMap : tempList) {
		  BigDecimal pct = (new BigDecimal(tempMap.get("INV_PERCENT").toString()).multiply(new BigDecimal(100).subtract(usedPct))).divide(noBondTotalPct, 8, BigDecimal.ROUND_HALF_UP);
		  tempMap.put("INV_PERCENT", pct);
		  tempMap.put("INV_TYPE", "1");
	  }
	  tempList.addAll(bondList);
	  prodList.clear();
	  prodList.addAll(tempList);
  }
  
  // 懶得抽 先跟 240寫一樣的，有時間再抽到 UTILS
  public Map<String,Object> getCustRisKPct(DataAccessManager dam) throws DAOException, JBranchException {
  	Map<String, Object> custRiskPctMap = new HashMap<String, Object>();
  	QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
  	qc.setQueryString(getCustRiskPctSQL());
  	List<Map<String, Object>> resultList = dam.exeQuery(qc);
  	if(CollectionUtils.isNotEmpty(resultList)) {
  		for(Map<String, Object> map : resultList) {
  			custRiskPctMap.put(ObjectUtils.toString(map.get("CUST_RISK_ATR")), map.get("VOLATILITY"));
  		}
  	}
  	return custRiskPctMap;
  }
  
  private final static String getCustRiskPctSQL() {
  	StringBuffer sb = new StringBuffer();
  	sb.append(" select body.cust_risk_atr CUST_RISK_ATR, body.volatility VOLATILITY , body.reinv_stock_vol stockPct, 100-body.reinv_stock_vol fixedPct"); 
  	sb.append(" from TBFPS_CUSTRISK_VOLATILITY_HEAD head");
  	sb.append(" inner join TBFPS_CUSTRISK_VOLATILITY body on head.param_no = body.param_no");
  	sb.append(" where head.status = 'A'");
  	return sb.toString();
  }
}
