package com.systex.jbranch.app.server.fps.sot520;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSOT_SN_TRADE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_SN_TRADE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.sot701.AcctVO;
import com.systex.jbranch.app.server.fps.sot701.CustAcctDataVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.CustNoteDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701OutputVO;
import com.systex.jbranch.app.server.fps.sot701.SotUtils;
import com.systex.jbranch.app.server.fps.sot707.SOT707;
import com.systex.jbranch.app.server.fps.sot707.SOT707InputVO;
import com.systex.jbranch.app.server.fps.sot707.SOT707OutputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * sot210
 * 
 * @author Kent
 * @date 2016/09/15
 * @spec SN贖回
 */
@Component("sot520")
@Scope("request")
public class SOT520 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;

	//取得客戶資料
	public void getSOTCustInfo(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		try {
			SOT520InputVO inputVO = (SOT520InputVO) body;
			SOT520OutputVO outputVO = new SOT520OutputVO();
			SOT701InputVO input_701 = new SOT701InputVO();
			SOT701OutputVO output_701 = new SOT701OutputVO();

			input_701.setCustID(inputVO.getCustID());
			input_701.setProdType(inputVO.getProdType());
			input_701.setTradeType(inputVO.getTradeType());

			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			output_701 = sot701.getSOTCustInfo(input_701);
			FP032675DataVO Fp032675 = output_701.getFp032675DataVO();
			CustKYCDataVO kyc = output_701.getCustKYCDataVO();
			CustAcctDataVO acc = output_701.getCustAcctDataVO();
			CustNoteDataVO note = output_701.getCustNoteDataVO();
			if (acc != null) {
				List<Map<String, Object>> debitList = new ArrayList();
				for (AcctVO vo : acc.getDebitAcctList()) {
					Map map = new HashMap();
					map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
					map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
					map.put("DEBIT_ACCT", vo.getAvbBalance());
					debitList.add(map);
				}
				List<Map<String, Object>> trustList = new ArrayList();
				for (AcctVO vo : acc.getTrustAcctList()) {
					Map map = new HashMap();
					map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
					map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
					trustList.add(map);
				}
				List<Map<String, Object>> creditList = new ArrayList();
				for (AcctVO vo : acc.getCreditAcctList()) {
					Map map = new HashMap();
					map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
					map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
					map.put("CURRENCY", vo.getCurrency());
					creditList.add(map);
				}
				outputVO.setDebitAcct(debitList);
				outputVO.setTrustAcct(trustList);
				outputVO.setCreditAcct(creditList);
			}
			if(Fp032675 != null){
				outputVO.setCustID(Fp032675.getCustID());	//驗證用
				outputVO.setNoSale(Fp032675.getNoSale());	//驗證用
				outputVO.setDeathFlag(Fp032675.getDeathFlag());//驗證用
				outputVO.setRejectProdFlag(Fp032675.getRejectProdFlag());//驗證用
				outputVO.setCustName(Fp032675.getCustName());
				outputVO.setCustRemarks(Fp032675.getCustRemarks());
				outputVO.setOutFlag(Fp032675.getObuFlag());
				outputVO.setProfInvestorYN(Fp032675.getCustProFlag());
				outputVO.setPiRemark(Fp032675.getCustProRemark());//這個欄位會直接回存電文DESC欄位資料
				outputVO.setPiDueDate(Fp032675.getCustProDate());
			}
			if (kyc != null) {
				outputVO.setKycLevel(kyc.getKycLevel());
				outputVO.setKycDueDate(kyc.getKycDueDate());
			}
			if (note != null) {
				outputVO.setIsInterdict(note.getInterdict());//驗證用
			}
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			throw new JBranchException(e);
		}
	}

	//非常規交易
	public void getSOTCustInfoCT(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT520InputVO inputVO = (SOT520InputVO) body;
		SOT520OutputVO outputVO = new SOT520OutputVO();

		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO sot712InputVO = new SOT712InputVO();
		sot712InputVO.setCustID(inputVO.getCustID());
		outputVO.setCustAge(sot712.getCUST_AGE(sot712InputVO)); // 查客戶年齡 (未成年警語)

		this.sendRtnObject(outputVO);
	}

	// 取得商品資訊
	public void getProdDTL(Object body, IPrimitiveMap header) throws JBranchException {
		SOT520InputVO inputVO = (SOT520InputVO) body;
		SOT520OutputVO outputVO = new SOT520OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
			// TODO: 1.適配邏輯SOT704→9/20小V
			boolean SOT704Status = true;
			if (SOT704Status) { // 適配條件符合
				StringBuffer sb = new StringBuffer();
				sb.append("select BOND_VALUE,SNINFO.UNIT_AMT_OF_BUYBACK,SNINFO.BASE_AMT_OF_BUYBACK,SN.PRD_ID,SN.SN_CNAME,SN.RISKCATE_ID,SN.CURRENCY_STD_ID,SNINFO.BASE_AMT_OF_PURCHASE,SNINFO.UNIT_AMT_OF_PURCHASE,SNINFO.DATE_OF_FLOTATION   ");
				sb.append("from TBPRD_SN SN ");
				sb.append("left join TBPRD_SNINFO SNINFO ON SNINFO.PRD_ID = SN.PRD_ID ");
				sb.append("where SN.PRD_ID=:PRD_ID ");
				queryCondition.setObject("PRD_ID", inputVO.getProdID());
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> prodDTL = dam.exeQuery(queryCondition);
				outputVO.setProdDTL(prodDTL);
			}
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		SOT520InputVO inputVO = (SOT520InputVO) body;
		SOT520OutputVO outputVO = new SOT520OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuilder sb = null;
		try {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append("SELECT * ");
			sb.append("FROM TBSOT_SN_TRADE_D ");
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());

			outputVO.setCarList(dam.exeQuery(queryCondition));

			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append("SELECT * ");
			sb.append("FROM TBSOT_TRADE_MAIN ");
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());

			outputVO.setMainList(dam.exeQuery(queryCondition));
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	//傳送OP交易並列印表單
	public void verifyTradeBond(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM

		SOT520InputVO inputVO = (SOT520InputVO) body;
		SOT520OutputVO outputVO = new SOT520OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
		try {
			// 1.寫入主檔/明細
			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

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
				mainVO.setPROD_TYPE("5"); //5：SN
				mainVO.setTRADE_TYPE("2"); //2:贖回
				mainVO.setCUST_ID(inputVO.getCustID());
				mainVO.setCUST_NAME(inputVO.getCustName());
				mainVO.setKYC_LV(inputVO.getKycLV());
				mainVO.setKYC_DUE_DATE(null != inputVO.getKycDueDate() ? new Timestamp(inputVO.getKycDueDate().getTime()) : null);
				mainVO.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
				mainVO.setPI_REMARK(inputVO.getPiRemark());//專業投資人註記
				mainVO.setPI_DUE_DATE(null != inputVO.getPiDueDate() ? new Timestamp(inputVO.getPiDueDate().getTime()) : null);
				mainVO.setCUST_REMARKS(inputVO.getCustRemarks());
				mainVO.setIS_OBU(inputVO.getIsOBU());
				mainVO.setIS_AGREE_PROD_ADV(inputVO.getIsAgreeProdAdv());
				mainVO.setBARGAIN_DUE_DATE(null != inputVO.getBargainDueDate() ? new Timestamp(inputVO.getBargainDueDate().getTime()) : null);
				mainVO.setTRADE_STATUS("3");
				mainVO.setIS_BARGAIN_NEEDED("N"); //TODO 待提供
				mainVO.setBARGAIN_FEE_FLAG(null); //TODO 待提供
				mainVO.setIS_REC_NEEDED("N"); //TODO 待提供
				mainVO.setREC_SEQ(null);
				mainVO.setSEND_DATE(null);

				/*
				 * WMS-CR-20191009-01_金錢信託套表需求申請單
				 * 
				 * 2019-12-20 add by ocean
				 */
				mainVO.setTRUST_TRADE_TYPE(inputVO.getTrustTS());

				if(StringUtils.isBlank(inputVO.getGUARDIANSHIP_FLAG())){
					mainVO.setGUARDIANSHIP_FLAG(" ");
				}else{
					mainVO.setGUARDIANSHIP_FLAG(inputVO.getGUARDIANSHIP_FLAG());
				}

				boolean isEnabledSimulatedDate = cbsservice.isEnabledSimulatedDate();
				if (isEnabledSimulatedDate) {
					// for cbs 測試報表印出日期為模擬日，所以該次先設為 autocommit 才能夠更新 LASTUPDATE。
					dam.setAutoCommit(true); // test
				}
				dam.create(mainVO);

				if (isEnabledSimulatedDate) {
					dam.setAutoCommit(false); // test
					// for CBS 測試用
					cbsservice.setTBSOT_TRADE_MAIN_LASTUPDATE_FOR_TEST(inputVO.getTradeSEQ());
				}
			}

			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			TBSOT_SN_TRADE_DPK dtlPK = new TBSOT_SN_TRADE_DPK();
			dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
			dtlPK.setSEQ_NO(new BigDecimal(sot712.getnewTrade_SEQNO()));
			TBSOT_SN_TRADE_DVO dtlVO = new TBSOT_SN_TRADE_DVO();
			dtlVO.setcomp_id(dtlPK);
			dtlVO.setTRADE_SUB_TYPE("2");
			dtlVO.setTRUST_UNIT(inputVO.getTrustUnit());//交易類型
			dtlVO.setTRUST_CURR_TYPE(inputVO.getTrustCurrType()); //信託幣別類型
			dtlVO.setTRUST_CURR(inputVO.getTrustCurr()); //信託幣別
			dtlVO.setMARKET_TYPE(inputVO.getMarketType()); //債券市場種類
			dtlVO.setCERTIFICATE_ID(inputVO.getCertificateID()); //憑證編號
			dtlVO.setPROD_ID(inputVO.getProdID()); //商品代號
			dtlVO.setPROD_NAME(inputVO.getProdName()); //商品名稱
			dtlVO.setPROD_CURR(inputVO.getProdCurr()); //計價幣別
			dtlVO.setPROD_RISK_LV(inputVO.getProdRiskLV()); //產品風險等級
			dtlVO.setPROD_MIN_BUY_AMT(inputVO.getProdMinBuyAmt()); //最低申購面額
			dtlVO.setPROD_MIN_GRD_AMT(inputVO.getProdMinGrdAmt()); //累計申購面額
			dtlVO.setPURCHASE_AMT(inputVO.getPurchaseAmt()); //申購金額/庫存面額
			dtlVO.setTRUST_AMT(inputVO.getTrustAmt()); //信託本金
			dtlVO.setREF_VAL(inputVO.getRefVal()); //參考報價
			dtlVO.setREF_VAL_DATE(null != inputVO.getRefValDate() ? new Timestamp(inputVO.getRefValDate().getTime()) : null); //參考報價日期
			dtlVO.setENTRUST_TYPE(inputVO.getEntrustType()); //委託價格類型/贖回方式
			dtlVO.setENTRUST_AMT(inputVO.getEntrustAmt()); //委託價格/贖回價格
			dtlVO.setTOT_AMT(inputVO.getTotAmt()); //總扣款金額/預估贖回入帳金額
			dtlVO.setDEBIT_ACCT(inputVO.getDebitAcct()); //扣款帳號
			dtlVO.setTRUST_ACCT(inputVO.getTrustAcct()); //信託帳號
			dtlVO.setCREDIT_ACCT(inputVO.getCreditAcct()); //收益入帳帳號/贖回款入帳帳號
			dtlVO.setTRADE_DATE(new Timestamp(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()).getTime())); //交易日期
			dtlVO.setNARRATOR_ID(inputVO.getNarratorID()); //解說專員員編
			dtlVO.setNARRATOR_NAME(inputVO.getNarratorName()); //解說專員姓名
			dtlVO.setREDEEM_TYPE(inputVO.getRedeemType());
			dtlVO.setREDEEM_AMT(inputVO.getRedeemAmt());
			dtlVO.setMGM_FEE(inputVO.getFee()); //手續費金額/預估信託管理費
			dtlVO.setMGM_FEE_RATE(inputVO.getFeeRate()); //手續費率/信託管理費率
			dtlVO.setBATCH_SEQ(null);
			dtlVO.setBOND_VALUE(inputVO.getBondVal());

			dtlVO.setCONTRACT_ID(inputVO.getContractID());
			dtlVO.setTRUST_PEOP_NUM(StringUtils.isNotEmpty(inputVO.getTrustPeopNum()) ? inputVO.getTrustPeopNum() : "N");

			dam.create(dtlVO);

			// 更新批號
			SOT712InputVO inputVO_712 = new SOT712InputVO();

			inputVO_712.setProdType("SN");
			inputVO_712.setTradeSeq(inputVO.getTradeSEQ());
			sot712.updateBatchSeq(inputVO_712);

			// TODO 上[2080]拆[2090]
			// 確認電文
