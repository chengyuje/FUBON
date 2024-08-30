package com.systex.jbranch.app.server.fps.cmjlb210;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.insjlb.ws.client.PolicySourceWsClient;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.AllPolicyOBJ;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.Allproducts;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverageDataSet;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.Dt;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.DtAllProductsHtmlClause;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.Expression;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.ExpressionKIND;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.ExpressionTableNET;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.InsuranceCompany;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.LogTable;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.NewDataSet;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.NewReportExpression;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PayYear;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.Policy;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PolicyColumn;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PolicyDtlColumn;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PremDetail;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PremPerMonth;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.RelationColumn;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.TmpExpression;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.WholeLife;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.WholeLifeDtl;
import com.systex.jbranch.comutil.collection.GenericMap;
//import com.systex.jbranch.platform.common.dataManager.System;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

import java.util.Arrays;


/**
 * CMJLB210-保險資訊源共用模組_JAVA
 * 
 * @author paul
 * @date 2017-11-01
 * @spec WMFPS-TDS-CMSUB210_CMJLB210-保單健診-保險資訊源共用模組
 */

@Component("cmjlb210")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CMJLB210 extends BizLogic {
	
	private Logger logger = LoggerFactory.getLogger(CMJLB210.class); // sen add check error
	
	private final String YN_Y = "Y";
	private final String YN_N = "N";
	private final String IS_0 = "0";
	private final String IS_1 = "1";
	
	@Autowired @Qualifier("PsWsGetCoverage01")
	private PolicySourceWsClient psWsGetCoverage01;
	
	@Autowired @Qualifier("PsWsGetCoverage03")
	private PolicySourceWsClient psWsGetCoverage03;
	
	@Autowired @Qualifier("PsWsGetHtmlClauseBinary")
	private PolicySourceWsClient psWsGetHtmlClauseBinary;
	
		
	public CoverageDataSet getCoverage01(Map<String , Object> getCoverage01) throws Exception {
		CoverageDataSet coverageDataSet = psWsGetCoverage01.caller(getCoverage01);
		return coverageDataSet;
	}
	
    /**
	 * 取得健檢攤列的各項資料(多筆)(報表)
	 * 
	 * @param inputVO
	 * @return
	 * @throws JBranchException
	 */
	public GetCoverage03OutputVO getCoverage03(GetCoverage03InputVO inputVO) throws JBranchException {
		if (inputVO.getLstPolicyDetail() == null) {
			throw new APException("ehl_01_common_006", createErrorMsgList("lstPolicyDetail"));
		} 
		else if (inputVO.getLstPolicyMaster() == null) {
			throw new APException("ehl_01_common_006", createErrorMsgList("lstPolicyMaster"));
		}
		
		// 準備call webservice之傳入資料，物件格是請參照wsdl
		List<PolicyDtlColumn> policyDtlColumnLst = new ArrayList<PolicyDtlColumn>();
		Map<String, List<Integer>> policyDtlColumnMap = new HashMap<String, List<Integer>>();
		List<Integer> policyDtlIndexLst = new ArrayList<Integer>();
		String policyID_str = "";
		Map<String , Object> getCoverage03Map = new HashMap();
		
		PolicyDtlColumn policyDtlColumn = new PolicyDtlColumn();
		
		// 2011-11-03 施陽欽 自主處理被保險人名稱(WebServices 返回的資料若為合計則不會有名稱)
		Map<String, Object> cust_map = new HashMap<String, Object>();
		
		//保單明細
		for (Map<String, Object> map : (List<Map<String, Object>>)inputVO.getLstPolicyDetail()) {
			policyDtlColumn = new PolicyDtlColumn();
			//保單序號
			policyDtlColumn.setPolicyID(getString(map.get("INSSEQ")));
			//保險公司代碼
			policyDtlColumn.setInsCompany(getString(map.get("INSCO")));
			//0為主約，1為附約
			policyDtlColumn.setIsMain(getBigDecimal(map.get("IS_MAIN")).shortValue());
			//商品型號
			policyDtlColumn.setProdID(getString(map.get("THIRDPROD_CODE")));
			//商品描述
			policyDtlColumn.setProdDesc(getString(map.get("PROD_NAME")));
			//計畫
			policyDtlColumn.setPlan(getString(map.get("Plan")));
			//商品數量或保額(原幣)
			policyDtlColumn.setProdQuantity(getBigDecimal(map.get("ProdQuantity")).doubleValue());
			//單項總保費(原幣)
			policyDtlColumn.setPremium(getBigDecimal(map.get("Premium")).doubleValue());
			//屬性歸類
			policyDtlColumn.setKind(getString(map.get("Kind")));
			//投保對象
			policyDtlColumn.setInsuredObject(getString(map.get("INSURED_RELATION")));
			//有無社保
			policyDtlColumn.setSocialSecurity(getString(map.get("SocialSecurity")));
			//客戶姓名
			policyDtlColumn.setCustName(getString(map.get("INSURED_ID")));
			//投保年齡
			policyDtlColumn.setInsuredAge(getBigDecimal(map.get("INSURED_AGE")).intValue());
			//性別
			policyDtlColumn.setSex(getString(map.get("INSURED_GENDER")));
			//職業等級
			policyDtlColumn.setJobGrade(getString(map.get("JOB_GRADE")));
			//繳費年期
			policyDtlColumn.setPremTerm(getString(map.get("PremTerm")));
			//累積(保障)年期
			policyDtlColumn.setAccuTerm(getString(map.get("AccuTerm")));
			//旅平險購買天數
			policyDtlColumn.setDays(getBigDecimal(map.get("Days")).intValue());
			//保單生效日
			policyDtlColumn.setSignDate(getDateString(map.get("EFFECTED_DATE"), true));
			
			policyDtlColumn.setAllProductsID(getString(map.get("AllProductsID")));
			//年金給付方式
			policyDtlColumn.setAnnuityPayType(getString(map.get("AnnuityPayType")));
			//年金給附開始年齡
			policyDtlColumn.setAnnuityBgnPayAge(getBigDecimal(map.get("AnnuityBgnPayAge")).intValue());
			//匯率
			policyDtlColumn.setRateCivcpo(getBigDecimal(map.get("CUR_RATE")).doubleValue());
			//投保對象
			policyDtlColumn.setIObject(getString(map.get("IObject")));
			//保單保全資料
			policyDtlColumn.setPolicyDesc(getString(map.get("PolicyDesc")));
			//親屬代碼
			policyDtlColumn.setRelationCode(getString(map.get("RelationCode")));
			//生日
			policyDtlColumn.setBirth(getDateString(map.get("INSURED_BIRTHDAY"), false));
			
			//被保人身分證字號
			policyDtlColumn.setCustSSNIO(getString(map.get("INSURED_NAME")));
			
			cust_map.put(getString(map.get("INSURED_ID")), map.get("INSURED_ID"));
			
			//主附約唯一辨識序號
			policyDtlColumn.setDetailSEQ(getBigDecimal(map.get("DetailSEQ")).longValue());
			
			policyID_str = policyDtlColumn.getPolicyID();
			
			if (policyDtlColumnMap.get(policyID_str) != null) {
				policyDtlIndexLst = policyDtlColumnMap.get(policyID_str);
			} 
			else {
				policyDtlIndexLst = new ArrayList<Integer>();
			}
			
			policyDtlIndexLst.add(policyDtlColumnLst.size());
			policyDtlColumnMap.put(policyID_str, policyDtlIndexLst);

			policyDtlColumnLst.add(policyDtlColumn);
		}
		
		List<PolicyColumn> policyColumn_lst = new ArrayList<PolicyColumn>();
		PolicyColumn policyColumn = new PolicyColumn();
		
		//保單主檔
		for (Map<String, Object> map : (List<Map<String, Object>>)inputVO.getLstPolicyMaster()) {
			policyColumn = new PolicyColumn();
			//保單序號
			policyColumn.setPolicyID(getString(map.get("INSSEQ")));
			policyColumn.setPolicyNO(getString(map.get("POLICYNO")));
			policyColumn.setPayType(getString(map.get("PAYTYPE")));	
			policyColumn.setSignYY(getBigDecimal(map.get("SIGN_YY")).shortValue());
			policyColumn.setSignMM(getBigDecimal(map.get("SIGN_MM")).shortValue());
			policyColumn.setSignDD(getBigDecimal(map.get("SIGN_DD")).shortValue());
			policyColumn.setPolicyStatus(getString(map.get("POLICY_STATUS")));
			
			policyColumn_lst.add(policyColumn);
		}
		
		// 2011-08-05 施陽欽 修改 getCoverage03 的傳入(按保單分開)
		Policy[] policy_arr = new Policy[policyColumn_lst.size()];
		int policy_int = 0;
		Policy policy = new Policy();
		List<PolicyDtlColumn> policyDtl_lst = null;
		
		for (PolicyColumn policyCol : policyColumn_lst) {
			policyDtlIndexLst = policyDtlColumnMap.get(policyCol.getPolicyID());
			policyDtl_lst = new ArrayList<PolicyDtlColumn>();
			
			for (int policyDtl_int : policyDtlIndexLst) {
				policyDtl_lst.add(policyDtlColumnLst.get(policyDtl_int));
			}
			
			// 每筆保單為一個 Policy
			policy = new Policy();
			policy.setPolicyColumn(policyCol);
			policy.setPolicyDtlColumns(policyDtl_lst);
			
			policy_arr[policy_int] = policy;
			policy_int ++;
		}
		// 2011-08-05 end.
		
		// Call WebServices 並接收回傳值
		GetCoverage03OutputVO outputVO = new GetCoverage03OutputVO();
		
		try {
			getCoverage03Map.put("policys", Arrays.asList(policy_arr));
			
			List<Map> relationList = inputVO.getLstRelation();
			List<RelationColumn> relationColumnList = new ArrayList<RelationColumn>();
			
			if(CollectionUtils.isNotEmpty(relationList)){
				for(Map relation : relationList){
					GenericMap relationGmap = new GenericMap(relation);
					RelationColumn reColumn = new RelationColumn();
					reColumn.setBirth(relationGmap.getNotNullStr("Birth"));
					reColumn.setCustName(relationGmap.getNotNullStr("INSURED_ID"));
					reColumn.setRelationCode(relationGmap.getNotNullStr("RELATIONCODE"));
					reColumn.setRelationSEQ(relationGmap.getNotNullStr("RelationSEQ"));
					reColumn.setSex(relationGmap.getNotNullStr("Sex"));
					relationColumnList.add(reColumn);
				}
			}
			
			getCoverage03Map.put("relations", relationColumnList);
			logger.info("CMJLB210 261 call psWsGetCoverage03.caller start"); // sen add check error
			NewDataSet result = psWsGetCoverage03.caller(getCoverage03Map);
			logger.info("CMJLB210 263 call psWsGetCoverage03.caller end " + result); // sen add check error
			
			if (result == null || (result != null &&
				ArrayUtils.isEmpty(result.getWholeLifeArray()) &&
				ArrayUtils.isEmpty(result.getExpressionArray()) &&
				ArrayUtils.isEmpty(result.getPremPerMonthArray()) &&
				ArrayUtils.isEmpty(result.getPremDetailArray()) &&
				ArrayUtils.isEmpty(result.getNewReportExpressionArray()) &&
				ArrayUtils.isEmpty(result.getTmpExpressionArray()) &&
				ArrayUtils.isEmpty(result.getAllPolicyOBJArray()) &&
				ArrayUtils.isEmpty(result.getWholeLifeDtlArray()) &&
				ArrayUtils.isEmpty(result.getPayYearArray())
			)) {
				throw new APException("ehl_01_common_032");
			}
			
			outputVO.setLstWholeLife(getLstWholeLife(result.getWholeLifeArray(), cust_map));
			outputVO.setLstExpression(getLstExpression(result.getExpressionArray(), cust_map));
			outputVO.setLstPremPerMonth(getLstPremPerMonth(result.getPremPerMonthArray(), cust_map));
			outputVO.setLstPremDetail(getLstPremDetail(result.getPremDetailArray(), cust_map));
			outputVO.setLstNewReportExpression(getLstNewReportExpression(result.getNewReportExpressionArray(), cust_map));
			outputVO.setLstTmpExpression(getLstTmpExpression(result.getTmpExpressionArray(), cust_map));
			outputVO.setLstAllPolicyObj(getLstAllPolicyObj(result.getAllPolicyOBJArray()));
			outputVO.setLstWholeLifeDtl(getLstWholeLifeDtl(result.getWholeLifeDtlArray(), cust_map));
			
			// 錯誤資訊
			outputVO.setLstLogTable(getLstLogTable(result.getLogTableArray()));
			
			// 增加主附約繳費年期
			outputVO.setLstPayYear(getLstPayYear(result.getPayYearArray()));
			
		} catch (Exception e) {
			e.printStackTrace();
			// 2011-10-08 施陽欽 增加異常拋出(交易執行失敗)
			throw new APException("call getCoverage03:" + e.getMessage());
			// 2011-10-08 end.
		}

		return outputVO;
	}

	/**
	 * 依保險公司、QueryItem、停現售、主附約及關鍵字查詢出符合條件的商品資料
	 * 
	 * @param inputVO
	 * @return
	 * @throws JBranchException
	 */
//	public GetInsProduct01OutputVO getInsProduct1(GetInsProduct01InputVO inputVO) throws JBranchException {
//		if (StringUtils.isBlank(inputVO.getQid())) {
//			throw new APException("ehl_01_common_006", createErrorMsgList("qid"));
//		} else if (inputVO.getM0() != 0 && inputVO.getM0() != 1) {
//			throw new APException("ehl_01_common_006", createErrorMsgList("m0"));
//		} else if (inputVO.getM1() != 0 && inputVO.getM1() != 1) {
//			throw new APException("ehl_01_common_006", createErrorMsgList("m1"));
//		} else if (inputVO.getM2() != 0 && inputVO.getM2() != 1) {
//			throw new APException("ehl_01_common_006", createErrorMsgList("m2"));
//		} else if (inputVO.getM3() != 0 && inputVO.getM3() != 1) {
//			throw new APException("ehl_01_common_006", createErrorMsgList("m3"));
//		}
//		
//		
//		// given
//		String lastUpdate = "";
//		if (inputVO.getP_strLastUPdate() != null) {
//			lastUpdate = getDateString(inputVO.getP_strLastUPdate(), false);
//		} else {
//			lastUpdate = "1900/01/01";// 全部
//		}
//		String ifChs_str = "";
//		if (!StringUtils.isBlank(inputVO.getIfChs())) {
//			ifChs_str = inputVO.getIfChs();
//		}
//		
//		// Call WebServices 並接收回傳值
//		GetInsProduct01OutputVO outputVO = new GetInsProduct01OutputVO();
//		try {
//			Allproducts[] result = getService().getInsProduct1(inputVO.getAllProdctsID(), inputVO.getStrfind(),
//														   inputVO.getCid(), inputVO.getQid(),
//														   inputVO.getM0(), inputVO.getM1(), inputVO.getM2(), inputVO.getM3(),
//														   lastUpdate, ifChs_str);
//			
//			outputVO.setLstInsProduct(getLstAllproducts(result));
//		} catch (WSException e) {
//			e.printStackTrace();
//			// 2011-10-08 施陽欽 增加異常拋出(交易執行失敗)
//			throw new APException("call getInsProduct1:" + e.getMessage());
//			// 2011-10-08 end.
//		}
//		
//		return outputVO;
//	}
//		
	/**
	 * 傳回單一商品的說明或條款的二進位資料流及附檔名
	 * 
	 * @param inputVO
	 * @return
	 * @throws JBranchException
	 */
	public GetHtmlClauseBinaryOutputVO getHtmlClauseBinary(String htmlClause) throws JBranchException {
		GetHtmlClauseBinaryOutputVO outputVo = new GetHtmlClauseBinaryOutputVO();
		Dt dt = null;
		if (StringUtils.isBlank(htmlClause)) {
			throw new APException("ehl_01_common_006", createErrorMsgList("htmlClause"));
		}
	
		try{
			dt = psWsGetHtmlClauseBinary.caller(htmlClause);
			outputVo.setLstDS(getLstDt(new Dt[]{dt}));
			return outputVo;
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw new JBranchException(ex);
		}
	}
//		
//	/**
//	 * 回傳條款說明清單
//	 * 
//	 * @param inputVO
//	 * @return
//	 * @throws JBranchException
//	 */
//	public RtnHtmlClauseIfmnOutputVO rtnHtmlClauseIfmn(RtnHtmlClauseIfmnInputVO inputVO) throws JBranchException {
//		// given
//		String lastUpdate = "";
//		if (inputVO.getLastUpdate() != null) {
//			lastUpdate = getDateString(inputVO.getLastUpdate(), false);
//		} else {
//			lastUpdate = "0";// 全部
//		}
//		
//		
//		// Call WebServices 並接收回傳值
//		RtnHtmlClauseIfmnOutputVO outputVO = new RtnHtmlClauseIfmnOutputVO();
//		try {
//			DtAllProductsHtmlClause[] result = getService().rtnHtmlClauseIfmn(lastUpdate);
//			outputVO.setLstDS(getLstDtAllProductsHtmlClause(result));
//		} catch (WSException e) {
//			e.printStackTrace();
//			// 2011-10-08 施陽欽 增加異常拋出(交易執行失敗)
//			throw new APException("call rtnHtmlClauseIfmn:" + e.getMessage());
//			// 2011-10-08 end.
//		}
//		
//		return outputVO;
//	}
//	
//	/**
//	 * 傳回保險公司相關資料by保險公司類別
//	 * 
//	 * @param inputVO
//	 * @return
//	 * @throws JBranchException
//	 */
//	public GetInsCompanyOutputVO getInsCompany(GetInsCompanyInputVO inputVO) throws JBranchException {
//		if (StringUtils.isBlank(inputVO.getClassid())) {
//			throw new APException("ehl_01_common_006", createErrorMsgList("classid"));
//		}
//		
//		
//		// Call WebServices 並接收回傳值
//		GetInsCompanyOutputVO outputVO = new GetInsCompanyOutputVO();
//		try {
//			InsuranceCompany[] result = getService().getInsCompany(inputVO.getClassid());
//			outputVO.setLstInsCompany(getLstInsuranceCompany(result));
//		} catch (WSException e) {
//			e.printStackTrace();
//			// 2011-10-08 施陽欽 增加異常拋出(交易執行失敗)
//			throw new APException("call getInsCompany:" + e.getMessage());
//			// 2011-10-08 end.
//		}
//		
//        return outputVO;
//	}
//	
//	/**
//	 * 回傳給付明細內容
//	 * 
//	 * @param inputVO
//	 * @return
//	 * @throws JBranchException
//	 */
//	public ExpressionTableNetOutputVO expressionTableNet(ExpressionTableNetInputVO inputVO) throws JBranchException {
//		// CHECK參數是否有值
//		
//		
//		// Call WebServices 並接收回傳值
//		ExpressionTableNetOutputVO outputVO = new ExpressionTableNetOutputVO();
//		try {
//			ExpressionTableNET[] result = getService().getExpressionTableNET();
//			outputVO.setLstExpressionTableNet(getLstExpressionTableNet(result));
//		} catch (WSException e) {
//			e.printStackTrace();
//			// 2011-10-08 施陽欽 增加異常拋出(交易執行失敗)
//			throw new APException("call getExpressionTableNET:" + e.getMessage());
//			// 2011-10-08 end.
//		}
//		
//		return outputVO;
//	}
//
//	/**
//	 * 回傳給付項目
//	 * 
//	 * @param inputVO
//	 * @return
//	 * @throws JBranchException
//	 */
//	public ExpressionKindOutputVO expressionKind(ExpressionKindInputVO inputVO) throws JBranchException {
//		// CHECK參數是否有值
//		
//		
//		// Call WebServices 並接收回傳值
//		ExpressionKindOutputVO outputVO = new ExpressionKindOutputVO();
//		try {
//			ExpressionKIND[] result = getService().getExpressionKIND();
//			outputVO.setLstExpressionKindNet(getLstExpressionKindNet(result));
//		} catch (WSException e) {
//			e.printStackTrace();
//			// 2011-10-08 施陽欽 增加異常拋出(交易執行失敗)
//			throw new APException("call getExpressionKIND:" + e.getMessage());
//			// 2011-10-08 end.
//		}
//		
//		return outputVO;
//	}
	
	/**
	 * 將 Array 轉為 List：LstWholeLife
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstWholeLife(WholeLife[] result, Map<String, Object> custMap) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		WholeLife resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
			resultItem = result[index_int];
			
			result_map.put("THEYEAR", resultItem.getTheYear());
			result_map.put("THEAGE", resultItem.getTheAge());
			result_map.put("LIFE", resultItem.getLife());
			result_map.put("PA", resultItem.getPa());
			result_map.put("CL", resultItem.getCl());
			result_map.put("AI", resultItem.getAi());
			result_map.put("DDB", resultItem.getDdb());
			result_map.put("PREMIUM", resultItem.getPremium());
			result_map.put("REPAY", resultItem.getRepay());
			result_map.put("POLICYNO", resultItem.getPolicyNo());
			result_map.put("CUSTNAME", resultItem.getCustSSN_IO());
			result_map.put("CUSTPOLICY", resultItem.getCustPolicy());
			result_map.put("INSSEQ", resultItem.getPolicyID());
			result_map.put("ROCYEAR", resultItem.getROCYear());
			result_map.put("INSURED_ID", resultItem.getCustName());
			
			result_lst.add(result_map);
		}
		return result_lst;
	}

	/**
	 * 將 Array 轉為 List：LstExpression
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstExpression(Expression[] result, Map<String, Object> custMap) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		Expression resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
			resultItem = result[index_int];
			
			result_map.put("FIRSTKIND", resultItem.getFirstKind());
			result_map.put("SECONDKIND", resultItem.getSecondKind());
			result_map.put("FIRSTKINDDESC", resultItem.getFirstKindDesc());
			result_map.put("SECONDKINDDESC", resultItem.getSecondKindDesc());
			result_map.put("EXPRESSDESC", resultItem.getExpressDESC());
			result_map.put("BEGUNITPRICE", resultItem.getBegUnitPrice());
			result_map.put("ENDUNITPRICE", resultItem.getEndUnitPrice());
			result_map.put("DESCRIPTION", resultItem.getDescription());
			result_map.put("CUSTNAME", resultItem.getCustSSN_IO());
			result_map.put("CUSTPOLICY", resultItem.getCustPolicy());
			result_map.put("RELEXPRESSION", resultItem.getRelExpression());
			result_map.put("SORTNO", resultItem.getSortNo());
			result_map.put("MUL_UNIT", resultItem.getMulUnit());
			result_map.put("RELATIONCODE", resultItem.getRelationCode());
			result_map.put("INSCOMPANY", resultItem.getInsCompany());
			result_map.put("POLICYNO", resultItem.getPolicyNo());
			result_map.put("SIGNDATE", resultItem.getSignDate());
			result_map.put("INSQUANTITY", resultItem.getInsQuantity());
			result_map.put("PAYTYPEPEMIUM", resultItem.getPayTypePremium());
			result_map.put("MAINPROD", resultItem.getMainProd());
			result_map.put("POLICYSTATUS", resultItem.getPolicyStatus());
			result_map.put("INSSEQ", resultItem.getPolicyID());
			result_map.put("INSURED_ID", resultItem.getCustName());
			
			if(resultItem.getBirth() != null)
				result_map.put("BIRTH", resultItem.getBirth().getTime());

			result_lst.add(result_map);
		}
		return result_lst;
	}
	
	/**
	 * 將 Array 轉為 List：LstPremPerMonth
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstPremPerMonth(PremPerMonth[] result, Map<String, Object> custMap) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		PremPerMonth resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
			resultItem = result[index_int];
			
			result_map.put("POLICYNO", resultItem.getPolicyNo());
			result_map.put("CUSTNAME", resultItem.getCustSSN_IO());
			result_map.put("INSCOMPANY", resultItem.getInsCompany());
			result_map.put("PRODNAME", resultItem.getProdName());
			result_map.put("INSSIGNDATE", resultItem.getInsSignDate());
			result_map.put("PRODQUANTITY", resultItem.getProdQuantity());
			result_map.put("PREMTERM", resultItem.getPremTerm());
			result_map.put("PAYTYPE", resultItem.getPayType());
			result_map.put("ONCEPAYTYPE", resultItem.getOncePayType());
			result_map.put("JAN", resultItem.getJAN());
			result_map.put("FEB", resultItem.getFEB());
			result_map.put("MAR", resultItem.getMAR());
			result_map.put("APR", resultItem.getAPR());
			result_map.put("MAY", resultItem.getMAY());
			result_map.put("JUN", resultItem.getJUN());
			result_map.put("JUL", resultItem.getJUL());
			result_map.put("AUG", resultItem.getAUG());
			result_map.put("SEP", resultItem.getSEP());
			result_map.put("OCT", resultItem.getOCT());
			result_map.put("NOV", resultItem.getNOV());
			result_map.put("DEC", resultItem.getDEC());
			result_map.put("POLICYTOTAL", resultItem.getPolicyTotal());
			result_map.put("MONTHTOTAL", resultItem.getMonthTotal());
			result_map.put("INSSEQ", resultItem.getPolicyID());
			result_map.put("INSURED_ID", resultItem.getCustName());			
			result_lst.add(result_map);
		}
		return result_lst;
	}
	
	/**
	 * 將 Array 轉為 List：LstPremDetail
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstPremDetail(PremDetail[] result, Map<String, Object> custMap) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		PremDetail resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
			resultItem = result[index_int];
			
			result_map.put("POLICYNO", resultItem.getPolicyNo());
			result_map.put("PRODNAME", resultItem.getProdName());
			result_map.put("MAINPRODNAME", resultItem.getMainProdName());
			result_map.put("PREMTERM", resultItem.getPremterm());
			result_map.put("DESCRIPTION", resultItem.getDescription());
			result_map.put("QUANTITY", resultItem.getQuantity());
			result_map.put("CUSTNAME", resultItem.getCustSSN_IO());
			result_map.put("INSCOMPANY", resultItem.getInsCompany());
			result_map.put("EXPRESSDESC", resultItem.getExpressDesc());
			result_map.put("PAYMODE", resultItem.getPaymode());
			result_map.put("PREMIUM", resultItem.getPremium());
			result_map.put("INSSEQ", resultItem.getPolicyID());
			result_map.put("INSURED_ID", resultItem.getCustName());
			
			result_lst.add(result_map);
		}
		return result_lst;
	}
	
	/**
	 * 將 Array 轉為 List：LstNewReportExpression
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstNewReportExpression(NewReportExpression[] result, Map<String, Object> custMap) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		NewReportExpression resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
			resultItem = result[index_int];
			
			result_map.put("FIRSTKIND", resultItem.getFirstKind());
			result_map.put("SECONDKIND", resultItem.getSecondKind());
			result_map.put("FIRSTKINDDESC", resultItem.getFirstKindDesc());
			result_map.put("SECONDKINDDESC", resultItem.getSecondKindDesc());
			result_map.put("EXPRESSDESC", resultItem.getExpressDESC());
			result_map.put("BEGUNITPRICE", resultItem.getBegUnitPrice());
			result_map.put("ENDUNITPRICE", resultItem.getEndUnitPrice());
			result_map.put("DESCRIPTION", resultItem.getDescription());
			result_map.put("CUSTNAME", resultItem.getCustSSN_IO());
			result_map.put("CUSTPOLICY", resultItem.getCustPolicy());
			result_map.put("RELEXPRESSION", resultItem.getRelExpression());
			result_map.put("SORTNO", resultItem.getSortNo());
			result_map.put("MUL_UNIT", resultItem.getMulUnit());
			result_map.put("RELATIONCODE", resultItem.getRelationCode());
			result_map.put("INSCOMPANY", resultItem.getInsCompany());
			result_map.put("POLICYNO", resultItem.getPolicyNo());
			result_map.put("INSSEQ", resultItem.getPolicyID());
			result_map.put("INSQUANTITY", resultItem.getInsQuantity());
			result_map.put("PAYTYPEPREMIUM", resultItem.getPayTypePremium());
			result_map.put("MAINPROD", resultItem.getMainProd());
			result_map.put("SIGNDATE", resultItem.getSignDate());
			result_map.put("POLICYSTATUS", resultItem.getPolicyStatus());
			result_map.put("EXPRESSIONPRODUCT", resultItem.getExpressionProduct());
			result_map.put("ACCUTERM", resultItem.getAccTerm());
			result_map.put("INSURED_ID", resultItem.getCustName());
			result_map.put("ALLPRODUCTSID", resultItem.getAllProductsID());
			
			if(resultItem.getBirth() != null)
				result_map.put("BIRTH", resultItem.getBirth().getTime());

			result_lst.add(result_map);
		}
		return result_lst;
	}
	
	/**
	 * 將 Array 轉為 List：LstTmpExpression
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstTmpExpression(TmpExpression[] result, Map<String, Object> custMap) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		TmpExpression resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
			resultItem = result[index_int];
			
			result_map.put("FIRSTKINID", resultItem.getFirstKind());
			result_map.put("SECONDKIND", resultItem.getSecondKind());
			result_map.put("FIRSTKINDDESC", resultItem.getFirstKindDesc());
			result_map.put("SECONDKINDDESC", resultItem.getSecondKindDesc());
			result_map.put("EXPRESSDESC", resultItem.getExpressDESC());
			result_map.put("BEGUNITPRICE", resultItem.getBegUnitPrice());
			result_map.put("ENDUNITPRICE", resultItem.getEndUnitPrice());
			result_map.put("DESCRIPTION", resultItem.getDescription());
			result_map.put("CUSTNAME", resultItem.getCustSSN_IO());
			result_map.put("CUSTPOLICY", resultItem.getCustPolicy());
			result_map.put("RELEXPRESSION", resultItem.getRelExpression());
			result_map.put("SORTNO", resultItem.getSortNo());
			result_map.put("MULUNIT", resultItem.getMulUnit());
			result_map.put("RELATIONCODE", resultItem.getRelationCode());
			result_map.put("BIRTH", resultItem.getBirth().getTime());
			result_map.put("INSCOMPANY", resultItem.getInsCompany());
			result_map.put("POLICYNO", resultItem.getPolicyNo());
			result_map.put("SIGNDATE", resultItem.getSignDate());
			result_map.put("INSQUANTITY", resultItem.getInsQuantity());
			result_map.put("PATYPEPREMIUM", resultItem.getPayTypePremium());
			result_map.put("MAINPROD", resultItem.getMainProd());
			result_map.put("POLICYSTATUS", resultItem.getPolicyStatus());
			result_map.put("INSSEQ", resultItem.getPolicyID());
			result_map.put("INSURED_ID", resultItem.getCustName());

			result_lst.add(result_map);
		}
		return result_lst;
	}
	
	/**
	 * 將 Array 轉為 List：LstAllPolicyObj
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstAllPolicyObj(AllPolicyOBJ[] result) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		AllPolicyOBJ resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
			resultItem = result[index_int];
			
			result_map.put("TABLENAM", resultItem.getTableName());
			result_map.put("COUNTNUM", resultItem.getCountNUM());
			result_map.put("LASTNUM", resultItem.getLastNUM());
			result_map.put("CUSTPOLICY", resultItem.getCustPolicy());
			result_map.put("STARTAGE", resultItem.getStartAge());
			result_map.put("INSSEQ", resultItem.getPolicyID());
			
			result_lst.add(result_map);
		}
		return result_lst;
	}
	
	/**
	 * 將 Array 轉為 List：LstWholeLifeDtl
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstWholeLifeDtl(WholeLifeDtl[] result, Map<String, Object> custMap) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		WholeLifeDtl resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
			resultItem = result[index_int];
			
			result_map.put("THEAGE", resultItem.getTheYear());
			result_map.put("LIFE", resultItem.getLife());
			result_map.put("PA", resultItem.getPa());
			result_map.put("CL", resultItem.getCl());
			result_map.put("AI", resultItem.getAi());
			result_map.put("DDB", resultItem.getDdb());
			result_map.put("PREMIUM", resultItem.getPremium());
			result_map.put("REPAY", resultItem.getRepay());
			result_map.put("POLICYNO", resultItem.getPolicyNo());
			result_map.put("CUSTNAME", resultItem.getCustSSN_IO());
			result_map.put("CUSTPOLICY", resultItem.getCustPolicy());
			result_map.put("INSSEQ", resultItem.getPolicyID());
			result_map.put("ROCYEAR", resultItem.getROCYear());
			result_map.put("INSURED_ID", resultItem.getCustName());
			
			result_lst.add(result_map);
		}
		return result_lst;
	}
	
	/**
	 * 將 Array 轉為 List：LstLogTable
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstLogTable(LogTable[] result) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		LogTable resultItem = null;
		
		if (result == null) {
			return result_lst;
		}
		
		for (int index_int = 0; index_int < result.length; index_int++) {
			resultItem = result[index_int];
			String logStr = ObjectUtils.toString(resultItem.getLogstr());
			
			if(logStr.equals("PolicyStatussyscode不存在22")){
				continue;
			}
			
			result_map = new HashMap<String, Object>();
        	result_map.put("AllProductsID", resultItem.getAllProductsID());
        	result_map.put("ProdName", resultItem.getProdName());
        	result_map.put("PolicyNo", resultItem.getPolicyNo());
        	result_map.put("logstr", resultItem.getLogstr());
        	
        	result_lst.add(result_map);
		}
		
		return result_lst;
	}
	
	/**
	 * 將 Array 轉為 List：LstPayYear
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstPayYear(PayYear[] result) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		PayYear resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
        	resultItem = result[index_int];
        	
        	result_map.put("DETAILSEQ", resultItem.getDetailSEQ());
        	result_map.put("PAYYEAR", resultItem.getPayYear());
        	
        	result_lst.add(result_map);
		}
		return result_lst;
	}
	
	/**
	 * 將 Array 轉為 List：LstAllproducts
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstAllproducts(Allproducts[] result) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		Allproducts resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
        	resultItem = result[index_int];
        	
        	result_map.put("ID", resultItem.getID());
        	result_map.put("PRODNAME", resultItem.getProdName());
        	result_map.put("INSCOMPANYID", resultItem.getInsCompanyID());
        	// ISMAIN
        	if (resultItem.getIsMain() == 1) {
        		result_map.put("IS_MAIN", YN_Y);
        	} else {
        		result_map.put("IS_MAIN", YN_N);
        	}
        	// ISOLD
        	if (resultItem.getIsOld()) {
        		result_map.put("IS_OLD", YN_Y);
        	} else {
        		result_map.put("IS_OLD", YN_N);
        	}
        	// ISWHOLELIFE
        	if (resultItem.getIsWholeLife()) {
        		result_map.put("IS_WHOLELIFE", "1");
        	} else {
        		result_map.put("IS_WHOLELIFE", "0");
        	}
        	result_map.put("WHOLELIFETERM", resultItem.getWholeLifeTerm());
        	result_map.put("BEGINTIME", resultItem.getBeginTime());
        	result_map.put("TERMINATETIME", resultItem.getTerminateTime());
        	result_map.put("HTML_N", resultItem.getHTMLN());
        	result_map.put("CLAUSE", resultItem.getClause());
        	// INVESTTYPE
        	if (resultItem.getInvestType() == 1) {
        		result_map.put("INVESTTYPE", IS_1);
        	} else {
        		result_map.put("INVESTTYPE", IS_0);
        	}
        	result_map.put("QID", resultItem.getQID());
        	result_map.put("PRODMENUITEMY", resultItem.getProdMenuItemY());
        	result_map.put("PRODMENUITEMA", resultItem.getProdMenuItemA());
        	result_map.put("PRODMENUITEMX", resultItem.getProdMenuItemX());
        	result_map.put("PRODMENUITEMK", resultItem.getProdMenuItemK());
        	result_map.put("PRODMENUITEMP", resultItem.getProdMenuItemP());
        	result_map.put("PRODMENUITEMU", resultItem.getProdMenuItemU());
        	result_map.put("PRODMENUITEMO", resultItem.getProdMenuItemO());
        	result_map.put("PRODMENULISTY", resultItem.getProdMenuListY());
        	result_map.put("PRODMENULISTA", resultItem.getProdMenuListA());
        	result_map.put("PRODMENULISTX", resultItem.getProdMenuListX());
        	result_map.put("PRODMENULISTK", resultItem.getProdMenuListK());
        	result_map.put("PRODMENULISTP", resultItem.getProdMenuListP());
        	result_map.put("PRODMENULISTU", resultItem.getProdMenuListU());
        	result_map.put("PRODMENULISTO", resultItem.getProdMenuListO());
        	result_map.put("PRODMODEL", resultItem.getProdModel());
        	// IS_OVERSEA
        	if (IS_1.equals(resultItem.getISOVERSEA())) {
        		result_map.put("IS_OVERSEA", YN_Y);
        	} else {
        		result_map.put("IS_OVERSEA", YN_N);
        	}
        	result_map.put("CURRENCY_TYPE", resultItem.getCURRENCYTYPE());
        	// 2011-10-13 增加以下欄位
        	result_map.put("QUANTITYSTYLE", resultItem.getQuantityStyle());
        	result_map.put("PRODMENUTYPE", resultItem.getProdMenuType());
        	result_map.put("PRODMENUITEM", resultItem.getProdMenuItem());
        	result_map.put("PRODMENULIST", resultItem.getProdMenuList());
        	// 2011-10-13 end.
        	// 2011-07-28 增加以下欄位
        	result_map.put("TYPEVALUE", resultItem.getTypeValue());
        	result_map.put("TYPEVALUE1", resultItem.getTypeValue1());
        	result_map.put("TYPEVALUE2", resultItem.getTypeValue2());
        	result_map.put("COVERCACULUNIT", resultItem.getCoverCaculUnit());
        	result_map.put("COVERCACULUNITDESC", resultItem.getCoverCaculUnitDesc());
        	// 2011-07-28 end.
        	result_map.put("LASTUPDATE", resultItem.getLastUpdate().getTime());
        	// 2011-11-10 增加以下欄位
        	// IFCHS
        	if (resultItem.getIfChs()) {
        		result_map.put("IFCHS", YN_Y);
        	} else {
        		result_map.put("IFCHS", YN_N);
        	}
        	// 2011-11-10 end.
        	result_lst.add(result_map);
		}
		return result_lst;
	}
	
	/**
	 * 將 Array 轉為 List：LstDt
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstDt(Dt[] result) {
		List<Map<String, Object>> resultLst = new ArrayList<Map<String, Object>>();
		Map<String, Object> resultMap = null;
		
		if (result == null) {
			return resultLst;
		}
		
		for (Dt resultItem : result) {
			resultMap = new HashMap<String, Object>();
        	resultMap.put("BINARY_DATA", resultItem.getBinaryData());
        	resultMap.put("VICEFILE", resultItem.getViceFile());
        	resultMap.put("VERSION", resultItem.getVersion());
        	resultLst.add(resultMap);
		}
		return resultLst;
	}
	
	/**
	 * 將 Array 轉為 List：LstDtAllProductsHtmlClause
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstDtAllProductsHtmlClause(DtAllProductsHtmlClause[] result) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		DtAllProductsHtmlClause resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
        	resultItem = result[index_int];
        	
        	result_map.put("ID", resultItem.getID());
        	result_map.put("CLAUSE_FILENAME", resultItem.getCLAUSEFILENAME());
        	result_map.put("CLAUSE_VERSION", resultItem.getCLAUSEVERSION());
        	result_map.put("CONTENT_FILENAME", resultItem.getCONTENTFILENAME());
        	result_map.put("CONTENT_VERSION", resultItem.getCONTENTVERSION());
        	
        	result_lst.add(result_map);
		}
		return result_lst;
	}
	
	/**
	 * 將 Array 轉為 List：LstInsuranceCompany
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstInsuranceCompany(InsuranceCompany[] result) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		InsuranceCompany resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
        	resultItem = result[index_int];
        	
        	result_map.put("ID", resultItem.getID());
        	result_map.put("INSCOMPANYNAME", resultItem.getInsCompanyName());
        	result_map.put("INSCOMPANYCLASSID", resultItem.getInsCompanyClassID());
        	result_map.put("INSCOMPANYCODE", resultItem.getInsCompanyCode());
        	result_map.put("INSCOMPANYSHOWORDER", resultItem.getInsCompanyShowOrder());
        	result_map.put("INSCOMPANYID", resultItem.getInsCompanyID());
        	result_map.put("COMPANYNAME", resultItem.getCompanyName());
        	
        	result_lst.add(result_map);
		}
		return result_lst;
	}
	
	/**
	 * 將 Array 轉為 List：LstExpressionTableNet
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstExpressionTableNet(ExpressionTableNET[] result) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		ExpressionTableNET resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
        	resultItem = result[index_int];
        	
        	result_map.put("SORTNO", resultItem.getSortNo());
        	result_map.put("FIRSTKIND", resultItem.getFirstKind());
        	result_map.put("SECONDKIND", resultItem.getSecondKind());
        	result_map.put("EXPRESSDESC", resultItem.getExpressDESC());
        	result_map.put("PAYMODE", resultItem.getPayMode());
        	
        	result_lst.add(result_map);
		}
		return result_lst;
	}
	
	/**
	 * 將 Array 轉為 List：lstExpressionKindNet
	 * 
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> getLstExpressionKindNet(ExpressionKIND[] result) {
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> result_map = null;
		ExpressionKIND resultItem = null;
		if (result == null) {
			return result_lst;
		}
		for (int index_int = 0; index_int < result.length; index_int++) {
			result_map = new HashMap<String, Object>();
        	resultItem = result[index_int];
        	
        	result_map.put("FIRSTKIND", resultItem.getFirstKind());
        	result_map.put("KINDDESC", resultItem.getKindDESC());
        	
        	result_lst.add(result_map);
		}
		return result_lst;
	}
	
	/**
	 * 將 vo 得到的值轉化為 String 類型
	 * 
	 * @param obj
	 * @return
	 */
	private String getString(Object obj) {
		if (obj != null) {
			return obj.toString();
		} else {
			return "";
		}
	}

	/**
	 * 將 vo 得到的值轉化為 BigDecimal 類型
	 * 
	 * @param obj
	 * @return
	 */
	private BigDecimal getBigDecimal(Object obj) {
		if (obj != null && !"".equals(obj)) {
			return new BigDecimal(obj.toString());
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	/**
	 * 將 Date 轉化為 String 類型
	 * 
	 * @param obj
	 * @return
	 */
	private String getDateString(Object obj, Boolean yyyBol) {
		SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd");
		String dtf_str = "";
		if (obj != null) {
			dtf_str = dtf.format(obj);
			if (yyyBol) {
				return (Integer.valueOf(dtf_str.substring(0, 4)) - 1911) + dtf_str.substring(4);
			} else {
				return dtf_str;
			}
		} else {
			return "";
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<String> createErrorMsgList(String...args){
		List errorMsg = new ArrayList<String>();
		CollectionUtils.addAll(errorMsg, args);
		return errorMsg;
	}
}
