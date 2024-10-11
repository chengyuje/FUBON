package com.systex.jbranch.app.server.fps.sot712;

import com.systex.jbranch.app.common.fps.table.*;
import com.systex.jbranch.app.server.fps.i18n.I18NInputVO;
import com.systex.jbranch.app.server.fps.i18n.I18NOutputVO;
import com.systex.jbranch.app.server.fps.oth001.OTH001;
import com.systex.jbranch.app.server.fps.prd110.PRD110;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.app.server.fps.sot714.SOT714InputVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHACRDataVO;
import com.systex.jbranch.app.server.fps.sot815.SOT815MoneyDJ;
import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.PdfConfigVO;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.fitness.ProdFitness;
import com.systex.jbranch.fubon.commons.fitness.ProdFitnessCustVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;



/**
 * SOT712
 *
 * @author Jimmy
 * @date 2016/08/15
 * @spec null
 *
 * modify by Ocean at 2016/10/05
 * modify by Sebastian at 2016/10/06
 * 增加供後台程式呼叫的getEffDate()
 * modify by Ocean at 2016/10/27 add getIsRecNeeded() 是否需要錄音
 */
@Component("sot712")
@Scope("request")
public class SOT712 extends FubonWmsBizLogic{
	@Autowired
	CBSService cbsService;

	Logger logger = LoggerFactory.getLogger(this.getClass());
	DataAccessManager dam;

	//WMS-CR-20200707-01_應附文件清單隨業管套表文件印出(金錢信託)
	private List<String> printRptIdList = new ArrayList<String>();

	public SOT712() {
	}

	/*
	 * 用解說專員編查詢解說專員姓名
	 *
	 */
	public void getTellerName (Object body, IPrimitiveMap<Object> header) throws JBranchException {

		SOT712InputVO inputVO = (SOT712InputVO) body;
		SOT712OutputVO outputVO = new SOT712OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT EMP_NAME ");
		sb.append("FROM TBORG_MEMBER ");
		sb.append("WHERE EMP_ID = :tellerID ");
		sb.append("AND SERVICE_FLAG = 'A' ");
		sb.append("AND CHANGE_FLAG IN('A', 'M', 'P') ");
		queryCondition.setObject("tellerID", inputVO.getTellerID());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> TellerNameList = dam.exeQuery(queryCondition);

		if (TellerNameList.size() <= 0) {
			 throw new JBranchException("ehl_01_common_009");
		} else {
			outputVO.setEMP_NAME((String) TellerNameList.get(0).get("EMP_NAME"));
		}

		sendRtnObject(outputVO);
	}

	public void validateRecseq(Object body, IPrimitiveMap<Object> header) throws JBranchException, ParseException {
		SOT712InputVO inputVO = (SOT712InputVO) body;
		SOT712OutputVO outputVO = new SOT712OutputVO();
		outputVO.setRecseq(validateRecseq(inputVO));
		sendRtnObject(outputVO);
	}
	
	//錄音序號檢核
	public boolean validateRecseq(SOT712InputVO inputVO) throws JBranchException, ParseException {
		//FOR CBS測試日期 改日期
		Date now = new SimpleDateFormat("yyyyMMdd").parse(cbsService.getCBSTestDate().substring(0, 8));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String now_temp = sdf.format(now);
		String now_date = now_temp.substring(2, now_temp.length());
		boolean recseq = true;
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		if (StringUtils.isBlank(inputVO.getRecSeq()) || StringUtils.isBlank(inputVO.getCustID()) || StringUtils.isBlank(inputVO.getProdType())) {
            throw new JBranchException("錄音序號檢核遺漏必要欄位");
        }else{
			// 平測模式共用序號檢核若通過，則不進行錄音序號檢核
			if (PlatformContext.getBean(OTH001.class).voiceRecordTestModePass(inputVO.getRecSeq())) {
				recseq = true;
			} else if (inputVO.getRecSeq().length()<6){
        		throw new JBranchException("錄音序號長度錯誤，不得低於6碼");
        	}else{
        		//#0238 SI/SN 南東分行反應非當日錄音序號無法申購SN，經查SI/SN申購業管錄音序號邏輯限當日有效，請修改為by 客戶ID+商品代號有效，不限當日。
            	if(!StringUtils.equals(inputVO.getProdType(), "SI")
            			&& !StringUtils.equals(inputVO.getProdType(), "SD")
            			&& !StringUtils.equals(inputVO.getProdType(), "SN")
            			&& !now_date.equals(inputVO.getRecSeq().substring(0, 6))){
            		recseq = false;
            	} else {
            		StringBuilder sb = new StringBuilder();
            		sb.append(" call P_VOICERECORDING_QRY(?,?,?,?,?,?) ");
            		queryCondition.setString(1, inputVO.getRecSeq());
            		queryCondition.setString(2, inputVO.getCustID());
            		queryCondition.setString(3, inputVO.getBranchNbr());

            		String prod_type = inputVO.getProdType();
            		if (StringUtils.equals(inputVO.getProdType(), "SI") && !this.isGuaranteed(inputVO.getProdID())) {
            			//非保本商品：須具備一筆錄音序號符合 PRODID = 商品代號、PRODTYPE = SDNPP
            			prod_type = "SDNPP";
            		} else if(StringUtils.equals("Y", inputVO.getOvsPrivateYN())) {
                		//境外私募基金
            			prod_type = "PE";
            		}            		
            		queryCondition.setString(4, prod_type);

            		queryCondition.setString(5, inputVO.getProdID());
              		queryCondition.registerOutParameter(6,Types.VARCHAR);

            		queryCondition.setQueryString(sb.toString());

            		Map<Integer, Object> RecList = dam.executeCallable(queryCondition);
            		if("0000".equals(RecList.get(6).toString())){
            			recseq = true;
            		}else{
            			recseq = false;
            		}
            	}
        	}
        }
		
		return recseq;
	}


	/*
	 * 取得預約交易日期
	 *
	 */
	public void getReserveTradeDate (Object body, IPrimitiveMap<Object> header) throws JBranchException, ParseException {

		SOT712InputVO inputVO = (SOT712InputVO) body;
		SOT712OutputVO outputVO = new SOT712OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		outputVO.setTradeDate(getTradeDate(dam, queryCondition, inputVO));

		sendRtnObject(outputVO);
	}

	/*
	 * 取得生效日期，用時間區分
	 * 3:20前為即時交易→生效日期等於交易日期
	 * 3:20~3:30之間不可執行交易
	 * 3:30以後生效日期為交易日期+3個營業日
	 */
	public void getEffDate (Object body, IPrimitiveMap<Object> header) throws JBranchException, ParseException {
		sendRtnObject(this.getEffDate(body));
	}

	/**
	 * 取得生效日期 (時間區分)
	 * 15:20前為即時交易→生效日期等於交易日期
	 * 15:20~15:30之間不可執行交易
	 * 15:30以後生效日期為交易日期+3個營業日
	 *
	 * 15:20 & 15:30為XML參數；PARAM_TYPE=SOT.RESERVE_DATE_TIMESTAMP
	 *
	 * @author Sebastian
	 * @version 2016-10-07
	 * @param body
	 * @return
	 * @throws JBranchException
	 * @throws ParseException
     */
	public SOT712OutputVO getEffDate (Object body) throws JBranchException, ParseException {
		SOT712InputVO inputVO = (SOT712InputVO) body;
		SOT712OutputVO outputVO = new SOT712OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		SimpleDateFormat sdfHHMM = new SimpleDateFormat("HHmm");
		Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsService.getCBSTestDate());  //FOR CBS測試日期

		String[] param = getReserveDateTimestamp();

		BigDecimal nowHHMM = new BigDecimal(sdfHHMM.format(date));
		if (nowHHMM.compareTo(new BigDecimal(param[0])) == -1) {
			outputVO.setTradeDate(date);
		} else if (nowHHMM.compareTo(new BigDecimal(param[0])) == 1 && nowHHMM.compareTo(new BigDecimal(param[1])) == -1) {
			throw new JBranchException("ehl_01_SOT_009");
		} else {
			outputVO.setTradeDate(getTradeDate(dam, queryCondition, inputVO));
		}

