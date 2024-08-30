package com.systex.jbranch.app.server.fps.fps200;

import com.systex.jbranch.app.common.fps.table.TBFPS_CUST_LISTVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_RPTPK;
import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_RPTVO;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.app.server.fps.ins110.INS110;
import com.systex.jbranch.app.server.fps.prd130.PRD130InputVO;
import com.systex.jbranch.app.server.fps.prd140.PRD140InputVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb372602.EB372602InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb372602.EB372602OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.fubon.commons.fitness.ProdFitness;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import de.schlichtherle.io.File;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.EB372602_DATA;

@Component("fps200")
@Scope("request")
public class FPS200 extends EsbUtil {

  private DataAccessManager dam = null;
  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  @Qualifier("ins110")
  private INS110 ins110;

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void getCust(Object body, IPrimitiveMap<?> header) throws JBranchException {
    try {
      FPS200InputVO inputVO = (FPS200InputVO) body;
      FPS200OutputVO outputVO = new FPS200OutputVO();
      List<Map<String, Object>> custResulList = new ArrayList<Map<String, Object>>();
      Map<String, Object> custResul = new HashMap<String, Object>();
      String custID = inputVO.getCustID();
      dam = this.getDataAccessManager();

      if (getCustListCnt(dam, custID) > 0) {
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append("  MAST.CUST_NAME, ");
        sb.append("  MAST.SAL_COMPANY, ");
        sb.append("  MAST.BIRTH_DATE, "); 	
        sb.append("  MAST.AGE, "); 							 							             // 年齡
        sb.append("  MAST.EDUCATION_STAT, "); 	 							             // 學歷
        sb.append("  MAST.CUST_RISK_ATR, "); 		 							             // 客戶風險等級
        sb.append("  MAST.GENDER, "); 					 							             // 性別
        sb.append("  MAST.KYC_DUE_DATE, "); 		 							             // KYC有效期
        sb.append("  MAST.ANNUAL_INCOME_AMT, "); 							             // 年收入
        sb.append("  NVL(NOTE.SIGN_AGMT_YN, 'N') SIGN_AGMT_YN, ");         // 有無簽屬推介同意書
        sb.append("  NVL(NOTE.REC_YN, 'N') REC_YN, ");                     // 可否推介
        sb.append("  NVL(NOTE.SP_CUST_YN, 'N') SP_CUST_YN, ");             // 特定客戶(特殊客戶)
        sb.append("  NVL(NOTE.COMM_RS_YN, 'N') COMM_RS_YN, ");             // 拒銷註記
        sb.append("  NVL(NOTE.COMM_NS_YN, 'N') COMM_NS_YN, ");             // 撤銷註記
        sb.append("  NVL(NOTE.OBU_YN, 'N') OBU_YN, ");                     // OBU註記
        sb.append("  NVL(NOTE.PROF_INVESTOR_YN, 'N') PROF_INVESTOR_YN, "); // 專業投資人註記
        sb.append("	 NVL(NOTE.SAL_ACC_YN, 'N') SAL_ACC_YN, ");             // 薪轉戶註記
        sb.append("	 CONTACT.EMAIL ");
        sb.append("FROM TBCRM_CUST_MAST MAST ");
        sb.append("LEFT JOIN TBCRM_CUST_NOTE NOTE ON MAST.CUST_ID = NOTE.CUST_ID ");
        sb.append("LEFT JOIN TBCRM_CUST_CONTACT CONTACT ON MAST.CUST_ID = CONTACT.CUST_ID ");
        sb.append("WHERE MAST.CUST_ID = :custId ");
        qc.setObject("custId", custID);
        qc.setQueryString(sb.toString());
        List<Map<String, Object>> list = dam.exeQuery(qc);

        if (list.size() <= 0) {
          throw new APException("ehl_01_common_009"); // 查無資料
        }
        custResul.put("CUST_NAME", list.get(0).get("CUST_NAME")); // 客戶姓名
        custResul.put("SAL_COMPANY", list.get(0).get("SAL_COMPANY") == null ? "N" : "Y"); // 薪資戶
        custResul.put("ANNUAL_INCOME_AMT", list.get(0).get("ANNUAL_INCOME_AMT")); // 年收入
        custResul.put("REC_YN", list.get(0).get("REC_YN")); // 能否簽署
        custResul.put("COMM_RS_YN", list.get(0).get("COMM_RS_YN")); // 拒銷
        custResul.put("COMM_NS_YN", list.get(0).get("COMM_NS_YN")); // 撤銷
        custResul.put("EMAIL", list.get(0).get("EMAIL")); //
        
        // 2018 12 21 開
        custResul.put("BDAY_D", list.get(0).get("BIRTH_DATE")); // 年齡 (FPS200前端會算，電文的 BDAY_D 是空的會造成前端計算錯誤，所以使用DB生日來計算)
        custResul.put("EDUCATION", list.get(0).get("EDUCATION_STAT")); // 學歷
        custResul.put("KYC_LEVEL", list.get(0).get("CUST_RISK_ATR")); // KYC等級
        custResul.put("KYC_DUE_DATE", list.get(0).get("KYC_DUE_DATE")); // KYC(效期)
        custResul.put("OBU_FLAG", list.get(0).get("OBU_YN")); // OBU註記
        custResul.put("CUST_PRO_FLAG", list.get(0).get("PROF_INVESTOR_YN")); // 專業投資人
        custResul.put("SIGN_AGMT_YN", list.get(0).get("SIGN_AGMT_YN")); // 已簽署推介同意書
        custResul.put("CUST_PRO_DATE", new Date());
        
        // 暫時 Juan
        try {
          SOT701InputVO inputVO_701 = new SOT701InputVO();
          inputVO_701.setCustID(inputVO.getCustID());
          SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
          FP032675DataVO FP032675Data = sot701.getFP032675Data(inputVO_701);
          FP032151OutputVO FP032151Data = sot701.getFP032151Data(inputVO_701, header);
          CustKYCDataVO CustKYCData = sot701.getCustKycData(inputVO_701);

          // 2018 12 21 關 (之後再開)
          // FC032675DataVO.java及SOT710.java 增加BDAY_D 在1.5階新增 到時要注意
//          custResul.put("BDAY_D", FC032151Data.getBDAY_D());// 年齡 電文是 null 改拿 DB
          custResul.put("EDUCATION", FP032151Data.getEDUCATION());// 學歷
          custResul.put("KYC_LEVEL", CustKYCData.getKycLevel());// KYC等級
          custResul.put("KYC_DUE_DATE", CustKYCData.getKycDueDate());// KYC(效期)
          custResul.put("OBU_FLAG", FP032675Data.getObuFlag());// OBU註記
          custResul.put("CUST_PRO_FLAG", FP032675Data.getCustProFlag());// 專業投資人
          custResul.put("CUST_PRO_DATE", FP032675Data.getCustProDate());// 專業投資人(效期)
          custResul.put("SIGN_AGMT_YN", StringUtils.isBlank(FP032675Data.getCustTxFlag())
              ? "N" : FP032675Data.getCustTxFlag());// 有無簽屬推介同意書
        } catch (Exception e) {
//          custResul.put("BDAY_D", list.get(0).get("AGE")); // 年齡
          custResul.put("EDUCATION", list.get(0).get("EDUCATION_STAT")); // 學歷
          custResul.put("KYC_LEVEL", list.get(0).get("CUST_RISK_ATR")); // KYC等級
          custResul.put("KYC_DUE_DATE", list.get(0).get("KYC_DUE_DATE")); // KYC(效期)
          custResul.put("OBU_FLAG", list.get(0).get("OBU_YN")); // OBU註記
          custResul.put("CUST_PRO_FLAG", list.get(0).get("PROF_INVESTOR_YN")); // 專業投資人
          // custResul.put("CUST_PRO_DATE", FC032675Data.getCustProDate());
          // //專業投資人(效期)
        }
        custResulList.add(custResul);

        // update cust loans
        Boolean canUpdate = false;
        TBFPS_CUST_LISTVO list_vo = (TBFPS_CUST_LISTVO) dam.findByPKey(TBFPS_CUST_LISTVO.TABLE_UID, custID);
        canUpdate = doCustLoan(dam, custID, list_vo) || canUpdate;
        // update cust ins
        canUpdate = doCustIns(dam, custID, list_vo) || canUpdate;
        if (canUpdate)
          dam.update(list_vo);

        outputVO.setCustInfo(custResulList);
      } else {
        outputVO.setCustInfo(new ArrayList());
      }
      outputVO.setHasInvest(hasStock(dam, custID) > 0);

      sendRtnObject(outputVO);
    } catch (Exception e) {
      logger.debug(e.getMessage());
      throw new APException(e.getMessage());
    }

  }