//			String isOBU = StringUtils.isBlank(inputVO.getIsOBU()) ? "" : inputVO.getIsOBU();
			SOT707InputVO inputVO_707 = new SOT707InputVO();
			SOT707OutputVO outputVO_707 = new SOT707OutputVO();

			inputVO_707.setProdType("1");
			inputVO_707.setCheckType("2");
			inputVO_707.setTradeSeq(inputVO.getTradeSEQ());

			SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
			outputVO_707 = sot707.verifyESBRedeemBN(inputVO_707);
			// TODO 上[2080]拆[2090]
//			if (isOBU.equals("Y")) {
//				outputVO_707 = sot707.verifyESBRedeemBN_OBU(inputVO_707);
//			} else {
//				outputVO_707 = sot707.verifyESBRedeemBN(inputVO_707);				
//			}

			String errorMsg = outputVO_707.getErrorMsg();
			outputVO.setWarningCode(outputVO_707.getWarningCode());
			if (!"".equals(errorMsg) && null != errorMsg) {
				outputVO.setErrorMsg(errorMsg);
				dam.delete(dtlVO);// 要刪除該筆 ， 不然SOT703會撈到重覆
			} else {
				TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();
				mainVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

				mainVO.setTRADE_STATUS("3"); //3:傳送OP交易並列印表單

				dam.update(mainVO);

				// for CBS 測試用
				cbsservice.setTBSOT_TRADE_MAIN_LASTUPDATE_FOR_TEST(mainVO.getTRADE_SEQ());
			}
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
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