package com.systex.jbranch.app.server.fps.fps325;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.fpsjlb.FPSJLB;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtilsOutputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("fps325")
@Scope("request")
public class FPS325 extends FubonWmsBizLogic {

  @Autowired
  @Qualifier("fpsjlb")
  private FPSJLB fpsjlb;

  public void trialBalance(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
    FPS325InputVO inputVO = (FPS325InputVO) body;
    FPS325OutputVO outputVO = new FPS325OutputVO();
    DataAccessManager dam = this.getDataAccessManager();

    // 上限參數
    Double upper = getEffectFrontier("UPPER");
    // 下限參數
    Double lower = getEffectFrontier("LOWER");

    List<FPS325PrdInputVO> prdList = inputVO.getPrdList();
    int cntProds = 0;
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    Map<String, FPS325PrdInputVO> m = new HashMap();

    String[] stringArray = null;
    Set<String> prdArr = new HashSet<String>();
    for (FPS325PrdInputVO prd : prdList) {
      HashMap<String, Object> tempMap = new HashMap<String, Object>();
      tempMap.put("PRD_ID", prd.getPRD_ID());
      tempMap.put("PRD_TYPE", prd.getPTYPE());
      tempMap.put("TARGETS", prd.getTARGETS());
      list.add(tempMap);

      m.put(prd.getPRD_ID(), prd);

      if ("INS".equals(prd.getPTYPE())) {
        prdArr.addAll(Arrays.asList(ObjectUtils.toString(prd.getTARGETS()).split("/")));
      } else {
        prdArr.add(prd.getPRD_ID());
      }
      stringArray = (String[]) Arrays.copyOf(prdArr.toArray(), prdArr.toArray().length, String[].class);
    }

    // check first if RETURN_M has data in 1 year
    String[] invalids = FPSUtils.checkValidYear(dam, list, 3, null);
    if (invalids.length > 0) {
      FPSUtilsOutputVO invalidOutputVO = new FPSUtilsOutputVO();
      invalidOutputVO.setError(true);
      invalidOutputVO.setInvalidPrdID(invalids);
      this.sendRtnObject(invalidOutputVO);
      return;
    }

    try {
      GenericMap result = fpsjlb.getPortEffFrontier(list, 10, 100, lower, upper);

      // efficient frontier
      List<Map<String, Object>> efList = (List<Map<String, Object>>) result.get("EfList");
      // market portfolio
      // List<Map<String, Object>> cmlList = (List<Map<String, Object>>)
      // result.get("CmlList");
      for (Map<String, Object> map : efList) {
        List<Map<String, Object>> PfoAllocList = (List<Map<String, Object>>) map.get("PfoAllocList");

        List<Map<String, Object>> fpsjlbList = new ArrayList<Map<String, Object>>();
        BigDecimal weightTemp = new BigDecimal(100);
        for (Map<String, Object> PfoAllocMap : PfoAllocList) {
          String prdId = PfoAllocMap.get("PRD_ID").toString();
          BigDecimal weight = new BigDecimal(PfoAllocMap.get("WEIGHT").toString());
          BigDecimal storeNtd = inputVO.getONETIME().multiply(weight).setScale(4, BigDecimal.ROUND_HALF_UP);
          BigDecimal storeRaw;
          if (m.get(prdId).getCURRENCY_TYPE().equals("TWD")) {// NTD 改為 TWD
            storeRaw = storeNtd;
          } else {
            storeRaw = storeNtd.divide(getRate(m.get(prdId).getCURRENCY_TYPE()), 2, RoundingMode.HALF_UP);
          }

          // fubon 金額不用小數位
          // if
          // (Arrays.asList("TWD,JPY".split(",")).indexOf(m.get(prdId).getCURRENCY_TYPE())
          // > -1) {// NTD 改為 TWD

          storeNtd = storeNtd.setScale(0, BigDecimal.ROUND_HALF_UP);
          storeRaw = storeRaw.setScale(0, BigDecimal.ROUND_HALF_UP);

          HashMap<String, Object> fpsjlbMap = new HashMap<String, Object>();
          fpsjlbMap.put("PRD_ID", prdId);
          fpsjlbMap.put("PRD_TYPE", "MFD");
          fpsjlbMap.put("WEIGHT", weight);

          fpsjlbList.add(fpsjlbMap);

          PfoAllocMap.put("WEIGHT", weight.setScale(4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)));
          PfoAllocMap.put("STORE_NTD", storeNtd);
          PfoAllocMap.put("STORE_RAW", storeRaw);
          weightTemp = weightTemp.subtract(weight.setScale(4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)));
        }

        if (weightTemp.compareTo(new BigDecimal(0.0000)) != 0) {
          PfoAllocList.get(0).put("WEIGHT",
              new BigDecimal(PfoAllocList.get(0).get("WEIGHT").toString()).add(weightTemp));
        }

        // ResultObj resultObj = fpsjlb.getPortfolioVolatility(fpsjlbList);
        // map.put("VOLATILITY", round(resultObj.getResultArray()[0][0], 4));
        // map.put("VOLATILITY", 1.0);
        map.put("yearStd", round(Double.parseDouble(map.get("yearStd").toString()), 4));
        map.put("yearRtn", round(Double.parseDouble(map.get("yearRtn").toString()), 4));
      }

