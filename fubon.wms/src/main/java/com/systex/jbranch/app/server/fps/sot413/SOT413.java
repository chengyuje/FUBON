package com.systex.jbranch.app.server.fps.sot413;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSOT_FCI_TRADE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_FCI_TRADE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.prd281.PRD281;
import com.systex.jbranch.app.server.fps.prd281.PRD281OutputVO;
import com.systex.jbranch.app.server.fps.sot701.AcctVO;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701OutputVO;
import com.systex.jbranch.app.server.fps.sot708.SOT708;
import com.systex.jbranch.app.server.fps.sot708.SOT708InputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712OutputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * sot413
 * @spec SI_FCI申購
 */
@Component("sot413")
@Scope("request")
public class SOT413 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;
	
	//撈客戶資料
	public void getSOTCustInfoCT(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		SOT413InputVO inputVO = (SOT413InputVO) body;
		SOT413OutputVO outputVO = new SOT413OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		SOT701OutputVO outputVO_701 = new SOT701OutputVO();
		SOT712InputVO inputVO_712 = new SOT712InputVO();
		SOT712OutputVO outputVO_712 = new SOT712OutputVO();

		inputVO_712.setCustID(inputVO.getCustID());
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		outputVO_712 = sot712.identifyKYCDateAndRecord(inputVO_712);
		outputVO.setKYCResult(outputVO_712.getKYCResult());
		outputVO.setCustAge(sot712.getCUST_AGE(inputVO_712)); //查客戶年齡 (未成年警語)

		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		outputVO_701 = sot701.getSOTCustInfo(inputVO_701);
		if(outputVO_701.getFp032675DataVO() != null){
			outputVO.setCustID(inputVO.getCustID());   //outputVO_701.getFp032675DataVO().getCustID() is EMPTY
			outputVO.setCustName(outputVO_701.getFp032675DataVO().getCustName()); //客戶姓名
			outputVO.setKycLv(outputVO_701.getCustKYCDataVO().getKycLevel()); //KYC等級
			outputVO.setKycDueDate(outputVO_701.getCustKYCDataVO().getKycDueDate()); //KYC日期
			outputVO.setIsAgreeProdAdv(outputVO_701.getFp032675DataVO().getCustTxFlag()); //交易中不需要再去做邏輯判斷是Y或N，可以直接回存DB
			outputVO.setPiRemark(outputVO_701.getFp032675DataVO().getCustProRemark());//這個欄位會直接回存電文DESC欄位資料
			
			outputVO.setCustRemarks(outputVO_701.getFp032675DataVO().getCustRemarks());  //客戶註記
			outputVO.setIsOBU(outputVO_701.getFp032675DataVO().getObuFlag());  //OBU註記
			outputVO.setProfInvestorYN(outputVO_701.getFp032675DataVO().getCustProFlag()); //專業投資人
			outputVO.setPiDueDate(outputVO_701.getFp032675DataVO().getCustProDate());  //專業投資人效期
			outputVO.setNoSale(outputVO_701.getFp032675DataVO().getNoSale());
			outputVO.setRejectProdFlag(outputVO_701.getFp032675DataVO().getRejectProdFlag());
			outputVO.setPro1500(outputVO_701.getFp032675DataVO().getPro1500());
			outputVO.setTrustProCorp(outputVO_701.getFp032675DataVO().getTrustProCorp());
			
			outputVO.setAgeUnder70Flag(outputVO_701.getFp032675DataVO().getAgeUnder70Flag());
			outputVO.setEduJrFlag(outputVO_701.getFp032675DataVO().getEduJrFlag());
			outputVO.setHealthFlag(outputVO_701.getFp032675DataVO().getHealthFlag());
//			outputVO.setCustProFlag(outputVO_701.getFp032675DataVO().getCustProFlag());

			outputVO.setProCorpInv(outputVO_701.getFp032675DataVO().getProCorpInv());
			outputVO.setHighYieldCorp(outputVO_701.getFp032675DataVO().getHighYieldCorp());
			outputVO.setSiProCorp(outputVO_701.getFp032675DataVO().getSiProCorp());
			outputVO.setPro3000(outputVO_701.getFp032675DataVO().getPro3000());
		}
		outputVO.setKycDueDateUseful(outputVO_701.getCustKYCDataVO().isKycDueDateUseful());
		outputVO.setPiDueDateUseful(outputVO_701.getFp032675DataVO().isPiDueDateUseful());
		outputVO.setCustStakeholder(outputVO_701.getCustStakeholder() ? "Y" : "N");

		List<AcctVO> prodAcct = outputVO_701.getCustAcctDataVO().getProdAcctList();
		List<Map<String, Object>> prodAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : prodAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
			//map.put("PROD_ACCT", vo.getAvbBalance()); 
			prodAcctList.add(map);
		}
		outputVO.setProdAcctList(prodAcctList);
		List<AcctVO> debitAcct = outputVO_701.getCustAcctDataVO().getDebitAcctList();
		List<Map<String, Object>> debitAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : debitAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo())+"_"+vo.getCurrency());
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo())+"_"+vo.getCurrency());
			map.put("AVBBALANCE", vo.getAvbBalance());
			map.put("CURRENCY", vo.getCurrency());
			debitAcctList.add(map);
		}
		outputVO.setDebitList(debitAcctList);

		outputVO.setNarratorID((String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
		outputVO.setNarratorName((String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINNAME));
		
		//查詢客戶高資產註記資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hNWCDataVO = sot714.getHNWCData(inputVO.getCustID());
		outputVO.setHnwcYN(StringUtils.isNotBlank(hNWCDataVO.getValidHnwcYN()) ? hNWCDataVO.getValidHnwcYN() : "N");
		outputVO.setHnwcServiceYN(hNWCDataVO.getHnwcService());
				
		this.sendRtnObject(outputVO);
	}	
	
	//取得商品明細
	public void getProdDTL(Object body, IPrimitiveMap header) throws Exception {
		SOT413InputVO inputVO = (SOT413InputVO) body;
		SOT413OutputVO outputVO = new SOT413OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT TRUNC(SYSDATE) AS TRADE_DATE, B.VALUE_DATE, C.MON_DATE_" + inputVO.getMonType() + " AS SPOT_DATE, ");
		sql.append(" B.MON_DATE_" + inputVO.getMonType() + " AS EXPIRE_DATE, ");
		sql.append(" (TRUNC(B.MON_DATE_" + inputVO.getMonType() + ") - TRUNC(B.VALUE_DATE)) AS INT_DATES, ");
		sql.append(" TRUNC(D.MON_RATE_" + inputVO.getMonType() + " * (1 + A.REF_PRICE_Y), 2) AS PRD_PROFEE_RATE, ");
		sql.append(" TRUNC(D.MON_RATE_" + inputVO.getMonType() + " * (1 + A.REF_PRICE_Y) + 0.01, 2) AS LESS_PROFEE_RATE, ");
		sql.append(" E.MON_RATE_" + inputVO.getMonType() + " AS FTP_RATE, A.TRADER_CHARGE ");
		sql.append(" FROM TBPRD_FCI A ");
		sql.append(" LEFT JOIN TBPRD_FCI_EXPIRE_DATE B ON B.CURR_ID = A.CURR_ID ");
		sql.append(" LEFT JOIN TBPRD_FCI_SPOT_DATE C ON C.CURR_ID = A.CURR_ID ");
		sql.append(" LEFT JOIN TBPRD_FCI_DEPOSIT_RATE D ON D.CURR_ID = A.CURR_ID ");
		sql.append(" LEFT JOIN TBPRD_FCI_FTP_RATE E ON E.CURR_ID = A.CURR_ID ");
		sql.append(" WHERE A.CURR_ID = :currId AND A.EFFECTIVE_YN = 'Y' ");
		queryCondition.setObject("currId", inputVO.getProdCurr());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setProdDTL(list);

		this.sendRtnObject(outputVO);
	}

	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		SOT413InputVO inputVO = (SOT413InputVO) body;
		SOT413OutputVO outputVO = new SOT413OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;

		try {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT A.*, (A.PURCHASE_AMT * IQ053.BUY_RATE) as PURCHASE_AMT_TWD ");
			sb.append(" FROM TBSOT_FCI_TRADE_D A ");
			sb.append(" LEFT JOIN (SELECT CUR_COD, BUY_RATE FROM TBPMS_IQ053 WHERE MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053)) IQ053 ");
			sb.append(" 	on IQ053.CUR_COD = (CASE WHEN A.PROD_CURR = 'CNH' THEN 'CNY' ELSE A.PROD_CURR END) " );
			sb.append(" WHERE A.TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());

			outputVO.setCarList(dam.exeQuery(queryCondition));

			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT * FROM TBSOT_TRADE_MAIN WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());

			outputVO.setMainList(dam.exeQuery(queryCondition));

			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	// 暫存
	public void save(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT413InputVO inputVO = (SOT413InputVO) body;
		SOT413OutputVO outputVO = new SOT413OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		add(dam, queryCondition, inputVO);

		this.sendRtnObject(outputVO);
	}

	public void add(DataAccessManager dam, QueryConditionIF queryCondition, SOT413InputVO inputVO) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM

		//是否需要錄音 ， SI不需要首購判斷 
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO inputVO_712 = new SOT712InputVO();
		inputVO_712.setCustID(inputVO.getCustID());
		inputVO_712.setProdID(inputVO.getProdID());
		inputVO_712.setProdType("FCI");
		inputVO_712.setProfInvestorYN(inputVO.getProfInvestorYN());
		inputVO_712.setCustRemarks(inputVO.getCustRemarks());
		inputVO_712.setCustProRemark(inputVO.getPiRemark());
		inputVO_712.setPro1500(inputVO.getPro1500());
		inputVO_712.setTrustProCorp(inputVO.getTrustProCorp());			
		
		if (StringUtils.indexOf(inputVO.getDebitAcct(), "_") > 0) {
			inputVO.setDebitAcct(StringUtils.split(inputVO.getDebitAcct(), "_")[0]);
		}
		
		// 1.寫入主檔/明細
		TBSOT_TRADE_MAINVO mainVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		if (null == mainVO) {
			mainVO = new TBSOT_TRADE_MAINVO();
			mainVO.setTRADE_SEQ(inputVO.getTradeSEQ());
			mainVO.setPROD_TYPE("7"); //7:FCI
			mainVO.setTRADE_TYPE("1"); //1:申購
			mainVO.setCUST_ID(inputVO.getCustID());
			mainVO.setCUST_NAME(inputVO.getCustName());
			mainVO.setKYC_LV(inputVO.getKycLv());
			mainVO.setKYC_DUE_DATE(null != inputVO.getKycDueDate() ? new Timestamp(inputVO.getKycDueDate().getTime()) : null);
			mainVO.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
			mainVO.setPI_DUE_DATE(null != inputVO.getPiDueDate() ? new Timestamp(inputVO.getPiDueDate().getTime()) : null);
			mainVO.setCUST_REMARKS(inputVO.getCustRemarks());
			mainVO.setIS_OBU(inputVO.getIsOBU());
			mainVO.setIS_AGREE_PROD_ADV(inputVO.getIsagreeProdAdv());
			mainVO.setPI_REMARK(inputVO.getPiRemark()); //專業投資人註記
			mainVO.setBARGAIN_DUE_DATE(null != inputVO.getBagainDueDate() ? new Timestamp(inputVO.getBagainDueDate().getTime()) : null);
			mainVO.setTRADE_STATUS(inputVO.getTradeStatus());
			mainVO.setIS_BARGAIN_NEEDED("N"); //無議價
			mainVO.setHNWC_YN(inputVO.getHnwcYN());
			mainVO.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());
			mainVO.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N"); // 是否需要錄音
			mainVO.setREC_SEQ(null);
			mainVO.setSEND_DATE(null);
			
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
				mainVO.setBRANCH_NBR(String.valueOf(getCommonVariable(SystemVariableConsts.LOGINBRH)));
			}
			mainVO.setTRUST_TRADE_TYPE("S");
			mainVO.setHNWC_YN(inputVO.getHnwcYN());
			mainVO.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());
			
			dam.create(mainVO);
		} else {
			mainVO.setCUST_ID(inputVO.getCustID());
			mainVO.setCUST_NAME(inputVO.getCustName());
			mainVO.setKYC_LV(inputVO.getKycLv());
			mainVO.setKYC_DUE_DATE(null != inputVO.getKycDueDate() ? new Timestamp(inputVO.getKycDueDate().getTime()) : null);
			mainVO.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
			mainVO.setPI_DUE_DATE(null != inputVO.getPiDueDate() ? new Timestamp(inputVO.getPiDueDate().getTime()) : null);
			mainVO.setCUST_REMARKS(inputVO.getCustRemarks());
			mainVO.setIS_OBU(inputVO.getIsOBU());
			mainVO.setIS_AGREE_PROD_ADV(inputVO.getIsagreeProdAdv());
			mainVO.setPI_REMARK(inputVO.getPiRemark()); //專業投資人註記
			mainVO.setBARGAIN_DUE_DATE(null != inputVO.getBagainDueDate() ? new Timestamp(inputVO.getBagainDueDate().getTime()) : null);
			mainVO.setTRADE_STATUS(inputVO.getTradeStatus());
			mainVO.setHNWC_YN(inputVO.getHnwcYN());
			mainVO.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());
			mainVO.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N"); // 是否需要錄音
			mainVO.setREC_SEQ(null);
			mainVO.setSEND_DATE(null);
			
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
				mainVO.setBRANCH_NBR(String.valueOf(getCommonVariable(SystemVariableConsts.LOGINBRH)));
			}
			
			mainVO.setHNWC_YN(inputVO.getHnwcYN());
			mainVO.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());
			
			dam.update(mainVO);
		}
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM TBSOT_FCI_TRADE_D WHERE TRADE_SEQ = :tradeSEQ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);

		sot712 = (SOT712) PlatformContext.getBean("sot712");
		BigDecimal newTrade_SEQNO = new BigDecimal(sot712.getnewTrade_SEQNO());
		TBSOT_FCI_TRADE_DPK dtlPK = new TBSOT_FCI_TRADE_DPK();
		dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dtlPK.setSEQ_NO(newTrade_SEQNO);

		TBSOT_FCI_TRADE_DVO dtlVO = new TBSOT_FCI_TRADE_DVO();
		dtlVO.setcomp_id(dtlPK);
		dtlVO.setBATCH_SEQ(null);//下單批號
		dtlVO.setTRADE_SUB_TYPE("1");//交易類型 1：申購 2：贖回																					
		dtlVO.setPROD_ID(inputVO.getProdID()); //商品代號															
		dtlVO.setPROD_NAME(inputVO.getProdName());//商品名稱																		
		dtlVO.setPROD_CURR(inputVO.getProdCurr());//計價幣別								
		dtlVO.setPROD_RISK_LV(inputVO.getProdRiskLv());//產品風險等級
		dtlVO.setPROD_MIN_BUY_AMT(inputVO.getProdMinBuyAmt());//最低申購面額
		dtlVO.setPROD_MIN_GRD_AMT(inputVO.getProdMinGrdAmt());//累計申購面額
		dtlVO.setPURCHASE_AMT(inputVO.getPurchaseAmt());//申購金額 / 庫存金額											
		dtlVO.setDEBIT_ACCT(inputVO.getDebitAcct());//扣款帳號 / 贖回款入帳帳號																
		dtlVO.setPROD_ACCT(inputVO.getProdAcct());//組合式商品帳號	
		dtlVO.setMON_PERIOD(inputVO.getMonPeriod());
		dtlVO.setRM_PROFEE(inputVO.getRmProfee());
		dtlVO.setTARGET_CURR_ID(inputVO.getTargetCurrId());
		dtlVO.setSTRIKE_PRICE(inputVO.getStrikePrice());
		dtlVO.setFTP_RATE(inputVO.getFtpRate());
		dtlVO.setTARGET_NAME(inputVO.getTargetName());
		dtlVO.setTRADE_DATE(inputVO.getTradeDate() == null ? null : new Timestamp(inputVO.getTradeDate().getTime()));//交易日期
		dtlVO.setVALUE_DATE(inputVO.getValueDate() == null ? null : new Timestamp(inputVO.getValueDate().getTime()));
		dtlVO.setSPOT_DATE(inputVO.getSpotDate() == null ? null : new Timestamp(inputVO.getSpotDate().getTime()));
		dtlVO.setEXPIRE_DATE(inputVO.getExpireDate() == null ? null : new Timestamp(inputVO.getExpireDate().getTime()));
		dtlVO.setINT_DATES(inputVO.getIntDates());
		dtlVO.setTRADER_CHARGE(inputVO.getTraderCharge());
		dtlVO.setPRD_PROFEE_RATE(inputVO.getPrdProfeeRate());
		dtlVO.setLESS_PROFEE_RATE(inputVO.getLessProfeeRate());
		dtlVO.setPRD_PROFEE_AMT(inputVO.getPrdProfeeAmt());
		dtlVO.setLESS_PROFEE_AMT(inputVO.getLessProfeeAmt());																		
		dtlVO.setNARRATOR_ID(inputVO.getNarratorID());//解說專員員編
		dtlVO.setNARRATOR_NAME(inputVO.getNarratorName());//解說專員姓名
		dtlVO.setBOSS_ID(inputVO.getBossID());//主管員編
		dtlVO.setAUTH_ID(inputVO.getAuthID());//授權交易人員員編
		dtlVO.setREC_CODE(getRecCode(inputVO.getProdCurr()));

		dam.create(dtlVO);
	}

	/***
	 * 取得錄音代碼
	 * 錄音代碼：西元年後兩碼+FCI+幣別代碼+交易日月份
	 * @param currId
	 * @return
	 * @throws JBranchException
	 */
	private String getRecCode(String currId) throws JBranchException {
		XmlInfo xmlInfo = new XmlInfo();
		String recCode = (String) xmlInfo.getVariable("PRD.FCI_CURRENCY_CODE", currId, "F3"); //幣別代碼
		
		Date date = new Date();
		SimpleDateFormat simpleY = new SimpleDateFormat("yyyy");
		SimpleDateFormat simpleM = new SimpleDateFormat("MM");
		String dateYear = simpleY.format(date).substring(2); //西元年後兩碼
		String dateMon = simpleM.format(date); //交易日月份

		return (dateYear + "FCI" + recCode + dateMon);
	}
	
	/**
	 * 下一步﹝檢核及列印文件﹞
	 * 將畫面上資料存檔
	 * 傳送檢核電文給400主機
	 * 檢核不通過，則顯示錯誤訊息，無法繼續申購交易
	 * 檢核通過，則系統產生批號、產生相關表單，並進到【申購風控】頁面
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void next(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		SOT413InputVO inputVO = (SOT413InputVO) body;
		SOT413OutputVO outputVO = new SOT413OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//超過交易時間
		if(!checkCutTime()) {
			throw new JBranchException("已超過交易時間");
		}
		
		add(dam, queryCondition, inputVO);

		// 檢核電文
		SOT708InputVO inputVO_708 = new SOT708InputVO();
		inputVO_708.setCheckType("1");
		inputVO_708.setTradeSeq(inputVO.getTradeSEQ());
		SOT708 sot708 = (SOT708) PlatformContext.getBean("sot708");
		sot708.verifyESBPurchaseFCI(inputVO_708);
		
		//更新主檔狀態
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		if (null != vo) {
			vo.setTRADE_STATUS("2"); //2:風控檢核中
			dam.update(vo);
		}
		
		//寫入批號
		SOT712InputVO inputVO_712 = new SOT712InputVO();
		inputVO_712.setTradeSeq(inputVO.getTradeSEQ());
		inputVO_712.setProdType("FCI");
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		sot712.updateBatchSeq(inputVO_712);
		
		this.sendRtnObject(outputVO);
	}

	//取消
	public void delTrade(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		SOT413InputVO inputVO = (SOT413InputVO) body;
		SOT413OutputVO outputVO = new SOT413OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("DELETE FROM TBSOT_FCI_TRADE_D WHERE TRADE_SEQ = :tradeSEQ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);

			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("DELETE FROM TBSOT_TRADE_REPORT WHERE TRADE_SEQ = :tradeSEQ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("DELETE FROM TBSOT_TRADE_MAIN WHERE TRADE_SEQ = :tradeSEQ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);

			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	//送出確認電文
	public void goOP(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		initUUID();
		
		SOT413InputVO inputVO = (SOT413InputVO) body;
		SOT413OutputVO outputVO = new SOT413OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM

		//超過交易時間
		if(!checkCutTime()) {
			throw new JBranchException("已超過交易時間");
		}
				
		//更新錄音序號
		this.updateRecSeq(inputVO);
	
		//確認電文
		SOT708InputVO inputVO_708 = new SOT708InputVO();
		inputVO_708.setCheckType("2");
		inputVO_708.setTradeSeq(inputVO.getTradeSEQ());
		SOT708 sot708 = (SOT708) PlatformContext.getBean("sot708");
		sot708.verifyESBPurchaseFCI(inputVO_708);
		
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		vo.setTRADE_STATUS("3"); //3:傳送OP交易並列印表單
		vo.setSEND_DATE(new Timestamp(System.currentTimeMillis())); //傳送OP後要設SEND_DATE
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
				vo.setBRANCH_NBR((String) loginBreach.get(0).get("BRANCH_NBR"));
			} else {
				throw new APException("人員無有效分行"); //顯示錯誤訊息
			}
		} else {
			vo.setBRANCH_NBR(String.valueOf(getCommonVariable(SystemVariableConsts.LOGINBRH)));
		}
		dam.update(vo);
		//		}
		// for CBS 測試用
		cbsservice.setTBSOT_TRADE_MAIN_LASTUPDATE_FOR_TEST(vo.getTRADE_SEQ());
		this.sendRtnObject(outputVO);
	}

	/**
	 * 更新錄音序號
	 * 
	 * @param body
	 * @throws JBranchException
	 * @throws Exception
	 */
	private void updateRecSeq(SOT413InputVO inputVO) throws JBranchException, Exception {
		dam = this.getDataAccessManager();
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		vo.setREC_SEQ(inputVO.getRecSEQ());
		dam.update(vo);
	}	
	
	/**
	 * 檢核錄音序號
	 * 該錄音序號是否有重複使用、錄音日期是否為當日、客戶ID=申購客戶ID、商品分類=FCI，以及錄音的幣別代碼是否正確
	 * @param inputVO
	 * @return
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void validateRecseq(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		SOT413InputVO inputVO = (SOT413InputVO) body;
		SOT413OutputVO outputVO = new SOT413OutputVO();
		
		//錄音序號是否有重複使用
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT 1 FROM TBSOT_TRADE_MAIN WHERE PROD_TYPE = '7' AND TRADE_SEQ <> :tradeSeq AND REC_SEQ = :recSeq ");
		queryCondition.setObject("tradeSeq", inputVO.getTradeSEQ());
		queryCondition.setObject("recSeq", inputVO.getRecSEQ());
		queryCondition.setQueryString(sb.toString());
		if(CollectionUtils.isNotEmpty(dam.exeQuery(queryCondition))) {
			throw new JBranchException("錄音序號重複使用");
		}
		
		//錄音序號檢核:錄音日期是否為當日、客戶ID=申購客戶ID、商品分類=FCI，以及錄音的幣別代碼是否正確
		SOT712InputVO inputVO712 = new SOT712InputVO();
		inputVO712.setTradeSeq(inputVO.getTradeSEQ());
		inputVO712.setCustID(inputVO.getCustID());
		inputVO712.setBranchNbr("");
		inputVO712.setProdType("FCI");
		inputVO712.setRecSeq(inputVO.getRecSEQ());
		inputVO712.setProdID(inputVO.getRecCode());
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		outputVO.setRecseq(sot712.validateRecseq(inputVO712));
		
		this.sendRtnObject(outputVO);
	}
	
	//取得申購金額折台
	public void getPurchaseAmtTwd(Object body, IPrimitiveMap header) throws JBranchException {
		SOT413InputVO inputVO = (SOT413InputVO) body;
		SOT413OutputVO outputVO = new SOT413OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT (:purchaseAmt * BUY_RATE) as PURCHASE_AMT_TWD ");
		sb.append(" FROM TBPMS_IQ053 " );
		sb.append(" WHERE MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" AND trim(CUR_COD) = trim(:prodCurr) ");
		queryCondition.setObject("purchaseAmt", inputVO.getPurchaseAmt());
		queryCondition.setObject("prodCurr", StringUtils.equals("CNH", inputVO.getProdCurr()) ? "CNY" : inputVO.getProdCurr());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		if(CollectionUtils.isEmpty(list) || list.get(0).get("PURCHASE_AMT_TWD") == null) {
			outputVO.setPurchaseAmtTwd(BigDecimal.ZERO);
		} else {
			outputVO.setPurchaseAmtTwd((BigDecimal)list.get(0).get("PURCHASE_AMT_TWD"));
		}

		this.sendRtnObject(outputVO);
	}
		
	public void printRecCaseSHR(Object body, IPrimitiveMap header) throws JBranchException, SQLException, IOException {
		SOT413InputVO inputVO = (SOT413InputVO) body;
		SOT413OutputVO outputVO = new SOT413OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DOC_FILE FROM TBPRD_FCI_DOC ");
		sb.append(" WHERE DOC_FILE IS NOT NULL AND DOC_TYPE = '3' ");	
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> listFile = dam.exeQuery(queryCondition);

		if(CollectionUtils.isEmpty(listFile) || listFile.get(0).get("DOC_FILE") == null) {
			throw new JBranchException("查無SI錄音案例分享文件");
		} else {
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			String uuid = UUID.randomUUID().toString();
			String fileName = String.format("%s.pdf", uuid);
			Blob blob = (Blob) listFile.get(0).get("DOC_FILE");
			int blobLength = (int) blob.length();
			byte[] blobAsBytes = blob.getBytes(1, blobLength);

			File targetFile = new File(filePath, fileName);
			FileOutputStream fos = new FileOutputStream(targetFile);
			fos.write(blobAsBytes);
			fos.close();
			notifyClientViewDoc("temp//" + fileName, "pdf");
		}
		
		this.sendRtnObject(outputVO);
	}
	
	/***
	 * 取得PM專區設定的交易時間，若已超過交易時間不可繼續
	 * @return true:可繼續交易	false:不可繼續交易
	 * @throws JBranchException
	 */
	private boolean checkCutTime() throws JBranchException {
		PRD281 prd281 = (PRD281) PlatformContext.getBean("prd281");
		PRD281OutputVO prd281OuputVO = prd281.inquireParam();
		Date rightNow = new Date();
		Date tradeDateTime = new Date(); //可交易時間
		tradeDateTime.setHours(Integer.parseInt(prd281OuputVO.getT_END_HOUR()));
		tradeDateTime.setMinutes(Integer.parseInt(prd281OuputVO.getT_END_MIN()));
		
		return rightNow.after(tradeDateTime) ? false : true;
	}
}
