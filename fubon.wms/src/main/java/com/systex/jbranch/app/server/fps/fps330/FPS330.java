package com.systex.jbranch.app.server.fps.fps330;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.fpsjlb.FPSJLB;
import com.systex.jbranch.app.server.fps.fpsjlb.dao.FpsjlbDao;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtilsOutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 特定目的投資組合規劃(STEP 2)歷史績效表現
 * 
 * @author Grace
 * @since 17-11-16
 */
@Component("fps330")
@Scope("request")
public class FPS330 extends FubonWmsBizLogic {

  SimpleDateFormat sdf = new SimpleDateFormat("yyMMmmss");

  @Autowired
  @Qualifier("fpsjlb")
  private FPSJLB fpsjlb;
  @Autowired
  @Qualifier("fpsjlbDao")
  private FpsjlbDao fpsDao;

  /**
   * 取得查詢結果_波動度
   * 
   * @param body
   * @param header
   * @throws Exception
   */
  @SuppressWarnings({ "rawtypes" })
  public void inquire(Object body, IPrimitiveMap header) throws Exception {
    FPS330InputVO inputVO = (FPS330InputVO) body;
    FPS330OutputVO outputVO = new FPS330OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    FPSUtilsOutputVO invalidOutputVO = new FPSUtilsOutputVO();
    invalidOutputVO.setError(true);

    // 波動度
    // 歷史績效表現 (長條圖)
    try {
      if (inputVO.getParamList() == null || inputVO.getParamList().size() == 0) {
        List<Map<String, Object>> prdList = calVolatility(inputVO.getPlanID(), dam);
        invalidOutputVO.setInvalidPrdID(FPSUtils.checkValidYear(dam, prdList, 1, null));
        if (invalidOutputVO.getInvalidPrdID().length == 0) {
          BigDecimal volatility = FPSUtils.getStandardDeviation(dam, prdList, 120, 12);
          outputVO.setVolatility(volatility == null ? null : volatility.doubleValue());

          outputVO.setYearRateList(FPSUtils.getReturnAnnM(dam, prdList, 10, 1));
        } else {
          StringBuffer sbf = new StringBuffer();
          for (String inv : invalidOutputVO.getInvalidPrdID()) {
            sbf.append(inv);
          }
          outputVO.setErrCode(sbf.toString());
        }
      } else {
        invalidOutputVO.setInvalidPrdID(FPSUtils.checkValidYear(dam, inputVO.getParamList(), 1, null));
        if (invalidOutputVO.getInvalidPrdID().length == 0) {
          BigDecimal volatility = FPSUtils.getStandardDeviation(dam, inputVO.getParamList(), 120, 12);
          outputVO.setVolatility(volatility == null ? null : volatility.doubleValue());
          outputVO.setYearRateList(FPSUtils.getReturnAnnM(dam, inputVO.getParamList(), 10, 1));
        } else {
          StringBuffer sbf = new StringBuffer();
          for (String inv : invalidOutputVO.getInvalidPrdID()) {
            sbf.append(inv);
          }
          outputVO.setErrCode(sbf.toString());
        }
      }

      this.sendRtnObject(outputVO);
    } catch (Exception e) {
      e.printStackTrace();
      this.sendRtnObject(invalidOutputVO);
    }

  }

  // 波動度
  public List<Map<String, Object>> calVolatility(String planID, DataAccessManager dam) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(dam.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT PRD_ID, SUM((PORTFOLIO_RATIO/100)) WEIGHT, PTYPE AS PRD_TYPE, TARGETS ");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_SPP ");
    sb.append(" WHERE plan_id = :planId ");
    sb.append(" GROUP BY PRD_ID, PTYPE, TARGETS ");

    qc.setObject("planId", planID);
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> list = dam.exeQuery(qc);

    for (Map<String, Object> cell : list) {
      cell.put("WEIGHT", Double.parseDouble(cell.get("WEIGHT").toString()));
    }
    return list;
  }

}
