package com.systex.jbranch.app.server.fps.sot220;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSOT_ETF_TRADE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_ETF_TRADE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421;
import com.systex.jbranch.app.server.fps.crm421.CRM421InputVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421OutputVO;
import com.systex.jbranch.app.server.fps.sot701.AcctVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701OutputVO;
import com.systex.jbranch.app.server.fps.sot701.SotUtils;
import com.systex.jbranch.app.server.fps.sot705.SOT705;
import com.systex.jbranch.app.server.fps.sot705.SOT705InputVO;
import com.systex.jbranch.app.server.fps.sot705.SOT705OutputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
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

/**
 * sot220
 * 
 * @author ocean
 * @date 2016/09/29
 * @spec ETF股票賣出
 */
@Component("sot220")
@Scope("request")
public class SOT220 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;

	public void getSOTCustInfo(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT220InputVO inputVO = (SOT220InputVO) body;
		SOT220OutputVO outputVO = new SOT220OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		SOT701OutputVO outputVO_701 = new SOT701OutputVO();

		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		outputVO_701 = sot701.getSOTCustInfo(inputVO_701);

		outputVO.setCustName(outputVO_701.getFp032675DataVO().getCustName());
		outputVO.setKycLV(outputVO_701.getCustKYCDataVO().getKycLevel());
		outputVO.setKycDueDate(outputVO_701.getCustKYCDataVO().getKycDueDate());
		outputVO.setProfInvestorYN(outputVO_701.getFp032675DataVO().getCustProFlag());
		outputVO.setPiDueDate(outputVO_701.getFp032675DataVO().getCustProDate());
		outputVO.setCustRemarks(outputVO_701.getFp032675DataVO().getCustRemarks());
		outputVO.setPiRemark(outputVO_701.getFp032675DataVO().getCustProRemark());
		outputVO.setIsOBU(outputVO_701.getFp032675DataVO().getObuFlag());
		outputVO.setIsAgreeProdAdv(("Y".equals(outputVO_701.getFp032675DataVO().getCustProFlag()) ? "Y" : (StringUtil.isBlank(outputVO_701.getFp032675DataVO().getCustTxFlag()) ? "" : ("Y".equals(outputVO_701.getFp032675DataVO().getCustTxFlag().substring(0, 1)) ? "Y" : "N"))));
		outputVO.setBargainDueDate(outputVO_701.getDueDate());
		outputVO.setPlNotifyWays(outputVO_701.getCustPLDataVO().getPlMsg());
		outputVO.setTakeProfitPerc(outputVO_701.getCustPLDataVO().getTakeProfitPerc());
		outputVO.setStopLossPerc(outputVO_701.getCustPLDataVO().getStopLossPerc());
		outputVO.setFatcaType(outputVO_701.getFatcaDataVO().getFatcaType());
		outputVO.setCustProType(outputVO_701.getFp032675DataVO().getCustProType());
		outputVO.setDeathFlag(outputVO_701.getFp032675DataVO().getDeathFlag());
		List<AcctVO> debitAcct = outputVO_701.getCustAcctDataVO().getDebitAcctList();
		List<Map<String, Object>> debitAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : debitAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("CURRENCY", vo.getCurrency());

			debitAcctList.add(map);
		}
		outputVO.setDebitAcct(debitAcctList);

		List<AcctVO> trustAcct = outputVO_701.getCustAcctDataVO().getTrustAcctList();
		List<Map<String, Object>> trustAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : trustAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));

			trustAcctList.add(map);
		}
		outputVO.setTrustAcct(trustAcctList);

		List<AcctVO> creditAcct = outputVO_701.getCustAcctDataVO().getCreditAcctList();
		List<Map<String, Object>> creditAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : creditAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("CURRENCY", vo.getCurrency());

			creditAcctList.add(map);
		}
		outputVO.setCreditAcct(creditAcctList);
		
		List<AcctVO> includingDigitdebitAcct = outputVO_701.getCustAcctDataVO().getIncludingDigitDebitAcctList();
		List<Map<String, Object>> includingDigitdebitAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : includingDigitdebitAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("CURRENCY", vo.getCurrency());

			includingDigitdebitAcctList.add(map);
		}
		outputVO.setIncludingDigitdebitAcct(includingDigitdebitAcctList);

		this.sendRtnObject(outputVO);
	}

	public void getSOTCustInfoCT(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT220InputVO inputVO = (SOT220InputVO) body;
		SOT220OutputVO outputVO = new SOT220OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		Boolean isFirstTrade = false;

		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());
		inputVO_701.setTrustTS(inputVO.getTrustTS());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		fp032675DataVO = sot701.getFP032675Data(inputVO_701);
		isFirstTrade = sot701.getIsCustFirstTrade(inputVO_701);

		outputVO.setIsFirstTrade((isFirstTrade) ? "Y" : "N");            //是否首購
		outputVO.setCustRemarks(fp032675DataVO.getCustRemarks()); 		 //弱勢
		outputVO.setAgeUnder70Flag(fp032675DataVO.getAgeUnder70Flag());  //年齡小於70
		outputVO.setEduJrFlag(fp032675DataVO.getEduJrFlag());            //教育程度國中以上(不含國中)
		outputVO.setHealthFlag(fp032675DataVO.getHealthFlag());          //未領有全民健保重大傷病證明


		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO sot712InputVO = new SOT712InputVO();
		sot712InputVO.setCustID(inputVO.getCustID());
		outputVO.setCustAge(sot712.getCUST_AGE(sot712InputVO)); //查客戶年齡 (未成年警語)

		this.sendRtnObject(outputVO);
	}

	// 加入購物車
	public void save(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM

		SimpleDateFormat SDFYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SOT220InputVO inputVO = (SOT220InputVO) body;
		SOT220OutputVO outputVO = new SOT220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		//若為申請議價，需先呼叫CRM421存檔並檢核
		if (StringUtils.equals("A", inputVO.getFeeRateType())) {
			CRM421OutputVO crm421OutputVO = applyBargain(inputVO);
			if (StringUtils.isNotBlank(crm421OutputVO.getErrorMsg())) {
				throw new APException("議價申請檢核錯誤：" + crm421OutputVO.getErrorMsg()); // 顯示資料不存在
			} else {
				inputVO.setBargainApplySEQ(crm421OutputVO.getApplySeqList().get(0).toString());
			}
		}

		// 1.寫入主檔/明細
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		String branch = (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH);

		if (null == vo) {
			TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();

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
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sb = new StringBuffer();
				
				sb.append("SELECT BRANCH_NBR ");
				sb.append("FROM TBORG_UHRM_BRH ");
				sb.append("WHERE EMP_ID = :loginID ");

				queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> loginBreach = dam.exeQuery(queryCondition);
				
				if (loginBreach.size() > 0) {
					mainVO.setBRANCH_NBR((String) loginBreach.get(0).get("BRANCH_NBR"));
				} else {
					throw new APException("人員無有效分行"); //顯示錯誤訊息
				}
			} else {
				SotUtils utils = (SotUtils) PlatformContext.getBean("sotUtils");
				mainVO.setBRANCH_NBR(utils.getBranchNbr(inputVO.getTrustTS(), String.valueOf(getCommonVariable(SystemVariableConsts.LOGINBRH))));
			}

			mainVO.setTRADE_SEQ(inputVO.getTradeSEQ());
			mainVO.setPROD_TYPE("2"); //2:海外ETF/股票
			mainVO.setTRADE_TYPE("2"); //2:贖回/贖回再申購
			mainVO.setCUST_ID(inputVO.getCustID());
			mainVO.setCUST_NAME(inputVO.getCustName());
			mainVO.setKYC_LV(inputVO.getKycLV());
			mainVO.setKYC_DUE_DATE(null != inputVO.getKycDueDate() ? new Timestamp(inputVO.getKycDueDate().getTime()) : null);
			mainVO.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
			mainVO.setPI_DUE_DATE(null != inputVO.getPiDueDate() ? new Timestamp(inputVO.getPiDueDate().getTime()) : null);
			mainVO.setCUST_REMARKS(inputVO.getCustRemarks());
			mainVO.setPI_REMARK(inputVO.getPiRemark());
			mainVO.setIS_OBU(inputVO.getIsOBU());
			mainVO.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
			mainVO.setIS_AGREE_PROD_ADV(inputVO.getIsAgreeProdAdv());
			mainVO.setBARGAIN_DUE_DATE(null != inputVO.getBargainDueDate() ? new Timestamp(inputVO.getBargainDueDate().getTime()) : null);
			mainVO.setTRADE_STATUS("1"); //1:暫存
			mainVO.setIS_BARGAIN_NEEDED(StringUtils.equals("A", inputVO.getFeeRateType()) ? "Y" : "N");
			mainVO.setBARGAIN_FEE_FLAG(StringUtils.equals("A", inputVO.getFeeRateType()) ? "1" : null);
			mainVO.setTRUST_TRADE_TYPE(inputVO.getTrustTS()); //S:特金 M:金錢信託
			//0000275: 金錢信託受監護受輔助宣告交易控管調整
			if(StringUtils.isBlank(inputVO.getGUARDIANSHIP_FLAG())){
				mainVO.setGUARDIANSHIP_FLAG(" ");
			}else{
				mainVO.setGUARDIANSHIP_FLAG(inputVO.getGUARDIANSHIP_FLAG());
			}
			
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			SOT712InputVO inputVO_712 = new SOT712InputVO();
			inputVO_712.setCustID(inputVO.getCustID());
			inputVO_712.setProdID(null);
			inputVO_712.setProdType("ETF");
			inputVO_712.setProfInvestorYN(inputVO.getProfInvestorYN());
			inputVO_712.setIsFirstTrade(inputVO.getProfInvestorYN());
			inputVO_712.setCustRemarks(inputVO.getCustRemarks());
			inputVO_712.setCustProRemark(inputVO.getPiRemark());
			mainVO.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N");

			mainVO.setREC_SEQ(null);
			mainVO.setSEND_DATE(null);

			mainVO.setPROF_INVESTOR_TYPE(inputVO.getCustProType());

			dam.create(mainVO);
		} else {
			vo.setTRADE_STATUS("1"); //暫存

			dam.update(vo);
		}

		TBSOT_ETF_TRADE_DPK dPK = new TBSOT_ETF_TRADE_DPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(inputVO.getCarSEQ());
		TBSOT_ETF_TRADE_DVO dVO = new TBSOT_ETF_TRADE_DVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_ETF_TRADE_DVO) dam.findByPKey(TBSOT_ETF_TRADE_DVO.TABLE_UID, dVO.getcomp_id());

		if (null != dVO) {
			dam.delete(dVO);
		}
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		BigDecimal newTrade_SEQNO = new BigDecimal(sot712.getnewTrade_SEQNO());
		TBSOT_ETF_TRADE_DPK dtlPK = new TBSOT_ETF_TRADE_DPK();
		dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dtlPK.setSEQ_NO(newTrade_SEQNO);

		TBSOT_ETF_TRADE_DVO dtlVO = new TBSOT_ETF_TRADE_DVO();
		dtlVO.setcomp_id(dtlPK);
		dtlVO.setBATCH_SEQ(null);
		dtlVO.setBATCH_NO(null);
		dtlVO.setTRADE_SUB_TYPE("S"); //S:贖回
		dtlVO.setCERTIFICATE_ID(null);
		dtlVO.setPROD_ID(inputVO.getProdID());
		dtlVO.setPROD_NAME(inputVO.getProdName());
		dtlVO.setPROD_CURR(inputVO.getProdCurr());
		dtlVO.setPROD_RISK_LV(inputVO.getProdRiskLV());
		dtlVO.setTRUST_CURR_TYPE(inputVO.getTrustCurrType());
		dtlVO.setREF_PRICE(inputVO.getRefPrice());
		dtlVO.setSELL_TYPE(inputVO.getSellType()); //1：整股 2:畸零股
		dtlVO.setPROD_MIN_BUY_AMT(inputVO.getProdMinBuyAmt());
		dtlVO.setPROD_MIN_BUY_UNIT(inputVO.getProdMinBuyUnit());
		dtlVO.setPROD_MIN_GRD_UNIT(inputVO.getProdMinGrdUnit());
		dtlVO.setTRUST_NUM(inputVO.getNumber());
		dtlVO.setUNIT_NUM(StringUtils.equals("2", inputVO.getSellType()) ? inputVO.getUnitNum1() : inputVO.getUnitNum());
		dtlVO.setENTRUST_TYPE(inputVO.getEntrustType());

		if ("2".equals(inputVO.getEntrustType())) {
			dtlVO.setENTRUST_AMT(inputVO.getEntrustAmt());
			dtlVO.setENTRUST_DISCOUNT(null);
		} else if ("3".equals(inputVO.getEntrustType())) {
			dtlVO.setENTRUST_AMT(inputVO.getEntrustAmt1());
			dtlVO.setENTRUST_DISCOUNT(null);
		} else if ("4".equals(inputVO.getEntrustType())) {
			dtlVO.setENTRUST_AMT(inputVO.getEntrustAmt2().setScale(2, BigDecimal.ROUND_HALF_UP));
			dtlVO.setENTRUST_DISCOUNT(inputVO.getEntrustDiscount());
		} else {
			dtlVO.setENTRUST_AMT(new BigDecimal("0"));
			dtlVO.setENTRUST_DISCOUNT(null);
		}

		dtlVO.setCLOSING_PRICE(inputVO.getClosingPrice());
		dtlVO.setCLOSING_PRICE_DATE(null != inputVO.getClosingPriceDate() ? new Timestamp(inputVO.getClosingPriceDate().getTime()) : null);
		dtlVO.setDUE_DATE_SHOW(inputVO.getDueDateShow());
		dtlVO.setDUE_DATE(null != inputVO.getDueDate() ? new Timestamp(SDFYYYYMMDD.parse(inputVO.getDueDate()).getTime()) : null);
		dtlVO.setDEFAULT_FEE_RATE(inputVO.getDefaultFeeRate());
		dtlVO.setFEE_TYPE(StringUtils.isNotBlank(inputVO.getFeeRateType()) ? inputVO.getFeeRateType() : "C");
		dtlVO.setBARGAIN_STATUS(StringUtils.isNotBlank(inputVO.getFeeRateType()) && "A".equals(inputVO.getFeeRateType()) ? "1" : null); //手續費方式為申請議價時，才放這個欄位資料
		dtlVO.setBARGAIN_APPLY_SEQ(inputVO.getBargainApplySEQ()); //手續費方式為事先單次議價，才會有議價編號欄位
		dtlVO.setFEE_RATE(inputVO.getFeeRate());
		dtlVO.setFEE(inputVO.getFee());
		dtlVO.setFEE_DISCOUNT(inputVO.getFeeDiscount());
		dtlVO.setOTHER_FEE(null);
		dtlVO.setTOT_AMT(null);
		dtlVO.setDEBIT_ACCT(inputVO.getDebitAcct());
		dtlVO.setTRUST_ACCT(inputVO.getTrustAcct());
		dtlVO.setCREDIT_ACCT(inputVO.getCreditAcct());
		dtlVO.setTRADE_DATE(new Timestamp(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()).getTime()));
		dtlVO.setSTOP_LOSS_PERC(inputVO.getStopLossPerc());
		dtlVO.setTAKE_PROFIT_PERC(inputVO.getTakeProfitPerc());
		dtlVO.setPL_NOTIFY_WAYS(inputVO.getPlNotifyWays());
		dtlVO.setIS_NEW_PROD(inputVO.getIsNewProd());
		dtlVO.setTRADE_MARKET(inputVO.getCountryCode());
		//解說專員暫時先放登入者員編與姓名
		//		dtlVO.setNARRATOR_ID((String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
		//		dtlVO.setNARRATOR_NAME((String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINNAME));
		dtlVO.setNARRATOR_ID(inputVO.getNarratorID());
		dtlVO.setNARRATOR_NAME(inputVO.getNarratorName());
		dtlVO.setCONTRACT_ID(inputVO.getContractID());
		dtlVO.setTRUST_PEOP_NUM(inputVO.getTrustPeopNum());
		
		dam.create(dtlVO);

		// 2.檢核電文
		SOT705InputVO inputVO_705 = new SOT705InputVO();
		SOT705OutputVO outputVO_705 = new SOT705OutputVO();

		inputVO_705.setCheckType("1");
		inputVO_705.setTradeSeq(inputVO.getTradeSEQ());
		inputVO_705.setSeqNo(newTrade_SEQNO.toString());
		inputVO_705.setTrustTS(inputVO.getTrustTS());

		SOT705 sot705 = (SOT705) PlatformContext.getBean("sot705");
		outputVO_705 = sot705.verifyESBRedeemETF(inputVO_705);

		String errorMsg = outputVO_705.getErrorMsg();
		if (!"".equals(errorMsg) && null != errorMsg) {
			TBSOT_ETF_TRADE_DPK errPK = new TBSOT_ETF_TRADE_DPK();
			errPK.setTRADE_SEQ(inputVO.getTradeSEQ());
			errPK.setSEQ_NO(newTrade_SEQNO);
			TBSOT_ETF_TRADE_DVO errVO = new TBSOT_ETF_TRADE_DVO();
			errVO.setcomp_id(errPK);
			errVO = (TBSOT_ETF_TRADE_DVO) dam.findByPKey(TBSOT_ETF_TRADE_DVO.TABLE_UID, errVO.getcomp_id());

			//刪除明細
			if (null != errVO) {
				dam.delete(errVO);
			}

			//===若無明細，則將主檔也刪除
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT 1 ");
			sb.append("FROM TBSOT_ETF_TRADE_D ");
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (list.size() == 0) {
				TBSOT_TRADE_MAINVO mVO = new TBSOT_TRADE_MAINVO();
				mVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

				dam.delete(mVO);
			}
			//===

			outputVO.setErrorMsg(errorMsg);
		}

		this.sendRtnObject(outputVO);
	}

	/**
	 * 相當於議價的加入清單; 存檔並送單次議價檢核電文
	 * 
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private CRM421OutputVO applyBargain(SOT220InputVO inputVO) throws Exception {
		CRM421OutputVO crm421OutputVO = new CRM421OutputVO();
		CRM421InputVO crm421InputVO = new CRM421InputVO();

		//initial data
		crm421InputVO.setApply_type("4"); //ETF
		crm421InputVO.setProd_type("4"); //ETF
		crm421InputVO.setCust_id(inputVO.getCustID());

		BigDecimal entrustAmt = BigDecimal.ZERO;
		if ("2".equals(inputVO.getEntrustType())) {
			entrustAmt = inputVO.getEntrustAmt();
		} else if ("3".equals(inputVO.getEntrustType())) {
			entrustAmt = inputVO.getEntrustAmt1();
		} else if ("4".equals(inputVO.getEntrustType())) {
			entrustAmt = inputVO.getEntrustAmt2();
		} else {
			entrustAmt = inputVO.getEntrustAmt();
		}

		BigDecimal unitNumber = StringUtils.equals("2", inputVO.getSellType()) ? inputVO.getUnitNum1() : inputVO.getUnitNum();
		crm421InputVO.setEntrust_amt(entrustAmt.toString());
		crm421InputVO.setEntrust_unit(unitNumber.toString());
		crm421InputVO.setFee(inputVO.getFee().toString());
		crm421InputVO.setFee_discount(inputVO.getFeeDiscount());
		crm421InputVO.setFee_rate(inputVO.getFeeRate());
		crm421InputVO.setProd_id(inputVO.getProdID());
		crm421InputVO.setProd_name(inputVO.getProdName());
		crm421InputVO.setPurchase_amt((entrustAmt.multiply(unitNumber)).toString());
		crm421InputVO.setBrg_reason(inputVO.getBrgReason()); //議價原因
		crm421InputVO.setTrust_curr(inputVO.getProdCurr());
		crm421InputVO.setTrustCurrType(inputVO.getTrustCurrType()); //贖回依庫存信託業務別
		crm421InputVO.setDefaultFeeRate(inputVO.getDefaultFeeRate());
		crm421InputVO.setDiscount_type("1"); //by費率
		crm421InputVO.setCon_degree(getCustConDegree(inputVO.getCustID()));
		crm421InputVO.setHighest_auth_lv(inputVO.getFeeDiscount().toString());

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

	// 取得商品資訊
	public void getProdDTL(Object body, IPrimitiveMap header) throws Exception {

		SOT220InputVO inputVO = (SOT220InputVO) body;
		SOT220OutputVO outputVO = new SOT220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// 查詢該prodID是ETF或STOCK；
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PTYPE, PRD_ID, PNAME ");
		sb.append("  FROM VWPRD_MASTER ");
		sb.append(" WHERE PRD_ID = :prodID ");
		sb.append("   AND PTYPE IN ('ETF', 'STOCK') ");
		queryCondition.setObject("prodID", inputVO.getProdID());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> pTypeList = dam.exeQuery(queryCondition);
		if (pTypeList.size() > 0) { // 區分 ETF 或 STOCK
			String pType = (String) pTypeList.get(0).get("PTYPE");

			// 2.查詢商品主檔
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT PROD.PRD_ID, PROD.").append(pType).append("_CNAME AS PROD_NAME, PROD.CURRENCY_STD_ID, PROD.RISKCATE_ID, ");
			sb.append("PROD.MARKET_PRICE, PROD.TXN_AMOUNT, PROD.TXN_UNIT, PROD.TRADING_UNIT, ");
			sb.append("PROD.STOCK_CODE, NREXGFL.COUNTRY_CODE, ");
			sb.append("CLOS_P.DATE_08 as SOU_DATE, CLOS_P.CUR_AMT ");
			sb.append("FROM TBPRD_").append(pType).append(" PROD ");
			sb.append("LEFT JOIN TBPRD_STOCK_NREXGFL NREXGFL ON PROD.STOCK_CODE = NREXGFL.STOCK_CODE ");
			sb.append("LEFT JOIN ( ");
			sb.append("SELECT PRICE.PRODUCT_NO, MAX_SOU.SOU_DATE, PRICE.CUR_AMT, PRICE.DATE_08 ");
			sb.append("FROM TBPRD_ETF_CLOSING_PRICE PRICE ");
			sb.append("LEFT JOIN (SELECT PRODUCT_NO, MAX(SOU_DATE) AS SOU_DATE FROM TBPRD_ETF_CLOSING_PRICE WHERE PRODUCT_NO = trim(:prodID) GROUP BY PRODUCT_NO) MAX_SOU ON MAX_SOU.PRODUCT_NO = PRICE.PRODUCT_NO ");
			sb.append("WHERE MAX_SOU.SOU_DATE = PRICE.SOU_DATE ) CLOS_P ON CLOS_P.PRODUCT_NO = PROD.PRD_ID ");
			sb.append("WHERE PRD_ID = trim(:prodID) ");
			queryCondition.setObject("prodID", inputVO.getProdID());
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			((Map) list.get(0)).put("PTYPE", pType);

			//3 call sot705.getCustAssetETFData電文抓取庫存股數(NUMBER)
			//					SOT705InputVO inputVO_705 = new SOT705InputVO();
			//					SOT705OutputVO outputVO_705 = new SOT705OutputVO();
			//					inputVO_705.setCustId(inputVO.getCustID());
			//					SOT705 sot705 = (SOT705) PlatformContext.getBean("sot705");
			//					outputVO_705 = sot705.getCustAssetETFData(inputVO_705);
			//					if(pType=="ETF"){
			//						for(CustAssetETFVO vo:outputVO_705.getCustAssetETFList()){
			//							if((vo.getInsuranceNo().trim()).equals(inputVO.getProdID())){
			//								((Map) list.get(0)).put("NUMBER",vo.getNumber());
			//								break;
			//							}
			//						}
			//					}else{
			//						for(CustAssetETFVO vo:outputVO_705.getCustAssetStockList()){
			//							if((vo.getInsuranceNo().trim()).equals(inputVO.getProdID())){
			//								((Map) list.get(0)).put("NUMBER",vo.getNumber());
			//								break;
			//							}
			//						}
			//					}
			outputVO.setProdDTL(list);
			//4

			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT * FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.ENTRUST_TYPE' ");
			sb.append("AND INSTR((SELECT PARAM_NAME FROM TBSYSPARAMETER  ");
			sb.append("WHERE PARAM_TYPE = 'SOT.ENTRUST_TYPE_SMAPPING' AND PARAM_CODE = :stock_code), PARAM_ORDER) > 0 ");
			queryCondition.setObject("stock_code", outputVO.getProdDTL().get(0).get("STOCK_CODE"));
			queryCondition.setQueryString(sb.toString());

			outputVO.setEntrustTypeList(dam.exeQuery(queryCondition));

		}

		this.sendRtnObject(outputVO);
	}

	public void query(Object body, IPrimitiveMap header) throws Exception {

		SOT220InputVO inputVO = (SOT220InputVO) body;
		SOT220OutputVO outputVO = new SOT220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT TRUST_NUM,TRADE_SEQ, SEQ_NO, BATCH_SEQ, BATCH_NO, TRADE_SUB_TYPE, CERTIFICATE_ID, PROD_ID, PROD_NAME, ");
		sb.append(" PROD_CURR, PROD_RISK_LV, TRUST_CURR_TYPE, REF_PRICE, SELL_TYPE, PROD_MIN_BUY_AMT, PROD_MIN_BUY_UNIT, PROD_MIN_GRD_UNIT, ");
		sb.append(" UNIT_NUM, ENTRUST_TYPE, ENTRUST_AMT, CLOSING_PRICE, CLOSING_PRICE_DATE, ENTRUST_DISCOUNT, DUE_DATE_SHOW, DUE_DATE, ");
		sb.append(" DEFAULT_FEE_RATE, FEE_TYPE, BARGAIN_STATUS, BARGAIN_APPLY_SEQ, FEE_RATE, FEE, FEE_DISCOUNT, OTHER_FEE, TOT_AMT, DEBIT_ACCT, ");
		sb.append(" TRUST_ACCT, CREDIT_ACCT, TRADE_DATE, STOP_LOSS_PERC, TAKE_PROFIT_PERC, PL_NOTIFY_WAYS, IS_NEW_PROD, TRADE_MARKET, NARRATOR_ID, NARRATOR_NAME, ");
		sb.append(" CONTRACT_ID, TRUST_PEOP_NUM ");
		sb.append("FROM TBSOT_ETF_TRADE_D ");
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		outputVO.setCarList(dam.exeQuery(queryCondition));

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT TRADE_SEQ, PROD_TYPE, TRADE_TYPE, CUST_ID, CUST_NAME, AGENT_ID, AGENT_NAME, KYC_LV, KYC_DUE_DATE, PROF_INVESTOR_YN, PI_DUE_DATE, CUST_REMARKS, IS_OBU, IS_AGREE_PROD_ADV, BARGAIN_DUE_DATE, TRADE_STATUS, IS_BARGAIN_NEEDED, BARGAIN_FEE_FLAG, IS_REC_NEEDED, REC_SEQ, SEND_DATE, TRUST_TRADE_TYPE ");
		sb.append("FROM TBSOT_TRADE_MAIN ");
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		outputVO.setMainList(dam.exeQuery(queryCondition));

		if (!outputVO.getMainList().isEmpty() && !outputVO.getCarList().isEmpty()) {
			//錄音序號
			SOT712 sot712 = new SOT712();
			SOT712InputVO input712 = new SOT712InputVO();

			input712.setCustID((String) outputVO.getMainList().get(0).get("CUST_ID"));
			input712.setProdID((String) outputVO.getCarList().get(0).get("PROD_ID"));
			input712.setProdType("ETF");
			input712.setProfInvestorYN(outputVO.getMainList().get(0).get("PROF_INVESTOR_YN") != null ? outputVO.getMainList().get(0).get("PROF_INVESTOR_YN").toString() : null);
			input712.setCustRemarks((String) outputVO.getMainList().get(0).get("CUST_REMARKS"));
			outputVO.setRecNeeded(new Boolean(sot712.getIsRecNeeded(input712)));
		}

		this.sendRtnObject(outputVO);
	}

	public void next(Object body, IPrimitiveMap header) throws Exception {

		WorkStation ws = DataManager.getWorkStation(uuid);
		SOT220InputVO inputVO = (SOT220InputVO) body;
		SOT220OutputVO outputVO = new SOT220OutputVO();

		//若有申請議價，則送出覆核
		callCRM421ApplySingle(inputVO.getTradeSEQ());

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

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

	private void callCRM421ApplySingle(String tradeSeq) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT '4' as APPLY_TYPE, PROD_NAME, BARGAIN_APPLY_SEQ as APPLY_SEQ ");
		sb.append("FROM TBSOT_ETF_TRADE_D WHERE TRADE_SEQ = :tradeSEQ and FEE_TYPE = 'A' ");
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

		SOT220InputVO inputVO = (SOT220InputVO) body;
		SOT220OutputVO outputVO = new SOT220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT 1 ");
		sb.append("FROM TBSOT_ETF_TRADE_D ");
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		if (list.size() == 1) {
			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

			dam.delete(vo);
		}
		TBSOT_ETF_TRADE_DPK dPK = new TBSOT_ETF_TRADE_DPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(inputVO.getCarSEQ());
		TBSOT_ETF_TRADE_DVO dVO = new TBSOT_ETF_TRADE_DVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_ETF_TRADE_DVO) dam.findByPKey(TBSOT_ETF_TRADE_DVO.TABLE_UID, dVO.getcomp_id());

		dam.delete(dVO);

		this.sendRtnObject(null);
	}

	//	public void getTradeDueDate (Object body, IPrimitiveMap header) throws JBranchException, Exception {
	//		
	//		SOT220InputVO inputVO = (SOT220InputVO) body;
	//		SOT220OutputVO outputVO = new SOT220OutputVO();
	//		SOT705InputVO inputVO_705 = new SOT705InputVO();
	//		SOT705OutputVO outputVO_705 = new SOT705OutputVO();
	//		
	//		inputVO_705.setCustId(inputVO.getCustID());
	//		inputVO_705.setStockCode(inputVO.getStockCode());
	//		
	//		SOT705 sot705 = (SOT705) PlatformContext.getBean("sot705");
	//		outputVO_705 = sot705.getTradeDueDate(inputVO_705);
	//		
	//		List<Map<String, Object>> dueDateList = new ArrayList<Map<String,Object>>();
	//		for (Integer i = 0; i < outputVO_705.getTradeDueDate().size(); i++) {
	//			Map<String, Object> map = new HashMap<String, Object>();
	//			map.put("LABEL", outputVO_705.getTradeDueDate().get(i));
	//			map.put("DATA", outputVO_705.getTradeDueDate().get(i));
	//			
	//			dueDateList.add(map);
	//		}
	//		
	//		this.sendRtnObject(outputVO);
	//	}

	public void goOP(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT220InputVO inputVO = (SOT220InputVO) body;
		SOT220OutputVO outputVO = new SOT220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		SOT705InputVO inputVO_705 = new SOT705InputVO();
		SOT705OutputVO outputVO_705 = new SOT705OutputVO();

		inputVO_705.setCheckType("2");
		inputVO_705.setTradeSeq(inputVO.getTradeSEQ());
		inputVO_705.setTrustTS(inputVO.getTrustTS());

		SOT705 sot705 = (SOT705) PlatformContext.getBean("sot705");
		outputVO_705 = sot705.confirmESBRedeemETF(inputVO_705);

		String errorMsg = outputVO_705.getErrorMsg();
		if (!"".equals(errorMsg) && null != errorMsg) {
			outputVO.setErrorMsg(errorMsg);
		} else {
			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

			vo.setTRADE_STATUS("3"); //3:傳送OP交易並列印表單
			vo.setIS_REC_NEEDED(inputVO.getIsRecNeeded() == "true" ? "Y" : "N");
			vo.setREC_SEQ(inputVO.getRecSEQ());
			vo.setSEND_DATE(new Timestamp(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()).getTime()));
			dam.update(vo);

			// for CBS 測試用
			cbsservice.setTBSOT_TRADE_MAIN_LASTUPDATE_FOR_TEST(vo.getTRADE_SEQ());
		}

		this.sendRtnObject(outputVO);
	}

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

}