  /**
   * API
   * 
   * checkEmpFpsAuth 檢查理專有無權限
   * 
   * @param body
   * @param header
   * @throws JBranchException
   */
  public void checkEmpFpsAuth(Object body, IPrimitiveMap<?> header) throws JBranchException {
    dam = this.getDataAccessManager();
    FPS200InputVO inputVO = (FPS200InputVO) body;
    String empID = inputVO.getEmpID();

    sendRtnObject(FPSUtils.hasEmpFpsAuth(dam, empID));
  }

  /**
   * hasStock 有無庫存 (基股商品,固收商品)
   * 
   * @param dam
   * @param custID
   * @return int 商品數
   * @throws DAOException
   * @throws JBranchException
   */
  @SuppressWarnings("unchecked")
  public int hasStock(DataAccessManager dam, String custID) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuilder sb = new StringBuilder();

    sb.append(" SELECT COUNT(*) CNT FROM MVFPS_AST_ALLPRD_DETAIL");
    sb.append(" WHERE 1=1");
    sb.append(" AND AST_TYPE IN ('07','08','09','12', '10','15', '14')");
    sb.append(" AND NOW_AMT != 0 and nvl(ins_type,0) <> 1 ");
    sb.append(" AND CUST_ID = :custID");

    qc.setObject("custID", custID);
    qc.setQueryString(sb.toString());

