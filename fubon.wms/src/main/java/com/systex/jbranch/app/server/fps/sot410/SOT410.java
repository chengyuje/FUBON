package com.systex.jbranch.app.server.fps.sot410;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSOT_SI_TRADE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_SI_TRADE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.prd150.PRD150;
import com.systex.jbranch.app.server.fps.prd150.PRD150InputVO;
import com.systex.jbranch.app.server.fps.prd150.PRD150OutputVO;
import com.systex.jbranch.app.server.fps.sot110.SOT110;
import com.systex.jbranch.app.server.fps.sot701.AcctVO;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701OutputVO;
import com.systex.jbranch.app.server.fps.sot708.SOT708;
import com.systex.jbranch.app.server.fps.sot708.SOT708InputVO;
import com.systex.jbranch.app.server.fps.sot708.SOT708OutputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712OutputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.app.server.fps.sot714.SOT714InputVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHACRDataVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHAIADataVO;
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
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * sot410
 * 
 * @author Lily
 * @date 2016/10/04
 * @spec SI申購
 */
@Component("sot410")
@Scope("request")
public class SOT410 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;

	//撈客戶資料
	public void getSOTCustInfo(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT410InputVO inputVO = (SOT410InputVO) body;
		SOT410OutputVO outputVO = new SOT410OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		SOT701OutputVO outputVO_701 = new SOT701OutputVO();
		SOT712InputVO inputVO_712 = new SOT712InputVO();
		SOT712OutputVO outputVO_712 = new SOT712OutputVO();

		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());
		inputVO_712.setCustID(inputVO.getCustID());
		
		inputVO_701.setNeedFlagNumber(true);

		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		outputVO_712 = sot712.identifyKYCDateAndRecord(inputVO_712);
		outputVO.setKYCResult(outputVO_712.getKYCResult());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		outputVO_701 = sot701.getSOTCustInfo(inputVO_701);
		if(outputVO_701.getFp032675DataVO()!=null){
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
		}
		// add by ocean 2016-10-26 因應適配
		//		outputVO.setIsCustStackholder(sot701.isCustStakeholder(inputVO_701));
		//		outputVO.setCustQValue(sot701.getCustQValue(inputVO_701));
		//FOR CBS測試日期修改
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

		//		outputVO.setCustName("TEST"); //客戶姓名
		//		outputVO.setKycLv("E");  //KYC等級
		//		outputVO.setKycDueDate(new Timestamp(System.currentTimeMillis())); //KYC日期
		//		outputVO.setCustRemarks("N");  //客戶註記
		//		outputVO.setIsOBU("Y");  //OBU註記
		//		outputVO.setProfInvestorYN("Y"); //專業投資人
		//		outputVO.setPiDueDate(new Timestamp(System.currentTimeMillis()));  //專業投資人效期
		outputVO.setNarratorID((String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
		outputVO.setNarratorName((String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINNAME));
		
		//查詢客戶高資產註記資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hNWCDataVO = sot714.getHNWCData(inputVO.getCustID());
		outputVO.setHnwcYN(StringUtils.isNotBlank(hNWCDataVO.getValidHnwcYN()) ? hNWCDataVO.getValidHnwcYN() : "N");
		outputVO.setHnwcServiceYN(hNWCDataVO.getHnwcService());
		outputVO.setFlagNumber(outputVO_701.getFlagNumber());
				
		this.sendRtnObject(outputVO);
	}

	//取的商品明細
	public void getProdDTL(Object body, IPrimitiveMap header) throws Exception {

		SOT410InputVO inputVO = (SOT410InputVO) body;
		SOT410OutputVO outputVO = new SOT410OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		boolean isFitness = StringUtils.equals("N", (String) new XmlInfo().getVariable("SOT.FITNESS_YN", "SI", "F3")) ? false : true;

		outputVO.setWarningMsg("");
		outputVO.setErrorMsg("");

		boolean isFitnessOK = false;
		if (isFitness) {
			// 1.適配，由商品查詢取得，邏輯需一致
			PRD150OutputVO prdOutputVO = new PRD150OutputVO();
			PRD150InputVO prdInputVO = new PRD150InputVO();
			prdInputVO.setCust_id(inputVO.getCustID().toUpperCase());
			prdInputVO.setSi_id(inputVO.getProdID());
			prdInputVO.setType("4");

			PRD150 prd150 = (PRD150) PlatformContext.getBean("prd150");
			prdOutputVO = prd150.inquire(prdInputVO);

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
		if (isFitnessOK) {

			try {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT TS.PRD_ID,TS.SI_CNAME,TS.CURRENCY_STD_ID,TS.RISKCATE_ID,TSN.BASE_AMT_OF_PURCHASE,TSN.UNIT_AMT_OF_PURCHASE, TS.HNWC_BUY ");
				sql.append("FROM TBPRD_SI TS, TBPRD_SIINFO TSN ");
				sql.append("WHERE TS.PRD_ID = TSN.PRD_ID ");
				if (!StringUtils.isBlank(inputVO.getProdID())) {
					sql.append("AND TS.PRD_ID = :prdID ");
					queryCondition.setObject("prdID", inputVO.getProdID());
				}
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				outputVO.setProdDTL(list);

				//此單申購金額
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sq_sumBDS = new StringBuffer();
				sq_sumBDS.append("SELECT sum(NVL(BDABP,0)) AS BDS ");
				sq_sumBDS.append("FROM TBPMS_BDS120_DAY ");
				sq_sumBDS.append("WHERE BDAB6 = 'B' AND BDABF = '1' ");
				if (!StringUtils.isBlank(inputVO.getCustID())) {
					sq_sumBDS.append(" AND BDAB5 = :custID ");
					queryCondition.setObject("custID", inputVO.getCustID());
				}
				if (!StringUtils.isBlank(inputVO.getProdID())) {
					sq_sumBDS.append(" AND BDAB3 = :prdID ");
					queryCondition.setObject("prdID", inputVO.getProdID());
				}
				queryCondition.setQueryString(sq_sumBDS.toString());
				List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);

				if (list1 != null && list1.size() > 0 && list1.get(0).get("BDS") != null) {
					outputVO.setSumBDS(new BigDecimal(list1.get(0).get("BDS").toString()));
				} else {
					logger.debug("not found 'BDS'");
					outputVO.setSumBDS(BigDecimal.ZERO);
				}

				//此單已申購金額
				/*
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sq_sumAJS = new StringBuffer();
				sq_sumAJS.append("select sum(NVL(BDABP,0)) AS AJS ");
				sq_sumAJS.append("from TBPMS_AJS120_DAY ");
				sq_sumAJS.append("where BDAB6 = 'B' and BDABF = '1' ");
				if (!StringUtils.isBlank(inputVO.getCustID())) {
					sq_sumAJS.append(" AND BDAB5 =:custID ");
					queryCondition.setObject("custID", inputVO.getCustID());
				}
				if (!StringUtils.isBlank(inputVO.getProdID())) {
					sq_sumAJS.append(" AND BDAB3 =:prodID ");
					queryCondition.setObject("prodID", inputVO.getProdID());
				}
				queryCondition.setQueryString(sq_sumAJS.toString());
				List<Map<String, Object>> list2_AJS = dam.exeQuery(queryCondition);
				if (list2_AJS != null && list2_AJS.size() > 0 && list2_AJS.get(0).get("AJS") != null) {
					outputVO.setSumAJS(new BigDecimal(list2_AJS.get(0).get("AJS").toString()));
				} else {
					logger.debug("not found 'AJS'");	
				}
				*/

				//前一日投資AUM
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sq_AUM = new StringBuffer();
				sq_AUM.append("select NVL(AMT_20,0) AS NVLA from MVCRM_AST_AMT ");
				if (!StringUtils.isBlank(inputVO.getCustID())) {
					sq_AUM.append(" where CUST_ID = :custID ");
					queryCondition.setObject("custID", inputVO.getCustID());
				}
				queryCondition.setQueryString(sq_AUM.toString());
				List<Map<String, Object>> list2_AUM = dam.exeQuery(queryCondition);
				if (list2_AUM != null && list2_AUM.size() > 0 && list2_AUM.get(0).get("NVLA") != null) {
					outputVO.setNvlAMT(new BigDecimal(list2_AUM.get(0).get("NVLA").toString()));
				} else {
					logger.debug("not found 'NVLA'");
					outputVO.setNvlAMT(BigDecimal.ZERO);
				}

				//SI已申購金額+SI庫存金額+SN已申購金額+SN在途申購金額+SN庫存金額
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sq_sumITEM = new StringBuffer();
				sq_sumITEM.append("select sum(NVL(BAL,0)) AS  SUMITEM ");
				sq_sumITEM.append("FROM TBPMS_CUST_DAY_PROFIT ");
				if (!StringUtils.isBlank(inputVO.getCustID())) {
					sq_sumITEM.append(" WHERE CUST_ID = :custID  ");
					queryCondition.setObject("custID", inputVO.getCustID());
				}
				sq_sumITEM.append("AND ITEM in ('02','03','07','08') ");
				sq_sumITEM.append("AND DATA_DATE = (SELECT max(DATA_DATE) ");
				sq_sumITEM.append("FROM TBPMS_CUST_DAY_PROFIT ");
				if (!StringUtils.isBlank(inputVO.getCustID())) {
					sq_sumITEM.append(" where CUST_ID = :custID ");
					queryCondition.setObject("custID", inputVO.getCustID());
				}
				sq_sumITEM.append(")");
				queryCondition.setQueryString(sq_sumITEM.toString());
				List<Map<String, Object>> list_ITEM = dam.exeQuery(queryCondition);
				if (list_ITEM != null && list_ITEM.size() > 0 && list_ITEM.get(0).get("SUMITEM") != null) {
					outputVO.setSumITEM(new BigDecimal(list_ITEM.get(0).get("SUMITEM").toString()));
				} else {
					logger.debug("not found 'SUMITEM'");
					outputVO.setSumITEM(BigDecimal.ZERO);
				}

				logger.debug(String.format("SumBDS: %f", (outputVO.getSumBDS() == null) ? 0 : outputVO.getSumBDS().floatValue()));
				logger.debug(String.format("NvlAMT: %f", (outputVO.getNvlAMT() == null) ? 0 : outputVO.getNvlAMT().floatValue()));
				logger.debug(String.format("SumITEM: %f", (outputVO.getSumITEM() == null) ? 0 : outputVO.getSumITEM().floatValue()));

			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
			}
		}

		this.sendRtnObject(outputVO);
	}

	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		SOT410InputVO inputVO = (SOT410InputVO) body;
		SOT410OutputVO outputVO = new SOT410OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;

		try {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT D.*, (D.PURCHASE_AMT*IQ053.BUY_RATE) as PURCHASE_AMT_TWD FROM TBSOT_SI_TRADE_D D " 
					+ " LEFT OUTER JOIN (SELECT to_char(MTN_DATE,'yyyy-MM-dd') as MTN_DATE,CUR_COD,BUY_RATE,SEL_RATE,BUY_RATE EX_RATE " //用買價
					+ " FROM TBPMS_IQ053 WHERE MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053)) IQ053 on trim(CUR_COD) = trim(D.PROD_CURR) " 
					+ " WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());

			outputVO.setCarList(dam.exeQuery(queryCondition));

			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT * FROM TBSOT_TRADE_MAIN ");
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
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

		SOT410InputVO inputVO = (SOT410InputVO) body;
		SOT410OutputVO outputVO = new SOT410OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		add(dam, queryCondition, inputVO);

		this.sendRtnObject(outputVO);
	}

	public void add(DataAccessManager dam, QueryConditionIF queryCondition, SOT410InputVO inputVO) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		
		//是否需要錄音 ， SI不需要首購判斷 
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO inputVO_712 = new SOT712InputVO();
		inputVO_712.setCustID(inputVO.getCustID());
		inputVO_712.setProdID(inputVO.getProdID());
		inputVO_712.setProdType("SI");
		inputVO_712.setProfInvestorYN(inputVO.getProfInvestorYN());
		inputVO_712.setCustRemarks(inputVO.getCustRemarks());
		inputVO_712.setCustProRemark(inputVO.getPiRemark());
		inputVO_712.setPro1500(inputVO.getPro1500());
		inputVO_712.setTrustProCorp(inputVO.getTrustProCorp());
		inputVO_712.setUnder300(this.checkAmt300(dam, inputVO.getPurchaseAmt(), inputVO.getProdCurr()));

		// 1.寫入主檔/明細
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		if (StringUtils.indexOf(inputVO.getDebitAcct(), "_") > 0)
			inputVO.setDebitAcct(StringUtils.split(inputVO.getDebitAcct(), "_")[0]);
		if (null == vo) {
			TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();
			mainVO.setTRADE_SEQ(inputVO.getTradeSEQ());
			mainVO.setPROD_TYPE("4"); //4:SI
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
			mainVO.setTRADE_STATUS("1"); //1:暫存
			mainVO.setIS_BARGAIN_NEEDED("N"); //TODO 待提供
			mainVO.setHNWC_YN(inputVO.getHnwcYN());
			mainVO.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());
						
			//TODO
			mainVO.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N"); // 是否需要錄音
//			mainVO.setIS_REC_NEEDED("Y"); // 是否需要錄音

			mainVO.setREC_SEQ(null);
			mainVO.setSEND_DATE(null);

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
				mainVO.setBRANCH_NBR(String.valueOf(getCommonVariable(SystemVariableConsts.LOGINBRH)));
			}
			
			/*
			 * WMS-CR-20191009-01_金錢信託套表需求申請單
			 * 
			 * 2019-12-20 add by ocean
			 */
			mainVO.setTRUST_TRADE_TYPE("S");
			
			mainVO.setHNWC_YN(inputVO.getHnwcYN());
			mainVO.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());
			mainVO.setFLAG_NUMBER(inputVO.getFlagNumber());
			
			dam.create(mainVO);
		} else {
			vo.setTRADE_STATUS("1"); //暫存  //
			vo.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N"); // 是否需要錄音
			dam.update(vo);
		}
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM TBSOT_SI_TRADE_D WHERE TRADE_SEQ = :tradeSEQ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);

		BigDecimal newTrade_SEQNO = new BigDecimal(sot712.getnewTrade_SEQNO());
		TBSOT_SI_TRADE_DPK dtlPK = new TBSOT_SI_TRADE_DPK();
		dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dtlPK.setSEQ_NO(newTrade_SEQNO);

		TBSOT_SI_TRADE_DVO dtlVO = new TBSOT_SI_TRADE_DVO();
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
		dtlVO.setREF_VAL(null);//參考報價  :待確認						
		dtlVO.setREF_VAL_DATE(null != inputVO.getRefValDate() ? new Timestamp(inputVO.getRefValDate().getTime()) : null);//參考報價日期		 :待確認												
		dtlVO.setDEBIT_ACCT(inputVO.getDebitAcct());//扣款帳號 / 贖回款入帳帳號																
		dtlVO.setPROD_ACCT(inputVO.getProdAcct());//組合式商品帳號						
		dtlVO.setTRADE_DATE(new Timestamp(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()).getTime()));//交易日期																									
		dtlVO.setNARRATOR_ID(inputVO.getNarratorID());//解說專員員編
		dtlVO.setNARRATOR_NAME(inputVO.getNarratorName());//解說專員姓名
		dtlVO.setBOSS_ID(inputVO.getBossID());//主管員編
		dtlVO.setAUTH_ID(inputVO.getAuthID());//授權交易人員員編
		dtlVO.setOVER_CENTRATE_YN(inputVO.getOverCentRateYN()); //集中度是否超過上限 Y:是

		dam.create(dtlVO);
	}
	
	private Boolean checkAmt300(DataAccessManager dam, BigDecimal amt, String cur) throws JBranchException {
		Boolean checkAmt = false;
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CASE WHEN (SEL_RATE * :amt) < 3000000 THEN 'Y' ELSE 'N' END AS AMT_FLAG ");
		sb.append("FROM TBPMS_IQ053 WHERE MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append("AND CUR_COD = :cur_cod ");
		queryCondition.setObject("amt", amt);
		queryCondition.setObject("cur_cod", cur);
		queryCondition.setQueryString(sb.toString());
		resultList = dam.exeQuery(queryCondition);
		if (resultList.size() > 0) {
			checkAmt = resultList.get(0).get("AMT_FLAG").toString().equals("Y");
		}
		return checkAmt;
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

	// 下一步
	public void next(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		WorkStation ws = DataManager.getWorkStation(uuid);
		SOT410InputVO inputVO = (SOT410InputVO) body;
		SOT410OutputVO outputVO = new SOT410OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		//買匯匯率
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		BigDecimal rate = sot110.getBuyRate(inputVO.getProdCurr());
				
		//高資產客戶投組風險權值檢核
	    //客戶風險屬性非C4，有選擇越級商品
		if(!StringUtils.equals("C4", inputVO.getKycLv()) 
				&& (Integer.parseInt(inputVO.getProdRiskLv().substring(1))) > (Integer.parseInt(inputVO.getKycLv().substring(1)))
				&& StringUtils.equals("Y", inputVO.getHnwcYN()) && StringUtils.equals("Y", inputVO.getHnwcServiceYN())) {
	    	//高資產客戶投組風險權值檢核
			if(!validHnwcRiskValue(inputVO, rate)) {
				throw new APException("客戶加計此筆投資之越級比率已超標，請選擇其他標的或調整投資面額");
			}
  		}
		
		//高資產客戶購買高風險商品或不保本商品，檢核集中度
		// TODO 上[2080]拆[2027]
		//#2027: WMS-CR-20240509-01_高資產集中度交易取消不保本境內外結構型商品檢核
		if(StringUtils.equals("Y", inputVO.getHnwcYN()) && (StringUtils.equals("Y", inputVO.getHnwcBuy()) || !isRateGuaranteed(inputVO.getProdID()))) {
			inputVO.setOverCentRateYN(getOverCentRateYN(inputVO, rate));
			if(StringUtils.equals("Y", inputVO.getOverCentRateYN())) {
				outputVO.setWarningMsg("客戶高風險商品集中度已超過控管上限，請取得客戶同意及處主管核准");
			}
		}
				
		add(dam, queryCondition, inputVO);

		// 檢核電文
		SOT708InputVO inputVO_708 = new SOT708InputVO();
		SOT708OutputVO outputVO_708 = new SOT708OutputVO();

		inputVO_708.setCheckType("1");
		inputVO_708.setTradeSeq(inputVO.getTradeSEQ());

		/*
		   SOT708 sot708 = (SOT708) PlatformContext.getBean("sot708");
		下一步不需要檢核吧
		 *因為也檢核錄音sot708.verifyESBPurchaseSI(inputVO_708);
		 *Cathy 耀婷: SI只有確認   下一步只需要儲存，不須打檢核電文
		 */

		//		String errorMsg = outputVO_708.getErrorMsg();
		//		if (!"".equals(errorMsg) && null != errorMsg) {
		//			outputVO.setErrorMsg(errorMsg);
		//		} else {
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		if (null != vo) {
			vo.setTRADE_STATUS("2"); //2:風控檢核中
			dam.update(vo);
		}
		//		}

		this.sendRtnObject(outputVO);
	}

	//取消
	public void delTrade(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		SOT410InputVO inputVO = (SOT410InputVO) body;
		SOT410OutputVO outputVO = new SOT410OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("DELETE FROM TBSOT_SI_TRADE_D WHERE TRADE_SEQ = :tradeSEQ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);

			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
			if (vo != null) {
				dam.delete(vo);
			}

			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	//非常規交易
	public void getSOTCustInfoCT(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT410InputVO inputVO = (SOT410InputVO) body;
		SOT410OutputVO outputVO = new SOT410OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		Boolean isFirstTrade = false;

		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		fp032675DataVO = sot701.getFP032675Data(inputVO_701);

		outputVO.setAgeUnder70Flag(fp032675DataVO.getAgeUnder70Flag());
		outputVO.setEduJrFlag(fp032675DataVO.getEduJrFlag());
		outputVO.setHealthFlag(fp032675DataVO.getHealthFlag());
		outputVO.setCustRemarks(fp032675DataVO.getCustRemarks());
		outputVO.setCustProFlag(fp032675DataVO.getCustProFlag());

		outputVO.setProCorpInv(fp032675DataVO.getProCorpInv());
		outputVO.setHighYieldCorp(fp032675DataVO.getHighYieldCorp());
		outputVO.setSiProCorp(fp032675DataVO.getSiProCorp());
		outputVO.setPro3000(fp032675DataVO.getPro3000());
		outputVO.setPro1500(fp032675DataVO.getPro1500());

		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO sot712InputVO = new SOT712InputVO();
		sot712InputVO.setCustID(inputVO.getCustID());
		outputVO.setCustAge(sot712.getCUST_AGE(sot712InputVO)); //查客戶年齡 (未成年警語)

		this.sendRtnObject(outputVO);
	}

	//傳送OP交易並列印表單
	public void goOP(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT410InputVO inputVO = (SOT410InputVO) body;
		SOT410OutputVO outputVO = new SOT410OutputVO();
		dam = this.getDataAccessManager();
		this.updateSecSeq(body);//更新錄音序號

		// 更新批號
		SOT712InputVO inputVO_712 = new SOT712InputVO();
		SOT712OutputVO outputVO_712 = new SOT712OutputVO();

		inputVO_712.setProdType("SI");
		inputVO_712.setTradeSeq(inputVO.getTradeSEQ());
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");

		// 檢核電文
		SOT708InputVO inputVO_708 = new SOT708InputVO();
		SOT708OutputVO outputVO_708 = new SOT708OutputVO();

		inputVO_708.setCheckType("2");
		inputVO_708.setTradeSeq(inputVO.getTradeSEQ());

		SOT708 sot708 = (SOT708) PlatformContext.getBean("sot708");
		try {
			sot708.verifyESBPurchaseSI(inputVO_708);
		} catch (Exception e) {
			String errorMsg = e.getMessage();
			//			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			errorMsg = sot712.inquireErrorCode(e.getMessage());
			logger.error(String.format("SI申購確認電文sot708.verifyESBPurchaseSI錯誤:%s", errorMsg), e);
			throw new JBranchException(String.format("SI申購確認電文錯誤:%s", errorMsg), e);
		}
		//		String errorMsg = outputVO_708.getErrorMsg();
		//		if (!"".equals(errorMsg) && null != errorMsg) {
		//			outputVO.setErrorMsg(errorMsg);
		//		} else {
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

		vo.setTRADE_STATUS("3"); //3:傳送OP交易並列印表單
		vo.setSEND_DATE(new Timestamp(System.currentTimeMillis())); //傳送OP後要設SEND_DATE
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
	private void updateSecSeq(Object body) throws JBranchException, Exception {
		SOT410InputVO inputVO = (SOT410InputVO) body;
		SOT410OutputVO outputVO = new SOT410OutputVO();
		dam = this.getDataAccessManager();
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		vo.setREC_SEQ(inputVO.getRecSEQ());
		dam.update(vo);
	}
	
	/***
	 * 集中度是否超過上限
	 * Y:超過 N:沒超過
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private String getOverCentRateYN(SOT410InputVO inputVO, BigDecimal rate) throws Exception {
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustID());
		//集中度加上新增商品金額
		inputVO714.setProdType("1");//SI
		inputVO714.setBuyAmt(inputVO.getPurchaseAmt().multiply(rate));
				
		//查詢客戶高資產集中度資料
		WMSHACRDataVO cRateData = sot714.getCentRateData(inputVO714);
		return StringUtils.equals("Y", cRateData.getVALIDATE_YN()) ? "N" : "Y"; //可交易=>沒超過上限; 不可交易=>超過上限
	}
	
	/***
	 * 商品是否保本
	 * true: 保本
	 * false: 不保本
	 * @param prodId
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private boolean isRateGuaranteed(String prodId) throws DAOException, JBranchException {
		boolean returnVal = false;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//取得保本率
		queryCondition.setQueryString("select NVL(RATE_GUARANTEEPAY, 0) AS RATE_GUARANTEEPAY from TBPRD_SI where PRD_ID = :prdId ");
		queryCondition.setObject("prdId", prodId);
		List<Map<String, Object>> pList = dam.exeQuery(queryCondition);
		if (CollectionUtils.isNotEmpty(pList)) {
			BigDecimal base = new BigDecimal(100);
			BigDecimal rate = (BigDecimal) pList.get(0).get("RATE_GUARANTEEPAY");
			returnVal = rate.compareTo(base) > -1;
		}
		
		return returnVal;
	}
	
	/***
	 * 高資產客戶投組風險權值檢核
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private boolean validHnwcRiskValue(SOT410InputVO inputVO, BigDecimal rate) throws Exception {
		//取得高資產客戶註記資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(inputVO.getCustID());
		//高資產客戶投組風險權值檢核
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustID());
		inputVO714.setCUST_KYC(inputVO.getKycLv()); //客戶C值
		inputVO714.setSP_YN(hnwcData.getSpFlag()); //是否為特定客戶
		inputVO714.setPROD_RISK(inputVO.getProdRiskLv()); //商品風險檢核值
		BigDecimal amtBuy = inputVO.getPurchaseAmt().multiply(rate);
		switch(inputVO.getProdRiskLv()) {
			case "P1":
				inputVO714.setAMT_BUY_1(amtBuy);
				break;
			case "P2":
				inputVO714.setAMT_BUY_2(amtBuy);
				break;
			case "P3":
				inputVO714.setAMT_BUY_3(amtBuy);
				break;
			case "P4":
				inputVO714.setAMT_BUY_4(amtBuy);
				break;
		}
		//查詢客戶風險檢核值資料
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		
		return StringUtils.equals("Y", riskValData.getVALIDATE_YN()) ? true : false;
	}
}
