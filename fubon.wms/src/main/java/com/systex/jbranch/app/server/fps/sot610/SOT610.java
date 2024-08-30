package com.systex.jbranch.app.server.fps.sot610;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Blob;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Ocean
 * @date 2016/09/13
 * 
 */
@Component("sot610")
@Scope("request")
public class SOT610 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT610.class);

	private StringBuffer tradeSQL(SOT610InputVO inputVO, QueryConditionIF queryCondition) {

		StringBuffer sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT CONTRACT_ID, TRADE_SEQ, PROD_ID, PROD_NAME, TRADE_DATE, BATCH_SEQ ");
		sb.append("  FROM ( ");
		sb.append("    SELECT CONTRACT_ID, TRADE_SEQ, PROD_ID, PROD_NAME, TRADE_DATE, BATCH_SEQ FROM TBSOT_NF_PURCHASE_D "); //基金-申購
		sb.append("    UNION ");
		sb.append("    SELECT CONTRACT_ID, TRADE_SEQ, RDM_PROD_ID, RDM_PROD_NAME, TRADE_DATE, BATCH_SEQ FROM TBSOT_NF_REDEEM_D "); //基金-贖回
		sb.append("    UNION ");
		sb.append("    SELECT NULL AS CONTRACT_ID, TRADE_SEQ, OUT_PROD_ID, OUT_PROD_NAME, TRADE_DATE, BATCH_SEQ FROM TBSOT_NF_TRANSFER_D "); //基金-轉換
		sb.append("    UNION ");
		sb.append("    SELECT NULL AS CONTRACT_ID, TRADE_SEQ, B_PROD_ID, B_PROD_NAME, LASTUPDATE, NULL AS BATCH_SEQ FROM TBSOT_NF_CHANGE_D "); //基金-變更
		sb.append("    UNION ");
		sb.append("    SELECT CONTRACT_ID, TRADE_SEQ, PROD_ID, PROD_NAME, TRADE_DATE, BATCH_SEQ FROM TBSOT_ETF_TRADE_D "); //海外ETF/STOCK
		sb.append("    UNION ");
		sb.append("    SELECT CONTRACT_ID, TRADE_SEQ, PROD_ID, PROD_NAME, TRADE_DATE, BATCH_SEQ FROM TBSOT_BN_TRADE_D "); //海外債
		sb.append("    UNION ");
		sb.append("    SELECT NULL AS CONTRACT_ID, TRADE_SEQ, PROD_ID, PROD_NAME, TRADE_DATE, BATCH_SEQ FROM TBSOT_SI_TRADE_D "); //SI
		sb.append("    UNION ");
		sb.append("    SELECT NULL AS CONTRACT_ID, TRADE_SEQ, PROD_ID, PROD_NAME, TRADE_DATE, BATCH_SEQ FROM TBSOT_SN_TRADE_D "); //SN
		sb.append("  ) ");
		sb.append("  WHERE 1 = 1 ");

		if (null != inputVO.getsDate() && null != inputVO.geteDate()) {// 活動起始日-起迄 
			sb.append("  AND TO_CHAR(TRADE_DATE, 'yyyyMMdd') BETWEEN TO_CHAR(:startDate, 'yyyyMMdd') AND TO_CHAR(:endDate, 'yyyyMMdd') ");
			queryCondition.setObject("startDate", inputVO.getsDate());
			queryCondition.setObject("endDate", inputVO.geteDate());
		}

		if (null != inputVO.getsDate() && null == inputVO.geteDate()) {
			sb.append("  AND TRADE_DATE >= TRUNC(:startDate) ");
			queryCondition.setObject("startDate", inputVO.getsDate());
		}

		if (null == inputVO.getsDate() && null != inputVO.geteDate()) {
			sb.append("  AND TRADE_DATE <= TRUNC(:endDate) ");
			queryCondition.setObject("endDate", inputVO.geteDate());
		}

		if (StringUtils.isNotBlank(inputVO.getProdID())) {
			sb.append("  AND PROD_ID = :prodID ");
			queryCondition.setObject("prodID", inputVO.getProdID());
		}
		sb.append(") ");

		return sb;
	}

	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);
		
		SOT610InputVO inputVO = (SOT610InputVO) body;
		SOT610OutputVO outputVO = new SOT610OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append(tradeSQL(inputVO, queryCondition));
		sb.append("SELECT T_MAIN.TRUST_TRADE_TYPE, ");
		sb.append("       T_MAIN.TRADE_SEQ, T_MAIN.PROD_TYPE, T_MAIN.TRADE_TYPE, ");
		sb.append("       T_MAIN.CUST_ID, T_MAIN.CUST_NAME, ");
		sb.append("       T_MAIN.TRADE_STATUS, T_MAIN.IS_BARGAIN_NEEDED, T_MAIN.BARGAIN_FEE_FLAG, ");
		sb.append("       CASE WHEN T_MAIN.TRADE_STATUS IN ('1', '2') THEN (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.GO_PAGE' AND PARAM_CODE = T_MAIN.PROD_TYPE||T_MAIN.TRADE_TYPE||T_MAIN.TRADE_STATUS||NVL(T_MAIN.TRUST_TRADE_TYPE,'S')) ");
		sb.append("       ELSE NULL END AS GO_PAGE, ");
		sb.append("       CASE WHEN T_MAIN.TRADE_STATUS = '4' THEN (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.GO_PAGE' AND PARAM_CODE = T_MAIN.PROD_TYPE||T_MAIN.TRADE_TYPE||'2') ");
		sb.append("       ELSE NULL END AS GO_WEBBANK, ");
		sb.append("       CASE WHEN T_MAIN.TRADE_STATUS = '3' THEN (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.PRINT_REPORT' AND PARAM_CODE = T_MAIN.PROD_TYPE||(CASE WHEN T_MAIN.PROD_TYPE = 1 THEN T_MAIN.TRADE_TYPE ELSE '' END)) ");
		sb.append("       ELSE NULL END AS PRINT_REPORT, ");
		sb.append("       NVL(T_MAIN.IS_WEB, 'N') AS IS_WEB, ");
		sb.append("		  R.CREATOR AS RPT_CREATOR_EMPID ");
		sb.append("FROM TBSOT_TRADE_MAIN T_MAIN ");
		sb.append("LEFT JOIN TBSOT_TRADE_REPORT R ON (T_MAIN.TRADE_SEQ = R.TRADE_SEQ) ");
		sb.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getTrustTradeType())) {
			sb.append("AND T_MAIN.TRUST_TRADE_TYPE = :trustTradeType ");
			queryCondition.setObject("trustTradeType", inputVO.getTrustTradeType());
		}

		if (StringUtils.isNotBlank(inputVO.getCustID())) {
			sb.append("AND T_MAIN.CUST_ID = :custID ");
			queryCondition.setObject("custID", inputVO.getCustID());
		}
		
		if (StringUtils.isNotBlank(inputVO.getProdType())) {
			sb.append("AND T_MAIN.PROD_TYPE = :prodType ");
			queryCondition.setObject("prodType", inputVO.getProdType());
		}
		
		if (StringUtils.isNotBlank(inputVO.getTradeType())) {
			sb.append("AND T_MAIN.TRADE_TYPE = :tradeType ");
			queryCondition.setObject("tradeType", inputVO.getTradeType());
		}
		
		if (StringUtils.isNotBlank(inputVO.getTradeStatus())) {
			sb.append("AND T_MAIN.TRADE_STATUS = :tradeStatus ");
			queryCondition.setObject("tradeStatus", inputVO.getTradeStatus());
		}
		
		if (StringUtils.isNotBlank(inputVO.getBargainFeeFlag())) {
			sb.append("AND T_MAIN.BARGAIN_FEE_FLAG = :bargainFeeFlag ");
			queryCondition.setObject("bargainFeeFlag", inputVO.getBargainFeeFlag());
		}
		
		if (StringUtils.isNotBlank(inputVO.getIsBaraginNeeded())) {
			sb.append("AND T_MAIN.IS_BARGAIN_NEEDED = :isBaraginNeeded ");
			queryCondition.setObject("isBaraginNeeded", inputVO.getIsBaraginNeeded());
		}
		
		if (fcMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			switch ((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) {
				case "JRM":
					sb.append("AND T_MAIN.CREATOR = :loginID ");
					queryCondition.setObject("loginID", (String) getCommonVariable(SystemVariableConsts.LOGINID));
					break;
				default:
//					sb.append("AND C.AO_CODE IN ( :AO_CODE )");
//					queryCondition.setObject("AO_CODE", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
					break;
			}
		}

		sb.append("AND (TRUNC(T_MAIN.CREATETIME) = TRUNC(SYSDATE) OR T_MAIN.TRADE_STATUS in ('3', '4')) ");
		sb.append("AND EXISTS(SELECT B.PROD_ID FROM BASE B WHERE B.TRADE_SEQ = T_MAIN.TRADE_SEQ) ");
		sb.append("ORDER BY T_MAIN.TRADE_SEQ DESC ");

		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for (Map<String, Object> map : list) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sbDTL = new StringBuffer();
			sbDTL.append(tradeSQL(inputVO, queryCondition));
			sbDTL.append("SELECT TRADE_SEQ, PROD_ID, PROD_NAME, TRADE_DATE, BATCH_SEQ ");
			sbDTL.append("FROM BASE ");
			sbDTL.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", map.get("TRADE_SEQ"));

			queryCondition.setQueryString(sbDTL.toString());

			List<Map<String, Object>> dtlList = dam.exeQuery(queryCondition);

			String prodID = "";
			String prodName = "";
			String batchSeq = "";
			String batchSeqList = "";
			for (Map<String, Object> dtlMap : dtlList) {
				prodID = prodID + dtlMap.get("PROD_ID") + " \n";
				prodName = prodName + dtlMap.get("PROD_NAME") + " \n";
				if(StringUtils.equals("Y", map.get("IS_WEB").toString()) &&
						StringUtils.isNotBlank(ObjectUtils.toString(dtlMap.get("BATCH_SEQ"))) &&
						!StringUtils.equals(batchSeq,dtlMap.get("BATCH_SEQ").toString())) {
					batchSeqList = batchSeqList + dtlMap.get("BATCH_SEQ").toString() + " \n";
					batchSeq = ObjectUtils.toString(dtlMap.get("BATCH_SEQ"));
				}
			}

			map.put("PROD_ID_LIST", prodID);
			map.put("PROD_NAME_LIST", prodName);
			map.put("TRADE_DATE", dtlList.get(0).get("TRADE_DATE"));
			map.put("BATCH_SEQ_LIST", batchSeqList);
			map.put("CUST_ID", DataFormat.getCustIdMaskForHighRisk(map.get("CUST_ID").toString()));
		}
		
		outputVO.setTradeList(list);

		this.sendRtnObject(outputVO);
	}

	public void printReport(Object body, IPrimitiveMap header) throws JBranchException {
		SOT610InputVO inputVO = (SOT610InputVO) body;

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM TBSOT_TRADE_REPORT WHERE TRADE_SEQ = :trade_seq");
		queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String errorMag = null;

		try {
			if (list.size() > 0 && list.get(0).get("REPORT_FILE") != null) {
				String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String uuid = UUID.randomUUID().toString();
				String fileName = String.format("%s.pdf", uuid);
				Blob blob = (Blob) list.get(0).get("REPORT_FILE");
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);

				File targetFile = new File(filePath, fileName);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				//				notifyClientToDownloadFile("temp//" + uuid, fileName);
				notifyClientViewDoc("temp//" + fileName, "pdf");
			} else {
				errorMag = "查無此資料請洽系統管理員";
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

		if (StringUtils.isNotBlank(errorMag)) {
			throw new APException(errorMag);
		}

		this.sendRtnObject(null);
	}

}