    return ((BigDecimal) ((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("CNT")).intValue();
  }

  @SuppressWarnings("unchecked")
  private int getCustListCnt(DataAccessManager dam, String custID) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuilder sb = new StringBuilder();
    sb.append(" SELECT COUNT(*) AS CHECKER FROM TBFPS_CUST_LIST WHERE CUST_ID = :custID ");
    qc.setObject("custID", custID);
    qc.setQueryString(sb.toString());

    return Integer.parseInt(((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("CHECKER").toString());
  }

  // fx匯率
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getFxRate(DataAccessManager dam, String cur) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT CUR_COD, SEL_RATE, BUY_RATE FROM TBPMS_IQ053");
    sb.append(" WHERE MTN_DATE = (SELECT MAX (MTN_DATE) FROM TBPMS_IQ053)");
    if (StringUtils.isNotBlank(cur)) {
      sb.append("AND CUR_COD = :code");
      qc.setObject("code", cur);
    }

    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getSN(DataAccessManager dam, String riskType, ProdFitness prodFitness) throws Exception {
    // TODO
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT MA.BOND_VALUE, MA.PRD_ID, MA.SN_CNAME AS PRD_CNAME, MA.IS_SALE, MA.RATE_GUARANTEEPAY,");
    sb.append(" MA.CURRENCY_STD_ID AS CURRENCY_TYPE, MA.RISKCATE_ID AS RISK_TYPE, MA.PRD_RANK, MA.OBU_BUY AS OBU_BUY_2,");
    sb.append(" MA.RISKCATE_ID, MA.SN_TYPE, INFO.INVESTMENT_TARGETS, MA.DATE_OF_MATURITY AS MATURITY_DATE, MA.GLCODE,");
    sb.append(" ROUND((MA.DATE_OF_MATURITY - SYSDATE) / 365, 2) AS DATE_OF_MATURITY,");
    sb.append(" NVL(INFO.BASE_AMT_OF_PURCHASE,0) AS GEN_SUBS_MINI_AMT_FOR,");
    sb.append(" NVL(INFO.UNIT_OF_VALUE,0) AS PRD_UNIT,");
    sb.append(" CASE WHEN MA.OBU_BUY = 'O' THEN 'Y' ELSE 'N' END AS OBU_BUY,");
    sb.append(" CASE WHEN SYSDATE BETWEEN INFO.INV_SDATE AND INFO.INV_EDATE THEN 'Y' ELSE NULL END AS IS_EXPIRED,");
    sb.append(" CASE WHEN MA.PI_BUY = 'Y' THEN 'Y' ELSE 'N' END AS PI_BUY,");
    sb.append(" CASE WHEN PABTH_UTIL.FC_GETBUSIDAY(SYSDATE, 'TWD', 3) > INFO.INV_EDATE THEN 'Y' ELSE 'N' END AS BUSIDAY");
    sb.append(" FROM TBPRD_SN MA");
    sb.append(" INNER JOIN TBPRD_SNINFO INFO ON MA.PRD_ID = INFO.PRD_ID");
    sb.append(" WHERE TRUNC(SYSDATE) BETWEEN INFO.INV_SDATE AND INFO.INV_EDATE");
    sb.append(" AND MA.RECORD_FLAG != 'Y'");
    sb.append(" AND TO_NUMBER(SUBSTR(MA.RISKCATE_ID,2,1)) <= :riskNum");
    qc.setObject("riskNum", Integer.parseInt(riskType.substring(1, 2)));

    qc.setQueryString(sb.toString());
    List<Map<String, Object>> result = dam.exeQuery(qc);
    List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

    for (Map<String, Object> SN : result) {
      if (!prodFitness.validProdSN(SN, false, new PRD140InputVO()).getIsError()) {
        SN.put("PTYPE", "SN");
        resultList.add(SN);
      }
    }
    return resultList;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getSI(DataAccessManager dam, String riskType, ProdFitness prodFitness, boolean isPro, String stockBondType) throws Exception {
    // TODO
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT MA.PRD_ID, MA.SI_CNAME AS PRD_CNAME, MA.CURRENCY_STD_ID AS CURRENCY_TYPE,");
    sb.append(" MA.RISKCATE_ID AS RISK_TYPE, MA.PRD_RANK, MA.SI_TYPE, MA.OBU_BUY AS OBU_BUY_2,");
    sb.append(" MA.RISKCATE_ID, MA.DATE_OF_MATURITY AS MATURITY_DATE, MA.GLCODE, MA.STAKEHOLDER,");
    sb.append(" NVL(INFO.BASE_AMT_OF_PURCHASE,0) AS GEN_SUBS_MINI_AMT_FOR,");
    sb.append(" ROUND((MA.DATE_OF_MATURITY - SYSDATE) / 365, 2) AS DATE_OF_MATURITY,");
    sb.append(" CASE WHEN MA.OBU_BUY = 'O' THEN 'Y' ELSE 'N' END AS OBU_BUY,");
    sb.append(" CASE WHEN SYSDATE BETWEEN INFO.INV_SDATE AND INFO.INV_EDATE THEN 'Y' ELSE NULL END AS IS_SALE,");
    sb.append(" CASE WHEN MA.RATE_GUARANTEEPAY IS NULL THEN 0 ELSE MA.RATE_GUARANTEEPAY END AS RATE_GUARANTEEPAY,");
    sb.append(" CASE WHEN MA.PI_BUY = 'Y' THEN 'Y' ELSE 'N' END AS PI_BUY,");
    sb.append(" CASE WHEN PABTH_UTIL.FC_GETBUSIDAY(SYSDATE, 'TWD', 3) > INFO.INV_EDATE THEN 'Y' ELSE 'N' END AS BUSIDAY,");
    sb.append(" INFO.INVESTMENT_TARGETS");
    sb.append(" FROM TBPRD_SI MA");
    sb.append(" INNER JOIN TBPRD_SIINFO INFO ON MA.PRD_ID = INFO.PRD_ID and INFO.STOCK_BOND_TYPE = :stockBondType");
    sb.append(" WHERE TRUNC(SYSDATE) BETWEEN INFO.INV_SDATE AND INFO.INV_EDATE");
    sb.append(" AND MA.RECORD_FLAG != 'Y' ");
    sb.append(" AND not exists(select 1 from TBPRD_CUSTOMIZED CM where CM.PRD_ID = MA.PRD_ID AND PTYPE = 'SI') ");
    sb.append(" AND TO_NUMBER(SUBSTR(MA.RISKCATE_ID,2,1)) <= :riskNum");
    qc.setObject("riskNum", Integer.parseInt(riskType.substring(1, 2)));
    qc.setObject("stockBondType", stockBondType);
//    qc.setObject("piBuy", isPro ? 'Y' : 'N');
    
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> result = dam.exeQuery(qc);
    List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

    for (Map<String, Object> SI : result) {
      if (!prodFitness.validProdSI(SI, false).getIsError()) {
    	  SI.put("PTYPE", "SI");
    	  resultList.add(SI);
      }
    }
    return resultList;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getBND(DataAccessManager dam, String riskType, ProdFitness prodFitness, boolean isPro, String stockBondType) throws Exception {
    // TODO
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT MA.BOND_VALUE, MA.PRD_ID, MA.BOND_CNAME AS PRD_CNAME, MA.IS_SALE, MA.CURRENCY_STD_ID AS CURRENCY_TYPE,");
    sb.append(" MA.RISKCATE_ID AS RISK_TYPE, MA.PRD_RANK, MA.PRD_RANK_DATE, MA.YTM, MA.BOND_CATE_ID, MA.DATE_OF_MATURITY AS MATURITY_DATE,");
    sb.append(" MA.RISKCATE_ID, MA.OBU_BUY AS OBU_BUY_2, INFO.BOND_CREDIT_RATING_SP,");
    sb.append(" NVL(INFO.BASE_AMT_OF_PURCHASE,0) AS GEN_SUBS_MINI_AMT_FOR,");
    sb.append(" NVL(INFO.UNIT_OF_VALUE,0) AS PRD_UNIT,");
    sb.append(" ROUND((MA.DATE_OF_MATURITY - SYSDATE) / 365, 2) AS DATE_OF_MATURITY,");
    sb.append(" CASE WHEN MA.OBU_BUY = 'O' THEN 'Y' ELSE 'N' END AS OBU_BUY,");
    sb.append(" CASE WHEN MA.PI_BUY = 'Y' THEN 'Y' ELSE 'N' END AS PI_BUY");
    sb.append(" FROM TBPRD_BOND MA");
    sb.append(" INNER JOIN TBPRD_BONDINFO INFO ON MA.PRD_ID = INFO.PRD_ID and INFO.STOCK_BOND_TYPE = :stockBondType");
    sb.append(" WHERE 1=1");
    sb.append(" AND MA.IS_SALE = 'Y' AND SYSDATE <= MA.DATE_OF_MATURITY");
    sb.append(" AND MA.PRD_RANK IS NOT NULL AND MA.PI_BUY = :piBuy ");
    sb.append(" AND not exists(select 1 from TBPRD_CUSTOMIZED CM where CM.PRD_ID = MA.PRD_ID AND PTYPE = 'BND') ");
    sb.append(" AND TO_NUMBER(SUBSTR(MA.RISKCATE_ID,2,1)) <= :riskNum");
    sb.append(" ORDER BY MA.PRD_RANK ");
    qc.setObject("riskNum", Integer.parseInt(riskType.substring(1, 2)));
    qc.setObject("stockBondType", stockBondType);
    qc.setObject("piBuy", isPro ? 'Y' : 'N');
    
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> result = dam.exeQuery(qc);
    List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

    for (Map<String, Object> BND : result) {
      if (!prodFitness.validProdBond(BND, true, new PRD130InputVO()).getIsError()) {
        BND.put("PTYPE", "BND");
        resultList.add(BND);
      }
    }
    return resultList;
  }

  // 參數
  public List<Map<String, Object>> getOtherPara(DataAccessManager dam) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT PA.*");
    sb.append(" FROM TBFPS_OTHER_PARA_HEAD HD");
    sb.append(" LEFT JOIN TBFPS_OTHER_PARA PA ON HD.PARAM_NO = PA.PARAM_NO");
    sb.append(" WHERE HD.STATUS = 'A'");
    sb.append(" AND SYSDATE BETWEEN NVL(HD.EFFECT_START_DATE, TO_DATE('1999/01/01', 'yyyy-MM-dd')) AND");
    sb.append(" NVL(HD.EFFECT_END_DATE, TO_DATE('2999/01/01', 'yyyy-MM-dd'))");
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  public BigDecimal toBD(Object obj) {
    // 使用BigDecimal，經測試發現可自動根據欄位的長度進行四捨五入
    if (obj == null) {
      return new BigDecimal(0);
    } else if ("java.math.BigDecimal".equals(obj.getClass().getName())) {
      return (BigDecimal) obj;
    } else if ("java.lang.Integer".equals(obj.getClass().getName())) {
      return new BigDecimal((Integer) obj);
    } else if ("java.lang.Double".equals(obj.getClass().getName())) {
      return new BigDecimal((Double) obj);
    } else {
      return new BigDecimal((Float) obj);
    }
  }

  public List<Map<String, Object>> getHisReturn(String planID, DataAccessManager dam) throws JBranchException {
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

    // 查詢最大共同資料區間
    sb.append("  SELECT MAX(NVL(SRC.MINDATADATE, '999999')) AS MAXPERIODSTARTDATE,   ");
    sb.append("         MIN(NVL(SRC.MAXDATADATE, '000000')) AS MAXPERIODENDDATE      ");
    sb.append("    FROM (SELECT P.PRD_ID,                                            ");
    sb.append("                 MAX(DATA_YEARMONTH) AS MAXDATADATE,                  ");
    sb.append("                 MIN(DATA_YEARMONTH) AS MINDATADATE                   ");
    sb.append("            FROM FPPDMT_PRODUCT P                                     ");
    sb.append("            LEFT JOIN TBFPS_PRD_RETURN_M M                            ");
    sb.append("              ON P.PRD_ID = M.PRD_ID                                  ");
    sb.append("             AND P.PTYPE = M.PRD_TYPE                                 ");
    sb.append("           WHERE P.PRD_ID IN                                          ");
    sb.append("                 (SELECT PRD.PRD_ID                                   ");
    sb.append("                    FROM TBFPS_PORTFOLIO_PLAN_PRD PRD                 ");
    sb.append("                   WHERE PLAN_ID = :planID                            ");
    sb.append("                     AND PRD.PRD_TYPE IN ('MFD', 'ETF', 'SS', 'OS'))  ");
    sb.append("             AND P.PTYPE IN ('MFD', 'ETF', 'SS', 'OS')                ");
    sb.append("           GROUP BY P.PRD_ID) SRC                                     ");
    qc.setObject("planID", planID);

    qc.setQueryString(sb.toString());
    List<Map<String, Object>> dateRange = dam.exeQuery(qc);

    // 資料區間最大只能是10年
    // MAXPERIODENDDATE與MAXPERIODSTARTDATE為null，說明這幾個商品沒有最大資料共同區間
    // MAXPERIODSTARTDATE = 999999 & MAXPERIODENDDATE = 000000
    // 說明TBFPS_PRD_RETURN_M中沒有某檔或某幾檔商品的資料
    // 以上都無法產生投組歷史績效表現
    if (dateRange.get(0).get("MAXPERIODENDDATE") != null
        && dateRange.get(0).get("MAXPERIODSTARTDATE") != null
        && !"999999".equals(dateRange.get(0).get("MAXPERIODSTARTDATE"))
        && !"000000".equals(dateRange.get(0).get("MAXPERIODENDDATE"))) {

      String endDate = (String) dateRange.get(0).get("MAXPERIODENDDATE");
      String startDate = (String) dateRange.get(0).get("MAXPERIODSTARTDATE");
      String year = endDate.substring(0, 3);
      String month = endDate.substring(4, 5);
      Calendar ca = Calendar.getInstance();
      ca.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
      ca.add(Calendar.MONTH, -120);
      String ymLarge = sdf.format(ca.getTime());
      if (Integer.parseInt(ymLarge) > Integer.parseInt(startDate)) {
        startDate = ymLarge;
      }

      // 查詢歷史報酬率
      qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
      sb = new StringBuffer();
      sb.append("SELECT M.DATA_YEARMONTH,                                              ");
      sb.append("       SUM(M.RETURN_1M * S.PRD_RATIO / 100) AS HIS_RETURN             ");
      sb.append("  FROM TBFPS_PRD_RETURN_M M                                           ");
      sb.append("  LEFT JOIN (SELECT PRD_ID,                                           ");
      sb.append("                    PRD_TYPE,                                         ");
      sb.append("                    ROUND(PRD_RATIO / SUM(PRD_RATIO)                  ");
      sb.append("                          OVER(PARTITION BY PLAN_ID) * 100,           ");
      sb.append("                          2) AS PRD_RATIO                             ");
      sb.append("               FROM TBFPS_PORTFOLIO_PLAN_PRD PRD                      ");
      sb.append("              WHERE PLAN_ID = :planID                                 ");
      sb.append("                AND PRD.PRD_TYPE IN ('MFD', 'ETF', 'SS', 'OS')) S     ");
      sb.append("    ON M.PRD_ID = S.PRD_ID                                            ");
      sb.append("   AND M.PRD_TYPE = S.PRD_TYPE                                        ");
      sb.append(" WHERE M.PRD_ID IN                                                    ");
      sb.append("       (SELECT PRD.PRD_ID                                             ");
      sb.append("          FROM TBFPS_PORTFOLIO_PLAN_PRD PRD                           ");
      sb.append("         WHERE PLAN_ID = :planID                                      ");
      sb.append("           AND PRD.PRD_TYPE IN ('MFD', 'ETF', 'SS', 'OS'))            ");
      sb.append("   AND M.DATA_YEARMONTH BETWEEN :startDate AND :endDate               ");
      sb.append(" GROUP BY M.DATA_YEARMONTH                                            ");
      sb.append(" ORDER BY M.DATA_YEARMONTH                                            ");
      qc.setObject("planID", planID);
      qc.setObject("startDate", startDate);
      qc.setObject("endDate", endDate);
      qc.setQueryString(sb.toString());
      list = dam.exeQuery(qc);
    } else {
      throw new JBranchException("您選的標的無歷史報酬可以進行分析!請選擇其他標的");
    }

    return list;
  }

  // /***
  // * 查詢歷史事件各自區間內歷史報酬率
  // * @param body
  // * @param header
  // * @throws JBranchException
  // */
  // public void getEventReturn (Object body, IPrimitiveMap<?> header) throws
  // JBranchException {
  // FPS200InputVO inputVO = (FPS200InputVO) body;
  // List<Map<String,Object>> rateList = getReturn(inputVO.getStartYM(),
  // inputVO.getEndYM(), inputVO.getPlanID(), "2");
  // FPS200OutputVO outputVO = new FPS200OutputVO();
  // outputVO.setRateList(rateList);
  // sendRtnObject(outputVO);
  // }

  public List<Map<String, Object>> getReturn(String startYM, String endYM, String planID, String type) throws JBranchException {
    dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    String str = new String();

    sb.append("          SELECT M.DATA_YEARMONTH,                                            ");
    sb.append("                 SUM(COALESCE(M.RETURN_1M, M.BENCHMARK_RETURN_1M) *           ");
    sb.append("                     S.PRD_RATIO / 100) AS HIS_RETURN                         ");
    sb.append("            FROM TBFPS_PRD_RETURN_M M                                         ");
    sb.append("            LEFT JOIN (SELECT PRD_ID,                                         ");
    sb.append("                             PRD_TYPE,                                        ");
    sb.append("                             ROUND(PRD_RATIO / SUM(PRD_RATIO)                 ");
    sb.append("                                   OVER(PARTITION BY PLAN_ID) * 100,          ");
    sb.append("                                   2) AS PRD_RATIO                            ");
    sb.append("                        FROM TBFPS_PORTFOLIO_PLAN_PRD PRD                     ");
    sb.append("                       WHERE PLAN_ID = :planID                                ");
    sb.append("                         AND PRD.PRD_TYPE IN ('MFD', 'ETF', 'SS', 'OS')) S    ");
    sb.append("              ON M.PRD_ID = S.PRD_ID                                          ");
    sb.append("             AND M.PRD_TYPE = S.PRD_TYPE                                      ");
    sb.append("           WHERE M.PRD_ID IN                                                  ");
    sb.append("                 (SELECT PRD.PRD_ID                                           ");
    sb.append("                    FROM TBFPS_PORTFOLIO_PLAN_PRD PRD                         ");
    sb.append("                   WHERE PLAN_ID = :planID                                    ");
    sb.append("                     AND PRD.PRD_TYPE IN ('MFD', 'ETF', 'SS', 'OS'))          ");
    sb.append("             AND M.DATA_YEARMONTH BETWEEN :startYM AND :endYM                 ");
    sb.append("           GROUP BY M.DATA_YEARMONTH                                         ");
    sb.append("           ORDER BY M.DATA_YEARMONTH                                         ");
    qc.setObject("planID", planID);
    qc.setObject("startYM", startYM);
    qc.setObject("endYM", endYM);

    if ("1".equals(type)) {
      str = " SELECT AVG(T.HIS_RETURN) AS AVG_RETURN FROM ( " + sb.toString() + " ) T ";
    } else {
      str = sb.toString();
    }
    qc.setQueryString(str);
    List<Map<String, Object>> rateList = dam.exeQuery(qc);
    return rateList;

  }

  public void queryPrint(Object body, IPrimitiveMap<?> header) throws JBranchException, IOException {
    FPS200InputVO inputVO = (FPS200InputVO) body;
    dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    FPS200OutputVO outputVO = new FPS200OutputVO();

    sb.append(" SELECT TO_CHAR(LAST_RPT_DATE, 'YYYY/MM/DD HH24:MI:SS') AS LAST_RPT_DATE FROM TBFPS_PORTFOLIO_PLAN_RPT ");
    sb.append(" WHERE PLAN_ID = :planID ");
    qc.setObject("planID", inputVO.getPlanID());
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> printList = dam.exeQuery(qc);
    outputVO.setPrintList(printList);
    sendRtnObject(outputVO);
  }

  /**
   * 補印意向書
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws IOException
   */
  public void printPlan(Object body, IPrimitiveMap<?> header) throws JBranchException, IOException {
    FPS200InputVO inputVO = (FPS200InputVO) body;
    dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    SerialNumberUtil sn = new SerialNumberUtil();

    sb.append(" SELECT REPORT_ID,                ");
    sb.append("        PLAN_ID,                  ");
    sb.append("        CUST_ID,                  ");
    sb.append("        REPORT_DATE,              ");
    sb.append("        REMINDER,                 ");
    sb.append("        PLAN_PDF_FILE,            ");
    sb.append("        LAST_RPT_DATE             ");
    sb.append("   FROM TBFPS_PORTFOLIO_PLAN_RPT  ");
    sb.append("  WHERE PLAN_ID = :planID         ");
    qc.setObject("planID", inputVO.getPlanID());
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> rptList = dam.exeQuery(qc);

    if (!rptList.isEmpty()) {

      String newRptID = "";
      try {
        newRptID = sdf.format(new Date())
            + sn.getNextSerialNumber("INV_RPT_ID");
      } catch (Exception e) {
        sn.createNewSerial("INV_RPT_ID", "00000", Integer.valueOf(1), String.valueOf('d'), new Timestamp(System.currentTimeMillis()), 1, new Long(
            "99999"), "y", new Long("1"), null);
        newRptID = "RPT" + sdf.format(new Date()) + sn.getNextSerialNumber("INV_RPT_ID");
      }
      // 留存補印的記錄
      TBFPS_PORTFOLIO_PLAN_RPTPK pk = new TBFPS_PORTFOLIO_PLAN_RPTPK();
      pk.setPLAN_ID(inputVO.getPlanID());
      pk.setREPORT_ID(newRptID);
      TBFPS_PORTFOLIO_PLAN_RPTVO vo = new TBFPS_PORTFOLIO_PLAN_RPTVO();
      vo.setcomp_id(pk);
      vo.setCUST_ID(inputVO.getCustID());
      vo.setREPORT_DATE((Date) rptList.get(0).get("REPORT_DATE"));
      vo.setREMINDER((String) rptList.get(0).get("REMINDER"));
      vo.setPLAN_PDF_FILE((Blob) rptList.get(0).get("PLAN_PDF_FILE"));
      vo.setLAST_RPT_DATE(new Date());
      dam.create(vo);

      // 下載意向書
      String tempPath = (String) getCommonVariable(SystemVariableConsts.TEMP_PATH);
      String tempPathRelative = (String) getCommonVariable(SystemVariableConsts.TEMP_PATH_RELATIVE);
      // 建立唯一檔名
      UUID uuid = UUID.randomUUID();
      String tempFileName = String.format("%s_%d", uuid.toString(),
          System.currentTimeMillis()); // 唯一檔名
      // 將資料庫檔案轉回真實檔案, 提供下載位置
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(new File(tempPath, tempFileName));

        byte[] fileBytes = ObjectUtil.blobToByteArr((Blob) rptList.get(0).get("PLAN_PDF_FILE"));

        fos.write(fileBytes, 0, fileBytes.length);

        String fileUrl = tempPathRelative + tempFileName;

        notifyClientToDownloadFile(fileUrl, "財富管理投資意向書.pdf");
      } catch (Exception e) {
        throw new JBranchException(e.getMessage(), e);
      } finally {
        if (fos != null) {
          try {
            fos.close();
          } catch (IOException e) {
            logger.error(e.getMessage(), e);
          }
        }
      }
    }
    sendRtnObject(null);
  }

  // Juan

  // 查詢客戶
  @SuppressWarnings("unchecked")
  public void searchCust(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
    FPS200InputVO inputVO = (FPS200InputVO) body;
    DataAccessManager dam = this.getDataAccessManager();
    FPS200OutputVO outputVO = new FPS200OutputVO();
    outputVO.setOutputList(queryCust(dam, inputVO));

    ResultIF pageList = queryCust(dam, inputVO);
    outputVO.setOutputList(pageList);
    outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
    outputVO.setTotalPage(pageList.getTotalPage());// 總頁次
    outputVO.setTotalRecord(pageList.getTotalRecord());// 總筆數

    sendRtnObject(outputVO);
  }

  private ResultIF queryCust(DataAccessManager dam, FPS200InputVO inputVO) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT CUST_ID , CUST_NAME ");
    sb.append("FROM TBCRM_CUST_MAST ");
    sb.append("WHERE 1 = 1 ");

    if (inputVO.getAoCode().length > 0) {
      sb.append("AND (AO_CODE IN (:aoCode) OR AO_CODE IS NULL) ");
      qc.setObject("aoCode", inputVO.getAoCode());
    }

    if (StringUtils.isNotBlank(inputVO.getBranchID()) && !("000").equals(inputVO.getBranchID())) {
      qc.setObject("branch", inputVO.getBranchID());
      sb.append("AND BRA_NBR = :branch ");
    }

    if (StringUtils.isNotBlank(inputVO.getCustID())) {
      sb.append("AND CUST_ID LIKE :custID ");
      qc.setObject("custID", "%" + inputVO.getCustID() + "%");
    }
    sb.append("ORDER BY CUST_ID");

    qc.setQueryString(sb.toString());

    return dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
  }

  @SuppressWarnings("unchecked")
  public Boolean doCustIns(DataAccessManager dam, String custID, TBFPS_CUST_LISTVO vo) throws JBranchException, ParseException {
    String loginBraNbr = SysInfo.getInfoValue(SystemVariableConsts.LOGINBRH).toString();
    ArrayList<String> loginAO = (ArrayList<String>) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
    try {
      BigDecimal totalInsYearFee3 = ins110.getTotalInsyearfee(custID, loginBraNbr, loginAO, "3");
      BigDecimal totalInsYearFee1 = ins110.getTotalInsyearfee(custID, loginBraNbr, loginAO, "1");
      // update TBFPS_CUST_LIST
      if (vo != null && (vo.getINS_YEAR_AMT_1().compareTo(totalInsYearFee3) != 0 || vo.getINS_YEAR_AMT_2().compareTo(totalInsYearFee1) != 0)) {
        vo.setINS_YEAR_AMT_1(totalInsYearFee3);
        vo.setINS_YEAR_AMT_2(totalInsYearFee1);
        // dam.update(vo);
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  // 2018/4/30 貸款電文
  @SuppressWarnings("unchecked")
  public Boolean doCustLoan(DataAccessManager dam, String cust_id, TBFPS_CUST_LISTVO list_vo) throws DAOException, JBranchException {
    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer head_sql = new StringBuffer();
    head_sql.append("select LNAC_ACCT1, LNAC_NAME from TBCRM_DEPT_LOAN ");
    head_sql.append("where CUST_ID = :cust_id ");
    head_sql.append("and TO_DATE(LNAC_EDATE, 'yyyyMMdd') > TRUNC(SYSDATE) ");
    queryCondition.setObject("cust_id", cust_id);
    try {
      // 房貸
      StringBuffer house_sql = new StringBuffer();
      house_sql.append(head_sql).append("and substr(LNAC_CLASS_1 || LNAC_SUBCLASS_1, 0 , 2) = 'B1' ");
      queryCondition.setQueryString(house_sql.toString());
      List<Map<String, Object>> house_list = dam.exeQuery(queryCondition);
      BigDecimal house_amt = new BigDecimal(0);
      for (Map<String, Object> map : house_list) {
        List<EB372602OutputVO> eb372602_list = this.doEB372602(map);
        for (EB372602OutputVO vo : eb372602_list) {
          if (StringUtils.isBlank(vo.getAR_TOTAL_AMT()))
            house_amt = house_amt.add(new BigDecimal(0));
          else
            house_amt = house_amt.add(new BigDecimal(vo.getAR_TOTAL_AMT().trim().replace(",", "")));
        }
      }
      // 信貸
      StringBuffer trust_sql = new StringBuffer();
      trust_sql.append(head_sql).append("and substr(LNAC_CLASS_1 || LNAC_SUBCLASS_1, 0 , 2) in ('BY', 'BV') ");
      queryCondition.setQueryString(trust_sql.toString());
      List<Map<String, Object>> trust_list = dam.exeQuery(queryCondition);
      BigDecimal trust_amt = new BigDecimal(0);
      for (Map<String, Object> map : trust_list) {
        List<EB372602OutputVO> eb372602_list = this.doEB372602(map);
        for (EB372602OutputVO vo : eb372602_list) {
          if (StringUtils.isBlank(vo.getAR_TOTAL_AMT()))
            trust_amt = trust_amt.add(new BigDecimal(0));
          else
            trust_amt = trust_amt.add(new BigDecimal(vo.getAR_TOTAL_AMT().trim().replace(",", "")));
        }
      }
      // 學貸
      StringBuffer study_sql = new StringBuffer();
      study_sql.append(head_sql).append("and substr(LNAC_CLASS_1 || LNAC_SUBCLASS_1, 0 , 2) = 'BW' ");
      queryCondition.setQueryString(study_sql.toString());
      List<Map<String, Object>> study_list = dam.exeQuery(queryCondition);
      BigDecimal study_amt = new BigDecimal(0);
      for (Map<String, Object> map : study_list) {
        List<EB372602OutputVO> eb372602_list = this.doEB372602(map);
        for (EB372602OutputVO vo : eb372602_list) {
          if (StringUtils.isBlank(vo.getAR_TOTAL_AMT()))
            study_amt = study_amt.add(new BigDecimal(0));
          else
            study_amt = study_amt.add(new BigDecimal(vo.getAR_TOTAL_AMT().trim().replace(",", "")));
        }
      }
      // update TBFPS_CUST_LIST
      if (list_vo != null && (list_vo.getLN_YEAR_AMT_1().compareTo(house_amt) != 0 ||
          list_vo.getLN_YEAR_AMT_2().compareTo(trust_amt) != 0 ||
          list_vo.getLN_YEAR_AMT_3().compareTo(study_amt) != 0)) {
        list_vo.setLN_YEAR_AMT_1(house_amt);
        list_vo.setLN_YEAR_AMT_2(trust_amt);
        list_vo.setLN_YEAR_AMT_3(study_amt);
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public List<EB372602OutputVO> doEB372602(Map<String, Object> map) throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    ESBUtilInputVO esbUtilInputVO = getTxInstance(EsbFmpJRunConfiguer.ESB_TYPE, EB372602_DATA);
    esbUtilInputVO.setModule(this.getClass().getSimpleName() + "." + new Object() {
    }.getClass().getEnclosingMethod().getName());
    // head
    TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
    esbUtilInputVO.setTxHeadVO(txHead);
    txHead.setDefaultTxHead();
    // body
    EB372602InputVO ebVO = new EB372602InputVO();
    esbUtilInputVO.setEb372602InputVO(ebVO);
    ebVO.setFUNC("1");
    ebVO.setACNO_LN(ObjectUtils.toString(map.get("LNAC_ACCT1")));
    ebVO.setRCV_DATE(sdf.format(new Date()));
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, 1);
    cal.add(Calendar.DATE, -1);
    ebVO.setINT_DATE(sdf.format(cal.getTime()));
    ebVO.setDETAIL_FLG("N");
    // 發送電文
    List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);
    List<EB372602OutputVO> eb372602_list = new ArrayList<EB372602OutputVO>();
    for (ESBUtilOutputVO vo : esbUtilOutputVO) {
      EB372602OutputVO eb372602OutputVO = vo.getEb372602OutputVO();
      eb372602_list.add(eb372602OutputVO);
    }

    return eb372602_list;
  }

  //

  // 有無投保
  @SuppressWarnings("unchecked")
  public boolean hasInvest(DataAccessManager dam, String planID) throws NumberFormatException, DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    
    //2020-1-31 by Jacky 新增奈米投
    sb.append(" SELECT NVL(MFD_PROD_AMT + ETF_PROD_AMT + INS_PROD_AMT + BOND_PROD_AMT + SN_PROD_AMT + SI_PROD_AMT + NANO_PROD_AMT, 0) AS CNT");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_INV_HEAD");
    sb.append(" WHERE PLAN_ID = :planID");

    qc.setObject("planID", planID);
    qc.setQueryString(sb.toString());
    return Integer.parseInt(((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("CNT").toString()) > 0;
  }

  // 儲存
  public String exeSave(Object body) throws NumberFormatException, JBranchException {
    FPS200InputVO inputVO = (FPS200InputVO) body;
    String planID = inputVO.getPlanID();
    String custID = inputVO.getCustID();
    DataAccessManager dam = this.getDataAccessManager();

    // if no planID in create
    if (("create").equals(inputVO.getAction()) && !StringUtils.isNotBlank(planID)) {
      Calendar cal = Calendar.getInstance();
      String year = String.valueOf(cal.get(Calendar.YEAR));
      String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
      String date = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
      planID = "INV" + year + month + date + String.format("%05d", Long.parseLong(getSEQ(dam, "FPS_INV")));
    }

    if (StringUtils.isNotBlank(inputVO.getAction()) && StringUtils.isNotBlank(planID)) {
      switch (inputVO.getAction()) {
      case "create":
        create(body, dam, planID);
        clearEffectivePlans(dam, custID, planID);
        break;
      case "update":
        update(body, dam, planID);
        break;
      case "delete":
        delete(body, dam, planID);
        break;
      }
    } else {
      throw new APException("no Action or planID"); // error xml
    }

    return planID;
  }

  // 新增
  protected void create(Object body, DataAccessManager dam, String planID) throws DAOException, JBranchException {
  }

  // 更新
  protected void update(Object body, DataAccessManager dam, String planID) throws DAOException, JBranchException {
  }

  // 刪除
  protected void delete(Object body, DataAccessManager dam, String planID) throws DAOException, JBranchException {
  }

  // 刪除計畫資料by planID
  public boolean deletePlanDetails(DataAccessManager dam, String planID) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    try {
      sb.append(" DELETE FROM TBFPS_PORTFOLIO_PLAN_INV");
      sb.append(" WHERE PLAN_ID = :planID");
      qc.setObject("planID", planID);

      qc.setQueryString(sb.toString());
      dam.exeUpdate(qc);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private void clearEffectivePlans(DataAccessManager dam, String custID, String planID) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append(" UPDATE TBFPS_PORTFOLIO_PLAN_INV_HEAD");
    sb.append(" SET VALID_FLAG = 'N4'");
    sb.append(" WHERE VALID_FLAG = 'Y'");
    sb.append(" AND CUST_ID = :custID");
    sb.append(" AND PLAN_ID != :planID");
    qc.setObject("custID", custID);
    qc.setObject("planID", planID);

    qc.setQueryString(sb.toString());
    dam.exeUpdate(qc);
  }

  // 取得擁有key的資料
  public List<Map<String, Object>> filterList(List<Map<String, Object>> ls, String key) {
    List<Map<String, Object>> filterLs = new ArrayList<Map<String, Object>>();

    for (Map<String, Object> item : ls) {
      if (key.equals(item.get("INV_PRD_TYPE").toString()) && !"BND".equals(item.get("PTYPE")) && !"SN".equals(item.get("PTYPE")) && !"SI".equals(item.get("PTYPE"))) {
        Map<String, Object> newItem = new HashMap<String, Object>();
        for (Entry<String, Object> entry : item.entrySet()) {
          newItem.put(entry.getKey(), entry.getValue());
        }
        filterLs.add(newItem);
      }
    }

    return filterLs;
  }

  // 計算新商品權重
  public List<Map<String, Object>> formatWeightMap(List<Map<String, Object>> ls) {
    List<Map<String, Object>> wmLs = new ArrayList<Map<String, Object>>();
    // calculate total
    BigDecimal totalAmount = totalAmount(ls);

    // group items
    Map<String, Map<String, Object>> groupMap = new HashMap<String, Map<String, Object>>();
    for (Map<String, Object> i : ls) {
      if (groupMap.containsKey(i.get("PRD_ID") + "-" + i.get("PTYPE"))) {
        Map<String, Object> tmp = groupMap.get(i.get("PRD_ID") + "-" + i.get("PTYPE"));
        tmp.put("PURCHASE_TWD_AMT", ((BigDecimal) tmp.get("PURCHASE_TWD_AMT")).add((BigDecimal) i.get("PURCHASE_TWD_AMT")));
        if (tmp.get("TARGETS") != null && i.get("TARGETS") != null) {
        	List<String> list =  new ArrayList<String>();
        	List<String> a = Arrays.asList(ObjectUtils.toString(tmp.get("TARGETS")).split("/"));
        	String[] b = ObjectUtils.toString(i.get("TARGETS")).split("/");
        	for (String c : b) {
        		if (!a.contains(c)) {
        			for (String target : a) {
        				list.add(target);
        			}
        			list.add(c);
	        	}
        	}
        	tmp.put("TARGETS", list.toArray(new String[list.size()]));
        }
        groupMap.put(i.get("PRD_ID") + "-" + i.get("PTYPE"), tmp);
      } else {
        groupMap.put(i.get("PRD_ID") + "-" + i.get("PTYPE"), i);
      }
    }

    // set format
    for (Entry<String, Map<String, Object>> item : groupMap.entrySet()) {
      Map<String, Object> wm = new HashMap<String, Object>();
      wm.put("PRD_ID", item.getValue().get("PRD_ID"));
      wm.put("PRD_TYPE", item.getValue().get("PTYPE"));
      wm.put("TARGETS", item.getValue().get("TARGETS"));
      if (totalAmount.compareTo(BigDecimal.ZERO) == 1) {
    	  wm.put("WEIGHT", new BigDecimal(ObjectUtils.toString(item.getValue().get("PURCHASE_TWD_AMT"))).divide(totalAmount, 4, BigDecimal.ROUND_HALF_UP));    	  
      } else {
    	  wm.put("WEIGHT", BigDecimal.ZERO);
      }
      wmLs.add(wm);
    }

    return wmLs;
  }

  // 取得總比例
  private BigDecimal totalAmount(List<Map<String, Object>> mfdEtfInsList) {
    BigDecimal totalPercent = BigDecimal.ZERO;
    for (Map<String, Object> map : mfdEtfInsList) {
      totalPercent = totalPercent.add(new BigDecimal(ObjectUtils.toString(map.get("PURCHASE_TWD_AMT"))).setScale(2, BigDecimal.ROUND_HALF_UP));
    }
    return totalPercent;
  }

  // 產生流水號
  @SuppressWarnings("unchecked")
  public String getSEQ(DataAccessManager dam, String type) throws JBranchException {
    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sql = new StringBuffer();
    if (type.equals("FPS_INV")) {
      sql.append("SELECT TBFPS_PORTFOLIO_PLAN_INV_H_SEQ.NEXTVAL AS SEQ FROM DUAL");
    } else if (type.equals("FPS_INV_DETAIL")) {
      sql.append("SELECT TBFPS_PORTFOLIO_PLAN_INV_SEQ.NEXTVAL AS SEQ FROM DUAL");
    }
    queryCondition.setQueryString(sql.toString());
    List<Map<String, Object>> seqNO = dam.exeQuery(queryCondition);
    return seqNO.get(0).get("SEQ").toString();
  }

  // 取得當前的前一個營業日
  public String getPrevBussinessDay(DataAccessManager dam) throws DAOException, JBranchException {
	  QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	  StringBuffer sql = new StringBuffer();
      sql.append("SELECT PABTH_UTIL.FC_getBusiDay(sysdate, 'TWD', -1)AS PREV_BUSI_DAY FROM DUAL");
	  queryCondition.setQueryString(sql.toString());
	  List<Map<String, Object>> busDayList = dam.exeQuery(queryCondition);
	  Date busDay = CollectionUtils.isNotEmpty(busDayList) ? new GenericMap(busDayList.get(0)).getDate("PREV_BUSI_DAY") : new Date(new Date().getTime() - 2);;
	  return new SimpleDateFormat("yyyy/MM/dd").format(busDay);
  }
}
