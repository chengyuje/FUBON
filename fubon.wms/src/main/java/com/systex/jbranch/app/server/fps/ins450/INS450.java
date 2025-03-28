package com.systex.jbranch.app.server.fps.ins450;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.lowagie.text.DocumentException;
import com.systex.jbranch.app.common.fps.table.TBINS_REPORTVO;
import com.systex.jbranch.app.common.fps.table.TBINS_SPP_MAINVO;
import com.systex.jbranch.app.server.fps.ins.parse.WSMappingParserUtils;
import com.systex.jbranch.app.server.fps.ins400.INS400;
import com.systex.jbranch.app.server.fps.ins810.FubonIns810;
import com.systex.jbranch.app.server.fps.ins810.PolicySuggestInputVO;
import com.systex.jbranch.app.server.fps.insjlb.FubonInsjlb;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremOutputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import com.systex.jbranch.ws.external.service.ExternalErrorMsg;

/**
 * INS450
 * 
 * @author Loui
 * @date 2017/11/23
 */
@Component("ins450")
@Scope("request")
public class INS450 extends FubonWmsBizLogic {
  private SimpleDateFormat SDF_YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
  @Autowired
  @Qualifier("ins810")
  private FubonIns810 ins810;
  
  public FubonIns810 getIns810() {
	  return ins810;
  }

  public void setIns810(FubonIns810 ins810) {
	  this.ins810 = ins810;
  }

  @Autowired
  @Qualifier("ins400")
  private INS400 ins400;

  @Autowired
  @Qualifier("insjlb")
  private FubonInsjlb insjlb;

  public FubonInsjlb getInsjlb() {
    return insjlb;
  }

  public void setInsjlb(FubonInsjlb insjlb) {
    this.insjlb = insjlb;
  }

  /**
   * INS450退休試算
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  @SuppressWarnings("unchecked")
  public void retiredInit(Object body, IPrimitiveMap header) throws JBranchException {
    INS450InputVO inputVO = (INS450InputVO) body;
    INS450OutputVO outputVO = new INS450OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = null;
    StringBuffer sb = null;

    qc = dam.getQueryCondition(dam.QUERY_LANGUAGE_TYPE_VAR_SQL);
    sb = new StringBuffer();
    sb.append("SELECT LABOR_INS_AMT1,LABOR_INS_AMT2,PENSION1,PENSION2 ");
    sb.append("FROM TBINS_SPP_MAIN ");
    sb.append("WHERE cust_id = :custID AND SPP_TYPE = '2' ");
    sb.append("ORDER BY LASTUPDATE DESC FETCH FIRST 1 ROWS ONLY ");
    qc.setObject("custID", inputVO.getCustID());
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> list = dam.exeQuery(qc);
    outputVO.setOutputList1(list);// 回傳資料

    qc = dam.getQueryCondition(dam.QUERY_LANGUAGE_TYPE_VAR_SQL);
    sb = new StringBuffer();
    sb.append("SELECT TYPE, NVL(SUM(ARTL_DEBT_AMT_ONCE),0) as ARTL_DEBT_AMT_ONCE, NVL(SUM(ARTL_DEBT_AMT_MONTHLY),0) as ARTL_DEBT_AMT_MONTHLY FROM ( ");
    sb.append("SELECT DECODE(PRD_KEYNO, NULL, 'A', 'B') TYPE, ARTL_DEBT_AMT_ONCE, ARTL_DEBT_AMT_MONTHLY FROM TBINS_SPP_INSLIST ");
    sb.append("WHERE CUST_ID = :custID) ");
    sb.append("GROUP BY TYPE");

    qc.setObject("custID", inputVO.getCustID());
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> list1 = dam.exeQuery(qc);
    outputVO.setOutputList(list1);// 回傳資料

    this.sendRtnObject(outputVO);
  }

  /**
   * INS450保額計算
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws ParseException
   */
  public void getPolicyFEE(Object body, IPrimitiveMap<Object> header) throws JBranchException {

    INS450InputVO inputVO = (INS450InputVO) body;
    INS450OutputVO outputVO = new INS450OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    // 取保額
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT KEY_NO,PRD_ID,POLICY_AMT_MIN, POLICY_AMT_MAX, POLICY_AMT_DISTANCE,CVRG_RATIO,INSPRD_ANNUAL ");
    sb.append("FROM VWPRD_INS_SUGGEST ");
    sb.append("WHERE  STATUS = 'A' AND SYSDATE BETWEEN EFFECT_DATE AND EXPIRY_DATE ");
    sb.append("AND KEY_NO = :keyno ");
    sb.append("ORDER BY EFFECT_DATE DESC FETCH FIRST ROWS ONLY ");

    queryCondition.setObject("keyno", inputVO.getKeyno());
    queryCondition.setQueryString(sb.toString());

    outputVO.setPolicyFEE(getInsured(inputVO));
    List<Map<String, Object>> list = dam.exeQuery(queryCondition);
    outputVO.setOutputList(list);// 回傳資料
    sendRtnObject(outputVO);

  }