		return outputVO;
	}

	private String[] getReserveDateTimestamp() throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT PARAM_NAME FROM TBSYSPARAMETER ");
		sb.append("WHERE PARAM_CODE = '1' AND PARAM_TYPE = 'SOT.RESERVE_DATE_TIMESTAMP' ");
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> listMap = dam.exeQuery(queryCondition);

		String[] rdTimestamp = new String[2];
		if (CollectionUtils.isNotEmpty(listMap) && listMap.size() > 0) {
			String param = (String) listMap.get(0).get("PARAM_NAME");
			rdTimestamp = StringUtils.split(param, "|");
		}

		if(rdTimestamp == null || rdTimestamp.length < 2
				|| StringUtils.isBlank(rdTimestamp[0]) || StringUtils.isBlank(rdTimestamp[1])) {
			rdTimestamp[0] = "1520";
			rdTimestamp[1] = "1530";
		}

		return rdTimestamp;
	}

	public Date getTradeDate (DataAccessManager dam, QueryConditionIF queryCondition, SOT712InputVO inputVO) throws JBranchException, ParseException {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT PARAM_NAME FROM TBSYSPARAMETER ");
		sb.append("WHERE PARAM_CODE = :prodType ");
		sb.append("AND PARAM_TYPE = 'SOT.RESERVE_TRADE_DAYS' ");
		queryCondition.setObject("prodType", inputVO.getProdType());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> days = dam.exeQuery(queryCondition);

		if (days.size() > 0) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append("SELECT PABTH_UTIL.FC_getBusiDay(:SYSDATE, 'TWD', :days) AS BUSI_DAY FROM DUAL "); //FN_getBusiDay(SYSDATE, :days)
			queryCondition.setObject("days", days.get(0).get("PARAM_NAME"));
			queryCondition.setObject("SYSDATE", new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsService.getCBSTestDate()));
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> tradeDateList = dam.exeQuery(queryCondition);

			return (Timestamp) tradeDateList.get(0).get("BUSI_DAY");
		}

		return null;
	}

	/*
	 * 根據傳入參數取得交易資料，由批號產生規則將批號寫入資料庫中
	 */
	public void updateBatchSeq (Object body, IPrimitiveMap<Object> header) throws JBranchException {
		sendRtnObject(this.updateBatchSeq(body));
	}

	public SOT712OutputVO updateBatchSeq(Object body) throws JBranchException {
		SOT712InputVO inputVO = (SOT712InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

		String modifier=(String) getCommonVariable(SystemVariableConsts.LOGINID);

		StringBuffer sb = new StringBuffer();
		if ("NF".equals(inputVO.getProdType())) { //基金
			// 從下單交易主檔取得客戶 OBU 註記，判斷是否為 OBU 客戶
			TBSOT_TRADE_MAINVO mainVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSeq());
			boolean isObu = mainVO != null && "Y".equals(mainVO.getIS_OBU());

			if ("1".equals(inputVO.getTradeType()) || "5".equals(inputVO.getTradeType())) { //單筆申購/小額申購

				sb = new StringBuffer();
				sb.append("UPDATE TBSOT_NF_PURCHASE_D SET BATCH_SEQ = null, BATCH_NO = null,MODIFIER=:MODIFIER,LASTUPDATE=SYSDATE  WHERE TRADE_SEQ = :tradeSEQ");
				queryCondition.setObject("MODIFIER", modifier);
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				queryCondition.setQueryString(sb.toString());
				dam.exeUpdate(queryCondition);

				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT TRADE_SUB_TYPE, TRUST_CURR_TYPE, TRADE_DATE, TRUST_ACCT, DEBIT_ACCT, CREDIT_ACCT, SEQ_NO, PROSPECTUS_TYPE, CONTRACT_ID ");
				sb.append("FROM TBSOT_NF_PURCHASE_D ");
				sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
				sb.append("ORDER BY TRADE_SUB_TYPE, TRUST_CURR_TYPE, TRADE_DATE, TRUST_ACCT, DEBIT_ACCT, CREDIT_ACCT ");
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> list = dam.exeQuery(queryCondition);

				String batchSEQ = getSeq();
				Integer batchNO = 1; 	//批號流水號

				for (Integer i = 0; i < list.size(); i++) {
					if (i != 0) {
						String tradeSubType_back = CharUtils.toString((Character)list.get(i - 1).get("TRADE_SUB_TYPE"));
						String trustCurrType_back = String.valueOf(list.get(i - 1).get("TRUST_CURR_TYPE"));
						String trustAcct_back = (String) list.get(i - 1).get("TRUST_ACCT");
						String debitAcct_back = (String) list.get(i - 1).get("DEBIT_ACCT");
						String creditAcct_back = (String) list.get(i - 1).get("CREDIT_ACCT");
						String tradeDate_back = (null != list.get(i - 1).get("TRADE_DATE") ? sdfYYYYMMDD.format((Timestamp) list.get(i - 1).get("TRADE_DATE")) : "");
						String prospectusType_back = list.get(i - 1).get("PROSPECTUS_TYPE").toString();
						String contractID_back = null != list.get(i - 1).get("CONTRACT_ID") ? list.get(i - 1).get("CONTRACT_ID").toString() : "";

						if ((StringUtils.isNotBlank(tradeSubType_back) && !tradeSubType_back.equals(CharUtils.toString((Character)list.get(i).get("TRADE_SUB_TYPE")))) ||
							(StringUtils.isNotBlank(trustCurrType_back) && !trustCurrType_back.equals(String.valueOf(list.get(i).get("TRUST_CURR_TYPE")))) ||
							(StringUtils.isNotBlank(trustAcct_back) && !trustAcct_back.equals(list.get(i).get("TRUST_ACCT"))) ||
							(StringUtils.isNotBlank(debitAcct_back) && !debitAcct_back.equals(list.get(i).get("DEBIT_ACCT"))) ||
							(StringUtils.isNotBlank(creditAcct_back) && !creditAcct_back.equals(list.get(i).get("CREDIT_ACCT"))) ||
							(StringUtils.isNotBlank(tradeDate_back) && !tradeDate_back.equals((null != list.get(i).get("TRADE_DATE") ? sdfYYYYMMDD.format((Timestamp) list.get(i).get("TRADE_DATE")) : ""))) ||
							(StringUtils.isNotBlank(prospectusType_back) && !prospectusType_back.equals(list.get(i).get("PROSPECTUS_TYPE").toString())) ||
							(StringUtils.isNotBlank(contractID_back) && !contractID_back.equals(list.get(i).get("CONTRACT_ID").toString()))
							) {

							batchSEQ = getSeq();
							batchNO = 1;
						} else {
							// DBU 電文最多可三筆，OBU 電文只能放一筆
							if ((new BigDecimal(String.valueOf(batchNO))).compareTo(new BigDecimal(isObu? "1": "3")) == 0) {
								batchSEQ = getSeq();
								batchNO = 1;
							} else {
								batchNO++;
							}
						}
					}

					TBSOT_NF_PURCHASE_DPK pk = new TBSOT_NF_PURCHASE_DPK();
					pk.setTRADE_SEQ(inputVO.getTradeSeq());
					pk.setSEQ_NO((BigDecimal) list.get(i).get("SEQ_NO"));

					TBSOT_NF_PURCHASE_DVO vo = new TBSOT_NF_PURCHASE_DVO();
					vo.setcomp_id(pk);
					vo = (TBSOT_NF_PURCHASE_DVO) dam.findByPKey(TBSOT_NF_PURCHASE_DVO.TABLE_UID, vo.getcomp_id());
					vo.setBATCH_SEQ(batchSEQ);
					vo.setBATCH_NO(new BigDecimal(String.valueOf(batchNO)));

					dam.update(vo);

					logger.info("TradeSeq:" + inputVO.getTradeSeq() + ", batchSEQ:" + batchSEQ);
				}
			} else if ("2".equals(inputVO.getTradeType())) { //贖回/贖回再申購
				sb = new StringBuffer();
				sb.append("UPDATE TBSOT_NF_REDEEM_D SET BATCH_SEQ = null, BATCH_NO = null,MODIFIER=:MODIFIER,LASTUPDATE=SYSDATE WHERE TRADE_SEQ = :tradeSEQ");
				queryCondition.setObject("MODIFIER", modifier);
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				queryCondition.setQueryString(sb.toString());
				dam.exeUpdate(queryCondition);

				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT IS_RE_PURCHASE, PROSPECTUS_TYPE, TRUST_ACCT, TRADE_DATE, SEQ_NO, CONTRACT_ID ");
				sb.append("FROM TBSOT_NF_REDEEM_D ");
				sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
				sb.append("ORDER BY IS_RE_PURCHASE, PROSPECTUS_TYPE, TRUST_ACCT, TRADE_DATE ");
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> list = dam.exeQuery(queryCondition);

				String batchSEQ = getSeq();
				Integer batchNO = 1; 	//批號流水號

				for (Integer i = 0; i < list.size(); i++) {
					if (i != 0) {
						String trustAcct_back = getString(list.get(i - 1).get("TRUST_ACCT"));
						String tradeDate_back = (null != list.get(i - 1).get("TRADE_DATE") ? sdfYYYYMMDD.format((Timestamp) list.get(i - 1).get("TRADE_DATE")) : "");
						String prospectusType_back = getString(list.get(i - 1).get("PROSPECTUS_TYPE"));
						String isRePurchase_back = getString(list.get(i - 1).get("IS_RE_PURCHASE"));
						String contractID_back = null != list.get(i - 1).get("CONTRACT_ID") ? list.get(i - 1).get("CONTRACT_ID").toString() : "";

						if ((StringUtils.isNotBlank(isRePurchase_back) && !isRePurchase_back.equals(getString(list.get(i).get("IS_RE_PURCHASE")))) ||
							(StringUtils.isNotBlank(trustAcct_back) && !trustAcct_back.equals(getString(list.get(i).get("TRUST_ACCT")))) ||
							(StringUtils.isNotBlank(tradeDate_back) && !tradeDate_back.equals((null != list.get(i).get("TRADE_DATE") ? sdfYYYYMMDD.format((Timestamp) list.get(i).get("TRADE_DATE")) : ""))) ||
							(StringUtils.isNotBlank(prospectusType_back) && !prospectusType_back.equals(getString(list.get(i).get("PROSPECTUS_TYPE")))) ||
							(StringUtils.isNotBlank(contractID_back) && !contractID_back.equals(list.get(i).get("CONTRACT_ID").toString()))) {

							batchSEQ = getSeq();
							batchNO = 1;
						} else {
							// DBU 電文最多可三筆，OBU 電文只能放一筆
							if ((new BigDecimal(String.valueOf(batchNO))).compareTo(new BigDecimal(isObu? "1": "3")) == 0) {
								batchSEQ = getSeq();
								batchNO = 1;
							} else {
								batchNO++;
							}
						}
					}

					TBSOT_NF_REDEEM_DPK pk = new TBSOT_NF_REDEEM_DPK();
					pk.setTRADE_SEQ(inputVO.getTradeSeq());
					pk.setSEQ_NO((BigDecimal) list.get(i).get("SEQ_NO"));

					TBSOT_NF_REDEEM_DVO vo = new TBSOT_NF_REDEEM_DVO();
					vo.setcomp_id(pk);
					vo = (TBSOT_NF_REDEEM_DVO) dam.findByPKey(TBSOT_NF_REDEEM_DVO.TABLE_UID, vo.getcomp_id());
					vo.setBATCH_SEQ(batchSEQ);
					vo.setBATCH_NO(new BigDecimal(String.valueOf(batchNO)));

					dam.update(vo);

					logger.info("TradeSeq:" + inputVO.getTradeSeq() + ", batchSEQ:" + batchSEQ);
				}
			} else if ("3".equals(inputVO.getTradeType())) { //轉換
				sb = new StringBuffer();
				sb.append("UPDATE TBSOT_NF_TRANSFER_D SET BATCH_SEQ = null, BATCH_NO = null,MODIFIER=:MODIFIER,LASTUPDATE=SYSDATE WHERE TRADE_SEQ = :tradeSEQ");
				queryCondition.setObject("MODIFIER", modifier);
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				queryCondition.setQueryString(sb.toString());
				dam.exeUpdate(queryCondition);

				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT OUT_TRUST_ACCT, FEE_DEBIT_ACCT, TRADE_DATE, SEQ_NO, PROSPECTUS_TYPE ");
				sb.append("FROM TBSOT_NF_TRANSFER_D ");
				sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
				sb.append("ORDER BY OUT_TRUST_ACCT, FEE_DEBIT_ACCT, TRADE_DATE ");
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> list = dam.exeQuery(queryCondition);

				String batchSEQ = getSeq();
				Integer batchNO = 1; 	//批號流水號

				for (Integer i = 0; i < list.size(); i++) {
					if (i != 0) {
						String outTrustAcct_back = (String) list.get(i - 1).get("OUT_TRUST_ACCT");
						String feeDebitAcct_back = (String) list.get(i - 1).get("FEE_DEBIT_ACCT");
						String tradeDate_back = (null != list.get(i - 1).get("TRADE_DATE") ? sdfYYYYMMDD.format((Timestamp) list.get(i - 1).get("TRADE_DATE")) : "");
						String prospectusType_back = list.get(i - 1).get("PROSPECTUS_TYPE").toString();

						if ((StringUtils.isNotBlank(outTrustAcct_back) && !outTrustAcct_back.equals(list.get(i).get("OUT_TRUST_ACCT"))) ||
							(StringUtils.isNotBlank(feeDebitAcct_back) && !feeDebitAcct_back.equals(list.get(i).get("FEE_DEBIT_ACCT"))) ||
							(StringUtils.isNotBlank(tradeDate_back) && !tradeDate_back.equals((null != list.get(i).get("TRADE_DATE") ? sdfYYYYMMDD.format((Timestamp) list.get(i).get("TRADE_DATE")) : ""))) ||
							(StringUtils.isNotBlank(prospectusType_back) && !prospectusType_back.equals(list.get(i).get("PROSPECTUS_TYPE").toString()))) {

							batchSEQ = getSeq();
							batchNO = 1;
						} else {
							// DBU 電文最多可二筆，OBU 電文只能放一筆
							if ((new BigDecimal(String.valueOf(batchNO))).compareTo(new BigDecimal(isObu? "1": "2")) == 0) {
								batchSEQ = getSeq();
								batchNO = 1;
							} else {
								batchNO++;
							}
						}
					}

					TBSOT_NF_TRANSFER_DPK pk = new TBSOT_NF_TRANSFER_DPK();
					pk.setTRADE_SEQ(inputVO.getTradeSeq());
					pk.setSEQ_NO((BigDecimal) list.get(i).get("SEQ_NO"));

					TBSOT_NF_TRANSFER_DVO vo = new TBSOT_NF_TRANSFER_DVO();
					vo.setcomp_id(pk);
					vo = (TBSOT_NF_TRANSFER_DVO) dam.findByPKey(TBSOT_NF_TRANSFER_DVO.TABLE_UID, vo.getcomp_id());
					vo.setBATCH_SEQ(batchSEQ);
					vo.setBATCH_NO(new BigDecimal(String.valueOf(batchNO)));

					dam.update(vo);

					logger.info("TradeSeq:" + inputVO.getTradeSeq() + ", batchSEQ:" + batchSEQ);
				}
			} else if ("4".equals(inputVO.getTradeType())) { //變更
				sb = new StringBuffer();
				sb.append("DELETE FROM TBSOT_NF_CHANGE_BATCH WHERE TRADE_SEQ = :tradeSEQ");
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				queryCondition.setQueryString(sb.toString());
				dam.exeUpdate(queryCondition);

				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("INSERT INTO TBSOT_NF_CHANGE_BATCH(SERIAL_NO, TRADE_SEQ, SEQ_NO, CHANGE_TYPE, VERSION, CREATETIME, CREATOR) ");
				sb.append("SELECT SQ_TBSOT_NF_CHANGE_BATCH.NEXTVAL, TRADE_SEQ, SEQ_NO, CHANGE_TYPE, 0, SYSDATE, CREATOR FROM ( ");
				sb.append("    SELECT M.*,ROW_NUMBER() OVER (PARTITION BY SEQ_NO || CHANGE_TYPE ORDER BY SEQ_NO ) AS RN FROM ( ");
				sb.append("        SELECT TRADE_SEQ, SEQ_NO, regexp_substr(CHANGE_TYPE,'[^,]+', 1, LEVEL) as CHANGE_TYPE, CREATOR ");
				sb.append("        FROM ( ");
				sb.append("             SELECT TRADE_SEQ,SEQ_NO,CHANGE_TYPE, CREATOR ");
				sb.append("             FROM TBSOT_NF_CHANGE_D ");
				sb.append("             WHERE  TRADE_SEQ = :tradeSEQ ");
				sb.append("        ) ");
				sb.append("        CONNECT BY LEVEL <= LENGTH(REGEXP_REPLACE(CHANGE_TYPE,',',''))/2 ");
				sb.append("    ) M ");
				sb.append(")WHERE RN = 1 ");
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				queryCondition.setQueryString(sb.toString());
				dam.exeUpdate(queryCondition);

				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT D.B_TRUST_ACCT, D.PROSPECTUS_TYPE, B.CHANGE_TYPE, B.SERIAL_NO, D.TRADE_DATE ");
				sb.append("FROM TBSOT_NF_CHANGE_BATCH B ");
				sb.append("INNER JOIN TBSOT_NF_CHANGE_D D ON D.TRADE_SEQ = B.TRADE_SEQ and D.SEQ_NO = B.SEQ_NO ");
				sb.append("WHERE B.TRADE_SEQ = :tradeSEQ ");
				sb.append("ORDER BY D.B_TRUST_ACCT, B.CHANGE_TYPE ");
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> list = dam.exeQuery(queryCondition);

				String batchSEQ = getSeq();
				Integer batchNO = 1; 	//批號流水號

				for (Integer i = 0; i < list.size(); i++) {
					if (i != 0) {
						String bTrustAcct_back = (String) list.get(i - 1).get("B_TRUST_ACCT");
						String changeType_back = (String) list.get(i - 1).get("CHANGE_TYPE");
						String tradeDate_back = (null != list.get(i - 1).get("TRADE_DATE") ? sdfYYYYMMDD.format((Timestamp) list.get(i - 1).get("TRADE_DATE")) : "");
						String prospectusType_back = getString(list.get(i - 1).get("PROSPECTUS_TYPE"));

						if ((StringUtils.isNotBlank(bTrustAcct_back) && !bTrustAcct_back.equals(list.get(i).get("B_TRUST_ACCT"))) ||
							(StringUtils.isNotBlank(changeType_back) && !changeType_back.equals(list.get(i).get("CHANGE_TYPE"))) ||
							(StringUtils.isNotBlank(tradeDate_back) && !tradeDate_back.equals((null != list.get(i).get("TRADE_DATE") ? sdfYYYYMMDD.format((Timestamp) list.get(i).get("TRADE_DATE")) : ""))) ||
							(StringUtils.isNotBlank(prospectusType_back) && !prospectusType_back.equals(getString(list.get(i).get("PROSPECTUS_TYPE"))))) {

							batchSEQ = getSeq();
							batchNO = 1;
						} else {
							// DBU 電文最多可三筆，OBU 電文只能放一筆
							if ((new BigDecimal(String.valueOf(batchNO))).compareTo(new BigDecimal(isObu? "1": "3")) == 0) {
								batchSEQ = getSeq();
								batchNO = 1;
							} else {
								batchNO++;
							}
						}
					}

					sb = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("UPDATE TBSOT_NF_CHANGE_BATCH SET BATCH_SEQ = :batchSEQ, BATCH_NO = :batchNO,MODIFIER=:MODIFIER,LASTUPDATE=SYSDATE WHERE SERIAL_NO = :serialNO ");
					queryCondition.setObject("MODIFIER", modifier);
					queryCondition.setObject("batchSEQ", batchSEQ);
					queryCondition.setObject("batchNO", batchNO);
					queryCondition.setObject("serialNO", list.get(i).get("SERIAL_NO"));
					queryCondition.setQueryString(sb.toString());

					dam.exeUpdate(queryCondition);
				}
			}
		} else if ("ETF".equals(inputVO.getProdType())) { //ETF
			sb = new StringBuffer();
			sb.append("UPDATE TBSOT_ETF_TRADE_D SET BATCH_SEQ = null, BATCH_NO = null,MODIFIER=:MODIFIER,LASTUPDATE=SYSDATE WHERE TRADE_SEQ = :tradeSEQ");
			queryCondition.setObject("MODIFIER", modifier);
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);

			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("SELECT NVL(B.STOCK_CODE, C.STOCK_CODE) AS STOCK_CODE, A.TRUST_CURR_TYPE, A.TRUST_ACCT, A.DEBIT_ACCT, A.CREDIT_ACCT, A.DUE_DATE, A.SELL_TYPE, A.SEQ_NO, A.NARRATOR_ID ");
			sb.append("FROM TBSOT_ETF_TRADE_D A ");
			sb.append("LEFT JOIN TBPRD_ETF B ON B.PRD_ID = A.PROD_ID ");
			sb.append("LEFT JOIN TBPRD_STOCK C ON C.PRD_ID = A.PROD_ID ");
			sb.append("WHERE A.TRADE_SEQ = :tradeSEQ ");
			sb.append("ORDER BY STOCK_CODE, A.TRUST_CURR_TYPE, A.TRUST_ACCT, A.DEBIT_ACCT, A.CREDIT_ACCT, A.DUE_DATE, A.SELL_TYPE ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			String batchSEQ = getSeq();
			Integer batchNO = 1; 	//批號流水號

			for (Integer i = 0; i < list.size(); i++) {
				if (i != 0) {
					String stockCode_back = (String) list.get(i - 1).get("STOCK_CODE");
					String trustCurrType_back = String.valueOf(list.get(i - 1).get("TRUST_CURR_TYPE"));
					String trustAcct_back = (String) list.get(i - 1).get("TRUST_ACCT");
					String debitAcct_back = (String) list.get(i - 1).get("DEBIT_ACCT");
					String creditAcct_back = (String) list.get(i - 1).get("CREDIT_ACCT");
					String dueDate_back = (null != list.get(i - 1).get("DUE_DATE") ? sdfYYYYMMDD.format((Timestamp) list.get(i - 1).get("DUE_DATE")) : "");
					String sellType_back = String.valueOf((Character) list.get(i - 1).get("SELL_TYPE"));
					String narrator_id = (String) list.get(i - 1).get("NARRATOR_ID");

					if ((StringUtils.isNotBlank(stockCode_back) && !stockCode_back.equals(list.get(i).get("STOCK_CODE"))) ||
						(StringUtils.isNotBlank(trustCurrType_back) && !trustCurrType_back.equals(String.valueOf(list.get(i).get("TRUST_CURR_TYPE")))) ||
						(StringUtils.isNotBlank(trustAcct_back) && !trustAcct_back.equals(list.get(i).get("TRUST_ACCT"))) ||
						(StringUtils.isNotBlank(debitAcct_back) && !debitAcct_back.equals(list.get(i).get("DEBIT_ACCT"))) ||
						(StringUtils.isNotBlank(creditAcct_back) && !creditAcct_back.equals(list.get(i).get("CREDIT_ACCT"))) ||
						(StringUtils.isNotBlank(dueDate_back) && !dueDate_back.equals((null != list.get(i).get("DUE_DATE") ? sdfYYYYMMDD.format((Timestamp) list.get(i).get("DUE_DATE")) : ""))) ||
						(StringUtils.isNotBlank(sellType_back) && !sellType_back.equals(String.valueOf((Character)list.get(i).get("SELL_TYPE"))) ||
						(StringUtils.isNotBlank(narrator_id)) && !narrator_id.equals(list.get(i).get("NARRATOR_ID")))) {

						batchSEQ = getSeq();
						batchNO = 1;
					} else {
						if ((new BigDecimal(String.valueOf(batchNO))).compareTo(new BigDecimal("2")) == 0) {
							batchSEQ = getSeq();
							batchNO = 1;
						} else {
							batchNO++;
						}
					}
				}

				TBSOT_ETF_TRADE_DPK pk = new TBSOT_ETF_TRADE_DPK();
				pk.setTRADE_SEQ(inputVO.getTradeSeq());
				pk.setSEQ_NO((BigDecimal) list.get(i).get("SEQ_NO"));

				TBSOT_ETF_TRADE_DVO vo = new TBSOT_ETF_TRADE_DVO();
				vo.setcomp_id(pk);
				vo = (TBSOT_ETF_TRADE_DVO) dam.findByPKey(TBSOT_ETF_TRADE_DVO.TABLE_UID, vo.getcomp_id());
				vo.setBATCH_SEQ(batchSEQ);
				vo.setBATCH_NO(new BigDecimal(String.valueOf(batchNO)));

				dam.update(vo);

				logger.info("TradeSeq:" + inputVO.getTradeSeq() + ", batchSEQ:" + batchSEQ);
			}
		} else if ("BN".equals(inputVO.getProdType())) { //債券
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("SELECT BATCH_SEQ ");
			sb.append("FROM TBSOT_BN_TRADE_D ");
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (StringUtils.isNotBlank(list.get(0).get("BATCH_SEQ") == null ? "" : list.get(0).get("BATCH_SEQ").toString())) {
				throw new JBranchException("ehl_01_SOT_013");
			} else {
				sb = new StringBuffer();
				sb.append(" UPDATE TBSOT_BN_TRADE_D ");
				sb.append(" SET BATCH_SEQ = :batchSEQ ,MODIFIER=:MODIFIER,LASTUPDATE=SYSDATE ");
				sb.append(" WHERE TRADE_SEQ = :tradeSEQ ");
				queryCondition.setObject("MODIFIER", modifier);
				queryCondition.setObject("batchSEQ", getSeq());
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				queryCondition.setQueryString(sb.toString());

				dam.exeUpdate(queryCondition);
			}
		} else if ("SI".equals(inputVO.getProdType())) { //SI
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("SELECT BATCH_SEQ ");
			sb.append("FROM TBSOT_SI_TRADE_D ");
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (StringUtils.isNotBlank(list.get(0).get("BATCH_SEQ") == null ? "" : list.get(0).get("BATCH_SEQ").toString())) {
				throw new JBranchException("ehl_01_SOT_013");
			} else {
				sb = new StringBuffer();
				sb.append("UPDATE TBSOT_SI_TRADE_D ");
				sb.append("SET BATCH_SEQ = :batchSEQ,MODIFIER=:MODIFIER,LASTUPDATE=SYSDATE ");
				sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
				queryCondition.setObject("MODIFIER", modifier);
				queryCondition.setObject("batchSEQ", getSeq(inputVO.getTradeSeq()));
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				queryCondition.setQueryString(sb.toString());

				dam.exeUpdate(queryCondition);
			}
		} else if ("SN".equals(inputVO.getProdType())) { //SN
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("SELECT BATCH_SEQ ");
			sb.append("FROM TBSOT_SN_TRADE_D ");
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (StringUtils.isNotBlank(list.get(0).get("BATCH_SEQ") == null ? "" : list.get(0).get("BATCH_SEQ").toString())) {
				throw new JBranchException("ehl_01_SOT_013");
			} else {
				sb = new StringBuffer();
				sb.append("UPDATE TBSOT_SN_TRADE_D ");
				sb.append("SET BATCH_SEQ = :batchSEQ ,MODIFIER= :MODIFIER,LASTUPDATE=SYSDATE ");
				sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
				queryCondition.setObject("MODIFIER", modifier);
				queryCondition.setObject("batchSEQ", getSeq());
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				queryCondition.setQueryString(sb.toString());

				dam.exeUpdate(queryCondition);
			}
		} else if ("FCI".equals(inputVO.getProdType())) { //FCI
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("SELECT BATCH_SEQ FROM TBSOT_FCI_TRADE_D WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (StringUtils.isNotBlank(list.get(0).get("BATCH_SEQ") == null ? "" : list.get(0).get("BATCH_SEQ").toString())) {
				throw new JBranchException("ehl_01_SOT_013");
			} else {
				sb = new StringBuffer();
				sb.append("UPDATE TBSOT_FCI_TRADE_D ");
				sb.append("SET BATCH_SEQ = :batchSEQ, MODIFIER=:MODIFIER,LASTUPDATE=SYSDATE ");
				sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
				queryCondition.setObject("MODIFIER", modifier);
				queryCondition.setObject("batchSEQ", getFCISeq(inputVO.getTradeSeq()));
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				queryCondition.setQueryString(sb.toString());

				dam.exeUpdate(queryCondition);
			}
		} else if ("8".equals(inputVO.getProdType())) { //基金動態鎖利
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			if(inputVO.getTradeType().matches("1|5|2")) {
				String tableName = "";
				if("1".equals(inputVO.getTradeType())) tableName = "TBSOT_NF_PURCHASE_DYNA"; //單筆申購
				if("5".equals(inputVO.getTradeType())) tableName = "TBSOT_NF_RAISE_AMT_DYNA"; //母基金加碼
				if("2".equals(inputVO.getTradeType())) tableName = "TBSOT_NF_REDEEM_DYNA"; //贖回
				
				queryCondition.setQueryString("SELECT BATCH_SEQ FROM " + tableName + " WHERE TRADE_SEQ = :tradeSEQ ");
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (StringUtils.isNotBlank(ObjectUtils.toString(list.get(0).get("BATCH_SEQ")))) {
					throw new JBranchException("ehl_01_SOT_013");
				} else {
					sb = new StringBuffer();
					sb.append("UPDATE " + tableName + " ");
					sb.append("SET BATCH_SEQ = :batchSEQ, MODIFIER=:MODIFIER, LASTUPDATE=SYSDATE ");
					sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
					queryCondition.setObject("MODIFIER", modifier);
					queryCondition.setObject("batchSEQ", getSeq());
					queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
					queryCondition.setQueryString(sb.toString());
	
					dam.exeUpdate(queryCondition);
				}
			} else if(inputVO.getTradeType().matches("3")) { //基金動態鎖利轉換
				queryCondition.setQueryString("SELECT * FROM TBSOT_NF_TRANSFER_DYNA WHERE TRADE_SEQ = :tradeSEQ ");
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if(StringUtils.equals("1", ObjectUtils.toString(list.get(0).get("TRANSFER_TYPE")))) {
					//母基金轉換
					if(StringUtils.isNotBlank(ObjectUtils.toString(list.get(0).get("BATCH_SEQ")))) {
						//若已有批號丟錯誤訊息
						throw new JBranchException("ehl_01_SOT_013");
					} else {
						sb = new StringBuffer();
						sb.append("UPDATE TBSOT_NF_TRANSFER_DYNA ");
						sb.append("SET BATCH_SEQ = :batchSEQ, MODIFIER=:MODIFIER, LASTUPDATE=SYSDATE ");
						sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
						queryCondition.setObject("MODIFIER", modifier);
						queryCondition.setObject("batchSEQ", getSeq());
						queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
						queryCondition.setQueryString(sb.toString());
		
						dam.exeUpdate(queryCondition);
					}
				} else {
					//子基金轉回母基金
					for(int i=1; i<=5; i++) {
						String cName = "_C" + Integer.toString(i);
						String inProdCYN = ObjectUtils.toString(list.get(0).get("IN_PROD" + cName + "_YN")); //轉入子基金
						String batchSeqC = ObjectUtils.toString(list.get(0).get("BATCH_SEQ" + cName)); //子基金批號
						if(StringUtils.equals("Y", inProdCYN)) { //有轉入子基金
							if(StringUtils.isNotBlank(batchSeqC)) {
								//若已有批號丟錯誤訊息
								throw new JBranchException("ehl_01_SOT_013");
							} else {
								sb = new StringBuffer();
								queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
								sb.append("UPDATE TBSOT_NF_TRANSFER_DYNA ");
								sb.append("SET BATCH_SEQ" + cName + " = :batchSEQ, MODIFIER=:MODIFIER, LASTUPDATE=SYSDATE ");
								sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
								queryCondition.setObject("MODIFIER", modifier);
								queryCondition.setObject("batchSEQ", getSeq());
								queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
								queryCondition.setQueryString(sb.toString());
				
								dam.exeUpdate(queryCondition);
							}
						}
					}
				}
			} else if(inputVO.getTradeType().matches("4")) { //基金動態鎖利事件變更
				queryCondition.setQueryString("SELECT * FROM TBSOT_NF_CHANGE_DYNA WHERE TRADE_SEQ = :tradeSEQ ");
				queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if(StringUtils.isNotBlank(ObjectUtils.toString(list.get(0).get("BATCH_SEQ_STATUS"))) ||
						StringUtils.isNotBlank(ObjectUtils.toString(list.get(0).get("BATCH_SEQ_AMOUNT"))) ||
						StringUtils.isNotBlank(ObjectUtils.toString(list.get(0).get("BATCH_SEQ_TRANSDATE"))) ||
						StringUtils.isNotBlank(ObjectUtils.toString(list.get(0).get("BATCH_SEQ_ADDPROD")))) {
					//若已有批號丟錯誤訊息
					throw new JBranchException("ehl_01_SOT_013");
				}
				//各變更事項為一批號
				for(int i=1; i<=4; i++) {
					String batchSeqName = "";
					if(i == 1 && StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("CHG_STATUS_YN")))) batchSeqName = "BATCH_SEQ_STATUS"; //變更事項_子基金扣款狀態
					if(i == 2 && StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("CHG_AMOUNT_YN")))) batchSeqName = "BATCH_SEQ_AMOUNT"; //變更事項_子基金轉換金額
					if(i == 3 && StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("CHG_TRANSDATE_YN")))) batchSeqName = "BATCH_SEQ_TRANSDATE"; //變更事項_每月扣款日期
					if(i == 4 && StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("CHG_ADDPROD_YN")))) batchSeqName = "BATCH_SEQ_ADDPROD"; //變更事項_新增子基金
					
					if(StringUtils.isNotBlank(batchSeqName)) {
						sb = new StringBuffer();
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append("UPDATE TBSOT_NF_CHANGE_DYNA ");
						sb.append("SET " + batchSeqName + " = :batchSEQ, MODIFIER=:MODIFIER, LASTUPDATE=SYSDATE ");
						sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
						queryCondition.setObject("MODIFIER", modifier);
						queryCondition.setObject("batchSEQ", getSeq());
						queryCondition.setObject("tradeSEQ", inputVO.getTradeSeq());
						queryCondition.setQueryString(sb.toString());
		
						dam.exeUpdate(queryCondition);
					}
				}
			}
		}

		return null;
	}

	/**
	 * 取得批號
	 * SI商品   	16WMSD03011608020001   (暫定)
	 * 		 	16WMSD0301 : 產品
	 * 		 	160802     :  西元年月日後六碼
	 * 			0001       : 當日流水號
	 * @param tradeSeq
	 * @return
	 * @throws JBranchException
	 */
	private String getSeq(String tradeSeq) throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT PROD_ID FROM TBSOT_SI_TRADE_D WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", tradeSeq);
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		String prodId = "";
		if(!CollectionUtils.isEmpty(list) && list.size() > 0) {
			prodId = (String) list.get(0).get("PROD_ID");
		}

		return prodId + getBatchSeqDateString(false) + getSeqNum("SOT712", "0000", new java.sql.Timestamp(System.currentTimeMillis()), 1, new Long("0000"), "y", new Long("0"));
	}
	
	/**
	 * 取得批號
	 * FCI商品   	USD1608020001   (暫定)
	 * 		 	USD: 幣別
	 * 		 	160802:  西元年月日後六碼
	 * 			0001: 當日流水號
	 * @param tradeSeq
	 * @return
	 * @throws JBranchException
	 */
	private String getFCISeq(String tradeSeq) throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setObject("tradeSEQ", tradeSeq);
		queryCondition.setQueryString("SELECT PROD_CURR FROM TBSOT_FCI_TRADE_D WHERE TRADE_SEQ = :tradeSEQ ");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		String prodCurr = "";
		if(!CollectionUtils.isEmpty(list) && list.size() > 0) {
			prodCurr = ObjectUtils.toString(list.get(0).get("PROD_CURR"));
		}

		return prodCurr + getBatchSeqDateString(false) + getSeqNum("SOT712", "0000", new java.sql.Timestamp(System.currentTimeMillis()), 1, new Long("0000"), "y", new Long("0"));
	}

	/**
	 * 取得批號
	 * 基債股，SN同債券 	A0508050001
	 * 				A       : 代表理專系統
	 * 				050802  : 民國年後六碼
	 * 				0001    : 當日流水號
	 * @return
	 * @throws JBranchException
	 */
	public String getSeq() throws JBranchException {
		return "A" + getBatchSeqDateString(true) + getSeqNum("SOT712", "0000", new java.sql.Timestamp(System.currentTimeMillis()), 1, new Long("0000"), "y", new Long("0"));
	}

	private String getBatchSeqDateString(Boolean isChineseYear) throws JBranchException {
		Date date = new Date();
		SimpleDateFormat simple = new SimpleDateFormat("MMdd");
		String date_seqnum = simple.format(date);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		Integer year = isChineseYear ? (c.get(Calendar.YEAR) - 1911) : c.get(Calendar.YEAR);
		String stryear = Integer.toString(year);

		return StringUtils.right((stryear + date_seqnum), 6);
	}

	private String getSeqNum (String TXN_ID, String format, Timestamp timeStamp, Integer minNum, Long maxNum,String status,Long nowNum) throws JBranchException {
		String seqNum = "";
		List<Map<String, Object>> INS_KEYNOList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" select lpad(TBSOT_BATCH_SEQ.NEXTVAL, 4, '0') as NEXTVAL from dual ");
		qc.setQueryString(sb.toString());
		INS_KEYNOList = dam.exeQuery(qc);
		seqNum = INS_KEYNOList.get(0).get("NEXTVAL").toString();

		return seqNum;
	}

	/**
	 * 產生交易序號TRADE_SEQ
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getTradeSEQ(Object body, IPrimitiveMap header) throws JBranchException{
		SOT712OutputVO outputVO = new SOT712OutputVO();

		List<Map<String, Object>> tradeseqList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" select lpad(TBSOT_TRADE_MAIN_SEQ.NEXTVAL, 6, '0') as NEXTVAL from dual ");
		qc.setQueryString(sb.toString());
		tradeseqList = dam.exeQuery(qc);

		outputVO.setTradeSEQ(new SimpleDateFormat("yyyyMMdd").format(new Date()) + tradeseqList.get(0).get("NEXTVAL").toString());
		this.sendRtnObject(outputVO);
	}

	/**
	 * 產生  交易序號TRADE_SEQ；流水號 SEQ_NO
	 *
	 * @return seqNum
	 * @throws JBranchException
	 */
	public String getnewTrade_SEQNO() throws JBranchException{
		String seqNum = "";
		List<Map<String, Object>> newTrade_SEQNOList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT TBSOT_SEQ_NO.NEXTVAL FROM DUAL ");
		qc.setQueryString(sb.toString());
		newTrade_SEQNOList = dam.exeQuery(qc);
		seqNum = newTrade_SEQNOList.get(0).get("NEXTVAL").toString();

		return seqNum;
	}


	public boolean getIsRecNeeded (Object body) {
		SOT712InputVO inputVO = (SOT712InputVO) body;
		boolean isrecNeeded = false;
		//是否為法人
		boolean isCorp = ((inputVO.getCustID().length() >=8 && inputVO.getCustID().length() < 10) ? true : false);
		/*
		 * NF：基金
		 * ETF：海外ETF/股票
		 * BN：債券
		 * SN：SN
		 * SD：SD
		 * FCI：FCI
		 */
		if (StringUtils.equals("NF", inputVO.getProdType())
				|| StringUtils.equals("ETF", inputVO.getProdType())
				|| StringUtils.equals("BN", inputVO.getProdType())) {

			if (isCorp)	//法人不檢核錄音
				isrecNeeded = false;
			else if (StringUtils.equals("Y", inputVO.getCustRemarks()))	//特定客戶都須檢核錄音
				isrecNeeded = true;
			else if (!StringUtils.equals("Y", inputVO.getOvsPrivateYN()) && //非境外私募基金才檢核
					!StringUtils.equals("Y", inputVO.getProfInvestorYN()) && StringUtils.equals("Y", inputVO.getIsFirstTrade()))	//非專投首購，須檢核錄音
				isrecNeeded = true;
			else
				isrecNeeded = false;	//非特定客戶、非首購，不須檢核錄音

		} else if (StringUtils.equals("SN", inputVO.getProdType())) {
			if(isCustomized(inputVO.getProdID(), inputVO.getProdType())) {	//客製化商品
				if (StringUtils.equals("Y", inputVO.getProfInvestorYN()) && isCorp)	//專投法人，不須檢核錄音
					isrecNeeded = false;
				else
					isrecNeeded = true;		//其他需錄音
			} else {	//募集型商品
				if (StringUtils.equals("Y", inputVO.getProfInvestorYN())
						&& (isCorp || !StringUtils.equals("Y", inputVO.getCustRemarks())))	//專投法人或專投非弱勢，不須檢核錄音
					isrecNeeded = false;
				else
					isrecNeeded = true;		//其他需錄音
			}
		} else if (StringUtils.equals("SI", inputVO.getProdType())) {
			//檢查是否為『保本商品』(TBPRD_ SDNPDMP2.F13NPD >= 100)
			if (this.isGuaranteed(inputVO.getProdID())) {
				if(isCustomized(inputVO.getProdID(), inputVO.getProdType())) {	//客製化商品
					if (StringUtils.equals("Y", inputVO.getProfInvestorYN()) &&
						isCorp &&
						!StringUtils.equals("Y", inputVO.getTrustProCorp()))	//專頭法人且註記不為31,32，不須檢核錄音
						isrecNeeded = false;
					else
						isrecNeeded = true;
				} else {
					//募集型商品	若符合下列四項條件中任一項，需要錄音序號
					// 1.非專業投資人
					// 2.專業投資人且為特定客戶
					// 3.專投法人且註記為31 or 32
					// 4.專業自然人(註記為J2, L2)且該筆申購金額 < 等值300萬台幣者
					if (!StringUtils.equals("Y", inputVO.getProfInvestorYN()) ||
						(StringUtils.equals("Y", inputVO.getProfInvestorYN()) &&
						 StringUtils.equals("Y", inputVO.getCustRemarks())) ||
						(StringUtils.equals("Y", inputVO.getProfInvestorYN()) &&
						 isCorp && StringUtils.equals("Y", inputVO.getTrustProCorp())) ||
						(StringUtils.equals("Y", inputVO.getPro1500()) && inputVO.getUnder300()))
						isrecNeeded = true;
					else
						isrecNeeded = false;
				}
			} else {
				// 二、非保本商品適用1.10商品解說錄音紀錄：下列客戶申購商品，如輸入的「商品代號」對應之商品主檔「保本比率」<100，
				//    須具備一筆錄音序號符合PRODID = 商品代號 + PRODTYPE= SDNPP；未符合則顯示警語[]：
				// 1.自然人須錄音 {商品解說錄音}
				// 2.法人客戶須錄音，僅排除專業法人(註記=31, 32以外所有類別)不須錄音。{客戶須知錄音}
				if (StringUtils.equals("Y", inputVO.getProfInvestorYN()) &&
					isCorp &&
					StringUtils.equals("Y", inputVO.getTrustProCorp()))
					isrecNeeded = false;
				else
					isrecNeeded = true;
			}
		} else if (StringUtils.equals("FCI", inputVO.getProdType())) {
			//FCI客戶是專業法人 and 註記 <> 31 and 註記 <> 32錄音序號可以空白, 其餘錄音序號都不可以空白
			isrecNeeded = true;
			if (StringUtils.equals("Y", inputVO.getProfInvestorYN()) &&
					isCorp &&
					StringUtils.equals("N", inputVO.getTrustProCorp())) {
					isrecNeeded = false;
			}
		}
		return isrecNeeded;
	}

	/**
	 * 取得客戶年齡
	 * @return
	 */
	public int getCUST_AGE(Object body) throws JBranchException {
		SOT712InputVO inputVO = (SOT712InputVO) body;
		List<Map<String, Object>> recList = null;
		QueryConditionIF qc = null;
		StringBuilder sb = new StringBuilder();
		int custAge = 0;
		int addDays = inputVO.getAddDays();
		try {
			if (String.valueOf(addDays)== null ){
				addDays=0;
			}

			if (StringUtils.isBlank(inputVO.getCustID())) {
				String error = "getCUST_AGE error empty CustID";
				logger.error(error);
				throw new JBranchException(error);
			}
			dam = this.getDataAccessManager();
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("select FLOOR((SYSDATE - BIRTH_DATE+"+addDays+")/365) as AGE  ");
			sb.append("From TBCRM_CUST_MAST ");
			sb.append("where CUST_ID = :CUST_ID ");
			qc.setObject("CUST_ID", inputVO.getCustID());
			qc.setQueryString(sb.toString());
			recList = dam.exeQuery(qc);
			if (recList.size() != 0 && recList.get(0).get("AGE") != null) {
				custAge = Integer.valueOf(recList.get(0).get("AGE").toString());
			} else {
				String error = "getCUST_AGE error:" + inputVO.getCustID();
				logger.error(error);
				throw new JBranchException(error);
			}
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}

		return custAge;
	}

	/**
	 * 取得SN商品年期
	 * @return
	 */
	public double getSN_CDSC(String prdID) throws JBranchException {
		List<Map<String, Object>> recList = null;
		QueryConditionIF qc = null;
		StringBuilder sb = new StringBuilder();
		double SN_CDSC = 0;

		try {

			dam = this.getDataAccessManager();
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("select CDSC from TBPRD_SNINFO where PRD_ID = :PRD_ID");
			qc.setObject("PRD_ID", prdID);
			qc.setQueryString(sb.toString());
			recList = dam.exeQuery(qc);
			logger.info("getSN_CDSC查出來的結果: " + recList.get(0).get("CDSC"));
			if (recList.size() != 0 && recList.get(0).get("CDSC") != null) {
				SN_CDSC = Double.valueOf(recList.get(0).get("CDSC").toString());
			} else {
				String error = "getSN_CDSC error:" + prdID + " CDSC是null";
				logger.error(error);
			}
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}

		return SN_CDSC;
	}

	private boolean isCustomized(String prodID, String prodType){
		boolean rtnValue = false;
		List<Map<String, Object>> recList = null;
		QueryConditionIF qc = null;
		StringBuilder sb = new StringBuilder();
		try {
			if( StringUtils.equals("SI", prodType)  || StringUtils.equals("SN", prodType) ) {
				dam = this.getDataAccessManager();
				qc=dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("select RECORD_FLAG from TBPRD_").append(prodType);
				sb.append(" where PRD_ID = :prodID ");
				qc.setObject("prodID", prodID);
				qc.setQueryString(sb.toString());
				recList = dam.exeQuery(qc);

				if(CollectionUtils.isNotEmpty(recList)) {
					if(StringUtils.equals("Y", (String) recList.get(0).get("RECORD_FLAG")))
						rtnValue = true;
				}
			}
		} catch(Exception  e) {
			logger.debug(e.getMessage(), e);
		}

		return rtnValue;
	}

	/**
	 * 	判斷是否為『保本型商品』
	 *  ‘0’= 無保本約定型商品
	 *  ‘1’= 保本率 80%( 不含 ) 以下
	 *  ‘2’= 保本率 80%( 含 ) 以上
	 *  ‘3’= 100% 保本型商品"
	 *
	 * **/
	private boolean isGuaranteed(String prodID){
		boolean isGua = false;
		List<Map<String, Object>> recList = null;
		QueryConditionIF qc = null;
		StringBuilder sb = new StringBuilder();
		try {
			dam = this.getDataAccessManager();
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("SELECT CASE WHEN F13NPD >= 100 THEN 'Y' ELSE 'N' END AS IS_GUA FROM TBPRD_SDNPDMP2 WHERE SDPRD = :sdprd");
			qc.setObject("sdprd", prodID);
			qc.setQueryString(sb.toString());
			recList = dam.exeQuery(qc);
			if(CollectionUtils.isNotEmpty(recList) && recList.get(0).get("IS_GUA") != null) {
				if ("Y".equals(recList.get(0).get("IS_GUA").toString())) {
					isGua = true;
				}
			}
		} catch(Exception  e) {
			logger.debug(e.getMessage(), e);
		}
		return isGua;
	}

	/**
	 * 對錯誤訊息:查詢；若是ehl_開頭則翻譯錯誤碼
	 * @param errorMsg
	 * @throws JBranchException
	 */
	public String inquireErrorCode(String errorMsg)
			throws JBranchException {
		return inquireErrorCode(errorMsg,null);
	}
	/**
	 * 對錯誤訊息:查詢；若是ehl_開頭則翻譯錯誤碼
	 * @param errorMsg
	 * @param params: param1^param2
	 * @throws JBranchException
	 */
	public String inquireErrorCode(String errorMsg,String params)
			throws JBranchException {
		if (!StringUtils.startsWith(errorMsg, "ehl_")) {
			return errorMsg;
		} else {
			I18NInputVO I18NInputVO = new I18NInputVO();
			I18NInputVO.setCode(errorMsg);

			I18NInputVO.setLocale("zh-tw");
			I18NInputVO.setCurrentPageIndex(1);
			I18NOutputVO return_VO = new I18NOutputVO();
			dam = this.getDataAccessManager();

			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			String sql_s = "SELECT LOCALE,CODE,TEXT,TYPE,CREATOR,CREATETIME,MODIFIER,LASTUPDATE from TBSYSI18N where 1=1 ";
			if (!StringUtils.isBlank(I18NInputVO.getLocale()))
				sql_s += "and LOCALE = :locale ";
			if (!StringUtils.isBlank(I18NInputVO.getCode()))
				sql_s += "and CODE = :code ";
			if (!StringUtils.isBlank(I18NInputVO.getText()))
				sql_s += "and TEXT like :text ";
			if (!StringUtils.isBlank(I18NInputVO.getType()))
				sql_s += "and TYPE = :type ";
			queryCondition.setQueryString(sql_s);
			if (!StringUtils.isBlank(I18NInputVO.getLocale()))
				queryCondition.setObject("locale", I18NInputVO.getLocale());
			if (!StringUtils.isBlank(I18NInputVO.getCode()))
				queryCondition.setObject("code", I18NInputVO.getCode());
			if (!StringUtils.isBlank(I18NInputVO.getText()))
				queryCondition.setObject("text", "%" + I18NInputVO.getText() + "%");
			if (!StringUtils.isBlank(I18NInputVO.getType()))
				queryCondition.setObject("type", I18NInputVO.getType());
			String errorMessage = errorMsg;
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if(!CollectionUtils.isEmpty(list) && list.size() > 0) {
				errorMessage = (String) list.get(0).get("TEXT");
			}

			if(StringUtils.isNotBlank(params)){
				String[] paramList = StringUtils.split(params, "^");
				for(int i=0;i<paramList.length;i++){
					errorMessage = StringUtils.replace(errorMessage, "{"+i+"}", paramList[i]);
				}
			}

			return errorMessage;
		}
	}


	public void updateBargainStatus(Object body, IPrimitiveMap<Object> header) throws JBranchException {

		SOT712InputVO inputVO = (SOT712InputVO) body;
		SOT712OutputVO outputVO = new SOT712OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		try {
			//基金申購
			StringBuilder sb = new StringBuilder();
			sb.append(" update TBSOT_NF_PURCHASE_D set BARGAIN_STATUS = :bargain_status ");
			sb.append(" where BARGAIN_APPLY_SEQ = :applySeq and FEE_TYPE = 'A' ");
			queryCondition.setObject("applySeq", inputVO.getBargainApplySeq());
			queryCondition.setObject("bargain_status", inputVO.getBargainStatus());
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);

			//基金贖回再申購
			sb = new StringBuilder();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" update TBSOT_NF_REDEEM_D set BARGAIN_STATUS = '2' ");
			sb.append(" where BARGAIN_APPLY_SEQ = :applySeq and FEE_TYPE = 'A' ");
			queryCondition.setObject("applySeq", inputVO.getBargainApplySeq());
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);

			//海外ETF/股票申購
			sb = new StringBuilder();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" update TBSOT_ETF_TRADE_D set BARGAIN_STATUS = '2' ");
			sb.append(" where BARGAIN_APPLY_SEQ = :applySeq and FEE_TYPE = 'A' ");
			queryCondition.setObject("applySeq", inputVO.getBargainApplySeq());
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);

			//基金動態鎖利申購
			sb = new StringBuilder();
			sb.append(" update TBSOT_NF_PURCHASE_DYNA set BARGAIN_STATUS = :bargain_status ");
			sb.append(" where BARGAIN_APPLY_SEQ = :applySeq and FEE_TYPE = 'A' ");
			queryCondition.setObject("applySeq", inputVO.getBargainApplySeq());
			queryCondition.setObject("bargain_status", inputVO.getBargainStatus());
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);

			//基金動態鎖利母基金加碼
			sb = new StringBuilder();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" update TBSOT_NF_RAISE_AMT_DYNA set BARGAIN_STATUS = '2' ");
			sb.append(" where BARGAIN_APPLY_SEQ = :applySeq and FEE_TYPE = 'A' ");
			queryCondition.setObject("applySeq", inputVO.getBargainApplySeq());
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);
			
			//取得傳入議價編號的所有下單交易序號
			List<Map<String, Object>> seqList = new ArrayList<Map<String,Object>>();
			sb = new StringBuilder();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" select distinct 'P' as TYPE, TRADE_SEQ from TBSOT_NF_PURCHASE_D ");
			sb.append(" where BARGAIN_APPLY_SEQ = :applySeq1 and FEE_TYPE = 'A' ");
			queryCondition.setObject("applySeq1", inputVO.getBargainApplySeq());
			sb.append(" UNION ");
			sb.append(" select distinct 'R' as TYPE, TRADE_SEQ from TBSOT_NF_REDEEM_D ");
			sb.append(" where BARGAIN_APPLY_SEQ = :applySeq2 and FEE_TYPE = 'A' ");
			queryCondition.setObject("applySeq2", inputVO.getBargainApplySeq());
			sb.append(" UNION ");
			sb.append(" select distinct 'E' as TYPE, TRADE_SEQ from TBSOT_ETF_TRADE_D ");
			sb.append(" where BARGAIN_APPLY_SEQ = :applySeq3 and FEE_TYPE = 'A' ");
			queryCondition.setObject("applySeq3", inputVO.getBargainApplySeq());
			sb.append(" UNION ");
			sb.append(" select distinct 'DP' as TYPE, TRADE_SEQ from TBSOT_NF_PURCHASE_DYNA ");//基金動態鎖利申購
			sb.append(" where BARGAIN_APPLY_SEQ = :applySeq4 and FEE_TYPE = 'A' ");
			queryCondition.setObject("applySeq4", inputVO.getBargainApplySeq());
			sb.append(" UNION ");
			sb.append(" select distinct 'DR' as TYPE, TRADE_SEQ from TBSOT_NF_RAISE_AMT_DYNA ");//基金動態鎖利母基金加碼
			sb.append(" where BARGAIN_APPLY_SEQ = :applySeq5 and FEE_TYPE = 'A' ");
			queryCondition.setObject("applySeq5", inputVO.getBargainApplySeq());
			queryCondition.setQueryString(sb.toString());
			seqList = dam.exeQuery(queryCondition);

			//明細檔資料都議價完成，將主檔議價狀態資料壓為：2：議價完成
			if(seqList.size()>0){
				for(int idx = 0;idx<seqList.size();idx++){
					if("P".equals(seqList.get(idx).get("TYPE").toString())){
						sb = new StringBuilder();
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append(" update TBSOT_TRADE_MAIN set BARGAIN_FEE_FLAG = '2' ");
						sb.append(" where TRADE_SEQ = :TRADE_SEQ1 ");
						queryCondition.setObject("TRADE_SEQ1", seqList.get(idx).get("TRADE_SEQ").toString());
						sb.append(" and not exists (select 1 from TBSOT_NF_PURCHASE_D ");
						sb.append(" where TRADE_SEQ = :TRADE_SEQ2 and FEE_TYPE = 'A' and BARGAIN_STATUS <> '2') ");
						queryCondition.setObject("TRADE_SEQ2", seqList.get(idx).get("TRADE_SEQ").toString());
						queryCondition.setQueryString(sb.toString());
						dam.exeUpdate(queryCondition);
					}

					if("R".equals(seqList.get(idx).get("TYPE").toString())){
						sb = new StringBuilder();
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append(" update TBSOT_TRADE_MAIN set BARGAIN_FEE_FLAG = '2' ");
						sb.append(" where TRADE_SEQ = :TRADE_SEQ1 ");
						queryCondition.setObject("TRADE_SEQ1", seqList.get(idx).get("TRADE_SEQ").toString());
						sb.append(" and not exists (select 1 from TBSOT_NF_REDEEM_D ");
						sb.append(" where TRADE_SEQ = :TRADE_SEQ2 and FEE_TYPE = 'A' and BARGAIN_STATUS <> '2') ");
						queryCondition.setObject("TRADE_SEQ2", seqList.get(idx).get("TRADE_SEQ").toString());
						queryCondition.setQueryString(sb.toString());
						dam.exeUpdate(queryCondition);
					}

					if("E".equals(seqList.get(idx).get("TYPE").toString())){
						sb = new StringBuilder();
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append(" update TBSOT_TRADE_MAIN set BARGAIN_FEE_FLAG = '2' ");
						sb.append(" where TRADE_SEQ = :TRADE_SEQ1 ");
						queryCondition.setObject("TRADE_SEQ1", seqList.get(idx).get("TRADE_SEQ").toString());
						sb.append(" and not exists (select 1 from TBSOT_ETF_TRADE_D ");
						sb.append(" where TRADE_SEQ = :TRADE_SEQ2 and FEE_TYPE = 'A' and BARGAIN_STATUS <> '2') ");
						queryCondition.setObject("TRADE_SEQ2", seqList.get(idx).get("TRADE_SEQ").toString());
						queryCondition.setQueryString(sb.toString());
						dam.exeUpdate(queryCondition);
					}
					
					if(ObjectUtils.toString(seqList.get(idx).get("TYPE")).matches("DP|DR")){ //基金動態鎖利明細檔都只有一筆資料
						sb = new StringBuilder();
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append(" update TBSOT_TRADE_MAIN set BARGAIN_FEE_FLAG = '2' ");
						sb.append(" where TRADE_SEQ = :TRADE_SEQ1 ");
						queryCondition.setObject("TRADE_SEQ1", seqList.get(idx).get("TRADE_SEQ").toString());
						queryCondition.setQueryString(sb.toString());
						dam.exeUpdate(queryCondition);
					}
				}
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	private String getString(Object val){
		if(val == null){
			return "";
		}else{
			return val.toString();
		}
	}

	/**
	 * 根據不同商品與流程case (下單、適配)，依照順序取得相關報表
	 * @param body : PRDFitInputVO 下單適配Input
	 * @param header
	 * @return void
	 * @throws JBranchException
	 * @throws Exception
	 * @see SOT712#checkInputVO(PRDFitInputVO)
	 * @see SOT712#getInfoList(PRDFitInputVO)
	 * @see SOT712#getPdfULst(PRDFitInputVO, String)
	 */
	public void fitToGetPDF(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		PRDFitInputVO inputVO = (PRDFitInputVO) body;
		dam = this.getDataAccessManager();
		//金錢信託應附文件清單
		printRptIdList = new ArrayList<String>();
		int monRptPageNum = 0;

		/*--------------------------------------
		*Step1
		*檢核inputVO是否合乎規範
		*--------------------------------------
		*/
		checkInputVO(inputVO);

		/*
		*--------------------------------------
		*Step2
		*判斷不同的商品類別，依照需求取得客戶相關資訊，判斷其身分是否要列印相關報表 (SOT811自主、SOT813推介)
		*(主機將會把相關資訊放到電文Desc欄位中，在客戶電文SOT701裡做處理，所以只需判斷 是否為專業投資人、與是否簽過推介同意書)
		*2022.09.22 #1298 修改相關邏輯
		*導致65-69歲的特定客戶 DESC可能是Y or N  (原本應該給X)
		*--------------------------------------
		*/
		String proFlag = "";			//專業投資人
		String remark = ""; 			//推介同意書註記
		String custRemark = ""; 		//是否為特定客戶
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		int custAge = sot701.getCustAge(inputVO.getCustId());				//客戶年齡
		boolean custCheck = false;
		if (inputVO.getCaseCode() == 1) { //Case1 下單
			if (inputVO.getPrdType().matches("1|8")) {
				//商品類別為基金/動態鎖利，有印自主聲明書與推介同意書需求
				custCheck = true;
			} else if (StringUtils.equals(inputVO.getPrdType(), "2")
				|| StringUtils.equals(inputVO.getPrdType(), "3")
				|| StringUtils.equals(inputVO.getPrdType(), "5")) {
				if (inputVO.getTradeSubType() == 1) {
					//當商品類別為海外ETF/股票、海外債、SN，且交易類別為"申購"，有印自主聲明書與推介同意書需求
					custCheck = true;
				}
			}
		} else if (inputVO.getCaseCode() == 2) {
			//Case2 適配 ，除了SI商品外，皆有印自主聲明書與推介同意書需求
			if (!StringUtils.equals(inputVO.getPrdType(), "4")){
				custCheck = true;
			}
		}

		if (custCheck) {
			boolean infoCheck = false;      //是否要撈電文
			//先看交易主檔(TBSOT_TRADE_MAIN)有無資料，否則撈電文
			if (StringUtils.isNotBlank(inputVO.getTradeSeq())) {
				TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
				vo = (TBSOT_TRADE_MAINVO)dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSeq());
				if (null != vo) {
					proFlag = vo.getPROF_INVESTOR_YN();
					remark = vo.getPI_REMARK();
					custRemark = vo.getCUST_REMARKS();
				} else {
					infoCheck = true;
				}
			} else {
					infoCheck = true;
			}

			if (infoCheck) {
				//交易主檔(TBSOT_TRADE_MAIN)無資料，撈取電文
				SOT701InputVO inputVO_701 = new SOT701InputVO();
				FP032675DataVO fp032675DataVO = new FP032675DataVO();
				inputVO_701.setCustID(inputVO.getCustId());
				fp032675DataVO = sot701.getFP032675Data(inputVO_701);

				proFlag = fp032675DataVO.getCustProFlag();
				remark = fp032675DataVO.getCustProRemark();   //(源自Desc欄位，主機已把相關資訊設定到Desc)
				custRemark = fp032675DataVO.getCustRemarks();
			}

			//客戶年齡
			SOT712InputVO  sot712InputVO = new SOT712InputVO();
			if (inputVO.getCaseCode() == 1) {
				//case1 下單  (三個營業日內達70歲者)
				sot712InputVO.setAddDays(3);
			} else if (inputVO.getCaseCode() == 2) {
				//case2 適配  (達70歲者)
				sot712InputVO.setAddDays(0);
			}
			sot712InputVO.setCustID(inputVO.getCustId());
			custAge = getCUST_AGE(sot712InputVO);
		}

		/*
		 * --------------------------------------
		 * Step3
		 * 開始產生報表
		 * --------------------------------------
		 * */
		List<String> url_list =  new ArrayList<String>();
		//case1 下單
		if (inputVO.getCaseCode() == 1) {
			if (StringUtils.equals(inputVO.getPrdType(), "4")) {
				//SI只會有一份，並且沒有使用batchSeq作為判斷，只有印單份
				switch (inputVO.getTradeSubType()) {
					case 1 : //申購
						url_list.addAll(getPdfULst(inputVO, "sot809"));

						//SI已於前端檢查是否需要列印
						if(StringUtils.equals("Y", inputVO.getIsPrintSot816()))
							url_list.addAll(getPdfULst(inputVO, "sot816"));		//列印結構型商品交易自主聲明書

						if(StringUtils.equals("Y", inputVO.getIsPrintSot817()))
							url_list.addAll(getPdfULst(inputVO, "sot817"));		//列印結構型商品推介終止同意書
												
						if (!(cbsService.getCBSIDCode(inputVO.getCustId()).matches("21|22|23|29|31|32|39"))
								&& "Y".equals(inputVO.getIsPrintSOT819())) {
							url_list.addAll(getPdfULst(inputVO, "sot819"));			//列印風險預告書 	不是法人&& 要印貸款風險預告書	
						}

						break;
					case 2 : //贖回
						url_list.addAll(getPdfULst(inputVO, "sot810"));
						break;
				}
				
				// SI 不保本聲明書套表
				//#2134:申購印，贖回不印
				if(inputVO.getTradeSubType() == 1) {
					List sot821pdf = getPdfULst(inputVO, "sot821");
					if (sot821pdf != null) {
						url_list.addAll(sot821pdf);
					}
				}
			} else if(StringUtils.equals(inputVO.getPrdType(), "FCI")) {
				//FCI申購商品契約書
				url_list.addAll(getPdfULst(inputVO, "sot809FCI"));
				
				//SI已於前端檢查是否需要列印
				if(StringUtils.equals("Y", inputVO.getIsPrintSot816()))
					url_list.addAll(getPdfULst(inputVO, "sot816"));		//列印結構型商品交易自主聲明書

				if(StringUtils.equals("Y", inputVO.getIsPrintSot817()))
					url_list.addAll(getPdfULst(inputVO, "sot817"));		//列印結構型商品推介終止同意書

			} else {
				//取得該交易BatchSeq List
				List<Map<String, Object>> infoList = getInfoList(inputVO);
				//分批處理
				for (Map<String, Object> map : infoList) {
					//設定inputVO的batchSeq，並將此當作參數，傳送至報表
					inputVO.setBatchSeq((String)map.get("BATCH_SEQ"));
					if (StringUtils.equals(inputVO.getPrdType(), "5")) {
						logger.info("#0564測試: 取得PROD_ID");
						inputVO.setPrdId((String)map.get("PROD_ID"));
					}

					if (StringUtils.equals(inputVO.getPrdType(), "1")) {
						//基金種類商品判斷tradeType，印出相關報表
						switch(inputVO.getTradeType()) {
							case 1 ://單筆申購
								url_list.addAll(getPdfULst(inputVO, "sot801"));
								break;
							case 2 ://贖回/贖回再申購
								url_list.addAll(getPdfULst(inputVO, "sot804"));
								break;
							case 3 ://轉換
								url_list.addAll(getPdfULst(inputVO, "sot805"));
								break;
							case 4 ://變更
								url_list.addAll(getPdfULst(inputVO, "sot806"));
								break;
							case 5 ://定期(不)定額申購
								url_list.addAll(getPdfULst(inputVO, "sot802"));
						}

						// 列印費用結構說明書
						prepareAndLoadPrdFundCssFiles(url_list, inputVO);

						//基金交易日期類別為"預約"，則列印報表
						if (StringUtils.equals(map.get("TRADE_DATE_TYPE").toString(), "2")) {
							url_list.addAll(getPdfULst(inputVO, "sot812"));
						}
					} else if(StringUtils.equals(inputVO.getPrdType(), "8")) {
						//動態鎖利
						//基金種類商品判斷tradeType，印出相關報表
						switch(inputVO.getTradeType()) {
							case 1 ://單筆申購
								url_list.addAll(getPdfULst(inputVO, "sot801dyna"));
								break;
							case 2 ://贖回
								url_list.addAll(getPdfULst(inputVO, "sot804dyna"));
								break;
							case 3 ://轉換
								url_list.addAll(getPdfULst(inputVO, "sot805dyna"));
								break;
							case 4 ://變更
								url_list.addAll(getPdfULst(inputVO, "sot806dyna"));
								break;
							case 5 ://母基金加碼
								url_list.addAll(getPdfULst(inputVO, "sot802dyna"));
						}
						//基金動態鎖利交易日期類別為"預約"，則列印報表
						if (StringUtils.equals(map.get("TRADE_DATE_TYPE").toString(), "2")) {
							url_list.addAll(getPdfULst(inputVO, "sot812dyna"));
						}
					} else if (StringUtils.equals(inputVO.getPrdType(), "2")) {
						//海外ETF/股票
						url_list.addAll(getPdfULst(inputVO, "sot807"));

					} else if (StringUtils.equals(inputVO.getPrdType(), "3")
							|| StringUtils.equals(inputVO.getPrdType(), "5") ) {
						//海外債/SN
						url_list.addAll(getPdfULst(inputVO, "sot808"));
					}

					//基金後收型約定書
					if(isBackendInTrade(inputVO)) {
						url_list.addAll(getPdfULst(inputVO, "sot815"));
					}

					//海外債風險預告書
					if(StringUtils.equals(inputVO.getPrdType(), "3") &&
							StringUtils.equals(ObjectUtils.toString(inputVO.getTradeSubType()), "1")) { //海外債申購
						url_list.addAll(getPdfULst(inputVO, "sot822"));

						//65歲以上專投選擇年期較長商品須帶出客戶年齡與金融商品年限申購聲明書
						if (isPrintSOT823(inputVO)) {
							url_list.addAll(getPdfULst(inputVO, "sot823"));
						}
					}

					//特定金錢信託投資國內指數型股票基金產品特別約定事項(單筆/小額申購05系列基金)
					if (isFund05(inputVO)) {
						url_list.addAll(getPdfULst(inputVO, "sot818"));
					}

					//列印風險預告書
					//基金 && 單筆申購 && 不是法人 && (要印貸款風險預告書 || (是金錢信託 && 是預約交易))
					//(海外ETF/股票 || 海外債 || SN) && 申購 && 不是法人&& 要印貸款風險預告書
					//動態鎖利 && 單筆申購 && 不是法人 && 要印貸款風險預告書 
					if (StringUtils.equals(inputVO.getPrdType(), "1") && 1 == inputVO.getTradeType() 
							&& !(cbsService.getCBSIDCode(inputVO.getCustId()).matches("21|22|23|29|31|32|39"))
							&& ("Y".equals(inputVO.getIsPrintSOT819()) || (isMonTrust(inputVO.getTradeSeq()) && StringUtils.equals(map.get("TRADE_DATE_TYPE").toString(), "2")))
						) {  
						url_list.addAll(getPdfULst(inputVO, "sot819"));
						printRptIdList.add("SOT819");
					} else if (Arrays.asList("2","3","5").contains(inputVO.getPrdType()) 
							&& !(cbsService.getCBSIDCode(inputVO.getCustId()).matches("21|22|23|29|31|32|39"))
							&& 1 == inputVO.getTradeSubType() && "Y".equals(inputVO.getIsPrintSOT819())) {
						url_list.addAll(getPdfULst(inputVO, "sot819"));		
						printRptIdList.add("SOT819");
					} else if (StringUtils.equals(inputVO.getPrdType(), "8") && (1 == inputVO.getTradeType() || 5 == inputVO.getTradeType())
							&& !(cbsService.getCBSIDCode(inputVO.getCustId()).matches("21|22|23|29|31|32|39"))
							&& "Y".equals(inputVO.getIsPrintSOT819())) {
						//動態鎖利申購或母基金加碼
						url_list.addAll(getPdfULst(inputVO, "sot819"));
						printRptIdList.add("SOT819");
					}

					if (custCheck) {
						CustHighNetWorthDataVO hnwcData = getHNWCData(inputVO.getCustId()); //客戶高資產註記資料
						//專投或有高資產客戶資格不用印自主聲明書
						if (!StringUtils.equals(proFlag, "Y") && !StringUtils.equals("Y", hnwcData.getValidHnwcYN())) {
							if (StringUtils.equals(remark, "N")){
								//非專投&&未簽推介同意，列印自主聲明書+推介同意書
								if(StringUtils.equals(inputVO.getPrdType(), "1") && inputVO.getTradeType() == 2 && !isRePurchase(inputVO.getTradeSeq())) {
									//基金贖回沒有再申購，"不"須列印自主聲明書
								} else {
									//當caseCode=1(下單時)，基金贖回有再申購時才需列印自主聲明書
									if(StringUtils.equals(inputVO.getPrdType(), "8")) {
										//動態鎖利非贖回才需要
										if(inputVO.getTradeType() != 2) {
											url_list.addAll(getPdfULst(inputVO, "sot811dyna"));
										}
									} else {
										url_list.addAll(getPdfULst(inputVO, "sot811"));
									}
								}
								if(!StringUtils.equals(custRemark, "Y")) { //非特定客戶才印推介同意書
									url_list.addAll(getPdfULst(inputVO, "sot813"));
								}
							} else if (custAge >= 70 && (StringUtils.isBlank(remark) || StringUtils.equals(custRemark, "Y") || StringUtils.equals(remark, "X"))) {
								//非專投&&有簽推介同意&&年齡>=70歲&&特定客戶，印自主聲明書
								if(StringUtils.equals(inputVO.getPrdType(), "1") && inputVO.getTradeType() == 2 && !isRePurchase(inputVO.getTradeSeq())) {
									//基金贖回沒有再申購，"不"須列印自主聲明書
								} else {
									//當caseCode=1(下單時)，基金贖回有再申購時才需列印自主聲明書
									if(StringUtils.equals(inputVO.getPrdType(), "8")) {
										//動態鎖利非贖回才需要
										if(inputVO.getTradeType() != 2) {
											url_list.addAll(getPdfULst(inputVO, "sot811dyna"));
										}
									} else {
										url_list.addAll(getPdfULst(inputVO, "sot811"));
									}
								}
							}
						}
					}
					
					// SN 不保本聲明書套表
					//#2134:申購印，贖回不印
					if ("5".equals(inputVO.getPrdType()) && inputVO.getTradeSubType() == 1) {
						List sot821pdf = getPdfULst(inputVO, "sot821");
						if (sot821pdf != null) {
							url_list.addAll(sot821pdf);
						}
					}

					//金錢信託下單(海外債、基金、SN、ETF)需檢附應附文件清單
					if (inputVO.getPrdType().matches("1|2|3|5")	&& isMonTrust(inputVO.getTradeSeq())) {
						//基金申購或贖回再申購，先將"通路報酬表"加進應附文件中
						if (StringUtils.equals(inputVO.getPrdType(), "1") &&
								(inputVO.getTradeType() == 1 || inputVO.getTradeType() == 5
									|| (inputVO.getTradeType() == 2 && isRePurchase(inputVO.getTradeSeq())))) {
								printRptIdList.add("SOT814");
						}
						logger.info("#0564測試: SN有走進金錢信託下單需檢附應付文件清單");
						genMonTrustReport(inputVO, url_list, monRptPageNum, proFlag);
						printRptIdList = new ArrayList<String>();
						monRptPageNum = url_list.size();
					}
				}
			}
			
			//高風險商品，集中度超過規定
			//列印高資產客戶投資產品集中度聲明書
			if(StringUtils.equals("Y", inputVO.getOverCentRateYN())) {
				BigDecimal amtTWD = getPurchseAmtTWD(inputVO); //取得台幣申購金額
				WMSHACRDataVO hmshacrDataVO = getCentRateData(inputVO, amtTWD); //集中度資訊
				inputVO.setHmshacrDataVO(hmshacrDataVO);
				url_list.addAll(getPdfULst(inputVO, "sot824"));
			}
			
		//case2 適配
		} else if (inputVO.getCaseCode() == 2) {
			// 判斷是否需要列印 "後收型基金約定書"
			if (isBackend(inputVO.getPrdId()))
				url_list.addAll(getPdfULst(inputVO, "sot815"));

			//海外債風險預告書
			if(StringUtils.equals(inputVO.getPrdType(), "3")) { //海外債
				url_list.addAll(getPdfULst(inputVO, "sot822"));

				//65歲以上專投選擇年期較長商品須帶出客戶年齡與金融商品年限申購聲明書
				if (isPrintSOT823(inputVO.getCustId(), inputVO.getPrdId())) {
					url_list.addAll(getPdfULst(inputVO, "sot823"));
				}
			}
			
			CustHighNetWorthDataVO hnwcData = getHNWCData(inputVO.getCustId()); //客戶高資產註記資料
			WMSHACRDataVO hmshacrDataVO = null; //集中度資訊
			//高資產客戶，購買高風險商品或SI/SN不保本商品，檢核集中度
			if(StringUtils.equals("Y", hnwcData.getValidHnwcYN()) && (StringUtils.equals("Y", inputVO.getHnwcBuy()) || !isRateGuaranteed(inputVO))) {
				hmshacrDataVO = getCentRateData(inputVO, BigDecimal.ZERO);
			}
			if(hmshacrDataVO != null && !StringUtils.equals("Y", hmshacrDataVO.getVALIDATE_YN())) {
				//高風險商品或不保本商品，集中度超過規定
				//列印高資產客戶投資產品集中度聲明書
				inputVO.setHmshacrDataVO(hmshacrDataVO);
				url_list.addAll(getPdfULst(inputVO, "sot824"));
			} else {
				//有列印集中度聲明書的，就不用再檢核推介同意書&自主聲明書
				if (custCheck) {
					//專投或有高資產客戶資格不用印自主聲明書與推介同意書
					if (!StringUtils.equals(proFlag, "Y") && !StringUtils.equals("Y", hnwcData.getValidHnwcYN())) {
						if (StringUtils.equals(remark, "N")){
							//非專投&&未簽推介同意，列印自主聲明書+推介同意書
							if(StringUtils.equals(inputVO.getPrdType(), "8")) {
								url_list.addAll(getPdfULst(inputVO, "sot811dyna")); //動態鎖利
							} else {
								url_list.addAll(getPdfULst(inputVO, "sot811"));
							}
							if(!StringUtils.equals(custRemark, "Y")) { //非特定客戶才印推介同意書
								url_list.addAll(getPdfULst(inputVO, "sot813"));
							}
						} else if (custAge >= 70 && (StringUtils.isBlank(remark) || StringUtils.equals(custRemark, "Y") || StringUtils.equals(remark, "X"))) {
							//非專投&&有簽推介同意&&年齡>=70歲&&特定客戶，印自主聲明書
							if(StringUtils.equals(inputVO.getPrdType(), "8")) {
								url_list.addAll(getPdfULst(inputVO, "sot811dyna")); //動態鎖利
							} else {
								url_list.addAll(getPdfULst(inputVO, "sot811"));
							}
						}
					}
				}
			}

			//SI已於前端檢查是否需要列印
			if (StringUtils.equals(inputVO.getPrdType(), "4")) {	//SI
				if(StringUtils.equals("Y", inputVO.getIsPrintSot816()))
					url_list.addAll(getPdfULst(inputVO, "sot816"));		//列印結構型商品交易自主聲明書

				if(StringUtils.equals("Y", inputVO.getIsPrintSot817()))
					url_list.addAll(getPdfULst(inputVO, "sot817"));		//列印結構型商品推介終止同意書
			}

			//基金種類商品，列印費用結構說明書
			if (StringUtils.equals(inputVO.getPrdType(), "1")) {    //基金
				loadPrdFundCssFile(url_list, inputVO.getPrdId()); //列印費用結構說明書
			}
			
			/*
			 * 1695 貸款風險預告書列印 
			 * 1695 2023.11.20 全商品要有貸款註記才印貸款風險預告書
			 */
			if("Y".equals(inputVO.getIsPrintSOT819()) 
					&& StringUtils.isNotBlank(inputVO.getCustId()) 
					&& !(cbsService.getCBSIDCode(inputVO.getCustId()).matches("21|22|23|29|31|32|39"))
					&& "Y".equals(sot701.getFlagNumber(inputVO.getCustId(),cbsService.getCBSIDCode(inputVO.getCustId())))) {
				url_list.addAll(getPdfULst(inputVO, "sot819"));		//列印貸款風險預告書
			}

			//當執行05系列富邦ETF基金純適配時，系統自動套表帶出『特定金錢信託投資國內指數股票型基金(ETF)產品特別約定事項』
			if ("05".equals(inputVO.getPrdId().substring(0, 2))) {
				url_list.addAll(getPdfULst(inputVO, "sot818"));
			}
		}

		//下單與適配，基金皆須列印通路報酬揭露表
		boolean mfdCheck = false;
		if (StringUtils.equals(inputVO.getPrdType(), "1")) {
			if (inputVO.getCaseCode() == 1 && inputVO.getTradeType() == 4) {
				//當caseCode=1(下單時)，其中基金變更只有在變更標的時(AX,X7)，需印報表
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();

				sql.append("select count(*) as COUNT from TBSOT_NF_CHANGE_D ");
				sql.append("where TRADE_SEQ = :trade_seq ");
				sql.append("and (instr(CHANGE_TYPE, 'AX') > 0 or instr(CHANGE_TYPE, 'X7') > 0) ");
				queryCondition.setQueryString(sql.toString());
				queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);

				if (Integer.valueOf(list.get(0).get("COUNT").toString()) > 0) {
					mfdCheck = true;
				}
			} else if(inputVO.getCaseCode() == 1 && inputVO.getTradeType() == 2) {
				//當caseCode=1(下單時)，基金贖回有再申購時才需列印通路報酬揭露表
				if(isRePurchase(inputVO.getTradeSeq())) {
					mfdCheck = true;
				}
			} else {
				mfdCheck = true;
			}

			if (mfdCheck) {
				url_list.addAll(getPdfULst(inputVO, "sot814"));
			}
		} else if (StringUtils.equals(inputVO.getPrdType(), "8")) {
			if(inputVO.getCaseCode() == 2) {
				//動態鎖利適配
				url_list.addAll(getPdfULst(inputVO, "sot814dyna"));
			} else if(inputVO.getCaseCode() == 1) {
				//動態鎖利下單
				if (inputVO.getTradeType() == 1 || inputVO.getTradeType() == 5 || inputVO.getTradeType() == 3 
						|| (inputVO.getTradeType() == 4 && StringUtils.isNotBlank(inputVO.getBatchSeq()))) {
					//申購或母基金加碼或轉換或事件變更有增加子基金時
					url_list.addAll(getPdfULst(inputVO, "sot814dyna"));
				}
			}
		}

		/*
		*--------------------------------------
		*Step4
		*將所有報表Merge起來，參數須傳入true(雙面列印需求)
		*下單所產出的報表，必須存入TBSOT_TRADE_REPORT
		*--------------------------------------
		*/
		// # 1506 所有商品皆使用新分頁開啟並進行加密
		if (url_list.size() > 0) {
			String reportURL = "";
			//#1317
			List<String> tempList = new ArrayList();

				for(String str : url_list) {
					if(!tempList.contains(str)) {
						tempList.add(str);
					}
				}
				if(url_list.size() < tempList.size()){
					url_list = tempList;
				}

			reportURL = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), true, url_list));

			notifyClientViewDoc(reportURL, "pdf");

			//將相關報表資訊儲存至 TBSOT_TRADE_REPORT
			if (inputVO.getCaseCode() == 1) {
				String serverPath = (String) SysInfo.getInfoValue(SystemVariableConsts.SERVER_PATH);
				byte[] reportData = Files.readAllBytes(new File(serverPath, reportURL).toPath());
				TBSOT_TRADE_REPORTVO rvo = new TBSOT_TRADE_REPORTVO();
				
				if(StringUtils.equals(inputVO.getPrdType(), "FCI")) { 
					//FCI可以重新產生報表
					rvo = (TBSOT_TRADE_REPORTVO)dam.findByPKey(TBSOT_TRADE_REPORTVO.TABLE_UID, inputVO.getTradeSeq());
					if(rvo != null) {
						rvo.setREPORT_FILE(ObjectUtil.byteArrToBlob(reportData));
						dam.update(rvo);
					} else {
						rvo = new TBSOT_TRADE_REPORTVO();
						rvo.setTRADE_SEQ(inputVO.getTradeSeq());
						rvo.setREPORT_FILE(ObjectUtil.byteArrToBlob(reportData));
						dam.create(rvo);
					}
				} else {
					rvo = new TBSOT_TRADE_REPORTVO();
					rvo.setTRADE_SEQ(inputVO.getTradeSeq());
					rvo.setREPORT_FILE(ObjectUtil.byteArrToBlob(reportData));
					dam.create(rvo);
				}
			}
		}

		this.sendRtnObject(null);
	}

	/***
	 * 是否列印客戶年齡與金融商品年限申購聲明書
	 * 65歲以上專投選擇年期較長商品，回傳true否則false
	 * 年期較長的定義是客戶年齡+商品剩餘年期大於等於80
	 * @return
	 * @throws JBranchException
	 * @throws DAOException
	 */
	private Boolean isPrintSOT823(PRDFitInputVO inputVO) throws DAOException, JBranchException {
		Map param = new HashMap();
		param.put("TRADE_SEQ", inputVO.getTradeSeq());

		StringBuilder sb = new StringBuilder();
		sb.append("select 1 ");
        sb.append(" from TBSOT_BN_TRADE_D D ");
        sb.append(" left join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
        sb.append(" left join TBCRM_CUST_MAST C on C.CUST_ID = M.CUST_ID ");
        sb.append(" left join TBPRD_BOND P on P.PRD_ID = D.PROD_ID ");
        sb.append("where D.TRADE_SEQ = :TRADE_SEQ ");
        sb.append(" AND ROUND(MONTHS_BETWEEN(SYSDATE, C.BIRTH_DATE)/12, 2) >= 65 ");
        sb.append(" AND (ROUND(MONTHS_BETWEEN(SYSDATE, C.BIRTH_DATE)/12, 2) + ROUND((P.DATE_OF_MATURITY - SYSDATE) / 365, 2)) >= 80 ");

		return CollectionUtils.isNotEmpty(exeQueryForMap(sb.toString(), param));
	}

	/***
	 * 是否列印客戶年齡與金融商品年限申購聲明書
	 * 65歲以上專投選擇年期較長商品，回傳true否則false
	 * 年期較長的定義是客戶年齡+商品剩餘年期大於等於80
	 * @return
	 * @throws JBranchException
	 * @throws DAOException
	 */
	private Boolean isPrintSOT823(String custId, String prdId) throws DAOException, JBranchException {
		Map param = new HashMap();
		param.put("custId", custId);
		param.put("prdId", prdId);

		StringBuilder sb = new StringBuilder();
		sb.append("select 1 ");
        sb.append(" FROM TBCRM_CUST_MAST C, TBPRD_BOND P ");
        sb.append(" WHERE C.CUST_ID = :custId AND P.PRD_ID = :prdId ");
        sb.append(" AND ROUND(MONTHS_BETWEEN(SYSDATE, C.BIRTH_DATE)/12, 2) >= 65 ");
        sb.append(" AND (ROUND(MONTHS_BETWEEN(SYSDATE, C.BIRTH_DATE)/12, 2) + ROUND((P.DATE_OF_MATURITY - SYSDATE) / 365, 2)) >= 80 ");

		return CollectionUtils.isNotEmpty(exeQueryForMap(sb.toString(), param));
	}

	/** 產生金錢信託套印表單
	 * @param proFlag
	 * @throws Exception
	 * @throws JBranchException **/
	private void genMonTrustReport(PRDFitInputVO inputVO, List<String> urlList, int newRptPosition, String proFlag) throws JBranchException, Exception {

		//計價商品為南非幣
		if(isCurrencyZAR(inputVO)) {
			printRptIdList.add("D");	//南非幣計價商品交易自主聲明書
		}

//		//基金申購時手續費有議價
//		if(StringUtils.equals(inputVO.getPrdType(), "1") && isNFBargain(inputVO)) {
//			printRptIdList.add("E");	//費率議價核准
//		}

		//ETF、海外債、SN
		if(inputVO.getPrdType().matches("2|3|5")) {
			//海外債、SN：將有列印的表單ID放入List中
			if(inputVO.getPrdType().matches("3|5") && inputVO.getTradeSubType() == 1) {
				printRptIdList.add("A");	//產品說明書
			}

			//海外債、SN：申購時手續費有議價
			if(inputVO.getPrdType().matches("3|5") && inputVO.getTradeSubType() == 1 && isBondBargain(inputVO.getTradeSeq(), inputVO.getPrdType())) {
				printRptIdList.add("E");	//費率議價核准
			}

			//申購時-小專投購買限專投商品
			if(inputVO.getTradeSubType() == 1){
				logger.info("#0564測試: 申購時-小專投購買限專投商品");
				List<Map<String, Object>> bList = new ArrayList<Map<String, Object>>();
				if(inputVO.getPrdType().matches("3|5")) { //海外債/SN
					bList = chkBondAUM1500List(inputVO.getTradeSeq(), inputVO.getPrdType());
				} else { //ETF
					bList = chkETFAUM1500List(inputVO.getTradeSeq(), inputVO.getPrdType());
				}

				logger.info("#0564測試: PROF_INVESTOR_TYPE " + ObjectUtils.toString(bList.get(0).get("PROF_INVESTOR_TYPE")));
				logger.info("#0564測試: PI_BUY" + ObjectUtils.toString(bList.get(0).get("PI_BUY")));
				logger.info("#0564測試: PURCHASE_AMT_TWD" + new BigDecimal(bList.get(0).get("PURCHASE_AMT_TWD").toString()));
				if(CollectionUtils.isNotEmpty(bList)) {
					if(StringUtils.equals("2", ObjectUtils.toString(bList.get(0).get("PROF_INVESTOR_TYPE")))
							&& StringUtils.equals("Y", ObjectUtils.toString(bList.get(0).get("PI_BUY")))) {
						//小專投購買限專投商品
						BigDecimal purchaseAmt = new BigDecimal(bList.get(0).get("PURCHASE_AMT_TWD").toString());
						BigDecimal w300 = new BigDecimal("3000000");
						if(purchaseAmt.compareTo(w300) == -1) {
							logger.info("#0564測試: 申購時-小專投購買限專投商品  走進C");
							//申購金低於等值TWD300萬
							printRptIdList.add("C");	//本行AUM1500萬資料，近三個月換匯證明
						} else {
							logger.info("#0564測試: 申購時-小專投購買限專投商品 走進B");
							//申購金高於等值TWD300萬
							printRptIdList.add("B");	//本行AUM1500萬資料
						}
					}
				}
			}


			//SN申購－高齡特簽
			//商品年期 > 一年
			//1. 一般客戶年齡 >=70 歲
			//2. 專業客戶年齡 >=80 歲
			if(StringUtils.equals(inputVO.getPrdType(), "5") && inputVO.getTradeSubType() == 1) {

				SOT712InputVO  sot712InputVO = new SOT712InputVO();

				sot712InputVO.setCustID(inputVO.getCustId());
				logger.info("#0564測試 CUST_ID: " + inputVO.getCustId());

				SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
				int custAge = sot701.getCustAge(sot712InputVO.getCustID());

				double CDSC = getSN_CDSC(inputVO.getPrdId());

				logger.info("#0564測試: 走進SN申購高齡特簽邏輯");
				logger.info("#0564測試 AGE: " + custAge);
				logger.info("#0564測試 專投註記: " +  proFlag);
				logger.info("#0564測試: 商品年期: " + CDSC  );
				if(CDSC > 1){
					if(!StringUtils.equals("Y", proFlag) && custAge >= 70){
						logger.info("#0564測試 非專投大於等於70歲");
						printRptIdList.add("F");
					} else if (StringUtils.equals("Y", proFlag) && custAge >= 80){
						logger.info("#0564測試 專投大於等於80歲");
						printRptIdList.add("F");
					}

				}


			}
		}

		//客戶65歲以上，客戶年齡+海外債商品剩餘年期 >= 80，則須:客戶年齡與金融商品年限申購聲明書
		//若為專投，應附文件顯示：客戶年齡與金融商品年限申購聲明書
		//若為非專投，應附文件顯示：主管特案簽核及客戶年齡與金融商品年限申購聲明書
		if(inputVO.getPrdType().matches("3|5")) {
			int sot823Idx = printRptIdList.indexOf("SOT823");
			if(sot823Idx >= 0) {
				if (StringUtils.equals("Y", proFlag)) {
					printRptIdList.set(sot823Idx, "SOT823_2"); //專投:客戶年齡與金融商品年限申購聲明書
				} else {
					printRptIdList.set(sot823Idx, "SOT823_1"); //非專投:主管特案簽核及客戶年齡與金融商品年限申購聲明書
				}
			}
		}

		//信託財產管理運用指示書
		inputVO.setMonTrustRptList(null);
		urlList.addAll(newRptPosition, getPdfULst(inputVO, "sot820"));

		//應附清單應檢附文件
		inputVO.setMonTrustRptList(printRptIdList);
		urlList.addAll(newRptPosition, getPdfULst(inputVO, "sot820"));
	}

	/** 判斷是否為金錢信託下單交易 **/
	private boolean isMonTrust(String tradeSeq) throws JBranchException {
		Map param = new HashMap();
		param.put("TRADE_SEQ", tradeSeq);
		return CollectionUtils.isNotEmpty(exeQueryForMap("select 1 from TBSOT_TRADE_MAIN where NVL(TRUST_TRADE_TYPE, 'S') = 'M' and TRADE_SEQ = :TRADE_SEQ ", param));
	}

	/** 判斷是否為南非幣計價幣別基金 **/
	private boolean isCurrencyZAR(PRDFitInputVO inputVO) throws JBranchException {
		Map<String, String> param = new HashMap();
		param.put("trade_seq", inputVO.getTradeSeq());
		param.put("batch_seq", inputVO.getBatchSeq());

		StringBuilder sb = new StringBuilder();
		if(StringUtils.equals(inputVO.getPrdType(), "1")) {
			sb.append("select 1 from TBSOT_NF_PURCHASE_D D INNER JOIN TBPRD_FUND F on F.PRD_ID = D.PROD_ID ");
			sb.append(" where D.TRADE_SEQ = :trade_seq and D.BATCH_SEQ = :batch_seq AND F.CURRENCY_STD_ID = 'ZAR' ");
		} else if(StringUtils.equals(inputVO.getPrdType(), "3")) {
			sb.append("select 1 from TBSOT_BN_TRADE_D D INNER JOIN TBPRD_BOND F on F.PRD_ID = D.PROD_ID ");
			sb.append(" where D.TRADE_SEQ = :trade_seq and D.BATCH_SEQ = :batch_seq AND F.CURRENCY_STD_ID = 'ZAR' ");
		} else {
			logger.info("#0564: 金錢信託SN南非幣邏輯");
			sb.append("select 1 from TBSOT_SN_TRADE_D D INNER JOIN TBPRD_SN F on F.PRD_ID = D.PROD_ID ");
			sb.append(" where D.TRADE_SEQ = :trade_seq and D.BATCH_SEQ = :batch_seq AND F.CURRENCY_STD_ID = 'ZAR' ");
		}

		return CollectionUtils.isNotEmpty(exeQueryForMap(sb.toString(), param));
	}

	/** 判斷是否海外債SN有議價 **/
	private boolean isBondBargain(String tradeSeq,String prodType) throws JBranchException {

		String tableName = null;
		if(prodType.equals("5")){
			tableName = "TBSOT_SN_TRADE_D";
		}else{
			tableName = "TBSOT_BN_TRADE_D";
		}
		Map param = new HashMap();
		param.put("TRADE_SEQ", tradeSeq);
		return CollectionUtils.isNotEmpty(exeQueryForMap("select 1 from " + tableName + " where TRADE_SEQ = :TRADE_SEQ "
				+ " and COALESCE(FEE_RATE, 0) <> COALESCE(DEFAULT_FEE_RATE, 0) and COALESCE(FEE_RATE, 0) <> COALESCE(BEST_FEE_RATE, 0)", param));
	}

//	/** 判斷是否基金有議價 **/
//	private boolean isNFBargain(PRDFitInputVO inputVO) throws JBranchException {
//		Map<String, String> param = new HashMap();
//		param.put("trade_seq", inputVO.getTradeSeq());
//		param.put("batch_seq", inputVO.getBatchSeq());
//
//		return CollectionUtils.isNotEmpty(exeQueryForMap("select 1 from TBSOT_NF_PURCHASE_D where TRADE_SEQ = :trade_seq AND BATCH_SEQ = :batch_seq"
//				+ " and FEE_TYPE <> 'C' ", param));
//	}

	/***
	 * 判斷是否ETF小專投購買限專投商品
	 * @param tradeSeq
	 * @return
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> chkETFAUM1500List(String tradeSeq, String prodType) throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT M.PROF_INVESTOR_TYPE, (CASE WHEN B.PRD_ID IS NULL THEN NVL(C.PI_BUY, 'N') ELSE NVL(B.PI_BUY, 'N') END) AS PI_BUY, ");
		sb.append(" (NVL(I.BUY_RATE, 1) * NVL(D.UNIT_NUM, 0) * NVL(D.ENTRUST_AMT, 0) * 0.01) AS PURCHASE_AMT_TWD ");
		sb.append(" FROM TBSOT_TRADE_MAIN M ");
		sb.append(" INNER JOIN TBSOT_ETF_TRADE_D D ON D.TRADE_SEQ = M.TRADE_SEQ ");
		sb.append(" LEFT JOIN TBPRD_ETF B ON B.PRD_ID = D.PROD_ID ");
		sb.append(" LEFT JOIN TBPRD_STOCK C ON C.PRD_ID = D.PROD_ID ");
		
		sb.append(" LEFT OUTER JOIN TBPMS_IQ053 I ON I.CUR_COD = D.PROD_CURR AND I.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" WHERE M.TRADE_SEQ = :TRADE_SEQ ");

		queryCondition.setObject("TRADE_SEQ", tradeSeq);
		queryCondition.setQueryString(sb.toString());

		return dam.exeQuery(queryCondition);
	}
	
	/***
	 * 判斷是否海外債/SN小專投購買限專投商品
	 * @param tradeSeq
	 * @return
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> chkBondAUM1500List(String tradeSeq, String prodType) throws JBranchException {

		String tableName = null;
		if(prodType.equals("5")){
			tableName = "TBSOT_SN_TRADE_D";
		}else{
			tableName = "TBSOT_BN_TRADE_D";
		}

		String prodTableName = null;
		if(prodType.equals("5")){
			prodTableName = "TBPRD_SN";
		}else{
			prodTableName = "TBPRD_BOND";
		}

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT M.PROF_INVESTOR_TYPE, NVL(B.PI_BUY, 'N') AS PI_BUY, (NVL(I.BUY_RATE, 1) * NVL(D.PURCHASE_AMT, 0) * NVL(D.ENTRUST_AMT, 0) * 0.01) AS PURCHASE_AMT_TWD ");
		sb.append(" FROM TBSOT_TRADE_MAIN M ");
		sb.append(" INNER JOIN " + tableName + " D ON D.TRADE_SEQ = M.TRADE_SEQ ");
		sb.append(" INNER JOIN " + prodTableName +" B ON B.PRD_ID = D.PROD_ID ");
		sb.append(" LEFT OUTER JOIN TBPMS_IQ053 I ON I.CUR_COD = D.PROD_CURR AND I.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" WHERE M.TRADE_SEQ = :TRADE_SEQ ");

		queryCondition.setObject("TRADE_SEQ", tradeSeq);
		queryCondition.setQueryString(sb.toString());

		return dam.exeQuery(queryCondition);
	}

	/** 判斷是否為贖回再申購 **/
	private boolean isRePurchase(String tradeSeq) throws JBranchException {
		Map param = new HashMap();
		param.put("TRADE_SEQ", tradeSeq);
		return CollectionUtils.isNotEmpty(exeQueryForMap("select 1 from TBSOT_NF_REDEEM_D where TRADE_SEQ = :TRADE_SEQ and IS_RE_PURCHASE = 'Y'", param));
	}

	/** 判斷是否為後收型基金 **/
	private boolean isBackend(String prodId) throws JBranchException {
		Map param = new HashMap();
		param.put("PRD_ID", prodId);
		return CollectionUtils.isNotEmpty(
				exeQueryForMap("select 1 from TBPRD_FUND where IS_BACKEND = 'Y' and PRD_ID = :PRD_ID ", param));
	}

	/**
	 * 同一批號中是否有後收型基金
	 * 列印報表時，只要同一批號交易中其中一筆為後收型基金，則加入[後收型基金申購約定書]
	 * @param inputVO
	 * @return
	 * @throws JBranchException
	 * @throws Exception
	 */
	private boolean isBackendInTrade(PRDFitInputVO inputVO) throws JBranchException, Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		boolean isBackend = false;

		if (StringUtils.equals(inputVO.getPrdType(), "1")) { //基金商品
			switch(inputVO.getTradeType()) {
				case 1 ://單筆申購
					//此交易中是否有後收型基金
					sql = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql.append("select 1 from TBSOT_NF_PURCHASE_D D INNER JOIN TBPRD_FUND F on F.PRD_ID = D.PROD_ID ");
					sql.append(" where D.TRADE_SEQ = :trade_seq and D.BATCH_SEQ = :batch_seq and F.IS_BACKEND = 'Y' ");
					queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
					queryCondition.setObject("batch_seq", inputVO.getBatchSeq());
					queryCondition.setQueryString(sql.toString());
					List<Map<String, Object>> b_list = dam.exeQuery(queryCondition);

					//列印報表時，只要同一批號交易中其中一筆為後收型基金，則加入[後收型基金申購約定書]
					if(CollectionUtils.isNotEmpty(b_list)){
						isBackend = true;
					}

					break;
				case 3 ://轉換
					//此交易中是否有後收型基金
					sql = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql.append("select NVL(F1.IS_BACKEND, 'N') as B1, NVL(F2.IS_BACKEND, 'N') as B2, NVL(F3.IS_BACKEND, 'N') as B3 ");
					sql.append(" from TBSOT_NF_TRANSFER_D D ");
					sql.append(" LEFT OUTER JOIN TBPRD_FUND F1 on F1.PRD_ID = D.IN_PROD_ID_1 ");
					sql.append(" LEFT OUTER JOIN TBPRD_FUND F2 on F2.PRD_ID = D.IN_PROD_ID_2 ");
					sql.append(" LEFT OUTER JOIN TBPRD_FUND F3 on F3.PRD_ID = D.IN_PROD_ID_3 ");
					sql.append(" where D.TRADE_SEQ = :trade_seq and D.BATCH_SEQ = :batch_seq ");
					queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
					queryCondition.setObject("batch_seq", inputVO.getBatchSeq());
					queryCondition.setQueryString(sql.toString());
					List<Map<String, Object>> b_list1 = dam.exeQuery(queryCondition);

					//列印報表時，只要同一批號交易中其中一筆為後收型基金，則加入[後收型基金申購約定書]
					if(CollectionUtils.isNotEmpty(b_list1)) {
						for(Map<String, Object> item :b_list1) {
							if (StringUtils.equals(item.get("B1").toString(), "Y") ||
								StringUtils.equals(item.get("B2").toString(), "Y") ||
								StringUtils.equals(item.get("B3").toString(), "Y") ) {
								isBackend = true;
							}
						}
					}

					break;
			}
		}

		return isBackend;
	}

	/** 基金單筆申購或轉入時須多加判斷所購買的基金是否有上傳費用結構說明書 **/
	private void prepareAndLoadPrdFundCssFiles(List urlList, PRDFitInputVO inputVO) throws Exception {
		Manager manager = Manager.manage(this.getDataAccessManager());

		if(inputVO.getTradeType() == 3){ // 轉換
			List<Map<String, String>> list = manager.append("select TRADE_SEQ, IN_PROD_ID_1, IN_PROD_ID_2, IN_PROD_ID_3 ")
					.append("from TBSOT_NF_TRANSFER_D ")
			 		.append("where TRADE_SEQ = :tradeSeq and BATCH_SEQ = :batchSeq ")
					.append("group by TRADE_SEQ, IN_PROD_ID_1, IN_PROD_ID_2, IN_PROD_ID_3 ")
					.put("tradeSeq", inputVO.getTradeSeq())
					.put("batchSeq", inputVO.getBatchSeq())
					.query();

			for(Map<String, String> map :list){
				for(int i = 1; i < 4; i++) {
					String prdId = map.get("IN_PROD_ID_" + i);
					if (StringUtils.isNotBlank(prdId)) {
						loadPrdFundCssFile(urlList, prdId);
					}
				}
			}
		} else if(inputVO.getTradeType() == 1 ){ // 單筆申購
			List<Map<String, Object>> list = manager.append("select TRADE_SUB_TYPE, PROD_ID from TBSOT_NF_PURCHASE_D ")
			   		.append("where TRADE_SEQ = :tradeSeq and BATCH_SEQ = :batchSeq ")
			   		.append("group by TRADE_SUB_TYPE , PROD_ID ")
					.put("tradeSeq", inputVO.getTradeSeq())
					.put("batchSeq", inputVO.getBatchSeq())
					.query();

			for(Map<String, Object> map :list){
				if("1".equals(map.get("TRADE_SUB_TYPE").toString())){    //單筆申購
					loadPrdFundCssFile(urlList, map.get("PROD_ID").toString());
				}
			}
		} else if(inputVO.getTradeType() == 2 ) { // 贖回/贖回再申購
			List<Map<String, String>> list = manager.append("select TRADE_SEQ, PCH_PROD_ID from TBSOT_NF_REDEEM_D  ")
					.append("where TRADE_SEQ = :tradeSeq and BATCH_SEQ = :batchSeq ")
					.append("group by TRADE_SEQ, PCH_PROD_ID ")
					.put("tradeSeq", inputVO.getTradeSeq())
					.put("batchSeq", inputVO.getBatchSeq())
					.query();

			for (Map<String, String> map : list) {
				if (StringUtils.isNotEmpty(map.get("PCH_PROD_ID"))) {
					loadPrdFundCssFile(urlList, map.get("PCH_PROD_ID"));
				}
			}
		}
	}

	/** 取得結構聲明書 **/
	private void loadPrdFundCssFile(List urlList, String prodId) throws JBranchException, SQLException, IOException {
		try {
			// 從 MoneyDJ 下載後收結構聲明書
			String fundCName = PlatformContext.getBean(PRD110.class).getFundCName(prodId);
			urlList.addAll(new SOT815MoneyDJ().printReport(prodId, fundCName));
		} catch (Exception e) {
			// 如果下載失敗，則使用既有邏輯從 Table 中下載
			logger.info("商品代號：" + prodId + "，下載發生錯誤：" + e.getMessage());
			String url = loadPrdFundCssFileFromDB(prodId);

			if (url == null) return;
			urlList.add(url);
		}
		printRptIdList.add("SOT815_1");	//金錢信託應附文件_境外基金後收型級別費用結構聲明書
	}

	private String loadPrdFundCssFileFromDB(String prodId) throws JBranchException, IOException, SQLException {
		List<Map> cssFile = Manager.manage(this.getDataAccessManager())
				.append("select CSS_FILE from TBPRD_FUND_CSS_FILE where PRD_ID = :id ")
				.put("id", prodId)
				.query();

		if (cssFile.isEmpty()) return null;

		Blob blob = (Blob) cssFile.get(0).get("CSS_FILE");
		if (blob == null) return null;

		String uuid = UUID.randomUUID() + ".pdf";
		try (FileOutputStream fos = new FileOutputStream(new File(AccessContext.tempReportPath, uuid))) {
			fos.write(blob.getBytes(1, (int) blob.length()));
		}
		return "temp" + File.separator + "reports" + File.separator + uuid;
	}

	/**
	 * 同一批號中是否有特定金錢信託投資國內指數型股票基金產品(單筆/小額申購05系列基金)
	 * 列印報表時，只要同一批號交易中其中一筆為05系列基金，則加入[特定金錢信託投資國內指數型股票基金產品特別約定事項]
	 * @param inputVO
	 * @return
	 * @throws JBranchException
	 * @throws Exception
	 */
	private boolean isFund05(PRDFitInputVO inputVO) throws JBranchException, Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		boolean isFund05 = false;

		if (StringUtils.equals(inputVO.getPrdType(), "1")) { //基金商品
			switch(inputVO.getTradeType()) {
				case 1: case 5:	//單筆/小額申購
					//此交易中是否有05系列基金
					sql = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql.append("select 1 from TBSOT_NF_PURCHASE_D D ");
					sql.append(" where D.TRADE_SEQ = :trade_seq and D.BATCH_SEQ = :batch_seq and SUBSTR(D.PROD_ID, 1, 2) = '05' ");
					queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
					queryCondition.setObject("batch_seq", inputVO.getBatchSeq());
					queryCondition.setQueryString(sql.toString());
					List<Map<String, Object>> b_list = dam.exeQuery(queryCondition);

					//列印報表時，只要同一批號交易中其中一筆為05系列基金，則加入[特定金錢信託投資國內指數型股票基金產品特別約定事項]
					if(CollectionUtils.isNotEmpty(b_list)){
						isFund05 = true;
					}

					break;
			}
		}

		return isFund05;
	}

	/**
	 * 取得該次交易的相關資訊
	 * @param inputVO : PRDFitInputVO 下單適配Input
	 * @return void
	 * @throws JBranchException
	 * @throws Exception
	 */
	private List<Map<String, Object>> getInfoList(PRDFitInputVO inputVO) throws JBranchException, Exception{
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		//判斷商品類型，依照不同商品類型，使用不同SQL
		String tableName = "";
		if (StringUtils.equals(inputVO.getPrdType(), "1")) { //基金商品
			if (inputVO.getTradeType() != 4) {
				switch(inputVO.getTradeType()) {
					case 1 ://單筆申購
						tableName = "TBSOT_NF_PURCHASE_D";
						break;
					case 2 ://贖回/贖回再申購
						tableName = "TBSOT_NF_REDEEM_D";
						break;
					case 3 ://轉換
						tableName = "TBSOT_NF_TRANSFER_D";
						break;
					case 5 ://定期(不)定額申購
						tableName = "TBSOT_NF_PURCHASE_D";
						break;
				}

				sql.append("select BATCH_SEQ, TRADE_DATE_TYPE from " + tableName + " ");
				sql.append("where TRADE_SEQ = :trade_seq group by BATCH_SEQ, TRADE_DATE_TYPE order by BATCH_SEQ");

			} else if (inputVO.getTradeType() == 4) { //變更
				sql.append("select B.BATCH_SEQ, D.TRADE_DATE_TYPE from TBSOT_NF_CHANGE_D D ");
				sql.append("inner join TBSOT_NF_CHANGE_BATCH B on B.TRADE_SEQ = D.TRADE_SEQ ");
				sql.append("where D.TRADE_SEQ = :trade_seq ");
				sql.append("group by B.BATCH_SEQ, D.TRADE_DATE_TYPE ");
				sql.append("order by B.BATCH_SEQ ");
			}
		} else if (StringUtils.equals(inputVO.getPrdType(), "8")) { //基金動態鎖利商品
			if (inputVO.getTradeType() != 4) {
				switch(inputVO.getTradeType()) {
					case 1 ://動態鎖利單筆申購
						tableName = "TBSOT_NF_PURCHASE_DYNA";
						break;
					case 2 ://動態鎖利贖回
						tableName = "TBSOT_NF_REDEEM_DYNA";
						break;
					case 3 ://動態鎖利轉換
						tableName = "TBSOT_NF_TRANSFER_DYNA";
						break;
					case 5 ://動態鎖利母基金加碼
						tableName = "TBSOT_NF_RAISE_AMT_DYNA";
						break;
				}

				sql.append("select BATCH_SEQ, TRADE_DATE_TYPE from " + tableName + " ");
				sql.append("where TRADE_SEQ = :trade_seq group by BATCH_SEQ, TRADE_DATE_TYPE order by BATCH_SEQ");

			} else if (inputVO.getTradeType() == 4) { //動態鎖利事件變更
				sql.append("select BATCH_SEQ_ADDPROD AS BATCH_SEQ, TRADE_DATE_TYPE from TBSOT_NF_CHANGE_DYNA ");
				sql.append(" where TRADE_SEQ = :trade_seq ");
			}
		} else if (!StringUtils.equals(inputVO.getPrdType(), "1") && !StringUtils.equals(inputVO.getPrdType(), "4")) { //非基金與SI
			switch(inputVO.getPrdType()) {
				case "2": //海外ETF/股票
					tableName = "TBSOT_ETF_TRADE_D";
					break;
				case "3": //海外債
					tableName = "TBSOT_BN_TRADE_D";
					break;
				case "5": //SN
					tableName = "TBSOT_SN_TRADE_D";
					break;
			}
			if(StringUtils.equals(inputVO.getPrdType(), "5")){
				sql.append("select BATCH_SEQ, PROD_ID from " + tableName + " ");
				sql.append("where TRADE_SEQ = :trade_seq group by BATCH_SEQ, PROD_ID order by BATCH_SEQ");
			} else {
				sql.append("select BATCH_SEQ from " + tableName + " ");
				sql.append("where TRADE_SEQ = :trade_seq group by BATCH_SEQ order by BATCH_SEQ");
			}
		}
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("trade_seq", inputVO.getTradeSeq());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return list;
	}

	/**
	 * 取得報表
	 * @param inputVO : PRDFitInputVO 下單適配Input
	 * @param reportType : 報表種類
	 * @return List : 該次交易相關資訊
	 * @throws JBranchException
	 * @throws Exception
	 * @see SotPdfContext#getSotPdfULst
	 */
	private List<String> getPdfULst(PRDFitInputVO inputVO, String reportType) throws JBranchException, Exception{
		//將有列印的表單ID放入List中
		if(!printRptIdList.contains(reportType.toUpperCase())) {
			printRptIdList.add(reportType.toUpperCase());
		}

		return new SotPdfContext(inputVO, reportType).getSotPdfULst();
	}

	/**
	 *  檢核InputVO
	 * @param inputVO : PRDFitInputVO 下單適配Input
	 * @return void
	 * @throws JBranchException
	 */
	public void checkInputVO(PRDFitInputVO inputVO) throws JBranchException{
		boolean errFlag = true;
		if (inputVO.getCaseCode() == 1 || inputVO.getCaseCode() == 2) {
			errFlag = false;
		}
		if(errFlag) {
			throw new APException("執行錯誤，請檢查程式傳入參數是否正確!");
		}
	}

	/**
	 * 儲存適配
	 * @return void
	 * @throws JBranchException
	 * @see SOT712#getSN()
	 * @see SOT712#validateFit(PRDFitInputVO)
	 */
	public void saveFitInfo(Object body, IPrimitiveMap<?> header) throws JBranchException {
		PRDFitInputVO inputVO = (PRDFitInputVO) body;
		this.sendRtnObject(saveFitInfo(inputVO));
	}
	
	public String saveFitInfo(PRDFitInputVO inputVO) throws JBranchException {
		dam = this.getDataAccessManager();
		String rtnMsg = null; //儲存錯誤訊息

		try{
			Object rtnObject = this.validateFit(inputVO);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
			String testDate = cbsService.getCBSTestDate();
			//該客戶今日尚未有適配資料，insert一筆資料
			if (rtnObject instanceof ProdFitnessCustVO) {
				ProdFitnessCustVO custVO = (ProdFitnessCustVO)rtnObject;

				TBSOT_FIT_MAINVO fitVO = new TBSOT_FIT_MAINVO();
				BigDecimal sn = new BigDecimal(getSN());
				fitVO.setSEQ(sn);
				fitVO.setCUST_ID(inputVO.getCustId());
				fitVO.setBRANCH_NBR(custVO.getBranchNbr() == null ? "999" : custVO.getBranchNbr());
				fitVO.setPROD_TYPE(inputVO.getPrdType());
				fitVO.setPROD_ID(inputVO.getPrdId());
				fitVO.setKYC_LV(custVO.getRiskAtr());
				fitVO.setPROD_LV(inputVO.getRiskLevel());
				dam.create(fitVO);
				if(!StringUtils.equals(testDate.substring(0,8), sdf2.format(new Date()))) {
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sql = new StringBuffer();
					sql.append("update TBSOT_FIT_MAIN set LASTUPDATE = :LASTUPDATE WHERE SEQ = :SEQ ");
					queryCondition.setObject("LASTUPDATE", new Timestamp(sdf.parse(testDate).getTime()));
					queryCondition.setObject("SEQ", sn);
					queryCondition.setQueryString(sql.toString());
					dam.exeUpdate(queryCondition);
				}
			//該客戶今日有存入適配資料，update 該筆資料
			} else if (rtnObject instanceof Object) {
				String seq = rtnObject.toString();

				BigDecimal sn = new BigDecimal(seq);
				TBSOT_FIT_MAINVO fitVO = (TBSOT_FIT_MAINVO)dam.findByPKey(TBSOT_FIT_MAINVO.TABLE_UID, sn);
				dam.update(fitVO);
				if(!StringUtils.equals(testDate.substring(0,8), sdf2.format(new Date()))) {
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sql = new StringBuffer();
					sql.append("update TBSOT_FIT_MAIN set LASTUPDATE = :LASTUPDATE WHERE SEQ = :SEQ ");
					queryCondition.setObject("LASTUPDATE", new Timestamp(sdf.parse(testDate).getTime()));
					queryCondition.setObject("SEQ", sn);
					queryCondition.setQueryString(sql.toString());
					dam.exeUpdate(queryCondition);
				}

			}

		}catch (APException e) {
			rtnMsg = e.getMessage();
		}catch (Exception e) {
			logger.debug(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			rtnMsg = "系統發生錯誤請洽系統管理員！";
		}

		return rtnMsg;
	}

	/**
	 * 取得序號
	 * @return String : SEQ
	 * @throws JBranchException
	 */
	private String getSN() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBSOT_FIT_MAIN.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);

		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}

	/**
	 * 驗證是否可執行適配  <i>(不能適配會丟出錯誤訊息)</i><br>
	 * <code>使用此Method必須處理丟出的錯誤訊息<b>為客製訊息</b></code>
	 * @param inputVO : PRDFitInputVO
	 * @return Object : 今日該客戶是否已存入適配資訊 <br>
	 * 		   <p>使用此Method會回傳一物件 <br>
	 * 			 	<b>Object : </b><code>代表今日已存在適配資料，此Object為該筆序號</code><br>
	 * 				<b>ProdFitnessCustVO : </b><code>代表今日尚無適配資料，回傳此物件提供該客戶相關資訊</code>
	 * 		   </p>
	 * @throws JBranchException : <br>
	 * 		   <p>
	 * 				<b>客製訊息如下 :</b>
	 * 				<li>ehl_01_common_024  (執行失敗 (<b>因為DB並無此商品資訊</b>))</li>
	 * 				<li>ehl_01_SOT702_009  (無客戶風險屬性或已逾期，無法適配)</li>
	 * 				<li>該客戶尚未有歸屬行，導致無法適配</li>
	 * 				<li>ehl_01_SOT702_011 (已有不合作帳戶註記，不得新承作商品)</li>
	 * 				<li>ehl_01_SOT702_012 (客戶屬美國國籍或稅籍不受理交易申請)</li>
	 * 				<li>ehl_01_SOT702_013 (未簽署協議之外國金融機構，不得新承作商品)</li>
	 * 		   </p>
	 */
	private Object validateFit(PRDFitInputVO inputVO) throws Exception {
		// 檢查系統是否有設置此商品資訊
		if (!new XmlInfo().doGetVariable("FPS.PROD_TYPE", FormatHelper.FORMAT_2)
			 			  .containsKey(inputVO.getPrdType()))
			throw new APException("ehl_01_common_024");  //執行失敗 (因為DB並無此商品資訊)

		// 初始化適配檢核
		ProdFitness prodFitness = (ProdFitness) PlatformContext.getBean("ProdFitness");
		prodFitness.setCustID(inputVO.getCustId());
		ProdFitnessCustVO custVO = prodFitness.getCustVO();

    // 問題單6525
		// if (custVO.getBranchNbr() == null) throw new APException("該客戶尚未有歸屬行，導致無法適配！");

		// 特殊身分判斷
		if (CollectionUtils.isNotEmpty(identifyProdNationality(inputVO.getPrdId()))
				|| CollectionUtils.isNotEmpty(identifyEtfStockExchange(inputVO.getPrdId()))) {
			String facaType = custVO.getFatcaDataVO().getFatcaType();
			if (StringUtils.equals("N", facaType)) throw new APException("ehl_01_SOT702_011"); // 已有不合作帳戶註記，不得新承作商品
			if (StringUtils.equals("Y", facaType)) throw new APException("ehl_01_SOT702_012"); // 客戶屬美國國籍或稅籍不受理交易申請
			if (StringUtils.equals("X", facaType)) throw new APException("ehl_01_SOT702_013"); // 未簽署協議之外國金融機構，不得新承作商品
		}

		// 判斷今日該客戶是否有存入適配資料
		List<Map<String, Object>> check = exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
			.append("select SEQ from TBSOT_FIT_MAIN where CUST_ID = :custId and PROD_ID = :prodId ")
			.append("and TRUNC(CREATETIME) = TRUNC(sysdate) and PROD_TYPE = :prodType ").toString())
			.setObject("custId", inputVO.getCustId())
			.setObject("prodId", inputVO.getPrdId())
			.setObject("prodType", inputVO.getPrdType()));
		if (CollectionUtils.isNotEmpty(check)) return check.get(0).get("SEQ");
		else return custVO;
	}

	/** 部分交易所需要特殊身分判斷 **/
	public List identifyEtfStockExchange(String prdId) throws JBranchException {
		return exeQueryForQcf(genDefaultQueryConditionIF()
				.setQueryString("select 1 from TBPRD_ETF_STOCK where COM01 IN ('NYSE','NASD','AMEX') and COM02 = :prdId ")
				.setObject("prdId", prdId));
	}

	/** 判斷商品國籍，部分商品需要特殊身分判斷 **/
	private List identifyProdNationality(String prdId) throws JBranchException {
		return exeQueryForQcf(genDefaultQueryConditionIF()
				.setQueryString("select 1 from TBPRD_NATIONALITY where COUNTRY_ID = 'US' and PROD_ID = :prdId ")
				.setObject("prdId", prdId));
	}

	/** SI下單及申購時，判斷KYC鍵機日期及是否有錄音序號 **/
	public void identifyKYCDateAndRecord(Object body, IPrimitiveMap<?> header) throws JBranchException {
		sendRtnObject(this.identifyKYCDateAndRecord(body));
	}

	public SOT712OutputVO identifyKYCDateAndRecord(Object body) throws JBranchException {
		String result;
		dam = this.getDataAccessManager();
		SOT712InputVO inputVO = (SOT712InputVO) body;
		SOT712OutputVO outputVO = new SOT712OutputVO();

		if(inputVO.getCustID().length() >= 10){
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT FN_VALID_SI_KYC_RECORD( :CUST_ID ) AS RESULT FROM DUAL ");
			queryCondition.setObject("CUST_ID", inputVO.getCustID().toUpperCase() );
			queryCondition.setQueryString(sql.toString());

			List<Map<String, Object>> KYCRecord = dam.exeQuery(queryCondition);
			outputVO.setKYCResult(ObjectUtils.toString(KYCRecord.get(0).get("RESULT")));
		}else{
			outputVO.setKYCResult("Y");
		}

		return outputVO;
	}
	
	/***
	 * 取得客戶高資產註記資料
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private CustHighNetWorthDataVO getHNWCData(String custID) throws Exception {
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");		
		CustHighNetWorthDataVO hnwcVO = sot714.getHNWCData(custID);
		
		return hnwcVO;
	}
	
	/***
	 * 取得客戶集中度資訊
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private WMSHACRDataVO getCentRateData(PRDFitInputVO inputVO, BigDecimal amount) throws Exception {
		WMSHACRDataVO hmshacrDataVO = new WMSHACRDataVO(); //集中度資訊
		
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustId());
		inputVO714.setBuyAmt(amount);
		if(StringUtils.equals(inputVO.getPrdType(), "3")) inputVO714.setProdType("4"); 			//海外債
		else if (StringUtils.equals(inputVO.getPrdType(), "4")) inputVO714.setProdType("1"); 	//SI 
		else if (StringUtils.equals(inputVO.getPrdType(), "5")) inputVO714.setProdType("2"); 	//SN
		
		hmshacrDataVO = sot714.getCentRateData(inputVO714);
		
		return hmshacrDataVO;
	}
	
	/***
	 * 取得台幣申購金額
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private BigDecimal getPurchseAmtTWD(PRDFitInputVO inputVO) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
				
		if(StringUtils.equals("3", inputVO.getPrdType())) {
			//海外債
			sb.append("select (NVL(A.TRUST_AMT, 0) * NVL(B.BUY_RATE, 1)) AS AMT ");
			sb.append(" FROM TBSOT_BN_TRADE_D A ");
			sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.PROD_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
			sb.append(" where A.TRADE_SEQ = :tradeSeq ");
		} else if(StringUtils.equals("4", inputVO.getPrdType())) {
			//SI
			sb.append("select (NVL(A.PURCHASE_AMT, 0) * NVL(B.BUY_RATE, 1)) AS AMT ");
			sb.append(" FROM TBSOT_SI_TRADE_D A ");
			sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.PROD_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
			sb.append(" where A.TRADE_SEQ = :tradeSeq ");
		} else if(StringUtils.equals("5", inputVO.getPrdType())) {
			//SN
			sb.append("select (NVL(A.PURCHASE_AMT, 0) * NVL(B.BUY_RATE, 1)) AS AMT ");
			sb.append(" FROM TBSOT_SN_TRADE_D A ");
			sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.PROD_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
			sb.append(" where A.TRADE_SEQ = :tradeSeq ");
		}		
		queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
		
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> cList = dam.exeQuery(queryCondition);
		BigDecimal amt = BigDecimal.ZERO;
		if (CollectionUtils.isNotEmpty(cList)) {
			amt = (BigDecimal) cList.get(0).get("AMT");
		}
		
		return amt;
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
	public boolean isRateGuaranteed(PRDFitInputVO inputVO) throws DAOException, JBranchException {
		boolean returnVal = false;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//取得保本率
		if(inputVO.getPrdType().matches("4|5")) {
			queryCondition.setQueryString("select NVL(RATE_GUARANTEEPAY, 0) AS RATE_GUARANTEEPAY from " + (StringUtils.equals("4", inputVO.getPrdType()) ? "TBPRD_SI" : "TBPRD_SN") + " where PRD_ID = :prdId ");
			queryCondition.setObject("prdId", inputVO.getPrdId());
			List<Map<String, Object>> pList = dam.exeQuery(queryCondition);
			if (CollectionUtils.isNotEmpty(pList)) {
				BigDecimal base = new BigDecimal(100);
				BigDecimal rate = (BigDecimal) pList.get(0).get("RATE_GUARANTEEPAY");
				returnVal = rate.compareTo(base) > -1;
			}
		} else {
			//非SI/SN，不檢查保本率
			returnVal = true;
		}
		
		return returnVal;
	}
}