      // cmlList.get(1).put("yearRtn",
      // round(Double.parseDouble(cmlList.get(1).get("yearRtn").toString()),
      // 4));

      outputVO.setEfList(efList);
      // outputVO.setCmlList(cmlList);
      outputVO.setMaxPfoRtn(round(Double.parseDouble(result.get("MaxPfoRtn").toString()), 4));
      outputVO.setMinPfoRtn(round(Double.parseDouble(result.get("MinPfoRtn").toString()), 4));
      outputVO.setPeriodStartDate(result.get("periodStartDate").toString());
      outputVO.setPeriodEndDate(result.get("periodEndDate").toString());
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
      throw new APException("ehl_01_FPS325_001"); // 標的共同區間小於1年，無法進行最佳配置試算
    }

    List<Map<String, Object>> outputList = new ArrayList<Map<String, Object>>();
    for (FPS325PrdInputVO prd : prdList) {
      HashMap<String, Object> tempMap = new HashMap<String, Object>();
      tempMap.put("PRD_ID", prd.getPRD_ID());
      tempMap.put("PRD_CNAME", prd.getPRD_CNAME());
      tempMap.put("INV_PERCENT", prd.getINV_PERCENT());
      tempMap.put("LowMoney", prd.getLowMoney());
      outputList.add(tempMap);
    }
    outputVO.setOutputList(outputList);

    this.sendRtnObject(outputVO);
  }

  public void checkVolatility(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
    FPS325InputVO inputVO = (FPS325InputVO) body;
    FPS325OutputVO outputVO = new FPS325OutputVO();

    String riskAttr = inputVO.getRISK_ATTR();
    Double volatility = inputVO.getVOLATILITY();

    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT a.VOLATILITY ");
    sb.append("FROM TBFPS_CUSTRISK_VOLATILITY a ");
    sb.append("JOIN TBFPS_CUSTRISK_VOLATILITY_HEAD b ");
    sb.append("ON a.param_no           = b.param_no ");
    sb.append("WHERE a.setting_type    = '1' ");
    sb.append("AND a.vol_type          = '1' ");
    sb.append("AND a.cust_risk_atr     = '" + riskAttr + "' ");
    sb.append("AND b.status            = 'A' ");
    sb.append("AND b.effect_start_date = ");
    sb.append("  (SELECT MAX(effect_start_date) ");
    sb.append("  FROM TBFPS_CUSTRISK_VOLATILITY_HEAD ");
    sb.append("  WHERE status           = 'A' ");
    sb.append("  AND effect_start_date <=sysdate ");
    sb.append("  ) ");
    qc.setQueryString(sb.toString());

    List<Map> list = dam.exeQuery(qc);
    Double vol = Double.parseDouble(list.get(0).get("VOLATILITY").toString());

    ArrayList outputList = new ArrayList();
    if (volatility > vol) {
      HashMap map = new HashMap();
      map.put("result", "fail");
      outputList.add(map);
      outputVO.setOutputList(outputList);
    } else {
      HashMap map = new HashMap();
      map.put("result", "success");
      outputList.add(map);
      outputVO.setOutputList(outputList);
    }

    this.sendRtnObject(outputVO);
  }

  // 取得效率前原上下限參數
  private Double getEffectFrontier(String paramCode) throws DAOException, JBranchException {
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT param_name FROM TBSYSPARAMETER WHERE param_type = 'FPS.EFFECT_FRONTIER' AND param_code = '"
        + paramCode + "' ");

    qc.setQueryString(sb.toString());

    List<Map> list = dam.exeQuery(qc);
    return Double.parseDouble(list.get(0).get("PARAM_NAME").toString());
  }

  @SuppressWarnings("unchecked")
  private BigDecimal getRate(String currencyType) throws DAOException, JBranchException {
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT BUY_RATE, SEL_RATE ");
    sb.append("FROM TBPMS_IQ053 ");
    sb.append("WHERE CUR_COD = '" + currencyType + "' ");
    sb.append("AND MTN_DATE = ");
    sb.append("  (SELECT MAX (MTN_DATE) FROM TBPMS_IQ053 WHERE CUR_COD = '" + currencyType + "' ");
    sb.append("  )  ");
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> list = dam.exeQuery(qc);

    return new BigDecimal(list.get(0).get("BUY_RATE").toString());
  }

  private double round(double v, int scale) {
    if (scale < 0) {
      throw new IllegalArgumentException("The scale must be a positive integer or zero");
    }

    BigDecimal b = new BigDecimal(Double.toString(v));
    BigDecimal one = new BigDecimal("1");

    return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
  }
}
