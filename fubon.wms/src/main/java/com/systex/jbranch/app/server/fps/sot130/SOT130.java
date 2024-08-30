package com.systex.jbranch.app.server.fps.sot130;

import com.ibm.icu.text.DecimalFormat;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_D_OVS_PRIPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_D_OVS_PRIVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421;
import com.systex.jbranch.app.server.fps.crm421.CRM421InputVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421OutputVO;
import com.systex.jbranch.app.server.fps.prd110.PRD110;
import com.systex.jbranch.app.server.fps.prd110.PRD110InputVO;
import com.systex.jbranch.app.server.fps.prd110.PRD110OutputVO;
import com.systex.jbranch.app.server.fps.sot110.SOT110;
import com.systex.jbranch.app.server.fps.sot110.SOT110InputVO;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.SotUtils;
import com.systex.jbranch.app.server.fps.sot703.SOT703;
import com.systex.jbranch.app.server.fps.sot703.SOT703InputVO;
import com.systex.jbranch.app.server.fps.sot703.SOT703OutputVO;
import com.systex.jbranch.app.server.fps.sot709.MeteringFeeRateVO;
import com.systex.jbranch.app.server.fps.sot709.SOT709;
import com.systex.jbranch.app.server.fps.sot709.SOT709InputVO;
import com.systex.jbranch.app.server.fps.sot709.SOT709OutputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.app.server.fps.sot714.SOT714InputVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHAIADataVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sot130
 * 
 * @author moron
 * @date 2016/08/15
 * @spec null
 */
