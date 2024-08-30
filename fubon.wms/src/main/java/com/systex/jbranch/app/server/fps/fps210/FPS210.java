package com.systex.jbranch.app.server.fps.fps210;

import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_INV_HEADVO;
import com.systex.jbranch.app.server.fps.fps200.FPS200;
import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot813.SOT813;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("fps210")
@Scope("request")
public class FPS210 extends FPS200 {
  Logger logger = LoggerFactory.getLogger(this.getClass());

  public static final String PLAN_IN_TWO_WEEKS = "PLAN_STEP";
  public static final String PLAN_EXPORTED = "2";
  public static final String PLAN_NO_EXPORT = "3";
  public static final String PLAN_INVALID = "4";
  final String PLAN_STEP = "1";

  public void inquire(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
    FPS210InputVO inputVO = (FPS210InputVO) body;
    FPS210OutputVO outputVO = new FPS210OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    String planID = inputVO.getPlanID();

    if (StringUtils.isNotBlank(inputVO.getCustID())) {
      outputVO.setCashList(getCashParam(dam, inputVO.getCustID()));
      if (StringUtils.isNotBlank(planID)) {
        outputVO.setNewFlag("N");
        outputVO.setOutputList(getCustPlan(dam, inputVO.getPlanID()));
        outputVO.setCustList(getCustAmt(dam, inputVO.getCustID()));
        outputVO.setHasInvest(hasInvest(dam, inputVO.getPlanID()));
      } else {
        outputVO.setNewFlag("Y");
        outputVO.setCustList(getCustAmt(dam, inputVO.getCustID()));
        outputVO.setOutputList(outputVO.getCustList());
      }
      outputVO.setPrevBusiDay(this.getPrevBussinessDay(dam));
      outputVO.setCashPreparePct(getCashPreparePct(dam, inputVO.getCustID()));
    } else {
      throw new APException("沒有CustID"); //
    }
    this.sendRtnObject(outputVO);
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getCustPlan(DataAccessManager dam, String planID) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT PLAN.*, (PLAN.INS_POLICY_AMT+PLAN.INS_SAV_AMT) AS INS_AMT ");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_INV_HEAD PLAN ");
    sb.append(" WHERE PLAN.PLAN_ID IN :planID ");
    qc.setObject("planID", planID);

    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getCustAmt(DataAccessManager dam, String custID) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT * ");
    sb.append(" FROM TBFPS_CUST_LIST ");
    sb.append(" WHERE CUST_ID = :custID ");
    qc.setObject("custID", custID);

    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  /**
   * 可供理財規劃上限
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws DAOException
   */
  public void getAvailableParam(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
    DataAccessManager dam = this.getDataAccessManager();
    int availableNum = 0;

    List<Map<String, Object>> resultList = getOtherPara(dam);
    if (resultList.size() > 0) {
      availableNum = Integer.parseInt(resultList.get(0).get("AVAILABLE_AMT").toString());
    }
    this.sendRtnObject(availableNum);
  }

