package com.systex.jbranch.app.server.fps.fps230;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FPS230Prod {

  // INS
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getFormatedIns(DataAccessManager dam, Map<String, Map<String, Object>> insList)
      throws DAOException, JBranchException {
    String[] keys = insList.keySet().toArray(new String[insList.size()]);
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" WITH");
    sb.append(" PARAM AS (");
    sb.append("   SELECT");
    sb.append("   CASE WHEN PARAM_CODE = '4' THEN 10000");
    sb.append("     WHEN PARAM_CODE = '3' THEN 1000");
    sb.append("     WHEN PARAM_CODE = '2' THEN 100");
    sb.append("     ELSE 1 END AS INS_UNIT,");
    sb.append("   PARAM_CODE");
    sb.append("   FROM TBSYSPARAMETER");
    sb.append("   WHERE PARAM_TYPE= 'INS.UNIT'),");
    sb.append(" SAVING AS (");
    sb.append("   SELECT B.KEY_NO, B.POLICY_AMT_MIN, C.PRD_UNIT,");
    sb.append("   B.POLICY_AMT_MIN*PARAM.INS_UNIT AS BASE_RESULT");
    sb.append("   FROM TBINS_PARA_HEADER A");
    sb.append("   INNER JOIN TBPRD_INS_SUGGEST B ON A.PARA_NO = B.PARA_NO");
    sb.append("   INNER JOIN TBPRD_INS C ON B.KEY_NO = C.KEY_NO");
    sb.append("   LEFT JOIN PARAM ON PARAM.PARAM_CODE = C.PRD_UNIT");
    sb.append("   WHERE A.PARA_TYPE = '5'");
    sb.append("   AND A.STATUS = 'A'");
    sb.append("   AND A.EFFECT_DATE <= SYSDATE");
    sb.append("   AND NVL(A.EXPIRY_DATE, SYSDATE) >= TRUNC(SYSDATE)");
    sb.append(" )");
    sb.append(" SELECT");
    sb.append(" DISTINCT BASE.PRD_ID,");
    sb.append(" BASE.INSPRD_NAME AS PRD_CNAME,");
    sb.append(" BASE.CURR_CD AS CURRENCY_TYPE,");
    sb.append(" CASE WHEN BASE.PRD_RANK IS NOT NULL THEN 'Y'");
    sb.append("   ELSE 'N' END AS MAIN_PRD,");
    sb.append(" BASE.INS_TYPE,");
    sb.append(" FIRST_VALUE(BASE.KEY_NO) OVER (PARTITION BY BASE.PRD_ID ORDER BY BASE.KEY_NO) AS KEY_NO,");
    sb.append(" CASE WHEN INSINFO.BASE_AMT_OF_PURCHASE IS NULL THEN SAVING.BASE_RESULT");
    sb.append(" ELSE INSINFO.BASE_AMT_OF_PURCHASE END AS GEN_SUBS_MINI_AMT_FOR,");
    sb.append(" NULL AS SML_SUBS_MINI_AMT_FOR,");
    sb.append(" BASE.PRD_UNIT AS PRD_UNIT");
    sb.append(" CASE BASE.INS_TYPE WHEN '1' THEN '1'");
    sb.append("   ELSE '3' END AS INV_PRD_TYPE,");
    sb.append(" CASE BASE.INS_TYPE WHEN '1' THEN '3'");
    sb.append("   ELSE NULL END AS INV_PRD_TYPE_2,");
    sb.append(" 'INS' AS PTYPE,");
    sb.append(" 0 AS INV_PERCENT,");
    sb.append(" NULL AS RISK_TYPE,");
    sb.append(" NULL AS CIS_3M,");
    sb.append(" NULL AS FUND_TYPE,");
    sb.append(" NULL AS FUND_TYPE_NAME,");
    sb.append(" NULL AS MF_MKT_CAT,");
    sb.append(" NULL AS NAME,");
    sb.append(" FROM TBPRD_INS BASE");
    sb.append(" LEFT JOIN TBPRD_INSINFO INSINFO ON BASE.PRD_ID = INSINFO.PRD_ID");
    sb.append(" LEFT JOIN SAVING ON SAVING.KEY_NO = BASE.KEY_NO");
    sb.append(" WHERE BASE.INS_TYPE IN ('1', '2')");
    sb.append(" AND BASE.PRD_ID IN :prdIDs");

    qc.setObject("prdIDs", keys);
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> queryList = dam.exeQuery(qc);

    // merge map
    for (Map<String, Object> ins : queryList) {
      String insPrdID = ins.get("PRD_ID").toString();
      Map<String, Object> insMap = new HashedMap();
      insMap.putAll(insList.get(insPrdID));
      insMap.putAll(ins);
      result.add(insMap);
    }
    return result;
  }

  // MFD
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getFormatedMFD(DataAccessManager dam, Map<String, Map<String, Object>> mfdList)
      throws DAOException, JBranchException {
    String[] keys = mfdList.keySet().toArray(new String[mfdList.size()]);
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" WITH");
    sb.append(" GEN AS (");
    sb.append("   SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_1'),");
    sb.append(" SML AS (");
    sb.append("   SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_2'),");
    sb.append(" SYSPAR AS (");
    sb.append("   SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FPS.FUND_TYPE'),");
    sb.append(" AREA AS (");
    sb.append("   SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.MKT_TIER1'),");
    sb.append(" TREND AS (");
    sb.append("   SELECT TRD.TYPE, TRD.TREND");
    sb.append("   FROM TBFPS_MARKET_TREND TR");
    sb.append("   LEFT JOIN TBFPS_MARKET_TREND_DETAIL TRD ON TR.PARAM_NO = TRD.PARAM_NO");
    sb.append("   WHERE TR.STATUS = 'A')");
    sb.append(" SELECT");
    sb.append(" '3' AS INV_PRD_TYPE,");
    sb.append(" NULL AS INV_PRD_TYPE_2,");
    sb.append(" 'MFD' AS PTYPE,");
    sb.append(" 0 AS INV_PERCENT,");
    sb.append(" FU.PRD_ID AS PRD_ID,");
    sb.append(" CASE WHEN SYSDATE BETWEEN FUIN.MAIN_PRD_SDATE AND FUIN.MAIN_PRD_EDATE THEN 'Y'");
    sb.append("   WHEN SYSDATE BETWEEN FUIN.RAISE_FUND_SDATE AND FUIN.RAISE_FUND_EDATE THEN 'Y'");
    sb.append(" ELSE 'N' END AS MAIN_PRD,");
    sb.append(" FU.FUND_CNAME AS PRD_CNAME,");
    sb.append(" FU.RISKCATE_ID AS RISK_TYPE,");
    sb.append(" FU.CURRENCY_STD_ID AS CURRENCY_TYPE,");
    sb.append(" TREND.TREND AS CIS_3M,");
    sb.append(" TO_NUMBER(GEN.PARAM_NAME) AS GEN_SUBS_MINI_AMT_FOR,");
    sb.append(" TO_NUMBER(SML.PARAM_NAME) AS SML_SUBS_MINI_AMT_FOR,");
    sb.append(" NULL AS PRD_UNIT,");
    sb.append(" FU.FUND_TYPE,");
    sb.append(" SYSPAR.PARAM_NAME AS FUND_TYPE_NAME,");
    sb.append(" FU.INV_AREA AS MF_MKT_CAT,");
    sb.append(" AREA.PARAM_NAME AS NAME,");
    sb.append(" NULL AS KEY_NO");
    sb.append(" FROM TBPRD_FUND FU");
    sb.append(" LEFT JOIN TBPRD_FUNDINFO FUIN ON FUIN.PRD_ID = FU.PRD_ID");
    sb.append(" LEFT JOIN GEN ON GEN.PARAM_CODE = FU.CURRENCY_STD_ID");
    sb.append(" LEFT JOIN SML ON SML.PARAM_CODE = FU.CURRENCY_STD_ID");
    sb.append(" LEFT JOIN SYSPAR ON SYSPAR.PARAM_CODE = FU.FUND_TYPE");
    sb.append(" LEFT JOIN TREND ON TREND.TYPE = FU.INV_TARGET");
    sb.append(" LEFT JOIN AREA ON AREA.PARAM_CODE = TREND.TYPE");
    sb.append(" WHERE FU.PRD_ID IN :prdIDs");

    qc.setObject("prdIDs", keys);
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> queryList = dam.exeQuery(qc);

    // merge map
    for (Map<String, Object> mfd : queryList) {
      String mfdPrdID = mfd.get("PRD_ID").toString();
      Map<String, Object> mfdMap = new HashedMap();
      mfdMap.putAll(mfdList.get(mfdPrdID));
      mfdMap.putAll(mfd);
      result.add(mfdMap);
    }
    return result;
  }

  // ETF
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getFormatedETF(DataAccessManager dam, Map<String, Map<String, Object>> etfList)
      throws DAOException, JBranchException {
    String[] keys = etfList.keySet().toArray(new String[etfList.size()]);
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" WITH");
    sb.append(" GEN AS (");
    sb.append("   SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_1'),");
    sb.append(" ETF AS(");
    sb.append("   SELECT PRD_ID, TXN_AMOUNT, INV_TARGET FROM TBPRD_ETF WHERE IS_SALE IS NULL),");
    sb.append(" TREND AS (");
    sb.append("   SELECT TRD.TYPE, TRD.TREND");
    sb.append("   FROM TBFPS_MARKET_TREND TR");
    sb.append("   LEFT JOIN TBFPS_MARKET_TREND_DETAIL TRD ON TR.PARAM_NO = TRD.PARAM_NO");
    sb.append("   WHERE TR.STATUS = 'A')");
    sb.append(" SELECT");
    sb.append(" '3' AS INV_PRD_TYPE,");
    sb.append(" NULL AS INV_PRD_TYPE_2,");
    sb.append(" 'ETF' AS PTYPE,");
    sb.append(" 0 AS INV_PERCENT,");
    sb.append(" MAIN.PRD_ID AS PRD_ID,");
    sb.append(" CASE WHEN MAIN.PRD_RANK IS NOT NULL THEN 'Y'");
    sb.append("   ELSE 'N' END AS MAIN_PRD,");
    sb.append(" MAIN.ETF_CNAME AS PRD_CNAME,");
    sb.append(" MAIN.RISKCATE_ID AS RISK_TYPE,");
    sb.append(" MAIN.CURRENCY_STD_ID AS CURRENCY_TYPE,");
    sb.append(" TREND.TREND AS CIS_3M,");
    sb.append(" TO_NUMBER(MAIN.TXN_AMOUNT) AS GEN_SUBS_MINI_AMT_FOR,");
    sb.append(" NULL AS SML_SUBS_MINI_AMT_FOR,");
    sb.append(" NULL AS PRD_UNIT,");
    sb.append(" NULL AS FUND_TYPE,");
    sb.append(" NULL AS FUND_TYPE_NAME,");
    sb.append(" NULL AS MF_MKT_CAT,");
    sb.append(" NULL AS NAME,");
    sb.append(" NULL AS KEY_NO");
    sb.append(" FROM TBPRD_ETF MAIN");
    sb.append(" LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = MAIN.PRD_ID");
    sb.append(" LEFT JOIN TREND ON TREND.TYPE = MAIN.INV_TARGET");
    sb.append(" LEFT JOIN GEN ON GEN.PARAM_CODE = FU.CURRENCY_STD_ID");
    sb.append(" WHERE MAIN.PRD_ID IN :prdIDs");

    qc.setObject("prdIDs", keys);
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> queryList = dam.exeQuery(qc);

    // merge map
    for (Map<String, Object> etf : queryList) {
      String etfPrdID = etf.get("PRD_ID").toString();
      Map<String, Object> etfMap = new HashedMap();
      etfMap.putAll(etfList.get(etfPrdID));
      etfMap.putAll(etf);
      result.add(etfMap);
    }
    return result;
  }

  // BND
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getFormatedBND(DataAccessManager dam, Map<String, Map<String, Object>> bndList)
      throws DAOException, JBranchException {
    String[] keys = bndList.keySet().toArray(new String[bndList.size()]);
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT");
    sb.append(" '2' AS INV_PRD_TYPE,");
    sb.append(" NULL AS INV_PRD_TYPE_2,");
    sb.append(" 'BND' AS PTYPE,");
    sb.append(" 0 AS INV_PERCENT,");
    sb.append(" MAIN.PRD_ID AS PRD_ID,");
    // sb.append(" CASE WHEN MAIN.PRD_RANK IS NOT NULL THEN 'Y'");
    // sb.append("   ELSE 'N' END AS MAIN_PRD,");
    sb.append(" NULL AS MAIN_PRD,");
    sb.append(" MAIN.BOND_CNAME AS PRD_CNAME,");
    sb.append(" MAIN.RISKCATE_ID AS RISK_TYPE,");
    sb.append(" MAIN.CURRENCY_STD_ID AS CURRENCY_TYPE,");
    sb.append(" NULL AS CIS_3M,");
    sb.append(" TO_NUMBER(INFO.BASE_AMT_OF_PURCHASE) AS GEN_SUBS_MINI_AMT_FOR,");
    sb.append(" TO_NUMBER(INFO.BASE_AMT_OF_BUYBACK) AS SML_SUBS_MINI_AMT_FOR,");
    sb.append(" INFO.UNIT_OF_VALUE AS PRD_UNIT,");
    sb.append(" NULL AS FUND_TYPE,");
    sb.append(" NULL AS FUND_TYPE_NAME,");
    sb.append(" NULL AS MF_MKT_CAT,");
    sb.append(" NULL AS NAME,");
    sb.append(" NULL AS KEY_NO");
    sb.append(" FROM TBPRD_BOND MAIN");
    sb.append(" LEFT JOIN TBPRD_BONDINFO INFO ON INFO.PRD_ID = MAIN.PRD_ID");
    sb.append(" WHERE MAIN.PRD_ID IN :prdIDs");

    qc.setObject("prdIDs", keys);
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> queryList = dam.exeQuery(qc);

    // merge map
    for (Map<String, Object> bnd : queryList) {
      String bndPrdID = bnd.get("PRD_ID").toString();
      Map<String, Object> bndMap = new HashedMap();
      bndMap.putAll(bndList.get(bndPrdID));
      bndMap.putAll(bnd);
      result.add(bndMap);
    }
    return result;
  }

  // SN
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getFormatedSN(DataAccessManager dam, Map<String, Map<String, Object>> snList)
      throws DAOException, JBranchException {
    String[] keys = snList.keySet().toArray(new String[snList.size()]);
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT");
    sb.append(" '2' AS INV_PRD_TYPE,");
    sb.append(" NULL AS INV_PRD_TYPE_2,");
    sb.append(" 'SN' AS PTYPE,");
    sb.append(" 0 AS INV_PERCENT,");
    sb.append(" MAIN.PRD_ID AS PRD_ID,");
    sb.append(" CASE WHEN PABTH_UTIL.FC_GETBUSIDAY(SYSDATE, 'TWD', 3) > INFO.INV_EDATE THEN 'Y'");
    sb.append("   ELSE 'N' END AS MAIN_PRD,");
    sb.append(" MAIN.SN_CNAME AS PRD_CNAME,");
    sb.append(" MAIN.RISKCATE_ID AS RISK_TYPE,");
    sb.append(" MAIN.CURRENCY_STD_ID AS CURRENCY_TYPE,");
    sb.append(" NULL AS CIS_3M,");
    sb.append(" TO_NUMBER(INFO.BASE_AMT_OF_PURCHASE) AS GEN_SUBS_MINI_AMT_FOR,");
    sb.append(" TO_NUMBER(INFO.BASE_AMT_OF_BUYBACK) AS SML_SUBS_MINI_AMT_FOR,");
    sb.append(" INFO.UNIT_OF_VALUE AS PRD_UNIT,");
    sb.append(" NULL AS FUND_TYPE,");
    sb.append(" NULL AS FUND_TYPE_NAME,");
    sb.append(" NULL AS MF_MKT_CAT,");
    sb.append(" NULL AS NAME,");
    sb.append(" NULL AS KEY_NO");
    sb.append(" FROM TBPRD_SN MAIN");
    sb.append(" LEFT JOIN TBPRD_SNINFO INFO ON INFO.PRD_ID = MAIN.PRD_ID");
    sb.append(" WHERE MAIN.PRD_ID IN :prdIDs");

    qc.setObject("prdIDs", keys);
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> queryList = dam.exeQuery(qc);

    // merge map
    for (Map<String, Object> sn : queryList) {
      String snPrdID = sn.get("PRD_ID").toString();
      Map<String, Object> snMap = new HashedMap();
      snMap.putAll(snList.get(snPrdID));
      snMap.putAll(sn);
      result.add(snMap);
    }
    return result;
  }

  // SI
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getFormatedSI(DataAccessManager dam, Map<String, Map<String, Object>> siList)
      throws DAOException, JBranchException {
    String[] keys = siList.keySet().toArray(new String[siList.size()]);
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT");
    sb.append(" '2' AS INV_PRD_TYPE,");
    sb.append(" NULL AS INV_PRD_TYPE_2,");
    sb.append(" 'SN' AS PTYPE,");
    sb.append(" 0 AS INV_PERCENT,");
    sb.append(" MAIN.PRD_ID AS PRD_ID,");
    sb.append(" CASE WHEN PABTH_UTIL.FC_GETBUSIDAY(SYSDATE, 'TWD', 3) > INFO.INV_EDATE THEN 'Y'");
    sb.append("   ELSE 'N' END AS MAIN_PRD,");
    sb.append(" MAIN.SI_CNAME AS PRD_CNAME,");
    sb.append(" MAIN.RISKCATE_ID AS RISK_TYPE,");
    sb.append(" MAIN.CURRENCY_STD_ID AS CURRENCY_TYPE,");
    sb.append(" NULL AS CIS_3M,");
    sb.append(" TO_NUMBER(INFO.BASE_AMT_OF_PURCHASE) AS GEN_SUBS_MINI_AMT_FOR,");
    sb.append(" NULL AS SML_SUBS_MINI_AMT_FOR,");
    sb.append(" INFO.UNIT_OF_VALUE AS PRD_UNIT,");
    sb.append(" NULL AS FUND_TYPE,");
    sb.append(" NULL AS FUND_TYPE_NAME,");
    sb.append(" NULL AS MF_MKT_CAT,");
    sb.append(" NULL AS NAME,");
    sb.append(" NULL AS KEY_NO");
    sb.append(" FROM TBPRD_SI MAIN");
    sb.append(" LEFT JOIN TBPRD_SIINFO INFO ON INFO.PRD_ID = MAIN.PRD_ID");
    sb.append(" WHERE MAIN.PRD_ID IN :prdIDs");

    qc.setObject("prdIDs", keys);
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> queryList = dam.exeQuery(qc);

    // merge map
    for (Map<String, Object> si : queryList) {
      String siPrdID = si.get("PRD_ID").toString();
      Map<String, Object> siMap = new HashedMap();
      siMap.putAll(siList.get(siPrdID));
      siMap.putAll(si);
      result.add(siMap);
    }
    return result;
  }

  // get his products
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> hisQuery(DataAccessManager dam, String planID)
      throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT");
    sb.append(" MAIN.PLAN_ID,");
    sb.append(" MAIN.SEQNO,");
    sb.append(" MAIN.PRD_ID,");
    sb.append(" MAIN.PRD_TYPE AS PTYPE,");
    sb.append(" MAIN.PRD_NAME AS PRD_CNAME,");
    sb.append(" MAIN.RISKCATE_ID AS RISK_TYPE,");
    sb.append(" MAIN.PROD_CURR AS CURRENCY_TYPE,");
    sb.append(" MAIN.TRUST_CURR,");
    sb.append(" MAIN.PURCHASE_ORG_AMT,");
    sb.append(" MAIN.PURCHASE_TWD_AMT,");
    sb.append(" MAIN.PORTFOLIO_RATIO AS INV_PERCENT,");
    sb.append(" MAIN.LIMIT_ORG_AMT,");
    sb.append(" MAIN.PORTFOLIO_TYPE,");
    sb.append(" MAIN.INV_PRD_TYPE_2,");
    sb.append(" MAIN.INV_PRD_TYPE,");
    sb.append(" MAIN.TXN_TYPE,");
    sb.append(" MAIN.INV_TYPE,");
    sb.append(" MAIN.INVENTORY_ORG_AMT AS NOW_AMT,");
    sb.append(" MAIN.INVENTORY_TWD_AMT AS NOW_AMT_TWD");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_INV MAIN");
    sb.append(" WHERE PLAN_ID = :planID");

    qc.setObject("planID", planID);
    qc.setQueryString(sb.toString());

    List<Map<String, Object>> result = dam.exeQuery(qc);
    return result;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getHisStock(DataAccessManager dam, String custID)
      throws DAOException, JBranchException {
    // CRM.AST_ALLPRD_TYPE : AST code
    // '01','02','04','05', 儲蓄
    // '07','08','09', 基金 ETF
    // '10','15', (債券 SI) SN
    // '14' 保險
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT");
    sb.append(" CASE ");
    sb.append("   AST_TYPE ");
    sb.append("   WHEN '01' THEN '1'");
    sb.append("   WHEN '02' THEN '1'");
    sb.append("   WHEN '04' THEN '1'");
    sb.append("   WHEN '05' THEN '1'");
    sb.append("   WHEN '07' THEN '3'");
    sb.append("   WHEN '08' THEN '3'");
    sb.append("   WHEN '09' THEN '3'");
    sb.append("   WHEN '12' THEN '3'");
    sb.append("   WHEN '10' THEN '2'");
    sb.append("   WHEN '15' THEN '2'");
    sb.append("   WHEN '14' THEN NULL");
    sb.append("   END AS INV_PRD_TYPE,");
    sb.append(" CASE ");
    sb.append("   WHEN MAIN.AST_TYPE IN ('01','02') THEN '1'");
    sb.append("   WHEN MAIN.AST_TYPE IN ('04','05') THEN '2'");
    sb.append(" ELSE NULL END AS INV_PRD_TYPE_2,");
    sb.append(" CASE ");
    sb.append("   WHEN AST_TYPE IN ('01','02','04','05') THEN NULL");
    sb.append("   WHEN AST_TYPE IN ('14') THEN 'INS'");
    sb.append("   ELSE PRD.PTYPE END AS PTYPE,");
    sb.append(" MAIN.AST_TYPE,");
    sb.append(" MAIN.PROD_ID AS PRD_ID,");
    sb.append(" MAIN.CUR_ID AS TRUST_CURR,");
    sb.append(" MAIN.NOW_AMT,");
    sb.append(" MAIN.NOW_AMT_TWD");
    sb.append(" FROM MVFPS_AST_ALLPRD_DETAIL MAIN");
    sb.append(" LEFT JOIN VWPRD_MASTER PRD ON PRD.PRD_ID = MAIN.PROD_ID ");
    sb.append("   AND PRD.PTYPE != 'STOCK' AND PRD.PTYPE != 'INS'");
    sb.append(" WHERE MAIN.CUST_ID = :CUSTID");
    sb.append(" AND MAIN.AST_TYPE IN ('01','02','04','05', '07','08','09','12', '10','15', '14')");
    sb.append(" AND MAIN.NOW_AMT != 0");


    qc.setObject("custID", custID);
    qc.setQueryString(sb.toString());

    List<Map<String, Object>> result = dam.exeQuery(qc);
    return result;
  }

  // get init model
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> modelQuery(DataAccessManager dam, String riskType, String invType)
      throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" WITH");
    sb.append(" SAVING AS (");
    sb.append(" SELECT '1' AS INV_PRD_TYPE, '3' AS INV_PRD_TYPE_2, TBPRD_INS.* FROM TBPRD_INS");
    sb.append(" WHERE INS_TYPE = '1' AND PRD_RANK IS NOT NULL ORDER BY PRD_RANK FETCH FIRST ROW ONLY)");
    sb.append(" SELECT");
    sb.append(" DE.INV_PRD_TYPE,");
    sb.append(" DE.INV_PRD_TYPE_2,");
    sb.append(" CASE WHEN DE.INV_PRD_TYPE='1' AND DE.INV_PRD_TYPE_2='3' THEN 'INS'");
    sb.append("      ELSE DE.PRD_TYPE END AS PTYPE,");
    sb.append(" CASE WHEN DE.INV_PRD_TYPE='1' AND DE.INV_PRD_TYPE_2='3' THEN SAVING.PRD_ID");
    sb.append("      ELSE DE.PRD_ID END AS PRD_ID,");
    sb.append(" CASE WHEN DE.INV_PRD_TYPE='1' AND DE.INV_PRD_TYPE_2='3' THEN SAVING.CURR_CD");
    sb.append("      ELSE DE.CURRENCY_STD_ID END AS CURRENCY_TYPE,");
    sb.append(" DE.INV_PERCENT");
    sb.append(" FROM TBFPS_MODEL_ALLOCATION_HEAD HE");
    sb.append(" INNER JOIN TBFPS_MODEL_ALLOCATION DE ON DE.PARAM_NO = HE.PARAM_NO");
    sb.append(" LEFT JOIN SAVING ON DE.INV_PRD_TYPE_2 = SAVING.INV_PRD_TYPE_2 ");
    sb.append("     AND DE.INV_PRD_TYPE = SAVING.INV_PRD_TYPE");
    sb.append(" WHERE HE.STATUS = 'A'");
    sb.append(" AND HE.SETTING_TYPE = '2'");
    sb.append(" AND HE.EFFECT_START_DATE <= SYSDATE + 1");
    sb.append(" AND DE.CUST_RISK_ATR = :riskType");

    if (StringUtils.isNotBlank(invType)) {
      sb.append(" AND DE.INV_PRD_TYPE = :invType ");
      qc.setObject("invType", invType);
    }

    qc.setObject("riskType", riskType);
    qc.setQueryString(sb.toString());

    List<Map<String, Object>> result = dam.exeQuery(qc);
    return result;
  }

  /**
   * SI SN BND MFD ETF INS
   */

  public List<Map<String, Object>> prodFormatter(DataAccessManager dam, List<Map<String, Object>> prods, String queryType)
      throws DAOException, JBranchException {
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

    switch (queryType) {
    case "model":
      result.addAll(getFormatedIns(dam, filterProdMap(prods, "PTYPE", "INS")));
      result.addAll(getFormatedETF(dam, filterProdMap(prods, "PTYPE", "ETF")));
      result.addAll(getFormatedMFD(dam, filterProdMap(prods, "PTYPE", "MFD")));
      result.addAll(filterProd(prods, "PTYPE", null));
      break;
    case "history":
      result.addAll(getFormatedIns(dam, filterProdMap(prods, "PTYPE", "INS")));
      result.addAll(getFormatedETF(dam, filterProdMap(prods, "PTYPE", "ETF")));
      result.addAll(getFormatedMFD(dam, filterProdMap(prods, "PTYPE", "MFD")));
      result.addAll(getFormatedSI(dam, filterProdMap(prods, "PTYPE", "INS")));
      result.addAll(getFormatedSN(dam, filterProdMap(prods, "PTYPE", "ETF")));
      result.addAll(getFormatedBND(dam, filterProdMap(prods, "PTYPE", "MFD")));
      break;
    case "stock":
      result.addAll(getFormatedIns(dam, filterProdMap(prods, "AST_TYPE", "14")));
      result.addAll(getFormatedETF(dam, filterProdMap(prods, "AST_TYPE", "ETF")));
      result.addAll(getFormatedMFD(dam, filterProdMap(prods, "AST_TYPE", "MFD")));
      result.addAll(getFormatedSI(dam, filterProdMap(prods, "AST_TYPE", "INS")));
      result.addAll(getFormatedSN(dam, filterProdMap(prods, "AST_TYPE", "ETF")));
      result.addAll(getFormatedBND(dam, filterProdMap(prods, "AST_TYPE", "MFD")));
      break;
    default:
      break;
    }

    return result;
  }

  private List<Map<String, Object>> filterProd(List<Map<String, Object>> prods, String key, String val) {
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

    for (Map<String, Object> prod : prods) {
      if (prod.get(key).toString().equals(val)) {
        result.add(prod);
      }
    }

    return result;
  }

  private Map<String, Map<String, Object>> filterProdMap(List<Map<String, Object>> prods, String key, String val) {
    Map<String, Map<String, Object>> result = new HashedMap();

    for (Map<String, Object> prod : prods) {
      if (prod.get(key) == null) {
        if (val == null) {
          result.put(prod.get("PRD_ID").toString(), prod);
        }
      } else if (prod.get(key).toString().equals(val)) {
        result.put(prod.get("PRD_ID").toString(), prod);
      }
    }

    return result;
  }

}