@Component("sot130")
@Scope("request")
public class SOT130 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT130.class);

	private String getSeqNum(String TXN_ID, String format, Timestamp timeStamp, Integer minNum, Long maxNum, String status, Long nowNum) throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();

		String seqNum = "";
		try {
			seqNum = sn.getNextSerialNumber(TXN_ID);
		} catch (Exception e) {
			sn.createNewSerial(TXN_ID, format, 1, "d", timeStamp, minNum, maxNum, status, nowNum, null);
			seqNum = sn.getNextSerialNumber(TXN_ID);
		}
		return seqNum;
	}

	public void getFeeTypeData(Object body, IPrimitiveMap header) throws Exception {
		SOT130InputVO inputVO = (SOT130InputVO) body;
		SOT130OutputVO return_VO = new SOT130OutputVO();
		dam = this.getDataAccessManager();

		List feeList = new ArrayList();
		Map m = new HashMap();
		m.put("LABEL", "申請議價");
		m.put("DATA", "A");
		m.put("feeTypeIndex", "idxAPPLY");
		feeList.add(m);

		//*贖回再申購不提供生日券*

		int feeTypeIndex = 0;
		//單次議價資料(已申請議價清單)
		SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
		SOT709InputVO sot709InputVO = new SOT709InputVO();
		sot709InputVO.setCustId(inputVO.getCustID());
		sot709InputVO.setStartDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()));
		//		List<SingleFeeRateVO> singleFeeRateList = null;
		SOT709OutputVO sot709OutputVO = null;
		try {
			sot709OutputVO = sot709.getSingleFeeRate(sot709InputVO);
		} catch (Exception e) {
			String errorMsg = e.getMessage();
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			errorMsg = sot712.inquireErrorCode(e.getMessage());
			logger.error(String.format("基金單次單筆議價查詢電文sot709.getSingleFeeRate錯誤:%s", errorMsg), e);
			throw new JBranchException(String.format("基金單次單筆議價查詢錯誤:%s", errorMsg), e);
		}
		// 2017/8/3 因贖回再申購事先議價規則尚未確認，請移除贖回再申購「手續費優惠方式」下拉選項的事先議價功能
		//		singleFeeRateList = sot709OutputVO.getSingleFeeRateList();
		//		for (SingleFeeRateVO rateVO : singleFeeRateList) {
		//			Map sRate = new HashMap();
		//			// 單次議價列表會列出〈代號+短名稱+折數〉。
		//			sRate.put("LABEL", "事先議價" + rateVO.getFeeRate() + "折" ); //議價編號beneCode
		//			sRate.put("DATA",  "D" ); //議價編號beneCode
		//			sRate.put("FeeRate", rateVO.getFeeRate());
		//			sRate.put("EviNum", rateVO.getEviNum());
		//			sRate.put("FundNo", rateVO.getFundNo());
		//			sRate.put("BeneCode", rateVO.getBeneCode());
		//			sRate.put("InvestAmt", rateVO.getInvestAmt());// 事先議價的申購金額，將來下單要可以事後 變更事先議價的申購金額
		//			sRate.put("feeTypeIndex", "idx" + feeTypeIndex++);
		//			feeList.add(sRate);
		//		}

		//查詢次數型優惠
		sot709OutputVO = new SOT709OutputVO();
		sot709OutputVO = sot709.getMeteringFeeRate(sot709InputVO);
		List<MeteringFeeRateVO> meteringRateList = sot709OutputVO.getMeteringFeeRateList();
		if (CollectionUtils.isNotEmpty(meteringRateList)) {
			for (MeteringFeeRateVO mvo : meteringRateList) {
				if (StringUtils.isNotBlank(mvo.getCntSingle())) { //空白表示沒有優惠
					Map mRate = new HashMap();
					mRate.put("LABEL", mvo.getGroupName()); //團體名稱
					mRate.put("feeType", "E"); //E：次數型團體優惠
					mRate.put("groupCode", mvo.getCntSingle()); //臨櫃單筆
					mRate.put("totalCount", mvo.getTotalCount());
					mRate.put("UsedCount", mvo.getUsedCount());
					mRate.put("feeTypeIndex", "idx" + feeTypeIndex++);
					feeList.add(mRate);
				}
			}
		}

		return_VO.setSingleFeeRateList(feeList);

		this.sendRtnObject(return_VO);
	}

	public void getSOTCustInfoCT(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT130InputVO inputVO = (SOT130InputVO) body;
		SOT130OutputVO outputVO = new SOT130OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		Boolean isFirstTrade = false;

		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		fp032675DataVO = sot701.getFP032675Data(inputVO_701);
		isFirstTrade = sot701.getIsCustFirstTrade(inputVO_701);

		outputVO.setIsFirstTrade((isFirstTrade) ? "Y" : "N");            //是否首購
		outputVO.setCustRemarks(fp032675DataVO.getCustRemarks());         //弱勢
		outputVO.setAgeUnder70Flag(fp032675DataVO.getAgeUnder70Flag());  //年齡小於70
		outputVO.setEduJrFlag(fp032675DataVO.getEduJrFlag());            //教育程度國中以上(不含國中)
		outputVO.setHealthFlag(fp032675DataVO.getHealthFlag());          //未領有全民健保重大傷病證明

		//錄音序號
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO input712 = new SOT712InputVO();

		input712.setCustID(inputVO.getCustID());
		input712.setProdID(null);
		input712.setProdType("NF");
		input712.setIsFirstTrade(outputVO.getIsFirstTrade()); //是否首購  
		input712.setProfInvestorYN(fp032675DataVO.getCustProFlag());
		input712.setCustRemarks(outputVO.getCustRemarks());
		input712.setCustProRemark(fp032675DataVO.getCustProRemark());
		String isRePurchase = getIsRePurchase(inputVO);
		if (isRePurchase == "N") {
			outputVO.setRecNeeded(false);
		} else {
			outputVO.setRecNeeded(new Boolean(sot712.getIsRecNeeded(input712)));
		}

		this.sendRtnObject(outputVO);
	}

	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		SOT130InputVO inputVO = (SOT130InputVO) body;
		SOT130OutputVO return_VO = new SOT130OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TBSOT_TRADE_MAIN WHERE TRADE_SEQ = :tradeSEQ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setCustDTL(list);

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT D.*, NVL(F.OVS_PRIVATE_YN, 'N') AS OVS_PRIVATE_YN ");
		sql.append(" FROM TBSOT_NF_REDEEM_D D ");
		sql.append(" LEFT JOIN TBPRD_FUND F ON F.PRD_ID = D.RDM_PROD_ID ");
		sql.append(" WHERE D.TRADE_SEQ = :tradeSEQ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> carList = dam.exeQuery(queryCondition);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> curMap = xmlInfo.doGetVariable("SOT.FUND_DECIMAL_POINT", FormatHelper.FORMAT_3);//幣別小數位控制

		for (Map<String, Object> dataMap : carList) {
			String trustCurr = dataMap.get("RDM_TRUST_CURR").toString();
			dataMap.put("CUR_NUM", curMap.get(trustCurr).toString());
			//2017-09-11 取基金-小數點位數
			String prodId = dataMap.get("RDM_PROD_ID").toString();
			if (StringUtils.isNotBlank(prodId)) {
				List<Map<String, Object>> prodInfo = this.getProdInfo(prodId);
				dataMap.put("FUS07", prodInfo.get(0).get("FUS07"));
			}
		}

		return_VO.setCartList(carList);

		//		if(StringUtils.isNotBlank(inputVO.getRdmProdID())) {
		//			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//			sql = new StringBuffer();
		//			sql.append("SELECT * FROM TBSOT_NF_REDEEM_D WHERE RDM_PROD_ID = :prod_id");
		//			queryCondition.setObject("prod_id", inputVO.getRdmProdID());
		//			queryCondition.setQueryString(sql.toString());
		//			list = dam.exeQuery(queryCondition);
		//			return_VO.setProdDTL(list);
		//		}

		this.sendRtnObject(return_VO);
	}

	public void checkBanker(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		SOT130InputVO inputVO = (SOT130InputVO) body;
		SOT130OutputVO return_VO = new SOT130OutputVO();

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL); //加在QUERY??
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT 1 AS EXIST FROM DUAL ");
		sql.append(" WHERE EXISTS (SELECT 'x' FROM TBORG_MEMBER WHERE CUST_ID = :isBanker ");
		sql.append(" and CHANGE_FLAG IN ('A', 'M', 'P') and SERVICE_FLAG = 'A') ");

		queryCondition.setObject("isBanker", inputVO.getCustID());
		queryCondition.setQueryString(sql.toString());

		List listBanker = dam.exeQuery(queryCondition);
		String isBanker = CollectionUtils.isEmpty(listBanker) ? "N" : "Y";
		return_VO.setIsBanker(isBanker);

		this.sendRtnObject(return_VO);
	}

	public void save(Object body, IPrimitiveMap header) throws Exception {
		
		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM

		SOT130InputVO inputVO = (SOT130InputVO) body;
		SOT130OutputVO outputVO = new SOT130OutputVO();
		dam = this.getDataAccessManager();
		
		String bargainApplySeq = "";
		Boolean isBargainNeeded = Boolean.FALSE;
		if (CollectionUtils.isNotEmpty(inputVO.getCartList())) {
			Map<String, Object> cart = inputVO.getCartList().get(0);
			isBargainNeeded = StringUtils.equals("A", cart.get("FEE_TYPE").toString()) ? Boolean.TRUE : Boolean.FALSE;
			//若為申請議價，需先呼叫CRM421存檔並檢核
			if (isBargainNeeded) {
				CRM421OutputVO crm421OutputVO = applyBargain(inputVO);
				if (StringUtils.isNotBlank(crm421OutputVO.getErrorMsg())) {
					throw new APException("議價申請檢核錯誤：" + crm421OutputVO.getErrorMsg()); //顯示錯誤訊息
				} else {
					bargainApplySeq = crm421OutputVO.getApplySeqList().get(0).toString();
				}
			}
		}

		//#0000201: 業管系統-部分贖回 同一交易序號、同一憑證，僅能執行部分贖回一次，若要執行第二筆放入購物車時，系統應該要出錯誤訊息
		//2020-04-22 by Jacky
		StringBuffer sb1 = new StringBuffer();
		QueryConditionIF queryCondition1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb1.append("SELECT * ");
		sb1.append("FROM TBSOT_NF_REDEEM_D ");
		sb1.append("WHERE TRADE_SEQ = :trade_seq AND RDM_CERTIFICATE_ID = :rdm_certID ");
		queryCondition1.setObject("trade_seq", inputVO.getTradeSEQ());
		queryCondition1.setObject("rdm_certID", ObjectUtils.toString(inputVO.getCartList().get(0).get("RDM_CERTIFICATE_ID")));
		queryCondition1.setQueryString(sb1.toString());
		List<Map<String, Object>> list1 = dam.exeQuery(queryCondition1);
		if (!list1.isEmpty()) {
			throw new APException("ehl_01_SOT703_001"); //顯示錯誤訊息
		}

		//境外私募基金檢核
		if(inputVO.getCartList().get(0).get("OVS_PRIVATE_SEQ") != null) {
			BigDecimal ovsSeq = new BigDecimal(ObjectUtils.toString(inputVO.getCartList().get(0).get("OVS_PRIVATE_SEQ")));
			StringBuffer sb = new StringBuffer();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("SELECT 1 ");
			sb.append("FROM TBSOT_NF_REDEEM_D ");
			sb.append("WHERE OVS_PRIVATE_SEQ = :ovsSeq AND RDM_CERTIFICATE_ID = :rdm_certID ");
			qc.setObject("ovsSeq", ovsSeq);
			qc.setObject("rdm_certID", ObjectUtils.toString(inputVO.getCartList().get(0).get("RDM_CERTIFICATE_ID")));
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> ovsList = dam.exeQuery(qc);
			if (CollectionUtils.isNotEmpty(ovsList)) {
				throw new APException("境外私募基金同一商品在同一段交易起訖日間，一個憑證編號只能做一次贖回"); //顯示錯誤訊息
			}
		}
		
		//高資產客戶投組風險權值檢核
		//只限特金，有再申購交易
		if(!StringUtils.equals("M", inputVO.getTrustTS()) && StringUtils.equals("Y", ObjectUtils.toString(inputVO.getCartList().get(0).get("IS_RE_PURCHASE")))
				&& StringUtils.equals("Y", ObjectUtils.toString(inputVO.getCustDTL().get(0).get("HNWC_YN"))) 
				&& StringUtils.equals("Y", ObjectUtils.toString(inputVO.getCustDTL().get(0).get("HNWC_SERVICE_YN")))) {
			//客戶風險屬性非C4，再申購有選擇越級商品
			String prodRiskLV = ObjectUtils.toString(inputVO.getCartList().get(0).get("PCH_PROD_RISK_LV"));
			String custRiskLV = ObjectUtils.toString(inputVO.getCustDTL().get(0).get("KYC_LV"));
			if(!StringUtils.equals("C4", custRiskLV) && (Integer.parseInt(prodRiskLV.substring(1))) > (Integer.parseInt(custRiskLV.substring(1)))) {
				String errorMsg = getHnwcRiskValue(inputVO.getTradeSEQ(), inputVO.getCustDTL().get(0), inputVO.getCartList().get(0));
				if(StringUtils.isNotBlank(errorMsg)) {
					throw new APException(errorMsg);
				}
			}		
		}		
				
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {

			// 需先判斷該筆是否已存在
			TBSOT_TRADE_MAINVO qvo = new TBSOT_TRADE_MAINVO();
			qvo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
//			String branch = (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH);

			if (qvo != null) {
				qvo.setTRADE_STATUS("1");
				dam.update(qvo);
			} else {
				// TBSOT_TRADE_MAIN 只有一筆
				Map<String, Object> cmap = inputVO.getCustDTL().get(0);
				qvo = new TBSOT_TRADE_MAINVO();
//				vo.setBRANCH_NBR(branch);
				/*
				 * About CR list ↓
				 * 	WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5
				 * 	WMS-CR-20190710-05_個人高端客群處業管系統_行銷模組調整申請_P1
				 *
				 * Version ↓
				 * 	2017-01-11 modify by mimi for SOT7XX
				 * 	2019-07-24 modify by ocean
				 *
				 */
				if (uhrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sb = new StringBuffer();
					
					sb.append("SELECT BRANCH_NBR ");
					sb.append("FROM TBORG_UHRM_BRH ");
					sb.append("WHERE EMP_ID = :loginID ");

					queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					queryCondition.setQueryString(sb.toString());

					List<Map<String, Object>> loginBreach = dam.exeQuery(queryCondition);
					
					if (loginBreach.size() > 0) {
						qvo.setBRANCH_NBR((String) loginBreach.get(0).get("BRANCH_NBR"));
					} else {
						throw new APException("人員無有效分行"); //顯示錯誤訊息
					}
				} else {
					SotUtils utils = (SotUtils) PlatformContext.getBean("sotUtils");
					qvo.setBRANCH_NBR(utils.getBranchNbr(inputVO.getTrustTS(), String.valueOf(getCommonVariable(SystemVariableConsts.LOGINBRH))));
				}

				qvo.setTRADE_SEQ(inputVO.getTradeSEQ());
				qvo.setPROD_TYPE("1");
				qvo.setTRADE_TYPE("2");
				qvo.setCUST_ID(ObjectUtils.toString(cmap.get("CUST_ID")));
				qvo.setCUST_NAME(ObjectUtils.toString(cmap.get("CUST_NAME")));
				qvo.setAGENT_ID(ObjectUtils.toString(cmap.get("AGENT_ID")));
				qvo.setAGENT_NAME(ObjectUtils.toString(cmap.get("AGENT_NAME")));
				qvo.setKYC_LV(ObjectUtils.toString(cmap.get("KYC_LV")));
				qvo.setPROF_INVESTOR_YN(StringUtils.isBlank(ObjectUtils.toString(cmap.get("PROF_INVESTOR_YN"))) ? "N" : ObjectUtils.toString(cmap.get("PROF_INVESTOR_YN")));
				if (StringUtils.isNotBlank(ObjectUtils.toString(cmap.get("KYC_DUE_DATE")))) {
					long d = dateFormat.parse(cmap.get("KYC_DUE_DATE").toString()).getTime();
					qvo.setKYC_DUE_DATE(new Timestamp(d));
				}
				qvo.setCUST_REMARKS(ObjectUtils.toString(cmap.get("CUST_REMARKS")));
				qvo.setIS_OBU(ObjectUtils.toString(cmap.get("IS_OBU")));
				qvo.setIS_AGREE_PROD_ADV("Y".equals(ObjectUtils.toString(cmap.get("TX_FLAG"))) ? "Y" : (StringUtil.isBlank(ObjectUtils.toString(cmap.get("IS_AGREE_PROD_ADV"))) ? "" : ("Y".equals(ObjectUtils.toString(cmap.get("IS_AGREE_PROD_ADV"))) ? "Y" : "N")));
				qvo.setPI_REMARK(ObjectUtils.toString(cmap.get("PI_REMARK"))); //專業投資人註記
				if (StringUtils.isNotBlank(ObjectUtils.toString(cmap.get("BARGAIN_DUE_DATE")))) {
					long d = dateFormat.parse(cmap.get("BARGAIN_DUE_DATE").toString()).getTime();
					qvo.setBARGAIN_DUE_DATE(new Timestamp(d));
				}
				qvo.setTRADE_STATUS(inputVO.getStatus());
				//			vo.setBARGAIN_FEE_FLAG(BARGAIN_FEE_FLAG);
				qvo.setIS_REC_NEEDED("N");
				//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
				qvo.setTRUST_TRADE_TYPE(inputVO.getTrustTS());  //M:金錢信託  S:特金交易

				//0000275: 金錢信託受監護受輔助宣告交易控管調整
				if (StringUtils.isBlank(inputVO.getGUARDIANSHIP_FLAG())) {
					qvo.setGUARDIANSHIP_FLAG(" ");
				} else {
					qvo.setGUARDIANSHIP_FLAG(inputVO.getGUARDIANSHIP_FLAG());
				}
				
				qvo.setHNWC_YN(ObjectUtils.toString(cmap.get("HNWC_YN")));
				qvo.setHNWC_SERVICE_YN(ObjectUtils.toString(cmap.get("HNWC_SERVICE_YN")));
				
				dam.create(qvo);
			}

			if (CollectionUtils.isNotEmpty(inputVO.getCartList())) {
				// TBSOT_NF_REDEEM_D
				TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
				vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
				Map<String, Object> map = inputVO.getCartList().get(0);
				BigDecimal newTrade_SEQNO = null;
				SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
				if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("SEQ_NO")))) {
					newTrade_SEQNO = new BigDecimal(ObjectUtils.toString(map.get("SEQ_NO")));
				} else {
					newTrade_SEQNO = new BigDecimal(sot712.getnewTrade_SEQNO());
				}
				insertREDEEM(newTrade_SEQNO, inputVO.getTradeSEQ(), map, bargainApplySeq);
				if (isBargainNeeded) {
					//有申請議價
					vo.setIS_BARGAIN_NEEDED("Y");
					vo.setBARGAIN_FEE_FLAG("1");
				} else {
					vo.setIS_BARGAIN_NEEDED("N");
				}
				dam.update(vo);

				// 2.檢核電文
				SOT703InputVO inputVO_703 = new SOT703InputVO();
				SOT703OutputVO outputVO_703 = new SOT703OutputVO();

				inputVO_703.setTradeSeq(inputVO.getTradeSEQ());
				inputVO_703.setSeqNo(newTrade_SEQNO.toString());

				SOT703 sot703 = (SOT703) PlatformContext.getBean("sot703");
				outputVO_703 = sot703.verifyESBRedeemNF(inputVO_703); //驗證電文 先關

				String errorMsg = outputVO_703.getErrorMsg();
				if (!"".equals(errorMsg) && null != errorMsg) {
					TBSOT_NF_REDEEM_DPK errPK = new TBSOT_NF_REDEEM_DPK();
					errPK.setTRADE_SEQ(inputVO.getTradeSEQ());
					errPK.setSEQ_NO(newTrade_SEQNO);
					TBSOT_NF_REDEEM_DVO errVO = new TBSOT_NF_REDEEM_DVO();
					errVO.setcomp_id(errPK);
					errVO = (TBSOT_NF_REDEEM_DVO) dam.findByPKey(TBSOT_NF_REDEEM_DVO.TABLE_UID, errVO.getcomp_id());

					//刪除明細
					if (null != errVO) {
						dam.delete(errVO);
					}

					//===若無明細，則將主檔也刪除
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

					StringBuffer sb = new StringBuffer();
					sb.append("SELECT * ");
					sb.append("FROM TBSOT_NF_REDEEM_D ");
					sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
					queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
					queryCondition.setQueryString(sb.toString());

					List<Map<String, Object>> list = dam.exeQuery(queryCondition);

					if (list.size() == 0) {
						TBSOT_TRADE_MAINVO mVO = new TBSOT_TRADE_MAINVO();
						mVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
						dam.delete(mVO);
					}

					outputVO.setErrorMsg(errorMsg);

				}
				//取得判斷是否為短期交易或何種類型的值
				outputVO.setShort_1(outputVO_703.getShort_1());
				outputVO.setShort_2(outputVO_703.getShort_2());
				outputVO.setShort_3(outputVO_703.getShort_3());
			}

			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	// TBSOT_NF_REDEEM_D
	private void insertREDEEM(BigDecimal seqNO, String TradeSEQ, Map<String, Object> map, String bargainApplySeq) throws JBranchException, ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		TBSOT_NF_REDEEM_DVO rvo = new TBSOT_NF_REDEEM_DVO();
		TBSOT_NF_REDEEM_DPK rpk = new TBSOT_NF_REDEEM_DPK();