  @SuppressWarnings("unchecked")
public List<Map<String, Object>> getCashParam(DataAccessManager dam, String custID) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" WITH MAX_DATE AS (");
    sb.append(" SELECT MAX(CREATETIME) MAX_CREATETIME FROM TBFPS_PORTFOLIO_PLAN_INV_HEAD");
    sb.append(" WHERE CUST_ID = :custID )");
    sb.append(" SELECT");
    sb.append(" LIVE_YEAR_AMT,");
    sb.append(" PREPARE_YEAR_AMT,");
    sb.append(" LN_HOUSE_AMT,");
    sb.append(" LN_CREDIT_AMT,");
    sb.append(" LN_EDCUATION_AMT,");
    sb.append(" BUY_HOUSE_AMT,");
    sb.append(" BUY_CAR_AMT,");
    sb.append(" TRAVEL_AMT,");
    sb.append(" OVERSEA_EDUCATION_AMT,");
    sb.append(" OTHER_AMT,");
    sb.append(" CASH_YEAR_AMT,");
    sb.append(" CASH_PREPARE,");
    sb.append(" LOAN_EXPENSES,");
    sb.append(" OTHER_EXPENSES,");
    sb.append(" INS_POLICY_AMT,");
    sb.append(" INS_SAV_AMT");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_INV_HEAD");
    sb.append(" JOIN MAX_DATE ON CREATETIME = MAX_DATE.MAX_CREATETIME");
    sb.append(" WHERE CUST_ID= :custID");
    qc.setObject("custID", custID);

    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
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

  protected void create(Object body, DataAccessManager dam, String planID) throws DAOException, JBranchException {
    FPS210InputVO inputVO = (FPS210InputVO) body;

    TBFPS_PORTFOLIO_PLAN_INV_HEADVO vo = putPlanInvHeadVO(new TBFPS_PORTFOLIO_PLAN_INV_HEADVO(), inputVO);
    vo.setPLAN_ID(planID);
    dam.create(vo);
  }

  protected void update(Object body, DataAccessManager dam, String planID) throws DAOException, JBranchException {
    FPS210InputVO inputVO = (FPS210InputVO) body;

    TBFPS_PORTFOLIO_PLAN_INV_HEADVO vo = (TBFPS_PORTFOLIO_PLAN_INV_HEADVO) dam
        .findByPKey(TBFPS_PORTFOLIO_PLAN_INV_HEADVO.TABLE_UID, planID);
    if (vo != null) {
      dam.update(putPlanInvHeadVO(vo, inputVO));
    } else {
      throw new APException("ehl_01_common_009"); // 查無資料
    }

  }

  private TBFPS_PORTFOLIO_PLAN_INV_HEADVO putPlanInvHeadVO(TBFPS_PORTFOLIO_PLAN_INV_HEADVO vo, FPS210InputVO inputVO) {
    vo.setCUST_ID(inputVO.getCustID());
    vo.setPLAN_STATUS(PLAN_IN_TWO_WEEKS);
    vo.setPLAN_STEP(StringUtils.isNotBlank(inputVO.getStep()) ? inputVO.getStep() : PLAN_STEP);
    // total
    vo.setPLAN_AMT(inputVO.getPlanAmt());
    // A
    vo.setDEPOSIT_AMT(inputVO.getDeposit());
    // A
//    vo.setANNUITY_INS_AMT(inputVO.getAnnuityProd());
//    vo.setFIXED_INCOME_AMT(inputVO.getFixedProd());
//    vo.setFUND_AMT(inputVO.getFundProd());
    vo.setMFD_PROD_AMT(inputVO.getMfdProd());
    vo.setETF_PROD_AMT(inputVO.getEtfProd());
    vo.setINS_PROD_AMT(inputVO.getInsProd());
    vo.setBOND_PROD_AMT(inputVO.getBondProd());
    vo.setSN_PROD_AMT(inputVO.getSnProd());
    vo.setSI_PROD_AMT(inputVO.getSiProd());
    //2020-01-30 by Jacky 新增奈米投產品
    vo.setNANO_PROD_AMT(inputVO.getNanoProd());
    // B
    vo.setSOW_AMT(inputVO.getSowAmt());
    // C
    vo.setCASH_YEAR_AMT(inputVO.getCash());
    // C cash
    vo.setLIVE_YEAR_AMT(inputVO.getEssCash());
    vo.setLOAN_EXPENSES(inputVO.getLoanExpenses());
    vo.setOTHER_EXPENSES(inputVO.getOtherExpenses());
//    vo.setPREPARE_YEAR_AMT(inputVO.getEmeCsh());
    
    vo.setLN_HOUSE_AMT(inputVO.getHouseLoan());
    vo.setLN_CREDIT_AMT(inputVO.getCreditLoan());
    vo.setLN_EDCUATION_AMT(inputVO.getStdLoan());
    
    vo.setBUY_HOUSE_AMT(inputVO.getPayForHouse());
    vo.setBUY_CAR_AMT(inputVO.getPayForCar());
    vo.setTRAVEL_AMT(inputVO.getTravel());
    vo.setOVERSEA_EDUCATION_AMT(inputVO.getStudy());
    vo.setOTHER_AMT(inputVO.getOther());
    // D
    vo.setINS_SAV_AMT(inputVO.getInsSavAmt());
    vo.setINS_POLICY_AMT(inputVO.getInsPolicyAmt());

    vo.setCASH_PREPARE(inputVO.getCashPrepare());
    
    vo.setCUST_RISK_ATR(inputVO.getCustRisk());
    vo.setVALID_FLAG("Y");

    vo.setPORTFOLIO2_RATIO(inputVO.getPortfolio2Ratio());
    vo.setPORTFOLIO3_RATIO(inputVO.getPortfolio3Ratio());
    vo.setPORTFOLIO2_AMT(inputVO.getPortfolio2Amt());
    vo.setPORTFOLIO3_AMT(inputVO.getPortfolio3Amt());

    return vo;
  }

  /**
   * 列印推介同意書
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws DAOException
   */
  public void printSignAgmt(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
    FPS210InputVO inputVO = (FPS210InputVO) body;
    String custID = inputVO.getCustID();

    List<String> urlList = new ArrayList<String>();
    PRDFitInputVO prdFitInputVO = new PRDFitInputVO();
    prdFitInputVO.setCaseCode(2);
    prdFitInputVO.setCustId(custID);
    try {
      SOT813 sot813 = (SOT813) PlatformContext.getBean("sot813");
      sot813.setInputVO(prdFitInputVO);
      urlList = sot813.printReport();
      if (urlList.size() > 0) {
        notifyClientToDownloadFile(urlList.get(0), "推介同意書.pdf");
        this.sendRtnObject(true);
      }
    } catch (Exception e) {
      e.printStackTrace();
      this.sendRtnObject(false);
      // TODO: handle exception
    }

  }
  
  public void getSuggestPct(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
	  DataAccessManager dam = this.getDataAccessManager();
	  Map<String, Object> param = (Map<String, Object>) body;
	  this.sendRtnObject(getSuggestPct(dam, param));
  }
  
  public BigDecimal[] getSuggestPct(DataAccessManager dam, Map<String, Object> param) throws DAOException, JBranchException {
	  boolean hasIns = (boolean) param.get("hasIns");
	  String column = hasIns ? "custID" : "riskType";
	  String value = hasIns ? ObjectUtils.toString(param.get("custID")) :  ObjectUtils.toString(param.get("riskType"));
	  BigDecimal[] suggestPcts = new BigDecimal[2];
	  String sql = hasIns ? suggestPctHasIns() : suggestPct();

	  return getSuggestPct(dam, value, column, sql, hasIns);
  }
  
  private BigDecimal[] getSuggestPct(DataAccessManager dam, String value, String column, String sql, boolean hasIns) throws DAOException, JBranchException , APException{
	  QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	  qc.setObject(column, value);
	  qc.setQueryString(sql);
	  List<Map<String, Object>> result = dam.exeQuery(qc);
	  BigDecimal[] pcts = null;
	  
	  if(result.isEmpty()) throw new APException("無有效KYC或無適合的投組推薦");
	  
	  // 0:類債券 1類股票 [2:再投資類債券, 3:再投資類股票]
	  if(hasIns) {
		  pcts = new BigDecimal[4];
		  BigDecimal total = BigDecimal.ZERO;
		  BigDecimal bTotal = BigDecimal.ZERO;
		  BigDecimal sTotal = BigDecimal.ZERO;
		  if(CollectionUtils.isNotEmpty(result)) {
			  for(Map<String, Object> resultMap : result) {
				  if("B".equals(resultMap.get("STOCK_BOND_TYPE"))) {
					  bTotal = bTotal.add(new GenericMap(resultMap).getBigDecimal("NOW_AMT_TWD").setScale(10, BigDecimal.ROUND_HALF_UP));
				  } else {
					  sTotal = sTotal.add(new GenericMap(resultMap).getBigDecimal("NOW_AMT_TWD").setScale(10, BigDecimal.ROUND_HALF_UP));
				  }
			  }
			  total = bTotal.add(sTotal);
		  }
		  if(total.compareTo(BigDecimal.ZERO) > 0) {
			  pcts[0] = bTotal.multiply(new BigDecimal(100)).divide(total, 0, BigDecimal.ROUND_HALF_UP);
			  pcts[1] = new BigDecimal(100).subtract(pcts[0]);	
			  pcts[2] = bTotal.setScale(0, BigDecimal.ROUND_HALF_UP);
			  pcts[3] = sTotal.setScale(0, BigDecimal.ROUND_HALF_UP);
		  }
	  } else {
		  pcts = new BigDecimal[2];
		  pcts[1] = result.size() > 0 ? new GenericMap(result.get(0)).getBigDecimal("VOLATILITY").setScale(0, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
		  pcts[0] = new BigDecimal(100).subtract(pcts[1]);		  
	  }
	  return pcts;
  }
  
  public BigDecimal getCashPreparePct(DataAccessManager dam, String custID) throws DAOException, JBranchException {
	  QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	  qc.setObject("CUST_ID", custID);
	  qc.setQueryString(cashPrepareSuggestPct());
	  List<Map<String, Object>> result = dam.exeQuery(qc);
	  
	  BigDecimal pct = BigDecimal.ZERO;
	  if(CollectionUtils.isNotEmpty(result)) {
		  GenericMap gmap = new GenericMap(result.get(0));
		  BigDecimal firstAge = gmap.get("CASH_PREPARE_AGE_1");
		  BigDecimal secondAge = gmap.get("CASH_PREPARE_AGE_2");
		  BigDecimal thridAge = gmap.get("CASH_PREPARE_AGE_3");
		  BigDecimal firstPct = gmap.getBigDecimal("CASH_PREPARE_1");
		  BigDecimal secondPct = gmap.getBigDecimal("CASH_PREPARE_2");
		  BigDecimal thridPct = gmap.getBigDecimal("CASH_PREPARE_3");
		  BigDecimal forthPct = gmap.getBigDecimal("CASH_PREPARE_4");
		  BigDecimal age = gmap.get("AGE");
		  
		  if(age.intValue() <= firstAge.intValue()) {
			  pct = firstPct;
		  } else if (firstAge.intValue() < age.intValue() && age.intValue() <= secondAge.intValue()) {
			  pct = secondPct;
		  } else if (secondAge.intValue() < age.intValue() && age.intValue() <= thridAge.intValue()) {
			  pct = thridPct;
		  } else if (age.intValue() > thridAge.intValue()) {
			  pct = forthPct;
		  } 
	  }
	  return pct;
  }
  
  // 再投資配置
  private final static String suggestPctHasIns() {
	  StringBuffer sb = new StringBuffer();
	  sb.append(" WITH ");
	  sb.append(" GEN AS ");
	  sb.append(" (SELECT PARAM_CODE, ");
	  sb.append(" PARAM_NAME ");
	  sb.append(" FROM TBSYSPARAMETER ");
	  sb.append(" WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_1'), ");
	  sb.append(" SML AS ");
	  sb.append(" (SELECT PARAM_CODE, ");
	  sb.append(" PARAM_NAME ");
	  sb.append(" FROM TBSYSPARAMETER ");
	  sb.append(" WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_2'), ");
	  sb.append(" ETF AS ");
	  sb.append(" (SELECT PRD_ID, TXN_AMOUNT, INV_TARGET, STOCK_BOND_TYPE ");
	  sb.append(" FROM TBPRD_ETF ");
	  sb.append(" WHERE IS_SALE IS NULL), ");
	  sb.append(" SYSPAR AS ");
	  sb.append(" (SELECT PARAM_CODE, ");
	  sb.append(" PARAM_NAME ");
	  sb.append(" FROM TBSYSPARAMETER ");
	  sb.append(" WHERE PARAM_TYPE = 'FPS.FUND_TYPE'), ");
	  sb.append(" AREA AS ");
	  sb.append(" (SELECT PARAM_CODE, ");
	  sb.append(" PARAM_NAME ");
	  sb.append(" FROM TBSYSPARAMETER ");
	  sb.append(" WHERE PARAM_TYPE = 'PRD.MKT_TIER1'), ");
	  sb.append(" TREND AS ");
	  sb.append(" (SELECT TRD.TYPE, ");
	  sb.append(" TRD.TREND ");
	  sb.append(" FROM TBFPS_MARKET_TREND TR ");
	  sb.append(" LEFT JOIN TBFPS_MARKET_TREND_DETAIL TRD ON TR.PARAM_NO = TRD.PARAM_NO ");
	  sb.append(" WHERE TR.STATUS = 'A'), ");
	  sb.append(" BASE_INS AS ");
	  sb.append(" (SELECT DISTINCT BASE.PRD_ID, ");
	  sb.append(" BASE.CURR_CD, ");
	  sb.append(" INSINFO.BASE_AMT_OF_PURCHASE, ");
	  sb.append(" BASE.INS_TYPE, ");
	  sb.append(" 'S' STOCK_BOND_TYPE, ");
	  sb.append(" LISTAGG(BASE.KEY_NO, ',') WITHIN ");
	  sb.append(" GROUP (ORDER BY BASE.KEY_NO) AS KEY_NO ");
	  sb.append(" FROM TBPRD_INS BASE ");
	  sb.append(" LEFT JOIN TBPRD_INSINFO INSINFO ON BASE.PRD_ID = INSINFO.PRD_ID ");
	  sb.append(" WHERE BASE.INS_TYPE IN ('1', '2') ");
	  sb.append(" GROUP BY BASE.PRD_ID, ");
	  sb.append(" BASE.CURR_CD, ");
	  sb.append(" INSINFO.BASE_AMT_OF_PURCHASE, ");
	  sb.append(" BASE.INS_TYPE,'S'), ");
	  sb.append(" INS_TARGET AS ( ");
	  sb.append(" SELECT (M.POLICY_NBR ||'-'|| TRIM(TO_CHAR(M.POLICY_SEQ  ,'00')) || CASE WHEN M.ID_DUP <> ' ' THEN '-' || M.ID_DUP END ) AS CERT_NBR,  ");
	  sb.append(" M.PRD_ID, LISTAGG(D.INV_TARGET_NO, '/') WITHIN GROUP (ORDER BY D.POLICY_NO) AS TARGETS  ");
	  sb.append(" FROM TBCRM_AST_INS_MAST M ");
	  sb.append(" INNER JOIN TBCRM_AST_INS_TARGET D ON M.POLICY_NBR = D.POLICY_NO AND M.POLICY_SEQ = D.POLICY_SEQ ");
	  sb.append(" WHERE 1=1  ");
	  sb.append(" and M.CUST_ID = :custID ");
	  sb.append(" GROUP BY M.PRD_ID, (M.POLICY_NBR ||'-'|| TRIM(TO_CHAR(M.POLICY_SEQ ,'00')) || CASE WHEN M.ID_DUP <> ' ' THEN '-' || M.ID_DUP END ) ");
	  sb.append(" ) ");
	  sb.append(" SELECT  ");
	  sb.append(" MAIN.NOW_AMT_TWD,");
	  sb.append(" case when ETF.PRD_ID is not null then ETF.STOCK_BOND_TYPE ");
	  sb.append(" when BASE_INS.PRD_ID  is not null then BASE_INS.STOCK_BOND_TYPE ");
	  sb.append(" when FUIN.PRD_ID  is not null then FUIN.STOCK_BOND_TYPE ");
	  sb.append(" when BNDINFO.PRD_ID  is not null then BNDINFO.STOCK_BOND_TYPE ");
	  sb.append(" when SIINFO.PRD_ID  is not null then SIINFO.STOCK_BOND_TYPE ");
	  sb.append(" when SNINFO.PRD_ID  is not null then SNINFO.STOCK_BOND_TYPE ");
	  sb.append(" when NANOINFO.PRD_ID  is not null then NANOINFO.STOCK_BOND_TYPE ");
	  sb.append(" end as STOCK_BOND_TYPE ");
	  sb.append(" FROM MVFPS_AST_ALLPRD_DETAIL MAIN ");
	  sb.append(" LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = MAIN.PROD_ID ");
	  sb.append(" LEFT JOIN TBPRD_FUNDINFO FUIN ON FUIN.PRD_ID = FU.PRD_ID ");
	  sb.append(" LEFT JOIN GEN ON GEN.PARAM_CODE = FU.CURRENCY_STD_ID ");
	  sb.append(" LEFT JOIN SML ON SML.PARAM_CODE = FU.CURRENCY_STD_ID ");
	  sb.append(" LEFT JOIN ETF ON ETF.PRD_ID = MAIN.PROD_ID ");
	  sb.append(" LEFT JOIN SYSPAR ON SYSPAR.PARAM_CODE = FU.FUND_TYPE ");
	  sb.append(" LEFT JOIN TREND ON TREND.TYPE = FU.INV_TARGET ");
	  sb.append(" LEFT JOIN AREA ON AREA.PARAM_CODE = TREND.TYPE ");
	  sb.append(" LEFT JOIN BASE_INS ON BASE_INS.PRD_ID = MAIN.PROD_ID AND BASE_INS.CURR_CD = MAIN.CUR_ID ");
	  sb.append(" LEFT JOIN TREND TREND_ETF ON TREND_ETF.TYPE = ETF.INV_TARGET ");
	  sb.append(" LEFT JOIN TBPRD_BONDINFO BNDINFO ON BNDINFO.PRD_ID = MAIN.PROD_ID ");
	  sb.append(" LEFT JOIN TBPRD_SIINFO SIINFO ON SIINFO.PRD_ID = MAIN.PROD_ID ");
	  sb.append(" LEFT JOIN TBPRD_SNINFO SNINFO ON SNINFO.PRD_ID = MAIN.PROD_ID ");
	  sb.append(" LEFT JOIN TBPRD_NANO NANOINFO ON NANOINFO.INV_LEVEL = MAIN.INS_TYPE AND NANOINFO.IS_SALE = 'Y'"); //2020-4-15 林家琪說奈米投的投資策略每一個level只會有一檔
	  sb.append(" LEFT JOIN VWPRD_MASTER PRD ON PRD.PRD_ID = MAIN.PROD_ID AND PRD.PTYPE != 'STOCK' AND PRD.CURRENCY_STD_ID = MAIN.CUR_ID_ORI ");
	  sb.append(" LEFT JOIN TBFPS_PLANID_MAPPING MAPPING ON MAPPING.CERTIFICATE_ID = MAIN.CERT_NBR ");
	  sb.append(" LEFT JOIN INS_TARGET ON INS_TARGET.CERT_NBR = MAIN.CERT_NBR ");
	  sb.append(" LEFT JOIN (SELECT CUR_COD, BUY_RATE FROM TBPMS_IQ053 WHERE MTN_DATE = (SELECT MAX (MTN_DATE) FROM TBPMS_IQ053)) IQ053 on IQ053.CUR_COD = MAIN.CUR_ID");
	  sb.append(" WHERE 1=1   ");
	  sb.append(" and MAIN.CUST_ID = :custID ");
	  sb.append(" AND MAIN.AST_TYPE IN ('07', '08', '09', '12', '10', '15', '14', '18') "); //2020-1-30 by Jacky新增奈米投
	  sb.append(" AND MAIN.NOW_AMT != 0 ");
	  sb.append(" AND (MAIN.AST_TYPE = '18' OR PRD.PTYPE <> 'INS') ");
	  sb.append(" AND MAPPING.CERTIFICATE_ID IS NULL ");
	  return sb.toString();
  }

  // 首作建議配置
  private final static String suggestPct() {
	  StringBuffer sb = new StringBuffer();
      sb.append(" select VOLATILITY from TBFPS_CUSTRISK_VOLATILITY_HEAD h ");
      sb.append(" inner join TBFPS_CUSTRISK_VOLATILITY d on h.param_no = d.param_no ");
      sb.append(" and h.status = 'A' and d.cust_risk_atr = :riskType ");
      return sb.toString();
  }
  
  private final static String cashPrepareSuggestPct() {
	  StringBuffer sb = new StringBuffer();
	  sb.append(" select ");
	  sb.append(" CASH_PREPARE_AGE_1, CASH_PREPARE_AGE_2, CASH_PREPARE_AGE_3, ");
	  sb.append(" CASH_PREPARE_1, CASH_PREPARE_2, CASH_PREPARE_3, CASH_PREPARE_4, ");
	  sb.append(" cust.age ");
	  sb.append(" from TBFPS_OTHER_PARA_HEAD head, tbcrm_cust_mast cust, TBFPS_OTHER_PARA body ");
	  sb.append(" where head.status = 'A' and head.param_no = body.param_no and cust_id = :CUST_ID ");
	  return sb.toString();
  }
}