  // 取費率，計算保費用
  public BigDecimal getInsured(INS450InputVO inputVO) throws JBranchException {
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(dam.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append("SELECT RAT.PREM_RATE ");
    sb.append("FROM TBPRD_INS_RATE RAT ");
    sb.append("LEFT JOIN TBPRD_INS	INS ON INS.KEY_NO = RAT.F_KEY_NO ");
    sb.append("WHERE 1 = 1");
    sb.append("AND NVL(RAT.SEX, :gender) = :gender ");
    sb.append("AND RAT.AGE = :age ");
    sb.append("AND INS.PRD_ID = :prdID ");
    sb.append("AND INS.INSPRD_ANNUAL = :annual ");
    sb.append("AND INS.CURR_CD = :currCD ");
    sb.append("AND RAT.PAY_TYPE = 'A' ");

    qc.setObject("gender", (StringUtils.equals("1", inputVO.getGender()) ? "M" : "F"));
    qc.setObject("age", inputVO.getAge());
    qc.setObject("prdID", inputVO.getInsprdID());
    qc.setObject("annual", inputVO.getPayYear());
    qc.setObject("currCD", inputVO.getCurrCD());
    qc.setQueryString(sb.toString());
    List<Map<String, Object>> rate = dam.exeQuery(qc);
    BigDecimal premRate = null;
    if (rate.size() > 0) {
      return premRate = (BigDecimal) rate.get(0).get("PREM_RATE");
    } else {
      return premRate;
    }
  }

  /**
   * 檢查重複命名 取得查詢結果
   * 
   * @param body
   * @param header
   * @return
   * @throws JBranchException
   */
  @SuppressWarnings("unchecked")
  public int checkNameMain(Object body, IPrimitiveMap header) throws JBranchException {
    INS450InputVO inputVO = (INS450InputVO) body;
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT COUNT(*) AS CHECKER ");
    sb.append(" FROM TBINS_SPP_MAIN ");
    sb.append(" WHERE SPP_NAME = :sppName AND CUST_ID = :custID ");
    qc.setObject("sppName", inputVO.getSppName());
    qc.setObject("custID", inputVO.getCustID());

    // 查詢結果
    qc.setQueryString(sb.toString());
    int checker = Integer.parseInt(((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("CHECKER").toString());

    return checker;
  }

  @SuppressWarnings("unchecked")
  public void checkName(Object body, IPrimitiveMap header) throws JBranchException {
    INS450InputVO inputVO = (INS450InputVO) body;
    sendRtnObject(checkNameMain(inputVO, header));
  }

  /**
   * 保險特定規劃2階API-檢查重複命名
   * 
   * @param body
   * @param header
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public void checkInsurancePlanName(Object body, IPrimitiveMap header) throws Exception {
    Map inputMap = (Map) body;
    INS450InputVO inputVO = new INS450InputVO();
    inputVO.setCustID(ObjectUtils.toString(inputMap.get("custId")));
    inputVO.setSppName(ObjectUtils.toString(inputMap.get("sppName")));
    int i = checkNameMain(inputVO, header);
    if (i == 0) {
      this.sendRtnObject(new HashMap());
    } else {
      // throw new Exception("特定目的規劃名稱重複");
      sendRtnObject(ExternalErrorMsg.getInstance("9999", "特定目的規劃名稱重複"));
    }
  }

  /**
   * 入口
   * */
  public void action(Object body, IPrimitiveMap<Object> header) throws JBranchException {

    INS450InputVO inputVO = (INS450InputVO) body;
    DataAccessManager dam = this.getDataAccessManager();

    this.sendRtnObject(save(dam, inputVO));
  }

  /**
   * 儲存
   * */
  public String save(DataAccessManager dam, INS450InputVO inputVO) throws JBranchException {

    TBINS_SPP_MAINVO vo = new TBINS_SPP_MAINVO();

    // 共通欄位
    if (ObjectUtils.toString(inputVO.getSppID()).isEmpty()) {
      String sppID = getSEQ(dam);
      vo.setSPP_ID(sppID);
    } else {
      vo.setSPP_ID(inputVO.getSppID());
    }
    vo.setCUST_ID(inputVO.getCustID());
    vo.setCUST_RISK_ATR(inputVO.getCustRiskATR());		// 客戶風險屬性
    vo.setSTATUS("2");									// 列印與投保為 2:已完成規劃
    vo.setSPP_TYPE(inputVO.getSppType());				// 特定規劃項目
    vo.setSPP_NAME(inputVO.getSppName());				// 規劃名稱

    if (StringUtils.equals("2", inputVO.getSppType())) {
      vo.setD_AMT1(inputVO.getdAMT1());
      vo.setAVG_LIFE(inputVO.getAvgLife());
      vo.setAVG_RETIRE(inputVO.getAvgRetire());
      vo.setLABOR_INS_AMT1(inputVO.getLaborINSAMT1());
      vo.setLABOR_INS_AMT2(inputVO.getLaborINSAMT2());
      vo.setPENSION1(inputVO.getPerson1());
      vo.setPENSION2(inputVO.getPerson2());
      vo.setOTH_INS_AMT1(inputVO.getOthINSAMT1());
      vo.setOTH_INS_AMT2(inputVO.getOthINSAMT2());
      vo.setOTH_AMT1(inputVO.getOthAMT1());
      vo.setOTH_AMT2(inputVO.getOthAMT2());
    } else {
      vo.setD_YEAR(inputVO.getdYear());
      vo.setD_AMT2(inputVO.getdAMT2());
    }

    vo.setINSPRD_ID(inputVO.getInsprdID());					// 險種代碼
    vo.setINSPRD_KEYNO(inputVO.getInsprdKEYNO());			// 險種主鍵
    vo.setPAY_YEARS(new BigDecimal(inputVO.getPayYear()));	// 繳費年期
    vo.setCURR_CD(inputVO.getCurrCD());						// 幣別
    vo.setPOLICY_ASSURE_AMT(inputVO.getPolicyAssureAMT());	// 保額
    vo.setUNIT(inputVO.getUnit());							// 單位
    vo.setPOLICY_FEE(inputVO.getPolicyFEE());				// 保費
    
//    System.out.println(new Gson().toJson(inputVO));
//    System.out.println(new Gson().toJson(vo));

    dam.create(vo);
    return vo.getSPP_ID();

  }

  /**
   * 保險特定規劃2階API-用商品key抓取商品資料 取得查詢結果
   * 
   * @param body
   * @param header
   * @return
   * @throws JBranchException
   */
  public List getPrdInfo(BigDecimal prdKeyNo) throws JBranchException {
    // INS450InputVO inputVO = (INS450InputVO) body;
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT PRD_ID,INSPRD_ANNUAL,CURR_CD,PRD_UNIT ");
    sb.append("FROM TBPRD_INS ");
    sb.append("WHERE KEY_NO = :prdKeyNo ");
    qc.setObject("prdKeyNo", prdKeyNo);
    qc.setQueryString(sb.toString());

    return dam.exeQuery(qc);
  }

  /**
   * 保險特定規劃2階API-儲存保險規劃-特定目的
   * 
   * @param body
   * @param header
   * @throws JBranchException
   */
  public void saveInsurancePurposePlan(Object body, IPrimitiveMap header) throws JBranchException, Exception {
    Map inputMap = (Map) body;
    DataAccessManager dam = this.getDataAccessManager();
    INS450InputVO inputVO = new INS450InputVO();
    inputVO.setCustID(ObjectUtils.toString(inputMap.get("custId")));
    List<Map<String, Object>> custlist = ins400.inquire(inputVO.getCustID(), dam,
        (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
    if(CollectionUtils.isNotEmpty(custlist) && MapUtils.isNotEmpty(custlist.get(0))) {
    	inputVO.setCustRiskATR(ObjectUtils.toString(custlist.get(0).get("CUST_RISK_ATR"), null));    	
    }
    inputVO.setSppID(ObjectUtils.toString(inputMap.get("sppId")));
    // inputVO.setParaNO(ObjectUtils.toString(inputMap.get("parameterVer")));
    inputVO.setSppType(ObjectUtils.toString(new GenericMap(inputMap).getBigDecimal("sppType")));
    inputVO.setStatus(ObjectUtils.toString(new GenericMap(inputMap).getBigDecimal("status")));
    // 規劃項目
    GenericMap requiredInfoList = new GenericMap((Map) inputMap.get("requiredInfo"));
    inputVO.setSppName(requiredInfoList.getStr("sppName"));
    inputVO.setAge(ObjectUtils.toString(requiredInfoList.getBigDecimal("age")));
    inputVO.setGender(ObjectUtils.toString(requiredInfoList.getBigDecimal("gender")));
    inputVO.setdYear(requiredInfoList.getBigDecimal("dYear"));
    inputVO.setdAMT2(requiredInfoList.getBigDecimal("dAMT2"));
    // 商品推薦
    GenericMap recommandInfo = new GenericMap((Map) inputMap.get("recommand"));
    inputVO.setInsprdKEYNO(recommandInfo.getBigDecimal("prdKeyNo"));

    GenericMap inputPrd = new GenericMap((Map) getPrdInfo(inputVO.getInsprdKEYNO()).get(0));
    inputVO.setInsprdID(inputPrd.getStr("PRD_ID"));
    inputVO.setPayYear(inputPrd.getBigDecimal("INSPRD_ANNUAL").toString());
    inputVO.setCurrCD(inputPrd.getStr("CURR_CD"));
    inputVO.setUnit(inputPrd.getStr("PRD_UNIT"));
    inputVO.setPolicyAssureAMT(recommandInfo.getBigDecimal("insuredamt"));
    inputVO.setPolicyFEE(recommandInfo.getBigDecimal("insyearfee"));

    if (inputVO.getSppType().equals("2")) {
      // 金額詳細資訊
      GenericMap amountInfo = new GenericMap((Map) requiredInfoList.getParamMap().get("amountInfo"));
      inputVO.setAvgRetire(amountInfo.getBigDecimal("retireAge"));
      inputVO.setAvgLife(amountInfo.getBigDecimal("avgLife"));
      inputVO.setdAMT1(amountInfo.getBigDecimal("retireSpend"));
      GenericMap socialInsuranceInfo = new GenericMap((Map) amountInfo.getParamMap().get("socialInsurance"));
      inputVO.setLaborINSAMT1(socialInsuranceInfo.getBigDecimal("monthly"));
      inputVO.setLaborINSAMT2(socialInsuranceInfo.getBigDecimal("once"));
      GenericMap socialWelfareInfo = new GenericMap((Map) amountInfo.getParamMap().get("socialWelfare"));
      inputVO.setPerson1(socialWelfareInfo.getBigDecimal("monthly"));
      inputVO.setPerson2(socialWelfareInfo.getBigDecimal("once"));
      GenericMap businessInsuranceInfo = new GenericMap((Map) amountInfo.getParamMap().get("businessInsurance"));
      inputVO.setOthINSAMT1(businessInsuranceInfo.getBigDecimal("monthly"));
      inputVO.setOthINSAMT2(businessInsuranceInfo.getBigDecimal("once"));
      GenericMap OtherInfo = new GenericMap((Map) amountInfo.getParamMap().get("socialInsurance"));
      inputVO.setOthAMT1(OtherInfo.getBigDecimal("monthly"));
      inputVO.setOthAMT2(OtherInfo.getBigDecimal("once"));

    } else if (("1").equals(inputVO.getSppType())) {
      List<Map<String, Object>> childEduList = new GenericMap((Map) requiredInfoList.getParamMap()).get("amountInfo");
      if (CollectionUtils.isNotEmpty(childEduList)) {
        ins400.saveChildDataForMobile(ObjectUtils.toString(inputMap.get("custId")), childEduList);
      }
    }

    String outVo = save(dam, inputVO);
    this.sendRtnObject(outVo);
  }
  
  /**
   * 保險特定規劃2階API-取得保險規劃-特定目的參數
   * 
   * @param body
   * @param header
   * @throws JBranchException
 * @throws ParseException 
   */
	public void getInsurancePurposeRefData(Object body,IPrimitiveMap<Object> header) throws DAOException, JBranchException, ParseException {
		GenericMap inputMap = new GenericMap((Map) body);
		System.out.println(new Gson().toJson(inputMap.getParamMap()));
		
		String sppType = String.valueOf(inputMap.getBigDecimal("sppType").intValue());
		String insuranceAge = ObjectUtils.toString(inputMap.getBigDecimal("insuranceAge").intValue());
		
		switch(sppType){
		case "1" : {
			sppType = "6";
			break;
		}
		case "2" : {
			sppType = "5";
			break;
		}
		case "3" : {}
		case "4" : {}
		case "5" : {}
		case "6" : {}
		case "7" : {}
		case "8" : {
			sppType = "7";
			break;
		}
		default : break;
		}
		// 取得推薦商品
		PolicySuggestInputVO inputVO = new PolicySuggestInputVO();
		inputVO.setParaType(sppType);
		inputVO.setInsAge(insuranceAge);
		DataAccessManager dam = getDataAccessManager();
		List<Map<String, Object>> suggestPrdList = getIns810().getSuggestListDatas(
				getIns810().getSuggestPrdSql(null, null,null), inputVO, dam);

		// 重組 ws 要的格式
		Map<String, Object> finalResultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		finalResultMap.put("sppId", "SPP" + getIns810().getInsSeq(null).getInsSeq());
		finalResultMap.put("paraNo", inputVO.getParaNo()); // ??? 參數版號
		
		//將數字字串轉換成單位
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> unitMap = xmlInfo.doGetVariable("INS.UNIT", FormatHelper.FORMAT_3);

		// for ws 資料處理
		for (Map<String, Object> fromMap : suggestPrdList) {
			Boolean estatePlan = true;
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("prdId", fromMap.get("PRD_ID"));
			resultMap.put("prdName", fromMap.get("INSPRD_NAME"));
			resultMap.put("unit",unitMap.get(fromMap.get("PRD_UNIT")));
			resultMap.put("currCd", fromMap.get("CURR_CD"));
			resultMap.put("estatePlan", estatePlan = "Y".equals(fromMap.get("ESTATE_PLAN")) ? true : false);
			List<Map<String, Object>> prodInfoList = new ArrayList<Map<String, Object>>();

			// for ws 資料處理 2
			WSMappingParserUtils
					.suggestPaymentSelMapping(fromMap, prodInfoList);
			resultMap.put("productInfo", prodInfoList);
			resultList.add(resultMap);
		}

		finalResultMap.put("recommand", resultList);
		
		/**子女教育所需基金細項**/
		INS400 ins400 = (INS400) PlatformContext.getBean("ins400");
		List<Map<String, Object>> eduExplist = ins400.inquireChildDataForMobile(ObjectUtils.toString(inputMap.get("custId")));
		
		finalResultMap.put("etuDetail", eduExplist);
		
		System.out.println(new Gson().toJson(finalResultMap));
		this.sendRtnObject(finalResultMap);
	}

  /**
   * 建議書檢核
   * 
   * @param body
   * @param header
   * @throws JBranchException
   * @throws IOException
   */
  public void checker(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
    DataAccessManager dam = this.getDataAccessManager();
    INS450InputVO inputVO = (INS450InputVO) body;
    String checkType = inputVO.getCheckType();
    String planID = inputVO.getSppID();
    List<Map<String, Object>> checker = null;
    List<Map<String, Object>> report = null;
    Map<String, Object> custInfo = null;
    switch (checkType) {
    case "PRINT":
      checker = checkPrint(dam, planID, inputVO);
      report = getReport(planID, inputVO.getCustID());
      break;
    case "APP_PRINT" :
	  checker = checkPrint(dam, planID, inputVO);
      report = getReport(planID, inputVO.getCustID());
      custInfo = getCustInfoData(dam, planID, inputVO.getCustID());
      break;	
    }
    this.sendRtnObject(new Object[] { checker, report, custInfo });
  }
  
  private Map<String, Object> getCustInfoData(DataAccessManager dam, String planId, String custId) throws DAOException, JBranchException {
	  QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	  StringBuffer sb = new StringBuffer();
	  sb.append(" select CUST.CUST_NAME, CUST.BIRTH_DATE, ");
	  sb.append(" DECODE(SPPM.SPP_TYPE, '1','子女教育','2','退休規劃','3','購屋','4','購車','5','結婚','6','留遊學','7','旅遊','8','其他','錯誤') SPPTYPE, ");
	  sb.append(" (SPPM.LABOR_INS_AMT1 + SPPM.PENSION1 + SPPM.OTH_INS_AMT1 + SPPM.OTH_AMT1) + ROUND((SPPM.LABOR_INS_AMT2 + SPPM.PENSION2 + SPPM.OTH_INS_AMT2 + SPPM.OTH_AMT2)/(SPPM.AVG_LIFE - SPPM.AVG_RETIRE)/12,0) AVG_P, "); // 已被退休金，平均每月
	  sb.append(" D_AMT1 - ((SPPM.LABOR_INS_AMT1 + SPPM.PENSION1 + SPPM.OTH_INS_AMT1 + SPPM.OTH_AMT1) + ROUND((SPPM.LABOR_INS_AMT2 + SPPM.PENSION2 + SPPM.OTH_INS_AMT2 + SPPM.OTH_AMT2)/(SPPM.AVG_LIFE - SPPM.AVG_RETIRE)/12,0)) GAP "); // 缺口
	  sb.append(" from TBINS_SPP_MAIN SPPM ");
	  sb.append(" left join TBCRM_CUST_MAST CUST on SPPM.CUST_ID = CUST.CUST_ID "); 
	  sb.append(" where SPPM.SPP_ID in(:sppId) and SPPM.CUST_ID = :custId ");
	  
	  qc.setObject("sppId", planId);
	  qc.setObject("custId", custId);
	  qc.setQueryString(sb.toString());

	  List<Map<String, Object>> resultList = dam.exeQuery(qc);
	  if(CollectionUtils.isNotEmpty(resultList)) {
		  Map<String, Object> resultMap = resultList.get(0);
//		  int age = getIns810().getAge(new GenericMap(resultMap).getDate("BIRTH_DATE"));
		  //與web版邏輯一致，使用實際年齡
		  List<Map<String, Object>> ageList = exeQueryWithoutSortForQcf(genDefaultQueryConditionIF()
					.setQueryString(new StringBuilder()
					.append(" SELECT AGE FROM TBCRM_CUST_MAST ")
					.append(" where CUST_ID = :custId ").toString())
					.setObject("custId", custId));
		  resultMap.put("AGE", Integer.parseInt(ObjectUtils.toString(ageList.get(0).get("AGE"))));
		  return resultMap;
	  } else {
		  return null;
	  }
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> checkPrint(DataAccessManager dam, String planID, INS450InputVO inputVO) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);;
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT A.SPP_ID,A.CUST_ID,A.INS_KEYNO,A.CUST_RISK_ATR,LPAD(A.STATUS,2,0)STATUS,A.SPP_TYPE,A.INSPRD_KEYNO,A.SPP_NAME,A.D_AMT1,A.D_AMT2, ");
    sb.append("A.AVG_LIFE,A.AVG_RETIRE,A.LABOR_INS_AMT1,A.LABOR_INS_AMT2,A.PENSION1,A.PENSION2,A.OTH_INS_AMT1,A.OTH_INS_AMT2,A.OTH_AMT1, ");
    sb.append("A.OTH_AMT2,A.INSPRD_ID,A.PAY_YEARS,A.CURR_CD,A.POLICY_ASSURE_AMT,A.UNIT,A.POLICY_FEE,A.D_YEAR,INS.SALE_SDATE, ");
    sb.append("INS.PRD_ID, INS.INSPRD_NAME,INS.IS_ANNUITY,INS.IS_INCREASING,INS.IS_REPAY ");
    sb.append("FROM TBINS_SPP_MAIN　A, ");
    sb.append("TBPRD_INS INS ");
    sb.append("WHERE A.INSPRD_KEYNO = INS.KEY_NO ");
    sb.append("AND A.INSPRD_ID = INS.PRD_ID ");
    sb.append("AND A.SPP_ID = :sppID ");

    qc.setObject("sppID", inputVO.getSppID());
    qc.setQueryString(sb.toString());

    return dam.exeQuery(qc);
  }

  /**
   * generatePdf
   * 
   * @throws Exception
   */
  public void generatePdf(Object body, IPrimitiveMap<?> header) throws Exception {
    DataAccessManager dam = this.getDataAccessManager();
    INS450InputVO inputVO = (INS450InputVO) body;

    BigDecimal printSEQ = inputVO.getPrintSEQ();
    String planID = inputVO.getSppID();
    String sppType = inputVO.getSppType();
    String sppName = inputVO.getSppName();

    if (printSEQ.compareTo(BigDecimal.ZERO) == -1) {
      printSEQ = newPDFFile(dam, inputVO, planID);
    }

//    downloadFile(getPDFFile(dam, planID, printSEQ), planID, printSEQ, sppType, sppName);

    this.sendRtnObject(printSEQ);
  }

  /**
   * download Pdf
   * 
   * @throws Exception
   */
  public void downloadPdf(Object body, IPrimitiveMap<?> header) throws Exception {
    DataAccessManager dam = this.getDataAccessManager();
    INS450InputVO inputVO = (INS450InputVO) body;

    BigDecimal printSEQ = inputVO.getPrintSEQ();
    String planID = inputVO.getSppID();
    String sppType = inputVO.getSppType();
    String sppName = inputVO.getSppName();

//    if (printSEQ.compareTo(BigDecimal.ZERO) == -1) {
//      printSEQ = newPDFFile(dam, inputVO, planID);
//    }

    downloadFile(getPDFFile(dam, planID, printSEQ), planID, printSEQ, sppType, sppName);

    this.sendRtnObject(printSEQ);
  }
  
  private List<Map<String, Object>> getPDFFile(DataAccessManager dam, String planID, BigDecimal seq) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuilder sb = new StringBuilder();

//    sb.append("SELECT A.REPORT_FILE, A.FILE_NAME, A.KEYNO ");
//    sb.append("FROM TBINS_REPORT A ");
//    sb.append("WHERE A.KEYNO = :seq ");
    
    sb.append("SELECT A.*, B.CUST_ID FROM ( ");
    sb.append("SELECT PLAN_ID, REPORT_FILE, FILE_NAME, KEYNO ");
    sb.append("FROM TBINS_REPORT ");
    sb.append("WHERE KEYNO = :seq ");
    sb.append(") A ");
    sb.append("LEFT JOIN TBINS_SPP_MAIN B ON A.PLAN_ID = B.SPP_ID ");

    qc.setObject("seq", seq);
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  // public INS450OutputVO pdfFileForWs (Object body, IPrimitiveMap<?> header)
  // throws DAOException, JBranchException {
  // INS450InputVO inputVO = (INS450InputVO)body;
  // INS450OutputVO outputVO = new INS450OutputVO();
  // DataAccessManager dam = new DataAccessManager();
  // List<Map<String,Object>> result = getPDFFile(dam, inputVO.getSppID(), new
  // BigDecimal(inputVO.getKeyno()));
  // outputVO.getOutputList(result);
  // return null;
  //
  // }

  // do file
  private void downloadFile(List<Map<String, Object>> result, String planID, BigDecimal seq, String sppType, String sppName) throws JBranchException, DocumentException {
    if (!result.isEmpty()) {
    	List<String> url_list =  new ArrayList<String>();
    	String filename = (String) result.get(0).get("FILE_NAME");
    	byte[] blobarray;
    	try {
    		blobarray = ObjectUtil.blobToByteArr((Blob) result.get(0).get("REPORT_FILE"));
    	} catch (Exception e) {
    		throw new APException("文件下載錯誤！"); // 文件下載錯誤！
    	}
    	String temp_path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
    	String temp_path_relative = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH_RELATIVE);
    	FileOutputStream download_file = null;
    	try {
    		download_file = new FileOutputStream(new File(temp_path, filename));
    		download_file.write(blobarray);
    		// execLog(planID, seq, "P");
    	} catch (Exception e) {
    		throw new APException("文件下載錯誤！"); // 下載檔案失敗
    	} finally {
    		try {
    			download_file.close();
    		} catch (Exception e) {
    			// IGNORE
    		}
    	}
    	url_list.add("doc//INS//INS400_COVER.pdf");
		url_list.add(temp_path_relative + "//" + filename);
		
		String reportURL = "";
		// 合併所有PDF
		if (result.get(0).get("CUST_ID") != null) {
			// 加密碼
			reportURL = PdfUtil.mergePDF(url_list, result.get(0).get("CUST_ID").toString());
		} else {
			reportURL = PdfUtil.mergePDF(url_list, false);			
		}
		
		this.notifyClientToDownloadFile(reportURL, sppType + "_" + sppName + "_"  + "保險規劃書.pdf");
//    	notifyClientToDownloadFile(temp_path_relative + "//" + filename,  sppType + "_" + sppName + "_"  + "保險規劃書.pdf");
    } else {
      throw new APException("無可下載的檔案!"); // 無可下載的檔案
    }
  }

  // new file
  private BigDecimal newPDFFile(DataAccessManager dam, INS450InputVO inputVO, String planID) throws JBranchException {
    String custID = inputVO.getCustID();
    String fileName = inputVO.getFileName();
    String tempFileName = inputVO.getTempFileName();
    BigDecimal seqNo = new BigDecimal(getReportSEQ(dam));

    if (StringUtils.isNotBlank(planID)) {
      try {
        String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
        byte[] regularData = Files.readAllBytes(new File(tempPath, tempFileName).toPath());

        if (regularData.length <= 0) {
          throw new APException("無PDF資料"); //
        }
        Blob unEncryptBlob = ObjectUtil.byteArrToBlob(regularData);

        TBINS_REPORTVO vo = new TBINS_REPORTVO();
        vo.setKEYNO(seqNo);
        vo.setPLAN_ID(planID);
        vo.setFILE_NAME(fileName);
        vo.setREPORT_FILE(unEncryptBlob);
        dam.create(vo);
      } catch (SQLException | IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return seqNo;
  }

  private List<Map<String, Object>> getReport(String planID, String custID) throws JBranchException {
    return coverageList(planID);
  }

  /**
   * 保險特定規劃2階API-產生保險規劃書-特定目的
   * 
   * @param body
   * @param header
   * @throws JBranchException
   */
  /* 尚未測試，尚未完成 */
  @SuppressWarnings("unchecked")
  public void generateInsurancePurposePlan(Object body, IPrimitiveMap header) throws Exception {
    Map inputMap = (Map) body;
    INS450InputVO inputVO = new INS450InputVO();
    DataAccessManager dam = new DataAccessManager();
    inputVO.setCustID(ObjectUtils.toString(inputMap.get("custId")));
    inputVO.setSppID(ObjectUtils.toString(inputMap.get("sppId")));
    inputVO.setNew((boolean) (inputMap.get("isNew")));
    GenericMap inputPDF = new GenericMap((Map) getPDFFile(dam, inputVO.getSppID(), new BigDecimal(inputVO.getKeyno())).get(0));
    Map<String, Object> finalResult = new HashMap<String, Object>();
    finalResult.put("fileCode", inputPDF.get("PLAN_ID"));
    finalResult.put("file", inputPDF.get("REPORT_FILE"));
    this.sendRtnObject(finalResult);
  }

  /**
   * 取流水號
   * */
  private String getSEQ(DataAccessManager dam) throws DAOException, JBranchException {

    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sql = new StringBuffer();

    sql.append("SELECT TBINS_SEQ.NEXTVAL AS SEQ FROM DUAL");
    queryCondition.setQueryString(sql.toString());

    List<Map<String, Object>> seqNO = dam.exeQuery(queryCondition);

    String seq = "SPP" + SDF_YYYYMMDD.format(new Date()) + String.format("%05d", Long.parseLong(seqNO.get(0).get("SEQ").toString())).toString();

    return seq;
  }

  private String getReportSEQ(DataAccessManager dam) throws DAOException, JBranchException {

    QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sql = new StringBuffer();

    sql.append("SELECT TBINS_REPORT_SEQ.NEXTVAL AS SEQ FROM DUAL");
    queryCondition.setQueryString(sql.toString());

    List<Map<String, Object>> seqNO = dam.exeQuery(queryCondition);
    String seq = ObjectUtils.toString(seqNO.get(0).get("SEQ"));
    return seq;
  }

  /**
   * 檢核是否為空值
   * */
  private String checkIsNull(Map map, String key) {
    if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
      return String.valueOf(map.get(key));
    } else {
      return "";
    }
  }

  /**
   * 取得推薦的商品
   * 
   * @param planID
   * @return
   * @throws DAOException
   * @throws JBranchException
   */
  private List<Map<String, Object>> getSelectSuggestProd(String planID) throws DAOException, JBranchException {
    StringBuffer sb = new StringBuffer();
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    sb.append(" select ");
    sb.append(" crm.CUST_ID, crm.AGE, crm.BIRTH_DATE,  ");
    sb.append(" insPrd.INSDATA_KEYNO, spp.INSPRD_ID, spp.PAY_YEARS, spp.POLICY_ASSURE_AMT, spp.CURR_CD, spp.POLICY_FEE ");
    sb.append(" from TBINS_SPP_MAIN spp ");
    sb.append(" left join TBCRM_CUST_MAST crm on spp.cust_id = crm.cust_id ");
    sb.append(" left join TBPRD_INS_COMPARED insPrd on spp.INSPRD_KEYNO = insPrd.KEY_NO ");
    sb.append(" where SPP_ID = :sppId ");
    qc.setObject("sppId", planID);
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  /**
   * 取得推薦的資訊源資料
   * 
   * @param planID
   * @return
   * @throws DAOException
   * @throws JBranchException
   */
  private List<Map<String, Object>> coverageList(String planID) throws DAOException, JBranchException {
    List<Map<String, Object>> selectSuggestProdList = getSelectSuggestProd(planID);
    if (CollectionUtils.isNotEmpty(selectSuggestProdList)) {
      // 取得建議商品
      Map<String, Object> selectSuggestPrdMap = selectSuggestProdList.get(0);

      // 準備調用資訊源
      GetInsPremInputVO inputVO = new GetInsPremInputVO();
      List<Map<String, Object>> lstInsData = new ArrayList<Map<String, Object>>();
      Map<String, Object> lstInsDataMap = new HashMap<String, Object>();
      lstInsDataMap.put("CURR_CD", selectSuggestPrdMap.get("CURR_CD"));
      lstInsDataMap.put("PRD_KEYNO", selectSuggestPrdMap.get("INSDATA_KEYNO"));
      lstInsDataMap.put("INSURED_AGE", selectSuggestPrdMap.get("AGE"));
      lstInsDataMap.put("INSURED_BIRTHDAY", selectSuggestPrdMap.get("BIRTH_DATE"));
      lstInsDataMap.put("JOB_GRADE", "1");
      lstInsDataMap.put("PREMTERM", selectSuggestPrdMap.get("PAY_YEARS")); // 繳費年期
      lstInsDataMap.put("INSYEARFEE", selectSuggestPrdMap.get("POLICY_FEE")); // 保費
      lstInsDataMap.put("INSUREDAMT", selectSuggestPrdMap.get("POLICY_ASSURE_AMT")); // 保額
      Calendar prevYear = Calendar.getInstance();
      prevYear.add(Calendar.YEAR, -0);
      lstInsDataMap.put("EFFECTED_DATE", prevYear.getTime());

      lstInsData.add(lstInsDataMap);
      inputVO.setFuncType("2");
      inputVO.setLstInsData(lstInsData);
      GetInsPremOutputVO outputVO = getInsjlb().getInsPrem(inputVO);
      List<Map<String, Object>> coverageAgePremList = outputVO.getLstCoverAgePrem();
      if (CollectionUtils.isNotEmpty(coverageAgePremList)) {
        Calendar cal = Calendar.getInstance();
        int nowROC = cal.get(Calendar.YEAR) - 1911;
        for (Map<String, Object> coverageAgePremMap : coverageAgePremList) {
          coverageAgePremMap.put("ROCYEAR", (Short) coverageAgePremMap.get("THEYEAR") + nowROC - 1);
        }
        return coverageAgePremList;
      }
    }
    return null;
  }

  // ============================ 待刪除 ============================
}