//		try {
			rpk.setSEQ_NO(seqNO);
			rpk.setTRADE_SEQ(TradeSEQ);
			rvo = (TBSOT_NF_REDEEM_DVO) dam.findByPKey(TBSOT_NF_REDEEM_DVO.TABLE_UID, rpk);
			if (rvo != null) {
				dam.delete(rvo);
			}
			rvo = new TBSOT_NF_REDEEM_DVO();
			rvo.setcomp_id(rpk);
			//		rvo.setBATCH_SEQ(BATCH_SEQ);
			//		rvo.setBATCH_NO(BATCH_NO);
			rvo.setRDM_PROD_ID(ObjectUtils.toString(map.get("RDM_PROD_ID")));
			rvo.setRDM_PROD_NAME(ObjectUtils.toString(map.get("RDM_PROD_NAME")));
			rvo.setRDM_PROD_CURR(ObjectUtils.toString(map.get("RDM_PROD_CURR")));
			rvo.setRDM_PROD_RISK_LV(ObjectUtils.toString(map.get("RDM_PROD_RISK_LV")));
			rvo.setRDM_CERTIFICATE_ID(ObjectUtils.toString(map.get("RDM_CERTIFICATE_ID")));
			rvo.setRDM_TRUST_CURR_TYPE(ObjectUtils.toString(map.get("RDM_TRUST_CURR_TYPE")));
			rvo.setRDM_TRUST_CURR(ObjectUtils.toString(map.get("RDM_TRUST_CURR")));
			rvo.setRDM_TRADE_TYPE(ObjectUtils.toString(map.get("RDM_TRADE_TYPE")));
			rvo.setRDM_TRADE_TYPE_D(ObjectUtils.toString(map.get("RDM_TRADE_TYPE_D")));
			rvo.setNOT_VERTIFY(ObjectUtils.toString(map.get("NOT_VERTIFY")));
			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("RDM_UNIT"))))
				rvo.setRDM_UNIT(new BigDecimal(ObjectUtils.toString(map.get("RDM_UNIT"))));
			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("RDM_TRUST_AMT"))))
				rvo.setRDM_TRUST_AMT(new BigDecimal(ObjectUtils.toString(map.get("RDM_TRUST_AMT"))));
			rvo.setREDEEM_TYPE(ObjectUtils.toString(map.get("REDEEM_TYPE")));
			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("UNIT_NUM")))) {
				rvo.setUNIT_NUM(new BigDecimal(ObjectUtils.toString(map.get("UNIT_NUM"))).setScale(6, BigDecimal.ROUND_HALF_UP));
			}

			rvo.setCREDIT_ACCT(ObjectUtils.toString(map.get("CREDIT_ACCT")));
			rvo.setIS_END_CERTIFICATE(ObjectUtils.toString(map.get("IS_END_CERTIFICATE")));
			rvo.setIS_RE_PURCHASE(ObjectUtils.toString(map.get("IS_RE_PURCHASE")));
			rvo.setTRADE_DATE_TYPE(ObjectUtils.toString(map.get("TRADE_DATE_TYPE")));
			rvo.setTRUST_ACCT(ObjectUtils.toString(map.get("TRUST_ACCT")));

			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("TRADE_DATE")))) {
				long d = dateFormat.parse(map.get("TRADE_DATE").toString()).getTime();
				rvo.setTRADE_DATE(new Timestamp(d));
			} else {
				rvo.setTRADE_DATE(new Timestamp(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()).getTime()));
			}
			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("RDM_FEE"))))
				rvo.setRDM_FEE(new BigDecimal(ObjectUtils.toString(map.get("RDM_FEE"))));
			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("RDM_PROD_NAME"))))
				rvo.setRDM_PROD_NAME(ObjectUtils.toString(map.get("RDM_PROD_NAME")));
			rvo.setPCH_PROD_ID(ObjectUtils.toString(map.get("PCH_PROD_ID")));
			rvo.setPCH_PROD_NAME(ObjectUtils.toString(map.get("PCH_PROD_NAME")));
			rvo.setPCH_PROD_CURR(ObjectUtils.toString(map.get("PCH_PROD_CURR")));
			rvo.setPCH_PROD_RISK_LV(ObjectUtils.toString(map.get("PCH_PROD_RISK_LV")));
			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("DEFAULT_FEE_RATE"))))
				rvo.setDEFAULT_FEE_RATE(new BigDecimal(ObjectUtils.toString(map.get("DEFAULT_FEE_RATE"))));
			rvo.setPROSPECTUS_TYPE(ObjectUtils.toString(map.get("PROSPECTUS_TYPE"))); //公開說明書
			rvo.setFEE_TYPE(ObjectUtils.toString(map.get("FEE_TYPE")));
			if (StringUtils.equals("A", ObjectUtils.toString(map.get("FEE_TYPE")))) {
				//申請議價
				rvo.setBARGAIN_STATUS("1");
				rvo.setBARGAIN_APPLY_SEQ(bargainApplySeq);
			} else {
				//rvo.setBARGAIN_STATUS(ObjectUtils.toString(map.get("BARGAIN_STATUS")));
				rvo.setBARGAIN_APPLY_SEQ(ObjectUtils.toString(map.get("BARGAIN_APPLY_SEQ")));
			}
			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("FEE_RATE"))))
				rvo.setFEE_RATE(new BigDecimal(ObjectUtils.toString(map.get("FEE_RATE"))));
			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("FEE"))))
				rvo.setFEE(new BigDecimal(ObjectUtils.toString(map.get("FEE"))));
			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("FEE_DISCOUNT"))))
				rvo.setFEE_DISCOUNT(new BigDecimal(ObjectUtils.toString(map.get("FEE_DISCOUNT"))));
			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("STOP_LOSS_PERC"))))
				rvo.setSTOP_LOSS_PERC(new BigDecimal(ObjectUtils.toString(map.get("STOP_LOSS_PERC"))));
			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("TAKE_PROFIT_PERC"))))
				rvo.setTAKE_PROFIT_PERC(new BigDecimal(ObjectUtils.toString(map.get("TAKE_PROFIT_PERC"))));
			if (map.get("PRESENT_VAL") != null)
				rvo.setPRESENT_VAL(new BigDecimal(ObjectUtils.toString(map.get("PRESENT_VAL"))));
			rvo.setPL_NOTIFY_WAYS(ObjectUtils.toString(map.get("PL_NOTIFY_WAYS")));
			rvo.setNARRATOR_ID(ObjectUtils.toString(map.get("NARRATOR_ID")));
			rvo.setNARRATOR_NAME(ObjectUtils.toString(map.get("NARRATOR_NAME")));
			//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
			rvo.setCONTRACT_ID(ObjectUtils.toString(map.get("CONTRACT_ID")));
			rvo.setTRUST_PEOP_NUM(StringUtils.isNotEmpty(ObjectUtils.toString(map.get("TRUST_PEOP_NUM"))) ? ObjectUtils.toString(map.get("TRUST_PEOP_NUM")) : "N");
			rvo.setOVS_PRIVATE_SEQ(map.get("OVS_PRIVATE_SEQ") == null ? null : new BigDecimal(ObjectUtils.toString(map.get("OVS_PRIVATE_SEQ"))));
			dam.create(rvo);

//		} catch (Exception e) {
//			logger.debug(e.getMessage(), e);
//		}
	}

	/**
	 * 相當於議價的加入清單; 存檔並送單次議價檢核電文
	 *
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private CRM421OutputVO applyBargain(SOT130InputVO inputVO) throws Exception {
		CRM421OutputVO crm421OutputVO = new CRM421OutputVO();
		CRM421InputVO crm421InputVO = new CRM421InputVO();

		Map<String, Object> custMap = inputVO.getCustDTL().get(0);
		Map<String, Object> prodMap = inputVO.getCartList().get(0);

		//initial data
		crm421InputVO.setApply_type("1"); //基金單筆申購
		crm421InputVO.setProd_type("1"); //基金
		crm421InputVO.setCust_id(ObjectUtils.toString(custMap.get("CUST_ID")));
		crm421InputVO.setProd_id(ObjectUtils.toString(prodMap.get("PCH_PROD_ID")));
		crm421InputVO.setProd_name(ObjectUtils.toString(prodMap.get("PCH_PROD_NAME")));
		crm421InputVO.setTrustCurrType(ObjectUtils.toString(prodMap.get("RDM_TRUST_CURR_TYPE")));
		crm421InputVO.setTrust_curr(ObjectUtils.toString(prodMap.get("RDM_TRUST_CURR"))); //原贖回標的信託幣別
		crm421InputVO.setPurchase_amt(ObjectUtils.toString(prodMap.get("PRESENT_VAL")));
		crm421InputVO.setDefaultFeeRate(new BigDecimal(ObjectUtils.toString(prodMap.get("DEFAULT_FEE_RATE"))));
		crm421InputVO.setFee(ObjectUtils.toString(prodMap.get("FEE")));
		crm421InputVO.setFee_discount(new BigDecimal(ObjectUtils.toString(prodMap.get("FEE_DISCOUNT"))));
		crm421InputVO.setFee_rate(new BigDecimal(ObjectUtils.toString(prodMap.get("FEE_RATE"))));
		crm421InputVO.setDiscount_type("1"); //by折數			
		crm421InputVO.setBrg_reason(ObjectUtils.toString(prodMap.get("BRG_REASON"))); //議價原因		
		crm421InputVO.setCon_degree(getCustConDegree(ObjectUtils.toString(custMap.get("CUST_ID"))));
		crm421InputVO.setHighest_auth_lv(ObjectUtils.toString(prodMap.get("FEE_DISCOUNT")));

		CRM421 crm421 = (CRM421) PlatformContext.getBean("crm421");
		//取得最高層級授權主管
		CRM421OutputVO crmoutput = crm421.getHighest_auth_lv(crm421InputVO);
		List<Map<String, Object>> mapList = crmoutput.getHighest_lvList();
		if (CollectionUtils.isNotEmpty(mapList)) {
			crm421InputVO.setHighest_auth_lv(mapList.get(0).get("HIGHEST_LV").toString());
		} else {
			crm421InputVO.setHighest_auth_lv("");
		}

		//相當於議價的加入清單
		crm421OutputVO = crm421.addToList(crm421InputVO);

		return crm421OutputVO;
	}

	/**
	 * 取得客戶貢獻度等級
	 *
	 * @param custId
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String getCustConDegree(String custId) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CON_DEGREE from TBCRM_CUST_MAST ");
		sb.append("WHERE CUST_ID = :custID ");
		queryCondition.setObject("custID", custId);
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> conlist = dam.exeQuery(queryCondition);
		String conDegree = "";
		if (CollectionUtils.isNotEmpty(conlist)) {
			conDegree = (String) conlist.get(0).get("CON_DEGREE");
		}

		return conDegree;
	}

	/**
	 * 取得 是否再申購 Y：是 N：否
	 *
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String getIsRePurchase(SOT130InputVO inputVO) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT count(IS_RE_PURCHASE) as COUNT from TBSOT_NF_REDEEM_D ");
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		sb.append("AND IS_RE_PURCHASE = 'Y' ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> conlist = dam.exeQuery(queryCondition);
		String isRePurchase = "";
		if (CollectionUtils.isNotEmpty(conlist)) {
			isRePurchase = "0".equals(conlist.get(0).get("COUNT").toString()) ? "N" : "Y";
		}
		return isRePurchase;
	}

	// 取得商品資訊
	public void getProdDTL(Object body, IPrimitiveMap header) throws Exception {
		SOT130InputVO inputVO = (SOT130InputVO) body;
		SOT130OutputVO outputVO = new SOT130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		boolean isFitness = StringUtils.equals("N", (String) new XmlInfo().getVariable("SOT.FITNESS_YN", "NF", "F3")) ? false : true;

		outputVO.setWarningMsg("");
		outputVO.setErrorMsg("");

		boolean isFitnessOK = false;
		if (isFitness) {
			// 1.適配，由商品查詢取得，邏輯需一致
			PRD110OutputVO prdOutputVO = new PRD110OutputVO();
			PRD110InputVO prdInputVO = new PRD110InputVO();
			prdInputVO.setCust_id(inputVO.getCustID().toUpperCase());
			prdInputVO.setType("4");
			prdInputVO.setFund_id(inputVO.getPchProdID());

			PRD110 prd110 = (PRD110) PlatformContext.getBean("prd110");
			prdOutputVO = prd110.inquire(prdInputVO);

			if (CollectionUtils.isNotEmpty(prdOutputVO.getResultList())) {
				String warningMsg = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("warningMsg");
				String errId = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("errorID");

				if (StringUtils.isBlank(errId)) {
					isFitnessOK = true;
				}

				outputVO.setWarningMsg(warningMsg);
				outputVO.setErrorMsg(errId);
			}
		} else {
			isFitnessOK = true;
		}

		// 2.查詢商品主檔
		if (isFitnessOK) { //  
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT FUND.PRD_ID, FUND.FUND_CNAME, FUND.CURRENCY_STD_ID, FUND.RISKCATE_ID, NVL(FUND.OVS_PRIVATE_YN, 'N') as OVS_PRIVATE_YN ");
			sb.append("FROM TBPRD_FUND FUND ");
			sb.append("WHERE PRD_ID = :prodID ");
			queryCondition.setObject("prodID", inputVO.getPchProdID());
			queryCondition.setQueryString(sb.toString());

			outputVO.setProdDTL(dam.exeQuery(queryCondition));

			List<Map<String, Object>> list = this.getProdInfo(inputVO.getPchProdID());
			outputVO.getProdDTL().get(0).put("FUS20", list.get(0).get("FUS20"));

		}

		this.sendRtnObject(outputVO);
	}

	private List<Map<String, Object>> getProdInfo(String prodID) throws JBranchException {
		StringBuffer sql = new StringBuffer();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		List<Map<String, Object>> list = null;
//		try {
			sql.append("SELECT F.FUND_CNAME, F. CURRENCY_STD_ID, F.RISKCATE_ID, F.FUS07, ");
			sql.append(" NVL(F.OVS_PRIVATE_YN, 'N') AS OVS_PRIVATE_YN, P.TRADE_DATE AS TRADE_DATE_OVSPRI, P.SEQ_NO AS OVS_PRIVATE_SEQ ");
			sql.append(" FROM TBPRD_FUND F ");
			sql.append(" LEFT JOIN TBPRD_FUND_OVS_PRIVATE P ON P.PRD_ID = F.PRD_ID AND P.TRADE_TYPE = '2' AND TRUNC(SYSDATE) BETWEEN TRUNC(P.START_DATE) AND TRUNC(P.END_DATE) ");
			sql.append(" WHERE F.PRD_ID = :PRD_ID ");
			queryCondition.setObject("PRD_ID", prodID);
			queryCondition.setQueryString(sql.toString());
			list = dam.exeQuery(queryCondition);

			//檢核境外私募基金是否在可贖回起訖時間內
			if(CollectionUtils.isNotEmpty(list) && 
					StringUtils.equals("Y", list.get(0).get("OVS_PRIVATE_YN").toString()) && 
					list.get(0).get("TRADE_DATE_OVSPRI") == null) {
				throw new APException("境外私募基金不在贖回交易開放起訖區間內"); //顯示錯誤訊息
			}
			
			//取得業務別
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT f.*,i.FUS20,i.FUS40 ");
			sb.append("FROM TBPRD_FUND f ");
			sb.append("LEFT JOIN TBPRD_FUNDINFO i on (f.PRD_ID=i.PRD_ID) ");
			sb.append("WHERE f.PRD_ID = :prodID ");
			queryCondition.setObject("prodID", prodID);
			queryCondition.setQueryString(sb.toString());
			Map map = (Map) dam.exeQuery(queryCondition).get(0);
			list.get(0).put("FUS20", map.get("FUS20"));
			list.get(0).put("FUS40", map.get("FUS40"));
//		} catch (Exception e) {
//			logger.debug(e.getMessage(), e);
//		}
		return list;
	}

	public void getProdInfo(Object body, IPrimitiveMap header) throws JBranchException {
		dam = this.getDataAccessManager();
		SOT130InputVO inputVO = (SOT130InputVO) body;
		this.sendRtnObject(this.getProdInfo(inputVO.getRdmProdID()));
	}

	public void next(Object body, IPrimitiveMap header) throws Exception {

		WorkStation ws = DataManager.getWorkStation(uuid);
		SOT130InputVO inputVO = (SOT130InputVO) body;
		SOT130OutputVO outputVO = new SOT130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		//若有申請議價，則送出覆核
		callCRM421ApplySingle(inputVO.getTradeSEQ());

		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		if (null != vo) {
			vo.setTRADE_STATUS("2"); //2:風控檢核中
			vo.setModifier(ws.getUser().getUserID());
			vo.setLastupdate(new Timestamp(System.currentTimeMillis()));

			dam.update(vo);
		}

		this.sendRtnObject(null);
	}

	/**
	 * 議價鍵機，送出覆核
	 *
	 * @param tradeSeq
	 * @throws Exception
	 */
	private void callCRM421ApplySingle(String tradeSeq) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT '1' as APPLY_TYPE, PCH_PROD_NAME as PROD_NAME, BARGAIN_APPLY_SEQ as APPLY_SEQ ");
		sb.append("FROM TBSOT_NF_REDEEM_D WHERE TRADE_SEQ = :tradeSEQ and FEE_TYPE = 'A' ");
		queryCondition.setObject("tradeSEQ", tradeSeq);
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (CollectionUtils.isNotEmpty(list)) {
			CRM421InputVO crm421InputVO = new CRM421InputVO();
			crm421InputVO.setList(list);

			CRM421 crm421 = (CRM421) PlatformContext.getBean("crm421");
			crm421.applySingle(crm421InputVO);
		}
	}

	public void delProd(Object body, IPrimitiveMap header) throws JBranchException {
		SOT130InputVO inputVO = (SOT130InputVO) body;
		SOT130OutputVO outputVO = new SOT130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * ");
		sb.append("FROM TBSOT_NF_REDEEM_D ");
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		condition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		condition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(condition);

		if (list.size() == 1) {
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition.setQueryString("DELETE FROM TBSOT_TRADE_MAIN WHERE TRADE_SEQ = :seq ");
			condition.setObject("seq", inputVO.getTradeSEQ());
			dam.exeUpdate(condition);
		}

		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("DELETE FROM TBSOT_NF_REDEEM_D WHERE TRADE_SEQ = :tradeSEQ AND  SEQ_NO = :seqNO");
		condition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		condition.setObject("seqNO", inputVO.getSeqNo());
		dam.exeUpdate(condition);
		this.sendRtnObject(null);
	}

	/**
	 * 風控 傳送OP交易並列印表單
	 *
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void goOP(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT130InputVO inputVO = (SOT130InputVO) body;
		SOT130OutputVO outputVO = new SOT130OutputVO();
		dam = this.getDataAccessManager();

		TBSOT_TRADE_MAINVO mainVo = new TBSOT_TRADE_MAINVO();
		mainVo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		//		mainVo.setPROSPECTUS_TYPE(this.getProspectusType(inputVO)); // MIMI 2/18 (公開說明書) 欄位都先寫死放1
		if (StringUtils.isNotBlank(inputVO.getRecSEQ())) {
			mainVo.setREC_SEQ(inputVO.getRecSEQ());//錄音序號
		}
		dam.update(mainVo);

		// 檢核電文
		SOT703InputVO inputVO_703 = new SOT703InputVO();
		SOT703OutputVO outputVO_703 = new SOT703OutputVO();

		inputVO_703.setCustId(inputVO.getCustID());
		inputVO_703.setTradeSeq(inputVO.getTradeSEQ());

		SOT703 sot703 = (SOT703) PlatformContext.getBean("sot703");
		sot703.confirmESBRedeemNF(inputVO_703); //確認電文

		/*Method說明	基金申購確認
		發送電文NFBRN1
		需傳入參數：tradeSeq
		Method名稱	confirmESBPurchaseNF(SOT703InputVO inputVO) 
		RETURN void
		
		TODO沒有回傳值outputVO_703?
		*/

		String errorMsg = outputVO_703.getErrorMsg();
		if (!"".equals(errorMsg) && null != errorMsg) {
			outputVO.setErrorMsg(errorMsg);
		} else {
			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

			vo.setTRADE_STATUS("3"); //3:傳送OP交易並列印表單 
			vo.setSEND_DATE(new Timestamp(System.currentTimeMillis())); //傳送OP後要設SEND_DATE
			vo.setIS_REC_NEEDED(inputVO.getIsRecNeeded() == "true" ? "Y" : "N");
			dam.update(vo);

			// for CBS 測試用
			cbsservice.setTBSOT_TRADE_MAIN_LASTUPDATE_FOR_TEST(vo.getTRADE_SEQ());
			
			//境外私募基金寫入一筆資料TBSOT_NF_REDEEM_D_OVS_PRI
			insertRedeemOvsPri(inputVO.getTradeSEQ());
		}

		this.sendRtnObject(outputVO);
	}

	/**
	 * 取得境外後收型基金贖回限制先進先出相關參數
	 *
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getBackendConstraintParameter(Object body, IPrimitiveMap header) throws JBranchException {
		SOT130InputVO inputVO = (SOT130InputVO) body;
		SOT130OutputVO outputVO = new SOT130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT IS_BACKEND, FUSM10 FROM TBPRD_FUND WHERE PRD_ID = :prd_id ");
		queryCondition.setObject("prd_id", inputVO.getOutProdID());
		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setProdDTL(list);
		this.sendRtnObject(outputVO);
	}

	public void getGoalDueFund(Object body, IPrimitiveMap header) throws JBranchException {
		SOT130InputVO inputVO = (SOT130InputVO) body;

		List<Map<String, Character>> data = Manager.manage(this.getDataAccessManager())
				.append("SELECT CASE WHEN ( ")
				.append("	SUBSTR(FUSM98, 32, 1) = 'Y' and ")
				.append("	TO_CHAR(SYSDATE, 'YYYY') - 1911 || TO_CHAR(SYSDATE, 'MMDD') <= SUBSTR(FUSM98, 39, 8) ")
				.append(") THEN 'Y' ELSE 'N' END AS GOAL_DUE_FUND ")
				.append("FROM TBPMS_NFFUSMP0 ")
				.append("WHERE FUS01 || FUS02 = :prdID ")
				.put("prdID", inputVO.getRdmProdID())
				.query();

		sendRtnObject(data.isEmpty()? "N": data.get(0).get("GOAL_DUE_FUND"));
	}
	
	//新增一筆資料進TBSOT_NF_REDEEM_D_OVS_PRI 境外私募基金贖回修正單位數資料檔
	//之後調整單位數使用
	private void insertRedeemOvsPri(String tradeSeq) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT D.*, M.CUST_ID, M.CUST_NAME FROM TBSOT_NF_REDEEM_D D, TBSOT_TRADE_MAIN M WHERE D.TRADE_SEQ = :tradeSeq AND M.TRADE_SEQ = D.TRADE_SEQ AND D.OVS_PRIVATE_SEQ IS NOT NULL ");
		queryCondition.setObject("tradeSeq", tradeSeq);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		//有境外商品序號TBPRD_FUND_OVS_PRIVATE.SEQ_NO
		if(CollectionUtils.isNotEmpty(list) && list.get(0).get("OVS_PRIVATE_SEQ") != null) {
			TBSOT_NF_REDEEM_D_OVS_PRIVO vo = new TBSOT_NF_REDEEM_D_OVS_PRIVO();
			TBSOT_NF_REDEEM_D_OVS_PRIPK pk = new TBSOT_NF_REDEEM_D_OVS_PRIPK();
			pk.setPRD_SEQ_NO((BigDecimal)list.get(0).get("OVS_PRIVATE_SEQ"));
			pk.setBATCH_SEQ((String)list.get(0).get("BATCH_SEQ"));	
			vo.setcomp_id(pk);
			
			vo.setPRD_ID((String)list.get(0).get("RDM_PROD_ID"));
			vo.setPRD_NAME((String)list.get(0).get("RDM_PROD_NAME"));	
			vo.setCUST_ID((String)list.get(0).get("CUST_ID"));
			vo.setCUST_NAME((String)list.get(0).get("CUST_NAME"));
			vo.setCERTIFICATE_ID((String)list.get(0).get("RDM_CERTIFICATE_ID"));
			vo.setRDM_UNIT((BigDecimal)list.get(0).get("RDM_UNIT"));
			vo.setRDM_TRUST_AMT((BigDecimal)list.get(0).get("RDM_TRUST_AMT"));
			vo.setREDEEM_TYPE(ObjectUtils.toString(list.get(0).get("REDEEM_TYPE")));
			vo.setUNIT_NUM((BigDecimal)list.get(0).get("UNIT_NUM"));
			vo.setPRESENT_VAL((BigDecimal)list.get(0).get("PRESENT_VAL"));
			vo.setRDM_TRADE_DATE((Timestamp)list.get(0).get("TRADE_DATE"));
			vo.setSTATUS("0"); //尚未列印贖回總表
			dam.create(vo);
		}
	}
		
	/***
	 * 高資產客戶投組風險權值檢核
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private String getHnwcRiskValue(String tradeSeq, Map<String, Object> custData, Map<String, Object> prodData) throws Exception {
		//取得購物車中各商品P值再申購金額合計
		Map<String, BigDecimal> cartBuy = getAmtBuyByPVal(tradeSeq); 
		//取得購物車中各商品P值贖回金額合計
		Map<String, BigDecimal> cartSell = getAmtSellByPVal(tradeSeq); 
		//再申購商品風險值
		String prodRiskLv = ObjectUtils.toString(prodData.get("PCH_PROD_RISK_LV"));
		//贖回標的商品風險值
		String sellRiskLv = ObjectUtils.toString(prodData.get("RDM_PROD_RISK_LV"));
		//信託幣別
		String trustCurr = ObjectUtils.toString(prodData.get("RDM_TRUST_CURR"));
		//取得這次申購商品信託幣別買匯
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		BigDecimal rate = sot110.getBuyRate(trustCurr);
		//準備電文資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		//取得高資產客戶註記資料
		CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(ObjectUtils.toString(custData.get("CUST_ID")));
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(ObjectUtils.toString(custData.get("CUST_ID")));
		inputVO714.setCUST_KYC(ObjectUtils.toString(custData.get("KYC_LV"))); //客戶C值
		inputVO714.setSP_YN(hnwcData.getSpFlag()); //是否為特定客戶
		inputVO714.setPROD_RISK(prodRiskLv); //商品風險檢核值
		BigDecimal amtBuy = (new BigDecimal(ObjectUtils.toString(prodData.get("PRESENT_VAL")))).multiply(rate);//這次申購金額折台
		
		for(int i = 1; i <= 4; i++) {
			String pVal = "P" + String.valueOf(i);
			//以這次申購商品風險檢核值判斷是否需要加上這次申購金額
			BigDecimal amtBuyPval = StringUtils.equals(pVal, prodRiskLv) ? amtBuy : BigDecimal.ZERO;
			//以這次贖回商品風險檢核值判斷是否需要加上這次贖回金額
			BigDecimal amtSellPval = StringUtils.equals(pVal, sellRiskLv) ? amtBuy : BigDecimal.ZERO;
			//這次再申購金額加上購物車中各商品P值再申購金額
			//這次贖回金額加上購物車中各商品P值贖回金額
			switch(pVal) {
				case "P1":
					inputVO714.setAMT_BUY_1(amtBuyPval.add(cartBuy.get("P1") == null ? BigDecimal.ZERO : cartBuy.get("P1"))); 
					inputVO714.setAMT_SELL_1(amtSellPval.add(cartSell.get("P1") == null ? BigDecimal.ZERO : cartSell.get("P1")));
					break;
				case "P2":
					inputVO714.setAMT_BUY_2(amtBuyPval.add(cartBuy.get("P2") == null ? BigDecimal.ZERO : cartBuy.get("P2")));
					inputVO714.setAMT_SELL_2(amtSellPval.add(cartSell.get("P2") == null ? BigDecimal.ZERO : cartSell.get("P2")));
					break;
				case "P3":
					inputVO714.setAMT_BUY_3(amtBuyPval.add(cartBuy.get("P3") == null ? BigDecimal.ZERO : cartBuy.get("P3")));
					inputVO714.setAMT_SELL_3(amtSellPval.add(cartSell.get("P3") == null ? BigDecimal.ZERO : cartSell.get("P3")));
					break;
				case "P4":
					inputVO714.setAMT_BUY_4(amtBuyPval.add(cartBuy.get("P4") == null ? BigDecimal.ZERO : cartBuy.get("P4")));
					inputVO714.setAMT_SELL_4(amtSellPval.add(cartSell.get("P4") == null ? BigDecimal.ZERO : cartSell.get("P4")));
					break;
			}
		}
		//查詢客戶風險檢核值資料
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		
		//若檢核不通過，產生錯誤訊息
		String errorMsg = "";
		if(!StringUtils.equals("Y", riskValData.getVALIDATE_YN())) {
			if((cartBuy.isEmpty() && cartSell.isEmpty()) || 
					(cartBuy.isEmpty() && cartSell.size() == 1 && cartSell.containsKey(prodRiskLv)) ||
					(cartBuy.size() == 1 && cartBuy.containsKey(prodRiskLv) && cartSell.size() == 1 && cartSell.containsKey(prodRiskLv))) {
				//若購物車中沒有資料，或贖回再申購所有資料的風險值都相同
				BigDecimal leftAmt = BigDecimal.ZERO;
  				switch(prodRiskLv) {
  					case "P1":
  						break;
  					case "P2":
  						leftAmt = riskValData.getAMT_LEFT_2();
  						break;
  					case "P3":
  						leftAmt = riskValData.getAMT_LEFT_3();
  						break;
  					case "P4":
  						leftAmt = riskValData.getAMT_LEFT_4();
  						break;
  				}
  				BigDecimal leftAmtCurr = leftAmt.divide(rate, 2, RoundingMode.HALF_UP); //原幣(信託幣別)金額
  				DecimalFormat df = new DecimalFormat("#,###.00");
  				errorMsg = "高資產客戶越級金額需低於" + trustCurr + df.format(leftAmtCurr);
			} else {
				errorMsg = "客戶投資組合風險權值已超標，可至「投組適配試算功能」重新試算";
			}
		}
		
		return errorMsg;
	}
	
	/***
	 * 取得購物車中各商品P值再申購金額折台合計
	 * @param tradeSeq
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private Map<String, BigDecimal> getAmtBuyByPVal(String tradeSeq) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		//取得購物車中各商品P值再申購金額合計
		sb.append("SELECT A.PCH_PROD_RISK_LV AS PROD_RISK_LV, SUM(A.PRESENT_VAL * NVL(B.BUY_RATE, 1)) AS AMT_BUY ");
		sb.append(" FROM TBSOT_NF_REDEEM_D A ");
		sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.RDM_TRUST_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" WHERE A.TRADE_SEQ = :tradeSeq ");
		sb.append(" GROUP BY A.PCH_PROD_RISK_LV ");
		queryCondition.setObject("tradeSeq", tradeSeq);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		//購物車中各商品P值申購金額合計
		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		for(Map<String, Object> cart : list) {
			map.put((String)cart.get("PROD_RISK_LV"), (BigDecimal)cart.get("AMT_BUY"));
		}
				
		return map;
	}
	
	/***
	 * 取得購物車中各商品P值贖回金額折台合計
	 * @param tradeSeq
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private Map<String, BigDecimal> getAmtSellByPVal(String tradeSeq) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		//取得購物車中各商品P值贖回金額合計
		sb.append("SELECT A.RDM_PROD_RISK_LV AS PROD_RISK_LV, SUM(A.PRESENT_VAL * NVL(B.BUY_RATE, 1)) AS AMT_SELL ");
		sb.append(" FROM TBSOT_NF_REDEEM_D A ");
		sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.RDM_TRUST_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" WHERE A.TRADE_SEQ = :tradeSeq ");
		sb.append(" GROUP BY A.RDM_PROD_RISK_LV ");
		queryCondition.setObject("tradeSeq", tradeSeq);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		//購物車中各商品P值贖回金額合計
		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		for(Map<String, Object> cart : list) {
			map.put((String)cart.get("PROD_RISK_LV"), (BigDecimal)cart.get("AMT_SELL"));
		}
				
		return map;
	}
}